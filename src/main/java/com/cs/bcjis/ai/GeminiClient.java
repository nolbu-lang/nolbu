package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Google Gemini(Generative Language API) 호출 클라이언트.
 *
 * - API 키 / 모델명은 globals.properties 의 config 빈에서 읽는다.
 * - 외부 라이브러리 추가 없이 java.net + net.sf.json(기존 의존)만 사용한다.
 *
 * [네트워크 우회(failover)]
 * 일부 기관망에서는 googleapis.com 의 여러 IP 중 일부가 차단되어 있고,
 * Java 표준 HttpURLConnection 은 DNS 가 돌려준 첫 번째 IP 로만 접속을 시도하므로
 * 운에 따라 접속 시간초과가 발생한다.
 * 이를 막기 위해, 접속 단계 실패 시 모든 IP 를 직접 탐색(TCP probe)하여
 * 살아있는 IP 로 SNI 를 유지한 채 직접 HTTPS 요청을 보내는 우회 경로를 사용한다.
 */
@Component("geminiClient")
public class GeminiClient implements LlmClient {

    private static final Logger logger = Logger.getLogger(GeminiClient.class);

    private static final String API_HOST = "generativelanguage.googleapis.com";

    private static final String ENDPOINT_FORMAT =
            "https://" + API_HOST + "/v1beta/models/%s:generateContent?key=%s";

    /** 접속(connect) 단계 제한시간 - 짧게 잡아 죽은 IP 를 빨리 포기 */
    private static final int CONNECT_TIMEOUT_MS = 10000;

    /** IP 탐색(TCP probe) 제한시간 */
    private static final int PROBE_TIMEOUT_MS = 3000;

    /** 기본 모델 429 발생 후 예비 모델을 우선 사용할 시간 */
    private static final long FALLBACK_REMEMBER_MS = 10L * 60L * 1000L;

    /** 이 시각까지는 예비 모델을 바로 사용 (기본 모델 쿼터 소진 기억) */
    private static volatile long fallbackUntilMs = 0L;

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean isEnabled() {
        return getApiKey().length() > 0;
    }

    public String getProviderName() {
        return "gemini";
    }

    public String getApiKey() {
        String key = config.getProperty("Globals.GeminiApiKey");
        return key == null ? "" : key.trim();
    }

    public String getModel() {
        String model = config.getProperty("Globals.GeminiModel");
        if (model == null || model.trim().length() == 0) {
            return "gemini-2.5-flash";
        }
        return model.trim();
    }

    /** 기본 모델의 쿼터 소진(429) 시 사용할 예비 모델 */
    public String getFallbackModel() {
        String model = config.getProperty("Globals.GeminiFallbackModel");
        if (model == null || model.trim().length() == 0) {
            return "gemini-2.5-flash-lite";
        }
        return model.trim();
    }

    private int getTimeoutMs() {
        try {
            String t = config.getProperty("Globals.GeminiTimeoutMs");
            if (t != null && t.trim().length() > 0) {
                return Integer.parseInt(t.trim());
            }
        } catch (Exception e) {
            // ignore - use default
        }
        return 60000;
    }

    /**
     * 단일 프롬프트를 전송하고 생성된 텍스트를 반환한다.
     */
    public String generate(String prompt) throws Exception {
        return generate(null, prompt);
    }

    public String generateUserQuery(String userQuery) throws Exception {
        return generate(null, userQuery);
    }

    /**
     * 시스템 지침(system_instruction)과 프롬프트를 함께 전송하고 생성된 텍스트를 반환한다.
     *
     * system_instruction 은 모델의 페르소나·출력 형식을 대화 내내 고정하는 필드로,
     * 일반 프롬프트보다 우선순위가 높게 반영된다.
     *
     * @param systemInstruction 페르소나/출력형식 고정 지침 (null 또는 빈 문자열이면 미사용)
     * @param prompt            사용자 질문 및 데이터 컨텍스트
     */
    public String generate(String systemInstruction, String prompt) throws Exception {
        if (!isEnabled()) {
            throw new IllegalStateException("Gemini API 키가 설정되어 있지 않습니다. (globals.properties 의 Globals.GeminiApiKey)");
        }

        JSONObject part = new JSONObject();
        part.put("text", prompt);

        JSONArray parts = new JSONArray();
        parts.add(part);

        JSONObject content = new JSONObject();
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.add(content);

        JSONObject genConfig = new JSONObject();
        genConfig.put("temperature", 0.2);
        // gemini-2.5 계열은 내부 사고(thinking) 토큰이 출력 한도를 함께 소비한다.
        // 사고 토큰에 상한을 두어 응답 속도를 높이고, 답변이 중간에 잘리지 않도록 출력 한도를 넉넉하게 설정
        genConfig.put("maxOutputTokens", 16384);
        JSONObject thinkingConfig = new JSONObject();
        thinkingConfig.put("thinkingBudget", 1024);
        genConfig.put("thinkingConfig", thinkingConfig);

        JSONObject body = new JSONObject();
        body.put("contents", contents);
        body.put("generationConfig", genConfig);

        // 페르소나·출력형식 고정 (Gemini v1beta system_instruction 필드)
        if (systemInstruction != null && systemInstruction.trim().length() > 0) {
            JSONObject sysPart = new JSONObject();
            sysPart.put("text", systemInstruction);

            JSONArray sysParts = new JSONArray();
            sysParts.add(sysPart);

            JSONObject sysContent = new JSONObject();
            sysContent.put("parts", sysParts);

            body.put("system_instruction", sysContent);
        }

        String jsonBody = body.toString();

        String primary = getModel();
        String fallback = getFallbackModel();
        boolean hasFallback = fallback.length() > 0 && !fallback.equals(primary);

        // 최근에 기본 모델 쿼터 소진(429)을 겪었다면 일정 시간 동안 바로 예비 모델 사용 (불필요한 재시도 지연 방지)
        if (hasFallback && System.currentTimeMillis() < fallbackUntilMs) {
            return extractText(postWithRetry(buildUrl(fallback), jsonBody));
        }

        // 기본 모델 호출 → 쿼터 소진(429) 시 즉시 예비 모델로 폴백
        String responseText;
        try {
            responseText = postWithRetry(buildUrl(primary), jsonBody);
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.indexOf("HTTP 429") > -1 && hasFallback) {
                fallbackUntilMs = System.currentTimeMillis() + FALLBACK_REMEMBER_MS;
                logger.warn("Gemini 모델(" + primary + ") 쿼터 소진 - 예비 모델(" + fallback + ")로 전환 (10분간 유지)");
                responseText = postWithRetry(buildUrl(fallback), jsonBody);
            } else {
                throw e;
            }
        }

        return extractText(responseText);
    }

    private String buildUrl(String model) throws Exception {
        return String.format(ENDPOINT_FORMAT, URLEncoder.encode(model, "UTF-8"), getApiKey());
    }

    /**
     * 일시적 과부하(503)는 잠시 대기 후 재시도한다.
     * (429 쿼터 소진은 재시도해도 소용없으므로 즉시 예외 -> 호출부에서 예비 모델로 폴백)
     */
    private String postWithRetry(String urlStr, String jsonBody) throws Exception {
        int[] waitMs = { 2000, 5000 };
        for (int attempt = 0; ; attempt++) {
            try {
                return post(urlStr, jsonBody);
            } catch (RuntimeException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage();
                boolean transientError = msg.indexOf("HTTP 503") > -1;
                if (!transientError || attempt >= waitMs.length) {
                    throw e;
                }
                logger.warn("Gemini 일시 오류 - " + (waitMs[attempt] / 1000) + "초 후 재시도 (" + (attempt + 1) + "/" + waitMs.length + ")");
                Thread.sleep(waitMs[attempt]);
            }
        }
    }

    private String post(String urlStr, String jsonBody) throws Exception {
        try {
            return postByUrlConnection(urlStr, jsonBody);
        } catch (Exception e) {
            if (!isConnectFailure(e)) {
                throw e;
            }
            logger.warn("Gemini 기본 접속 실패(" + e.getMessage() + ") - 살아있는 IP 탐색 후 우회 접속 시도");
            return postByDirectSsl(urlStr, jsonBody);
        }
    }

    /** 접속(connect) 단계 실패 여부 판단 */
    private boolean isConnectFailure(Exception e) {
        if (e instanceof ConnectException) {
            return true;
        }
        if (e instanceof SocketTimeoutException) {
            String msg = e.getMessage();
            return msg != null && msg.toLowerCase().indexOf("connect") > -1;
        }
        return false;
    }

    // ------------------------------------------------------------------
    // 기본 경로: HttpsURLConnection
    // ------------------------------------------------------------------

    private String postByUrlConnection(String urlStr, String jsonBody) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            if (conn instanceof HttpsURLConnection) {
                SSLSocketFactory factory = buildTls12Factory();
                if (factory != null) {
                    ((HttpsURLConnection) conn).setSSLSocketFactory(factory);
                }
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(getTimeoutMs());
            conn.setDoOutput(true);

            byte[] payload = jsonBody.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            try {
                os.write(payload);
                os.flush();
            } finally {
                os.close();
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            String responseText = readStream(is);

            return checkStatus(status, responseText);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // ------------------------------------------------------------------
    // 우회 경로: 살아있는 IP 탐색 후 SNI 유지한 직접 HTTPS 호출
    // ------------------------------------------------------------------

    /**
     * DNS 의 모든 IP 를 TCP 로 탐색해 접속 가능한 IP 를 찾고,
     * 해당 IP 로 직접 TLS(SNI=호스트명) 연결을 맺어 HTTP/1.0 요청을 보낸다.
     * (HTTP/1.0 + Connection: close 를 사용해 chunked 응답을 피한다)
     */
    private String postByDirectSsl(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        String host = url.getHost();
        String pathAndQuery = url.getPath() + (url.getQuery() == null ? "" : "?" + url.getQuery());

        InetAddress alive = pickReachableAddress(host);
        if (alive == null) {
            throw new ConnectException("Gemini 서버(" + host + ")의 어떤 IP 로도 접속할 수 없습니다. 네트워크 차단 여부를 확인하세요.");
        }

        logger.warn("Gemini 우회 접속 IP: " + alive.getHostAddress());

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, null, null);

        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket();
        try {
            // SNI 를 실제 호스트명으로 설정하고, TLS 핸드셰이크 단계에서
            // 인증서-호스트명 검증(endpoint identification)을 수행하도록 지정
            // (IP 직결이어도 올바른 인증서/라우팅/검증 보장)
            SSLParameters params = socket.getSSLParameters();
            params.setServerNames(Collections.<SNIServerName>singletonList(new javax.net.ssl.SNIHostName(host)));
            params.setEndpointIdentificationAlgorithm("HTTPS");
            socket.setSSLParameters(params);

            socket.connect(new InetSocketAddress(alive, 443), CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(getTimeoutMs());
            socket.startHandshake();

            byte[] payload = jsonBody.getBytes("UTF-8");

            StringBuilder req = new StringBuilder();
            req.append("POST ").append(pathAndQuery).append(" HTTP/1.0\r\n");
            req.append("Host: ").append(host).append("\r\n");
            req.append("Content-Type: application/json; charset=utf-8\r\n");
            req.append("Accept: application/json\r\n");
            req.append("Content-Length: ").append(payload.length).append("\r\n");
            req.append("Connection: close\r\n");
            req.append("\r\n");

            OutputStream os = socket.getOutputStream();
            os.write(req.toString().getBytes("UTF-8"));
            os.write(payload);
            os.flush();

            // 응답 전체 수신 (Connection: close 이므로 EOF 까지)
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            InputStream is = socket.getInputStream();
            byte[] chunk = new byte[8192];
            int n;
            while ((n = is.read(chunk)) > -1) {
                buf.write(chunk, 0, n);
            }

            String raw = buf.toString("UTF-8");
            int headerEnd = raw.indexOf("\r\n\r\n");
            if (headerEnd < 0) {
                throw new java.io.IOException("Gemini 우회 응답 형식 오류");
            }

            String statusLine = raw.substring(0, raw.indexOf("\r\n"));
            String bodyText = raw.substring(headerEnd + 4);

            int status = parseStatusCode(statusLine);
            return checkStatus(status, bodyText);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /** DNS 의 모든 IPv4 주소를 순서대로 TCP 탐색하여 접속 가능한 첫 주소를 반환 */
    private InetAddress pickReachableAddress(String host) {
        InetAddress[] addrs;
        try {
            addrs = InetAddress.getAllByName(host);
        } catch (Exception e) {
            logger.error("Gemini DNS 조회 실패: " + host, e);
            return null;
        }

        for (int i = 0; i < addrs.length; i++) {
            Socket probe = new Socket();
            try {
                probe.connect(new InetSocketAddress(addrs[i], 443), PROBE_TIMEOUT_MS);
                return addrs[i];
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Gemini IP 탐색 실패: " + addrs[i].getHostAddress());
                }
            } finally {
                try {
                    probe.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return null;
    }

    private int parseStatusCode(String statusLine) {
        try {
            String[] tokens = statusLine.trim().split("\\s+");
            return Integer.parseInt(tokens[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    /** 상태코드 검사 후 본문 반환 (오류 시 예외) */
    private String checkStatus(int status, String responseText) {
        if (status < 200 || status >= 300) {
            logger.error("Gemini API 오류 status=" + status + " body=" + responseText);
            throw new RuntimeException("Gemini API 호출 실패 (HTTP " + status + "): " + extractErrorMessage(responseText));
        }
        return responseText;
    }

    private SSLSocketFactory buildTls12Factory() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, null);
            return ctx.getSocketFactory();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("TLSv1.2 설정 실패 - 기본 SSL 사용", e);
            }
            return null;
        }
    }

    private String readStream(InputStream is) throws Exception {
        if (is == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    /**
     * Gemini 응답 JSON 에서 생성 텍스트를 추출한다.
     */
    private String extractText(String responseText) {
        try {
            JSONObject root = JSONObject.fromObject(responseText);
            if (!root.containsKey("candidates")) {
                return "";
            }
            JSONArray candidates = root.getJSONArray("candidates");
            if (candidates.isEmpty()) {
                return "";
            }
            JSONObject first = candidates.getJSONObject(0);
            JSONObject content = first.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                JSONObject p = parts.getJSONObject(i);
                if (p.containsKey("text")) {
                    sb.append(p.getString("text"));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("Gemini 응답 파싱 오류: " + responseText, e);
            return "";
        }
    }

    private String extractErrorMessage(String responseText) {
        try {
            JSONObject root = JSONObject.fromObject(responseText);
            if (root.containsKey("error")) {
                JSONObject error = root.getJSONObject("error");
                if (error.containsKey("message")) {
                    return error.getString("message");
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return responseText;
    }
}

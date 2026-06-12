package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 네이버 HyperCLOVA X (CLOVA Studio Chat Completions v3) 호출 클라이언트.
 *
 * 운영 환경용. globals.properties 의 ClovaApiKey·ClovaModel 등으로 설정한다.
 * GeminiClient 와 동일한 generate(systemInstruction, prompt) 시그니처를 제공한다.
 */
@Component("hyperClovaClient")
public class HyperClovaClient implements LlmClient {

    private static final Logger logger = Logger.getLogger(HyperClovaClient.class);

    private static final String DEFAULT_ENDPOINT =
            "https://clovastudio.stream.ntruss.com/v3/chat-completions";

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean isEnabled() {
        return getApiKey().length() > 0;
    }

    public String getProviderName() {
        return "hyperclova";
    }

    public String generate(String prompt) throws Exception {
        return generate(null, prompt);
    }

    public String generate(String systemInstruction, String prompt) throws Exception {
        if (!isEnabled()) {
            throw new IllegalStateException(
                    "HyperCLOVA API 키가 설정되어 있지 않습니다. (globals.properties 의 Globals.ClovaApiKey)");
        }

        JSONArray messages = new JSONArray();

        if (systemInstruction != null && systemInstruction.trim().length() > 0) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("content", systemInstruction);
            messages.add(sys);
        }

        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", prompt);
        messages.add(user);

        JSONObject body = new JSONObject();
        body.put("messages", messages);
        body.put("temperature", 0.2);
        body.put("maxTokens", getMaxTokens());
        body.put("topP", 0.8);
        body.put("repetitionPenalty", 1.1);

        String url = buildUrl();
        String responseText = postWithRetry(url, body.toString());
        return extractText(responseText);
    }

    private String getApiKey() {
        String key = config.getProperty("Globals.ClovaApiKey");
        return key == null ? "" : key.trim();
    }

    private String getModel() {
        String model = config.getProperty("Globals.ClovaModel");
        if (model == null || model.trim().length() == 0) {
            return "HCX-007";
        }
        return model.trim();
    }

    private String getEndpoint() {
        String ep = config.getProperty("Globals.ClovaEndpoint");
        if (ep == null || ep.trim().length() == 0) {
            return DEFAULT_ENDPOINT;
        }
        String s = ep.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private int getMaxTokens() {
        try {
            String v = config.getProperty("Globals.ClovaMaxTokens");
            if (v != null && v.trim().length() > 0) {
                return Integer.parseInt(v.trim());
            }
        } catch (Exception e) {
            // ignore
        }
        return 4096;
    }

    private int getTimeoutMs() {
        try {
            String v = config.getProperty("Globals.ClovaTimeoutMs");
            if (v != null && v.trim().length() > 0) {
                return Integer.parseInt(v.trim());
            }
        } catch (Exception e) {
            // ignore
        }
        return 60000;
    }

    private String buildUrl() {
        return getEndpoint() + "/" + getModel();
    }

    private String postWithRetry(String urlStr, String jsonBody) throws Exception {
        int[] waitMs = { 2000, 5000 };
        for (int attempt = 0; ; attempt++) {
            try {
                return post(urlStr, jsonBody);
            } catch (RuntimeException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage();
                boolean retryable = msg.indexOf("HTTP 503") > -1 || msg.indexOf("HTTP 502") > -1;
                if (!retryable || attempt >= waitMs.length) {
                    throw e;
                }
                logger.warn("HyperCLOVA 일시 오류 - " + (waitMs[attempt] / 1000) + "초 후 재시도");
                Thread.sleep(waitMs[attempt]);
            }
        }
    }

    private String post(String urlStr, String jsonBody) throws Exception {
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
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(getTimeoutMs());
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + getApiKey());
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            if (is == null) {
                throw new RuntimeException("HyperCLOVA API 응답 없음 (HTTP " + code + ")");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            if (code >= 400) {
                throw new RuntimeException("HyperCLOVA API 호출 실패 (HTTP " + code + "): " + sb.toString());
            }
            return sb.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private SSLSocketFactory buildTls12Factory() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, null);
            return ctx.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }

    /** CLOVA Studio v3 / OpenAI 호환 응답에서 텍스트 추출 */
    private String extractText(String responseJson) {
        JSONObject root = JSONObject.fromObject(responseJson);

        if (root.containsKey("result")) {
            JSONObject result = root.getJSONObject("result");
            if (result.containsKey("message")) {
                JSONObject msg = result.getJSONObject("message");
                if (msg.containsKey("content")) {
                    return msg.getString("content");
                }
            }
        }

        if (root.containsKey("choices")) {
            JSONArray choices = root.getJSONArray("choices");
            if (choices.size() > 0) {
                JSONObject first = choices.getJSONObject(0);
                if (first.containsKey("message")) {
                    return first.getJSONObject("message").optString("content", "");
                }
                return first.optString("text", "");
            }
        }

        throw new RuntimeException("HyperCLOVA 응답에서 텍스트를 찾지 못했습니다.");
    }
}

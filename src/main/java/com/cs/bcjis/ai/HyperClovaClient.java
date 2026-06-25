package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 부산시 내부 행정 AI (LLM Studio) 호출 클라이언트.
 *
 * 외부망(네이버 CLOVA Studio 등)은 사용하지 않는다.
 * globals.properties:
 *   Globals.ClovaEndpoint = http://{내부서버}/llm-studio/v1/api/task/generate/syncapi/{project}/{task}
 * 요청: POST {"prompt":"..."}
 * 응답: llm_result.answer
 */
@Component("hyperClovaClient")
public class HyperClovaClient implements LlmClient {

    private static final Logger logger = Logger.getLogger(HyperClovaClient.class);

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean isEnabled() {
        return getEndpoint().length() > 0;
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
                    "내부 행정 AI(LLM Studio) 엔드포인트가 설정되어 있지 않습니다. "
                    + "(globals.properties 의 Globals.ClovaEndpoint)");
        }

        String url = getEndpoint();
        String bodyJson = buildRequestBody(systemInstruction, prompt);
        String responseText = postWithRetry(url, bodyJson);
        return extractAnswer(responseText);
    }

    private String buildRequestBody(String systemInstruction, String prompt) {
        StringBuilder combined = new StringBuilder();
        if (systemInstruction != null && systemInstruction.trim().length() > 0) {
            combined.append(systemInstruction.trim()).append("\n\n");
        }
        combined.append(prompt == null ? "" : prompt);
        JSONObject body = new JSONObject();
        body.put("prompt", combined.toString());
        return body.toString();
    }

    private String getEndpoint() {
        String ep = config.getProperty("Globals.ClovaEndpoint");
        if (ep == null || ep.trim().length() == 0) {
            return "";
        }
        String s = ep.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
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
        return 120000;
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
                logger.warn("LLM Studio 일시 오류 - " + (waitMs[attempt] / 1000) + "초 후 재시도");
                Thread.sleep(waitMs[attempt]);
            }
        }
    }

    private String post(String urlStr, String jsonBody) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(getTimeoutMs());
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            if (is == null) {
                throw new RuntimeException("LLM Studio API 응답 없음 (HTTP " + code + ")");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            if (code >= 400) {
                throw new RuntimeException("LLM Studio API 호출 실패 (HTTP " + code + "): " + sb.toString());
            }
            return sb.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String extractAnswer(String responseJson) {
        JSONObject root = JSONObject.fromObject(responseJson);
        if (root.containsKey("llm_result")) {
            JSONObject llmResult = root.getJSONObject("llm_result");
            if (llmResult.containsKey("answer")) {
                return llmResult.getString("answer");
            }
        }
        String code = root.optString("response_code", "");
        String message = root.optString("response_message", "");
        throw new RuntimeException("LLM Studio 응답에서 answer를 찾지 못했습니다. "
                + "response_code=" + code + ", message=" + message);
    }
}

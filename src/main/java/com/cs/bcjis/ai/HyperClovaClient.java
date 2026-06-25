package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 부산시 내부 행정 AI (LLM Studio) 호출 클라이언트.
 *
 * globals.properties:
 *   Globals.ClovaEndpoint = http://{내부서버}/llm-studio/v1/api/task/generate/syncapi/{project}/{task}
 *
 * 요청 본문(필수 4항목, 인터페이스 정의서 V2.x):
 *   user_query (String), datetime (String), stream (Boolean), dialog_history (String, JSON 형식)
 * 응답: llm_result.answer
 */
@Component("hyperClovaClient")
public class HyperClovaClient implements LlmClient {

    private static final Logger logger = Logger.getLogger(HyperClovaClient.class);

    /** LLM Studio datetime — 정의서: yyyy년 M월 d일 EEEE a h:mm */
    private static final String DATETIME_PATTERN = "yyyy년 M월 d일 EEEE a h:mm";

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
        return generateUserQuery(prompt);
    }

    public String generate(String systemInstruction, String prompt) throws Exception {
        // LLM Studio budget_search — user_query는 챗봇 원문만 전달 (systemInstruction 미포함)
        return generateUserQuery(prompt);
    }

    /**
     * 챗봇 입력칸 사용자 문자열을 user_query로 전달하여 budget_search API 호출.
     */
    public String generateUserQuery(String userQuery) throws Exception {
        if (!isEnabled()) {
            throw new IllegalStateException(
                    "내부 행정 AI(LLM Studio) 엔드포인트가 설정되어 있지 않습니다. "
                    + "(globals.properties 의 Globals.ClovaEndpoint)");
        }
        if (userQuery == null) {
            userQuery = "";
        }

        String url = getEndpoint();
        String bodyJson = buildRequestBody(userQuery);
        logger.info("LLM Studio POST " + url + " user_queryChars=" + userQuery.length());
        String responseText = postWithRetry(url, bodyJson);
        String answer = extractAnswer(responseText);
        logger.info("LLM Studio OK answerChars=" + (answer == null ? 0 : answer.length()));
        return answer;
    }

    private String buildRequestBody(String userQuery) {
        JSONObject body = new JSONObject();
        body.put("user_query", userQuery);
        body.put("datetime", formatDatetime());
        body.put("stream", Boolean.TRUE);
        body.put("dialog_history", "[]");
        return body.toString();
    }

    private String formatDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN, Locale.KOREAN);
        return sdf.format(new Date());
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
                sb.append(line).append('\n');
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

    private String extractAnswer(String responseText) {
        if (responseText == null) {
            throw new RuntimeException("LLM Studio 응답이 비어 있습니다.");
        }
        String trimmed = responseText.trim();
        if (trimmed.indexOf("data:") > -1) {
            return extractAnswerFromSse(trimmed);
        }
        return extractAnswerFromJson(trimmed);
    }

    /** stream=true 일 때 SSE(data: {...}) 다중 청크 응답 처리 */
    private String extractAnswerFromSse(String sseText) {
        StringBuilder merged = new StringBuilder();
        String finalAnswer = null;
        int searchFrom = 0;
        while (searchFrom < sseText.length()) {
            int dataIdx = sseText.indexOf("data:", searchFrom);
            if (dataIdx < 0) {
                break;
            }
            int jsonStart = dataIdx + 5;
            while (jsonStart < sseText.length()) {
                char ch = sseText.charAt(jsonStart);
                if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
                    jsonStart++;
                } else {
                    break;
                }
            }
            if (jsonStart >= sseText.length() || sseText.charAt(jsonStart) != '{') {
                searchFrom = dataIdx + 5;
                continue;
            }
            String jsonPart = extractJsonObject(sseText, jsonStart);
            if (jsonPart == null) {
                searchFrom = jsonStart + 1;
                continue;
            }
            searchFrom = jsonStart + jsonPart.length();
            try {
                JSONObject root = JSONObject.fromObject(jsonPart);
                if (!root.containsKey("llm_result")) {
                    continue;
                }
                JSONObject llmResult = root.getJSONObject("llm_result");
                if (!llmResult.containsKey("answer")) {
                    continue;
                }
                String chunk = llmResult.getString("answer");
                if (chunk == null) {
                    continue;
                }
                String finishReason = llmResult.optString("finish_reason", "");
                if ("stop".equalsIgnoreCase(finishReason) && chunk.length() > 0) {
                    finalAnswer = chunk;
                } else {
                    merged.append(chunk);
                }
            } catch (Exception e) {
                logger.warn("LLM Studio SSE 청크 파싱 실패: " + e.getMessage());
            }
        }
        if (finalAnswer != null && finalAnswer.length() > 0) {
            return finalAnswer;
        }
        if (merged.length() > 0) {
            return merged.toString();
        }
        throw new RuntimeException("LLM Studio SSE 응답에서 answer를 찾지 못했습니다.");
    }

    /** 문자열 s 의 start 위치부터 시작하는 JSON 객체 한 덩어리 추출 */
    private String extractJsonObject(String s, int start) {
        int depth = 0;
        boolean inString = false;
        boolean escape = false;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inString) {
                if (escape) {
                    escape = false;
                } else if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return s.substring(start, i + 1);
                }
            }
        }
        return null;
    }

    private String extractAnswerFromJson(String responseJson) {
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

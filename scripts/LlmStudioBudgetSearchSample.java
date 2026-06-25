import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JSONObject;

/**
 * LLM Studio budget_search API 연동 샘플 (내부 AI 담당자 제출용).
 *
 * 요청 본문:
 *   user_query      — 챗봇 입력칸 사용자 문자열
 *   datetime        — 현재시간 자동입력
 *   stream          — true (Boolean, 스트리밍)
 *   dialog_history  — "[]" (String, JSON 형식)
 *
 * 컴파일·실행 예 (프로젝트 루트):
 *   javac -cp "target/bcjis-webapp/WEB-INF/lib/json-lib-2.4-jdk15.jar;target/classes" scripts/LlmStudioBudgetSearchSample.java
 *   java  -cp "target/bcjis-webapp/WEB-INF/lib/json-lib-2.4-jdk15.jar;target/classes;scripts" LlmStudioBudgetSearchSample
 */
public class LlmStudioBudgetSearchSample {

    private static final String ENDPOINT =
            "http://99.1.82.207:8080/llm-studio/v1/api/task/generate/syncapi/busan_ai_llm/budget_search";

    private static final String DATETIME_PATTERN = "yyyy년 M월 d일 EEEE a h:mm";

    public static void main(String[] args) throws Exception {
        String userQuery = "부산시청 AI 예산 심의방법을 알려줘";
        if (args.length > 0) {
            userQuery = args[0];
        }

        String bodyJson = buildRequestBody(userQuery);
        System.out.println("POST " + ENDPOINT);
        System.out.println("Request body: " + bodyJson);

        String response = post(ENDPOINT, bodyJson);
        System.out.println("Response (truncated): "
                + (response.length() > 500 ? response.substring(0, 500) + "..." : response));

        String answer = extractAnswer(response);
        System.out.println("=== answer ===");
        System.out.println(answer);
    }

    private static String extractAnswer(String responseText) {
        if (responseText == null) {
            throw new RuntimeException("empty response");
        }
        String trimmed = responseText.trim();
        if (trimmed.indexOf("data:") > -1) {
            return extractAnswerFromSse(trimmed);
        }
        JSONObject root = JSONObject.fromObject(trimmed);
        return root.getJSONObject("llm_result").getString("answer");
    }

    private static String extractAnswerFromSse(String sseText) {
        StringBuilder merged = new StringBuilder();
        String finalAnswer = null;
        String[] lines = sseText.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.startsWith("data:")) {
                continue;
            }
            String jsonPart = line.substring(5).trim();
            if (jsonPart.length() == 0) {
                continue;
            }
            JSONObject root = JSONObject.fromObject(jsonPart);
            if (!root.containsKey("llm_result")) {
                continue;
            }
            JSONObject llmResult = root.getJSONObject("llm_result");
            if (!llmResult.containsKey("answer")) {
                continue;
            }
            String chunk = llmResult.getString("answer");
            String finishReason = llmResult.optString("finish_reason", "");
            if ("stop".equalsIgnoreCase(finishReason) && chunk != null && chunk.length() > 0) {
                finalAnswer = chunk;
            } else if (chunk != null) {
                merged.append(chunk);
            }
        }
        if (finalAnswer != null && finalAnswer.length() > 0) {
            return finalAnswer;
        }
        if (merged.length() > 0) {
            return merged.toString();
        }
        throw new RuntimeException("answer not found in SSE response");
    }

    private static String buildRequestBody(String userQuery) {
        JSONObject body = new JSONObject();
        body.put("user_query", userQuery);
        body.put("datetime", formatDatetime());
        body.put("stream", Boolean.TRUE);
        body.put("dialog_history", "[]");
        return body.toString();
    }

    private static String formatDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN, Locale.KOREAN);
        return sdf.format(new Date());
    }

    private static String post(String urlStr, String jsonBody) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(120000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            if (code >= 400) {
                throw new RuntimeException("HTTP " + code + ": " + sb.toString());
            }
            return sb.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}

package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 부산시 내부행정 AI '부기주무관' 시정자료 검색 클라이언트.
 * API 키는 Globals.AiBugiApiKey (추후 발급) 로 설정한다.
 */
@Component("aiBugiGovDataClient")
public class AiBugiGovDataClient {

    private static final Logger logger = Logger.getLogger(AiBugiGovDataClient.class);

    private static final String DEFAULT_URL =
            "https://busanai.busan.go.kr/bugi/external-research/government-data";

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean hasApiKey() {
        return getApiKey().length() > 0;
    }

    public JSONObject search(String keyword) throws Exception {
        JSONObject out = new JSONObject();
        String q = keyword == null ? "" : keyword.trim();
        if (q.length() == 0) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색어를 입력해 주세요.");
            return out;
        }
        if (!hasApiKey()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "시홈페이지(부기주무관) API 키가 아직 설정되지 않았습니다.\n"
                    + "globals.properties 에 Globals.AiBugiApiKey 를 등록한 뒤 Tomcat을 재기동해 주세요.\n"
                    + "(엔드포인트: " + getEndpoint() + ")");
            out.put("endpoint", getEndpoint());
            return out;
        }

        String method = getProp("Globals.AiBugiHttpMethod", "GET").toUpperCase();
        String body;
        if ("POST".equals(method)) {
            body = httpPostJson(getEndpoint(), buildPostBody(q));
        } else {
            body = httpGet(buildGetUrl(q));
        }

        List<JSONObject> items = parseItems(body);
        out.put("ok", Boolean.TRUE);
        out.put("keyword", q);
        out.put("items", items);
        out.put("count", Integer.valueOf(items.size()));
        if (items.isEmpty()) {
            out.put("rawHint", truncate(body, 400));
        }
        return out;
    }

    private String buildGetUrl(String q) throws Exception {
        String ep = getEndpoint();
        String sep = ep.indexOf('?') >= 0 ? "&" : "?";
        StringBuilder sb = new StringBuilder(ep);
        sb.append(sep).append("query=").append(URLEncoder.encode(q, "UTF-8"));
        String keyParam = getProp("Globals.AiBugiKeyParam", "apiKey");
        sb.append("&").append(keyParam).append("=").append(URLEncoder.encode(getApiKey(), "UTF-8"));
        return sb.toString();
    }

    private String buildPostBody(String q) {
        JSONObject body = new JSONObject();
        body.put("query", q);
        body.put("keyword", q);
        body.put("apiKey", getApiKey());
        return body.toString();
    }

    private List<JSONObject> parseItems(String body) {
        List<JSONObject> items = new ArrayList<JSONObject>();
        if (body == null || body.trim().length() == 0) {
            return items;
        }
        try {
            Object parsed = body.trim().startsWith("[")
                    ? (Object) JSONArray.fromObject(body)
                    : (Object) JSONObject.fromObject(body);
            collectItems(parsed, items, 0);
        } catch (Exception e) {
            logger.warn("부기주무관 응답 JSON 파싱 실패: " + e.getMessage());
            JSONObject one = new JSONObject();
            one.put("kind", "시홈페이지");
            one.put("title", "부기주무관 응답");
            one.put("sub", truncate(body, 300));
            one.put("url", getEndpoint());
            items.add(one);
        }
        return items;
    }

    private void collectItems(Object node, List<JSONObject> items, int depth) {
        if (node == null || depth > 6 || items.size() >= 30) {
            return;
        }
        if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.size(); i++) {
                collectItems(arr.get(i), items, depth + 1);
            }
            return;
        }
        if (!(node instanceof JSONObject)) {
            return;
        }
        JSONObject o = (JSONObject) node;
        String title = firstStr(o, new String[] {
                "title", "name", "subject", "제목", "자료명", "docTitle", "headline"
        });
        String url = firstStr(o, new String[] {
                "url", "link", "href", "detailUrl", "원문링크", "sourceUrl"
        });
        String snip = firstStr(o, new String[] {
                "snippet", "summary", "content", "description", "내용", "요약"
        });
        if (title.length() > 0 || url.length() > 0 || snip.length() > 0) {
            JSONObject item = new JSONObject();
            item.put("kind", "시홈페이지");
            item.put("title", title.length() > 0 ? title : "(제목 없음)");
            item.put("sub", truncate(snip, 200));
            item.put("url", url);
            items.add(item);
            return;
        }
        // 흔한 래퍼 키
        String[] wrap = new String[] { "data", "items", "results", "list", "documents", "rows" };
        for (int i = 0; i < wrap.length; i++) {
            if (o.containsKey(wrap[i])) {
                collectItems(o.get(wrap[i]), items, depth + 1);
            }
        }
        if (items.isEmpty() && depth == 0) {
            Iterator<?> it = o.keys();
            while (it.hasNext() && items.size() < 30) {
                String k = String.valueOf(it.next());
                collectItems(o.get(k), items, depth + 1);
            }
        }
    }

    private String httpGet(String urlStr) throws Exception {
        return http("GET", urlStr, null);
    }

    private String httpPostJson(String urlStr, String jsonBody) throws Exception {
        return http("POST", urlStr, jsonBody);
    }

    private String http(String method, String urlStr, String jsonBody) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(getIntProp("Globals.AiBugiTimeoutMs", 20000));
            conn.setReadTimeout(getIntProp("Globals.AiBugiTimeoutMs", 20000));
            conn.setRequestProperty("Accept", "application/json");
            String keyHeader = getProp("Globals.AiBugiKeyHeader", "X-API-KEY");
            if (keyHeader.length() > 0 && getApiKey().length() > 0) {
                conn.setRequestProperty(keyHeader, getApiKey());
            }
            if ("POST".equals(method) && jsonBody != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();
            }
            int code = conn.getResponseCode();
            InputStream in = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String resp = readStream(in);
            if (code >= 400) {
                logger.warn("부기주무관 API HTTP " + code + " " + truncate(resp, 300));
                throw new IllegalStateException("부기주무관 API 오류 HTTP " + code
                        + (resp.length() > 0 ? ("\n" + truncate(resp, 200)) : ""));
            }
            return resp;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readStream(InputStream in) throws Exception {
        if (in == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        br.close();
        return sb.toString();
    }

    private String firstStr(JSONObject row, String[] names) {
        for (int i = 0; i < names.length; i++) {
            if (row.containsKey(names[i]) && row.get(names[i]) != null) {
                String s = String.valueOf(row.get(names[i])).trim();
                if (s.length() > 0 && !"null".equalsIgnoreCase(s)) {
                    return s;
                }
            }
        }
        return "";
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max) + "...";
    }

    private String getEndpoint() {
        return getProp("Globals.AiBugiEndpoint", DEFAULT_URL);
    }

    private String getApiKey() {
        return getProp("Globals.AiBugiApiKey", "");
    }

    private String getProp(String key, String def) {
        if (config == null) {
            return def;
        }
        String v = config.getProperty(key);
        if (v == null || v.trim().length() == 0) {
            return def;
        }
        return v.trim();
    }

    private int getIntProp(String key, int def) {
        try {
            String v = getProp(key, "");
            if (v.length() > 0) {
                return Integer.parseInt(v);
            }
        } catch (Exception e) {
            // ignore
        }
        return def;
    }
}

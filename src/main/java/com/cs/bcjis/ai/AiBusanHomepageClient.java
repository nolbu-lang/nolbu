package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 부산시 홈페이지 게시판(보도자료·고시공고·새소식) 키워드 검색.
 * 부기주무관 API 키 없이 제목·게시일·링크를 직접 수집한다. (기간: 2025~2026)
 */
@Component("aiBusanHomepageClient")
public class AiBusanHomepageClient {

    private static final Logger logger = Logger.getLogger(AiBusanHomepageClient.class);

    private static final String BASE = "https://www.busan.go.kr";
    private static final String BEGIN_DT = "2025-01-01";
    private static final String END_DT = "2026-12-31";

    private static final Pattern PRESS_BLOCK = Pattern.compile(
            "(?is)<a\\s+[^>]*href=\"(/nbtnewsBU/\\d+)[^\"]*\"[^>]*class=\"[^\"]*item[^\"]*\"[^>]*>"
                    + "(.*?)</a>");
    private static final Pattern PRESS_BLOCK_ALT = Pattern.compile(
            "(?is)<a\\s+[^>]*class=\"[^\"]*item[^\"]*\"[^>]*href=\"(/nbtnewsBU/\\d+)[^\"]*\"[^>]*>"
                    + "(.*?)</a>");
    private static final Pattern PRESS_TITLE = Pattern.compile(
            "(?is)<div\\s+class=\"bTitle\">(.*?)</div>");
    private static final Pattern GOSI_ROW = Pattern.compile(
            "(?is)<tr[^>]*>\\s*"
                    + "<td[^>]*>.*?</td>\\s*"
                    + "<td[^>]*>\\s*<a\\s+[^>]*href=\"(/nbgosi/view\\?[^\"]+)\"[^>]*>(.*?)</a>\\s*</td>\\s*"
                    + "<td[^>]*>.*?</td>\\s*"
                    + "<td[^>]*>(.*?)</td>");
    private static final Pattern NEWS_ROW = Pattern.compile(
            "(?is)<tr[^>]*>\\s*(?:<!--.*?-->\\s*)*"
                    + "<td[^>]*>.*?</td>\\s*(?:<!--.*?-->\\s*)*"
                    + "<td[^>]*class=\"[^\"]*title[^\"]*\"[^>]*>\\s*"
                    + "<a\\s+[^>]*href=\"(/nbnews/\\d+)[^\"]*\"[^>]*>(.*?)</a>\\s*</td>"
                    + "(.*?)</tr>");
    private static final Pattern NEWS_DATE_TD = Pattern.compile(
            "(?is)<td[^>]*class=\"[^\"]*nowrap[^\"]*\"[^>]*>\\s*(20\\d{2}[-.]\\d{2}[-.]\\d{2})\\s*</td>");
    private static final Pattern DATE_YMD = Pattern.compile("(20\\d{2})[-.](\\d{2})[-.](\\d{2})");
    private static final Pattern TAG = Pattern.compile("(?is)<[^>]+>");
    private static final Pattern WS = Pattern.compile("\\s+");

    private static final ExecutorService POOL = Executors.newFixedThreadPool(3, new ThreadFactory() {
        private final AtomicInteger n = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ai-busan-hp-" + n.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    });

    @Autowired
    @Qualifier("config")
    private Properties config;

    public JSONObject search(String keyword) throws Exception {
        JSONObject out = new JSONObject();
        String q = keyword == null ? "" : keyword.trim();
        if (q.length() == 0) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색어를 입력해 주세요.");
            return out;
        }
        final AiKeywordMatcher.SearchExpression expression = AiKeywordMatcher.parseExpression(q);
        if (expression.isEmpty()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색할 키워드를 입력해 주세요.");
            return out;
        }

        int perBoard = getIntProp("Globals.AiBusanHomepageMaxPerBoard", 10);
        List<Callable<BoardResult>> tasks = new ArrayList<Callable<BoardResult>>();
        List<String> terms = expression.getTerms();
        int termLimit = Math.min(terms.size(), 6);
        for (int t = 0; t < termLimit; t++) {
            String remoteQ = AiKeywordMatcher.collapseSpaces(terms.get(t));
            tasks.add(searchPressTask(remoteQ, perBoard));
            tasks.add(searchGosiTask(remoteQ, perBoard));
            tasks.add(searchNewsTask(remoteQ, perBoard));
        }

        List<JSONObject> items = new ArrayList<JSONObject>();
        Map<String, JSONObject> uniqueItems = new LinkedHashMap<String, JSONObject>();
        List<String> errors = new ArrayList<String>();
        try {
            List<Future<BoardResult>> futures = POOL.invokeAll(tasks, 18, TimeUnit.SECONDS);
            for (Future<BoardResult> f : futures) {
                if (f.isCancelled()) {
                    errors.add("검색 시간 초과");
                    continue;
                }
                try {
                    BoardResult br = f.get();
                    if (br.error != null && br.error.length() > 0) {
                        errors.add(br.kind + ": " + br.error);
                    }
                    if (br.items != null) {
                        for (int i = 0; i < br.items.size(); i++) {
                            JSONObject it = br.items.get(i);
                            String title = it.optString("title", "");
                            // (A AND B) OR C 조건 + 문장형 유사도 60%
                            if (AiKeywordMatcher.matchesExpression(expression, title)) {
                                it.put("score", Double.valueOf(
                                        AiKeywordMatcher.bestExpressionSimilarity(expression, title)));
                                String key = it.optString("url", "");
                                if (key.length() == 0) {
                                    key = it.optString("kind", "") + "|" + title;
                                }
                                uniqueItems.put(key, it);
                            }
                        }
                    }
                } catch (Exception e) {
                    errors.add(e.getMessage() == null ? e.toString() : e.getMessage());
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            out.put("ok", Boolean.FALSE);
            out.put("message", "시홈페이지 검색이 중단되었습니다.");
            return out;
        }
        items.addAll(uniqueItems.values());

        // 붙여쓰기 키워드(제2회추경)는 시 홈페이지가 0건을 주는 경우가 많음.
        // → 끝 2글자(추경 등)로 넓게 재검색 후, 제목 유사도(공백무시)로 거른다.
        if (items.isEmpty()) {
            try {
                List<Callable<BoardResult>> fbTasks = new ArrayList<Callable<BoardResult>>();
                for (int t = 0; t < termLimit; t++) {
                    String compact = AiKeywordMatcher.compactQuery(terms.get(t));
                    if (compact.length() < 4) {
                        continue;
                    }
                    String fallback = compact.substring(compact.length() - 2);
                    fbTasks.add(searchPressTask(fallback, perBoard));
                    fbTasks.add(searchGosiTask(fallback, perBoard));
                    fbTasks.add(searchNewsTask(fallback, perBoard));
                }
                if (fbTasks.isEmpty()) {
                    throw new IllegalArgumentException("보조검색 키워드 없음");
                }
                List<Future<BoardResult>> fbFutures = POOL.invokeAll(fbTasks, 18, TimeUnit.SECONDS);
                Map<String, JSONObject> uniq = new LinkedHashMap<String, JSONObject>();
                for (Future<BoardResult> f : fbFutures) {
                    if (f.isCancelled()) {
                        continue;
                    }
                    BoardResult br = f.get();
                    if (br == null || br.items == null) {
                        continue;
                    }
                    for (int i = 0; i < br.items.size(); i++) {
                        JSONObject it = br.items.get(i);
                        String title = it.optString("title", "");
                        String url = it.optString("url", "");
                        if (!AiKeywordMatcher.matchesExpression(expression, title)) {
                            continue;
                        }
                        if (!uniq.containsKey(url)) {
                            it.put("score", Double.valueOf(
                                    AiKeywordMatcher.bestExpressionSimilarity(expression, title)));
                            uniq.put(url, it);
                        }
                    }
                }
                items.addAll(uniq.values());
            } catch (Exception e) {
                logger.warn("보도/고시 보조검색 실패: " + e.getMessage());
            }
        }

        out.put("ok", Boolean.TRUE);
        out.put("keyword", q);
        out.put("items", items);
        out.put("count", Integer.valueOf(items.size()));
        out.put("period", BEGIN_DT + " ~ " + END_DT);
        if (!errors.isEmpty() && items.isEmpty()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "시홈페이지 검색에 실패했습니다.\n" + join(errors, "\n"));
        } else if (!errors.isEmpty()) {
            out.put("warnings", errors);
        }
        return out;
    }

    private Callable<BoardResult> searchPressTask(final String q, final int limit) {
        return new Callable<BoardResult>() {
            public BoardResult call() {
                BoardResult br = new BoardResult("보도자료");
                try {
                    String body = "srchBeginDt=" + enc(BEGIN_DT)
                            + "&srchEndDt=" + enc(END_DT)
                            + "&srchKey=sj"
                            + "&srchText=" + enc(q);
                    String html = httpPost(BASE + "/nbtnewsBU", body);
                    br.items = parsePress(html, limit);
                } catch (Exception e) {
                    logger.warn("보도자료 검색 실패: " + e.getMessage());
                    br.error = e.getMessage();
                }
                return br;
            }
        };
    }

    private Callable<BoardResult> searchGosiTask(final String q, final int limit) {
        return new Callable<BoardResult>() {
            public BoardResult call() {
                BoardResult br = new BoardResult("고시공고");
                try {
                    String url = BASE + "/nbgosi/list"
                            + "?conIfmStdt=" + enc(BEGIN_DT)
                            + "&conIfmEnddt=" + enc(END_DT)
                            + "&conGosiGbn="
                            + "&schKeyType=A"
                            + "&srchText=" + enc(q);
                    String html = httpGet(url);
                    br.items = parseGosi(html, limit);
                } catch (Exception e) {
                    logger.warn("고시공고 검색 실패: " + e.getMessage());
                    br.error = e.getMessage();
                }
                return br;
            }
        };
    }

    private Callable<BoardResult> searchNewsTask(final String q, final int limit) {
        return new Callable<BoardResult>() {
            public BoardResult call() {
                BoardResult br = new BoardResult("새소식");
                try {
                    String body = "srchBeginDt=" + enc(BEGIN_DT)
                            + "&srchEndDt=" + enc(END_DT)
                            + "&srchKey=sj"
                            + "&srchText=" + enc(q);
                    String html = httpPost(BASE + "/nbnews", body);
                    br.items = parseNews(html, limit);
                } catch (Exception e) {
                    logger.warn("새소식 검색 실패: " + e.getMessage());
                    br.error = e.getMessage();
                }
                return br;
            }
        };
    }

    private List<JSONObject> parsePress(String html, int limit) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (html == null) {
            return list;
        }
        Map<String, String[]> found = new LinkedHashMap<String, String[]>();
        collectPressBlocks(PRESS_BLOCK.matcher(html), found, limit);
        if (found.size() < limit) {
            collectPressBlocks(PRESS_BLOCK_ALT.matcher(html), found, limit);
        }
        for (Map.Entry<String, String[]> e : found.entrySet()) {
            list.add(item("보도자료", e.getValue()[0], BASE + e.getKey(), e.getValue()[1]));
        }
        return list;
    }

    private void collectPressBlocks(Matcher m, Map<String, String[]> found, int limit) {
        while (m.find() && found.size() < limit) {
            String path = htmlUnescape(m.group(1)).trim();
            String block = m.group(2);
            Matcher tm = PRESS_TITLE.matcher(block);
            String title = tm.find() ? cleanText(tm.group(1)) : "";
            if (path.length() == 0 || title.length() == 0) {
                continue;
            }
            int q = path.indexOf('?');
            String key = q > 0 ? path.substring(0, q) : path;
            if (!found.containsKey(key)) {
                found.put(key, new String[] { title, extractDate(block) });
            }
        }
    }

    private List<JSONObject> parseGosi(String html, int limit) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (html == null) {
            return list;
        }
        Map<String, Boolean> seen = new LinkedHashMap<String, Boolean>();
        Matcher m = GOSI_ROW.matcher(html);
        while (m.find() && seen.size() < limit) {
            String path = htmlUnescape(m.group(1)).trim();
            String title = cleanText(m.group(2));
            String dateCell = m.group(3);
            if (title.length() == 0 || path.indexOf("sno=") < 0 || path.indexOf("/nbgosi/view") < 0) {
                continue;
            }
            String sno = extractQueryParam(path, "sno");
            if (sno.length() == 0 || seen.containsKey(sno)) {
                continue;
            }
            seen.put(sno, Boolean.TRUE);
            String gbn = extractQueryParam(path, "gosiGbn");
            StringBuilder href = new StringBuilder(BASE).append("/nbgosi/view?").append(sno);
            if (gbn.length() > 0) {
                href.append("&").append(gbn);
            }
            list.add(item("고시공고", title, href.toString(), extractDate(dateCell)));
        }
        // 행 파싱 실패 시 제목만이라도 수집
        if (list.isEmpty()) {
            Matcher m2 = Pattern.compile(
                    "(?is)<a\\s+[^>]*href=\"(/nbgosi/view\\?[^\"]+)\"[^>]*>(.*?)</a>").matcher(html);
            while (m2.find() && list.size() < limit) {
                String path = htmlUnescape(m2.group(1)).trim();
                String title = cleanText(m2.group(2));
                String sno = extractQueryParam(path, "sno");
                if (title.length() == 0 || sno.length() == 0 || seen.containsKey(sno)) {
                    continue;
                }
                seen.put(sno, Boolean.TRUE);
                String gbn = extractQueryParam(path, "gosiGbn");
                StringBuilder href = new StringBuilder(BASE).append("/nbgosi/view?").append(sno);
                if (gbn.length() > 0) {
                    href.append("&").append(gbn);
                }
                list.add(item("고시공고", title, href.toString(), ""));
            }
        }
        return list;
    }

    private List<JSONObject> parseNews(String html, int limit) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (html == null) {
            return list;
        }
        Map<String, Boolean> seen = new LinkedHashMap<String, Boolean>();
        Matcher m = NEWS_ROW.matcher(html);
        while (m.find() && seen.size() < limit) {
            String path = htmlUnescape(m.group(1)).trim();
            String title = cleanText(m.group(2));
            String rest = m.group(3);
            if (path.length() == 0 || title.length() < 2) {
                continue;
            }
            int q = path.indexOf('?');
            String key = q > 0 ? path.substring(0, q) : path;
            if (seen.containsKey(key)) {
                continue;
            }
            seen.put(key, Boolean.TRUE);
            String date = "";
            Matcher dm = NEWS_DATE_TD.matcher(rest == null ? "" : rest);
            if (dm.find()) {
                date = extractDate(dm.group(1));
            } else {
                date = extractDate(rest);
            }
            list.add(item("새소식", title, BASE + key, date));
        }
        if (list.isEmpty()) {
            Matcher m2 = Pattern.compile(
                    "(?is)<a\\s+[^>]*href=\"(/nbnews/\\d+)[^\"]*\"[^>]*>(.*?)</a>").matcher(html);
            while (m2.find() && list.size() < limit) {
                String path = htmlUnescape(m2.group(1)).trim();
                String title = cleanText(m2.group(2));
                if (path.length() == 0 || title.length() < 2) {
                    continue;
                }
                int q = path.indexOf('?');
                String key = q > 0 ? path.substring(0, q) : path;
                if (seen.containsKey(key)) {
                    continue;
                }
                seen.put(key, Boolean.TRUE);
                list.add(item("새소식", title, BASE + key, ""));
            }
        }
        return list;
    }

    /** YYYY-MM-DD 로 정규화. 없으면 빈 문자열. */
    private static String extractDate(String raw) {
        if (raw == null || raw.length() == 0) {
            return "";
        }
        Matcher m = DATE_YMD.matcher(raw);
        if (!m.find()) {
            return "";
        }
        return m.group(1) + "-" + m.group(2) + "-" + m.group(3);
    }

    private JSONObject item(String kind, String title, String url, String date) {
        JSONObject o = new JSONObject();
        o.put("kind", kind);
        o.put("title", title);
        o.put("url", url);
        o.put("date", date == null ? "" : date);
        o.put("sub", "");
        return o;
    }

    private String httpGet(String urlStr) throws Exception {
        return http("GET", urlStr, null);
    }

    private String httpPost(String urlStr, String formBody) throws Exception {
        return http("POST", urlStr, formBody);
    }

    private String http(String method, String urlStr, String formBody) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod(method);
            int timeout = getIntProp("Globals.AiBusanHomepageTimeoutMs", 15000);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (compatible; BCJIS-AI/1.0; +https://www.busan.go.kr)");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9");
            if ("POST".equals(method) && formBody != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded; charset=UTF-8");
                byte[] bytes = formBody.getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                OutputStream os = conn.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
            int code = conn.getResponseCode();
            InputStream in = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String charset = charsetFromContentType(conn.getContentType());
            String resp = readStream(in, charset);
            if (code >= 400) {
                throw new Exception("HTTP " + code + " " + urlStr);
            }
            return resp;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readStream(InputStream in, String charset) throws Exception {
        if (in == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[4096];
        int n;
        int max = 2 * 1024 * 1024;
        while ((n = br.read(buf)) >= 0) {
            if (sb.length() + n > max) {
                sb.append(buf, 0, max - sb.length());
                break;
            }
            sb.append(buf, 0, n);
        }
        br.close();
        return sb.toString();
    }

    private static String charsetFromContentType(String ct) {
        if (ct == null) {
            return "UTF-8";
        }
        String lower = ct.toLowerCase();
        int i = lower.indexOf("charset=");
        if (i < 0) {
            return "UTF-8";
        }
        String cs = ct.substring(i + 8).trim();
        int sc = cs.indexOf(';');
        if (sc > 0) {
            cs = cs.substring(0, sc).trim();
        }
        if (cs.startsWith("\"") && cs.endsWith("\"") && cs.length() > 1) {
            cs = cs.substring(1, cs.length() - 1);
        }
        return cs.length() > 0 ? cs : "UTF-8";
    }

    private static String enc(String s) throws Exception {
        return URLEncoder.encode(s == null ? "" : s, "UTF-8");
    }

    private static String cleanText(String raw) {
        if (raw == null) {
            return "";
        }
        String t = TAG.matcher(raw).replaceAll("");
        t = htmlUnescape(t);
        t = WS.matcher(t).replaceAll(" ").trim();
        return t;
    }

    private static String htmlUnescape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&nbsp;", " ");
    }

    private static String extractQueryParam(String path, String name) {
        int i = path.indexOf(name + "=");
        if (i < 0) {
            return path;
        }
        int start = i + name.length() + 1;
        int end = path.indexOf('&', start);
        if (end < 0) {
            end = path.length();
        }
        return name + "=" + path.substring(start, end);
    }

    private static String join(List<String> list, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private int getIntProp(String key, int def) {
        try {
            String v = config == null ? null : config.getProperty(key);
            if (v == null || v.trim().length() == 0) {
                return def;
            }
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static final class BoardResult {
        final String kind;
        List<JSONObject> items = new ArrayList<JSONObject>();
        String error;

        BoardResult(String kind) {
            this.kind = kind;
        }
    }
}

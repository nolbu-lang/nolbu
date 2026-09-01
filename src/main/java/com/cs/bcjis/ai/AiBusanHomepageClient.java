package com.cs.bcjis.ai;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    // 운영 WAS의 구버전 JDK(JSSE)가 TLS1.2를 지원하지 않아 HttpsURLConnection 대신
    // OS의 curl(OpenSSL)로 우회 요청한다. (2026-08-20)
    private static final String HTTP_CODE_MARK = "\n@@BCJIS_HTTP_CODE@@:";

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
                    logger.warn("보도자료 검색 실패: " + e.getMessage(), e);
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
                    logger.warn("고시공고 검색 실패: " + e.getMessage(), e);
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
                    logger.warn("새소식 검색 실패: " + e.getMessage(), e);
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

    /**
     * HttpsURLConnection(JVM 내장 JSSE) 대신 OS의 curl(OpenSSL)로 요청한다.
     * 운영 WAS의 구버전 JDK가 TLS1.2를 지원하지 않아 www.busan.go.kr(TLS1.2 필수)과의
     * 핸드셰이크가 항상 실패했기 때문(2026-08-20 확인) — curl은 OS OpenSSL을 쓰므로 무관하다.
     */
    private String http(String method, String urlStr, String formBody) throws Exception {
        int timeoutMs = getIntProp("Globals.AiBusanHomepageTimeoutMs", 15000);
        int timeoutSec = Math.max(1, (timeoutMs + 999) / 1000);

        List<String> cmd = new ArrayList<String>();
        cmd.add("curl");
        cmd.add("-s");
        cmd.add("-S");
        cmd.add("-L");
        cmd.add("--max-time");
        cmd.add(String.valueOf(timeoutSec));
        cmd.add("-A");
        cmd.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        cmd.add("-H");
        cmd.add("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        cmd.add("-H");
        cmd.add("Accept-Language: ko-KR,ko;q=0.9");
        if ("POST".equals(method) && formBody != null) {
            cmd.add("-H");
            cmd.add("Content-Type: application/x-www-form-urlencoded; charset=UTF-8");
            cmd.add("--data");
            cmd.add(formBody);
        }
        cmd.add("-w");
        cmd.add(HTTP_CODE_MARK + "%{http_code}");
        cmd.add(urlStr);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        String out = new String(readAllBytes(proc.getInputStream()), "UTF-8");
        int exitCode = proc.waitFor();

        int mark = out.lastIndexOf(HTTP_CODE_MARK);
        String body = mark >= 0 ? out.substring(0, mark) : out;
        String codeStr = mark >= 0 ? out.substring(mark + HTTP_CODE_MARK.length()).trim() : "";

        if (exitCode != 0) {
            String detail = out.trim();
            if (detail.length() > 300) {
                detail = detail.substring(0, 300);
            }
            throw new Exception("curl 실행 실패(exit=" + exitCode + "): " + urlStr
                    + (detail.length() > 0 ? " - " + detail : ""));
        }
        int code = codeStr.length() > 0 ? Integer.parseInt(codeStr) : 0;
        if (code >= 400) {
            throw new Exception("HTTP " + code + " " + urlStr);
        }
        return body;
    }

    private static byte[] readAllBytes(InputStream in) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int n;
        while ((n = in.read(chunk)) >= 0) {
            buf.write(chunk, 0, n);
        }
        return buf.toByteArray();
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

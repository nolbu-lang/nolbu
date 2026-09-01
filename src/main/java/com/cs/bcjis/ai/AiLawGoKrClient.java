package com.cs.bcjis.ai;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 법제처 국가법령정보 Open API (law.go.kr DRF) 클라이언트.
 * OC 인증키로 법령·자치법규(조례) 목록을 조회한다.
 */
@Component("aiLawGoKrClient")
public class AiLawGoKrClient {

    private static final Logger logger = Logger.getLogger(AiLawGoKrClient.class);

    private static final String DEFAULT_BASE = "https://www.law.go.kr/DRF/lawSearch.do";
    private static final String SERVICE_BASE = "https://www.law.go.kr/DRF/lawService.do";
    private static final String DETAIL_BASE = "https://www.law.go.kr";
    // 운영 WAS의 구버전 JDK(JSSE)가 TLS1.2를 지원하지 않아 HttpsURLConnection 대신
    // OS의 curl(OpenSSL)로 우회 요청한다. (2026-08-20, AiBusanHomepageClient와 동일 조치)
    private static final String HTTP_CODE_MARK = "\n@@BCJIS_HTTP_CODE@@:";
    private static final Pattern JO_PATTERN = Pattern.compile(
            "제\\s*([0-9]+)\\s*조(?:\\s*의\\s*([0-9]+))?");

    private static final ExecutorService LAW_IO_POOL = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private final AtomicInteger n = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ai-law-" + n.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    });
    /** 검색식 항(OR/AND 분해) 병렬용 — LAW_IO_POOL 과 분리(중첩 submit 교착 방지) */
    private static final ExecutorService LAW_TERM_POOL = Executors.newFixedThreadPool(3, new ThreadFactory() {
        private final AtomicInteger n = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ai-law-term-" + n.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    });

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean isEnabled() {
        return getOc().length() > 0;
    }

    /**
     * 키워드로 법령 + 부산광역시(본청) 조례를 검색한다.
     * "○○법 제N조" 형이면 JSON 본문 API로 해당 조문 텍스트까지 채운다.
     */
    public JSONObject searchLawAndBusanOrdin(String keyword) throws Exception {
        AiKeywordMatcher.SearchExpression expression = AiKeywordMatcher.parseExpression(keyword);
        List<String> terms = expression.getTerms();
        if (terms.size() <= 1) {
            return searchLawAndBusanOrdinSingle(keyword);
        }

        JSONObject out = new JSONObject();
        Map<String, JSONObject> unique = new java.util.LinkedHashMap<String, JSONObject>();
        int termLimit = Math.min(terms.size(), 6);
        // 항별 원격검색을 병렬 수행 (순차 대비 대기시간 단축, 결과 집합은 동일)
        List<Future<JSONObject>> futures = new ArrayList<Future<JSONObject>>();
        for (int t = 0; t < termLimit; t++) {
            final String term = terms.get(t);
            futures.add(LAW_TERM_POOL.submit(new Callable<JSONObject>() {
                public JSONObject call() throws Exception {
                    return searchLawAndBusanOrdinSingle(term);
                }
            }));
        }
        int waitMs = getIntProp("Globals.AiLawGoKrTimeoutMs", 15000) + 8000;
        for (int t = 0; t < futures.size(); t++) {
            JSONObject one;
            try {
                one = futures.get(t).get(waitMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.warn("법령 항별 병렬검색 실패 term=" + terms.get(t) + ": " + e.getMessage());
                try {
                    one = searchLawAndBusanOrdinSingle(terms.get(t));
                } catch (Exception e2) {
                    continue;
                }
            }
            JSONArray oneItems = one.optJSONArray("items");
            if (oneItems == null) {
                continue;
            }
            for (int i = 0; i < oneItems.size(); i++) {
                JSONObject item = oneItems.getJSONObject(i);
                String title = item.optString("title", "");
                String body = item.optString("body", "");
                String searchable = title + "\n" + body;
                boolean pass;
                if (body.trim().length() > 0) {
                    pass = AiKeywordMatcher.containsExpression(expression, searchable)
                            || AiKeywordMatcher.matchesExpression(expression, searchable);
                } else {
                    pass = AiKeywordMatcher.containsExpression(expression, title)
                            || AiKeywordMatcher.matchesExpression(expression, title)
                            || titleMatchesAnyTerm(title, terms);
                }
                if (!pass) {
                    continue;
                }
                String key = item.optString("url", "");
                if (key.length() == 0) {
                    key = item.optString("kind", "") + "|" + title;
                }
                unique.put(key, item);
            }
        }
        JSONArray merged = new JSONArray();
        merged.addAll(unique.values());
        out.put("ok", Boolean.TRUE);
        out.put("keyword", keyword == null ? "" : keyword.trim());
        out.put("joNum", Integer.valueOf(0));
        out.put("items", merged);
        out.put("count", Integer.valueOf(merged.size()));
        return out;
    }

    /** 제목에 검색식 항(개별 키워드)이 하나라도 포함되면 true */
    private boolean titleMatchesAnyTerm(String title, List<String> terms) {
        if (title == null || terms == null || terms.isEmpty()) {
            return false;
        }
        for (int i = 0; i < terms.size(); i++) {
            String term = terms.get(i);
            if (term == null || term.trim().length() == 0) {
                continue;
            }
            if (AiKeywordMatcher.matches(term, title) || AiKeywordMatcher.containsExpression(
                    AiKeywordMatcher.parseExpression(term), title)) {
                return true;
            }
        }
        return false;
    }

    private JSONObject searchLawAndBusanOrdinSingle(String keyword) throws Exception {
        JSONObject out = new JSONObject();
        if (!isEnabled()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "법제처 Open API OC 키가 없습니다. (Globals.AiLawGoKrOc)");
            return out;
        }
        String rawQ = keyword == null ? "" : keyword.trim();
        String q = normalizeLawQuery(rawQ);
        if (q.length() == 0) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색어를 입력해 주세요.");
            return out;
        }

        ParsedQuery pq = parseQuery(q);
        final int display = getIntProp("Globals.AiLawGoKrDisplay", 20);
        List<JSONObject> items = new ArrayList<JSONObject>();

        // 법제처 목록 API는 "지방재정법의 예산의원칙"처럼 법령명+주제를 붙이면 0건이 된다.
        // → 법령명으로 검색하고, 주제/조문번호로 본문을 채운다.
        String listQuery = pq.lawName.length() > 0 ? pq.lawName : q;
        final String org = getProp("Globals.AiLawOrdinOrgCd", "6260000");
        final int ordinDisplay = Math.max(display, 25);
        final String fq = listQuery;

        Future<JSONArray> lawFuture = LAW_IO_POOL.submit(new Callable<JSONArray>() {
            public JSONArray call() throws Exception {
                return fetchList("law", fq, null, null, display);
            }
        });
        Future<JSONArray> ordinFuture = LAW_IO_POOL.submit(new Callable<JSONArray>() {
            public JSONArray call() throws Exception {
                return fetchList("ordin", fq, org, "30001", ordinDisplay);
            }
        });

        JSONArray laws;
        JSONArray ordins;
        try {
            int waitMs = getIntProp("Globals.AiLawGoKrTimeoutMs", 15000) + 5000;
            laws = lawFuture.get(waitMs, TimeUnit.MILLISECONDS);
            ordins = ordinFuture.get(waitMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("법령/조례 병렬 목록 조회 실패, 순차 재시도: " + e.getMessage());
            try { lawFuture.cancel(true); } catch (Exception ignore) { /* */ }
            try { ordinFuture.cancel(true); } catch (Exception ignore) { /* */ }
            laws = fetchList("law", listQuery, null, null, display);
            ordins = fetchList("ordin", listQuery, org, "30001", ordinDisplay);
        }

        // 조문 지정·주제 검색 시 법령명 정확 매칭 우선
        if (pq.joNum > 0 || pq.topic.length() > 0) {
            laws = preferExactName(laws, pq.lawName.length() > 0 ? pq.lawName : listQuery);
        }
        appendLawItems(items, laws, "법령");
        appendOrdinItems(items, ordins, "조례", true);
        if (countByKind(items, "조례") == 0) {
            JSONArray ordins2 = fetchList("ordin", "부산광역시 " + listQuery, null, "30001", ordinDisplay);
            appendOrdinItems(items, ordins2, "조례", true);
        }

        // 조문번호가 있으면 해당 조문 본문 채움
        if (pq.joNum > 0) {
            fillArticleBodies(items, pq.joNum, pq.joBranch, 3);
        } else if (pq.topic.length() > 0) {
            // "지방재정법의 예산의원칙" → 관련 조문(제목·내용 키워드) 본문 채움
            fillArticlesByTopic(items, pq.topic, 5);
        }

        // 일반자료: 제목 유사도 필터. 주제검색은 법령명 기준으로 필터
        String simKey = pq.lawName.length() > 0 ? pq.lawName : q;
        items = filterByTitleSimilarity(items, simKey);

        // 조문 본문이 채워진 항목을 앞으로
        if (pq.joNum > 0 || pq.topic.length() > 0) {
            List<JSONObject> withBody = new ArrayList<JSONObject>();
            List<JSONObject> without = new ArrayList<JSONObject>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).optString("body").length() > 0) {
                    withBody.add(items.get(i));
                } else {
                    without.add(items.get(i));
                }
            }
            items = new ArrayList<JSONObject>();
            items.addAll(withBody);
            items.addAll(without);
        }

        if (items.size() > display) {
            items = new ArrayList<JSONObject>(items.subList(0, display));
        }

        out.put("ok", Boolean.TRUE);
        out.put("keyword", rawQ.length() > 0 ? rawQ : q);
        out.put("joNum", Integer.valueOf(pq.joNum));
        out.put("topic", pq.topic);
        out.put("lawName", pq.lawName);
        out.put("items", items);
        out.put("count", Integer.valueOf(items.size()));
        return out;
    }

    private static class ParsedQuery {
        String lawName = "";
        String topic = "";
        int joNum = 0;
        int joBranch = 0;
    }

    /**
     * 질의 파싱.
     * - "지방재정법 제17조" → 법령명 + 조문번호
     * - "지방재정법의 예산의원칙" / "지방재정법 예산 원칙" → 법령명 + 주제키워드
     */
    private ParsedQuery parseQuery(String q) {
        ParsedQuery pq = new ParsedQuery();
        if (q == null || q.trim().length() == 0) {
            return pq;
        }
        String raw = q.trim();
        Matcher m = JO_PATTERN.matcher(raw);
        if (m.find()) {
            try {
                pq.joNum = Integer.parseInt(m.group(1));
            } catch (Exception e) {
                pq.joNum = 0;
            }
            if (m.group(2) != null) {
                try {
                    pq.joBranch = Integer.parseInt(m.group(2));
                } catch (Exception e) {
                    pq.joBranch = 0;
                }
            }
            String name = (raw.substring(0, m.start()) + " " + raw.substring(m.end())).trim();
            name = name.replaceAll("\\s+", " ").trim();
            // 조사 잔여 제거
            name = name.replaceAll("(의|에서|중|에\\s*관한)$", "").trim();
            pq.lawName = name;
            return pq;
        }

        Matcher lm = Pattern.compile(
                "^(.+?(?:법|령|규칙|규정|조례))(?:의|에서|중|에\\s*관한|에\\s*대한)?\\s*(.+)$")
                .matcher(raw);
        if (lm.find()) {
            String name = lm.group(1).replaceAll("\\s+", " ").trim();
            String topic = lm.group(2).replaceAll("\\s+", " ").trim();
            // 주제 mid "의" 연결 정리 (예산의원칙 → 예산 원칙)
            topic = topic.replaceAll("의", " ").replaceAll("\\s+", " ").trim();
            if (name.length() >= 2 && topic.length() >= 2) {
                pq.lawName = name;
                pq.topic = topic;
                return pq;
            }
        }
        pq.lawName = raw;
        return pq;
    }

    /**
     * 법령 검색어 정규화.
     * - 「」, 『』, [], (), 따옴표 등 특수문자를 공백으로 치환
     * - &/, 는 검색식 연산자이므로 단일항 검색 전에 이미 분리됨
     */
    private String normalizeLawQuery(String q) {
        if (q == null) {
            return "";
        }
        String s = q.trim();
        s = s.replaceAll("[「」『』【】\\[\\]\\(\\)\\{\\}\"'`´“”‘’<>]", " ");
        // 쉼표·& 는 상위(검색식)에서 분리. 단일항에서는 공백화
        s = s.replaceAll("[,.;:|/\\\\~!@#$%^*_+=?]+", " ");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    /** 제목 기준 유사도 필터. 전부 탈락하면 원본 유지(원격 API 결과 보호). */
    private List<JSONObject> filterByTitleSimilarity(List<JSONObject> items, String query) {
        if (items == null || items.isEmpty() || query == null || query.trim().length() == 0) {
            return items;
        }
        List<JSONObject> kept = new ArrayList<JSONObject>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject it = items.get(i);
            String title = it.optString("title", "");
            if (AiKeywordMatcher.matches(query, title)) {
                kept.add(it);
            }
        }
        return kept.isEmpty() ? items : kept;
    }

    private JSONArray preferExactName(JSONArray laws, String lawName) {
        if (laws == null || laws.isEmpty() || lawName == null || lawName.length() == 0) {
            return laws;
        }
        JSONArray exact = new JSONArray();
        JSONArray rest = new JSONArray();
        String target = lawName.replaceAll("\\s+", "");
        for (int i = 0; i < laws.size(); i++) {
            JSONObject row = laws.getJSONObject(i);
            String title = firstStr(row, new String[] { "법령명한글", "법령명" }).replaceAll("\\s+", "");
            if (title.equals(target) || title.indexOf(target) >= 0 || target.indexOf(title) >= 0) {
                exact.add(row);
            } else {
                rest.add(row);
            }
        }
        JSONArray out = new JSONArray();
        for (int i = 0; i < exact.size(); i++) {
            out.add(exact.get(i));
        }
        for (int i = 0; i < rest.size(); i++) {
            out.add(rest.get(i));
        }
        return out;
    }

    private void fillArticleBodies(List<JSONObject> items, final int joNum, final int joBranch, int maxFetch) {
        List<JSONObject> targets = new ArrayList<JSONObject>();
        for (int i = 0; i < items.size() && targets.size() < maxFetch; i++) {
            JSONObject item = items.get(i);
            if (item.optString("mst", "").length() > 0) {
                targets.add(item);
            }
        }
        if (targets.isEmpty()) {
            return;
        }
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < targets.size(); i++) {
            final JSONObject item = targets.get(i);
            futures.add(LAW_IO_POOL.submit(new Runnable() {
                public void run() {
                    String mst = item.optString("mst", "");
                    String kind = item.optString("kind", "");
                    try {
                        String target = "조례".equals(kind) ? "ordin" : "law";
                        JSONObject bodyJson = fetchBodyJson(target, mst);
                        String text = extractJoText(bodyJson, joNum, joBranch);
                        if (text != null && text.trim().length() > 0) {
                            synchronized (item) {
                                item.put("body", text.trim());
                                String title = item.optString("title");
                                String joLabel = "제" + joNum + "조" + (joBranch > 0 ? ("의" + joBranch) : "");
                                item.put("sub", joLabel + (item.optString("sub").length() > 0
                                        ? (" · " + item.optString("sub")) : ""));
                                if (title.length() > 0 && title.indexOf(joLabel) < 0) {
                                    item.put("title", title + " " + joLabel);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("조문 본문 조회 실패 mst=" + mst + " : " + e.getMessage());
                    }
                }
            }));
        }
        int bodyTimeout = getIntProp("Globals.AiLawGoKrBodyTimeoutMs", 25000);
        for (int i = 0; i < futures.size(); i++) {
            try {
                futures.get(i).get(bodyTimeout + 3000L, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                try { futures.get(i).cancel(true); } catch (Exception ignore) { /* */ }
                logger.warn("조문 본문 대기 타임아웃/실패: " + e.getMessage());
            }
        }
    }

    /**
     * 주제 키워드로 관련 조문을 찾아 본문을 채운다.
     * 예: topic="예산 원칙" → 제34조(예산총계주의의 원칙) 등.
     */
    private void fillArticlesByTopic(List<JSONObject> items, String topic, int maxArticles) {
        if (items == null || items.isEmpty() || topic == null || topic.trim().length() == 0) {
            return;
        }
        List<String> needles = splitTopicNeedles(topic);
        if (needles.isEmpty()) {
            return;
        }
        List<JSONObject> articleItems = new ArrayList<JSONObject>();
        int lawScan = 0;
        for (int i = 0; i < items.size() && lawScan < 2 && articleItems.size() < maxArticles; i++) {
            JSONObject base = items.get(i);
            String mst = base.optString("mst", "");
            if (mst.length() == 0) {
                continue;
            }
            lawScan++;
            String kind = base.optString("kind", "법령");
            String target = "조례".equals(kind) ? "ordin" : "law";
            try {
                JSONObject bodyJson = fetchBodyJson(target, mst);
                List<JSONObject> units = new ArrayList<JSONObject>();
                Object joRoot = findFirstKey(bodyJson, "조문단위");
                if (joRoot != null) {
                    collectJoUnits(joRoot, units);
                }
                if (units.isEmpty()) {
                    collectJoUnits(bodyJson, units);
                }
                List<ScoredJo> scored = new ArrayList<ScoredJo>();
                for (int u = 0; u < units.size(); u++) {
                    JSONObject unit = units.get(u);
                    String yn = firstStr(unit, new String[] { "조문여부" });
                    if ("전문".equals(yn)) {
                        continue;
                    }
                    String no = firstStr(unit, new String[] { "조문번호" }).replaceFirst("^0+(?!$)", "");
                    int joNum = 0;
                    try {
                        joNum = Integer.parseInt(no);
                    } catch (Exception e) {
                        continue;
                    }
                    if (joNum <= 0) {
                        continue;
                    }
                    String title = firstStr(unit, new String[] { "조문제목" });
                    String content = firstStr(unit, new String[] { "조문내용" });
                    String blob = AiKeywordMatcher.normalize(title + " " + content);
                    int score = 0;
                    int hit = 0;
                    for (int n = 0; n < needles.size(); n++) {
                        String nd = needles.get(n);
                        if (nd.length() == 0) {
                            continue;
                        }
                        if (AiKeywordMatcher.normalize(title).indexOf(nd) >= 0) {
                            score += 50;
                            hit++;
                        } else if (blob.indexOf(nd) >= 0) {
                            score += 15;
                            hit++;
                        }
                    }
                    if (hit == 0) {
                        continue;
                    }
                    // 모든 주제어가 제목/내용에 있을수록 가산
                    if (hit >= needles.size()) {
                        score += 30;
                    }
                    scored.add(new ScoredJo(joNum, score, unit, base));
                }
                java.util.Collections.sort(scored, new java.util.Comparator<ScoredJo>() {
                    public int compare(ScoredJo a, ScoredJo b) {
                        if (a.score != b.score) {
                            return b.score - a.score;
                        }
                        return a.joNum - b.joNum;
                    }
                });
                for (int s = 0; s < scored.size() && articleItems.size() < maxArticles; s++) {
                    ScoredJo sj = scored.get(s);
                    String text = formatJoUnit(sj.unit, sj.joNum);
                    if (text == null || text.trim().length() == 0) {
                        continue;
                    }
                    JSONObject art = new JSONObject();
                    art.put("kind", kind);
                    String lawTitle = base.optString("title", "");
                    String joLabel = "제" + sj.joNum + "조";
                    String joTitle = firstStr(sj.unit, new String[] { "조문제목" });
                    art.put("title", lawTitle + " " + joLabel
                            + (joTitle.length() > 0 ? ("(" + joTitle.replaceAll("^\\(|\\)$", "") + ")") : ""));
                    art.put("sub", joLabel + " · 주제: " + topic);
                    art.put("body", text.trim());
                    art.put("url", base.optString("url", ""));
                    art.put("mst", mst);
                    art.put("date", base.optString("date", ""));
                    articleItems.add(art);
                }
            } catch (Exception e) {
                logger.warn("주제 조문 조회 실패 mst=" + mst + " : " + e.getMessage());
            }
        }
        if (!articleItems.isEmpty()) {
            // 관련 조문을 앞에 두고, 원 목록은 뒤로
            List<JSONObject> merged = new ArrayList<JSONObject>();
            merged.addAll(articleItems);
            for (int i = 0; i < items.size(); i++) {
                merged.add(items.get(i));
            }
            items.clear();
            items.addAll(merged);
        }
    }

    private static final class ScoredJo {
        final int joNum;
        final int score;
        final JSONObject unit;
        final JSONObject base;

        ScoredJo(int joNum, int score, JSONObject unit, JSONObject base) {
            this.joNum = joNum;
            this.score = score;
            this.unit = unit;
            this.base = base;
        }
    }

    private List<String> splitTopicNeedles(String topic) {
        List<String> out = new ArrayList<String>();
        if (topic == null) {
            return out;
        }
        String t = topic.replaceAll("[·ㆍ/]", " ").replaceAll("\\s+", " ").trim();
        String[] parts = t.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String n = AiKeywordMatcher.normalize(parts[i]);
            if (n.length() >= 2 && !out.contains(n)) {
                out.add(n);
            }
        }
        // 공백 없는 원주제도 추가
        String whole = AiKeywordMatcher.normalize(topic);
        if (whole.length() >= 4 && !out.contains(whole)) {
            out.add(whole);
        }
        return out;
    }

    private JSONObject fetchBodyJson(String target, String mst) throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(getProp("Globals.AiLawGoKrServiceUrl", SERVICE_BASE));
        url.append("?OC=").append(URLEncoder.encode(getOc(), "UTF-8"));
        url.append("&target=").append(URLEncoder.encode(target, "UTF-8"));
        url.append("&MST=").append(URLEncoder.encode(mst, "UTF-8"));
        url.append("&type=JSON");
        String body = httpGet(url.toString(), getIntProp("Globals.AiLawGoKrBodyTimeoutMs", 25000));
        if (body == null || body.length() == 0) {
            return new JSONObject();
        }
        return JSONObject.fromObject(body);
    }

    /**
     * 법제처 JSON 본문에서 조문번호에 해당하는 텍스트를 추출한다.
     */
    String extractJoText(JSONObject root, int joNum, int joBranch) {
        if (root == null || joNum <= 0) {
            return "";
        }
        List<JSONObject> units = new ArrayList<JSONObject>();
        // 조문단위 경로를 우선 탐색해 전체 JSON 순회를 줄임
        Object joRoot = findFirstKey(root, "조문단위");
        if (joRoot != null) {
            collectJoUnits(joRoot, units);
        }
        if (units.isEmpty()) {
            collectJoUnits(root, units);
        }
        String want = String.valueOf(joNum);
        JSONObject best = null;
        int bestScore = -1;
        for (int i = 0; i < units.size(); i++) {
            JSONObject u = units.get(i);
            String yn = firstStr(u, new String[] { "조문여부" });
            if ("전문".equals(yn)) {
                continue;
            }
            String no = firstStr(u, new String[] { "조문번호" }).replaceFirst("^0+(?!$)", "");
            if (!want.equals(no)) {
                continue;
            }
            String key = firstStr(u, new String[] { "조문키" });
            int score = 0;
            if ("조문".equals(yn)) {
                score += 10;
            }
            if (u.containsKey("항")) {
                score += 20;
            }
            String content = firstStr(u, new String[] { "조문내용" });
            if (content.length() > 30) {
                score += 5;
            }
            // 가지번호
            int keyBranch = 0;
            if (key.length() >= 7) {
                try {
                    keyBranch = Integer.parseInt(key.substring(4, 7));
                } catch (Exception e) {
                    keyBranch = 0;
                }
            }
            String branchStr = firstStr(u, new String[] { "조문가지번호" }).replaceFirst("^0+(?!$)", "");
            int branch = 0;
            try {
                if (branchStr.length() > 0) {
                    branch = Integer.parseInt(branchStr);
                } else {
                    branch = keyBranch;
                }
            } catch (Exception e) {
                branch = keyBranch;
            }
            if (joBranch > 0) {
                if (branch != joBranch) {
                    continue;
                }
                score += 15;
            } else {
                // 본조(가지 없음) 우선. 0022001 형태는 본조로 취급
                if (branch == 0 || branch == 1) {
                    score += 8;
                } else {
                    score -= 5;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = u;
                // 충분히 좋은 매칭이면 조기 종료
                if (score >= 35) {
                    break;
                }
            }
        }
        if (best == null) {
            return "";
        }
        return formatJoUnit(best, joNum);
    }

    private Object findFirstKey(Object node, String key) {
        if (node == null || key == null) {
            return null;
        }
        if (node instanceof JSONObject) {
            JSONObject o = (JSONObject) node;
            if (o.containsKey(key)) {
                return o.get(key);
            }
            Iterator<?> it = o.keys();
            while (it.hasNext()) {
                Object found = findFirstKey(o.get(String.valueOf(it.next())), key);
                if (found != null) {
                    return found;
                }
            }
        } else if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.size(); i++) {
                Object found = findFirstKey(arr.get(i), key);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void collectJoUnits(Object node, List<JSONObject> out) {
        if (node == null || out.size() > 5000) {
            return;
        }
        if (node instanceof JSONObject) {
            JSONObject o = (JSONObject) node;
            if (o.containsKey("조문번호")) {
                out.add(o);
            }
            Iterator<?> it = o.keys();
            while (it.hasNext()) {
                collectJoUnits(o.get(String.valueOf(it.next())), out);
            }
        } else if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.size(); i++) {
                collectJoUnits(arr.get(i), out);
            }
        }
    }

    private String formatJoUnit(JSONObject u, int joNum) {
        StringBuilder sb = new StringBuilder();
        String title = firstStr(u, new String[] { "조문제목" });
        String head = cleanJoText(firstStr(u, new String[] { "조문내용" }));
        sb.append("제").append(joNum).append("조");
        if (title.length() > 0) {
            sb.append("(").append(title.replaceAll("^\\(|\\)$", "")).append(")");
        }
        sb.append("\n");

        StringBuilder hangBuf = new StringBuilder();
        List<JSONObject> hangs = asObjectList(u.get("항"));
        for (int i = 0; i < hangs.size(); i++) {
            JSONObject h = hangs.get(i);
            String hc = cleanJoText(firstStr(h, new String[] { "항내용" }));
            if (hc.length() > 0) {
                hangBuf.append(hc).append("\n");
            }
            List<JSONObject> hos = asObjectList(h.get("호"));
            for (int j = 0; j < hos.size(); j++) {
                JSONObject oi = hos.get(j);
                String oc = cleanJoText(firstStr(oi, new String[] { "호내용" }));
                if (oc.length() > 0) {
                    hangBuf.append("  ").append(oc).append("\n");
                }
                List<JSONObject> moks = asObjectList(oi.get("목"));
                for (int k = 0; k < moks.size(); k++) {
                    String mc = cleanJoText(firstStr(moks.get(k), new String[] { "목내용" }));
                    if (mc.length() > 0) {
                        hangBuf.append("    ").append(mc).append("\n");
                    }
                }
            }
        }

        // 항이 없으면 조문내용 전체 사용. 항이 있어도 머리글(본문)이 항과 다르면 유지
        if (hangs.isEmpty()) {
            if (head.length() > 0) {
                sb.append(head).append("\n");
            }
        } else {
            if (head.length() > 0 && !isJoHeadRedundant(head, hangBuf.toString(), joNum, title)) {
                sb.append(head).append("\n");
            }
            sb.append(hangBuf);
        }

        String ref = firstStr(u, new String[] { "조문참고자료" });
        if (ref.length() > 0) {
            sb.append("\n※ ").append(ref.trim());
        }
        return sb.toString().trim();
    }

    /** 법제처 JSON에서 항/호/목이 단일 객체 또는 배열로 올 수 있음 */
    private List<JSONObject> asObjectList(Object node) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (node instanceof JSONObject) {
            list.add((JSONObject) node);
        } else if (node instanceof JSONArray) {
                JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.size(); i++) {
                Object o = arr.get(i);
                if (o instanceof JSONObject) {
                    list.add((JSONObject) o);
                }
            }
        }
        return list;
    }

    private boolean isJoHeadRedundant(String head, String hangText, int joNum, String title) {
        if (head == null || head.length() == 0) {
            return true;
        }
        String h = head.replaceAll("\\s+", "");
        String t = title == null ? "" : title.replaceAll("\\s+", "");
        if (t.length() > 0 && (h.equals(t) || h.equals("(" + t + ")") || h.indexOf(t) >= 0 && h.length() < t.length() + 12)) {
            return true;
        }
        // "제N조(제목)" 형태만 있는 머리글
        if (h.matches("제" + joNum + "조(\\([^)]*\\))?.*") && h.length() < 40) {
            return true;
        }
        String hangCompact = hangText == null ? "" : hangText.replaceAll("\\s+", "");
        if (hangCompact.length() > 0 && hangCompact.indexOf(h) >= 0) {
            return true;
        }
        // 머리글의 핵심이 이미 ①항에 포함
        if (hangCompact.length() > 40 && h.length() > 40) {
            String core = h.length() > 60 ? h.substring(0, 60) : h;
            if (hangCompact.indexOf(core) >= 0) {
                return true;
            }
        }
        return false;
    }

    private String cleanJoText(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\r", "").replaceAll("[ \\t\\x0B\\f]+", " ").replaceAll("\\n{3,}", "\n\n").trim();
    }

    private void appendLawItems(List<JSONObject> items, JSONArray arr, String kind) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject row = arr.getJSONObject(i);
            JSONObject item = new JSONObject();
            item.put("kind", kind);
            String title = firstStr(row, new String[] { "법령명한글", "법령명", "lawNm" });
            item.put("title", title);
            item.put("sub", firstStr(row, new String[] { "법령구분명", "소관부처명", "제개정구분명" }));
            String mst = firstStr(row, new String[] { "법령일련번호", "MST" });
            item.put("mst", mst);
            item.put("url", buildPublicLawUrl(title, mst));
            item.put("date", firstStr(row, new String[] { "시행일자", "공포일자" }));
            items.add(item);
        }
    }

    private void appendOrdinItems(List<JSONObject> items, JSONArray arr, String kind, boolean metroOnly) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject row = arr.getJSONObject(i);
            String orgNm = firstStr(row, new String[] { "지자체기관명", "지자체구분명" });
            if (metroOnly && !isBusanMetroOnly(orgNm)) {
                continue;
            }
            JSONObject item = new JSONObject();
            item.put("kind", kind);
            String title = firstStr(row, new String[] { "자치법규명", "법령명한글" });
            item.put("title", title);
            item.put("sub", orgNm + (orgNm.length() > 0 ? " · " : "")
                    + firstStr(row, new String[] { "자치법규종류", "제개정구분명" }));
            String mst = firstStr(row, new String[] { "자치법규일련번호", "법령일련번호", "MST" });
            item.put("mst", mst);
            item.put("url", buildPublicOrdinUrl(title, mst));
            item.put("date", firstStr(row, new String[] { "시행일자", "공포일자" }));
            items.add(item);
        }
    }

    /** 부산광역시 본청만 (구·군·교육청 제외) */
    static boolean isBusanMetroOnly(String orgNm) {
        if (orgNm == null) {
            return false;
        }
        String s = orgNm.trim();
        return "부산광역시".equals(s);
    }

    private int countByKind(List<JSONObject> items, String kind) {
        int n = 0;
        for (int i = 0; i < items.size(); i++) {
            if (kind.equals(items.get(i).optString("kind"))) {
                n++;
            }
        }
        return n;
    }

    private JSONArray fetchList(String target, String query, String org, String knd, int display)
            throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(getProp("Globals.AiLawGoKrBaseUrl", DEFAULT_BASE));
        url.append("?OC=").append(URLEncoder.encode(getOc(), "UTF-8"));
        url.append("&target=").append(URLEncoder.encode(target, "UTF-8"));
        url.append("&type=JSON");
        url.append("&query=").append(URLEncoder.encode(query, "UTF-8"));
        url.append("&display=").append(display);
        url.append("&page=1");
        if (org != null && org.length() > 0) {
            url.append("&org=").append(URLEncoder.encode(org, "UTF-8"));
        }
        if (knd != null && knd.length() > 0) {
            url.append("&knd=").append(URLEncoder.encode(knd, "UTF-8"));
        }
        String body = httpGet(url.toString());
        if (body == null || body.length() == 0) {
            return new JSONArray();
        }
        JSONObject root = JSONObject.fromObject(body);
        return extractLawArray(root);
    }

    private JSONArray extractLawArray(JSONObject root) {
        if (root == null) {
            return new JSONArray();
        }
        // LawSearch / OrdinSearch
        Iterator<?> keys = root.keys();
        while (keys.hasNext()) {
            String k = String.valueOf(keys.next());
            Object v = root.get(k);
            if (v instanceof JSONObject) {
                JSONObject inner = (JSONObject) v;
                Object law = inner.get("law");
                if (law instanceof JSONArray) {
                    return (JSONArray) law;
                }
                if (law instanceof JSONObject) {
                    JSONArray one = new JSONArray();
                    one.add(law);
                    return one;
                }
            }
        }
        return new JSONArray();
    }

    /**
     * Open API HTML 본문은 별도 신청이 없으면 '미신청된 목록/본문' 오류가 난다.
     * 국가법령정보센터 공개 본문 화면(OC 불필요)으로 연결한다.
     */
    private String buildPublicLawUrl(String title, String mst) {
        if (mst != null && mst.trim().length() > 0) {
            return "https://www.law.go.kr/LSW/lsInfoP.do?lsiSeq=" + mst.trim();
        }
        if (title != null && title.trim().length() > 0) {
            return "https://www.law.go.kr/법령/" + encodePath(title.trim());
        }
        return "https://www.law.go.kr/";
    }

    private String buildPublicOrdinUrl(String title, String mst) {
        if (title != null && title.trim().length() > 0) {
            return "https://www.law.go.kr/자치법규/" + encodePath(title.trim());
        }
        if (mst != null && mst.trim().length() > 0) {
            return "https://www.law.go.kr/ordinInfoP.do?ordinSeq=" + mst.trim();
        }
        return "https://www.law.go.kr/";
    }

    private String encodePath(String s) {
        try {
            // 경로 세그먼트용: 공백 등만 인코딩
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            return s;
        }
    }

    private String toAbsUrl(String link) {
        if (link == null || link.trim().length() == 0) {
            return "";
        }
        String s = link.trim();
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return s;
        }
        if (!s.startsWith("/")) {
            s = "/" + s;
        }
        return DETAIL_BASE + s;
    }

    private String firstStr(JSONObject row, String[] names) {
        if (row == null || names == null) {
            return "";
        }
        for (int i = 0; i < names.length; i++) {
            if (row.containsKey(names[i])) {
                Object v = row.get(names[i]);
                if (v != null) {
                    String s = String.valueOf(v).trim();
                    if (s.length() > 0 && !"null".equalsIgnoreCase(s)) {
                        return s;
                    }
                }
            }
        }
        return "";
    }

    private String httpGet(String urlStr) throws Exception {
        return httpGet(urlStr, getIntProp("Globals.AiLawGoKrTimeoutMs", 15000));
    }

    /**
     * HttpsURLConnection(JVM 내장 JSSE) 대신 OS의 curl(OpenSSL)로 요청한다.
     * 운영 WAS의 구버전 JDK가 TLS1.2를 지원하지 않아 www.law.go.kr(TLS1.2 필수)과의
     * 핸드셰이크가 항상 실패하기 때문(2026-08-20 확인) — curl은 OS OpenSSL을 쓰므로 무관하다.
     */
    private String httpGet(String urlStr, int timeoutMs) throws Exception {
        int timeoutSec = Math.max(1, (timeoutMs + 999) / 1000);
        List<String> cmd = new ArrayList<String>();
        cmd.add("curl");
        cmd.add("-s");
        cmd.add("-S");
        cmd.add("-L");
        cmd.add("--max-time");
        cmd.add(String.valueOf(timeoutSec));
        cmd.add("-H");
        cmd.add("Accept: application/json");
        cmd.add("-w");
        cmd.add(HTTP_CODE_MARK + "%{http_code}");
        cmd.add(urlStr);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        String out = new String(readAllBytes(proc.getInputStream()), "UTF-8");
        int exitCode = proc.waitFor();

        int mark = out.lastIndexOf(HTTP_CODE_MARK);
        String resp = mark >= 0 ? out.substring(0, mark) : out;
        String codeStr = mark >= 0 ? out.substring(mark + HTTP_CODE_MARK.length()).trim() : "";

        if (exitCode != 0) {
            String detail = out.trim();
            if (detail.length() > 300) {
                detail = detail.substring(0, 300);
            }
            throw new IllegalStateException("curl 실행 실패(exit=" + exitCode + "): " + urlStr
                    + (detail.length() > 0 ? " - " + detail : ""));
        }
        int code = codeStr.length() > 0 ? Integer.parseInt(codeStr) : 0;
        if (code >= 400) {
            logger.warn("법제처 API HTTP " + code + " body=" + resp);
            throw new IllegalStateException("법제처 API 오류 HTTP " + code);
        }
        return resp;
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

    private String getOc() {
        return getProp("Globals.AiLawGoKrOc", "");
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

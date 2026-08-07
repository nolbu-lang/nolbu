package com.cs.bcjis.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * AI 검색 공통: 영문 대소문자·공백 무시 정규화, bigram Dice 유사도.
 * 일반자료검색 기본 유사도 임계값은 {@link #DEFAULT_THRESHOLD} (60%).
 */
public final class AiKeywordMatcher {

    /** 일반자료검색 기본 유사도 (60%) */
    public static final double DEFAULT_THRESHOLD = 0.60;

    private AiKeywordMatcher() {
    }

    /**
     * 공통 검색식.
     * 쉼표(,)로 나눈 묶음은 OR, 묶음 안의 &(AND)는 AND로 처리한다.
     * 연산자가 없는 공백 포함 입력은 여러 단어가 아니라 하나의 문장형 키워드다.
     * 예: "세출예산 절차별 이행사항" / "투자심사&40억원,예비타당성"
     */
    public static final class SearchExpression {
        private final String raw;
        private final List<List<String>> orGroups;

        private SearchExpression(String raw, List<List<String>> orGroups) {
            this.raw = raw == null ? "" : raw;
            this.orGroups = orGroups;
        }

        public String getRaw() {
            return raw;
        }

        public boolean isEmpty() {
            return orGroups.isEmpty();
        }

        public List<List<String>> getOrGroups() {
            return orGroups;
        }

        /** 원격 검색에 사용할 중복 없는 개별 문장형 키워드 목록 */
        public List<String> getTerms() {
            List<String> out = new ArrayList<String>();
            for (int g = 0; g < orGroups.size(); g++) {
                List<String> group = orGroups.get(g);
                for (int t = 0; t < group.size(); t++) {
                    String term = group.get(t);
                    if (!out.contains(term)) {
                        out.add(term);
                    }
                }
            }
            return out;
        }
    }

    public static SearchExpression parseExpression(String query) {
        String raw = query == null ? "" : query.trim();
        List<List<String>> groups = new ArrayList<List<String>>();
        if (raw.length() == 0) {
            return new SearchExpression(raw, groups);
        }
        String[] orParts = raw.split("[,，]");
        for (int i = 0; i < orParts.length; i++) {
            String[] andParts = orParts[i].split("[&＆]");
            List<String> group = new ArrayList<String>();
            for (int j = 0; j < andParts.length; j++) {
                String term = collapseSpaces(andParts[j]);
                if (term.length() > 0) {
                    group.add(term);
                }
            }
            if (!group.isEmpty()) {
                groups.add(group);
            }
        }
        return new SearchExpression(raw, groups);
    }

    /** (AND 묶음) 중 하나라도 모두 60% 이상이면 일치 */
    public static boolean matchesExpression(SearchExpression expr, String text) {
        return bestExpressionSimilarity(expr, text) + 1e-9 >= DEFAULT_THRESHOLD;
    }

    public static boolean matchesExpression(String query, String text) {
        return matchesExpression(parseExpression(query), text);
    }

    /**
     * 검색식 적합도.
     * AND 묶음은 가장 낮은 단어 적합도를 묶음 점수로 사용하고, OR 묶음 중 최고점을 반환한다.
     */
    public static double bestExpressionSimilarity(SearchExpression expr, String text) {
        if (expr == null || expr.isEmpty() || text == null || text.length() == 0) {
            return 0;
        }
        double bestOr = 0;
        List<List<String>> groups = expr.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            double andScore = 1.0;
            for (int t = 0; t < group.size(); t++) {
                double score = bestSimilarity(group.get(t), text);
                if (score < andScore) {
                    andScore = score;
                }
            }
            if (andScore > bestOr) {
                bestOr = andScore;
            }
        }
        return bestOr;
    }

    /** 검색식의 OR 묶음 중 하나가 모든 AND 문구를 원문에 그대로 포함하는지 확인 */
    public static boolean containsExpression(SearchExpression expr, String text) {
        if (expr == null || expr.isEmpty()) {
            return false;
        }
        String nt = normalize(text);
        List<List<String>> groups = expr.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            boolean all = true;
            for (int t = 0; t < group.size(); t++) {
                if (nt.indexOf(normalize(group.get(t))) < 0) {
                    all = false;
                    break;
                }
            }
            if (all) {
                return true;
            }
        }
        return false;
    }

    /**
     * 비교용 정규화: 영문 소문자화, 모든 공백·중점·하이픈 제거.
     */
    public static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String t = s.toLowerCase(Locale.ROOT);
        t = t.replaceAll("[\\s·ㆍ․\\u00A0~\\-–—_/／]+", "");
        return t.trim();
    }

    /** 원격 API 전송용: 공백만 하나로 줄이고 trim (법령명 등). */
    public static String collapseSpaces(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("\\s+", " ").trim();
    }

    /** 원격 게시판 제목검색용: 공백 제거 (예산 편성 → 예산편성). */
    public static String compactQuery(String s) {
        return normalize(s);
    }

    /**
     * 질의어와 대상 텍스트가 임계값 이상 유사한지.
     * - 정규화 후 포함관계면 통과
     * - 짧은 대상(제목)은 전체 Dice
     * - 긴 본문은 질의 길이 기준 슬라이딩 윈도우 최대 Dice
     */
    public static boolean matches(String query, String text, double threshold) {
        return bestSimilarity(query, text) + 1e-9 >= threshold;
    }

    public static boolean matches(String query, String text) {
        return matches(query, text, DEFAULT_THRESHOLD);
    }

    public static double bestSimilarity(String query, String text) {
        String nq = normalize(query);
        String nt = normalize(text);
        if (nq.length() == 0 || nt.length() == 0) {
            return 0;
        }
        if (nt.indexOf(nq) >= 0 || nq.indexOf(nt) >= 0) {
            return 1.0;
        }
        // 제목·짧은 필드
        if (nt.length() <= Math.max(nq.length() * 3, 40)) {
            return similarity(nq, nt);
        }
        // 긴 본문: 질의 길이 ±여유 윈도우
        int win = Math.max(nq.length() + 4, (int) Math.ceil(nq.length() / DEFAULT_THRESHOLD));
        if (win > nt.length()) {
            win = nt.length();
        }
        double best = 0;
        int step = Math.max(1, nq.length() / 2);
        for (int i = 0; i + win <= nt.length(); i += step) {
            double s = similarity(nq, nt.substring(i, i + win));
            if (s > best) {
                best = s;
                if (best >= 0.99) {
                    return best;
                }
            }
        }
        // 마지막 조각
        if (nt.length() > win) {
            double s = similarity(nq, nt.substring(nt.length() - win));
            if (s > best) {
                best = s;
            }
        }
        return best;
    }

    /**
     * 문자 bigram Dice 계수. 입력이 이미 normalize 된 것을 권장.
     */
    public static double similarity(String a, String b) {
        if (a == null || b == null || a.length() == 0 || b.length() == 0) {
            return 0;
        }
        if (a.equals(b)) {
            return 1.0;
        }
        if (a.indexOf(b) >= 0 || b.indexOf(a) >= 0) {
            int mn = Math.min(a.length(), b.length());
            int mx = Math.max(a.length(), b.length());
            return Math.min(0.95, 0.6 + (mn * 0.35 / mx));
        }
        List<String> ba = bigrams(a);
        List<String> bb = bigrams(b);
        if (ba.isEmpty() || bb.isEmpty()) {
            if (a.length() == 1 && b.length() == 1) {
                return a.equals(b) ? 1.0 : 0.0;
            }
            return 0;
        }
        Map<String, Integer> freqB = new HashMap<String, Integer>();
        for (int i = 0; i < bb.size(); i++) {
            String g = bb.get(i);
            Integer c = freqB.get(g);
            freqB.put(g, c == null ? Integer.valueOf(1) : Integer.valueOf(c.intValue() + 1));
        }
        int hit = 0;
        for (int i = 0; i < ba.size(); i++) {
            String g = ba.get(i);
            Integer c = freqB.get(g);
            if (c != null && c.intValue() > 0) {
                hit++;
                freqB.put(g, Integer.valueOf(c.intValue() - 1));
            }
        }
        return (2.0 * hit) / (ba.size() + bb.size());
    }

    private static List<String> bigrams(String s) {
        List<String> out = new ArrayList<String>();
        if (s == null || s.length() < 2) {
            return out;
        }
        for (int i = 0; i < s.length() - 1; i++) {
            out.add(s.substring(i, i + 2));
        }
        return out;
    }
}

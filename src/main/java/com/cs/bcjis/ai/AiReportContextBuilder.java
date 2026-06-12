package com.cs.bcjis.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 심사조서(경상 010 / 투자 020) DB 조회 결과를
 * AI(Gemini)가 오해 없이 이해할 수 있는 '사업 단위 텍스트 뭉치'로 변환하는 유틸리티.
 *
 * 변환 원칙:
 * - 한 사업(통계목 편성 단위)의 정형 데이터(차수·사업명·부서·금액·재원)와
 *   비정형 데이터(요구내용·검토의견·조건검색어)를 하나의 ■ 블록으로 완전하게 묶는다.
 * - 화면의 병합 셀/재원 교차 구조를 풀어서 항목별 라벨을 붙인 일렬 텍스트로 만든다.
 * - 모든 금액은 '백만원' 단위로 통일한다. (DB 원시값은 원 단위)
 * - 국비는 종류(국고보조금·교부세 등)를 통합해 하나의 '국비'로 합산한다.
 */
public class AiReportContextBuilder {

    /** 비정형 텍스트(요구/검토) 기본 최대 길이 */
    private static final int MAX_CONT_LENGTH = 400;

    /** [구분] 필드 최대 길이 (요구내용 원문이 길어 토큰 폭주 방지) */
    private static final int MAX_GUBUN_LENGTH = 300;

    private static final long MILLION = 1000000L;

    /**
     * AI 컨텍스트 생성 옵션 — 질문 유형에 따라 포함 필드를 줄여 속도·비용을 절감한다.
     */
    public static class ContextOptions {
        public boolean includeGubun;
        public boolean includePreYearAmt;
        public boolean includeDemandAmt;
        public boolean includeTags;
        public int maxBlocks = 50;
        public int maxReviewLength = MAX_CONT_LENGTH;

        public static ContextOptions defaults() {
            return new ContextOptions();
        }
    }

    private AiReportContextBuilder() {
    }

    /**
     * 조서 행 목록을 사업 단위 AI 컨텍스트 텍스트로 변환한다.
     *
     * 필요한 컬럼(별칭 기준, 대소문자 무관):
     *   report_nm, fis_year, bgt_dgr, bgt_compo_fg, add_times,
     *   office_nm, dept_nm, pbiz_nm, ubiz_nm, dbiz_nm, fis_fg_nm,
     *   te_mng_mok_cd, te_mng_mok_nm, gubun(구분),
     *   pre_amt, pre_bgt_amt, demand_bgt_amt, bgt_amt, diff_amt   (원 단위)
     *   tot_biz_amt, frsc_gov_amt, frsc_si_amt, frsc_etc_amt (투자조서, 원 단위)
     *   demand_cont, exam_cont, srch_val
     */
    public static String buildReportContext(List<Map<String, Object>> rows) {
        return buildReportContext(rows, "", "", ContextOptions.defaults());
    }

    public static String buildReportContext(List<Map<String, Object>> rows, String queryYear, String queryDgr) {
        return buildReportContext(rows, queryYear, queryDgr, ContextOptions.defaults());
    }

    /**
     * @param queryYear 질문에서 추출한 회계연도
     * @param queryDgr  질문에서 추출한 차수구분
     * @param options   포함 필드·건수 제한 (속도·비용 절감)
     */
    public static String buildReportContext(List<Map<String, Object>> rows, String queryYear, String queryDgr,
            ContextOptions options) {
        if (options == null) {
            options = ContextOptions.defaults();
        }
        StringBuilder sb = new StringBuilder();

        sb.append("[심사조서 데이터] 연도:").append(queryYear.length() > 0 ? queryYear : "-");
        sb.append(" 차수:").append(queryDgr.length() > 0 ? queryDgr : "전체").append("\n");
        sb.append("- ■ 블록 1건 = 사업 1건. 금액 단위 백만원. 재원=국비/시비/기타.\n");
        if (options.includeGubun) {
            sb.append("- [구분] 포함(시행주관·시행주체 관련 질문).\n");
        }
        sb.append("\n");

        if (rows == null || rows.isEmpty()) {
            sb.append("(조건에 해당하는 심사조서 데이터가 없습니다.)\n");
            return sb.toString();
        }

        // [사업명] 기준으로 묶는다. (같은 사업의 차수별 행을 하나의 블록으로 조립)
        LinkedHashMap<String, List<Map<String, Object>>> groups = new LinkedHashMap<String, List<Map<String, Object>>>();
        for (Iterator<Map<String, Object>> it = rows.iterator(); it.hasNext();) {
            Map<String, Object> row = it.next();
            String bizNm = getBizNm(row);
            String key = getStr(row, "report_nm") + "|" + getStr(row, "fis_year")
                    + "|" + getStr(row, "office_nm") + "|" + getStr(row, "dept_nm")
                    + "|" + bizNm + "|" + getStr(row, "te_mng_mok_cd");
            List<Map<String, Object>> group = groups.get(key);
            if (group == null) {
                group = new ArrayList<Map<String, Object>>();
                groups.put(key, group);
            }
            group.add(row);
        }

        int seq = 0;
        for (Iterator<List<Map<String, Object>>> it = groups.values().iterator(); it.hasNext();) {
            if (seq >= options.maxBlocks) {
                sb.append("... (총 ").append(groups.size()).append("건 중 상위 ").append(options.maxBlocks).append("건만 표시)\n");
                break;
            }
            List<Map<String, Object>> group = it.next();
            seq++;
            sb.append(buildOneBizBlock(seq, group, options));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 사업 1건(여러 차수 행 포함 가능)을 하나의 텍스트 블록으로 만든다.
     * 사업 공통 정보(사업명·부서 등)는 한 번만 쓰고, 차수별 내역을 그 아래에 나열한다.
     */
    public static String buildOneBizBlock(int seq, List<Map<String, Object>> group) {
        return buildOneBizBlock(seq, group, ContextOptions.defaults());
    }

    public static String buildOneBizBlock(int seq, List<Map<String, Object>> group, ContextOptions options) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> first = group.get(0);
        String bizLabel = buildBizLabel(first);

        sb.append("■").append(seq).append(" [사업명(통계목)] ").append(bizLabel).append("\n");

        // 소관부서 — 사업당 1회만
        StringBuilder dept = new StringBuilder();
        String officeNm = getStr(first, "office_nm");
        String deptNm = getStr(first, "dept_nm");
        if (officeNm.length() > 0) dept.append(officeNm);
        if (deptNm.length() > 0) {
            if (dept.length() > 0) dept.append(" ");
            dept.append(deptNm);
        }
        sb.append("  [소관부서] ").append(dept.length() > 0 ? dept.toString() : "-").append("\n");

        if (options.includeGubun) {
            String gubun = getStr(first, "gubun");
            if (gubun.length() == 0) gubun = getStr(first, "invest_plan");
            if (gubun.length() > 0) {
                sb.append("  [구분] ").append(truncateTo(gubun, MAX_GUBUN_LENGTH)).append("\n");
            }
        }

        for (int i = 0; i < group.size(); i++) {
            sb.append(buildDgrSection(group.get(i), options));
        }

        return sb.toString();
    }

    /** 사업명(통계목) 표기: 세부사업명 + 통계목코드만 (예: 일상돌봄 서비스사업 ( 308-13)) */
    public static String buildBizLabel(Map<String, Object> row) {
        String dbizNm = getStr(row, "dbiz_nm");
        String mokCd = getStr(row, "te_mng_mok_cd");
        String mokNm = getStr(row, "te_mng_mok_nm");

        StringBuilder sb = new StringBuilder();
        sb.append(dbizNm.length() > 0 ? dbizNm : mokNm);
        String mokCodeLabel = formatMokCode(mokCd);
        if (mokCodeLabel.length() > 0) {
            sb.append(" ( ").append(mokCodeLabel).append(")");
        }
        return sb.toString();
    }

    /** 통계목코드 → 308-13 형식 */
    public static String formatMokCode(String mokCd) {
        if (mokCd == null || mokCd.trim().length() == 0) {
            return "";
        }
        String cd = mokCd.trim();
        if (cd.length() >= 5) {
            return cd.substring(0, 3) + "-" + cd.substring(3);
        }
        return cd;
    }

    /** 사업 블록에 포함된 차수구분 요약 */
    private static String buildGroupDgrSummary(List<Map<String, Object>> group) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < group.size(); i++) {
            Map<String, Object> row = group.get(i);
            String label = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                    getLong(row, "add_times"), getStr(row, "bgt_dgr"));
            if (label.length() == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(label);
        }
        return sb.length() > 0 ? sb.toString() : "미상";
    }

    private static String getBizNm(Map<String, Object> row) {
        String dbizNm = getStr(row, "dbiz_nm");
        return dbizNm.length() > 0 ? dbizNm : getStr(row, "te_mng_mok_nm");
    }

    /** 한 차수의 경량 세트: 예산·검토 (답변 4항목에 필요한 최소 정보만) */
    private static String buildDgrSection(Map<String, Object> row, ContextOptions options) {
        StringBuilder sb = new StringBuilder();

        String dgrLabel = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));

        long demandAmt = getLong(row, "demand_bgt_amt");
        long bgtAmt = getLong(row, "bgt_amt");
        String frscDetail = formatFrscForRow(row);
        String shortDgr = buildShortDgrLabel(getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));

        // [차수별 예산내역] — 답변 서식과 동일: 본예산:547백만원(국비500, 시비47)
        sb.append("  · [차수별 예산내역] ");
        sb.append(shortDgr.length() > 0 ? shortDgr : (dgrLabel.length() > 0 ? dgrLabel : "차수미상"));
        sb.append(":");
        if (options.includePreYearAmt) {
            long preBgtAmt = getLong(row, "pre_bgt_amt");
            long preAmt = getLong(row, "pre_amt");
            long prevYearAmt = preBgtAmt != 0 ? preBgtAmt : preAmt;
            sb.append("전년").append(toMillion(prevYearAmt)).append("백만원,");
        }
        if (options.includeDemandAmt) {
            sb.append("요구").append(toMillion(demandAmt)).append("백만원,");
        }
        sb.append(toMillion(bgtAmt)).append("백만원");
        if (frscDetail.length() > 0) {
            sb.append("(").append(frscDetail).append(")");
        }
        sb.append("\n");

        // [차수별 요구·검토의견] — 핵심만 축약
        String demandCont = truncateTo(getStr(row, "demand_cont"), options.maxReviewLength);
        String examCont = truncateTo(getStr(row, "exam_cont"), options.maxReviewLength);
        if (demandCont.length() > 0 || examCont.length() > 0) {
            sb.append("    검토: ");
            if (demandCont.length() > 0) {
                sb.append("○").append(compactOneLine(demandCont));
            }
            if (examCont.length() > 0) {
                if (demandCont.length() > 0) sb.append(" ");
                sb.append("◈").append(compactOneLine(examCont));
            }
            sb.append("\n");
        }

        if (options.includeTags) {
            String srchVal = getStr(row, "srch_val");
            if (srchVal.length() > 0) {
                sb.append("    태그: ").append(formatTags(srchVal)).append("\n");
            }
        }

        return sb.toString();
    }

    /** 여러 줄 텍스트를 한 줄로 압축 */
    private static String compactOneLine(String s) {
        return s.replaceAll("\\s+", " ").trim();
    }

    private static String truncateTo(String s, int maxLen) {
        if (s.length() <= maxLen) {
            return s;
        }
        return s.substring(0, maxLen) + "...";
    }

    /**
     * 차수 라벨: 예) "2026년 본예산", "2026년 1회추경"
     * bgt_compo_fg '10' = 본예산, '20' = 추경(add_times 회차)
     */
    public static String buildDgrLabel(String fisYear, String bgtCompoFg, long addTimes, String bgtDgr) {
        StringBuilder sb = new StringBuilder();
        if (fisYear.length() > 0) {
            sb.append(fisYear).append("년 ");
        }

        if ("10".equals(bgtCompoFg)) {
            sb.append("본예산");
        } else if ("20".equals(bgtCompoFg)) {
            if (addTimes > 0) {
                sb.append(addTimes).append("회추경");
            } else {
                sb.append("추경");
            }
        } else if (bgtDgr.length() > 0) {
            sb.append(bgtDgr).append("차수");
        }

        if (bgtDgr.length() > 0) {
            sb.append("(예산차수 ").append(bgtDgr).append(")");
        }
        return sb.toString();
    }

    /** 원 단위 금액 → 백만원 문자열(콤마 포함, 반올림) */
    public static String toMillion(long won) {
        long m = Math.round(won / (double) MILLION);
        return addComma(m);
    }

    /** 원 단위 금액 → 부호 포함 백만원 문자열 (증감액용) */
    public static String toMillionSigned(long won) {
        long m = Math.round(won / (double) MILLION);
        if (m > 0) {
            return "+" + addComma(m);
        }
        return addComma(m);
    }

    private static String addComma(long v) {
        return String.format("%,d", v);
    }

    /** 차수 라벨(연도 제외): 본예산, 1회추경 등 */
    public static String buildShortDgrLabel(String bgtCompoFg, long addTimes, String bgtDgr) {
        if ("10".equals(bgtCompoFg)) {
            return "본예산";
        }
        if ("20".equals(bgtCompoFg)) {
            if (addTimes > 0) {
                return addTimes + "회추경";
            }
            return "추경";
        }
        if (bgtDgr != null && bgtDgr.trim().length() > 0) {
            return bgtDgr.trim() + "차수";
        }
        return "";
    }

    /** 행 단위 재원 표기 (frsc_detail 우선, 없으면 투자조서 합산 컬럼 사용) */
    public static String formatFrscForRow(Map<String, Object> row) {
        String detail = formatFrscDetail(getStr(row, "frsc_detail"));
        if (detail.length() > 0) {
            return detail;
        }
        long gov = getLong(row, "frsc_gov_amt");
        long si = getLong(row, "frsc_si_amt");
        long etc = getLong(row, "frsc_etc_amt");
        return formatFrscAmounts(gov, si, etc);
    }

    /**
     * 재원별 편성액 상세 문자열을 국비/시비/기타 백만원 합산으로 정리한다.
     *
     * 입력 형식: "재원명:금액원|재원명:금액원" (예: "국고보조금:100000000|자체수입:209000000")
     * 출력 예시: "국비500, 시비47"
     *
     * 분류 규칙: 재원명 첫 글자 '국'/'균' → 국비(국비 종류 통합),
     *            '자'/'시' → 시비, 그 외 → 기타
     */
    public static String formatFrscDetail(String detail) {
        if (detail == null || detail.trim().length() == 0) {
            return "";
        }

        long gov = 0L;
        long si = 0L;
        long etc = 0L;

        String[] items = detail.split("\\|");
        for (int i = 0; i < items.length; i++) {
            String item = items[i].trim();
            int colon = item.lastIndexOf(':');
            if (colon < 1) {
                continue;
            }
            String name = item.substring(0, colon).trim();
            long amt;
            try {
                amt = (long) Double.parseDouble(item.substring(colon + 1).trim().replaceAll(",", ""));
            } catch (NumberFormatException e) {
                continue;
            }
            if (amt == 0) {
                continue;
            }

            char first = name.length() > 0 ? name.charAt(0) : ' ';
            if (first == '국' || first == '균') {
                gov += amt;
            } else if (first == '자' || first == '시') {
                si += amt;
            } else {
                etc += amt;
            }
        }

        return formatFrscAmounts(gov, si, etc);
    }

    /** 국비/시비/기타 금액을 답변 서식으로 조합 (예: 국비500, 시비47) */
    public static String formatFrscAmounts(long gov, long si, long etc) {
        StringBuilder sb = new StringBuilder();
        if (gov != 0) {
            sb.append("국비").append(toMillion(gov));
        }
        if (si != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("시비").append(toMillion(si));
        }
        if (etc != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("기타").append(toMillion(etc));
        }
        return sb.toString();
    }

    /** srch_val 을 #태그 형태로 정리. 예) "민생,실국협의" -> "#민생 #실국협의" */
    public static String formatTags(String srchVal) {
        String[] tokens = srchVal.split("[,;\\s/]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            String t = tokens[i].trim();
            if (t.length() == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(" ");
            }
            if (!t.startsWith("#")) {
                sb.append("#");
            }
            sb.append(t);
        }
        return sb.toString();
    }

    private static String truncate(String s) {
        return truncateTo(s, MAX_CONT_LENGTH);
    }

    /** 여러 줄 텍스트를 블록 내부 들여쓰기로 정리 */
    private static String indent(String s) {
        String[] lines = s.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append("    ").append(lines[i].trim());
        }
        return sb.toString();
    }

    /** Map 컬럼값을 대소문자 무관하게 문자열로 꺼낸다. */
    public static String getStr(Map<String, Object> row, String key) {
        Object v = getValue(row, key);
        if (v == null) {
            return "";
        }
        String s = String.valueOf(v).trim();
        return "null".equals(s) ? "" : s;
    }

    /** Map 컬럼값을 대소문자 무관하게 long 으로 꺼낸다. */
    public static long getLong(Map<String, Object> row, String key) {
        Object v = getValue(row, key);
        if (v == null) {
            return 0L;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        try {
            String s = String.valueOf(v).trim().replaceAll(",", "");
            if (s.length() == 0) {
                return 0L;
            }
            return (long) Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static Object getValue(Map<String, Object> row, String key) {
        if (row.containsKey(key)) {
            return row.get(key);
        }
        String upper = key.toUpperCase();
        if (row.containsKey(upper)) {
            return row.get(upper);
        }
        String lower = key.toLowerCase();
        if (row.containsKey(lower)) {
            return row.get(lower);
        }
        // 컬럼 별칭이 camelCase 등으로 변형된 경우 대비
        for (Iterator<String> it = row.keySet().iterator(); it.hasNext();) {
            String k = it.next();
            if (k != null && k.equalsIgnoreCase(key)) {
                return row.get(k);
            }
        }
        return null;
    }
}

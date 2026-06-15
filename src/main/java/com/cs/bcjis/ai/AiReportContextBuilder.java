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
        /** 여러 연도 범위 질문 시 답변 블록 앞에 회계연도 표시 */
        public boolean multiYearRangeQuery;
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
     *   office_nm, dept_nm, pbiz_nm, ubiz_nm, dbiz_nm, comp_ground(세세사업명), fis_fg_nm,
     *   te_mng_mok_cd, te_mng_mok_nm, gubun(구분),
     *   pre_amt, pre_bgt_amt, demand_bgt_amt, bgt_amt, diff_amt   (원 단위)
     *   frsc_amt1~6, frsc_detail (연간 조정예산 재원, 원 단위)
     *   tot_biz_amt (투자조서 총사업비 합계, 원 단위)
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
        sb.append("- ■ 블록 1건 = 사업 1건. 금액 단위 백만원.\n");
        sb.append("- [차수별 예산내역] 서식: 본예산:200백만원(국비140, 시비60). 예산액=조정액 총합(bgt_amt), 괄호=조정액 재원(ADJ_DEF_FRSC_AMT).\n");
        if (options.includeGubun) {
            sb.append("- [구분] 포함(시행주관·시행주체 관련 질문).\n");
        }
        sb.append("\n");

        if (rows == null || rows.isEmpty()) {
            sb.append("(조건에 해당하는 심사조서 데이터가 없습니다.)\n");
            return sb.toString();
        }

        // [사업명] 기준으로 묶는다. (같은 사업의 차수별 행을 하나의 블록으로 조립)
        LinkedHashMap<String, List<Map<String, Object>>> groups = groupRowsByBiz(rows);

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

    /** 사업명(통계목) 표기: 세세사업명(comp_ground) + 통계목코드 (예: 지역사랑상품권 인센티브 보상금 ( 308-13)) */
    public static String buildBizLabel(Map<String, Object> row) {
        String compGround = getStr(row, "comp_ground");
        String dbizNm = getStr(row, "dbiz_nm");
        String mokCd = getStr(row, "te_mng_mok_cd");
        String mokNm = getStr(row, "te_mng_mok_nm");

        StringBuilder sb = new StringBuilder();
        if (compGround.length() > 0) {
            sb.append(compGround);
        } else if (dbizNm.length() > 0) {
            sb.append(dbizNm);
        } else {
            sb.append(mokNm);
        }
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

    private static String getBizNm(Map<String, Object> row) {
        String compGround = getStr(row, "comp_ground");
        if (compGround.length() > 0) {
            return compGround;
        }
        String dbizNm = getStr(row, "dbiz_nm");
        return dbizNm.length() > 0 ? dbizNm : getStr(row, "te_mng_mok_nm");
    }

    /** 한 차수의 경량 세트: 예산·검토 (답변 4항목에 필요한 최소 정보만) */
    private static String buildDgrSection(Map<String, Object> row, ContextOptions options) {
        StringBuilder sb = new StringBuilder();

        String dgrLabel = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));

        long demandAmt = getLong(row, "demand_bgt_amt");
        String frscDetail = formatFrscForRow(row);
        String shortDgr = buildShortDgrLabel(getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));

        // [차수별 예산내역] — 예) 본예산:50,000백만원(시비50,000)
        sb.append("  · [차수별 예산내역] ");
        String budgetLine = formatBudgetDgrDisplay(row, options);
        if (options.includePreYearAmt || options.includeDemandAmt) {
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
            sb.append(toMillion(getLong(row, "bgt_amt"))).append("백만원");
            if (frscDetail.length() > 0) {
                sb.append("(").append(frscDetail).append(")");
            }
        } else {
            sb.append(budgetLine);
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

    /**
     * TB_DGRCOMPOFRSC 조회 결과를 행에 반영한다.
     * frsc_detail = 재원별 ADJ_DEF_FRSC_AMT 합(심사조서 FRSCES 와 동일 출처).
     */
    public static void applyAdjFrscFromDb(Map<String, Object> row, Map<String, Object> fr) {
        if (row == null || fr == null) {
            return;
        }
        row.put("frsc_amt1", fr.get("frsc_amt1"));
        row.put("frsc_amt2", fr.get("frsc_amt2"));
        row.put("frsc_amt3", fr.get("frsc_amt3"));
        row.put("frsc_amt4", fr.get("frsc_amt4"));
        row.put("frsc_amt5", fr.get("frsc_amt5"));
        row.put("frsc_amt6", fr.get("frsc_amt6"));
        row.put("frsc_detail", fr.get("frsc_detail"));
    }

    /**
     * 조정액 재원 표기 — TB_DGRCOMPOFRSC.ADJ_DEF_FRSC_AMT 만 사용한다.
     * (요구액 DMN_DEF_FRSC_AMT, 총사업비 tot_frsc_amt 는 사용하지 않음)
     */
    public static String formatFrscForRow(Map<String, Object> row) {
        String detail = getStr(row, "frsc_detail");
        if (detail.length() > 0) {
            String fromDetail = formatFrscDetail(detail);
            if (fromDetail.length() > 0) {
                return fromDetail;
            }
        }

        return formatFrscAmountsFromAdjSlots(row);
    }

    /** frsc_amt1~6(ADJ_DEF_FRSC_AMT 슬롯 합) → 국비/시비/기타 표기 */
    public static String formatFrscAmountsFromAdjSlots(Map<String, Object> row) {
        long si;
        long gov;
        long etc;
        if (isInvestReport(row)) {
            si = getLong(row, "frsc_amt1");
            gov = getLong(row, "frsc_amt2");
            etc = getLong(row, "frsc_amt3") + getLong(row, "frsc_amt4")
                    + getLong(row, "frsc_amt5") + getLong(row, "frsc_amt6");
        } else {
            si = getLong(row, "frsc_amt1");
            gov = getLong(row, "frsc_amt2") + getLong(row, "frsc_amt3") + getLong(row, "frsc_amt4");
            etc = getLong(row, "frsc_amt5") + getLong(row, "frsc_amt6");
        }
        return formatFrscAmounts(gov, si, etc);
    }

    /** 투자사업심사조서(020) 여부 */
    public static boolean isInvestReport(Map<String, Object> row) {
        String reportNm = getStr(row, "report_nm");
        return reportNm.indexOf("투자") >= 0;
    }

    /**
     * 차수별 예산 한 줄.
     * 예산액 = 조정액 총합(bgt_amt), 괄호 = 조정액 재원구성(ADJ_DEF_FRSC_AMT).
     * 예) 본예산:200백만원(국비140, 시비60)
     */
    public static String formatBudgetDgrDisplay(Map<String, Object> row, ContextOptions options) {
        String shortDgr = buildShortDgrLabel(getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));
        if (shortDgr.length() == 0) {
            shortDgr = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                    getLong(row, "add_times"), getStr(row, "bgt_dgr"));
        }
        long bgtAmt = getLong(row, "bgt_amt");
        String frsc = formatFrscForRow(row);
        StringBuilder sb = new StringBuilder();
        sb.append(shortDgr).append(":").append(toMillion(bgtAmt)).append("백만원");
        if (frsc.length() > 0) {
            sb.append("(").append(frsc).append(")");
        }
        return sb.toString();
    }

    /** 사업별 차수 예산을 한 줄로 — 예) 본예산:50,000백만원(시비50,000) / 1회추경:100,000백만원(국비50,000, 시비50,000) */
    public static String buildCombinedBudgetLine(List<Map<String, Object>> group, ContextOptions options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < group.size(); i++) {
            if (i > 0) {
                sb.append(" / ");
            }
            sb.append(formatBudgetDgrDisplay(group.get(i), options));
        }
        return sb.toString();
    }

    /** 사업명 기준 그룹 (buildReportContext · assembleReportAnswer 공통) */
    public static LinkedHashMap<String, List<Map<String, Object>>> groupRowsByBiz(List<Map<String, Object>> rows) {
        LinkedHashMap<String, List<Map<String, Object>>> groups = new LinkedHashMap<String, List<Map<String, Object>>>();
        if (rows == null) {
            return groups;
        }
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
        return groups;
    }

    /** 사업(블록) 수 상한 — 연도 병합 등으로 행이 많아져도 답변·표 건수 제한 */
    public static List<Map<String, Object>> trimRowsToMaxBizGroups(List<Map<String, Object>> rows, int maxBlocks) {
        if (rows == null || rows.isEmpty() || maxBlocks <= 0) {
            return rows;
        }
        LinkedHashMap<String, List<Map<String, Object>>> groups = groupRowsByBiz(rows);
        if (groups.size() <= maxBlocks) {
            return rows;
        }
        List<Map<String, Object>> trimmed = new ArrayList<Map<String, Object>>();
        int count = 0;
        for (Iterator<List<Map<String, Object>>> it = groups.values().iterator(); it.hasNext();) {
            if (count >= maxBlocks) {
                break;
            }
            trimmed.addAll(it.next());
            count++;
        }
        return trimmed;
    }

    /**
     * LLM 답변의 1~3번 항목을 DB 기준으로 재조립한다. (재원 괄호 누락 방지)
     * 4번 검토의견은 LLM 요약을 우선 사용하고, 없으면 DB 원문을 축약한다.
     */
    public static String assembleReportAnswer(List<Map<String, Object>> rows, String llmAnswer,
            ContextOptions options) {
        if (rows == null || rows.isEmpty()) {
            return llmAnswer;
        }
        if (options == null) {
            options = ContextOptions.defaults();
        }

        LinkedHashMap<String, List<Map<String, Object>>> groups = groupRowsByBiz(rows);
        List<String> llmBlocks = splitLlmBizBlocks(llmAnswer);
        int totalGroups = groups.size();
        int maxBlocks = options.maxBlocks > 0 ? options.maxBlocks : totalGroups;

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (Iterator<List<Map<String, Object>>> it = groups.values().iterator(); it.hasNext();) {
            if (idx >= maxBlocks) {
                sb.append("\n\n... (총 ").append(totalGroups).append("건 중 상위 ")
                        .append(maxBlocks).append("건만 표시)");
                break;
            }
            List<Map<String, Object>> group = it.next();
            Map<String, Object> first = group.get(0);
            if (idx > 0) {
                sb.append("\n\n");
            }
            String llmBlock = idx < llmBlocks.size() ? llmBlocks.get(idx) : "";

            if (options.multiYearRangeQuery) {
                String fisYear = getStr(first, "fis_year");
                if (fisYear.length() == 4) {
                    sb.append("[").append(fisYear).append("년]\n");
                }
            }

            sb.append("1. [사업명(통계목)] : ").append(buildBizLabel(first)).append("\n");
            sb.append("2. [소관부서] ").append(formatDeptLine(first)).append("\n");

            if (options.includeGubun) {
                String gubun = getStr(first, "gubun");
                if (gubun.length() == 0) {
                    gubun = getStr(first, "invest_plan");
                }
                if (gubun.length() > 0) {
                    sb.append("   [구분] ").append(truncateTo(gubun, MAX_GUBUN_LENGTH)).append("\n");
                }
            }

            sb.append("3. [차수별 예산내역(재원표시)] : ")
                    .append(buildCombinedBudgetLine(group, options)).append("\n");

            String review;
            if (group.size() > 1) {
                review = buildReviewSectionFromDb(group, options);
                sb.append("4. [차수별 요구, 검토의견] :\n").append(review);
            } else {
                review = extractReviewSectionFromLlm(llmBlock);
                if (review.length() == 0) {
                    review = buildReviewSectionFromDb(group, options);
                }
                sb.append("4. [차수별 요구, 검토의견] : ").append(review);
            }

            idx++;
        }
        return sb.toString().trim();
    }

    public static String formatDeptLine(Map<String, Object> row) {
        StringBuilder dept = new StringBuilder();
        String officeNm = getStr(row, "office_nm");
        String deptNm = getStr(row, "dept_nm");
        if (officeNm.length() > 0) {
            dept.append(officeNm);
        }
        if (deptNm.length() > 0) {
            if (dept.length() > 0) {
                dept.append(" ");
            }
            dept.append(deptNm);
        }
        return dept.length() > 0 ? dept.toString() : "-";
    }

    private static List<String> splitLlmBizBlocks(String llmAnswer) {
        List<String> blocks = new ArrayList<String>();
        if (llmAnswer == null || llmAnswer.trim().length() == 0) {
            return blocks;
        }
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "(?=(?:^|\\n)\\s*1\\.\\s*\\[사업명)", java.util.regex.Pattern.MULTILINE);
        java.util.regex.Matcher m = p.matcher(llmAnswer);
        List<Integer> starts = new ArrayList<Integer>();
        while (m.find()) {
            starts.add(m.start());
        }
        if (starts.isEmpty()) {
            blocks.add(llmAnswer.trim());
            return blocks;
        }
        for (int i = 0; i < starts.size(); i++) {
            int start = starts.get(i);
            int end = i + 1 < starts.size() ? starts.get(i + 1) : llmAnswer.length();
            blocks.add(llmAnswer.substring(start, end).trim());
        }
        return blocks;
    }

    private static String extractReviewSectionFromLlm(String llmBlock) {
        if (llmBlock == null || llmBlock.length() == 0) {
            return "";
        }
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "4\\.\\s*\\[차수별 요구[^\\]]*\\]\\s*[:：]\\s*([\\s\\S]*)",
                java.util.regex.Pattern.MULTILINE);
        java.util.regex.Matcher m = p.matcher(llmBlock);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }

    private static String buildReviewSectionFromDb(List<Map<String, Object>> group, ContextOptions options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < group.size(); i++) {
            Map<String, Object> row = group.get(i);
            if (i > 0) {
                sb.append("\n");
            }
            String shortDgr = buildShortDgrLabel(getStr(row, "bgt_compo_fg"),
                    getLong(row, "add_times"), getStr(row, "bgt_dgr"));
            if (shortDgr.length() == 0) {
                shortDgr = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                        getLong(row, "add_times"), getStr(row, "bgt_dgr"));
            }
            if (shortDgr.length() > 0) {
                sb.append("[").append(shortDgr).append("] ");
            }
            String demandCont = truncateTo(getStr(row, "demand_cont"), options.maxReviewLength);
            String examCont = truncateTo(getStr(row, "exam_cont"), options.maxReviewLength);
            if (demandCont.length() > 0) {
                sb.append("○").append(compactOneLine(demandCont));
            }
            if (examCont.length() > 0) {
                if (demandCont.length() > 0) {
                    sb.append(" ");
                }
                sb.append("◈").append(compactOneLine(examCont));
            }
        }
        return sb.length() > 0 ? sb.toString() : "-";
    }

    /** frsc_detail 문자열 → [국비, 시비, 기타] 원 단위 배열 */
    private static long[] parseFrscDetailBuckets(String detail) {
        long gov = 0L;
        long si = 0L;
        long etc = 0L;

        if (detail == null || detail.trim().length() == 0) {
            return new long[] { gov, si, etc };
        }

        String[] items = detail.split("\\|");
        for (int i = 0; i < items.length; i++) {
            String item = items[i].trim();
            if (item.length() == 0) {
                continue;
            }
            String[] parts = item.split(":");
            long amt = 0L;
            String standCd = "";
            String name = "";

            if (parts.length >= 3) {
                standCd = parts[0].trim();
                name = parts[1].trim();
                amt = parseFrscAmount(parts[2]);
            } else if (parts.length == 2) {
                name = parts[0].trim();
                amt = parseFrscAmount(parts[1]);
            } else {
                continue;
            }
            if (amt == 0) {
                continue;
            }

            int bucket;
            if (standCd.length() > 0) {
                bucket = frscBucketByStandCd(standCd, name);
            } else {
                bucket = frscBucketByName(name);
            }
            if (bucket == 0) {
                gov += amt;
            } else if (bucket == 1) {
                si += amt;
            } else {
                etc += amt;
            }
        }
        return new long[] { gov, si, etc };
    }

    /**
     * 재원별 편성액 상세 문자열을 국비/시비/기타 백만원 합산으로 정리한다.
     *
     * 입력 형식:
     * - "STAND_CD:재원명:금액원" (권장)
     * - "재원명:금액원" (구형 호환)
     * 출력 예시: "국비500, 시비47"
     */
    public static String formatFrscDetail(String detail) {
        long[] buckets = parseFrscDetailBuckets(detail);
        return formatFrscAmounts(buckets[0], buckets[1], buckets[2]);
    }

    private static long parseFrscAmount(String raw) {
        if (raw == null) {
            return 0L;
        }
        try {
            return (long) Double.parseDouble(raw.trim().replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /** 0=국비, 1=시비, 2=기타 — 심사조서 FRSC_AMT1~6 슬롯 표기(Report010/020 SaveFile) */
    private static int frscBucketByStandCd(String standCd, String name) {
        if ("160".equals(standCd)) {
            return 1;
        }
        if ("110".equals(standCd) || "120".equals(standCd) || "130".equals(standCd)) {
            return 0;
        }
        if ("180".equals(standCd) || "190".equals(standCd) || "170".equals(standCd)
                || "200".equals(standCd) || "210".equals(standCd)
                || "140".equals(standCd) || "150".equals(standCd)) {
            return 2;
        }
        return frscBucketByName(name);
    }

    /** 재원명 기반 분류(구형 호환) */
    private static int frscBucketByName(String name) {
        if (name == null || name.length() == 0) {
            return 2;
        }
        if (name.indexOf("국") > -1 || name.indexOf("균") > -1 || name.indexOf("교부") > -1) {
            return 0;
        }
        if (name.indexOf("시") > -1 || name.indexOf("자체") > -1 || name.indexOf("자") == 0) {
            return 1;
        }
        return 2;
    }

    /** 국비/시비/기타 금액을 답변 서식으로 조합 (예: 국비500, 시비47) */
    public static String formatFrscAmounts(long gov, long si, long etc) {
        StringBuilder sb = new StringBuilder();
        if (gov != 0) {
            sb.append("국비 ").append(toMillion(gov));
        }
        if (si != 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("시비 ").append(toMillion(si));
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

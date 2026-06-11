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

    /** 비정형 텍스트(요구내용/검토의견) 1건당 최대 길이 (프롬프트 폭주 방지) */
    private static final int MAX_CONT_LENGTH = 1500;

    private static final long MILLION = 1000000L;

    private AiReportContextBuilder() {
    }

    /**
     * 조서 행 목록(사업 1건 = 1 row)을 AI 컨텍스트 텍스트로 변환한다.
     *
     * 필요한 컬럼(별칭 기준, 대소문자 무관):
     *   report_nm, fis_year, bgt_dgr, bgt_compo_fg, add_times,
     *   office_nm, dept_nm, pbiz_nm, ubiz_nm, dbiz_nm, fis_fg_nm,
     *   te_mng_mok_cd, te_mng_mok_nm,
     *   pre_amt, demand_bgt_amt, bgt_amt, diff_amt   (원 단위)
     *   tot_biz_amt, frsc_gov_amt, frsc_si_amt, frsc_etc_amt (투자조서, 원 단위)
     *   demand_cont, exam_cont, srch_val
     */
    public static String buildReportContext(List<Map<String, Object>> rows) {
        StringBuilder sb = new StringBuilder();

        sb.append("[심사조서 데이터 안내]\n");
        sb.append("- 아래 ■ 블록 하나가 '사업 1건'의 완전한 심사조서 맥락입니다. 블록 간 내용을 섞지 마세요.\n");
        sb.append("- 같은 사업이 여러 차수(본예산·추경)에 반영된 경우, 하나의 ■ 블록 안에 [차수] 단위로 묶어 두었습니다.\n");
        sb.append("- 모든 금액 단위는 '백만원'으로 통일했습니다(원 단위에서 반올림).\n");
        sb.append("- 재원의 구분: '국비'는 국고보조금·교부세 등 국가 재원을 통합한 값, '시비'는 자체 재원, '기타'는 지방채·채무 등입니다.\n");
        sb.append("- 차수의 구별: '본예산', 'N회추경'으로 표기합니다. 질문에 차수 구분이 없으면 해당 연도의 모든 차수가 포함되어 있습니다.\n");
        sb.append("- '사업명'은 세부사업명을 기본으로 하고, 통계목·세세사업 명칭도 함께 표기합니다.\n");
        sb.append("\n");

        if (rows == null || rows.isEmpty()) {
            sb.append("(조건에 해당하는 심사조서 데이터가 없습니다.)\n");
            return sb.toString();
        }

        // 같은 사업(연도+조서+부서+사업명+통계목)을 하나로 묶는다. (차수만 다른 행들을 그룹화)
        LinkedHashMap<String, List<Map<String, Object>>> groups = new LinkedHashMap<String, List<Map<String, Object>>>();
        for (Iterator<Map<String, Object>> it = rows.iterator(); it.hasNext();) {
            Map<String, Object> row = it.next();
            String key = getStr(row, "report_nm") + "|" + getStr(row, "fis_year")
                    + "|" + getStr(row, "office_nm") + "|" + getStr(row, "dept_nm")
                    + "|" + getStr(row, "dbiz_nm") + "|" + getStr(row, "te_mng_mok_cd")
                    + "|" + getStr(row, "te_mng_mok_nm");
            List<Map<String, Object>> group = groups.get(key);
            if (group == null) {
                group = new ArrayList<Map<String, Object>>();
                groups.put(key, group);
            }
            group.add(row);
        }

        int seq = 0;
        for (Iterator<List<Map<String, Object>>> it = groups.values().iterator(); it.hasNext();) {
            List<Map<String, Object>> group = it.next();
            seq++;
            sb.append(buildOneBizBlock(seq, group));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 사업 1건(여러 차수 행 포함 가능)을 하나의 텍스트 블록으로 만든다.
     * 사업 공통 정보(사업명·부서 등)는 한 번만 쓰고, 차수별 내역을 그 아래에 나열한다.
     */
    public static String buildOneBizBlock(int seq, List<Map<String, Object>> group) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> first = group.get(0);

        String reportNm = getStr(first, "report_nm");
        String officeNm = getStr(first, "office_nm");
        String deptNm = getStr(first, "dept_nm");
        String pbizNm = getStr(first, "pbiz_nm");
        String ubizNm = getStr(first, "ubiz_nm");
        String dbizNm = getStr(first, "dbiz_nm");
        String fisFgNm = getStr(first, "fis_fg_nm");
        String mokCd = getStr(first, "te_mng_mok_cd");
        String mokNm = getStr(first, "te_mng_mok_nm");

        sb.append("■ 사업 ").append(seq).append("\n");

        if (reportNm.length() > 0) {
            sb.append("- 조서구분: ").append(reportNm).append("\n");
        }

        // 사업명 (세부사업/통계목/세세사업 계층을 함께 제공)
        sb.append("- 사업명: ").append(dbizNm.length() > 0 ? dbizNm : mokNm);
        StringBuilder bizDetail = new StringBuilder();
        if (pbizNm.length() > 0) {
            bizDetail.append("정책사업: ").append(pbizNm);
        }
        if (ubizNm.length() > 0) {
            if (bizDetail.length() > 0) bizDetail.append(" / ");
            bizDetail.append("단위사업: ").append(ubizNm);
        }
        if (mokNm.length() > 0) {
            if (bizDetail.length() > 0) bizDetail.append(" / ");
            bizDetail.append("통계목: ");
            if (mokCd.length() >= 5) {
                bizDetail.append(mokCd.substring(0, 3)).append("-").append(mokCd.substring(3)).append(" ");
            }
            bizDetail.append(mokNm);
        }
        if (bizDetail.length() > 0) {
            sb.append(" (").append(bizDetail).append(")");
        }
        sb.append("\n");

        // 소관부서
        StringBuilder dept = new StringBuilder();
        if (officeNm.length() > 0) {
            dept.append(officeNm);
        }
        if (deptNm.length() > 0) {
            if (dept.length() > 0) dept.append(" ");
            dept.append(deptNm);
        }
        sb.append("- 소관부서: ").append(dept.length() > 0 ? dept.toString() : "(미상)").append("\n");

        if (fisFgNm.length() > 0) {
            sb.append("- 회계구분: ").append(fisFgNm).append("\n");
        }

        // 차수별 내역 (같은 사업이 본예산·추경 등 여러 차수에 걸치면 모두 나열)
        sb.append("- 차수별 내역").append(group.size() > 1 ? " (총 " + group.size() + "개 차수)" : "").append(":\n");
        for (int i = 0; i < group.size(); i++) {
            sb.append(buildDgrSection(group.get(i)));
        }

        return sb.toString();
    }

    /** 한 차수의 예산·재원·검토의견 내역 */
    private static String buildDgrSection(Map<String, Object> row) {
        StringBuilder sb = new StringBuilder();

        String dgrLabel = buildDgrLabel(getStr(row, "fis_year"), getStr(row, "bgt_compo_fg"),
                getLong(row, "add_times"), getStr(row, "bgt_dgr"));

        sb.append("  [").append(dgrLabel).append("]\n");

        // 예산액 — 단위: 백만원
        long preAmt = getLong(row, "pre_amt");
        long demandAmt = getLong(row, "demand_bgt_amt");
        long bgtAmt = getLong(row, "bgt_amt");
        long diffAmt = getLong(row, "diff_amt");

        sb.append("  · 예산액(단위: 백만원): ");
        sb.append("기정액 ").append(toMillion(preAmt));
        sb.append(", 예산요구액 ").append(toMillion(demandAmt));
        sb.append(", 조정액(반영액) ").append(toMillion(bgtAmt));
        sb.append(", 증감액 ").append(toMillionSigned(diffAmt));
        sb.append("\n");

        // 반영액의 재원구성 (국비/시비/기타 - 원 단위 상세를 백만원으로 합산)
        String frscDetail = formatFrscDetail(getStr(row, "frsc_detail"));
        if (frscDetail.length() > 0) {
            sb.append("  · 반영액 재원구성(단위: 백만원): ").append(frscDetail).append("\n");
        }

        // 투자조서: 총사업비 + 재원의 구분 (국비 통합 / 시비 / 기타)
        long totBiz = getLong(row, "tot_biz_amt");
        long frscGov = getLong(row, "frsc_gov_amt");
        long frscSi = getLong(row, "frsc_si_amt");
        long frscEtc = getLong(row, "frsc_etc_amt");
        if (totBiz != 0 || frscGov != 0 || frscSi != 0 || frscEtc != 0) {
            sb.append("  · 총사업비(단위: 백만원): 합계 ").append(toMillion(totBiz));
            sb.append(" [국비(국비 종류 통합) ").append(toMillion(frscGov));
            sb.append(", 시비 ").append(toMillion(frscSi));
            sb.append(", 기타(지방채·채무 등) ").append(toMillion(frscEtc));
            sb.append("]\n");
        }

        // 검토의견 및 추진상황 (비정형 텍스트 — 원문 유지)
        String demandCont = truncate(getStr(row, "demand_cont"));
        String examCont = truncate(getStr(row, "exam_cont"));
        if (demandCont.length() > 0 || examCont.length() > 0) {
            sb.append("  · 검토의견 및 추진상황:\n");
            if (demandCont.length() > 0) {
                sb.append("    ○ 요구내용:\n");
                sb.append(indent(demandCont)).append("\n");
            }
            if (examCont.length() > 0) {
                sb.append("    ◈ 검토의견:\n");
                sb.append(indent(examCont)).append("\n");
            }
        }

        // 조건검색어 (#태그)
        String srchVal = getStr(row, "srch_val");
        if (srchVal.length() > 0) {
            sb.append("  · 조건검색어: ").append(formatTags(srchVal)).append("\n");
        }

        return sb.toString();
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

    /**
     * 재원별 편성액 상세 문자열을 국비/시비/기타 백만원 합산으로 정리한다.
     *
     * 입력 형식: "재원명:금액원|재원명:금액원" (예: "국고보조금:100000000|자체수입:209000000")
     * 출력 예시: "국비 100, 시비 209"
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

        StringBuilder sb = new StringBuilder();
        if (gov != 0) {
            sb.append("국비 ").append(toMillion(gov));
        }
        if (si != 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("시비 ").append(toMillion(si));
        }
        if (etc != 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("기타 ").append(toMillion(etc));
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
        if (s.length() <= MAX_CONT_LENGTH) {
            return s;
        }
        return s.substring(0, MAX_CONT_LENGTH) + " ...(이하 생략)";
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

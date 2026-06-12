package com.cs.bcjis.ai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.cs.bcjis.ai.AiReportContextBuilder;
import com.cs.bcjis.ai.AiSchemaProvider;
import com.cs.bcjis.ai.LlmClient;
import com.cs.bcjis.ai.service.AiChatService;

/**
 * AI 챗봇 서비스 구현.
 *
 * 처리 흐름:
 * 1) 질문 분류 (Gemini) : report(심사조서) / sql(일반 통계·목록) / chat(DB 불필요)
 * 2-A) report : 경상(010)·투자(020) 심사조서를 사업 단위로 조회하여
 *               AiReportContextBuilder 로 'AI 맞춤형 텍스트 뭉치'를 만들고 RAG 방식으로 답변
 * 2-B) sql    : Gemini 가 생성한 SELECT 를 검증 후 실행하고 결과를 요약
 *
 * 내부 심사정보시스템 CUBRID 데이터만 사용하며, 데이터 변경(INSERT/UPDATE/DELETE 등)은 허용하지 않는다.
 */
@Service("aiChatService")
public class AiChatServiceImpl implements AiChatService {

    private static final Logger logger = Logger.getLogger(AiChatServiceImpl.class);

    @Resource(name = "bcjis.dataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "aiLlmClient")
    private LlmClient llmClient;

    @Resource(name = "aiSchemaProvider")
    private AiSchemaProvider aiSchemaProvider;

    /**
     * 답변 생성용 시스템 지침(system_instruction).
     * 예산 심사관 페르소나와 보고서 출력 서식을 모든 답변에 강제한다.
     * (질문 분류/SQL 생성 단계에는 적용하지 않는다 - JSON 출력이 필요하므로)
     */
    private static final String SYSTEM_PERSONA =
            "너는 부산시 예산담당관실 예산편성 전문 AI 도우미다. 반드시 간결한 보고서 서식으로만 답하라.\n"
            + "\n"
            + "[기본 답변 서식 - 사업 1건마다 아래 4항목만 작성. 그 외 항목 절대 추가 금지]\n"
            + "1. [사업명(통계목)] : 사업명 뒤 통계목코드만 괄호 표기. 예) 일상돌봄 서비스사업 ( 308-13)\n"
            + "   - '통계목:' 문구·통계목 명칭 전체는 절대 표시하지 말 것.\n"
            + "2. [소관부서]\n"
            + "3. [차수별 예산내역(재원표시)] : 차수별 조정액+재원별 금액 필수. 예) 본예산:547백만원(국비500, 시비47) / 1회추경:50백만원(시비50)\n"
            + "   - (국비,시비)처럼 재원명만 쓰지 말고 반드시 재원별 백만원 금액을 괄호 안에 표기.\n"
            + "4. [차수별 요구, 검토의견] : 차수별 ○요구 ◈검토 핵심 1~2문장만\n"
            + "\n"
            + "[명시적 요청이 있을 때만 3번에 추가 표시]\n"
            + "- '요구액' 요청 → [차수별 예산내역]에 요구액 포함 (예: 본예산:요구350,547백만원(국비500, 시비47))\n"
            + "- '전년도예산' 요청 → [차수별 예산내역]에 전년도예산 포함\n"
            + "- '시행주관/시행주체' 요청 → [구분] 항목을 2번과 3번 사이에 추가\n"
            + "\n"
            + "[절대 표시하지 말 것 - 명시 요청 없는 경우]\n"
            + "조서구분, 총사업비, 조건검색어, 전년도예산, 요구액, 종합의견, 총평, 해설, 부가 설명\n"
            + "\n"
            + "[공통 규칙]\n"
            + "- 같은 사업의 여러 차수는 사업 1건으로 묶어 3·4번 안에서 차수별 구분.\n"
            + "- 검색된 해당 사업은 건수와 관계없이 모두 동일한 4항목 서식으로 빠짐없이 표시한다. 일부만 상세·나머지 생략하지 말 것.\n"
            + "- 금액은 '백만원' 단위. 데이터 없으면 없다고 안내.\n"
            + "- 제공 데이터에 없는 내용 지어내지 말 것.";

    /** 시행주관 관련 질문어 — [구분] 필드(요구내용) 검색에 사용 */
    private static final String[] IMPL_ORG_KEYWORDS = {
            "시행주관", "시행주체", "시행처", "시행기관", "사업기관", "사업자",
            "시행 주관", "시행 주체", "시행 기관"
    };

    /** 질문에서 시행주관 기관명 추출 패턴 (예: '테크노파크가 시행주관인') */
    private static final Pattern[] IMPL_EXTRACT_PATTERNS = {
            Pattern.compile("[\"'「]([^\"'」]+)[\"'」]"),
            Pattern.compile("([\\uAC00-\\uD7A3A-Za-z0-9()（）\\-\\.]+?)\\s*(?:가|이|을|를)\\s*시행(?:주관|주체|처|기관)"),
            Pattern.compile("시행(?:주관|주체|처|기관)(?:\\s*(?:인|이))?\\s*([\\uAC00-\\uD7A3A-Za-z0-9()（）\\-\\.]+?)(?:\\s*(?:인|임|인\\s*사업|사업|을|를|에서|중|으로))"),
            Pattern.compile("([\\uAC00-\\uD7A3A-Za-z0-9()（）\\-\\.]+?)\\s*시행(?:주관|주체|처|기관)")
    };

    private static final String[] FORBIDDEN_KEYWORDS = {
            "insert", "update", "delete", "drop", "alter", "create",
            "truncate", "grant", "revoke", "merge", "call", "exec",
            "execute", "rename", "replace", "commit", "rollback"
    };

    /** "N회추경" / "추경 N회" 형태에서 회차 추출 */
    private static final Pattern ADD_TIMES_PATTERN = Pattern.compile("(\\d+)\\s*회");

    private int getMaxRows() {
        return getIntProp("Globals.GeminiMaxRows", 200);
    }

    /** 심사조서 RAG 시 컨텍스트로 넘길 최대 사업(블록) 수 */
    private int getMaxReportBlocks() {
        int v = getIntProp("Globals.AiMaxReportBlocks", -1);
        if (v > 0) {
            return v;
        }
        return getIntProp("Globals.GeminiMaxReportBlocks", 50);
    }

    private int getIntProp(String key, int defaultValue) {
        try {
            String v = config.getProperty(key);
            if (v != null && v.trim().length() > 0) {
                return Integer.parseInt(v.trim());
            }
        } catch (Exception e) {
            // ignore
        }
        return defaultValue;
    }

    public JSONObject ask(String question) throws Exception {
        JSONObject result = new JSONObject();

        if (question == null || question.trim().length() == 0) {
            result.put("answer", "질문을 입력해 주세요.");
            return result;
        }

        if (!llmClient.isEnabled()) {
            result.put("answer", "AI 챗봇 API가 설정되지 않았습니다.\n"
                    + "globals.properties 에 Globals.AiProvider 와 API 키를 등록하세요.\n"
                    + "  · 테스트: AiProvider=gemini, Globals.GeminiApiKey\n"
                    + "  · 운영: AiProvider=hyperclova, Globals.ClovaApiKey");
            return result;
        }

        result.put("aiProvider", llmClient.getProviderName());

        // 1) 질문 분류 및 계획 수립 (JSON — system_instruction 미사용)
        String planRaw = llmClient.generate(buildPlanPrompt(question));
        JSONObject plan = parseJsonFromModel(planRaw);

        String mode = plan.optString("mode", "");

        if ("report".equalsIgnoreCase(mode)) {
            return handleReportQuestion(question, plan, result);
        }

        if ("chat".equalsIgnoreCase(mode) || !plan.optBoolean("needData", true)) {
            String directAnswer = plan.optString("answer", "");
            result.put("answer", directAnswer.length() > 0 ? directAnswer
                    : "죄송합니다. 이 질문은 심사정보시스템 데이터로 답변하기 어렵습니다.");
            return result;
        }

        return handleSqlQuestion(question, plan, result);
    }

    // ------------------------------------------------------------------
    // 심사조서(경상 010 / 투자 020) RAG 경로
    // ------------------------------------------------------------------

    private JSONObject handleReportQuestion(String question, JSONObject plan, JSONObject result) throws Exception {
        String fisYear = plan.optString("fisYear", "").trim();
        String dgr = plan.optString("dgr", "").trim();
        String reportCd = plan.optString("reportCd", "").trim();
        String bizKeyword = plan.optString("bizKeyword", "").trim();
        String deptKeyword = plan.optString("deptKeyword", "").trim();
        String tagKeyword = plan.optString("tagKeyword", "").trim();
        String implKeyword = plan.optString("implKeyword", "").trim();
        if (implKeyword.length() == 0) {
            implKeyword = extractImplKeyword(question);
        }
        // 시행주관 질문인데 Gemini가 기관명을 bizKeyword로 넣은 경우 → [구분] 검색으로 전환
        if (implKeyword.length() == 0 && containsImplOrgQuestion(question) && bizKeyword.length() > 0) {
            implKeyword = bizKeyword;
            bizKeyword = "";
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setMaxRows(getMaxReportBlocks());

        // 회계연도 미지정 시 최신 연도
        if (fisYear.length() == 0) {
            try {
                Object maxYear = jdbcTemplate.queryForObject("SELECT MAX(fis_year) FROM TB_BGTDGR", String.class);
                fisYear = maxYear == null ? "" : String.valueOf(maxYear);
            } catch (Exception e) {
                logger.error("최신 회계연도 조회 실패", e);
            }
        }

        // 차수 해석: 본예산 / N회추경 / 전체
        String bgtCompoFg = "";
        int addTimes = -1;
        if (dgr.indexOf("본예산") > -1) {
            bgtCompoFg = "10";
        } else if (dgr.indexOf("추경") > -1) {
            bgtCompoFg = "20";
            Matcher m = ADD_TIMES_PATTERN.matcher(dgr);
            if (m.find()) {
                addTimes = Integer.parseInt(m.group(1));
            }
        }

        // 1차 조회: 사업명/부서/태그 조건
        List<Object> args = new ArrayList<Object>();
        String sql = buildReportSql(reportCd, fisYear, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword, false, args);
        List<Map<String, Object>> rows = queryReport(jdbcTemplate, sql, args);

        // 2차 조회(완화): 사업명 키워드를 검토의견·검색태그까지 확장
        if (rows.isEmpty() && bizKeyword.length() > 0) {
            args = new ArrayList<Object>();
            sql = buildReportSql(reportCd, fisYear, bgtCompoFg, addTimes,
                    bizKeyword, deptKeyword, tagKeyword, implKeyword, true, args);
            rows = queryReport(jdbcTemplate, sql, args);
        }

        // 3차 조회: 시행주관 질문 — [구분](요구내용)에서 기관명 재검색
        if (rows.isEmpty() && containsImplOrgQuestion(question)) {
            String retryImpl = implKeyword.length() > 0 ? implKeyword : extractImplKeyword(question);
            if (retryImpl.length() > 0) {
                args = new ArrayList<Object>();
                sql = buildReportSql(reportCd, fisYear, bgtCompoFg, addTimes,
                        "", deptKeyword, tagKeyword, retryImpl, true, args);
                rows = queryReport(jdbcTemplate, sql, args);
            }
        }

        // 사업 단위 경량 컨텍스트 생성 (질문 유형에 맞게 포함 필드·건수 제한)
        AiReportContextBuilder.ContextOptions ctxOpts = buildContextOptions(question, tagKeyword);
        ctxOpts.maxBlocks = getMaxReportBlocks();
        String context = AiReportContextBuilder.buildReportContext(rows, fisYear, dgr, ctxOpts);

        logger.info("AI RAG[report] provider=" + llmClient.getProviderName()
                + " year=" + fisYear + " rows=" + rows.size()
                + " contextChars=" + context.length());

        // RAG 답변 생성 (예산 심사관 페르소나 + 보고서 서식 강제)
        String answer = llmClient.generate(SYSTEM_PERSONA, buildReportAnswerPrompt(question, context));
        if (answer == null || answer.trim().length() == 0) {
            answer = rows.isEmpty()
                    ? "조건에 해당하는 심사조서 데이터를 찾지 못했습니다. 연도·차수·사업명을 바꾸어 다시 질문해 주세요."
                    : "총 " + rows.size() + "건의 심사조서를 찾았습니다. 아래 표를 확인해 주세요.";
        }

        result.put("answer", answer.trim());
        result.put("rowCount", rows.size());

        // 화면 표 출력용 요약 목록
        JSONArray columns = new JSONArray();
        columns.add("사업명");
        columns.add("소관부서");
        columns.add("차수");
        columns.add("요구액(백만원)");
        columns.add("조정액(백만원)");

        JSONArray dataList = new JSONArray();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            String bizNm = AiReportContextBuilder.getStr(row, "dbiz_nm");
            if (bizNm.length() == 0) {
                bizNm = AiReportContextBuilder.getStr(row, "te_mng_mok_nm");
            }
            obj.put("사업명", bizNm);
            obj.put("소관부서", (AiReportContextBuilder.getStr(row, "office_nm") + " " + AiReportContextBuilder.getStr(row, "dept_nm")).trim());
            obj.put("차수", AiReportContextBuilder.buildDgrLabel(
                    AiReportContextBuilder.getStr(row, "fis_year"),
                    AiReportContextBuilder.getStr(row, "bgt_compo_fg"),
                    AiReportContextBuilder.getLong(row, "add_times"),
                    AiReportContextBuilder.getStr(row, "bgt_dgr")));
            obj.put("요구액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "demand_bgt_amt")));
            obj.put("조정액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "bgt_amt")));
            dataList.add(obj);
        }
        result.put("columns", columns);
        result.put("dataList", dataList);

        return result;
    }

    private List<Map<String, Object>> queryReport(JdbcTemplate jdbcTemplate, String sql, List<Object> args) {
        try {
            return jdbcTemplate.queryForList(sql, args.toArray());
        } catch (Exception e) {
            logger.error("심사조서 RAG 조회 오류. sql=" + sql, e);
            return new ArrayList<Map<String, Object>>();
        }
    }

    /**
     * 심사조서 사업 단위 조회 SQL 생성.
     *
     * - 조서 1건 = TB_REPORT010/020 의 (report_cd, report_detl_cd, te_bgt_compo_id)
     * - 금액은 TB_DGRCOMPO 의 통계목 헤더 행(COMPO_LEVEL='1')을 사업 단위로 합산 (원 단위)
     * - 투자조서(020) 재원: tot_frsc_amt1=시비, 2=국비, 3=교부세, 4=지방채, 5=채무, 6=기타
     *   → 국비(2+3 통합) / 시비(1) / 기타(4+5+6) 로 정리
     *
     * @param broadKeyword true 면 사업명 키워드를 검토의견/검색태그까지 확장(OR)하여 재검색
     */
    private String buildReportSql(String reportCd, String fisYear, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            boolean broadKeyword, List<Object> args) {

        boolean both = !"010".equals(reportCd) && !"020".equals(reportCd);

        StringBuilder sb = new StringBuilder();

        // 재원별 편성액 상세(GROUP_CONCAT)는 비용이 크므로 필터링·LIMIT 이 끝난
        // 최종 결과(최대 N건)에 대해서만 계산한다. (속도 개선)
        sb.append("SELECT X.*\n");
        sb.append("     , NVL((SELECT GROUP_CONCAT(ZZ.FRSC SEPARATOR '|')\n");
        sb.append("              FROM (SELECT MAX(Z2.FRSC_FG_NM) || ':' || TO_CHAR(NVL(SUM(Z1.PRE_DEF_FRSC_AMT), 0) + NVL(SUM(Z1.ADJ_DEF_FRSC_AMT), 0)) AS FRSC\n");
        sb.append("                         , NVL(SUM(Z1.PRE_DEF_FRSC_AMT), 0) + NVL(SUM(Z1.ADJ_DEF_FRSC_AMT), 0) AS FRSC_AMT\n");
        sb.append("                      FROM TB_DGRCOMPOFRSC Z1\n");
        sb.append("                         , TB_YEARFRSC Z2\n");
        sb.append("                     WHERE Z2.FIS_YEAR = Z1.FIS_YEAR\n");
        sb.append("                       AND Z2.FRSC_FG_CD = Z1.FRSC_FG_CD\n");
        sb.append("                       AND Z1.FIS_YEAR = X.fis_year\n");
        sb.append("                       AND Z1.BGT_DGR = X.bgt_dgr\n");
        sb.append("                       AND Z1.TE_BGT_COMPO_ID = X.te_bgt_compo_id\n");
        sb.append("                     GROUP BY Z2.FRSC_FG_CD) ZZ\n");
        sb.append("             WHERE ZZ.FRSC_AMT <> 0), '') AS frsc_detail\n");
        sb.append("  FROM (\n");
        sb.append("SELECT * FROM (\n");
        if (both || "010".equals(reportCd)) {
            appendReportBranch(sb, "010", fisYear, bgtCompoFg, addTimes, args);
        }
        if (both) {
            sb.append("UNION ALL\n");
        }
        if (both || "020".equals(reportCd)) {
            appendReportBranch(sb, "020", fisYear, bgtCompoFg, addTimes, args);
        }
        sb.append(") W\n");
        sb.append("WHERE 1=1\n");

        // 행정운영경비(정책사업)의 기본경비·인력운영비 사업은 심사 대상 정보가 아니므로 제외
        sb.append("  AND NOT (W.pbiz_nm LIKE '%행정운영경비%'\n");
        sb.append("           AND (W.ubiz_nm LIKE '%기본경비%' OR W.ubiz_nm LIKE '%인력운영비%'\n");
        sb.append("                OR W.dbiz_nm LIKE '%기본경비%' OR W.dbiz_nm LIKE '%인력운영비%'))\n");

        // 사업명 키워드: 쉼표(,)로 여러 개를 받아 OR 검색 (예: "유가보조금,유류비,연료비")
        if (bizKeyword.length() > 0) {
            String[] keywords = bizKeyword.split("[,;]");
            StringBuilder kwSql = new StringBuilder();
            for (int i = 0; i < keywords.length; i++) {
                String kw = keywords[i].trim();
                if (kw.length() == 0) {
                    continue;
                }
                String like = "%" + kw + "%";
                if (kwSql.length() > 0) {
                    kwSql.append(" OR ");
                }
                if (broadKeyword) {
                    kwSql.append("W.dbiz_nm LIKE ? OR W.te_mng_mok_nm LIKE ? OR W.ubiz_nm LIKE ? OR W.pbiz_nm LIKE ?");
                    kwSql.append(" OR W.srch_val LIKE ? OR W.demand_cont LIKE ? OR W.exam_cont LIKE ?");
                    args.add(like); args.add(like); args.add(like); args.add(like);
                    args.add(like); args.add(like); args.add(like);
                } else {
                    kwSql.append("W.dbiz_nm LIKE ? OR W.te_mng_mok_nm LIKE ? OR W.ubiz_nm LIKE ? OR W.pbiz_nm LIKE ?");
                    args.add(like); args.add(like); args.add(like); args.add(like);
                }
            }
            if (kwSql.length() > 0) {
                sb.append("  AND (").append(kwSql).append(")\n");
            }
        }
        if (deptKeyword.length() > 0) {
            String like = "%" + deptKeyword + "%";
            sb.append("  AND (W.dept_nm LIKE ? OR W.office_nm LIKE ?)\n");
            args.add(like); args.add(like);
        }
        if (tagKeyword.length() > 0) {
            sb.append("  AND W.srch_val LIKE ?\n");
            args.add("%" + tagKeyword.replaceAll("#", "") + "%");
        }
        // 시행주관·시행주체·시행처 등 → [구분] 필드(요구내용·검토내용) 검색
        if (implKeyword.length() > 0) {
            String[] implKws = implKeyword.split("[,;]");
            StringBuilder implSql = new StringBuilder();
            for (int i = 0; i < implKws.length; i++) {
                String kw = implKws[i].trim();
                if (kw.length() == 0) {
                    continue;
                }
                String like = "%" + kw + "%";
                if (implSql.length() > 0) {
                    implSql.append(" OR ");
                }
                // gubun = demand_cont(심사조서 [구분] 목록의 요구내용)
                implSql.append("W.gubun LIKE ? OR W.demand_cont LIKE ? OR W.exam_cont LIKE ? OR W.invest_plan LIKE ?");
                args.add(like);
                args.add(like);
                args.add(like);
                args.add(like);
            }
            if (implSql.length() > 0) {
                sb.append("  AND (").append(implSql).append(")\n");
            }
        }

        // 같은 사업의 차수별 행이 인접하도록 사업 기준으로 정렬 (차수는 마지막)
        sb.append("ORDER BY W.office_nm, W.dept_nm, W.dbiz_nm, W.te_mng_mok_cd, W.bgt_dgr\n");
        sb.append("LIMIT ").append(getMaxReportBlocks()).append("\n");
        sb.append(") X");

        return sb.toString();
    }

    /** 조서구분(010/020)별 단일 SELECT 분기 생성 */
    private void appendReportBranch(StringBuilder sb, String reportCd, String fisYear,
            String bgtCompoFg, int addTimes, List<Object> args) {

        boolean invest = "020".equals(reportCd);
        String table = invest ? "TB_REPORT020" : "TB_REPORT010";
        String reportNm = invest ? "투자사업심사조서" : "경상사업심사조서";

        sb.append("SELECT '").append(reportNm).append("' AS report_nm\n");
        sb.append("     , R.fis_year AS fis_year\n");
        sb.append("     , R.bgt_dgr AS bgt_dgr\n");
        sb.append("     , R.te_bgt_compo_id AS te_bgt_compo_id\n");
        sb.append("     , G.bgt_compo_fg AS bgt_compo_fg\n");
        sb.append("     , NVL(G.add_times, 0) AS add_times\n");
        sb.append("     , NVL(D.office_nm, '') AS office_nm\n");
        sb.append("     , NVL(D.dept_nm, '') AS dept_nm\n");
        sb.append("     , NVL(B.pbiz_nm, '') AS pbiz_nm\n");
        sb.append("     , NVL(B.ubiz_nm, '') AS ubiz_nm\n");
        sb.append("     , NVL(B.dbiz_nm, '') AS dbiz_nm\n");
        sb.append("     , NVL(B.fis_fg_nm, '') AS fis_fg_nm\n");
        sb.append("     , C.te_mng_mok_cd AS te_mng_mok_cd\n");
        sb.append("     , C.te_mng_mok_nm AS te_mng_mok_nm\n");
        sb.append("     , C.pre_amt AS pre_amt\n");
        sb.append("     , C.pre_bgt_amt AS pre_bgt_amt\n");
        sb.append("     , C.demand_bgt_amt AS demand_bgt_amt\n");
        sb.append("     , C.bgt_amt AS bgt_amt\n");
        sb.append("     , C.diff_amt AS diff_amt\n");
        sb.append("     , NVL(R.demand_cont, '') AS gubun\n");
        sb.append("     , NVL(R.invest_plan, '') AS invest_plan\n");
        sb.append("     , NVL(R.demand_cont, '') AS demand_cont\n");
        sb.append("     , NVL(R.exam_cont, '') AS exam_cont\n");
        sb.append("     , NVL(R.srch_val, '') AS srch_val\n");
        if (invest) {
            sb.append("     , NVL(R.tot_frsc_amt1,0)+NVL(R.tot_frsc_amt2,0)+NVL(R.tot_frsc_amt3,0)+NVL(R.tot_frsc_amt4,0)+NVL(R.tot_frsc_amt5,0)+NVL(R.tot_frsc_amt6,0) AS tot_biz_amt\n");
            sb.append("     , NVL(R.tot_frsc_amt2,0)+NVL(R.tot_frsc_amt3,0) AS frsc_gov_amt\n");
            sb.append("     , NVL(R.tot_frsc_amt1,0) AS frsc_si_amt\n");
            sb.append("     , NVL(R.tot_frsc_amt4,0)+NVL(R.tot_frsc_amt5,0)+NVL(R.tot_frsc_amt6,0) AS frsc_etc_amt\n");
        } else {
            sb.append("     , 0 AS tot_biz_amt, 0 AS frsc_gov_amt, 0 AS frsc_si_amt, 0 AS frsc_etc_amt\n");
        }
        sb.append("  FROM ").append(table).append(" R\n");
        sb.append("     , (SELECT fis_year, bgt_dgr, te_bgt_compo_id\n");
        sb.append("             , MIN(dept_cd) AS dept_cd\n");
        sb.append("             , MIN(dbiz_cd) AS dbiz_cd\n");
        sb.append("             , MIN(te_mng_mok_cd) AS te_mng_mok_cd\n");
        sb.append("             , MIN(te_mng_mok_nm) AS te_mng_mok_nm\n");
        sb.append("             , SUM(NVL(pre_amt, 0)) AS pre_amt\n");
        sb.append("             , SUM(NVL(pre_bgt_amt, 0)) AS pre_bgt_amt\n");
        sb.append("             , SUM(NVL(demand_bgt_amt, 0)) AS demand_bgt_amt\n");
        sb.append("             , SUM(NVL(bgt_amt, 0)) AS bgt_amt\n");
        sb.append("             , SUM(NVL(diff_amt, 0)) AS diff_amt\n");
        sb.append("          FROM TB_DGRCOMPO\n");
        sb.append("         WHERE compo_level = '1'\n");
        sb.append("           AND fis_year = ?\n");
        sb.append("           AND (cng_type IS NULL OR cng_type = 'CH02' OR (cng_type = 'CH01' AND grp_lvl <> '2'))\n");
        sb.append("         GROUP BY fis_year, bgt_dgr, te_bgt_compo_id) C\n");
        // 부서명·사업명은 스칼라 서브쿼리 대신 집계 조인으로 조회 (속도 개선)
        sb.append("     , TB_BGTDGR G\n");
        sb.append("     , (SELECT fis_year, bgt_dgr, dept_cd, MIN(office_nm) AS office_nm, MIN(dept_nm) AS dept_nm\n");
        sb.append("          FROM TB_DGRDEPT\n");
        sb.append("         WHERE fis_year = ?\n");
        sb.append("         GROUP BY fis_year, bgt_dgr, dept_cd) D\n");
        sb.append("     , (SELECT fis_year, bgt_dgr, dbiz_cd, MIN(pbiz_nm) AS pbiz_nm, MIN(ubiz_nm) AS ubiz_nm\n");
        sb.append("             , MIN(dbiz_nm) AS dbiz_nm, MIN(fis_fg_nm) AS fis_fg_nm\n");
        sb.append("          FROM TB_DGRBIZ\n");
        sb.append("         WHERE fis_year = ?\n");
        sb.append("         GROUP BY fis_year, bgt_dgr, dbiz_cd) B\n");
        sb.append(" WHERE C.fis_year = R.fis_year\n");
        sb.append("   AND C.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND C.te_bgt_compo_id = R.te_bgt_compo_id\n");
        sb.append("   AND G.fis_year = R.fis_year\n");
        sb.append("   AND G.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND D.fis_year = R.fis_year\n");
        sb.append("   AND D.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND D.dept_cd = C.dept_cd\n");
        sb.append("   AND B.fis_year = R.fis_year\n");
        sb.append("   AND B.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND B.dbiz_cd = C.dbiz_cd\n");
        sb.append("   AND R.report_cd = '").append(reportCd).append("'\n");
        sb.append("   AND R.fis_year = ?\n");
        // '?' 등장 순서: C(fis_year) -> D(fis_year) -> B(fis_year) -> R(fis_year)
        args.add(fisYear);
        args.add(fisYear);
        args.add(fisYear);
        args.add(fisYear);

        if ("10".equals(bgtCompoFg)) {
            sb.append("   AND G.bgt_compo_fg = '10'\n");
        } else if ("20".equals(bgtCompoFg)) {
            sb.append("   AND G.bgt_compo_fg = '20'\n");
            if (addTimes > 0) {
                sb.append("   AND G.add_times = ?\n");
                args.add(Integer.valueOf(addTimes));
            }
        }
        sb.append("\n");
    }

    /** 심사조서 RAG 최종 답변 프롬프트 (페르소나·서식은 system_instruction 으로 별도 고정) */
    private String buildReportAnswerPrompt(String question, String context) {
        StringBuilder sb = new StringBuilder();
        sb.append("아래 [심사조서 데이터]만 근거로 [사용자 질문]에 답하라.\n");
        sb.append("\n[답변 규칙]\n");
        sb.append("- 기본: [사업명(통계목)]→[소관부서]→[차수별 예산내역(재원표시)]→[차수별 요구, 검토의견] 4항목만.\n");
        sb.append("- 1번: 사업명 ( 통계목코드) 형식. '통계목:'·통계목 명칭 표시 금지. 예) 일상돌봄 서비스사업 ( 308-13)\n");
        sb.append("- 3번: 본예산:547백만원(국비500, 시비47) 형식. 재원명만 나열 금지, 괄호 안에 재원별 금액 필수.\n");
        sb.append("- 3번 기본은 조정액(반영액)+재원금액. 요구액·전년도예산은 사용자가 명시했을 때만 3번에 추가.\n");
        sb.append("- 검토의견은 차수당 1~2문장 핵심 요약. 원문 전체 복사 금지.\n");
        sb.append("- 제공된 데이터의 모든 사업을 동일 4항목 서식으로 빠짐없이 표시. 생략·요약 목록으로 대체하지 말 것.\n");

        String explicitHint = buildExplicitFieldHint(question);
        if (explicitHint.length() > 0) {
            sb.append("- ").append(explicitHint).append("\n");
        }
        if (containsImplOrgQuestion(question)) {
            sb.append("- 시행주관 관련 질문이므로 [구분]을 2번과 3번 사이에 간략히 추가.\n");
        } else {
            sb.append("- [구분][조건검색어][총사업비][전년도예산][요구액] 등은 표시하지 말 것.\n");
        }

        sb.append("\n[사용자 질문]\n").append(question).append("\n");
        sb.append("\n[심사조서 데이터]\n").append(context).append("\n");
        return sb.toString();
    }

    /** 질문 유형에 따른 AI 컨텍스트 포함 옵션 (토큰·비용 절감) */
    private AiReportContextBuilder.ContextOptions buildContextOptions(String question, String tagKeyword) {
        AiReportContextBuilder.ContextOptions opts = AiReportContextBuilder.ContextOptions.defaults();
        opts.includeGubun = containsImplOrgQuestion(question);
        opts.includePreYearAmt = wantsPreYearAmt(question);
        opts.includeDemandAmt = wantsDemandAmt(question);
        opts.includeTags = tagKeyword != null && tagKeyword.trim().length() > 0;
        opts.maxReviewLength = 300;
        return opts;
    }

    private boolean wantsDemandAmt(String question) {
        if (question == null) return false;
        return question.indexOf("요구액") > -1;
    }

    private boolean wantsPreYearAmt(String question) {
        if (question == null) return false;
        return question.indexOf("전년도") > -1 || question.indexOf("전년") > -1
                || question.indexOf("기정액") > -1;
    }

    /** 질문에서 시행주관 기관명 추출 (구분/요구내용 필드 검색용) */
    private String extractImplKeyword(String question) {
        if (question == null) {
            return "";
        }
        if (!containsImplOrgQuestion(question)) {
            return "";
        }
        String q = question.trim();
        for (int i = 0; i < IMPL_EXTRACT_PATTERNS.length; i++) {
            Matcher m = IMPL_EXTRACT_PATTERNS[i].matcher(q);
            if (m.find()) {
                String kw = m.group(1).trim();
                kw = kw.replaceAll("(?:에서|중에서|중|으로|를|을|이|가)$", "").trim();
                if (kw.length() >= 2 && !isImplOrgWord(kw)) {
                    return kw;
                }
            }
        }
        return "";
    }

    private boolean isImplOrgWord(String word) {
        for (int i = 0; i < IMPL_ORG_KEYWORDS.length; i++) {
            if (word.indexOf(IMPL_ORG_KEYWORDS[i]) > -1) {
                return true;
            }
        }
        return false;
    }

    private boolean containsImplOrgQuestion(String question) {
        if (question == null) {
            return false;
        }
        for (int i = 0; i < IMPL_ORG_KEYWORDS.length; i++) {
            if (question.indexOf(IMPL_ORG_KEYWORDS[i]) > -1) {
                return true;
            }
        }
        return false;
    }

    /** 사용자가 명시적으로 요청한 금액 항목 힌트 생성 */
    private String buildExplicitFieldHint(String question) {
        if (question == null) {
            return "";
        }
        StringBuilder hint = new StringBuilder();
        hint.append("명시 요청 항목: ");
        boolean found = false;

        if (wantsDemandAmt(question)) {
            hint.append("[요구액] ");
            found = true;
        }
        if (question.indexOf("반영액") > -1 || question.indexOf("조정액") > -1) {
            hint.append("[반영액] ");
            found = true;
        }
        if (wantsPreYearAmt(question)) {
            hint.append("[전년도예산] ");
            found = true;
        }

        if (!found) {
            return "";
        }
        hint.append("→ [차수별 예산내역(재원표시)]에 차수별로 포함.");
        return hint.toString();
    }

    // ------------------------------------------------------------------
    // 일반 SQL 경로 (기존 동작)
    // ------------------------------------------------------------------

    private JSONObject handleSqlQuestion(String question, JSONObject plan, JSONObject result) throws Exception {
        String directAnswer = plan.optString("answer", "");
        String sql = plan.optString("sql", "");

        if (sql == null || sql.trim().length() == 0) {
            result.put("answer", directAnswer.length() > 0 ? directAnswer
                    : "질문을 조회 조건으로 변환하지 못했습니다. 조금 더 구체적으로 질문해 주세요.");
            return result;
        }

        sql = sanitizeSql(sql);
        validateSelectOnly(sql);
        sql = applyRowLimit(sql, getMaxRows());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setMaxRows(getMaxRows());

        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.error("AI 생성 SQL 실행 오류. sql=" + sql, e);
            result.put("sql", sql);
            result.put("answer", "조회 실행 중 오류가 발생했습니다. 질문을 다른 표현으로 다시 시도해 주세요.\n(상세: " + e.getMessage() + ")");
            return result;
        }

        JSONArray columns = new JSONArray();
        JSONArray dataList = new JSONArray();
        buildResultJson(rows, columns, dataList);

        logger.info("AI RAG[sql] provider=" + llmClient.getProviderName()
                + " rows=" + rows.size());

        String finalAnswer = llmClient.generate(SYSTEM_PERSONA, buildSummaryPrompt(question, sql, rows));
        if (finalAnswer == null || finalAnswer.trim().length() == 0) {
            finalAnswer = "총 " + rows.size() + "건의 결과를 조회했습니다. 아래 표를 확인해 주세요.";
        }

        result.put("answer", finalAnswer.trim());
        result.put("sql", sql);
        result.put("columns", columns);
        result.put("dataList", dataList);
        result.put("rowCount", rows.size());

        return result;
    }

    /** 질문 분류 + 계획(JSON) 프롬프트 */
    private String buildPlanPrompt(String question) {
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 한국 지방자치단체 '예산편성 심사정보시스템'의 CUBRID 데이터베이스 전문가입니다.\n");
        sb.append("사용자의 질문을 분석하여 아래 세 가지 모드 중 하나로 분류하고, 모드별 정보를 JSON 으로 출력하세요.\n");
        sb.append("\n[모드 분류 기준]\n");
        sb.append("1. \"report\" : 경상사업심사조서/투자사업심사조서의 내용에 관한 질문.\n");
        sb.append("   - 예: 특정 사업의 검토의견·요구내용·조정(반영) 결과, 사업별 예산 심사 내용, 어떤 부서의 어떤 사업이 얼마 반영됐는지, 검색태그(#민생 등)로 사업 찾기 등.\n");
        sb.append("   - 사업명·부서명·심사·검토·조서·반영·요구내용 등의 표현이 있으면 대부분 report 모드.\n");
        sb.append("2. \"sql\" : 그 외 단순 통계·목록 질문 (예: 연도 목록, 차수 목록, 부서 수, 통계목별 합계 등).\n");
        sb.append("3. \"chat\" : DB 조회가 필요 없는 인사·시스템 안내 질문.\n");
        sb.append("\n[report 모드일 때 추출할 정보]\n");
        sb.append("- fisYear: 질문 속 회계연도 4자리 (예: \"2026\"). 없으면 빈 문자열.\n");
        sb.append("- dgr: 차수 표현. \"본예산\" 또는 \"N회추경\" (예: \"1회추경\"). 구분이 없으면 빈 문자열(모든 차수).\n");
        sb.append("- reportCd: 경상사업 관련이면 \"010\", 투자사업 관련이면 \"020\", 구분 없으면 빈 문자열(둘 다).\n");
        sb.append("- bizKeyword: 사업명에서 검색할 핵심 키워드. 여러 개면 쉼표로 구분 (예: \"유가보조금,유류비,연료비\"). 없으면 빈 문자열.\n");
        sb.append("- deptKeyword: 부서/실국 이름 키워드 (예: \"복지\"). 없으면 빈 문자열.\n");
        sb.append("- tagKeyword: #으로 시작하는 검색태그 키워드 (예: \"민생\"). 없으면 빈 문자열.\n");
        sb.append("- implKeyword: 시행주관·시행주체·시행처·시행기관·사업기관 관련 질문일 때 [구분](요구내용)에서 검색할 기관/주체 키워드. 없으면 빈 문자열.\n");
        sb.append("  (예: '테크노파크가 시행주관인 사업' → implKeyword: \"테크노파크\", bizKeyword: \"\")\n");
        sb.append("  ※ 시행주관 질문의 기관명은 bizKeyword가 아니라 implKeyword로 넣을 것.\n");
        sb.append("\n[sql 모드 규칙]\n");
        sb.append("- 반드시 조회(SELECT) 질의만 생성. INSERT/UPDATE/DELETE/DDL 절대 금지.\n");
        sb.append("- CUBRID SQL 문법. 행 수 제한은 LIMIT 사용. 존재하는 테이블/컬럼만 사용.\n");
        sb.append("- 금액 집계는 SUM 등 집계함수 사용. DB 금액 원시 단위는 '원'이다.\n");
        sb.append("\n");
        sb.append(aiSchemaProvider.getSchemaText());
        sb.append("\n[출력 형식] 아래 JSON 한 개만 출력하고 다른 설명은 하지 마세요.\n");
        sb.append("{\"mode\": \"report|sql|chat\", \"fisYear\": \"\", \"dgr\": \"\", \"reportCd\": \"\", \"bizKeyword\": \"\", \"deptKeyword\": \"\", \"tagKeyword\": \"\", \"implKeyword\": \"\", \"needData\": true, \"sql\": \"(sql 모드일 때 SELECT 문)\", \"answer\": \"(chat 모드일 때 한국어 답변)\"}\n");
        sb.append("\n[사용자 질문]\n").append(question).append("\n");
        return sb.toString();
    }

    /** 일반 SQL 결과 요약 프롬프트 */
    private String buildSummaryPrompt(String question, String sql, List<Map<String, Object>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("아래 조회 결과를 바탕으로 사용자 질문에 답하라.\n");
        sb.append("- 결과 수치를 근거로 설명하되 단답형이 아니라 해석과 보충 설명을 덧붙일 것.\n");
        sb.append("- 조회 결과가 사업 단위가 아니면(연도 목록, 부서 통계 등) 보고서 서식 중 적용 가능한 항목만 사용해 정리할 것.\n");
        sb.append("- DB 금액 원시 단위는 '원'이므로, 큰 금액은 백만원 단위로 환산해 함께 표기할 것.\n");
        sb.append("- 없는 내용을 지어내지 말 것. 결과가 비어있으면 해당 조건의 데이터가 없다고 안내할 것.\n");
        sb.append("- 표는 화면에 따로 표시되므로, 답변은 핵심 요약과 해석 위주로 작성할 것.\n\n");
        sb.append("[사용자 질문]\n").append(question).append("\n\n");
        sb.append("[실행된 SQL]\n").append(sql).append("\n\n");
        sb.append("[조회 결과 - 최대 50행, JSON]\n");

        JSONArray sample = new JSONArray();
        int limit = Math.min(rows.size(), 50);
        for (int i = 0; i < limit; i++) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                obj.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
            }
            sample.add(obj);
        }
        sb.append("총 행수: ").append(rows.size()).append("\n");
        sb.append(sample.toString()).append("\n");
        return sb.toString();
    }

    private void buildResultJson(List<Map<String, Object>> rows, JSONArray columns, JSONArray dataList) {
        boolean firstRow = true;
        for (Map<String, Object> row : rows) {
            if (firstRow) {
                for (String col : row.keySet()) {
                    columns.add(col);
                }
                firstRow = false;
            }
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                obj.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
            }
            dataList.add(obj);
        }
    }

    /**
     * 모델 출력에서 JSON 을 추출한다. (```json ... ``` 같은 코드펜스 제거)
     */
    private JSONObject parseJsonFromModel(String raw) {
        if (raw == null) {
            return new JSONObject();
        }
        String text = raw.trim();

        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            if (firstNewline > -1) {
                text = text.substring(firstNewline + 1);
            }
            int lastFence = text.lastIndexOf("```");
            if (lastFence > -1) {
                text = text.substring(0, lastFence);
            }
            text = text.trim();
        }

        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start > -1 && end > start) {
            text = text.substring(start, end + 1);
        }

        try {
            return JSONObject.fromObject(text);
        } catch (Exception e) {
            logger.error("모델 JSON 파싱 실패: " + raw, e);
            JSONObject fallback = new JSONObject();
            fallback.put("mode", "chat");
            fallback.put("needData", false);
            fallback.put("answer", "질문을 이해하지 못했습니다. 다시 한 번 구체적으로 질문해 주세요.");
            return fallback;
        }
    }

    private String sanitizeSql(String sql) {
        String s = sql.trim();
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl > -1) {
                s = s.substring(nl + 1);
            }
            int lastFence = s.lastIndexOf("```");
            if (lastFence > -1) {
                s = s.substring(0, lastFence);
            }
            s = s.trim();
        }
        while (s.endsWith(";")) {
            s = s.substring(0, s.length() - 1).trim();
        }
        return s;
    }

    /**
     * SELECT 전용 / 단일 문장 검증.
     */
    private void validateSelectOnly(String sql) {
        String lower = sql.toLowerCase();

        if (!(lower.startsWith("select") || lower.startsWith("with"))) {
            throw new IllegalArgumentException("조회(SELECT) 질의만 허용됩니다.");
        }

        if (sql.indexOf(';') > -1) {
            throw new IllegalArgumentException("다중 구문은 허용되지 않습니다.");
        }

        for (int i = 0; i < FORBIDDEN_KEYWORDS.length; i++) {
            String kw = FORBIDDEN_KEYWORDS[i];
            if (containsWord(lower, kw)) {
                throw new IllegalArgumentException("허용되지 않는 구문(" + kw + ")이 포함되어 있습니다.");
            }
        }
    }

    private boolean containsWord(String text, String word) {
        int idx = 0;
        while ((idx = text.indexOf(word, idx)) > -1) {
            boolean leftOk = (idx == 0) || !isWordChar(text.charAt(idx - 1));
            int after = idx + word.length();
            boolean rightOk = (after >= text.length()) || !isWordChar(text.charAt(after));
            if (leftOk && rightOk) {
                return true;
            }
            idx = after;
        }
        return false;
    }

    private boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * LIMIT 이 없으면 행 수 제한을 추가한다.
     */
    private String applyRowLimit(String sql, int maxRows) {
        String lower = sql.toLowerCase();
        if (containsWord(lower, "limit")) {
            return sql;
        }
        return sql + " LIMIT " + maxRows;
    }
}

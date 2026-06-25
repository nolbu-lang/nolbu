package com.cs.bcjis.ai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    /** TB_BGTDGR 최대 회계연도 — 요청마다 반복 조회 방지 */
    private volatile String cachedMaxFisYear = "";

    /**
     * 답변 생성용 시스템 지침(system_instruction).
     * 예산 심사관 페르소나와 보고서 출력 서식을 모든 답변에 강제한다.
     * (질문 분류/SQL 생성 단계에는 적용하지 않는다 - JSON 출력이 필요하므로)
     */
    private static final String SYSTEM_PERSONA =
            "너는 부산시 예산담당관실 예산편성 전문 AI 도우미다. 반드시 간결한 보고서 서식으로만 답하라.\n"
            + "\n"
            + "[기본 답변 서식 - 사업 1건마다 아래 4항목만 작성. 그 외 항목 절대 추가 금지]\n"
            + "1. [사업명(통계목)] : 세세사업명(comp_ground) 뒤 통계목코드만 괄호 표기. 예) 지역사랑상품권 인센티브 보상금 ( 308-13)\n"
            + "   - 세부사업명(dbiz_nm)은 표시하지 말 것. '통계목:' 문구·통계목 명칭 전체는 절대 표시하지 말 것.\n"
            + "2. [소관부서]\n"
            + "3. [차수별 예산내역(재원표시)] : 앞 숫자=조정액 총합(bgt_amt), 괄호=조정액 재원구성(ADJ_DEF_FRSC_AMT). 예) 본예산:200백만원(국비140, 시비60)\n"
            + "   - 요구액·총사업비·전년도예산을 3번 예산액으로 쓰지 말 것. 재원도 요구액(DMN) 재원이 아닌 조정액 재원만.\n"
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
            + "- 2013년~최신 회계연도까지 동일 규칙: 3번 예산액=조정액(bgt_amt), 괄호=조정액 재원(ADJ_DEF_FRSC_AMT).\n"
            + "- 여러 연도 범위 질문이면 각 사업 블록 앞에 [회계연도] 표시 후 1~4번 항목 작성.\n"
            + "- 4번은 차수가 여러 개이면 차수별 줄바꿈으로 표시.\n"
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

    /** 질문 속 회계연도 (예: 2026년, 2026년도) */
    private static final Pattern FIS_YEAR_PATTERN = Pattern.compile("(\\d{4})\\s*년(?:도)?");

    /** 연도 범위 최대 폭(성능 보호). 그 이상이어도 범위 전체를 검색한다. */
    private static final int MAX_YEAR_RANGE_SPAN = 50;

    /**
     * 질문 속 회계연도 범위 — 임의 4자리 연도 쌍.
     * 예: 2017년~2020년, 2017~2020, 2018년에서 2022년까지, 2015년도~2019년도
     */
    private static final Pattern[] FIS_YEAR_RANGE_PATTERNS = {
            Pattern.compile("(\\d{4})\\s*년(?:도)?\\s*(?:에서|부터)?\\s*(?:~|～|\\-|–|—)\\s*(\\d{4})\\s*년(?:도)?(?:\\s*까지)?"),
            Pattern.compile("(\\d{4})\\s*년(?:도)?\\s*(?:에서|부터)\\s*(\\d{4})\\s*년(?:도)?\\s*까지"),
            Pattern.compile("(\\d{4})\\s*(?:~|～|\\-|–|—)\\s*(\\d{4})\\s*년(?:도)?(?:\\s*까지)?"),
            Pattern.compile("(\\d{4})\\s*년(?:도)?\\s*(?:~|～|\\-|–|—)\\s*(\\d{4})(?!\\d)"),
            Pattern.compile("(\\d{4})\\s*(?:~|～|\\-|–|—)\\s*(\\d{4})(?!\\d)")
    };

    /** 심사조서 비정형 필드: [구분]=gubun/demand_cont, [검토내용]=exam_cont */
    private static final String CONTENT_FIELD_GUBUN = "gubun";
    private static final String CONTENT_FIELD_EXAM = "exam";
    private static final String BRACKET_MARKER_EXAM = "[검토내용]";
    private static final String BRACKET_MARKER_GUBUN = "[구분]";
    private static final String BRACKET_MARKER_DEPT = "[소관부서]";

    /**
     * [소관부서] 지정 질문에서 부서·실국 키워드 추출.
     */
    private static final Pattern DEPT_QUOTED_PATTERN =
            Pattern.compile("^\\s*['\"'「]([^\"'」]+)['\"'」]");

    /** 소관부서 키워드에서 제외할 일반어 */
    private static final String[] DEPT_STOP_WORDS = {
            "소관부서", "소관", "부서", "실국", "국", "사업", "찾아", "검색", "정리", "해줘", "주세요",
            "관련", "내용", "경상", "투자", "심사조서", "예산", "회계연도", "차수"
    };

    /**
     * [구분]/[검토내용] 지정 질문에서 키워드 추출 (대괄호 표기 뒤 문장만 대상).
     * UI 예시: "에 있는 내용 중 마무리 사업", "테크노파크가 시행처인 사업"
     */
    private static final Pattern[] CONTENT_KEYWORD_PATTERNS = {
            Pattern.compile("[\"'「]([^\"'」]+)[\"'」]"),
            Pattern.compile("(?:있는\\s*내용\\s*중|내용\\s*중)\\s*([\\uAC00-\\uD7A3A-Za-z0-9()]+?)(?:\\s*(?:가\\s*시행|사업|을|를|인|으로|관련|찾))"),
            Pattern.compile("([\\uAC00-\\uD7A3A-Za-z0-9()]{2,})\\s*가\\s*시행(?:처|주관|주체|기관)(?:인)?"),
            Pattern.compile("(?:에|에서|중|이|의)?\\s*(?:있는\\s*)?(?:내용\\s*중\\s*)?([\\uAC00-\\uD7A3A-Za-z0-9()]{2,}?)(?:\\s*(?:가\\s*시행|사업|을|를|인|으로|관련|찾))")
    };

    /** 사업명 키워드 추출용 패턴 (LLM 보완) */
    private static final Pattern[] BIZ_KEYWORD_PATTERNS = {
            Pattern.compile("[\"'「]([^\"'」]+)[\"'」]"),
            Pattern.compile("([\\uAC00-\\uD7A3\\w]+(?:\\s*,\\s*[\\uAC00-\\uD7A3\\w]+)+)\\s*관련"),
            Pattern.compile("([\\uAC00-\\uD7A3\\w]{2,})\\s*관련\\s*사업"),
            Pattern.compile("(?:에서|중에서|중)\\s*([\\uAC00-\\uD7A3\\w]{2,})\\s*(?:관련\\s*)?사업"),
            Pattern.compile("([\\uAC00-\\uD7A3\\w]{2,})\\s*사업(?:을|를|중| 관련| 찾)"),
            // "유가보조금 찾아줘" 등 '사업' 접미어 없이 검색하는 질문
            Pattern.compile("([\\uAC00-\\uD7A3\\w]{2,})\\s*(?:찾아|검색|정리|알려)(?:줘|주세요)?"),
            Pattern.compile("([\\uAC00-\\uD7A3\\w]{2,})\\s*(?:관련|대한|관한)")
    };

    /** 사업명 키워드에서 제외할 일반어 */
    private static final String[] BIZ_STOP_WORDS = {
            "경상사업", "투자사업", "경상사업심사조서", "투자사업심사조서", "경상", "투자", "심사조서",
            "예산", "본예산", "추경", "회추경", "찾아줘", "찾아", "검색", "정리", "알려줘", "알려",
            "관련", "사업", "조서", "년", "연도", "회계연도", "차수", "내용", "요약", "정리해줘",
            "해줘", "주세요", "보여줘", "확인", "검토의견", "심사의견", "검토내용", "요구내용", "구분",
            "편성", "반영", "조정", "요구", "전년도", "이전년도", "작년", "올해", "금년", "도우미",
            "소관부서", "소관", "부서", "실국"
    };

    private int getMaxRows() {
        return getIntProp("Globals.GeminiMaxRows", 200);
    }

    /** 심사조서 RAG 시 컨텍스트로 넘길 최대 사업(블록) 수 */
    private boolean isReportDbOnly() {
        try {
            String v = config.getProperty("Globals.AiReportDbOnly", "true");
            return !"false".equalsIgnoreCase(v.trim());
        } catch (Exception e) {
            return true;
        }
    }

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
                    + "globals.properties 설정을 확인하세요.\n"
                    + "  · Globals.ClovaEndpoint (내부 LLM Studio URL)");
            return result;
        }

        result.put("aiProvider", llmClient.getProviderName());

        // 1) 질문 분류 — 심사조서 질문은 규칙 기반으로 바로 처리 (LLM 1회 절약)
        JSONObject plan;
        if (looksLikeReportQuestion(question)) {
            plan = enrichPlanForReport(question, new JSONObject());
        } else {
            String planRaw = llmClient.generate(buildPlanPrompt(question));
            plan = parseJsonFromModel(planRaw);
        }

        String mode = plan.optString("mode", "");

        // 사업명·심사조서 질문은 LLM 분류와 무관하게 report 경로 우선
        if ("report".equalsIgnoreCase(mode) || looksLikeReportQuestion(question)) {
            if (!looksLikeReportQuestion(question)) {
                plan = enrichPlanForReport(question, plan);
            }
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
        String deptKeyword = plan.optString("deptKeyword", "").trim();
        String tagKeyword = plan.optString("tagKeyword", "").trim();
        String implKeyword = plan.optString("implKeyword", "").trim();
        boolean explicitContent = isExplicitContentFieldSearch(question);
        // [소관부서] 대괄호가 있을 때만 부서·실국 필터 (없으면 기존과 동일, LLM 추출 dept 무시)
        if (hasDeptBracketFilter(question)) {
            deptKeyword = resolveDeptKeyword(question, deptKeyword);
        } else {
            deptKeyword = "";
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setMaxRows(getMaxReportBlocks());

        // [구분]/[검토내용] 대괄호 지정 시 — 해당 비정형 필드 검색 (+ [소관부서] 있으면 부서 필터)
        String contentField = "";
        String contentKeyword = "";
        String bizKeyword = "";
        if (explicitContent) {
            ContentSearchInfo contentSearch = resolveContentSearch(question,
                    plan.optString("contentKeyword", "").trim());
            contentField = contentSearch.field;
            contentKeyword = contentSearch.keyword;
            if (contentKeyword.length() == 0) {
                result.put("answer",
                        "비정형 내용 검색 시 [구분] 또는 [검토내용]을 지정하고, 해당 목록에서 찾을 키워드를 함께 적어 주세요.\n"
                        + "예) 2026년 경상사업 및 투자사업에서 [검토내용]에 있는 내용 중 마무리 사업을 찾아서 정리해줘\n"
                        + "예) 2026년 [소관부서]복지국 [검토내용] 마무리 사업을 찾아서 정리해줘");
                result.put("rowCount", 0);
                result.put("columns", new JSONArray());
                result.put("dataList", new JSONArray());
                return result;
            }
        } else {
            if (implKeyword.length() == 0) {
                implKeyword = extractImplKeyword(question);
            }
            // 사업명 키워드: LLM + 규칙 기반 보완 (핵심 기능)
            bizKeyword = resolveBizKeyword(question, plan.optString("bizKeyword", "").trim());
            if (hasDeptBracketFilter(question) && deptKeyword.length() > 0) {
                bizKeyword = removeDeptTokensFromKeywordCsv(bizKeyword, deptKeyword);
            }
            // 시행주관 질문인데 기관명이 bizKeyword로 들어온 경우만 전환
            if (implKeyword.length() == 0 && containsImplOrgQuestion(question) && bizKeyword.length() > 0) {
                implKeyword = bizKeyword;
                bizKeyword = "";
            }
        }

        // 회계연도: 질문·계획·이전년도 표현 보완 (검색 범위 판단은 resolve 전 plan 연도 기준)
        String planFisYear = fisYear;
        boolean explicitFisYear = hasExplicitFisYear(question, planFisYear);
        fisYear = resolveFisYear(question, planFisYear, jdbcTemplate);

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

        List<Map<String, Object>> rows = searchReportRows(jdbcTemplate, question,
                reportCd, planFisYear, explicitFisYear, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword);

        if (!rows.isEmpty()) {
            enrichReportRowsWithFrsc(jdbcTemplate, rows);
            rows = AiReportContextBuilder.trimRowsToMaxBizGroups(rows, getMaxReportBlocks());
        }

        if (rows.isEmpty()) {
            if (explicitContent) {
                result.put("answer",
                        "조건에 맞는 심사조서 데이터가 없습니다.\n"
                        + "심사조서 비정형 내용 검색 시 [구분] 또는 [검토내용] 목록을 명시해 주세요.\n"
                        + "예) 2026년 경상사업 및 투자사업에서 [검토내용]에 있는 내용 중 마무리 사업을 찾아서 정리해줘\n"
                        + "예) 2026년 경상사업 및 투자사업에서 [구분]에 있는 내용 중 테크노파크가 시행처인 사업을 찾아서 정리해줘");
            } else {
                result.put("answer",
                        "조건에 맞는 심사조서 데이터가 없습니다.\n"
                        + "사업명 키워드와 회계연도를 확인해 주세요.\n"
                        + (hasDeptBracketFilter(question) && deptKeyword.length() > 0
                                ? "([소관부서] " + deptKeyword + " 소속 사업만 검색했습니다.)\n" : "")
                        + "예) 2025년 경상사업 및 투자사업에서 유가보조금 관련 사업을 찾아서 정리해줘\n"
                        + "예) 2026년 [소관부서]복지국 유가보조금 사업을 찾아서 정리해줘");
            }
            result.put("rowCount", 0);
            result.put("columns", new JSONArray());
            result.put("dataList", new JSONArray());
            return result;
        }

        // 답변 생성 — 기본은 DB 직접 조립(1~4항목). LLM 호출 생략으로 응답 속도 개선.
        AiReportContextBuilder.ContextOptions ctxOpts = buildContextOptions(question, planFisYear, tagKeyword, contentField);
        ctxOpts.maxBlocks = getMaxReportBlocks();
        String answer;
        if (isReportDbOnly()) {
            answer = AiReportContextBuilder.assembleReportAnswer(rows, "", ctxOpts);
            logger.info("AI RAG[report] db-only year=" + fisYear + " rows=" + rows.size());
        } else {
            String context = AiReportContextBuilder.buildReportContext(rows, fisYear, dgr, ctxOpts);
            logger.info("AI RAG[report] provider=" + llmClient.getProviderName()
                    + " year=" + fisYear + " rows=" + rows.size()
                    + " contextChars=" + context.length());
            answer = llmClient.generate(SYSTEM_PERSONA, buildReportAnswerPrompt(question, context));
            if (answer == null || answer.trim().length() == 0) {
                answer = "총 " + rows.size() + "건의 심사조서를 찾았습니다. 아래 표를 확인해 주세요.";
            } else {
                answer = answer.trim();
            }
            answer = AiReportContextBuilder.assembleReportAnswer(rows, answer, ctxOpts);
        }

        if (rows.size() > 0) {
            int frscZero = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (AiReportContextBuilder.formatFrscForRow(rows.get(i)).length() == 0) {
                    frscZero++;
                }
            }
            Map<String, Object> sample = rows.get(0);
            logger.info("AI RAG[report] frsc sample: frsc_amt2=" + AiReportContextBuilder.getLong(sample, "frsc_amt2")
                    + " frsc_detail=" + AiReportContextBuilder.getStr(sample, "frsc_detail")
                    + " frsc_fmt=" + AiReportContextBuilder.formatFrscForRow(sample)
                    + " frsc_zero_rows=" + frscZero + "/" + rows.size());
        }

        result.put("answer", answer);
        result.put("rowCount", rows.size());

        // 화면 표 출력용 요약 목록
        JSONArray columns = new JSONArray();
        columns.add("사업명");
        columns.add("소관부서");
        columns.add("차수");
        columns.add("요구액(백만원)");
        columns.add("조정액(백만원)");
        columns.add("조정재원");

        JSONArray dataList = new JSONArray();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            obj.put("사업명", AiReportContextBuilder.buildBizLabel(row));
            obj.put("소관부서", (AiReportContextBuilder.getStr(row, "office_nm") + " " + AiReportContextBuilder.getStr(row, "dept_nm")).trim());
            obj.put("차수", AiReportContextBuilder.buildDgrLabel(
                    AiReportContextBuilder.getStr(row, "fis_year"),
                    AiReportContextBuilder.getStr(row, "bgt_compo_fg"),
                    AiReportContextBuilder.getLong(row, "add_times"),
                    AiReportContextBuilder.getStr(row, "bgt_dgr")));
            obj.put("요구액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "demand_bgt_amt")));
            obj.put("조정액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "bgt_amt")));
            obj.put("조정재원", AiReportContextBuilder.formatFrscForRow(row));
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
     * 조회된 조서 행에 TB_DGRCOMPOFRSC.ADJ_DEF_FRSC_AMT 기반 재원(frsc_amt1~6)을 부여한다.
     * 연도별로 일괄 조회하며 2013~2026 등 TB_FISYEAR 전 연도에 동일 로직 적용.
     * 예산액(bgt_amt)=조정액 총액, 괄호=재원별 조정액(ADJ_DEF_FRSC_AMT) 합.
     */
    private void enrichReportRowsWithFrsc(JdbcTemplate jdbcTemplate, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        java.util.LinkedHashMap<String, List<Map<String, Object>>> byYear =
                new java.util.LinkedHashMap<String, List<Map<String, Object>>>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            String year = AiReportContextBuilder.getStr(row, "fis_year");
            if (year.length() == 0) {
                continue;
            }
            List<Map<String, Object>> list = byYear.get(year);
            if (list == null) {
                list = new ArrayList<Map<String, Object>>();
                byYear.put(year, list);
            }
            list.add(row);
        }

        for (java.util.Iterator<java.util.Map.Entry<String, List<Map<String, Object>>>> it =
                byYear.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry<String, List<Map<String, Object>>> entry = it.next();
            String fisYear = entry.getKey();
            List<Map<String, Object>> yearRows = entry.getValue();

            java.util.LinkedHashSet<String> keySet = new java.util.LinkedHashSet<String>();
            List<Object> pairArgs = new ArrayList<Object>();
            for (int i = 0; i < yearRows.size(); i++) {
                Map<String, Object> row = yearRows.get(i);
                long dgrNum = AiReportContextBuilder.getLong(row, "bgt_dgr");
                String compoId = AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
                if (dgrNum == 0L || compoId.length() == 0) {
                    continue;
                }
                String key = dgrNum + "\u0001" + compoId;
                if (keySet.add(key)) {
                    pairArgs.add(Integer.valueOf((int) dgrNum));
                    pairArgs.add(compoId);
                }
            }
            if (pairArgs.isEmpty()) {
                continue;
            }

            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < pairArgs.size(); i += 2) {
                if (inClause.length() > 0) {
                    inClause.append(",");
                }
                inClause.append("(?,?)");
            }

            List<Object> args = new ArrayList<Object>();
            args.add(fisYear);
            args.addAll(pairArgs);

            String sql = "SELECT Z1.fis_year AS fis_year\n"
                    + "     , Z1.bgt_dgr AS bgt_dgr\n"
                    + "     , Z1.te_bgt_compo_id AS te_bgt_compo_id\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '160', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt1\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '110', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), '120', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), '130', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt2\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '140', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), '150', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt3\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '180', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt4\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '190', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt5\n"
                    + "     , NVL(SUM(DECODE(Z2.STAND_FRSC_CD, '170', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), '200', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), '210', NVL(Z1.ADJ_DEF_FRSC_AMT, 0), 0)), 0) AS frsc_amt6\n"
                    + "     , NVL((SELECT GROUP_CONCAT(ZZ.FRSC SEPARATOR '|')\n"
                    + "              FROM (SELECT MAX(Z2B.STAND_FRSC_CD) || ':' || MAX(Z2B.FRSC_FG_NM) || ':' || TO_CHAR(NVL(SUM(Z1B.ADJ_DEF_FRSC_AMT), 0)) AS FRSC\n"
                    + "                         , NVL(SUM(Z1B.ADJ_DEF_FRSC_AMT), 0) AS FRSC_AMT\n"
                    + "                      FROM TB_DGRCOMPOFRSC Z1B, TB_YEARFRSC Z2B\n"
                    + "                     WHERE Z1B.FIS_YEAR = Z1.FIS_YEAR\n"
                    + "                       AND Z1B.BGT_DGR = Z1.BGT_DGR\n"
                    + "                       AND Z1B.TE_BGT_COMPO_ID = Z1.TE_BGT_COMPO_ID\n"
                    + appendYearFrscJoin("Z1B", "Z2B")
                    + appendFrscBootstrapExcludeClause("Z1B")
                    + "                     GROUP BY Z2B.FRSC_FG_CD) ZZ\n"
                    + "             WHERE ZZ.FRSC_AMT <> 0), '') AS frsc_detail\n"
                    + "  FROM TB_DGRCOMPOFRSC Z1, TB_YEARFRSC Z2\n"
                    + " WHERE 1=1\n"
                    + appendYearFrscJoin("Z1", "Z2")
                    + appendFrscBootstrapExcludeClause("Z1")
                    + "   AND Z1.FIS_YEAR = ?\n"
                    + "   AND (Z1.BGT_DGR, Z1.TE_BGT_COMPO_ID) IN (" + inClause + ")\n"
                    + " GROUP BY Z1.fis_year, Z1.bgt_dgr, Z1.te_bgt_compo_id";

            List<Map<String, Object>> frscRows;
            try {
                frscRows = jdbcTemplate.queryForList(sql, args.toArray());
            } catch (Exception e) {
                logger.error("재원 일괄 조회 오류 year=" + fisYear, e);
                continue;
            }

            java.util.HashMap<String, Map<String, Object>> lookup =
                    new java.util.HashMap<String, Map<String, Object>>();
            for (int i = 0; i < frscRows.size(); i++) {
                Map<String, Object> fr = frscRows.get(i);
                String k = AiReportContextBuilder.getLong(fr, "bgt_dgr") + "\u0001"
                        + AiReportContextBuilder.getStr(fr, "te_bgt_compo_id");
                lookup.put(k, fr);
            }

            for (int i = 0; i < yearRows.size(); i++) {
                Map<String, Object> row = yearRows.get(i);
                String k = AiReportContextBuilder.getLong(row, "bgt_dgr") + "\u0001"
                        + AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
                Map<String, Object> fr = lookup.get(k);
                if (fr != null) {
                    AiReportContextBuilder.applyAdjFrscFromDb(row, fr);
                }
            }
        }
    }

    /** bootstrap 가짜 재원 행 제외 — 동일 편성에 운영 재원이 있으면 bootstrap 은 합산하지 않음 */
    private static String appendFrscBootstrapExcludeClause(String alias) {
        return "   AND (" + alias + ".regi_id <> 'bootstrap'\n"
                + "        OR NOT EXISTS (SELECT 1 FROM TB_DGRCOMPOFRSC Z0\n"
                + "                        WHERE Z0.FIS_YEAR = " + alias + ".FIS_YEAR\n"
                + "                          AND Z0.BGT_DGR = " + alias + ".BGT_DGR\n"
                + "                          AND Z0.TE_BGT_COMPO_ID = " + alias + ".TE_BGT_COMPO_ID\n"
                + "                          AND NVL(Z0.regi_id, '') <> 'bootstrap'\n"
                + "                          AND NVL(Z0.ADJ_DEF_FRSC_AMT, 0) <> 0))\n";
    }

    /**
     * TB_YEARFRSC 조인 — 해당 연도 코드가 없으면 동일 frsc_fg_cd 의 최신 연도 매핑 사용.
     * (2013~2026 등 전 연도에서 재원 슬롯·표시 로직 동일 적용)
     */
    private static String appendYearFrscJoin(String frscAlias, String yearFrscAlias) {
        return "   AND " + yearFrscAlias + ".FRSC_FG_CD = " + frscAlias + ".FRSC_FG_CD\n"
                + "   AND " + yearFrscAlias + ".FIS_YEAR = (\n"
                + "         SELECT MAX(Y.FIS_YEAR) FROM TB_YEARFRSC Y\n"
                + "          WHERE Y.FRSC_FG_CD = " + frscAlias + ".FRSC_FG_CD\n"
                + "            AND (Y.FIS_YEAR = " + frscAlias + ".FIS_YEAR\n"
                + "                 OR NOT EXISTS (SELECT 1 FROM TB_YEARFRSC Y0\n"
                + "                                 WHERE Y0.FIS_YEAR = " + frscAlias + ".FIS_YEAR\n"
                + "                                   AND Y0.FRSC_FG_CD = " + frscAlias + ".FRSC_FG_CD)))\n";
    }

    /**
     * 심사조서 검색 — 사업명 키워드 우선, 단계별 조건 완화.
     * 이전 개선 사항이 LLM 응답 변동으로 누락되지 않도록 규칙 기반 재시도를 포함한다.
     */
    private List<Map<String, Object>> searchReportRows(JdbcTemplate jdbcTemplate, String question,
            String reportCd, String planFisYear, boolean explicitFisYear, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword) {

        List<String> years = buildYearSearchList(jdbcTemplate, question, planFisYear, explicitFisYear);
        boolean mergeAllYears = shouldMergeYearResults(question, planFisYear, years);
        boolean explicitContent = isExplicitContentFieldSearch(question);
        String ruleBizKeyword = explicitContent ? "" : extractBizKeywordFromQuestion(question);

        // 사용자가 [구분]/[검토내용]을 지정한 경우 — 비정형 필드 검색 (+ deptKeyword 부서 필터)
        if (explicitContent) {
            if (contentField.length() > 0 && contentKeyword.length() > 0) {
                List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                        mergeAllYears, bgtCompoFg, addTimes, "", deptKeyword, tagKeyword,
                        "", contentField, contentKeyword, true);
                logger.info("AI RAG hit[content-bracket] years=" + years.size() + " merge=" + mergeAllYears
                        + " field=" + contentField + " kw=" + contentKeyword
                        + " dept=" + deptKeyword + " rows=" + rows.size());
                return rows;
            }
            return new ArrayList<Map<String, Object>>();
        }

        // 1차: 사업명 넓은 검색(핵심) — deptKeyword 있으면 해당 부서 소속만
        if (bizKeyword.length() > 0) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                    "", "", "", true);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[1-biz-broad] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + bizKeyword + " rows=" + rows.size());
                return rows;
            }
        }

        // 2차: 사업명 좁은 검색 — 사업명 필드만
        if (bizKeyword.length() > 0) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                    "", "", "", false);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[2-biz-narrow] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + bizKeyword + " rows=" + rows.size());
                return rows;
            }
        }

        // 3차: 규칙 추출 사업명 키워드 (LLM이 비우거나 틀린 경우)
        if (ruleBizKeyword.length() > 0 && !ruleBizKeyword.equals(bizKeyword)) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, ruleBizKeyword, deptKeyword, tagKeyword,
                    "", "", "", true);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[3-rule-biz] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + ruleBizKeyword + " rows=" + rows.size());
                return rows;
            }
        }

        // 4차: 시행주관 — [구분] 검색
        if (containsImplOrgQuestion(question)) {
            String retryImpl = implKeyword.length() > 0 ? implKeyword : extractImplKeyword(question);
            if (retryImpl.length() > 0) {
                List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                        mergeAllYears, bgtCompoFg, addTimes, "", deptKeyword, tagKeyword,
                        retryImpl, "", "", true);
                if (!rows.isEmpty()) {
                    logger.info("AI RAG hit[4-impl] years=" + years.size() + " merge=" + mergeAllYears
                            + " kw=" + retryImpl + " rows=" + rows.size());
                    return rows;
                }
            }
        }

        // 6차: 부서·태그만으로 검색
        // [소관부서]+사업명/비정형 검색 시에는 부서 단독 폴백 금지 (사업명 무시·한도까지 부서 전체 반환 방지)
        boolean deptBracketWithSearchIntent = hasDeptBracketWithNonDeptSearchIntent(
                question, explicitContent, bizKeyword, ruleBizKeyword);
        if (!deptBracketWithSearchIntent && (deptKeyword.length() > 0 || tagKeyword.length() > 0)) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, "", deptKeyword, tagKeyword,
                    "", "", "", false);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[6-dept-tag-only] years=" + years.size() + " merge=" + mergeAllYears
                        + " dept=" + deptKeyword + " tag=" + tagKeyword + " rows=" + rows.size());
                return rows;
            }
        }

        // 7차: 회계연도 미지정·인근연도 실패 시 TB_FISYEAR 전체 연도 순차 검색 (2013~ 등 동일 로직)
        if (!explicitFisYear) {
            List<String> allYears = getAllFisYears(jdbcTemplate);
            for (int i = 0; i < allYears.size(); i++) {
                String y = allYears.get(i);
                if (years.contains(y)) {
                    continue;
                }
                List<Map<String, Object>> rows = runReportQuery(jdbcTemplate, reportCd, y,
                        bgtCompoFg, addTimes, bizKeyword.length() > 0 ? bizKeyword : ruleBizKeyword,
                        deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword,
                        bizKeyword.length() > 0);
                if (!rows.isEmpty()) {
                    logger.info("AI RAG hit[7-all-years] year=" + y);
                    return rows;
                }
            }
        }

        return new ArrayList<Map<String, Object>>();
    }

    private List<Map<String, Object>> runReportQuery(JdbcTemplate jdbcTemplate, String reportCd, String fisYear,
            String bgtCompoFg, int addTimes, String bizKeyword, String deptKeyword, String tagKeyword,
            String implKeyword, String contentField, String contentKeyword, boolean broadKeyword) {
        List<Object> args = new ArrayList<Object>();
        String sql = buildReportSql(reportCd, fisYear, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword,
                contentField, contentKeyword, broadKeyword, args);
        return queryReport(jdbcTemplate, sql, args);
    }

    /**
     * 검색 대상 연도를 순회한다.
     * mergeAllYears=true(연도 범위·연도별 질문)이면 각 연도 결과를 모두 합쳐 반환하고,
     * false이면 첫 번째 hit 연도만 반환한다.
     */
    private List<Map<String, Object>> collectReportRowsAcrossYears(JdbcTemplate jdbcTemplate, String reportCd,
            List<String> years, boolean mergeAllYears, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword, boolean broadKeyword) {
        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < years.size(); i++) {
            List<Map<String, Object>> rows = runReportQuery(jdbcTemplate, reportCd, years.get(i),
                    bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                    implKeyword, contentField, contentKeyword, broadKeyword);
            if (!rows.isEmpty()) {
                if (mergeAllYears) {
                    merged.addAll(rows);
                } else {
                    return rows;
                }
            }
        }
        if (mergeAllYears && !merged.isEmpty()) {
            sortReportRowsByYearAndBiz(merged);
            return AiReportContextBuilder.trimRowsToMaxBizGroups(merged, getMaxReportBlocks());
        }
        return new ArrayList<Map<String, Object>>();
    }

    /** 연도·부서·사업·차수 순 정렬 (범위 검색 결과 표시 일관성) */
    private void sortReportRowsByYearAndBiz(List<Map<String, Object>> rows) {
        java.util.Collections.sort(rows, new java.util.Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                int c = AiReportContextBuilder.getStr(a, "fis_year")
                        .compareTo(AiReportContextBuilder.getStr(b, "fis_year"));
                if (c != 0) {
                    return c;
                }
                c = AiReportContextBuilder.getStr(a, "office_nm")
                        .compareTo(AiReportContextBuilder.getStr(b, "office_nm"));
                if (c != 0) {
                    return c;
                }
                c = AiReportContextBuilder.getStr(a, "dept_nm")
                        .compareTo(AiReportContextBuilder.getStr(b, "dept_nm"));
                if (c != 0) {
                    return c;
                }
                c = AiReportContextBuilder.buildBizLabel(a)
                        .compareTo(AiReportContextBuilder.buildBizLabel(b));
                if (c != 0) {
                    return c;
                }
                return AiReportContextBuilder.getStr(a, "bgt_dgr")
                        .compareTo(AiReportContextBuilder.getStr(b, "bgt_dgr"));
            }
        });
    }

    /** 여러 연도 범위·연도별 질문이면 전 연도 결과를 합쳐야 함 */
    private boolean shouldMergeYearResults(String question, String planFisYear, List<String> years) {
        if (years == null || years.size() <= 1) {
            return false;
        }
        return extractYearRangeBounds(question) != null
                || parseYearRangeLabel(planFisYear) != null
                || mentionsAllYearsSearch(question);
    }

    /**
     * 검색 대상 회계연도 목록.
     * - 연도 범위(임의 시작~끝 연도)면 범위 내 전 연도를 검색 대상으로 사용.
     * - 단일 연도 명시면 해당 연도만.
     * - 미지정 시 최신 연도 + 인근 4개년, 최종 실패 시 TB_FISYEAR 전체(7차).
     */
    private List<String> buildYearSearchList(JdbcTemplate jdbcTemplate, String question,
            String planFisYear, boolean explicitYear) {
        int[] range = extractYearRangeBounds(question);
        if (range == null && planFisYear != null) {
            range = parseYearRangeLabel(planFisYear);
        }
        if (range != null) {
            return buildYearsInRange(range[0], range[1]);
        }

        List<String> years = new ArrayList<String>();
        String fromQuestion = extractFisYearFromQuestion(question);

        if (fromQuestion.length() > 0) {
            years.add(fromQuestion);
        }
        if (planFisYear != null && planFisYear.length() > 0 && !years.contains(planFisYear)) {
            years.add(planFisYear);
        }
        if (mentionsPreviousYear(question)) {
            String maxYear = getMaxFisYear(jdbcTemplate);
            if (maxYear.length() == 4) {
                String prev = String.valueOf(Integer.parseInt(maxYear) - 1);
                if (!years.contains(prev)) {
                    years.add(prev);
                }
            }
        }
        if (years.isEmpty()) {
            String maxYear = getMaxFisYear(jdbcTemplate);
            if (maxYear.length() > 0) {
                years.add(maxYear);
            }
        }

        // 연도를 명시하지 않은 경우에만 인근 연도 확장 (명시 연도는 해당 연도만 — 2020·2022 등 동일 적용)
        if (!explicitYear) {
            String anchor = years.isEmpty() ? getMaxFisYear(jdbcTemplate) : years.get(0);
            if (anchor.length() == 4) {
                int base = Integer.parseInt(anchor);
                for (int i = 1; i < 5; i++) {
                    String y = String.valueOf(base - i);
                    if (!years.contains(y)) {
                        years.add(y);
                    }
                }
            }
        }

        if (mentionsAllYearsSearch(question)) {
            List<String> allYears = getAllFisYears(jdbcTemplate);
            for (int i = 0; i < allYears.size(); i++) {
                String y = allYears.get(i);
                if (!years.contains(y)) {
                    years.add(y);
                }
            }
        }
        return years;
    }

    /** "2017~2020", "2017-2020" 등 범위 라벨 파싱 (임의 4자리 연도) */
    private int[] parseYearRangeLabel(String label) {
        if (label == null) {
            return null;
        }
        Matcher m = Pattern.compile("(\\d{4})\\s*(?:~|～|\\-|–|—)\\s*(\\d{4})").matcher(label.trim());
        if (m.find()) {
            return normalizeYearRange(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
        }
        return null;
    }

    /** 질문·LLM 계획에 회계연도(단일·범위)가 명시되었는지 */
    private boolean hasExplicitFisYear(String question, String planFisYear) {
        return extractYearRangeBounds(question) != null
                || parseYearRangeLabel(planFisYear) != null
                || extractFisYearFromQuestion(question).length() > 0
                || (planFisYear != null && planFisYear.trim().length() > 0);
    }

    /** '년도별', '전체 연도', '모든 연도' 등 전 연도 검색 의도 */
    private boolean mentionsAllYearsSearch(String question) {
        if (question == null) {
            return false;
        }
        return question.indexOf("년도별") > -1 || question.indexOf("연도별") > -1
                || question.indexOf("전체연도") > -1 || question.indexOf("전체 연도") > -1
                || question.indexOf("모든연도") > -1 || question.indexOf("모든 연도") > -1
                || question.indexOf("전 연도") > -1 || question.indexOf("매년") > -1;
    }

    /** TB_FISYEAR 기준 전체 회계연도 (오름차순) */
    private List<String> getAllFisYears(JdbcTemplate jdbcTemplate) {
        List<String> years = new ArrayList<String>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT fis_year FROM TB_FISYEAR ORDER BY fis_year");
            for (int i = 0; i < rows.size(); i++) {
                String y = AiReportContextBuilder.getStr(rows.get(i), "fis_year");
                if (y.length() == 4 && !years.contains(y)) {
                    years.add(y);
                }
            }
        } catch (Exception e) {
            logger.error("TB_FISYEAR 전체 연도 조회 실패", e);
        }
        if (years.isEmpty()) {
            String maxYear = getMaxFisYear(jdbcTemplate);
            if (maxYear.length() == 4) {
                int end = Integer.parseInt(maxYear);
                for (int y = 2013; y <= end; y++) {
                    years.add(String.valueOf(y));
                }
            }
        }
        return years;
    }

    private String getMaxFisYear(JdbcTemplate jdbcTemplate) {
        if (cachedMaxFisYear != null && cachedMaxFisYear.length() == 4) {
            return cachedMaxFisYear;
        }
        try {
            Object maxYear = jdbcTemplate.queryForObject("SELECT MAX(fis_year) FROM TB_BGTDGR", String.class);
            String year = maxYear == null ? "" : String.valueOf(maxYear);
            if (year.length() == 4) {
                cachedMaxFisYear = year;
            }
            return year;
        } catch (Exception e) {
            logger.error("최신 회계연도 조회 실패", e);
            return "";
        }
    }

    private String resolveFisYear(String question, String planYear, JdbcTemplate jdbcTemplate) {
        int[] range = extractYearRangeBounds(question);
        if (range == null) {
            range = parseYearRangeLabel(planYear);
        }
        if (range != null) {
            return String.valueOf(range[0]) + "~" + String.valueOf(range[1]);
        }
        String fromQuestion = extractFisYearFromQuestion(question);
        if (fromQuestion.length() > 0) {
            return fromQuestion;
        }
        if (planYear.length() > 0) {
            return planYear;
        }
        if (mentionsPreviousYear(question)) {
            String maxYear = getMaxFisYear(jdbcTemplate);
            if (maxYear.length() == 4) {
                return String.valueOf(Integer.parseInt(maxYear) - 1);
            }
        }
        return getMaxFisYear(jdbcTemplate);
    }

    /** 질문 속 회계연도 범위 [시작, 끝]. 없으면 null (임의 4자리 연도) */
    private int[] extractYearRangeBounds(String question) {
        if (question == null) {
            return null;
        }
        for (int i = 0; i < FIS_YEAR_RANGE_PATTERNS.length; i++) {
            Matcher m = FIS_YEAR_RANGE_PATTERNS[i].matcher(question);
            if (m.find()) {
                int[] range = normalizeYearRange(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                if (range != null) {
                    return range;
                }
            }
        }
        return null;
    }

    /** 4자리 연도 쌍 → [min, max]. 동일 연도(2024~2024)도 허용 */
    private int[] normalizeYearRange(int y1, int y2) {
        if (y1 < 1900 || y1 > 2100 || y2 < 1900 || y2 > 2100) {
            return null;
        }
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);
        if (end - start > MAX_YEAR_RANGE_SPAN) {
            logger.warn("AI RAG 연도 범위가 넓음(" + start + "~" + end + "). 검색은 전체 수행.");
        }
        return new int[] { start, end };
    }

    /**
     * 범위 내 회계연도 목록 — 사용자가 지정한 시작~끝 연도 전체(오름차순).
     * TB_FISYEAR 존재 여부와 무관하게 범위의 모든 연도를 검색한다.
     */
    private List<String> buildYearsInRange(int startYear, int endYear) {
        List<String> years = new ArrayList<String>();
        for (int y = startYear; y <= endYear; y++) {
            years.add(String.valueOf(y));
        }
        return years;
    }

    private String extractFisYearFromQuestion(String question) {
        if (question == null) {
            return "";
        }
        if (extractYearRangeBounds(question) != null) {
            return "";
        }
        Matcher m = FIS_YEAR_PATTERN.matcher(question);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private boolean mentionsPreviousYear(String question) {
        if (question == null) {
            return false;
        }
        return question.indexOf("이전년도") > -1 || question.indexOf("전년도") > -1
                || question.indexOf("작년") > -1 || question.indexOf("전년") > -1
                || question.indexOf("이전 연도") > -1 || question.indexOf("이전연도") > -1;
    }

    private boolean looksLikeReportQuestion(String question) {
        if (question == null) {
            return false;
        }
        return question.indexOf("사업") > -1 || question.indexOf("심사조서") > -1
                || question.indexOf("경상") > -1 || question.indexOf("투자") > -1
                || question.indexOf("조서") > -1 || question.indexOf("찾아") > -1
                || question.indexOf("검색") > -1 || question.indexOf("검토의견") > -1
                || question.indexOf("반영") > -1 || question.indexOf("요구내용") > -1;
    }

    private JSONObject enrichPlanForReport(String question, JSONObject plan) {
        JSONObject enriched = plan == null ? new JSONObject() : JSONObject.fromObject(plan);
        enriched.put("mode", "report");
        if (hasDeptBracketFilter(question)) {
            String deptKw = extractDeptKeyword(question);
            if (deptKw.length() > 0) {
                enriched.put("deptKeyword", deptKw);
            }
        } else {
            enriched.put("deptKeyword", "");
        }
        if (isExplicitContentFieldSearch(question)) {
            ContentSearchInfo contentSearch = extractContentSearch(question);
            if (contentSearch.field.length() > 0) {
                enriched.put("contentField", contentSearch.field);
            }
            if (contentSearch.keyword.length() > 0) {
                enriched.put("contentKeyword", contentSearch.keyword);
            }
            enriched.put("bizKeyword", "");
            enriched.put("implKeyword", "");
        } else if (enriched.optString("bizKeyword", "").trim().length() == 0) {
            String ruleKw = extractBizKeywordFromQuestion(question);
            if (ruleKw.length() > 0) {
                enriched.put("bizKeyword", ruleKw);
            }
        }
        if (enriched.optString("fisYear", "").trim().length() == 0) {
            int[] range = extractYearRangeBounds(question);
            if (range != null) {
                enriched.put("fisYear", String.valueOf(range[0]) + "~" + String.valueOf(range[1]));
            } else {
                String year = extractFisYearFromQuestion(question);
                if (year.length() > 0) {
                    enriched.put("fisYear", year);
                }
            }
        }
        if (enriched.optString("reportCd", "").trim().length() == 0) {
            if (question.indexOf("투자") > -1 && question.indexOf("경상") == -1) {
                enriched.put("reportCd", "020");
            } else if (question.indexOf("경상") > -1 && question.indexOf("투자") == -1) {
                enriched.put("reportCd", "010");
            }
        }
        return enriched;
    }

    private String resolveBizKeyword(String question, String planKeyword) {
        String ruleKw = extractBizKeywordFromQuestion(question);
        if (planKeyword.length() > 0) {
            if (ruleKw.length() > 0 && !containsKeyword(planKeyword, ruleKw)) {
                return planKeyword + "," + ruleKw;
            }
            return planKeyword;
        }
        return ruleKw;
    }

    private boolean containsKeyword(String haystack, String needle) {
        String[] parts = haystack.split("[,;]");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].trim().equals(needle.trim())) {
                return true;
            }
        }
        return haystack.indexOf(needle) > -1;
    }

    private String extractBizKeywordFromQuestion(String question) {
        if (question == null || isExplicitContentFieldSearch(question)) {
            return "";
        }
        String q = stripDeptBracketClause(question);
        List<String> found = new ArrayList<String>();
        for (int i = 0; i < BIZ_KEYWORD_PATTERNS.length; i++) {
            Matcher m = BIZ_KEYWORD_PATTERNS[i].matcher(q);
            while (m.find()) {
                String raw = m.group(1).trim();
                if (raw.indexOf(",") > -1 || raw.indexOf(";") > -1) {
                    String[] parts = raw.split("[,;]");
                    for (int j = 0; j < parts.length; j++) {
                        addBizKeywordCandidate(found, parts[j]);
                    }
                } else {
                    addBizKeywordCandidate(found, raw);
                }
            }
        }
        if (found.isEmpty()) {
            String loose = extractLooseBizKeyword(q);
            if (loose.length() > 0) {
                addBizKeywordCandidate(found, loose);
            }
        }
        if (found.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < found.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(found.get(i));
        }
        return sb.toString();
    }

    /**
     * 패턴 매칭 실패 시 남은 문장에서 사업명 후보 1개 추출.
     * [소관부서]부서명 제거 후 "유가보조금 찾아줘" 같은 짧은 질문용.
     */
    private String extractLooseBizKeyword(String q) {
        if (q == null || q.trim().length() == 0) {
            return "";
        }
        String s = q.replaceAll("\\d{4}\\s*년(?:도)?", " ");
        s = s.replaceAll("(?:경상|투자)\\s*사업(?:\\s*및\\s*(?:경상|투자)\\s*사업)?", " ");
        s = s.replaceAll("(?:심사조서|조서|예산|회계연도|차수)", " ");
        Matcher quoted = Pattern.compile("[\"'「]([^\"'」]+)[\"'」]").matcher(s);
        if (quoted.find()) {
            String kw = cleanLooseBizToken(quoted.group(1));
            if (kw.length() >= 2) {
                return kw;
            }
        }
        Matcher beforeVerb = Pattern.compile(
                "([\\uAC00-\\uD7A3][\\uAC00-\\uD7A3\\w\\s]{0,30}?)\\s*(?:찾아|검색|정리|알려|해줘|주세요)").matcher(s);
        if (beforeVerb.find()) {
            String kw = cleanLooseBizToken(beforeVerb.group(1));
            if (kw.length() >= 2) {
                return kw;
            }
        }
        Matcher tokens = Pattern.compile("[\\uAC00-\\uD7A3\\w]{2,}").matcher(s);
        String best = "";
        while (tokens.find()) {
            String tok = cleanLooseBizToken(tokens.group());
            if (tok.length() >= 2 && !isBizStopWord(tok) && !isDeptStopWord(tok) && !isYearToken(tok)
                    && tok.length() > best.length()) {
                best = tok;
            }
        }
        return best;
    }

    private String cleanLooseBizToken(String raw) {
        if (raw == null) {
            return "";
        }
        String kw = raw.trim();
        kw = kw.replaceAll("(?:사업|을|를|인|으로|에서|관련|찾아|검색|정리|알려|해줘|주세요|및|에서)$", "").trim();
        kw = kw.replaceAll("^(?:에서|중|의|에)\\s*", "").trim();
        return kw;
    }

    private void addBizKeywordCandidate(List<String> found, String raw) {
        if (raw == null) {
            return;
        }
        String kw = raw.trim();
        kw = kw.replaceAll("(?:사업|을|를|인|으로|에서|관련|찾아|검색|정리)$", "").trim();
        if (kw.length() < 2 || isBizStopWord(kw) || isYearToken(kw)) {
            return;
        }
        for (int i = 0; i < found.size(); i++) {
            if (found.get(i).equals(kw)) {
                return;
            }
        }
        found.add(kw);
    }

    private boolean isBizStopWord(String word) {
        for (int i = 0; i < BIZ_STOP_WORDS.length; i++) {
            if (word.equals(BIZ_STOP_WORDS[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isYearToken(String word) {
        return word.matches("\\d{4}");
    }

    /** LIKE 검색 패턴 — 영문 대·소문자 구분 없음 (한글은 그대로) */
    private String toCaseInsensitiveLike(String keyword) {
        if (keyword == null) {
            return "%";
        }
        String kw = keyword.trim();
        if (kw.length() == 0) {
            return "%";
        }
        return "%" + kw.toUpperCase(Locale.ENGLISH) + "%";
    }

    private void addCaseInsensitiveLikeArgs(List<Object> args, String keyword, int count) {
        String like = toCaseInsensitiveLike(keyword);
        for (int i = 0; i < count; i++) {
            args.add(like);
        }
    }

    /**
     * 심사조서 사업 단위 조회 SQL 생성.
     *
     * - 조서 1건 = TB_REPORT010/020 의 (report_cd, report_detl_cd, te_bgt_compo_id)
     * - 금액은 TB_DGRCOMPO 의 통계목 헤더 행(COMPO_LEVEL='1')을 사업 단위로 합산 (원 단위)
     * - [차수별 예산내역] 재원은 TB_DGRCOMPOFRSC.ADJ_DEF_FRSC_AMT 기준 FRSC_AMT1~6
     *   (ReportWrite020.xml 과 동일: 1=시비160, 2=국비110~130, 3=교부세140~150, …)
     * - tot_frsc_amt1~6 은 총사업비(화면 라벨 1=시비·2=국비·3=교부세)이므로 연간 재원 표시에 사용하지 않음
     *
     * @param broadKeyword true 면 사업명 키워드를 검토의견/검색태그까지 확장(OR)하여 재검색
     */
    private String buildReportSql(String reportCd, String fisYear, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword,
            boolean broadKeyword, List<Object> args) {

        boolean both = !"010".equals(reportCd) && !"020".equals(reportCd);

        StringBuilder sb = new StringBuilder();

        // 재원(frsc_amt1~6)은 조회 후 enrichReportRowsWithFrsc()에서 일괄 부여 (연도·차수 무관 동일 로직)
        sb.append("SELECT X.* FROM (\n");
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
                if (kwSql.length() > 0) {
                    kwSql.append(" OR ");
                }
                if (broadKeyword) {
                    kwSql.append("UPPER(W.comp_ground) LIKE ? OR UPPER(W.dbiz_nm) LIKE ? OR UPPER(W.te_mng_mok_nm) LIKE ?");
                    kwSql.append(" OR UPPER(W.ubiz_nm) LIKE ? OR UPPER(W.pbiz_nm) LIKE ? OR UPPER(W.fis_fg_nm) LIKE ?");
                    kwSql.append(" OR UPPER(W.srch_val) LIKE ? OR UPPER(W.demand_cont) LIKE ? OR UPPER(W.exam_cont) LIKE ?");
                    addCaseInsensitiveLikeArgs(args, kw, 9);
                } else {
                    kwSql.append("UPPER(W.comp_ground) LIKE ? OR UPPER(W.dbiz_nm) LIKE ? OR UPPER(W.te_mng_mok_nm) LIKE ?");
                    kwSql.append(" OR UPPER(W.ubiz_nm) LIKE ? OR UPPER(W.pbiz_nm) LIKE ? OR UPPER(W.fis_fg_nm) LIKE ?");
                    addCaseInsensitiveLikeArgs(args, kw, 6);
                }
            }
            if (kwSql.length() > 0) {
                sb.append("  AND (").append(kwSql).append(")\n");
            }
        }
        if (deptKeyword.length() > 0) {
            String[] deptKws = deptKeyword.split("[,;]");
            StringBuilder deptSql = new StringBuilder();
            for (int i = 0; i < deptKws.length; i++) {
                String kw = deptKws[i].trim();
                if (kw.length() == 0) {
                    continue;
                }
                String like = toCaseInsensitiveLike(kw);
                if (deptSql.length() > 0) {
                    deptSql.append(" OR ");
                }
                deptSql.append("(UPPER(W.dept_nm) LIKE ? OR UPPER(W.office_nm) LIKE ?");
                deptSql.append(" OR UPPER(TRIM(CONCAT(NVL(W.office_nm,''), ' ', NVL(W.dept_nm,'')))) LIKE ?)");
                args.add(like);
                args.add(like);
                args.add(like);
            }
            if (deptSql.length() > 0) {
                sb.append("  AND (").append(deptSql).append(")\n");
            }
        }
        if (tagKeyword.length() > 0) {
            sb.append("  AND UPPER(W.srch_val) LIKE ?\n");
            args.add(toCaseInsensitiveLike(tagKeyword.replaceAll("#", "")));
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
                if (implSql.length() > 0) {
                    implSql.append(" OR ");
                }
                // gubun = demand_cont(심사조서 [구분] 목록의 요구내용)
                implSql.append("UPPER(W.gubun) LIKE ? OR UPPER(W.demand_cont) LIKE ? OR UPPER(W.invest_plan) LIKE ?");
                addCaseInsensitiveLikeArgs(args, kw, 3);
            }
            if (implSql.length() > 0) {
                sb.append("  AND (").append(implSql).append(")\n");
            }
        }

        if (contentKeyword != null && contentKeyword.length() > 0) {
            String like = toCaseInsensitiveLike(contentKeyword);
            if (CONTENT_FIELD_EXAM.equals(contentField)) {
                sb.append("  AND UPPER(W.exam_cont) LIKE ?\n");
                args.add(like);
            } else if (CONTENT_FIELD_GUBUN.equals(contentField)) {
                sb.append("  AND (UPPER(W.gubun) LIKE ? OR UPPER(W.demand_cont) LIKE ? OR UPPER(W.invest_plan) LIKE ?)\n");
                args.add(like);
                args.add(like);
                args.add(like);
            }
        }

        // 같은 사업의 차수별 행이 인접하도록 사업 기준으로 정렬 (차수는 마지막)
        sb.append("ORDER BY W.office_nm, W.dept_nm, W.comp_ground, W.te_mng_mok_cd, W.bgt_dgr\n");
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
        sb.append("     , NVL(C.comp_ground, '') AS comp_ground\n");
        sb.append("     , NVL(B.fis_fg_nm, '') AS fis_fg_nm\n");
        sb.append("     , C.te_mng_mok_cd AS te_mng_mok_cd\n");
        sb.append("     , C.te_mng_mok_nm AS te_mng_mok_nm\n");
        sb.append("     , C.dept_cd AS dept_cd\n");
        sb.append("     , C.dbiz_cd AS dbiz_cd\n");
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
        } else {
            sb.append("     , 0 AS tot_biz_amt\n");
        }
        sb.append("  FROM ").append(table).append(" R\n");
        sb.append("     , (SELECT fis_year, bgt_dgr, te_bgt_compo_id\n");
        sb.append("             , MIN(dept_cd) AS dept_cd\n");
        sb.append("             , MIN(dbiz_cd) AS dbiz_cd\n");
        sb.append("             , MIN(te_mng_mok_cd) AS te_mng_mok_cd\n");
        sb.append("             , MIN(te_mng_mok_nm) AS te_mng_mok_nm\n");
        sb.append("             , MIN(comp_ground) AS comp_ground\n");
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
        sb.append("- 1번: 세세사업명 ( 통계목코드) 형식. 세부사업명 표시 금지. 예) 지역사랑상품권 인센티브 보상금 ( 308-13)\n");
        sb.append("- 3번: [차수별 예산내역] 앞=조정액 총합(bgt_amt), 괄호=조정액 재원(ADJ_DEF_FRSC_AMT). 예) 본예산:200백만원(국비140, 시비60). '조정' 문구 붙이지 말 것.\n");
        sb.append("- 요구액·전년도예산은 사용자가 명시했을 때만 3번에 추가(앞 숫자는 여전히 조정액 총합).\n");
        sb.append("- 검토의견은 차수당 1~2문장 핵심 요약. 원문 전체 복사 금지.\n");
        sb.append("- 제공된 데이터의 모든 사업을 동일 4항목 서식으로 빠짐없이 표시. 생략·요약 목록으로 대체하지 말 것.\n");

        String explicitHint = buildExplicitFieldHint(question);
        if (explicitHint.length() > 0) {
            sb.append("- ").append(explicitHint).append("\n");
        }
        if (containsImplOrgQuestion(question) || CONTENT_FIELD_GUBUN.equals(detectExplicitContentField(question))) {
            sb.append("- [구분] 관련 질문이므로 [구분]을 2번과 3번 사이에 간략히 추가.\n");
        } else {
            sb.append("- [구분][조건검색어][총사업비][전년도예산][요구액] 등은 표시하지 말 것.\n");
        }
        if (CONTENT_FIELD_EXAM.equals(detectExplicitContentField(question))) {
            sb.append("- [검토내용] 검색 질문이므로 4번 검토의견에 해당 키워드가 반영된 내용을 요약.\n");
        }

        sb.append("\n[사용자 질문]\n").append(question).append("\n");
        sb.append("\n[심사조서 데이터]\n").append(context).append("\n");
        return sb.toString();
    }

    /** 질문 유형에 따른 AI 컨텍스트 포함 옵션 (토큰·비용 절감) */
    private AiReportContextBuilder.ContextOptions buildContextOptions(String question, String planFisYear,
            String tagKeyword, String contentField) {
        AiReportContextBuilder.ContextOptions opts = AiReportContextBuilder.ContextOptions.defaults();
        opts.includeGubun = containsImplOrgQuestion(question)
                || CONTENT_FIELD_GUBUN.equals(contentField);
        opts.includePreYearAmt = wantsPreYearAmt(question);
        opts.includeDemandAmt = wantsDemandAmt(question);
        opts.includeTags = tagKeyword != null && tagKeyword.trim().length() > 0;
        opts.multiYearRangeQuery = extractYearRangeBounds(question) != null
                || parseYearRangeLabel(planFisYear) != null
                || mentionsAllYearsSearch(question);
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

    /** [구분]/[검토내용] 비정형 필드 검색 정보 */
    private static class ContentSearchInfo {
        String field = "";
        String keyword = "";
    }

    private ContentSearchInfo resolveContentSearch(String question, String planKeyword) {
        ContentSearchInfo info = extractContentSearch(question);
        if (info.field.length() == 0) {
            return info;
        }
        if (info.keyword.length() == 0 && planKeyword.length() > 0) {
            info.keyword = cleanContentKeyword(planKeyword);
        }
        return info;
    }

    private ContentSearchInfo extractContentSearch(String question) {
        ContentSearchInfo info = new ContentSearchInfo();
        if (question == null) {
            return info;
        }
        info.field = detectExplicitContentField(question);
        if (info.field.length() == 0) {
            return info;
        }
        info.keyword = extractContentKeyword(question, info.field);
        return info;
    }

    /** [소관부서] 대괄호가 있으면 부서·실국 필터 적용 (단독 검색 아님) */
    private boolean hasDeptBracketFilter(String question) {
        return question != null && question.indexOf(BRACKET_MARKER_DEPT) > -1;
    }

    /**
     * [소관부서] 지정 + 사업명/비정형/시행주관 검색 의도가 함께 있는 경우.
     * 이때는 부서 단독 폴백 검색을 하지 않는다.
     */
    private boolean hasDeptBracketWithNonDeptSearchIntent(String question, boolean explicitContent,
            String bizKeyword, String ruleBizKeyword) {
        if (!hasDeptBracketFilter(question)) {
            return false;
        }
        if (explicitContent) {
            return true;
        }
        if (bizKeyword != null && bizKeyword.trim().length() > 0) {
            return true;
        }
        if (ruleBizKeyword != null && ruleBizKeyword.trim().length() > 0) {
            return true;
        }
        if (containsImplOrgQuestion(question)) {
            return true;
        }
        String loose = extractLooseBizKeyword(stripDeptBracketClause(question));
        return loose.length() > 0;
    }

    /** 사업명 키워드 목록에서 부서명 토큰 제거 (LLM이 부서명을 bizKeyword에 넣은 경우) */
    private String removeDeptTokensFromKeywordCsv(String csv, String deptKeyword) {
        if (csv == null || csv.trim().length() == 0) {
            return "";
        }
        if (deptKeyword == null || deptKeyword.trim().length() == 0) {
            return csv.trim();
        }
        String[] deptParts = deptKeyword.split("[,;]");
        String[] bizParts = csv.split("[,;]");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bizParts.length; i++) {
            String kw = bizParts[i].trim();
            if (kw.length() == 0) {
                continue;
            }
            boolean isDeptToken = false;
            for (int j = 0; j < deptParts.length; j++) {
                String dept = deptParts[j].trim();
                if (dept.length() == 0) {
                    continue;
                }
                if (kw.equals(dept) || kw.indexOf(dept) > -1 || dept.indexOf(kw) > -1) {
                    isDeptToken = true;
                    break;
                }
            }
            if (!isDeptToken) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(kw);
            }
        }
        return sb.toString();
    }

    /**
     * 사업명 추출 시 [소관부서]부서명 절을 제거한다.
     * 예) "2026년 [소관부서]복지국 유가보조금 사업" → "2026년  유가보조금 사업"
     */
    private String stripDeptBracketClause(String question) {
        if (!hasDeptBracketFilter(question)) {
            return question;
        }
        String deptKw = extractDeptKeyword(question);
        if (deptKw.length() > 0) {
            String pattern = Pattern.quote(BRACKET_MARKER_DEPT)
                    + "\\s*['\"'「]?" + Pattern.quote(deptKw) + "['\"'」]?"
                    + "\\s*(?:에서|에|인|이|은|는|으로)?\\s*";
            String stripped = question.replaceFirst(pattern, " ");
            if (!stripped.equals(question)) {
                return stripped;
            }
        }
        int idx = question.indexOf(BRACKET_MARKER_DEPT);
        String tail = question.substring(idx + BRACKET_MARKER_DEPT.length());
        Matcher m = Pattern.compile("^\\s*['\"'「]?[\\uAC00-\\uD7A3A-Za-z0-9()（）\\-\\.]+['\"'」]?\\s*(?:에서|에)?\\s*")
                .matcher(tail);
        if (m.find()) {
            return question.substring(0, idx) + " " + tail.substring(m.end());
        }
        return question.replace(BRACKET_MARKER_DEPT, " ");
    }

    private String resolveDeptKeyword(String question, String planKeyword) {
        String ruleKw = extractDeptKeyword(question);
        if (planKeyword.length() > 0) {
            if (ruleKw.length() > 0 && !containsKeyword(planKeyword, ruleKw)) {
                return planKeyword + "," + ruleKw;
            }
            return planKeyword;
        }
        return ruleKw;
    }

    private String extractDeptKeyword(String question) {
        if (!hasDeptBracketFilter(question)) {
            return "";
        }
        String tail = question.substring(question.indexOf(BRACKET_MARKER_DEPT) + BRACKET_MARKER_DEPT.length());

        Matcher quoted = DEPT_QUOTED_PATTERN.matcher(tail);
        if (quoted.find()) {
            String kw = cleanDeptKeyword(quoted.group(1));
            if (kw.length() >= 2) {
                return kw;
            }
        }

        // [소관부서] 바로 뒤 첫 토큰만 부서명 (사업명과 구분)
        Matcher singleWord = Pattern.compile(
                "^\\s*([\\uAC00-\\uD7A3A-Za-z0-9()（）\\-\\.]+)\\s*(?:에서|에|인|이|은|는|으로)?").matcher(tail);
        if (singleWord.find()) {
            String kw = cleanDeptKeyword(singleWord.group(1));
            if (kw.length() >= 2) {
                return kw;
            }
        }
        return "";
    }

    private String cleanDeptKeyword(String kw) {
        if (kw == null) {
            return "";
        }
        String s = kw.trim();
        s = s.replaceAll("(?:사업|을|를|인|으로|에서|관련|찾아|검색|정리|해줘|주세요|소속)$", "").trim();
        if (s.length() < 2 || isDeptStopWord(s) || isYearToken(s)) {
            return "";
        }
        return s;
    }

    private boolean isDeptStopWord(String word) {
        for (int i = 0; i < DEPT_STOP_WORDS.length; i++) {
            if (DEPT_STOP_WORDS[i].equals(word)) {
                return true;
            }
        }
        return false;
    }

    /** [구분]/[검토내용] 대괄호 지정 여부 — 이 경우에만 비정형 필드 검색 */
    private boolean isExplicitContentFieldSearch(String question) {
        return detectExplicitContentField(question).length() > 0;
    }

    /** 질문에 [검토내용] 또는 [구분] 대괄호 표기가 있는지 (둘 다 있으면 먼저 나온 쪽) */
    private String detectExplicitContentField(String question) {
        if (question == null) {
            return "";
        }
        int examIdx = question.indexOf(BRACKET_MARKER_EXAM);
        int gubunIdx = question.indexOf(BRACKET_MARKER_GUBUN);
        if (examIdx < 0 && gubunIdx < 0) {
            return "";
        }
        if (examIdx >= 0 && (gubunIdx < 0 || examIdx <= gubunIdx)) {
            return CONTENT_FIELD_EXAM;
        }
        return CONTENT_FIELD_GUBUN;
    }

    private String getBracketMarker(String field) {
        return CONTENT_FIELD_EXAM.equals(field) ? BRACKET_MARKER_EXAM : BRACKET_MARKER_GUBUN;
    }

    private String extractContentKeyword(String question, String field) {
        if (question == null || field == null || field.length() == 0) {
            return "";
        }
        String marker = getBracketMarker(field);
        int idx = question.indexOf(marker);
        if (idx < 0) {
            return "";
        }
        String tail = question.substring(idx + marker.length());
        for (int i = 0; i < CONTENT_KEYWORD_PATTERNS.length; i++) {
            Matcher m = CONTENT_KEYWORD_PATTERNS[i].matcher(tail);
            if (m.find()) {
                String kw = cleanContentKeyword(m.group(1));
                if (kw.length() >= 1) {
                    return kw;
                }
            }
        }
        return "";
    }

    private String cleanContentKeyword(String kw) {
        if (kw == null) {
            return "";
        }
        String s = kw.trim();
        s = s.replaceAll("(?:사업|을|를|인|으로|에서|관련|찾아|검색|정리|해줘|주세요)$", "").trim();
        if (isBizStopWord(s) || isYearToken(s) || isImplOrgWord(s)) {
            return "";
        }
        return s;
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
        sb.append("※ 가장 중요: bizKeyword(사업명 키워드). 사용자 질문의 핵심 명사를 반드시 추출할 것.\n");
        sb.append("- fisYear: 질문 속 회계연도 4자리 (예: \"2026\"). 여러 연도 범위면 \"시작~끝\" (예: \"2017~2020\", \"2023~2026\"). 없으면 빈 문자열.\n");
        sb.append("- dgr: 차수 표현. \"본예산\" 또는 \"N회추경\" (예: \"1회추경\"). 구분이 없으면 빈 문자열(모든 차수).\n");
        sb.append("- reportCd: 경상사업 관련이면 \"010\", 투자사업 관련이면 \"020\", 구분 없으면 빈 문자열(둘 다).\n");
        sb.append("- bizKeyword: 사업명·분야에서 검색할 핵심 키워드. 여러 개면 쉼표로 구분 (예: \"유가보조금,유류비\"). 가장 중요.\n");
        sb.append("  (예: '2025년 유가보조금 관련 사업 찾아줘' → bizKeyword: \"유가보조금\")\n");
        sb.append("  ※ '검토의견 정리해줘'는 답변 요청이지 bizKeyword가 아님. 사업명 키워드를 bizKeyword에 넣을 것.\n");
        sb.append("- deptKeyword: [소관부서]대괄호 지정 시 부서·실국명 필터 (예: \"복지국\"). 사업명·비정형 검색 결과를 해당 부서로 한정.\n");
        sb.append("- tagKeyword: #으로 시작하는 검색태그 키워드 (예: \"민생\"). 없으면 빈 문자열.\n");
        sb.append("- implKeyword: 시행주관·시행주체·시행처·시행기관·사업기관 관련 질문일 때 [구분](요구내용)에서 검색할 기관/주체 키워드. 없으면 빈 문자열.\n");
        sb.append("  (예: '테크노파크가 시행주관인 사업' → implKeyword: \"테크노파크\", bizKeyword: \"\")\n");
        sb.append("  ※ 시행주관 질문의 기관명은 bizKeyword가 아니라 implKeyword로 넣을 것.\n");
        sb.append("- contentField: 사용자가 **[구분]** 이라고 대괄호로 지정했을 때만 \"gubun\", **[검토내용]** 지정 시만 \"exam\". 없으면 빈 문자열.\n");
        sb.append("- contentKeyword: [구분]/[검토내용] 지정 시 해당 목록 문장에서 찾을 키워드(예: \"마무리\", \"테크노파크\"). 없으면 빈 문자열.\n");
        sb.append("  (예: '[검토내용]에 마무리 사업' → contentField: \"exam\", contentKeyword: \"마무리\", bizKeyword: \"\")\n");
        sb.append("  (예: '[구분]에 테크노파크가 시행처인 사업' → contentField: \"gubun\", contentKeyword: \"테크노파크\", bizKeyword: \"\")\n");
        sb.append("  ※ '검토내용', '구분' 단어만 있고 대괄호 [ ] 가 없으면 비정형 검색 아님. 반드시 [검토내용] 또는 [구분] 표기.\n");
        sb.append("\n[sql 모드 규칙]\n");
        sb.append("- 반드시 조회(SELECT) 질의만 생성. INSERT/UPDATE/DELETE/DDL 절대 금지.\n");
        sb.append("- CUBRID SQL 문법. 행 수 제한은 LIMIT 사용. 존재하는 테이블/컬럼만 사용.\n");
        sb.append("- 금액 집계는 SUM 등 집계함수 사용. DB 금액 원시 단위는 '원'이다.\n");
        sb.append("\n");
        sb.append(aiSchemaProvider.getSchemaText());
        sb.append("\n[출력 형식] 아래 JSON 한 개만 출력하고 다른 설명은 하지 마세요.\n");
        sb.append("{\"mode\": \"report|sql|chat\", \"fisYear\": \"\", \"dgr\": \"\", \"reportCd\": \"\", \"bizKeyword\": \"\", \"deptKeyword\": \"\", \"tagKeyword\": \"\", \"implKeyword\": \"\", \"contentField\": \"\", \"contentKeyword\": \"\", \"needData\": true, \"sql\": \"(sql 모드일 때 SELECT 문)\", \"answer\": \"(chat 모드일 때 한국어 답변)\"}\n");
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

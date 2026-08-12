package com.cs.bcjis.ai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

import javax.annotation.Resource;
import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.cs.bcjis.ai.AiBugiGovDataClient;
import com.cs.bcjis.ai.AiBusanHomepageClient;
import com.cs.bcjis.ai.AiExternalSourceFetcher;
import com.cs.bcjis.ai.AiKeywordMatcher;
import com.cs.bcjis.ai.AiLawGoKrClient;
import com.cs.bcjis.ai.AiManualDocService;
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

    /**
     * 내부검색 조서구분·연도구간·재원 조회 병렬용 (daemon).
     * 동시 조회 수는 DB 커넥션 풀을 넘지 않도록 제한한다.
     * 기동 옵션 -Dbcjis.ai.dbPoolSize=N 으로 조정 가능.
     */
    private static final ExecutorService AI_DB_POOL =
            Executors.newFixedThreadPool(resolveDbPoolSize(), new ThreadFactory() {
        private final AtomicInteger n = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ai-db-" + n.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    });

    private static int resolveDbPoolSize() {
        int size = 6;
        try {
            String v = System.getProperty("bcjis.ai.dbPoolSize");
            if (v != null && v.trim().length() > 0) {
                size = Integer.parseInt(v.trim());
            }
        } catch (Exception e) {
            size = 6;
        }
        if (size < 2) {
            size = 2;
        }
        if (size > 16) {
            size = 16;
        }
        return size;
    }

    @Resource(name = "bcjis.dataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "aiLlmClient")
    private LlmClient llmClient;

    @Resource(name = "aiSchemaProvider")
    private AiSchemaProvider aiSchemaProvider;

    @Resource(name = "aiExternalSourceFetcher")
    private AiExternalSourceFetcher aiExternalSourceFetcher;

    @Resource(name = "aiLawGoKrClient")
    private AiLawGoKrClient aiLawGoKrClient;

    @Resource(name = "aiBugiGovDataClient")
    private AiBugiGovDataClient aiBugiGovDataClient;

    @Resource(name = "aiBusanHomepageClient")
    private AiBusanHomepageClient aiBusanHomepageClient;

    @Resource(name = "aiManualDocService")
    private AiManualDocService aiManualDocService;

    /** 내부자료 검색 최소 회계년도 */
    private static final int MIN_FIS_YEAR = 2013;

    /** TB_BGTDGR 최대 회계연도 — 요청마다 반복 조회 방지 */
    private volatile String cachedMaxFisYear = "";

    /** TB_FISYEAR 전체 회계연도 — 요청마다 반복 조회 방지 */
    private volatile List<String> cachedAllFisYears = null;

    private static final String REPORT_ANSWER_STYLE =
            "[답변 문체 — 보고서 음슴체]\n"
            + "- 자료 원문의 용어·표기·순서를 우선 사용\n"
            + "- 개조식: '○', '-', '·', ':', '→', '※' 등으로 항목 나열\n"
            + "- 종결은 음슴체(함/임/됨/음/없음)만 사용. '~습니다/~입니다/~합니다/~됩니다' 금지\n"
            + "- '먼저', '또한', '마지막으로', '다음과 같은', '이러한 과정은' 등 서술형 연결문구 금지\n"
            + "- 서두·맺음말·해설 문장 금지. 핵심 기준·금액·시기·제출기한만 노출\n";

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
            + "- 제공 데이터에 없는 내용 지어내지 말 것.\n"
            + "- 서술형 존댓말(~입니다/~합니다/~습니다) 금지. 종결은 음슴체(함/임/됨/음)로 개조식 보고서 문체 사용.";

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

    /** 회계연도 미지정 시 TB_FISYEAR 전체 연도 순차 검색(7차 폴백). 운영은 false 권장. */
    private boolean isAllYearsFallbackEnabled() {
        try {
            String v = config.getProperty("Globals.AiSearchAllYearsFallback", "false");
            return "true".equalsIgnoreCase(v.trim());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 사업명 넓은 검색(검토의견·요구내용 CLOB LIKE). 운영 대용량 DB에서는 매우 느림.
     * 기본 false — 사업명·세부사업명으로 못 찾을 때만 수동으로 true.
     */
    private boolean isBroadSearchEnabled() {
        try {
            String v = config.getProperty("Globals.AiEnableBroadSearch", "false");
            return "true".equalsIgnoreCase(v.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPerfLogEnabled() {
        try {
            String v = config.getProperty("Globals.AiPerfLog", "true");
            return !"false".equalsIgnoreCase(v.trim());
        } catch (Exception e) {
            return true;
        }
    }

    private void logPerf(String tag, long startMs, String detail) {
        if (isPerfLogEnabled()) {
            logger.info("AI PERF[" + tag + "] ms=" + (System.currentTimeMillis() - startMs) + " " + detail);
        }
    }

    private int getMaxReportBlocks() {
        int v = getIntProp("Globals.AiMaxReportBlocks", -1);
        if (v > 0) {
            return v;
        }
        return getIntProp("Globals.GeminiMaxReportBlocks", 100);
    }

    /**
     * 조회·표시 원시 행(차수) 상한. 사업 하나가 여러 차수(행)로 나뉘므로
     * 사업 상한(getMaxReportBlocks)의 몇 배로 넉넉히 잡아 100개 사업이 잘리지 않게 한다.
     */
    private int getMaxReportRows() {
        int v = getIntProp("Globals.AiMaxReportRows", -1);
        if (v > 0) {
            return v;
        }
        return getMaxReportBlocks() * 6;
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

    private String getStringProp(String key, String defaultValue) {
        try {
            if (config == null) {
                return defaultValue;
            }
            String v = config.getProperty(key);
            if (v != null && v.trim().length() > 0) {
                return v.trim();
            }
        } catch (Exception e) {
            // ignore
        }
        return defaultValue;
    }

    public JSONObject ask(String question) throws Exception {
        JSONObject params = new JSONObject();
        params.put("question", question == null ? "" : question);
        return ask(params);
    }

    public JSONObject getMeta() throws Exception {
        JSONObject meta = new JSONObject();
        meta.put("minFisYear", String.valueOf(MIN_FIS_YEAR));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        meta.put("latestFisYear", getMaxFisYear(jdbcTemplate));
        try {
            JSONObject manuals = aiManualDocService.listManuals();
            meta.put("manualCanManage", manuals.optBoolean("canManage", false));
            meta.put("manualAdminOnly", manuals.optBoolean("adminOnly", false));
            meta.put("manualFiles", manuals.optJSONArray("files"));
        } catch (Exception e) {
            meta.put("manualCanManage", Boolean.FALSE);
            meta.put("manualFiles", new JSONArray());
        }
        meta.put("lawApiReady", Boolean.valueOf(aiLawGoKrClient.isEnabled()));
        meta.put("bugiApiReady", Boolean.valueOf(aiBugiGovDataClient.hasApiKey()));
        return meta;
    }

    public JSONObject listManuals() throws Exception {
        return aiManualDocService.listManuals();
    }

    public JSONObject uploadManual(javax.servlet.http.HttpServletRequest request) throws Exception {
        return aiManualDocService.uploadManual(request);
    }

    public JSONObject deleteManual(String id) throws Exception {
        return aiManualDocService.deleteManual(id);
    }

    /**
     * 내부자료검색 DB(심사조서 010/020)를 지정 회계년도 1년분만 모바일 뷰어용 JSON으로 조립한다.
     */
    public JSONObject exportInternalData(String fisYear) throws Exception {
        JSONObject out = new JSONObject();
        if (fisYear == null) {
            fisYear = "";
        }
        fisYear = fisYear.trim();
        if (!fisYear.matches("^[0-9]{4}$")) {
            throw new IllegalArgumentException("회계년도는 4자리 숫자여야 합니다.");
        }

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String yearErr = validateFisYearRange(fisYear, fisYear, jdbcTemplate);
        if (yearErr.length() > 0) {
            throw new IllegalArgumentException(yearErr);
        }

        // 0 이하 = 한도 없음 (필터에 맞는 전체 추출)
        int exportMaxRows = getIntProp("Globals.AiInternalExportMaxRows", 0);
        int exportMaxBiz = getIntProp("Globals.AiInternalExportMaxBiz", 0);
        int qTimeout = Math.max(180, getIntProp("Globals.AiQueryTimeoutSec", 45) + 120);
        if (exportMaxRows > 0) {
            jdbcTemplate.setMaxRows(exportMaxRows);
        }
        if (qTimeout > 0) {
            jdbcTemplate.setQueryTimeout(qTimeout);
        }

        List<String> years = new ArrayList<String>();
        years.add(fisYear);

        // checkbox != null 이면 CLOB 미조회(후속 fill). active()=false 이면 키워드 필터 없음 → 해당 연도 전체
        final CheckboxSearch exportChk = new CheckboxSearch("", true, false, false, false);
        long t0 = System.currentTimeMillis();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String[] reportCds = new String[] { "010", "020" };
        for (int r = 0; r < reportCds.length; r++) {
            List<Object> args = new ArrayList<Object>();
            String sql = buildReportSql(reportCds[r], years, "", 0,
                    "", "", "", "", "", "", false, exportChk, args, exportMaxRows);
            List<Map<String, Object>> part = queryReport(jdbcTemplate, sql, args);
            if (part != null) {
                rows.addAll(part);
            }
        }
        if (!rows.isEmpty()) {
            sortReportRowsByYearAndBiz(rows);
            if (exportMaxRows > 0 && rows.size() > exportMaxRows) {
                rows = new ArrayList<Map<String, Object>>(rows.subList(0, exportMaxRows));
            }
            if (exportMaxBiz > 0) {
                rows = AiReportContextBuilder.trimRowsToMaxBizGroups(rows, exportMaxBiz);
            }
        }
        logPerf("internalExportQuery", t0, "year=" + fisYear + " rows=" + rows.size());

        if (rows.isEmpty()) {
            out.put("ok", Boolean.FALSE);
            out.put("error", "해당 회계년도의 내부자료(심사조서)가 없습니다.");
            out.put("fisYear", fisYear);
            out.put("bizCount", Integer.valueOf(0));
            out.put("rowCount", Integer.valueOf(0));
            return out;
        }

        // 연도 단위 1회 조회로 본문·재원을 채운다. (100건 배치+타임아웃 재시도는 수분 지연)
        try {
            fillExportTextColumns(jdbcTemplate, rows, fisYear);
        } catch (Exception e) {
            logger.warn("내보내기 본문 보강 실패, 배치 경로로 재시도: " + e.getMessage());
            fillReportTextColumns(jdbcTemplate, rows);
        }
        try {
            enrichExportFrsc(jdbcTemplate, rows, fisYear);
        } catch (Exception e) {
            logger.warn("내보내기 재원 보강 실패(본문은 유지): " + e.getMessage());
        }

        AiReportContextBuilder.ContextOptions ctxOpts = AiReportContextBuilder.ContextOptions.defaults();
        ctxOpts.includeGubun = true;
        ctxOpts.includeDemandAmt = true;
        ctxOpts.maxBlocks = exportMaxBiz > 0 ? exportMaxBiz : Integer.MAX_VALUE;

        java.util.LinkedHashMap<String, List<Map<String, Object>>> multiYearGroups =
                AiReportContextBuilder.groupRowsByBizNameAcrossYears(rows);

        JSONArray businesses = new JSONArray();
        JSONArray dataList = new JSONArray();
        java.util.HashSet<String> detailEmitted = new java.util.HashSet<String>();

        for (java.util.Iterator<java.util.Map.Entry<String, List<Map<String, Object>>>> it =
                multiYearGroups.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry<String, List<Map<String, Object>>> ent = it.next();
            List<Map<String, Object>> g = ent.getValue();
            if (g == null || g.isEmpty()) {
                continue;
            }
            Map<String, Object> first = g.get(0);
            String bizLabel = AiReportContextBuilder.buildBizLabel(first);
            String deptLabel = AiReportContextBuilder.formatDeptLine(first);
            String fisFgNm = AiReportContextBuilder.getStr(first, "fis_fg_nm");
            JSONArray detailRows = toDetailRowsJson(g, ctxOpts);

            JSONObject biz = new JSONObject();
            biz.put("bizNm", bizLabel);
            biz.put("dept", deptLabel);
            biz.put("fisFgNm", fisFgNm);
            biz.put("reportNm", AiReportContextBuilder.getStr(first, "report_nm"));
            biz.put("detailRows", detailRows);
            businesses.add(biz);

            for (int i = 0; i < g.size(); i++) {
                Map<String, Object> row = g.get(i);
                JSONObject obj = new JSONObject();
                String fisYearVal = AiReportContextBuilder.getStr(row, "fis_year");
                String rowBiz = AiReportContextBuilder.buildBizLabel(row);
                String rowDept = AiReportContextBuilder.formatDeptLine(row);
                obj.put("연도", fisYearVal);
                obj.put("사업명", rowBiz);
                obj.put("소관부서", rowDept);
                obj.put("회계", AiReportContextBuilder.getStr(row, "fis_fg_nm"));
                obj.put("차수", AiReportContextBuilder.buildDgrLabel(
                        fisYearVal,
                        AiReportContextBuilder.getStr(row, "bgt_compo_fg"),
                        AiReportContextBuilder.getLong(row, "add_times"),
                        AiReportContextBuilder.getStr(row, "bgt_dgr")));
                obj.put("요구액(백만원)", AiReportContextBuilder.toMillion(
                        AiReportContextBuilder.getLong(row, "demand_bgt_amt")));
                obj.put("조정액(백만원)", AiReportContextBuilder.toMillion(
                        AiReportContextBuilder.getLong(row, "bgt_amt")));
                obj.put("조정재원", AiReportContextBuilder.formatFrscForRow(row));
                obj.put("_detailKey", bizLabel);
                obj.put("_detailTitle", bizLabel);
                if (detailRows != null && !detailRows.isEmpty() && detailEmitted.add(bizLabel)) {
                    obj.put("_detailRows", detailRows);
                }
                dataList.add(obj);
            }
        }

        JSONArray columns = new JSONArray();
        columns.add("연도");
        columns.add("사업명");
        columns.add("소관부서");
        columns.add("회계");
        columns.add("차수");
        columns.add("요구액(백만원)");
        columns.add("조정액(백만원)");
        columns.add("조정재원");

        out.put("ok", Boolean.TRUE);
        out.put("format", "bcjis-ai-internal-export");
        out.put("version", Integer.valueOf(1));
        out.put("fisYear", fisYear);
        out.put("exportedAt", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .format(new java.util.Date()));
        out.put("source", "bcjis-internal");
        out.put("purpose", "mobile-viewer");
        out.put("bizCount", Integer.valueOf(businesses.size()));
        out.put("rowCount", Integer.valueOf(rows.size()));
        out.put("truncated", Boolean.valueOf(
                (exportMaxRows > 0 && rows.size() >= exportMaxRows)
                        || (exportMaxBiz > 0 && businesses.size() >= exportMaxBiz)));
        out.put("columns", columns);
        out.put("dataList", dataList);
        out.put("businesses", businesses);
        logPerf("internalExportBuild", t0, "year=" + fisYear + " biz=" + businesses.size()
                + " rows=" + rows.size());
        return out;
    }

    public JSONObject ask(JSONObject params) throws Exception {
        if (params == null) {
            params = new JSONObject();
        }
        String question = params.optString("question", "").trim();
        JSONObject result = new JSONObject();

        if (question.length() == 0) {
            result.put("answer", "질문을 입력해 주세요.");
            return result;
        }

        boolean searchBizNm = isYnFlag(params.optString("searchBizNm", "N"));
        boolean searchGubun = isYnFlag(params.optString("searchGubun", "N"));
        boolean searchExam = isYnFlag(params.optString("searchExam", "N"));
        boolean searchSrchVal = isYnFlag(params.optString("searchSrchVal", "N"));
        boolean anyInternal = searchBizNm || searchGubun || searchExam || searchSrchVal;

        boolean searchLaw = isYnFlag(params.optString("searchLaw", "N"));
        boolean searchCity = isYnFlag(params.optString("searchCity", "N"));
        boolean searchManual = isYnFlag(params.optString("searchManual", "N"));
        int generalCnt = (searchLaw ? 1 : 0) + (searchCity ? 1 : 0) + (searchManual ? 1 : 0);
        boolean anyGeneral = generalCnt > 0;

        String fisYearFrom = params.optString("fisYearFrom", "").trim();
        String fisYearTo = params.optString("fisYearTo", "").trim();

        if (anyInternal && anyGeneral) {
            result.put("answer", "내부자료 검색과 일반자료 검색은 동시에 선택할 수 없습니다.\n한쪽만 체크한 뒤 다시 검색해 주세요.");
            result.put("aiProvider", "validation");
            return result;
        }
        if (generalCnt > 1) {
            result.put("answer", "일반자료 검색은 법령·조례 / 보도자료,고시공고 / 예산운용지침 중 하나만 선택할 수 있습니다.");
            result.put("aiProvider", "validation");
            return result;
        }

        // 일반자료 검색
        if (searchLaw) {
            return handleGeneralLawSearch(question, result);
        }
        if (searchCity) {
            return handleGeneralCitySearch(question, result);
        }
        if (searchManual) {
            return handleGeneralManualSearch(question, result);
        }

        // 내부자료 검색 체크 ≥1 → 명시적 DB OR 검색 (LLM 질문분류 생략)
        if (anyInternal) {
            return handleCheckboxInternalSearch(question, fisYearFrom, fisYearTo,
                    searchBizNm, searchGubun, searchExam, searchSrchVal, result);
        }

        // 체크 없음 → 외부자료 URL + LLM 추론
        if (params.containsKey("searchBizNm") || params.containsKey("searchGubun")
                || params.containsKey("searchExam") || params.containsKey("searchSrchVal")
                || params.containsKey("searchLaw") || params.containsKey("searchCity")
                || params.containsKey("searchManual")) {
            return handleExternalSearch(question, result);
        }

        // 하위호환: 구 클라이언트(체크 파라미터 없음) → 기존 자연어 RAG
        return askLegacyNaturalLanguage(question, result);
    }

    private JSONObject handleGeneralLawSearch(String keyword, JSONObject result) throws Exception {
        JSONObject api = aiLawGoKrClient.searchLawAndBusanOrdin(keyword);
        if (!api.optBoolean("ok", false)) {
            result.put("answer", api.optString("message", "법령·조례 검색에 실패했습니다."));
            result.put("aiProvider", "law.go.kr");
            return result;
        }
        JSONArray items = api.optJSONArray("items");
        int cnt = api.optInt("count", items == null ? 0 : items.size());
        if (cnt == 0) {
            result.put("answer", "\"" + keyword + "\" 관련 법령·부산광역시 조례를 찾지 못했습니다.");
            result.put("aiProvider", "law.go.kr");
            result.put("generalItems", new JSONArray());
            return result;
        }

        // 조문 본문을 모아 LLM 요약용 컨텍스트 구성 (화면에는 요약 + 출처 링크만)
        StringBuilder bodyCtx = new StringBuilder();
        int bodyCnt = 0;
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject it = items.getJSONObject(i);
                String body = it.optString("body", "");
                if (body.length() > 0) {
                    bodyCnt++;
                    if (bodyCtx.length() < 14000) {
                        bodyCtx.append("[").append(it.optString("kind")).append("] ")
                                .append(it.optString("title")).append("\n")
                                .append(body).append("\n\n");
                    }
                }
                // 출처만 표시: 본문 텍스트는 화면 generalItems에서 제거
                it.put("body", "");
                if (it.optString("sub", "").length() == 0 && body.length() > 0) {
                    it.put("sub", "조문 원문 링크 참고");
                }
            }
        }

        String extractAnswer = buildLawExtractAnswer(keyword, bodyCtx.toString(), api);
        String answer = extractAnswer.length() > 0 ? extractAnswer
                : ("○ 법령·조례 검색 결과: " + cnt + "건\n○ 조례 범위: 부산광역시 본청");
        boolean summaryOk = false;
        int extractCoreLen = extractAnswer.length();
        int joNumForSummary = api.optInt("joNum", 0);

        // 특정 조문(제N조) 조회: 법제처 원문 추출본을 우선 사용
        // (LLM이 짧은 키워드 나열로 축약하던 문제 방지)
        if (joNumForSummary > 0 && extractCoreLen >= 120) {
            answer = extractAnswer;
            result.put("aiProvider", "law.go.kr-extract");
            result.put("answerHighlight", Boolean.TRUE);
            summaryOk = true;
        }

        // 주제 검색 등: LLM으로 읽기 쉽게 다듬되, 짧은 초요약은 버리고 추출본 유지
        if (!summaryOk && llmClient != null && llmClient.isEnabled() && bodyCtx.length() > 0) {
            try {
                StringBuilder prompt = new StringBuilder();
                prompt.append("당신은 부산시 예산편성 도우미입니다.\n");
                prompt.append("아래 [조문 원문]을 읽기 쉬운 개조식으로 재작성하세요.\n");
                prompt.append("중요: 요약·축약이 아니라 항·호·목의 실질 문장 의미를 유지하세요.\n");
                prompt.append("짧은 키워드 나열(예: '법률에 규정 있음')만 쓰지 말고, 각 호의 조건을 문장으로 풀어 쓰세요.\n");
                prompt.append(REPORT_ANSWER_STYLE).append("\n");
                prompt.append("[규칙]\n");
                prompt.append("1. 헤더: 법령명 + 제N조(+제목)\n");
                prompt.append("2. ①항·②항·각 호를 빠짐없이 계층(· / -)으로 정리\n");
                prompt.append("3. 요건·예외·금지·의무·금액·기한·대상을 원문 수준으로 유지 (과도한 축약 금지)\n");
                prompt.append("4. 출력 분량은 원문과 비슷하거나 더 길게. 초요약 금지\n");
                prompt.append("5. 종결은 음슴체(함/임/됨/음). '~습니다' 금지\n");
                prompt.append("6. 조문에 없는 내용 추측 금지\n");
                prompt.append("7. 형식 예시의 짧은 길이를 따라 하지 말 것\n\n");
                prompt.append("[질문] ").append(keyword).append("\n\n");
                prompt.append("[조문 원문]\n");
                if (bodyCtx.length() > 12000) {
                    prompt.append(bodyCtx.substring(0, 12000)).append("\n...(생략)...");
                } else {
                    prompt.append(bodyCtx);
                }
                String summary = llmClient.generateUserQuery(prompt.toString());
                if (summary != null && summary.trim().length() > 40) {
                    String styled = toReportEumseumStyle(summary.trim());
                    int minLen = Math.max(420, (int) (extractCoreLen * 0.70));
                    // 추출본보다 현저히 짧으면 거부 (사용자가 본 짧은 조문요약 방지)
                    boolean hasSubstance = styled.indexOf("①") >= 0 || styled.indexOf("1.") >= 0
                            || styled.indexOf("제1항") >= 0 || countMatches(styled, "\n-") >= 3;
                    if (styled.length() >= minLen && countMatches(styled, "\n") >= 5 && hasSubstance) {
                        answer = "【조문 요약】\n" + styled;
                        result.put("aiProvider", llmClient.getProviderName() + "-law");
                        result.put("answerHighlight", Boolean.TRUE);
                        summaryOk = true;
                    } else {
                        logger.info("법령 LLM 요약이 너무 짧아 조문 추출본 사용 (llm="
                                + styled.length() + ", min=" + minLen + ", extract=" + extractCoreLen + ")");
                    }
                }
            } catch (Exception e) {
                logger.warn("법령·조례 LLM 요약 실패: " + e.getMessage());
            }
        }

        if (!summaryOk) {
            if (extractAnswer.length() > 0) {
                answer = extractAnswer;
                result.put("answerHighlight", Boolean.TRUE);
                result.put("aiProvider", "law.go.kr-extract");
            } else {
                int joNum = api.optInt("joNum", 0);
                StringBuilder ans = new StringBuilder();
                if (joNum > 0 && bodyCnt > 0) {
                    ans.append("○ 검색어: 「").append(keyword).append("」\n");
                    ans.append("○ 관련 조문: ").append(cnt).append("건\n");
                    ans.append("○ 처리결과: 요약 미생성 → 관련자료출처 표시");
                } else if (joNum > 0) {
                    ans.append("「").append(keyword).append("」 조문 본문을 찾지 못해 관련 법령·조례 목록을 표시합니다.");
                } else if (api.optString("topic", "").length() > 0 && bodyCnt == 0) {
                    ans.append("「").append(keyword).append("」 관련 조문 본문을 찾지 못했습니다.\n");
                    ans.append("○ 권장 형식: \"법률명 제N조\" 또는 \"법률명의 주제\" (예: 지방재정법 제34조 / 지방재정법의 예산 원칙)\n");
                    ans.append("○ 관련자료출처의 법령 링크를 참고하세요.");
                } else {
                    ans.append("○ 법령·조례 검색 결과: ").append(cnt).append("건\n");
                    ans.append("○ 조례 범위: 부산광역시 본청\n");
                    ans.append("○ 조문 검색 형식: \"법률명 제N조\" / \"법률명의 주제\"");
                }
                answer = ans.toString();
                result.put("aiProvider", "law.go.kr");
            }
        }

        if (answer == null || answer.trim().length() == 0) {
            answer = "○ 법령·조례 검색 결과: " + cnt + "건\n○ 관련자료출처를 아래에 표시";
        }
        result.put("answer", answer);
        result.put("generalItems", items);
        result.put("rowCount", Integer.valueOf(cnt));
        result.put("generalSourcesOnly", Boolean.TRUE);
        return result;
    }

    /** LLM 실패/미사용 시: 조문 원문을 읽기 쉬운 개조식으로 정리 */
    private String buildLawExtractAnswer(String keyword, String bodyCtx, JSONObject api) {
        if (bodyCtx == null || bodyCtx.trim().length() < 20) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【조문 요약】\n");
        if (keyword != null && keyword.trim().length() > 0) {
            sb.append("○ 검색어: ").append(keyword.trim()).append("\n");
        }
        String lawName = api != null ? api.optString("lawName", "") : "";
        String topic = api != null ? api.optString("topic", "") : "";
        if (lawName.length() > 0) {
            sb.append("○ 법령: ").append(lawName);
            if (topic.length() > 0) {
                sb.append(" · 주제: ").append(topic);
            }
            sb.append("\n");
        }
        sb.append("\n");
        // [법령] 제목\n본문 블록을 잘라 붙임 (과도한 길이 제한)
        String[] blocks = bodyCtx.split("(?=\\[(?:법령|조례)\\])");
        int used = 0;
        for (int i = 0; i < blocks.length; i++) {
            String block = blocks[i].trim();
            if (block.length() == 0) {
                continue;
            }
            if (used >= 3) {
                break;
            }
            String clipped = block;
            // 조문 실질 내용이 잘리지 않도록 여유 있게 유지
            if (clipped.length() > 5500) {
                clipped = clipped.substring(0, 5500).trim() + "\n…";
            }
            // 단순 줄바꿈 정리
            clipped = clipped.replaceAll("\n{3,}", "\n\n");
            sb.append(toReportEumseumStyle(clipped)).append("\n\n");
            used++;
        }
        return sb.toString().trim();
    }

    private JSONObject handleGeneralCitySearch(String keyword, JSONObject result) throws Exception {
        // 시홈페이지 직접 검색(보도자료·고시공고·새소식). 부기주무관 API는 키 발급 후 보강용.
        JSONObject api = aiBusanHomepageClient.search(keyword);
        if (!api.optBoolean("ok", false)) {
            if (aiBugiGovDataClient.hasApiKey()) {
                JSONObject bugi = aiBugiGovDataClient.search(keyword);
                if (bugi.optBoolean("ok", false)) {
                    return putCitySearchResult(result, bugi, "bugi",
                            "보도자료·고시공고 검색 결과 ");
                }
            }
            result.put("answer", api.optString("message", "보도자료·고시공고 검색에 실패했습니다."));
            result.put("aiProvider", "busanHomepage");
            return result;
        }
        return putCitySearchResult(result, api, "busanHomepage",
                "보도자료·고시공고 검색 결과 ");
    }

    private JSONObject putCitySearchResult(JSONObject result, JSONObject api,
            String provider, String answerPrefix) {
        JSONArray items = api.optJSONArray("items");
        int cnt = api.optInt("count", items == null ? 0 : items.size());
        if (cnt == 0) {
            result.put("answer", "\"" + api.optString("keyword", "")
                    + "\" 관련 보도자료·고시공고 자료를 찾지 못했습니다. (검색기간 2025~2026)");
            result.put("aiProvider", provider);
            result.put("generalItems", new JSONArray());
            return result;
        }
        result.put("answer", "○ " + answerPrefix + cnt + "건\n○ 검색기간: 2025~2026");
        result.put("generalItems", items);
        result.put("aiProvider", provider);
        result.put("rowCount", Integer.valueOf(cnt));
        return result;
    }

    private JSONObject handleGeneralManualSearch(String keyword, JSONObject result) throws Exception {
        JSONObject api = aiManualDocService.search(keyword);
        if (!api.optBoolean("ok", false)) {
            result.put("answer", api.optString("message", "예산운용지침 검색에 실패했습니다."));
            result.put("aiProvider", "manual");
            return result;
        }
        JSONArray items = api.optJSONArray("items");
        int cnt = api.optInt("count", items == null ? 0 : items.size());
        if (cnt == 0) {
            result.put("answer", api.optString("message",
                    "\"" + keyword + "\" 관련 예산운용지침 내용을 찾지 못했습니다."));
            result.put("aiProvider", "manual");
            result.put("generalItems", new JSONArray());
            return result;
        }

        String summaryCtx = api.optString("summaryContext", "");
        String hitPageLabel = api.optString("hitPageLabel", "");

        // 화면에는 요약 + 출처(파일·페이지)만 표시. 발췌 본문은 서버 내부용.
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject it = items.getJSONObject(i);
                it.put("body", "");
            }
        }

        String answer = "○ 예산운용지침 검색 결과: " + cnt + "건\n○ 관련자료출처를 아래에 표시";
        // LLM 실패 시에만 발췌 fallback 생성 (성공 경로 지연 제거)
        String fallback = "";

        boolean useLlm = llmClient != null && llmClient.isEnabled() && summaryCtx.length() > 0
                && isYnFlag(getStringProp("Globals.AiManualUseLlm", "true"));

        if (useLlm) {
            try {
                long t0 = System.currentTimeMillis();
                // 밀도 높은 키워드 블록만 전달 → 토큰↓·속도↑·관련성↑
                int ctxLimit = getIntProp("Globals.AiManualPromptChars", 3600);
                String cleanCtx = buildCompactManualLlmContext(summaryCtx, keyword, ctxLimit);
                StringBuilder prompt = new StringBuilder(700 + cleanCtx.length());
                prompt.append("당신은 부산시 예산편성 도우미입니다.\n");
                prompt.append("아래 [발췌]만 근거로 [질문]에 실질적으로 도움이 되는 답을 음슴체 개조식으로 작성하세요.\n");
                prompt.append("[필수]\n");
                prompt.append("1. 질문 주제와 직접 관련된 내용만 작성. 다른 장·절(예: 여비·국제화 등) 끌어오지 말 것\n");
                prompt.append("2. 금액·기준(억/원/이상/미만)·대상·예외·절차·기한이 발췌에 있으면 반드시 포함\n");
                prompt.append("3. 짧은 키워드 나열만 하지 말 것. 각 항목은 한 줄 이상으로 의미를 이해할 수 있게 작성\n");
                prompt.append("4. 형식: ○ 문서명 (p.N) 다음에 - 항목. 종결은 음슴체(함/임/됨/음). '~습니다' 금지\n");
                prompt.append("5. 발췌에 없는 내용 추측 금지\n");
                prompt.append("[질문] ").append(keyword).append('\n');
                prompt.append("[발췌]\n").append(cleanCtx);

                String summary = llmClient.generateUserQuery(prompt.toString());
                logger.info("예산운용지침 LLM ms=" + (System.currentTimeMillis() - t0)
                        + " promptChars=" + prompt.length());
                if (summary != null && summary.trim().length() > 0) {
                    String styled = toReportEumseumStyle(summary.trim());
                    boolean qualityOk = isManualSummarySubstantial(styled, cleanCtx, keyword)
                            && (isManualAnswerFaithful(styled, summaryCtx, keyword)
                            || isManualSummaryAcceptable(styled, keyword));
                    if (qualityOk) {
                        answer = "【예산운용지침】\n" + styled;
                        result.put("aiProvider", llmClient.getProviderName() + "-manual");
                        result.put("answerHighlight", Boolean.TRUE);
                    } else {
                        fallback = buildManualMultiFileFallback(keyword, items, hitPageLabel, summaryCtx);
                        if (fallback.length() > 0) {
                            answer = fallback;
                            result.put("aiProvider", "manual-extract");
                            result.put("answerHighlight", Boolean.TRUE);
                            logger.info("예산운용지침 LLM 요약 품질 미달 → 발췌 요약으로 대체");
                        } else {
                            answer = "【예산운용지침】\n" + styled;
                            result.put("aiProvider", llmClient.getProviderName() + "-manual");
                            result.put("answerHighlight", Boolean.TRUE);
                        }
                    }
                } else {
                    fallback = buildManualMultiFileFallback(keyword, items, hitPageLabel, summaryCtx);
                    if (fallback.length() > 0) {
                        answer = fallback;
                    }
                    result.put("aiProvider", fallback.length() > 0 ? "manual-extract" : "manual");
                }
            } catch (Exception e) {
                logger.warn("예산운용지침 LLM 요약 실패: " + e.getMessage());
                fallback = buildManualMultiFileFallback(keyword, items, hitPageLabel, summaryCtx);
                if (fallback.length() > 0) {
                    answer = fallback;
                }
                result.put("aiProvider", fallback.length() > 0 ? "manual-extract" : "manual");
            }
        } else {
            fallback = buildManualMultiFileFallback(keyword, items, hitPageLabel, summaryCtx);
            if (fallback.length() > 0) {
                answer = fallback;
                result.put("answerHighlight", Boolean.TRUE);
            }
            result.put("aiProvider", fallback.length() > 0 ? "manual-extract" : "manual");
        }

        result.put("answer", answer);
        result.put("generalItems", items);
        result.put("rowCount", Integer.valueOf(cnt));
        result.put("generalSourcesOnly", Boolean.TRUE);
        return result;
    }

    /**
     * LLM용 발췌 압축: 문서별로 키워드 문단(+뒤따르는 기준·금액 줄) 위주.
     * 잡음·무관 장은 줄이고, 실질 기준은 보존한다.
     */
    private String buildCompactManualLlmContext(String summaryCtx, String keyword, int limit) {
        if (summaryCtx == null || summaryCtx.length() == 0) {
            return "";
        }
        int lim = limit > 800 ? limit : 3600;
        String[] parts = summaryCtx.split("(?m)^###\\s*파일:\\s*");
        StringBuilder out = new StringBuilder();
        int fileCnt = 0;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].trim().length() > 0) {
                fileCnt++;
            }
        }
        if (fileCnt <= 0) {
            fileCnt = 1;
        }
        // 속도: LLM에는 상위 2개 문서만 (검색 결과 출처 목록은 items로 별도 표시)
        int maxFilesForLlm = Math.min(fileCnt, getIntProp("Globals.AiManualLlmMaxFiles", 2));
        int perFile = Math.max(900, lim / Math.max(1, maxFilesForLlm));
        int usedFiles = 0;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.length() == 0) {
                continue;
            }
            if (usedFiles >= maxFilesForLlm || out.length() >= lim) {
                break;
            }
            int nl = part.indexOf('\n');
            String name;
            String body;
            if (nl > 0) {
                name = part.substring(0, nl).trim();
                body = part.substring(nl + 1).trim();
            } else {
                name = part;
                body = "";
            }
            body = sanitizeManualExcerptForLlm(body);
            body = focusManualLines(body, keyword);
            // 정보량 과도 축소 금지 — 금액·기준 줄이 잘리지 않게
            if (body.length() > perFile) {
                body = clipManualExcerpt(body, perFile);
            }
            int remain = lim - out.length();
            if (remain < 80) {
                break;
            }
            String block = "### 파일: " + name + "\n" + body + "\n";
            if (block.length() > remain) {
                block = block.substring(0, remain);
            }
            out.append(block);
            usedFiles++;
        }
        return out.toString().trim();
    }

    /** PDF 표 추출 잡음 제거 — LLM 요약 품질 향상 */
    private String sanitizeManualExcerptForLlm(String ctx) {
        if (ctx == null || ctx.length() == 0) {
            return "";
        }
        String s = ctx;
        s = s.replace('\u00A0', ' ');
        // 페이지 장식 번호 (• 30 • / · 96 ·)
        s = s.replaceAll("[•·]\\s*\\d{1,3}\\s*[•·]", " ");
        // 단독 짧은 숫자 줄(표 행번호)
        s = s.replaceAll("(?m)^\\s*\\d{1,3}\\s*$", "");
        // 현행/개정안 표 헤더 잔여
        s = s.replaceAll("현\\s*행\\s*개\\s*정\\s*안\\s*개정사유", " ");
        s = s.replaceAll("(?m)^\\s*개\\s*정\\s*안\\s*$", "");
        s = s.replaceAll("[ \\t]{2,}", " ");
        s = s.replaceAll("\n{3,}", "\n\n");
        return s.trim();
    }

    /** LLM 요약이 원문 덤프가 아니고 질문 키워드를 담으면 허용 */
    private boolean isManualSummaryAcceptable(String answer, String keyword) {
        if (answer == null || answer.length() < 40) {
            return false;
        }
        int polite = countMatches(answer, "습니다") + countMatches(answer, "입니다");
        if (polite >= 3) {
            return false;
        }
        // 원문 덤프 징후: 장식 페이지번호·과도한 깨진 단문
        if (answer.indexOf("• ") >= 0 && countMatches(answer, "• ") >= 3) {
            return false;
        }
        String ansNorm = AiKeywordMatcher.normalize(answer);
        AiKeywordMatcher.SearchExpression expr = AiKeywordMatcher.parseExpression(keyword);
        java.util.List<String> terms = expr.getTerms();
        if (terms.isEmpty()) {
            return ansNorm.indexOf(AiKeywordMatcher.normalize(keyword)) >= 0;
        }
        int hit = 0;
        for (int i = 0; i < terms.size(); i++) {
            String n = AiKeywordMatcher.normalize(terms.get(i));
            if (n.length() >= 2 && ansNorm.indexOf(n) >= 0) {
                hit++;
            }
        }
        return hit >= 1;
    }

    /**
     * LLM 실패 시: 매칭된 문서별로 핵심 발췌를 음슴체 보고 형식으로 묶는다.
     */
    private String buildManualMultiFileFallback(String keyword, JSONArray items,
            String hitPageLabel, String summaryCtx) {
        StringBuilder sb = new StringBuilder();
        sb.append("【예산운용지침】\n");
        if (keyword != null && keyword.trim().length() > 0) {
            sb.append("○ 검색어: ").append(keyword.trim()).append("\n");
        }
        int docCnt = items == null ? 0 : items.size();
        sb.append("○ 검색문서: ").append(docCnt).append("건\n");
        if (hitPageLabel != null && hitPageLabel.length() > 0) {
            sb.append("○ 출처: ").append(hitPageLabel).append("\n");
        }
        sb.append("\n");

        if (items != null && items.size() > 0) {
            // items의 body는 화면용으로 비우기 전·후 모두 대응 — summaryCtx 파싱 우선
            String ctx = summaryCtx == null ? "" : summaryCtx;
            if (ctx.length() > 0) {
                String[] parts = ctx.split("(?m)^###\\s*파일:\\s*");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.length() == 0) {
                        continue;
                    }
                    int nl = part.indexOf('\n');
                    String name;
                    String body;
                    if (nl > 0) {
                        name = part.substring(0, nl).trim();
                        body = part.substring(nl + 1).trim();
                    } else {
                        name = part;
                        body = "";
                    }
                    body = body.replaceAll("\\[p\\.(\\d+)\\]\\s*", "(p.$1) ");
                    // 키워드 문단+기준 줄 유지 (과도한 줄수 축소 금지)
                    body = toReportEumseumStyle(focusManualLines(body, keyword));
                    if (body.length() > 2800) {
                        body = clipManualExcerpt(body, 2800);
                    }
                    sb.append("○ [").append(name).append("]\n");
                    if (body.length() > 0) {
                        sb.append(body).append("\n\n");
                    }
                }
            }
        }
        String out = sb.toString().trim();
        return out.length() > 30 ? out : "";
    }

    private String clipManualExcerpt(String body, int max) {
        if (body == null) {
            return "";
        }
        String s = body.trim();
        if (s.length() <= max) {
            return s;
        }
        int cut = s.lastIndexOf('\n', max);
        if (cut < max / 2) {
            cut = max;
        }
        return s.substring(0, cut).trim() + "\n…";
    }

    /**
     * 요약 정보량 축소: 가로 글자 절단이 아니라 줄(항목) 수를 줄인다.
     * keepRatio=0.90 → 약 10% 분량 감소, 줄 내용은 유지.
     */
    private String reduceManualVolumeByLines(String body, double keepRatio, int maxLines) {
        if (body == null || body.length() == 0) {
            return "";
        }
        String[] raw = body.split("\n");
        java.util.List<String> lines = new java.util.ArrayList<String>();
        for (int i = 0; i < raw.length; i++) {
            String line = raw[i] == null ? "" : raw[i].trim();
            if (line.length() == 0) {
                continue;
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            return "";
        }
        double ratio = keepRatio > 0 && keepRatio <= 1.0 ? keepRatio : 0.90;
        int keep = (int) Math.ceil(lines.size() * ratio);
        if (keep < 1) {
            keep = 1;
        }
        if (maxLines > 0 && keep > maxLines) {
            keep = maxLines;
        }
        if (keep >= lines.size()) {
            StringBuilder all = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    all.append('\n');
                }
                all.append(lines.get(i));
            }
            return all.toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keep; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(lines.get(i));
        }
        sb.append("\n…");
        return sb.toString();
    }

    /** 키워드 줄 + 바로 뒤 기준·금액 줄을 묶어 요약 밀도를 높인다. */
    private String focusManualLines(String body, String keyword) {
        if (body == null || body.length() == 0) {
            return "";
        }
        String kw = keyword == null ? "" : keyword.trim();
        if (kw.length() == 0) {
            return body;
        }
        AiKeywordMatcher.SearchExpression expr = AiKeywordMatcher.parseExpression(kw);
        java.util.List<String> needles = new java.util.ArrayList<String>();
        if (!expr.isEmpty()) {
            java.util.List<String> terms = expr.getTerms();
            for (int t = 0; t < terms.size(); t++) {
                String n = AiKeywordMatcher.normalize(terms.get(t));
                if (n.length() > 0) {
                    needles.add(n);
                }
            }
        }
        if (needles.isEmpty()) {
            needles.add(AiKeywordMatcher.normalize(kw));
        }
        String[] lines = body.split("\n");
        boolean[] take = new boolean[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line == null || line.trim().length() == 0) {
                continue;
            }
            String ln = AiKeywordMatcher.normalize(line);
            boolean matched = line.indexOf("(p.") >= 0 || line.indexOf("[p.") >= 0;
            if (!matched) {
                for (int n = 0; n < needles.size(); n++) {
                    if (needles.get(n).length() > 0 && ln.indexOf(needles.get(n)) >= 0) {
                        matched = true;
                        break;
                    }
                }
            }
            if (matched) {
                take[i] = true;
                // 제목 다음 기준·금액 문단 포함 (최대 10줄)
                int added = 0;
                for (int j = i + 1; j < lines.length && added < 10; j++) {
                    String next = lines[j] == null ? "" : lines[j].trim();
                    if (next.length() == 0) {
                        if (added > 0) {
                            break;
                        }
                        continue;
                    }
                    String nn = AiKeywordMatcher.normalize(next);
                    boolean nextHasKw = false;
                    for (int n = 0; n < needles.size(); n++) {
                        if (nn.indexOf(needles.get(n)) >= 0) {
                            nextHasKw = true;
                            break;
                        }
                    }
                    boolean substance = next.indexOf('억') >= 0 || next.indexOf('원') >= 0
                            || next.indexOf("이상") >= 0 || next.indexOf("미만") >= 0
                            || next.indexOf("초과") >= 0 || next.indexOf("이하") >= 0
                            || next.matches(".*\\d{2,}.*")
                            || next.startsWith("-") || next.startsWith("·") || next.startsWith("○")
                            || next.startsWith("※") || next.startsWith("(")
                            || next.length() >= 18;
                    if (nextHasKw && added > 0) {
                        // 다음 키워드 제목은 바깥 루프에서 처리
                        break;
                    }
                    if (!nextHasKw && !substance && next.length() < 14 && added >= 2) {
                        break;
                    }
                    take[j] = true;
                    added++;
                }
            }
        }
        StringBuilder out = new StringBuilder();
        boolean any = false;
        for (int i = 0; i < lines.length; i++) {
            if (!take[i]) {
                continue;
            }
            String line = lines[i] == null ? "" : lines[i].trim();
            if (line.length() == 0) {
                continue;
            }
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(line);
            any = true;
        }
        return any ? out.toString() : body;
    }

    /**
     * 단답·주제이탈·금액누락 요약을 걸러낸다.
     * 발췌에 억/원 기준이 있는데 답에 없으면 실패.
     */
    private boolean isManualSummarySubstantial(String answer, String excerpt, String keyword) {
        if (answer == null || answer.trim().length() < 80) {
            return false;
        }
        if (countMatches(answer, "\n") < 2) {
            return false;
        }
        String ansNorm = AiKeywordMatcher.normalize(answer);
        String exNorm = excerpt == null ? "" : AiKeywordMatcher.normalize(excerpt);
        String kwNorm = AiKeywordMatcher.normalize(keyword);
        if (kwNorm.length() >= 4 && ansNorm.indexOf(kwNorm) < 0) {
            // 키워드 일부라도
            AiKeywordMatcher.SearchExpression expr = AiKeywordMatcher.parseExpression(keyword);
            java.util.List<String> terms = expr.getTerms();
            int hit = 0;
            for (int i = 0; i < terms.size(); i++) {
                String t = AiKeywordMatcher.normalize(terms.get(i));
                if (t.length() >= 2 && ansNorm.indexOf(t) >= 0) {
                    hit++;
                }
            }
            if (hit == 0) {
                return false;
            }
        }
        boolean exHasAmt = exNorm.indexOf("억") >= 0 || (exNorm.indexOf("원") >= 0 && exNorm.matches(".*\\d+.*"));
        boolean ansHasAmt = ansNorm.indexOf("억") >= 0 || ansNorm.indexOf("원") >= 0
                || ansNorm.matches(".*\\d{2,}.*");
        if (exHasAmt && !ansHasAmt) {
            return false;
        }
        // 질문과 무관한 장 남발 방지(여비 등이 질문에 없는데 답에만 많음)
        String[] offTopics = { "국외업무여비", "국제화여비", "국내출장", "특근매식비" };
        String kwCheck = kwNorm;
        int off = 0;
        for (int i = 0; i < offTopics.length; i++) {
            String o = AiKeywordMatcher.normalize(offTopics[i]);
            if (kwCheck.indexOf(o) < 0 && ansNorm.indexOf(o) >= 0 && exNorm.indexOf(o) >= 0) {
                // 발췌에 있어도 질문과 무관하면 감점
                off++;
            } else if (kwCheck.indexOf(o) < 0 && ansNorm.indexOf(o) >= 0 && exNorm.indexOf(o) < 0) {
                return false;
            }
        }
        if (off >= 2 && kwCheck.indexOf("여비") < 0) {
            return false;
        }
        return true;
    }

    /** LLM 답이 검색된 복수 문서 출처를 어느 정도 반영하는지 검사 */
    private boolean coversManualSources(String answer, JSONArray items) {
        if (answer == null || items == null || items.size() <= 1) {
            return true;
        }
        String ansNorm = AiKeywordMatcher.normalize(answer);
        int hit = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject it = items.getJSONObject(i);
            String title = it.optString("title", "");
            if (title.length() == 0) {
                continue;
            }
            // 파일명 핵심어(예산편성계획/회계관리/운영기준 등)가 답에 있으면 커버로 본다
            String[] keys = extractManualTitleKeys(title);
            boolean covered = false;
            for (int k = 0; k < keys.length; k++) {
                if (keys[k].length() >= 4 && ansNorm.indexOf(AiKeywordMatcher.normalize(keys[k])) >= 0) {
                    covered = true;
                    break;
                }
            }
            // 페이지 표기만으로도 출처 언급으로 인정하지 않음 — 문서 구분 필요
            if (covered) {
                hit++;
            }
        }
        // 2건 이상이면 절반 이상, 3건이면 최소 2건 반영
        int need = items.size() >= 3 ? 2 : 1;
        return hit >= need;
    }

    private String[] extractManualTitleKeys(String title) {
        if (title == null) {
            return new String[0];
        }
        java.util.List<String> keys = new java.util.ArrayList<String>();
        String t = title.replace(" ", "");
        if (t.indexOf("예산편성계획") >= 0 || t.indexOf("책자") >= 0) {
            keys.add("예산편성계획");
            keys.add("책자");
        }
        if (t.indexOf("회계관리") >= 0) {
            keys.add("회계관리");
        }
        if (t.indexOf("운영기준") >= 0 || t.indexOf("기금운용") >= 0) {
            keys.add("운영기준");
            keys.add("예산편성");
        }
        if (keys.isEmpty() && title.length() >= 6) {
            // 파일명 앞부분 일부
            String shortName = title.length() > 20 ? title.substring(0, 20) : title;
            keys.add(shortName.replace(" ", ""));
        }
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * 서술형(~습니다/~입니다)을 보고서 음슴체로 정리하고,
     * LLM이 자주 넣는 상투적 연결문구를 제거한다.
     */
    private String toReportEumseumStyle(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String s = text;
        s = s.replace('\u00A0', ' ');
        // 상투적 서술 문장·연결어 제거
        s = s.replaceAll("(?m)^\\s*(먼저|또한|그리고|마지막으로|한편|아울러)[,:]?\\s*", "");
        s = s.replaceAll("다음과 같은 (사항들이 |내용이 )?포함되어 있습니다[.!]?", "");
        s = s.replaceAll("이러한 (과정|절차)들은?[^.\\n]*[.!]?", "");
        s = s.replaceAll("에 대한 지침에서는[^.\\n]*[.!]?", "");
        // 긴 종결형부터 치환 (짧은 '습니다'가 먼저 먹으면 '있습니다'→'있음'이 깨짐)
        s = s.replaceAll("이루어집니다", "이루어짐");
        s = s.replaceAll("포함됩니다", "포함됨");
        s = s.replaceAll("분류됩니다", "분류됨");
        s = s.replaceAll("필요합니다", "필요함");
        s = s.replaceAll("중요합니다", "중요함");
        s = s.replaceAll("있습니다", "있음");
        s = s.replaceAll("없습니다", "없음");
        s = s.replaceAll("됩니다", "됨");
        s = s.replaceAll("합니다", "함");
        s = s.replaceAll("입니까", "임?");
        s = s.replaceAll("합니까", "함?");
        s = s.replaceAll("습니까", "음?");
        s = s.replaceAll("입니다", "임");
        s = s.replaceAll("습니다", "음");
        s = s.replaceAll("명시되어 있음", "명시됨");
        // 이중 변환 잔여 정리
        s = s.replaceAll("음\\.", "음");
        s = s.replaceAll("임\\.", "임");
        s = s.replaceAll("함\\.", "함");
        s = s.replaceAll("됨\\.", "됨");
        s = s.replaceAll("[ \\t]+\\n", "\n");
        s = s.replaceAll("\n{3,}", "\n\n");
        return s.trim();
    }

    /**
     * LLM 답이 발췌·질문 핵심어를 반영하는지 검사.
     * 서술형 일반론만 있고 원문 고유어가 없으면 불성실 답으로 본다.
     */
    private boolean isManualAnswerFaithful(String answer, String excerpt, String keyword) {
        if (answer == null || answer.length() < 20 || excerpt == null || excerpt.length() == 0) {
            return false;
        }
        // 서술형 존댓말이 많이 남아 있으면 실패로 보고 원문 대체 유도
        int polite = countMatches(answer, "습니다") + countMatches(answer, "입니다")
                + countMatches(answer, "합니다") + countMatches(answer, "됩니다");
        if (polite >= 2) {
            return false;
        }
        String ansNorm = AiKeywordMatcher.normalize(answer);
        String exNorm = AiKeywordMatcher.normalize(excerpt);
        String kwNorm = AiKeywordMatcher.normalize(keyword);
        if (kwNorm.length() >= 4 && ansNorm.indexOf(kwNorm) < 0
                && exNorm.indexOf(kwNorm) >= 0) {
            // 질문에 해당하는 원문 제목이 답에 전혀 없으면 실패
            return false;
        }
        // 발췌에만 있는 고유 표기 일부가 답에 들어와야 함
        String[] markers = {
                "사전심사", "지방재정영향평가", "중기지방재정계획", "투자심사",
                "예비타당성", "공유재산관리계획", "정책연구용역", "기술용역",
                "정보화사업", "지방보조금", "정수물품", "출자", "민간위탁",
                "편성 불가", "미 이행", "제출기한", "심사대상", "심사시기"
        };
        int markerInExcerpt = 0;
        int markerInAnswer = 0;
        for (int i = 0; i < markers.length; i++) {
            String m = AiKeywordMatcher.normalize(markers[i]);
            if (exNorm.indexOf(m) >= 0) {
                markerInExcerpt++;
                if (ansNorm.indexOf(m) >= 0) {
                    markerInAnswer++;
                }
            }
        }
        if (markerInExcerpt >= 3) {
            return markerInAnswer >= 2;
        }
        return true;
    }

    private int countMatches(String text, String token) {
        if (text == null || token == null || token.length() == 0) {
            return 0;
        }
        int n = 0;
        int from = 0;
        while ((from = text.indexOf(token, from)) >= 0) {
            n++;
            from += token.length();
        }
        return n;
    }

    private boolean isYnFlag(String v) {
        if (v == null) {
            return false;
        }
        String s = v.trim();
        return "Y".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s) || "1".equals(s);
    }

    /** 구버전: 자연어만으로 report/sql/chat 분기 */
    private JSONObject askLegacyNaturalLanguage(String question, JSONObject result) throws Exception {
        boolean reportLike = looksLikeReportQuestion(question);
        boolean llmReady = llmClient.isEnabled();
        if (!llmReady && !(reportLike && isReportDbOnly())) {
            result.put("answer", "AI 챗봇 API가 설정되지 않았습니다.\n"
                    + "globals.properties 설정을 확인하세요.\n"
                    + "  · Globals.ClovaEndpoint = http://99.1.82.207:8080/llm-studio/v1/api/task/generate/syncapi/busan_ai_llm/budget_search");
            result.put("aiProvider", "none");
            return result;
        }

        result.put("aiProvider", llmReady ? llmClient.getProviderName() : "db-only");

        JSONObject plan;
        if (looksLikeReportQuestion(question)) {
            plan = enrichPlanForReport(question, new JSONObject());
        } else {
            String planRaw = llmClient.generateUserQuery(question);
            plan = parsePlanOrDirectChatAnswer(planRaw);
        }

        String mode = plan.optString("mode", "");

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

    /**
     * 내부자료 검색 체크박스 OR 검색.
     * 대소문자·띄어쓰기 무시, 회계년도 범위 inclusive, 목록 양식은 기존과 동일.
     */
    private JSONObject handleCheckboxInternalSearch(String keyword, String fisYearFrom, String fisYearTo,
            boolean searchBizNm, boolean searchGubun, boolean searchExam, boolean searchSrchVal,
            JSONObject result) throws Exception {

        if (keyword == null || keyword.trim().length() == 0) {
            result.put("answer", "검색어를 입력해 주세요.");
            return result;
        }
        keyword = keyword.trim();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setMaxRows(getMaxReportRows());
        int qTimeout = getIntProp("Globals.AiQueryTimeoutSec", 45);
        if (qTimeout > 0) {
            jdbcTemplate.setQueryTimeout(qTimeout);
        }

        String yearErr = validateFisYearRange(fisYearFrom, fisYearTo, jdbcTemplate);
        if (yearErr.length() > 0) {
            result.put("answer", yearErr);
            return result;
        }

        List<String> years = buildInclusiveYearList(fisYearFrom, fisYearTo);
        CheckboxSearch chk = new CheckboxSearch(keyword, searchBizNm, searchGubun, searchExam, searchSrchVal);

        long t0 = System.currentTimeMillis();
        List<Map<String, Object>> rows = collectCheckboxRows(jdbcTemplate, years, chk);
        logPerf("checkboxSearch", t0, "years=" + years.size() + " rows=" + rows.size()
                + " biz=" + searchBizNm + " gubun=" + searchGubun
                + " exam=" + searchExam + " srch=" + searchSrchVal);

        if (rows == null || rows.isEmpty()) {
            result.put("answer", "검색 결과가 없습니다.\n"
                    + "회계년도 " + fisYearFrom + "~" + fisYearTo + ", 검색어: " + keyword);
            result.put("aiProvider", "db-checkbox");
            result.put("rowCount", Integer.valueOf(0));
            return result;
        }

        // 재원·본문 후속 조회 병렬
        final List<Map<String, Object>> finalRows = rows;
        Future<?> frscFuture = AI_DB_POOL.submit(new Runnable() {
            public void run() {
                enrichReportRowsWithFrsc(jdbcTemplate, finalRows);
            }
        });
        Future<?> textFuture = AI_DB_POOL.submit(new Runnable() {
            public void run() {
                fillReportTextColumns(jdbcTemplate, finalRows);
            }
        });
        try {
            int waitSec = Math.max(30, getIntProp("Globals.AiQueryTimeoutSec", 45) + 15);
            frscFuture.get(waitSec, TimeUnit.SECONDS);
            textFuture.get(waitSec, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("재원/본문 병렬 보강 실패: " + e.getMessage());
            try { frscFuture.cancel(true); } catch (Exception ignore) { /* */ }
            try { textFuture.cancel(true); } catch (Exception ignore) { /* */ }
            enrichReportRowsWithFrsc(jdbcTemplate, rows);
            fillReportTextColumns(jdbcTemplate, rows);
        }
        return assembleReportListResult(rows, result, "db-checkbox");
    }

    private JSONObject handleExternalSearch(String question, JSONObject result) throws Exception {
        List<String> urls = aiExternalSourceFetcher.getConfiguredUrls();
        if (urls == null || urls.isEmpty()) {
            result.put("answer", "외부자료 URL이 등록되지 않았습니다. 관리자에게 문의하세요.\n"
                    + "(globals.properties 의 Globals.AiExternalSourceUrls)");
            result.put("aiProvider", "external-none");
            return result;
        }

        if (!llmClient.isEnabled()) {
            result.put("answer", "외부자료 추론을 위해 내부 AI(LLM Studio) 설정이 필요합니다.\n"
                    + "globals.properties 의 Globals.ClovaEndpoint 를 확인하세요.");
            result.put("aiProvider", "none");
            return result;
        }

        String context = aiExternalSourceFetcher.buildContextForLlm();
        if (context == null || context.trim().length() == 0) {
            result.put("answer", "등록된 외부자료를 가져오지 못했습니다. URL·네트워크를 확인한 뒤 다시 시도해 주세요.\n"
                    + "등록 URL 수: " + urls.size());
            result.put("aiProvider", llmClient.getProviderName());
            return result;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("당신은 부산시 예산편성 도우미입니다. 아래 [외부자료]만을 근거로 질문에 답하세요.\n");
        prompt.append("자료에 없는 내용은 추측하지 말고 없다고 표시하세요.\n");
        prompt.append(REPORT_ANSWER_STYLE).append("\n");
        prompt.append("[질문]\n").append(question).append("\n\n");
        prompt.append("[외부자료]\n").append(context);

        String answer = llmClient.generateUserQuery(prompt.toString());
        if (answer == null || answer.trim().length() == 0) {
            result.put("answer", "외부자료 기반 답변을 생성하지 못했습니다.");
        } else {
            result.put("answer", toReportEumseumStyle(answer.trim()));
        }
        result.put("aiProvider", llmClient.getProviderName() + "-external");
        return result;
    }

    private String validateFisYearRange(String from, String to, JdbcTemplate jdbcTemplate) {
        if (from == null || !from.matches("\\d{4}") || to == null || !to.matches("\\d{4}")) {
            return "회계년도는 4자리 숫자로 입력해 주세요.";
        }
        int fromN = Integer.parseInt(from);
        int toN = Integer.parseInt(to);
        String maxYear = getMaxFisYear(jdbcTemplate);
        int maxN = Integer.parseInt(maxYear);
        if (fromN < MIN_FIS_YEAR || toN < MIN_FIS_YEAR) {
            return "회계년도는 " + MIN_FIS_YEAR + "년 이상이어야 합니다.";
        }
        if (fromN > maxN || toN > maxN) {
            return "회계년도는 최근 회계년도(" + maxYear + ") 이하여야 합니다.";
        }
        if (fromN > toN) {
            return "시작 회계년도가 종료 회계년도보다 클 수 없습니다.";
        }
        return "";
    }

    private List<String> buildInclusiveYearList(String from, String to) {
        List<String> years = new ArrayList<String>();
        int fromN = Integer.parseInt(from);
        int toN = Integer.parseInt(to);
        for (int y = fromN; y <= toN; y++) {
            years.add(String.valueOf(y));
        }
        return years;
    }

    /**
     * 체크박스 내부검색 조회.
     *
     * 연도 범위가 넓으면(예: 2013~2026) 단일 SQL 한 방은 조서 CLOB LIKE 를 전체 구간에 대해
     * 한 번에 평가하고 마지막에 ORDER BY + LIMIT 를 적용하므로, LIMIT 가 작업량을 줄여주지 못한다.
     * 연도를 작은 구간으로 쪼개 (조서구분 × 연도구간) 을 병렬로 던지면
     * 각 조회가 fis_year 인덱스를 좁게 타고 자체 LIMIT 로 조기 종료되어 전체 대기시간이 줄어든다.
     */
    private List<Map<String, Object>> collectCheckboxRows(final JdbcTemplate jdbcTemplate,
            List<String> years, final CheckboxSearch chk) {

        List<List<String>> chunks = splitYearChunks(years);
        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        String[] reportCds = new String[] { "010", "020" };

        List<Future<List<Map<String, Object>>>> futures =
                new ArrayList<Future<List<Map<String, Object>>>>();
        for (int r = 0; r < reportCds.length; r++) {
            final String reportCd = reportCds[r];
            for (int c = 0; c < chunks.size(); c++) {
                final List<String> chunkYears = chunks.get(c);
                futures.add(AI_DB_POOL.submit(new Callable<List<Map<String, Object>>>() {
                    public List<Map<String, Object>> call() {
                        return runCheckboxReportQuery(jdbcTemplate, reportCd, chunkYears, chk);
                    }
                }));
            }
        }

        long t0 = System.currentTimeMillis();
        boolean failed = false;
        int waitSec = Math.max(30, getIntProp("Globals.AiQueryTimeoutSec", 45) + 15);
        for (int i = 0; i < futures.size(); i++) {
            try {
                List<Map<String, Object>> part = futures.get(i).get(waitSec, TimeUnit.SECONDS);
                if (part != null) {
                    merged.addAll(part);
                }
            } catch (Exception e) {
                failed = true;
                logger.warn("내부검색 분할 조회 실패(" + i + "/" + futures.size() + "): " + e.getMessage());
            }
        }
        logPerf("checkboxChunks", t0, "chunks=" + chunks.size() + " tasks=" + futures.size()
                + " rows=" + merged.size());

        if (failed && merged.isEmpty()) {
            for (int i = 0; i < futures.size(); i++) {
                try { futures.get(i).cancel(true); } catch (Exception ignore) { /* */ }
            }
            logger.warn("내부검색 병렬 조회 전부 실패 — 전체 구간 순차로 재시도");
            List<Map<String, Object>> r010 = runCheckboxReportQuery(jdbcTemplate, "010", years, chk);
            List<Map<String, Object>> r020 = runCheckboxReportQuery(jdbcTemplate, "020", years, chk);
            if (r010 != null) {
                merged.addAll(r010);
            }
            if (r020 != null) {
                merged.addAll(r020);
            }
        }

        if (!merged.isEmpty()) {
            sortReportRowsByYearAndBiz(merged);
            // 분할 조회는 구간마다 LIMIT 이 걸리므로 합계가 상한을 넘을 수 있다.
            // 후속 재원·본문 보강 비용을 단일 조회와 같은 수준으로 묶기 위해 상한을 다시 적용한다.
            int maxRows = getMaxReportRows();
            if (merged.size() > maxRows) {
                merged = new ArrayList<Map<String, Object>>(merged.subList(0, maxRows));
            }
            return AiReportContextBuilder.trimRowsToMaxBizGroups(merged, getMaxReportBlocks());
        }
        return merged;
    }

    /**
     * 회계연도 목록을 병렬 조회용 구간으로 분할한다.
     * Globals.AiYearChunkSize (기본 2) 이하 구간으로 나누고, 구간 수는 최대 12개로 제한한다.
     * 연도 수가 구간 크기 이하이면 분할하지 않는다(기존 동작 유지).
     */
    private List<List<String>> splitYearChunks(List<String> years) {
        List<List<String>> chunks = new ArrayList<List<String>>();
        if (years == null || years.isEmpty()) {
            chunks.add(years == null ? new ArrayList<String>() : years);
            return chunks;
        }
        int size = getIntProp("Globals.AiYearChunkSize", 2);
        if (size < 1) {
            size = 1;
        }
        if (years.size() <= size) {
            chunks.add(years);
            return chunks;
        }
        int maxChunks = getIntProp("Globals.AiYearMaxChunks", 12);
        if (maxChunks < 1) {
            maxChunks = 1;
        }
        int needed = (years.size() + size - 1) / size;
        if (needed > maxChunks) {
            size = (years.size() + maxChunks - 1) / maxChunks;
        }
        for (int i = 0; i < years.size(); i += size) {
            int end = Math.min(i + size, years.size());
            chunks.add(new ArrayList<String>(years.subList(i, end)));
        }
        return chunks;
    }

    private List<Map<String, Object>> runCheckboxReportQuery(JdbcTemplate jdbcTemplate,
            String reportCd, List<String> years, CheckboxSearch chk) {
        List<Object> args = new ArrayList<Object>();
        String sql = buildReportSql(reportCd, years, "", 0,
                "", "", "", "", "", "", false, chk, args);
        return queryReport(jdbcTemplate, sql, args);
    }

    /** 심사조서 목록 JSON 조립 (기존 handleReportQuestion 표 양식과 동일) */
    private JSONObject assembleReportListResult(List<Map<String, Object>> rows, JSONObject result,
            String providerLabel) {
        AiReportContextBuilder.ContextOptions ctxOpts = AiReportContextBuilder.ContextOptions.defaults();
        ctxOpts.includeGubun = true;
        ctxOpts.includeDemandAmt = true;
        ctxOpts.maxBlocks = getMaxReportBlocks();

        java.util.LinkedHashMap<String, List<Map<String, Object>>> multiYearGroups =
                AiReportContextBuilder.groupRowsByBizNameAcrossYears(rows);
        java.util.IdentityHashMap<Map<String, Object>, JSONArray> detailRowsByRow =
                new java.util.IdentityHashMap<Map<String, Object>, JSONArray>();
        for (java.util.Iterator<List<Map<String, Object>>> git = multiYearGroups.values().iterator();
                git.hasNext();) {
            List<Map<String, Object>> g = git.next();
            AiReportContextBuilder.ContextOptions detailOpts = copyContextOptions(ctxOpts);
            detailOpts.includeGubun = true;
            JSONArray detailRows = toDetailRowsJson(g, detailOpts);
            for (int gi = 0; gi < g.size(); gi++) {
                detailRowsByRow.put(g.get(gi), detailRows);
            }
        }

        java.util.LinkedHashMap<String, List<Map<String, Object>>> bizGroups =
                AiReportContextBuilder.groupRowsByBiz(rows);

        String answer = "○ 검색 결과: " + bizGroups.size() + "개 사업 / " + rows.size() + "건\n"
                + "○ 표시 기준: 연도별·사업명별 묶음 / 차수별 구분\n"
                + "○ 상세 확인: 사업명 클릭 → 연도별·차수별 상세";

        result.put("answer", answer);
        result.put("rowCount", Integer.valueOf(rows.size()));
        result.put("aiProvider", providerLabel);

        JSONArray columns = new JSONArray();
        columns.add("연도");
        columns.add("사업명");
        columns.add("소관부서");
        columns.add("차수");
        columns.add("요구액(백만원)");
        columns.add("조정액(백만원)");
        columns.add("조정재원");

        JSONArray dataList = new JSONArray();
        java.util.HashSet<String> detailEmitted = new java.util.HashSet<String>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            String fisYearVal = AiReportContextBuilder.getStr(row, "fis_year");
            String bizLabel = AiReportContextBuilder.buildBizLabel(row);
            String deptLabel = (AiReportContextBuilder.getStr(row, "office_nm") + " "
                    + AiReportContextBuilder.getStr(row, "dept_nm")).trim();
            String grpKey = fisYearVal + "|" + bizLabel + "|" + deptLabel;
            obj.put("연도", fisYearVal);
            obj.put("사업명", bizLabel);
            obj.put("소관부서", deptLabel);
            obj.put("차수", AiReportContextBuilder.buildDgrLabel(
                    fisYearVal,
                    AiReportContextBuilder.getStr(row, "bgt_compo_fg"),
                    AiReportContextBuilder.getLong(row, "add_times"),
                    AiReportContextBuilder.getStr(row, "bgt_dgr")));
            obj.put("요구액(백만원)", AiReportContextBuilder.toMillion(
                    AiReportContextBuilder.getLong(row, "demand_bgt_amt")));
            obj.put("조정액(백만원)", AiReportContextBuilder.toMillion(
                    AiReportContextBuilder.getLong(row, "bgt_amt")));
            obj.put("조정재원", AiReportContextBuilder.formatFrscForRow(row));
            JSONArray detailRows = detailRowsByRow.get(row);
            String detailKey = bizLabel;
            obj.put("_detailKey", detailKey);
            if (detailRows != null && !detailRows.isEmpty() && detailEmitted.add(detailKey)) {
                obj.put("_detailRows", detailRows);
            }
            obj.put("_detailTitle", bizLabel);
            obj.put("_grpKey", grpKey);
            dataList.add(obj);
        }
        result.put("columns", columns);
        result.put("dataList", dataList);
        return result;
    }

    /** 체크박스 검색 조건 — 선택 필드 안에서 공통 키워드 식(& AND, 쉼표 OR) 적용 */
    private static final class CheckboxSearch {
        final String keyword;
        final AiKeywordMatcher.SearchExpression expression;
        final boolean bizNm;
        final boolean gubun;
        final boolean exam;
        final boolean srchVal;

        CheckboxSearch(String keyword, boolean bizNm, boolean gubun, boolean exam, boolean srchVal) {
            this.keyword = keyword;
            this.expression = AiKeywordMatcher.parseExpression(keyword);
            this.bizNm = bizNm;
            this.gubun = gubun;
            this.exam = exam;
            this.srchVal = srchVal;
        }

        boolean active() {
            return keyword != null && keyword.length() > 0 && !expression.isEmpty()
                    && (bizNm || gubun || exam || srchVal);
        }
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
        jdbcTemplate.setMaxRows(getMaxReportRows());
        int qTimeout = getIntProp("Globals.AiQueryTimeoutSec", 45);
        if (qTimeout > 0) {
            jdbcTemplate.setQueryTimeout(qTimeout);
        }

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

        long tSearch = System.currentTimeMillis();
        List<Map<String, Object>> rows = searchReportRows(jdbcTemplate, question,
                reportCd, planFisYear, explicitFisYear, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword);
        logPerf("searchReport", tSearch, "rows=" + (rows == null ? 0 : rows.size()) + " kw=" + bizKeyword);

        if (!rows.isEmpty()) {
            // 먼저 사업 수 상한으로 자른 뒤 재원 조회 — 잘릴 행까지 FRSC 조회하지 않음(운영 속도)
            rows = AiReportContextBuilder.trimRowsToMaxBizGroups(rows, getMaxReportBlocks());
            long tFrsc = System.currentTimeMillis();
            enrichReportRowsWithFrsc(jdbcTemplate, rows);
            logPerf("enrichFrsc", tFrsc, "rows=" + rows.size());
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

        // 답변 생성 — 표 목록을 먼저 보여주고, 사업 상세는 표 클릭 시 새 창에서 표시한다.
        AiReportContextBuilder.ContextOptions ctxOpts = buildContextOptions(question, planFisYear, tagKeyword, contentField);
        ctxOpts.maxBlocks = getMaxReportBlocks();

        // 표 클릭 상세: 같은 사업명(통계목)을 연도와 무관하게 묶어, 연도별→차수별로 나눠 표시한다.
        // (표 행 하나를 클릭하면 그 사업의 전체 연도·차수 상세가 새 창에 함께 표시됨)
        java.util.LinkedHashMap<String, List<Map<String, Object>>> bizNameGroups =
                AiReportContextBuilder.groupRowsByBizNameAcrossYears(rows);
        java.util.IdentityHashMap<Map<String, Object>, JSONArray> detailRowsByRow =
                new java.util.IdentityHashMap<Map<String, Object>, JSONArray>();
        for (java.util.Iterator<List<Map<String, Object>>> git = bizNameGroups.values().iterator(); git.hasNext();) {
            List<Map<String, Object>> g = git.next();
            AiReportContextBuilder.ContextOptions detailOpts = copyContextOptions(ctxOpts);
            detailOpts.includeGubun = true;
            JSONArray detailRows = toDetailRowsJson(g, detailOpts);
            for (int gi = 0; gi < g.size(); gi++) {
                detailRowsByRow.put(g.get(gi), detailRows);
            }
        }

        // 표 목록 그룹 수(연도+사업명 단위) — 안내 문구용
        java.util.LinkedHashMap<String, List<Map<String, Object>>> bizGroups =
                AiReportContextBuilder.groupRowsByBiz(rows);

        String answer = "○ 검색 결과: " + bizGroups.size() + "개 사업 / " + rows.size() + "건\n"
                + "○ 표시 기준: 연도별·사업명별 묶음 / 차수별 구분\n"
                + "○ 상세 확인: 사업명 클릭 → 연도별·차수별 상세";
        logger.info("AI RAG[report] year=" + fisYear + " rows=" + rows.size()
                + " bizGroups=" + bizGroups.size() + " dbOnly=" + isReportDbOnly());

        if (rows.size() > 0) {
            int frscZero = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (AiReportContextBuilder.formatFrscForRow(rows.get(i)).length() == 0) {
                    frscZero++;
                }
            }
            Map<String, Object> sample = rows.get(0);
            logger.info("AI RAG[report] frsc sample: bgt_amt=" + AiReportContextBuilder.getLong(sample, "bgt_amt")
                    + " adj_gov=" + AiReportContextBuilder.getLong(sample, "adj_frsc_gov")
                    + " adj_si=" + AiReportContextBuilder.getLong(sample, "adj_frsc_si")
                    + " frsc_detail=" + AiReportContextBuilder.getStr(sample, "frsc_detail")
                    + " frsc_fmt=" + AiReportContextBuilder.formatFrscForRow(sample)
                    + " frsc_zero_rows=" + frscZero + "/" + rows.size());
        }

        result.put("answer", answer);
        result.put("rowCount", rows.size());

        // 화면 표 출력용 요약 목록 (연도 컬럼 추가 — 연도 범위 검색 시 연도 구분)
        JSONArray columns = new JSONArray();
        columns.add("연도");
        columns.add("사업명");
        columns.add("소관부서");
        columns.add("차수");
        columns.add("요구액(백만원)");
        columns.add("조정액(백만원)");
        columns.add("조정재원");

        JSONArray dataList = new JSONArray();
        java.util.HashSet<String> detailEmitted = new java.util.HashSet<String>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            JSONObject obj = new JSONObject();
            String fisYearVal = AiReportContextBuilder.getStr(row, "fis_year");
            String bizLabel = AiReportContextBuilder.buildBizLabel(row);
            String deptLabel = (AiReportContextBuilder.getStr(row, "office_nm") + " "
                    + AiReportContextBuilder.getStr(row, "dept_nm")).trim();
            String grpKey = fisYearVal + "|" + bizLabel + "|" + deptLabel;
            obj.put("연도", fisYearVal);
            obj.put("사업명", bizLabel);
            obj.put("소관부서", deptLabel);
            obj.put("차수", AiReportContextBuilder.buildDgrLabel(
                    fisYearVal,
                    AiReportContextBuilder.getStr(row, "bgt_compo_fg"),
                    AiReportContextBuilder.getLong(row, "add_times"),
                    AiReportContextBuilder.getStr(row, "bgt_dgr")));
            obj.put("요구액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "demand_bgt_amt")));
            obj.put("조정액(백만원)", AiReportContextBuilder.toMillion(AiReportContextBuilder.getLong(row, "bgt_amt")));
            obj.put("조정재원", AiReportContextBuilder.formatFrscForRow(row));
            // 상세 JSON은 사업명 그룹당 1회만 — 동일 HTML을 모든 차수 행에 중복 실지 않음(응답·렌더 속도)
            JSONArray detailRows = detailRowsByRow.get(row);
            String detailKey = bizLabel;
            obj.put("_detailKey", detailKey);
            if (detailRows != null && !detailRows.isEmpty() && detailEmitted.add(detailKey)) {
                obj.put("_detailRows", detailRows);
            }
            obj.put("_detailTitle", bizLabel);
            obj.put("_grpKey", grpKey);
            dataList.add(obj);
        }
        result.put("columns", columns);
        result.put("dataList", dataList);

        return result;
    }

    private List<Map<String, Object>> queryReport(JdbcTemplate jdbcTemplate, String sql, List<Object> args) {
        long t0 = System.currentTimeMillis();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args.toArray());
            logPerf("queryReport", t0, "rows=" + (rows == null ? 0 : rows.size())
                    + " args=" + (args == null ? 0 : args.size()));
            return rows;
        } catch (Exception e) {
            logPerf("queryReportERR", t0, e.getMessage() == null ? "" : e.getMessage());
            logger.error("심사조서 RAG 조회 오류. sql=" + sql, e);
            return new ArrayList<Map<String, Object>>();
        }
    }

    /**
     * JSON 내보내기용 본문 — 회계년도 1회로 010/020 CLOB를 읽는다.
     */
    private void fillExportTextColumns(JdbcTemplate jdbcTemplate, List<Map<String, Object>> rows, String fisYear)
            throws Exception {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        long t0 = System.currentTimeMillis();
        String[] tables = new String[] { "TB_REPORT010", "TB_REPORT020" };
        java.util.HashMap<String, Map<String, Object>> textByKey = new java.util.HashMap<String, Map<String, Object>>();
        for (int t = 0; t < tables.length; t++) {
            String table = tables[t];
            String sql = "SELECT R.BGT_DGR AS bgt_dgr, R.TE_BGT_COMPO_ID AS te_bgt_compo_id,"
                    + " CAST(NVL(R.DEMAND_CONT,'') AS VARCHAR(3000)) AS demand_cont,"
                    + " CAST(NVL(R.EXAM_CONT,'') AS VARCHAR(3000)) AS exam_cont,"
                    + " CAST(NVL(R.INVEST_PLAN,'') AS VARCHAR(1500)) AS invest_plan,"
                    + " NVL(R.SRCH_VAL,'') AS srch_val"
                    + " FROM " + table + " R WHERE R.FIS_YEAR = ?";
            List<Map<String, Object>> textRows = jdbcTemplate.queryForList(sql, new Object[] { fisYear });
            for (int i = 0; i < textRows.size(); i++) {
                Map<String, Object> tr = textRows.get(i);
                String k = table + "\u0001" + AiReportContextBuilder.getLong(tr, "bgt_dgr")
                        + "\u0001" + AiReportContextBuilder.getStr(tr, "te_bgt_compo_id");
                textByKey.put(k, tr);
            }
        }
        int filled = 0;
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            String reportNm = AiReportContextBuilder.getStr(row, "report_nm");
            String table = reportNm.indexOf("투자") >= 0 ? "TB_REPORT020" : "TB_REPORT010";
            String k = table + "\u0001" + AiReportContextBuilder.getLong(row, "bgt_dgr")
                    + "\u0001" + AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
            Map<String, Object> tr = textByKey.get(k);
            if (tr == null) {
                continue;
            }
            String demand = AiReportContextBuilder.getStr(tr, "demand_cont");
            String exam = AiReportContextBuilder.getStr(tr, "exam_cont");
            String invest = AiReportContextBuilder.getStr(tr, "invest_plan");
            row.put("demand_cont", demand);
            row.put("gubun", demand);
            row.put("exam_cont", exam);
            row.put("invest_plan", invest);
            row.put("srch_val", AiReportContextBuilder.getStr(tr, "srch_val"));
            filled++;
        }
        logPerf("exportFillText", t0, "year=" + fisYear + " filled=" + filled + "/" + rows.size());
    }

    /**
     * JSON 내보내기용 재원 — 해당 연도 TB_DGRCOMPOFRSC 를 1회 조회한다.
     */
    private void enrichExportFrsc(JdbcTemplate jdbcTemplate, List<Map<String, Object>> rows, String fisYear)
            throws Exception {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        long t0 = System.currentTimeMillis();
        String sql = "SELECT BB.BGT_DGR AS bgt_dgr"
                + "     , BB.TE_BGT_COMPO_ID AS te_bgt_compo_id"
                + "     , A.FRSC_FG_NM AS frsc_fg_nm"
                + "     , A.STAND_FRSC_CD AS stand_frsc_cd"
                + "     , NVL(SUM(NVL(BB.ADJ_DEF_FRSC_AMT, 0)), 0) AS adj_def_frsc_amt"
                + "     , NVL(SUM(NVL(BB.DMN_DEF_FRSC_AMT, 0)), 0) AS dmn_def_frsc_amt"
                + "     , NVL(SUM(NVL(BB.PRE_DEF_FRSC_AMT, 0)), 0) AS pre_def_frsc_amt"
                + "     , NVL(SUM(NVL(BB.PRE_FRSC_AMT, 0)), 0) AS pre_frsc_amt"
                + "  FROM TB_DGRCOMPOFRSC BB"
                + " INNER JOIN TB_YEARFRSC A"
                + "    ON A.FIS_YEAR = BB.FIS_YEAR AND A.FRSC_FG_CD = BB.FRSC_FG_CD"
                + " WHERE BB.FIS_YEAR = ?"
                + " GROUP BY BB.BGT_DGR, BB.TE_BGT_COMPO_ID, A.FRSC_FG_CD, A.FRSC_FG_NM, A.STAND_FRSC_CD";
        List<Map<String, Object>> frscRows = jdbcTemplate.queryForList(sql, new Object[] { fisYear });
        java.util.HashMap<String, List<Map<String, Object>>> linesByCompo =
                new java.util.HashMap<String, List<Map<String, Object>>>();
        for (int i = 0; i < frscRows.size(); i++) {
            Map<String, Object> line = frscRows.get(i);
            String k = AiReportContextBuilder.getLong(line, "bgt_dgr") + "\u0001"
                    + AiReportContextBuilder.getStr(line, "te_bgt_compo_id");
            List<Map<String, Object>> list = linesByCompo.get(k);
            if (list == null) {
                list = new ArrayList<Map<String, Object>>();
                linesByCompo.put(k, list);
            }
            list.add(line);
        }
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            String k = AiReportContextBuilder.getLong(row, "bgt_dgr") + "\u0001"
                    + AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
            AiReportContextBuilder.applyAdjFrscFromFrscLines(row, linesByCompo.get(k));
        }
        logPerf("exportFillFrsc", t0, "year=" + fisYear + " lines=" + frscRows.size()
                + " keys=" + linesByCompo.size());
    }

    /**
     * 조회된 조서 행에 TB_DGRCOMPOFRSC.ADJ_DEF_FRSC_AMT(화면 재원별 '조정액') 기반
     * adj_frsc_gov/si/etc 를 부여한다. 재원명 매핑은 경상·투자 동일.
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

            // IN (많은 쌍) 한 방은 운영 CUBRID에서 느림 → 배치 단위로 쪼개 조회
            int pairCount = pairArgs.size() / 2;
            int batchSize = getIntProp("Globals.AiFrscBatchSize", 100);
            if (batchSize < 10) {
                batchSize = 10;
            }
            java.util.HashMap<String, List<Map<String, Object>>> linesByCompo =
                    new java.util.HashMap<String, List<Map<String, Object>>>();

            for (int offset = 0; offset < pairCount; offset += batchSize) {
                int end = Math.min(offset + batchSize, pairCount);
                StringBuilder orClause = new StringBuilder();
                List<Object> args = new ArrayList<Object>();
                args.add(fisYear);
                for (int p = offset; p < end; p++) {
                    if (orClause.length() > 0) {
                        orClause.append(" OR ");
                    }
                    orClause.append("(BB.BGT_DGR = ? AND BB.TE_BGT_COMPO_ID = ?)");
                    args.add(pairArgs.get(p * 2));
                    args.add(pairArgs.get(p * 2 + 1));
                }

                String sql = "SELECT BB.BGT_DGR AS bgt_dgr\n"
                        + "     , BB.TE_BGT_COMPO_ID AS te_bgt_compo_id\n"
                        + "     , A.FRSC_FG_NM AS frsc_fg_nm\n"
                        + "     , A.STAND_FRSC_CD AS stand_frsc_cd\n"
                        + "     , NVL(SUM(NVL(BB.ADJ_DEF_FRSC_AMT, 0)), 0) AS adj_def_frsc_amt\n"
                        + "     , NVL(SUM(NVL(BB.DMN_DEF_FRSC_AMT, 0)), 0) AS dmn_def_frsc_amt\n"
                        + "     , NVL(SUM(NVL(BB.PRE_DEF_FRSC_AMT, 0)), 0) AS pre_def_frsc_amt\n"
                        + "     , NVL(SUM(NVL(BB.PRE_FRSC_AMT, 0)), 0) AS pre_frsc_amt\n"
                        + "     , MAX(NVL(BB.REGI_ID, '')) AS regi_id\n"
                        + "  FROM TB_DGRCOMPOFRSC BB\n"
                        + " INNER JOIN TB_YEARFRSC A\n"
                        + "    ON A.FIS_YEAR = BB.FIS_YEAR\n"
                        + "   AND A.FRSC_FG_CD = BB.FRSC_FG_CD\n"
                        + " WHERE BB.FIS_YEAR = ?\n"
                        + "   AND (" + orClause + ")\n"
                        + " GROUP BY BB.BGT_DGR, BB.TE_BGT_COMPO_ID, A.FRSC_FG_CD, A.FRSC_FG_NM, A.STAND_FRSC_CD\n"
                        + " HAVING NVL(SUM(NVL(BB.ADJ_DEF_FRSC_AMT, 0)), 0) <> 0\n"
                        + "     OR NVL(SUM(NVL(BB.DMN_DEF_FRSC_AMT, 0)), 0) <> 0\n"
                        + "     OR NVL(SUM(NVL(BB.PRE_DEF_FRSC_AMT, 0)), 0) <> 0\n"
                        + "     OR NVL(SUM(NVL(BB.PRE_FRSC_AMT, 0)), 0) <> 0";

                List<Map<String, Object>> frscRows;
                try {
                    long tFrscQ = System.currentTimeMillis();
                    frscRows = jdbcTemplate.queryForList(sql, args.toArray());
                    logPerf("frscBatch", tFrscQ, "year=" + fisYear + " pairs=" + (end - offset)
                            + " lines=" + (frscRows == null ? 0 : frscRows.size()));
                } catch (Exception e) {
                    logger.error("재원 일괄 조회 오류 year=" + fisYear + " batch=" + offset, e);
                    continue;
                }

                for (int i = 0; i < frscRows.size(); i++) {
                    Map<String, Object> line = frscRows.get(i);
                    String k = AiReportContextBuilder.getLong(line, "bgt_dgr") + "\u0001"
                            + AiReportContextBuilder.getStr(line, "te_bgt_compo_id");
                    List<Map<String, Object>> list = linesByCompo.get(k);
                    if (list == null) {
                        list = new ArrayList<Map<String, Object>>();
                        linesByCompo.put(k, list);
                    }
                    list.add(line);
                }
            }

            for (int i = 0; i < yearRows.size(); i++) {
                Map<String, Object> row = yearRows.get(i);
                String k = AiReportContextBuilder.getLong(row, "bgt_dgr") + "\u0001"
                        + AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
                List<Map<String, Object>> lines = linesByCompo.get(k);
                AiReportContextBuilder.applyAdjFrscFromFrscLines(row, lines);
            }
        }
    }

    /**
     * 체크박스 목록 조회 후, 화면에 필요한 조서 본문(구분·검토내용 등)만 대상 행에 한해 채운다.
     * 목록 SQL에서 CLOB SELECT를 빼 연도 범위 검색을 빠르게 유지한다.
     */
    private void fillReportTextColumns(JdbcTemplate jdbcTemplate, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        long t0 = System.currentTimeMillis();
        java.util.LinkedHashMap<String, List<Map<String, Object>>> byKey =
                new java.util.LinkedHashMap<String, List<Map<String, Object>>>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            String reportNm = AiReportContextBuilder.getStr(row, "report_nm");
            String table = reportNm.indexOf("투자") >= 0 ? "TB_REPORT020" : "TB_REPORT010";
            String year = AiReportContextBuilder.getStr(row, "fis_year");
            long dgr = AiReportContextBuilder.getLong(row, "bgt_dgr");
            String compo = AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
            if (year.length() == 0 || dgr == 0L || compo.length() == 0) {
                continue;
            }
            String bucket = table + "\u0001" + year;
            List<Map<String, Object>> list = byKey.get(bucket);
            if (list == null) {
                list = new ArrayList<Map<String, Object>>();
                byKey.put(bucket, list);
            }
            list.add(row);
        }

        int batchSize = getIntProp("Globals.AiFrscBatchSize", 100);
        if (batchSize < 10) {
            batchSize = 10;
        }
        int filled = 0;
        for (java.util.Iterator<java.util.Map.Entry<String, List<Map<String, Object>>>> it =
                byKey.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry<String, List<Map<String, Object>>> e = it.next();
            String[] parts = e.getKey().split("\u0001", 2);
            String table = parts[0];
            String fisYear = parts[1];
            List<Map<String, Object>> yearRows = e.getValue();

            java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<String>();
            List<Object> pairArgs = new ArrayList<Object>();
            for (int i = 0; i < yearRows.size(); i++) {
                Map<String, Object> row = yearRows.get(i);
                long dgr = AiReportContextBuilder.getLong(row, "bgt_dgr");
                String compo = AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
                String k = dgr + "\u0001" + compo;
                if (seen.add(k)) {
                    pairArgs.add(Integer.valueOf((int) dgr));
                    pairArgs.add(compo);
                }
            }
            java.util.HashMap<String, Map<String, Object>> textByKey =
                    new java.util.HashMap<String, Map<String, Object>>();
            int pairCount = pairArgs.size() / 2;
            for (int offset = 0; offset < pairCount; offset += batchSize) {
                int end = Math.min(offset + batchSize, pairCount);
                StringBuilder orClause = new StringBuilder();
                List<Object> args = new ArrayList<Object>();
                args.add(fisYear);
                for (int p = offset; p < end; p++) {
                    if (orClause.length() > 0) {
                        orClause.append(" OR ");
                    }
                    orClause.append("(R.BGT_DGR = ? AND R.TE_BGT_COMPO_ID = ?)");
                    args.add(pairArgs.get(p * 2));
                    args.add(pairArgs.get(p * 2 + 1));
                }
                String sql = "SELECT R.BGT_DGR AS bgt_dgr, R.TE_BGT_COMPO_ID AS te_bgt_compo_id,\n"
                        + "       NVL(R.DEMAND_CONT, '') AS demand_cont,\n"
                        + "       NVL(R.EXAM_CONT, '') AS exam_cont,\n"
                        + "       NVL(R.INVEST_PLAN, '') AS invest_plan\n"
                        + "  FROM " + table + " R\n"
                        + " WHERE R.FIS_YEAR = ?\n"
                        + "   AND (" + orClause + ")";
                try {
                    List<Map<String, Object>> textRows = jdbcTemplate.queryForList(sql, args.toArray());
                    for (int i = 0; i < textRows.size(); i++) {
                        Map<String, Object> tr = textRows.get(i);
                        String k = AiReportContextBuilder.getLong(tr, "bgt_dgr") + "\u0001"
                                + AiReportContextBuilder.getStr(tr, "te_bgt_compo_id");
                        textByKey.put(k, tr);
                    }
                } catch (Exception ex) {
                    logger.warn("조서 본문 후속 조회 실패 table=" + table + " year=" + fisYear
                            + " : " + ex.getMessage());
                }
            }
            for (int i = 0; i < yearRows.size(); i++) {
                Map<String, Object> row = yearRows.get(i);
                String k = AiReportContextBuilder.getLong(row, "bgt_dgr") + "\u0001"
                        + AiReportContextBuilder.getStr(row, "te_bgt_compo_id");
                Map<String, Object> tr = textByKey.get(k);
                if (tr == null) {
                    continue;
                }
                String demand = AiReportContextBuilder.getStr(tr, "demand_cont");
                String exam = AiReportContextBuilder.getStr(tr, "exam_cont");
                String invest = AiReportContextBuilder.getStr(tr, "invest_plan");
                row.put("demand_cont", demand);
                row.put("gubun", demand);
                row.put("exam_cont", exam);
                row.put("invest_plan", invest);
                filled++;
            }
        }
        logPerf("fillReportText", t0, "rows=" + rows.size() + " filled=" + filled);
    }

    /**
     * 재원구분(FRSC_FG_CD)별 ADJ_DEF_FRSC_AMT 합계 — DialogDgrcompoModify·심사조서 화면과 동일.
     * 추경 등 동일 재원에 +/- 행이 있으면 SUM 으로 상계( MAX 는 잔여 양수만 남겨 오표시 ).
     */
    private static String frscAdjSummedSubquery(String alias) {
        return "(SELECT Z0.FIS_YEAR AS FIS_YEAR\n"
                + "      , Z0.BGT_DGR AS BGT_DGR\n"
                + "      , Z0.TE_BGT_COMPO_ID AS TE_BGT_COMPO_ID\n"
                + "      , Z0.FRSC_FG_CD AS FRSC_FG_CD\n"
                + "      , NVL(SUM(NVL(Z0.ADJ_DEF_FRSC_AMT, 0)), 0) AS ADJ_DEF_FRSC_AMT\n"
                + "   FROM TB_DGRCOMPOFRSC Z0\n"
                + "  WHERE 1=1\n"
                + appendFrscBootstrapExcludeClause("Z0")
                + "  GROUP BY Z0.FIS_YEAR, Z0.BGT_DGR, Z0.TE_BGT_COMPO_ID, Z0.FRSC_FG_CD) " + alias;
    }

    /** TB_YEARFRSC 조인 — 해당 연도·재원코드 정확히 일치 (DialogDgrcompoModify 와 동일) */
    private static String appendYearFrscExactJoin(String frscAlias, String yearFrscAlias) {
        return "   AND " + yearFrscAlias + ".FIS_YEAR = " + frscAlias + ".FIS_YEAR\n"
                + "   AND " + yearFrscAlias + ".FRSC_FG_CD = " + frscAlias + ".FRSC_FG_CD\n";
    }

    /**
     * @deprecated {@link #appendYearFrscExactJoin} 사용 — 연도 fallback 은 재원명·금액 오매핑 유발
     */
    private static String frscAdjDedupedSubquery(String alias) {
        return frscAdjSummedSubquery(alias);
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

        // 1차: 사업명 좁은 검색(이름 컬럼만) — 대용량 검토의견 텍스트를 읽지 않아 매우 빠름.
        if (bizKeyword.length() > 0) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                    "", "", "", false);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[1-biz-narrow] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + bizKeyword + " rows=" + rows.size());
                return rows;
            }
        }

        // 2차: 사업명 넓은 검색 — CLOB 전수 LIKE(운영에서 수십 초). Globals.AiEnableBroadSearch=true 일만.
        if (bizKeyword.length() > 0 && isBroadSearchEnabled()) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                    "", "", "", true);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[2-biz-broad] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + bizKeyword + " rows=" + rows.size());
                return rows;
            }
        } else if (bizKeyword.length() > 0) {
            logger.info("AI RAG skip[2-biz-broad] AiEnableBroadSearch=false kw=" + bizKeyword);
        }

        // 3차: 규칙 추출 사업명 키워드 — 좁은 검색 우선 (넓은 검색은 옵션)
        if (ruleBizKeyword.length() > 0 && !ruleBizKeyword.equals(bizKeyword)) {
            List<Map<String, Object>> rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                    mergeAllYears, bgtCompoFg, addTimes, ruleBizKeyword, deptKeyword, tagKeyword,
                    "", "", "", false);
            if (!rows.isEmpty()) {
                logger.info("AI RAG hit[3-rule-biz-narrow] years=" + years.size() + " merge=" + mergeAllYears
                        + " kw=" + ruleBizKeyword + " rows=" + rows.size());
                return rows;
            }
            if (isBroadSearchEnabled()) {
                rows = collectReportRowsAcrossYears(jdbcTemplate, reportCd, years,
                        mergeAllYears, bgtCompoFg, addTimes, ruleBizKeyword, deptKeyword, tagKeyword,
                        "", "", "", true);
                if (!rows.isEmpty()) {
                    logger.info("AI RAG hit[3-rule-biz-broad] years=" + years.size() + " merge=" + mergeAllYears
                            + " kw=" + ruleBizKeyword + " rows=" + rows.size());
                    return rows;
                }
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

        // 7차: 회계연도 미지정·인근연도 실패 시 전 연도 순차 검색 (기본 비활성 — 운영 DB 부하 큼)
        if (!explicitFisYear && isAllYearsFallbackEnabled()) {
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
        List<String> years = new ArrayList<String>();
        years.add(fisYear);
        return runReportQueryForYears(jdbcTemplate, reportCd, years, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword, broadKeyword);
    }

    /** 연도 목록 전체를 IN 조건으로 한 번에 조회 (연도 범위·다연도 검색 성능·정확도 개선) */
    private List<Map<String, Object>> runReportQueryForYears(JdbcTemplate jdbcTemplate, String reportCd,
            List<String> years, String bgtCompoFg, int addTimes, String bizKeyword, String deptKeyword,
            String tagKeyword, String implKeyword, String contentField, String contentKeyword, boolean broadKeyword) {
        List<Object> args = new ArrayList<Object>();
        String sql = buildReportSql(reportCd, years, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword,
                contentField, contentKeyword, broadKeyword, args);
        return queryReport(jdbcTemplate, sql, args);
    }

    /**
     * 검색 대상 연도를 순회한다.
     * mergeAllYears=true(연도 범위·연도별 질문)이면 각 연도 결과를 모두 합쳐 반환하고,
     * false이면 첫 번째 hit 연도만 반환한다.
     * 조서구분 미지정(010+020)일 때는 UNION ALL 대신 010→020 순차 조회로
     * 대용량 조인계획을 피한다(운영 DB 속도).
     */
    private List<Map<String, Object>> collectReportRowsAcrossYears(JdbcTemplate jdbcTemplate, String reportCd,
            List<String> years, boolean mergeAllYears, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword, boolean broadKeyword) {

        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < years.size(); i++) {
            String year = years.get(i);
            List<Map<String, Object>> rows;
            // 경상·투자 동시: UNION 한 방보다 단품 조서가 인덱스·옵티마이저에 유리
            if (reportCd == null || reportCd.length() == 0
                    || (!"010".equals(reportCd) && !"020".equals(reportCd))) {
                rows = runReportQuery(jdbcTemplate, "010", year,
                        bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                        implKeyword, contentField, contentKeyword, broadKeyword);
                if (rows.isEmpty()) {
                    rows = runReportQuery(jdbcTemplate, "020", year,
                            bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                            implKeyword, contentField, contentKeyword, broadKeyword);
                } else if (mergeAllYears) {
                    List<Map<String, Object>> inv = runReportQuery(jdbcTemplate, "020", year,
                            bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                            implKeyword, contentField, contentKeyword, broadKeyword);
                    if (!inv.isEmpty()) {
                        rows = new ArrayList<Map<String, Object>>(rows);
                        rows.addAll(inv);
                    }
                }
            } else {
                rows = runReportQuery(jdbcTemplate, reportCd, year,
                        bgtCompoFg, addTimes, bizKeyword, deptKeyword, tagKeyword,
                        implKeyword, contentField, contentKeyword, broadKeyword);
            }
            if (!rows.isEmpty()) {
                if (mergeAllYears) {
                    merged.addAll(rows);
                    if (merged.size() >= getMaxReportRows()) {
                        break;
                    }
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

        // 연도를 명시하지 않은 경우에만 인근 연도 확장 (운영 기본 0 — PC 대비 데이터량 차이 최소화)
        if (!explicitYear) {
            String anchor = years.isEmpty() ? getMaxFisYear(jdbcTemplate) : years.get(0);
            int nearbyCount = getIntProp("Globals.AiNearbyYearCount", 0);
            if (anchor.length() == 4 && nearbyCount > 0) {
                int base = Integer.parseInt(anchor);
                for (int i = 1; i <= nearbyCount; i++) {
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
        if (cachedAllFisYears != null && !cachedAllFisYears.isEmpty()) {
            return cachedAllFisYears;
        }
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
        if (!years.isEmpty()) {
            cachedAllFisYears = years;
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

    /** 띄어쓰기·영문 대소문자 무시 LIKE 패턴 — 키워드의 공백을 모두 제거한다. */
    private String toSpaceInsensitiveLike(String keyword) {
        if (keyword == null) {
            return "%";
        }
        String kw = keyword.replaceAll("\\s+", "").trim();
        if (kw.length() == 0) {
            return "%";
        }
        return "%" + kw.toUpperCase(Locale.ENGLISH) + "%";
    }

    /** 컬럼도 공백을 제거하고 비교 (예: UPPER(REPLACE(W.comp_ground,' ','')) LIKE ?) */
    private String spaceInsensitiveLikeCol(String col) {
        return "UPPER(REPLACE(" + col + ", ' ', '')) LIKE ?";
    }

    private void addSpaceInsensitiveLikeArgs(List<Object> args, String keyword, int count) {
        String like = toSpaceInsensitiveLike(keyword);
        for (int i = 0; i < count; i++) {
            args.add(like);
        }
    }

    /**
     * 회계연도 검색 조건절.
     * - 단일 연도: "col = ?"  (인덱스 범위 스캔 그대로 사용 — 속도 최적)
     * - 연도 범위: "col BETWEEN ? AND ?" (연속 구간 인덱스 스캔, IN 목록보다 빠름)
     * CUBRID 옵티마이저는 IN(?,?,...) 에서 인덱스 활용이 저하되어 = / BETWEEN 을 사용한다.
     */
    private String yearPredicate(String col, List<String> years) {
        if (years == null || years.size() <= 1) {
            return col + " = ?";
        }
        return col + " BETWEEN ? AND ?";
    }

    private void addYearPredicateArgs(List<Object> args, List<String> years) {
        if (years == null || years.isEmpty()) {
            args.add("");
            return;
        }
        if (years.size() == 1) {
            args.add(years.get(0));
            return;
        }
        String min = years.get(0);
        String max = years.get(0);
        for (int i = 1; i < years.size(); i++) {
            String y = years.get(i);
            if (y.compareTo(min) < 0) {
                min = y;
            }
            if (y.compareTo(max) > 0) {
                max = y;
            }
        }
        args.add(min);
        args.add(max);
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
    private String buildReportSql(String reportCd, List<String> years, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword,
            boolean broadKeyword, List<Object> args) {
        return buildReportSql(reportCd, years, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword,
                contentField, contentKeyword, broadKeyword, null, args, -1);
    }

    private String buildReportSql(String reportCd, List<String> years, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword,
            boolean broadKeyword, CheckboxSearch checkbox, List<Object> args) {
        return buildReportSql(reportCd, years, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword,
                contentField, contentKeyword, broadKeyword, checkbox, args, -1);
    }

    private String buildReportSql(String reportCd, List<String> years, String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword,
            boolean broadKeyword, CheckboxSearch checkbox, List<Object> args, int limitOverride) {

        boolean both = !"010".equals(reportCd) && !"020".equals(reportCd);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT X.* FROM (\n");
        sb.append("SELECT * FROM (\n");
        if (both || "010".equals(reportCd)) {
            appendReportBranch(sb, "010", years, bgtCompoFg, addTimes,
                    bizKeyword, deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword,
                    broadKeyword, checkbox, args);
        }
        if (both) {
            sb.append("UNION ALL\n");
        }
        if (both || "020".equals(reportCd)) {
            appendReportBranch(sb, "020", years, bgtCompoFg, addTimes,
                    bizKeyword, deptKeyword, tagKeyword, implKeyword, contentField, contentKeyword,
                    broadKeyword, checkbox, args);
        }
        sb.append(") W\n");
        sb.append("WHERE 1=1\n");

        sb.append("ORDER BY W.fis_year, W.office_nm, W.dept_nm, W.comp_ground, W.te_mng_mok_cd, W.bgt_dgr\n");
        if (limitOverride != 0) {
            int limit = limitOverride > 0 ? limitOverride : getMaxReportRows();
            sb.append("LIMIT ").append(limit).append("\n");
        }
        sb.append(") X");

        return sb.toString();
    }

    /** 조서구분(010/020)별 단일 SELECT 분기 생성. 단일 연도=등호, 범위=BETWEEN 으로 인덱스 활용. */
    private void appendReportBranch(StringBuilder sb, String reportCd, List<String> years,
            String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword, boolean broadKeyword, List<Object> args) {
        appendReportBranch(sb, reportCd, years, bgtCompoFg, addTimes,
                bizKeyword, deptKeyword, tagKeyword, implKeyword,
                contentField, contentKeyword, broadKeyword, null, args);
    }

    private void appendReportBranch(StringBuilder sb, String reportCd, List<String> years,
            String bgtCompoFg, int addTimes,
            String bizKeyword, String deptKeyword, String tagKeyword, String implKeyword,
            String contentField, String contentKeyword, boolean broadKeyword,
            CheckboxSearch checkbox, List<Object> args) {

        boolean invest = "020".equals(reportCd);
        String table = invest ? "TB_REPORT020" : "TB_REPORT010";
        String reportNm = invest ? "투자사업심사조서" : "경상사업심사조서";

        sb.append("SELECT '").append(reportNm).append("' AS report_nm\n");
        sb.append("     , R.fis_year AS fis_year\n");
        sb.append("     , R.bgt_dgr AS bgt_dgr\n");
        sb.append("     , R.te_bgt_compo_id AS te_bgt_compo_id\n");
        sb.append("     , MIN(G.bgt_compo_fg) AS bgt_compo_fg\n");
        sb.append("     , NVL(MIN(G.add_times), 0) AS add_times\n");
        sb.append("     , NVL(MIN(D.office_nm), '') AS office_nm\n");
        sb.append("     , NVL(MIN(D.dept_nm), '') AS dept_nm\n");
        sb.append("     , NVL(MIN(B.pbiz_nm), '') AS pbiz_nm\n");
        sb.append("     , NVL(MIN(B.ubiz_nm), '') AS ubiz_nm\n");
        sb.append("     , NVL(MIN(B.dbiz_nm), '') AS dbiz_nm\n");
        sb.append("     , NVL(MIN(C.comp_ground), '') AS comp_ground\n");
        sb.append("     , NVL(MIN(B.fis_fg_nm), '') AS fis_fg_nm\n");
        sb.append("     , MIN(C.te_mng_mok_cd) AS te_mng_mok_cd\n");
        sb.append("     , MIN(C.te_mng_mok_nm) AS te_mng_mok_nm\n");
        sb.append("     , MIN(C.dept_cd) AS dept_cd\n");
        sb.append("     , MIN(C.dbiz_cd) AS dbiz_cd\n");
        sb.append("     , SUM(NVL(C.pre_amt, 0)) AS pre_amt\n");
        sb.append("     , SUM(NVL(C.pre_bgt_amt, 0)) AS pre_bgt_amt\n");
        sb.append("     , SUM(NVL(C.demand_diff_amt, 0)) AS demand_bgt_amt\n");
        sb.append("     , SUM(NVL(C.diff_amt, 0)) AS bgt_amt\n");
        sb.append("     , SUM(NVL(C.diff_amt, 0)) AS diff_amt\n");
        // 체크박스 목록 조회는 CLOB를 SELECT하지 않음(필터 WHERE만 사용) → 상세는 후속 fillReportTextColumns
        if (checkbox != null) {
            sb.append("     , '' AS gubun\n");
            sb.append("     , '' AS invest_plan\n");
            sb.append("     , '' AS demand_cont\n");
            sb.append("     , '' AS exam_cont\n");
            sb.append("     , NVL(MIN(R.srch_val), '') AS srch_val\n");
        } else {
            sb.append("     , NVL(MIN(R.demand_cont), '') AS gubun\n");
            sb.append("     , NVL(MIN(R.invest_plan), '') AS invest_plan\n");
            sb.append("     , NVL(MIN(R.demand_cont), '') AS demand_cont\n");
            sb.append("     , NVL(MIN(R.exam_cont), '') AS exam_cont\n");
            sb.append("     , NVL(MIN(R.srch_val), '') AS srch_val\n");
        }
        if (invest) {
            sb.append("     , MIN(NVL(R.tot_frsc_amt1,0)+NVL(R.tot_frsc_amt2,0)+NVL(R.tot_frsc_amt3,0)+NVL(R.tot_frsc_amt4,0)+NVL(R.tot_frsc_amt5,0)+NVL(R.tot_frsc_amt6,0)) AS tot_biz_amt\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt1,0)) AS tot_frsc_amt1\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt2,0)) AS tot_frsc_amt2\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt3,0)) AS tot_frsc_amt3\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt4,0)) AS tot_frsc_amt4\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt5,0)) AS tot_frsc_amt5\n");
            sb.append("     , MIN(NVL(R.tot_frsc_amt6,0)) AS tot_frsc_amt6\n");
        } else {
            sb.append("     , 0 AS tot_biz_amt\n");
            sb.append("     , 0 AS tot_frsc_amt1\n");
            sb.append("     , 0 AS tot_frsc_amt2\n");
            sb.append("     , 0 AS tot_frsc_amt3\n");
            sb.append("     , 0 AS tot_frsc_amt4\n");
            sb.append("     , 0 AS tot_frsc_amt5\n");
            sb.append("     , 0 AS tot_frsc_amt6\n");
        }
        sb.append("  FROM ").append(table).append(" R\n");
        sb.append("     , TB_DGRCOMPO C\n");
        sb.append("     , TB_BGTDGR G\n");
        sb.append("     , TB_DGRDEPT D\n");
        sb.append("     , TB_DGRBIZ B\n");
        sb.append(" WHERE C.fis_year = R.fis_year\n");
        sb.append("   AND C.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND C.te_bgt_compo_id = R.te_bgt_compo_id\n");
        sb.append("   AND C.compo_level = '1'\n");
        sb.append("   AND (C.cng_type IS NULL OR C.cng_type = 'CH02' OR (C.cng_type = 'CH01' AND C.grp_lvl <> '2'))\n");
        sb.append("   AND G.fis_year = R.fis_year\n");
        sb.append("   AND G.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND D.fis_year = R.fis_year\n");
        sb.append("   AND D.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND D.dept_cd = C.dept_cd\n");
        sb.append("   AND B.fis_year = R.fis_year\n");
        sb.append("   AND B.bgt_dgr = R.bgt_dgr\n");
        sb.append("   AND B.dbiz_cd = C.dbiz_cd\n");
        sb.append("   AND R.report_cd = '").append(reportCd).append("'\n");
        sb.append("   AND ").append(yearPredicate("R.fis_year", years)).append("\n");
        sb.append("   AND ").append(yearPredicate("C.fis_year", years)).append("\n");
        addYearPredicateArgs(args, years);
        addYearPredicateArgs(args, years);

        if ("10".equals(bgtCompoFg)) {
            sb.append("   AND G.bgt_compo_fg = '10'\n");
        } else if ("20".equals(bgtCompoFg)) {
            sb.append("   AND G.bgt_compo_fg = '20'\n");
            if (addTimes > 0) {
                sb.append("   AND G.add_times = ?\n");
                args.add(Integer.valueOf(addTimes));
            }
        }

        if (checkbox != null && checkbox.active()) {
            appendCheckboxOrFilters(sb, checkbox, args);
        } else {
            appendReportBranchFilters(sb, bizKeyword, deptKeyword, tagKeyword, implKeyword,
                    contentField, contentKeyword, broadKeyword, args);
        }

        sb.append("   GROUP BY R.fis_year, R.bgt_dgr, R.te_bgt_compo_id\n");
        sb.append("\n");
    }

    /**
     * 체크박스 검색 — 대소문자·띄어쓰기 무시.
     * 키워드 식: A&B,C = (A AND B) OR C.
     * 각 term은 선택된 필드 중 하나에 있으면 충족한다.
     */
    private void appendCheckboxOrFilters(StringBuilder sb, CheckboxSearch chk, List<Object> args) {
        // 행정운영경비 제외 (기존과 동일)
        sb.append("   AND NOT (B.pbiz_nm LIKE '%행정운영경비%'\n");
        sb.append("            AND (B.ubiz_nm LIKE '%기본경비%' OR B.ubiz_nm LIKE '%인력운영비%'\n");
        sb.append("                 OR B.dbiz_nm LIKE '%기본경비%' OR B.dbiz_nm LIKE '%인력운영비%'))\n");

        List<String> fields = new ArrayList<String>();
        if (chk.bizNm) {
            fields.add("C.comp_ground");
            fields.add("B.dbiz_nm");
        }
        if (chk.gubun) {
            fields.add("R.demand_cont");
            fields.add("R.invest_plan");
        }
        if (chk.exam) {
            fields.add("R.exam_cont");
        }
        if (chk.srchVal) {
            fields.add("R.srch_val");
        }
        if (fields.isEmpty()) {
            return;
        }

        StringBuilder expressionSql = new StringBuilder();
        List<List<String>> groups = chk.expression.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            if (expressionSql.length() > 0) {
                expressionSql.append(" OR ");
            }
            expressionSql.append("(");
            List<String> andTerms = groups.get(g);
            for (int t = 0; t < andTerms.size(); t++) {
                if (t > 0) {
                    expressionSql.append(" AND ");
                }
                expressionSql.append("(");
                for (int f = 0; f < fields.size(); f++) {
                    if (f > 0) {
                        expressionSql.append(" OR ");
                    }
                    expressionSql.append(spaceInsensitiveLikeCol(fields.get(f)));
                    addSpaceInsensitiveLikeArgs(args, andTerms.get(t), 1);
                }
                expressionSql.append(")");
            }
            expressionSql.append(")");
        }
        if (expressionSql.length() > 0) {
            sb.append("   AND (").append(expressionSql).append(")\n");
        }
    }

    /**
     * 브랜치(R/C/B/D 별칭) WHERE 에 필터를 push down 한다.
     * 컬럼 매핑: comp_ground·te_mng_mok_nm=C, dbiz_nm·ubiz_nm·pbiz_nm·fis_fg_nm=B,
     * dept_nm·office_nm=D, srch_val·demand_cont·exam_cont·invest_plan=R (gubun=R.demand_cont).
     */
    private void appendReportBranchFilters(StringBuilder sb, String bizKeyword, String deptKeyword,
            String tagKeyword, String implKeyword, String contentField, String contentKeyword,
            boolean broadKeyword, List<Object> args) {

        // 행정운영경비(정책사업)의 기본경비·인력운영비 사업은 심사 대상 정보가 아니므로 제외
        sb.append("   AND NOT (B.pbiz_nm LIKE '%행정운영경비%'\n");
        sb.append("            AND (B.ubiz_nm LIKE '%기본경비%' OR B.ubiz_nm LIKE '%인력운영비%'\n");
        sb.append("                 OR B.dbiz_nm LIKE '%기본경비%' OR B.dbiz_nm LIKE '%인력운영비%'))\n");

        // 사업명 키워드: 쉼표(,)로 여러 개를 받아 OR 검색.
        // 좁은 검색(!broadKeyword): 세세사업명·세부사업명 — 공백 없는 키워드는 REPLACE 없이 LIKE(운영 속도).
        // 넓은 검색: 검토의견·검색태그까지 확장(느림, AiEnableBroadSearch=true 시에만).
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
                    kwSql.append(spaceInsensitiveLikeCol("C.comp_ground")).append(" OR ").append(spaceInsensitiveLikeCol("B.dbiz_nm")).append(" OR ").append(spaceInsensitiveLikeCol("C.te_mng_mok_nm"));
                    kwSql.append(" OR ").append(spaceInsensitiveLikeCol("B.ubiz_nm")).append(" OR ").append(spaceInsensitiveLikeCol("B.pbiz_nm")).append(" OR ").append(spaceInsensitiveLikeCol("B.fis_fg_nm"));
                    addSpaceInsensitiveLikeArgs(args, kw, 6);
                    kwSql.append(" OR UPPER(R.srch_val) LIKE ? OR UPPER(R.demand_cont) LIKE ? OR UPPER(R.exam_cont) LIKE ?");
                    addCaseInsensitiveLikeArgs(args, kw, 3);
                } else {
                    // 평문 LIKE(빠름) + 키워드에 공백이 있을 때만 띄어쓰기무시 OR
                    boolean needSpaceIgnore = kw.indexOf(' ') >= 0 || kw.indexOf('\t') >= 0;
                    kwSql.append("(UPPER(B.dbiz_nm) LIKE ? OR UPPER(C.comp_ground) LIKE ?");
                    addCaseInsensitiveLikeArgs(args, kw, 2);
                    if (needSpaceIgnore) {
                        kwSql.append(" OR ").append(spaceInsensitiveLikeCol("B.dbiz_nm"))
                                .append(" OR ").append(spaceInsensitiveLikeCol("C.comp_ground"));
                        addSpaceInsensitiveLikeArgs(args, kw, 2);
                    }
                    kwSql.append(")");
                }
            }
            if (kwSql.length() > 0) {
                sb.append("   AND (").append(kwSql).append(")\n");
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
                deptSql.append("(UPPER(D.dept_nm) LIKE ? OR UPPER(D.office_nm) LIKE ?");
                deptSql.append(" OR UPPER(TRIM(CONCAT(NVL(D.office_nm,''), ' ', NVL(D.dept_nm,'')))) LIKE ?)");
                args.add(like);
                args.add(like);
                args.add(like);
            }
            if (deptSql.length() > 0) {
                sb.append("   AND (").append(deptSql).append(")\n");
            }
        }
        if (tagKeyword.length() > 0) {
            sb.append("   AND UPPER(R.srch_val) LIKE ?\n");
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
                // gubun = R.demand_cont (심사조서 [구분] 목록의 요구내용)
                implSql.append("UPPER(R.demand_cont) LIKE ? OR UPPER(R.invest_plan) LIKE ?");
                addCaseInsensitiveLikeArgs(args, kw, 2);
            }
            if (implSql.length() > 0) {
                sb.append("   AND (").append(implSql).append(")\n");
            }
        }

        if (contentKeyword != null && contentKeyword.length() > 0) {
            String like = toCaseInsensitiveLike(contentKeyword);
            if (CONTENT_FIELD_EXAM.equals(contentField)) {
                sb.append("   AND UPPER(R.exam_cont) LIKE ?\n");
                args.add(like);
            } else if (CONTENT_FIELD_GUBUN.equals(contentField)) {
                sb.append("   AND (UPPER(R.demand_cont) LIKE ? OR UPPER(R.invest_plan) LIKE ?)\n");
                args.add(like);
                args.add(like);
            }
        }
    }

    /** 심사조서 RAG 최종 답변 프롬프트 (페르소나·서식은 system_instruction 으로 별도 고정) */
    private String buildReportAnswerPrompt(String question, String context) {
        StringBuilder sb = new StringBuilder();
        sb.append("아래 [심사조서 데이터]만 근거로 [사용자 질문]에 답하라.\n");
        sb.append(REPORT_ANSWER_STYLE);
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

    private AiReportContextBuilder.ContextOptions copyContextOptions(
            AiReportContextBuilder.ContextOptions src) {
        AiReportContextBuilder.ContextOptions opts = AiReportContextBuilder.ContextOptions.defaults();
        if (src == null) {
            return opts;
        }
        opts.includeGubun = src.includeGubun;
        opts.includePreYearAmt = src.includePreYearAmt;
        opts.includeDemandAmt = src.includeDemandAmt;
        opts.includeTags = src.includeTags;
        opts.multiYearRangeQuery = src.multiYearRangeQuery;
        opts.maxReviewLength = src.maxReviewLength;
        opts.maxBlocks = src.maxBlocks;
        return opts;
    }

    /** 상세 창용 JSON 행 배열 — 조건검색어·예산재원단독열·소관부서단독열 제외 */
    private JSONArray toDetailRowsJson(List<Map<String, Object>> group,
            AiReportContextBuilder.ContextOptions options) {
        JSONArray arr = new JSONArray();
        List<Map<String, String>> rows = AiReportContextBuilder.buildMultiYearBizDetailRows(group, options);
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> line = rows.get(i);
            JSONObject o = new JSONObject();
            o.put("yearDgr", nullToEmpty(line.get("yearDgr")));
            o.put("fisFgNm", nullToEmpty(line.get("fisFgNm")));
            o.put("dept", nullToEmpty(line.get("dept")));
            o.put("gubun", nullToEmpty(line.get("gubun")));
            o.put("hasTot", nullToEmpty(line.get("hasTot")));
            o.put("totAmt", nullToEmpty(line.get("totAmt")));
            o.put("totFrsc", nullToEmpty(line.get("totFrsc")));
            o.put("preAmt", nullToEmpty(line.get("preAmt")));
            o.put("preFrsc", nullToEmpty(line.get("preFrsc")));
            o.put("demandAmt", nullToEmpty(line.get("demandAmt")));
            o.put("demandFrsc", nullToEmpty(line.get("demandFrsc")));
            o.put("adjAmt", nullToEmpty(line.get("adjAmt")));
            o.put("adjFrsc", nullToEmpty(line.get("adjFrsc")));
            o.put("exam", nullToEmpty(line.get("exam")));
            arr.add(o);
        }
        return arr;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
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

        String finalAnswer = llmClient.generateUserQuery(question);
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
     * LLM 응답 처리 — JSON 질문분류 계획 또는 budget_search API 자연어 답변.
     * 내부 AI(budget_search)는 user_query 원문에 대해 llm_result.answer(자연어)를 반환한다.
     */
    private JSONObject parsePlanOrDirectChatAnswer(String raw) {
        JSONObject planJson = tryParseJsonPlan(raw);
        if (planJson != null && planJson.containsKey("mode")) {
            return planJson;
        }
        JSONObject chat = new JSONObject();
        chat.put("mode", "chat");
        chat.put("needData", false);
        if (raw != null && raw.trim().length() > 0) {
            chat.put("answer", raw.trim());
        } else {
            chat.put("answer", "질문을 이해하지 못했습니다. 다시 한 번 구체적으로 질문해 주세요.");
        }
        return chat;
    }

    /** 모델 출력에서 JSON 계획 객체를 추출. 실패 시 null */
    private JSONObject tryParseJsonPlan(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.length() == 0) {
            return null;
        }

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
        } else {
            return null;
        }

        try {
            return JSONObject.fromObject(text);
        } catch (Exception e) {
            logger.info("LLM 응답이 JSON 계획이 아님 — 자연어 답변으로 처리: chars=" + raw.length());
            return null;
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

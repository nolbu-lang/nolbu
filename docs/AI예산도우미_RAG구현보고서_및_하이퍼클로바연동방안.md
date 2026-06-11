# 심사정보시스템 'AI 예산도우미' RAG 구현 보고서 및 하이퍼클로바 X 연동 방안

| 구분 | 내용 |
|---|---|
| 문서 목적 | 행정AI사업 담당부서 협의용 (샘플 시스템 시연 + 기술 검토 자료) |
| 작성일 | 2026. 6. 11. |
| 대상 시스템 | 예산편성 심사정보시스템 (eGovFrame / Spring MVC / CUBRID) |
| 샘플(데모) 주소 | http://99.1.21.27:8080/bcjis-webapp/ (사내망 전용, 담당자 PC가 서버) |
| 현재 LLM | Google Gemini 2.5 Flash (무료 API, 테스트용) |
| 목표 LLM | **네이버 하이퍼클로바 X (CLOVA Studio API, 시 계약분)** |

---

## 1. 개요

### 1.1 추진 배경
- 예산편성 심사 업무 중 '경상사업심사조서'·'투자사업심사조서'의 내용(사업별 요구내용, 검토의견, 차수별 반영액, 재원구성)을 자연어 질문으로 즉시 조회·요약하는 수요가 큼
- 이를 위해 기존 심사정보시스템(내부망, CUBRID DB)에 **RAG(Retrieval-Augmented Generation) 방식의 AI 챗봇**을 시범 구축함
- 현재는 검증용으로 Gemini 무료 API를 사용 중이며, 정식 운영 시 **시에서 계약한 네이버 하이퍼클로바 X로 교체**가 필요함

### 1.2 핵심 설계 원칙
1. **내부 데이터만 사용**: AI는 심사정보시스템 CUBRID DB에서 조회한 데이터만 근거로 답변 (외부 지식으로 지어내기 금지)
2. **LLM에 DB 직접 접근 금지**: LLM은 ① 질문 해석과 ② 조회된 결과의 요약·서식화만 담당. DB 조회는 서버(Java)가 파라미터 바인딩된 안전한 SQL로만 수행
3. **읽기 전용**: 어떤 경로로도 INSERT/UPDATE/DELETE/DDL 불가 (서버단 검증)
4. **LLM 교체 용이성**: LLM 호출부를 단일 클라이언트 클래스로 분리하여, Gemini → 하이퍼클로바 교체 시 해당 클래스만 수정하면 되는 구조

### 1.3 데모 시연 방법
1. 사내망 PC 브라우저에서 `http://99.1.21.27:8080/bcjis-webapp/` 접속 → 로그인
2. 화면 하단 중앙의 **「AI 예산편성 도우미」** 채팅창 클릭
3. 질문 템플릿 예시:
   - "2024년 경상 및 투자사업 중에서 **유가보조금, 유류비** 관련 사업을 전부 찾아서 **차수별 예산 추이와 심사의견 요약**을 정리해줘"
   - "2024년 경로당 관련 사업의 차수별 예산과 검토의견을 정리해줘"
4. 답변은 `[부서명] → [사업명] → [차수별 예산(국비/시비 구성)] → [검토의견]` 보고서 서식으로 출력되며, 하단에 근거 데이터 표가 함께 표시됨

---

## 2. 시스템 구성(아키텍처)

```
[브라우저 - 채팅 위젯]                       [WAS (Tomcat / Spring MVC)]                    [외부]
┌──────────────────┐   ① 질문(JSON)   ┌────────────────────────────────────┐
│ aiChat.jsp / .js │ ───────────────▶ │ AiChatController (/ai/ajaxAiChat.do)│
│ (화면 하단 위젯) │                  │   └ AiChatServiceImpl  ── RAG 파이프라인
│                  │ ◀─────────────── │        │ ②질문분류  ⑤답변생성        │   LLM API
│ 답변 말풍선 + 표 │   ⑥ 응답(JSON)   │        ▼                            │ ◀──────────▶
└──────────────────┘                  │   GeminiClient (LLM 호출부, 교체대상)│  (현재 Gemini,
                                      │        │ ③검색(SQL)                  │   목표 HyperCLOVA X)
                                      │        ▼                            │
                                      │   CUBRID DB (심사조서 테이블)        │
                                      │        │ ④컨텍스트 변환              │
                                      │        ▼                            │
                                      │   AiReportContextBuilder            │
                                      └────────────────────────────────────┘
```

### 2.1 소스 구성 (신규 개발분)

| 구분 | 파일 | 역할 |
|---|---|---|
| 입력 UI | `webapp/WEB-INF/views/ai/aiChat.jsp` | 채팅 위젯 화면(모든 화면 하단 공통 표시) |
| 입력 UI | `webapp/js/ai/aiChat.js` | 질문 전송(AJAX), 답변·표 렌더링 |
| 입력 UI | `webapp/css/ai/aiChat.css` | 위젯 스타일 |
| 컨트롤러 | `java/com/cs/bcjis/ai/web/AiChatController.java` | REST 엔드포인트 `/ai/ajaxAiChat.do` |
| 서비스 | `java/com/cs/bcjis/ai/service/impl/AiChatServiceImpl.java` | **RAG 파이프라인 본체** (질문분류→검색→컨텍스트→답변) |
| 컨텍스트 | `java/com/cs/bcjis/ai/AiReportContextBuilder.java` | DB 행 → AI 맞춤형 텍스트 뭉치 변환 유틸리티 |
| LLM 호출 | `java/com/cs/bcjis/ai/GeminiClient.java` | **LLM API 클라이언트 (하이퍼클로바 교체 지점)** |
| 스키마 | `java/com/cs/bcjis/ai/AiSchemaProvider.java` | DB 스키마 설명(일반 통계질문의 Text-to-SQL용) |
| 설정 | `resources/csframework/bcjisProps/globals.properties` | API 키·모델명·행수 제한 등 |

---

## 3. RAG 컨트롤러 구현 상세

### 3.1 입력부 (사용자 → 서버)

- 채팅 위젯에서 질문 입력 → jQuery AJAX로 JSON 전송

```
POST /ai/ajaxAiChat.do
Content-Type: application/json

{ "question": "2024년 경로당 관련 사업의 차수별 예산과 검토의견을 정리해줘" }
```

- `AiChatController`가 수신하여 `AiChatService.ask(question)` 호출. 세션 인증(기존 시스템 로그인)을 그대로 사용하므로 비로그인 접근 불가

### 3.2 처리 파이프라인 (4단계)

#### ① 질문 분류·계획 수립 (LLM 1차 호출)
- 질문을 LLM에 보내 아래 JSON으로 구조화 (모드 분류 + 검색조건 추출)

```json
{
  "mode": "report | sql | chat",
  "fisYear": "2024",            // 회계연도
  "dgr": "본예산 | 1회추경 | ''", // 차수 (없으면 모든 차수)
  "reportCd": "010 | 020 | ''",  // 경상/투자 (없으면 둘 다)
  "bizKeyword": "경로당,냉난방", // 사업명 키워드(쉼표로 다중)
  "deptKeyword": "",             // 부서 키워드
  "tagKeyword": ""               // #검색태그
}
```

- `report` = 심사조서 질문(주 사용 경로), `sql` = 일반 통계 질문(Text-to-SQL), `chat` = DB 불필요 질문

#### ② 데이터 검색 (CUBRID, LLM 미개입)
- 검색 대상은 **경상사업심사조서(TB_REPORT010)·투자사업심사조서(TB_REPORT020) 두 조서로 한정**
- 조서 + 예산편성내역(TB_DGRCOMPO, 통계목 헤더 합산) + 사업/부서 명칭(TB_DGRBIZ/TB_DGRDEPT) + 차수(TB_BGTDGR) + 재원별 편성액(TB_DGRCOMPOFRSC)을 사업 단위로 조인
- 안전장치 및 품질 규칙:
  - 모든 검색조건은 **PreparedStatement 파라미터 바인딩** (SQL 인젝션 원천 차단)
  - **행정운영경비(기본경비·인력운영비) 사업 자동 제외**
  - 결과 0건이면 키워드 범위를 검토의견·검색태그까지 **자동 확장하여 2차 검색**
  - 최대 20건(설정값) 제한, 무거운 재원상세 집계는 최종 결과에만 수행(성능 최적화)

#### ③ AI 맞춤형 컨텍스트 변환 (`AiReportContextBuilder`)
- 심사조서는 병합 셀·재원 교차 구조라 원시 데이터를 그대로 주면 LLM이 오해함
- 이를 **사업 1건 = 텍스트 블록 1개**로 변환하고, **같은 사업이 여러 차수에 걸치면 한 블록 안에 차수별 섹션으로 그룹핑**

```
■ 사업 1
- 조서구분: 경상사업심사조서
- 사업명: 경로당 냉난방비 및 양곡비 (정책사업: … / 통계목: 308-01 …)
- 소관부서: 사회복지국 노인복지과
- 차수별 내역 (총 3개 차수):
  [2024년 본예산]
  · 예산액(단위: 백만원): 기정액 0, 예산요구액 380, 조정액(반영액) 380, 증감액 +380
  · 반영액 재원구성(단위: 백만원): 국비 253, 시비 127
  · 검토의견 및 추진상황:
    ○ 요구내용: (원문 유지)
    ◈ 검토의견: (원문 유지)
  [2024년 1회추경] …
  [2024년 2회추경] …
- 조건검색어: #민생
```

- 변환 규칙: 금액 **백만원 통일**(DB 원 단위 환산), **국비는 종류(국고보조·교부세 등) 통합**, 시비/기타 구분, 차수는 '본예산'·'N회추경' 표준 표기

#### ④ 답변 생성 (LLM 2차 호출)
- **system_instruction(시스템 지침)** 으로 페르소나·출력 서식을 강제:
  - "부산시 예산담당관실에서 2013년부터 예산편성업무를 한 최고수준 전문가 AI 도우미"
  - 보고서 서식 고정: `[부서명] → [사업명] → [차수별 예산(국비/시비)] → [검토의견]`
  - 심사조서에 있는 내용만 표시(종합의견·총평 금지), 데이터에 없는 내용 생성 금지
- 사용자 질문 + ③의 컨텍스트를 본문 프롬프트로 전달

### 3.3 출력부 (서버 → 사용자)

```json
{
  "answer":   "1. [사회복지국 노인복지과]\n2. [경로당 냉난방비 및 양곡비]\n3. [차수별 예산] …",
  "columns":  ["사업명", "소관부서", "차수", "요구액(백만원)", "조정액(백만원)"],
  "dataList": [ { "사업명": "…", "소관부서": "…", … } ],
  "rowCount": 4
}
```

- `answer` → 채팅 말풍선(보고서 서식), `columns`+`dataList` → 근거 데이터 표로 화면에 함께 렌더링

### 3.4 안정성·보안 장치 요약

| 항목 | 내용 |
|---|---|
| SQL 안전성 | SELECT 단일 구문만 허용(키워드 차단), 파라미터 바인딩, 행수 제한(LIMIT) |
| 인증 | 기존 시스템 세션 로그인 필수 |
| 장애 대응 | LLM 일시 과부하(503) 자동 재시도, 쿼터 소진(429) 시 예비 모델 자동 전환(10분 기억) |
| 망 제약 대응 | 일부 API IP 차단 환경 대비, 접속 가능 IP 자동 탐색 후 우회 접속(TLS·인증서 검증 유지) |
| 응답 품질 | 출력 토큰 한도 확대 + 내부 사고 토큰 제한(답변 잘림 방지, 응답속도 개선) |

---

## 4. 하이퍼클로바 X 연동을 위한 기술 요구사항 및 수정사항

### 4.1 대상 API: CLOVA Studio Chat Completions v3

(네이버클라우드 공식 가이드 기준, 2026. 6. 현재)

| 항목 | 내용 |
|---|---|
| 엔드포인트 | `POST https://clovastudio.stream.ntruss.com/v3/chat-completions/{모델명}` |
| 모델 예시 | `HCX-007`(고성능·추론), `HCX-005`(텍스트+이미지), `HCX-DASH-002`(경량·고속) |
| 인증 | HTTP 헤더 `Authorization: Bearer {CLOVA Studio API 키}` (예: `nv-…`) |
| 보조 헤더 | `X-NCP-CLOVASTUDIO-REQUEST-ID`(선택, 요청 추적용), `Content-Type: application/json` |
| 부가 기능 | Structured Outputs(JSON Schema 출력), Function calling, OpenAI 호환 API 제공 |
| 참고 | 구 버전 URL(`clovastudio.apigw.ntruss.com`)은 지원 중단 예정 → 신규 URL 사용 권장 |

> ※ 시 계약이 **공공존/뉴로클라우드(전용 인프라)** 형태인 경우 엔드포인트 도메인과 키 발급 절차가 위와 다를 수 있으므로, 4.4의 확인 요청 목록을 담당자와 협의 필요

### 4.2 요청/응답 구조 매핑 (Gemini → HyperCLOVA X)

현재 코드가 LLM에 보내는 정보는 ① 시스템 지침, ② 사용자 프롬프트, ③ 생성 파라미터 3가지뿐이므로 매핑이 단순함.

| 현재 (Gemini generateContent) | 변경 후 (CLOVA Chat Completions v3) |
|---|---|
| `system_instruction.parts[].text` | `messages[0] = { "role": "system", "content": "…" }` |
| `contents[0].parts[0].text` (사용자 프롬프트) | `messages[1] = { "role": "user", "content": "…" }` |
| `generationConfig.temperature` (0.2) | `temperature` (0.2) |
| `generationConfig.maxOutputTokens` | `maxTokens` |
| `generationConfig.thinkingConfig` (사고 토큰 제한) | HCX-007 추론 옵션으로 대응(미지원 모델은 생략) |
| 응답: `candidates[0].content.parts[].text` | 응답: `result.message.content` |
| 오류: HTTP 429/503 | 오류: HTTP 429(쿼터)/5xx — 동일한 재시도·폴백 로직 재사용 |

요청 예시(참고):

```bash
curl -X POST 'https://clovastudio.stream.ntruss.com/v3/chat-completions/HCX-007' \
  -H 'Authorization: Bearer {CLOVA Studio API 키}' \
  -H 'Content-Type: application/json' \
  -d '{
    "messages": [
      { "role": "system", "content": "너는 부산시 예산담당관실에서 … (페르소나/서식 지침)" },
      { "role": "user",   "content": "[사용자 질문] … [심사조서 데이터] …" }
    ],
    "temperature": 0.2,
    "maxTokens": 4096,
    "topP": 0.8,
    "repetitionPenalty": 1.1
  }'
```

### 4.3 소스코드 수정사항 (예상 공수: 2~3일)

| 순번 | 수정 항목 | 내용 | 공수 |
|---|---|---|---|
| 1 | `HyperClovaClient.java` 신규 작성 | `GeminiClient`와 동일한 메서드 시그니처(`generate(systemInstruction, prompt)`)로 CLOVA Studio v3 호출 구현. 기존의 TLS1.2 설정, 타임아웃, 503 재시도 로직 재사용 | 1일 |
| 2 | LLM 공급자 전환 설정 | `globals.properties`에 `Globals.AiProvider = hyperclova` 추가, `AiChatServiceImpl`이 설정값에 따라 Gemini/HyperCLOVA 클라이언트를 선택하도록 분기 (운영=하이퍼클로바, 개발테스트=Gemini 병행 가능) | 0.5일 |
| 3 | 설정 항목 추가 | `Globals.ClovaApiKey`, `Globals.ClovaEndpoint`(공공존 대비 도메인 설정화), `Globals.ClovaModel`(HCX-007 등), `Globals.ClovaMaxTokens` | 포함 |
| 4 | 질문 분류 단계 JSON 안정화 | 1차 호출(질문분류)은 JSON 출력이 필수 → CLOVA **Structured Outputs**(JSON Schema) 적용 권장. 미적용 시 현재의 JSON 추출·검증 로직(코드펜스 제거, 파싱 실패 시 안전 폴백)이 그대로 동작하므로 필수는 아님 | 0.5일 |
| 5 | 토큰 한도 검토 | 컨텍스트(심사조서 최대 20건, 건당 약 1~3KB)와 모델별 최대 입력/출력 토큰을 비교하여 `Globals.GeminiMaxReportBlocks`(현 20건) 조정. HCX-DASH 계열 사용 시 블록 수 축소 필요 가능 | 0.5일 |
| 6 | 통합 테스트 | 데모 질문 세트(차수별 추이, 키워드 검색, 부서별, 태그 검색)로 회귀 테스트 | 0.5일 |

> 프롬프트(페르소나·서식·데이터 컨텍스트)는 모델 중립적으로 작성되어 있어 **수정 없이 재사용** 가능. 다만 모델별 어투 차이가 있으므로 시범 운영 중 페르소나 문구 미세조정 권장.

### 4.4 행정AI사업 담당자에게 확인 요청할 사항 (체크리스트)

| 번호 | 확인 항목 | 비고 |
|---|---|---|
| 1 | 계약 형태: CLOVA Studio 공용(ntruss.com) / 공공존 / 뉴로클라우드(전용 구축형) 여부 | 엔드포인트 도메인 결정 |
| 2 | 사용 가능 모델명과 버전 (HCX-007 / HCX-005 / HCX-DASH-002 등) | 품질·속도·단가 트레이드오프 |
| 3 | API 키 발급 절차 및 키 관리 정책 (테스트용/서비스용 구분) | `Bearer nv-…` 형식 |
| 4 | 호출 한도: 분당 요청 수(RPM)·분당 토큰(TPM)·일일 한도, 과금 기준 | 동시 사용자 수 산정 근거 |
| 5 | 심사정보시스템 서버(업무망)에서 API 엔드포인트(443)로의 **방화벽 허용** 절차 | 망분리 환경 필수 협의 |
| 6 | 예산 데이터(비공개 행정정보)를 프롬프트로 전송하는 것에 대한 **보안성 검토 요건** | 학습 미사용 보장, 로그 보관 정책 등 |
| 7 | Structured Outputs / Function calling 사용 가능 여부 (계약 플랜에 따라 상이할 수 있음) | 질문분류 단계 안정화용 |
| 8 | 장애·점검 공지 채널, 기술지원 창구 | 운영 SLA |

### 4.5 운영 전환 시 권장 사항

1. **이중화 기간 운영**: 설정 전환 방식(`Globals.AiProvider`)을 이용해 하이퍼클로바를 기본, Gemini를 개발용으로 병행 후 안정화되면 Gemini 키 폐기
2. **요청 로그**: 질문/생성SQL/응답요약을 기존 `TB_TRACELOG` 체계에 기록하여 사용량·품질 모니터링
3. **키 보안**: API 키는 `globals.properties`(서버 내부)에만 보관, 화면·로그 노출 금지
4. **부하 보호**: 현재 행수 제한(20건)·토큰 제한 유지, 필요시 사용자별 호출 횟수 제한 추가

---

## 5. 맺음말

- 본 샘플은 **"LLM은 해석·요약만, 데이터는 내부 DB에서 안전하게"** 라는 구조로 구현되어, LLM 공급자 교체가 클라이언트 클래스 1개 수준의 작업으로 가능하도록 설계되어 있음
- 하이퍼클로바 X의 Chat Completions v3는 현재 사용 중인 Gemini API와 메시지 구조가 사실상 1:1로 대응되므로, **연동의 기술적 난이도는 낮음**
- 관건은 코드보다 **계약 형태 확인(엔드포인트), 업무망 방화벽 허용, 보안성 검토** 등 행정 절차이므로, 4.4 체크리스트 기준으로 담당부서와 협의를 요청드림

*(끝)*

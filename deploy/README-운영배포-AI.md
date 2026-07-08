# AI 예산편성 도우미 — 운영 서버(99.1.1.39) 속도 재점검·배포 (2026-07-08)

운영 URL: http://99.1.1.39:8080/main/main.do

## PC vs 운영 속도차 — 점검 결과와 조치

| # | 원인 | 점검 | 조치 |
|---|------|------|------|
| 1 | 운영 DB 대용량 | 운영 > PC | 인덱스·검색 경로 최적화 |
| 2 | 인덱스 미적용 | `check-ai-indexes.ps1` | `apply-indexes.ps1` 필수 |
| 3 | REPLACE/LIKE | 코드 | 공백 없으면 평문 UPPER LIKE |
| 4 | CLOB 넓은 검색 | 설정 | `AiEnableBroadSearch=false` |
| 5 | 010∪020 UNION | 코드 | 010→020 순차 |
| 6 | 재원 일괄조회 | 코드 | 상한 trim 후 FRSC, 배치 OR |
| 7 | 상세 JSON 중복 | 코드 | 사업당 `_detailRows` 1회만 |
| 8 | LLM | 설정 | `AiReportDbOnly=true` |

---

## 관리자 적용 순서 (필수)

### A. 인덱스 점검 → 적용

```powershell
# 1) 누락 확인
.\scripts\check-ai-indexes.ps1 -DbPassword "운영DB비밀번호"

# 2) 누락 시 (또는 최초 1회)
.\scripts\apply-indexes.ps1 -DbPassword "운영DB비밀번호"
```

`MISSING` 이 나오면 AI 검색이 PC보다 현저히 느립니다. 반드시 적용하세요.

### B. globals.properties (기존 DB URL은 유지)

```properties
Globals.AiReportDbOnly = true
Globals.AiMaxReportBlocks = 50
Globals.AiNearbyYearCount = 0
Globals.AiSearchAllYearsFallback = false
Globals.AiEnableBroadSearch = false
Globals.AiFrscBatchSize = 40
Globals.AiQueryTimeoutSec = 45
Globals.AiPerfLog = true
```

### C. WAR 배포

전달물: `bcjis-배포-20260708` (또는 최신 ZIP) 의 `bcjis-webapp.war`

1. Tomcat 중지  
2. `webapps\bcjis-webapp` 삭제  
3. WAR 복사  
4. Tomcat 기동  
5. Ctrl+F5 (`aiChat.js?v=20260708b`)

### D. 확인

질의: `2026년 경상사업 및 투자사업에서 일상돌봄 사업을 찾아줘`

`catalina.out` 예:

```
AI PERF[searchReport] ms=...
AI PERF[enrichFrsc] ms=...
AI RAG hit[1-biz-narrow] ...
```

- `searchReport` 수 초 이상 → 인덱스·넓은검색 설정 재확인  
- `hit[2-biz-broad]` → `AiEnableBroadSearch` 가 true 인지 확인 후 false  

---

## 한 번에

```powershell
.\deploy\deploy-all.ps1 -TomcatHome "Tomcat경로" -DbPassword "DB비밀번호"
.\scripts\check-ai-indexes.ps1 -DbPassword "DB비밀번호"
```

globals 속도 설정 + Tomcat 재기동 필수.

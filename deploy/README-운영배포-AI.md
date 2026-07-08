# AI 예산편성 도우미 — 운영 서버(99.1.1.39) 속도·배포 절차

운영 URL: http://99.1.1.39:8080/main/main.do

## PC는 빠른데 운영만 느린 이유

| 원인 | PC | 운영(99.1.1.39) |
|------|----|-----------------|
| 조서·세세목 데이터량 | 적음 | 대용량 |
| 인덱스 | 로컬 적용됨 | **미적용이면 수십 배 느림** |
| `REPLACE()` 사업명 LIKE | 체감 적음 | 풀스캔 |
| 요구·검토 CLOB 넓은 검색 | 드묾 | **자동 재시도 시 급격히 느림** |
| 010∪020 UNION | 가벼움 | 옵티마이저 부담 |
| LLM | db-only면 생략 | `AiReportDbOnly=false`면 추가 지연 |

---

## 1단계 — 개발 PC에서 WAR 빌드

```powershell
cd "프로젝트루트"
.\scripts\build.ps1
```

생성물: `target\bcjis-webapp.war`

---

## 2단계 — 운영 DB 인덱스 (**속도의 핵심, 필수**)

```powershell
.\scripts\apply-indexes.ps1 -DbPassword "운영DB비밀번호"
```

또는:

```powershell
.\deploy\deploy-db.ps1 -DbPassword "운영DB비밀번호" -RunMenuPatch
```

이번 배포 추가 인덱스:

- `ix_dgrcompo_compground` — 세세사업명
- `ix_dgrbiz_dbiz_nm` — 세부사업명

이미 있으면 스크립트가 건너뜁니다.

---

## 3단계 — 운영 `globals.properties` (속도 설정)

기존 DB 접속 정보는 유지하고 **아래만 추가·확인**:

```properties
Globals.AiReportDbOnly = true
Globals.AiMaxReportBlocks = 50

# 연도 미지정 시 인근 연도 확장 없음 (0 = 최신연도만)
Globals.AiNearbyYearCount = 0
Globals.AiSearchAllYearsFallback = false

# 요구·검토 CLOB 넓은 검색 OFF (운영 필수)
Globals.AiEnableBroadSearch = false

Globals.AiFrscBatchSize = 40
Globals.AiPerfLog = true
```

`deploy/globals.properties.ai-snippet.example` 참고.  
수정 후 **Tomcat 재기동**.

---

## 4단계 — WAR 배포

```powershell
.\deploy\deploy-app.ps1 -TomcatHome "운영Tomcat경로"
```

수동: Tomcat 중지 → `webapps\bcjis-webapp` 삭제 → WAR 복사 → 기동

---

## 5단계 — 확인

1. http://99.1.1.39:8080/main/main.do 로그인
2. Ctrl+F5
3. 질의: `2026년 경상사업 및 투자사업에서 일상돌봄 사업을 찾아줘`
4. `catalina.out`에서:

```
AI PERF[searchReport] ms=...
AI PERF[queryReport] ms=...
AI PERF[enrichFrsc] ms=...
AI RAG hit[1-biz-narrow] ...
```

- `searchReport`가 **수 초 이상**이면 → 인덱스 미적용 또는 `AiEnableBroadSearch`/`AiReportDbOnly` 점검
- `hit[2-biz-broad]`가 보이면 → 넓은 검색이 켜져 있음 (`false`로 변경)

---

## 이번 코드 개선 요약 (2026-07-08)

| 개선 | 내용 |
|------|------|
| 좁은 검색 LIKE | 공백 없으면 `REPLACE` 없이 `UPPER(col) LIKE` |
| 넓은 검색 | 기본 **OFF** (`AiEnableBroadSearch=false`) |
| 조서 010/020 | UNION 대신 **순차 조회** (first-hit 시 010만으로 종료 가능) |
| 재원 enrich | 큰 IN 절 → **배치 OR** + FRSC 선행 조인 |
| 연도 확장 | 기본 **0** (명시 연도만) |
| 인덱스 | `comp_ground`, `dbiz_nm` 추가 |

---

## 한 번에

```powershell
.\deploy\deploy-all.ps1 -TomcatHome "Tomcat경로" -DbPassword "운영DB비밀번호"
```

Tomcat 재기동 + `globals.properties` 속도 설정 반영 필수.

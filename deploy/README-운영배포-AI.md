# AI 예산편성 도우미 — 운영 서버(99.1.1.39) 배포 절차

운영 URL: http://99.1.1.39:8080/main/main.do

## 1단계 — 개발 PC에서 WAR 빌드

```powershell
cd "프로젝트루트"
.\scripts\build.ps1
```

생성물: `target\bcjis-webapp.war`

---

## 2단계 — 운영 DB 인덱스 적용 (최초 1회 + loaddb 후)

**운영 WAS 서버(99.1.1.39)에서** CUBRID `csql` 접근 가능한 계정으로 실행합니다.

```powershell
cd "프로젝트루트"
.\deploy\deploy-db.ps1 -DbPassword "운영DB비밀번호" -RunMenuPatch
```

인덱스만 적용:

```powershell
.\scripts\apply-indexes.ps1 -DbPassword "운영DB비밀번호"
```

`scripts\create-indexes.sql` — `TB_DGRCOMPO`, `TB_REPORT010/020`, `TB_DGRCOMPOFRSC` 등 AI 검색·화면 조회용 인덱스.

---

## 3단계 — 운영 globals.properties 확인

서버의 `WEB-INF/classes/csframework/bcjisProps/globals.properties` (또는 Tomcat 외부 설정)에 아래를 추가·확인:

```properties
# 심사조서 검색 시 LLM 생략(표·상세는 DB만) — 속도 필수
Globals.AiReportDbOnly = true

# 연도 미지정 시 추가 검색 연도 수 (운영 1 권장)
Globals.AiNearbyYearCount = 1

# 전체 연도 순차 검색 폴백 — 운영 false 권장
Globals.AiSearchAllYearsFallback = false

# 구간별 소요시간 로그 (catalina.out 에 AI PERF[...] 출력)
Globals.AiPerfLog = true
```

---

## 4단계 — WAR 배포

### A. 스크립트 (운영 Tomcat 경로 확인 후)

```powershell
.\deploy\deploy-app.ps1 -TomcatHome "D:\was\apache-tomcat-9.0.89"
```

### B. 수동

1. Tomcat **중지**
2. `webapps\bcjis-webapp` 폴더 삭제 (있으면)
3. `target\bcjis-webapp.war` → `webapps\bcjis-webapp.war` 복사
4. Tomcat **기동**
5. 로그에서 `Deployment of web application archive ... has finished` 확인

---

## 5단계 — 배포 후 확인

1. http://99.1.1.39:8080/main/main.do 로그인
2. 예산편성 화면 하단 **AI 예산편성 도우미** 위젯 표시 확인
3. 브라우저 개발자도구 → Network: `aiChat.css?v=20260706b`, `aiChat.js?v=20260706b` 로드 확인 (구버전 캐시면 Ctrl+F5)
4. 테스트 질의: `2026년 경상사업 및 투자사업에서 일상돌봄 사업을 찾아줘`
5. Tomcat `catalina.out` 에 `AI PERF[searchReport] ms=...` 로그 확인 — 수 초 이상이면 인덱스 미적용·DB 부하 점검

---

## 속도 저하 원인 (테스트 PC vs 운영)

| 원인 | 조치 |
|------|------|
| 운영 DB 데이터량·인덱스 미적용 | 2단계 인덱스 스크립트 실행 |
| 연도 미지정 시 5개년+전체연도 순차 검색 | `AiNearbyYearCount=1`, `AiSearchAllYearsFallback=false` |
| 사업명 넓은 검색(검토의견 CLOB) | 1차 좁은 검색(세세사업명·세부사업명) 우선 — 이번 빌드 반영 |
| LLM 호출 | `AiReportDbOnly=true` |
| 브라우저 캐시(구 JS/CSS) | `?v=20260706b` 갱신 후 강력 새로고침 |

---

## 한 번에 (DB + WAR)

```powershell
.\deploy\deploy-all.ps1 -TomcatHome "Tomcat경로" -DbPassword "운영DB비밀번호"
```

Tomcat 재기동 필수.

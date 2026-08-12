# bcjis 개선본 — 관리자 배포 안내

> **최신 운영 배포 절차(2026-08-12):**  
> → **`docs/운영서버_배포_가이드_20260812.md`**  
> → (참고) `docs/운영서버_배포_가이드_20260807.md`  
> → 인수 요약: **`docs/AI작업자_인수인계_참고사항.md`**

## 0. 개선 범위 요약

| 과제 | 내용 |
|------|------|
| **1. 예산편성·심사정보 개선** | 전년도예산/조서·예산조회, 조서작성, 사업설명서 매칭, DB 인덱스 |
| **2. AI 예산도우미** | 상단 팝업 — 내부검색·법령·매뉴얼·JSON내보내기 + 내부 LLM |

상세 적용 순서·파일 목록은 **`docs/운영서버_배포_가이드_20260807.md`** 를 따르세요.

---

## 1. 관리직원에게 전달할 것

### A. 필수 (애플리케이션)

| 전달물 | 설명 |
|--------|------|
| **`bcjis-webapp.war`** | Maven 빌드 결과물 (패키지 루트 또는 `target/`) |

개발 PC에서 패키지 생성:

```powershell
cd "프로젝트루트"
.\scripts\package-deploy.ps1
```

→ `bcjis-배포-YYYYMMDD.zip` 생성 (WAR + 스크립트 + 문서 일괄)

### B. 필수 (DB — 기존 DB 그대로 연결)

| 전달물 | 실행 시점 | 설명 |
|--------|-----------|------|
| `scripts/apply-indexes.ps1` | **최초 1회 + loaddb 후** | 조회 속도 개선 인덱스 (중복 skip) |
| `scripts/check-ai-indexes.ps1` | 배포 전후 | 인덱스 누락 점검 |
| `scripts/patch-menu-budget-copy.sql` | **1회** | 전년도예산/조서 적용 메뉴 URL 통합 |
| `scripts/patch-menu-budget-select-all.sql` | **1회 (필수)** | 조서·집계 / 심사조서 보고항목선택 메뉴 분리·배치 |
| `scripts/apply-menu-budget-select.ps1` | **1회 (권장)** | 위 SQL 적용 + `check-menu-budget-select.ps1` 점검 |

### C. 조건부 (DB)

| 전달물 | 실행 시점 | 설명 |
|--------|-----------|------|
| `scripts/seed-comm-seq.sql` | loaddb·DB 재구축 후 | AJAX 채번 — **운영 중이면 DBA 협의** |
| `scripts/create-tb-bizdesc.sql` 등 | 사업설명서 매칭 사용 시 | 테이블·컬럼 |
| `scripts/add-menu-budget-select-split.sql` 등 | (구버전) | → **`patch-menu-budget-select-all.sql`** 사용 |

### D. AI 예산도우미 (설정만 추가, WAR에 포함됨)

| 전달물 | 설명 |
|--------|------|
| `deploy/globals.properties.ai-snippet.example` | 운영 `globals.properties`에 **추가**할 AI 설정 템플릿 |

- 기존 DB URL·비밀번호는 **변경하지 않음**
- WAS → 내부 LLM 서버(`99.1.82.207:8080`) **방화벽 개방** 필요 (행정AI 담당 확인)
- 매뉴얼 폴더: `Globals.AiManualStorePath` (기본 `C:/bcjis/upload/ai-manual/`)

### E. 전달하지 않는 것

- `globals.properties` 원본 (서버별 비밀 — Git 미포함)
- `src/` 전체 소스 (WAR + 문서면 충분, GitHub 가능)
- `target/` 폴더 전체

---

## 2. 적용 순서 (권장)

```
1. 운영 DB 백업
2. check-ai-indexes.ps1 → (필요 시) apply-indexes.ps1
3. **apply-menu-budget-select.ps1** (또는 deploy-db -RunMenuPatch) — **필수**
4. patch-menu-budget-copy.sql (미적용 시)
4. (loaddb 직후만) seed-comm-seq.sql — DBA 협의
5. globals.properties 에 AI 설정 추가 (deploy/globals.properties.ai-snippet.example)
6. Tomcat 중지
7. webapps/bcjis-webapp.war 교체 (기존 폴더 삭제 권장)
8. Tomcat 기동
9. Ctrl+F5 → AI 예산도우미·내부검색·법령·JSON내보내기·기존 메뉴 확인
```

자세한 스모크 테스트표: `docs/운영서버_배포_가이드_20260807.md` §⑥

---

## 3. 자동 배포 스크립트 (Windows WAS)

| 스크립트 | 용도 |
|----------|------|
| `deploy-app.ps1` | WAR 빌드 + Tomcat `webapps` 복사 |
| `deploy-db.ps1` | 인덱스 / (선택) 시드 / 메뉴 패치 |
| `deploy-all.ps1` | DB → WAR 순 일괄 |

```powershell
.\deploy\deploy-db.ps1 -DbPassword "DB비밀번호" -RunMenuPatch
.\deploy\deploy-app.ps1 -TomcatHome "D:\was\apache-tomcat-9.0.89"
# Tomcat 재기동 후 globals.properties AI 항목 확인
```

---

## 4. 배포 후 확인

| # | 항목 | 기대 결과 |
|---|------|-----------|
| 1 | 로그인 | 정상 |
| 2 | 전년도예산/조서·예산조회 | 목록 표시 |
| 3 | 예산안관리 → 심사조서 보고항목선택 | 분류항목·투자사업유형 화면 (조서작성 아래 **아님**) |
| 4 | 일반 메뉴 AJAX | "조회 실패" 없음 |
| 4 | AI 예산도우미 | 팝업, **내부 AI** 배지 |
| 5 | 내부검색 상세 | 회계 표시, `2026년 본예산` (예산차수 괄호 없음) |
| 6 | JSON내보내기 | 1년 단위 JSON 파일 저장 |
| 7 | 법령 `지방재정법 제17조` | 조문 요약 충실 |

---

## 5. 롤백

1. Tomcat 중지 → 백업 WAR 복원 → 기동
2. DB 인덱스·메뉴 변경은 롤백 필수 아님

---

## 6. 참고 문서

| 문서 | 용도 |
|------|------|
| `docs/운영서버_배포_가이드_20260807.md` | **서버관리자 본편** |
| `docs/AI작업자_인수인계_참고사항.md` | 인수·범위 요약 |
| `docs/운영배포_종합개선보고서.md` | 종합 설명 |
| `deploy/README-운영배포-AI.md` | AI 속도·인덱스 |
| `docs/AI예산편성도우미_사용설명서.md` | 사용자 매뉴얼 |

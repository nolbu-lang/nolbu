# bcjis 개선본 — 관리자 배포 안내 (2026-06)

## 0. 개선 범위 요약

| 과제 | 내용 |
|------|------|
| **1. 예산편성시스템 개선** | 전년도예산조서적용 경량 조회·일괄적용, AJAX 안정성, DB 인덱스 |
| **2. AI 예산도우미** | 화면 하단 챗봇 — 심사조서 DB 검색 + 내부 LLM Studio 연동 |

상세: `docs/운영배포_종합개선보고서.md`

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
| `scripts/patch-menu-budget-copy.sql` | **1회** | 메뉴 URL 통합 |

### C. 조건부 (DB)

| 전달물 | 실행 시점 | 설명 |
|--------|-----------|------|
| `scripts/seed-comm-seq.sql` | loaddb·DB 재구축 후 | AJAX 채번 — **운영 중이면 DBA 협의** |

### D. AI 예산도우미 (설정만 추가, WAR에 포함됨)

| 전달물 | 설명 |
|--------|------|
| `deploy/globals.properties.ai-snippet.example` | 운영 `globals.properties`에 **추가**할 AI 설정 템플릿 |

- 기존 DB URL·비밀번호는 **변경하지 않음**
- WAS → 내부 LLM 서버(`99.1.82.207:8080`) **방화벽 개방** 필요 (행정AI 담당 확인)

### E. 전달하지 않는 것

- `globals.properties` 원본 (서버별 비밀 — Git 미포함)
- `src/` 전체 소스 (WAR + 문서면 충분)
- `target/` 폴더 전체

---

## 2. 적용 순서 (권장)

```
1. 운영 DB 백업
2. patch-menu-budget-copy.sql 실행
3. apply-indexes.ps1 실행
4. (loaddb 직후만) seed-comm-seq.sql — DBA 협의
5. globals.properties 에 AI 설정 추가 (deploy/globals.properties.ai-snippet.example)
6. Tomcat 중지
7. webapps/bcjis-webapp.war 교체 (기존 폴더 삭제 권장)
8. Tomcat 기동
9. 로그인 → 전년도예산/조서 적용 메뉴 + AI 챗봇 테스트
```

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
| 2 | 전년도예산/조서 적용[신규] 조회 | 목록 빠르게 표시 |
| 3 | 일반 메뉴 AJAX | "조회 실패" 없음 |
| 4 | AI 예산편성 도우미 (화면 하단) | 패널 표시, **내부 AI** 배지 |
| 5 | AI 일반 질문 | LLM 답변 (연결: hyperclova) |
| 6 | AI 심사조서 질문 | DB 검색 결과 (연결: db-only 가능) |

---

## 5. 롤백

1. Tomcat 중지 → 백업 WAR 복원 → 기동
2. DB 인덱스·메뉴 변경은 롤백 필수 아님

---

## 6. 참고 문서

| 문서 | 용도 |
|------|------|
| `docs/운영배포_종합개선보고서.md` | **전체 개선·배포 종합** |
| `docs/업무서버_적용_가이드.md` | 요약 가이드 |
| `docs/개선사항_보고서.md` | 예산편성 개선 상세 |
| `docs/AI예산편성도우미_사용설명서.md` | 사용자 매뉴얼 |

# bcjis 개선본 — 관리자 배포 안내

## 1. 관리직원에게 전달할 것

### A. 필수 (애플리케이션)

| 전달물 | 설명 |
|--------|------|
| **`target/bcjis-webapp.war`** | Maven 빌드 결과물 (또는 `deploy-app.ps1` 실행으로 Tomcat에 직접 복사) |

빌드 방법 (개발 PC):

```powershell
cd "프로젝트루트"
.\scripts\build.ps1
```

생성 파일: `target\bcjis-webapp.war`

### B. 필수 (DB — 기존 DB 그대로 연결)

| 전달물 | 실행 시점 | 설명 |
|--------|-----------|------|
| `scripts/apply-indexes.ps1` | **최초 1회 + loaddb 후** | 조회 속도 개선 인덱스 (중복은 자동 skip) |
| `scripts/patch-menu-budget-copy.sql` | **1회** | 메뉴 URL 통합 (메뉴 이름은 유지) |

### C. 조건부 (DB)

| 전달물 | 실행 시점 | 설명 |
|--------|-----------|------|
| `scripts/seed-comm-seq.sql` | loaddb·DB 재구축 후 | AJAX 채번(TB_COMM_SEQ) — **운영 중이면 DBA 협의** |

### D. 전달하지 않는 것

- `globals.properties` — **서버별 DB 접속 정보** (Git 미포함, 기존 파일 유지)
- `target/` 전체 — WAR 하나만 전달하면 됨

---

## 2. 자동 배포 스크립트 (Windows)

프로젝트 `deploy` 폴더:

| 스크립트 | 용도 |
|----------|------|
| `deploy-app.ps1` | WAR 빌드 + Tomcat `webapps` 복사 |
| `deploy-db.ps1` | 인덱스 / (선택) 시드 / 메뉴 패치 |
| `deploy-all.ps1` | 위 두 가지 순서 실행 |

### 예시

```powershell
cd "프로젝트루트"

# DB (인덱스 + 메뉴)
.\deploy\deploy-db.ps1 -DbPassword "DB비밀번호" -RunMenuPatch

# WAR
.\deploy\deploy-app.ps1 -TomcatHome "D:\was\apache-tomcat-9.0.89"

# 또는 한 번에
.\deploy\deploy-all.ps1 -TomcatHome "D:\was\apache-tomcat-9.0.89" -DbPassword "DB비밀번호"
```

배포 후 **Tomcat 재기동** 필수.

---

## 3. 수동 배포 (스크립트 없을 때)

1. 운영 DB **백업**
2. `csql` 로 `patch-menu-budget-copy.sql` 실행
3. `apply-indexes.ps1` 실행 (또는 DBA가 인덱스 DDL 적용)
4. Tomcat 중지 → `webapps/bcjis-webapp.war` 교체 → 기동
5. 로그인 → **전년도예산/조서 적용[신규]** 메뉴 조회·적용 확인

---

## 4. 변경된 주요 소스 (참고)

| 영역 | 파일 |
|------|------|
| 기존 화면 + 경량 조회 | `budgetCopy.jsp`, `budgetCopy.js` |
| 경량 API·일괄적용 | `BudgetCopyNewController`, `BudgetCopyNew*.java`, `BudgetCopyNew.xml` |
| AJAX 안정성 | `AccessLogFilter.java` |
| DB | `create-indexes.sql`, `seed-comm-seq.sql`, `patch-menu-budget-copy.sql` |

메뉴 **표시 이름**은 바꾸지 않습니다. 클릭 시 **기존과 같은 화면**(`budgetCopy.do`)이 열리고, **조회는 개선된 API**를 사용합니다.

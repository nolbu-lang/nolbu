# AI 작업자 인수인계 참고사항 (2026-07-16 기준)

다른 작업자(AI 코딩 도구, 예: Cursor)가 이 저장소로 작업을 시작하기 전에
반드시 알아야 할 운영상 특이사항 2가지를 정리한 문서입니다.

## 1. 전년도예산/조서 적용 메뉴가 두 벌 존재함

- **기존 메뉴**는 이름·URL 그대로 유지되어 있음 (예: `/budget/budgetCopy.do`) — 임의로 변경/제거하지 말 것
- **개선판(신규)**은 `/budget/budgetCopyMap.do` 로 **별도 추가**되어 있고, 기존 메뉴와 별개로 적용됨 (매핑 후 일괄 적용 화면)
- 즉 같은 기능이 "기존 메뉴 + 신규 budgetCopyMap" 두 벌 존재. 한쪽만 보고 로직을 파악하거나 수정하면 안 됨.

관련 파일:

| 구분 | 경로 |
|---|---|
| Controller | `src/main/java/com/cs/bcjis/budget/web/BudgetCopyNewController.java` |
| View | `src/main/webapp/WEB-INF/views/budget/budgetCopyMap.jsp` |
| JS | `src/main/webapp/js/budget/budgetCopyMap.js` |
| 메뉴 DB 패치 | `scripts/patch-menu-budget-copy.sql`, `scripts/restore-menu-budget-copynew.sql` |

작업 시 주의: 기존 `budgetCopy` 관련 로직을 수정할 때 `budgetCopyMap` 쪽에도 동일한 영향이 있는지(또는 그 반대) 반드시 함께 확인할 것.

## 2. 실서버는 WAR 배포가 아니라 소스 자체로 구동 중

- 운영 서버는 WAR를 패키징해서 교체하는 방식이 아니라, **소스(exploded) 자체를 직접 실행/수정하는 방식**으로 구동 중임
- 실서버 파일을 직접 수정하면 그대로 반영되는 구조이며, git 저장소와 실서버 소스가 어긋나 있던 이력이 있음
  (커밋 `2052c98 현재 소스 동기화: git 기준(예전 소스)을 실제 운영 작업 소스로 전면 반영` 참고)
- 따라서 작업 시:
  - `docs/업무서버_적용_가이드.md` 등 기존 배포 문서는 **WAR 교체 절차 기준**으로 작성되어 있으나, 실제 운영 반영은 소스 직접 수정 방식일 수 있으므로 **배포/반영 방식은 실제 서버 담당자에게 재확인** 후 진행할 것
  - git에 커밋한다고 해서 자동으로 실서버에 반영되지 않음 — 별도 반영(소스 전달/적용) 절차가 필요함
  - 로컬 개발/빌드 자체는 기존과 동일 (`docs/LOCAL-SETUP.md`, `scripts/build.ps1`)

## 참고 문서

| 문서 | 내용 |
|---|---|
| `docs/LOCAL-SETUP.md` | 로컬 개발환경 구성 |
| `docs/업무서버_적용_가이드.md` | 운영 배포 절차 요약 (WAR 기준 — 위 2번 항목 참고하여 실제 반영 방식 재확인 필요) |
| `docs/운영배포_종합개선보고서.md` | 종합 개선 보고 |

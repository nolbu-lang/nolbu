/* =====================================================================
   전년도예산/조서 적용[신규] 메뉴 통합
   - 메뉴 **이름(menu_nm)은 변경하지 않음**
   - 실행 URL 은 /budget/budgetCopy.do (기존 화면) 로 통일
   - 개발용 budgetCopyNew 전용 메뉴가 별도로 있으면 숨김(use_yn='N')
   - 운영 DB에서 menu_cd / menu_nm 이 다를 수 있으므로 DBA 확인 후 실행
   ===================================================================== */

/* 1) 전년도예산/조서 관련 메뉴 URL → 기존 화면(budgetCopy.do) */
UPDATE tb_menu
   SET url = '/budget/budgetCopy.do'
 WHERE use_yn = 'Y'
   AND (url LIKE '%budgetCopyNew%'
        OR url LIKE '%budgetCopy%'
        OR menu_nm LIKE '%전년도%조서%적용%');

/* 2) budgetCopyNew URL 만 가진 중복 메뉴 숨김 (이름은 그대로 두고 목록에서만 제외) */
UPDATE tb_menu
   SET use_yn = 'N'
 WHERE url = '/budget/budgetCopyNew.do';

COMMIT;

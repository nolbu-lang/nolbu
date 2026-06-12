/* =====================================================================
   로컬 PC 테스트용 — 전년도예산조서적용[신규] 메뉴를 개선 화면으로 복원
   (patch-menu-budget-copy.sql 적용 후 budgetCopy.do 로 바뀐 경우 되돌림)
   ===================================================================== */

UPDATE tb_menu
   SET url = '/budget/budgetCopyNew.do',
       use_yn = 'Y'
 WHERE menu_nm LIKE '%전년도%조서%적용%신규%'
    OR menu_nm LIKE '%전년도%예산%조서%적용%신규%'
    OR menu_nm LIKE '%전년도예산조서적용%신규%';

COMMIT;

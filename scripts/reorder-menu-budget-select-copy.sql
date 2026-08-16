/* =====================================================================
   예산안관리(MUBG00000) 하위 메뉴 순서 변경

   변경 전:
     1) 전년도예산조서적용[신규]   (MUBG10000)
     2) 조서·집계 항목선택         (MUBG05000)
     3) 심사조서 보고항목선택       (MUBG05100)

   변경 후:
     1) 조서·집계 항목선택         (MUBG05000)  line_up_no = 1
     2) 전년도예산조서적용[신규]   (MUBG10000)  line_up_no = 2
     3) 심사조서 보고항목선택       (MUBG05100)  line_up_no = 3
   ===================================================================== */

UPDATE tb_menu
   SET line_up_no = 1,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05000'
    OR url = '/budget/budgetSelectNew.do';

UPDATE tb_menu
   SET line_up_no = 2,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG10000'
    OR url = '/budget/budgetCopyNew.do';

UPDATE tb_menu
   SET line_up_no = 3,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05100'
    OR url = '/budget/budgetSelectAttr.do';

COMMIT;

-- 메뉴명: 보고항목·분류항목 → 심사조서 보고항목선택
UPDATE tb_menu
   SET menu_nm = '심사조서 보고항목선택',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05100'
    OR url LIKE '%budgetSelectAttr%';

COMMIT;

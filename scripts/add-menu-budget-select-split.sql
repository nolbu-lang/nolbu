/* =====================================================================
   단기: 조서·집계 항목선택 / 보고항목·사전절차 메뉴 분리
   - MUBG05000 : 조서·집계 항목선택  → /budget/budgetSelectNew.do
   - MUBG05100 : 보고항목·사전절차   → /budget/budgetSelectAttr.do
   ===================================================================== */

/* 1) 조서·집계 항목선택 (기존 메뉴 명칭·URL 정리) */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05000', 'MUBG00000', '조서·집계 항목선택', NULL, 2,
       'Y', '/budget/budgetSelectNew.do', 50, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05000'
       );

UPDATE tb_menu
   SET url = '/budget/budgetSelectNew.do',
       use_yn = 'Y',
       menu_nm = '조서·집계 항목선택',
       line_up_no = 50,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05000';

INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'BC001', 'MUBG05000', 'Y', 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu
            WHERE pow_gr_cd = 'BC001' AND menu_cd = 'MUBG05000'
       );

UPDATE tb_powgrpmenu
   SET use_yn = 'Y', rw_fg = 'Y',
       modi_id = 'USER_ADMIN', modi_date = SYSTIMESTAMP
 WHERE pow_gr_cd = 'BC001' AND menu_cd = 'MUBG05000';

/* 2) 보고항목·사전절차 */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05100', 'MUBG00000', '보고항목·사전절차', NULL, 2,
       'Y', '/budget/budgetSelectAttr.do', 51, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05100'
       )
   AND NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE url = '/budget/budgetSelectAttr.do'
       );

UPDATE tb_menu
   SET url = '/budget/budgetSelectAttr.do',
       use_yn = 'Y',
       menu_nm = '보고항목·사전절차',
       up_menu_cd = 'MUBG00000',
       line_up_no = 51,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05100'
    OR url LIKE '%budgetSelectAttr%';

INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'BC001', 'MUBG05100', 'Y', 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu
            WHERE pow_gr_cd = 'BC001' AND menu_cd = 'MUBG05100'
       );

UPDATE tb_powgrpmenu
   SET use_yn = 'Y', rw_fg = 'Y',
       modi_id = 'USER_ADMIN', modi_date = SYSTIMESTAMP
 WHERE pow_gr_cd = 'BC001' AND menu_cd = 'MUBG05100';

COMMIT;

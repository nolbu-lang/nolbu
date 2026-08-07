/* =====================================================================
   로컬 PC — 예산심사조서, 집계표항목 선택 메뉴 추가
   (서버 http://99.1.1.39:8080 의 동일 메뉴를 로컬에서 사용)
   URL: /budget/budgetSelectNew.do
   ===================================================================== */

/* 1) 메뉴 */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05000', 'MUBG00000', '예산심사조서, 집계표항목 선택', NULL, 2,
       'Y', '/budget/budgetSelectNew.do', 0, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05000'
       )
   AND NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE url = '/budget/budgetSelectNew.do'
       );

/* 이미 있으면 URL/사용여부만 보정 */
UPDATE tb_menu
   SET url = '/budget/budgetSelectNew.do',
       use_yn = 'Y',
       menu_nm = '예산심사조서, 집계표항목 선택',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05000'
    OR url LIKE '%budgetSelectNew%'
    OR menu_nm LIKE '%예산심사조서%집계표%';

/* 2) 권한 (BC001 — 로컬 관리자 그룹) */
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
   SET use_yn = 'Y',
       rw_fg = 'Y',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE pow_gr_cd = 'BC001'
   AND menu_cd = 'MUBG05000';

COMMIT;

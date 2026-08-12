/* =====================================================================
   예산안관리 — 조서·집계 / 심사조서 보고항목선택 메뉴 일괄 정리
   (운영 서버: WAR만 배포 시 메뉴가 조서작성/보고항목서식 아래로 잘못 보이는 문제 해결)

   목표 구조 (예산안관리 MUBG00000 하위):
     - MUBG05000  조서·집계 항목선택        → /budget/budgetSelectNew.do
     - MUBG05100  심사조서 보고항목선택    → /budget/budgetSelectAttr.do

   ※ 조서작성(reportWrite055 등) 메뉴는 이름·URL 을 변경하지 않음
   ===================================================================== */

/* 0) 조서작성 쪽에 잘못 붙은 '심사조서 보고항목' 명칭 복원 (URL 이 reportWrite055 인 경우) */
UPDATE tb_menu
   SET menu_nm = '보고항목서식[신규]',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE url LIKE '%reportWrite055%'
   AND (menu_nm LIKE '%심사조서%보고%'
        OR menu_nm LIKE '%보고항목선택%');

/* 1) 조서·집계 항목선택 */
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
       up_menu_cd = 'MUBG00000',
       menu_level = 2,
       lowest_menu_yn = 'Y',
       menu_nm = '조서·집계 항목선택',
       line_up_no = 50,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05000'
    OR url = '/budget/budgetSelectNew.do';

/* 구 명칭(통합 메뉴) 숨김 — 분리 후 중복 표시 방지 */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd <> 'MUBG05000'
   AND use_yn = 'Y'
   AND url = '/budget/budgetSelectNew.do';

UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE use_yn = 'Y'
   AND menu_cd NOT IN ('MUBG05000', 'MUBG05100')
   AND menu_nm LIKE '%예산심사조서%집계표%'
   AND url IS NOT NULL
   AND url <> '/budget/budgetSelectNew.do'
   AND url <> '/budget/budgetSelectAttr.do';

/* 2) 심사조서 보고항목선택 */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05100', 'MUBG00000', '심사조서 보고항목선택', NULL, 2,
       'Y', '/budget/budgetSelectAttr.do', 51, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (
           SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05100'
       );

/* budgetSelectAttr URL 을 쓰는 모든 메뉴 → 예산안관리 하위·정식 명칭 */
UPDATE tb_menu
   SET url = '/budget/budgetSelectAttr.do',
       use_yn = 'Y',
       menu_nm = '심사조서 보고항목선택',
       up_menu_cd = 'MUBG00000',
       menu_level = 2,
       lowest_menu_yn = 'Y',
       line_up_no = 51,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05100'
    OR url LIKE '%budgetSelectAttr%';

/* MUBG05100 이 아닌데 같은 URL 을 가진 중복 메뉴 숨김 */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd <> 'MUBG05100'
   AND use_yn = 'Y'
   AND url LIKE '%budgetSelectAttr%';

/* 조서작성 등 다른 상위에 잘못 등록된 '심사조서 보고항목' 명칭 메뉴 숨김 (URL 이 attr 가 아닌 경우) */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd NOT IN ('MUBG05000', 'MUBG05100')
   AND use_yn = 'Y'
   AND up_menu_cd <> 'MUBG00000'
   AND (menu_nm LIKE '%심사조서%보고항목%'
        OR menu_nm LIKE '%보고항목·사전절차%'
        OR menu_nm LIKE '%보고항목·분류항목%');

/* 3) 권한 — MUBG05000 권한이 있는 모든 그룹에 MUBG05100 부여 */
INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT p.pow_gr_cd, 'MUBG05000', p.rw_fg, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM tb_powgrpmenu p
 WHERE p.menu_cd = 'MUBG05000'
   AND p.use_yn = 'Y'
   AND NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu x
            WHERE x.pow_gr_cd = p.pow_gr_cd AND x.menu_cd = 'MUBG05000'
       );

INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT p.pow_gr_cd, 'MUBG05100', p.rw_fg, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM tb_powgrpmenu p
 WHERE p.menu_cd = 'MUBG05000'
   AND p.use_yn = 'Y'
   AND NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu x
            WHERE x.pow_gr_cd = p.pow_gr_cd AND x.menu_cd = 'MUBG05100'
       );

UPDATE tb_powgrpmenu
   SET use_yn = 'Y',
       rw_fg = (SELECT MAX(p2.rw_fg) FROM tb_powgrpmenu p2
                 WHERE p2.pow_gr_cd = tb_powgrpmenu.pow_gr_cd
                   AND p2.menu_cd = 'MUBG05000'),
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd IN ('MUBG05000', 'MUBG05100');

COMMIT;

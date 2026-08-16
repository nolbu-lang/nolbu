/* =====================================================================
   예산안관리(MUBG00000) 메뉴 — 운영 PC(개선본)과 동일하게 동기화
   작성일: 2026-08-16

   [목표 메뉴 — use_yn='Y', 예산안관리 하위]
     1) 조서·집계 항목선택           MUBG05000  /budget/budgetSelectNew.do   line_up_no=1
     2) 전년도예산조서적용[신규]     MUBG10000  /budget/budgetCopyNew.do     line_up_no=2
     3) 심사조서 보고항목선택         MUBG05100  /budget/budgetSelectAttr.do  line_up_no=3

   [숨김]
     - 메뉴명에 [삭제예정] 포함
     - 전년도예산/조서 적용[매핑일괄테스트] 등 테스트·구버전 URL
     - budgetCopy.do / budgetPreCopy / budgetModify / budgetSheetSelect 등 구 화면
       (예산안관리 하위에서만 숨김 — 다른 상위 메뉴는 건드리지 않음)

   ※ WAR 배포만으로는 메뉴가 바뀌지 않습니다. 본 SQL을 운영 DB에 반드시 실행하세요.
   ===================================================================== */

/* ---------- 1) 조서·집계 항목선택 ---------- */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05000', 'MUBG00000', '조서·집계 항목선택', NULL, 2,
       'Y', '/budget/budgetSelectNew.do', 1, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05000');

UPDATE tb_menu
   SET url = '/budget/budgetSelectNew.do',
       use_yn = 'Y',
       up_menu_cd = 'MUBG00000',
       menu_level = 2,
       lowest_menu_yn = 'Y',
       menu_nm = '조서·집계 항목선택',
       line_up_no = 1,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05000';

/* ---------- 2) 전년도예산조서적용[신규] (개선 화면 budgetCopyNew) ---------- */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG10000', 'MUBG00000', '전년도예산조서적용[신규]', NULL, 2,
       'Y', '/budget/budgetCopyNew.do', 2, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG10000');

UPDATE tb_menu
   SET url = '/budget/budgetCopyNew.do',
       use_yn = 'Y',
       up_menu_cd = 'MUBG00000',
       menu_level = 2,
       lowest_menu_yn = 'Y',
       menu_nm = '전년도예산조서적용[신규]',
       line_up_no = 2,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG10000';

/* 이름에 전년도·조서·적용·신규 가 있는 다른 코드도 동일 URL로 맞춤 */
UPDATE tb_menu
   SET url = '/budget/budgetCopyNew.do',
       use_yn = 'Y',
       up_menu_cd = 'MUBG00000',
       line_up_no = 2,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd <> 'MUBG10000'
   AND up_menu_cd = 'MUBG00000'
   AND menu_nm LIKE '%전년도%조서%적용%신규%';

/* ---------- 3) 심사조서 보고항목선택 ---------- */
INSERT INTO tb_menu (
       menu_cd, up_menu_cd, menu_nm, menu_descr, menu_level,
       lowest_menu_yn, url, line_up_no, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT 'MUBG05100', 'MUBG00000', '심사조서 보고항목선택', NULL, 2,
       'Y', '/budget/budgetSelectAttr.do', 3, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM db_root
 WHERE NOT EXISTS (SELECT 1 FROM tb_menu WHERE menu_cd = 'MUBG05100');

UPDATE tb_menu
   SET url = '/budget/budgetSelectAttr.do',
       use_yn = 'Y',
       up_menu_cd = 'MUBG00000',
       menu_level = 2,
       lowest_menu_yn = 'Y',
       menu_nm = '심사조서 보고항목선택',
       line_up_no = 3,
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd = 'MUBG05100'
    OR (up_menu_cd = 'MUBG00000' AND url LIKE '%budgetSelectAttr%');

/* ---------- 4) 예산안관리 하위 — 구버전·테스트·삭제예정 메뉴 숨김 ---------- */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE up_menu_cd = 'MUBG00000'
   AND use_yn = 'Y'
   AND menu_cd NOT IN ('MUBG05000', 'MUBG05100', 'MUBG10000')
   AND (
           menu_nm LIKE '%삭제예정%'
        OR menu_nm LIKE '%매핑일괄%'
        OR menu_nm LIKE '%매핑일괄테스트%'
        OR url LIKE '%budgetCopyMap%'
        OR url = '/budget/budgetCopy.do'
        OR url LIKE '%budgetPreCopy%'
        OR url LIKE '%budgetModify%'
        OR url LIKE '%budgetSheetSelect%'
        OR url LIKE '%budgetSelect.do%'
        OR url LIKE '%budgetApply%'
       );

/* 정식 3개와 동일 URL을 쓰는 중복 메뉴 숨김 */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE use_yn = 'Y'
   AND menu_cd NOT IN ('MUBG05000', 'MUBG05100', 'MUBG10000')
   AND (
           url = '/budget/budgetSelectNew.do'
        OR url = '/budget/budgetSelectAttr.do'
        OR url = '/budget/budgetCopyNew.do'
       );

/* 조서작성 등 다른 상위에 잘못 붙은 보고항목선택 명칭 숨김 */
UPDATE tb_menu
   SET use_yn = 'N',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd NOT IN ('MUBG05000', 'MUBG05100', 'MUBG10000')
   AND use_yn = 'Y'
   AND up_menu_cd <> 'MUBG00000'
   AND (menu_nm LIKE '%심사조서%보고항목%'
        OR menu_nm LIKE '%보고항목·사전절차%'
        OR menu_nm LIKE '%보고항목·분류항목%')
   AND url NOT LIKE '%reportWrite%';

/* ---------- 5) 권한 — 기존 MUBG05000 권한 그룹에 05100·10000 부여 ---------- */
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

INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT p.pow_gr_cd, 'MUBG10000', p.rw_fg, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM tb_powgrpmenu p
 WHERE p.menu_cd = 'MUBG05000'
   AND p.use_yn = 'Y'
   AND NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu x
            WHERE x.pow_gr_cd = p.pow_gr_cd AND x.menu_cd = 'MUBG10000'
       );

/* MUBG10000 만 있는 환경: 예산안관리(MUBG00000) 권한 그룹에도 부여 */
INSERT INTO tb_powgrpmenu (
       pow_gr_cd, menu_cd, rw_fg, use_yn,
       regi_id, regi_date, modi_id, modi_date
)
SELECT p.pow_gr_cd, 'MUBG10000', p.rw_fg, 'Y',
       'USER_ADMIN', SYSTIMESTAMP, NULL, NULL
  FROM tb_powgrpmenu p
 WHERE p.menu_cd = 'MUBG00000'
   AND p.use_yn = 'Y'
   AND NOT EXISTS (
           SELECT 1 FROM tb_powgrpmenu x
            WHERE x.pow_gr_cd = p.pow_gr_cd AND x.menu_cd = 'MUBG10000'
       );

UPDATE tb_powgrpmenu
   SET use_yn = 'Y',
       modi_id = 'USER_ADMIN',
       modi_date = SYSTIMESTAMP
 WHERE menu_cd IN ('MUBG05000', 'MUBG05100', 'MUBG10000');

COMMIT;

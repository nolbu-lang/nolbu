/* =====================================================================
   BCJIS 성능 개선용 인덱스 DDL (전체)
   - loaddb 는 인덱스/PK 를 만들지 않으므로 데이터 적재 후 반드시 실행
   - SQL 맵 65개 파일의 WHERE/JOIN 패턴을 분석해 공통 접근 경로에 인덱스 부여
   - CUBRID는 CREATE INDEX IF NOT EXISTS 미지원 → 이미 있으면 해당 문만 오류(무해)
   ===================================================================== */

/* ===================================================================
   1. TB_DGRCOMPO (33만+건) — 예산 구성요소 트리/목록/집계의 핵심
   =================================================================== */
/* 상위→자식 집계: UP_TE_BGT_COMPO_ID + COMPO_LEVEL (BudgetComm 집계, CHILD_CNT) */
CREATE INDEX ix_dgrcompo_up ON tb_dgrcompo(fis_year, bgt_dgr, up_te_bgt_compo_id, compo_level);
/* 단건 접근/UPDATE: TE_BGT_COMPO_ID (copyReport, copyPreInfo, 조서 저장) */
CREATE INDEX ix_dgrcompo_te ON tb_dgrcompo(fis_year, bgt_dgr, te_bgt_compo_id);
/* 세세목(리프) 평면 목록: COMPO_LEVEL='1' + 연도/차수 (BudgetCopyNew 경량조회 등) */
CREATE INDEX ix_dgrcompo_leaf ON tb_dgrcompo(fis_year, bgt_dgr, compo_level);
/* 부서/사업 JOIN·필터 (TB_DGRDEPT/TB_DGRBIZ 조인 키) */
CREATE INDEX ix_dgrcompo_dept ON tb_dgrcompo(fis_year, bgt_dgr, dept_cd);
CREATE INDEX ix_dgrcompo_dbiz ON tb_dgrcompo(fis_year, bgt_dgr, dbiz_cd);
/* 통계목 범위 필터 */
CREATE INDEX ix_dgrcompo_temng ON tb_dgrcompo(fis_year, bgt_dgr, te_mng_mok_cd);
/* 병합그룹 조회 (BudgetApply CNG_TYPE) */
CREATE INDEX ix_dgrcompo_grp ON tb_dgrcompo(fis_year, bgt_dgr, grp_id, grp_lvl);

/* ===================================================================
   2. TB_DGRCOMPOFRSC / TB_DGRCOMPOCHAR — 재원·성질별 금액
   =================================================================== */
CREATE INDEX ix_dgrcompofrsc_te ON tb_dgrcompofrsc(fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd);
CREATE INDEX ix_dgrcompochar_te ON tb_dgrcompochar(fis_year, bgt_dgr, te_bgt_compo_id, char_fg_cd);

/* ===================================================================
   3. 차원(마스터) 테이블 — 목록 제목/콤보/필터
   =================================================================== */
CREATE INDEX ix_dgrbiz_dbiz ON tb_dgrbiz(fis_year, bgt_dgr, dbiz_cd);
CREATE INDEX ix_dgrbiz_fisfg ON tb_dgrbiz(fis_year, bgt_dgr, fis_fg_cd);
CREATE INDEX ix_dgrbiz_fisfgmst ON tb_dgrbiz(fis_year, bgt_dgr, fis_fg_mst_cd);
CREATE INDEX ix_dgrdept_dept ON tb_dgrdept(fis_year, bgt_dgr, dept_cd);
CREATE INDEX ix_dgrdept_office ON tb_dgrdept(fis_year, bgt_dgr, office_cd);
CREATE INDEX ix_dgrtemngmok_cd ON tb_dgrtemngmok(fis_year, bgt_dgr, te_mng_mok_cd);
CREATE INDEX ix_yearfisfg_cd ON tb_yearfisfg(fis_year, fis_fg_cd);
CREATE INDEX ix_yearfisfg_mst ON tb_yearfisfg(fis_year, fis_fg_mst_cd);
CREATE INDEX ix_yearfrsc_cd ON tb_yearfrsc(fis_year, frsc_fg_cd);
CREATE INDEX ix_bgtdgr_year ON tb_bgtdgr(fis_year, bgt_dgr);

/* ===================================================================
   4. 조서 테이블 TB_REPORTxxx — 조회/copyReport/저장 (157K~대용량)
      공통 WHERE: REPORT_CD, REPORT_DETL_CD, FIS_YEAR, BGT_DGR, TE_BGT_COMPO_ID
      보조 WHERE: FIS_YEAR, BGT_DGR, TE_BGT_COMPO_ID, REPORT_CD (다른 조서 참조 서브쿼리)
   =================================================================== */
CREATE INDEX ix_report010_key ON tb_report010(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report010_te ON tb_report010(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report020_key ON tb_report020(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report020_te ON tb_report020(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
/* tb_report030: 로컬 empty 스키마에 te_bgt_compo_id 없을 수 있음 → apply-indexes.ps1 에서 실패 시 무시 */
/* CREATE INDEX ix_report030_key ON tb_report030(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id); */
/* CREATE INDEX ix_report030_te ON tb_report030(fis_year, bgt_dgr, te_bgt_compo_id, report_cd); */
CREATE INDEX ix_report040_key ON tb_report040(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report040_te ON tb_report040(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report050_key ON tb_report050(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report050_te ON tb_report050(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report055_key ON tb_report055(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report055_te ON tb_report055(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report060_key ON tb_report060(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report060_te ON tb_report060(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report070_key ON tb_report070(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report070_te ON tb_report070(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report080_key ON tb_report080(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report080_te ON tb_report080(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report090_key ON tb_report090(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report090_te ON tb_report090(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report0a0_key ON tb_report0a0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0a0_te ON tb_report0a0(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report0b0_key ON tb_report0b0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0b0_te ON tb_report0b0(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report0c0_key ON tb_report0c0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0c0_te ON tb_report0c0(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);
CREATE INDEX ix_report0d0_key ON tb_report0d0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0d0_te ON tb_report0d0(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);

/* 조서 마스터 TB_REPORT */
CREATE INDEX ix_report_key ON tb_report(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report_te ON tb_report(fis_year, bgt_dgr, te_bgt_compo_id, report_cd);

/* ===================================================================
   5. 권한/메뉴/사용자 — 로그인·메뉴·부서권한 (매 화면 로드)
   =================================================================== */
CREATE INDEX ix_userdept_user ON tb_userdept(user_id, use_yn, dept_cd);
CREATE INDEX ix_userreport_user ON tb_userreport(user_id, use_yn, report_cd);
CREATE INDEX ix_userpowgrp_user ON tb_userpowgrp(user_id, use_yn, pow_gr_cd);
CREATE INDEX ix_powgrpmenu_grp ON tb_powgrpmenu(pow_gr_cd, use_yn, menu_cd);
CREATE INDEX ix_menu_up ON tb_menu(up_menu_cd, use_yn);
CREATE INDEX ix_menu_cd ON tb_menu(menu_cd, use_yn);

/* ===================================================================
   6. 공통코드 — 콤보/명칭 서브쿼리 (CL_CD + DETL_CD, 화면마다 반복)
   =================================================================== */
CREATE INDEX ix_commcddetl_cl ON tb_commcddetl(cl_cd, detl_cd, use_yn);
CREATE INDEX ix_commcd_cl ON tb_commcd(cl_cd, use_yn);

/* ===================================================================
   7. 기타 업무 테이블
   =================================================================== */
CREATE INDEX ix_dgrcompo_rlk_seq ON tb_dgrcompo_rlk(fis_year, bgt_dgr, te_bgt_compo_seq);
CREATE INDEX ix_dgrcompo_seq ON tb_dgrcompo(fis_year, bgt_dgr, te_bgt_compo_seq);
CREATE INDEX ix_cng_history_id ON tb_cng_history(cng_history_id);
CREATE INDEX ix_file_atch ON tb_file(atch_file_id);
CREATE INDEX ix_file_detl_atch ON tb_file_detl(atch_file_id);

/* =====================================================================
   성능 개선용 인덱스 DDL
   - 대상: 전년도예산조서적용(budgetCopyNew) "적용" 시 사용되는 집계/복사 쿼리
   - 원인: 대용량 테이블(TB_DGRCOMPO 등)에 인덱스가 없어 매 쿼리가 풀스캔됨
   - 효과(로컬 33만건 기준): 자식 집계 쿼리 약 200ms -> 1ms 미만 (약 60배)
   - 주의: CUBRID는 CREATE INDEX IF NOT EXISTS 미지원.
           이미 존재하면 해당 문만 오류 후 다음 문 계속 진행됨(무해).
   ===================================================================== */

/* ---- TB_DGRCOMPO : 구성요소 트리 집계의 핵심 ---- */
/* 상위->자식 집계: WHERE FIS_YEAR, BGT_DGR, UP_TE_BGT_COMPO_ID (+COMPO_LEVEL EXISTS) */
CREATE INDEX ix_dgrcompo_up ON tb_dgrcompo(fis_year, bgt_dgr, up_te_bgt_compo_id, compo_level);
/* 대상 행 단건 접근 / selectUpDgrcompoInfo: WHERE FIS_YEAR, BGT_DGR, TE_BGT_COMPO_ID */
CREATE INDEX ix_dgrcompo_te ON tb_dgrcompo(fis_year, bgt_dgr, te_bgt_compo_id);

/* ---- TB_DGRCOMPOFRSC / TB_DGRCOMPOCHAR : 재원/성질별 금액 복사·집계 ---- */
CREATE INDEX ix_dgrcompofrsc_te ON tb_dgrcompofrsc(fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd);
CREATE INDEX ix_dgrcompochar_te ON tb_dgrcompochar(fis_year, bgt_dgr, te_bgt_compo_id, char_fg_cd);

/* ---- 조서 테이블 : copyReport / 목록 조회 단건 접근 ----
   WHERE REPORT_CD, REPORT_DETL_CD, FIS_YEAR, BGT_DGR, TE_BGT_COMPO_ID */
CREATE INDEX ix_report010_key ON tb_report010(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report020_key ON tb_report020(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report030_key ON tb_report030(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report040_key ON tb_report040(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report050_key ON tb_report050(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report055_key ON tb_report055(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report060_key ON tb_report060(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report070_key ON tb_report070(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report080_key ON tb_report080(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report090_key ON tb_report090(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0a0_key ON tb_report0a0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0b0_key ON tb_report0b0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0c0_key ON tb_report0c0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
CREATE INDEX ix_report0d0_key ON tb_report0d0(report_cd, report_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);

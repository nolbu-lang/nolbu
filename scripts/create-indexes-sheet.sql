/* 예산심사조서·집계표항목선택 속도 개선용 인덱스 */
CREATE INDEX ix_sheet_te ON tb_sheet(fis_year, bgt_dgr, te_bgt_compo_id, sheet_cd);
CREATE INDEX ix_sheet_key ON tb_sheet(sheet_cd, sheet_detl_cd, fis_year, bgt_dgr, te_bgt_compo_id);
COMMIT;

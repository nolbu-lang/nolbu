-- 로컬/개발 DB: TB_DGRCOMPOFRSC 가 없는 심사조서 편성에 재원 행을 보완한다.
-- 대상: TB_REPORT010/020 에 연결된 compo (AI RAG 조회 범위와 동일)

INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
)
SELECT C.fis_year, C.bgt_dgr, C.te_bgt_compo_id, '130',
       0, C.bgt_amt, 0, 0,
       NVL(C.te_bgt_compo_seq, 0), 'bootstrap', SYSDATETIME
  FROM TB_DGRCOMPO C
 INNER JOIN (
       SELECT DISTINCT fis_year, bgt_dgr, te_bgt_compo_id
         FROM TB_REPORT020
        UNION
       SELECT DISTINCT fis_year, bgt_dgr, te_bgt_compo_id
         FROM TB_REPORT010
      ) R
    ON R.fis_year = C.fis_year
   AND R.bgt_dgr = C.bgt_dgr
   AND R.te_bgt_compo_id = C.te_bgt_compo_id
  LEFT JOIN TB_DGRCOMPOFRSC F
    ON F.fis_year = C.fis_year
   AND F.bgt_dgr = C.bgt_dgr
   AND F.te_bgt_compo_id = C.te_bgt_compo_id
   AND NVL(F.adj_def_frsc_amt, 0) <> 0
 WHERE C.compo_level = '1'
   AND NVL(C.bgt_amt, 0) <> 0
   AND F.te_bgt_compo_id IS NULL;

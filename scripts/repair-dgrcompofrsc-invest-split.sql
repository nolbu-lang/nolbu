-- 투자조서(TB_REPORT020): bootstrap 단일 자체재원(130) 행을
-- TB_REPORT020.tot_frsc_amt 비율에 맞게 국고보조금(111)+자체재원(130) 으로 분리한다.
-- (예: 조정 200백만원, tot 시비60·국비140 → ADJ 시비60·국비140)

DELETE FROM TB_DGRCOMPOFRSC F
 WHERE F.regi_id = 'bootstrap'
   AND F.frsc_fg_cd = '130'
   AND EXISTS (
         SELECT 1
           FROM TB_REPORT020 R
           JOIN TB_DGRCOMPO C
             ON C.fis_year = R.fis_year
            AND C.bgt_dgr = R.bgt_dgr
            AND C.te_bgt_compo_id = R.te_bgt_compo_id
            AND C.compo_level = '1'
          WHERE R.fis_year = F.fis_year
            AND R.bgt_dgr = F.bgt_dgr
            AND R.te_bgt_compo_id = F.te_bgt_compo_id
            AND NVL(C.bgt_amt, 0) > 0
            AND NVL(R.tot_frsc_amt1, 0) > 0
            AND NVL(R.tot_frsc_amt2, 0) > 0
            AND F.adj_def_frsc_amt = C.bgt_amt
       );

INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
)
SELECT X.fis_year, X.bgt_dgr, X.te_bgt_compo_id, X.frsc_fg_cd,
       0, X.adj_amt, 0, 0,
       X.te_bgt_compo_seq, 'bootstrap', SYSDATETIME
  FROM (
        SELECT C.fis_year, C.bgt_dgr, C.te_bgt_compo_id, C.te_bgt_compo_seq,
               '111' AS frsc_fg_cd,
               CAST((C.bgt_amt * NVL(R.tot_frsc_amt2, 0)
                     / NULLIF(NVL(R.tot_frsc_amt1, 0) + NVL(R.tot_frsc_amt2, 0)
                            + NVL(R.tot_frsc_amt3, 0) + NVL(R.tot_frsc_amt4, 0)
                            + NVL(R.tot_frsc_amt5, 0) + NVL(R.tot_frsc_amt6, 0), 0)) AS NUMERIC(19,0)) AS adj_amt
          FROM TB_DGRCOMPO C
          JOIN TB_REPORT020 R
            ON R.fis_year = C.fis_year
           AND R.bgt_dgr = C.bgt_dgr
           AND R.te_bgt_compo_id = C.te_bgt_compo_id
         WHERE C.compo_level = '1'
           AND NVL(C.bgt_amt, 0) > 0
           AND NVL(R.tot_frsc_amt1, 0) > 0
           AND NVL(R.tot_frsc_amt2, 0) > 0
           AND NOT EXISTS (
                 SELECT 1 FROM TB_DGRCOMPOFRSC F
                  WHERE F.fis_year = C.fis_year
                    AND F.bgt_dgr = C.bgt_dgr
                    AND F.te_bgt_compo_id = C.te_bgt_compo_id
                    AND F.frsc_fg_cd = '111'
                    AND NVL(F.adj_def_frsc_amt, 0) <> 0
               )
        UNION ALL
        SELECT C.fis_year, C.bgt_dgr, C.te_bgt_compo_id, C.te_bgt_compo_seq,
               '130' AS frsc_fg_cd,
               C.bgt_amt
               - CAST((C.bgt_amt * NVL(R.tot_frsc_amt2, 0)
                     / NULLIF(NVL(R.tot_frsc_amt1, 0) + NVL(R.tot_frsc_amt2, 0)
                            + NVL(R.tot_frsc_amt3, 0) + NVL(R.tot_frsc_amt4, 0)
                            + NVL(R.tot_frsc_amt5, 0) + NVL(R.tot_frsc_amt6, 0), 0)) AS NUMERIC(19,0)) AS adj_amt
          FROM TB_DGRCOMPO C
          JOIN TB_REPORT020 R
            ON R.fis_year = C.fis_year
           AND R.bgt_dgr = C.bgt_dgr
           AND R.te_bgt_compo_id = C.te_bgt_compo_id
         WHERE C.compo_level = '1'
           AND NVL(C.bgt_amt, 0) > 0
           AND NVL(R.tot_frsc_amt1, 0) > 0
           AND NVL(R.tot_frsc_amt2, 0) > 0
           AND NOT EXISTS (
                 SELECT 1 FROM TB_DGRCOMPOFRSC F
                  WHERE F.fis_year = C.fis_year
                    AND F.bgt_dgr = C.bgt_dgr
                    AND F.te_bgt_compo_id = C.te_bgt_compo_id
                    AND F.frsc_fg_cd = '130'
                    AND NVL(F.adj_def_frsc_amt, 0) <> 0
               )
       ) X
 WHERE X.adj_amt > 0;

-- 연도별 TB_DGRCOMPOFRSC 보완 (플레이스홀더 @YEAR@ 를 bootstrap 스크립트가 치환)
-- 1) 투자조서+tot_frsc 비율 → 국고(111)+자체(130) ADJ_DEF 분리 삽입
-- 2) 그 외 미보유 편성 → 자체(130) 단일행 (경상 등, 재원 미입력 시)

-- (1) 투자조서 국비·시비 분리
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
            ON R.fis_year = C.fis_year AND R.bgt_dgr = C.bgt_dgr AND R.te_bgt_compo_id = C.te_bgt_compo_id
         WHERE C.fis_year = '@YEAR@'
           AND C.compo_level = '1'
           AND NVL(C.bgt_amt, 0) <> 0
           AND NVL(R.tot_frsc_amt1, 0) > 0
           AND NVL(R.tot_frsc_amt2, 0) > 0
           AND NOT EXISTS (
                 SELECT 1 FROM TB_DGRCOMPOFRSC F
                  WHERE F.fis_year = C.fis_year AND F.bgt_dgr = C.bgt_dgr
                    AND F.te_bgt_compo_id = C.te_bgt_compo_id
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
            ON R.fis_year = C.fis_year AND R.bgt_dgr = C.bgt_dgr AND R.te_bgt_compo_id = C.te_bgt_compo_id
         WHERE C.fis_year = '@YEAR@'
           AND C.compo_level = '1'
           AND NVL(C.bgt_amt, 0) <> 0
           AND NVL(R.tot_frsc_amt1, 0) > 0
           AND NVL(R.tot_frsc_amt2, 0) > 0
           AND NOT EXISTS (
                 SELECT 1 FROM TB_DGRCOMPOFRSC F
                  WHERE F.fis_year = C.fis_year AND F.bgt_dgr = C.bgt_dgr
                    AND F.te_bgt_compo_id = C.te_bgt_compo_id
               )
       ) X
 WHERE X.adj_amt > 0;

-- (2) 재원 행이 전혀 없는 심사조서 편성 — 자체재원 단일행
INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
)
SELECT C.fis_year, C.bgt_dgr, C.te_bgt_compo_id, '130',
       0, C.bgt_amt, 0, 0,
       NVL(C.te_bgt_compo_seq, 0), 'bootstrap', SYSDATETIME
  FROM TB_DGRCOMPO C
 WHERE C.fis_year = '@YEAR@'
   AND C.compo_level = '1'
   AND NVL(C.bgt_amt, 0) <> 0
   AND (EXISTS (SELECT 1 FROM TB_REPORT020 R
                 WHERE R.fis_year = C.fis_year AND R.bgt_dgr = C.bgt_dgr
                   AND R.te_bgt_compo_id = C.te_bgt_compo_id)
        OR EXISTS (SELECT 1 FROM TB_REPORT010 R
                    WHERE R.fis_year = C.fis_year AND R.bgt_dgr = C.bgt_dgr
                      AND R.te_bgt_compo_id = C.te_bgt_compo_id))
   AND NOT EXISTS (
         SELECT 1 FROM TB_DGRCOMPOFRSC F
          WHERE F.fis_year = C.fis_year AND F.bgt_dgr = C.bgt_dgr
            AND F.te_bgt_compo_id = C.te_bgt_compo_id
       );

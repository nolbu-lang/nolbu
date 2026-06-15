-- 로컬 검증용: 지역사랑상품권발행지원 (te_bgt_compo_id=R0000242454) 조정재원 시드
-- 본예산(bgt_dgr=1): 시비 50,000백만원 (자체재원 frsc_fg_cd=130 → FRSC_AMT1/시비 슬롯)
-- 1회추경(bgt_dgr=4): 국비 50,000 + 시비 50,000백만원

DELETE FROM TB_DGRCOMPOFRSC
 WHERE fis_year = '2026'
   AND te_bgt_compo_id = 'R0000242454'
   AND bgt_dgr IN (1, 4);

INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
) VALUES (
       '2026', 1, 'R0000242454', '130',
       0, 50000000000, 0, 0,
       242454, 'seed', SYSDATETIME
);

INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
) VALUES (
       '2026', 4, 'R0000242454', '130',
       0, 50000000000, 0, 0,
       242454, 'seed', SYSDATETIME
);

INSERT INTO TB_DGRCOMPOFRSC (
       fis_year, bgt_dgr, te_bgt_compo_id, frsc_fg_cd,
       dmn_def_frsc_amt, adj_def_frsc_amt, pre_def_frsc_amt, pre_frsc_amt,
       te_bgt_compo_seq, regi_id, regi_date
) VALUES (
       '2026', 4, 'R0000242454', '111',
       0, 50000000000, 0, 0,
       242454, 'seed', SYSDATETIME
);

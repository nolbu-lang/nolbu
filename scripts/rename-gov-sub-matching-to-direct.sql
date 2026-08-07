-- 국고보조(RP013) 035: 국고-매칭펀드 → 국비직접지원
-- 집계표 상세(RT002) TF1: 매칭펀드사업(국비기관 직접교부) → 국비직접지원
UPDATE TB_COMMCDDETL
   SET DETL_CD_NM = '국비직접지원'
 WHERE CL_CD = 'RP013'
   AND DETL_CD = '035'
   AND DETL_CD_NM = '국고-매칭펀드';

UPDATE TB_COMMCDDETL
   SET DETL_CD_NM = '국비직접지원'
 WHERE CL_CD = 'RT002'
   AND DETL_CD = 'TF1'
   AND (DETL_CD_NM LIKE '%매칭펀드%' OR DETL_CD_NM = '국고-매칭펀드');

-- 확인
SELECT CL_CD, DETL_CD, DETL_CD_NM
  FROM TB_COMMCDDETL
 WHERE (CL_CD = 'RP013' AND DETL_CD = '035')
    OR (CL_CD = 'RT002' AND DETL_CD = 'TF1');

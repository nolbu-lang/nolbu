/* =====================================================================
   TB_COMM_SEQ 시드 (ID 채번 시퀀스)
   - 원인: loaddb 로 적재 시 TB_COMM_SEQ 가 비어 있어, EgovTableIdGnrService
           (file/user/cng/tracelog/pledge 채번)가 "no row ... in TB_COMM_SEQ" 로 실패.
   - 증상: AccessLogFilter 가 tracelog ID 채번에서 매 요청마다 예외 -> 요청 본문 소비 후
           request 속성 미설정 -> 컨트롤러가 빈 본문 파싱 -> "조회에 실패하였습니다".
   - 조치: 각 채번키에 대해 기존 데이터 최대 숫자 ID + 여유(100) 로 next_id 시드.
           (context-idgen.xml 의 tableName 값과 1:1 대응)
   - 주의: 채번 형식 = prefix + 0패딩 숫자.  prefix 길이만큼 SUBSTRING 시작 위치 지정.
           CUBRID는 멱등(IF NOT EXISTS) 미지원이므로 DELETE 후 INSERT.
   ===================================================================== */

DELETE FROM tb_comm_seq
 WHERE table_name IN ('TB_TRACELOG','TB_FILE','TB_USER','TB_CNG_HISTORY','TB_PLEDGEINFO','TB_PLEDGEBIZ');

/* tracelog : LOG_ + 16자리 (prefix 4글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_TRACELOG',
       NVL(MAX(CAST(SUBSTRING(log_id, 5) AS BIGINT)), 0) + 100
  FROM tb_tracelog
 WHERE log_id LIKE 'LOG_%'
   AND SUBSTRING(log_id, 5) REGEXP '^[0-9]+$';

/* file : FILE_ + 15자리 (prefix 5글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_FILE',
       NVL(MAX(CAST(SUBSTRING(atch_file_id, 6) AS BIGINT)), 0) + 100
  FROM tb_file
 WHERE atch_file_id LIKE 'FILE_%'
   AND SUBSTRING(atch_file_id, 6) REGEXP '^[0-9]+$';

/* user : USER_ + 15자리 (prefix 5글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_USER',
       NVL(MAX(CAST(SUBSTRING(user_id, 6) AS BIGINT)), 0) + 100
  FROM tb_user
 WHERE user_id LIKE 'USER_%'
   AND SUBSTRING(user_id, 6) REGEXP '^[0-9]+$';

/* cng_history : CNG_ + 15자리 (prefix 4글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_CNG_HISTORY',
       NVL(MAX(CAST(SUBSTRING(cng_history_id, 5) AS BIGINT)), 0) + 100
  FROM tb_cng_history
 WHERE cng_history_id LIKE 'CNG_%'
   AND SUBSTRING(cng_history_id, 5) REGEXP '^[0-9]+$';

/* pledge_info : P + 10자리 (prefix 1글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_PLEDGEINFO',
       NVL(MAX(CAST(SUBSTRING(pledge_info_id, 2) AS BIGINT)), 0) + 100
  FROM tb_pledgeinfo
 WHERE pledge_info_id LIKE 'P%'
   AND SUBSTRING(pledge_info_id, 2) REGEXP '^[0-9]+$';

/* pledge_biz : B + 10자리 (prefix 1글자) */
INSERT INTO tb_comm_seq (table_name, next_id)
SELECT 'TB_PLEDGEBIZ',
       NVL(MAX(CAST(SUBSTRING(pledge_biz_id, 2) AS BIGINT)), 0) + 100
  FROM tb_pledgebiz
 WHERE pledge_biz_id LIKE 'B%'
   AND SUBSTRING(pledge_biz_id, 2) REGEXP '^[0-9]+$';

COMMIT;

package com.cs.bcjis.bizdesc.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public interface BizDescMatchService {

    /** 공유 업로드 파일 목록 */
    JSONObject selectFileList(Map<String, Object> param) throws Exception;

    /** HWPX 업로드·파싱·공유 저장 */
    JSONObject uploadFile(HttpServletRequest request, Map<String, Object> param) throws Exception;

    /** 공유 파일 삭제(누구나) */
    JSONObject deleteFile(Map<String, Object> param) throws Exception;

    /** 조서 사업명 ↔ 사업설명서 ≥70% 후보 */
    JSONObject suggestByBizNm(Map<String, Object> param) throws Exception;

    /** 매칭 저장 */
    JSONObject saveMatch(Map<String, Object> param) throws Exception;

    /** 매칭 해제 */
    JSONObject clearMatch(Map<String, Object> param) throws Exception;

    /** 매칭된 사업설명서 요약(+가져오기 초안) */
    JSONObject getSummary(Map<String, Object> param) throws Exception;

    /**
     * 웹앱용 사업설명서 JSON 내보내기 본문.
     * 회계년도·예산차수·실국 메타 + 파서 businesses 포함.
     */
    JSONObject buildExportJson(Map<String, Object> param) throws Exception;

    /** 내보내기 파일명 제안 (확장자 제외 가능) */
    String buildExportFileName(Map<String, Object> param) throws Exception;
}

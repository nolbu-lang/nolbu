package com.cs.bcjis.ai.service;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

/**
 * AI 챗봇 서비스.
 */
public interface AiChatService {

    /**
     * 사용자 자연어 질문을 처리한다. (하위호환)
     *
     * @param question 자연어 질문
     * @return 결과 JSON (answer, sql, columns, dataList 등)
     */
    JSONObject ask(String question) throws Exception;

    /**
     * 회계년도·내부검색·일반자료검색 체크 파라미터를 포함한 질의 처리.
     *
     * @param params question, fisYearFrom/To, searchBizNm/... , searchLaw/City/Manual
     * @return 결과 JSON
     */
    JSONObject ask(JSONObject params) throws Exception;

    /**
     * 챗봇 UI 초기값(최근·최소 회계년도, 매뉴얼 권한 등).
     */
    JSONObject getMeta() throws Exception;

    /** 매뉴얼 목록 */
    JSONObject listManuals() throws Exception;

    /** 매뉴얼 PDF 업로드 */
    JSONObject uploadManual(HttpServletRequest request) throws Exception;

    /** 매뉴얼 삭제 */
    JSONObject deleteManual(String id) throws Exception;

    /**
     * 내부자료(심사조서) 1개 회계년도 전체를 모바일 뷰어용 JSON으로 내보낸다.
     * @param fisYear 4자리 회계년도 (1년 단위만 허용)
     */
    JSONObject exportInternalData(String fisYear) throws Exception;
}

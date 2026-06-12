package com.cs.bcjis.ai;

/**
 * AI 챗봇용 LLM 호출 공통 인터페이스.
 *
 * 테스트(Gemini)와 운영(HyperCLOVA X) 환경에서 동일한 시그니처로 교체 가능하도록 한다.
 * RAG 파이프라인(질문분류·답변생성)은 이 인터페이스만 의존한다.
 */
public interface LlmClient {

    /** API 키 등 필수 설정이 되어 호출 가능한지 */
    boolean isEnabled();

    /** 공급자 식별자 (gemini, hyperclova 등) */
    String getProviderName();

    /** 단일 프롬프트 생성 (질문 분류·JSON 계획 등) */
    String generate(String prompt) throws Exception;

    /**
     * 시스템 지침 + 사용자 프롬프트 생성 (답변·요약 등 페르소나 고정)
     *
     * @param systemInstruction 페르소나·출력 서식 (null 이면 미사용)
     * @param prompt            사용자 질문 및 DB 컨텍스트
     */
    String generate(String systemInstruction, String prompt) throws Exception;
}

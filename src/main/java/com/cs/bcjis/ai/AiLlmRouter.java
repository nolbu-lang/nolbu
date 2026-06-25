package com.cs.bcjis.ai;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * AI 예산도우미 LLM 라우터 — 부산시 내부 행정 AI (LLM Studio / HyperCLOVA) 전용.
 *
 * globals.properties 의 Globals.ClovaEndpoint 로 내부망 API에 연결한다.
 * RAG 서비스는 이 빈(aiLlmClient)만 주입받는다.
 */
@Component("aiLlmClient")
public class AiLlmRouter implements LlmClient {

    private static final Logger logger = Logger.getLogger(AiLlmRouter.class);

    @Resource(name = "hyperClovaClient")
    private HyperClovaClient hyperClovaClient;

    public String getConfiguredProvider() {
        return "hyperclova";
    }

    private LlmClient resolveDelegate() {
        if (!hyperClovaClient.isEnabled()) {
            logger.error("내부 행정 AI 엔드포인트가 없습니다. globals.properties 의 Globals.ClovaEndpoint 를 확인하세요.");
        }
        return hyperClovaClient;
    }

    public boolean isEnabled() {
        return resolveDelegate().isEnabled();
    }

    public String getProviderName() {
        return resolveDelegate().getProviderName();
    }

    public String generate(String prompt) throws Exception {
        return resolveDelegate().generate(prompt);
    }

    public String generate(String systemInstruction, String prompt) throws Exception {
        return resolveDelegate().generate(systemInstruction, prompt);
    }

    public String generateUserQuery(String userQuery) throws Exception {
        return resolveDelegate().generateUserQuery(userQuery);
    }
}

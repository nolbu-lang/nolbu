package com.cs.bcjis.ai;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * globals.properties 의 AiProvider 설정에 따라 Gemini / HyperCLOVA 클라이언트를 선택한다.
 *
 * - 테스트(PC): Globals.AiProvider = gemini
 * - 운영(업무서버): Globals.AiProvider = hyperclova
 *
 * RAG 서비스는 이 빈(aiLlmClient)만 주입받으면 LLM 공급자 교체가 완료된다.
 */
@Component("aiLlmClient")
public class AiLlmRouter implements LlmClient {

    private static final Logger logger = Logger.getLogger(AiLlmRouter.class);

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "geminiClient")
    private GeminiClient geminiClient;

    @Resource(name = "hyperClovaClient")
    private HyperClovaClient hyperClovaClient;

    /** 설정된 공급자 (gemini | hyperclova) */
    public String getConfiguredProvider() {
        String p = config.getProperty("Globals.AiProvider");
        if (p == null || p.trim().length() == 0) {
            return "gemini";
        }
        return p.trim().toLowerCase();
    }

    private LlmClient resolveDelegate() {
        String provider = getConfiguredProvider();
        if ("hyperclova".equals(provider) || "clova".equals(provider)) {
            if (!hyperClovaClient.isEnabled()) {
                logger.error("Globals.AiProvider=hyperclova 이지만 ClovaApiKey가 없습니다.");
            }
            return hyperClovaClient;
        }
        return geminiClient;
    }

    public boolean isEnabled() {
        return resolveDelegate().isEnabled();
    }

    /** 실제 호출에 사용되는 공급자명 */
    public String getProviderName() {
        return resolveDelegate().getProviderName();
    }

    public String generate(String prompt) throws Exception {
        return resolveDelegate().generate(prompt);
    }

    public String generate(String systemInstruction, String prompt) throws Exception {
        return resolveDelegate().generate(systemInstruction, prompt);
    }
}

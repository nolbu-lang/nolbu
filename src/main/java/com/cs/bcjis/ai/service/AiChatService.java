package com.cs.bcjis.ai.service;

import net.sf.json.JSONObject;

/**
 * AI \ucc57\ubd07 \uc11c\ube44\uc2a4.
 */
public interface AiChatService {

    /**
     * \uc0ac\uc6a9\uc790\uc758 \uc790\uc5f0\uc5b4 \uc9c8\ubb38\uc744 \ucc98\ub9ac\ud55c\ub2e4.
     *
     * @param question \uc790\uc5f0\uc5b4 \uc9c8\ubb38
     * @return \uacb0\uacfc JSON (answer, sql, columns, dataList \ub4f1)
     */
    JSONObject ask(String question) throws Exception;
}

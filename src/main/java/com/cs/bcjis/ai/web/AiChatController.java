package com.cs.bcjis.ai.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.ai.service.AiChatService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.util.BcjisCommUtil;

/**
 * AI \ucc57\ubd07 \ucee8\ud2b8\ub864\ub7ec.
 *
 * \uc608\uc0b0\ud3b8\uc131 \ud654\uba74\uc758 \ucc57\ubd07 \ucc3d\uc5d0\uc11c \ud638\ucd9c\ub418\uba70, \uc790\uc5f0\uc5b4 \uc9c8\ubb38\uc744 \ubc1b\uc544
 * \ub0b4\ubd80 CUBRID \ub370\uc774\ud130\ub97c \uc870\ud68c\u00b7\uc694\uc57d\ud55c \uacb0\uacfc\ub97c JSON \uc73c\ub85c \ubc18\ud658\ud55c\ub2e4.
 */
@Controller
public class AiChatController {

    private static final Logger logger = Logger.getLogger(AiChatController.class);

    @Resource(name = "aiChatService")
    private AiChatService aiChatService;

    @RequestMapping("/ai/ajaxAiChat.do")
    public ModelAndView ajaxAiChat(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            String question = jsonParam.optString("question", "");

            JSONObject aiResult = aiChatService.ask(question);

            jsonObject.put("data", aiResult);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (IllegalArgumentException iae) {
            // SQL \uac80\uc99d \uc2e4\ud328 \ub4f1 \uc0ac\uc6a9\uc790\uc5d0\uac8c \uc548\ub0b4 \uac00\ub2a5\ud55c \uc624\ub958
            logger.warn("ajaxAiChat - \uc798\ubabb\ub41c \uc694\uccad: " + iae.getMessage());
            JSONObject errData = new JSONObject();
            errData.put("answer", "\uc694\uccad\uc744 \ucc98\ub9ac\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: " + iae.getMessage());
            jsonObject.put("data", errData);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxAiChat(ModelMap, HttpServletRequest)", e);

            JSONObject errData = new JSONObject();
            errData.put("answer", "AI \ucc98\ub9ac \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4. \uc7a0\uc2dc \ud6c4 \ub2e4\uc2dc \uc2dc\ub3c4\ud574 \uc8fc\uc138\uc694.\n(\uc0c1\uc138: " + e.getMessage() + ")");
            jsonObject.put("data", errData);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "AI \ucc98\ub9ac \uc624\ub958");
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }
}

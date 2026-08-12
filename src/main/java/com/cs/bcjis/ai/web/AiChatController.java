package com.cs.bcjis.ai.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * AI 챗봇 컨트롤러.
 *
 * 예산편성 화면의 챗봇 창에서 호출되며, 자연어 질문을 받아
 * 내부 CUBRID 데이터 조회·요약 또는 일반자료(법령/시홈페이지/매뉴얼) 검색 결과를 JSON 으로 반환한다.
 */
@Controller
public class AiChatController {

    private static final Logger logger = Logger.getLogger(AiChatController.class);

    @Resource(name = "aiChatService")
    private AiChatService aiChatService;

    @RequestMapping("/ai/aiChatPopup.do")
    public String aiChatPopup(ModelMap model, HttpServletRequest request) throws Exception {
        return "ai/aiChatPopup";
    }

    @RequestMapping("/ai/ajaxAiChatMeta.do")
    public ModelAndView ajaxAiChatMeta(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject meta = aiChatService.getMeta();
            jsonObject.put("data", meta);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxAiChatMeta", e);
            JSONObject meta = new JSONObject();
            meta.put("minFisYear", "2013");
            meta.put("latestFisYear", String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)));
            jsonObject.put("data", meta);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/ai/ajaxAiChat.do")
    public ModelAndView ajaxAiChat(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONObject aiResult = aiChatService.ask(jsonParam);

            jsonObject.put("data", aiResult);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (IllegalArgumentException iae) {
            logger.warn("ajaxAiChat - 잘못된 요청: " + iae.getMessage());
            JSONObject errData = new JSONObject();
            errData.put("answer", "요청을 처리할 수 없습니다: " + iae.getMessage());
            jsonObject.put("data", errData);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxAiChat(ModelMap, HttpServletRequest)", e);

            JSONObject errData = new JSONObject();
            errData.put("answer", "AI 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.\n(상세: " + e.getMessage() + ")");
            jsonObject.put("data", errData);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "AI 처리 오류");
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/ai/ajaxAiManualList.do")
    public ModelAndView ajaxAiManualList(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", aiChatService.listManuals());
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxAiManualList", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/ai/ajaxAiManualUpload.do")
    public ModelAndView ajaxAiManualUpload(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject data = aiChatService.uploadManual(request);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, data.optString("message", "업로드되었습니다."));
        } catch (IllegalArgumentException iae) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, iae.getMessage());
        } catch (Exception e) {
            logger.error("ajaxAiManualUpload", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "매뉴얼 업로드에 실패했습니다: " + e.getMessage());
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/ai/ajaxAiManualDelete.do")
    public ModelAndView ajaxAiManualDelete(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject param = BcjisCommUtil.getJsonObjectFromRequest(request);
            String id = param.optString("id", request.getParameter("id"));
            JSONObject data = aiChatService.deleteManual(id);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, data.optString("message", "삭제되었습니다."));
        } catch (IllegalArgumentException iae) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, iae.getMessage());
        } catch (Exception e) {
            logger.error("ajaxAiManualDelete", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "매뉴얼 삭제에 실패했습니다.");
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    /**
     * 내부자료(심사조서) 1개 회계년도 JSON 내보내기 — AJAX.
     * 모바일 뷰어용. 회계년도는 1년 단위만 허용.
     */
    @RequestMapping("/ai/ajaxAiInternalExport.do")
    public ModelAndView ajaxAiInternalExport(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject param = BcjisCommUtil.getJsonObjectFromRequest(request);
            String fisYear = param != null ? param.optString("fisYear", "") : "";
            if (fisYear.length() == 0) {
                fisYear = request.getParameter("fisYear");
            }
            if (fisYear == null) {
                fisYear = "";
            }
            JSONObject data = aiChatService.exportInternalData(fisYear.trim());
            jsonObject.put("data", data);
            if (data.optBoolean("ok", false)) {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            } else {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, data.optString("error", "내보내기 실패"));
            }
        } catch (IllegalArgumentException iae) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, iae.getMessage());
        } catch (Exception e) {
            logger.error("ajaxAiInternalExport", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "내부자료 JSON 내보내기에 실패했습니다: " + e.getMessage());
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    /**
     * 내부자료 JSON 파일 직접 다운로드 (Content-Disposition attachment).
     */
    @RequestMapping("/ai/downloadAiInternalExport.do")
    public void downloadAiInternalExport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String fisYear = request.getParameter("fisYear");
        if (fisYear == null || fisYear.trim().length() == 0) {
            try {
                JSONObject param = BcjisCommUtil.getJsonObjectFromRequest(request);
                if (param != null) {
                    fisYear = param.optString("fisYear", "");
                }
            } catch (Exception ignore) {
                /* */
            }
        }
        try {
            JSONObject data = aiChatService.exportInternalData(fisYear == null ? "" : fisYear.trim());
            if (!data.optBoolean("ok", false)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write(data.optString("error", "내보내기 실패"));
                return;
            }
            String year = data.optString("fisYear", fisYear);
            String filename = "bcjis-ai-internal-" + year + ".json";
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename);
            // JSONObject#toString(indent) — 모바일 뷰어에서 읽기 쉽게 들여쓰기
            response.getWriter().write(data.toString());
            response.getWriter().flush();
        } catch (IllegalArgumentException iae) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write(iae.getMessage());
        } catch (Exception e) {
            logger.error("downloadAiInternalExport", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("내부자료 JSON 내보내기 오류: " + e.getMessage());
        }
    }
}

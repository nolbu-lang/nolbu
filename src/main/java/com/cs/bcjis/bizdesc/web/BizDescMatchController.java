package com.cs.bcjis.bizdesc.web;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.bizdesc.service.BizDescMatchService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;

@Controller
public class BizDescMatchController {

    private static final Logger logger = Logger.getLogger(BizDescMatchController.class);

    @Resource(name = "bizDescMatchService")
    private BizDescMatchService bizDescMatchService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @RequestMapping("/bizdesc/bizDescViewPopup.do")
    public String bizDescViewPopup(HttpServletRequest request, ModelMap model) throws Exception {
        // 별도 브라우저 창에서 사업설명서 매칭 결과 표시 (듀얼모니터 참고용)
        return "bizdesc/bizDescViewPopup";
    }

    @RequestMapping("/bizdesc/ajaxBizDescFileList.do")
    public ModelAndView ajaxBizDescFileList(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("fisYear", jsonParam.get("fisYear"));
            param.put("bgtDgr", jsonParam.get("bgtDgr"));
            param.put("officeCd", jsonParam.get("officeCd"));
            param.put("reportCd", jsonParam.get("reportCd"));
            requireYearBgtOffice(param);
            JSONObject data = bizDescMatchService.selectFileList(param);
            jsonObject.put("dataList", data.get("dataList"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescFileList", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescFileUpload.do")
    public ModelAndView ajaxBizDescFileUpload(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            Map<String, Object> param = new HashMap<String, Object>();
            // multipart form + 쿼리스트링 모두 수용 (ajaxfileupload 파라미터 누락 대비)
            param.put("fisYear", firstNonEmptyParam(request, "fisYear"));
            param.put("bgtDgr", firstNonEmptyParam(request, "bgtDgr"));
            param.put("reportCd", firstNonEmptyParam(request, "reportCd"));
            param.put("officeCd", firstNonEmptyParam(request, "officeCd"));
            param.put("officeNm", firstNonEmptyParam(request, "officeNm"));
            param.put("userId", user.getUserId());
            requireYearBgtOffice(param);
            JSONObject data = bizDescMatchService.uploadFile(request, param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "사업설명서가 업로드되었습니다.");
        } catch (IllegalArgumentException e) {
            logger.warn("ajaxBizDescFileUpload: " + e.getMessage());
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescFileUpload", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "사업설명서 업로드에 실패하였습니다.");
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescFileDelete.do")
    public ModelAndView ajaxBizDescFileDelete(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("bizdescFileId", jsonParam.get("bizdescFileId"));
            param.put("officeCd", jsonParam.get("officeCd"));
            if (BcjisCommUtil.isNullString(param.get("bizdescFileId"))) {
                throw new IllegalArgumentException("파일ID가 필요합니다.");
            }
            JSONObject data = bizDescMatchService.deleteFile(param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "사업설명서가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescFileDelete", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "삭제에 실패하였습니다.");
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescSuggest.do")
    public ModelAndView ajaxBizDescSuggest(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = baseMatchParam(jsonParam);
            param.put("reportBizNm", jsonParam.get("reportBizNm"));
            param.put("officeCd", jsonParam.get("officeCd"));
            BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            if (user != null) {
                param.put("regiId", user.getUserId());
            }
            if (BcjisCommUtil.isNullString(param.get("officeCd"))) {
                throw new IllegalArgumentException(
                        "조회조건 '실국'(또는 '전체')을 선택해 주세요.");
            }
            JSONObject data = bizDescMatchService.suggestByBizNm(param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescSuggest", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescMatchSave.do")
    public ModelAndView ajaxBizDescMatchSave(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = baseMatchParam(jsonParam);
            param.put("bizdescFileId", jsonParam.get("bizdescFileId"));
            param.put("bizSeq", jsonParam.get("bizSeq"));
            param.put("bizNm", jsonParam.get("bizNm"));
            param.put("deptNm", jsonParam.get("deptNm"));
            param.put("matchScore", jsonParam.get("matchScore"));
            param.put("regiId", user.getUserId());
            if (BcjisCommUtil.isNullString(param.get("bizdescFileId"))) {
                throw new IllegalArgumentException("사업설명서 파일ID가 필요합니다.");
            }
            JSONObject data = bizDescMatchService.saveMatch(param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "사업설명서가 매칭되었습니다.");
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescMatchSave", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "매칭 저장에 실패하였습니다.");
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescMatchClear.do")
    public ModelAndView ajaxBizDescMatchClear(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = baseMatchParam(jsonParam);
            JSONObject data = bizDescMatchService.clearMatch(param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "매칭이 해제되었습니다.");
        } catch (Exception e) {
            logger.error("ajaxBizDescMatchClear", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "매칭 해제에 실패하였습니다.");
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/bizdesc/ajaxBizDescSummary.do")
    public ModelAndView ajaxBizDescSummary(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = baseMatchParam(jsonParam);
            JSONObject data = bizDescMatchService.getSummary(param);
            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescSummary", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    /**
     * 웹앱용 사업설명서 JSON 내보내기 (클릭 PC에 저장용 본문).
     * 클라이언트에서 Blob으로 파일 저장한다.
     */
    @RequestMapping("/bizdesc/ajaxBizDescExportJson.do")
    public ModelAndView ajaxBizDescExportJson(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("fisYear", jsonParam.get("fisYear"));
            param.put("bgtDgr", jsonParam.get("bgtDgr"));
            param.put("officeCd", jsonParam.get("officeCd"));
            param.put("officeNm", jsonParam.get("officeNm") == null ? "" : jsonParam.get("officeNm"));
            param.put("reportCd", jsonParam.get("reportCd") == null ? "" : jsonParam.get("reportCd"));
            param.put("bizdescFileIds", jsonParam.get("bizdescFileIds"));
            requireYearBgtOffice(param);

            JSONObject root = bizDescMatchService.buildExportJson(param);
            String fileName = bizDescMatchService.buildExportFileName(param);

            JSONObject data = new JSONObject();
            data.put("fileName", fileName);
            data.put("content", root);
            data.put("fileCount", root.get("fileCount"));
            data.put("bizCount", root.get("bizCount"));

            jsonObject.put("data", data);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "JSON 내보내기 준비가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBizDescExportJson", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            String detail = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE,
                    "사업설명서 JSON 내보내기에 실패하였습니다.<br>" + detail);
        }
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    private Map<String, Object> baseMatchParam(JSONObject jsonParam) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("fisYear", jsonParam.get("fisYear"));
        param.put("bgtDgr", jsonParam.get("bgtDgr"));
        param.put("reportCd", jsonParam.get("reportCd"));
        param.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
        if (BcjisCommUtil.isNullString(param.get("fisYear")) || BcjisCommUtil.isNullString(param.get("reportCd"))) {
            throw new IllegalArgumentException("회계연도·조서구분이 필요합니다.");
        }
        if (BcjisCommUtil.isNullString(param.get("bgtDgr")) || BcjisCommUtil.isNullString(param.get("teBgtCompoId"))) {
            throw new IllegalArgumentException("예산차수·사업구성ID가 필요합니다.");
        }
        return param;
    }

    private void requireYearBgtOffice(Map<String, Object> param) {
        if (BcjisCommUtil.isNullString(param.get("fisYear"))) {
            throw new IllegalArgumentException("회계연도가 필요합니다.");
        }
        if (BcjisCommUtil.isNullString(param.get("bgtDgr"))) {
            throw new IllegalArgumentException("예산차수가 필요합니다.");
        }
        if (BcjisCommUtil.isNullString(param.get("officeCd"))) {
            throw new IllegalArgumentException(
                    "조서 조회조건의 실국(또는 '전체')을 선택해 주세요.");
        }
    }

    /**
     * multipart form / querystring 파라미터를 읽고,
     * ajaxfileupload의 encodeURIComponent 값을 복원한다.
     */
    private String firstNonEmptyParam(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        if (BcjisCommUtil.isNullString(v)) {
            return "";
        }
        v = v.trim();
        try {
            // %XX 형태면 URL 디코딩 (이중 디코딩 방지를 위해 % 포함 시에만)
            if (v.indexOf('%') >= 0) {
                v = URLDecoder.decode(v, "UTF-8");
            }
        } catch (Exception ignore) {
            // keep original
        }
        if ("null".equalsIgnoreCase(v) || "undefined".equalsIgnoreCase(v)) {
            return "";
        }
        return v;
    }
}

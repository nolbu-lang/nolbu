package com.cs.bcjis.dialog.web;

import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.dialog.service.DialogDgroffice010SortService;

@Controller
public class DialogDgroffice010SortController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgroffice010SortController.class);
     
    @Resource(name = "dialogDgroffice010SortService")
    private DialogDgroffice010SortService dialogDgroffice010SortService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/ajaxDgroffice010SortDgroffice010List.do")
    public ModelAndView ajaxDgroffice010SortDgroffice010List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgroffice010SortDgroffice010List(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(dialogDgroffice010SortService.selectDgroffice010List(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgroffice010SortDgroffice010List(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgroffice010SortDgroffice010List(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/dialog/ajaxDialogDgroffice010SaveOfficeRank010s.do")
    public ModelAndView ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgroffice010SortService.saveOfficeRank010s(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDgroffice0F0SortDgroffice0F0List010.do")
    public ModelAndView ajaxDgroffice0F0SortDgroffice0F0List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgroffice0F0SortDgroffice0F0List(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(dialogDgroffice010SortService.selectDgroffice0F0List(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgroffice0F0SortDgroffice0F0List(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgroffice0F0SortDgroffice0F0List(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/dialog/ajaxDialogDgroffice0F0SaveOfficeRank0F0s010.do")
    public ModelAndView ajaxDialogDgrcompoRegiSaveDgrcompos0F0(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgroffice010SortService.saveOfficeRank0F0s(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDialogDgroffice010InitOfficeRank010s.do")
    public ModelAndView ajaxDialogDgroffice010InitOfficeRank010s(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgroffice010InitOfficeRank010s(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgroffice010SortService.initOfficeRank010(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgroffice010InitOfficeRank010s(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgroffice010InitOfficeRank010s(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
}

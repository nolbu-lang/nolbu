package com.cs.bcjis.dialog.web;

import org.apache.log4j.Logger;

import java.util.Map;

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
import com.cs.bcjis.dialog.service.DialogManageUserRegiService;

import egovframework.rte.fdl.cryptography.EgovCryptoService;
import egovframework.rte.fdl.idgnr.EgovIdGnrService;



@Controller
public class DialogManageUserRegiController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogManageUserRegiController.class);
    
    @Resource(name = "dialogManageUserRegiService")
    private DialogManageUserRegiService dialogManageUserRegiService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
   
    @Resource(name = "csUserIdGnrService")
    private EgovIdGnrService csUserIdGnrService;
    
    @Resource(name = "ARIACryptoService")
    private EgovCryptoService egovCryptoService;
    
    @SuppressWarnings("rawtypes")
    @RequestMapping("/dialog/ajaxDialogManageUserCheckLoginId.do")
    public ModelAndView ajaxDialogManageUserCheckUserId(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserCheckUserId(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            Map map = dialogManageUserRegiService.selectDialogManageUser(jsonParam);           
            
            if(map == null || BcjisCommUtil.isNullString(map.get("loginId")) == true){
                jsonObject.put("checkUserYn", "Y");
            }else{
                jsonObject.put("checkUserYn", "N");
            }     
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDialogManageUserCheckUserId(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserCheckUserId(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDialogManageUserRegiSaveUser.do")
    public ModelAndView ajaxDialogManageUserRegiSaveUser(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserRegiSaveUser(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            String pswd = jsonParam.getString("userPass");
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            jsonParam.put("userPass", BcjisCommUtil.getEgovEncrypt(egovCryptoService, pswd));
          
            jsonParam.put("newUserId", csUserIdGnrService.getNextStringId());
            
            dialogManageUserRegiService.insertDialogManageUserUser(jsonParam);           
            dialogManageUserRegiService.insertDialogManageUserPowgrp(jsonParam);
            
            jsonParam.put("reportUserId", jsonParam.get("newUserId"));
            dialogManageUserRegiService.insertDialogManageUserReport(jsonParam);
            
            jsonParam.put("deptUserId", jsonParam.get("newUserId"));
            dialogManageUserRegiService.insertDialogManageUserDept(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogManageUserRegiSaveUser(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserRegiSaveUser(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDialogManageUserRegiReportSearch.do")
    public ModelAndView ajaxDialogManageUserRegiReportSearch(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserRegiReportSearch(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray reportList = JSONArray.fromObject(dialogManageUserRegiService.selectManageUserRegiReport(jsonParam));
            JSONArray deptList = JSONArray.fromObject(dialogManageUserRegiService.selectManageUserRegiDept(jsonParam));

            JSONObject jsonReportObject = new JSONObject();
            jsonReportObject.put("dataList", reportList);

            JSONObject jsonDeptObject = new JSONObject();
            jsonDeptObject.put("dataList", deptList);
            
            jsonObject.put("reportInfo", jsonReportObject);
            jsonObject.put("deptInfo", jsonDeptObject);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDialogManageUserRegiReportSearch(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserRegiReportSearch(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

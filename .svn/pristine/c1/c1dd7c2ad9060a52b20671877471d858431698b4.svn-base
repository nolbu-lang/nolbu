package com.cs.bcjis.dialog.web;

import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.cs.bcjis.dialog.service.DialogManageUserModifyService;
import com.cs.bcjis.dialog.service.DialogManageUserRegiService;

import egovframework.rte.fdl.cryptography.EgovCryptoService;

@Controller
public class DialogManageUserModifyController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogManageUserModifyController.class);

    @Resource(name = "dialogManageUserRegiService")
    private DialogManageUserRegiService dialogManageUserRegiService;
    
    @Resource(name = "dialogManageUserModifyService")
    private DialogManageUserModifyService dialogManageUserModifyService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "ARIACryptoService")
    private EgovCryptoService egovCryptoService;
    
    @RequestMapping("/dialog/ajaxDialogManageUserModifySave.do")
    public ModelAndView ajaxDialogManageUserModifySave(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserModifySave(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            String pswd = jsonParam.getString("userPass");
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            jsonParam.put("userPass", BcjisCommUtil.getEgovEncrypt(egovCryptoService, pswd));
            
            dialogManageUserModifyService.updateDialogManageUser(jsonParam);
            dialogManageUserModifyService.updateDialogManageUserPowgrp(jsonParam);  
            
            jsonParam.put("reportUserId", jsonParam.get("modifyuserId"));
            dialogManageUserRegiService.insertDialogManageUserReport(jsonParam);

            jsonParam.put("deptUserId", jsonParam.get("modifyuserId"));
            dialogManageUserRegiService.insertDialogManageUserDept(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogManageUserModifySave(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogManageUserModifySave(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

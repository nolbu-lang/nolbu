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
import com.cs.bcjis.dialog.service.DialogDgrcompoRegiService;

@Controller
public class DialogDgrcompoRegiController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrcompoRegiController.class);
    
    @Resource(name = "dialogDgrcompoRegiService")
    private DialogDgrcompoRegiService dialogDgrcompoRegiService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/ajaxDialogDgrcompoRegiSelectDgrcompos.do")
    public ModelAndView ajaxDialogDgrcompoRegiSelectDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSelectDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            JSONObject upDgrcompo = JSONObject.fromObject(dialogDgrcompoRegiService.selectDgrcompoRegiUpDgrcompoInfo(jsonParam));
            JSONArray frscList = JSONArray.fromObject(dialogDgrcompoRegiService.selectDgrcompofrscList(jsonParam));
            JSONArray charList = JSONArray.fromObject(dialogDgrcompoRegiService.selectDgrcompocharList(jsonParam));

            JSONObject jsonFrscObject = new JSONObject();
            jsonFrscObject.put("dataList", frscList);
            
            JSONObject jsonCharObject = new JSONObject();
            jsonCharObject.put("dataList", charList);

            jsonObject.put("upDgrcompo", upDgrcompo);
            jsonObject.put("frscInfo", jsonFrscObject);
            jsonObject.put("charInfo", jsonCharObject);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoRegiSelectDgrcompos(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSelectDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDialogDgrcompoRegiSaveDgrcompos.do")
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
            dialogDgrcompoRegiService.saveDgrcompos(jsonParam);
            
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

}

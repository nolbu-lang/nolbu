package com.cs.bcjis.dialog.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.dialog.service.DialogDgrcompoTeMngRegiService;

@Controller
public class DialogDgrcompoTeMngRegiController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrcompoTeMngRegiController.class);
    
    @Resource(name = "dialogDgrcompoTeMngRegiService")
    private DialogDgrcompoTeMngRegiService dialogDgrcompoTeMngRegiService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/ajaxDialogDgrcompoTeMngRegiSaveDgrcompos.do")
    public ModelAndView ajaxDialogDgrcompoTeMngRegiSaveDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoTeMngRegiSaveDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgrcompoTeMngRegiService.insertDgrcompo(jsonParam);
            dialogDgrcompoTeMngRegiService.insertTeMngMok(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoTeMngRegiSaveDgrcompos(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoTeMngRegiSaveDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

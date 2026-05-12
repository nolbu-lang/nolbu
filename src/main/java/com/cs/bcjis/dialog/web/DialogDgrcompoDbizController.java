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
import com.cs.bcjis.dialog.service.DialogDgrcompoDbizService;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;

@Controller
public class DialogDgrcompoDbizController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrcompoDbizController.class);
    
    @Resource(name = "dialogDgrcompoDbizService")
    private DialogDgrcompoDbizService dialogDgrcompoDbizService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/ajaxDialogDgrcompoDbizSelectDgrcompos.do")
    public ModelAndView ajaxDialogDgrcompoDbizSelectDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoDbizSelectDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
 
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray dgrcompo = JSONArray.fromObject(dialogDgrcompoDbizService.selectDgrcompo(jsonParam));
            jsonObject.put("dataList", dgrcompo);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoDbizSelectDgrcompos(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoDbizSelectDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

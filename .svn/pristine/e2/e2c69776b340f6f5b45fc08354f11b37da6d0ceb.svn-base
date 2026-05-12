package com.cs.bcjis.dialog.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.dialog.service.DialogManageCloseHisService;



@Controller
public class DialogManageCloseHisController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogManageCloseHisController.class);
    
    @Resource(name = "dialogManageCloseHisService")
    private DialogManageCloseHisService dialogManageCloseHisService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/ajaxDialogManageCloseHisSearch.do")
    public ModelAndView ajaxDialogManageCloseHisSearch(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchLogList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(dialogManageCloseHisService.selectManageCloseHisList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxManageBatchLogList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchLogList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

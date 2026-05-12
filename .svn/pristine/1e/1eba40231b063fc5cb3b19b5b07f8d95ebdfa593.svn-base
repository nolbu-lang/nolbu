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
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.dialog.service.DialogBgtFrscAmtService;

@Controller
public class DialogBgtFrscAmtController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogBgtFrscAmtController.class);
    
    @Resource(name = "dialogBgtFrscAmtService")
    private DialogBgtFrscAmtService dialogBgtFrscAmtService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/dialog/selectDgrfrscCompoSeqList.do")
    public ModelAndView selectDgrfrscCompoSeqList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("selectDgrfrscCompoSeqList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(dialogBgtFrscAmtService.selectDgrfrscCompoSeqList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("selectDgrfrscCompoSeqList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("selectDgrfrscCompoSeqList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

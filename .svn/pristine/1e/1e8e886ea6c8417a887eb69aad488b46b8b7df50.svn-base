package com.cs.bcjis.report.web;

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
import com.cs.bcjis.report.service.ReportWrite001Service;
import com.cs.bcjis.report.util.Report001SaveFile;

@Controller
public class ReportWrite001Controller {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportWrite001Controller.class);
    
    @Resource(name = "reportWrite001Service")
    private ReportWrite001Service reportWrite001Service;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name="report001SaveFile")
    Report001SaveFile report001SaveFile;
    
    @RequestMapping("/report/ajaxReportWrite001SaveSheet.do")
    public ModelAndView ajaxReportWrite001SaveSheet(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite001SaveSheet(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray dataList = JSONArray.fromObject(reportWrite001Service.selectReport001SheetList(jsonParam));
            JSONArray totList = JSONArray.fromObject(reportWrite001Service.selectReportTotSheetList(jsonParam));
            JSONArray totList2 = JSONArray.fromObject(reportWrite001Service.selectReportTotSheetList2(jsonParam));

            jsonParam.put("dataList", dataList);
            jsonParam.put("totList", totList);
            jsonParam.put("totList2", totList2);

            report001SaveFile.buildSheetDocument(jsonParam, "RP", "");
            
            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite001SaveSheet(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet001"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite001SaveSheet(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
}

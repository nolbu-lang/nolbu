package com.cs.bcjis.report.web;

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
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.service.ReportWrite070Service;
import com.cs.bcjis.report.util.Report070SaveFile;

@Controller
public class ReportWrite070Controller {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportWrite070Controller.class);
    
    @Resource(name = "reportWrite070Service")
    private ReportWrite070Service reportWrite070Service;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name="report070SaveFile")
    Report070SaveFile report070SaveFile;

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/report/reportWrite070.do")
    public String reportWrite070(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite070(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite070(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite070";
    }

    @RequestMapping("/report/ajaxReportWrite070Report070List.do")
    public ModelAndView ajaxReportWrite070ReportWrite070List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070ReportWrite070List(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            jsonParam.put("userId", bcjisUserVO.getUserId());
            if(BcjisCommUtil.isNullString(jsonParam.get("userDeptYn")) == true){
                jsonParam.put("userDeptYn", bcjisCommService.getDeptUserYn(bcjisUserVO, String.valueOf(jsonParam.get("reportCd"))));
            }

            JSONArray resultList = JSONArray.fromObject(reportWrite070Service.selectReport070List(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite070ReportWrite070List(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070ReportWrite070List(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite070SaveReport070.do")
    public ModelAndView ajaxReportWrite070SaveReport070(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveReport070(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            reportWrite070Service.saveReport070(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxReportWrite070SaveReport070(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveReport070(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/report/ajaxReportWrite070SaveFile.do")
    public ModelAndView ajaxReportWrite070SaveFile(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveFile(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            jsonParam.put("userId", bcjisUserVO.getUserId());
            if(BcjisCommUtil.isNullString(jsonParam.get("userDeptYn")) == true){
                jsonParam.put("userDeptYn", bcjisCommService.getDeptUserYn(bcjisUserVO, String.valueOf(jsonParam.get("reportCd"))));
            }
            
            JSONArray resultList = JSONArray.fromObject(reportWrite070Service.selectReport070ExcelList(jsonParam));
            JSONArray bizList = JSONArray.fromObject(reportWrite070Service.selectReportBizExcelList(jsonParam));

            jsonParam.put("dataList", resultList);
            jsonParam.put("bizList", bizList);

            report070SaveFile.buildExcelDocument(jsonParam, "RP", "");
            
            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite070SaveFile(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveFile"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveFile(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/report/ajaxReportWrite070SaveSheet.do")
    public ModelAndView ajaxReportWrite070SaveSheet(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveSheet(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray resultList = JSONArray.fromObject(reportWrite070Service.selectReport070ExcelList(jsonParam));
            JSONArray bizList = JSONArray.fromObject(reportWrite070Service.selectReportBizExcelList(jsonParam));

            jsonParam.put("dataList", resultList);
            jsonParam.put("bizList", bizList);

            report070SaveFile.buildSheetDocument(jsonParam, "RP", "");
            
            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite070SaveSheet(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite070SaveSheet(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

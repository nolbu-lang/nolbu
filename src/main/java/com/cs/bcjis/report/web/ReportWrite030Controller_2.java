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
import com.cs.bcjis.report.service.ReportWrite030Service;
import com.cs.bcjis.report.service.ReportWrite030Service_2;
import com.cs.bcjis.report.util.Report030SaveFile;
import com.cs.bcjis.report.util.Report030SaveFile_2;

@Controller
public class ReportWrite030Controller_2 {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportWrite030Controller.class);
    
    @Resource(name = "reportWrite030Service_2")
    private ReportWrite030Service_2 reportWrite030Service_2;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name="report030SaveFile_2")
    Report030SaveFile_2 report030SaveFile_2;

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/report/reportWrite030_2.do")
    public String reportWrite030(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite030_2(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite030_2(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite030_2";
    }

    @RequestMapping("/report/ajaxReportWrite030Report030List_2.do")
    public ModelAndView ajaxReportWrite030ReportWrite030List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030ReportWrite030List_2(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(reportWrite030Service_2.selectReport030List(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite030ReportWrite030List_2(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030ReportWrite030List_2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite030SaveReport030_2.do")
    public ModelAndView ajaxReportWrite030SaveReport030(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveReport030_2(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            reportWrite030Service_2.saveReport030(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxReportWrite030SaveReport030_2(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveReport030_2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/report/ajaxReportWrite030SaveFile_2.do")
    public ModelAndView ajaxReportWrite030SaveFile(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveFile_2(ModelMap, HttpServletRequest) - start");
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

            if ("034".equals(jsonParam.get("reportDetlCd")) == true) {
                jsonParam.put("dataList034", JSONArray.fromObject(reportWrite030Service_2.selectReport034ExcelList(jsonParam)));
            }else{
                jsonParam.put("dataList", JSONArray.fromObject(reportWrite030Service_2.selectReport030ExcelList(jsonParam)));
            }
            JSONArray totList = JSONArray.fromObject(reportWrite030Service_2.selectReport030Sheet001List(jsonParam));

            jsonParam.put("totList", totList);

            report030SaveFile_2.buildExcelDocument(jsonParam, "RP", "");
            
            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite030SaveFile_2(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveFile"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveFile_2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite030SaveSheet_2.do")
    public ModelAndView ajaxReportWrite030SaveSheet(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveSheet_2(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray totList = JSONArray.fromObject(reportWrite030Service_2.selectReport030Sheet001List(jsonParam));
            JSONArray dataList = JSONArray.fromObject(reportWrite030Service_2.selectReport030ExcelList(jsonParam));
            JSONArray dataList034 = JSONArray.fromObject(reportWrite030Service_2.selectReport034ExcelList(jsonParam));

            dataList.addAll(dataList034);
            
            jsonParam.put("totList", totList);
            jsonParam.put("dataList", dataList);

            report030SaveFile_2.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite030SaveSheet_2(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite030SaveSheet_2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

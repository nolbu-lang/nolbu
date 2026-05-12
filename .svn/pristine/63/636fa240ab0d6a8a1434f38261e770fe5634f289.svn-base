package com.cs.bcjis.report.web;

import java.util.Map;

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
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.service.ReportWrite0C0Service;
import com.cs.bcjis.report.util.Report0C0SaveFile;

@Controller
public class ReportWrite0C0Controller {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportWrite0C0Controller.class);

    @Resource(name = "reportWrite0C0Service")
    private ReportWrite0C0Service reportWrite0C0Service;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name="report0C0SaveFile")
    Report0C0SaveFile report0C0SaveFile;

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/report/reportWrite0C0.do")
    public String reportWrite0C0(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite0C0(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite0C0(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite0C0";
    }

    @RequestMapping("/report/ajaxReportWrite0C0Report0C0List.do")
    public ModelAndView ajaxReportWrite0C0ReportWrite0C0List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0ReportWrite0C0List(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(reportWrite0C0Service.selectReport0C0List(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite0C0ReportWrite0C0List(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0ReportWrite0C0List(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite0C0SaveReport0C0.do")
    public ModelAndView ajaxReportWrite0C0SaveReport0C0(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0SaveReport0C0(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());
            reportWrite0C0Service.saveReport0C0(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxReportWrite0C0SaveReport0C0(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0SaveReport0C0(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite0C0InsertReport0C0.do")
    public ModelAndView ajaxReportWrite0C0InsertReport0C0(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0InsertReport0C0(ModelMap, HttpServletRequest) - start");
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

            reportWrite0C0Service.insertReport(jsonParam);
            reportWrite0C0Service.insertReport0C0(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxReportWrite0C0InsertReport0C0(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0InsertReport0C0(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/report/ajaxReportWrite0C0SaveHwp.do")
    public ModelAndView ajaxReportWrite0C0SaveHwp(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0SaveHwp(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray dataList = JSONArray.fromObject(reportWrite0C0Service.selectReport0C0HwpList(jsonParam));

            jsonParam.put("dataList", dataList);

            report0C0SaveFile.buildHwpDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite0C0SaveHwp(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite0C0SaveHwp(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

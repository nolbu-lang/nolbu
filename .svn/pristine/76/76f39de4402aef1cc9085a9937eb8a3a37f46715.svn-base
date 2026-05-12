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
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.service.ReportWrite020Service;
import com.cs.bcjis.report.util.Report020GugunSaveFile;
import com.cs.bcjis.report.util.Report020SaveFile;

@Controller
public class ReportWrite020Controller {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportWrite020Controller.class);

    @Resource(name = "reportWrite020Service")
    private ReportWrite020Service reportWrite020Service;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @Resource(name = "report020SaveFile")
    Report020SaveFile report020SaveFile;

    @Resource(name = "report020GugunSaveFile")
    Report020GugunSaveFile report020GugunSaveFile;

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/report/reportWrite020.do")
    public String reportWrite020(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite020";
    }

    @RequestMapping("/report/reportWrite020Page.do")
    public String reportWrite020Page(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020Page(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020Page(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite020Page";
    }

    @RequestMapping("/report/reportWrite020Tot.do")
    public String reportWrite020Tot(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020Tot(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportWrite020Tot(Map, ModelMap, HttpServletRequest) - end");
        }
        return "report/reportWrite020Tot";
    }

    @RequestMapping("/report/ajaxReportWrite020Report020List.do")
    public ModelAndView ajaxReportWrite020ReportWrite020List(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020List(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(reportWrite020Service.selectReport020List(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020ReportWrite020List(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020List(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020Report020PageList.do")
    public ModelAndView ajaxReportWrite020ReportWrite020PageList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020PageList(ModelMap, HttpServletRequest) - start");
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

            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(reportWrite020Service.selectReport020PageListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(reportWrite020Service.selectReport020PageList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020ReportWrite020PageList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020PageList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020Report020TotList.do")
    public ModelAndView ajaxReportWrite020ReportWrite020TotList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020TotList(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(reportWrite020Service.selectReport020TotList(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020ReportWrite020TotList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020ReportWrite020TotList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020SaveReport020.do")
    public ModelAndView ajaxReportWrite020SaveReport020(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveReport020(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());
            reportWrite020Service.saveReport020(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxReportWrite020SaveReport020(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveReport020(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020SaveFile.do")
    public ModelAndView ajaxReportWrite020SaveFile(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveFile(ModelMap, HttpServletRequest) - start");
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

            JSONArray dataList = null;
            JSONArray dataList027 = null;
            JSONArray dataListTot = null;
            JSONArray bizList = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelList(jsonParam));
            jsonParam.put("bizList", bizList);
            JSONArray bizListTot = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelListTot(jsonParam));
            jsonParam.put("bizListTot", bizListTot);

            if ("".equals(jsonParam.get("reportDetlCd")) == true) {
                dataList = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelList(jsonParam));
                dataList027 = JSONArray.fromObject(reportWrite020Service.selectReport027ExcelList(jsonParam));
                dataListTot = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelListTot(jsonParam));

                jsonParam.put("dataList", dataList);
                jsonParam.put("dataList027", dataList027);
                jsonParam.put("dataListTot", dataListTot);
            } else if ("027".equals(jsonParam.get("reportDetlCd")) == true) {
                dataList027 = JSONArray.fromObject(reportWrite020Service.selectReport027ExcelList(jsonParam));
                jsonParam.put("dataList027", dataList027);
            } else {
                dataList = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelList(jsonParam));
                dataListTot = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelListTot(jsonParam));
                jsonParam.put("dataList", dataList);
                jsonParam.put("dataListTot", dataListTot);
            }

            report020SaveFile.buildExcelDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020SaveFile(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveFile"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveFile(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020SaveSheet.do")
    public ModelAndView ajaxReportWrite020SaveSheet(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveSheet(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray officeList = JSONArray.fromObject(reportWrite020Service.selectReport020OfficeList(jsonParam));
            JSONArray officeSiList = JSONArray.fromObject(reportWrite020Service.selectReport020OfficeSiList(jsonParam));
            JSONArray officeSi022List = JSONArray.fromObject(reportWrite020Service.selectReportOfficeSi022List(jsonParam));
            JSONArray totList = JSONArray.fromObject(reportWrite020Service.selectReport020RptTotList(jsonParam));
            JSONArray dataList = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelList(jsonParam));
            JSONArray dataList027 = JSONArray.fromObject(reportWrite020Service.selectReport027ExcelList(jsonParam));
            JSONArray dataListTot = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelListTot(jsonParam));
            JSONArray bizList = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelList(jsonParam));
            JSONArray bizListTot = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelListTot(jsonParam));

            jsonParam.put("officeList", officeList);
            jsonParam.put("officeSiList", officeSiList);
            jsonParam.put("officeSi022List", officeSi022List);
            jsonParam.put("totList", totList);
            jsonParam.put("dataList", dataList);
            jsonParam.put("dataList027", dataList027);
            jsonParam.put("dataListTot", dataListTot);
            jsonParam.put("bizList", bizList);
            jsonParam.put("bizListTot", bizListTot);

            report020SaveFile.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020SaveSheet(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveSheet(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020SaveSheet2.do")
    public ModelAndView ajaxReportWrite020SaveSheet2(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveSheet2(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray officeList = JSONArray.fromObject(reportWrite020Service.selectReport020OfficeList(jsonParam));
            JSONArray officeSiList = JSONArray.fromObject(reportWrite020Service.selectReport020OfficeSiList(jsonParam));
            JSONArray officeSi022List = JSONArray.fromObject(reportWrite020Service.selectReportOfficeSi022List(jsonParam));
            JSONArray totList = JSONArray.fromObject(reportWrite020Service.selectReport020RptTotList(jsonParam));
            JSONArray dataList = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelList2(jsonParam));
            JSONArray dataList027 = JSONArray.fromObject(reportWrite020Service.selectReport027ExcelList(jsonParam));
            JSONArray dataListTot = JSONArray.fromObject(reportWrite020Service.selectReport020ExcelListTot(jsonParam));
            JSONArray bizList = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelList(jsonParam));
            JSONArray bizListTot = JSONArray.fromObject(reportWrite020Service.selectReportBizExcelListTot(jsonParam));

            jsonParam.put("officeList", officeList);
            jsonParam.put("officeSiList", officeSiList);
            jsonParam.put("officeSi022List", officeSi022List);
            jsonParam.put("totList", totList);
            jsonParam.put("dataList", dataList);
            jsonParam.put("dataList027", dataList027);
            jsonParam.put("dataListTot", dataListTot);
            jsonParam.put("bizList", bizList);
            jsonParam.put("bizListTot", bizListTot);

            report020SaveFile.buildSheetDocument2(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020SaveSheet2(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveSheet2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/report/ajaxReportWrite020SaveGuGun.do")
    public ModelAndView ajaxReportWrite020SaveGuGun(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveGuGun(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray gugunTotList = JSONArray.fromObject(reportWrite020Service.selectReportGugunTotExcelList(jsonParam));
            JSONArray gugunList = JSONArray.fromObject(reportWrite020Service.selectReportGugunExcelList(jsonParam));

            jsonParam.put("gugunTotList", gugunTotList);
            jsonParam.put("gugunList", gugunList);

            report020GugunSaveFile.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportWrite020SaveGuGun(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheet"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportWrite020SaveGuGun(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

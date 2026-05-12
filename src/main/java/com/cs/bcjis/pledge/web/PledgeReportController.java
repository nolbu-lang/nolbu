package com.cs.bcjis.pledge.web;

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
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.pledge.service.PledgeReportService;
import com.cs.bcjis.pledge.util.PledgeReportSaveSheet1File;
import com.cs.bcjis.pledge.util.PledgeReportSaveSheet2File;
import com.cs.bcjis.pledge.util.PledgeReportSaveSheet3File;
import com.cs.bcjis.pledge.util.PledgeReportSaveSheet4File;
import com.cs.bcjis.report.service.ReportWrite000Service;

@Controller
public class PledgeReportController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(PledgeReportController.class);

    @Resource(name = "pledgeReportService")
    private PledgeReportService pledgeReportService;

    @Resource(name = "pledgeReportSaveSheet1File")
    PledgeReportSaveSheet1File pledgeReportSaveSheet1File;

    @Resource(name = "pledgeReportSaveSheet2File")
    PledgeReportSaveSheet2File pledgeReportSaveSheet2File;

    @Resource(name = "pledgeReportSaveSheet3File")
    PledgeReportSaveSheet3File pledgeReportSaveSheet3File;

    @Resource(name = "pledgeReportSaveSheet4File")
    PledgeReportSaveSheet4File pledgeReportSaveSheet4File;

    @Resource(name = "reportWrite000Service")
    private ReportWrite000Service reportWrite000Service;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @RequestMapping("/pledge/pledgeReport.do")
    public String pledgeReport(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("pledgeReport(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("pledgeReport(Map, ModelMap, HttpServletRequest) - end");
        }
        return "pledge/pledgeReport";
    }

    @RequestMapping("/pledge/ajaxPledgeReportPledgeList.do")
    public ModelAndView ajaxPledgeReportPledgeList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportPledgeList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(pledgeReportService.selectPledgeList(jsonParam));

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeReportPledgeList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportPledgeList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/pledge/ajaxPledgeReportSavePledge.do")
    public ModelAndView ajaxPledgeReportSavePledge(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSavePledge(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());
            pledgeReportService.savePledge(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxPledgeReportSavePledge(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSavePledge(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/pledge/ajaxPledgeReportSaveSheet1.do")
    public ModelAndView ajaxPledgeReportSaveSheet1(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet1(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            Map pledgeInfo = pledgeReportService.selectPledgeInfo(jsonParam);
            jsonParam.put("pledgeInfo", pledgeInfo);

            JSONArray pledgeInfoList = JSONArray.fromObject(pledgeReportService.selectPledgeInfoExcelList(jsonParam));
            jsonParam.put("pledgeInfoList", pledgeInfoList);

            pledgeReportSaveSheet1File.buildExcelDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeReportSaveSheet1(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheetP10"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet1(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/pledge/ajaxPledgeReportSaveSheet2.do")
    public ModelAndView ajaxPledgeReportSaveSheet2(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet2(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray dataList = JSONArray.fromObject(reportWrite000Service.selectReport000SheetList(jsonParam));
            jsonParam.put("dataList", dataList);
            JSONArray dataTotList = JSONArray.fromObject(pledgeReportService.selectPledgeTotList(jsonParam));
            jsonParam.put("dataTotList", dataTotList);
            JSONArray dataSumList = JSONArray.fromObject(pledgeReportService.selectPledgeSumList(jsonParam));
            jsonParam.put("dataSumList", dataSumList);

            jsonParam.put("fisFgMstCd100Yn", "Y");
            jsonParam.put("fisFgMstCdEtcYn", "N");
            JSONArray dataList100 = JSONArray.fromObject(reportWrite000Service.selectReport000SheetList(jsonParam));
            jsonParam.put("dataList100", dataList100);
            JSONArray dataTotList100 = JSONArray.fromObject(pledgeReportService.selectPledgeTotList(jsonParam));
            jsonParam.put("dataTotList100", dataTotList100);
            JSONArray dataSumList100 = JSONArray.fromObject(pledgeReportService.selectPledgeSumList(jsonParam));
            jsonParam.put("dataSumList100", dataSumList100);

            jsonParam.put("fisFgMstCd100Yn", "N");
            jsonParam.put("fisFgMstCdEtcYn", "Y");
            JSONArray dataListEtc = JSONArray.fromObject(reportWrite000Service.selectReport000SheetList(jsonParam));
            jsonParam.put("dataListEtc", dataListEtc);
            JSONArray dataSumListEtc = JSONArray.fromObject(pledgeReportService.selectPledgeSumList(jsonParam));
            jsonParam.put("dataSumListEtc", dataSumListEtc);

            JSONArray dataTotListEtc = JSONArray.fromObject(pledgeReportService.selectPledgeTotList(jsonParam));
            jsonParam.put("dataTotListEtc", dataTotListEtc);

            pledgeReportSaveSheet2File.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeReportSaveSheet2(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheetP20"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet2(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/pledge/ajaxPledgeReportSaveSheet3.do")
    public ModelAndView ajaxPledgeReportSaveSheet3(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet3(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray officeList = JSONArray.fromObject(pledgeReportService.selectPledgeOfficeList(jsonParam));
            jsonParam.put("officeList", officeList);
            JSONArray bizList = JSONArray.fromObject(pledgeReportService.selectPledgeBizList(jsonParam));
            jsonParam.put("bizList", bizList);
            JSONArray deptList = JSONArray.fromObject(pledgeReportService.selectPledgeDeptList(jsonParam));
            jsonParam.put("deptList", deptList);

            jsonParam.put("fisFgMstCd100Yn", "Y");
            jsonParam.put("fisFgMstCdEtcYn", "N");
            JSONArray officeList100 = JSONArray.fromObject(pledgeReportService.selectPledgeOfficeList(jsonParam));
            jsonParam.put("officeList100", officeList100);
            JSONArray bizList100 = JSONArray.fromObject(pledgeReportService.selectPledgeBizList(jsonParam));
            jsonParam.put("bizList100", bizList100);
            JSONArray deptList100 = JSONArray.fromObject(pledgeReportService.selectPledgeDeptList(jsonParam));
            jsonParam.put("deptList100", deptList100);

            jsonParam.put("fisFgMstCd100Yn", "N");
            jsonParam.put("fisFgMstCdEtcYn", "Y");
            JSONArray officeListEtc = JSONArray.fromObject(pledgeReportService.selectPledgeOfficeList(jsonParam));
            jsonParam.put("officeListEtc", officeListEtc);
            JSONArray bizListEtc = JSONArray.fromObject(pledgeReportService.selectPledgeBizList(jsonParam));
            jsonParam.put("bizListEtc", bizListEtc);
            JSONArray deptListEtc = JSONArray.fromObject(pledgeReportService.selectPledgeDeptList(jsonParam));
            jsonParam.put("deptListEtc", deptListEtc);

            JSONArray etcList = JSONArray.fromObject(pledgeReportService.selectPledgeEtcList(jsonParam));
            jsonParam.put("etcList", etcList);

            pledgeReportSaveSheet3File.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeReportSaveSheet3(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheetP30"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet3(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/pledge/ajaxPledgeReportSaveSheet4.do")
    public ModelAndView ajaxPledgeReportSaveSheet4(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet4(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray guGunList = JSONArray.fromObject(pledgeReportService.selectPledgeGuGunList(jsonParam));
            jsonParam.put("guGunList", guGunList);
            

            JSONArray guGunDetlList = JSONArray.fromObject(pledgeReportService.selectPledgeGuGunDetlList(jsonParam));
            jsonParam.put("guGunDetlList", guGunDetlList);

            pledgeReportSaveSheet4File.buildSheetDocument(jsonParam, "RP", "");

            jsonObject.put("fileName", jsonParam.get("fileName"));
            jsonObject.put("realFileName", jsonParam.get("realFileName"));
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeReportSaveSheet4(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.saveSheetP30"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeReportSaveSheet4(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

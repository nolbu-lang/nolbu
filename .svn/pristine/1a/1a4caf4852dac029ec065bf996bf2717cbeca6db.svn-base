package com.cs.bcjis.manage.web;

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
import com.cs.bcjis.comm.service.BcjisFileMngService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.manage.service.ManageCloseService;
import com.cs.bcjis.manage.util.ManageCloseUtil;
import com.cs.bcjis.report.service.ReportWrite000Service;
import com.cs.bcjis.report.service.ReportWrite001Service;
import com.cs.bcjis.report.service.ReportWrite002Service;
import com.cs.bcjis.report.service.ReportWrite010Service;
import com.cs.bcjis.report.service.ReportWrite020Service;
import com.cs.bcjis.report.service.ReportWrite030Service;
import com.cs.bcjis.report.service.ReportWrite040Service;
import com.cs.bcjis.report.service.ReportWrite050Service;
import com.cs.bcjis.report.service.ReportWrite055Service;
import com.cs.bcjis.report.service.ReportWrite060Service;
import com.cs.bcjis.report.service.ReportWrite090Service;
import com.cs.bcjis.report.util.Report000SaveFile;
import com.cs.bcjis.report.util.Report001SaveFile;
import com.cs.bcjis.report.util.Report002SaveFile;
import com.cs.bcjis.report.util.Report010SaveFile;
import com.cs.bcjis.report.util.Report020SaveFile;
import com.cs.bcjis.report.util.Report030SaveFile;
import com.cs.bcjis.report.util.Report040SaveFile;
import com.cs.bcjis.report.util.Report050SaveFile;
import com.cs.bcjis.report.util.Report055SaveFile;
import com.cs.bcjis.report.util.Report060SaveFile;
import com.cs.bcjis.report.util.Report090SaveFile;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;


@Controller
public class ManageCloseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ManageCloseController.class);

    @Resource(name = "manageCloseService")
    private ManageCloseService manageCloseService;
    
    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
   
    @Resource(name = "csFileIdGnrService")
    private EgovIdGnrService csFileIdGnrService;

    @Resource(name = "reportWrite000Service")
    private ReportWrite000Service reportWrite000Service;
    
    @Resource(name="report000SaveFile")
    private Report000SaveFile report000SaveFile;
    
    @Resource(name = "reportWrite001Service")
    private ReportWrite001Service reportWrite001Service;
    
    @Resource(name="report001SaveFile")
    private Report001SaveFile report001SaveFile;
    
    @Resource(name = "reportWrite002Service")
    private ReportWrite002Service reportWrite002Service;
    
    @Resource(name="report002SaveFile")
    private Report002SaveFile report002SaveFile;
    
    @Resource(name = "reportWrite010Service")
    private ReportWrite010Service reportWrite010Service;

    @Resource(name="report010SaveFile")
    private Report010SaveFile report010SaveFile;
    
    @Resource(name = "reportWrite020Service")
    private ReportWrite020Service reportWrite020Service;
    
    @Resource(name="report020SaveFile")
    private Report020SaveFile report020SaveFile;
    
    @Resource(name = "reportWrite030Service")
    private ReportWrite030Service reportWrite030Service;
    
    @Resource(name="report030SaveFile")
    private Report030SaveFile report030SaveFile;
    
    @Resource(name = "reportWrite040Service")
    private ReportWrite040Service reportWrite040Service;
    
    @Resource(name="report040SaveFile")
    private Report040SaveFile report040SaveFile;
    
    @Resource(name = "reportWrite050Service")
    private ReportWrite050Service reportWrite050Service;
    
    @Resource(name="report050SaveFile")
    private Report050SaveFile report050SaveFile;
    
    @Resource(name = "reportWrite055Service")
    private ReportWrite055Service reportWrite055Service;
    
    @Resource(name="report055SaveFile")
    private Report055SaveFile report055SaveFile;
    
    @Resource(name = "reportWrite060Service")
    private ReportWrite060Service reportWrite060Service;
    
    @Resource(name="report060SaveFile")
    private Report060SaveFile report060SaveFile;
    
    @Resource(name = "reportWrite090Service")
    private ReportWrite090Service reportWrite090Service;
    
    @Resource(name="report090SaveFile")
    private Report090SaveFile report090SaveFile;
    
    @Resource(name="bcjisFileMngService")
    private BcjisFileMngService bcjisFileMngService;
    
    @RequestMapping("/manage/manageClose.do")
    public String manageClose(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("manageClose(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("manageClose(Map, ModelMap, HttpServletRequest) - end");
        }
        return "manage/manageClose";
    }

    
    @RequestMapping("/manage/ajaxManageCloseList.do")
    public ModelAndView ajaxManageCloseList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageCloseList(ModelMap, HttpServletRequest) - start");
        }
       

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(manageCloseService.selectManageCloseListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(manageCloseService.selectManageCloseList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxManageCloseList(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);


        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageCloseList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    
    @RequestMapping("/manage/ajaxManageCloseUpdateManageClose.do")
    public ModelAndView ajaxManageCloseUpdateManageClose(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageCloseUpdateManageClose(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            
            manageCloseService.updateManageClose(jsonParam);
            
            if("Y".equals(jsonParam.get("closeYn")) == true){
                String atchFileId = csFileIdGnrService.getNextStringId();
                buildSheetDocument(jsonParam, atchFileId);
                jsonParam.put("atchFileId", atchFileId);
                
                manageCloseService.insertManageCloseHis(jsonParam);
            }else{
                manageCloseService.updateManageCloseHis(jsonParam);
            }
                       
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxManageCloseUpdateManageClose(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageCloseUpdateManageClose(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    public void buildSheetDocument(JSONObject jsonParam, String atchFileId) throws Exception {
        
        ManageCloseUtil manageCloseUtil = new ManageCloseUtil(jsonParam, atchFileId
                , reportWrite000Service, report000SaveFile
                , reportWrite001Service, report001SaveFile
                , reportWrite002Service, report002SaveFile
                , reportWrite010Service, report010SaveFile
                , reportWrite020Service, report020SaveFile
                , reportWrite030Service, report030SaveFile
                , reportWrite040Service, report040SaveFile
                , reportWrite050Service, report050SaveFile
                , reportWrite055Service, report055SaveFile
                , reportWrite060Service, report060SaveFile
                , reportWrite090Service, report090SaveFile
                , bcjisFileMngService);
        
        Thread t = new Thread(manageCloseUtil);
        t.start();
    }
}

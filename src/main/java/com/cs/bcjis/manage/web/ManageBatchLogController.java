package com.cs.bcjis.manage.web;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.batch.util.BatchApi;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.manage.service.ManageBatchLogService;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class ManageBatchLogController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ManageBatchLogController.class);

    @Resource(name = "manageBatchLogService")
    private ManageBatchLogService manageBatchLogService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "batchApi")
    private BatchApi batchApi;

    @Autowired
    @Qualifier("config")
    private Properties config;
    
    @RequestMapping("/manage/manageBatchLog.do")
    public String manageBatchLogList(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("manageBatchLogList(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("manageBatchLogList(Map, ModelMap, HttpServletRequest) - end");
        }
        return "manage/manageBatchLogList";
    }

    @RequestMapping("/manage/ajaxManageBatchLogList.do")
    public ModelAndView ajaxManageBatchLogList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchLogList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(manageBatchLogService.selectManageBatchLogListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(manageBatchLogService.selectManageBatchLogList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

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
    
    @RequestMapping("/manage/ajaxManageBatchProc.do")
    public ModelAndView ajaxManageBatchProc(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchProc(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());

            batchApi.getData();
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "배치가 실행되었습니다.");

        } catch (Exception e) {
            logger.error("ajaxManageBatchProc(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "배치를 실패 하였습니다.");
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchProc(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/manage/ajaxManageBatchLogFileList.do")
    public ModelAndView ajaxManageBatchLogFileList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchLogFileList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            Properties prop = new Properties();
            prop.load(this.getClass().getClassLoader().getResourceAsStream("csframework/bcjisProps/api.properties"));
            String logPath = prop.getProperty("api.LogPath");
            
            JSONArray resultList = batchApi.getLogFileList(logPath);
            
            System.out.println(resultList.size());
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxManageBatchLogFileList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageBatchLogFileList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
}

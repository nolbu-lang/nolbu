package com.cs.bcjis.budget.web;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.budget.service.BudgetCopyNewApplyJob;
import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.budget.service.impl.BudgetCopyNewApplyQueueService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class BudgetCopyNewController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BudgetCopyNewController.class);
    
    @Resource(name = "budgetCopyNewService")
    private BudgetCopyNewService budgetCopyNewService;

    @Resource(name = "budgetCopyNewApplyQueueService")
    private BudgetCopyNewApplyQueueService budgetCopyNewApplyQueueService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    /** 목록 조회 공통: JSON 파라미터 + 사용자/부서권한 세팅 */
    private JSONObject prepareListJsonParam(HttpServletRequest request) throws Exception {
        JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

        BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
        jsonParam.put("userId", bcjisUserVO.getUserId());
        if (BcjisCommUtil.isNullString(jsonParam.get("userDeptYn")) == true) {
            jsonParam.put("userDeptYn", bcjisCommService.getDeptUserYn(bcjisUserVO, ReportSaveUtil.getStringValue(jsonParam.get("reportCd"))));
        }

        return jsonParam;
    }

    private ModelAndView buildListAjaxModel(JSONArray resultList, Exception e, String logContext) {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        if (e == null) {
            jsonObject.put("dataList", resultList);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } else {
            logger.error(logContext, e);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    @RequestMapping("/budget/budgetCopyNew.do")
    public String budgetCopyNew(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopyNew(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopyNew(Map, ModelMap, HttpServletRequest) - end");
        }
        return "budget/budgetCopyNew";
    }

    @RequestMapping("/budget/budgetCopyMap.do")
    public String budgetCopyMap(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopyMap(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopyMap(Map, ModelMap, HttpServletRequest) - end");
        }
        // 전년도예산/조서 적용[매핑일괄] — 개선 화면(budgetCopyMap.jsp: 매핑·일괄적용)
        return "budget/budgetCopyMap";
    }
    
    @RequestMapping("/budget/ajaxBudgetCopyNewList.do")
    public ModelAndView ajaxBudgetPreCopyPageList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewList(ModelMap, HttpServletRequest) - start");
        }

        JSONArray resultList = null;
        Exception error = null;

        try {
            JSONObject jsonParam = prepareListJsonParam(request);
            resultList = JSONArray.fromObject(budgetCopyNewService.selectCopyReportList(jsonParam));
        } catch (Exception e) {
            error = e;
        }

        ModelAndView ajaxModel = buildListAjaxModel(resultList, error, "ajaxBudgetCopyNewList(ModelMap, HttpServletRequest)");

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    /**
     * [경량] 매핑용 세세목 평면 목록 조회 (제목 + 조정액).
     */
    @RequestMapping("/budget/ajaxBudgetCopyNewMapList.do")
    public ModelAndView ajaxBudgetCopyNewMapList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewMapList(ModelMap, HttpServletRequest) - start");
        }

        JSONArray resultList = null;
        Exception error = null;

        try {
            JSONObject jsonParam = prepareListJsonParam(request);
            resultList = JSONArray.fromObject(budgetCopyNewService.selectCopyNewMapList(jsonParam));
        } catch (Exception e) {
            error = e;
        }

        ModelAndView ajaxModel = buildListAjaxModel(resultList, error, "ajaxBudgetCopyNewMapList(ModelMap, HttpServletRequest)");

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewMapList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    /**
     * [신규] 매핑 목록 일괄 적용. 여러 (전년도->올해) 쌍을 한 번에 적용한다.
     * 각 쌍은 기존 단건 적용과 동일한 로직(copyReport + copyPreInfo)으로 처리되어 결과가 동일하다.
     * ※ 하위 호환용 동기 API. 다수 동시 적용 시에는 enqueue API 사용을 권장한다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("/budget/ajaxBudgetCopyNewCopyReportBatch.do")
    public ModelAndView ajaxBudgetCopyNewCopyReportBatch(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewCopyReportBatch(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray mappingArr = jsonParam.getJSONArray("mappings");

            List<Map> mappings = new ArrayList<Map>();
            for (int i = 0; i < mappingArr.size(); i++) {
                JSONObject item = mappingArr.getJSONObject(i);
                item.put("userId", bcjisUserVO.getUserId());
                mappings.add(item);
            }

            budgetCopyNewService.copyReportBatch(mappings);

            jsonObject.put("appliedCnt", mappings.size());
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.apply"));
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyNewCopyReportBatch(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewCopyReportBatch(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    /**
     * 일괄적용 대기열 등록(비동기). 20명 동시 적용 시 FIFO 순차 처리.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("/budget/ajaxBudgetCopyNewCopyReportBatchEnqueue.do")
    public ModelAndView ajaxBudgetCopyNewCopyReportBatchEnqueue(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            JSONArray mappingArr = jsonParam.getJSONArray("mappings");

            List<Map> mappings = new ArrayList<Map>();
            for (int i = 0; i < mappingArr.size(); i++) {
                JSONObject item = mappingArr.getJSONObject(i);
                item.put("userId", bcjisUserVO.getUserId());
                mappings.add(item);
            }

            String userNm = bcjisUserVO.getUserNm();
            BudgetCopyNewApplyJob job = budgetCopyNewApplyQueueService.enqueue(
                    bcjisUserVO.getUserId(), userNm, mappings);

            putJobToJson(jsonObject, job);
            // 이미 진행 중인 요청을 반환한 경우에도 SUCC + alreadyActive 플래그
            if (job.getMessage() != null && job.getMessage().indexOf("이미 대기") >= 0
                    && (BudgetCopyNewApplyJob.STATUS_QUEUED.equals(job.getStatus())
                        || BudgetCopyNewApplyJob.STATUS_RUNNING.equals(job.getStatus()))) {
                jsonObject.put("alreadyActive", "Y");
            }
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, job.getMessage());
        } catch (IllegalStateException ise) {
            logger.warn("ajaxBudgetCopyNewCopyReportBatchEnqueue: " + ise.getMessage());
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, ise.getMessage());
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyNewCopyReportBatchEnqueue(ModelMap, HttpServletRequest)", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    /**
     * 일괄적용 Job 상태 조회(폴링).
     */
    @RequestMapping("/budget/ajaxBudgetCopyNewCopyReportBatchStatus.do")
    public ModelAndView ajaxBudgetCopyNewCopyReportBatchStatus(ModelMap model, HttpServletRequest request) throws Exception {
        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            String jobId = jsonParam == null ? null : String.valueOf(jsonParam.get("jobId"));
            if (jobId == null || "null".equals(jobId) || jobId.trim().length() < 1) {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "jobId가 없습니다.");
            } else {
                BudgetCopyNewApplyJob job = budgetCopyNewApplyQueueService.getJob(jobId.trim());
                if (job == null) {
                    jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                    jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, "적용 요청을 찾을 수 없습니다. 다시 시도하여 주십시오.");
                } else {
                    putJobToJson(jsonObject, job);
                    jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
                    jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, job.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyNewCopyReportBatchStatus(ModelMap, HttpServletRequest)", e);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        return ajaxModel;
    }

    private void putJobToJson(JSONObject jsonObject, BudgetCopyNewApplyJob job) {
        jsonObject.put("jobId", job.getJobId());
        jsonObject.put("status", job.getStatus());
        jsonObject.put("queuePos", Integer.valueOf(job.getQueuePos()));
        jsonObject.put("totalCnt", Integer.valueOf(job.getTotalCnt()));
        jsonObject.put("appliedCnt", Integer.valueOf(job.getAppliedCnt()));
        jsonObject.put("jobMessage", job.getMessage() == null ? "" : job.getMessage());
    }

    @RequestMapping("/budget/ajaxBudgetCopyNewCopyReport.do")
    public ModelAndView ajaxBudgetCopyCopyReport(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewCopyReport(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            budgetCopyNewService.copyReport(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.apply"));
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyNewCopyReport(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyNewCopyReport(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

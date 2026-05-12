package com.cs.bcjis.budget.web;

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

import com.cs.bcjis.budget.service.BudgetCopyService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class BudgetCopyController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BudgetCopyController.class);
    
    @Resource(name = "budgetCopyService")
    private BudgetCopyService budgetCopyService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/budget/budgetCopy.do")
    public String budgetCopy(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopy(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("budgetCopy(Map, ModelMap, HttpServletRequest) - end");
        }
        return "budget/budgetCopy";
    }

    @RequestMapping("/budget/ajaxBudgetCopyReportList.do")
    public ModelAndView ajaxBudgetCopyReportList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyReportList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            jsonParam.put("userId", bcjisUserVO.getUserId());
            if(BcjisCommUtil.isNullString(jsonParam.get("userDeptYn")) == true){
                jsonParam.put("userDeptYn", bcjisCommService.getDeptUserYn(bcjisUserVO, ReportSaveUtil.getStringValue(jsonParam.get("reportCd"))));
            }

            JSONArray resultList = JSONArray.fromObject(budgetCopyService.selectReportList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyReportList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyReportList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }


    @RequestMapping("/budget/ajaxBudgetCopyCopyReport.do")
    public ModelAndView ajaxBudgetCopyCopyReport(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyCopyReport(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            budgetCopyService.copyReport(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.apply"));
        } catch (Exception e) {
            logger.error("ajaxBudgetCopyCopyReport(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyCopyReport(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

package com.cs.bcjis.budget.web;

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

import com.cs.bcjis.budget.service.BudgetPreCopyService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class BudgetPreCopyController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BudgetPreCopyController.class);

    @Resource(name = "budgetPreCopyService")
    private BudgetPreCopyService budgetPreCopyService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @RequestMapping("/budget/budgetPreCopy.do")
    public String budgetPreCopy(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        return "budget/budgetPreCopy";
    }

    @RequestMapping("/budget/budgetPreCopyPage.do")
    public String budgetPreCopyPage(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        return "budget/budgetPreCopyPage";
    }

    @RequestMapping("/budget/ajaxBudgetPreCopyList.do")
    public ModelAndView ajaxBudgetPreCopyList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetPreCopyList(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(budgetPreCopyService.selectPreCopyList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxBudgetPreCopyList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetPreCopyList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/budget/ajaxBudgetPreCopyPageList.do")
    public ModelAndView ajaxBudgetPreCopyPageList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetPreCopyPageList(ModelMap, HttpServletRequest) - start");
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

            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(budgetPreCopyService.selectPreCopyPageListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(budgetPreCopyService.selectPreCopyPageList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("data", jsonParam);
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxBudgetPreCopyPageList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetPreCopyPageList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/budget/ajaxBudgetPreCopyCopyPreInfo.do")
    public ModelAndView ajaxBudgetPreCopyCopyPreInfo(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCopyCopyReport(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());
            budgetPreCopyService.copyPreInfo(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.apply"));
        } catch (Exception e) {
            logger.error("ajaxBudgetPreCopyCopyPreInfo(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetPreCopyCopyPreInfo(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

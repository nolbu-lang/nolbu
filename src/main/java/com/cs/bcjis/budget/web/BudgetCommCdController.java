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

import com.cs.bcjis.budget.service.BudgetApplyService;
import com.cs.bcjis.budget.service.BudgetCommCdService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class BudgetCommCdController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BudgetCommCdController.class);
    
    @Resource(name = "budgetCommCdService")
    private BudgetCommCdService budgetCommCdService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;
    
    @RequestMapping("/budget/ajaxBudgetCommCdList.do")
    public ModelAndView ajaxBudgetApplyDgrCompoRlkList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCommCdList(ModelMap, HttpServletRequest) - start");
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

            JSONArray resultList = JSONArray.fromObject(budgetCommCdService.selectList(jsonParam));
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxBudgetCommCdList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxBudgetCommCdList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/budget/ajaxDialogDgrcompoSaveCommCd.do")
    public ModelAndView ajaxDialogDgrcompoModifySaveDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoSaveCommCd(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            budgetCommCdService.saveCommCd(jsonParam);
            
            JSONArray resultList = JSONArray.fromObject(budgetCommCdService.selectList(jsonParam));
            jsonObject.put("dataList", resultList);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoSaveCommCd(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoSaveCommCd(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/budget/ajaxDialogDgrcompoDelCommCd.do")
    public ModelAndView ajaxDialogDgrcompoDelCommCd(ModelMap model, HttpServletRequest request) throws Exception {
    	if (logger.isDebugEnabled()) {
    		logger.debug("ajaxDialogDgrcompoDelCommCd(ModelMap, HttpServletRequest) - start");
    	}
    	
    	ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
    	JSONObject jsonObject = new JSONObject();
    	
    	try {
    		
    		BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
    		JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
    		jsonParam.put("userId", bcjisUserVO.getUserId());
    		budgetCommCdService.delCommCd(jsonParam);
    		
    		JSONArray resultList = JSONArray.fromObject(budgetCommCdService.selectList(jsonParam));
    		jsonObject.put("dataList", resultList);
    		
    		jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
    		jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.delete"));
    	} catch (Exception e) {
    		logger.error("ajaxDialogDgrcompoDelCommCd(ModelMap, HttpServletRequest)", e);
    		
    		jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
    		jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.delete"));
    	}
    	
    	ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug("ajaxDialogDgrcompoDelCommCd(ModelMap, HttpServletRequest) - end");
    	}
    	return ajaxModel;
    }
    
}

package com.cs.bcjis.pledge.web;

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

import com.cs.bcjis.pledge.service.PledgeCopyService;
import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;

@Controller
public class PledgeCopyController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(PledgeCopyController.class);
    
    @Resource(name = "pledgeCopyService")
    private PledgeCopyService pledgeCopyService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @RequestMapping("/pledge/pledgeCopy.do")
    public String pledgeCopy(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("pledgeCopy(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("pledgeCopy(Map, ModelMap, HttpServletRequest) - end");
        }
        return "pledge/pledgeCopy";
    }

    @RequestMapping("/pledge/ajaxPledgeCopyPledgeList.do")
    public ModelAndView ajaxPledgeCopyPledgeList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeCopyPledgeList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(pledgeCopyService.selectPledgeList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeCopyPledgeList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeCopyPledgeList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }


    @RequestMapping("/pledge/ajaxPledgeCopyCopyPledge.do")
    public ModelAndView ajaxPledgeCopyCopyPledge(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeCopyCopyPledge(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            jsonParam.put("userId", bcjisUserVO.getUserId());
            pledgeCopyService.copyPledge(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.apply"));
        } catch (Exception e) {
            logger.error("ajaxPledgeCopyCopyPledge(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.apply"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeCopyCopyPledge(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
}

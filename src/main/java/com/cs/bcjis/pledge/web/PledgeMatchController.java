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
import com.cs.bcjis.pledge.service.PledgeMatchService;

@Controller
public class PledgeMatchController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(PledgeMatchController.class);

    @Resource(name = "pledgeMatchService")
    private PledgeMatchService pledgeMatchService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @RequestMapping("/pledge/pledgeMatch.do")
    public String pledgeMatch(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("pledgeMatch(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("pledgeMatch(Map, ModelMap, HttpServletRequest) - end");
        }
        return "pledge/pledgeMatch";
    }

    @RequestMapping("/pledge/ajaxPledgeMatchSelectPledgeInfoList.do")
    public ModelAndView ajaxPledgeMatchSelectPledgeInfoList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeMatchSelectPledgeInfoList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(pledgeMatchService.selectPledgeInfoIdList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxPledgeMatchSelectPledgeInfoList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeMatchSelectPledgeInfoList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @RequestMapping("/pledge/ajaxPledgeMatchUpdatePledgeInfoId.do")
    public ModelAndView ajaxPledgeMatchUpdatePledgeInfoId(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeMatchUpdatePledgeInfoId(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {

            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());

            pledgeMatchService.updatePledgeInfoId(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxPledgeMatchUpdatePledgeInfoId(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxPledgeMatchUpdatePledgeInfoId(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

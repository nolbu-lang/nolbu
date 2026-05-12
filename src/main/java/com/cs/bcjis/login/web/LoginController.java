package com.cs.bcjis.login.web;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.login.service.LoginService;

import egovframework.rte.fdl.cryptography.EgovCryptoService;

@Controller
public class LoginController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @Resource(name = "loginService")
    private LoginService loginService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @Resource(name = "ARIACryptoService")
    private EgovCryptoService egovCryptoService;

    @RequestMapping("/login/login.do")
    public String login(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("login(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("login(Map, ModelMap, HttpServletRequest) - end");
        }
        return "login/login";
    }

    @RequestMapping("/login/ajaxLogin.do")
    public ModelAndView ajaxLogin(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxLogin(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            String loginId = jsonParam.getString("loginId");
            String pswd = jsonParam.getString("pswd");
            if (BcjisCommUtil.isNullString(loginId) == true || BcjisCommUtil.isNullString(pswd) == true) {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.login.isNotExistInfo"));

                ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

                if (logger.isDebugEnabled()) {
                    logger.debug("ajaxLogin(ModelMap, HttpServletRequest) - end");
                }
                return ajaxModel;

            }

            BcjisUserVO bcjisUserVO = loginService.selectLoginInfo(jsonParam);

            if (bcjisUserVO == null || BcjisCommUtil.isNullString(bcjisUserVO.getUserId()) == true) {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.login.isNotExistUserId"));

                ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

                if (logger.isDebugEnabled()) {
                    logger.debug("ajaxLogin(ModelMap, HttpServletRequest) - end");
                }
                return ajaxModel;
            }

            if (BcjisCommUtil.getEgovEncrypt(egovCryptoService, pswd).equals(bcjisUserVO.getPswd()) == false) {
                jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
                jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.login.invalidPassword"));

                ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

                if (logger.isDebugEnabled()) {
                    logger.debug("ajaxLogin(ModelMap, HttpServletRequest) - end");
                }
                return ajaxModel;

            }

            request.getSession().setAttribute("bcjisUserVO", bcjisUserVO);

            jsonParam.put("connectYn", "Y");
            jsonParam.put("finalConnectIp", request.getRemoteAddr());
            jsonParam.put("finalConnectSessid", request.getSession().getId());
            jsonParam.put("userId", bcjisUserVO.getUserId());
            loginService.updateUserLoginInfo(jsonParam);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.login.login"));

        } catch (Exception e) {
            logger.error("ajaxLogin(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxLogin(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("/login/logoutAction.do")
    public String logoutAction(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("logoutAction(Map, ModelMap, HttpServletRequest) - start");
        }

        
        BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();

        Map userLogMap = new HashMap();
        userLogMap.put("connectYn", "N");
        userLogMap.put("userId", user.getUserId());
        loginService.updateUserLoginInfo(userLogMap);
        
        request.getSession().setAttribute("bcjisUserVO", null);
        
        if (logger.isDebugEnabled()) {
            logger.debug("logoutAction(Map, ModelMap, HttpServletRequest) - end");
        }
        return "redirect:/";
    }

}

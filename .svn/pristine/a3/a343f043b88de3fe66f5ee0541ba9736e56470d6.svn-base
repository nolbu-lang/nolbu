package com.cs.bcjis.manage.web;

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

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.manage.service.ManageUserService;


@Controller
public class ManageUserController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ManageUserController.class);
 
    @Resource(name = "manageUserService")
    private ManageUserService manageUserService;
    
    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/manage/manageUser.do")
    public String manageCommcdList(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("manageCommcdList(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("manageCommcdList(Map, ModelMap, HttpServletRequest) - end");
        }
        return "manage/manageUserList";
    }
    
    @RequestMapping("/manage/ajaxManageUserList.do")
    public ModelAndView ajaxManageUserList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageUserList(ModelMap, HttpServletRequest) - start");
        }
       

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(manageUserService.selectManageUserUserListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(manageUserService.selectManageUserUserList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxManageUserList(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);


        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageUserList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    
    @RequestMapping("/manage/ajaxManageUserDeleteUserList.do")
    public ModelAndView ajaxManageUserDeleteUserList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageUserDeleteUserList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            
            manageUserService.deleteUser(jsonParam);
                       
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.delete"));
        } catch (Exception e) {
            logger.error("ajaxManageUserDeleteUserList(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.delete"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageUserDeleteUserList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    
}

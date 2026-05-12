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
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.manage.service.ManageCommcdService;


@Controller
public class ManageCommcdController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ManageCommcdController.class);
    
    @Resource(name = "manageCommcdService")
    private ManageCommcdService manageCommcdService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
   
    @RequestMapping("/manage/manageCommcd.do")
    public String manageCommcdList(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("manageCommcdList(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("manageCommcdList(Map, ModelMap, HttpServletRequest) - end");
        }
        return "manage/manageCommcdList";
    }
    @RequestMapping("/manage/ajaxManageCommcdList.do")
    public ModelAndView ajaxReportCenterList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportCenterList(ModelMap, HttpServletRequest) - start");
        }
       

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            bcjisCommVO.setTotalCount(manageCommcdService.selectManageCommcdCommcddetlListCnt(jsonParam));

            jsonParam.put("rowNum", bcjisCommVO.getRowNum());
            jsonParam.put("start", bcjisCommVO.getStart());

            JSONArray resultList = JSONArray.fromObject(manageCommcdService.selectManageCommcdCommcddetlList(jsonParam));

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxReportCenterList(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);


        if (logger.isDebugEnabled()) {
            logger.debug("ajaxReportCenterList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
   
}

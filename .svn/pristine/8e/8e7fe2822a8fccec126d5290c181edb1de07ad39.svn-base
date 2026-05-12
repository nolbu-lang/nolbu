package com.cs.bcjis.manage.web;

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
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.manage.service.ManageTeMngVeriService;


@Controller
public class ManageTeMngVeriController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ManageTeMngVeriController.class);

    @Resource(name = "manageTeMngVeriService")
    private ManageTeMngVeriService manageTeMngVeriService;
    
    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @RequestMapping("/manage/manageTeMngVeri.do")
    public String manageTeMngVeri(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("manageTeMngVeri(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("manageTeMngVeri(Map, ModelMap, HttpServletRequest) - end");
        }
        return "manage/manageTeMngVeri";
    }

    
    @RequestMapping("/manage/ajaxManageTeMngVeriList.do")
    public ModelAndView ajaxManageTeMngVeriList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageTeMngVeriList(ModelMap, HttpServletRequest) - start");
        }
       

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            BcjisCommVO bcjisCommVO = new BcjisCommVO();
            bcjisCommVO.setPage(jsonParam.get("page"));
            bcjisCommVO.setRowNum(jsonParam.get("rowNum"));
            
            JSONArray resultList = JSONArray.fromObject(manageTeMngVeriService.selectManageTeMngVeriList(jsonParam));
            
            bcjisCommVO.setTotalCount(resultList.size());

            bcjisCommVO.setJsonData(jsonObject);

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxManageTeMngVeriList(ModelMap, HttpServletRequest)", e);
            
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);


        if (logger.isDebugEnabled()) {
            logger.debug("ajaxManageTeMngVeriList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
}


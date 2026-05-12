package com.cs.bcjis.dialog.web;

import org.apache.log4j.Logger;

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
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisCommVO;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.dialog.service.DialogDgrDeptSeltService;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Controller
public class DialogDgrDeptSeltController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrDeptSeltController.class);
    
    @Resource(name = "dialogDgrDeptSeltService")
    private DialogDgrDeptSeltService dialogDgrDeptSeltService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;
    
    @RequestMapping("/dialog/ajaxDgrDeptSeltList.do")
    public ModelAndView ajaxDgrDeptSeltList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrDeptSeltList(ModelMap, HttpServletRequest) - start");
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
            
            BcjisCommVO cmiosComVO = new BcjisCommVO();
            cmiosComVO.setPage(jsonParam.get("page"));
            cmiosComVO.setRowNum(jsonParam.get("rowNum"));
            cmiosComVO.setTotalCount(dialogDgrDeptSeltService.selectDgrDeptSeltListCnt(jsonParam));
            
            jsonParam.put("rowNum", cmiosComVO.getRowNum());
            jsonParam.put("start", cmiosComVO.getStart());

            JSONArray resultList = JSONArray.fromObject(dialogDgrDeptSeltService.selectDgrDeptSeltList(jsonParam));

            cmiosComVO.setJsonData(jsonObject);
            
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgrDeptSeltList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrDeptSeltList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

}

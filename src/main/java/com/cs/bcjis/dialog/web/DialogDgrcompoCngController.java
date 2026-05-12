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
import com.cs.bcjis.dialog.service.DialogDgrcompoCngService;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;
import com.cs.bcjis.dialog.service.DialogDgrcompoSortService;
import com.cs.bcjis.report.util.ReportSaveUtil;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Controller
public class DialogDgrcompoCngController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrcompoCngController.class);
    
    @Resource(name = "dialogDgrcompoCngService")
    private DialogDgrcompoCngService dialogDgrcompoCngService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;
    
    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;
    
    @Resource(name = "dialogDgrcompoModifyService")
    private DialogDgrcompoModifyService dialogDgrcompoModifyService;

    @RequestMapping("/dialog/ajaxDgrCngHistoryListList.do")
    public ModelAndView ajaxDgrDeptSeltList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngHistoryListList(ModelMap, HttpServletRequest) - start");
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
            cmiosComVO.setTotalCount(dialogDgrcompoCngService.selectDgrCngHistoryListCnt(jsonParam));
            
            jsonParam.put("rowNum", cmiosComVO.getRowNum());
            jsonParam.put("start", cmiosComVO.getStart());

            JSONArray resultList = JSONArray.fromObject(dialogDgrcompoCngService.selectDgrCngHistoryList(jsonParam));

            cmiosComVO.setJsonData(jsonObject);
            
            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgrCngHistoryListList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngHistoryListList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDgrCngLogData.do")
    public ModelAndView ajaxDialogDgrcompoModifySelectDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngLogData(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoCngService.selectCngLogData(jsonParam));

            jsonObject.put("logData", dgrcompo);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgrCngLogData(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngLogData(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    @RequestMapping("/dialog/ajaxDgrCngModiList.do")
    public ModelAndView ajaxDgrCngModiList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngHistoryListList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
        	JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray dgrcompo = JSONArray.fromObject(dialogDgrcompoCngService.selectDgrCompoDataList(jsonParam));
            jsonObject.put("dataList", dgrcompo);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgrCngModiList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrCngModiList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    //분리된 예산안 수정
    @RequestMapping("/dialog/ajaxDialogDgrcompoModifyMergeSaveDgrcompos.do")
    public ModelAndView ajaxDialogDgrcompoModifySaveDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoModifyMergeSaveDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgrcompoCngService.updateDgrcompos(jsonParam);
            
            JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoModifyService.selectModifyDgrcompo(jsonParam));

            jsonObject.put("dgrcompo", dgrcompo);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoModifyMergeSaveDgrcompos(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoModifyMergeSaveDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
	//예산안 병합
	@RequestMapping("/dialog/ajaxDialogDgrcompoCngDgrcompoMerge.do")
	public ModelAndView ajaxDialogDgrcompoRegiMergeDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoCngDgrcompoMerge(ModelMap, HttpServletRequest) - start");
		}

		ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
		JSONObject jsonObject = new JSONObject();

		try {
			BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
			JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
			jsonParam.put("userId", bcjisUserVO.getUserId());
			dialogDgrcompoCngService.dgrcompoMerge(jsonParam);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
		} catch (Exception e) {
			logger.error("ajaxDialogDgrcompoCngDgrcompoMerge(ModelMap, HttpServletRequest)", e);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
		}

		ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoCngDgrcompoMerge(ModelMap, HttpServletRequest) - end");
		}
		return ajaxModel;
	}
	
	//예산안 분리
	@RequestMapping("/dialog/ajaxDialogDgrcompoCngDgrcompoSep.do")
	public ModelAndView ajaxDialogDgrcompoCngDgrcompoSep(ModelMap model, HttpServletRequest request) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoCngDgrcompoSep(ModelMap, HttpServletRequest) - start");
		}

		ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
		JSONObject jsonObject = new JSONObject();

		try {
			BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
			JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
			jsonParam.put("userId", bcjisUserVO.getUserId());
			
			dialogDgrcompoCngService.dgrcompoSep(jsonParam);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
		} catch (Exception e) {
			logger.error("ajaxDialogDgrcompoCngDgrcompoSep(ModelMap, HttpServletRequest)", e);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
		}

		ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoCngDgrcompoSep(ModelMap, HttpServletRequest) - end");
		}
		return ajaxModel;
	}
}

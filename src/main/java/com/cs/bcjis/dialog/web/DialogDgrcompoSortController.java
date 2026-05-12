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
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.dialog.service.DialogDgrcompoSortService;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Controller
public class DialogDgrcompoSortController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DialogDgrcompoSortController.class);
    
    @Resource(name = "dialogDgrcompoSortService")
    private DialogDgrcompoSortService dialogDgrcompoSortService;

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

	@Resource(name = "csCngHisGnrService")
	private EgovIdGnrService csCngHisGnrService;

    //기존 정렬순서변경 목록
    @RequestMapping("/dialog/ajaxDgrcompoSortDgrcompoList.do")
    public ModelAndView ajaxDgrcompoSortDgrcompoList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrcompoSortDgrcompoList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONArray resultList = JSONArray.fromObject(dialogDgrcompoSortService.selectDgrcompoSortDgrcompoList(jsonParam));

            jsonObject.put("dataList", resultList);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxDgrcompoSortDgrcompoList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDgrcompoSortDgrcompoList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }
    
    //변경된 정렬순서 통계목 제외
    @RequestMapping("/dialog/ajaxDgrcompoSortDgrcompoListNew.do")
    public ModelAndView ajaxDgrcompoSortDgrcompoListNew(ModelMap model, HttpServletRequest request) throws Exception {
    	if (logger.isDebugEnabled()) {
    		logger.debug("ajaxDgrcompoSortDgrcompoListNew(ModelMap, HttpServletRequest) - start");
    	}
    	
    	ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
    	JSONObject jsonObject = new JSONObject();
    	
    	try {
    		JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
    		
    		JSONArray resultList = JSONArray.fromObject(dialogDgrcompoSortService.selectDgrcompoSortDgrcompoListNew(jsonParam));
    		
    		jsonObject.put("dataList", resultList);
    		
    		jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
    	} catch (Exception e) {
    		logger.error("ajaxDgrcompoSortDgrcompoListNew(ModelMap, HttpServletRequest)", e);
    		
    		jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
    	}
    	
    	ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug("ajaxDgrcompoSortDgrcompoListNew(ModelMap, HttpServletRequest) - end");
    	}
    	return ajaxModel;
    }

    @RequestMapping("/dialog/ajaxDialogDgrcompoSortSaveDgrcompoSorts.do")
    public ModelAndView ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            jsonParam.put("userId", bcjisUserVO.getUserId());
            dialogDgrcompoSortService.saveDgrcompoSorts(jsonParam);
            
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
        } catch (Exception e) {
            logger.error("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest)", e);
                        
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxDialogDgrcompoRegiSaveDgrcompos(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

	@RequestMapping("/dialog/ajaxDialogDgrcompoSortMergeDgrcompoSorts.do")
	public ModelAndView ajaxDialogDgrcompoRegiMergeDgrcompos(ModelMap model, HttpServletRequest request) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoRegiMergeDgrcompos(ModelMap, HttpServletRequest) - start");
		}

		ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
		JSONObject jsonObject = new JSONObject();

		try {
			JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
			jsonParam.put("cngHistoryId", csCngHisGnrService.getNextStringId());
			dialogDgrcompoSortService.mergeDgrcompoSorts(jsonParam);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("success.common.save"));
		} catch (Exception e) {
			logger.error("ajaxDialogDgrcompoRegiMergeDgrcompos(ModelMap, HttpServletRequest)", e);

			jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
			jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.save"));
		}

		ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

		if (logger.isDebugEnabled()) {
			logger.debug("ajaxDialogDgrcompoRegiMergeDgrcompos(ModelMap, HttpServletRequest) - end");
		}
		return ajaxModel;
	}
}

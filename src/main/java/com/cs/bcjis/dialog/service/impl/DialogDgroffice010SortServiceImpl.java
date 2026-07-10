package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogDgroffice010SortService;

@Service("dialogDgroffice010SortService")
public class DialogDgroffice010SortServiceImpl implements DialogDgroffice010SortService {
    @Resource(name = "dialogDgroffice010SortDAO")
    private DialogDgroffice010SortDAO dialogDgroffice010SortDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgroffice010List(Map map) throws Exception {
        return dialogDgroffice010SortDAO.selectDgroffice010List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveOfficeRank010s(JSONObject jsonParam) throws Exception {
        JSONObject tempParam = null;
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        for(int i = 0; i < saveDatas.size(); i++){
            tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            
            dialogDgroffice010SortDAO.updateOfficeRank010(tempParam);
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgroffice0F0List(Map map) throws Exception {
    	return dialogDgroffice010SortDAO.selectDgroffice0F0List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveOfficeRank0F0s(JSONObject jsonParam) throws Exception {
    	JSONObject tempParam = null;
    	List saveDatas = jsonParam.getJSONArray("saveDatas");
    	for(int i = 0; i < saveDatas.size(); i++){
    		tempParam = (JSONObject) saveDatas.get(i);
    		
    		tempParam.put("userId", jsonParam.get("userId"));
    		
    		dialogDgroffice010SortDAO.updateOfficeRank0F0(tempParam);
    	}
    	
    }
    
    @SuppressWarnings("rawtypes")
    public void initOfficeRank010(JSONObject jsonParam) throws Exception {
    	dialogDgroffice010SortDAO.initOfficeRank010(jsonParam);
    }
}

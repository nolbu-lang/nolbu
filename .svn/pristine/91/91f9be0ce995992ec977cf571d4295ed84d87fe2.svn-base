package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogDgrcompoSortService;

@Service("dialogDgrcompoSortService")
public class DialogDgrcompoSortServiceImpl implements DialogDgrcompoSortService {
    @Resource(name = "dialogDgrcompoSortDAO")
    private DialogDgrcompoSortDAO dialogDgrcompoSortDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoList(Map map) throws Exception {
        return dialogDgrcompoSortDAO.selectDgrcompoSortDgrcompoList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoListNew(Map map) throws Exception {
    	return dialogDgrcompoSortDAO.selectDgrcompoSortDgrcompoListNew(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveDgrcompoSorts(JSONObject jsonParam) throws Exception {
        JSONObject tempParam = null;
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        for(int i = 0; i < saveDatas.size(); i++){
            tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            
            dialogDgrcompoSortDAO.updateDgrcompoSort(tempParam);
        }
        
    }

	@SuppressWarnings("rawtypes")
	public void mergeDgrcompoSorts(JSONObject jsonParam) throws Exception {
		JSONObject tempParam = null;
		List saveDatas = jsonParam.getJSONArray("saveDatas");
		for(int i = 0; i < saveDatas.size(); i++){
			tempParam = (JSONObject) saveDatas.get(i);
			tempParam.put("cngHistoryId", jsonParam.get("cngHistoryId"));
			
			if(tempParam.get("rowId").equals("Y")){
				//변경이력 아이디 생성
				dialogDgrcompoSortDAO.updateCngHistory(tempParam);
			}
		}
	}
}

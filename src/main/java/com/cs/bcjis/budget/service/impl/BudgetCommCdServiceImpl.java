package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCommCdService;
import com.cs.bcjis.budget.service.BudgetCopyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetCommCdService")
public class BudgetCommCdServiceImpl implements BudgetCommCdService {

    @Resource(name = "budgetCommCdDAO")
    private BudgetCommCdDAO budgetCommCdDAO;

    @Override
    public List selectList(Map map) throws Exception {
        return budgetCommCdDAO.selectList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveCommCd(JSONObject jsonParam) throws Exception {
    	
    	JSONArray saveList = JSONArray.fromObject(jsonParam.get("saveData"));
    	String maxCdStr = budgetCommCdDAO.selectMaxCd(jsonParam);
    	
    	int maxCd = Integer.parseInt(maxCdStr);
    	String userId = (String) jsonParam.get("userId");
    			
    	for(int i=0; i<saveList.size() ; i++){
    		JSONObject saveData = saveList.getJSONObject(i);
    		saveData.put("userId", userId);
    		String addYn = (String)saveData.get("addYn");
    		if("Y".equals(addYn)){
    			maxCd++;
    			saveData.put("detlCd", maxCd);
    			budgetCommCdDAO.insertCommCd(saveData);
    		}else{
    			budgetCommCdDAO.updateCommCd(saveData);
    		}
    	}
    }
    
    @SuppressWarnings("rawtypes")
    public void delCommCd(JSONObject jsonParam) throws Exception {
    	
    	JSONArray delList = JSONArray.fromObject(jsonParam.get("delData"));
    	String userId = (String) jsonParam.get("userId");
    			
    	for(int i=0; i<delList.size() ; i++){
    		JSONObject delData = delList.getJSONObject(i);
    		delData.put("userId", userId);
    		budgetCommCdDAO.updateCommCdUseYn(delData);
    	}
    }

}

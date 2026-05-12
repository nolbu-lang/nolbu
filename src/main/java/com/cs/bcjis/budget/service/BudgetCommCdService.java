package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface BudgetCommCdService {
    
    @SuppressWarnings("rawtypes")      
    public List selectList(Map map) throws Exception;
    
    public void saveCommCd(JSONObject jsonParam) throws Exception;
    
    public void delCommCd(JSONObject jsonParam) throws Exception;
    
}

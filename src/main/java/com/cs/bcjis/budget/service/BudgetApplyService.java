package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface BudgetApplyService {
    
    @SuppressWarnings("rawtypes")      
    public List selectDgrcompoRlkList(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")      
    public List selectDgrcompoList(Map map) throws Exception;

    public void saveDgrcompoDatas(JSONObject jsonParam) throws Exception;

    public void detlDgrcompoDatas(JSONObject jsonParam) throws Exception;

    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompoInfoAll(Map param) throws Exception;
}

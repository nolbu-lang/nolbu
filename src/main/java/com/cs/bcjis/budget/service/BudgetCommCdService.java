package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface BudgetCommCdService {
    
    @SuppressWarnings("rawtypes")
    public List selectList(Map map) throws Exception;

    /** 사용안함(USE_YN='N') 코드도 포함 — 이미 연결된 사업의 이름표시 전용 */
    @SuppressWarnings("rawtypes")
    public List selectListAll(Map map) throws Exception;

    public void saveCommCd(JSONObject jsonParam) throws Exception;
    
    public void delCommCd(JSONObject jsonParam) throws Exception;
    
}

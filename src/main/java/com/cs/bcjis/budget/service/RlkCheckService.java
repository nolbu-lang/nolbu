package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface RlkCheckService {

    @SuppressWarnings("rawtypes")
    public List selectDeptList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception;

    public void applyDeptCd(JSONObject jsonParam) throws Exception;
}

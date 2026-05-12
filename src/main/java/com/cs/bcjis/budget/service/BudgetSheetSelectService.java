package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface BudgetSheetSelectService {

    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception;

    public void saveSheet(JSONObject jsonParam) throws Exception;

    @SuppressWarnings("rawtypes")
    public Map getExistDataMap(Map map) throws Exception;
}

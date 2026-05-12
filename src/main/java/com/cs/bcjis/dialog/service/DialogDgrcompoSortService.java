package com.cs.bcjis.dialog.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface DialogDgrcompoSortService {

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoList(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoListNew(Map map) throws Exception;

    public void saveDgrcompoSorts(JSONObject jsonParam) throws Exception;

    public void mergeDgrcompoSorts(JSONObject jsonParam) throws Exception;

}

package com.cs.bcjis.dialog.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface DialogDgroffice020SortService {

    @SuppressWarnings("rawtypes")
    public List selectDgroffice020List(Map map) throws Exception;
    
    public void saveOfficeRank020s(JSONObject jsonParam) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public List selectDgroffice0F0List(Map map) throws Exception;
    
    public void saveOfficeRank0F0s(JSONObject jsonParam) throws Exception;

}

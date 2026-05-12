package com.cs.bcjis.dialog.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface DialogDgrcompoRegiService {
    
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompoRegiUpDgrcompoInfo(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharList(Map map) throws Exception;

    public void saveDgrcompos(JSONObject jsonParam) throws Exception;

}

package com.cs.bcjis.pledge.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface PledgeSelectService {
    
    @SuppressWarnings("rawtypes")      
    public List selectDgrCompoList(Map map) throws Exception;

    public void savePledge(JSONObject jsonParam) throws Exception;
}

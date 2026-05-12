package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite055Service {

    @SuppressWarnings("rawtypes")
    public List selectReport055List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport055ExcelList(Map map) throws Exception;

    public void saveReport055(JSONObject jsonParam) throws Exception;

}

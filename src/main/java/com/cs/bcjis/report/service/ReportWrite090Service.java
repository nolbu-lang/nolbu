package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite090Service {

    @SuppressWarnings("rawtypes")
    public List selectReport090List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport090ExcelList(Map map) throws Exception;

    public void saveReport090(JSONObject jsonParam) throws Exception;

}

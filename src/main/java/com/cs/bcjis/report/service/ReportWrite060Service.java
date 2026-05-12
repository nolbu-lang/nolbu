package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite060Service {

    @SuppressWarnings("rawtypes")
    public List selectReport060List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport060ExcelList(Map map) throws Exception;

    public void saveReport060(JSONObject jsonParam) throws Exception;

}

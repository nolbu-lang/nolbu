package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite070Service {

    @SuppressWarnings("rawtypes")
    public List selectReport070List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport070ExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception;

    public void saveReport070(JSONObject jsonParam) throws Exception;

}

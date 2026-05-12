package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite0A0Service {

    @SuppressWarnings("rawtypes")
    public List selectReport0A0List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport0A0ExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception;

    public void saveReport0A0(JSONObject jsonParam) throws Exception;

}

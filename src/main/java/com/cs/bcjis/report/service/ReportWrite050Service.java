package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite050Service {

    @SuppressWarnings("rawtypes")
    public List selectReport050List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport050ExcelList(Map map) throws Exception;

    public void saveReport050(JSONObject jsonParam) throws Exception;

}

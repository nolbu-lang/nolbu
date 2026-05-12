package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

public interface ReportWrite001Service {

    @SuppressWarnings("rawtypes")
    public List selectReport001SheetList(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList2(Map map) throws Exception;

}

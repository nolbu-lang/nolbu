package com.cs.bcjis.report.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ReportWrite020Service {

    @SuppressWarnings("rawtypes")
    public List selectReport020List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public int selectReport020PageListCnt(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020PageList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020TotList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelListTot(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList2(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport027ExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020RptTotList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeSiList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportOfficeSi022List(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelListTot(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportGugunTotExcelList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectReportGugunExcelList(Map map) throws Exception;

    public void saveReport020(JSONObject jsonParam) throws Exception;

}

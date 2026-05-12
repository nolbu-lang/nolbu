package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite020DAO")
public class ReportWrite020DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport020List(Map map) throws Exception{
        return list("ReportWrite020.selectReport020List", map);
    }

    @SuppressWarnings("rawtypes")
    public int selectReport020PageListCnt(Map map) throws Exception {
        return (Integer) getSqlMapClientTemplate().queryForObject("ReportWrite020.selectReport020PageListCnt", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020PageList(Map map) throws Exception {
        return list("ReportWrite020.selectReport020PageList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport020TotList(Map map) throws Exception{
        return list("ReportWrite020.selectReport020TotList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList(Map map) throws Exception{
        return list("ReportWrite020.selectReport020ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelListTot(Map map) throws Exception{
        return list("ReportWrite020.selectReport020ExcelListTot", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList2(Map map) throws Exception{
        return list("ReportWrite020.selectReport020ExcelList2", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport027ExcelList(Map map) throws Exception{
        return list("ReportWrite020.selectReport027ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020RptTotList(Map map) throws Exception{
        return list("ReportWrite020.selectReport020RptTotList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeList(Map map) throws Exception{
        return list("ReportWrite020.selectReport020OfficeList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeSiList(Map map) throws Exception{
        return list("ReportWrite020.selectReport020OfficeSiList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportOfficeSi022List(Map map) throws Exception{
        return list("ReportWrite020.selectReportOfficeSi022List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception{
        return list("ReportWrite020.selectReportBizExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelListTot(Map map) throws Exception{
        return list("ReportWrite020.selectReportBizExcelListTot", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportGugunTotExcelList(Map map) throws Exception{
        return list("ReportWrite020.selectReportGugunTotExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportGugunExcelList(Map map) throws Exception{
        return list("ReportWrite020.selectReportGugunExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport020(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport020H", map);
        }catch(Exception e){
            logger.error("updateReport020(map)", e);
        }
        
        update("ReportWrite020.updateReport020", map);
    }
}

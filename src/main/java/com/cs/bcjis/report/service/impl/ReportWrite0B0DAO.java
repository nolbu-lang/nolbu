package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite0B0DAO")
public class ReportWrite0B0DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport0B0List(Map map) throws Exception{
        return list("ReportWrite0B0.selectReport0B0List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport0B0ExcelList(Map map) throws Exception{
        return list("ReportWrite0B0.selectReport0B0ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception{
        return list("ReportWrite0B0.selectReportBizExcelList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertReport0B0(Map map) throws Exception{       
        insert("ReportWrite0B0.insertReport0B0", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertReport(Map map) throws Exception{       
        insert("ReportWrite0B0.insertReport", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport0B0(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport0B0H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite0B0.updateReport0B0", map);
    }
}

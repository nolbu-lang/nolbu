package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite070DAO")
public class ReportWrite070DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport070List(Map map) throws Exception{
        return list("ReportWrite070.selectReport070List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport070ExcelList(Map map) throws Exception{
        return list("ReportWrite070.selectReport070ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception{
        return list("ReportWrite070.selectReportBizExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport070(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport070H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite070.updateReport070", map);
    }
}

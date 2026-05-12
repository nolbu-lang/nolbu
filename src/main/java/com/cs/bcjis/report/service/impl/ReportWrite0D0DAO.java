package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite0D0DAO")
public class ReportWrite0D0DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport0D0List(Map map) throws Exception{
        return list("ReportWrite0D0.selectReport0D0List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport0D0ExcelList(Map map) throws Exception{
        return list("ReportWrite0D0.selectReport0D0ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception{
        return list("ReportWrite0D0.selectReportBizExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport0D0(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport0D0H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite0D0.updateReport0D0", map);
    }
}

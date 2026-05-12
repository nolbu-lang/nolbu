package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite0A0DAO")
public class ReportWrite0A0DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport0A0List(Map map) throws Exception{
        return list("ReportWrite0A0.selectReport0A0List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport0A0ExcelList(Map map) throws Exception{
        return list("ReportWrite0A0.selectReport0A0ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception{
        return list("ReportWrite0A0.selectReportBizExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport0A0(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport0A0H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite0A0.updateReport0A0", map);
    }
}

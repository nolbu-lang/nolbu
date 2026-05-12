package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite060DAO")
public class ReportWrite060DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport060List(Map map) throws Exception{
        return list("ReportWrite060.selectReport060List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport060ExcelList(Map map) throws Exception{
        return list("ReportWrite060.selectReport060ExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport060(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport060H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite060.updateReport060", map);
    }
}

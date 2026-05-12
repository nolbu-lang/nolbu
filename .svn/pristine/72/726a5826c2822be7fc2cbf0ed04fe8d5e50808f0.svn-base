package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite055DAO")
public class ReportWrite055DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport055List(Map map) throws Exception{
        return list("ReportWrite055.selectReport055List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport055ExcelList(Map map) throws Exception{
        return list("ReportWrite055.selectReport055ExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport055(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport055H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite055.updateReport055", map);
    }
}

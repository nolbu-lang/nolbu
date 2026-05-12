package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite040DAO")
public class ReportWrite040DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport040List(Map map) throws Exception{
        return list("ReportWrite040.selectReport040List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport040ExcelList(Map map) throws Exception{
        return list("ReportWrite040.selectReport040ExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport040(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport040H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite040.updateReport040", map);
    }
}

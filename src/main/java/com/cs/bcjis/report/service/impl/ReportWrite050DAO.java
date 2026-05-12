package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite050DAO")
public class ReportWrite050DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport050List(Map map) throws Exception{
        return list("ReportWrite050.selectReport050List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport050ExcelList(Map map) throws Exception{
        return list("ReportWrite050.selectReport050ExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport050(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport050H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite050.updateReport050", map);
    }
}

package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite030DAO")
public class ReportWrite030DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport030List(Map map) throws Exception{
        return list("ReportWrite030.selectReport030List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport030ExcelList(Map map) throws Exception{
        return list("ReportWrite030.selectReport030ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport034ExcelList(Map map) throws Exception{
        return list("ReportWrite030.selectReport034ExcelList", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport030Sheet001List(Map map) throws Exception{
        return list("ReportWrite030.selectReport030Sheet001List", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport030(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport030H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite030.updateReport030", map);
    }
}

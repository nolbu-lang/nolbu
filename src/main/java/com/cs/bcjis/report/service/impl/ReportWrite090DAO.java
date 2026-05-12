package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite090DAO")
public class ReportWrite090DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport090List(Map map) throws Exception{
        return list("ReportWrite090.selectReport090List", map);
    }
 
    @SuppressWarnings("rawtypes")
    public List selectReport090ExcelList(Map map) throws Exception{
        return list("ReportWrite090.selectReport090ExcelList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport090(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport090H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("ReportWrite090.updateReport090", map);
    }
}

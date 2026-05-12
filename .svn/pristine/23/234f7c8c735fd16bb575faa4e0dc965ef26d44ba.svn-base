package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoRegiDAO")
public class DialogDgrcompoRegiDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompoRegiUpDgrcompoInfo(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoModify.selectDgrcompoRegiUpDgrcompoInfo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompoRegiInfo(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoModify.selectDgrcompoRegiInfo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscList(Map map) throws Exception{
        return list("DialogDgrcompoRegi.selectDgrcompofrscList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharList(Map map) throws Exception{
        return list("DialogDgrcompoRegi.selectDgrcompocharList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompo(Map map) throws Exception{
        insert("DialogDgrcompoRegi.insertDgrcompo", map);
        
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("updateDgrcompo(map)", e);
        }
    }

}

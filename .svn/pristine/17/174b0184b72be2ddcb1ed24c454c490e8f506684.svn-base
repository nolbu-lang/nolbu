package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoTeMngRegiDAO")
public class DialogDgrcompoTeMngRegiDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompo(Map map) throws Exception{
        insert("DialogDgrcompoTeMngRegi.insertDgrcompo", map);
        
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("updateDgrcompo(map)", e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void insertTeMngMok(Map map) throws Exception{
        insert("DialogDgrcompoTeMngRegi.insertTeMngMok", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertDgrcompofrsc(Map map) throws Exception{
        insert("DialogDgrcompoTeMngRegi.insertDgrcompofrsc", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertDgrcompochar(Map map) throws Exception{
        insert("DialogDgrcompoTeMngRegi.insertDgrcompochar", map);
    }

}

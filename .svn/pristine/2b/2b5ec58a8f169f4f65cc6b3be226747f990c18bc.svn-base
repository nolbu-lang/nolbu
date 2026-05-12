package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoModifyDAO")
public class DialogDgrcompoModifyDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompo(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoModify.selectDgrcompo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscList(Map map) throws Exception{
        return list("DialogDgrcompoModify.selectDgrcompofrscList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharList(Map map) throws Exception{
        return list("DialogDgrcompoModify.selectDgrcompocharList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompo(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("updateDgrcompo(map)", e);
        }
        
        update("DialogDgrcompoModify.updateDgrcompo", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectModifyDgrcompo(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoModify.selectModifyDgrcompo", map);
    }
}

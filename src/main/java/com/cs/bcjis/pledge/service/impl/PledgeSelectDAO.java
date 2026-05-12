package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("pledgeSelectDAO")
public class PledgeSelectDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception{
        return list("PledgeSelect.selectDgrCompoList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectPledgeKeyList(Map map) throws Exception{
        return list("PledgeSelect.selectPledgeKeyList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertPledge(Map map) throws Exception{
        insert("PledgeSelect.insertPledge", map);
        
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbPledgeH", map);
        }catch(Exception e){
            logger.error("insertPledge(map)", e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deletePledge(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbPledgeH", map);
        }catch(Exception e){
            logger.error("deletePledge(map)", e);
        }
        
        delete("PledgeSelect.deletePledge", map);
    }

}

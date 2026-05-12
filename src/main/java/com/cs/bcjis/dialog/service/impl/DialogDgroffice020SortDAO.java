package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgroffice020SortDAO")
public class DialogDgroffice020SortDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDgroffice020List(Map map) throws Exception{
        return list("DialogDgroffice020Sort.selectDgroffice020List", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateOfficeRank020(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgroffice020H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("DialogDgroffice020Sort.updateOfficeRank020", map);
    }
}

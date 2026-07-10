package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgroffice010SortDAO")
public class DialogDgroffice010SortDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDgroffice010List(Map map) throws Exception{
        return list("DialogDgroffice010Sort.selectDgroffice010List", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateOfficeRank010(Map map) throws Exception{
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgroffice010H", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("DialogDgroffice010Sort.updateOfficeRank010", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgroffice0F0List(Map map) throws Exception{
    	return list("DialogDgroffice010Sort.selectDgroffice0F0List", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateOfficeRank0F0(Map map) throws Exception{
    	update("DialogDgroffice010Sort.updateOfficeRank0F0", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void initOfficeRank010(Map map) throws Exception{
    	update("DialogDgroffice010Sort.initOfficeRank010", map);
    }
}

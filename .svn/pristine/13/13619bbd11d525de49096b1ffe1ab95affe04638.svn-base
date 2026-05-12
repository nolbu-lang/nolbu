package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoSortDAO")
public class DialogDgrcompoSortDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoList(Map map) throws Exception{
        return list("DialogDgrcompoSort.selectDgrcompoSortDgrcompoList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoSortDgrcompoListNew(Map map) throws Exception{
    	return list("DialogDgrcompoSort.selectDgrcompoSortDgrcompoListNew", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompoSort(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("updateDgrcompoSort(map)", e);
        }
        
        update("DialogDgrcompoSort.updateDgrcompoSort", map);
    }

	@SuppressWarnings("rawtypes")
	public void updateCngHistory(Map map) {
		insert("DialogDgrcompoSort.insertCngHistory", map);
	}
}
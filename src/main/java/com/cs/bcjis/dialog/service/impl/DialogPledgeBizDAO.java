package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogPledgeBizDAO")
public class DialogPledgeBizDAO extends BcjisCommAbstractDAO {
    @SuppressWarnings("rawtypes")
    public void insertPledgeBiz(Map map) {
        insert("DialogPledgeBiz.insertPledgeBiz", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updatePledgeBiz(Map map) {
        update("DialogPledgeBiz.updatePledgeBiz", map);
    }
    
}

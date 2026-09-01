package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("rlkCheckDAO")
public class RlkCheckDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDeptList(Map map) throws Exception {
        return list("RlkCheck.selectDeptList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception {
        return list("RlkCheck.selectDgrcompoList", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompoDept(Map map) throws Exception {
        try {
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        } catch (Exception e) {
            logger.error("updateDgrcompoDept(map)", e);
        }

        update("RlkCheck.updateDgrcompoDept", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertCngHistory(Map map) throws Exception {
        insert("RlkCheck.insertCngHistory", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrCompoOri(Map map) throws Exception {
        insert("RlkCheck.insertDgrCompoOri", map);
    }
}

package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("pledgeManageDAO")
public class PledgeManageDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoList(Map map) throws Exception {
        return list("PledgeManage.selectPledgeInfoList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void deletePledgeInfo(Map map) {
        update("PledgeManage.deletePledgeInfo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void deletePledgeBiz(Map map) {
        update("PledgeManage.deletePledgeBiz", map);
    }
}

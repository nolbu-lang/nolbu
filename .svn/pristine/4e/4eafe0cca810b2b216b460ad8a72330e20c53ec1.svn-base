package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("pledgeMatchDAO")
public class PledgeMatchDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoIdList(Map map) throws Exception {
        return list("PledgeMatch.selectPledgeInfoIdList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updatePledgeInfoId(Map map) {
        update("PledgeMatch.updatePledgeInfoId", map);
    }
}

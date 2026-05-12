package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("pledgeCopyDAO")
public class PledgeCopyDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectCopyPledgeList(Map map) throws Exception {

        return list("PledgeCopy.selectCopyPledgeList", map);
    }

    @SuppressWarnings("rawtypes")
    public void copyPledge(Map map) throws Exception {

        update("PledgeCopy.updatePledge", map);
    }

}

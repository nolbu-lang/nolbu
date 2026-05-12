package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.pledge.service.PledgeCopyService;

@Service("pledgeCopyService")
public class PledgeCopyServiceImpl implements PledgeCopyService {

    @Resource(name = "pledgeCopyDAO")
    private PledgeCopyDAO pledgeCopyDAO;

    @SuppressWarnings("rawtypes")
    public List selectPledgeList(Map map) throws Exception {

        return pledgeCopyDAO.selectCopyPledgeList(map);
    }

    @SuppressWarnings("rawtypes")
    public void copyPledge(Map map) throws Exception {
        pledgeCopyDAO.copyPledge(map);
    }

}

package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.pledge.service.PledgeManageService;

@Service("pledgeManageService")
public class PledgeManageServiceImpl implements PledgeManageService {

    @Resource(name = "pledgeManageDAO")
    private PledgeManageDAO pledgeManageDAO;

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoList(Map map) throws Exception {
        return pledgeManageDAO.selectPledgeInfoList(map);
    }

    @SuppressWarnings("rawtypes")
    public void deletePledgeInfo(Map map) throws Exception {
        pledgeManageDAO.deletePledgeInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public void deletePledgeBiz(Map map) throws Exception {
        pledgeManageDAO.deletePledgeBiz(map);
    }
}

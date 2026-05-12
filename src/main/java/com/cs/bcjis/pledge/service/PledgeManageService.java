package com.cs.bcjis.pledge.service;

import java.util.List;
import java.util.Map;

public interface PledgeManageService {

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void deletePledgeInfo(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void deletePledgeBiz(Map map) throws Exception;

}

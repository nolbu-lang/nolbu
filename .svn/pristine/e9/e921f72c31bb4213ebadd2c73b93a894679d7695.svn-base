package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("manageBatchLogDAO")
public class ManageBatchLogDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectManageBatchLogList(Map map) throws Exception {
        return list("ManageBatchLog.selectManageBatchLogList", map);
    }

    @SuppressWarnings("rawtypes")
    public int selectManageBatchLogListCnt(Map map) throws Exception {
        return (Integer) getSqlMapClientTemplate().queryForObject("ManageBatchLog.selectManageBatchLogListCnt", map);
    }

}

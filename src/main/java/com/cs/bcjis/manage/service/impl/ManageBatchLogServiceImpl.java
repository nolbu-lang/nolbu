package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.manage.service.ManageBatchLogService;

@Service("manageBatchLogService")
public class ManageBatchLogServiceImpl implements ManageBatchLogService {

    @Resource(name = "manageBatchLogDAO")
    private ManageBatchLogDAO manageBatchLogDAO;

    @SuppressWarnings("rawtypes")
    public List selectManageBatchLogList(Map map) throws Exception {
        // TODO Auto-generated method stub
        return manageBatchLogDAO.selectManageBatchLogList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectManageBatchLogListCnt(Map map) throws Exception {
        // TODO Auto-generated method stub
        return manageBatchLogDAO.selectManageBatchLogListCnt(map);
    }
}

package com.cs.bcjis.manage.service;

import java.util.List;
import java.util.Map;

public interface ManageBatchLogService {

    @SuppressWarnings("rawtypes")
    public List selectManageBatchLogList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public int selectManageBatchLogListCnt(Map map) throws Exception;

}

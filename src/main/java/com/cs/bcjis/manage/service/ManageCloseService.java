package com.cs.bcjis.manage.service;

import java.util.List;
import java.util.Map;

public interface ManageCloseService {

    @SuppressWarnings("rawtypes")
    public List selectManageCloseList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public int selectManageCloseListCnt(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void updateManageClose(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void insertManageCloseHis(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void updateManageCloseHis(Map map) throws Exception;
    
}

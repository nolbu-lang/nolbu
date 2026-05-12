package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("manageCloseDAO")
public class ManageCloseDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectManageCloseList(Map map) throws Exception{
        return list("ManageClose.selectManageCloseList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectManageCloseListcnt(Map map) throws Exception{
        return (Integer)getSqlMapClientTemplate().queryForObject("ManageClose.selectManageCloseListCnt", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageClose(Map map){
        update("ManageClose.updateManageClose", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertManageCloseHis(Map map){
        insert("ManageClose.insertManageCloseHis", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageCloseHis(Map map){
        update("ManageClose.updateManageCloseHis", map);
    }
    
}

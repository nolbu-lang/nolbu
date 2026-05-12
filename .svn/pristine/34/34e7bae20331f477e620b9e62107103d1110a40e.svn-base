package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("manageUserDAO")
public class ManageUserDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectManageUserUserList(Map map) throws Exception{
        return list("ManageUser.selectManageUserUserList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectManageUserUserListcnt(Map map) throws Exception{
        return (Integer)getSqlMapClientTemplate().queryForObject("ManageUser.selectManageUserUserListCnt", map);
    }

    @SuppressWarnings("rawtypes")
    public void deleteManageUser(Map map){
        delete("ManageUser.deleteManageUser", map);
    }
    @SuppressWarnings("rawtypes")
    public void deleteManageUserPowgbr(Map map){
        delete("ManageUser.deleteManageUserPowgbr", map);
    }
    
}

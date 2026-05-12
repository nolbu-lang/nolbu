package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("manageCommcdDAO")
public class ManageCommcdDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectManageCommcddetlList(Map map) throws Exception{
        return list("ManageCommcd.selectManageCommcdCommcddetlList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectManageCommcddetlListcnt(Map map) throws Exception{
        return (Integer)getSqlMapClientTemplate().queryForObject("ManageCommcd.selectManageCommcdCommcddetlListCnt", map);
    }
    
  
}

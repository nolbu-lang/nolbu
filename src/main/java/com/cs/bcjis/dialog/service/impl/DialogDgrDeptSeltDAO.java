package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrDeptSeltDAO")
public class DialogDgrDeptSeltDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDgrDeptSeltList(Map map) throws Exception{
        return list("DialogDgrDeptSelt.selectDgrDeptSeltList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectDgrDeptSeltListCnt(Map map) {
        return (Integer)getSqlMapClientTemplate().queryForObject("DialogDgrDeptSelt.selectDgrDeptSeltListCnt", map);
    }
}

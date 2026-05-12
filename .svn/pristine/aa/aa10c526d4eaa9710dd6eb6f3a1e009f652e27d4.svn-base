package com.cs.bcjis.comm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;
import com.cs.bcjis.comm.util.BcjisCommUtil;

@Repository("bcjisCommDAO")
public class BcjisCommDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectLeftMenuList(Map map) throws Exception {
        return list("BcjisComm.selectLeftMenuList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectCommComboList(Map map) throws Exception {
        String queryId = "BcjisComm.selectCommComboList";
        if (BcjisCommUtil.isNullString(map.get("subQueryId")) == false) {
            queryId += map.get("subQueryId");
        }

        return list(queryId, map);
    }

    @SuppressWarnings("rawtypes")
    public void insertTracelog(Map map) throws Exception {
        insert("BcjisComm.insertTracelog", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectIsCloseYn(Map map) throws Exception {
        return (Map) selectByPk("BcjisComm.selectIsCloseYn", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectDeptUserYn(Map map) throws Exception {
        return (Map) selectByPk("BcjisComm.selectDeptUserYn", map);
    }
}

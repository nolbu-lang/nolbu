package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetPreCopyDAO")
public class BudgetPreCopyDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectPreCopyList(Map map) throws Exception {
        return list("BudgetPreCopy.selectPreCopyList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPreCopyPageList(Map map) throws Exception {
        return list("BudgetPreCopy.selectPreCopyPageList", map);
    }

    @SuppressWarnings("rawtypes")
    public int selectPreCopyPageListCnt(Map map) throws Exception {
        return (Integer) getSqlMapClientTemplate().queryForObject("BudgetPreCopy.selectPreCopyPageListCnt", map);
    }

}

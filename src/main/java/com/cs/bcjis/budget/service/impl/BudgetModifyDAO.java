package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetModifyDAO")
public class BudgetModifyDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception {
        return list("BudgetModify.selectDgrcompoList", map);
    }
}

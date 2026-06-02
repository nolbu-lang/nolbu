package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetCopyNewDAO")
public class BudgetCopyNewDAO extends BcjisCommAbstractDAO {

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyReportList(Map map) throws Exception {

        return list("BudgetCopyNew.selectCopyReportList", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyNewMapList(Map map) throws Exception {

        return list("BudgetCopyNew.selectCopyNewMapList", map);
    }
}

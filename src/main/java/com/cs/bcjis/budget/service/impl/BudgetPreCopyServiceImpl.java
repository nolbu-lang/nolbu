package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetPreCopyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetPreCopyService")
public class BudgetPreCopyServiceImpl implements BudgetPreCopyService {
    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetPreCopyDAO")
    private BudgetPreCopyDAO budgetPreCopyDAO;

    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @SuppressWarnings("rawtypes")
    public List selectPreCopyList(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyList(map);
    }

    @SuppressWarnings("rawtypes")
    public void copyPreInfo(Map map) throws Exception {
        budgetCommDAO.copyPreInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectPreCopyPageListCnt(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyPageListCnt(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPreCopyPageList(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyPageList(map);
    }

}

package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetCopyService")
public class BudgetCopyServiceImpl implements BudgetCopyService {
    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetCopyDAO")
    private BudgetCopyDAO budgetCopyDAO;

    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @SuppressWarnings("rawtypes")
    public List selectReportList(Map map) throws Exception {

        return budgetCopyDAO.selectCopyReportList(map);
    }

    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        reportCommDAO.copyReport(map);
        budgetCommDAO.copyPreInfo(map);
    }

}

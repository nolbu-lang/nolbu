package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetModifyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetModifyService")
public class BudgetModifyServiceImpl implements BudgetModifyService {
    
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name = "budgetModifyDAO")
    private BudgetModifyDAO budgetModifyDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception {
        return budgetModifyDAO.selectDgrcompoList(map);
    }

    @SuppressWarnings("rawtypes")
    public void deleteDgrcompos(Map map) throws Exception {
        budgetCommDAO.deleteDgrcompochar(map);
        budgetCommDAO.deleteDgrcompofrsc(map);
        
        reportCommDAO.deleteAllReport(map);
        budgetCommDAO.deleteDgrcompo(map);
        
        budgetCommDAO.saveUpDgrcompoInfoAll(map);
    }
}

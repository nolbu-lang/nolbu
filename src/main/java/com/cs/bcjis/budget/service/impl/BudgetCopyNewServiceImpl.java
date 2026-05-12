package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.budget.service.BudgetCopyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetCopyNewService")
public class BudgetCopyNewServiceImpl implements BudgetCopyNewService {

	@Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
	
	@Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
	
    @Resource(name = "budgetCopyNewDAO")
    private BudgetCopyNewDAO budgetCopyNewDAO;

    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception {
    	
    	map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));
    	
        return budgetCopyNewDAO.selectCopyReportList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        reportCommDAO.copyReport(map);
        budgetCommDAO.copyPreInfo(map);
    }
}

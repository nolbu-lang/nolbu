package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

public interface BudgetCopyNewService {
    
    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception;
    
}

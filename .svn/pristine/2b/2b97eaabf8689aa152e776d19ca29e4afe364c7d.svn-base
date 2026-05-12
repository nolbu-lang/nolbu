package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetSelectDAO")
public class BudgetSelectDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(String reportCd, Map map) throws Exception{
        if("070".equals(reportCd) == true){
            return list("BudgetSelect.selectDgrCompoList070", map);
        }
        
        return list("BudgetSelect.selectDgrCompoList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportKeyList(String reportCd, Map map) throws Exception{
        if("070".equals(reportCd) == true){
            return list("BudgetSelect.selectReportKeyList070", map);
        }
        
        return list("BudgetSelect.selectReportKeyList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportKeyListNew(String reportCd, Map map) throws Exception{
    	if("070".equals(reportCd) == true){
    		return list("BudgetSelect.selectReportKeyListNew070", map);
    	}
    	
    	return list("BudgetSelect.selectReportKeyListNew", map);
    }
}

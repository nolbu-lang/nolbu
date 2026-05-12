package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetCommCdDAO")
public class BudgetCommCdDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectList(Map map) throws Exception{
        return list("BudgetCommCd.selectList", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertCommCd(Map map) throws Exception{
    	insert("BudgetCommCd.insertCommCd", map);
    }
    
    @SuppressWarnings("rawtypes")
    public String selectMaxCd(Map map) throws Exception{
    	return (String) selectByPk("BudgetCommCd.selectMaxCd", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectCnt(Map map) throws Exception{
    	return (Integer)getSqlMapClientTemplate().queryForObject("BudgetCommCd.selectCnt", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateCommCd(Map map) throws Exception{
    	update("BudgetCommCd.updateCommCd", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateCommCdUseYn(Map map) throws Exception{
    	update("BudgetCommCd.updateCommCdUseYn", map);
    }
}

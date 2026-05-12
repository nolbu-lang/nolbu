package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetApplyDAO")
public class BudgetApplyDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoRlkList(Map map) throws Exception{
        return list("BudgetApply.selectDgrcompoRlkList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception{
        return list("BudgetApply.selectDgrcompoList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompo(Map map) throws Exception{
        return (Map)selectByPk("BudgetApply.selectDgrcompo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectDgrcompoCnt(Map map) {
        return (Integer)getSqlMapClientTemplate().queryForObject("BudgetApply.selectDgrcompoCnt", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompo(Map map) throws Exception{
        insert("BudgetApply.insertDgrcompo", map);

        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("insertDgrcompo(map)", e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompo(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("updateDgrcompo(map)", e);
        }
        
        update("BudgetApply.updateDgrcompo", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompo(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("deleteDgrcompo(map)", e);
        }
        
        delete("BudgetApply.deleteDgrcompo", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompofrsc(Map map) throws Exception{
        insert("BudgetApply.insertDgrcompofrsc", map);
        
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompofrscH", map);
        }catch(Exception e){
            logger.error("insertDgrcompofrsc(map)", e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompofrsc(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompofrscH", map);
        }catch(Exception e){
            logger.error("updateDgrcompofrsc(map)", e);
        }
        
        update("BudgetApply.updateDgrcompofrsc", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompofrsc(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompofrscH", map);
        }catch(Exception e){
            logger.error("updateDgrcompofrsc(map)", e);
        }
        
        delete("BudgetApply.deleteDgrcompofrsc", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompochar(Map map) throws Exception{
        insert("BudgetApply.insertDgrcompochar", map);
        
        try{
            map.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompocharH", map);
        }catch(Exception e){
            logger.error("insertDgrcompochar(map)", e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompochar(Map map) throws Exception{
        try{
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbDgrcompocharH", map);
        }catch(Exception e){
            logger.error("updateDgrcompochar(map)", e);
        }
        
        update("BudgetApply.updateDgrcompochar", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompochar(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompocharH", map);
        }catch(Exception e){
            logger.error("deleteDgrcompochar(map)", e);
        }
        
        delete("BudgetApply.deleteDgrcompochar", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateDgrcompoCngData(Map map) throws Exception{
    	
    	update("BudgetApply.updateDgrcompoCngData", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateCngHistoryData(Map map) throws Exception{
    	
    	update("BudgetApply.updateCngHistoryData", map);
    }
    
    
}

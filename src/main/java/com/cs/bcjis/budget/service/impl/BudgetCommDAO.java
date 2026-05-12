package com.cs.bcjis.budget.service.impl;

import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;
import com.cs.bcjis.comm.util.BcjisCommUtil;

@Repository("budgetCommDAO")
public class BudgetCommDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompo(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompoH", map);
        }catch(Exception e){
            logger.error("deleteDgrcompo(map)", e);
        }
        
        delete("BudgetComm.deleteDgrcompo", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompofrsc(Map map) throws Exception{
        insert("BudgetComm.insertDgrcompofrsc", map);
        
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
        
        update("BudgetComm.updateDgrcompofrsc", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompofrsc(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompofrscH", map);
        }catch(Exception e){
            logger.error("deleteDgrcompofrsc(map)", e);
        }
        
        delete("BudgetComm.deleteDgrcompofrsc", map);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompochar(Map map) throws Exception{
        insert("BudgetComm.insertDgrcompochar", map);

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
        
        update("BudgetComm.updateDgrcompochar", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteDgrcompochar(Map map) throws Exception{
        try{
            map.put("hisFg", "030");
            insert("BcjisHisComm.insertTbDgrcompocharH", map);
        }catch(Exception e){
            logger.error("deleteDgrcompochar(map)", e);
        }
        
        delete("BudgetComm.deleteDgrcompochar", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectUpDgrcompoInfo(Map map) throws Exception{
        return (Map)selectByPk("BudgetComm.selectUpDgrcompoInfo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompoAmtAll(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompoAmtAll", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompofrscAmtAll(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompofrscAmtAll", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompocharAmtAll(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompocharAmtAll", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompoAmt(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompoAmt", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompofrscAmt(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompofrscAmt", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompocharAmt(Map map) throws Exception{
        update("BudgetComm.updateUpDgrcompocharAmt", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyDgrcompoPreBgtAmt(Map map) throws Exception{
        if(isCompoBgt(map) == true){
            update("BudgetComm.copyDgrcompoPreBgtAmtAdd", map);
        }else{
            update("BudgetComm.copyDgrcompoPreBgtAmt", map);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void copyDgrcompofrscPreFrscAmt(Map map) throws Exception{
        if(isCompoBgt(map) == true){
            update("BudgetComm.copyDgrcompofrscPreFrscAmtAdd", map);
        }else{
            update("BudgetComm.copyDgrcompofrscPreFrscAmt", map);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void copyDgrcompocharPreCharAmt(Map map) throws Exception{
        if(isCompoBgt(map) == true){
            update("BudgetComm.copyDgrcompocharPreCharAmtAdd", map);
        }else{
            update("BudgetComm.copyDgrcompocharPreCharAmt", map);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void saveUpDgrcompoInfoAll(Map param) throws Exception{
        Map upParam = selectUpDgrcompoInfo(param);
        
        if(upParam == null){
            return;
        }
        
        if(BcjisCommUtil.isNullString(upParam.get("teBgtCompoId")) == true || "00000000000".equals(upParam.get("teBgtCompoId")) == true){
            return;
        }
        
        updateUpDgrcompoAmt(upParam);
        updateUpDgrcompofrscAmt(upParam);
        updateUpDgrcompocharAmt(upParam);
        
        saveUpDgrcompoInfoAll(upParam);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyPreInfo(Map param) throws Exception{
        copyDgrcompocharPreCharAmt(param);
        copyDgrcompofrscPreFrscAmt(param);
        copyDgrcompoPreBgtAmt(param);
        
        saveUpDgrcompoInfoAll(param);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUpDgrcompoInfoAll(Map param) throws Exception{
        
        updateUpDgrcompoAmtAll(param);
        updateUpDgrcompofrscAmtAll(param);
        updateUpDgrcompocharAmtAll(param);
    }

    @SuppressWarnings("rawtypes")
    public void updateDgrcompoDiffAmt(Map map) throws Exception {
        update("BudgetComm.updateDgrcompoDiffAmt", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateDgrcompofrscDiffAmt(Map map) throws Exception {
        update("BudgetComm.updateDgrcompofrscDiffAmt", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateDgrcompocharDiffAmt(Map map) throws Exception {
        update("BudgetComm.updateDgrcompocharDiffAmt", map);
    }
    
    public void updateDiffAmtByReflegFg(JSONObject jsonParam) throws Exception {
        if("Y".equals(jsonParam.get("reflegFgYn")) == false) {
            return;
        }
        
        if("020".equals(jsonParam.get("reflectFg")) == false) {
            return;
        }
        
        updateDgrcompoDiffAmt(jsonParam);
        updateDgrcompofrscDiffAmt(jsonParam);
        updateDgrcompocharDiffAmt(jsonParam);
        
        saveUpDgrcompoInfoAll(jsonParam);
    }
    
    @SuppressWarnings("rawtypes")
    public boolean isCompoBgt(Map map){
        String srcFisYear = String.valueOf(map.get("srcFisYear"));
        String srcBgtDgr = String.valueOf(map.get("srcBgtDgr"));
        String fisYear = String.valueOf(map.get("fisYear"));
        String bgtDgr = String.valueOf(map.get("bgtDgr"));
        
        //동일 회계연도에 본예산을 추경에 적용할 경우
        if(fisYear.equals(srcFisYear) == true && "1".equals(srcBgtDgr) == true && "1".equals(bgtDgr) == false){
            return true;
        }
        
        return false;
    }
}

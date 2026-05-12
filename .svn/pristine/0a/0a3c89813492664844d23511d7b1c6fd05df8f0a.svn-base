package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;

@Service("dialogDgrcompoModifyService")
public class DialogDgrcompoModifyServiceImpl implements DialogDgrcompoModifyService {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name = "dialogDgrcompoModifyDAO")
    private DialogDgrcompoModifyDAO dialogDgrcompoModifyDAO;

    @SuppressWarnings("rawtypes")
    public Map selectDgrcompo(Map map) throws Exception {
        return dialogDgrcompoModifyDAO.selectDgrcompo(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscList(Map map) throws Exception {
        return dialogDgrcompoModifyDAO.selectDgrcompofrscList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharList(Map map) throws Exception {
        return dialogDgrcompoModifyDAO.selectDgrcompocharList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveDgrcompos(JSONObject jsonParam) throws Exception {
        dialogDgrcompoModifyDAO.updateDgrcompo(jsonParam);
        
        if("true".equals(jsonParam.get("isLeaf")) == false){
            return;
        }
        
        JSONObject tempParam = null;
        List frscs = jsonParam.getJSONArray("frscs");
        
        for(int i = 0; i < frscs.size(); i++){
            tempParam = (JSONObject) frscs.get(i);
            
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));
            
            budgetCommDAO.updateDgrcompofrsc(tempParam);
        }
        
        tempParam = null;
        List chars = jsonParam.getJSONArray("chars");
        for(int i = 0; i < chars.size(); i++){
            tempParam = (JSONObject) chars.get(i);
            
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));
            
            budgetCommDAO.updateDgrcompochar(tempParam);
        }
        
        budgetCommDAO.saveUpDgrcompoInfoAll(jsonParam);
    }

    @SuppressWarnings("rawtypes")
    public Map selectModifyDgrcompo(Map map) throws Exception {
        return dialogDgrcompoModifyDAO.selectModifyDgrcompo(map);
    }
}

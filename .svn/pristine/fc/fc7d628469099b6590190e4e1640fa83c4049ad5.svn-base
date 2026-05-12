package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.dialog.service.DialogDgrcompoRegiService;

@Service("dialogDgrcompoRegiService")
public class DialogDgrcompoRegiServiceImpl implements DialogDgrcompoRegiService {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name = "dialogDgrcompoRegiDAO")
    private DialogDgrcompoRegiDAO dialogDgrcompoRegiDAO;

    @SuppressWarnings("rawtypes")
    public Map selectDgrcompoRegiUpDgrcompoInfo(Map map) throws Exception {
        return dialogDgrcompoRegiDAO.selectDgrcompoRegiUpDgrcompoInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscList(Map map) throws Exception {
        return dialogDgrcompoRegiDAO.selectDgrcompofrscList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharList(Map map) throws Exception {
        return dialogDgrcompoRegiDAO.selectDgrcompocharList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveDgrcompos(JSONObject jsonParam) throws Exception {
        Map param = dialogDgrcompoRegiDAO.selectDgrcompoRegiInfo(jsonParam);
        jsonParam.put("teBgtCompoId", param.get("teBgtCompoId"));
        jsonParam.put("teBgtCompoSeq", param.get("teBgtCompoSeq"));
        jsonParam.put("teMngMokNm", param.get("teMngMokNm"));
        jsonParam.put("sortSeq", param.get("sortSeq"));
        
        dialogDgrcompoRegiDAO.insertDgrcompo(jsonParam);
        
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
            
            budgetCommDAO.insertDgrcompofrsc(tempParam);
        }
        
        List chars = jsonParam.getJSONArray("chars");
        for(int i = 0; i < chars.size(); i++){
            tempParam = (JSONObject) chars.get(i);
            
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));
            
            budgetCommDAO.insertDgrcompochar(tempParam);
        }
        
        budgetCommDAO.saveUpDgrcompoInfoAll(jsonParam);
    }
}

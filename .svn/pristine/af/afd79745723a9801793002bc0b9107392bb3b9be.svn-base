package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogTeMngVeriService;

@Service("dialogTeMngVeriService")
public class DialogTeMngVeriServiceImpl implements DialogTeMngVeriService {
    @Resource(name = "dialogTeMngVeriDAO")
    private DialogTeMngVeriDAO dialogTeMngVeriDAO;

    @SuppressWarnings("rawtypes")
    public List selectTeMngVeriList(Map map) throws Exception {
        return dialogTeMngVeriDAO.selectTeMngVeriList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertDialogTeMngVeri(JSONObject jsonParam) throws Exception {

        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;
        
        String fisYear = jsonParam.getString("fisYear");
        String bgtDgr = jsonParam.getString("bgtDgr");
        String teMngMokCd = jsonParam.getString("teMngMokCd");
        String veriFg = jsonParam.getString("veriFg");
        String userId = jsonParam.getString("userId");
        
        dialogTeMngVeriDAO.deleteTeMngVeri(jsonParam);
        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);

            tempParam.put("fisYear", fisYear);
            tempParam.put("bgtDgr", bgtDgr);
            tempParam.put("teMngMokCd", teMngMokCd);
            tempParam.put("veriFg", veriFg);
            tempParam.put("userId", userId);
         
            dialogTeMngVeriDAO.insertTeMngVeri(tempParam);
        }
    }
    
}

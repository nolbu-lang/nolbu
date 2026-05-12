package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogDgroffice020SortService;

@Service("dialogDgroffice020SortService")
public class DialogDgroffice020SortServiceImpl implements DialogDgroffice020SortService {
    @Resource(name = "dialogDgroffice020SortDAO")
    private DialogDgroffice020SortDAO dialogDgroffice020SortDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgroffice020List(Map map) throws Exception {
        return dialogDgroffice020SortDAO.selectDgroffice020List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void saveOfficeRank020s(JSONObject jsonParam) throws Exception {
        JSONObject tempParam = null;
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        for(int i = 0; i < saveDatas.size(); i++){
            tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            
            dialogDgroffice020SortDAO.updateOfficeRank020(tempParam);
        }
        
    }
}

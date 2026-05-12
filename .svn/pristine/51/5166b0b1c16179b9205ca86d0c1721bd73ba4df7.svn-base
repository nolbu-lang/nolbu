package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogPledgeSortService;

@Service("dialogPledgeSortService")
public class DialogPledgeSortServiceImpl implements DialogPledgeSortService {
    @Resource(name = "dialogPledgeSortDAO")
    private DialogPledgeSortDAO dialogPledgeSortDAO;

    @SuppressWarnings("rawtypes")
    public List selectPledgeSortPledgeBizList(Map map) throws Exception {
        return dialogPledgeSortDAO.selectPledgeSortPledgeBizList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void savePledgeBizSorts(JSONObject jsonParam) throws Exception {
        JSONObject tempParam = null;
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        for(int i = 0; i < saveDatas.size(); i++){
            tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            
            dialogPledgeSortDAO.updatePledgeBizSort(tempParam);
        }
        
    }
}

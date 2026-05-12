package com.cs.bcjis.pledge.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.pledge.service.PledgeSelectService;

@Service("pledgeSelectService")
public class PledgeSelectServiceImpl implements PledgeSelectService {
    
    @Resource(name = "pledgeSelectDAO")
    private PledgeSelectDAO pledgeSelectDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception {
        return pledgeSelectDAO.selectDgrCompoList(map);
    }

    @SuppressWarnings("rawtypes")
    public void savePledge(JSONObject jsonParam) throws Exception {
        
        String pledgeBizId = String.valueOf(jsonParam.get("pledgeBizId"));

        List savePledgeDatas = jsonParam.getJSONArray("savePledgeDatas");
        JSONObject tempParam = null;

        Map existDataMap = getExistDataMap(jsonParam);
        Map tempKeyMap = null;
        String pledgeKeyString = "";
        for (int i = 0; i < savePledgeDatas.size(); i++) {
            tempParam = (JSONObject) savePledgeDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("pledgeBizId", pledgeBizId);

            pledgeKeyString = getPledgeKeyString(tempParam);

            tempKeyMap = (Map) existDataMap.remove(pledgeKeyString);
            if (tempKeyMap == null) {
                pledgeSelectDAO.insertPledge(tempParam);
            }
        }

        deletePledge(existDataMap);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMap(Map map) throws Exception {
        Map existDataMap = new HashMap();

        List existDatas = pledgeSelectDAO.selectPledgeKeyList(map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map tempMap = null;
        while (!existDatas.isEmpty()) {
            tempMap = (Map) existDatas.remove(0);
            existDataMap.put(getPledgeKeyString(tempMap), tempMap);
        }

        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public void deletePledge(Map map) throws Exception {
        if (map == null || map.keySet() == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        if (iterator == null) {
            return;
        }

        String key = "";
        Map tempMap = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            tempMap = (Map) map.get(key);
            pledgeSelectDAO.deletePledge(tempMap);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public String getPledgeKeyString(Map map){

        return String.valueOf(map.get("pledgeBizId")) + "_"
                + String.valueOf(map.get("fisYear")) + "_"
                + String.valueOf(map.get("bgtDgr")) + "_"
                + String.valueOf(map.get("teBgtCompoId"));
    }
}

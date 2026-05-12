package com.cs.bcjis.budget.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetSheetSelectService;

@Service("budgetSheetSelectService")
public class BudgetSheetSelectServiceImpl implements BudgetSheetSelectService {
    
    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception {
        return budgetSheetSelectDAO.selectDgrCompoList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveSheet(JSONObject jsonParam) throws Exception {
        
        String sheetCd = String.valueOf(jsonParam.get("sheetCd"));
        String sheetDetlCd = String.valueOf(jsonParam.get("sheetDetlCd"));

        List saveSheetDatas = jsonParam.getJSONArray("saveSheetDatas");
        JSONObject tempParam = null;

        Map existDataMap = getExistDataMap(jsonParam);
        Map tempKeyMap = null;
        String sheetKeyString = "";
        for (int i = 0; i < saveSheetDatas.size(); i++) {
            tempParam = (JSONObject) saveSheetDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("sheetCd", sheetCd);
            tempParam.put("sheetDetlCd", sheetDetlCd);

            sheetKeyString = getSheetKeyString(tempParam);

            tempKeyMap = (Map) existDataMap.remove(sheetKeyString);
            if (tempKeyMap == null) {
                budgetSheetSelectDAO.insertSheet(tempParam);
            }
        }

        deleteSheet(existDataMap);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMap(Map map) throws Exception {
        Map existDataMap = new HashMap();

        List existDatas = budgetSheetSelectDAO.selectSheetKeyList(map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map tempMap = null;
        while (!existDatas.isEmpty()) {
            tempMap = (Map) existDatas.remove(0);
            existDataMap.put(getSheetKeyString(tempMap), tempMap);
        }

        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public void deleteSheet(Map map) throws Exception {
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
            budgetSheetSelectDAO.deleteSheet(tempMap);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public String getSheetKeyString(Map map){

        return String.valueOf(map.get("sheetCd")) + "_"
                + String.valueOf(map.get("sheetDetlCd")) + "_"
                + String.valueOf(map.get("fisYear")) + "_"
                + String.valueOf(map.get("bgtDgr")) + "_"
                + String.valueOf(map.get("teBgtCompoId"));
    }
}

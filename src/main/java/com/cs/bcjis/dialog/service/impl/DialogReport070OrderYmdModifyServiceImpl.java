package com.cs.bcjis.dialog.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogReport070OrderYmdModifyService;

@Service("dialogReport070OrderYmdModifyService")
public class DialogReport070OrderYmdModifyServiceImpl implements DialogReport070OrderYmdModifyService {
    @Resource(name = "dialogReport070OrderYmdModifyDAO")
    private DialogReport070OrderYmdModifyDAO dialogReport070OrderYmdModifyDAO;

    @SuppressWarnings("rawtypes")
    public List selectDialogReport070OList(Map map) throws Exception {
        return dialogReport070OrderYmdModifyDAO.selectDialogReport070OList(map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void saveDialogReport070O(Map map) throws Exception {
        if (map.get("report070Os") == null) {
            return;
        }

        JSONArray report070Os = (JSONArray) map.get("report070Os");
        String userId = String.valueOf(map.get("userId"));

        Map existDataMap = getExistReport070ODataMap(map);

        Map temp = null;
        Map tempKey = null;
        for (int i = 0; i < report070Os.size(); i++) {
            temp = (Map) report070Os.get(i);

            tempKey = (Map) existDataMap.remove(getReport070OKeyString(temp));
            temp.put("userId", userId);

            if (tempKey == null) {
                dialogReport070OrderYmdModifyDAO.insertDialogReport070O(temp);
            } else {
                dialogReport070OrderYmdModifyDAO.updateDialogReport070O(temp);
            }

        }

        deleteBlncexResultReport070Os(existDataMap, userId);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteBlncexResultReport070Os(Map map, String userId) throws Exception {
        if (map == null || map.keySet() == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        if (iterator == null) {
            return;
        }

        String key = "";
        Map temp = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            temp = (Map) map.get(key);
            temp.put("userId", userId);
            dialogReport070OrderYmdModifyDAO.deleteDialogReport070O(temp);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistReport070ODataMap(Map map) throws Exception {
        Map existDataMap = new HashMap();

        List existDatas = dialogReport070OrderYmdModifyDAO.selectDialogReport070OKeyList(map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map temp = null;
        while (!existDatas.isEmpty()) {
            temp = (Map) existDatas.remove(0);
            existDataMap.put(getReport070OKeyString(temp), temp);
        }

        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public String getReport070OKeyString(Map data) {
        if (data == null) {
            return "";
        }

        return data.get("fisYear") + "_" + data.get("bgtDgr") + "_" + data.get("orderYmdSeq");
    }
}

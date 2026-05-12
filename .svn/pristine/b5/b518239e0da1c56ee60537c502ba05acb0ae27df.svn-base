package com.cs.bcjis.budget.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetSelectService;
import com.cs.bcjis.budget.service.BudgetSheetSelectService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetSelectService")
public class BudgetSelectServiceImpl implements BudgetSelectService {
    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetSelectDAO")
    private BudgetSelectDAO budgetSelectDAO;

    @Resource(name = "budgetSheetSelectService")
    private BudgetSheetSelectService budgetSheetSelectService;
    
    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception {
        String reportCd = String.valueOf(map.get("reportCd"));

        return budgetSelectDAO.selectDgrCompoList(reportCd, map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport(JSONObject jsonParam) throws Exception {

        String reportCd = String.valueOf(jsonParam.get("reportCd"));
        String reportDetlCd = String.valueOf(jsonParam.get("reportDetlCd"));
        String orderYmdSeq = String.valueOf(jsonParam.get("orderYmdSeq"));

        List saveReportDatas = jsonParam.getJSONArray("saveReportDatas");
        JSONObject tempParam = null;

        Map existDataMap = getExistDataMap(reportCd, jsonParam);

        Map tempKeyMap = null;
        String reportKeyString = "";
        for (int i = 0; i < saveReportDatas.size(); i++) {
            tempParam = (JSONObject) saveReportDatas.get(i);

            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("reportCd", reportCd);
            tempParam.put("reportDetlCd", reportDetlCd);
            tempParam.put("orderYmdSeq", orderYmdSeq);

            reportKeyString = getReportKeyString(reportCd, tempParam);

            tempKeyMap = (Map) existDataMap.remove(reportKeyString);
            if (tempKeyMap == null) {
                reportCommDAO.insertReport(reportCd, tempParam);
            }
        }

        deleteReport(reportCd, existDataMap);

        if ("070".equals(reportCd) == true) {
            insertReport070s(reportCd, reportDetlCd);
        }

        if ("021".equals(reportDetlCd) == true || "022".equals(reportDetlCd) == true || "023".equals(reportDetlCd) == true) {
            reportCd = "030";
            String sheetCd = "TF0";
            String sheetDetlCd = "TF1";
            
            List saveReportDatas030 = jsonParam.getJSONArray("saveReportDatas030");
            jsonParam.put("reportDetlCd", "031");
            Map existDataMap031 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "032");
            Map existDataMap032 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "033");
            Map existDataMap033 = getExistDataMap(reportCd, jsonParam);

            jsonParam.put("sheetCd", sheetCd);
            jsonParam.put("sheetDetlCd", sheetDetlCd);
            Map existDataMapTF1 = budgetSheetSelectService.getExistDataMap(jsonParam);
            
            for (int i = 0; i < saveReportDatas030.size(); i++) {
                tempParam = (JSONObject) saveReportDatas030.get(i);

                tempParam.put("userId", jsonParam.get("userId"));
                tempParam.put("reportCd", "030");
                tempParam.put("sheetCd", sheetCd);
                if("Y".equals(tempParam.get("checkYn031Yn")) == true){
                    tempParam.put("reportDetlCd", "031");
                    
                    if("Y".equals(tempParam.get("checkYn031")) == true){
                        tempKeyMap = (Map) existDataMap031.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn032Yn")) == true){
                    tempParam.put("reportDetlCd", "032");
                    if("Y".equals(tempParam.get("checkYn032")) == true){
                        tempKeyMap = (Map) existDataMap032.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn033Yn")) == true){
                    tempParam.put("reportDetlCd", "033");
                    if("Y".equals(tempParam.get("checkYn033")) == true){
                        tempKeyMap = (Map) existDataMap033.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYnTf1Yn")) == true){
                    tempParam.put("sheetDetlCd", sheetDetlCd);
                    if("Y".equals(tempParam.get("checkYnTf1")) == true){
                        tempKeyMap = (Map) existDataMapTF1.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            budgetSheetSelectDAO.insertSheet(tempParam);
                        }
                    }else{
                        budgetSheetSelectDAO.deleteSheet(tempParam);
                    }
                }
            }
        }
    }
    
    public void insertReport070s(String reportCd, String reportDetlCd) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("reportCd", reportCd);
        map.put("reportDetlCd", reportDetlCd);
        
        reportCommDAO.deleteReport(map);
        reportCommDAO.insertReport070s(map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMap(String reportCd, Map map) throws Exception {
        Map existDataMap = new HashMap();

        List existDatas = budgetSelectDAO.selectReportKeyList(reportCd, map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map tempMap = null;
        while (!existDatas.isEmpty()) {
            tempMap = (Map) existDatas.remove(0);
            existDataMap.put(getReportKeyString(reportCd, tempMap), tempMap);
        }

        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public void deleteReport(String reportCd, Map map) throws Exception {
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
            reportCommDAO.deleteReport(reportCd, tempMap);
        }
    }

    @SuppressWarnings("rawtypes")
    public String getReportKeyString(String reportCd, Map map) {
        if ("070".equals(reportCd) == true) {
            return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("orderYmdSeq")) + "_" + String.valueOf(map.get("teBgtCompoId"));
        }

        return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("teBgtCompoId"));
    }

}

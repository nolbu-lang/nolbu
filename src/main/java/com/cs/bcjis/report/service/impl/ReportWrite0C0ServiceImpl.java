package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite0C0Service;

@Service("reportWrite0C0Service")
public class ReportWrite0C0ServiceImpl implements ReportWrite0C0Service {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "reportWrite0C0DAO")
    private ReportWrite0C0DAO reportWrite0C0DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport0C0List(Map map) throws Exception {
        return reportWrite0C0DAO.selectReport0C0List(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport0C0(JSONObject jsonParam) throws Exception {
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            reportWrite0C0DAO.updateReport0C0(tempParam);

            if ("Y".equals(tempParam.get("srchValYn")) == true) {
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void insertReport0C0(Map map) throws Exception {  
        reportWrite0C0DAO.insertReport0C0(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertReport(Map map) throws Exception {  
        reportWrite0C0DAO.insertReport(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport0C0HwpList(Map map) throws Exception {
        return reportWrite0C0DAO.selectReport0C0HwpList(map);
    }
}

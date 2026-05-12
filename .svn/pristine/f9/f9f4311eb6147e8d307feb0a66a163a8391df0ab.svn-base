package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite0B0Service;

@Service("reportWrite0B0Service")
public class ReportWrite0B0ServiceImpl implements ReportWrite0B0Service {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "reportWrite0B0DAO")
    private ReportWrite0B0DAO reportWrite0B0DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport0B0List(Map map) throws Exception {
        return reportWrite0B0DAO.selectReport0B0List(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport0B0ExcelList(Map map) throws Exception {
        return reportWrite0B0DAO.selectReport0B0ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite0B0DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void insertReport0B0(Map map) throws Exception {
        reportWrite0B0DAO.insertReport0B0(map);
    }

    @SuppressWarnings("rawtypes")
    public void insertReport(Map map) throws Exception {
        reportWrite0B0DAO.insertReport(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport0B0(JSONObject jsonParam) throws Exception {
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            reportWrite0B0DAO.updateReport0B0(tempParam);

            if ("Y".equals(tempParam.get("srchValYn")) == true) {
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void deleteReport0B0(JSONObject jsonParam) throws Exception {
        List deleteReportDatas = jsonParam.getJSONArray("deleteReportDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < deleteReportDatas.size(); i++) {
            tempParam = (JSONObject) deleteReportDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            reportCommDAO.deleteReport("0B0", tempParam);
        }
    }
}

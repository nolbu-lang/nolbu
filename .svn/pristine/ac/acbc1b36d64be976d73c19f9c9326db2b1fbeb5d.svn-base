package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite020Service;

@Service("reportWrite020Service")
public class ReportWrite020ServiceImpl implements ReportWrite020Service {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "reportWrite020DAO")
    private ReportWrite020DAO reportWrite020DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport020List(Map map) throws Exception {
        return reportWrite020DAO.selectReport020List(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectReport020PageListCnt(Map map) throws Exception {
        return reportWrite020DAO.selectReport020PageListCnt(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020PageList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020PageList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020TotList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020TotList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelListTot(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelListTot(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList2(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelList2(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport027ExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReport027ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020RptTotList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020RptTotList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020OfficeList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeSiList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020OfficeSiList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportOfficeSi022List(Map map) throws Exception {
        return reportWrite020DAO.selectReportOfficeSi022List(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelListTot(Map map) throws Exception {
        return reportWrite020DAO.selectReportBizExcelListTot(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportGugunTotExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportGugunTotExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportGugunExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportGugunExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport020(JSONObject jsonParam) throws Exception {
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            String updateReportFlag = (String) tempParam.get("updateReportFlag");
            
            //보고항목, 사전절차 수정시 업데이트
            if(updateReportFlag != null && "Y".equals(updateReportFlag)){
            	reportCommDAO.updateReport(tempParam);
            }
            
            reportWrite020DAO.updateReport020(tempParam);

            reportCommDAO.updateReport020D(tempParam);

            if ("Y".equals(tempParam.get("srchValYn")) == true) {
                reportCommDAO.updateSrchValChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("reportSortSeqYn")) == true) {
                reportCommDAO.updateReportSortSeqChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("mayorReportChangeYn")) == true) {
                reportCommDAO.updateMayorReportYnChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true) {
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

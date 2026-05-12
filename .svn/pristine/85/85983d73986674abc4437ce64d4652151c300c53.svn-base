package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite070Service;


@Service("reportWrite070Service")
public class ReportWrite070ServiceImpl  implements ReportWrite070Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite070DAO")
    private ReportWrite070DAO reportWrite070DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport070List(Map map) throws Exception {
        return reportWrite070DAO.selectReport070List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport070ExcelList(Map map) throws Exception {
        return reportWrite070DAO.selectReport070ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite070DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport070(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            reportWrite070DAO.updateReport070(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }

            if("Y".equals(tempParam.get("reportSortSeqYn")) == true){
                reportCommDAO.updateReportSortSeqChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

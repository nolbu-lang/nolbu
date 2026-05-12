package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite080Service;


@Service("reportWrite080Service")
public class ReportWrite080ServiceImpl  implements ReportWrite080Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite080DAO")
    private ReportWrite080DAO reportWrite080DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport080List(Map map) throws Exception {
        return reportWrite080DAO.selectReport080List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport080ExcelList(Map map) throws Exception {
        return reportWrite080DAO.selectReport080ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite080DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport080(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            reportWrite080DAO.updateReport080(tempParam);
            
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

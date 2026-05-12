package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite050Service;


@Service("reportWrite050Service")
public class ReportWrite050ServiceImpl  implements ReportWrite050Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite050DAO")
    private ReportWrite050DAO reportWrite050DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport050List(Map map) throws Exception {
        return reportWrite050DAO.selectReport050List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport050ExcelList(Map map) throws Exception {
        return reportWrite050DAO.selectReport050ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport050(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            String updateReportFlag = (String) tempParam.get("updateReportFlag");
            
            //보고항목, 사전절차 수정시 업데이트
            if(updateReportFlag != null && "Y".equals(updateReportFlag)){
            	reportCommDAO.updateReport(tempParam);
            }
            
            reportWrite050DAO.updateReport050(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

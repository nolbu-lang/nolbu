package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite055Service;


@Service("reportWrite055Service")
public class ReportWrite055ServiceImpl  implements ReportWrite055Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite055DAO")
    private ReportWrite055DAO reportWrite055DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport055List(Map map) throws Exception {
        return reportWrite055DAO.selectReport055List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport055ExcelList(Map map) throws Exception {
        return reportWrite055DAO.selectReport055ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport055(JSONObject jsonParam) throws Exception {        
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

            reportWrite055DAO.updateReport055(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

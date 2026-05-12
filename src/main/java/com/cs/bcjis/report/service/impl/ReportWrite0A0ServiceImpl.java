package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite0A0Service;


@Service("reportWrite0A0Service")
public class ReportWrite0A0ServiceImpl  implements ReportWrite0A0Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite0A0DAO")
    private ReportWrite0A0DAO reportWrite0A0DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport0A0List(Map map) throws Exception {
        return reportWrite0A0DAO.selectReport0A0List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport0A0ExcelList(Map map) throws Exception {
        return reportWrite0A0DAO.selectReport0A0ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite0A0DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport0A0(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            reportWrite0A0DAO.updateReport0A0(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("mayorReportChangeYn")) == true) {
                reportCommDAO.updateMayorReportYnChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

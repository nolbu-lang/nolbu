package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite0D0Service;


@Service("reportWrite0D0Service")
public class ReportWrite0D0ServiceImpl  implements ReportWrite0D0Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite0D0DAO")
    private ReportWrite0D0DAO reportWrite0D0DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport0D0List(Map map) throws Exception {
        return reportWrite0D0DAO.selectReport0D0List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport0D0ExcelList(Map map) throws Exception {
        return reportWrite0D0DAO.selectReport0D0ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite0D0DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport0D0(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            reportWrite0D0DAO.updateReport0D0(tempParam);
            
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

package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite040Service;


@Service("reportWrite040Service")
public class ReportWrite040ServiceImpl  implements ReportWrite040Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite040DAO")
    private ReportWrite040DAO reportWrite040DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport040List(Map map) throws Exception {
        return reportWrite040DAO.selectReport040List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport040ExcelList(Map map) throws Exception {
        return reportWrite040DAO.selectReport040ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport040(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            reportWrite040DAO.updateReport040(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.report.service.ReportWrite060Service;


@Service("reportWrite060Service")
public class ReportWrite060ServiceImpl  implements ReportWrite060Service {
    @Resource(name="budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite060DAO")
    private ReportWrite060DAO reportWrite060DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport060List(Map map) throws Exception {
        return reportWrite060DAO.selectReport060List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport060ExcelList(Map map) throws Exception {
        return reportWrite060DAO.selectReport060ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport060(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            reportWrite060DAO.updateReport060(tempParam);
            
            if("Y".equals(tempParam.get("srchValYn")) == true){
                reportCommDAO.updateSrchValChildReport(tempParam);
            }
            
            if("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true){
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
        }
    }
}

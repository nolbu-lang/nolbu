package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.report.service.ReportWrite030Service;
import com.cs.bcjis.report.service.ReportWrite030Service_2;


@Service("reportWrite030Service_2")
public class ReportWrite030ServiceImpl_2  implements ReportWrite030Service_2 {
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite030DAO_2")
    private ReportWrite030DAO_2 reportWrite030DAO_2;

    @SuppressWarnings("rawtypes")
    public List selectReport030List(Map map) throws Exception {
        return reportWrite030DAO_2.selectReport030List(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport030ExcelList(Map map) throws Exception {
        return reportWrite030DAO_2.selectReport030ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport034ExcelList(Map map) throws Exception {
        return reportWrite030DAO_2.selectReport034ExcelList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport030Sheet001List(Map map) throws Exception {
        return reportWrite030DAO_2.selectReport030Sheet001List(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport030(JSONObject jsonParam) throws Exception {        
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            reportWrite030DAO_2.updateReport030(tempParam);
        }
    }
}

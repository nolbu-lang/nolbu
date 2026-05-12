package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.report.service.ReportWrite001Service;


@Service("reportWrite001Service")
public class ReportWrite001ServiceImpl  implements ReportWrite001Service {
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite001DAO")
    private ReportWrite001DAO reportWrite001DAO;
    
    @SuppressWarnings("rawtypes")
    public List selectReport001SheetList(Map map) throws Exception {
        return reportWrite001DAO.selectReport001SheetList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList(Map map) throws Exception {
        return reportWrite001DAO.selectReportTotSheetList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList2(Map map) throws Exception {
        return reportWrite001DAO.selectReportTotSheetList2(map);
    }
}

package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite001DAO")
public class ReportWrite001DAO extends BcjisCommAbstractDAO {
    @SuppressWarnings("rawtypes")
    public List selectReport001SheetList(Map map) throws Exception {
        return list("ReportWrite001.selectReport001SheetList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList(Map map) throws Exception {
        return list("ReportWrite001.selectReportTotSheetList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportTotSheetList2(Map map) throws Exception {
        return list("ReportWrite001.selectReportTotSheetList2", map);
    }
}

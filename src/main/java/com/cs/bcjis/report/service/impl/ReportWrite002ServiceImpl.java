package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.report.service.ReportWrite002Service;


@Service("reportWrite002Service")
public class ReportWrite002ServiceImpl  implements ReportWrite002Service {
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite002DAO")
    private ReportWrite002DAO reportWrite002DAO;
    
    @SuppressWarnings("rawtypes")
    public List selectReport002SheetList(Map map) throws Exception {
        return reportWrite002DAO.selectReport002SheetList(map);
    }
}

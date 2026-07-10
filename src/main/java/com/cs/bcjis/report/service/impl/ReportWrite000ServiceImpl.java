package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.report.service.ReportWrite000Service;


@Service("reportWrite000Service")
public class ReportWrite000ServiceImpl  implements ReportWrite000Service {
    @Resource(name="reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name="reportWrite000DAO")
    private ReportWrite000DAO reportWrite000DAO;
    
    @SuppressWarnings("rawtypes")
    public List selectReport000SheetList(Map map) throws Exception {
        return reportWrite000DAO.selectReport000SheetList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReport000SheetListNew(Map map) throws Exception {
    	
    	List list = reportWrite000DAO.selectReport000SheetListNew(map);
    	// 본예산 => 전년도 최종예산액 추가
    	if ("1".equals(map.get("bgtDgr"))) {
    		list.addAll(reportWrite000DAO.selectReport000SheetListNewPre(map));
    	}	
    	// 추경 1차 => 기정액 추가
    	//추경 코드가 3이 고정이 아니므로 else 로 변경
    	//else if ("3".equals(map.get("bgtDgr"))) {
    	else{
    		list.addAll(reportWrite000DAO.selectReport000SheetListNewPreDef(map));
    	}
    	
    	return list;
    }
}

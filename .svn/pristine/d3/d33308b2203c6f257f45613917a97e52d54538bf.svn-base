package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.DailyRollingFileAppender;
import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyReportService;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;

@Service("dialogDgrcompoModifyReportService")
public class DialogDgrcompoModifyReportServiceImpl implements DialogDgrcompoModifyReportService {

	@Resource(name = "dialogDgrcompoModifyReportDAO")
    private DialogDgrcompoModifyReportDAO dialogDgrcompoModifyReportDAO;

    @SuppressWarnings("rawtypes")
    public Map selectReportData(Map map) throws Exception {
    	Map rtnMap = null;
    	Map reportMap = dialogDgrcompoModifyReportDAO.selectReportData(map);
    	if(reportMap != null){
    		String reportCd = (String) reportMap.get("reportCd");
    		map.put("reportCd", reportCd);
        	map.put("reportDetlCd", reportMap.get("reportDetlCd"));
        	map.put("fisYear", reportMap.get("fisYear"));
        	map.put("bgtDgr", reportMap.get("bgtDgr"));
        	map.put("reportTableNm", "TB_REPORT" + reportCd);
        	if("010".equals(reportCd) || "020".equals(reportCd)){
        		rtnMap = dialogDgrcompoModifyReportDAO.selectReportDetlData(map);
        	}else{
        		rtnMap = dialogDgrcompoModifyReportDAO.selectReportDetlDataEtc(map);
        	}
    	}
    	
        return rtnMap;
    }
    
    @SuppressWarnings("rawtypes")
    public void updateReportData(JSONObject jsonParam) throws Exception {
    	String reportCd = (String) jsonParam.get("reportCd");

    	jsonParam.put("reportTableNm", "TB_REPORT" + reportCd);
    	
    	if("010".equals(reportCd) || "020".equals(reportCd)){
    		dialogDgrcompoModifyReportDAO.updateReportData(jsonParam);
    	}else{
    		dialogDgrcompoModifyReportDAO.updateReportDataEtc(jsonParam);
    	}
    }

}

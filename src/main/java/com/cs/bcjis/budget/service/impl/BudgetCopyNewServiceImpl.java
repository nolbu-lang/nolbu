package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetCopyNewService")
public class BudgetCopyNewServiceImpl implements BudgetCopyNewService {

	@Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
	
	@Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
	
    @Resource(name = "budgetCopyNewDAO")
    private BudgetCopyNewDAO budgetCopyNewDAO;

    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception {
    	
    	map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));
    	
        return budgetCopyNewDAO.selectCopyReportList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        reportCommDAO.copyReport(map);
        budgetCommDAO.copyPreInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectCopyNewMapList(Map map) throws Exception {

        map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));

        return budgetCopyNewDAO.selectCopyNewMapList(map);
    }

    /**
     * 매핑 목록(전년도 세세목 -> 올해 세세목)을 한 트랜잭션에서 일괄 적용한다.
     * 각 매핑은 기존 단건 적용 로직(copyReport + copyPreInfo)을 그대로 재사용하므로 결과는 동일하다.
     */
    @SuppressWarnings("rawtypes")
    public void copyReportBatch(List<Map> mappings) throws Exception {
        if (mappings == null) {
            return;
        }

        for (Map map : mappings) {
            copyReport(map);
        }
    }
}

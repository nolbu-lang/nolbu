package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.budget.service.BudgetCopyService;

@Service("budgetCopyService")
public class BudgetCopyServiceImpl implements BudgetCopyService {
    @Resource(name = "budgetCopyDAO")
    private BudgetCopyDAO budgetCopyDAO;

    @Resource(name = "budgetCopyNewService")
    private BudgetCopyNewService budgetCopyNewService;

    @SuppressWarnings("rawtypes")
    public List selectReportList(Map map) throws Exception {

        return budgetCopyDAO.selectCopyReportList(map);
    }

    /**
     * 기정예산 조서 적용 — 조서·집계 분류 상속 후 본문·기정액 복사.
     * (BudgetCopyNew 와 동일: 적용대상에 분류가 없어도 기정예산 성질을 상속)
     */
    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        budgetCopyNewService.copyReport(map);
    }

}

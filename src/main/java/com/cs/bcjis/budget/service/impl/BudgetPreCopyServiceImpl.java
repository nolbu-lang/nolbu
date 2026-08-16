package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.budget.service.BudgetPreCopyService;

@Service("budgetPreCopyService")
public class BudgetPreCopyServiceImpl implements BudgetPreCopyService {
    private static final Logger logger = Logger.getLogger(BudgetPreCopyServiceImpl.class);

    @Resource(name = "budgetPreCopyDAO")
    private BudgetPreCopyDAO budgetPreCopyDAO;

    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "budgetCopyNewService")
    private BudgetCopyNewService budgetCopyNewService;

    @SuppressWarnings("rawtypes")
    public List selectPreCopyList(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyList(map);
    }

    /**
     * 전년도 예산(기정액) 적용 + 기정예산의 조서·집계 분류 상속.
     * 상속된 분류는 「조서·집계 항목선택」에서 조회·수정적용 가능.
     */
    @SuppressWarnings("rawtypes")
    public void copyPreInfo(Map map) throws Exception {
        try {
            budgetCopyNewService.inheritReportNatureOnly(map);
        } catch (Exception e) {
            logger.error("전년도 예산적용 조서·집계 분류 상속 실패", e);
        }
        budgetCommDAO.copyPreInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectPreCopyPageListCnt(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyPageListCnt(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPreCopyPageList(Map map) throws Exception {
        return budgetPreCopyDAO.selectPreCopyPageList(map);
    }

}

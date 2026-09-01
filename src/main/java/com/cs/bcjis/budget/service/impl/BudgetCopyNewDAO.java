package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetCopyNewDAO")
public class BudgetCopyNewDAO extends BcjisCommAbstractDAO {

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyReportList(Map map) throws Exception {

        return list("BudgetCopyNew.selectCopyReportList", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyNewMapList(Map map) throws Exception {

        return list("BudgetCopyNew.selectCopyNewMapList", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyNewMapListAll(Map map) throws Exception {

        return list("BudgetCopyNew.selectCopyNewMapListAll", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public Map selectSrcReportNature(Map map) throws Exception {

        return (Map) selectByPk("BudgetCopyNew.selectSrcReportNature", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public int selectReportCnt(Map map) throws Exception {
        Integer cnt = (Integer) selectByPk("BudgetCopyNew.selectReportCnt", map);
        return cnt == null ? 0 : cnt.intValue();
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer selectTeBgtCompoSeq(Map map) throws Exception {
        return (Integer) selectByPk("BudgetCopyNew.selectTeBgtCompoSeq", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectSrcSheetList(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcSheetList", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectSrcReportAttrList(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcReportAttrList", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectExistReportKeys(Map map) throws Exception {
        return list("BudgetCopyNew.selectExistReportKeys", map);
    }
}

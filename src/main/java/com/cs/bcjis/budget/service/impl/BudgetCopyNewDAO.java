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
    public List selectSrcReportNatureBatch(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcReportNatureBatch", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectTgtReportNatureList(Map map) throws Exception {
        return list("BudgetCopyNew.selectTgtReportNatureList", map);
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
    public List selectSrcSheetListBatch(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcSheetListBatch", map);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectSrcReportAttrList(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcReportAttrList", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectSrcReportAttrListBatch(Map map) throws Exception {
        return list("BudgetCopyNew.selectSrcReportAttrListBatch", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String selectReportMstrByReportCd(String reportCd) throws Exception {
        Map p = new java.util.HashMap();
        p.put("reportCd", reportCd);
        return (String) selectByPk("BudgetCopyNew.selectReportMstrByReportCd", p);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String selectReportCdByReportDetlCd(String reportDetlCd) throws Exception {
        Map p = new java.util.HashMap();
        p.put("reportDetlCd", reportDetlCd);
        return (String) selectByPk("BudgetCopyNew.selectReportCdByReportDetlCd", p);
    }

	@SuppressWarnings("rawtypes")
    public void insertReportNature(Map map) throws Exception {
        insert("BudgetCopyNew.insertReportNature", map);
    }
}

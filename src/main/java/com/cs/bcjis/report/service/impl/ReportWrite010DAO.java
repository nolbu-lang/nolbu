package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite010DAO")
public class ReportWrite010DAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectReport010List(Map map) throws Exception {
        return list("ReportWrite010.selectReport010List", map);
    }

    @SuppressWarnings("rawtypes")
    public int selectReport010PageListCnt(Map map) throws Exception {
        return (Integer) getSqlMapClientTemplate().queryForObject("ReportWrite010.selectReport010PageListCnt", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport010PageList(Map map) throws Exception {
        return list("ReportWrite010.selectReport010PageList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport010TotList(Map map) throws Exception {
        return list("ReportWrite010.selectReport010TotList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport010ExcelList(Map map) throws Exception {
        return list("ReportWrite010.selectReport010ExcelList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport010SheetList(Map map) throws Exception {
        return list("ReportWrite010.selectReport010SheetList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport012ExcelList(Map map) throws Exception {
        return list("ReportWrite010.selectReport012ExcelList", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport010(Map map) throws Exception {
        try {
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbReport010H", map);
        } catch (Exception e) {
            logger.error("updateDgrcompoSort(map)", e);
        }

        update("ReportWrite010.updateReport010", map);
    }
}

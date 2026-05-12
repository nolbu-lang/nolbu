package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite0C0DAO")
public class ReportWrite0C0DAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectReport0C0List(Map map) throws Exception {
        return list("ReportWrite0C0.selectReport0C0List", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReport0C0(Map map) throws Exception {
        try {
            map.put("hisFg", "0C0");
            insert("BcjisHisComm.insertTbReport0C0H", map);
        } catch (Exception e) {
            logger.error("updateDgrcompoSort(map)", e);
        }

        update("ReportWrite0C0.updateReport0C0", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertReport0C0(Map map) throws Exception {
        insert("ReportWrite0C0.insertReport0C0", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertReport(Map map) throws Exception {
        insert("ReportWrite0C0.insertReport", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport0C0HwpList(Map map) throws Exception {
        return list("ReportWrite0C0.selectReport0C0HwpList", map);
    }
}

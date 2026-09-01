package com.cs.bcjis.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("reportWrite0F0DAO")
public class ReportWrite0F0DAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport0F0List(Map map) throws Exception{
        return list("ReportWrite0F0.selectReport0F0List", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectReport0F0ListCnt(Map map) throws Exception {
        return (Integer) getSqlMapClientTemplate().queryForObject("ReportWrite0F0.selectReport0F0ListCnt", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectReportAttrCnt(Map map) throws Exception {
    	return (Integer) getSqlMapClientTemplate().queryForObject("ReportWrite0F0.selectReportAttrCnt", map);
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertReportAttr(Map map) throws Exception {
    	insert("ReportWrite0F0.insertReportAttr", map);
    	insertReportAttrH(map, "010");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertReportAttrSel(Map map) throws Exception {
    	insert("ReportWrite0F0.insertReportAttrSel", map);
    	insertReportAttrH(map, "010");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReportAttr(Map map) throws Exception {
    	// 변경 전 원본 값을 먼저 스냅샷한 뒤에 UPDATE한다.
    	insertReportAttrH(map, "020");
    	update("ReportWrite0F0.updateReportAttr", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateReportAttrSel(Map map) throws Exception {
    	insertReportAttrH(map, "020");
    	update("ReportWrite0F0.updateReportAttrSel", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport0F0ExcelList(Map map) throws Exception{
    	return list("ReportWrite0F0.selectReport0F0ExcelList", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteReportAttr(Map map) throws Exception {
    	// 삭제 전 원본 값을 먼저 스냅샷한 뒤에 DELETE한다.
    	insertReportAttrH(map, "030");
    	delete("ReportWrite0F0.deleteReportAttr", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectReportAttrListByCompo(Map map) throws Exception {
    	return list("ReportWrite0F0.selectReportAttrListByCompo", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteReportAttrByCompo(Map map) throws Exception {
    	delete("ReportWrite0F0.deleteReportAttrByCompo", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertReportAttrH(Map map, String hisFg) throws Exception {
    	try {
    		map.put("hisFg", hisFg);
    		insert("BcjisHisComm.insertTbReportAttrH", map);
    	} catch (Exception e) {
    		logger.error("insertReportAttrH(map)", e);
    	}
    }
}

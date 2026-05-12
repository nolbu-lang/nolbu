package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoModifyReportDAO")
public class DialogDgrcompoModifyReportDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public Map selectReportData(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoModifyReport.selectReportData", map);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectReportDetlData(Map map) throws Exception{
    	return (Map)selectByPk("DialogDgrcompoModifyReport.selectReportDetlData", map);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectReportDetlDataEtc(Map map) throws Exception{
    	return (Map)selectByPk("DialogDgrcompoModifyReport.selectReportDetlDataEtc", map);
    }
    
    @SuppressWarnings("rawtypes")
	public void updateReportData(Map map) {
		update("DialogDgrcompoModifyReport.updateReportData", map);
	}
    
    @SuppressWarnings("rawtypes")
    public void updateReportDataEtc(Map map) {
    	update("DialogDgrcompoModifyReport.updateReportDataEtc", map);
    }
    
}

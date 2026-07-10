package com.cs.bcjis.report.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.comm.util.BcjisNumberUtil;
import com.cs.bcjis.comm.util.BcjisStringUtil;
import com.cs.bcjis.report.service.ReportWrite020Service;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service("reportWrite020Service")
public class ReportWrite020ServiceImpl implements ReportWrite020Service {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "reportWrite020DAO")
    private ReportWrite020DAO reportWrite020DAO;

    @SuppressWarnings("rawtypes")
    public List selectReport020List(Map map) throws Exception {
        return reportWrite020DAO.selectReport020List(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectReport020PageListCnt(Map map) throws Exception {
        return reportWrite020DAO.selectReport020PageListCnt(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020PageList(Map map) throws Exception {
    	//return reportWrite020DAO.selectReport020PageList(map);
    	List list = reportWrite020DAO.selectReport020PageList(map);
    	
    	for(int i=0 ; i<list.size() ; i++){
    		EgovMap data = (EgovMap)list.get(i);
    		String fisYear = BcjisStringUtil.nullConvert(data.get("fisYear"));
    		int bgtDgr = BcjisNumberUtil.nullConvertToInt(data.get("bgtDgr"));
    		String teBgtCompoId = BcjisStringUtil.nullConvert(data.get("teBgtCompoId"));
    		String reportCd = BcjisStringUtil.nullConvert(data.get("reportCd"));
    		String reportDetlCd = BcjisStringUtil.nullConvert(data.get("reportDetlCd"));
    		
    		Map<String, String> vo = new HashMap<String, String>();
    		vo.put("fisYear", fisYear);
    		vo.put("bgtDgr", String.valueOf(bgtDgr)); 
    		vo.put("teBgtCompoId", teBgtCompoId);
    		vo.put("reportCd", reportCd);
    		vo.put("reportDetlCd", reportDetlCd);
    		
    		Map res = reportWrite020DAO.selectReport020DData(vo);
    		
    		if(res == null){
    			data.put("CHECK_YN_3250000", "N");
    			data.put("CHECK_YN_3260000", "N");
    			data.put("CHECK_YN_3270000", "N");
    			data.put("CHECK_YN_3280000", "N");
    			data.put("CHECK_YN_3290000", "N");
    			data.put("CHECK_YN_3300000", "N");
    			data.put("CHECK_YN_3310000", "N");
    			data.put("CHECK_YN_3320000", "N");
    			data.put("CHECK_YN_3330000", "N");
    			data.put("CHECK_YN_3340000", "N");
    			data.put("CHECK_YN_3350000", "N");
    			data.put("CHECK_YN_3360000", "N");
    			data.put("CHECK_YN_3370000", "N");
    			data.put("CHECK_YN_3380000", "N");
    			data.put("CHECK_YN_3390000", "N");
    			data.put("CHECK_YN_3400000", "N");
    		}else{
    			data.put("CHECK_YN_3250000", BcjisStringUtil.nullConvert(res.get("checkYn3250000")));
    			data.put("CHECK_YN_3260000", BcjisStringUtil.nullConvert(res.get("checkYn3260000")));
    			data.put("CHECK_YN_3270000", BcjisStringUtil.nullConvert(res.get("checkYn3270000")));
    			data.put("CHECK_YN_3280000", BcjisStringUtil.nullConvert(res.get("checkYn3280000")));
    			data.put("CHECK_YN_3290000", BcjisStringUtil.nullConvert(res.get("checkYn3290000")));
    			data.put("CHECK_YN_3300000", BcjisStringUtil.nullConvert(res.get("checkYn3300000")));
    			data.put("CHECK_YN_3310000", BcjisStringUtil.nullConvert(res.get("checkYn3310000")));
    			data.put("CHECK_YN_3320000", BcjisStringUtil.nullConvert(res.get("checkYn3320000")));
    			data.put("CHECK_YN_3330000", BcjisStringUtil.nullConvert(res.get("checkYn3330000")));
    			data.put("CHECK_YN_3340000", BcjisStringUtil.nullConvert(res.get("checkYn3340000")));
    			data.put("CHECK_YN_3350000", BcjisStringUtil.nullConvert(res.get("checkYn3350000")));
    			data.put("CHECK_YN_3360000", BcjisStringUtil.nullConvert(res.get("checkYn3360000")));
    			data.put("CHECK_YN_3370000", BcjisStringUtil.nullConvert(res.get("checkYn3370000")));
    			data.put("CHECK_YN_3380000", BcjisStringUtil.nullConvert(res.get("checkYn3380000")));
    			data.put("CHECK_YN_3390000", BcjisStringUtil.nullConvert(res.get("checkYn3390000")));
    			data.put("CHECK_YN_3400000", BcjisStringUtil.nullConvert(res.get("checkYn3400000")));
    		}
    		
    	}
    	
    	return list;
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020TotList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020TotList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelListTot(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelListTot(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020ExcelList2(Map map) throws Exception {
        return reportWrite020DAO.selectReport020ExcelList2(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport027ExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReport027ExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020RptTotList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020RptTotList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020OfficeList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReport020OfficeSiList(Map map) throws Exception {
        return reportWrite020DAO.selectReport020OfficeSiList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportOfficeSi022List(Map map) throws Exception {
        return reportWrite020DAO.selectReportOfficeSi022List(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportBizExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportBizExcelListTot(Map map) throws Exception {
        return reportWrite020DAO.selectReportBizExcelListTot(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportGugunTotExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportGugunTotExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectReportGugunExcelList(Map map) throws Exception {
        return reportWrite020DAO.selectReportGugunExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport020(JSONObject jsonParam) throws Exception {
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            String updateReportFlag = (String) tempParam.get("updateReportFlag");
            
            //보고항목, 사전절차 수정시 업데이트
            if(updateReportFlag != null && "Y".equals(updateReportFlag)){
            	reportCommDAO.updateReport(tempParam);
            }
            
            reportWrite020DAO.updateReport020(tempParam);

            reportCommDAO.updateReport020D(tempParam);

            if ("Y".equals(tempParam.get("srchValYn")) == true) {
                reportCommDAO.updateSrchValChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("reportSortSeqYn")) == true) {
                reportCommDAO.updateReportSortSeqChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("mayorReportChangeYn")) == true) {
                reportCommDAO.updateMayorReportYnChildReport(tempParam);
            }

            if ("Y".equals(tempParam.get("reflegFgYn")) == true && "020".equals(tempParam.get("reflectFg")) == true) {
                budgetCommDAO.updateDiffAmtByReflegFg(tempParam);
            }
            
            if ("Y".equals(tempParam.get("reflegFgYn")) == true && "010".equals(tempParam.get("reflectFg")) == true) {
            	budgetCommDAO.updateDiffAmtByReflegFgDmn(tempParam);
            }
        }
    }
}

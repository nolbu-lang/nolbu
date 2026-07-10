package com.cs.bcjis.budget.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetSelectNewService;
import com.cs.bcjis.budget.service.BudgetSelectService;
import com.cs.bcjis.budget.service.BudgetSheetSelectService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;
import com.cs.bcjis.report.service.impl.ReportWrite0F0DAO;

@Service("budgetSelectNewService")
public class BudgetSelectNewServiceImpl implements BudgetSelectNewService {
    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetSelectDAO")
    private BudgetSelectDAO budgetSelectDAO;
    
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "budgetSheetSelectService")
    private BudgetSheetSelectService budgetSheetSelectService;
    
    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;
    
    @Resource(name="reportWrite0F0DAO")
    private ReportWrite0F0DAO reportWrite0F0DAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(Map map) throws Exception {
        String reportCd = String.valueOf(map.get("reportCd"));

        return budgetSelectDAO.selectDgrCompoList(reportCd, map);
    }
    
    private Map<String, String> reportCdToSheetCd = new HashMap<String, String>();
    private Map<String, String> reportDetlCdToSheetDetlCd = new HashMap<String, String>();

    @SuppressWarnings("rawtypes")
    public void saveReport(JSONObject jsonParam) throws Exception {

        String fisFgMstCd = String.valueOf(jsonParam.get("fisFgMstCd"));
        String fisFgCd = String.valueOf(jsonParam.get("fisFgCd"));
        String officeCd = String.valueOf(jsonParam.get("officeCd"));
        String deptRankFr = String.valueOf(jsonParam.get("deptRankFr"));
        String deptRankTo = String.valueOf(jsonParam.get("deptRankTo"));
        String teMngMokCdFr = String.valueOf(jsonParam.get("teMngMokCdFr"));
        String teMngMokCdTo = String.valueOf(jsonParam.get("teMngMokCdTo"));
        String frscFgCdFr = String.valueOf(jsonParam.get("frscFgCdFr"));
        String frscFgCdTo = String.valueOf(jsonParam.get("frscFgCdTo"));
        
        initCdData(); //집계표 데이터 초기화
        List saveReportDatas = jsonParam.getJSONArray("saveReportDatas");
        JSONObject tempParam = null;
        
        for (int i = 0; i < saveReportDatas.size(); i++) {
        	tempParam = (JSONObject) saveReportDatas.get(i);
        	//System.out.println("@@@@@@@@@@@@@@@@  tempParam : " + tempParam);
        	String reportCd = String.valueOf(tempParam.get("reportCd"));
        	String reportDetlCd = String.valueOf(tempParam.get("reportDetlCd"));
        	
        	tempParam.put("fisFgMstCd", fisFgMstCd);
        	tempParam.put("fisFgCd", fisFgCd);
        	tempParam.put("officeCd", officeCd);
        	tempParam.put("deptRankFr", deptRankFr);
        	tempParam.put("deptRankTo", deptRankTo); 
        	tempParam.put("teMngMokCdFr", teMngMokCdFr);
        	tempParam.put("teMngMokCdTo", teMngMokCdTo);
        	tempParam.put("frscFgCdFr", frscFgCdFr);
        	tempParam.put("frscFgCdTo", frscFgCdTo);
        	tempParam.put("userId", jsonParam.get("userId"));
        	
        	budgetCommDAO.updateDgrcompoSrchVal(tempParam);
        	
        	Map existDataMap = getExistDataMap(tempParam);
        	Map tempKeyMap = null;
            String reportKeyString = getReportKeyString(tempParam);
            
            //System.out.println("@@@@ reportKeyString : " + reportKeyString);
            //System.out.println("@@@@ existDataMap : " + existDataMap);
        	tempKeyMap = (Map) existDataMap.remove(reportKeyString);
            if (tempKeyMap == null) {
            	if(!"".equals(reportCd)){
            		reportCommDAO.insertReport(reportCd, tempParam);
            	}
            }else{
            	if(!"".equals(reportCd)){
            		reportCommDAO.updateReport(tempParam);
            		
            		if("Y".equals(tempParam.get("checkYn031")) == true){
            			JSONObject tempParam030 = tempParam;
            			tempParam030.put("reportCd", "030");
            			tempParam030.put("reportDetlCd", "031");
                    	reportCommDAO.updateReport(tempParam030);
                    }
                    
                    if("Y".equals(tempParam.get("checkYn032")) == true){
                    	JSONObject tempParam030 = tempParam;
            			tempParam030.put("reportCd", "030");
            			tempParam030.put("reportDetlCd", "032");
                    	reportCommDAO.updateReport(tempParam030);
                    }
                    
                    if("Y".equals(tempParam.get("checkYn033")) == true){
                    	JSONObject tempParam030 = tempParam;
            			tempParam030.put("reportCd", "030");
            			tempParam030.put("reportDetlCd", "033");
                    	reportCommDAO.updateReport(tempParam030);
                    }
                    
                    if("Y".equals(tempParam.get("checkYn034")) == true){
                    	JSONObject tempParam030 = tempParam;
            			tempParam030.put("reportCd", "030");
            			tempParam030.put("reportDetlCd", "034");
                    	reportCommDAO.updateReport(tempParam030);
                    }
                    
                    if("Y".equals(tempParam.get("checkYn035")) == true){
                    	JSONObject tempParam030 = tempParam;
            			tempParam030.put("reportCd", "030");
            			tempParam030.put("reportDetlCd", "035");
                    	reportCommDAO.updateReport(tempParam030);
                    }
            	}
            }
            
            String indiAttr = String.valueOf(tempParam.get("indiAttr")); //변경된 보고항목
            String indiAttrOrg = String.valueOf(tempParam.get("indiAttrOrg")); //원본 보고항목

            
            if(!"".equals(indiAttr)){
            	String[] indiAttrArr = indiAttr.split(",");
            	String[] indiAttrOrgArr = indiAttrOrg.split(",");
            	JSONObject attrParam = (JSONObject) saveReportDatas.get(i);

            	for(int j=0 ; j<indiAttrArr.length ; j++){
            		
            		//기존 보고항목에 포함안된 보고항목 수정및 입력
            		if(!Arrays.asList(indiAttrOrgArr).contains(indiAttrArr[j])){
            			attrParam.put("indiAttr", indiAttrArr[j]);
                		int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
                		int reportCdInt = Integer.parseInt(reportCd);
                        if(reportCdInt > 100){
                        	attrParam.put("reportTableNm", "TB_REPORT100");
                        }else{
                        	attrParam.put("reportTableNm", "TB_REPORT" + reportCd);
                        }
                        if(cnt > 0){
                        	reportWrite0F0DAO.updateReportAttrSel(attrParam);
                        }else{
                        	reportWrite0F0DAO.insertReportAttrSel(attrParam);
                        }
            		}
            		 
            	} 
            	
            	//사용안하는 보고항목 삭제
            	/*for(int j=0 ; j<indiAttrOrgArr.length ; j++){
            		if(!Arrays.asList(indiAttrArr).contains(indiAttrOrgArr[j])){
            			System.out.println("delete      :    " + indiAttrOrgArr[j] + "      " + indiAttrOrgArr.length);
            			if(!spaceCheck(indiAttrOrgArr[j])){
            				attrParam.put("indiAttr", indiAttrOrgArr[j]);
                			reportWrite0F0DAO.deleteReportAttr(attrParam);
            			}
            			
            		}
            	}*/
            	
            }else{
            	JSONObject attrParam = (JSONObject) saveReportDatas.get(i);
            	attrParam.put("indiAttr", null);
            	int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
                
                if(cnt > 0){
                	reportWrite0F0DAO.deleteReportAttr(attrParam);
                }
            }
            
            deleteReport(existDataMap);
            
            if ("070".equals(reportCd) == true) {
                insertReport070s(reportCd, reportDetlCd);
            }
            
            //집계표 입력
            /*String sheetCd = "";
            String sheetDetlCd = "";
            String govSub = String.valueOf(tempParam.get("govSub"));
            
            if(("020".equals(reportDetlCd) && "0292".equals(reportDetlCd))	//투자사업비-국고투자
            		|| ("300".equals(reportDetlCd) && "301".equals(reportDetlCd))	//일반국비
            		){	
            	sheetCd = reportCdToSheetCd.get(reportCd);
                sheetDetlCd = reportDetlCdToSheetDetlCd.get(govSub);
            }else{
            	sheetCd = reportCdToSheetCd.get(reportCd);
                sheetDetlCd = reportDetlCdToSheetDetlCd.get(reportDetlCd);
            }
            
            if(sheetCd != null && sheetDetlCd != null && !"".equals(sheetCd) && !"".equals(sheetDetlCd)){
            	tempParam.put("userId", jsonParam.get("userId"));
                tempParam.put("sheetCd", sheetCd);
                tempParam.put("sheetDetlCd", sheetDetlCd);
                int cnt = budgetSheetSelectDAO.selectSheetCnt(tempParam);
                if(cnt == 0){
                	budgetSheetSelectDAO.insertSheet(tempParam);
                }else{
                	budgetSheetSelectDAO.deleteSheet(tempParam);
                	budgetSheetSelectDAO.insertSheet(tempParam);
                }
            }*/
            
        }
        
        /**
         * 국고보조 관련 처리
         * 국고보조 데이터를 가져와서
         * 031~035 체크하여 수정된게 있으면 삭제하거나 insert 한다.
         * insert 할때는 tb_report 테이블과 tb_report030 둘다 insert한다
         * 그외 정보 update 되는게 없다.
         * tb_report에 030까지 들어가있어서 update가 필요
         * checkYn031 : 현재 국고-일반이 체크가 되어있는지
         * checkYn031Yn : 국고-일반 체크 여부가 변경된경우 (다시 체크하거나 수정되었을때)
         * 따라서 checkYn031Yn이 Y인 경우만 구분하면 다른 국고보조로 변경하지 않으면 별다른 작업이 없다는 이야기
         * 보고항목, 분류항목이 수정되는 경우가 반영되려면 
         * 위에서 tb_Report update 실행시 checkYn031 ~ checkYn035까지 중에 하나라도 있으면 tb_report 업데이트만 반영
         * 무조건 반영하기에는 reportDetlCd가 다르기 때문에 checkYn을 확인하고 Y가 있으면 update실행
         * 
         */
        List saveReportDatas030 = jsonParam.getJSONArray("saveReportDatas030");

        for (int i = 0; i < saveReportDatas030.size(); i++) {
            tempParam = (JSONObject) saveReportDatas030.get(i);
            String reportCd = "030";
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("reportCd", reportCd);
            
            Map existDataMap = getExistDataMap(tempParam);
            String reportKeyString = getReportKeyString(tempParam);
            Map tempKeyMap = null;
            tempKeyMap = (Map) existDataMap.remove(reportKeyString);

            if("Y".equals(tempParam.get("checkYn031Yn")) == true){
                tempParam.put("reportDetlCd", "031");
                
                if("Y".equals(tempParam.get("checkYn031")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }

            if("Y".equals(tempParam.get("checkYn032Yn")) == true){
                tempParam.put("reportDetlCd", "032");
                if("Y".equals(tempParam.get("checkYn032")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }

            if("Y".equals(tempParam.get("checkYn033Yn")) == true){
                tempParam.put("reportDetlCd", "033");
                if("Y".equals(tempParam.get("checkYn033")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }
            
            if("Y".equals(tempParam.get("checkYn034Yn")) == true){
            	tempParam.put("reportDetlCd", "034");
            	if("Y".equals(tempParam.get("checkYn034")) == true){
            		tempKeyMap = (Map) existDataMap.remove(reportKeyString);

            		if (tempKeyMap == null) {
            			reportCommDAO.insertReport(reportCd, tempParam);
            		}
            	}else{
            		reportCommDAO.deleteReport(reportCd, tempParam);
            	}
            }
            
            if("Y".equals(tempParam.get("checkYn035Yn")) == true){
            	tempParam.put("reportDetlCd", "035");
            	if("Y".equals(tempParam.get("checkYn035")) == true){
            		tempKeyMap = (Map) existDataMap.remove(reportKeyString);

            		if (tempKeyMap == null) {
            			reportCommDAO.insertReport(reportCd, tempParam);
            		}
            	}else{
            		reportCommDAO.deleteReport(reportCd, tempParam);
            	}
            }
            
        }
        
        
        

/*
        Map tempKeyMap = null;
        String reportKeyString = "";
        //저장할 데이터들을 저장
        for (int i = 0; i < saveReportDatas.size(); i++) {
            tempParam = (JSONObject) saveReportDatas.get(i);

            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("reportCd", reportCd);
            tempParam.put("reportDetlCd", reportDetlCd);
            tempParam.put("orderYmdSeq", orderYmdSeq);

            reportKeyString = getReportKeyString(reportCd, tempParam);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  " + i + "   :  reportKeyString :  " + reportKeyString);
			//이미 등록되어있는 데이터 목록에서 하나씩 가져와서 null일 경우 insert
            tempKeyMap = (Map) existDataMap.remove(reportKeyString);
            if (tempKeyMap == null) {
                reportCommDAO.insertReport(reportCd, tempParam);
            }
        }

        deleteReport(reportCd, existDataMap);
*/
        /*
        if ("070".equals(reportCd) == true) {
            insertReport070s(reportCd, reportDetlCd);
        }

        if ("021".equals(reportDetlCd) == true || "022".equals(reportDetlCd) == true || "023".equals(reportDetlCd) == true) {
            reportCd = "030";
            String sheetCd = "TF0";
            String sheetDetlCd = "TF1";
            
            List saveReportDatas030 = jsonParam.getJSONArray("saveReportDatas030");
            jsonParam.put("reportDetlCd", "031");
            Map existDataMap031 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "032");
            Map existDataMap032 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "033");
            Map existDataMap033 = getExistDataMap(reportCd, jsonParam);

            jsonParam.put("sheetCd", sheetCd);
            jsonParam.put("sheetDetlCd", sheetDetlCd);
            Map existDataMapTF1 = budgetSheetSelectService.getExistDataMap(jsonParam);
            
            for (int i = 0; i < saveReportDatas030.size(); i++) {
                tempParam = (JSONObject) saveReportDatas030.get(i);

                tempParam.put("userId", jsonParam.get("userId"));
                tempParam.put("reportCd", "030");
                tempParam.put("sheetCd", sheetCd);
                
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  " + i + "   :  tempParam :  " + tempParam);
                if("Y".equals(tempParam.get("checkYn031Yn")) == true){
                    tempParam.put("reportDetlCd", "031");
                    
                    if("Y".equals(tempParam.get("checkYn031")) == true){
                        tempKeyMap = (Map) existDataMap031.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  031  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn032Yn")) == true){
                    tempParam.put("reportDetlCd", "032");
                    if("Y".equals(tempParam.get("checkYn032")) == true){
                        tempKeyMap = (Map) existDataMap032.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  032  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn033Yn")) == true){
                    tempParam.put("reportDetlCd", "033");
                    if("Y".equals(tempParam.get("checkYn033")) == true){
                        tempKeyMap = (Map) existDataMap033.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  033  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYnTf1Yn")) == true){
                    tempParam.put("sheetDetlCd", sheetDetlCd);
                    if("Y".equals(tempParam.get("checkYnTf1")) == true){
                        tempKeyMap = (Map) existDataMapTF1.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            budgetSheetSelectDAO.insertSheet(tempParam);
                        }
                    }else{
                        budgetSheetSelectDAO.deleteSheet(tempParam);
                    }
                }
            }
        }*/
    }
    
    public void insertReport070s(String reportCd, String reportDetlCd) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("reportCd", reportCd);
        map.put("reportDetlCd", reportDetlCd);
        
        reportCommDAO.deleteReport(map);
        reportCommDAO.insertReport070s(map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMap(Map map) throws Exception {
        Map existDataMap = new HashMap();
        String reportCd = String.valueOf(map.get("reportCd"));
        List existDatas = budgetSelectDAO.selectReportKeyListNew(reportCd, map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map tempMap = null;
        while (!existDatas.isEmpty()) {
            tempMap = (Map) existDatas.remove(0);
            existDataMap.put(getReportKeyString(tempMap), tempMap);
        }
        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public void deleteReport(Map map) throws Exception {
        if (map == null || map.keySet() == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        if (iterator == null) {
            return;
        }

        String key = "";
        Map tempMap = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            tempMap = (Map) map.get(key);
            String reportCd = String.valueOf(tempMap.get("reportCd"));
            
            if(!"030".equals(reportCd)){
            	reportCommDAO.deleteReport(reportCd, tempMap);
            }
            
            
        }
    }

    @SuppressWarnings("rawtypes")
    public String getReportKeyString(Map map) {
    	String reportCd = String.valueOf(map.get("reportCd"));
    	
        if ("070".equals(reportCd) == true) {
            return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("orderYmdSeq")) + "_" + String.valueOf(map.get("teBgtCompoId"));
        }

        return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("teBgtCompoId"));
    }

    private void initCdData(){
    	reportCdToSheetCd = new HashMap<String, String>();
    	reportDetlCdToSheetDetlCd = new HashMap<String, String>();
    	
    	
    	reportCdToSheetCd.put("101", "TI1"); //예산삭감
    	reportDetlCdToSheetDetlCd.put("100", "TI0"); //예산삭감
    	reportCdToSheetCd.put("150", "T90"); //지방채상환 
    	reportDetlCdToSheetDetlCd.put("151", "T91"); //지방채상환
    	
    }
    
    public boolean spaceCheck(String spaceCheck)
    {
        for(int i = 0 ; i < spaceCheck.length() ; i++)
        {
            if(spaceCheck.charAt(i) == ' ')
                return true;
        }
        return false;
    }

}

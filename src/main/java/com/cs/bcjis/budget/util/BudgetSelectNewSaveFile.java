package com.cs.bcjis.budget.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cs.bcjis.comm.service.impl.BcjisCommDAO;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;
import com.cs.bcjis.report.util.ReportFormulaUtil;
import com.cs.bcjis.report.util.ReportSaveUtil;

import egovframework.rte.fdl.string.EgovStringUtil;

@Component("budgetSelectNewSaveFile")
public class BudgetSelectNewSaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @Resource(name = "bcjisCommDAO")
    private BcjisCommDAO bcjisCommDAO;
    
    int lineNum = 1;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        dataList(model, wb, (List<Object>) model.get("dataList"));

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void dataList(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {

        int rowNum = 0;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;

        int dgrLevel = 0;

        int totDataCnt = 0;

        Map commParam = new HashMap();
        commParam.put("codeId", "RP014");
        List indiList = bcjisCommDAO.selectCommComboList(commParam);
        commParam.put("codeId", "RP015");
        List advncList = bcjisCommDAO.selectCommComboList(commParam);
        commParam.put("codeId", "RP010");
        List mstrList = bcjisCommDAO.selectCommComboList(commParam);
        commParam.put("codeId", "RP011");
        List reportList = bcjisCommDAO.selectCommComboList(commParam);
        commParam.put("codeId", "RP012");
        List detlList = bcjisCommDAO.selectCommComboList(commParam);
        
        Map param = new HashMap();
        param.put("reportCd", "0F0");
        param.put("reportDetlCd", "0F1");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));
        param.put("amtUnit", model.get("amtUnit"));

        Map reportInfo = reportCommDAO.selectReportInfo(param);
        //boolean bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;

        boolean bgtCompoFlag = false;
        
        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);

        String sheetName = ReportSaveUtil.getStringValue(reportInfo.get("sheetNm"));
        
        int fisYear = ReportSaveUtil.getIntValue(model.get("fisYear"));
        
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet(biz)";
        }

        sheet = wb.createSheet(sheetName);
        reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeDataListHeader(param, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")).replace("§toYear§", rtnYear(fisYear, 0)));

        for (int i = 0; i < categories.size(); i++) {
            category = (JSONObject) categories.get(i);

            try {
                //dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
                dgrLevel = (int) Double.parseDouble(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                totDataCnt = 0;
            } else if (dgrLevel > 3) {
                totDataCnt++;
            }
             
            if(dgrLevel == 0 || dgrLevel == 2 || dgrLevel == 4){ //부서랑 항목만
            	rowNum = writeDataListData(sheet, rowNum, category, styles, totDataCnt, bgtCompoFlag, reportFormulaUtil, indiList, advncList, mstrList, reportList, detlList);
            }
            
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, param, wb, sheet, rowNum, styles, reportInfo, 13, 6);
            sheet.setColumnWidth(17, (short)6208);
            sheet.setColumnWidth(18, (short)6208);
            sheet.setColumnWidth(19, (short)6208);
            sheet.setColumnWidth(20, (short)6208);
            sheet.setColumnWidth(21, (short)6208);
            sheet.addMergedRegion(CellRangeAddress.valueOf("R3:R4"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("S3:S4"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("T3:T4"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("U3:U4"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("V3:V4"));
            reportFormulaUtil.writeCellFormula();
            sheet = null;
            wb.setPrintArea(0, 0, 7, 0, rowNum);
        }
    }

    @SuppressWarnings("rawtypes")
    public int writeDataListHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;
        int fisYear = ReportSaveUtil.getIntValue(model.get("fisYear"));
        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(34.50f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title.replace("보고항목", "예산심사조서, 집계표 항목"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$H$1")); //총사업비 병합

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(19.5f);
        //row.setHeightInPoints(33.75f);

        String amtUnit = (String)model.get("amtUnit");
        String amtUnitNm = "(단위 : 백만원)";
        if("1000".equals(amtUnit)){
        	amtUnitNm = "(단위 : 천원)";
        }

        cell = row.createCell(7);
        cell.setCellValue(amtUnitNm);
        cell.setCellStyle(styles.get("unit"));

        repeatingStartRow = rowNum;

        float rowHeight = 29.75f;
        int preRowSeq = -1;
        int rowSeq = -1;
        int cellSeq = -1;
        String headerCont = "";

        list = reportCommDAO.selectReportHeaderList(model);
        while (!list.isEmpty()) {
            map = (Map) list.remove(0);

            
            rowSeq = ReportSaveUtil.getIntValue(map.get("rowSeq"));
            cellSeq = ReportSaveUtil.getIntValue(map.get("cellSeq"));
            headerCont = ReportSaveUtil.getStringValue(map.get("headerCont"));
            if (rowSeq < 0) {
                throw new Exception("보고서 Header 정보 오류입니다.");
            }

            if (preRowSeq == -1 || preRowSeq != rowSeq) {
                row = sheet.createRow(rowNum);
                rowNum++;
                row.setHeightInPoints(rowHeight);
            }

            preRowSeq = rowSeq;

            cell = row.createCell(cellSeq);
            cell.setCellStyle(styles.get("header" + rowSeq + "Col" + cellSeq));
            
            if (BcjisCommUtil.isNullString(headerCont) == false) {
            	headerCont = headerCont.replace("§toYear§", rtnYear(fisYear, 0));	//해당년도
            	headerCont = headerCont.replace("§preYear§", rtnYear(fisYear, -1)); //해당년도 -1
            	headerCont = headerCont.replace("§prePreYear§", rtnYear(fisYear, -2)); //해당년도 -2
                cell.setCellValue(headerCont);
            }
        }
        
        row = sheet.getRow(2);
        cell = row.createCell(17);
        cell.setCellStyle(styles.get("header0" + "Col16"));
        cell.setCellValue("보고항목");
        
        cell = row.createCell(18);
        cell.setCellStyle(styles.get("header0" + "Col16"));
        cell.setCellValue("분류항목");
        
        cell = row.createCell(19);
        cell.setCellStyle(styles.get("header0" + "Col16"));
        cell.setCellValue("대분류");
        
        cell = row.createCell(20);
        cell.setCellStyle(styles.get("header0" + "Col16"));
        cell.setCellValue("중분류");
        
        cell = row.createCell(21);
        cell.setCellStyle(styles.get("header0" + "Col16"));
        cell.setCellValue("소분류");
        
        row = sheet.getRow(3);
        cell = row.createCell(17);
        cell.setCellStyle(styles.get("header1" + "Col16"));
        cell = row.createCell(18);
        cell.setCellStyle(styles.get("header1" + "Col16"));
        cell = row.createCell(19);
        cell.setCellStyle(styles.get("header1" + "Col16"));
        cell = row.createCell(20);
        cell.setCellStyle(styles.get("header1" + "Col16"));
        cell = row.createCell(21);
        cell.setCellStyle(styles.get("header1" + "Col16"));
        
        
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(BcjisCommUtil.getShortValue(reportInfo.get("printPaperSize")));
        printSetup.setScale(BcjisCommUtil.getShortValue(reportInfo.get("printScale")));

        sheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("leftMargin")));
        sheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("rightMargin")));
        sheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("topMargin")));
        sheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("bottomMargin")));
        sheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("headerMargin")));
        sheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("footerMargin")));

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(0, 4);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(true);

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        ReportSaveUtil.reportMerge(reportCommDAO, model, sheet);

        return rowNum;
    }

    private String rtnYear(int toYear, int type){
    	String rtnYear = ReportSaveUtil.getStringValue((toYear + type));
    	
    	int len = rtnYear.length();
    	if(len == 4){
    		
    		rtnYear = rtnYear.substring(2, 4);
    	}else{
    		rtnYear = "";
    	}
    	
    	return rtnYear;
    }
    
    public int writeDataListData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int totDataCnt, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil
    		, List indiList, List advncList, List mstrList, List reportList, List detlList) {
    	int unit = 1000;
    	unit = 1;
        float rowHeight = 34.5f;
        Row row = null;
        Cell cell = null;

        int dgrLevel = 0;
        try {
            //dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            dgrLevel = (int) Double.parseDouble(String.valueOf(category.get("dgrLevel")));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        String bgtDgr = ReportSaveUtil.getStringValue(category.get("bgtDgr"));
        String dgrcompoId = ReportSaveUtil.getStringValue(category.get("dgrcompoId"));
        String upDgrcompoId = ReportSaveUtil.getStringValue(category.get("upDgrcompoId"));

        boolean formulaFlag = true;

        String preStyleNm = "";
        if (dgrLevel == 0) {
            preStyleNm = "tot";
            rowHeight = 34.5f;
        } else if (dgrLevel == 2) {
            preStyleNm = "dept";
            rowHeight = 34.5f;
        } else if (dgrLevel == 4) {
            preStyleNm = "data0";
            rowHeight = 34.5f;
            formulaFlag = false;
        } 
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        if (dgrLevel == 4) {
            cell.setCellValue(lineNum);
            lineNum++;
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));

	    cell = row.createCell(2);
	    cell.setCellStyle(styles.get(preStyleNm + "Col2"));
	    if (formulaFlag == false) {
	    	//cell.setCellFormula("" + "0" + "/" + unit);	//총사업비
	    	cell.setCellFormula("" + "0");	//총사업비
	    }
	
	    cell = row.createCell(3);
	    cell.setCellStyle(styles.get(preStyleNm + "Col3"));
	    if (formulaFlag == false) {
	    	//cell.setCellFormula("" + "0" + "/" + unit);	//기투자
	    	cell.setCellFormula("" + "0");	//기투자
	    }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
        	if("1".equals(bgtDgr)){
        		//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preBgtAmt")) + "/" + unit);
        		cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preBgtAmt")));
        	}else{
        		//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preAmt")) + "/" + unit);
        		cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preAmt")));
        	}
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("demandDiffAmt")) + "/" + unit);
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("demandDiffAmt")));
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("demandBgtAmt")));
        }

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("diffAmt")) + "/" + unit);
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("diffAmt")));
        	long sumAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt1")) 
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt2")) 
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt3"))
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt4"))
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt5"))
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt6"))
        			+ ReportSaveUtil.getAmtValue(category.get("frscAmt7"))
        			;
        	cell.setCellFormula("" + sumAmt); 
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("bgtAmt")));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "Col7"));
        if (formulaFlag == false) {
        	cell.setCellValue(ReportSaveUtil.getBizListRemark(category));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        cell.setCellFormula("J" + rowNum + "+" + "K" + rowNum + "+" + "L" + rowNum + "+" + "M" + rowNum + "+" + "N" + rowNum + "+" + "O" + rowNum);
        
        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "Col9"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt1")) + "/" + unit);
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt1")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "Col10"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt2")) + "/" + unit);
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt2")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "Col11"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt3")) + "/" + unit);
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt3")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "Col12"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt4")) + "/" + unit);
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt4")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "Col13"));
        if (formulaFlag == false) {
            //cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt5")) + "/" + unit);
            cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt5")));
        }
        
        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "Col14"));
        if (formulaFlag == false) {
        	//cell.setCellFormula("" + (ReportSaveUtil.getAmtValue(category.get("frscAmt6")) + ReportSaveUtil.getAmtValue(category.get("frscAmt7"))) + "/" + unit);
        	cell.setCellFormula("" + (ReportSaveUtil.getAmtValue(category.get("frscAmt6")) + ReportSaveUtil.getAmtValue(category.get("frscAmt7"))));
        }
        
        cell = row.createCell(15);
        cell.setCellStyle(styles.get(preStyleNm + "Col15"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("srchVal")));
        
        cell = row.createCell(16);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("teMngMokNm")));

        cell = row.createCell(16);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("teMngMokNm")));
        
        cell = row.createCell(17);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(commCodeRtnStr(indiList, ReportSaveUtil.getStringValue(category.get("indiAttr"))));
        
        cell = row.createCell(18);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(commCodeRtnStr(advncList, ReportSaveUtil.getStringValue(category.get("advncProc"))));
        
        String mstrNm = "";
        String reportNm = "";
        String detlNm = "";
        String mstrCd = ReportSaveUtil.getStringValue(category.get("reportMstr"));
        String reportCd = ReportSaveUtil.getStringValue(category.get("reportCd"));
        String detlCd = ReportSaveUtil.getStringValue(category.get("reportDetlCd"));
        
        for(int i=0 ; i<mstrList.size() ; i++){
        	Map tempMap = (Map) mstrList.get(i);
    		String cd = String.valueOf(tempMap.get("code"));
    		String cdNm = String.valueOf(tempMap.get("codeNm"));
    		if(cd.equals(mstrCd)){
    			mstrNm = cdNm;
    		}
        }
        for(int i=0 ; i<reportList.size() ; i++){
        	Map tempMap = (Map) reportList.get(i);
        	String cd = String.valueOf(tempMap.get("code"));
        	String cdNm = String.valueOf(tempMap.get("codeNm"));
        	String groupId = String.valueOf(tempMap.get("groupId"));
        	if(cd.equals(reportCd)){
        		reportNm = cdNm;
        		if("".equals(mstrCd)){ //중분류는 있는데 대분류가 없을경우
        			for(int j=0 ; j<mstrList.size() ; j++){
        	        	Map tempMap2 = (Map) mstrList.get(j);
        	    		String cd2 = String.valueOf(tempMap2.get("code"));
        	    		String cdNm2 = String.valueOf(tempMap2.get("codeNm"));
        	    		if(cd2.equals(groupId)){
        	    			mstrNm = cdNm2;
        	    		}
        	        }
        		}
        	}
        }
        for(int i=0 ; i<detlList.size() ; i++){
        	Map tempMap = (Map) detlList.get(i);
        	String cd = String.valueOf(tempMap.get("code"));
        	String cdNm = String.valueOf(tempMap.get("codeNm"));
        	if(cd.equals(detlCd)){
        		detlNm = cdNm;
        	}
        }
        
        cell = row.createCell(19);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(mstrNm);
        cell = row.createCell(20);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(reportNm);
        cell = row.createCell(21);
        cell.setCellStyle(styles.get(preStyleNm + "Col16"));
        cell.setCellValue(detlNm);
        
        if (formulaFlag == true) {
        	addBizDataFormulaCellList(reportFormulaUtil, rtnMergeKey(category, "dgrcompoId"), rowNum, bgtCompoFlag);
        	//System.out.println("nm : " + ReportSaveUtil.getStringValue(category.get("dgrcompoNm")) + "   dgrcompoId : " + rtnMergeKey(category, "dgrcompoId"));
        }  
        addBizDataFormulaValueList(reportFormulaUtil, rtnMergeKey(category, "upDgrcompoId"), rowNum, bgtCompoFlag);
        //System.out.println("nm : " + ReportSaveUtil.getStringValue(category.get("dgrcompoNm")) + "   upDgrcompoId : "  + rtnMergeKey(category, "upDgrcompoId"));
        
        return rowNum;
    }
    
    private String rtnMergeKey(JSONObject category, String type){
    	String rtnYear = "";
    	
    	int dgrLevel = 0;
    	String deptCd = "";
    	String teBgtCompoId = "";
        try {
            //dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            dgrLevel = (int) Double.parseDouble(String.valueOf(category.get("dgrLevel")));
            deptCd = ReportSaveUtil.getStringValue(category.get("deptCd"));
        	teBgtCompoId = ReportSaveUtil.getStringValue(category.get("teBgtCompoId"));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }
        
    	if("dgrcompoId".equals(type)){
    		if(dgrLevel == 0){
    			rtnYear = "1" + "_" + "0000000" + "_" + "00000000000";
    		}else if(dgrLevel == 2){
    			rtnYear = "2" + "_" + deptCd + "_" + "00000000000";
    		}else if(dgrLevel == 4){
    			rtnYear = "3" + "_" + deptCd + "_" + teBgtCompoId;
    		}
    	}else{
    		if(dgrLevel == 0){
    			rtnYear = "0" + "_" + "0000000" + "_" + "00000000000";
    		}else if(dgrLevel == 2){
    			rtnYear = "1" + "_" + "0000000" + "_" + "00000000000";
    		}else if(dgrLevel == 4){
    			rtnYear = "2" + "_" + deptCd + "_" + "00000000000";
    		}
    	}
    	
    	return rtnYear;
    }
    
    private String commCodeRtnStr(List list, String val){
    	
    	String rtnStr = "";
    	String[] valArr = val.split(",");
    	
    	for(int i=0 ; i<valArr.length ; i++){
    		String valCd = valArr[i];
    		for(int j=0; j<list.size(); j++){
        		Map tempMap = (Map) list.get(j);
        		String cd = String.valueOf(tempMap.get("code"));
        		String cdNm = String.valueOf(tempMap.get("codeNm"));
        		if(valCd.equals(cd)){
        			if("".equals(rtnStr)){
        				rtnStr = cdNm;
        			}else{
        				rtnStr += "," + cdNm;
        			}
        		}
        		
        	}
    	}
    	
    	return rtnStr;
    }
    
    public void addBizDataFormulaValueList(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaValue(keyStr + "_C", "C" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_D", "D" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_K", "K" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_L", "L" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_M", "M" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_N", "N" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_O", "O" + rowNum);
    }

    public void addBizDataFormulaCellList(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaCell(keyStr + "_C", rowNum - 1, 2);
        reportFormulaUtil.addFormulaCell(keyStr + "_D", rowNum - 1, 3);
        reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);
        reportFormulaUtil.addFormulaCell(keyStr + "_J", rowNum - 1, 9);
        reportFormulaUtil.addFormulaCell(keyStr + "_K", rowNum - 1, 10);
        reportFormulaUtil.addFormulaCell(keyStr + "_L", rowNum - 1, 11);
        reportFormulaUtil.addFormulaCell(keyStr + "_M", rowNum - 1, 12);
        reportFormulaUtil.addFormulaCell(keyStr + "_N", rowNum - 1, 13);
        reportFormulaUtil.addFormulaCell(keyStr + "_O", rowNum - 1, 14);
    }
}

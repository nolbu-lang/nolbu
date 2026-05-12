package com.cs.bcjis.report.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

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

import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Component("report090SaveFile")
public class Report090SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    int lineNum = 1;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        dataList(model, wb, (List<Object>) model.get("dataList"));
        data(model, wb, (List<Object>) model.get("dataList"));

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildSheetDocument(Map model, String KeyStr, String storePath) throws Exception {

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        data(model, wb, (List<Object>) model.get("dataList"));

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void data(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {
        int rowNum = 0;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;
        int dgrLevel = 0;

        String reportDetlCd = "";
        Map reportInfo = null;
        boolean bgtCompoFlag = false;

        Map<String, CellStyle> styles = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            reportDetlCd = ReportSaveUtil.getStringValue(category.get("reportDetlCd"));
            if (dgrLevel == 0) {
                if (sheet != null) {
                    ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 10);
                    reportFormulaUtil.writeCellFormula();

                    sheet = null;
                }

                rowNum = 0;

                model.put("reportDetlCd", reportDetlCd);
                reportInfo = reportCommDAO.selectReportInfo(model);

                bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;
                styles = reportCommDAO.getReportStyleMap(model, wb);

                sheet = wb.createSheet(ReportSaveUtil.getStringValue(reportInfo.get("sheetNm")));
                reportFormulaUtil = new ReportFormulaUtil(sheet);

                rowNum = writeHeader(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")), 10);

            }

            rowNum = writeData(sheet, rowNum, category, styles, bgtCompoFlag, reportFormulaUtil);
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 10);
            reportFormulaUtil.writeCellFormula();

            sheet = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public int writeHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title, int unitPos) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(56.25f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(styles.get("title"));

        rowNum = writeReportCont(model, sheet, rowNum, styles);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(15f);

        cell = row.createCell(unitPos);
        cell.setCellStyle(styles.get("unit"));
        cell.setCellValue("(단위:천원)");

        repeatingStartRow = rowNum;

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
                row.setHeightInPoints(24.75f);
            }

            preRowSeq = rowSeq;

            cell = row.createCell(cellSeq);
            cell.setCellStyle(styles.get("header" + rowSeq + "Col" + cellSeq));
            if (BcjisCommUtil.isNullString(headerCont) == false) {
                cell.setCellValue(headerCont);
            }

        }

        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(BcjisCommUtil.getShortValue(reportInfo.get("printPaperSize")));
        printSetup.setScale(BcjisCommUtil.getShortValue(reportInfo.get("printScale")));
        printSetup.setLandscape(true);

        sheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("leftMargin")));
        sheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("rightMargin")));
        sheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("topMargin")));
        sheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("bottomMargin")));
        sheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("headerMargin")));
        sheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("footerMargin")));
        sheet.createFreezePane(3, 5);

        sheet.setHorizontallyCenter(true);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(3, 6);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        Footer footer = sheet.getFooter();

        footer.setCenter("" + HeaderFooter.page());

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);
        ReportSaveUtil.reportMerge(reportCommDAO, model, sheet);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public int writeReportCont(Map param, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles) throws Exception {
        float rowHeight = 38.25f;
        Row row = null;
        Cell cell = null;

        List list = reportCommDAO.selectReportContList(param);
        Map map = null;
        for (int i = 0; i < list.size(); i++) {
            map = (Map) list.get(i);

            row = sheet.createRow(rowNum);
            rowNum++;
            row.setHeightInPoints(rowHeight);

            cell = row.createCell(0);
            cell.setCellStyle(styles.get("cont"));

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("cont"));
            cell.setCellValue(ReportSaveUtil.getStringValue(map.get("cont")));

            if (i == 0) {
                cell = row.createCell(10);
                cell.setCellStyle(styles.get("today"));
                cell.setCellFormula("TODAY()");
            }
        }

        return rowNum;
    }

    public int writeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil) throws Exception {
        float rowHeight = 27f;
        Row row = null;
        Cell cell = null;

        int dgrLevel = 0;
        try {
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        String dgrcompoId = ReportSaveUtil.getStringValue(category.get("dgrcompoId"));
        String upDgrcompoId = ReportSaveUtil.getStringValue(category.get("upDgrcompoId"));

        boolean formulaFlag = true;

        String preStyleNm = "";
        if (dgrLevel == 0) {
            preStyleNm = "tot";
        } else if (dgrLevel == 1) {
            preStyleNm = "office";
            rowHeight = 30f;
        } else if (dgrLevel == 2) {
            preStyleNm = "dept";
            rowHeight = 30f;
        } else if (dgrLevel == 3) {
            preStyleNm = "dbiz";
            rowHeight = 30f;
        } else if (dgrLevel == 4) {
            preStyleNm = "data0";
            formulaFlag = false;
        } else {
            preStyleNm = "data1";
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        if (dgrLevel < 4) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        if (dgrLevel >= 4) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("demandCompFormular")));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("demandBgtAmt")));
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("compFormular")));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("bgtAmt")));
        }

        if (bgtCompoFlag == false) {
            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col7"));
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("preFormular")));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get(preStyleNm + "Col8"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preAmt")));
            }
        } else {
            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col7"));
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("preCompFormular")));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get(preStyleNm + "Col8"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preBgtAmt")));
            }
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "Col9"));
        cell.setCellFormula("G" + (rowNum) + "-" + "I" + (rowNum));

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "Col10"));
        if (dgrLevel > 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("reflectFgNm")));
        }

        if (dgrLevel < 4) {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNum + ":$C$" + rowNum));
        } else {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + rowNum + ":$C$" + rowNum));
        }

        if (dgrLevel < 5) {
            addDataFormulaValue(reportFormulaUtil, upDgrcompoId, rowNum);
        }

        if (formulaFlag == true) {
            addDataFormulaCell(reportFormulaUtil, dgrcompoId, rowNum);
        }

        String demandCont = ReportSaveUtil.getStringValue(category.get("demandCont"));
        String examCont = ReportSaveUtil.getStringValue(category.get("examCont"));

        if (BcjisCommUtil.isNullString(demandCont) == true && BcjisCommUtil.isNullString(examCont) == true) {
            return rowNum;
        }

        String[] demandContArray = demandCont.split("\n");
        String[] examContArray = examCont.split("\n");

        int maxLength = demandContArray.length > examContArray.length ? demandContArray.length : examContArray.length;
        if (maxLength < 1) {
            return rowNum;
        }

        if (dgrLevel <= 4) {
            preStyleNm = "dataCont0";
        } else {
            preStyleNm = "dataCont1";
        }

        for (int i = 0; i < maxLength; i++) {
            row = sheet.createRow(rowNum);
            rowNum++;
            row.setHeightInPoints(rowHeight);

            cell = row.createCell(0);
            cell.setCellStyle(styles.get(preStyleNm + "Col0"));

            cell = row.createCell(1);
            cell.setCellStyle(styles.get(preStyleNm + "Col1"));
            if (i < demandContArray.length) {
                cell.setCellValue(demandContArray[i]);
            }

            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));

            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "Col3"));

            cell = row.createCell(4);
            cell.setCellStyle(styles.get(preStyleNm + "Col4"));

            cell = row.createCell(5);
            cell.setCellStyle(styles.get(preStyleNm + "Col5"));

            cell = row.createCell(6);
            cell.setCellStyle(styles.get(preStyleNm + "Col6"));

            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col7"));

            cell = row.createCell(8);
            cell.setCellStyle(styles.get(preStyleNm + "Col8"));

            cell = row.createCell(9);
            cell.setCellStyle(styles.get(preStyleNm + "Col9"));

            cell = row.createCell(10);
            cell.setCellStyle(styles.get(preStyleNm + "Col10"));
            if (i < examContArray.length) {
                cell.setCellValue(examContArray[i]);
            }

            sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + rowNum + ":$C$" + rowNum));
        }

        return rowNum;
    }

    public void addDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_I", "I" + rowNum);
    }

    public void addDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);
        reportFormulaUtil.addFormulaCell(keyStr + "_I", rowNum - 1, 8);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void dataList(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {

        int rowNum = 0;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;

        int dgrLevel = 0;

        int totDataCnt = 0;

        String title = "";
        String reportDetlCd = ReportSaveUtil.getStringValue(model.get("reportDetlCd"));
        if("091".equals(reportDetlCd)){
        	title = "공무직보수";
        }else{
        	title = "공무직보수";
        }
        
        Map param = new HashMap();
        param.put("reportCd", "0F0");
        param.put("reportDetlCd", "0F1");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));

        Map reportInfo = reportCommDAO.selectReportInfo(param);
        //boolean bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;

        boolean bgtCompoFlag = false;
        
        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);

        String sheetName = ReportSaveUtil.getStringValue(reportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet(biz)";
        }

        sheet = wb.createSheet(sheetName);
        reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeDataListHeader(param, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")).replace("§toYear§", rtnYear(ReportSaveUtil.getIntValue(model.get("fisYear")), 0)).replace("보고항목", title));

        for (int i = 0; i < categories.size(); i++) {
            category = (JSONObject) categories.get(i);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                totDataCnt = 0;
            } else if (dgrLevel > 3) {
                totDataCnt++;
            }
             
            if(dgrLevel == 0 || dgrLevel == 2 || dgrLevel == 4){ //부서랑 항목만
            	rowNum = writeDataListData(sheet, rowNum, category, styles, totDataCnt, bgtCompoFlag, reportFormulaUtil);
            }
            
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, param, wb, sheet, rowNum, styles, reportInfo, 13, 6);
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
        titleCell.setCellValue(title);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$H$1")); //총사업비 병합

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(33.75f);

        cell = row.createCell(7);
        cell.setCellValue("※ 시비 금액\n(단위 : 백만원)");
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
    
    public int writeDataListData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int totDataCnt, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil) {
    	int unit = 1000;
        float rowHeight = 34.5f;
        Row row = null;
        Cell cell = null;

        int dgrLevel = 0;
        try {
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
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
	    	cell.setCellFormula("" + "0" + "/" + unit);	//총사업비
	    }
	
	    cell = row.createCell(3);
	    cell.setCellStyle(styles.get(preStyleNm + "Col3"));
	    if (formulaFlag == false) {
	    	cell.setCellFormula("" + "0" + "/" + unit);	//기투자
	    }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
        	if("1".equals(bgtDgr)){
        		cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preBgtAmt")) + "/" + unit);
        	}else{
        		cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("preAmt")) + "/" + unit);
        	}
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("demandBgtAmt")) + "/" + unit);
        }

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("bgtAmt")) + "/" + unit);
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "Col7"));
        if (formulaFlag == false) {
        	cell.setCellValue(ReportSaveUtil.getBizListRemark(category));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        cell.setCellFormula("K" + rowNum + "+" + "L" + rowNum + "+" + "M" + rowNum + "+" + "N" + rowNum + "+" + "O" + rowNum + "+" + "P" + rowNum);
        
        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "Col9"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt1")) + "/" + unit);
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "Col10"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt2")) + "/" + unit);
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "Col11"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt3")) + "/" + unit);
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "Col12"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt4")) + "/" + unit);
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "Col13"));
        if (formulaFlag == false) {
            cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt5")) + "/" + unit);
        }
        
        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "Col14"));
        if (formulaFlag == false) {
        	cell.setCellFormula("" + ReportSaveUtil.getAmtValue(category.get("frscAmt6")) + "/" + unit);
        }
        
        cell = row.createCell(15);
        cell.setCellStyle(styles.get(preStyleNm + "Col15"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("srchVal")));

        if (formulaFlag == true) {
        	addBizDataFormulaCellList(reportFormulaUtil, rtnMergeKey(category, "dgrcompoId"), rowNum, bgtCompoFlag);
        } 
        addBizDataFormulaValueList(reportFormulaUtil, rtnMergeKey(category, "upDgrcompoId"), rowNum, bgtCompoFlag);
        
        return rowNum;
    }
    
    private String rtnMergeKey(JSONObject category, String type){
    	String rtnYear = "";
    	
    	int dgrLevel = 0;
    	String deptCd = "";
    	String teBgtCompoId = "";
        try {
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
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

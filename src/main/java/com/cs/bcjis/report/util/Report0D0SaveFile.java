package com.cs.bcjis.report.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
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

@Component("report0D0SaveFile")
public class Report0D0SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    private String officeYn = "";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {
        XSSFSheet contentsTableSheet = null;
        Map<String, CellStyle> contentsTableStyles = null;

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        bizList(model, wb, (List<Object>) model.get("bizList"));

        data(model, wb, (List<Object>) model.get("dataList"), contentsTableSheet, contentsTableStyles);

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildSheetDocument(Map model, String KeyStr, String storePath) throws Exception {

        XSSFSheet contentsTableSheet = null;
        Map contentsTableParam = null;
        Map<String, CellStyle> contentsTableStyles = null;

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        writeCover(model, wb);

        officeYn = ReportSaveUtil.getStringValue(model.get("officeYn"));
        contentsTableParam = new Hashtable();
        contentsTableParam.put("reportCd", model.get("reportCd"));
        contentsTableParam.put("reportDetlCd", "001");
        contentsTableParam.put("fisYear", model.get("fisYear"));
        contentsTableParam.put("bgtDgr", model.get("bgtDgr"));

        contentsTableStyles = reportCommDAO.getReportStyleMap(contentsTableParam, wb);

        if("N".equals(officeYn) == false){
            contentsTableSheet = writeContentsTableHeader(contentsTableParam, wb, contentsTableStyles);
        }

        bizList(model, wb, (List<Object>) model.get("bizList"));

        data(model, wb, (List<Object>) model.get("dataList"), contentsTableSheet, contentsTableStyles);

        if("N".equals(officeYn) == false){
            writeContentsTableLastSheet(contentsTableParam, wb, contentsTableSheet, contentsTableStyles);
        }
        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int writeCover(Map model, XSSFWorkbook wb) throws Exception {

        int rowNum = 0;
        XSSFSheet sheet = null;

        Map param = new HashMap();
        param.put("reportCd", model.get("reportCd"));
        param.put("reportDetlCd", "003");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));
        Map reportInfo = reportCommDAO.selectReportInfo(param);

        String reportNm = String.valueOf(reportInfo.get("reportNm"));
        if (BcjisCommUtil.isNullString(reportNm) == true) {
            reportNm = "";
        }

        String sheetName = String.valueOf(reportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet";
        }

        sheet = wb.createSheet(sheetName);

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);

        Row row = null;
        Cell cell = null;

        float rowHeight = 30.75f;

        // 1
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(8);
        cell.setCellStyle(styles.get("now"));
        cell.setCellFormula("NOW()");

        // 2
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 3
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(15);

        // 4
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(17.25f);

        // 5
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(30f);

        // 6
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 7
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 8
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 9
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(39.75f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("title0"));
        cell.setCellValue(reportNm);

        // 10
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(83.25f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("title1"));
        cell.setCellValue("문제사업 심사조서");

        // 11
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57.75f);

        // 12
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(38.25f);

        // 13
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(18f);

        rowHeight = 30f;

        // 14
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 15
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 16
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 17
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 18
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(18f);

        // 19
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(21f);

        rowHeight = 30.75f;
        // 20
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 21
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(42f);

        // 22
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(38.25f);

        // 23
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(31.50f);

        // 24
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(34.50f);

        // 25
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(46.50f);

        ReportSaveUtil.reportMerge(reportCommDAO, param, sheet);

        reportCommDAO.setReportColWidth(param, sheet);

        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(BcjisCommUtil.getShortValue(reportInfo.get("printPaperSize")));
        printSetup.setScale(BcjisCommUtil.getShortValue(reportInfo.get("printScale")));

        sheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("leftMargin")));
        sheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("rightMargin")));
        sheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("topMargin")));
        sheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("bottomMargin")));
        sheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("headerMargin")));
        sheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("footerMargin")));

        sheet.setFitToPage(true);

        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        wb.setPrintArea(wb.getSheetIndex(sheet), 0, 8, 0, rowNum - 1);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public XSSFSheet writeContentsTableHeader(Map contentsTableParam, XSSFWorkbook wb, Map<String, CellStyle> contentsTableStyles) throws Exception {
        XSSFSheet contentsTableSheet = null;
        int contentsTableRowNum = 0;

        Map contentsTableReportInfo = reportCommDAO.selectReportInfo(contentsTableParam);

        String reportNm = String.valueOf(contentsTableReportInfo.get("reportNm"));
        if (BcjisCommUtil.isNullString(reportNm) == true) {
            reportNm = "";
        }

        String sheetName = String.valueOf(contentsTableReportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet";
        }

        contentsTableSheet = wb.createSheet(sheetName);

        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        row = contentsTableSheet.createRow(contentsTableRowNum);
        contentsTableRowNum++;
        row.setHeightInPoints(54f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellValue(ReportSaveUtil.getStringValue(contentsTableReportInfo.get("reportNm")));
        titleCell.setCellStyle(contentsTableStyles.get("title"));

        row = contentsTableSheet.createRow(contentsTableRowNum);
        contentsTableRowNum++;
        row.setHeightInPoints(33f);

        int preRowSeq = -1;
        int rowSeq = -1;
        int cellSeq = -1;
        String headerCont = "";

        list = reportCommDAO.selectReportHeaderList(contentsTableParam);
        while (!list.isEmpty()) {
            map = (Map) list.remove(0);

            rowSeq = ReportSaveUtil.getIntValue(map.get("rowSeq"));
            cellSeq = ReportSaveUtil.getIntValue(map.get("cellSeq"));
            headerCont = ReportSaveUtil.getStringValue(map.get("headerCont"));
            if (rowSeq < 0) {
                throw new Exception("보고서 Header 정보 오류입니다.");
            }

            if (preRowSeq == -1 || preRowSeq != rowSeq) {
                row = contentsTableSheet.createRow(contentsTableRowNum);
                contentsTableRowNum++;
                row.setHeightInPoints(40f);
            }

            preRowSeq = rowSeq;

            cell = row.createCell(cellSeq);
            cell.setCellStyle(contentsTableStyles.get("header" + rowSeq + "Col" + cellSeq));
            if (BcjisCommUtil.isNullString(headerCont) == false) {
                cell.setCellValue(headerCont);
            }
        }

        XSSFPrintSetup printSetup = contentsTableSheet.getPrintSetup();
        printSetup.setPaperSize(BcjisCommUtil.getShortValue(contentsTableReportInfo.get("printPaperSize")));
        printSetup.setScale(BcjisCommUtil.getShortValue(contentsTableReportInfo.get("printScale")));

        contentsTableSheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("leftMargin")));
        contentsTableSheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("rightMargin")));
        contentsTableSheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("topMargin")));
        contentsTableSheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("bottomMargin")));
        contentsTableSheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("headerMargin")));
        contentsTableSheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(contentsTableReportInfo.get("footerMargin")));

        contentsTableSheet.setHorizontallyCenter(true);

        contentsTableSheet.setRepeatingRows(CellRangeAddress.valueOf((contentsTableRowNum - 1) + ":" + (contentsTableRowNum - 1)));
        contentsTableSheet.createFreezePane(0, contentsTableRowNum - 1);
        contentsTableSheet.setZoom(BcjisCommUtil.getIntValue(contentsTableReportInfo.get("zoom")));

        CTSheetView view = contentsTableSheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        ReportSaveUtil.reportMerge(reportCommDAO, contentsTableParam, contentsTableSheet);

        return contentsTableSheet;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void bizList(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {

        int rowNum = 0;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;

        int dgrLevel = 0;

        int totDataCnt = 0;
        int dataCnt = 0;
        int subCnt = 0;

        Map param = new HashMap();
        param.put("reportCd", model.get("reportCd"));
        param.put("reportDetlCd", "004");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));

        Map reportInfo = reportCommDAO.selectReportInfo(param);
        boolean bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);

        String sheetName = ReportSaveUtil.getStringValue(reportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet(biz)";
        }

        sheet = wb.createSheet(sheetName);
        reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeBizlistHeader(param, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")));

        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                totDataCnt = 0;
                dataCnt = 0;
            } else if (dgrLevel == 1) {
                // dataCnt = 0;
            } else if (dgrLevel == 2) {
                totDataCnt++;
                dataCnt++;
                subCnt = 0;
            } else if (dgrLevel > 2) {
                subCnt++;
            }

            rowNum = writeBizlistData(sheet, rowNum, category, styles, totDataCnt, dataCnt, subCnt, bgtCompoFlag, reportFormulaUtil);
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, param, wb, sheet, rowNum, styles, reportInfo, 13, 6);
            reportFormulaUtil.writeCellFormula();
            sheet = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public void data(Map model, XSSFWorkbook wb, List<Object> categories, XSSFSheet contentsTableSheet, Map<String, CellStyle> contentsTableStyles) throws Exception {
        int rowNum = 0;
        int dataCnt = 0;

        JSONObject category = null;
        int dgrLevel = 0;

        Map reportInfo = reportCommDAO.selectReportInfo(model);
        boolean bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(model, wb);

        Row row = null;
        Cell cell = null;

        XSSFSheet sheet = wb.createSheet(ReportSaveUtil.getStringValue(reportInfo.get("sheetNm")));
        rowNum = writeHeader(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")), 7);

        ReportFormulaUtil reportFormulaUtil = new ReportFormulaUtil(sheet);
        String dgrcompoId = "";

        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            dgrcompoId = ReportSaveUtil.getStringValue(category.get("dgrcompoId"));

            if (dgrLevel == 0) {

                row = sheet.createRow(rowNum);
                rowNum++;
                row.setHeightInPoints(22f);

                cell = row.createCell(0);
                cell.setCellStyle(styles.get("officeCol0"));
                cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));

                cell = row.createCell(1);
                cell.setCellStyle(styles.get("officeCol1"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + (rowNum) + ":$B$" + (rowNum)));

                cell = row.createCell(2);
                cell.setCellStyle(styles.get("officeCol2"));

                cell = row.createCell(3);
                cell.setCellStyle(styles.get("officeCol3"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + (rowNum) + ":$D$" + (rowNum)));

                cell = row.createCell(4);
                cell.setCellStyle(styles.get("officeCol4"));
                cell.setCellFormula("G" + rowNum + "-" + "C" + rowNum);

                cell = row.createCell(5);
                cell.setCellStyle(styles.get("officeCol5"));

                cell = row.createCell(6);
                cell.setCellStyle(styles.get("officeCol6"));

                cell = row.createCell(7);
                cell.setCellStyle(styles.get("officeCol7"));

                cell = row.createCell(8);
                cell.setCellStyle(styles.get("officeCol8"));

                addDataFormulaCell(reportFormulaUtil, dgrcompoId + "_0", rowNum, bgtCompoFlag);

                writeContentsTableData(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")), contentsTableSheet, contentsTableStyles);

            } else {
                dataCnt++;
                rowNum = writeData(sheet, rowNum, category, styles, dataCnt, bgtCompoFlag, reportFormulaUtil);
            }
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 8);
            reportFormulaUtil.writeCellFormula();
            sheet = null;
        }
    }

    public int writeBizlistData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int totDataCnt, int dataCnt, int subCnt, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil) {
        float rowHeight = 34.50f;
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
        } else {
            preStyleNm = "data";
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        if (dgrLevel > 1) {
            cell.setCellValue(dataCnt);
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));

        if (bgtCompoFlag == false) {
            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt0")));
            }

            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "Col3"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preFrscAmt0")));
            }
        } else {
            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preFrscAmt0")));
            }

            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "Col3"));
            cell.setCellFormula("F" + rowNum + "-" + "C" + rowNum);
        }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt0")));
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt0")));
        }

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getBizListRemark(category));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "Col7"));
        cell.setCellFormula("I" + rowNum + "+" + "J" + rowNum + "+" + "K" + rowNum + "+" + "L" + rowNum + "+" + "M" + rowNum + "+" + "N" + rowNum);

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt1")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt2")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt3")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt4")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt5")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt6")));
        }

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId, rowNum, bgtCompoFlag);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId, rowNum, bgtCompoFlag);
        }

        return rowNum;
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
        row.setHeightInPoints(32.25f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(styles.get("title"));

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(27f);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(21);

        cell = row.createCell(unitPos);
        cell.setCellStyle(styles.get("unit"));
        cell.setCellValue("(단위 : 백만원)");

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
                row.setHeightInPoints(26.25f);
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

        sheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("leftMargin")));
        sheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("rightMargin")));
        sheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("topMargin")));
        sheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("bottomMargin")));
        sheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("headerMargin")));
        sheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("footerMargin")));

        sheet.setHorizontallyCenter(true);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(0, 5);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        Footer footer = sheet.getFooter();

        footer.setCenter("" + HeaderFooter.page());

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        ReportSaveUtil.reportMerge(reportCommDAO, model, sheet);

        return rowNum;
    }

    public void writeContentsTableData(String officeNm, XSSFSheet contentsTableSheet, Map<String, CellStyle> contentsTableStyles) throws Exception {
        if (contentsTableSheet == null) {
            return;
        }

        float rowHeight = 31f;
        Row row = null;
        Cell cell = null;

        String preStyleNm = "data";

        row = contentsTableSheet.createRow(contentsTableSheet.getLastRowNum() + 1);
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col2"));
        cell.setCellValue(contentsTableSheet.getLastRowNum() - 2);

        cell = row.createCell(3);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col4"));
        cell.setCellValue(officeNm);

        cell = row.createCell(5);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col5"));
        cell.setCellValue(" -----------------------------------------------------------");

        cell = row.createCell(6);
        cell.setCellStyle(contentsTableStyles.get(preStyleNm + "Col6"));

    }

    @SuppressWarnings("rawtypes")
    public int writeBizlistHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(34.50f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(33.75f);

        cell = row.createCell(6);
        cell.setCellValue("※ 시비 금액\n(단위 백만원)");
        cell.setCellStyle(styles.get("unit"));

        repeatingStartRow = rowNum;

        float rowHeight = 51.75f;
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

    public int writeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int dataCnt, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil) throws Exception {
        float rowHeight = 17.5f;
        if (dataCnt > 4) {
            rowHeight = 18.5f;
        }

        Row row = null;
        Cell cell = null;

        String preStyleNm = "data";

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "0Col0"));
        cell.setCellValue(dataCnt);

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "0Col1"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "0Col2"));
        cell.setCellFormula("D" + (rowNum + 1) + "+" + "D" + (rowNum + 2) + "+" + "D" + (rowNum + 3) + "+" + "D" + (rowNum + 4) + "+" + "D" + (rowNum + 5) + "+" + "D" + (rowNum + 6));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "0Col3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + (rowNum) + ":$D$" + (rowNum)));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "0Col4"));
        if (bgtCompoFlag == false) {
            cell.setCellFormula("E" + (rowNum + 1) + "+" + "E" + (rowNum + 2) + "+" + "E" + (rowNum + 3) + "+" + "E" + (rowNum + 4) + "+" + "E" + (rowNum + 5) + "+" + "E" + (rowNum + 6));
        } else {
            cell.setCellFormula("G" + rowNum + "-" + "C" + rowNum);
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "0Col5"));
        cell.setCellFormula("F" + (rowNum + 1) + "+" + "F" + (rowNum + 2) + "+" + "F" + (rowNum + 3) + "+" + "F" + (rowNum + 4) + "+" + "F" + (rowNum + 5) + "+" + "F" + (rowNum + 6));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "0Col6"));
        cell.setCellFormula("G" + (rowNum + 1) + "+" + "G" + (rowNum + 2) + "+" + "G" + (rowNum + 3) + "+" + "G" + (rowNum + 4) + "+" + "G" + (rowNum + 5) + "+" + "G" + (rowNum + 6));

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "0Col7"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("reflectFgNm")));

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("teMngMokCd")));

        String upDgrcompoId = ReportSaveUtil.getStringValue(category.get("upDgrcompoId"));
        addDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_0", rowNum, bgtCompoFlag);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        String demandCont = ReportSaveUtil.getStringValue(category.get("demandCont"));
        String examCont = ReportSaveUtil.getStringValue(category.get("examCont"));

        List<String> demandConts = new ArrayList<String>(Arrays.asList(demandCont.split("\n")));
        List<String> examConts = new ArrayList<String>(Arrays.asList(examCont.split("\n")));
        List<Report0D0SaveFrscAmtsVO> frscAmts = getFrscAmts(category, bgtCompoFlag);

        String frscNm = "";
        long dmnFrscAmt = 0l;
        long preFrscAmt = 0l;
        long preDefFrscAmt = 0l;
        long frscAmt = 0l;
        Report0D0SaveFrscAmtsVO report0D0SaveFrscAmtsVO = null;
        if (!frscAmts.isEmpty()) {
            report0D0SaveFrscAmtsVO = frscAmts.remove(0);
            frscNm = report0D0SaveFrscAmtsVO.getFrscNm();
            dmnFrscAmt = report0D0SaveFrscAmtsVO.getDmnFrscAmt();
            preFrscAmt = report0D0SaveFrscAmtsVO.getPreFrscAmt();
            preDefFrscAmt = report0D0SaveFrscAmtsVO.getPreDefFrscAmt();
            frscAmt = report0D0SaveFrscAmtsVO.getFrscAmt();
        }

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "1Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "1Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "1Col2"));
        cell.setCellValue(frscNm);

        if (bgtCompoFlag == false) {
            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "1Col3"));
            cell.setCellValue(preDefFrscAmt);

            cell = row.createCell(4);
            cell.setCellStyle(styles.get(preStyleNm + "1Col4"));
            cell.setCellValue(preFrscAmt);

        } else {
            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "1Col3"));
            cell.setCellValue(preFrscAmt);

            cell = row.createCell(4);
            cell.setCellStyle(styles.get(preStyleNm + "1Col4"));
            cell.setCellFormula("G" + rowNum + "-" + "D" + rowNum);
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "1Col5"));
        cell.setCellValue(dmnFrscAmt);

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "1Col6"));
        cell.setCellValue(frscAmt);

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "1Col7"));
        if (examConts.isEmpty() == false) {
            cell.setCellValue(examConts.remove(0));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "1Col8"));

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + (rowNum - 1) + ":$A$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + (rowNum - 1) + ":$B$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$I$" + (rowNum - 1) + ":$I$" + rowNum));

        preStyleNm = "data2";
        int lineCnt = 12;
        if (dataCnt % 4 == 0) {
            lineCnt = 12 + (((rowNum + 5) % 51) <= 0 ? 0 : (51 - ((rowNum + 5) % 51)));
        }

        for (int i = 2; i < lineCnt; i++) {

            if (i == lineCnt - 1) {
                if (dataCnt % 4 == 0) {
                    preStyleNm = "dataL";
                } else {
                    preStyleNm = "data9";
                }
            }

            row = sheet.createRow(rowNum);
            rowNum++;
            row.setHeightInPoints(rowHeight);

            if (!frscAmts.isEmpty()) {
                report0D0SaveFrscAmtsVO = frscAmts.remove(0);
                frscNm = report0D0SaveFrscAmtsVO.getFrscNm();
                dmnFrscAmt = report0D0SaveFrscAmtsVO.getDmnFrscAmt();
                preFrscAmt = report0D0SaveFrscAmtsVO.getPreFrscAmt();
                preDefFrscAmt = report0D0SaveFrscAmtsVO.getPreDefFrscAmt();
                frscAmt = report0D0SaveFrscAmtsVO.getFrscAmt();
            } else {
                frscNm = "";
                dmnFrscAmt = 0l;
                preFrscAmt = 0l;
                preDefFrscAmt = 0l;
                frscAmt = 0l;
            }

            cell = row.createCell(0);
            cell.setCellStyle(styles.get(preStyleNm + "Col0"));

            cell = row.createCell(1);
            cell.setCellStyle(styles.get(preStyleNm + "Col1"));
            if (!demandConts.isEmpty()) {
                cell.setCellValue(demandConts.remove(0));
            }

            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));
            cell.setCellValue(frscNm);

            if (bgtCompoFlag == false) {
                cell = row.createCell(3);
                cell.setCellStyle(styles.get(preStyleNm + "Col3"));
                cell.setCellValue(preDefFrscAmt);

                cell = row.createCell(4);
                cell.setCellStyle(styles.get(preStyleNm + "Col4"));
                cell.setCellValue(preFrscAmt);

            } else {
                cell = row.createCell(3);
                cell.setCellStyle(styles.get(preStyleNm + "Col3"));
                cell.setCellValue(preFrscAmt);

                cell = row.createCell(4);
                cell.setCellStyle(styles.get(preStyleNm + "Col4"));
                cell.setCellFormula("G" + rowNum + "-" + "D" + rowNum);
            }

            cell = row.createCell(5);
            cell.setCellStyle(styles.get(preStyleNm + "Col5"));
            cell.setCellValue(dmnFrscAmt);

            cell = row.createCell(6);
            cell.setCellStyle(styles.get(preStyleNm + "Col6"));
            cell.setCellValue(frscAmt);

            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col7"));
            if (examConts.isEmpty() == false) {
                cell.setCellValue(examConts.remove(0));
            }

            cell = row.createCell(8);
            cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        }

        if (dataCnt % 4 == 0) {
            sheet.setRowBreak(rowNum - 1);
        }

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void writeContentsTableLastSheet(Map contentsTableParam, XSSFWorkbook wb, XSSFSheet contentsTableSheet, Map<String, CellStyle> contentsTableStyles) throws Exception {
        if (contentsTableSheet == null) {
            return;
        }

        Row row = null;
        Cell cell = null;

        row = contentsTableSheet.createRow(contentsTableSheet.getLastRowNum() + 1);
        row.setHeightInPoints(21f);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(contentsTableStyles.get("lastCol" + i));
        }

        reportCommDAO.setReportColWidth(contentsTableParam, contentsTableSheet);

        wb.setPrintArea(1, 0, 6, 0, contentsTableSheet.getLastRowNum() - 1);
    }

    public void addDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaValue(keyStr + "_C", "C" + rowNum);
        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        }
        reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);
    }

    public void addDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaCell(keyStr + "_C", rowNum - 1, 2);
        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        }
        reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);
    }

    public void addBizDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaValue(keyStr + "_C", "C" + rowNum);
        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaValue(keyStr + "_D", "D" + rowNum);
        }
        reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_I", "I" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_K", "K" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_L", "L" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_M", "M" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_N", "N" + rowNum);
    }

    public void addBizDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaCell(keyStr + "_C", rowNum - 1, 2);
        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaCell(keyStr + "_D", rowNum - 1, 3);
        }
        reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        reportFormulaUtil.addFormulaCell(keyStr + "_I", rowNum - 1, 8);
        reportFormulaUtil.addFormulaCell(keyStr + "_J", rowNum - 1, 9);
        reportFormulaUtil.addFormulaCell(keyStr + "_K", rowNum - 1, 10);
        reportFormulaUtil.addFormulaCell(keyStr + "_L", rowNum - 1, 11);
        reportFormulaUtil.addFormulaCell(keyStr + "_M", rowNum - 1, 12);
        reportFormulaUtil.addFormulaCell(keyStr + "_N", rowNum - 1, 13);
    }

    public List<Report0D0SaveFrscAmtsVO> getFrscAmts(JSONObject category, boolean bgtCompoFlag) {
        List<Report0D0SaveFrscAmtsVO> list = new ArrayList<Report0D0SaveFrscAmtsVO>();

        long dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt1"));
        long preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt1"));
        long preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt1"));
        long frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt1"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("시", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("시", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }

        dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt2"));
        preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt2"));
        preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt2"));
        frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt2"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("국", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("국", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }

        dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt3"));
        preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt3"));
        preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt3"));
        frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt3"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("교", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("교", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }

        dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt4"));
        preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt4"));
        preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt4"));
        frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt4"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("지", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("지", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }

        dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt5"));
        preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt5"));
        preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt5"));
        frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt5"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("채", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("채", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }
        dmnFrscAmt = ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt6"));
        preFrscAmt = ReportSaveUtil.getAmtValue(category.get("preFrscAmt6"));
        preDefFrscAmt = ReportSaveUtil.getAmtValue(category.get("preDefFrscAmt6"));
        frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt6"));

        if (bgtCompoFlag == false) {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || preDefFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("기", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        } else {
            if (dmnFrscAmt != 0 || preFrscAmt != 0 || frscAmt != 0) {
                list.add(new Report0D0SaveFrscAmtsVO("기", dmnFrscAmt, preFrscAmt, preDefFrscAmt, frscAmt));
            }
        }

        return list;
    }
}

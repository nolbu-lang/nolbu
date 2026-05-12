package com.cs.bcjis.pledge.util;

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
import com.cs.bcjis.report.util.Report000SaveFile;
import com.cs.bcjis.report.util.ReportFormulaUtil;
import com.cs.bcjis.report.util.ReportSaveUtil;

@Component("pledgeReportSaveSheet3File")
public class PledgeReportSaveSheet3File {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "report000SaveFile")
    Report000SaveFile report000SaveFile;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildSheetDocument(Map model, String KeyStr, String storePath) throws Exception {
        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        writeCover(model, wb);

        model.put("reportDetlCd", "P32");
        Map reportInfo = reportCommDAO.selectReportInfo(model);
        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(model, wb);

        officeData(model, wb, (List<Object>) model.get("officeList"), reportInfo, styles, "실국현황(전체)", "실·본부·국별");
        officeData(model, wb, (List<Object>) model.get("officeList100"), reportInfo, styles, "실국현황(일반)", "일반회계 실·본부·국별");
        officeData(model, wb, (List<Object>) model.get("officeListEtc"), reportInfo, styles, "실국현황(기타특별)", "기타특별회계 실·본부·국별");

        model.put("reportDetlCd", "P33");
        reportInfo = reportCommDAO.selectReportInfo(model);
        styles = reportCommDAO.getReportStyleMap(model, wb);

        bizData(model, wb, (List<Object>) model.get("bizList"), reportInfo, styles, "공약사업별(전체)");
        bizData(model, wb, (List<Object>) model.get("bizList100"), reportInfo, styles, "공약사업별(일반)");
        bizData(model, wb, (List<Object>) model.get("bizListEtc"), reportInfo, styles, "공약사업별(기타특별)");

        model.put("reportDetlCd", "P34");
        reportInfo = reportCommDAO.selectReportInfo(model);
        styles = reportCommDAO.getReportStyleMap(model, wb);

        deptData(model, wb, (List<Object>) model.get("deptList"), reportInfo, styles, "부서별(전체)");
        deptData(model, wb, (List<Object>) model.get("deptList100"), reportInfo, styles, "부서별(일반)");
        deptData(model, wb, (List<Object>) model.get("deptListEtc"), reportInfo, styles, "부서별(기타특별)");
        
        model.put("reportDetlCd", "P34");
        reportInfo = reportCommDAO.selectReportInfo(model);
        styles = reportCommDAO.getReportStyleMap(model, wb);

        etcData(model, wb, (List<Object>) model.get("etcList"), reportInfo, styles, "회계별, 부서별(기타특별)");

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

        model.put("reportDetlCd", "P31");
        Map reportInfo = reportCommDAO.selectReportInfo(model);

        String pledgeInfNm = ReportSaveUtil.getStringValue(model.get("pledgeInfNm"));
        if (BcjisCommUtil.isNullString(pledgeInfNm) == true) {
            pledgeInfNm = "";
        }

        String reportNm = ReportSaveUtil.getStringValue(reportInfo.get("reportNm"));
        if (BcjisCommUtil.isNullString(reportNm) == true) {
            reportNm = "";
        }

        String sheetName = String.valueOf(reportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet";
        }

        sheet = wb.createSheet(sheetName);

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(model, wb);

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
        row.setHeightInPoints(43.50f);

        // 4
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 5
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

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
        row.setHeightInPoints(14.25f);

        // 9
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("title0"));
        cell.setCellValue(reportNm);

        // 10
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57.75f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("title1"));
        cell.setCellValue(pledgeInfNm + " 공약사업 예산현황");

        // 11
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(38.25f);

        // 12
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(18f);

        rowHeight = 30f;
        // 13
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

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
        row.setHeightInPoints(rowHeight);

        // 19
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(30.75f);

        rowHeight = 31.50f;
        // 20
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 21
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 22
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 23
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 24
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("footer0"));
        cell.setCellValue("부산광역시");

        // 25
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(35.25f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("footer1"));
        cell.setCellValue("(예산담당관실)");

        // 26
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(22.50f);

        rowHeight = 14.25f;

        // 27
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 28
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 29
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        // 30
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        reportMerge(model, sheet);

        reportCommDAO.setReportColWidth(model, sheet);

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
    public void officeData(Map model, XSSFWorkbook wb, List<Object> categories, Map reportInfo, Map<String, CellStyle> styles, String sheetNm, String title) throws Exception {
        int officeDataCnt = 0;
        int officeCnt = 0;
        int officeTotDataCnt = 1;

        int rowNum = 0;
        int dgrLevel = 0;
        String preStyleNm = "";
        XSSFSheet sheet = null;

        JSONObject category = null;

        String reportNm = String.valueOf(reportInfo.get("reportNm"));
        if (BcjisCommUtil.isNullString(reportNm) == true) {
            reportNm = "";
        }

        sheet = wb.createSheet(sheetNm);
        ReportFormulaUtil reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeOfficeHeader(model, sheet, rowNum, styles, reportInfo, reportNm + " " + title);

        category = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            dgrLevel = 0;
            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                preStyleNm = "tot";
            } else {
                if (officeDataCnt < 1) {
                    preStyleNm = "data0";
                } else {
                    preStyleNm = "data1";
                }
                officeDataCnt++;
            }

            officeCnt = 0;
            try {
                officeCnt = Integer.parseInt(String.valueOf(category.get("officeCnt")));
            } catch (NumberFormatException nfe) {
                officeCnt = 0;
            }

            rowNum = writeOfficeData(sheet, rowNum, category, styles, officeTotDataCnt, officeCnt, officeDataCnt, preStyleNm, reportFormulaUtil);
            if (dgrLevel > 0) {
                officeTotDataCnt = officeTotDataCnt + officeCnt;
            }
        }

        writeLastSheet(model, wb, sheet, rowNum, styles, reportInfo, 11);
        reportFormulaUtil.writeCellFormula();
    }

    @SuppressWarnings("rawtypes")
    public int writeOfficeHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(13.50f);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(42.00f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(23.25f);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get("now"));
        cell.setCellFormula("NOW()");

        cell = row.createCell(9);
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
                if (rowSeq == 0) {
                    row.setHeightInPoints(23.25f);
                } else {
                    row.setHeightInPoints(21f);
                }
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
        sheet.createFreezePane(0, 5);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(false);

        Footer footer = sheet.getFooter();

        footer.setCenter("- " + HeaderFooter.page() + " -");

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        reportMerge(model, sheet);

        return rowNum;
    }

    public int writeOfficeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int officeTotDataCnt, int officeCnt, int officeDataCnt, String preStyleNm, ReportFormulaUtil reportFormulaUtil) {
        float rowHeight = 35f;
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
        if (dgrLevel != 0) {
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));

        if (dgrLevel == 0) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));
        } else {
            cell.setCellValue(officeDataCnt);
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        if (dgrLevel > 0) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "Col2"));
        if (formulaFlag == false) {
            cell.setCellValue(officeCnt);
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        if (formulaFlag == false) {
            cell.setCellFormula("E" + (rowNum) + "+" + "F" + (rowNum) + "+" + "G" + (rowNum) + "+" + "H" + (rowNum) + "+" + "I" + (rowNum));
        }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt0")));
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt0")));
        }

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt0")));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt0")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt0")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt0")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt0")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "Col11"));

        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_C", "C" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_D", "D" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_G", "G" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_H", "H" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_I", "I" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(upDgrcompoId + "_K", "K" + rowNum);
        if (formulaFlag == true) {
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_C", rowNum - 1, 2);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_D", rowNum - 1, 3);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_E", rowNum - 1, 4);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_F", rowNum - 1, 5);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_G", rowNum - 1, 6);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_H", rowNum - 1, 7);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_I", rowNum - 1, 8);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_J", rowNum - 1, 9);
            reportFormulaUtil.addFormulaCell(dgrcompoId + "_K", rowNum - 1, 10);
        }

        if (dgrLevel == 0) {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNum + ":$B$" + rowNum));
        }

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void bizData(Map model, XSSFWorkbook wb, List<Object> categories, Map reportInfo, Map<String, CellStyle> styles, String sheetNm) throws Exception {
        int rowNum = 0;
        int totDataCnt = 0;
        int sheetDataCnt = 0;
        int dgrLevel = 0;
        String preStyleNm = "";
        XSSFSheet sheet = null;

        JSONObject category = null;

        if (BcjisCommUtil.isNullString(sheetNm) == true) {
            sheetNm = "new sheet";
        }

        sheet = wb.createSheet(sheetNm);
        ReportFormulaUtil reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeBizHeader(model, sheet, rowNum, styles, reportInfo, sheetNm);

        category = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            dgrLevel = 0;
            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                preStyleNm = "tot";
                sheetDataCnt = 0;
                totDataCnt = 0;
            } else if (dgrLevel < 4) {
                preStyleNm = "lv" + dgrLevel;
            } else {
                preStyleNm = "data";
                totDataCnt++;
            }

            rowNum = writeBizData(sheet, rowNum, category, styles, sheetDataCnt, totDataCnt, preStyleNm, reportFormulaUtil);
            sheetDataCnt++;
        }

        writeLastSheet(model, wb, sheet, rowNum, styles, reportInfo, 14);
        reportFormulaUtil.writeCellFormula();
    }

    @SuppressWarnings("rawtypes")
    public int writeBizHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57.0f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        cell = row.createCell(12);
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
                if (rowSeq == 0) {
                    row.setHeightInPoints(26.25f);
                } else {
                    row.setHeightInPoints(33.75f);
                }
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
        sheet.createFreezePane(0, 3);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(false);

        Footer footer = sheet.getFooter();

        footer.setCenter("- " + HeaderFooter.page() + " -");

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        reportMerge(model, sheet);

        return rowNum;
    }

    public int writeBizData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int sheetDataCnt, int totDataCnt, String preStyleNm, ReportFormulaUtil reportFormulaUtil) {
        int startRowNum = rowNum + 1;

        float rowHeight = 16.5f;
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
        String dgrcompoNm = ReportSaveUtil.getStringValue(category.get("dgrcompoNm"));
        int childCnt = ReportSaveUtil.getIntValue(category.get("childCnt"));

        boolean formulaFlag = true;
        if (dgrLevel > 3) {
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "0Col0"));
        if (dgrLevel > 3) {
            cell.setCellValue(totDataCnt);
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "0Col1"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "0Col2"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("deptNm")));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "0Col3"));
        if (dgrLevel < 4) {
            cell.setCellValue(dgrcompoNm + "\n(" + childCnt + "건)");
        } else {
            cell.setCellValue(dgrcompoNm);
        }

        // 계
        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "0Col4"));
        cell.setCellValue("계");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "0Col5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + rowNum + ":$F$" + rowNum));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("G" + (rowNum + 1) + "+" + "G" + (rowNum + 2) + "+" + "G" + (rowNum + 3) + "+" + "G" + (rowNum + 4) + "+" + "G" + (rowNum + 5) + "+" + "G" + (rowNum + 6));

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "0Col7"));
        cell.setCellFormula("H" + (rowNum + 1) + "+" + "H" + (rowNum + 2) + "+" + "H" + (rowNum + 3) + "+" + "H" + (rowNum + 4) + "+" + "H" + (rowNum + 5) + "+" + "H" + (rowNum + 6));

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("I" + (rowNum + 1) + "+" + "I" + (rowNum + 2) + "+" + "I" + (rowNum + 3) + "+" + "I" + (rowNum + 4) + "+" + "I" + (rowNum + 5) + "+" + "I" + (rowNum + 6));

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "0Col9"));
        cell.setCellFormula("J" + (rowNum + 1) + "+" + "J" + (rowNum + 2) + "+" + "J" + (rowNum + 3) + "+" + "J" + (rowNum + 4) + "+" + "J" + (rowNum + 5) + "+" + "J" + (rowNum + 6));

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "0Col10"));
        cell.setCellFormula("K" + (rowNum + 1) + "+" + "K" + (rowNum + 2) + "+" + "K" + (rowNum + 3) + "+" + "K" + (rowNum + 4) + "+" + "K" + (rowNum + 5) + "+" + "K" + (rowNum + 6));

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "0Col11"));
        cell.setCellFormula("L" + (rowNum + 1) + "+" + "L" + (rowNum + 2) + "+" + "L" + (rowNum + 3) + "+" + "L" + (rowNum + 4) + "+" + "L" + (rowNum + 5) + "+" + "L" + (rowNum + 6));

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "0Col12"));
        cell.setCellFormula("M" + (rowNum + 1) + "+" + "M" + (rowNum + 2) + "+" + "M" + (rowNum + 3) + "+" + "M" + (rowNum + 4) + "+" + "M" + (rowNum + 5) + "+" + "M" + (rowNum + 6));

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "0Col13"));
        cell.setCellFormula("N" + (rowNum + 1) + "+" + "N" + (rowNum + 2) + "+" + "N" + (rowNum + 3) + "+" + "N" + (rowNum + 4) + "+" + "N" + (rowNum + 5) + "+" + "N" + (rowNum + 6));

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "0Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        // 시비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "1Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "1Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "1Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "1Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "1Col4"));
        cell.setCellValue("재\n원\n별");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "1Col5"));
        cell.setCellValue("시비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "1Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "1Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt1")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "1Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt1")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "1Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt1")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "1Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt1")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "1Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt1")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "1Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt1")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "1Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt1")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "1Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_1", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_1", rowNum);
        }

        // 국비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "2Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "2Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "2Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "2Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "2Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "2Col5"));
        cell.setCellValue("국비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "2Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "2Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt2")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "2Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt2")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "2Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt2")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "2Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt2")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "2Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt2")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "2Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt2")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "2Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt2")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "2Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_2", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_2", rowNum);
        }

        // 교부세
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "3Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "3Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "3Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "3Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "3Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "3Col5"));
        cell.setCellValue("교부세");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "3Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "3Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt3")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "3Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt3")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "3Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt3")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "3Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt3")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "3Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt3")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "3Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt3")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "3Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt3")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "3Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_3", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_3", rowNum);
        }

        // 지방채
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "4Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "4Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "4Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "4Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "4Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "4Col5"));
        cell.setCellValue("지방채");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "4Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "4Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt4")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "4Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt4")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "4Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt4")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "4Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt4")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "4Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt4")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "4Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt4")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "4Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt4")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "4Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_4", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_4", rowNum);
        }

        // 채무
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "5Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "5Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "5Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "5Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "5Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "5Col5"));
        cell.setCellValue("채무");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "5Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "5Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt5")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "5Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt5")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "5Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt5")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "5Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt5")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "5Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt5")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "5Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt5")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "5Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt5")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "5Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_5", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_5", rowNum);
        }

        // 기타
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        String colLast = "6";
        if (sheetDataCnt % 8 == 7) {
            colLast = "L";
        }

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col5"));
        cell.setCellValue("기타");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt6")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt6")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt6")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt6")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt6")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt6")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt6")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_6", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_6", rowNum);
        }

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + startRowNum + ":$A$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + startRowNum + ":$B$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + startRowNum + ":$C$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$" + startRowNum + ":$D$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + (startRowNum + 1) + ":$E$" + (startRowNum + 1 + 5)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$O$" + startRowNum + ":$O$" + rowNum));

        if (sheetDataCnt % 8 == 7) {
            sheet.setRowBreak(rowNum - 1);
        }

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void deptData(Map model, XSSFWorkbook wb, List<Object> categories, Map reportInfo, Map<String, CellStyle> styles, String sheetNm) throws Exception {
        int rowNum = 0;
        int totDataCnt = 0;
        int sheetDataCnt = 0;
        int dgrLevel = 0;
        String preStyleNm = "";
        XSSFSheet sheet = null;

        JSONObject category = null;

        if (BcjisCommUtil.isNullString(sheetNm) == true) {
            sheetNm = "new sheet";
        }

        sheet = wb.createSheet(sheetNm);
        ReportFormulaUtil reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeDeptHeader(model, sheet, rowNum, styles, reportInfo, sheetNm);

        category = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            dgrLevel = 0;
            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                preStyleNm = "tot";
                sheetDataCnt = 0;
                totDataCnt = 0;
            } else if (dgrLevel == 1) {
                preStyleNm = "office";
            } else {
                preStyleNm = "data";
                totDataCnt++;
            }

            rowNum = writeDeptData(sheet, rowNum, category, styles, sheetDataCnt, totDataCnt, preStyleNm, reportFormulaUtil);
            sheetDataCnt++;
        }

        writeLastSheet(model, wb, sheet, rowNum, styles, reportInfo, 14);
        reportFormulaUtil.writeCellFormula();
    }

    @SuppressWarnings("rawtypes")
    public int writeDeptHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57.0f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        cell = row.createCell(12);
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
                if (rowSeq == 0) {
                    row.setHeightInPoints(26.25f);
                } else {
                    row.setHeightInPoints(33.75f);
                }
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
        sheet.createFreezePane(0, 3);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(false);

        Footer footer = sheet.getFooter();

        footer.setCenter("- " + HeaderFooter.page() + " -");

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        reportMerge(model, sheet);

        return rowNum;
    }

    public int writeDeptData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int sheetDataCnt, int totDataCnt, String preStyleNm, ReportFormulaUtil reportFormulaUtil) {
        int startRowNum = rowNum + 1;

        float rowHeight = 16.5f;
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
        String dgrcompoNm = ReportSaveUtil.getStringValue(category.get("dgrcompoNm"));
        int childCnt = ReportSaveUtil.getIntValue(category.get("childCnt"));

        boolean formulaFlag = true;
        if (dgrLevel > 1) {
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "0Col0"));
        if (dgrLevel > 1) {
            cell.setCellValue(totDataCnt);
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "0Col1"));
        if (dgrLevel > 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("deptNm")));
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "0Col2"));
        if (dgrLevel > 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "0Col3"));
        if (dgrLevel < 2) {
            cell.setCellValue(dgrcompoNm + "\n(" + childCnt + "건)");
        } else {
            cell.setCellValue(dgrcompoNm);
        }

        // 계
        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "0Col4"));
        cell.setCellValue("계");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "0Col5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + rowNum + ":$F$" + rowNum));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("G" + (rowNum + 1) + "+" + "G" + (rowNum + 2) + "+" + "G" + (rowNum + 3) + "+" + "G" + (rowNum + 4) + "+" + "G" + (rowNum + 5) + "+" + "G" + (rowNum + 6));

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "0Col7"));
        cell.setCellFormula("H" + (rowNum + 1) + "+" + "H" + (rowNum + 2) + "+" + "H" + (rowNum + 3) + "+" + "H" + (rowNum + 4) + "+" + "H" + (rowNum + 5) + "+" + "H" + (rowNum + 6));

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("I" + (rowNum + 1) + "+" + "I" + (rowNum + 2) + "+" + "I" + (rowNum + 3) + "+" + "I" + (rowNum + 4) + "+" + "I" + (rowNum + 5) + "+" + "I" + (rowNum + 6));

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "0Col9"));
        cell.setCellFormula("J" + (rowNum + 1) + "+" + "J" + (rowNum + 2) + "+" + "J" + (rowNum + 3) + "+" + "J" + (rowNum + 4) + "+" + "J" + (rowNum + 5) + "+" + "J" + (rowNum + 6));

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "0Col10"));
        cell.setCellFormula("K" + (rowNum + 1) + "+" + "K" + (rowNum + 2) + "+" + "K" + (rowNum + 3) + "+" + "K" + (rowNum + 4) + "+" + "K" + (rowNum + 5) + "+" + "K" + (rowNum + 6));

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "0Col11"));
        cell.setCellFormula("L" + (rowNum + 1) + "+" + "L" + (rowNum + 2) + "+" + "L" + (rowNum + 3) + "+" + "L" + (rowNum + 4) + "+" + "L" + (rowNum + 5) + "+" + "L" + (rowNum + 6));

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "0Col12"));
        cell.setCellFormula("M" + (rowNum + 1) + "+" + "M" + (rowNum + 2) + "+" + "M" + (rowNum + 3) + "+" + "M" + (rowNum + 4) + "+" + "M" + (rowNum + 5) + "+" + "M" + (rowNum + 6));

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "0Col13"));
        cell.setCellFormula("N" + (rowNum + 1) + "+" + "N" + (rowNum + 2) + "+" + "N" + (rowNum + 3) + "+" + "N" + (rowNum + 4) + "+" + "N" + (rowNum + 5) + "+" + "N" + (rowNum + 6));

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "0Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        // 시비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "1Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "1Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "1Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "1Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "1Col4"));
        cell.setCellValue("재\n원\n별");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "1Col5"));
        cell.setCellValue("시비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "1Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "1Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt1")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "1Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt1")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "1Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt1")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "1Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt1")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "1Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt1")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "1Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt1")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "1Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt1")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "1Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_1", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_1", rowNum);
        }

        // 국비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "2Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "2Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "2Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "2Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "2Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "2Col5"));
        cell.setCellValue("국비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "2Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "2Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt2")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "2Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt2")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "2Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt2")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "2Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt2")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "2Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt2")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "2Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt2")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "2Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt2")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "2Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_2", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_2", rowNum);
        }

        // 교부세
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "3Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "3Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "3Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "3Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "3Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "3Col5"));
        cell.setCellValue("교부세");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "3Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "3Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt3")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "3Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt3")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "3Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt3")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "3Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt3")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "3Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt3")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "3Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt3")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "3Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt3")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "3Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_3", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_3", rowNum);
        }

        // 지방채
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "4Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "4Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "4Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "4Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "4Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "4Col5"));
        cell.setCellValue("지방채");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "4Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "4Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt4")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "4Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt4")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "4Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt4")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "4Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt4")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "4Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt4")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "4Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt4")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "4Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt4")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "4Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_4", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_4", rowNum);
        }

        // 채무
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "5Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "5Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "5Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "5Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "5Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "5Col5"));
        cell.setCellValue("채무");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "5Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "5Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt5")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "5Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt5")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "5Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt5")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "5Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt5")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "5Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt5")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "5Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt5")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "5Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt5")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "5Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_5", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_5", rowNum);
        }

        // 기타
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        String colLast = "6";
        if (sheetDataCnt % 8 == 7) {
            colLast = "L";
        }

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col5"));
        cell.setCellValue("기타");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt6")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt6")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt6")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt6")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt6")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt6")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt6")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_6", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_6", rowNum);
        }

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + startRowNum + ":$A$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + startRowNum + ":$B$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + startRowNum + ":$C$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$" + startRowNum + ":$D$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + (startRowNum + 1) + ":$E$" + (startRowNum + 1 + 5)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$O$" + startRowNum + ":$O$" + rowNum));

        if (sheetDataCnt % 8 == 7) {
            sheet.setRowBreak(rowNum - 1);
        }

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void etcData(Map model, XSSFWorkbook wb, List<Object> categories, Map reportInfo, Map<String, CellStyle> styles, String sheetNm) throws Exception {
        int rowNum = 0;
        int totDataCnt = 0;
        int sheetDataCnt = 0;
        int dgrLevel = 0;
        String preStyleNm = "";
        XSSFSheet sheet = null;

        JSONObject category = null;

        if (BcjisCommUtil.isNullString(sheetNm) == true) {
            sheetNm = "new sheet";
        }

        sheet = wb.createSheet(sheetNm);
        ReportFormulaUtil reportFormulaUtil = new ReportFormulaUtil(sheet);

        rowNum = writeEtcHeader(model, sheet, rowNum, styles, reportInfo, sheetNm);

        category = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            dgrLevel = 0;
            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (dgrLevel == 0) {
                preStyleNm = "tot";
                sheetDataCnt = 0;
                totDataCnt = 0;
            } else if (dgrLevel == 1) {
                preStyleNm = "office";
            } else {
                preStyleNm = "data";
                totDataCnt++;
            }

            rowNum = writeEtcData(sheet, rowNum, category, styles, sheetDataCnt, totDataCnt, preStyleNm, reportFormulaUtil);
            sheetDataCnt++;
        }

        writeLastSheet(model, wb, sheet, rowNum, styles, reportInfo, 14);
        reportFormulaUtil.writeCellFormula();
    }

    @SuppressWarnings("rawtypes")
    public int writeEtcHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(57.0f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        cell = row.createCell(12);
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
                if (rowSeq == 0) {
                    row.setHeightInPoints(26.25f);
                } else {
                    row.setHeightInPoints(33.75f);
                }
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
        sheet.createFreezePane(0, 3);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(false);

        Footer footer = sheet.getFooter();

        footer.setCenter("- " + HeaderFooter.page() + " -");

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        reportMerge(model, sheet);

        return rowNum;
    }

    public int writeEtcData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int sheetDataCnt, int totDataCnt, String preStyleNm, ReportFormulaUtil reportFormulaUtil) {
        int startRowNum = rowNum + 1;

        float rowHeight = 16.5f;
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
        String dgrcompoNm = ReportSaveUtil.getStringValue(category.get("dgrcompoNm"));
        int childCnt = ReportSaveUtil.getIntValue(category.get("childCnt"));

        boolean formulaFlag = true;
        if (dgrLevel > 1) {
            formulaFlag = false;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "0Col0"));
        if (dgrLevel > 1) {
            cell.setCellValue(totDataCnt);
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "0Col1"));
        if (dgrLevel > 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("deptNm")));
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "0Col2"));
        if (dgrLevel > 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "0Col3"));
        if (dgrLevel == 0) {
            cell.setCellValue(dgrcompoNm + "\n합            계\n(" + childCnt + "건)");
        }else if (dgrLevel == 1) {
            cell.setCellValue(dgrcompoNm + "\n(" + childCnt + "건)");
        } else {
            cell.setCellValue(dgrcompoNm);
        }

        // 계
        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "0Col4"));
        cell.setCellValue("계");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "0Col5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + rowNum + ":$F$" + rowNum));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("G" + (rowNum + 1) + "+" + "G" + (rowNum + 2) + "+" + "G" + (rowNum + 3) + "+" + "G" + (rowNum + 4) + "+" + "G" + (rowNum + 5) + "+" + "G" + (rowNum + 6));

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "0Col7"));
        cell.setCellFormula("H" + (rowNum + 1) + "+" + "H" + (rowNum + 2) + "+" + "H" + (rowNum + 3) + "+" + "H" + (rowNum + 4) + "+" + "H" + (rowNum + 5) + "+" + "H" + (rowNum + 6));

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "0Col8"));
        cell.setCellFormula("I" + (rowNum + 1) + "+" + "I" + (rowNum + 2) + "+" + "I" + (rowNum + 3) + "+" + "I" + (rowNum + 4) + "+" + "I" + (rowNum + 5) + "+" + "I" + (rowNum + 6));

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "0Col9"));
        cell.setCellFormula("J" + (rowNum + 1) + "+" + "J" + (rowNum + 2) + "+" + "J" + (rowNum + 3) + "+" + "J" + (rowNum + 4) + "+" + "J" + (rowNum + 5) + "+" + "J" + (rowNum + 6));

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "0Col10"));
        cell.setCellFormula("K" + (rowNum + 1) + "+" + "K" + (rowNum + 2) + "+" + "K" + (rowNum + 3) + "+" + "K" + (rowNum + 4) + "+" + "K" + (rowNum + 5) + "+" + "K" + (rowNum + 6));

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "0Col11"));
        cell.setCellFormula("L" + (rowNum + 1) + "+" + "L" + (rowNum + 2) + "+" + "L" + (rowNum + 3) + "+" + "L" + (rowNum + 4) + "+" + "L" + (rowNum + 5) + "+" + "L" + (rowNum + 6));

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "0Col12"));
        cell.setCellFormula("M" + (rowNum + 1) + "+" + "M" + (rowNum + 2) + "+" + "M" + (rowNum + 3) + "+" + "M" + (rowNum + 4) + "+" + "M" + (rowNum + 5) + "+" + "M" + (rowNum + 6));

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "0Col13"));
        cell.setCellFormula("N" + (rowNum + 1) + "+" + "N" + (rowNum + 2) + "+" + "N" + (rowNum + 3) + "+" + "N" + (rowNum + 4) + "+" + "N" + (rowNum + 5) + "+" + "N" + (rowNum + 6));

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "0Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        // 시비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "1Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "1Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "1Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "1Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "1Col4"));
        cell.setCellValue("재\n원\n별");

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "1Col5"));
        cell.setCellValue("시비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "1Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "1Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt1")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "1Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt1")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "1Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt1")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "1Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt1")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "1Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt1")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "1Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt1")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "1Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt1")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "1Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_1", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_1", rowNum);
        }

        // 국비
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "2Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "2Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "2Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "2Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "2Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "2Col5"));
        cell.setCellValue("국비");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "2Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "2Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt2")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "2Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt2")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "2Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt2")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "2Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt2")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "2Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt2")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "2Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt2")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "2Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt2")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "2Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_2", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_2", rowNum);
        }

        // 교부세
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "3Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "3Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "3Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "3Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "3Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "3Col5"));
        cell.setCellValue("교부세");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "3Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "3Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt3")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "3Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt3")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "3Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt3")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "3Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt3")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "3Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt3")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "3Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt3")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "3Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt3")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "3Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_3", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_3", rowNum);
        }

        // 지방채
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "4Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "4Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "4Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "4Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "4Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "4Col5"));
        cell.setCellValue("지방채");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "4Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "4Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt4")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "4Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt4")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "4Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt4")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "4Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt4")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "4Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt4")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "4Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt4")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "4Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt4")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "4Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_4", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_4", rowNum);
        }

        // 채무
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "5Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "5Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "5Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "5Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "5Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "5Col5"));
        cell.setCellValue("채무");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "5Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "5Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt5")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "5Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt5")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + "5Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt5")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + "5Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt5")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + "5Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt5")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + "5Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt5")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + "5Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt5")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + "5Col14"));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_5", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_5", rowNum);
        }

        // 기타
        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        String colLast = "6";
        if (sheetDataCnt % 8 == 7) {
            colLast = "L";
        }

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col0"));

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col1"));

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col2"));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col3"));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col4"));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col5"));
        cell.setCellValue("기타");

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col6"));
        if (formulaFlag == false) {
            cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
        }

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col7"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year1FrscAmt6")));
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col8"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year2FrscAmt6")));
        }

        cell = row.createCell(9);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col9"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year3FrscAmt6")));
        }

        cell = row.createCell(10);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col10"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year4FrscAmt6")));
        }

        cell = row.createCell(11);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col11"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("year5FrscAmt6")));
        }

        cell = row.createCell(12);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col12"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("dmnFrscAmt6")));
        }

        cell = row.createCell(13);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col13"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("frscAmt6")));
        }

        cell = row.createCell(14);
        cell.setCellStyle(styles.get(preStyleNm + colLast + "Col14"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("examCont")));

        addBizDataFormulaValue(reportFormulaUtil, upDgrcompoId + "_6", rowNum);
        if (formulaFlag == true) {
            addBizDataFormulaCell(reportFormulaUtil, dgrcompoId + "_6", rowNum);
        }

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + startRowNum + ":$A$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + startRowNum + ":$B$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$C$" + startRowNum + ":$C$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$" + startRowNum + ":$D$" + rowNum));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$E$" + (startRowNum + 1) + ":$E$" + (startRowNum + 1 + 5)));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$O$" + startRowNum + ":$O$" + rowNum));

        if (sheetDataCnt % 8 == 7) {
            sheet.setRowBreak(rowNum - 1);
        }

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public int writeLastSheet(Map model, XSSFWorkbook wb, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, int cellCnt) throws Exception {
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(21f);
        for (int i = 0; i <= cellCnt; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get("lastCol" + i));
        }

        reportCommDAO.setReportColWidth(model, sheet);

        wb.setPrintArea(wb.getSheetIndex(sheet), 0, cellCnt, 0, rowNum - 1);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void reportMerge(Map model, XSSFSheet sheet) throws Exception {
        String mergeVal = "";
        List list = null;
        Map map = null;

        list = reportCommDAO.selectReportMergeList(model);
        while (!list.isEmpty()) {
            map = (Map) list.remove(0);

            mergeVal = ReportSaveUtil.getStringValue(map.get("mergeVal"));
            if (BcjisCommUtil.isNullString(mergeVal) == false) {
                sheet.addMergedRegion(CellRangeAddress.valueOf(mergeVal));
            }
        }
    }

    public void addBizDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_H", "H" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_I", "I" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_K", "K" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_L", "L" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_M", "M" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_N", "N" + rowNum);
    }

    public void addBizDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);
        reportFormulaUtil.addFormulaCell(keyStr + "_H", rowNum - 1, 7);
        reportFormulaUtil.addFormulaCell(keyStr + "_I", rowNum - 1, 8);
        reportFormulaUtil.addFormulaCell(keyStr + "_J", rowNum - 1, 9);
        reportFormulaUtil.addFormulaCell(keyStr + "_K", rowNum - 1, 10);
        reportFormulaUtil.addFormulaCell(keyStr + "_L", rowNum - 1, 11);
        reportFormulaUtil.addFormulaCell(keyStr + "_M", rowNum - 1, 12);
        reportFormulaUtil.addFormulaCell(keyStr + "_N", rowNum - 1, 13);
    }
}

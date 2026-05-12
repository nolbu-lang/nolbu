package com.cs.bcjis.report.util;

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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;
import com.cs.bcjis.report.service.impl.ReportWrite001DAO;

@Component("report001SaveFile")
public class Report001SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "reportWrite001DAO")
    private ReportWrite001DAO reportWrite001DAO;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildSheetDocument(Map model, String KeyStr, String storePath) throws Exception {
        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        data(model, wb, (List<Object>) model.get("dataList"));
        data(model, wb, (List<Object>) model.get("totList"));
        data2(model, wb, (List<Object>) model.get("totList2"));
         
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
        int deptFlag = 0;

        String sheetCd = "";
        Map reportInfo = null;

        Map<String, CellStyle> styles = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            sheetCd = ReportSaveUtil.getStringValue(category.get("sheetCd"));
            if (dgrLevel == 0) {
                if (sheet != null) {
                    ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 19);
                    reportFormulaUtil.writeCellFormula();
                    
                    sheet = null;
                }

                rowNum = 0;

                model.put("reportDetlCd", sheetCd);
                reportInfo = reportCommDAO.selectReportInfo(model);
                styles = reportCommDAO.getReportStyleMap(model, wb);
                sheet = wb.createSheet(ReportSaveUtil.getStringValue(reportInfo.get("sheetNm")));
                reportFormulaUtil = new ReportFormulaUtil(sheet);

                rowNum = writeHeader(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")), 19);

            }

            rowNum = writeData(sheet, rowNum, category, styles, deptFlag, reportFormulaUtil);
            if (dgrLevel > 1) {
                deptFlag = 1;
            } else {
                deptFlag = 0;
            }

        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 19);
            reportFormulaUtil.writeCellFormula();
            
            sheet = null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void data2(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {
        int rowNum = 0;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;
        int dgrLevel = 0;
        int deptFlag = 0;

        String sheetCd = "";
        Map reportInfo = null;

        Map<String, CellStyle> styles = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            try {
                dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            sheetCd = ReportSaveUtil.getStringValue(category.get("sheetCd"));
            if (dgrLevel == 0) {
                if (sheet != null) {
                    ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 14);
                    reportFormulaUtil.writeCellFormula();
                    
                    sheet = null;
                }

                rowNum = 0;

                model.put("reportDetlCd", sheetCd);
                reportInfo = reportCommDAO.selectReportInfo(model);
                styles = reportCommDAO.getReportStyleMap(model, wb);

                sheet = wb.createSheet(ReportSaveUtil.getStringValue(reportInfo.get("sheetNm")));
                reportFormulaUtil = new ReportFormulaUtil(sheet);

                rowNum = writeHeader2(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")), 14);

            }

            rowNum = writeData2(sheet, rowNum, category, styles, deptFlag, reportFormulaUtil);
            if (dgrLevel > 1) {
                deptFlag = 1;
            } else {
                deptFlag = 0;
            }

        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 14);
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
        row.setHeightInPoints(15f);

        cell = row.createCell(1);
        cell.setCellStyle(styles.get("title"));
        cell.setCellValue(title);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(12.75f);

        rowNum = writeReportCont(model, sheet, rowNum, styles);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(14.25f);

        cell = row.createCell(unitPos);
        cell.setCellStyle(styles.get("unit"));
        cell.setCellValue("(단위 : 천원)");

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
                    row.setHeightInPoints(14.75f);
                }
            }

            preRowSeq = rowSeq;

            if (rowSeq == 0 && cellSeq == 7 && headerCont.length() > 6) {
                cell = row.createCell(cellSeq);
                cell.setCellStyle(styles.get("header" + rowSeq + "Col" + cellSeq));
                if (BcjisCommUtil.isNullString(headerCont) == false) {
                    XSSFCellStyle style = (XSSFCellStyle) styles.get("header" + rowSeq + "Col" + cellSeq + "_1");
                    cell = row.createCell(cellSeq);
                    cell.setCellStyle(styles.get("header" + rowSeq + "Col" + cellSeq));
                    
                    if(style.getFont() != null){
                    	XSSFFont font = style.getFont();
                        XSSFRichTextString cellValue = new XSSFRichTextString(headerCont);
                        cellValue.applyFont(6, headerCont.length(), font);
                        cell.setCellValue(cellValue);
                    }
                }
            } else {
                cell = row.createCell(cellSeq);
                cell.setCellStyle(styles.get("header" + rowSeq + "Col" + cellSeq));
                if (BcjisCommUtil.isNullString(headerCont) == false) {
                    cell.setCellValue(headerCont);
                }
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

        sheet.setHorizontallyCenter(true);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(3, 8);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        Footer footer = sheet.getFooter();

        footer.setCenter("" + HeaderFooter.page());

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        ReportSaveUtil.reportMerge(reportCommDAO, model, sheet);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public int writeHeader2(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title, int unitPos) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 1;

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
                row.setHeightInPoints(14.25f);
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

        sheet.setHorizontallyCenter(true);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(2, 3);
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
        float rowHeight = 15f;
        Row row = null;
        Cell cell = null;

        List list = reportCommDAO.selectReportContList(param);
        Map map = null;
        for (int i = 0; i < 1; i++) {
            map = (Map) list.get(i);

            row = sheet.createRow(rowNum);
            rowNum++;
            row.setHeightInPoints(rowHeight);

            cell = row.createCell(1);
            cell.setCellStyle(styles.get("cont"));
            cell.setCellValue(ReportSaveUtil.getStringValue(map.get("cont")));
        }

        return rowNum;
    }

    public int writeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int deptFlag, ReportFormulaUtil reportFormulaUtil) throws Exception {
        float rowHeight = 14.75f;
        Row row = null;
        Cell cell = null;

        int dgrLevel = 0;
        try {
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        int deptCnt = ReportSaveUtil.getIntValue(category.get("deptCnt"));

        String dgrcompoId = ReportSaveUtil.getStringValue(category.get("dgrcompoId"));
        String upDgrcompoId = ReportSaveUtil.getStringValue(category.get("upDgrcompoId"));

        boolean formulaFlag = true;
        if (dgrLevel > 1 || deptCnt == 1) {
            formulaFlag = false;
        }

        String preStyleNm = ReportSaveUtil.getStringValue(category.get("preStyleNm"));

        if (dgrLevel > 1) {
            preStyleNm = preStyleNm + deptFlag;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        for (int i = 0; i <= 19; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get(preStyleNm + "Col" + i));
            if (i < 2) {
                cell.setCellValue(ReportSaveUtil.getStringValue(category.get("col" + i)));
            } else if (i == 3) {
                cell.setCellFormula("F" + (rowNum) + "+" + "G" + (rowNum) + "+" + "N" + (rowNum) + "+" + "O" + (rowNum) + "+" + "P" + (rowNum) + "+" + "Q" + (rowNum) + "+" + "R" + (rowNum) + "+" + "S" + (rowNum) + "+" + "T" + (rowNum));
            } else if (i == 4) {
                cell.setCellFormula("F" + (rowNum) + "+" + "G" + (rowNum) + "+" + "N" + (rowNum) + "+" + "O" + (rowNum) + "+" + "P" + (rowNum));
            } else if (i == 6) {
                cell.setCellFormula("H" + (rowNum) + "+" + "I" + (rowNum) + "+" + "M" + (rowNum));
            } else if (i == 8) {
                cell.setCellFormula("J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum));
            } else {
                if (formulaFlag == false) {
                    cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("col" + i)));
                }
            }
        }

        if (formulaFlag == true) {
            addDataFormulaCell(reportFormulaUtil, dgrcompoId, rowNum);
        }
        addDataFormulaValue(reportFormulaUtil, upDgrcompoId, rowNum);

        if (dgrLevel < 2) {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNum + ":$B$" + rowNum));
        }

        return rowNum;
    }

    public int writeData2(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int deptFlag, ReportFormulaUtil reportFormulaUtil) throws Exception {
        float rowHeight = 14.25f;
        Row row = null;
        Cell cell = null;

        int dgrLevel = 0;
        try {
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        int officeCnt = ReportSaveUtil.getIntValue(category.get("officeCnt"));

        String dgrcompoId = ReportSaveUtil.getStringValue(category.get("dgrcompoId"));
        String upDgrcompoId = ReportSaveUtil.getStringValue(category.get("upDgrcompoId"));

        boolean formulaFlag = true;
        if (dgrLevel > 1 || officeCnt == 1) {
            formulaFlag = false;
        }

        String preStyleNm = ReportSaveUtil.getStringValue(category.get("preStyleNm"));

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        for (int i = 0; i <= 14; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get(preStyleNm + "Col" + i));
            if (i < 2) {
                cell.setCellValue(ReportSaveUtil.getStringValue(category.get("col" + i)));
            } else if (i == 2) {
                cell.setCellFormula("D" + (rowNum) + "+" + "J" + (rowNum) + "+" + "K" + (rowNum) + "+" + "L" + (rowNum) + "+" + "M" + (rowNum) + "+" + "N" + (rowNum) + "+" + "O" + (rowNum));
            } else if (i == 3) {
                cell.setCellFormula("E" + (rowNum) + "+" + "F" + (rowNum) + "+" + "G" + (rowNum) + "+" + "H" + (rowNum) + "+" + "I" + (rowNum));
            } else {
                if (formulaFlag == false) {
                    cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("col" + i)));
                }
            }
        }

        if (formulaFlag == true) {
            addDataFormulaCell2(reportFormulaUtil, dgrcompoId, rowNum);
        }
        addDataFormulaValue2(reportFormulaUtil, upDgrcompoId, rowNum);

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNum + ":$B$" + rowNum));

        return rowNum;
    }

    public void addDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaValue(keyStr + "_C", "C" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_H", "H" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_K", "K" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_L", "L" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_M", "M" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_N", "N" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_O", "O" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_P", "P" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_Q", "Q" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_R", "R" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_S", "S" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_T", "T" + rowNum);
    }

    public void addDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaCell(keyStr + "_C", rowNum - 1, 2);
        reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        reportFormulaUtil.addFormulaCell(keyStr + "_H", rowNum - 1, 7);
        reportFormulaUtil.addFormulaCell(keyStr + "_J", rowNum - 1, 9);
        reportFormulaUtil.addFormulaCell(keyStr + "_K", rowNum - 1, 10);
        reportFormulaUtil.addFormulaCell(keyStr + "_L", rowNum - 1, 11);
        reportFormulaUtil.addFormulaCell(keyStr + "_M", rowNum - 1, 12);
        reportFormulaUtil.addFormulaCell(keyStr + "_N", rowNum - 1, 13);
        reportFormulaUtil.addFormulaCell(keyStr + "_O", rowNum - 1, 14);
        reportFormulaUtil.addFormulaCell(keyStr + "_P", rowNum - 1, 15);
        reportFormulaUtil.addFormulaCell(keyStr + "_Q", rowNum - 1, 16);
        reportFormulaUtil.addFormulaCell(keyStr + "_R", rowNum - 1, 17);
        reportFormulaUtil.addFormulaCell(keyStr + "_S", rowNum - 1, 18);
        reportFormulaUtil.addFormulaCell(keyStr + "_T", rowNum - 1, 19);
    }

    public void addDataFormulaValue2(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_H", "H" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_I", "I" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_J", "J" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_K", "K" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_L", "L" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_M", "M" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_N", "N" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_O", "O" + rowNum);
    }

    public void addDataFormulaCell2(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum) {
        reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);
        reportFormulaUtil.addFormulaCell(keyStr + "_H", rowNum - 1, 7);
        reportFormulaUtil.addFormulaCell(keyStr + "_I", rowNum - 1, 8);
        reportFormulaUtil.addFormulaCell(keyStr + "_J", rowNum - 1, 9);
        reportFormulaUtil.addFormulaCell(keyStr + "_K", rowNum - 1, 10);
        reportFormulaUtil.addFormulaCell(keyStr + "_L", rowNum - 1, 11);
        reportFormulaUtil.addFormulaCell(keyStr + "_M", rowNum - 1, 12);
        reportFormulaUtil.addFormulaCell(keyStr + "_N", rowNum - 1, 13);
        reportFormulaUtil.addFormulaCell(keyStr + "_O", rowNum - 1, 14);
    }
}

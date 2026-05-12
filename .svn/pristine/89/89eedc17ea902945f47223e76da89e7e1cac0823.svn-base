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

@Component("report002SaveFile")
public class Report002SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

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

    @SuppressWarnings("rawtypes")
    public void data(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {
        int rowNum = 0;
        XSSFSheet sheet = null;

        Map reportInfo = reportCommDAO.selectReportInfo(model);
        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(model, wb);

        sheet = wb.createSheet(ReportSaveUtil.getStringValue(reportInfo.get("sheetNm")));

        rowNum = writeHeader(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")));

        JSONObject category = null;
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);
            rowNum = writeData(sheet, rowNum, category, styles);
        }

        if (sheet != null) {
            writeLastSheet(model, wb, sheet, rowNum, styles, reportInfo, 79);
            sheet = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public int writeHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

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
                row.setHeightInPoints(12.75f);
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

        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:2"));
        
        sheet.createFreezePane(0, 2);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        Footer footer = sheet.getFooter();

        footer.setCenter("" + HeaderFooter.page());

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);
        
        reportMerge(model, sheet);

        return rowNum;
    }

    public int writeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles) throws Exception {
        float rowHeight = 12.75f;
        Row row = null;
        Cell cell = null;

        String preStyleNm = "data0";

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        String value = "";
        for (int i = 0; i <= 79; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get(preStyleNm + "Col" + i));
            if (i < 9
                    || i == 27
                    || i == 39
                    || i == 72
                    || i == 73
                    || i == 74
                    || i == 76
                    || i == 77
                    || i == 78
                    || i == 79) {
                if(i == 79){
                    value = ReportSaveUtil.getStringValue(category.get("col" + i));
                    if(value != null && value.length() > 0){
                        value = value.substring(0, value.length()-1);
                    }
                    
                    cell.setCellValue(value);
                }else{
                    cell.setCellValue(ReportSaveUtil.getStringValue(category.get("col" + i)));
                }
            } else {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("col" + i)));
            }
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
}

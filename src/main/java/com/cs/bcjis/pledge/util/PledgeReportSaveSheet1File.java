package com.cs.bcjis.pledge.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.cs.bcjis.report.util.ReportSaveUtil;

@Component("pledgeReportSaveSheet1File")
public class PledgeReportSaveSheet1File {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {

        Map pledgeInfo = (Map) model.get("pledgeInfo");
        String pledgeInfoNm = ReportSaveUtil.getStringValue(pledgeInfo.get("pledgeInfoNm"));

        String fileNm = pledgeInfoNm + " " + String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        pledgeInfoList(model, wb, pledgeInfo, (List<Object>) model.get("pledgeInfoList"));

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void pledgeInfoList(Map model, XSSFWorkbook wb, Map pledgeInfo, List<Object> categories) throws Exception {

        int rowNum = 0;
        XSSFSheet sheet = null;

        JSONObject category = null;

        Map param = new HashMap();
        param.put("reportCd", "P10");
        param.put("reportDetlCd", "P11");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));

        Map reportInfo = reportCommDAO.selectReportInfo(param);

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);

        String pledgeInfoNm = ReportSaveUtil.getStringValue(pledgeInfo.get("pledgeInfoNm"));
        String sheetName = pledgeInfoNm + " " + ReportSaveUtil.getStringValue(reportInfo.get("sheetNm"));
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet";
        }

        sheet = wb.createSheet(sheetName);

        String title = pledgeInfoNm + " " + ReportSaveUtil.getStringValue(reportInfo.get("reportNm"));
        rowNum = writePledgeInfoListHeader(param, sheet, rowNum, styles, reportInfo, title);

        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            rowNum = writePledgeInfoListData(sheet, rowNum, category, styles);
        }

        if (sheet != null) {
            writeLastSheet(param, wb, sheet, rowNum, styles, reportInfo, 3);
            sheet = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public int writePledgeInfoListHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(35.25f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(14.25f);

        cell = row.createCell(0);
        cell.setCellValue("");

        repeatingStartRow = rowNum;

        float rowHeight = 18.0f;
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
        sheet.createFreezePane(0, 3);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));
        sheet.setDisplayGridlines(true);

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        reportMerge(model, sheet);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public int writeLastSheet(Map model, XSSFWorkbook wb, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, int cellCnt) throws Exception {
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(13.50f);
        for (int i = 0; i <= cellCnt; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get("lastCol" + i));
        }

        reportCommDAO.setReportColWidth(model, sheet);

        wb.setPrintArea(wb.getSheetIndex(sheet), 0, cellCnt, 0, rowNum - 1);

        return rowNum;
    }

    public int writePledgeInfoListData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles) {
        float rowHeight = 18.0f;
        Row row = null;
        Cell cell = null;

        int pledgeBizLevel = 0;
        try {
            pledgeBizLevel = Integer.parseInt(String.valueOf(category.get("pledgeBizLevel")));
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        String preStyleNm = "lv" + pledgeBizLevel;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        if (pledgeBizLevel == 1) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));
        }

        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        if (pledgeBizLevel == 2) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "Col2"));
        if (pledgeBizLevel == 3) {
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizNm")));

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

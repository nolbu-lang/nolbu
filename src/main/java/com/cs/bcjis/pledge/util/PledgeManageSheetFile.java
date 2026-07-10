package com.cs.bcjis.pledge.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
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

@Component("pledgeManageSheetFile")
public class PledgeManageSheetFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {

        String fileNm = "공약사업엑셀업로드양식";
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        XSSFWorkbook wb = new XSSFWorkbook();

        excelForm(model, wb, (List<Object>) model.get("resultList"));

        if (wb.getNumberOfSheets() < 1) {
            wb.createSheet("Sheet1");
        }

        String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
        model.put("fileName", fileNm + ".xlsx");
        model.put("realFileName", ReportSaveUtil.writeExcelFile(wb, storePathString));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void excelForm(Map model, XSSFWorkbook wb, List<Object> categories) throws Exception {
    	int rowNum = 0;
        XSSFSheet sheet = null;

        JSONObject category = null;

        Map param = new HashMap();
        param.put("reportCd", "P10");
        param.put("reportDetlCd", "P11");
        param.put("fisYear", model.get("fisYear"));
        param.put("bgtDgr", model.get("bgtDgr"));

        Map reportInfo = null; //reportCommDAO.selectReportInfo(param);

//        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(param, wb);
        
        Map<String, CellStyle> styles = getStyleMap(wb);

        String sheetName = "업로드양식";
        if (BcjisCommUtil.isNullString(sheetName) == true) {
            sheetName = "new sheet";
        }

        sheet = wb.createSheet(sheetName);

        rowNum = writeExcelFormHeader(param, sheet, rowNum, styles, "");

        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            rowNum = writeExcelFormListData(sheet, rowNum, category, styles);
        }

        if (sheet != null) {
            writeLastSheet(param, wb, sheet, rowNum, styles, 3);
            sheet = null;
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public int writeExcelFormHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, String title) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(90.25f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        String helpTitle = "기존 데이터는 그대로 두고 신규 항목들만 추가하여 작성 바랍니다.";
        helpTitle += "\n -- 신규 추가 방법";
        helpTitle += "\n 1. [공약정보ID]는 그대로 복사";
        helpTitle += "\n 2. [사업ID]는 빈칸으로 둘것";
        helpTitle += "\n 3. [상위ID]는 빈칸으로 둘것";
        helpTitle += "\n 4. [level]의 경우 기존 데이터를 참고하여 순서대로 증가";
        helpTitle += "\n 5. [분류]와 [사업명] 은 필요한 내용 입력";
        titleCell.setCellValue(helpTitle);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(14.25f);

        repeatingStartRow = rowNum;

        cell = row.createCell(0);
        cell.setCellValue("공약정보ID");
        cell.setCellStyle(styles.get("header"));
        
        cell = row.createCell(1);
        cell.setCellValue("사업ID");
        cell.setCellStyle(styles.get("header"));
        
        cell = row.createCell(2);
        cell.setCellValue("상위ID");
        cell.setCellStyle(styles.get("header"));
        
        cell = row.createCell(3);
        cell.setCellValue("level");
        cell.setCellStyle(styles.get("header"));
        
        cell = row.createCell(4);
        cell.setCellValue("분류");
        cell.setCellStyle(styles.get("header"));
        
        cell = row.createCell(5);
        cell.setCellValue("사업명");
        
        /*cell = row.createCell(5);
        cell.setCellValue("5");
        
        cell = row.createCell(6);
        cell.setCellValue("6");*/
        
        cell.setCellStyle(styles.get("header"));
        
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize((short)12);
        printSetup.setScale((short)83);

        sheet.setMargin(XSSFSheet.LeftMargin, (double)0.40);
        sheet.setMargin(XSSFSheet.RightMargin, (double)0.40);
        sheet.setMargin(XSSFSheet.TopMargin, (double)1.00);
        sheet.setMargin(XSSFSheet.BottomMargin, (double)0.80);
        sheet.setMargin(XSSFSheet.HeaderMargin, (double)0.52);
        sheet.setMargin(XSSFSheet.FooterMargin, (double)0.52);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + rowNum));
        sheet.createFreezePane(0, 2);
        sheet.setZoom(90);
        //sheet.setDisplayGridlines(true);

        //CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        //view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$F$1"));

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public int writeLastSheet(Map model, XSSFWorkbook wb, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, int cellCnt) throws Exception {
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(13.50f);
        for (int i = 0; i <= cellCnt; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.get("lastCol" + i));
        }

        sheet.setColumnWidth(0, 3104); //공약정보ID
        sheet.setColumnWidth(1, 3104); //사업ID
        sheet.setColumnWidth(2, 3104); //상위ID
        sheet.setColumnWidth(3, 2004); //level
        sheet.setColumnWidth(4, 1504); //분류
        sheet.setColumnWidth(5, 20000); //사업명
        //reportCommDAO.setReportColWidth(model, sheet);

        //wb.setPrintArea(wb.getSheetIndex(sheet), 0, cellCnt, 0, rowNum - 1);

        return rowNum;
    }

    public int writeExcelFormListData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles) {
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
        cell.setCellStyle(styles.get("key"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeInfoId")));
        
        cell = row.createCell(1);
        cell.setCellStyle(styles.get("key"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizId")));
        
        cell = row.createCell(2);
        cell.setCellStyle(styles.get("key"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("upPledgeBizId")));

        cell = row.createCell(3);
        cell.setCellStyle(styles.get("normal"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizLevel")));

        cell = row.createCell(4);
        cell.setCellStyle(styles.get("normal"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizFg")));

        cell = row.createCell(5);
        cell.setCellStyle(styles.get("normal"));
    	cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizNm")));
    	
        /*cell = row.createCell(5);
        cell.setCellStyle(styles.get("normal"));
        cell.setCellValue(ReportSaveUtil.getStringValue(category.get("pledgeBizNm")));*/

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public void reportMerge(Map model, XSSFSheet sheet) throws Exception {
        String mergeVal = "";
        List list = null;
        Map map = null;

        /*list = reportCommDAO.selectReportMergeList(model);
        while (!list.isEmpty()) {
            map = (Map) list.remove(0);

            mergeVal = ReportSaveUtil.getStringValue(map.get("mergeVal"));
            if (BcjisCommUtil.isNullString(mergeVal) == false) {
                sheet.addMergedRegion(CellRangeAddress.valueOf(mergeVal));
            }
        }*/
    }

    public Map<String, CellStyle> getStyleMap(XSSFWorkbook wb){
    	Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        XSSFCellStyle style = null;
        XSSFCellStyle headerStyle = null;
        XSSFCellStyle titleStyle = null;
        XSSFFont font = null;
        XSSFFont headerFont = null;
        XSSFFont titleFont = null;
        
        
        style = wb.createCellStyle();
        style.setAlignment((short)1);
        style.setVerticalAlignment((short)1);
        style.setBorderRight((short)1);
        style.setRightBorderColor((short)8);
        style.setBorderLeft((short)1);
        style.setLeftBorderColor((short)8);
        style.setBorderTop((short)1);
        style.setTopBorderColor((short)8);
        style.setBorderBottom((short)1); //선
        style.setBottomBorderColor((short)8); //검정
        style.setFillPattern((short)0); //사용안함
        style.setWrapText(true);
        style.setFont(font);
        styles.put("normal", style);
        
        style = wb.createCellStyle();
        style.setAlignment((short)1);
        style.setVerticalAlignment((short)1);
        style.setBorderRight((short)1);
        style.setRightBorderColor((short)8);
        style.setBorderLeft((short)1);
        style.setLeftBorderColor((short)8);
        style.setBorderTop((short)1);
        style.setTopBorderColor((short)8);
        style.setBorderBottom((short)1); //선
        style.setBottomBorderColor((short)8); //검정
        style.setFillPattern((short)0); //사용안함
        style.setWrapText(true);
        style.setFont(font);
        style.setFillForegroundColor(HSSFColor.RED.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styles.put("key", style);
        
        
        headerFont = wb.createFont();
        ExtendedFormatRecord e = new ExtendedFormatRecord();
        e.setShrinkToFit(true);
        headerFont.setFontHeightInPoints((short)11);
        headerFont.setBoldweight((short)700);
        headerFont.setFontName("궁서체");
        
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment((short)2);
        headerStyle.setVerticalAlignment((short)1);
        headerStyle.setBorderRight((short)1);
        headerStyle.setRightBorderColor((short)8);
        headerStyle.setBorderLeft((short)1);
        headerStyle.setLeftBorderColor((short)8);
        headerStyle.setBorderTop((short)1);
        headerStyle.setTopBorderColor((short)8);
        headerStyle.setBorderBottom((short)1); //선
        headerStyle.setBottomBorderColor((short)8); //검정
        headerStyle.setFillPattern((short)0); //사용안함
        headerStyle.setWrapText(true);
        
        headerStyle.setFont(headerFont);
        styles.put("header", headerStyle);
        
        
        titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)11);
        titleFont.setBoldweight((short)700);
        //titleFont.setFontName("궁서체");
        
        titleStyle = wb.createCellStyle();
        titleStyle.setAlignment((short)1);
        titleStyle.setVerticalAlignment((short)1);
        titleStyle.setBorderRight((short)1);
        titleStyle.setRightBorderColor((short)8);
        titleStyle.setBorderLeft((short)1);
        titleStyle.setLeftBorderColor((short)8);
        titleStyle.setBorderTop((short)1);
        titleStyle.setTopBorderColor((short)8);
        titleStyle.setBorderBottom((short)1); //선
        titleStyle.setBottomBorderColor((short)8); //검정
        titleStyle.setFillPattern((short)0); //사용안함
        titleStyle.setWrapText(true);
        
        titleStyle.setFont(titleFont);
        styles.put("title", titleStyle);
        
        
        return styles;
    }
}

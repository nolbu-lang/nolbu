package com.cs.bcjis.report.util;

import java.text.DecimalFormat;
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

@Component("report0B0SaveFile")
public class Report0B0SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {

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
        int dataCnt = -1;
        XSSFSheet sheet = null;
        ReportFormulaUtil reportFormulaUtil = null;

        JSONObject category = null;

        int dgrLevel = 0;
        String preFisFgMstCd = "";
        String fisFgMstCd = "";
        String preSectCd = "";
        String sectCd = "";
        String preFisFgCd = "";
        String fisFgCd = "";
        Map reportInfo = reportCommDAO.selectReportInfo(model);
        boolean bgtCompoFlag = "10".equals(ReportSaveUtil.getStringValue(reportInfo.get("bgtCompoFg"))) ? true : false;

        Map<String, CellStyle> styles = reportCommDAO.getReportStyleMap(model, wb);
        while (!categories.isEmpty()) {
            category = (JSONObject) categories.remove(0);

            fisFgMstCd = ReportSaveUtil.getStringValue(category.get("fisFgMstCd"));
            sectCd = ReportSaveUtil.getStringValue(category.get("sectCd"));
            fisFgCd = ReportSaveUtil.getStringValue(category.get("fisFgCd"));
            dgrLevel = Integer.parseInt(String.valueOf(category.get("dgrLevel")));
            if (fisFgMstCd.equals(preFisFgMstCd) == false) {
                if (sheet != null) {
                    ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 8);
                    reportFormulaUtil.writeCellFormula();
                    sheet = null;
                }

                rowNum = 0;

                sheet = wb.createSheet(ReportSaveUtil.getStringValue(category.get("fisFgMstNm")));
                reportFormulaUtil = new ReportFormulaUtil(sheet);
                dataCnt = -1;

                rowNum = writeHeader(model, sheet, rowNum, styles, reportInfo, ReportSaveUtil.getStringValue(reportInfo.get("reportNm")), 8, ReportSaveUtil.getStringValue(category.get("fisFgMstNm")));

            }

            if (dgrLevel < 2) {
                dataCnt = -1;
            } else {
                if ("100".equals(fisFgMstCd) == true && sectCd.equals(preSectCd) == false) {
                    dataCnt = -1;
                } else if ("100".equals(fisFgMstCd) == false && fisFgCd.equals(preFisFgCd) == false) {
                    dataCnt = -1;
                }
            }

            dataCnt++;
            rowNum = writeData(sheet, rowNum, category, styles, dataCnt, bgtCompoFlag, reportFormulaUtil);

            preFisFgMstCd = fisFgMstCd;
            preSectCd = sectCd;
            preFisFgCd = fisFgCd;
        }

        if (sheet != null) {
            ReportSaveUtil.writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, 8);
            reportFormulaUtil.writeCellFormula();
            sheet = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public int writeHeader(Map model, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, String title, int unitPos, String fisMstNm) throws Exception {
        Row row = null;
        Cell cell = null;

        List list = null;
        Map map = null;

        int repeatingStartRow = 0;

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(29.25f);

        Cell titleCell = row.createCell(0);
        titleCell.setCellStyle(styles.get("title"));
        titleCell.setCellValue(title);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(14.25f);

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(19.50f);

        titleCell = row.createCell(1);
        titleCell.setCellStyle(styles.get("fisMstNm"));
        titleCell.setCellValue("<" + fisMstNm + ">");

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(14.25f);

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

        sheet.setMargin(XSSFSheet.LeftMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("leftMargin")));
        sheet.setMargin(XSSFSheet.RightMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("rightMargin")));
        sheet.setMargin(XSSFSheet.TopMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("topMargin")));
        sheet.setMargin(XSSFSheet.BottomMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("bottomMargin")));
        sheet.setMargin(XSSFSheet.HeaderMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("headerMargin")));
        sheet.setMargin(XSSFSheet.FooterMargin, BcjisCommUtil.getDoubleValue(reportInfo.get("footerMargin")));

        sheet.setHorizontallyCenter(true);

        sheet.setRepeatingRows(CellRangeAddress.valueOf(repeatingStartRow + ":" + (rowNum - 1)));
        sheet.createFreezePane(0, 6);
        sheet.setZoom(BcjisCommUtil.getIntValue(reportInfo.get("zoom")));

        sheet.setDisplayGridlines(false);

        Footer footer = sheet.getFooter();

        footer.setCenter("" + HeaderFooter.page());

        CTSheetView view = sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0);
        view.setView(STSheetViewType.PAGE_BREAK_PREVIEW);

        ReportSaveUtil.reportMerge(reportCommDAO, model, sheet);

        return rowNum;
    }

    public int writeData(XSSFSheet sheet, int rowNum, JSONObject category, Map<String, CellStyle> styles, int dataCnt, boolean bgtCompoFlag, ReportFormulaUtil reportFormulaUtil) throws Exception {
        float rowHeight = 14.25f;
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
        String bizAmount = ReportSaveUtil.getStringValue(category.get("bizAmount"));
        String bizPeriod = ReportSaveUtil.getStringValue(category.get("bizPeriod"));
        long frscAmt = ReportSaveUtil.getAmtValue(category.get("frscAmt")); //preBgtAmt->frscAmt로 수정

        DecimalFormat formatter = new DecimalFormat("#,###");
        String remark = (frscAmt == 0 ? "" : "국비 " + formatter.format(frscAmt) + "포함");

        boolean formulaFlag = true;

        String preStyleNm = "";
        if (dgrLevel == 0) {
            preStyleNm = "tot";
            rowHeight = 18f;
        } else if (dgrLevel == 1) {
            preStyleNm = "sect";
            rowHeight = 18f;
        } else {
            if(dataCnt == 1){
                preStyleNm = "dataFst";
            }else{
                preStyleNm = "data0";
            }
            formulaFlag = false;
        }

        String[] dgrcompoNmArray = dgrcompoNm.split("\n");
        String[] bizAmountArray = bizAmount.split("\n");
        String[] bizPeriodArray = bizPeriod.split("\n");

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);
        if (dgrLevel < 2) {
            cell = row.createCell(0);
            cell.setCellStyle(styles.get(preStyleNm + "Col0"));
            cell.setCellValue(ReportSaveUtil.getStringValue(category.get("dgrcompoNm")));

            cell = row.createCell(1);
            cell.setCellStyle(styles.get(preStyleNm + "Col1"));

            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));

            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        } else {
            cell = row.createCell(0);
            cell.setCellStyle(styles.get(preStyleNm + "Col0"));
            
            cell.setCellValue(dataCnt);
            
            
            cell = row.createCell(1);
            cell.setCellStyle(styles.get(preStyleNm + "Col1"));
            if (dgrcompoNmArray.length > 0) {
                cell.setCellValue(dgrcompoNmArray[0]);
            }

            cell = row.createCell(2);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));
            if (bizAmountArray.length > 0) {
                cell.setCellValue(bizAmountArray[0]);
            }

            cell = row.createCell(3);
            cell.setCellStyle(styles.get(preStyleNm + "Col3"));
            if (bizPeriodArray.length > 0) {
                cell.setCellValue(bizPeriodArray[0]);
            }
        }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (formulaFlag == false) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("totBizAmt")));
        }

        if (bgtCompoFlag == false) {
            cell = row.createCell(5);
            cell.setCellStyle(styles.get(preStyleNm + "Col5"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preAmt")));
            }

            cell = row.createCell(6);
            cell.setCellStyle(styles.get(preStyleNm + "Col6"));
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("addBgtAmt")));
            }

            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col7"));
            cell.setCellFormula("G" + (rowNum) + "-" + "F" + (rowNum));
        } else {
            cell = row.createCell(5);
            cell.setCellStyle(styles.get(preStyleNm + "Col5"));

            cell = row.createCell(6);
            cell.setCellStyle(styles.get(preStyleNm + "Col6"));
            //row.createCell(7) 에 있던내용 (6)으로 변경
            if (formulaFlag == false) {
                cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("bgtAmt")));
            }
            cell = row.createCell(7);
            cell.setCellStyle(styles.get(preStyleNm + "Col2"));
            
        }

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));
        cell.setCellValue(remark);

        addDataFormulaValue(reportFormulaUtil, upDgrcompoId, rowNum, bgtCompoFlag);
        if (formulaFlag == true) {
            addDataFormulaCell(reportFormulaUtil, dgrcompoId, rowNum, bgtCompoFlag);
        }

        if (dgrLevel < 2) {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowNum + ":$D$" + rowNum));
            return rowNum;
        }

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        preStyleNm = "data1";
        
        //추가
        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        
        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        if (dgrcompoNmArray.length > 1) {
            cell.setCellValue(dgrcompoNmArray[1]);
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "Col2"));
        if (bizAmountArray.length > 1) {
            cell.setCellValue(bizAmountArray[1]);
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        if (bizPeriodArray.length > 1) {
            cell.setCellValue(bizPeriodArray[1]);
        }

        cell = row.createCell(4);
        cell.setCellStyle(styles.get(preStyleNm + "Col4"));
        if (ReportSaveUtil.getAmtValue(category.get("preBizAmt")) != 0) {
            cell.setCellValue(ReportSaveUtil.getAmtValue(category.get("preBizAmt")));
        }

        cell = row.createCell(5);
        cell.setCellStyle(styles.get(preStyleNm + "Col5"));

        cell = row.createCell(6);
        cell.setCellStyle(styles.get(preStyleNm + "Col6"));

        cell = row.createCell(7);
        cell.setCellStyle(styles.get(preStyleNm + "Col7"));

        cell = row.createCell(8);
        cell.setCellStyle(styles.get(preStyleNm + "Col8"));

        row = sheet.createRow(rowNum);
        rowNum++;
        row.setHeightInPoints(rowHeight);

        preStyleNm = "data2";
        
        //추가
        cell = row.createCell(0);
        cell.setCellStyle(styles.get(preStyleNm + "Col0"));
        
        cell = row.createCell(1);
        cell.setCellStyle(styles.get(preStyleNm + "Col1"));
        if (dgrcompoNmArray.length > 2) {
            cell.setCellValue(dgrcompoNmArray[2]);
        }

        cell = row.createCell(2);
        cell.setCellStyle(styles.get(preStyleNm + "Col2"));
        if (bizAmountArray.length > 2) {
            cell.setCellValue(bizAmountArray[2]);
        }

        cell = row.createCell(3);
        cell.setCellStyle(styles.get(preStyleNm + "Col3"));
        if (bizPeriodArray.length > 2) {
            cell.setCellValue(bizPeriodArray[2]);
        }

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

        return rowNum;
    }

    public void addDataFormulaValue(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaValue(keyStr + "_E", "E" + rowNum);
        reportFormulaUtil.addFormulaValue(keyStr + "_G", "G" + rowNum);

        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaValue(keyStr + "_F", "F" + rowNum);
        }
    }

    public void addDataFormulaCell(ReportFormulaUtil reportFormulaUtil, String keyStr, int rowNum, boolean bgtCompoFlag) {
        reportFormulaUtil.addFormulaCell(keyStr + "_E", rowNum - 1, 4);
        reportFormulaUtil.addFormulaCell(keyStr + "_G", rowNum - 1, 6);

        if (bgtCompoFlag == false) {
            reportFormulaUtil.addFormulaCell(keyStr + "_F", rowNum - 1, 5);
        }
    }

}

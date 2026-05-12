package com.cs.bcjis.report.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.util.BcjisWebUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

public class ReportSaveUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportSaveUtil.class);

    public static String writeExcelFile(XSSFWorkbook wb, String storePathString) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("writeExcelFile(XSSFWorkbook, String) - start");
        }

        String realFileName = "";
        FileOutputStream fos = null;
        try {
            File saveFolder = new File(BcjisWebUtil.filePathBlackList(storePathString));

            if (!saveFolder.exists() || saveFolder.isFile()) {
                saveFolder.mkdirs();
            }

            realFileName = new StringBuilder().append(storePathString).append(File.separator).append(BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS")).append(UUID.randomUUID()).append(".xlsx").toString();

            fos = new FileOutputStream(realFileName);

            wb.write(fos);

        } catch (Exception e) {
            logger.error("writeExcelFile(XSSFWorkbook, String)", e);

            throw e;
        } finally {
            if (fos != null)
                fos.close();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("writeExcelFile(XSSFWorkbook, String) - end");
        }
        return realFileName;
    }

    public static String getStorePathString(Properties config, String storePath, String KeyStr) {
        if (logger.isDebugEnabled()) {
            logger.debug("getStorePathString(Properties, String, String) - start");
        }

        if ("".equals(storePath) || storePath == null) {
            String returnString = config.getProperty("Globals.excelStorePath") + File.separator + KeyStr;
            if (logger.isDebugEnabled()) {
                logger.debug("getStorePathString(Properties, String, String) - end");
            }
            return returnString;
        }

        String returnString = config.getProperty(storePath) + File.separator + KeyStr;
        if (logger.isDebugEnabled()) {
            logger.debug("getStorePathString(Properties, String, String) - end");
        }
        return returnString;
    }

    public static String getStringValue(Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("getStringValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("getStringValue(Object) - end");
            }
            return "";
        }

        String returnString = String.valueOf(value);
        if (logger.isDebugEnabled()) {
            logger.debug("getStringValue(Object) - end");
        }
        return returnString;
    }

    public static long getAmtValue(Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("getAmtValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAmtValue(Object) - end");
            }
            return 0l;
        }

        long l = 0l;

        try {
            l = Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException nef) {
            logger.error("getAmtValue(Object)", nef);

            l = 0l;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getAmtValue(Object) - end");
        }
        return l;
    }

    public static long getAmtValue(Object value, int unit) {
        if (logger.isDebugEnabled()) {
            logger.debug("getAmtValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAmtValue(Object) - end");
            }
            return 0l;
        }

        if (unit == 0) {
            unit = 1;
        }

        long l = 0l;

        try {
            l = Long.parseLong(String.valueOf(value));
            if (unit > 1) {
                l = Math.round(l / unit);
            }
        } catch (NumberFormatException nef) {
            logger.error("getAmtValue(Object)", nef);

            l = 0l;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getAmtValue(Object) - end");
        }
        return l;
    }

    public static int getIntValue(Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("getIntValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            int returnint = -1;
            if (logger.isDebugEnabled()) {
                logger.debug("getIntValue(Object) - end");
            }
            return returnint;
        }

        int i = 0;

        try {
            i = Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException nef) {
            logger.error("getIntValue(Object)", nef);

            i = -1;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getIntValue(Object) - end");
        }
        return i;
    }

    public static String getAmtStrValue(Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("getAmtStrValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("getAmtStrValue(Object) - end");
            }
            return "0";
        }

        long l = 0l;

        try {
            l = Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException nef) {
            logger.error("getAmtStrValue(Object)", nef);

            l = 0l;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getAmtStrValue(Object) - end");
        }

        NumberFormat formatter = new DecimalFormat("###,###,###,###,###,###");
        return formatter.format(l);
    }

    public static String getBizListRemark(JSONObject category) {
        StringBuffer sBuf = new StringBuffer();

        String amt = ReportSaveUtil.getAmtStrValue(category.get("frscAmt2"));
        String preStr = "";

        if ("0".equals(amt) == false) {
            sBuf.append("· 국비 " + amt);
            preStr = "\n";
        }

        amt = ReportSaveUtil.getAmtStrValue(category.get("frscAmt3"));
        if ("0".equals(amt) == false) {
            sBuf.append(preStr + "· 교부세 " + amt);
            preStr = "\n";
        }

        amt = ReportSaveUtil.getAmtStrValue(category.get("frscAmt5"));
        if ("0".equals(amt) == false) {
            sBuf.append(preStr + "· 채무 " + amt + " 별도");
            preStr = "\n";
        }

        amt = ReportSaveUtil.getAmtStrValue(category.get("frscAmt6"));
        if ("0".equals(amt) == false) {
            sBuf.append(preStr + "· 기타 " + amt + " 별도");
            preStr = "\n";
        }

        return sBuf.toString();
    }

    @SuppressWarnings("rawtypes")
    public static int writeLastSheet(ReportCommDAO reportCommDAO, Map model, XSSFWorkbook wb, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, int cellCnt) throws Exception {
        return writeLastSheet(reportCommDAO, model, wb, sheet, rowNum, styles, reportInfo, cellCnt, cellCnt);
    }

    @SuppressWarnings("rawtypes")
    public static int writeLastSheet(ReportCommDAO reportCommDAO, Map model, XSSFWorkbook wb, XSSFSheet sheet, int rowNum, Map<String, CellStyle> styles, Map reportInfo, int cellCnt, int printCellCnt) throws Exception {
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

        wb.setPrintArea(wb.getSheetIndex(sheet), 0, printCellCnt, 0, rowNum - 1);

        return rowNum;
    }

    @SuppressWarnings("rawtypes")
    public static void reportMerge(ReportCommDAO reportCommDAO, Map model, XSSFSheet sheet) throws Exception {
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

    public static String getXmlStringValue(Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("getStringValue(Object) - start");
        }

        if (BcjisCommUtil.isNullString(value) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("getStringValue(Object) - end");
            }
            return "";
        }

        String returnString = String.valueOf(value);

        returnString = returnString.replaceAll("\"", "&quot;").replaceAll("&", "&amp;").replaceAll("\'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        if (logger.isDebugEnabled()) {
            logger.debug("getStringValue(Object) - end");
        }
        return returnString;
    }
}

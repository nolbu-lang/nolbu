package com.cs.bcjis.comm.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("bcjisSaveExcelUtil")
public class BcjisSaveExcelUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BcjisSaveExcelUtil.class);

    @Autowired
    @Qualifier("config")
    private Properties config;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildExcelDocument(Map model, String KeyStr, String storePath) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("buildExcelDocument(Map, String, String) - start");
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        
        String excelFileName = String.valueOf(model.get("excelFileName"));
        if (BcjisCommUtil.isNullString(excelFileName) == true) {
            excelFileName = "예산편성심사정보시스템";
        }

        int rowCnt = 0;

        HSSFCell cell = null;

        HSSFSheet sheet = wb.createSheet(excelFileName);
        sheet.setDefaultColumnWidth(12);

        HSSFFont titleFont = wb.createFont();
        HSSFCellStyle titleStyle = wb.createCellStyle();

        titleFont.setColor(HSSFColor.BLACK.index);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        JSONArray arrColInfo = JSONArray.fromObject(model.get("excelColInfo"));
        
        if (arrColInfo == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("buildExcelDocument(Map, String, String) - end");
            }
            return;
        }

        int len = arrColInfo.size();
        HSSFRow titleRow = sheet.createRow(rowCnt);
        rowCnt++;
        JSONObject objColInfo = null;
        String name = "";
        for (int i = 0; i < len; i++) {
            objColInfo = JSONObject.fromObject(arrColInfo.getString(i));
            HSSFCell titleCell = titleRow.createCell(i);
            name = objColInfo.getString("name");
            if(BcjisCommUtil.isNullString(name) == true){
                name = "";
            }
            titleCell.setCellValue(new HSSFRichTextString(name));
            titleCell.setCellStyle(titleStyle);
        }

        List<Object> categories = (List<Object>) model.get("dataList");
        JSONObject category = null;
        String colPhyCol = null;
        String colValue = null;
        long l = 0l;
        while(!categories.isEmpty()){

            category = (JSONObject) categories.remove(0);

            for (int j = 0; j < len; j++) {
                objColInfo = JSONObject.fromObject(arrColInfo.getString(j));

                cell = getCell(sheet, rowCnt, j);
                
                colPhyCol = String.valueOf(objColInfo.get("colPhyCol"));
                if(BcjisCommUtil.isNullString(colPhyCol) == true){
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(new HSSFRichTextString());
                    
                    continue;
                }
                
                colValue = String.valueOf(category.get(colPhyCol));
                if ("NMR".equals(objColInfo.get("colType"))) {
                    try{
                        if(BcjisCommUtil.isNullString(colValue) == true){
                            colValue = "0";
                        }

                        l = Long.parseLong(colValue);
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(l);
                    }catch(NumberFormatException nfe){
                        logger.error("buildExcelDocument(Map, String, String)", nfe);

                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(new HSSFRichTextString("0"));
                    }
                } else if ("YMD".equals(objColInfo.get("colType"))) {
                    if(BcjisCommUtil.isNullString(colValue) == true){
                        colValue = "";
                    }

                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(new HSSFRichTextString(BcjisCommUtil.getDateFormat(colValue)));
                } else {
                    if(BcjisCommUtil.isNullString(colValue) == true){
                        colValue = "";
                    }

                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(new HSSFRichTextString(colValue));
                }
            }
            
            rowCnt++;
        }
        
        FileOutputStream fos = null;
        try{
            String storePathString = "";
            
            if ("".equals(storePath) || storePath == null) {
                storePathString = config.getProperty("Globals.excelStorePath") + File.separator + KeyStr;
            } else {
                storePathString = config.getProperty(storePath) + File.separator + KeyStr;
            }
            
            File saveFolder = new File(BcjisWebUtil.filePathBlackList(storePathString));
    
            if (!saveFolder.exists() || saveFolder.isFile()) {
                saveFolder.mkdirs();
            }
            
            
            String realFileName = new StringBuilder().append(storePathString).append(File.separator)
                                                        .append(BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS"))
                                                        .append(UUID.randomUUID())
                                                        .append(".xlsx").toString();
            
            fos = new FileOutputStream(realFileName);
            
            wb.write(fos);
            
            model.put("fileName", excelFileName + ".xls");
            model.put("realFileName", realFileName);
            
        }catch(Exception e){
            logger.error("buildExcelDocument(Map, String, String)", e);

        }finally{
            if(fos != null) fos.close();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("buildExcelDocument(Map, String, String) - end");
        }
    }
    
    public HSSFCell getCell(HSSFSheet sheet, int row, int col) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCell(HSSFSheet, int, int) - start");
        }

        HSSFRow sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        HSSFCell cell = sheetRow.getCell(col);
        if (cell == null) {
            cell = sheetRow.createCell(col);
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("getCell(HSSFSheet, int, int) - end");
        }
        return cell;
    }
}

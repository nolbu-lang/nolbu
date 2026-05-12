package com.cs.bcjis.comm.util;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component("bcjisUploadExcelUtil")
public class BcjisUploadExcelUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BcjisUploadExcelUtil.class);

    @Autowired
    @Qualifier("config")
    private Properties config;


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> uploadExcelFiles(HttpServletRequest request, String[] colName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - start");
        }

        List<Map> list = new ArrayList<Map>();

        MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileIter = mptRequest.getFileNames();

        if (fileIter.hasNext() == false) {
            if (logger.isDebugEnabled()) {
                logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
            }
            return list;
        }

        MultipartFile mFile = mptRequest.getFile(String.valueOf(fileIter.next()));
        String orginFileName = BcjisCommUtil.getAttachFileName(mFile.getOriginalFilename());

        if (BcjisCommUtil.isNullString(orginFileName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
            }
            return list;
        }
        
        int lastIdx = orginFileName.lastIndexOf(".");
        if(lastIdx < 0){
            if (logger.isDebugEnabled()) {
                logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
            }
            return list;
        }
        
        String ext = orginFileName.substring(lastIdx + 1);
        if("xls".equals(ext) == true){
            List returnList = uploadExcelFilesXls(mFile, colName);
            if (logger.isDebugEnabled()) {
                logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
            }
            return returnList;
        }else if("xlsx".equals(ext) == true){
            List returnList = uploadExcelFilesXlsx(mFile, colName);
            if (logger.isDebugEnabled()) {
                logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
            }
            return returnList;
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFiles(HttpServletRequest, String[]) - end");
        }
        return list;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> uploadExcelFilesXls(MultipartFile mFile, String[] colName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFilesXls(MultipartFile, String[]) - start");
        }

        List<Map> list = new ArrayList<Map>();

        InputStream stream = null;
        try {
            stream = mFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();

            // Skip Header
            if (rows.hasNext() == true) {
                rows.next();
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("uploadExcelFilesXls(MultipartFile, String[]) - end");
                }
                return list;
            }

            Map dataMap = null;
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                if(row.getLastCellNum() < colName.length){
                    continue;
                }

                dataMap = new HashMap();
                for (int i = 0; i < colName.length; i++) {
                    dataMap.put(colName[i], getCellValue((HSSFCell) row.getCell(i)));
                }

                list.add(dataMap);
            }
        } catch (Exception e) {
            logger.error("uploadExcelFilesXls(MultipartFile, String[])", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFilesXls(MultipartFile, String[]) - end");
        }
        return list;
    }
    

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> uploadExcelFilesXlsx(MultipartFile mFile, String[] colName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFilesXlsx(MultipartFile, String[]) - start");
        }

        List<Map> list = new ArrayList<Map>();

        InputStream stream = null;
        try {
            stream = mFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();

            // Skip Header
            if (rows.hasNext() == true) {
                rows.next();
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("uploadExcelFilesXlsx(MultipartFile, String[]) - end");
                }
                return list;
            }

            Map dataMap = null;
            XSSFRow row = null;
            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                if(row.getLastCellNum() < colName.length){
                    continue;
                }

                dataMap = new HashMap();
                for (int i = 0; i < colName.length; i++) {
                    dataMap.put(colName[i], getCellValue((XSSFCell) row.getCell(i)));
                }

                list.add(dataMap);
            }
        } catch (Exception e) {
            logger.error("uploadExcelFilesXlsx(MultipartFile, String[])", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("uploadExcelFilesXlsx(MultipartFile, String[]) - end");
        }
        return list;
    }

    public String getCellValue(HSSFCell cell) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCellValue(HSSFCell) - start");
        }

        DecimalFormat dft = new DecimalFormat("########################.########"); 
        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            String returnString = dft.format(cell.getNumericCellValue());
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(HSSFCell) - end");
            }
            return returnString;
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            String returnString = Boolean.toString(cell.getBooleanCellValue());
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(HSSFCell) - end");
            }
            return returnString;
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            String returnString = cell.getRichStringCellValue().toString();
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(HSSFCell) - end");
            }
            return returnString;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getCellValue(HSSFCell) - end");
        }
        return "";
    }

    public String getCellValue(XSSFCell cell) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCellValue(XSSFCell) - start");
        }

        DecimalFormat dft = new DecimalFormat("########################.########"); 
        if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            String returnString = dft.format(cell.getNumericCellValue());
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(XSSFCell) - end");
            }
            return returnString;
        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            String returnString = Boolean.toString(cell.getBooleanCellValue());
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(XSSFCell) - end");
            }
            return returnString;
        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            String returnString = cell.getRichStringCellValue().toString();
            if (logger.isDebugEnabled()) {
                logger.debug("getCellValue(XSSFCell) - end");
            }
            return returnString;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getCellValue(XSSFCell) - end");
        }
        return "";
    }

}

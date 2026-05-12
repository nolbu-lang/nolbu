package com.cs.bcjis.report.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ReportFormulaUtil {

    private XSSFSheet sheet = null;
    private Map<String, ReportCellFormula> map = null;

    public ReportFormulaUtil(XSSFSheet sheet) {
        this.sheet = sheet;
        map = new HashMap<String, ReportCellFormula>();
    }

    public void addFormulaCell(String id, int rownum, int cellnum) {
        addFormulaCell(id, rownum, cellnum, "+");
    }

    public void addFormulaCell(String id, int rownum, int cellnum, String operator) {
        map.put(id, new ReportCellFormula(rownum, cellnum, operator));
    }

    public void addFormulaValue(String id, String value) {
        ReportCellFormula reportCellFormula = map.get(id);
        if (reportCellFormula == null) {
            return;
        }

        reportCellFormula.addValue(value);
    }

    public void writeCellFormula() {
        if (map == null || map.keySet() == null) {
            return;
        }

        Iterator<String> iterator = map.keySet().iterator();
        if (iterator == null) {
            return;
        }

        String key = "";
        ReportCellFormula reportCellFormula = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            reportCellFormula = map.get(key);

            reportCellFormula.setCellFormula(sheet);
        }

    }

    @Override
    public String toString() {
        return "ReportFormulaUtil [map=" + map + "]";
    }

}

package com.cs.bcjis.report.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.cs.bcjis.comm.util.BcjisCommUtil;

public class ReportCellFormula {

    private int rownum = 0;
    private int cellnum = 0;
    private String operator = null;
    private StringBuffer formulaBuf = null;

    public ReportCellFormula(int rownum, int cellnum, String operator) {
        this.rownum = rownum;
        this.cellnum = cellnum;
        this.operator = operator;
        this.formulaBuf = new StringBuffer();
    }

    public void addValue(String data) {
        if (BcjisCommUtil.isNullString(this.formulaBuf.toString()) == true) {
            this.formulaBuf.append(data);
        } else {
            this.formulaBuf.append(this.operator + data);
        }
    }

    public void setCellFormula(XSSFSheet sheet) {

        if (BcjisCommUtil.isNullString(this.formulaBuf.toString()) == true) {
            return;
        }

        Cell cell = sheet.getRow(rownum).getCell(cellnum);
        
        if(cell != null){
            cell.setCellFormula(this.formulaBuf.toString());
        }
    }

    @Override
    public String toString() {
        return "ReportCellFormula [rownum=" + rownum + ", cellnum=" + cellnum + ", operator=" + operator + ", formulaBuf=" + formulaBuf + "]";
    }
}

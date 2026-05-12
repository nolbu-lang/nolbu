package com.cs.bcjis.report.util;

public class Report070SaveFrscAmtsVO {
    private String frscNm = "";
    private long dmnFrscAmt = 0l;
    private long considerAmt = 0l;
    private long frscAmt = 0l;

    public Report070SaveFrscAmtsVO(String frscNm, long dmnFrscAmt, long considerAmt, long frscAmt) {
        this.frscNm = frscNm;
        this.dmnFrscAmt = dmnFrscAmt;
        this.considerAmt = considerAmt;
        this.frscAmt = frscAmt;
    }

    public String getFrscNm() {
        return frscNm;
    }

    public void setFrscNm(String frscNm) {
        this.frscNm = frscNm;
    }

    public long getDmnFrscAmt() {
        return dmnFrscAmt;
    }

    public void setDmnFrscAmt(long dmnFrscAmt) {
        this.dmnFrscAmt = dmnFrscAmt;
    }

    public long getConsiderAmt() {
        return considerAmt;
    }

    public void setConsiderAmt(long considerAmt) {
        this.considerAmt = considerAmt;
    }

    public long getFrscAmt() {
        return frscAmt;
    }

    public void setFrscAmt(long frscAmt) {
        this.frscAmt = frscAmt;
    }

    @Override
    public String toString() {
        return "Report070SaveFrscAmtsVO [frscNm=" + frscNm + ", dmnFrscAmt=" + dmnFrscAmt + ", considerAmt=" + considerAmt + ", frscAmt=" + frscAmt + "]";
    }

}

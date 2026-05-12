package com.cs.bcjis.report.util;

public class Report080SaveFrscAmtsVO {
    private String frscNm = "";
    private long dmnFrscAmt = 0l;
    private long preFrscAmt = 0l;
    private long frscAmt = 0l;

    public Report080SaveFrscAmtsVO(String frscNm, long dmnFrscAmt, long preFrscAmt, long frscAmt) {
        this.frscNm = frscNm;
        this.dmnFrscAmt = dmnFrscAmt;
        this.preFrscAmt = preFrscAmt;
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

    public long getPreFrscAmt() {
        return preFrscAmt;
    }

    public void setPreFrscAmt(long preFrscAmt) {
        this.preFrscAmt = preFrscAmt;
    }

    public long getFrscAmt() {
        return frscAmt;
    }

    public void setFrscAmt(long frscAmt) {
        this.frscAmt = frscAmt;
    }

    @Override
    public String toString() {
        return "Report080SaveFrscAmtsVO [frscNm=" + frscNm + ", dmnFrscAmt=" + dmnFrscAmt + ", preFrscAmt=" + preFrscAmt + ", frscAmt=" + frscAmt + "]";
    }

}

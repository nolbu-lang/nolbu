package com.cs.bcjis.report.util;

public class Report0F0SaveFrscAmtsVO {
    private String frscNm = "";
    private long totFrscAmt = 0l;  //총사업비
    private long preInvFrscAmt = 0l;  //기정액
    
    private long dmnFrscAmt = 0l;	//최종예산
    private long preFrscAmt = 0l;	//증감액
    private long preDefFrscAmt = 0l;	//요구액
    private long frscAmt = 0l;		//조정액

    public Report0F0SaveFrscAmtsVO(String frscNm, long totFrscAmt, long preInvFrscAmt, long dmnFrscAmt, long preFrscAmt, long preDefFrscAmt, long frscAmt) {
        this.frscNm = frscNm;
        this.setTotFrscAmt(totFrscAmt);
        this.setPreInvFrscAmt(preInvFrscAmt);

        this.setPreFrscAmt(preFrscAmt);
        this.setDmnFrscAmt(dmnFrscAmt);
        this.setPreDefFrscAmt(preDefFrscAmt);
        this.setFrscAmt(frscAmt);
    }

    public String getFrscNm() {
        return frscNm;
    }

    public void setFrscNm(String frscNm) {
        this.frscNm = frscNm;
    }

    public long getTotFrscAmt() {
		return totFrscAmt;
	}

	public void setTotFrscAmt(long totFrscAmt) {
		this.totFrscAmt = totFrscAmt;
	}

	public long getPreInvFrscAmt() {
		return preInvFrscAmt;
	}

	public void setPreInvFrscAmt(long preInvFrscAmt) {
		this.preInvFrscAmt = preInvFrscAmt;
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

	public long getPreDefFrscAmt() {
		return preDefFrscAmt;
	}

	public void setPreDefFrscAmt(long preDefFrscAmt) {
		this.preDefFrscAmt = preDefFrscAmt;
	}

	public long getFrscAmt() {
		return frscAmt;
	}

	public void setFrscAmt(long frscAmt) {
		this.frscAmt = frscAmt;
	}

	@Override
    public String toString() {
        return "Report010SaveFrscAmtsVO [frscNm=" + frscNm + ", totFrscAmt=" + totFrscAmt + ", preInvFrscAmt=" + preInvFrscAmt + "]";
    }

	

}

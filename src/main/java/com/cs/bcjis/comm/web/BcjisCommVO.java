package com.cs.bcjis.comm.web;

import java.io.Serializable;

import com.cs.bcjis.comm.util.BcjisCommUtil;

import net.sf.json.JSONObject;

public class BcjisCommVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6661283614350495911L;

    private int page = 1;
    private int rowNum = 1;
    private int totalCount = 0;

    public int getPage() {
        if (this.page > getTotalPages()) {
            return getTotalPages();
        }

        return this.page;
    }

    public void setPage(int page) {
        if (page < 1) {
            this.page = 1;
        } else {
            this.page = page;
        }
    }

    public void setPage(String page) {
        if (BcjisCommUtil.isNullString(page) == true) {
            this.page = 1;
        }

        try {
            this.page = Integer.parseInt(page);
        } catch (NumberFormatException nfe) {
            this.page = 1;
        }
    }

    public void setPage(Object page) {
        setPage(String.valueOf(page));
    }

    public int getRowNum() {
        return rowNum < 1 ? 10 : rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public void setRowNum(String rowNum) {
        if (BcjisCommUtil.isNullString(rowNum) == true) {
            this.rowNum = 1;
        }

        try {
            this.rowNum = Integer.parseInt(rowNum);
        } catch (NumberFormatException nfe) {
            this.rowNum = 1;
        }
    }

    public void setRowNum(Object page) {
        setRowNum(String.valueOf(page));
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        if (getRowNum() < 1) {
            return 1;
        }

        if (getTotalCount() < 1) {
            return 1;
        }

        int totalPage = getTotalCount() / getRowNum();
        if (getTotalCount() % getRowNum() != 0) {
            totalPage++;
        }

        return totalPage < 1 ? 1 : totalPage;
    }

    public int getStart() {
        return getRowNum() * (getPage() - 1) + 1;
    }

    public JSONObject toJsonData() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("page", getPage());
        jsonObject.put("totalPages", getTotalPages());
        jsonObject.put("totalCount", getTotalCount());

        return jsonObject;
    }

    public void setJsonData(JSONObject jsonObject) {
        jsonObject.put("page", getPage());
        jsonObject.put("rowNum", getRowNum());
        jsonObject.put("totalPages", getTotalPages());
        jsonObject.put("totalCount", getTotalCount());
    }
}

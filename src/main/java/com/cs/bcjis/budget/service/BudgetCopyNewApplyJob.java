package com.cs.bcjis.budget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 전년도예산조서적용[신규] 일괄적용 대기열 Job.
 * 다수 사용자 동시 적용 시 DB 경합을 줄이기 위해 FIFO로 순차 처리한다.
 */
public class BudgetCopyNewApplyJob {

    public static final String STATUS_QUEUED = "QUEUED";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_DONE = "DONE";
    public static final String STATUS_ERROR = "ERROR";

    private String jobId;
    private String userId;
    private String userNm;
    private String status;
    private String message;
    private int totalCnt;
    private int appliedCnt;
    private int queuePos;
    private long createdAt;
    private long startedAt;
    private long finishedAt;
    private List<Map> mappings;

    public BudgetCopyNewApplyJob() {
        this.mappings = new ArrayList<Map>();
        this.status = STATUS_QUEUED;
        this.createdAt = System.currentTimeMillis();
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getAppliedCnt() {
        return appliedCnt;
    }

    public void setAppliedCnt(int appliedCnt) {
        this.appliedCnt = appliedCnt;
    }

    public int getQueuePos() {
        return queuePos;
    }

    public void setQueuePos(int queuePos) {
        this.queuePos = queuePos;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }

    @SuppressWarnings("rawtypes")
    public List<Map> getMappings() {
        return mappings;
    }

    @SuppressWarnings("rawtypes")
    public void setMappings(List<Map> mappings) {
        this.mappings = mappings;
    }
}

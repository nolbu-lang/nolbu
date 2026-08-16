package com.cs.bcjis.budget.service;

import java.util.List;
import java.util.Map;

public interface BudgetCopyNewService {
    
    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectCopyNewMapList(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void copyReportBatch(List<Map> mappings) throws Exception;

    /**
     * 기정예산 사업의 조서·집계 분류(대/중/소·국고·집계표)만 적용대상에 상속.
     * 전년도 예산적용(금액만)에서도 호출한다.
     */
    @SuppressWarnings("rawtypes")
    public void inheritReportNatureOnly(Map map) throws Exception;
    
}

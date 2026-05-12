package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetCopyDAO")
public class BudgetCopyDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyReportList(Map map) throws Exception {

        if ("070".equals(map.get("reportCd")) == true) {
            return list("BudgetCopy.selectCopyReportList070", map);
        }

        map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));

        return list("BudgetCopy.selectCopyReportList", map);
    }

}

package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("budgetSelectDAO")
public class BudgetSelectDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoList(String reportCd, Map map) throws Exception{
        if("070".equals(reportCd) == true){
            return list("BudgetSelect.selectDgrCompoList070", map);
        }
        
        return list("BudgetSelect.selectDgrCompoList", map);
    }

    /** 예산심사조서·집계표항목선택 경량 세세목 목록 (하위 호환) */
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoLeafListFast(Map map) throws Exception {
        return list("BudgetSelectNew.selectDgrCompoLeafListFast", map);
    }

    /** [class] 조서·집계 항목선택 — 분류·금액 */
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoLeafListClass(Map map) throws Exception {
        return list("BudgetSelectNew.selectDgrCompoLeafListClass", map);
    }

    /** [attr] 보고항목·사전절차 — 분류 완료 세세목 */
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoLeafListAttr(Map map) throws Exception {
        return list("BudgetSelectNew.selectDgrCompoLeafListAttr", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportKeyList(String reportCd, Map map) throws Exception{
        if("070".equals(reportCd) == true){
            return list("BudgetSelect.selectReportKeyList070", map);
        }
        
        return list("BudgetSelect.selectReportKeyList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectReportKeyListNew(String reportCd, Map map) throws Exception{
    	if("070".equals(reportCd) == true){
    		return list("BudgetSelect.selectReportKeyListNew070", map);
    	}
    	
    	return list("BudgetSelect.selectReportKeyListNew", map);
    }

    /** 저장용: TB_REPORT 키만 조회 (조인 없는 경량 쿼리) */
    @SuppressWarnings("rawtypes")
    public List selectReportKeyListFast(Map map) throws Exception {
        return list("BudgetSelectNew.selectReportKeyListFast", map);
    }
}

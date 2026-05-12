package com.cs.bcjis.budget.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetApplyService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Service("budgetApplyService")
public class BudgetApplyServiceImpl implements BudgetApplyService {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetApplyDAO")
    private BudgetApplyDAO budgetApplyDAO;

    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompoRlkList(Map map) throws Exception {
        return budgetApplyDAO.selectDgrcompoRlkList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception {
        return budgetApplyDAO.selectDgrcompoList(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveDgrcompoDatas(JSONObject jsonParam) throws Exception {
        List dgrCompoDatas = jsonParam.getJSONArray("dgrCompoDatas");
        JSONObject tempParam = null;

        int dgrcompCnt = 0;

        for (int i = 0; i < dgrCompoDatas.size(); i++) {
            tempParam = (JSONObject) dgrCompoDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            dgrcompCnt = budgetApplyDAO.selectDgrcompoCnt(tempParam);
            if (dgrcompCnt > 0) {
                budgetApplyDAO.updateDgrcompo(tempParam);
                budgetApplyDAO.updateDgrcompofrsc(tempParam);
                budgetApplyDAO.updateDgrcompochar(tempParam);
            } else {
                budgetApplyDAO.insertDgrcompo(tempParam);
                budgetApplyDAO.insertDgrcompofrsc(tempParam);
                budgetApplyDAO.insertDgrcompochar(tempParam);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void detlDgrcompoDatas(JSONObject jsonParam) throws Exception {
        List dgrCompoDatas = jsonParam.getJSONArray("dgrCompoDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < dgrCompoDatas.size(); i++) {
            tempParam = (JSONObject) dgrCompoDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));

            budgetApplyDAO.deleteDgrcompochar(tempParam);
            budgetApplyDAO.deleteDgrcompofrsc(tempParam);

            reportCommDAO.deleteAllReport(tempParam);
            budgetApplyDAO.deleteDgrcompo(tempParam);
            //집계표 데이터도 삭제
            //budgetSheetSelectDAO.deleteSheet(tempParam);
            
            String grpId = (String) tempParam.get("grpId");
            String cngType = (String) tempParam.get("cngType"); 
            
            if(grpId != null && !"".equals(grpId)){
            	if(cngType != null && "CH01".equals(cngType)){
            		//compoData 그룹데이터 초기화
            		budgetApplyDAO.updateDgrcompoCngData(tempParam);
            		
            		//cng_history 테이블 cngType을 'CH03' 삭제로 업데이트
            		tempParam.put("cngType", "CH03");
            		budgetApplyDAO.updateCngHistoryData(tempParam);
            	}
            }
            
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateUpDgrcompoInfoAll(Map param) throws Exception {
        param.put("compoLevel", "4");
        budgetCommDAO.updateUpDgrcompoInfoAll(param);

        param.put("compoLevel", "3");
        budgetCommDAO.updateUpDgrcompoInfoAll(param);

        param.put("compoLevel", "2");
        budgetCommDAO.updateUpDgrcompoInfoAll(param);
    }
}

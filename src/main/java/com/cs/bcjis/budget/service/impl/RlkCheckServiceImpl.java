package com.cs.bcjis.budget.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.RlkCheckService;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Service("rlkCheckService")
public class RlkCheckServiceImpl implements RlkCheckService {

    @Resource(name = "rlkCheckDAO")
    private RlkCheckDAO rlkCheckDAO;

    @Resource(name = "csCngHisGnrService")
    private EgovIdGnrService csCngHisGnrService;

    @SuppressWarnings("rawtypes")
    public List selectDeptList(Map map) throws Exception {
        return rlkCheckDAO.selectDeptList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDgrcompoList(Map map) throws Exception {
        return rlkCheckDAO.selectDgrcompoList(map);
    }

    /*
     * 적용 처리
     * 1. 변경이력 헤더 입력(TB_CNG_HISTORY, CNG_TYPE='CH04')
     * 2. 체크된 세부사업(들)의 DEPT_CD를 좌측에서 선택한 부서 코드로 UPDATE (UPDATE 직전 TB_DGRCOMPO_H 자동 스냅샷)
     * 3. 변경 후 상태를 TB_DGRCOMPO_ORI에 스냅샷 (CNG_HISTORY_ID로 태깅)
     * DBIZ_CD/TE_BGT_COMPO_ID/TE_BGT_COMPO_SEQ/설명·금액 컬럼, TB_DGRCOMPOFRSC/TB_DGRCOMPOCHAR는 손대지 않음.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void applyDeptCd(JSONObject jsonParam) throws Exception {
        String userId = (String) jsonParam.get("userId");
        String fisYear = (String) jsonParam.get("fisYear");
        String bgtDgr = String.valueOf(jsonParam.get("bgtDgr"));
        String deptCd = (String) jsonParam.get("deptCd");
        String deptNm = (String) jsonParam.get("deptNm");

        List checkedDatas = jsonParam.getJSONArray("checkedDatas");
        if (checkedDatas == null || checkedDatas.isEmpty()) {
            return;
        }

        String cngHistoryId = csCngHisGnrService.getNextStringId();

        JSONObject historyParam = new JSONObject();
        historyParam.put("cngHistoryId", cngHistoryId);
        historyParam.put("title", "연계정보확인 부서정보 적용");
        historyParam.put("indiBns", deptNm);
        historyParam.put("note", checkedDatas.size() + "건 세부사업의 소속 부서를 '" + deptCd + "(" + deptNm + ")'(으)로 변경");
        historyParam.put("userId", userId);
        rlkCheckDAO.insertCngHistory(historyParam);

        List<String> teBgtCompoIds = new ArrayList<String>();
        JSONObject tempParam = null;
        for (int i = 0; i < checkedDatas.size(); i++) {
            tempParam = (JSONObject) checkedDatas.get(i);

            String teBgtCompoId = (String) tempParam.get("teBgtCompoId");
            teBgtCompoIds.add(teBgtCompoId);

            tempParam.put("fisYear", fisYear);
            tempParam.put("bgtDgr", bgtDgr);
            tempParam.put("deptCd", deptCd);
            tempParam.put("userId", userId);
            rlkCheckDAO.updateDgrcompoDept(tempParam);
        }

        JSONObject oriParam = new JSONObject();
        oriParam.put("fisYear", fisYear);
        oriParam.put("bgtDgr", bgtDgr);
        oriParam.put("cngHistoryId", cngHistoryId);
        oriParam.put("userId", userId);
        oriParam.put("teBgtCompoIds", teBgtCompoIds);
        rlkCheckDAO.insertDgrCompoOri(oriParam);
    }
}

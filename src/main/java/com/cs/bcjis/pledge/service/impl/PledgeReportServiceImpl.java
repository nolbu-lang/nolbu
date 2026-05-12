package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.pledge.service.PledgeReportService;

@Service("pledgeReportService")
public class PledgeReportServiceImpl implements PledgeReportService {
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;

    @Resource(name = "pledgeReportDAO")
    private PledgeReportDAO pledgeReportDAO;

    @SuppressWarnings("rawtypes")
    public List selectPledgeList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeList(map);
    }

    @SuppressWarnings("rawtypes")
    public void savePledge(JSONObject jsonParam) throws Exception {
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        JSONObject tempParam = null;

        for (int i = 0; i < saveDatas.size(); i++) {
            tempParam = (JSONObject) saveDatas.get(i);
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));

            pledgeReportDAO.updatePledge(tempParam);

        }
    }

    @SuppressWarnings("rawtypes")
    public Map selectPledgeInfo(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoExcelList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeInfoExcelList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeTotList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeTotList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeSumList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeSumList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeOfficeList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeOfficeList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeBizList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeBizList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeDeptList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeDeptList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeEtcList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeEtcList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeGuGunList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeGuGunList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeGuGunDetlList(Map map) throws Exception {
        return pledgeReportDAO.selectPledgeGuGunDetlList(map);
    }
}

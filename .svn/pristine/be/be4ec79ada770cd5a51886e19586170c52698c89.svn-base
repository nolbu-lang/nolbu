package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("pledgeReportDAO")
public class PledgeReportDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectPledgeList(Map map) throws Exception {
        return list("PledgeReport.selectPledgeList", map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updatePledge(Map map) throws Exception {
        try {
            map.put("hisFg", "020");
            insert("BcjisHisComm.insertTbPledgeH", map);
        } catch (Exception e) {
            logger.error("updatePledge(map)", e);
        }

        update("PledgeReport.updatePledge", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectPledgeInfo(Map map) throws Exception {
        return (Map) selectByPk("PledgeReport.selectPledgeInfo", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoExcelList(Map map) throws Exception {
        return list("PledgeReport1.selectPledgeInfoExcelList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeTotList(Map map) throws Exception {
        return list("PledgeReport2.selectPledgeTotList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeSumList(Map map) throws Exception {
        return list("PledgeReport2.selectPledgeSumList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeOfficeList(Map map) throws Exception {
        return list("PledgeReport3.selectPledgeOfficeList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeBizList(Map map) throws Exception {
        return list("PledgeReport3.selectPledgeBizList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeDeptList(Map map) throws Exception {
        return list("PledgeReport3.selectPledgeDeptList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeEtcList(Map map) throws Exception {
        return list("PledgeReport3.selectPledgeEtcList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeGuGunList(Map map) throws Exception {
        return list("PledgeReport4.selectPledgeGuGunList", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectPledgeGuGunDetlList(Map map) throws Exception {
        return list("PledgeReport4.selectPledgeGuGunDetlList", map);
    }

}

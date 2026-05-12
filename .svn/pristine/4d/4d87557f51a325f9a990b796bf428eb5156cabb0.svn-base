package com.cs.bcjis.pledge.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.pledge.service.PledgeMatchService;

@Service("pledgeMatchService")
public class PledgeMatchServiceImpl implements PledgeMatchService {

    @Resource(name = "pledgeMatchDAO")
    private PledgeMatchDAO pledgeMatchDAO;

    @SuppressWarnings("rawtypes")
    public List selectPledgeInfoIdList(Map map) throws Exception {
        return pledgeMatchDAO.selectPledgeInfoIdList(map);
    }

    @SuppressWarnings("rawtypes")
    public void updatePledgeInfoId(JSONObject jsonParam) throws Exception {
        JSONObject tempParam = null;
        List saveDatas = jsonParam.getJSONArray("saveDatas");
        for(int i = 0; i < saveDatas.size(); i++){
            tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));

            pledgeMatchDAO.updatePledgeInfoId(tempParam);
        }
    }
}

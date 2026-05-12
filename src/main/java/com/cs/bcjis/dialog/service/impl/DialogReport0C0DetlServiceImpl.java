package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogReport0C0DetlService;

@Service("dialogReport0C0DetlService")
public class DialogReport0C0DetlServiceImpl implements DialogReport0C0DetlService {
    @Resource(name = "dialogReport0C0DetlDAO")
    private DialogReport0C0DetlDAO dialogReport0C0DetlDAO;

    @SuppressWarnings("rawtypes")
    public List selectReport0C0D(Map map) throws Exception {
        return dialogReport0C0DetlDAO.selectReport0C0D(map);
    }

    @SuppressWarnings("rawtypes")
    public void saveReport0C0D(JSONObject jsonParam) throws Exception {
        dialogReport0C0DetlDAO.deleteReport0C0D(jsonParam);

        JSONObject tempParam = null;
        List report0C0Ds = jsonParam.getJSONArray("report0C0Ds");

        for (int i = 0; i < report0C0Ds.size(); i++) {
            tempParam = (JSONObject) report0C0Ds.get(i);

            tempParam.put("reportCd", jsonParam.get("reportCd"));
            tempParam.put("reportDetlCd", jsonParam.get("reportDetlCd"));
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));

            dialogReport0C0DetlDAO.insertReport0C0D(tempParam);
        }
    }

    @SuppressWarnings("rawtypes")
    public Map selectReport0C0DDgrcompo(Map map) throws Exception {
        return dialogReport0C0DetlDAO.selectReport0C0DDgrcompo(map);
    }
}

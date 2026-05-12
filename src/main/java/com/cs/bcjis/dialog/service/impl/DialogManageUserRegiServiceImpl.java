package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogManageUserRegiService;

@Service("dialogManageUserRegiService")
public class DialogManageUserRegiServiceImpl implements DialogManageUserRegiService {
    @Resource(name = "dialogManageUserRegiDAO")
    private DialogManageUserRegiDAO dialogManageUserRegiDAO;

    @SuppressWarnings("rawtypes")
    public Map selectDialogManageUser(Map map) throws Exception {
        return dialogManageUserRegiDAO.selectDialogManageUser(map);
    }
    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserUser(Map map) throws Exception {
        dialogManageUserRegiDAO.insertDialogManageUserUser(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserPowgrp(Map map) throws Exception {
        dialogManageUserRegiDAO.insertDialogManageUserPowgrp(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectManageUserRegiReport(Map map) throws Exception {
        return dialogManageUserRegiDAO.selectManageUserRegiReport(map);
    }

    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserReport(JSONObject jsonParam) throws Exception {

        List reportDatas = jsonParam.getJSONArray("reportDatas");
        JSONObject tempParam = null;
        

        dialogManageUserRegiDAO.updateManageUserReportUseYn(jsonParam);
        for (int i = 0; i < reportDatas.size(); i++) {
            tempParam = (JSONObject) reportDatas.get(i);

            tempParam.put("reportUserId", jsonParam.get("reportUserId"));
            tempParam.put("userId", jsonParam.get("userId"));
         
            dialogManageUserRegiDAO.updateManageUserReport(tempParam);
        }
    }

    @SuppressWarnings("rawtypes")
    public List selectManageUserRegiDept(Map map) throws Exception {
        return dialogManageUserRegiDAO.selectManageUserRegiDept(map);
    }

    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserDept(JSONObject jsonParam) throws Exception {

        List deptDatas = jsonParam.getJSONArray("deptDatas");
        JSONObject tempParam = null;
        

        dialogManageUserRegiDAO.updateManageUserDeptUseYn(jsonParam);
        for (int i = 0; i < deptDatas.size(); i++) {
            tempParam = (JSONObject) deptDatas.get(i);

            tempParam.put("deptUserId", jsonParam.get("deptUserId"));
            tempParam.put("userId", jsonParam.get("userId"));
         
            dialogManageUserRegiDAO.updateManageUserDept(tempParam);
        }
    }
}

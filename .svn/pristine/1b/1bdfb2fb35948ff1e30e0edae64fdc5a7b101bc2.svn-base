package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogManageUserRegiDAO")
public class DialogManageUserRegiDAO extends BcjisCommAbstractDAO {
    @SuppressWarnings("rawtypes")
    public Map selectDialogManageUser(Map map) throws Exception {
        return (Map) selectByPk("DialogManageUserRegi.selectDialogManageUser", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserUser(Map map) {
        insert("DialogManageUserRegi.insertDialogManageUserUser", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserPowgrp(Map map) {
        insert("DialogManageUserRegi.insertDialogManageUserPowgrp", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectManageUserRegiReport(Map map) throws Exception{
        return list("DialogManageUserRegi.selectManageUserRegiReport", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageUserReportUseYn(Map map) {
        update("DialogManageUserRegi.updateManageUserReportUseYn", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageUserReport(Map map) {
        update("DialogManageUserRegi.updateManageUserReport", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectManageUserRegiDept(Map map) throws Exception{
        return list("DialogManageUserRegi.selectManageUserRegiDept", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageUserDeptUseYn(Map map) {
        update("DialogManageUserRegi.updateManageUserDeptUseYn", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateManageUserDept(Map map) {
        update("DialogManageUserRegi.updateManageUserDept", map);
    }
}

package com.cs.bcjis.dialog.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;


public interface DialogManageUserRegiService {
    
    @SuppressWarnings("rawtypes")
    public Map selectDialogManageUser(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserUser(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void insertDialogManageUserPowgrp(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public List selectManageUserRegiReport(Map map) throws Exception;

    public void insertDialogManageUserReport(JSONObject jsonParam) throws Exception;

    @SuppressWarnings("rawtypes")    
    public List selectManageUserRegiDept(Map map) throws Exception;

    public void insertDialogManageUserDept(JSONObject jsonParam) throws Exception;
}

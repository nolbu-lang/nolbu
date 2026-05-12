package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogManageUserModifyService;

@Service("dialogManageUserModifyService")
public class DialogManageUserModifyServiceImpl implements DialogManageUserModifyService {
    @Resource(name = "dialogManageUserModifyDAO")
    private DialogManageUserModifyDAO dialogManageUserModifyDAO;

    @SuppressWarnings("rawtypes")
    public void updateDialogManageUser(Map map) throws Exception {
        dialogManageUserModifyDAO.updateDialogManageUser(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateDialogManageUserPowgrp(Map map) throws Exception {
        dialogManageUserModifyDAO.updateDialogManageUserPowgrp(map);
    }
}

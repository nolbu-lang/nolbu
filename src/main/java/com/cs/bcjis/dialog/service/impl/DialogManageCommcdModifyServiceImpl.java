package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogManageCommcdModifyService;

@Service("dialogManageCommcdModifyService")
public class DialogManageCommcdModifyServiceImpl implements DialogManageCommcdModifyService {
    @Resource(name = "dialogManageCommcdModifyDAO")
    private DialogManageCommcdModifyDAO dialogManageCommcdModifyDAO;

    @SuppressWarnings("rawtypes")
    public void updateDialogManageCommcd(Map map) throws Exception {
        dialogManageCommcdModifyDAO.updateDialogManageCommcd(map);
    }
}

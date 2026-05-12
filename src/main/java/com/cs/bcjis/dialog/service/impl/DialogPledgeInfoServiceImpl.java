package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogPledgeInfoService;

@Service("dialogPledgeInfoService")
public class DialogPledgeInfoServiceImpl implements DialogPledgeInfoService {
    @Resource(name = "dialogPledgeInfoDAO")
    private DialogPledgeInfoDAO dialogPledgeInfoDAO;

    @SuppressWarnings("rawtypes")
    public void insertPledgeInfo(Map map) throws Exception {
        dialogPledgeInfoDAO.insertPledgeInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public void updatePledgeInfo(Map map) throws Exception {
        dialogPledgeInfoDAO.updatePledgeInfo(map);
    }
}

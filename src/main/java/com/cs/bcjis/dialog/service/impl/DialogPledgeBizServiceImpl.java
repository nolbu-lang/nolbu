package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogPledgeBizService;

@Service("dialogPledgeBizService")
public class DialogPledgeBizServiceImpl implements DialogPledgeBizService {
    @Resource(name = "dialogPledgeBizDAO")
    private DialogPledgeBizDAO dialogPledgeBizDAO;

    @SuppressWarnings("rawtypes")
    public void insertPledgeBiz(Map map) throws Exception {
        dialogPledgeBizDAO.insertPledgeBiz(map);
    }

    @SuppressWarnings("rawtypes")
    public void updatePledgeBiz(Map map) throws Exception {
        dialogPledgeBizDAO.updatePledgeBiz(map);
    }
}

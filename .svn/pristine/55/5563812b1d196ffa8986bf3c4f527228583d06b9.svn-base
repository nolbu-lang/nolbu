package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogManageCloseHisService;

@Service("dialogManageCloseHisService")
public class DialogManageCloseHisServiceImpl implements DialogManageCloseHisService {
    @Resource(name = "dialogManageCloseHisDAO")
    private DialogManageCloseHisDAO dialogManageCloseHisDAO;

    @SuppressWarnings("rawtypes")
    public List selectManageCloseHisList(Map map) throws Exception {
        return dialogManageCloseHisDAO.selectManageCloseHisList(map);
    }
    
}

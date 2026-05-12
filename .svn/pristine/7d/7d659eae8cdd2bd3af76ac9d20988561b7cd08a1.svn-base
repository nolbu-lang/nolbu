package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogDgrDeptSeltService;

@Service("dialogDgrDeptSeltService")
public class DialogDgrDeptSeltServiceImpl implements DialogDgrDeptSeltService {
    @Resource(name = "dialogDgrDeptSeltDAO")
    private DialogDgrDeptSeltDAO dialogDgrDeptSeltDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrDeptSeltList(Map map) throws Exception {
        return dialogDgrDeptSeltDAO.selectDgrDeptSeltList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectDgrDeptSeltListCnt(Map map) throws Exception {
        return dialogDgrDeptSeltDAO.selectDgrDeptSeltListCnt(map);
    }
}

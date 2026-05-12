package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogBgtFrscAmtService;

@Service("dialogBgtFrscAmtService")
public class DialogBgtFrscAmtServiceImpl implements DialogBgtFrscAmtService {
    @Resource(name = "dialogBgtFrscAmtDAO")
    private DialogBgtFrscAmtDAO dialogBgtFrscAmtDAO;

    @SuppressWarnings("rawtypes")
    public List selectDgrfrscCompoSeqList(Map map) throws Exception {
        return dialogBgtFrscAmtDAO.selectDgrfrscCompoSeqList(map);
    }
}

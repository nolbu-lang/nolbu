package com.cs.bcjis.dialog.service;

import java.util.List;
import java.util.Map;

public interface DialogDgrDeptSeltService {

    @SuppressWarnings("rawtypes")
    public List selectDgrDeptSeltList(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public int selectDgrDeptSeltListCnt(Map map) throws Exception;

}

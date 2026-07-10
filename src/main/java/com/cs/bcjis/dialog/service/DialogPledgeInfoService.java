package com.cs.bcjis.dialog.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cs.bcjis.comm.web.BcjisUserVO;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public interface DialogPledgeInfoService {

    @SuppressWarnings("rawtypes")
    public void insertPledgeInfo(Map map) throws Exception;

    @SuppressWarnings("rawtypes")
    public void updatePledgeInfo(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public EgovMap excelUploadPledgeInfo(MultipartFile multi, BcjisUserVO bcjisUserVO) throws Exception;

}

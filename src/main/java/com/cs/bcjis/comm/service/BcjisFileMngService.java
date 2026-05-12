package com.cs.bcjis.comm.service;

import java.util.List;

import com.cs.bcjis.comm.web.BcjisFileVO;

public interface BcjisFileMngService {

    public List<BcjisFileVO> selectFileInfs(BcjisFileVO bcjisFileVO) throws Exception;

    public BcjisFileVO selectFileInf(BcjisFileVO bcjisFileVO) throws Exception;

    @SuppressWarnings("rawtypes")
    public String insertFileInfs(List fvoList) throws Exception;

    @SuppressWarnings("rawtypes")
    public void updateFileInfs(List fvoList) throws Exception;

    public void insertFile(BcjisFileVO vo) throws Exception;

    public void insertFileDetl(BcjisFileVO vo) throws Exception;
}

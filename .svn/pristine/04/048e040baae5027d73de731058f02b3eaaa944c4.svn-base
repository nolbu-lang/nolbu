package com.cs.bcjis.comm.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.comm.service.BcjisFileMngService;
import com.cs.bcjis.comm.web.BcjisFileVO;

@Service("bcjisFileMngService")
public class BcjisFileMngServiceImpl implements BcjisFileMngService {

    @Resource(name = "bcjisFileMngDAO")
    private BcjisFileMngDAO bcjisFileMngDAO;

    public List<BcjisFileVO> selectFileInfs(BcjisFileVO bcjisFileVO) throws Exception {
        return bcjisFileMngDAO.selectFileInfs(bcjisFileVO);
    }

    public BcjisFileVO selectFileInf(BcjisFileVO bcjisFileVO) throws Exception {
        return bcjisFileMngDAO.selectFileInf(bcjisFileVO);
    }

    @SuppressWarnings("rawtypes")
    public String insertFileInfs(List fvoList) throws Exception {
        String atchFileId = "";

        if (fvoList.size() != 0) {
            atchFileId = bcjisFileMngDAO.insertFileInfs(fvoList);
        }

        return atchFileId;
    }

    @SuppressWarnings("rawtypes")
    public void updateFileInfs(List fvoList) throws Exception {
        bcjisFileMngDAO.updateFileInfs(fvoList);
    }

    public void insertFile(BcjisFileVO vo) throws Exception {
        bcjisFileMngDAO.insertFile(vo);
    }

    public void insertFileDetl(BcjisFileVO vo) throws Exception {
        bcjisFileMngDAO.insertFileDetl(vo);
    }

}

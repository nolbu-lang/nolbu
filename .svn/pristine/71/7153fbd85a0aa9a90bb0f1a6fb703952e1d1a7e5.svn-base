package com.cs.bcjis.comm.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;
import com.cs.bcjis.comm.web.BcjisFileVO;

@Repository("bcjisFileMngDAO")
public class BcjisFileMngDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("unchecked")
    public List<BcjisFileVO> selectFileInfs(BcjisFileVO bcjisFileVO) throws Exception {
        return list("BcjisFile.selectFileList", bcjisFileVO);
    }

    public BcjisFileVO selectFileInf(BcjisFileVO bcjisFileVO) throws Exception {
        return (BcjisFileVO) selectByPk("BcjisFile.selectFileInf", bcjisFileVO);
    }

    @SuppressWarnings("rawtypes")
    public String insertFileInfs(List fileList) throws Exception {
        BcjisFileVO vo = (BcjisFileVO) fileList.get(0);
        String atchFileId = vo.getAtchFileId();

        insert("BcjisFile.insertTbFile", vo);

        Iterator iter = fileList.iterator();
        while (iter.hasNext()) {
            vo = (BcjisFileVO) iter.next();

            insert("BcjisFile.insertTbFileDetl", vo);
        }

        return atchFileId;
    }

    @SuppressWarnings("rawtypes")
    public void updateFileInfs(List fileList) throws Exception {
        BcjisFileVO vo;
        BcjisFileVO deleteVo = new BcjisFileVO();

        Iterator iter = fileList.iterator();
        int cnt = 0;
        while (iter.hasNext()) {
            vo = (BcjisFileVO) iter.next();
            if (cnt == 0) {
                deleteVo.setAtchFileId(vo.getAtchFileId());
                delete("BcjisFile.deleteFileInf", deleteVo);
            }

            insert("BcjisFile.insertTbFileDetl", vo);
        }
    }
    
    public void insertFile(BcjisFileVO vo) throws Exception {
        insert("BcjisFile.insertTbFile", vo);
    }
    
    public void insertFileDetl(BcjisFileVO vo) throws Exception {
        insert("BcjisFile.insertTbFileDetl", vo);
    }
}

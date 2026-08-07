package com.cs.bcjis.bizdesc.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("bizDescMatchDAO")
public class BizDescMatchDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectReportCandidates(Map map) throws Exception {
        return list("BizDescMatch.selectReportCandidates", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectFileList(Map map) throws Exception {
        return list("BizDescMatch.selectFileList", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectFile(Map map) throws Exception {
        return (Map) selectByPk("BizDescMatch.selectFile", map);
    }

    public void insertFile(Map map) throws Exception {
        insert("BizDescMatch.insertFile", map);
    }

    public void deleteFile(Map map) throws Exception {
        delete("BizDescMatch.deleteFile", map);
    }

    public void deleteMatchByFile(Map map) throws Exception {
        delete("BizDescMatch.deleteMatchByFile", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectMatch(Map map) throws Exception {
        return (Map) selectByPk("BizDescMatch.selectMatch", map);
    }

    public void insertMatch(Map map) throws Exception {
        insert("BizDescMatch.insertMatch", map);
    }

    public void updateMatch(Map map) throws Exception {
        update("BizDescMatch.updateMatch", map);
    }

    public void deleteMatch(Map map) throws Exception {
        delete("BizDescMatch.deleteMatch", map);
    }
}

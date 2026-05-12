package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogReport070OrderYmdModifyDAO")
public class DialogReport070OrderYmdModifyDAO extends BcjisCommAbstractDAO {

    @SuppressWarnings("rawtypes")
    public List selectDialogReport070OList(Map map) throws Exception {
        return list("DialogReport070OrderYmdModify.selectDialogReport070OList", map);
    }

    @SuppressWarnings("rawtypes")
    public void insertDialogReport070O(Map map) throws Exception {
        insert("DialogReport070OrderYmdModify.insertDialogReport070O", map);
    }

    @SuppressWarnings("rawtypes")
    public void updateDialogReport070O(Map map) throws Exception {
        update("DialogReport070OrderYmdModify.updateDialogReport070O", map);
    }

    @SuppressWarnings("rawtypes")
    public void deleteDialogReport070O(Map map) throws Exception {
        update("DialogReport070OrderYmdModify.deleteDialogReport070O", map);
    }

    @SuppressWarnings("rawtypes")
    public List selectDialogReport070OKeyList(Map map) throws Exception {
        return list("DialogReport070OrderYmdModify.selectDialogReport070OKeyList", map);
    }

}

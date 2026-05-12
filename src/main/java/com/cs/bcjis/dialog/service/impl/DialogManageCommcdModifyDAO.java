package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogManageCommcdModifyDAO")
public class DialogManageCommcdModifyDAO extends BcjisCommAbstractDAO {
   
    @SuppressWarnings("rawtypes")
    public void updateDialogManageCommcd(Map map){
        update("DialogManageCommcdModify.updateDialogManageCommcd", map);
    }
  
}

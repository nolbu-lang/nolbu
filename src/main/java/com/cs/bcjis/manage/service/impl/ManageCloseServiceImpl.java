package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.manage.service.ManageCloseService;


@Service("manageCloseService")
public class ManageCloseServiceImpl  implements ManageCloseService {
    
    @Resource(name="manageCloseDAO")
    private ManageCloseDAO manageCloseDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectManageCloseList(Map map) throws Exception { 
        return manageCloseDAO.selectManageCloseList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectManageCloseListCnt(Map map) throws Exception {
        return manageCloseDAO.selectManageCloseListcnt(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateManageClose(Map map) throws Exception {
        manageCloseDAO.updateManageClose(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertManageCloseHis(Map map) throws Exception {
        manageCloseDAO.insertManageCloseHis(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateManageCloseHis(Map map) throws Exception {
        manageCloseDAO.updateManageCloseHis(map);
    }
}

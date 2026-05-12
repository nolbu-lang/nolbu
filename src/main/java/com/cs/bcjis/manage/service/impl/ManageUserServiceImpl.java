package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;

import com.cs.bcjis.manage.service.ManageUserService;
import com.cs.bcjis.manage.service.impl.ManageUserDAO;


@Service("manageUserService")
public class ManageUserServiceImpl  implements ManageUserService {
    
    @Resource(name="manageUserDAO")
    private ManageUserDAO manageUserDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectManageUserUserList(Map map) throws Exception { 
        return manageUserDAO.selectManageUserUserList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectManageUserUserListCnt(Map map) throws Exception {
        return manageUserDAO.selectManageUserUserListcnt(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void deleteUser(Map map) throws Exception {

        manageUserDAO.deleteManageUser(map);
        manageUserDAO.deleteManageUserPowgbr(map);
        
    }

}

package com.cs.bcjis.login.service.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;
import com.cs.bcjis.comm.web.BcjisUserVO;

@Repository("loginDAO")
public class LoginDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public BcjisUserVO selectLoginInfo(Map map) throws Exception{
        return (BcjisUserVO)selectByPk("Login.selectLoginInfo", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateUserLoginInfo(Map map) throws Exception{
        update("Login.updateUserLoginInfo", map);
    }
}

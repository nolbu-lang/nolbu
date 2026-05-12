package com.cs.bcjis.login.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.login.service.LoginService;

@Service("loginService")
public class LoginServiceImpl  implements LoginService {
    @Resource(name="loginDAO")
    private LoginDAO loginDAO;
    
    @SuppressWarnings("rawtypes")      
    public BcjisUserVO selectLoginInfo(Map map) throws Exception {
        return loginDAO.selectLoginInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public void updateUserLoginInfo(Map map) throws Exception {
        loginDAO.updateUserLoginInfo(map);
    }
}

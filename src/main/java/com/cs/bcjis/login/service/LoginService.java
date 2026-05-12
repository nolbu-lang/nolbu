package com.cs.bcjis.login.service;

import java.util.Map;

import com.cs.bcjis.comm.web.BcjisUserVO;

public interface LoginService {

    @SuppressWarnings("rawtypes")
    public BcjisUserVO selectLoginInfo(Map map) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public void updateUserLoginInfo(Map map) throws Exception;
}

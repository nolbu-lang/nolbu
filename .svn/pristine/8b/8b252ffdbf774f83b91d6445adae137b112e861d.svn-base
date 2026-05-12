package com.cs.bcjis.comm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cs.bcjis.comm.service.BcjisUserDetailsService;

import egovframework.rte.fdl.cmmn.AbstractServiceImpl;

public class BcjisUserDetailsServiceImpl extends AbstractServiceImpl implements BcjisUserDetailsService {

    public Object getAuthenticatedUser() {
        return RequestContextHolder.getRequestAttributes().getAttribute("bcjisUserVO", RequestAttributes.SCOPE_SESSION);
    }

    public List<String> getAuthorities() {
        List<String> listAuth = new ArrayList<String>();

        return listAuth;
    }

    public Boolean isAuthenticated() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return false;
        } else {
            if (RequestContextHolder.getRequestAttributes().getAttribute("bcjisUserVO", RequestAttributes.SCOPE_SESSION) == null) {
                return false;
            } else {
                return true;
            }
        }
    }

}

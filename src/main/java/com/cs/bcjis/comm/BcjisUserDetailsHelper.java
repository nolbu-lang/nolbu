package com.cs.bcjis.comm;

import java.util.List;

import com.cs.bcjis.comm.service.BcjisUserDetailsService;
import com.cs.bcjis.comm.web.BcjisUserVO;

public class BcjisUserDetailsHelper {

    static BcjisUserDetailsService bcjisUserDetailsService;

    public BcjisUserDetailsService getBcjisUserDetailsService() {
        return bcjisUserDetailsService;
    }

    public void setBcjisUserDetailsService(BcjisUserDetailsService bcjisUserDetailsService) {
        BcjisUserDetailsHelper.bcjisUserDetailsService = bcjisUserDetailsService;
    }

    public static Object getAuthenticatedUser() {
        if(bcjisUserDetailsService.getAuthenticatedUser() == null){
            return new BcjisUserVO();
        }
        
        return bcjisUserDetailsService.getAuthenticatedUser();
    }

    public static List<String> getAuthorities() {
        return bcjisUserDetailsService.getAuthorities();
    }

    public static Boolean isAuthenticated() {
        return bcjisUserDetailsService.isAuthenticated();
    }
}

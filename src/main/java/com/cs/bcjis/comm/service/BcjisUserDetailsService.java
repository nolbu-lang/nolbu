package com.cs.bcjis.comm.service;

import java.util.List;

public interface BcjisUserDetailsService {

    public Object getAuthenticatedUser();

    public List<String> getAuthorities();

    public Boolean isAuthenticated();

}

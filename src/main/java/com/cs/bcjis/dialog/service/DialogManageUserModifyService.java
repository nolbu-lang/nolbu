package com.cs.bcjis.dialog.service;

import java.util.Map;


public interface DialogManageUserModifyService {
    
    @SuppressWarnings("rawtypes")
    public void updateDialogManageUser(Map map) throws Exception;
   
    @SuppressWarnings("rawtypes")
    public void updateDialogManageUserPowgrp(Map map) throws Exception;
   
}

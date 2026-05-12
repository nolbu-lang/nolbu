package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Service;

import com.cs.bcjis.manage.service.ManageCommcdService;
import com.cs.bcjis.manage.service.impl.ManageCommcdDAO;


@Service("manageCommcdService")
public class ManageCommcdServiceImpl  implements ManageCommcdService {
    
    @Resource(name="manageCommcdDAO")
    private ManageCommcdDAO manageCommcdDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectManageCommcdCommcddetlList(Map map) throws Exception {
        // TODO Auto-generated method stub
        return manageCommcdDAO.selectManageCommcddetlList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectManageCommcdCommcddetlListCnt(Map map) throws Exception {
        // TODO Auto-generated method stub
        return manageCommcdDAO.selectManageCommcddetlListcnt(map);
    }
}

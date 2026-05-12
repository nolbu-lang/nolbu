package com.cs.bcjis.manage.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.manage.service.ManageTeMngVeriService;


@Service("manageTeMngVeriService")
public class ManageTeMngVeriServiceImpl  implements ManageTeMngVeriService {
    
    @Resource(name="manageTeMngVeriDAO")
    private ManageTeMngVeriDAO manageTeMngVeriDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectManageTeMngVeriList(Map map) throws Exception { 
        return manageTeMngVeriDAO.selectManageTeMngVeriList(map);
    }

}

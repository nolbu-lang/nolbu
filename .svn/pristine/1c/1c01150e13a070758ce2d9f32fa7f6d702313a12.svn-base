package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.dialog.service.DialogDgrcompoDbizService;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;

@Service("dialogDgrcompoDbizService")
public class DialogDgrcompoDbizServiceImpl implements DialogDgrcompoDbizService {
    
    @Resource(name = "dialogDgrcompoDbizDAO")
    private DialogDgrcompoDbizDAO dialogDgrcompoDbizDAO;


    @SuppressWarnings("rawtypes")
    public List selectDgrcompo(Map map) throws Exception {
        return dialogDgrcompoDbizDAO.selectDgrcompo(map);
    }

}

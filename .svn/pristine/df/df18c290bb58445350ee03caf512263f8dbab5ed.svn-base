package com.cs.bcjis.dialog.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.dialog.service.DialogDgrcompoTeMngRegiService;

@Service("dialogDgrcompoTeMngRegiService")
public class DialogDgrcompoTeMngRegiServiceImpl implements DialogDgrcompoTeMngRegiService {
    
    @Resource(name = "dialogDgrcompoTeMngRegiDAO")
    private DialogDgrcompoTeMngRegiDAO dialogDgrcompoTeMngRegiDAO;
    
    @Resource(name = "dialogDgrcompoRegiDAO")
    private DialogDgrcompoRegiDAO dialogDgrcompoRegiDAO;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertDgrcompo(Map map) throws Exception {
        Map param = dialogDgrcompoRegiDAO.selectDgrcompoRegiInfo(map);
        map.put("teBgtCompoId", param.get("teBgtCompoId"));
        map.put("teBgtCompoSeq", param.get("teBgtCompoSeq"));
        map.put("sortSeq", param.get("sortSeq"));
        
        dialogDgrcompoTeMngRegiDAO.insertDgrcompo(map);
        dialogDgrcompoTeMngRegiDAO.insertDgrcompofrsc(map);
        dialogDgrcompoTeMngRegiDAO.insertDgrcompochar(map);
    }

    @SuppressWarnings("rawtypes")
    public void insertTeMngMok(Map map) throws Exception {
        dialogDgrcompoTeMngRegiDAO.insertTeMngMok(map);
    }

}

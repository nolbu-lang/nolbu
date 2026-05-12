package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogReport0C0DetlDAO")
public class DialogReport0C0DetlDAO extends BcjisCommAbstractDAO {
    
    @SuppressWarnings("rawtypes")
    public List selectReport0C0D(Map map) throws Exception{
        return list("DialogReport0C0Detl.selectReport0C0D", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void insertReport0C0D(Map map) throws Exception{
        insert("DialogReport0C0Detl.insertReport0C0D", map);
    }
    
    @SuppressWarnings("rawtypes")
    public void deleteReport0C0D(Map map) throws Exception{
        delete("DialogReport0C0Detl.deleteReport0C0D", map);
    }

    @SuppressWarnings("rawtypes")
    public Map selectReport0C0DDgrcompo(Map map) throws Exception{
        return (Map)selectByPk("DialogReport0C0Detl.selectReport0C0DDgrcompo", map);
    }
}

package com.cs.bcjis.comm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.web.BcjisUserVO;

@Service("bcjisCommService")
public class BcjisCommServiceImpl implements BcjisCommService {
    @Resource(name = "bcjisCommDAO")
    private BcjisCommDAO bcjisCommDAO;

    @SuppressWarnings("rawtypes")
    public List selectLeftMenuList(Map map) throws Exception {
        return bcjisCommDAO.selectLeftMenuList(map);
    }

    @SuppressWarnings("rawtypes")
    public List selectCommComboList(Map searchMap) throws Exception {
        return bcjisCommDAO.selectCommComboList(searchMap);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectIsCloseYn(Map map) throws Exception {
        return bcjisCommDAO.selectIsCloseYn(map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String getDeptUserYn(BcjisUserVO bcjisUserVO, String reportCd) throws Exception {
        if("BC001".equals(bcjisUserVO.getPowGrCd()) == true || "BC002".equals(bcjisUserVO.getPowGrCd())){
            return "N";
        }

        Map map = new HashMap();
        map.put("userId", bcjisUserVO.getUserId());
        map.put("reportCd", reportCd);
        Map tempMap = bcjisCommDAO.selectDeptUserYn(map);
        
        return String.valueOf(tempMap.get("userDeptYn"));
    }
}

package com.cs.bcjis.comm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BcjisTreeUtil {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getTreeIdMap(List treeList, String upTreeNm, String treeNm){
        Map map = new HashMap();
        
        Map tmpMap = null;
        List list = null;

        String tmpUpTreeId = "";
        String tmpTreeId = "";
        for(int i = 0; i < treeList.size(); i++){
            tmpMap = (Map)treeList.get(i);
            tmpUpTreeId = String.valueOf(tmpMap.get(upTreeNm));
            tmpTreeId = String.valueOf(tmpMap.get(treeNm));
            
            if(map.get(tmpUpTreeId) == null){
                list = new ArrayList();
                map.put(tmpUpTreeId, list);
            }else{
                list = (List)map.get(tmpUpTreeId);
            }
            
            list.add(list.size(), tmpTreeId);
        }
        
        return map;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getTreeMap(List treeList, String treeNm){
        Map map = new HashMap();
        
        Map tmpMap = null;

        String tmpTreeId = "";
        for(int i = 0; i < treeList.size(); i++){
            tmpMap = (Map)treeList.get(i);
            tmpTreeId = String.valueOf(tmpMap.get(treeNm));
            
            map.put(tmpTreeId, tmpMap);
        }
        
        return map;
    }
}

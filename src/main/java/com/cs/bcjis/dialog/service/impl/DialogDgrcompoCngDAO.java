package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cs.bcjis.comm.BcjisCommAbstractDAO;

@Repository("dialogDgrcompoCngDAO")
public class DialogDgrcompoCngDAO extends BcjisCommAbstractDAO {

	@SuppressWarnings("rawtypes")
    public List selectDgrCngHistoryList(Map map) throws Exception{
        return list("DialogDgrcompoCng.selectDgrCngHistoryList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public int selectDgrCngHistoryListCnt(Map map) {
        return (Integer)getSqlMapClientTemplate().queryForObject("DialogDgrcompoCng.selectDgrCngHistoryListCnt", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoDataList(Map map) throws Exception{
    	return list("DialogDgrcompoCng.selectDgrCompoDataList", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoOriDataList(Map map) throws Exception{
    	return list("DialogDgrcompoCng.selectDgrCompoOriDataList", map);
    }
	
    @SuppressWarnings("rawtypes")
    public Map selectDgrcompoOri(Map map) throws Exception{
        return (Map)selectByPk("DialogDgrcompoCng.selectDgrcompoOri", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompofrscListOri(Map map) throws Exception{
        return list("DialogDgrcompoCng.selectDgrcompofrscListOri", map);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrcompocharListOri(Map map) throws Exception{
        return list("DialogDgrcompoCng.selectDgrcompocharListOri", map);
    }
    
    //HISTORY 입력
	@SuppressWarnings("rawtypes")
	public void insertCngHistory(Map map) {
		insert("DialogDgrcompoCng.insertCngHistory", map);
	}
	
	//데이터 수정 여러건 (oriDatas)
	@SuppressWarnings("rawtypes")
	public void updateDgrCompoChild(Map map) {
		update("DialogDgrcompoCng.updateDgrCompoChild", map);
	}
	
	//dgrCompo 신규 입력
	@SuppressWarnings("rawtypes")
	public void insertDgrCompoParent(Map map) {
		insert("DialogDgrcompoCng.insertDgrCompoParent", map);
		insert("DialogDgrcompoCng.insertDgrcompoParentFrsc", map);
		insert("DialogDgrcompoCng.insertDgrcompoParentChar", map);
		
		Map tempMap = map;
		tempMap.put("teBgtCompoId", map.get("teBgtCompoIdParent"));
		try{
			tempMap.put("hisFg", "010");
            insert("BcjisHisComm.insertTbDgrcompoH", tempMap);
        }catch(Exception e){
            logger.error("updateDgrcompo(map)", e);
        }
	}
		
	//원본 데이터 입력
	@SuppressWarnings("rawtypes")
	public void insertDgrCompoOriChild(Map map) {
		insert("DialogDgrcompoCng.insertDgrCompoOriChild", map);
	}
	
	@SuppressWarnings("rawtypes")
	public void insertDgrCompoFrscOriChild(Map map) {
		insert("DialogDgrcompoCng.insertDgrCompoFrscOriChild", map);
	}
	
	@SuppressWarnings("rawtypes")
	public void insertDgrCompoCharOriChild(Map map) {
		insert("DialogDgrcompoCng.insertDgrCompoCharOriChild", map);
	}
	
	//데이터 수정 1건(teBgtCompoId)
	@SuppressWarnings("rawtypes")
	public void updateDgrCompoData(Map map) {
		update("DialogDgrcompoCng.updateDgrCompoData", map);
	}
	
	@SuppressWarnings("rawtypes")
	public void updateDgrCompoMergeData(Map map) {
		try{
            map.put("hisFg", "020");
            insert("DialogDgrcompoCng.insertDgrCompoMergeDataH", map);
        }catch(Exception e){
            logger.error("updateDgrCompoMergeData(map)", e);
        }
		
		update("DialogDgrcompoCng.updateDgrCompoMergeData", map);
	}

	@SuppressWarnings("rawtypes")
	public void updateDgrCompoFrscMergeData(Map map) {
		update("DialogDgrcompoCng.updateDgrCompoFrscMergeData", map);
	}
	
	@SuppressWarnings("rawtypes")
	public void updateDgrCompoCharMergeData(Map map) {
		update("DialogDgrcompoCng.updateDgrCompoCharMergeData", map);
	}
	
}
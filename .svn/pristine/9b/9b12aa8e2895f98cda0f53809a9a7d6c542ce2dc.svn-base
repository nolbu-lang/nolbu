package com.cs.bcjis.dialog.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetModifyService;
import com.cs.bcjis.budget.service.impl.BudgetCommDAO;
import com.cs.bcjis.dialog.service.DialogDgrcompoCngService;
import com.cs.bcjis.dialog.service.DialogDgrcompoModifyService;
import com.cs.bcjis.dialog.service.DialogDgrcompoRegiService;
import com.cs.bcjis.dialog.service.DialogDgrcompoSortService;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Service("dialogDgrcompoCngService")
public class DialogDgrcompoCngServiceImpl implements DialogDgrcompoCngService {
    @Resource(name = "dialogDgrcompoCngDAO")
    private DialogDgrcompoCngDAO dialogDgrcompoCngDAO;

    @Resource(name = "csCngHisGnrService")
	private EgovIdGnrService csCngHisGnrService;
    
    @Resource(name = "dialogDgrcompoRegiDAO")
    private DialogDgrcompoRegiDAO dialogDgrcompoRegiDAO;
    
    @Resource(name = "budgetModifyService")
    private BudgetModifyService budgetModifyService;
    
    @Resource(name = "dialogDgrcompoRegiService")
    private DialogDgrcompoRegiService dialogDgrcompoRegiService;
    
    @Resource(name = "dialogDgrcompoModifyService")
    private DialogDgrcompoModifyService dialogDgrcompoModifyService;
    
    @Resource(name = "dialogDgrcompoModifyDAO")
    private DialogDgrcompoModifyDAO dialogDgrcompoModifyDAO;
    
    @Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCngHistoryList(Map map) throws Exception {
        return dialogDgrcompoCngDAO.selectDgrCngHistoryList(map);
    }

    @SuppressWarnings("rawtypes")
    public int selectDgrCngHistoryListCnt(Map map) throws Exception {
        return dialogDgrcompoCngDAO.selectDgrCngHistoryListCnt(map);
    }
    
    @SuppressWarnings("rawtypes")
    public Map selectCngLogData(Map map) throws Exception {
    	Map rtnMap = map;
    	JSONArray dataList = new JSONArray();
    	String cngHistoryId = (String)map.get("cngHistoryId");
    	String cngType = (String)map.get("cngType");
    	String amtUnit = (String)map.get("amtUnit");
    	
    	List compoList = dialogDgrcompoCngDAO.selectDgrCompoOriDataList(map);
    	
    	if(compoList.size() > 0){
    		for(int i=0 ; i<compoList.size() ; i++){
				Map param = (Map)compoList.get(i);
				param.put("amtUnit", amtUnit);
				param.put("cngHistoryId", cngHistoryId);
				/*param.put("cngType", cngType);
				param.put("grpLvl", "1");*/
				JSONObject jsonObject = new JSONObject();
				JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoCngDAO.selectDgrcompoOri(param));
		        JSONArray frscList = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompofrscListOri(param));
		        JSONArray charList = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompocharListOri(param));
		/*        	        JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoModifyService.selectDgrcompo(param));
			        JSONArray frscList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompofrscList(param));
			        JSONArray charList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompocharList(param));
		*/        			
		        jsonObject.put("dgrcompo", dgrcompo);
		        
		        JSONObject jsonFrscObject = new JSONObject();
		        jsonFrscObject.put("dataList", frscList);
		        
		        JSONObject jsonCharObject = new JSONObject();
		        jsonCharObject.put("dataList", charList);
		        
		        jsonObject.put("frscInfo", jsonFrscObject);
		        jsonObject.put("charInfo", jsonCharObject);
		        
		        dataList.add(jsonObject);
    		}
            /*
    		//병합 데이터 
    		if("CH01".equals(cngType)){	//병합 
    			for(int i=0 ; i<compoList.size() ; i++){
    				Map param = (Map)compoList.get(i);
    				param.put("amtUnit", amtUnit);
    				param.put("cngHistoryId", cngHistoryId);
    				param.put("cngType", "CH01");
    				param.put("grpLvl", "1");
    				JSONObject jsonObject = new JSONObject();
        			JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoCngDAO.selectDgrcompoOri(param));
        	        JSONArray frscList = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompofrscListOri(param));
        	        JSONArray charList = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompocharListOri(param));
        	        JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoModifyService.selectDgrcompo(param));
        	        JSONArray frscList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompofrscList(param));
        	        JSONArray charList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompocharList(param));
        			
        	        jsonObject.put("dgrcompo", dgrcompo);
                    
                    JSONObject jsonFrscObject = new JSONObject();
                    jsonFrscObject.put("dataList", frscList);
                    
                    JSONObject jsonCharObject = new JSONObject();
                    jsonCharObject.put("dataList", charList);
                    
                    jsonObject.put("frscInfo", jsonFrscObject);
                    jsonObject.put("charInfo", jsonCharObject);
                    
                    dataList.add(jsonObject);
    			}
    		}else if("CH02".equals(cngType)){	//분리
    			Map parentParam = (Map)compoList.get(0);
    			String fisYear = (String)parentParam.get("fisYear"); 
    			String bgtDgr = (String)parentParam.get("fisYear"); 
    			String teBgtCompoId = (String)parentParam.get("teBgtCompoId");
    			map.put("fisYear", fisYear);
    			map.put("bgtDgr", bgtDgr); 
    			map.put("teBgtCompoId", teBgtCompoId);
    			//분리원본 가져오기
    			JSONObject jsonObjectP = new JSONObject();
    			JSONObject dgrcompoP = JSONObject.fromObject(dialogDgrcompoCngDAO.selectDgrcompoOri(map));
    	        JSONArray frscListP = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompofrscListOri(map));
    	        JSONArray charListP = JSONArray.fromObject(dialogDgrcompoCngDAO.selectDgrcompocharListOri(map));
    			
    	        jsonObjectP.put("dgrcompo", dgrcompoP);
                
                JSONObject jsonFrscObjectP = new JSONObject();
                jsonFrscObjectP.put("dataList", frscListP);
                
                JSONObject jsonCharObjectP = new JSONObject();
                jsonCharObjectP.put("dataList", charListP);
                
                jsonObjectP.put("frscInfo", jsonFrscObjectP);
                jsonObjectP.put("charInfo", jsonCharObjectP);
                
                dataList.add(jsonObjectP);
    			
                //분리된 데이터 가져오기
    			for(int i=0 ; i<compoList.size() ; i++){
    				Map param = (Map)compoList.get(i);
    				param.put("amtUnit", amtUnit);
    				JSONObject jsonObject = new JSONObject();
        			JSONObject dgrcompo = JSONObject.fromObject(dialogDgrcompoModifyService.selectDgrcompo(param));
        	        JSONArray frscList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompofrscList(param));
        	        JSONArray charList = JSONArray.fromObject(dialogDgrcompoModifyService.selectDgrcompocharList(param));
        			
        	        jsonObject.put("dgrcompo", dgrcompo);
                    
                    JSONObject jsonFrscObject = new JSONObject();
                    jsonFrscObject.put("dataList", frscList);
                    
                    JSONObject jsonCharObject = new JSONObject();
                    jsonCharObject.put("dataList", charList);
                    
                    jsonObject.put("frscInfo", jsonFrscObject);
                    jsonObject.put("charInfo", jsonCharObject);
                    
                    dataList.add(jsonObject);
    			}
    		}*/
    	}
    	rtnMap.put("dataList", dataList);
        return rtnMap;
    }
    
    @SuppressWarnings("rawtypes")
    public List selectDgrCompoDataList(Map map) throws Exception {
        return dialogDgrcompoCngDAO.selectDgrCompoDataList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void updateDgrcompos(JSONObject jsonParam) throws Exception {
    	//선택된 데이터 수정
        dialogDgrcompoModifyDAO.updateDgrcompo(jsonParam);
        
        JSONObject tempParam = null;
        List frscs = jsonParam.getJSONArray("frscs");
        
        for(int i = 0; i < frscs.size(); i++){
            tempParam = (JSONObject) frscs.get(i);
            
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));
            
            budgetCommDAO.updateDgrcompofrsc(tempParam);
        }
        
        tempParam = null;
        List chars = jsonParam.getJSONArray("chars");
        for(int i = 0; i < chars.size(); i++){
            tempParam = (JSONObject) chars.get(i);
            
            tempParam.put("fisYear", jsonParam.get("fisYear"));
            tempParam.put("bgtDgr", jsonParam.get("bgtDgr"));
            tempParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
            tempParam.put("teBgtCompoSeq", jsonParam.get("teBgtCompoSeq"));
            tempParam.put("amtUnit", jsonParam.get("amtUnit"));
            tempParam.put("userId", jsonParam.get("userId"));
            
            budgetCommDAO.updateDgrcompochar(tempParam);
        }
        
        budgetCommDAO.saveUpDgrcompoInfoAll(jsonParam);
        
        //병합 개별사업에 반영
        dialogDgrcompoCngDAO.updateDgrCompoMergeData(jsonParam);
        dialogDgrcompoCngDAO.updateDgrCompoFrscMergeData(jsonParam);
        dialogDgrcompoCngDAO.updateDgrCompoCharMergeData(jsonParam);
        
    }
    
	@SuppressWarnings("rawtypes")
	public void dgrcompoMerge(JSONObject jsonParam) throws Exception {
		/*
		 * 	ex) A + B + C = D
		 *	1. 변경이력 입력(TB_CNG_HISTORY)
		 *	2. 예산편성(TB_DGRCOMPO) 수정 (A,B,C : CNG_TYPE = 'CH01', GRP_ID = CHG_HISTORY_ID, GRP_TYPE = 2)
		 *	3. 예산편성(TB_DGRCOMPO) 신규 입력 (D : CNG_TYPE = 'CH01', GRP_ID = CNG_HISTORY_ID, GRP_TYPE = 1)
		 *	4. 예산편성재원(TB_DGRCOMPOFRSC) 입력 (D)
		 *	5. 예산편성성격(TB_DGRCOMPOCHAR) 입력 (D)
		 *	6. 예산편성 원본(TB_DGRCOMPO_ORI) 입력 (A,B,C,D)
		 *	7. 예산편성재원 원본(TB_DGRCOMPOFRSC_ORI) 입력 (A,B,C,D)
		 *	8. 예산편성성격 원본(TB_DGRCOMPOCHAR_ORI) 입력 (A,B,C,D)
		 */
		
		JSONObject saveData = jsonParam.getJSONObject("saveData");
		String cngHistoryId = csCngHisGnrService.getNextStringId();
		saveData.put("cngHistoryId", cngHistoryId);
		saveData.put("userId", jsonParam.get("userId"));
		
		//1. 변경이력 입력(TB_CNG_HISTORY)
		dialogDgrcompoCngDAO.insertCngHistory(saveData);
		
		//2. 예산편성(TB_DGRCOMPO) 수정 (A,B,C : CNG_TYPE = 'CH01', GRP_ID = CHG_HISTORY_ID, GRP_TYPE = 2)
		Map childData = saveData;
		childData.put("cngType", "CH01");
		childData.put("grpId", cngHistoryId);
		childData.put("grpLvl", "2");
		dialogDgrcompoCngDAO.updateDgrCompoChild(childData);
				
		//3. 예산편성(TB_DGRCOMPO) 신규 입력 (D : CNG_TYPE = 'CH01', GRP_ID = CNG_HISTORY_ID, GRP_TYPE = 1)
		//4. 예산편성재원(TB_DGRCOMPOFRSC) 입력 (D)
		//5. 예산편성성격(TB_DGRCOMPOCHAR) 입력 (D)
		Map parentData = saveData;
		Map param = dialogDgrcompoRegiDAO.selectDgrcompoRegiInfo(parentData);
		parentData.put("teBgtCompoIdParent", param.get("teBgtCompoId"));
		parentData.put("teBgtCompoSeq", param.get("teBgtCompoSeq"));
		parentData.put("sortSeq", param.get("sortSeq"));
		
		parentData.put("cngType", "CH01");
		parentData.put("grpId", cngHistoryId);
		parentData.put("grpLvl", "1");
		dialogDgrcompoCngDAO.insertDgrCompoParent(parentData);
		
		//6. 예산편성 원본(TB_DGRCOMPO_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoOriChild(saveData); 
		
		//7. 예산편성재원 원본(TB_DGRCOMPOFRSC_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoFrscOriChild(saveData);
		
		//8. 예산편성성격 원본(TB_DGRCOMPOCHAR_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoCharOriChild(saveData);
		
	}
	
	@SuppressWarnings("rawtypes")
	public void dgrcompoSep(JSONObject jsonParam) throws Exception {
		/*
		 * 	ex) D = A + B + C
		 *	1. 변경이력 입력(TB_CNG_HISTORY)
		 *	2. 예산편성(TB_DGRCOMPO) 수정 (D : CNG_TYPE = 'CH02', GRP_ID = CNG_HISTORY_ID, GRP_TYPE = 1)
		 *	3. 예산편성(TB_DGRCOMPO) 신규 입력  (A,B,C : CNG_TYPE = 'CH02', GRP_ID = CHG_HISTORY_ID, GRP_TYPE = 2)
		 *	4. 예산편성재원(TB_DGRCOMPOFRSC) 입력 (A,B,C)
		 *	5. 예산편성성격(TB_DGRCOMPOCHAR) 입력 (A,B,C)
		 *	6. 예산편성 원본(TB_DGRCOMPO_ORI) 입력 (A,B,C,D)
		 *	7. 예산편성재원 원본(TB_DGRCOMPOFRSC_ORI) 입력 (A,B,C,D)
		 *	8. 예산편성성격 원본(TB_DGRCOMPOCHAR_ORI) 입력 (A,B,C,D)
		 *	9. 개별사업 원본 삭제 (TB_DGRCOMPO, TB_DGRCOMPOFRSC, TB_DGRCOMPOCHAR) (D)
		 */
		
		JSONObject tempParam = null;
		String cngHistoryId = csCngHisGnrService.getNextStringId();
		jsonParam.put("cngHistoryId", cngHistoryId);
		List saveDatas = jsonParam.getJSONArray("saveDatas");
		
		//1. 변경이력 입력(TB_CNG_HISTORY)
		dialogDgrcompoCngDAO.insertCngHistory(jsonParam);
		
		//2. 예산편성(TB_DGRCOMPO) 수정 (D : CNG_TYPE = 'CH02', GRP_ID = CNG_HISTORY_ID, GRP_TYPE = 1)
		JSONObject updateParentParam = new JSONObject();
		updateParentParam.put("fisYear", jsonParam.get("fisYear"));
		updateParentParam.put("bgtDgr", jsonParam.get("bgtDgr"));
		updateParentParam.put("teBgtCompoId", jsonParam.get("teBgtCompoId"));
		updateParentParam.put("cngType", "CH02");
		updateParentParam.put("grpId", cngHistoryId);
		updateParentParam.put("grpLvl", "1");
		updateParentParam.put("userId", jsonParam.get("userId"));
		
		dialogDgrcompoCngDAO.updateDgrCompoData(updateParentParam);
		
		//3. 예산편성(TB_DGRCOMPO) 신규 입력  (A,B,C : CNG_TYPE = 'CH02', GRP_ID = CHG_HISTORY_ID, GRP_TYPE = 2)
		//4. 예산편성재원(TB_DGRCOMPOFRSC) 입력 (A,B,C)
		//5. 예산편성성격(TB_DGRCOMPOCHAR) 입력 (A,B,C)
		for(int i=0 ; i<saveDatas.size() ; i++){
			tempParam = (JSONObject) saveDatas.get(i);
            
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("grpId", cngHistoryId);
            dialogDgrcompoRegiService.saveDgrcompos(tempParam);
            
		}
		
		//6. 예산편성 원본(TB_DGRCOMPO_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoOriChild(jsonParam);
		//7. 예산편성재원 원본(TB_DGRCOMPOFRSC_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoFrscOriChild(jsonParam);
		//8. 예산편성성격 원본(TB_DGRCOMPOCHAR_ORI) 입력 (A,B,C,D)
		dialogDgrcompoCngDAO.insertDgrCompoCharOriChild(jsonParam);
		//9. 개별사업 원본 삭제 (TB_DGRCOMPO, TB_DGRCOMPOFRSC, TB_DGRCOMPOCHAR) (D)
		budgetModifyService.deleteDgrcompos(jsonParam);
		
	}
}

package com.cs.bcjis.budget.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetSelectNewService;
import com.cs.bcjis.budget.service.BudgetSelectService;
import com.cs.bcjis.budget.service.BudgetSheetSelectService;
import com.cs.bcjis.report.service.impl.ReportCommDAO;
import com.cs.bcjis.report.service.impl.ReportWrite0F0DAO;

@Service("budgetSelectNewService")
public class BudgetSelectNewServiceImpl implements BudgetSelectNewService {
    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;

    @Resource(name = "budgetSelectDAO")
    private BudgetSelectDAO budgetSelectDAO;

    @Resource(name = "budgetSheetSelectService")
    private BudgetSheetSelectService budgetSheetSelectService;
    
    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;
    
    @Resource(name="reportWrite0F0DAO")
    private ReportWrite0F0DAO reportWrite0F0DAO;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectDgrCompoList(Map map) throws Exception {
        String reportCd = String.valueOf(map.get("reportCd"));
        // 070은 기존 전용 쿼리 유지.
        if ("070".equals(reportCd)) {
            return budgetSelectDAO.selectDgrCompoList(reportCd, map);
        }

        String viewMode = str(map.get("viewMode"));
        List leaves;
        if ("attr".equals(viewMode)) {
            leaves = budgetSelectDAO.selectDgrCompoLeafListAttr(map);
            // 보고항목·사전절차: 평면 목록
            return buildFlatSelectRows(leaves);
        }
        // class — 조서·집계: lazy 트리(기본 접힘)
        leaves = budgetSelectDAO.selectDgrCompoLeafListClass(map);
        return buildSelectTreeFromLeaves(leaves);
    }

    /**
     * 세세목 평면 행 (csTreeGrid 호환: level0 leaf).
     * dgrcompoNm = 부서 &gt; 세부사업 &gt; 통계목 &gt; 산출근거
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List buildFlatSelectRows(List leaves) {
        List result = new ArrayList();
        if (leaves == null || leaves.isEmpty()) {
            return result;
        }
        for (int i = 0; i < leaves.size(); i++) {
            Map leaf = (Map) leaves.get(i);
            String fisYear = str(leaf.get("fisYear"));
            String bgtDgr = pad3(leaf.get("bgtDgr"));
            String teId = str(leaf.get("teBgtCompoId"));
            String id = fisYear + "_" + bgtDgr + "_" + teId;

            Map row = new HashMap();
            row.put("dgrcompoId", id);
            row.put("upDgrcompoId", "");
            row.put("parent", "");
            row.put("fisYear", leaf.get("fisYear"));
            row.put("bgtDgr", leaf.get("bgtDgr"));
            row.put("teBgtCompoId", teId);
            row.put("dgrLevel", Integer.valueOf(0));
            row.put("level", Integer.valueOf(0));
            row.put("teBgtCompoSeq", leaf.get("teBgtCompoSeq"));
            String pathNm = str(leaf.get("pathNm"));
            if (pathNm.length() < 1) {
                pathNm = str(leaf.get("compGround"));
            }
            row.put("dgrcompoNm", pathNm);
            row.put("teMngMokNm", leaf.get("teMngMokNm"));
            row.put("demandBgtAmt", leaf.get("demandBgtAmt"));
            row.put("demandDiffAmt", leaf.get("demandDiffAmt"));
            row.put("bgtAmt", leaf.get("bgtAmt"));
            row.put("preAmt", leaf.get("preAmt"));
            row.put("diffAmt", leaf.get("diffAmt"));
            row.put("frsces", leaf.get("frsces"));
            String reportMstr = str(leaf.get("reportMstr"));
            String reportCdVal = str(leaf.get("reportCd"));
            String reportDetlCd = str(leaf.get("reportDetlCd"));
            String govSub = str(leaf.get("govSub"));
            String indiAttr = normalizeMultiCode(str(leaf.get("indiAttr")));
            String advncProc = normalizeMultiCode(str(leaf.get("advncProc")));
            row.put("reportMstr", reportMstr);
            row.put("reportMstrNm", reportMstr);
            row.put("reportCd", reportCdVal);
            row.put("reportCdNm", reportCdVal);
            row.put("reportDetlCd", reportDetlCd);
            row.put("reportDetlCdNm", reportDetlCd);
            row.put("govSub", govSub);
            row.put("govSubNm", govSub);
            row.put("indiAttr", indiAttr);
            row.put("indiAttrOrg", indiAttr);
            row.put("advncProc", advncProc);
            row.put("selYn", nvlYn(leaf.get("selYn")));
            row.put("selSheetYn", nvlYn(leaf.get("selSheetYn")));
            row.put("sel010Yn", nvlYn(leaf.get("sel010Yn")));
            row.put("sel020Yn", nvlYn(leaf.get("sel020Yn")));
            row.put("sel030Yn", nvlYn(leaf.get("sel030Yn")));
            row.put("sel040Yn", nvlYn(leaf.get("sel040Yn")));
            row.put("sel050Yn", nvlYn(leaf.get("sel050Yn")));
            row.put("sel055Yn", nvlYn(leaf.get("sel055Yn")));
            row.put("sel060Yn", nvlYn(leaf.get("sel060Yn")));
            row.put("sel090Yn", nvlYn(leaf.get("sel090Yn")));
            row.put("seletcYn", nvlYn(leaf.get("seletcYn")));
            row.put("checkYn031", nvlYn(leaf.get("checkYn031")));
            row.put("checkYn032", nvlYn(leaf.get("checkYn032")));
            row.put("checkYn033", nvlYn(leaf.get("checkYn033")));
            row.put("checkYn034", nvlYn(leaf.get("checkYn034")));
            row.put("checkYn035", nvlYn(leaf.get("checkYn035")));
            row.put("checkYnTf1", nvlYn(leaf.get("checkYnTf1")));
            row.put("report030FgView", "");
            row.put("selNames", "");
            row.put("existYn", "");
            row.put("changeFlag", "");
            row.put("isLeaf", Boolean.TRUE);
            row.put("expanded", Boolean.TRUE);
            row.put("loaded", Boolean.TRUE);
            result.add(row);
        }
        return result;
    }

    /**
     * 세세목 평면 목록 → csTreeGrid 호환 트리(회계-실국-부서-세부사업-세세목).
     * 부모/자식 모두 expanded=true — 조회 직후 사업(세세목)이 보이도록 함.
     * (접힘(lazy)은 사업이 안 보이는 것으로 오인되어 기본은 펼침 유지)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List buildSelectTreeFromLeaves(List leaves) {
        List result = new ArrayList();
        if (leaves == null || leaves.isEmpty()) {
            return result;
        }

        java.util.LinkedHashMap parentMap = new java.util.LinkedHashMap();

        for (int i = 0; i < leaves.size(); i++) {
            Map leaf = (Map) leaves.get(i);
            String fisYear = str(leaf.get("fisYear"));
            String bgtDgr = pad3(leaf.get("bgtDgr"));
            String fisFgCd = str(leaf.get("fisFgCd"));
            String officeCd = str(leaf.get("officeCd"));
            String deptCd = str(leaf.get("deptCd"));
            String dbizCd = str(leaf.get("dbizCd"));
            String teId = str(leaf.get("teBgtCompoId"));

            String id0 = fisYear + "_" + bgtDgr + "_" + fisFgCd + "_0000_0000000_0000000000000000_00000000000";
            String id1 = fisYear + "_" + bgtDgr + "_" + fisFgCd + "_" + officeCd + "_0000000_0000000000000000_00000000000";
            String id2 = fisYear + "_" + bgtDgr + "_" + fisFgCd + "_" + officeCd + "_" + deptCd + "_0000000000000000_00000000000";
            String id3 = fisYear + "_" + bgtDgr + "_" + fisFgCd + "_" + officeCd + "_" + deptCd + "_" + dbizCd + "_00000000000";
            String id4 = fisYear + "_" + bgtDgr + "_" + fisFgCd + "_" + officeCd + "_" + deptCd + "_" + dbizCd + "_" + teId;

            ensureParent(parentMap, result, id0, null, 0, fisYear, leaf.get("bgtDgr"),
                    str(leaf.get("fisFgNm")), leaf);
            ensureParent(parentMap, result, id1, id0, 1, fisYear, leaf.get("bgtDgr"),
                    str(leaf.get("officeNm")), leaf);
            ensureParent(parentMap, result, id2, id1, 2, fisYear, leaf.get("bgtDgr"),
                    str(leaf.get("deptNm")), leaf);
            ensureParent(parentMap, result, id3, id2, 3, fisYear, leaf.get("bgtDgr"),
                    str(leaf.get("dbizNm")), leaf);

            addAmt(parentMap, id0, leaf);
            addAmt(parentMap, id1, leaf);
            addAmt(parentMap, id2, leaf);
            addAmt(parentMap, id3, leaf);

            Map row = new HashMap();
            row.put("dgrcompoId", id4);
            row.put("upDgrcompoId", id3);
            row.put("parent", id3);
            row.put("fisYear", leaf.get("fisYear"));
            row.put("bgtDgr", leaf.get("bgtDgr"));
            row.put("teBgtCompoId", teId);
            row.put("dgrLevel", Integer.valueOf(4));
            row.put("level", Integer.valueOf(4));
            row.put("teBgtCompoSeq", leaf.get("teBgtCompoSeq"));
            row.put("dgrcompoNm", str(leaf.get("compGround")));
            // 통계목 코드만 표시(통계목명 제외)
            row.put("teMngMokNm", formatTeMngMokCd(leaf.get("teMngMokCd"), leaf.get("teMngMokNm")));
            row.put("demandBgtAmt", leaf.get("demandBgtAmt"));
            row.put("demandDiffAmt", leaf.get("demandDiffAmt"));
            row.put("bgtAmt", leaf.get("bgtAmt"));
            row.put("preAmt", leaf.get("preAmt"));
            row.put("diffAmt", leaf.get("diffAmt"));
            row.put("frsces", leaf.get("frsces"));
            String reportMstr = str(leaf.get("reportMstr"));
            String reportCdVal = str(leaf.get("reportCd"));
            String reportDetlCd = str(leaf.get("reportDetlCd"));
            String govSub = str(leaf.get("govSub"));
            String indiAttr = normalizeMultiCode(str(leaf.get("indiAttr")));
            String advncProc = normalizeMultiCode(str(leaf.get("advncProc")));
            row.put("reportMstr", reportMstr);
            row.put("reportMstrNm", reportMstr);
            row.put("reportCd", reportCdVal);
            row.put("reportCdNm", reportCdVal);
            row.put("reportDetlCd", reportDetlCd);
            row.put("reportDetlCdNm", reportDetlCd);
            row.put("govSub", govSub);
            row.put("govSubNm", govSub);
            row.put("indiAttr", indiAttr);
            row.put("indiAttrOrg", indiAttr);
            row.put("advncProc", advncProc);
            row.put("selYn", nvlYn(leaf.get("selYn")));
            row.put("selSheetYn", nvlYn(leaf.get("selSheetYn")));
            row.put("sel010Yn", nvlYn(leaf.get("sel010Yn")));
            row.put("sel020Yn", nvlYn(leaf.get("sel020Yn")));
            row.put("sel030Yn", nvlYn(leaf.get("sel030Yn")));
            row.put("sel040Yn", nvlYn(leaf.get("sel040Yn")));
            row.put("sel050Yn", nvlYn(leaf.get("sel050Yn")));
            row.put("sel055Yn", nvlYn(leaf.get("sel055Yn")));
            row.put("sel060Yn", nvlYn(leaf.get("sel060Yn")));
            row.put("sel090Yn", nvlYn(leaf.get("sel090Yn")));
            row.put("seletcYn", nvlYn(leaf.get("seletcYn")));
            row.put("checkYn031", nvlYn(leaf.get("checkYn031")));
            row.put("checkYn032", nvlYn(leaf.get("checkYn032")));
            row.put("checkYn033", nvlYn(leaf.get("checkYn033")));
            row.put("checkYn034", nvlYn(leaf.get("checkYn034")));
            row.put("checkYn035", nvlYn(leaf.get("checkYn035")));
            row.put("checkYnTf1", nvlYn(leaf.get("checkYnTf1")));
            row.put("report030FgView", "");
            row.put("selNames", buildSelNames(leaf));
            row.put("existYn", "");
            row.put("changeFlag", "");
            row.put("isLeaf", Boolean.TRUE);
            row.put("expanded", Boolean.TRUE);
            row.put("loaded", Boolean.TRUE);
            result.add(row);
        }

        return result;
    }

    /** 통계목 코드(XXX-XX)만 반환. 통계목명 제거. */
    private String formatTeMngMokCd(Object teMngMokCd, Object teMngMokNm) {
        String cd = str(teMngMokCd);
        if (cd.length() >= 5 && !"00000".equals(cd)) {
            return cd.substring(0, 3) + "-" + cd.substring(3);
        }
        // SQL에서 이미 '100-01 명칭' 형태면 코드만 추출
        String nm = str(teMngMokNm);
        if (nm.length() >= 6 && nm.charAt(3) == '-') {
            int sp = nm.indexOf(' ');
            return sp > 0 ? nm.substring(0, sp) : nm.substring(0, 6);
        }
        return nm;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void ensureParent(java.util.LinkedHashMap parentMap, List result,
            String id, String upId, int level, String fisYear, Object bgtDgr,
            String nm, Map leafSample) {
        if (parentMap.containsKey(id)) {
            return;
        }
        Map row = new HashMap();
        row.put("dgrcompoId", id);
        row.put("upDgrcompoId", upId == null ? "" : upId);
        row.put("parent", upId == null ? "" : upId);
        row.put("fisYear", fisYear);
        row.put("bgtDgr", bgtDgr);
        row.put("teBgtCompoId", "00000000000");
        row.put("dgrLevel", Integer.valueOf(level));
        row.put("level", Integer.valueOf(level));
        row.put("teBgtCompoSeq", Integer.valueOf(0));
        row.put("dgrcompoNm", nm);
        row.put("teMngMokNm", "");
        row.put("demandBgtAmt", Long.valueOf(0));
        row.put("demandDiffAmt", Long.valueOf(0));
        row.put("bgtAmt", Long.valueOf(0));
        row.put("preAmt", Long.valueOf(0));
        row.put("diffAmt", Long.valueOf(0));
        row.put("frsces", "");
        row.put("reportMstr", "");
        row.put("reportCd", "");
        row.put("reportDetlCd", "");
        row.put("govSub", "");
        row.put("indiAttr", "");
        row.put("advncProc", "");
        row.put("selYn", "N");
        row.put("selSheetYn", "N");
        row.put("sel010Yn", "N");
        row.put("sel020Yn", "N");
        row.put("sel030Yn", "N");
        row.put("sel040Yn", "N");
        row.put("sel050Yn", "N");
        row.put("sel055Yn", "N");
        row.put("sel060Yn", "N");
        row.put("sel090Yn", "N");
        row.put("seletcYn", "N");
        row.put("checkYn031", "N");
        row.put("checkYn032", "N");
        row.put("checkYn033", "N");
        row.put("checkYn034", "N");
        row.put("checkYn035", "N");
        row.put("checkYnTf1", "N");
        row.put("report030FgView", "");
        row.put("selNames", "");
        row.put("existYn", "");
        row.put("changeFlag", "");
        row.put("isLeaf", Boolean.FALSE);
        row.put("expanded", Boolean.TRUE);
        row.put("loaded", Boolean.TRUE);
        // 부서코드(체크박스 트리용)
        if (level >= 2) {
            row.put("deptCd", leafSample.get("deptCd"));
        }
        parentMap.put(id, row);
        result.add(row);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addAmt(java.util.LinkedHashMap parentMap, String id, Map leaf) {
        Map parent = (Map) parentMap.get(id);
        if (parent == null) {
            return;
        }
        parent.put("demandBgtAmt", Long.valueOf(toLong(parent.get("demandBgtAmt")) + toLong(leaf.get("demandBgtAmt"))));
        parent.put("demandDiffAmt", Long.valueOf(toLong(parent.get("demandDiffAmt")) + toLong(leaf.get("demandDiffAmt"))));
        parent.put("bgtAmt", Long.valueOf(toLong(parent.get("bgtAmt")) + toLong(leaf.get("bgtAmt"))));
        parent.put("preAmt", Long.valueOf(toLong(parent.get("preAmt")) + toLong(leaf.get("preAmt"))));
        parent.put("diffAmt", Long.valueOf(toLong(parent.get("diffAmt")) + toLong(leaf.get("diffAmt"))));
    }

    private String buildSelNames(Map leaf) {
        String reportNames = str(leaf.get("reportNames"));
        String sheetNames = str(leaf.get("sheetNames"));
        if (reportNames.length() < 1) {
            return sheetNames;
        }
        if (sheetNames.length() < 1) {
            return reportNames;
        }
        return reportNames + ", " + sheetNames;
    }

    private String str(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    /** 빈 토큰 제거 후 | 로 재결합 (''|A||B → A|B) */
    private String normalizeMultiCode(String raw) {
        if (raw == null || raw.length() < 1) {
            return "";
        }
        String[] parts = raw.split("[,|]");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i] == null ? "" : parts[i].trim();
            if (p.length() < 1) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(p);
        }
        return sb.toString();
    }

    private String nvlYn(Object o) {
        String s = str(o);
        return s.length() < 1 ? "N" : s;
    }

    private String pad3(Object o) {
        try {
            int v = Integer.parseInt(str(o));
            if (v < 0) {
                v = 0;
            }
            return String.format("%03d", Integer.valueOf(v));
        } catch (Exception e) {
            return "000";
        }
    }

    private long toLong(Object o) {
        if (o == null) {
            return 0L;
        }
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(o).replace(",", ""));
        } catch (Exception e) {
            return 0L;
        }
    }

    private Map<String, String> reportCdToSheetCd = new HashMap<String, String>();
    private Map<String, String> reportDetlCdToSheetDetlCd = new HashMap<String, String>();

    @SuppressWarnings("rawtypes")
    public void saveReport(JSONObject jsonParam) throws Exception {

        String fisFgMstCd = String.valueOf(jsonParam.get("fisFgMstCd"));
        String fisFgCd = String.valueOf(jsonParam.get("fisFgCd"));
        String officeCd = String.valueOf(jsonParam.get("officeCd"));
        String deptRankFr = String.valueOf(jsonParam.get("deptRankFr"));
        String deptRankTo = String.valueOf(jsonParam.get("deptRankTo"));
        String teMngMokCdFr = String.valueOf(jsonParam.get("teMngMokCdFr"));
        String teMngMokCdTo = String.valueOf(jsonParam.get("teMngMokCdTo"));
        String frscFgCdFr = String.valueOf(jsonParam.get("frscFgCdFr"));
        String frscFgCdTo = String.valueOf(jsonParam.get("frscFgCdTo"));
        String viewMode = String.valueOf(jsonParam.get("viewMode"));
        boolean attrMode = "attr".equals(viewMode);
        
        initCdData(); //집계표 데이터 초기화
        List saveReportDatas = jsonParam.getJSONArray("saveReportDatas");
        JSONObject tempParam = null;

        // 세세목별 기존 TB_REPORT 키를 1회(또는 청크) 조회로 미리 적재 — 행마다 SELECT 제거
        Map existByCompoId = getExistDataMapByCompoIds(saveReportDatas, jsonParam);
        Set report070DetlCds = new HashSet();
        
        for (int i = 0; i < saveReportDatas.size(); i++) {
        	tempParam = (JSONObject) saveReportDatas.get(i);
        	
        	String reportCd = String.valueOf(tempParam.get("reportCd"));
        	String reportDetlCd = String.valueOf(tempParam.get("reportDetlCd"));

        	tempParam.put("fisFgMstCd", fisFgMstCd);
        	tempParam.put("fisFgCd", fisFgCd);
        	tempParam.put("officeCd", officeCd);
        	tempParam.put("deptRankFr", deptRankFr);
        	tempParam.put("deptRankTo", deptRankTo);
        	tempParam.put("teMngMokCdFr", teMngMokCdFr);
        	tempParam.put("teMngMokCdTo", teMngMokCdTo);
        	tempParam.put("frscFgCdFr", frscFgCdFr);
        	tempParam.put("frscFgCdTo", frscFgCdTo);
        	tempParam.put("userId", jsonParam.get("userId"));
        	
        	String teBgtCompoId = String.valueOf(tempParam.get("teBgtCompoId"));
        	Map existDataMapSrc = (Map) existByCompoId.get(teBgtCompoId);
        	Map existDataMap = existDataMapSrc == null ? new HashMap() : new HashMap(existDataMapSrc);
        	Map tempKeyMap = null;
            String reportKeyString = getReportKeyString(tempParam);

            // 분류 취소(reportCd 비움): 기존 조서·집계 항목 삭제
            // 보고항목모드(attr)에서는 분류 미지정 행은 건너뜀(삭제하지 않음)
            if (reportCd == null || "".equals(reportCd) || "null".equals(reportCd)) {
                if (!attrMode) {
                    deleteReport(existDataMap);
                    existByCompoId.put(teBgtCompoId, new HashMap());
                }
                continue;
            }

        	tempKeyMap = (Map) existDataMap.remove(reportKeyString);
            if (tempKeyMap == null) {
            	if(attrMode){
            		// 보고항목모드: 신규 조서성질 생성 없이, 기존 성질이 없으면 스킵
            		continue;
            	}
            	if(!"".equals(reportCd)){
            		reportCommDAO.insertReport(reportCd, tempParam);
            	}
            }else{
            	if(!"".equals(reportCd)){
            		reportCommDAO.updateReport(tempParam);
            	}
            }
            
            String indiAttr = String.valueOf(tempParam.get("indiAttr")); //변경된 보고항목
            String indiAttrOrg = String.valueOf(tempParam.get("indiAttrOrg")); //원본 보고항목
            String indiAttrSkip = String.valueOf(tempParam.get("indiAttrSkip"));

            // attr 모드: 투자사업유형/분류항목은 TB_REPORT 컬럼만 갱신 (TB_REPORT_ATTR 미사용)
            if (attrMode) {
                indiAttrSkip = "Y";
            }

            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   indiAttr : " + indiAttr + "      indiAttrOrg : " + indiAttrOrg);
            if (!"Y".equals(indiAttrSkip)) {
            if(!"".equals(indiAttr) && !"null".equals(indiAttr)){
            	String[] indiAttrArr = indiAttr.split(",");
            	String[] indiAttrOrgArr = (indiAttrOrg == null || "null".equals(indiAttrOrg)) ? new String[0] : indiAttrOrg.split(",");
            	JSONObject attrParam = (JSONObject) saveReportDatas.get(i);

            	for(int j=0 ; j<indiAttrArr.length ; j++){
            		
            		//기존 보고항목에 포함안된 보고항목 수정및 입력
            		if(!Arrays.asList(indiAttrOrgArr).contains(indiAttrArr[j])){
            			attrParam.put("indiAttr", indiAttrArr[j]);
                		int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
                        
                        if(cnt > 0){
                        	reportWrite0F0DAO.updateReportAttrSel(attrParam);
                        }else{
                        	reportWrite0F0DAO.insertReportAttrSel(attrParam);
                        }
            		}
            		 
            	} 
            	
            }else{
            	JSONObject attrParam = (JSONObject) saveReportDatas.get(i);
            	attrParam.put("indiAttr", null);
            	int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
                
                if(cnt > 0){
                	reportWrite0F0DAO.deleteReportAttr(attrParam);
                }
            }
            } // end indiAttrSkip
            
            // 조서·집계 모드만: 다른 성질 키 삭제. attr 모드는 기존 성질 유지
            if (!attrMode) {
                deleteReport(existDataMap);
                // 캐시 갱신: 남은 키는 삭제됨 → 적용한 키만 유지
                Map kept = new HashMap();
                kept.put(reportKeyString, tempParam);
                existByCompoId.put(teBgtCompoId, kept);
            }
            
            if (!attrMode && "070".equals(reportCd) == true) {
                report070DetlCds.add(reportDetlCd);
            }
            
        }

        // 070 집계 재구성은 건별이 아니라 소분류별로 1회만
        Iterator detlIt = report070DetlCds.iterator();
        while (detlIt.hasNext()) {
            insertReport070s("070", String.valueOf(detlIt.next()));
        }
        
        List saveReportDatas030 = jsonParam.getJSONArray("saveReportDatas030");
        if (saveReportDatas030 == null || saveReportDatas030.size() < 1) {
            return;
        }

        // attr 모드: 국고030 UI 없음 — 오인 삭제/저장 방지
        if (attrMode) {
            return;
        }

        Map existByCompoId030 = getExistDataMapByCompoIds(saveReportDatas030, jsonParam);

        for (int i = 0; i < saveReportDatas030.size(); i++) {
            tempParam = (JSONObject) saveReportDatas030.get(i);
            String reportCd = "030";
            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("reportCd", reportCd);
            
            String teBgtCompoId = String.valueOf(tempParam.get("teBgtCompoId"));
            Map existDataMapSrc = (Map) existByCompoId030.get(teBgtCompoId);
            Map existDataMap = existDataMapSrc == null ? new HashMap() : new HashMap(existDataMapSrc);
            Map tempKeyMap = null;

            if("Y".equals(tempParam.get("checkYn031Yn")) == true){
                tempParam.put("reportDetlCd", "031");
                String reportKeyString = getReportKeyString(tempParam);
                
                if("Y".equals(tempParam.get("checkYn031")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }

            if("Y".equals(tempParam.get("checkYn032Yn")) == true){
                tempParam.put("reportDetlCd", "032");
                String reportKeyString = getReportKeyString(tempParam);
                if("Y".equals(tempParam.get("checkYn032")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }

            if("Y".equals(tempParam.get("checkYn033Yn")) == true){
                tempParam.put("reportDetlCd", "033");
                String reportKeyString = getReportKeyString(tempParam);
                if("Y".equals(tempParam.get("checkYn033")) == true){
                    tempKeyMap = (Map) existDataMap.remove(reportKeyString);

                    if (tempKeyMap == null) {
                        reportCommDAO.insertReport(reportCd, tempParam);
                    }
                }else{
                    reportCommDAO.deleteReport(reportCd, tempParam);
                }
            }
            
            if("Y".equals(tempParam.get("checkYn034Yn")) == true){
            	tempParam.put("reportDetlCd", "034");
            	String reportKeyString = getReportKeyString(tempParam);
            	if("Y".equals(tempParam.get("checkYn034")) == true){
            		tempKeyMap = (Map) existDataMap.remove(reportKeyString);

            		if (tempKeyMap == null) {
            			reportCommDAO.insertReport(reportCd, tempParam);
            		}
            	}else{
            		reportCommDAO.deleteReport(reportCd, tempParam);
            	}
            }
            
            if("Y".equals(tempParam.get("checkYn035Yn")) == true){
            	tempParam.put("reportDetlCd", "035");
            	String reportKeyString = getReportKeyString(tempParam);
            	if("Y".equals(tempParam.get("checkYn035")) == true){
            		tempKeyMap = (Map) existDataMap.remove(reportKeyString);

            		if (tempKeyMap == null) {
            			reportCommDAO.insertReport(reportCd, tempParam);
            		}
            	}else{
            		reportCommDAO.deleteReport(reportCd, tempParam);
            	}
            }
        }

/*
        Map tempKeyMap = null;
        String reportKeyString = "";
        //저장할 데이터들을 저장
        for (int i = 0; i < saveReportDatas.size(); i++) {
            tempParam = (JSONObject) saveReportDatas.get(i);

            tempParam.put("userId", jsonParam.get("userId"));
            tempParam.put("reportCd", reportCd);
            tempParam.put("reportDetlCd", reportDetlCd);
            tempParam.put("orderYmdSeq", orderYmdSeq);

            reportKeyString = getReportKeyString(reportCd, tempParam);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  " + i + "   :  reportKeyString :  " + reportKeyString);
			//이미 등록되어있는 데이터 목록에서 하나씩 가져와서 null일 경우 insert
            tempKeyMap = (Map) existDataMap.remove(reportKeyString);
            if (tempKeyMap == null) {
                reportCommDAO.insertReport(reportCd, tempParam);
            }
        }

        deleteReport(reportCd, existDataMap);
*/
        /*
        if ("070".equals(reportCd) == true) {
            insertReport070s(reportCd, reportDetlCd);
        }

        if ("021".equals(reportDetlCd) == true || "022".equals(reportDetlCd) == true || "023".equals(reportDetlCd) == true) {
            reportCd = "030";
            String sheetCd = "TF0";
            String sheetDetlCd = "TF1";
            
            List saveReportDatas030 = jsonParam.getJSONArray("saveReportDatas030");
            jsonParam.put("reportDetlCd", "031");
            Map existDataMap031 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "032");
            Map existDataMap032 = getExistDataMap(reportCd, jsonParam);
            jsonParam.put("reportDetlCd", "033");
            Map existDataMap033 = getExistDataMap(reportCd, jsonParam);

            jsonParam.put("sheetCd", sheetCd);
            jsonParam.put("sheetDetlCd", sheetDetlCd);
            Map existDataMapTF1 = budgetSheetSelectService.getExistDataMap(jsonParam);
            
            for (int i = 0; i < saveReportDatas030.size(); i++) {
                tempParam = (JSONObject) saveReportDatas030.get(i);

                tempParam.put("userId", jsonParam.get("userId"));
                tempParam.put("reportCd", reportCd);

                reportKeyString = getReportKeyString(tempParam);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  030  reportKeyString : " + reportKeyString);

                if("Y".equals(tempParam.get("checkYn031Yn")) == true){
                    tempParam.put("reportDetlCd", "031");
                    if("Y".equals(tempParam.get("checkYn031")) == true){
                        tempKeyMap = (Map) existDataMap031.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  031  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn032Yn")) == true){
                    tempParam.put("reportDetlCd", "032");
                    if("Y".equals(tempParam.get("checkYn032")) == true){
                        tempKeyMap = (Map) existDataMap032.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  032  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYn033Yn")) == true){
                    tempParam.put("reportDetlCd", "033");
                    if("Y".equals(tempParam.get("checkYn033")) == true){
                        tempKeyMap = (Map) existDataMap033.remove(reportKeyString);
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  033  tempKeyMap : " + tempKeyMap);
                        if (tempKeyMap == null) {
                            reportCommDAO.insertReport(reportCd, tempParam);
                        }
                    }else{
                        reportCommDAO.deleteReport(reportCd, tempParam);
                    }
                }

                if("Y".equals(tempParam.get("checkYnTf1Yn")) == true){
                    tempParam.put("sheetDetlCd", sheetDetlCd);
                    if("Y".equals(tempParam.get("checkYnTf1")) == true){
                        tempKeyMap = (Map) existDataMapTF1.remove(reportKeyString);
                        if (tempKeyMap == null) {
                            budgetSheetSelectDAO.insertSheet(tempParam);
                        }
                    }else{
                        budgetSheetSelectDAO.deleteSheet(tempParam);
                    }
                }
            }
        }*/
    }
    
    public void insertReport070s(String reportCd, String reportDetlCd) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("reportCd", reportCd);
        map.put("reportDetlCd", reportDetlCd);
        
        reportCommDAO.deleteReport(map);
        reportCommDAO.insertReport070s(map);
    }

    /**
     * 저장 대상 세세목들의 TB_REPORT 키를 일괄 조회하여
     * Map&lt;teBgtCompoId, Map&lt;reportKey, row&gt;&gt; 형태로 반환.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMapByCompoIds(List saveDatas, Map jsonParam) throws Exception {
        Map result = new HashMap();
        if (saveDatas == null || saveDatas.size() < 1) {
            return result;
        }

        List teBgtCompoIds = new ArrayList();
        Set idSet = new HashSet();
        String fisYear = "";
        String bgtDgr = "";
        for (int i = 0; i < saveDatas.size(); i++) {
            Map row = (Map) saveDatas.get(i);
            String teId = String.valueOf(row.get("teBgtCompoId"));
            if (teId == null || "".equals(teId) || "null".equals(teId) || idSet.contains(teId)) {
                continue;
            }
            idSet.add(teId);
            teBgtCompoIds.add(teId);
            if ("".equals(fisYear)) {
                fisYear = String.valueOf(row.get("fisYear"));
                bgtDgr = String.valueOf(row.get("bgtDgr"));
            }
        }
        if (teBgtCompoIds.isEmpty()) {
            return result;
        }
        if (fisYear == null || "".equals(fisYear) || "null".equals(fisYear)) {
            fisYear = String.valueOf(jsonParam.get("fisYear"));
            bgtDgr = String.valueOf(jsonParam.get("bgtDgr"));
        }

        // IN 절 길이 제한 대비 청크 조회
        int chunkSize = 200;
        for (int from = 0; from < teBgtCompoIds.size(); from += chunkSize) {
            int to = Math.min(from + chunkSize, teBgtCompoIds.size());
            List chunk = teBgtCompoIds.subList(from, to);
            Map param = new HashMap();
            param.put("fisYear", fisYear);
            param.put("bgtDgr", bgtDgr);
            param.put("teBgtCompoIds", new ArrayList(chunk));
            List existDatas = budgetSelectDAO.selectReportKeyListFastBatch(param);
            if (existDatas == null || existDatas.isEmpty()) {
                continue;
            }
            while (!existDatas.isEmpty()) {
                Map tempMap = (Map) existDatas.remove(0);
                String teId = String.valueOf(tempMap.get("teBgtCompoId"));
                Map keyMap = (Map) result.get(teId);
                if (keyMap == null) {
                    keyMap = new HashMap();
                    result.put(teId, keyMap);
                }
                keyMap.put(getReportKeyString(tempMap), tempMap);
            }
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExistDataMap(Map map) throws Exception {
        Map existDataMap = new HashMap();
        // 기존 selectReportKeyListNew(다테이블 조인) 대신 TB_REPORT 단건 조회로 저장 지연 해소
        List existDatas = budgetSelectDAO.selectReportKeyListFast(map);
        if (existDatas == null || existDatas.size() < 1) {
            return existDataMap;
        }

        Map tempMap = null;
        while (!existDatas.isEmpty()) {
            tempMap = (Map) existDatas.remove(0);
            existDataMap.put(getReportKeyString(tempMap), tempMap);
        }

        return existDataMap;
    }

    @SuppressWarnings("rawtypes")
    public void deleteReport(Map map) throws Exception {
        if (map == null || map.keySet() == null) {
            return;
        }

        Iterator iterator = map.keySet().iterator();
        if (iterator == null) {
            return;
        }

        String key = "";
        Map tempMap = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            tempMap = (Map) map.get(key);
            String reportCd = String.valueOf(tempMap.get("reportCd"));
            
            if(!"030".equals(reportCd)){
            	reportCommDAO.deleteReport(reportCd, tempMap);
            }
            
            
        }
    }

    @SuppressWarnings("rawtypes")
    public String getReportKeyString(Map map) {
    	String reportCd = String.valueOf(map.get("reportCd"));
    	
        if ("070".equals(reportCd) == true) {
            return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("orderYmdSeq")) + "_" + String.valueOf(map.get("teBgtCompoId"));
        }

        return String.valueOf(map.get("reportCd")) + "_" + String.valueOf(map.get("reportDetlCd")) + "_" + String.valueOf(map.get("fisYear")) + "_" + String.valueOf(map.get("bgtDgr")) + "_" + String.valueOf(map.get("teBgtCompoId"));
    }

    private void initCdData(){
    	reportCdToSheetCd = new HashMap<String, String>();
    	reportDetlCdToSheetDetlCd = new HashMap<String, String>();
    	
    	
    	reportCdToSheetCd.put("101", "TI1"); //예산삭감
    	reportDetlCdToSheetDetlCd.put("100", "TI0"); //예산삭감
    	reportCdToSheetCd.put("150", "T90"); //지방채상환 
    	reportDetlCdToSheetDetlCd.put("151", "T91"); //지방채상환
    	
    }
    
    public boolean spaceCheck(String spaceCheck)
    {
        for(int i = 0 ; i < spaceCheck.length() ; i++)
        {
            if(spaceCheck.charAt(i) == ' ')
                return true;
        }
        return false;
    }

}

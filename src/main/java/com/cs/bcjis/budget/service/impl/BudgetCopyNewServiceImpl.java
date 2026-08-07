package com.cs.bcjis.budget.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;
import com.cs.bcjis.report.service.impl.ReportWrite0F0DAO;

@Service("budgetCopyNewService")
public class BudgetCopyNewServiceImpl implements BudgetCopyNewService {

	@Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
	
	@Resource(name = "budgetCommDAO")
    private BudgetCommDAO budgetCommDAO;
	
    @Resource(name = "budgetCopyNewDAO")
    private BudgetCopyNewDAO budgetCopyNewDAO;

    @Resource(name = "budgetSheetSelectDAO")
    private BudgetSheetSelectDAO budgetSheetSelectDAO;

    @Resource(name = "reportWrite0F0DAO")
    private ReportWrite0F0DAO reportWrite0F0DAO;

    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception {
    	
    	map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));
    	
        return budgetCopyNewDAO.selectCopyReportList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        inheritReportNature(map);
        reportCommDAO.copyReport(map);
        // 투자사업심사조서: 시군 CHECK_YN(TB_REPORT020_D)은 기정예산 상속.
        // 보고(MAYOR_REPORT_YN)는 당해 판단 항목이므로 상속하지 않음(미체크).
        if (map != null && "020".equals(String.valueOf(map.get("reportCd")))) {
            clearMayorReportYn(map);
            reportCommDAO.copyReport020D(map);
        }
        inheritReportAttrs(map);
        budgetCommDAO.copyPreInfo(map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List selectCopyNewMapList(Map map) throws Exception {
        // 분류(중분류) 미선택 시 전체 세세목 조회 — 조서성질 상속 매핑용
        if (BcjisCommUtil.isNullString(map.get("reportCd"))) {
            return budgetCopyNewDAO.selectCopyNewMapListAll(map);
        }

        map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));

        return budgetCopyNewDAO.selectCopyNewMapList(map);
    }

    /**
     * 매핑 일괄 적용.
     * 세세목 복사 후 상위금액 재계산은 부모 단위로 1회만 수행해 N건 반복 집계 비용을 줄인다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void copyReportBatch(List<Map> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            return;
        }

        if (mappings.size() == 1) {
            copyReport(mappings.get(0));
            return;
        }

        Set<String> rolledParents = new HashSet<String>();

        for (int i = 0; i < mappings.size(); i++) {
            Map map = mappings.get(i);
            inheritReportNature(map);
            reportCommDAO.copyReport(map);
            if (map != null && "020".equals(String.valueOf(map.get("reportCd")))) {
                clearMayorReportYn(map);
                reportCommDAO.copyReport020D(map);
            }
            inheritReportAttrs(map);
            budgetCommDAO.copyPreInfoLeaf(map);
        }

        // 동일 부모는 1회만 상위 집계 (자식 세세목 복사가 모두 끝난 뒤)
        for (int i = 0; i < mappings.size(); i++) {
            Map map = mappings.get(i);
            Map upParam = budgetCommDAO.selectUpDgrcompoInfo(map);
            if (upParam == null) {
                continue;
            }
            if (BcjisCommUtil.isNullString(upParam.get("teBgtCompoId"))
                    || "00000000000".equals(String.valueOf(upParam.get("teBgtCompoId")))) {
                continue;
            }

            String key = String.valueOf(upParam.get("fisYear")) + "_"
                    + String.valueOf(upParam.get("bgtDgr")) + "_"
                    + String.valueOf(upParam.get("teBgtCompoId"));
            if (rolledParents.contains(key)) {
                continue;
            }
            rolledParents.add(key);

            if (map.get("userId") != null) {
                upParam.put("userId", map.get("userId"));
            }
            budgetCommDAO.saveUpDgrcompoInfoAll(upParam);
        }
    }

    /** 투자심사조서 '보고' 체크는 기정예산에서 상속하지 않음 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void clearMayorReportYn(Map map) throws Exception {
        if (map == null) {
            return;
        }
        Map clear = new HashMap();
        clear.put("reportCd", map.get("reportCd"));
        clear.put("reportDetlCd", map.get("reportDetlCd"));
        clear.put("fisYear", map.get("fisYear"));
        clear.put("bgtDgr", map.get("bgtDgr"));
        clear.put("teBgtCompoId", map.get("teBgtCompoId"));
        clear.put("mayorReportYn", "N");
        reportCommDAO.updateMayorReportYnReport(clear);
    }

    /**
     * 기정예산 사업의 조서성질(분류·집계표)을 적용대상에 상속.
     * 상단 조서.집계 선택과 무관하게 기정예산 사업(TE_BGT_COMPO)에 등록된 성질을 기준으로 한다.
     * TB_REPORT / TB_SHEET 를 대상 키로 insert 또는 update 한 뒤
     * 기존 copyReport(본문)가 동작하도록 map 의 reportCd/reportDetlCd 를 맞춘다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void inheritReportNature(Map map) throws Exception {
        if (map == null || BcjisCommUtil.isNullString(map.get("srcTeBgtCompoId"))) {
            return;
        }

        Map srcNature = budgetCopyNewDAO.selectSrcReportNature(map);
        if (srcNature == null || BcjisCommUtil.isNullString(srcNature.get("reportCd"))) {
            return;
        }

        String reportCd = String.valueOf(srcNature.get("reportCd"));
        String reportDetlCd = String.valueOf(srcNature.get("reportDetlCd"));

        map.put("reportCd", reportCd);
        map.put("reportDetlCd", reportDetlCd);
        map.put("srcReportCd", reportCd);
        map.put("srcReportDetlCd", reportDetlCd);

        Map tgt = new HashMap();
        tgt.put("reportCd", reportCd);
        tgt.put("reportDetlCd", reportDetlCd);
        tgt.put("fisYear", map.get("fisYear"));
        tgt.put("bgtDgr", map.get("bgtDgr"));
        tgt.put("teBgtCompoId", map.get("teBgtCompoId"));
        tgt.put("reportMstr", srcNature.get("reportMstr"));
        tgt.put("govSub", srcNature.get("govSub"));
        tgt.put("indiAttr", srcNature.get("indiAttr"));
        tgt.put("advncProc", srcNature.get("advncProc"));
        tgt.put("userId", map.get("userId"));

        Object teBgtCompoSeq = map.get("teBgtCompoSeq");
        if (BcjisCommUtil.isNullString(teBgtCompoSeq)) {
            teBgtCompoSeq = budgetCopyNewDAO.selectTeBgtCompoSeq(tgt);
        }
        if (teBgtCompoSeq == null) {
            teBgtCompoSeq = Integer.valueOf(0);
        }
        tgt.put("teBgtCompoSeq", teBgtCompoSeq);
        map.put("teBgtCompoSeq", teBgtCompoSeq);

        if (budgetCopyNewDAO.selectReportCnt(tgt) < 1) {
            reportCommDAO.insertReport(reportCd, tgt);
        } else {
            reportCommDAO.updateReport(tgt);
        }

        inheritSheets(map, teBgtCompoSeq);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void inheritSheets(Map map, Object teBgtCompoSeq) throws Exception {
        List sheets = budgetCopyNewDAO.selectSrcSheetList(map);
        if (sheets == null || sheets.isEmpty()) {
            return;
        }

        for (int i = 0; i < sheets.size(); i++) {
            Map sheet = (Map) sheets.get(i);
            Map sheetParam = new HashMap();
            sheetParam.put("sheetCd", sheet.get("sheetCd"));
            sheetParam.put("sheetDetlCd", sheet.get("sheetDetlCd"));
            sheetParam.put("fisYear", map.get("fisYear"));
            sheetParam.put("bgtDgr", map.get("bgtDgr"));
            sheetParam.put("teBgtCompoId", map.get("teBgtCompoId"));
            sheetParam.put("teBgtCompoSeq", teBgtCompoSeq);
            sheetParam.put("userId", map.get("userId"));

            if (budgetSheetSelectDAO.selectSheetCnt(sheetParam) < 1) {
                budgetSheetSelectDAO.insertSheet(sheetParam);
            }
        }
    }

    /** 보고항목(TB_REPORT_ATTR) 상속 — 조서 본문 복사 후 호출 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void inheritReportAttrs(Map map) throws Exception {
        if (map == null || BcjisCommUtil.isNullString(map.get("srcReportCd"))) {
            return;
        }
        String reportCd = String.valueOf(map.get("reportCd"));
        String reportDetlCd = String.valueOf(map.get("reportDetlCd"));
        if (BcjisCommUtil.isNullString(reportCd)) {
            return;
        }

        List attrs = budgetCopyNewDAO.selectSrcReportAttrList(map);
        if (attrs == null || attrs.isEmpty()) {
            return;
        }

        for (int i = 0; i < attrs.size(); i++) {
            Map attr = (Map) attrs.get(i);
            if (BcjisCommUtil.isNullString(attr.get("indiAttr"))) {
                continue;
            }

            Map attrParam = new HashMap();
            attrParam.put("reportCd", reportCd);
            attrParam.put("reportDetlCd", reportDetlCd);
            attrParam.put("fisYear", map.get("fisYear"));
            attrParam.put("bgtDgr", map.get("bgtDgr"));
            attrParam.put("teBgtCompoId", map.get("teBgtCompoId"));
            attrParam.put("indiAttr", attr.get("indiAttr"));
            attrParam.put("userId", map.get("userId"));

            int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
            if (cnt < 1) {
                reportWrite0F0DAO.insertReportAttrSel(attrParam);
            }
        }
    }
}

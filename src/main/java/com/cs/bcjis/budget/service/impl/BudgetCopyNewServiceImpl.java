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
        boolean natureMatched = inheritReportNature(map);
        if (natureMatched) {
            reportCommDAO.copyReport(map);
            // 투자사업심사조서: 시군 CHECK_YN(TB_REPORT020_D)은 기정예산 상속.
            // 보고(MAYOR_REPORT_YN)는 당해 판단 항목이므로 상속하지 않음(미체크).
            if (map != null && "020".equals(String.valueOf(map.get("reportCd")))) {
                clearMayorReportYn(map);
                reportCommDAO.copyReport020D(map);
            }
            inheritReportAttrs(map);
        }
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
            boolean natureMatched = inheritReportNature(map);
            if (natureMatched) {
                reportCommDAO.copyReport(map);
                if (map != null && "020".equals(String.valueOf(map.get("reportCd")))) {
                    clearMayorReportYn(map);
                    reportCommDAO.copyReport020D(map);
                }
                inheritReportAttrs(map);
            }
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
     *
     * 대상(올해) 세세목이 이미 갖고 있는 조서 종류(REPORT_CD)와 원본(전년도) 세세목의
     * 조서 종류를 비교해 다음 네 가지로만 동작한다. 어느 경우든 REPORT_CD/REPORT_DETL_CD
     * 자체는 절대 새로 만들거나 지우지 않는다 — 그래야 한 세세목이 서로 다른 두 조서에
     * 동시에 집계되는 중복이 생기지 않는다.
     *   1) 원본·대상 종류가 같음        → 종류는 그대로 두고 내용(REPORT_MSTR/GOV_SUB 등)만 갱신
     *   2) 원본·대상 종류가 서로 다름   → 무시(둘 다 손대지 않음)
     *   3) 원본만 있고 대상은 없음      → 무시(새로 만들지 않음)
     *   4) 원본이 없고 대상만 있음      → 무시(기존 것을 지우지 않음)
     *
     * INDI_ATTR(투자사업유형)/ADVNC_PROC(분류항목)은 심사조서 보고항목선택
     * (/budget/budgetSelectAttr.do) 화면에서 별도로 관리하는 항목이라 어느 경우에도
     * 이 상속 대상에서 제외한다 — 갱신 시에도 대상이 원래 갖고 있던 값을 그대로 유지한다.
     *
     * @return 종류가 일치해 실제로 상속(내용 갱신)을 수행했으면 true.
     *         false면 호출부는 본문/보고항목 복사를 건너뛰어야 한다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean inheritReportNature(Map map) throws Exception {
        if (map == null || BcjisCommUtil.isNullString(map.get("srcTeBgtCompoId"))) {
            return false;
        }

        Map srcNature = budgetCopyNewDAO.selectSrcReportNature(map);
        if (srcNature == null || BcjisCommUtil.isNullString(srcNature.get("reportCd"))) {
            // case 3/4의 "원본 없음" 쪽 — 대상에 뭐가 있든 손대지 않는다.
            return false;
        }
        String srcReportCd = String.valueOf(srcNature.get("reportCd"));

        Map tgtKey = new HashMap();
        tgtKey.put("fisYear", map.get("fisYear"));
        tgtKey.put("bgtDgr", map.get("bgtDgr"));
        tgtKey.put("teBgtCompoId", map.get("teBgtCompoId"));

        Map tgtExisting = selectTgtReportNature(tgtKey);
        if (tgtExisting == null) {
            // case 3: 원본은 있고 대상은 아직 없음 — 새로 만들지 않는다.
            return false;
        }

        String tgtReportCd = String.valueOf(tgtExisting.get("reportCd"));
        if (!tgtReportCd.equals(srcReportCd)) {
            // case 2: 서로 종류가 다름 — 무시.
            return false;
        }

        // case 1: 종류가 같음 — 대상이 이미 갖고 있는 REPORT_CD/REPORT_DETL_CD는 그대로 두고
        // 나머지 내용(REPORT_MSTR/GOV_SUB)만 원본 값으로 갱신한다.
        // INDI_ATTR(투자사업유형)/ADVNC_PROC(분류항목)은 심사조서 보고항목선택
        // (/budget/budgetSelectAttr.do) 화면에서 별도로 관리하는 항목이므로 이 자동매칭/
        // 일괄적용 대상에서 제외한다 — 대상이 이미 갖고 있던 값을 그대로 유지시킨다.
        String tgtReportDetlCd = String.valueOf(tgtExisting.get("reportDetlCd"));

        map.put("reportCd", tgtReportCd);
        map.put("reportDetlCd", tgtReportDetlCd);
        map.put("srcReportCd", srcReportCd);
        map.put("srcReportDetlCd", String.valueOf(srcNature.get("reportDetlCd")));

        Map tgt = new HashMap();
        tgt.put("reportCd", tgtReportCd);
        tgt.put("reportDetlCd", tgtReportDetlCd);
        tgt.put("fisYear", map.get("fisYear"));
        tgt.put("bgtDgr", map.get("bgtDgr"));
        tgt.put("teBgtCompoId", map.get("teBgtCompoId"));
        tgt.put("reportMstr", srcNature.get("reportMstr"));
        tgt.put("govSub", srcNature.get("govSub"));
        // 원본(전년도) 값이 아니라 대상이 이미 갖고 있던 값을 그대로 되돌려 넣어 보존한다.
        tgt.put("indiAttr", tgtExisting.get("indiAttr"));
        tgt.put("advncProc", tgtExisting.get("advncProc"));
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

        reportCommDAO.updateReport(tgt);

        inheritSheets(map, teBgtCompoSeq);
        return true;
    }

    /**
     * 대상(올해) 세세목이 현재 갖고 있는 조서성질 1건을 반환.
     * "030"(국고보조사업심사조서)은 별도 화면(reportWrite030)에서 부서단위로 관리되는
     * 독립된 성질이라 여기서는 비교 대상에서 제외한다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map selectTgtReportNature(Map tgtKey) throws Exception {
        List existKeys = budgetCopyNewDAO.selectExistReportKeys(tgtKey);
        if (existKeys == null || existKeys.isEmpty()) {
            return null;
        }

        for (int i = 0; i < existKeys.size(); i++) {
            Map existKey = (Map) existKeys.get(i);
            if (!"030".equals(String.valueOf(existKey.get("reportCd")))) {
                return existKey;
            }
        }
        return null;
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
            attrParam.put("reportTableNm", "TB_REPORT" + reportCd);

            int cnt = reportWrite0F0DAO.selectReportAttrCnt(attrParam);
            if (cnt < 1) {
                reportWrite0F0DAO.insertReportAttrSel(attrParam);
            }
        }
    }
}

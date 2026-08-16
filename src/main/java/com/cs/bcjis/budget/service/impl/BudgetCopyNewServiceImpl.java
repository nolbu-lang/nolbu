package com.cs.bcjis.budget.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    /** 일괄적용 시 기정예산 조회 결과를 재사용 */
    private static class CopyBatchCache {
        Map<String, Map> natureBySrc = new HashMap<String, Map>();
        Map<String, List> sheetsBySrc = new HashMap<String, List>();
        Map<String, List> attrsBySrc = new HashMap<String, List>();
        Map<String, String> reportMstrByCd = new HashMap<String, String>();
    }

    @SuppressWarnings("rawtypes")
    public List selectCopyReportList(Map map) throws Exception {
    	
    	map.put("reportTableNm", "TB_REPORT" + map.get("reportCd"));
    	
        return budgetCopyNewDAO.selectCopyReportList(map);
    }
    
    @SuppressWarnings("rawtypes")
    public void copyReport(Map map) throws Exception {
        inheritReportNature(map, false, null);
        reportCommDAO.copyReport(map);
        // 투자사업심사조서: 시군 CHECK_YN(TB_REPORT020_D)은 기정예산 상속.
        // 보고(MAYOR_REPORT_YN)는 당해 판단 항목이므로 상속하지 않음(미체크).
        if (map != null && "020".equals(String.valueOf(map.get("reportCd")))) {
            clearMayorReportYn(map);
            reportCommDAO.copyReport020D(map);
        }
        inheritReportAttrs(map, null);
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
     * 기정예산 성질/집계/보고항목은 배치 조회로 1회 적재 후 재사용한다.
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

        CopyBatchCache cache = new CopyBatchCache();
        preloadBatchCache(mappings, cache);

        Set<String> rolledParents = new HashSet<String>();

        for (int i = 0; i < mappings.size(); i++) {
            Map map = mappings.get(i);
            inheritReportNature(map, false, cache);
            if (BcjisCommUtil.isNullString(map.get("reportCd"))) {
                // 조서성질이 없으면 본문 복사 불가 — 기정액만 복사
                budgetCommDAO.copyPreInfoLeaf(map);
                continue;
            }
            reportCommDAO.copyReport(map);
            if ("020".equals(String.valueOf(map.get("reportCd")))) {
                clearMayorReportYn(map);
                reportCommDAO.copyReport020D(map);
            }
            inheritReportAttrs(map, cache);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void preloadBatchCache(List<Map> mappings, CopyBatchCache cache) throws Exception {
        // (srcFisYear|srcBgtDgr) -> srcTeBgtCompoId set
        Map<String, Set<String>> idsByYearDgr = new HashMap<String, Set<String>>();
        for (int i = 0; i < mappings.size(); i++) {
            Map map = mappings.get(i);
            if (map == null || BcjisCommUtil.isNullString(map.get("srcTeBgtCompoId"))) {
                continue;
            }
            String year = String.valueOf(map.get("srcFisYear"));
            String dgr = String.valueOf(map.get("srcBgtDgr"));
            String id = String.valueOf(map.get("srcTeBgtCompoId")).trim();
            if (!isSafeCompoId(id)) {
                continue;
            }
            String gd = year + "|" + dgr;
            Set<String> ids = idsByYearDgr.get(gd);
            if (ids == null) {
                ids = new HashSet<String>();
                idsByYearDgr.put(gd, ids);
            }
            ids.add(id);
        }

        Iterator<Map.Entry<String, Set<String>>> it = idsByYearDgr.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<String>> e = it.next();
            String[] yd = e.getKey().split("\\|", 2);
            String year = yd[0];
            String dgr = yd.length > 1 ? yd[1] : "";
            List<String> idList = new ArrayList<String>(e.getValue());
            // IN 절 길이 제한 대비 청크
            int chunk = 200;
            for (int from = 0; from < idList.size(); from += chunk) {
                int to = Math.min(from + chunk, idList.size());
                String inClause = buildSafeInClause(idList.subList(from, to));
                if (inClause.length() < 1) {
                    continue;
                }
                Map param = new HashMap();
                param.put("srcFisYear", year);
                param.put("srcBgtDgr", dgr);
                param.put("srcTeBgtCompoIdIn", inClause);

                List natures = budgetCopyNewDAO.selectSrcReportNatureBatch(param);
                if (natures != null) {
                    for (int i = 0; i < natures.size(); i++) {
                        Map row = (Map) natures.get(i);
                        String id = nvlTrim(row.get("teBgtCompoId"));
                        if (id.length() < 1) {
                            continue;
                        }
                        String key = srcCacheKey(year, dgr, id);
                        if (!cache.natureBySrc.containsKey(key)) {
                            cache.natureBySrc.put(key, row);
                        }
                    }
                }

                List sheets = budgetCopyNewDAO.selectSrcSheetListBatch(param);
                if (sheets != null) {
                    for (int i = 0; i < sheets.size(); i++) {
                        Map row = (Map) sheets.get(i);
                        String id = nvlTrim(row.get("teBgtCompoId"));
                        if (id.length() < 1) {
                            continue;
                        }
                        String key = srcCacheKey(year, dgr, id);
                        List list = cache.sheetsBySrc.get(key);
                        if (list == null) {
                            list = new ArrayList();
                            cache.sheetsBySrc.put(key, list);
                        }
                        list.add(row);
                    }
                }

                List attrs = budgetCopyNewDAO.selectSrcReportAttrListBatch(param);
                if (attrs != null) {
                    for (int i = 0; i < attrs.size(); i++) {
                        Map row = (Map) attrs.get(i);
                        String id = nvlTrim(row.get("teBgtCompoId"));
                        if (id.length() < 1) {
                            continue;
                        }
                        String key = srcCacheKey(year, dgr, id);
                        List list = cache.attrsBySrc.get(key);
                        if (list == null) {
                            list = new ArrayList();
                            cache.attrsBySrc.put(key, list);
                        }
                        list.add(row);
                    }
                }
            }
            // 시트/속성이 없는 출처도 빈 목록으로 표시해 재조회 방지
            for (int i = 0; i < idList.size(); i++) {
                String key = srcCacheKey(year, dgr, idList.get(i));
                if (!cache.sheetsBySrc.containsKey(key)) {
                    cache.sheetsBySrc.put(key, new ArrayList());
                }
                if (!cache.attrsBySrc.containsKey(key)) {
                    cache.attrsBySrc.put(key, new ArrayList());
                }
            }
        }
    }

    private String srcCacheKey(Object year, Object dgr, Object id) {
        return String.valueOf(year) + "_" + String.valueOf(dgr) + "_" + String.valueOf(id);
    }

    private boolean isSafeCompoId(String id) {
        if (id == null || id.length() < 1 || id.length() > 32) {
            return false;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                return false;
            }
        }
        return true;
    }

    private String buildSafeInClause(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            if (!isSafeCompoId(id)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("'").append(id).append("'");
        }
        return sb.toString();
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
    public void inheritReportNatureOnly(Map map) throws Exception {
        inheritReportNature(map, true, null);
        try {
            inheritReportAttrs(map, null);
        } catch (Exception e) {
            // 분류(TB_REPORT) 상속은 유지. 보고항목은 없어도 조서·집계 표시·수정적용 가능.
        }
    }

    private String nvlTrim(Object o) {
        if (BcjisCommUtil.isNullString(o)) {
            return "";
        }
        return String.valueOf(o).trim();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void inheritReportNature(Map map, boolean natureOnly, CopyBatchCache cache) throws Exception {
        if (map == null || BcjisCommUtil.isNullString(map.get("srcTeBgtCompoId"))) {
            return;
        }

        Map srcNature = null;
        String srcKey = srcCacheKey(map.get("srcFisYear"), map.get("srcBgtDgr"), map.get("srcTeBgtCompoId"));
        if (cache != null && cache.natureBySrc.containsKey(srcKey)) {
            srcNature = cache.natureBySrc.get(srcKey);
        }

        if (srcNature == null) {
            srcNature = budgetCopyNewDAO.selectSrcReportNature(map);
            // srcReportCd 지정 시 해당 건에 분류가 없으면 사업 전체에서 재조회
            if ((srcNature == null || nvlTrim(srcNature.get("reportCd")).length() < 1
                    || nvlTrim(srcNature.get("reportMstr")).length() < 1)
                    && !BcjisCommUtil.isNullString(map.get("srcReportCd"))) {
                Object keepCd = map.get("srcReportCd");
                Object keepDetl = map.get("srcReportDetlCd");
                map.remove("srcReportCd");
                map.remove("srcReportDetlCd");
                Map alt = budgetCopyNewDAO.selectSrcReportNature(map);
                map.put("srcReportCd", keepCd);
                map.put("srcReportDetlCd", keepDetl);
                if (alt != null && nvlTrim(alt.get("reportCd")).length() > 0) {
                    srcNature = alt;
                }
            }
            if (cache != null && srcNature != null) {
                cache.natureBySrc.put(srcKey, srcNature);
            }
        }

        if (srcNature == null || nvlTrim(srcNature.get("reportCd")).length() < 1) {
            return;
        }

        String reportCd = nvlTrim(srcNature.get("reportCd"));
        String reportDetlCd = nvlTrim(srcNature.get("reportDetlCd"));
        String reportMstr = nvlTrim(srcNature.get("reportMstr"));
        if (reportMstr.length() < 1 && reportCd.length() > 0) {
            if (cache != null && cache.reportMstrByCd.containsKey(reportCd)) {
                reportMstr = cache.reportMstrByCd.get(reportCd);
            } else {
                reportMstr = nvlTrim(budgetCopyNewDAO.selectReportMstrByReportCd(reportCd));
                if (cache != null) {
                    cache.reportMstrByCd.put(reportCd, reportMstr);
                }
            }
        }
        if (reportCd.length() < 1 && reportDetlCd.length() > 0) {
            reportCd = nvlTrim(budgetCopyNewDAO.selectReportCdByReportDetlCd(reportDetlCd));
        }
        if (reportCd.length() < 1) {
            return;
        }

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
        tgt.put("reportMstr", reportMstr);
        tgt.put("govSub", nvlTrim(srcNature.get("govSub")));
        tgt.put("indiAttr", nvlTrim(srcNature.get("indiAttr")));
        tgt.put("advncProc", nvlTrim(srcNature.get("advncProc")));
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
            saveInheritedReport(tgt, reportCd, natureOnly);
        } else {
            reportCommDAO.updateReport(tgt);
        }

        // 적용대상에 기존 분류가 없을 때는 정리 조회 생략 (일괄적용 속도)
        if (!"Y".equals(String.valueOf(map.get("tgtReportEmpty")))) {
            removeOtherReportNatures(map, reportCd, reportDetlCd);
        }

        try {
            inheritSheets(map, teBgtCompoSeq, cache);
        } catch (Exception e) {
            // 집계표 항목이 없어도 대/중/소 분류는 표시·수정적용 가능
        }
    }

    @SuppressWarnings("rawtypes")
    private void saveInheritedReport(Map tgt, String reportCd, boolean natureOnly) throws Exception {
        if (natureOnly) {
            budgetCopyNewDAO.insertReportNature(tgt);
            return;
        }
        try {
            reportCommDAO.insertReport(reportCd, tgt);
        } catch (Exception e) {
            if (budgetCopyNewDAO.selectReportCnt(tgt) < 1) {
                budgetCopyNewDAO.insertReportNature(tgt);
            } else {
                reportCommDAO.updateReport(tgt);
            }
        }
    }

    /** 상속한 분류 외 TB_REPORT(본조서) 행 삭제 — 030/070 등 집계성 제외 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void removeOtherReportNatures(Map map, String keepReportCd, String keepReportDetlCd) throws Exception {
        List others = budgetCopyNewDAO.selectTgtReportNatureList(map);
        if (others == null || others.isEmpty()) {
            return;
        }
        for (int i = 0; i < others.size(); i++) {
            Map row = (Map) others.get(i);
            String rc = String.valueOf(row.get("reportCd"));
            String rd = String.valueOf(row.get("reportDetlCd"));
            if (keepReportCd.equals(rc) && String.valueOf(keepReportDetlCd).equals(rd)) {
                continue;
            }
            if ("030".equals(rc) || "070".equals(rc) || "0A0".equals(rc)
                    || "0B0".equals(rc) || "0C0".equals(rc) || "0D0".equals(rc)) {
                continue;
            }
            Map del = new HashMap();
            del.put("reportCd", rc);
            del.put("reportDetlCd", rd);
            del.put("fisYear", map.get("fisYear"));
            del.put("bgtDgr", map.get("bgtDgr"));
            del.put("teBgtCompoId", map.get("teBgtCompoId"));
            del.put("userId", map.get("userId"));
            reportCommDAO.deleteReport(rc, del);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void inheritSheets(Map map, Object teBgtCompoSeq, CopyBatchCache cache) throws Exception {
        List sheets = null;
        String srcKey = srcCacheKey(map.get("srcFisYear"), map.get("srcBgtDgr"), map.get("srcTeBgtCompoId"));
        if (cache != null && cache.sheetsBySrc.containsKey(srcKey)) {
            sheets = cache.sheetsBySrc.get(srcKey);
        } else {
            sheets = budgetCopyNewDAO.selectSrcSheetList(map);
            if (cache != null) {
                cache.sheetsBySrc.put(srcKey, sheets == null ? new ArrayList() : sheets);
            }
        }
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
    private void inheritReportAttrs(Map map, CopyBatchCache cache) throws Exception {
        if (map == null || BcjisCommUtil.isNullString(map.get("srcReportCd"))) {
            return;
        }
        String reportCd = String.valueOf(map.get("reportCd"));
        String reportDetlCd = String.valueOf(map.get("reportDetlCd"));
        if (BcjisCommUtil.isNullString(reportCd)) {
            return;
        }

        List attrs = null;
        String srcKey = srcCacheKey(map.get("srcFisYear"), map.get("srcBgtDgr"), map.get("srcTeBgtCompoId"));
        if (cache != null && cache.attrsBySrc.containsKey(srcKey)) {
            attrs = cache.attrsBySrc.get(srcKey);
        } else {
            attrs = budgetCopyNewDAO.selectSrcReportAttrList(map);
            if (cache != null) {
                cache.attrsBySrc.put(srcKey, attrs == null ? new ArrayList() : attrs);
            }
        }
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

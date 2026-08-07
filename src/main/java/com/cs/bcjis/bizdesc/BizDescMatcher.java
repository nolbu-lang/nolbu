package com.cs.bcjis.bizdesc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cs.bcjis.bizdesc.HwpxBizDescParser.BizBlock;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 사업설명서 블록 ↔ 심사조서 사업명 유사도 매칭.
 */
public class BizDescMatcher {

    public static final double NAME_MATCH_THRESHOLD = 0.60;

    public static class NameSuggest {
        public String bizdescFileId;
        public int bizSeq;
        public String bizNm;
        public String detailBiz;
        public String indivBiz;
        public String deptNm;
        public double score;
    }

    /**
     * 조서 사업명 기준으로 업로드된 사업설명서 사업 중 유사도 ≥ threshold 목록.
     */
    public List<NameSuggest> suggestByReportName(String reportBizNm, List<BizBlock> businesses,
            String bizdescFileId, double threshold) {
        List<NameSuggest> out = new ArrayList<NameSuggest>();
        String target = normalizeName(reportBizNm);
        if (target.length() == 0 || businesses == null) {
            return out;
        }
        for (BizBlock biz : businesses) {
            double score = Math.max(
                    similarity(target, normalizeName(biz.indivBiz)),
                    similarity(target, normalizeName(biz.detailBiz)));
            if (score >= threshold) {
                NameSuggest s = new NameSuggest();
                s.bizdescFileId = bizdescFileId;
                s.bizSeq = biz.seq;
                s.bizNm = biz.bizNm();
                s.detailBiz = biz.detailBiz;
                s.indivBiz = biz.indivBiz;
                s.deptNm = biz.dept;
                s.score = round3(score);
                out.add(s);
            }
        }
        Collections.sort(out, new Comparator<NameSuggest>() {
            public int compare(NameSuggest a, NameSuggest b) {
                return Double.compare(b.score, a.score);
            }
        });
        return out;
    }

    public static String buildDemandCont(BizBlock biz) {
        return biz == null ? "" : biz.buildDemandCont();
    }

    public static String buildExamCont(BizBlock biz) {
        return biz == null ? "" : biz.buildExamCont();
    }

    public static JSONObject toJson(BizBlock biz) {
        JSONObject o = new JSONObject();
        o.put("seq", biz.seq);
        o.put("dept", n(biz.dept));
        o.put("manager", n(biz.manager));
        o.put("detailBiz", n(biz.detailBiz));
        o.put("indivBiz", n(biz.indivBiz));
        o.put("mokCd", n(biz.mokCd));
        o.put("overviewLines", JSONArray.fromObject(biz.overviewLines));
        o.put("contentLines", JSONArray.fromObject(biz.contentLines));
        o.put("progressLines", JSONArray.fromObject(biz.progressLines));
        o.put("reasonLines", JSONArray.fromObject(biz.reasonLines));
        o.put("calcLines", JSONArray.fromObject(biz.calcLines));
        o.put("planLines", JSONArray.fromObject(biz.planLines));
        o.put("procedureLines", JSONArray.fromObject(biz.procedureLines));
        o.put("yearlyBudgetLines", JSONArray.fromObject(biz.yearlyBudgetLines));
        o.put("extractedTables", tablesToJson(biz.extractedTables));
        // blocks는 표시 시 buildBlocksFromLines로 재생성 (포맷 변경 반영)
        o.put("blocks", blocksToJson(biz.blocks));
        return o;
    }

    public static BizBlock fromJson(JSONObject o) {
        BizBlock biz = new BizBlock();
        biz.seq = o.optInt("seq", 0);
        biz.dept = o.optString("dept", "");
        biz.manager = o.optString("manager", "");
        biz.detailBiz = o.optString("detailBiz", "");
        biz.indivBiz = o.optString("indivBiz", "");
        biz.mokCd = o.optString("mokCd", "");
        biz.overviewLines = toStrList(o.optJSONArray("overviewLines"));
        biz.contentLines = toStrList(o.optJSONArray("contentLines"));
        biz.progressLines = toStrList(o.optJSONArray("progressLines"));
        biz.reasonLines = toStrList(o.optJSONArray("reasonLines"));
        biz.calcLines = toStrList(o.optJSONArray("calcLines"));
        biz.planLines = toStrList(o.optJSONArray("planLines"));
        biz.procedureLines = toStrList(o.optJSONArray("procedureLines"));
        biz.yearlyBudgetLines = toStrList(o.optJSONArray("yearlyBudgetLines"));
        biz.extractedTables = tablesFromJson(o.optJSONArray("extractedTables"));
        // blocks는 요약 표시 시 buildBlocksFromLines()로 생성 (전체 선생성 비용 회피)
        return biz;
    }

    public static JSONArray toJsonArray(List<BizBlock> list) {
        JSONArray arr = new JSONArray();
        if (list != null) {
            for (BizBlock b : list) {
                arr.add(toJson(b));
            }
        }
        return arr;
    }

    public static List<BizBlock> fromJsonArray(JSONArray arr) {
        List<BizBlock> list = new ArrayList<BizBlock>();
        if (arr == null) {
            return list;
        }
        for (int i = 0; i < arr.size(); i++) {
            list.add(fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    public static String normalizeName(String s) {
        if (s == null) {
            return "";
        }
        String t = s;
        t = t.replaceAll("\\(중기주요사업\\)", "");
        t = t.replaceAll("\\(성인지예산[^)]*\\)", "");
        t = t.replaceAll("\\(국가직접지원\\)", "");
        t = t.replaceAll("\\([^)]*특별회계\\)", "");
        t = t.replaceAll("\\([^)]*\\d{3}[-–,]?\\d{0,2}[^)]*\\)", "");
        t = t.replaceAll("\\[[^\\]]*\\]", "");
        t = t.replaceAll("[·ㆍ․\\s~\\-–—_()／/]", "");
        return t.toLowerCase();
    }

    /**
     * 문자 bigram Dice 계수 기반 유사도.
     * normalizeName 후 호출하므로 공백·대소문자·구분자는 이미 제거된 상태.
     */
    public static double similarity(String a, String b) {
        if (a == null || b == null || a.length() == 0 || b.length() == 0) {
            return 0;
        }
        if (a.equals(b)) {
            return 1;
        }
        if (a.contains(b) || b.contains(a)) {
            int mn = Math.min(a.length(), b.length());
            int mx = Math.max(a.length(), b.length());
            return Math.min(0.95, 0.6 + (mn * 0.35 / mx));
        }
        List<String> ba = bigrams(a);
        List<String> bb = bigrams(b);
        if (ba.isEmpty() || bb.isEmpty()) {
            // 한 글자만 남은 경우: 단일 문자 비교
            if (a.length() == 1 && b.length() == 1) {
                return a.equals(b) ? 1.0 : 0.0;
            }
            return 0;
        }
        java.util.Map<String, Integer> freqB = new java.util.HashMap<String, Integer>();
        for (String g : bb) {
            Integer c = freqB.get(g);
            freqB.put(g, c == null ? 1 : c.intValue() + 1);
        }
        int hit = 0;
        for (String g : ba) {
            Integer c = freqB.get(g);
            if (c != null && c.intValue() > 0) {
                hit++;
                freqB.put(g, c.intValue() - 1);
            }
        }
        return (2.0 * hit) / (ba.size() + bb.size());
    }

    private static List<String> bigrams(String s) {
        List<String> out = new ArrayList<String>();
        if (s == null || s.length() < 2) {
            return out;
        }
        for (int i = 0; i < s.length() - 1; i++) {
            out.add(s.substring(i, i + 2));
        }
        return out;
    }

    private static JSONArray blocksToJson(List<HwpxBizDescParser.ContentBlock> blocks) {
        JSONArray arr = new JSONArray();
        if (blocks == null) {
            return arr;
        }
        for (HwpxBizDescParser.ContentBlock b : blocks) {
            JSONObject o = new JSONObject();
            o.put("type", b.type == null ? "para" : b.type);
            o.put("text", b.text == null ? "" : b.text);
            o.put("kind", b.kind == null ? "" : b.kind);
            if (b.rows != null) {
                o.put("rows", JSONArray.fromObject(b.rows));
            }
            if (b.cells != null) {
                JSONArray cellsArr = new JSONArray();
                for (List<HwpxBizDescParser.TableCell> row : b.cells) {
                    JSONArray rowArr = new JSONArray();
                    if (row != null) {
                        for (HwpxBizDescParser.TableCell c : row) {
                            JSONObject co = new JSONObject();
                            co.put("text", c == null || c.text == null ? "" : c.text);
                            co.put("colSpan", c == null ? 1 : c.colSpan);
                            co.put("rowSpan", c == null ? 1 : c.rowSpan);
                            rowArr.add(co);
                        }
                    }
                    cellsArr.add(rowArr);
                }
                o.put("cells", cellsArr);
            }
            arr.add(o);
        }
        return arr;
    }

    private static JSONArray tablesToJson(List<List<List<String>>> tables) {
        JSONArray arr = new JSONArray();
        if (tables == null) {
            return arr;
        }
        for (List<List<String>> t : tables) {
            arr.add(JSONArray.fromObject(t));
        }
        return arr;
    }

    @SuppressWarnings("unchecked")
    private static List<List<List<String>>> tablesFromJson(JSONArray arr) {
        List<List<List<String>>> list = new ArrayList<List<List<String>>>();
        if (arr == null) {
            return list;
        }
        for (int i = 0; i < arr.size(); i++) {
            Object tObj = arr.get(i);
            if (!(tObj instanceof JSONArray)) {
                continue;
            }
            JSONArray tableJa = (JSONArray) tObj;
            List<List<String>> table = new ArrayList<List<String>>();
            for (int r = 0; r < tableJa.size(); r++) {
                Object rowObj = tableJa.get(r);
                List<String> row = new ArrayList<String>();
                if (rowObj instanceof JSONArray) {
                    JSONArray cells = (JSONArray) rowObj;
                    for (int c = 0; c < cells.size(); c++) {
                        row.add(String.valueOf(cells.get(c)));
                    }
                } else if (rowObj instanceof List) {
                    for (Object cell : (List) rowObj) {
                        row.add(String.valueOf(cell));
                    }
                }
                table.add(row);
            }
            if (!table.isEmpty()) {
                list.add(table);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static List<HwpxBizDescParser.ContentBlock> blocksFromJson(JSONArray arr) {
        List<HwpxBizDescParser.ContentBlock> list = new ArrayList<HwpxBizDescParser.ContentBlock>();
        if (arr == null) {
            return list;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject o = arr.getJSONObject(i);
            HwpxBizDescParser.ContentBlock b = new HwpxBizDescParser.ContentBlock();
            b.type = o.optString("type", "para");
            b.text = o.optString("text", "");
            b.kind = o.optString("kind", "");
            JSONArray rows = o.optJSONArray("rows");
            if (rows != null) {
                b.rows = new ArrayList<List<String>>();
                for (int r = 0; r < rows.size(); r++) {
                    List<String> row = new ArrayList<String>();
                    Object cellObj = rows.get(r);
                    if (cellObj instanceof JSONArray) {
                        JSONArray cells = (JSONArray) cellObj;
                        for (int c = 0; c < cells.size(); c++) {
                            row.add(String.valueOf(cells.get(c)));
                        }
                    } else if (cellObj instanceof List) {
                        for (Object cell : (List) cellObj) {
                            row.add(String.valueOf(cell));
                        }
                    }
                    b.rows.add(row);
                }
            }
            list.add(b);
        }
        return list;
    }

    private static List<String> toStrList(JSONArray arr) {
        List<String> list = new ArrayList<String>();
        if (arr == null) {
            return list;
        }
        for (int i = 0; i < arr.size(); i++) {
            list.add(String.valueOf(arr.get(i)));
        }
        return list;
    }

    private static String n(String s) {
        return s == null ? "" : s;
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    /** 기존 초안 규칙(호환) */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public List matchAll(List businesses, List candidates) {
        return new ArrayList();
    }
}

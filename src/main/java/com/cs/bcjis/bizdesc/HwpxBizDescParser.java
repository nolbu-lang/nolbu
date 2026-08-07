package com.cs.bcjis.bizdesc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * HWPX(ZIP+XML) 사업설명서 파서. 문서 순서대로 사업 블록·구간을 추출한다.
 */
public class HwpxBizDescParser {

    private static final Set<String> HEADER_KEYS = new HashSet<String>(Arrays.asList(
            "부서명", "담당자", "세부사업", "개별사업", "사업명세서", "쪽",
            "□ 사업개요", "□ 사업내용 및 성과", "□ 투자계획", "□ 사업내용", "주요 투자사업 설명서",
            "주요 경상사업 설명서"
    ));

    private static final Pattern TEXT_NODE = Pattern.compile(
            "<(?:hp:)?t(?:\\s[^>]*)?>([\\s\\S]*?)</(?:hp:)?t>", Pattern.CASE_INSENSITIVE);

    private static final Pattern TBL_NODE = Pattern.compile(
            "<(?:hp:)?tbl(?:\\s[^>]*)?>([\\s\\S]*?)</(?:hp:)?tbl>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TR_NODE = Pattern.compile(
            "<(?:hp:)?tr(?:\\s[^>]*)?>([\\s\\S]*?)</(?:hp:)?tr>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TC_NODE = Pattern.compile(
            "<(?:hp:)?tc(?:\\s[^>]*)?>([\\s\\S]*?)</(?:hp:)?tc>", Pattern.CASE_INSENSITIVE);

    private static final Pattern CELL_SPAN = Pattern.compile(
            "<(?:hp:)?cellSpan\\b([^>]*)/?>",
            Pattern.CASE_INSENSITIVE);

    /** 표 셀(병합 정보 포함) */
    public static class TableCell {
        public String text = "";
        public int colSpan = 1;
        public int rowSpan = 1;

        public TableCell() {
        }

        public TableCell(String text, int colSpan, int rowSpan) {
            this.text = text == null ? "" : text;
            this.colSpan = colSpan < 1 ? 1 : colSpan;
            this.rowSpan = rowSpan < 1 ? 1 : rowSpan;
        }
    }

    /** 화면 렌더용 구조화 블록: heading / para / table / meta */
    public static class ContentBlock {
        public String type; // heading | para | table | meta
        public String text = "";
        public String kind = ""; // meta | procedure | yearly | plan | line
        public List<List<String>> rows; // table only (단순 격자)
        public List<List<TableCell>> cells; // table only (병합 유지)
    }

    public static class BizBlock {
        public int seq;
        public String dept = "";
        public String manager = "";
        public String detailBiz = "";
        public String indivBiz = "";
        public String mokCd = "";
        public List<String> overviewLines = new ArrayList<String>();
        public List<String> contentLines = new ArrayList<String>();
        public List<String> progressLines = new ArrayList<String>();
        public List<String> reasonLines = new ArrayList<String>();
        public List<String> calcLines = new ArrayList<String>();
        /** □ 투자계획 이하(표 원문 보조) */
        public List<String> planLines = new ArrayList<String>();
        /** □ 사전절차 */
        public List<String> procedureLines = new ArrayList<String>();
        /** 【연도별 예산 및 집행현황】 */
        public List<String> yearlyBudgetLines = new ArrayList<String>();
        public List<ContentBlock> blocks = new ArrayList<ContentBlock>();
        public List<List<List<String>>> extractedTables = new ArrayList<List<List<String>>>();
        /** 병합정보 포함 표 (투자계획 등) */
        public List<List<List<TableCell>>> extractedCellTables = new ArrayList<List<List<TableCell>>>();
        /** 문서 전체 투자계획 표 후보(매칭 실패 시 재검색용) */
        public List<List<List<TableCell>>> planTableCandidates = new ArrayList<List<List<TableCell>>>();
        /** 문서 전체 연도별 예산 표 후보(잘못된 분배 시 재검색용) */
        public List<List<List<TableCell>>> yearlyTableCandidates = new ArrayList<List<List<TableCell>>>();
        /** 이미 편성·산출 등에 소비한 기타 표(말미 중복 출력 방지) */
        public Set<Integer> consumedOtherTableIdx = new HashSet<Integer>();

        public String bizNm() {
            if (indivBiz != null && indivBiz.length() > 0) {
                return indivBiz;
            }
            return detailBiz == null ? "" : detailBiz;
        }

        /**
         * HWP 사업설명서 포맷에 맞춘 구조화 블록.
         * - 헤더: 부서명·담당자 한 줄 (세부/개별사업 미표시)
         * - □ 사전절차 / 【연도별 예산】 / □ 투자계획 → 표
         * - 그 외 □ 섹션 → 표 없이 문단
         */
        public List<ContentBlock> buildBlocksFromLines() {
            splitLegacySections();
            List<ContentBlock> out = new ArrayList<ContentBlock>();

            // 부서명 | 값 | 담당자 | 값  (한 줄)
            ContentBlock ht = new ContentBlock();
            ht.type = "meta";
            ht.kind = "meta";
            ht.rows = new ArrayList<List<String>>();
            ht.rows.add(Arrays.asList("부서명", n(dept), "담당자", n(manager)));
            out.add(ht);

            addTextSection(out, "□ 사업개요", overviewLines);
            addTextSection(out, "□ 사업내용 및 성과", contentLines);

            // 투자계획 표 뒤에 섞인 추진실적/추진계획을 progress로 복원
            recoverProgressFromPlanLines();

            // □ 투자계획 — 병합셀 표. 후보 풀에서 재검색. ※/주) 제외
            if (planLines != null && !planLines.isEmpty()) {
                List<List<TableCell>> planCells = findBestPlanCellTable();
                List<List<TableCell>> rebuilt = rebuildPlanCellsFromLines(planLines);
                // 셀 표가 약하거나 라인 복원이 더 잘 맞으면 라인 복원 우선(HWP 빈칸 생략 대응)
                if (planCells != null) {
                    int cellSc = scorePlanTable(planCells);
                    int lineSc = rebuilt != null ? scorePlanTable(rebuilt) : 0;
                    if (cellSc < 10 || (rebuilt != null && lineSc > cellSc)) {
                        planCells = rebuilt;
                    }
                } else {
                    planCells = rebuilt;
                }
                ContentBlock h = new ContentBlock();
                h.type = "heading";
                h.text = "□ 투자계획";
                out.add(h);
                for (String line : safe(planLines)) {
                    String t = line.trim();
                    if (t.startsWith("(단위")) {
                        ContentBlock unit = new ContentBlock();
                        unit.type = "para";
                        unit.text = t;
                        out.add(unit);
                        break;
                    }
                }
                if (planCells != null) {
                    List<List<TableCell>> normalized = normalizePlanCells(planCells);
                    if (!normalized.isEmpty()) {
                        ContentBlock tb = new ContentBlock();
                        tb.type = "table";
                        tb.kind = "plan";
                        tb.cells = normalized;
                        tb.rows = flattenCells(normalized);
                        out.add(tb);
                    }
                }
            }

            addProgressSection(out);

            // □ 편성내용·산출근거 — HWP 줄바꿈(▹ 등) 유지
            addCalcReasonSection(out);

            // 지정 표: 반드시 표로 출력 (평문 변환 금지)
            addDesignatedProcedureTable(out);
            addDesignatedYearlyTable(out);

            // 지정 표 이외(미소비): 칸=공백 한 줄. 병합 표는 표시하지 않음
            if (extractedCellTables != null) {
                for (int ti = 0; ti < extractedCellTables.size(); ti++) {
                    if (consumedOtherTableIdx != null && consumedOtherTableIdx.contains(Integer.valueOf(ti))) {
                        continue;
                    }
                    List<List<TableCell>> t = extractedCellTables.get(ti);
                    if (t == null || t.isEmpty()) {
                        continue;
                    }
                    String k = classifyCellTable(t);
                    if ("plan".equals(k) || "procedure".equals(k) || "yearly".equals(k) || "header".equals(k)) {
                        // 지정 표는 위에서 표로 출력. 여기선 절대 평문화하지 않음
                        markConsumed(ti);
                        continue;
                    }
                    if (hasMergedCells(t)) {
                        markConsumed(ti);
                        continue;
                    }
                    addTableAsLines(out, t);
                    markConsumed(ti);
                }
            } else if (extractedTables != null) {
                for (List<List<String>> t : extractedTables) {
                    if (t == null || t.isEmpty()) {
                        continue;
                    }
                    String k = classifyTable(t);
                    if ("plan".equals(k) || "procedure".equals(k) || "yearly".equals(k) || "header".equals(k)) {
                        continue;
                    }
                    addStringTableAsLines(out, t);
                }
            }
            return out;
        }

        private void markConsumed(int ti) {
            if (consumedOtherTableIdx == null) {
                consumedOtherTableIdx = new HashSet<Integer>();
            }
            consumedOtherTableIdx.add(Integer.valueOf(ti));
        }

        /** □ 사전절차 — 표로만 표시 */
        private void addDesignatedProcedureTable(List<ContentBlock> out) {
            List<List<TableCell>> cells = findCellTableByKind("procedure");
            List<List<String>> rows = null;
            if (cells != null) {
                rows = flattenCells(compactCellTable(cells));
            }
            if (rows == null || rows.isEmpty()) {
                rows = findTableByKind("procedure");
            }
            if (rows == null || rows.isEmpty()) {
                rows = buildProcedureTable(procedureLines);
            }
            if ((rows == null || rows.isEmpty())
                    && (procedureLines == null || procedureLines.isEmpty())) {
                return;
            }
            if (rows == null || rows.isEmpty()) {
                return;
            }
            ContentBlock h = new ContentBlock();
            h.type = "heading";
            h.text = "□ 사전절차";
            out.add(h);
            if (cells != null && !cells.isEmpty()) {
                ContentBlock tb = new ContentBlock();
                tb.type = "table";
                tb.kind = "procedure";
                tb.cells = compactCellTable(cells);
                tb.rows = rows;
                out.add(tb);
            } else {
                out.add(tableBlock(rows, "procedure"));
            }
            markConsumedKind("procedure");
        }

        /** 【연도별 예산 및 집행현황】 — 표로만 표시 (해당 사업 라인·표 내용으로 검증) */
        private void addDesignatedYearlyTable(List<ContentBlock> out) {
            List<List<String>> fromLines = buildYearlyBudgetTable(yearlyBudgetLines);
            List<List<TableCell>> cells = findBestYearlyCellTable();
            int cellScore = cells != null ? scoreYearlyTable(cells) : 0;
            int lineScore = scoreYearlyRows(fromLines);

            // 셀 표는 금액 토큰이 이 사업 라인과 겹칠 때만 사용(연도 헤더만 같으면 다른 사업 표).
            // 그 외에는 해당 사업 yearlyBudgetLines로 재구성.
            int cellAmtHits = countYearlyAmountHits(cells);
            int lineAmtHits = countYearlyAmountTokens(yearlyBudgetLines);
            List<List<TableCell>> useCells = null;
            List<List<String>> useRows = null;
            boolean cellTrusted = cells != null && cellAmtHits > 0 && cellScore >= 8
                    && cellScore >= lineScore;
            if (cellTrusted) {
                useCells = compactCellTable(cells);
                useRows = flattenCells(useCells);
            } else if (fromLines != null && !fromLines.isEmpty()) {
                useRows = fromLines;
            } else if (cells != null && cellAmtHits == 0 && lineAmtHits == 0 && cellScore > 0) {
                // 신규 등 양쪽 모두 금액 없음 → 빈 표 템플릿
                useCells = compactCellTable(cells);
                useRows = flattenCells(useCells);
            } else {
                List<List<String>> rows = findTableByKind("yearly");
                if (rows != null && !rows.isEmpty()) {
                    useRows = rows;
                }
            }

            boolean hasLines = yearlyBudgetLines != null && !yearlyBudgetLines.isEmpty();
            if ((useRows == null || useRows.isEmpty()) && !hasLines) {
                return;
            }
            if (useRows == null || useRows.isEmpty()) {
                return;
            }
            ContentBlock h = new ContentBlock();
            h.type = "heading";
            h.text = "【연도별 예산 및 집행현황】";
            out.add(h);
            for (String line : safe(yearlyBudgetLines)) {
                String t = line.trim();
                if (t.startsWith("(단위")) {
                    ContentBlock p = new ContentBlock();
                    p.type = "para";
                    p.text = t;
                    out.add(p);
                    break;
                }
            }
            if (useCells != null && !useCells.isEmpty()) {
                ContentBlock tb = new ContentBlock();
                tb.type = "table";
                tb.kind = "yearly";
                tb.cells = useCells;
                tb.rows = useRows;
                out.add(tb);
            } else {
                out.add(tableBlock(useRows, "yearly"));
            }
            markConsumedKind("yearly");
        }

        private void markConsumedKind(String kind) {
            if (extractedCellTables == null) {
                return;
            }
            for (int ti = 0; ti < extractedCellTables.size(); ti++) {
                if (kind.equals(classifyCellTable(extractedCellTables.get(ti)))) {
                    markConsumed(ti);
                }
            }
        }

        private static List<List<TableCell>> compactCellTable(List<List<TableCell>> src) {
            List<List<TableCell>> out = new ArrayList<List<TableCell>>();
            if (src == null) {
                return out;
            }
            for (List<TableCell> row : src) {
                if (row == null) {
                    continue;
                }
                List<TableCell> nr = new ArrayList<TableCell>();
                for (TableCell c : row) {
                    if (c == null) {
                        continue;
                    }
                    nr.add(new TableCell(compactText(c.text), c.colSpan, c.rowSpan));
                }
                if (!nr.isEmpty()) {
                    out.add(nr);
                }
            }
            return out;
        }

        /** 구 JSON에 섞여 저장된 사전절차·연도별 라인을 분리 */
        public void splitLegacySections() {
            if (procedureLines == null) {
                procedureLines = new ArrayList<String>();
            }
            if (yearlyBudgetLines == null) {
                yearlyBudgetLines = new ArrayList<String>();
            }
            if (planLines == null) {
                planLines = new ArrayList<String>();
            }
            @SuppressWarnings("unchecked")
            List<String>[] bags = new List[] { overviewLines, contentLines, progressLines, reasonLines, calcLines };
            for (List<String> bag : bags) {
                if (bag == null || bag.isEmpty()) {
                    continue;
                }
                List<String> keep = new ArrayList<String>();
                String mode = "keep";
                for (String line : bag) {
                    if (line == null) {
                        continue;
                    }
                    String t = line.trim();
                    if (t.startsWith("□ 사전절차") || "사전절차".equals(t)) {
                        mode = "procedure";
                        continue;
                    }
                    if (t.contains("연도별 예산") || t.startsWith("【연도별")) {
                        mode = "yearly";
                        continue;
                    }
                    if (t.startsWith("□ 투자계획")) {
                        mode = "plan";
                        continue;
                    }
                    if (t.startsWith("□ ") && !"procedure".equals(mode) && !"yearly".equals(mode) && !"plan".equals(mode)) {
                        mode = "keep";
                        // □ 제목 자체는 섹션 헤더로 쓰이므로 본문 keep에 넣지 않음
                        if (t.startsWith("□ 사업개요") || t.startsWith("□ 사업내용") || t.startsWith("□ 추진")
                                || t.startsWith("□ 편성") || t.contains("산출근거") || t.contains("향후계획")) {
                            continue;
                        }
                    }
                    if ("procedure".equals(mode)) {
                        if (t.startsWith("□ ") || t.startsWith("【")) {
                            mode = t.contains("연도별") ? "yearly" : "keep";
                            if ("yearly".equals(mode)) {
                                continue;
                            }
                            if (mode.equals("keep")) {
                                keep.add(line);
                            }
                            continue;
                        }
                        procedureLines.add(t);
                    } else if ("yearly".equals(mode)) {
                        if (t.startsWith("□ ") && !t.contains("연도별")) {
                            mode = "keep";
                            keep.add(line);
                            continue;
                        }
                        if (isBizDescSectionBoundary(t)) {
                            mode = "keep";
                            continue;
                        }
                        yearlyBudgetLines.add(t);
                    } else if ("plan".equals(mode)) {
                        if (isProgressSectionHeading(t)) {
                            mode = "keep";
                            // 투자계획 뒤에 붙은 추진실적/계획은 progress로
                            if (progressLines == null) {
                                progressLines = new ArrayList<String>();
                            }
                            progressLines.add(t);
                            // 이후 줄도 progress로 옮기기 위해 임시 모드
                            mode = "progressRecover";
                            continue;
                        }
                        if (t.startsWith("□ ") && !t.startsWith("□ 투자")) {
                            mode = "keep";
                            keep.add(line);
                            continue;
                        }
                        planLines.add(t);
                    } else if ("progressRecover".equals(mode)) {
                        if (isBudgetOrReasonHeading(t) || isProcedureLikeHeading(t) || t.startsWith("【")
                                || (t.startsWith("□ ") && !isProgressSectionHeading(t))) {
                            mode = "keep";
                            keep.add(line);
                            continue;
                        }
                        if (isProgressSectionHeading(t)) {
                            progressLines.add(t);
                            continue;
                        }
                        progressLines.add(t);
                    } else {
                        keep.add(line);
                    }
                }
                bag.clear();
                bag.addAll(keep);
            }
        }

        private String progressSectionTitle() {
            for (String line : safe(progressLines)) {
                String t = line.trim();
                if (isProgressSectionHeading(t)) {
                    return t;
                }
            }
            if (planLines != null && !planLines.isEmpty()) {
                return "□ 추진실적/추진계획";
            }
            return "□ 추진실적/추진경과";
        }

        /** 투자계획 라인에 섞인 □ 추진실적·추진계획을 progress로 분리 */
        private void recoverProgressFromPlanLines() {
            if (planLines == null || planLines.isEmpty()) {
                return;
            }
            List<String> newPlan = new ArrayList<String>();
            List<String> recovered = new ArrayList<String>();
            boolean inProgress = false;
            for (String line : planLines) {
                if (line == null) {
                    continue;
                }
                String t = line.trim();
                if (isProgressSectionHeading(t)) {
                    inProgress = true;
                    recovered.add(t);
                    continue;
                }
                if (inProgress) {
                    if (isBudgetOrReasonHeading(t) || isProcedureLikeHeading(t)
                            || t.startsWith("【") || (t.startsWith("□ ") && t.contains("투자"))) {
                        inProgress = false;
                        if (!isBudgetOrReasonHeading(t) && !isProcedureLikeHeading(t) && !t.startsWith("【")) {
                            newPlan.add(line);
                        }
                        continue;
                    }
                    if (t.startsWith("□ ") && !isProgressSectionHeading(t)) {
                        inProgress = false;
                        newPlan.add(line);
                        continue;
                    }
                    recovered.add(t);
                } else {
                    newPlan.add(line);
                }
            }
            planLines = newPlan;
            if (progressLines == null) {
                progressLines = new ArrayList<String>();
            }
            // 잘못 progress에 들어간 예산안 헤더 제거
            List<String> cleaned = new ArrayList<String>();
            for (String p : progressLines) {
                if (p == null) {
                    continue;
                }
                String t = p.trim();
                if (isBudgetOrReasonHeading(t) || isProcedureLikeHeading(t)) {
                    continue;
                }
                cleaned.add(p);
            }
            progressLines = cleaned;
            if (!recovered.isEmpty()) {
                // 복원 내용을 앞에 두고, 기존(본문만) 뒤에 유지
                List<String> merged = new ArrayList<String>();
                merged.addAll(recovered);
                for (String p : progressLines) {
                    if (!merged.contains(p)) {
                        merged.add(p);
                    }
                }
                progressLines = merged;
            }
        }

        private void addProgressSection(List<ContentBlock> out) {
            List<String> lines = new ArrayList<String>();
            for (String line : safe(progressLines)) {
                if (line == null) {
                    continue;
                }
                String t = line.trim();
                if (t.length() == 0 || isBudgetOrReasonHeading(t) || isProcedureLikeHeading(t)) {
                    continue;
                }
                lines.add(t);
            }
            if (lines.isEmpty()) {
                return;
            }
            String title = progressSectionTitle();
            ContentBlock h = new ContentBlock();
            h.type = "heading";
            h.text = title;
            out.add(h);
            for (String t : lines) {
                if (isProgressSectionHeading(t)) {
                    if (t.equals(title)) {
                        continue;
                    }
                    ContentBlock sh = new ContentBlock();
                    sh.type = "heading";
                    sh.text = t;
                    out.add(sh);
                    continue;
                }
                ContentBlock pb = new ContentBlock();
                pb.type = "para";
                pb.text = t;
                out.add(pb);
            }
        }

        /** □/ㅁ 추진실적·추진계획·추진경과 등 섹션 머릿글 */
        static boolean isProgressSectionHeading(String t) {
            if (t == null || t.length() == 0) {
                return false;
            }
            String s = t.trim();
            char c0 = s.charAt(0);
            boolean box = c0 == '□' || c0 == 'ㅁ' || c0 == '■' || c0 == '▣' || c0 == '【';
            if (!box) {
                return false;
            }
            String n = s.replace(" ", "");
            return n.contains("추진실적") || n.contains("추진계획") || n.contains("추진경과")
                    || n.contains("향후계획") || n.contains("향후추진") || n.contains("그동안추진");
        }

        static boolean isBudgetOrReasonHeading(String t) {
            if (t == null || t.length() == 0) {
                return false;
            }
            String s = t.trim();
            String n = s.replace(" ", "");
            if (n.contains("편성사유") || n.contains("편성내용") || n.startsWith("□편성")) {
                return true;
            }
            // □ ’26년 예산안 등
            if ((s.startsWith("□") || s.startsWith("【")) && n.contains("예산안")) {
                return true;
            }
            return false;
        }

        static boolean isProcedureLikeHeading(String t) {
            if (t == null) {
                return false;
            }
            String s = t.trim();
            return s.startsWith("□ 사전절차") || "사전절차".equals(s.replace(" ", ""));
        }

        private List<List<String>> findTableByKind(String kind) {
            if (extractedTables == null) {
                return null;
            }
            for (List<List<String>> t : extractedTables) {
                if (kind.equals(classifyTable(t))) {
                    return t;
                }
            }
            return null;
        }

        private List<List<TableCell>> findCellTableByKind(String kind) {
            if (extractedCellTables == null) {
                return null;
            }
            for (List<List<TableCell>> t : extractedCellTables) {
                if (kind.equals(classifyCellTable(t))) {
                    return t;
                }
            }
            return null;
        }

        /** 사업의 planLines·금액과 가장 잘 맞는 투자계획 표 선택 */
        private List<List<TableCell>> findBestPlanCellTable() {
            List<List<List<TableCell>>> pool = new ArrayList<List<List<TableCell>>>();
            if (extractedCellTables != null) {
                for (List<List<TableCell>> t : extractedCellTables) {
                    if ("plan".equals(classifyCellTable(t))) {
                        pool.add(t);
                    }
                }
            }
            if (planTableCandidates != null) {
                for (List<List<TableCell>> t : planTableCandidates) {
                    if (t != null && !poolContains(pool, t)) {
                        pool.add(t);
                    }
                }
            }
            if (pool.isEmpty()) {
                return null;
            }
            List<List<TableCell>> best = null;
            int bestScore = -1;
            for (List<List<TableCell>> t : pool) {
                int sc = scorePlanTable(t);
                if (sc > bestScore) {
                    bestScore = sc;
                    best = t;
                }
            }
            // 점수 동점이거나 0이어도 후보는 반드시 반환 (빈 투자계획 방지)
            if (best != null) {
                return best;
            }
            return pool.get(0);
        }

        private static boolean poolContains(List<List<List<TableCell>>> pool, List<List<TableCell>> t) {
            for (List<List<TableCell>> p : pool) {
                if (p == t) {
                    return true;
                }
            }
            return false;
        }

        private int scorePlanTable(List<List<TableCell>> table) {
            String flat = flatCellTable(table);
            if (flat.length() == 0) {
                return 0;
            }
            int score = 0;
            if (flat.contains("총사업비") || flat.contains("기 투 자") || flat.contains("금 후 투 자")) {
                score += 2;
            }
            String planText = joinLines(safe(planLines));
            Matcher nm = Pattern.compile("\\d{1,3}(?:,\\d{3})+(?:\\.\\d+)?|\\d+(?:\\.\\d+)?").matcher(planText);
            while (nm.find()) {
                String num = nm.group();
                if (num.length() >= 2 && flat.contains(num)) {
                    score += 4;
                }
            }
            for (String line : safe(planLines)) {
                String t = compactText(line);
                if (t.length() < 5 || t.startsWith("주)") || t.startsWith("※") || t.startsWith("(단위")) {
                    continue;
                }
                if (flat.contains(t)) {
                    score += 6;
                } else {
                    // 줄바꿈이 합쳐진 셀 대비: 공백 제거 후 부분 일치
                    String flatNs = flat.replace(" ", "");
                    String tNs = t.replace(" ", "");
                    if (tNs.length() >= 6 && flatNs.contains(tNs)) {
                        score += 5;
                    }
                }
            }
            String biz = compactText(indivBiz);
            if (biz.length() >= 4) {
                String core = biz.replaceAll("\\([^)]*\\)", "").replace(" ", "");
                if (core.length() >= 4 && flat.replace(" ", "").contains(core.substring(0, Math.min(8, core.length())))) {
                    score += 3;
                }
            }
            return score;
        }

        /** 연도별 예산 표: 해당 사업 yearlyBudgetLines의 연도·금액과 얼마나 맞는지 */
        private int scoreYearlyTable(List<List<TableCell>> table) {
            return scoreYearlyFlat(flatCellTable(table));
        }

        private int scoreYearlyRows(List<List<String>> rows) {
            if (rows == null || rows.isEmpty()) {
                return 0;
            }
            StringBuilder sb = new StringBuilder();
            for (List<String> row : rows) {
                if (row == null) {
                    continue;
                }
                for (String c : row) {
                    if (c != null && c.length() > 0) {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(c);
                    }
                }
            }
            return scoreYearlyFlat(sb.toString());
        }

        private int scoreYearlyFlat(String flat) {
            if (flat == null || flat.length() == 0) {
                return 0;
            }
            int score = 0;
            if (flat.contains("예산액") && flat.contains("집행액")) {
                score += 2;
            }
            if (flat.contains("구분") || flat.contains("구 분")) {
                score += 1;
            }
            String lineText = joinLines(safe(yearlyBudgetLines));
            if (lineText.length() == 0) {
                return score;
            }
            // 연도 헤더는 사업 간 공통 → 가중치 낮게 (금액 일치가 본질)
            Matcher ym = Pattern.compile("[’'](\\d{2})년|20(\\d{2})").matcher(lineText);
            Set<String> years = new HashSet<String>();
            while (ym.find()) {
                String y = ym.group(1) != null ? ym.group(1) : ym.group(2);
                if (y != null && y.length() > 0) {
                    years.add(y);
                }
            }
            int yearHits = 0;
            for (String y : years) {
                if (flat.contains("’" + y + "년") || flat.contains("'" + y + "년")
                        || flat.contains(y + "년") || flat.contains("20" + y)) {
                    yearHits++;
                }
            }
            score += Math.min(2, yearHits);
            // 금액 토큰 일치
            Matcher nm = Pattern.compile("\\d{1,3}(?:,\\d{3})+(?:\\.\\d+)?|\\d{4,}(?:\\.\\d+)?").matcher(lineText);
            Set<String> seen = new HashSet<String>();
            int amtHits = 0;
            while (nm.find()) {
                String num = nm.group();
                if (seen.contains(num)) {
                    continue;
                }
                seen.add(num);
                if (flat.contains(num)) {
                    score += 5;
                    amtHits++;
                }
            }
            // 금액이 있는 사업인데 표에 금액 겹침이 없으면 연도만 맞아도 채택하지 않음
            if (seen.size() >= 2 && amtHits == 0) {
                return Math.min(score, 3);
            }
            return score;
        }

        private int countYearlyAmountHits(List<List<TableCell>> table) {
            if (table == null) {
                return 0;
            }
            return countAmountOverlap(flatCellTable(table), yearlyBudgetLines);
        }

        private static int countYearlyAmountTokens(List<String> lines) {
            int n = 0;
            for (String line : safe(lines)) {
                if (isYearlyAmountToken(line)) {
                    n++;
                }
            }
            return n;
        }

        private static int countAmountOverlap(String flat, List<String> lines) {
            if (flat == null || flat.length() == 0) {
                return 0;
            }
            int hits = 0;
            Set<String> seen = new HashSet<String>();
            Matcher nm = Pattern.compile("\\d{1,3}(?:,\\d{3})+(?:\\.\\d+)?|\\d{4,}(?:\\.\\d+)?").matcher(joinLines(safe(lines)));
            while (nm.find()) {
                String num = nm.group();
                if (seen.contains(num)) {
                    continue;
                }
                seen.add(num);
                if (flat.contains(num)) {
                    hits++;
                }
            }
            return hits;
        }

        /** 사업의 yearlyBudgetLines와 가장 잘 맞는 연도별 표 선택 */
        private List<List<TableCell>> findBestYearlyCellTable() {
            List<List<List<TableCell>>> pool = new ArrayList<List<List<TableCell>>>();
            if (extractedCellTables != null) {
                for (List<List<TableCell>> t : extractedCellTables) {
                    if ("yearly".equals(classifyCellTable(t))) {
                        pool.add(t);
                    }
                }
            }
            if (yearlyTableCandidates != null) {
                for (List<List<TableCell>> t : yearlyTableCandidates) {
                    if (t != null && !poolContains(pool, t)) {
                        pool.add(t);
                    }
                }
            }
            if (pool.isEmpty()) {
                return null;
            }
            List<List<TableCell>> best = null;
            int bestScore = -1;
            for (List<List<TableCell>> t : pool) {
                int sc = scoreYearlyTable(t);
                if (sc > bestScore) {
                    bestScore = sc;
                    best = t;
                }
            }
            return best;
        }

        public int scoreYearlyTablePublic(List<List<TableCell>> table) {
            return scoreYearlyTable(table);
        }

        private static String flatCellTable(List<List<TableCell>> table) {
            StringBuilder sb = new StringBuilder();
            if (table == null) {
                return "";
            }
            for (List<TableCell> row : table) {
                if (row == null) {
                    continue;
                }
                for (TableCell c : row) {
                    if (c != null && c.text != null && c.text.length() > 0) {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(c.text);
                    }
                }
            }
            return sb.toString();
        }

        /** 투자계획 표: 제목행·각주 제거, 셀 내부 줄바꿈→공백 */
        private static List<List<TableCell>> normalizePlanCells(List<List<TableCell>> src) {
            List<List<TableCell>> out = new ArrayList<List<TableCell>>();
            if (src == null) {
                return out;
            }
            for (List<TableCell> row : src) {
                if (row == null || row.isEmpty()) {
                    continue;
                }
                List<TableCell> compactRow = new ArrayList<TableCell>();
                for (TableCell c : row) {
                    if (c == null) {
                        continue;
                    }
                    // 사업량 등 셀 내 줄바꿈 유지
                    compactRow.add(new TableCell(compactTextKeepLines(c.text), c.colSpan, c.rowSpan));
                }
                String joined = joinCellTexts(compactRow);
                if (joined.contains("□ 투자계획") || joined.startsWith("투자계획")) {
                    continue;
                }
                if (isPlanFootnoteRow(compactRow, joined)) {
                    continue;
                }
                out.add(compactRow);
            }
            return out;
        }

        private static boolean isPlanFootnoteRow(List<TableCell> row, String joined) {
            if (joined.startsWith("※") || joined.startsWith("주)") || joined.startsWith("주）") || joined.startsWith("注")) {
                return true;
            }
            if (joined.contains("국비 = 국고보조금") || joined.contains("채무부담행위액으로 기반영")
                    || joined.contains("기타 = 국가직접지원")) {
                return true;
            }
            // 표 하단 전체병합 주석
            if (row.size() == 1 && row.get(0).colSpan >= 5 && (joined.startsWith("주") || joined.contains("국비 ="))) {
                return true;
            }
            return false;
        }

        private static String compactText(String s) {
            if (s == null) {
                return "";
            }
            return s.replace('\u00a0', ' ').replaceAll("[\\r\\n\\t]+", " ").replaceAll(" +", " ").trim();
        }

        /** 공백 정리하되 의도적인 줄바꿈은 유지 */
        private static String compactTextKeepLines(String s) {
            if (s == null) {
                return "";
            }
            String[] parts = s.replace('\u00a0', ' ').split("[\\r\\n]+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                String t = parts[i].replaceAll("[\\t]+", " ").replaceAll(" +", " ").trim();
                if (t.length() == 0) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(t);
            }
            return sb.toString();
        }

        private static List<String> filterPlanNoteLines(List<String> lines) {
            List<String> out = new ArrayList<String>();
            for (String line : safe(lines)) {
                String t = compactText(line);
                if (t.length() == 0 || t.startsWith("※") || t.startsWith("주)") || t.startsWith("주）")
                        || t.startsWith("(단위") || t.startsWith("□")) {
                    continue;
                }
                out.add(t);
            }
            return out;
        }

        /** 지정 외 표: 칸은 공백 1칸으로 이어 한 줄. 병합(셀) 표는 표시하지 않음 */
        private static void addTableAsLines(List<ContentBlock> out, List<List<TableCell>> table) {
            if (hasMergedCells(table)) {
                return;
            }
            String line = flattenTableToLine(table);
            if (line.length() == 0) {
                return;
            }
            ContentBlock pb = new ContentBlock();
            pb.type = "para";
            pb.kind = "line";
            pb.text = line;
            out.add(pb);
        }

        private static void addStringTableAsLines(List<ContentBlock> out, List<List<String>> table) {
            StringBuilder all = new StringBuilder();
            if (table == null) {
                return;
            }
            for (List<String> row : table) {
                if (row == null) {
                    continue;
                }
                for (String c : row) {
                    String t = compactText(c);
                    if (t.length() == 0 || t.startsWith("※") || t.startsWith("주)") || t.contains("□ 현장사진")) {
                        continue;
                    }
                    if (all.length() > 0) {
                        all.append(' ');
                    }
                    all.append(t);
                }
            }
            String line = all.toString().trim();
            if (line.length() == 0) {
                return;
            }
            ContentBlock pb = new ContentBlock();
            pb.type = "para";
            pb.kind = "line";
            pb.text = line;
            out.add(pb);
        }

        /** colSpan/rowSpan > 1 이면 병합 표로 간주 */
        public static boolean hasMergedCells(List<List<TableCell>> table) {
            if (table == null) {
                return false;
            }
            for (List<TableCell> row : table) {
                if (row == null) {
                    continue;
                }
                for (TableCell c : row) {
                    if (c == null) {
                        continue;
                    }
                    if (c.colSpan > 1 || c.rowSpan > 1) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static String joinCellTexts(List<TableCell> row) {
            StringBuilder sb = new StringBuilder();
            if (row == null) {
                return "";
            }
            for (TableCell c : row) {
                if (c == null || c.text == null) {
                    continue;
                }
                String t = c.text.trim();
                if (t.length() == 0) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(t);
            }
            return sb.toString();
        }

        private static List<List<TableCell>> toCellsFromRows(List<List<String>> rows) {
            List<List<TableCell>> out = new ArrayList<List<TableCell>>();
            if (rows == null) {
                return out;
            }
            for (List<String> r : rows) {
                List<TableCell> row = new ArrayList<TableCell>();
                if (r != null) {
                    for (String c : r) {
                        row.add(new TableCell(c, 1, 1));
                    }
                }
                out.add(row);
            }
            return out;
        }

        private static List<List<String>> flattenCells(List<List<TableCell>> cells) {
            List<List<String>> out = new ArrayList<List<String>>();
            if (cells == null) {
                return out;
            }
            for (List<TableCell> row : cells) {
                List<String> r = new ArrayList<String>();
                if (row != null) {
                    for (TableCell c : row) {
                        r.add(c == null ? "" : c.text);
                    }
                }
                out.add(r);
            }
            return out;
        }

        private static ContentBlock tableBlock(List<List<String>> rows, String kind) {
            ContentBlock tb = new ContentBlock();
            tb.type = "table";
            tb.kind = kind == null ? "" : kind;
            tb.rows = rows;
            return tb;
        }

        /** □ 섹션 — 표 없이 문단만 */
        private static void addTextSection(List<ContentBlock> out, String title, List<String> lines) {
            List<String> body = filterSectionBody(lines);
            if (body.isEmpty()) {
                return;
            }
            ContentBlock h = new ContentBlock();
            h.type = "heading";
            h.text = title;
            out.add(h);
            addParas(out, body);
        }

        /**
         * 편성·산출근거: 지정 외 표는 칸을 이어 표시하되 ▹는 줄바꿈.
         * 텍스트 라인은 HWP와 같이 원래 줄 단위 유지.
         */
        private void addCalcReasonSection(List<ContentBlock> out) {
            String flatFromTable = "";
            if (extractedCellTables != null) {
                int bestIdx = -1;
                int bestScore = -1;
                for (int ti = 0; ti < extractedCellTables.size(); ti++) {
                    List<List<TableCell>> t = extractedCellTables.get(ti);
                    if (t == null || t.isEmpty()) {
                        continue;
                    }
                    // 병합 표는 편성·산출 표시에서도 제외
                    if (hasMergedCells(t)) {
                        continue;
                    }
                    String k = classifyCellTable(t);
                    if ("plan".equals(k) || "procedure".equals(k) || "yearly".equals(k) || "header".equals(k)) {
                        continue;
                    }
                    String all = flatCellTable(t);
                    int sc = 0;
                    if (all.contains("산출근거")) {
                        sc += 5;
                    }
                    if (all.contains("천원") || all.contains("백만원")) {
                        sc += 2;
                    }
                    if (all.contains("▹") || all.contains("▸") || all.contains("×")) {
                        sc += 3;
                    }
                    if (overlapsCalcReason(all)) {
                        sc += 10;
                    }
                    if (sc > bestScore) {
                        bestScore = sc;
                        bestIdx = ti;
                    }
                }
                if (bestIdx >= 0 && bestScore >= 5) {
                    flatFromTable = flattenTableToLine(extractedCellTables.get(bestIdx));
                    if (consumedOtherTableIdx == null) {
                        consumedOtherTableIdx = new HashSet<Integer>();
                    }
                    consumedOtherTableIdx.add(Integer.valueOf(bestIdx));
                }
            }

            List<String> textLines = expandSubBulletLines(filterSectionBody(merge(reasonLines, calcLines)));
            if (flatFromTable.length() == 0 && textLines.isEmpty()) {
                return;
            }
            ContentBlock h = new ContentBlock();
            h.type = "heading";
            h.text = "□ 편성내용·산출근거";
            out.add(h);
            if (flatFromTable.length() > 0) {
                // 표에서 온 경우에도 ▹/▸ 앞에서 줄바꿈해 HWP 형식 유지
                List<String> parts = splitByItemHeadersAndSubBullets(compactText(flatFromTable));
                // 표에 없는 편성사유 머리말만 앞에 보강
                String textJoined = joinCompact(textLines);
                if (textJoined.length() > 0 && textJoined.contains("편성") && !flatFromTable.contains("편성사유")) {
                    String head = textJoined;
                    int cut = textJoined.indexOf("산출근거");
                    if (cut > 0) {
                        head = textJoined.substring(0, cut).trim();
                    }
                    if (head.length() > 0 && head.length() < textJoined.length()) {
                        List<String> headParts = splitByItemHeadersAndSubBullets(head);
                        appendLineParas(out, headParts);
                    }
                }
                appendLineParas(out, parts);
            } else {
                // 원본 줄 단위 유지 (- 산출근거 / ▹ 인건비 …)
                addParas(out, textLines);
            }
        }

        /** ▹/▸ 가 같은 줄에 붙어 있으면 앞줄과 분리 */
        private static List<String> expandSubBulletLines(List<String> lines) {
            List<String> out = new ArrayList<String>();
            for (String line : safe(lines)) {
                if (line == null) {
                    continue;
                }
                String t = line.trim();
                if (t.length() == 0) {
                    continue;
                }
                // 이미 단독 ▹ 줄이면 그대로
                if (t.startsWith("▹") || t.startsWith("▸")) {
                    out.add(t);
                    continue;
                }
                // "- 산출근거 ▹ 인건비…" → 분리
                List<String> parts = splitByItemHeadersAndSubBullets(t);
                if (parts.size() <= 1) {
                    out.add(t);
                } else {
                    out.addAll(parts);
                }
            }
            return out;
        }

        private static void appendLineParas(List<ContentBlock> out, List<String> segments) {
            if (segments == null) {
                return;
            }
            for (String seg : segments) {
                String t = compactText(seg);
                if (t.length() == 0) {
                    continue;
                }
                ContentBlock pb = new ContentBlock();
                pb.type = "para";
                pb.kind = "line";
                pb.text = t;
                out.add(pb);
            }
        }

        /**
         * 항목 머릿글(-, ○, □) 및 하위불릿(▹, ▸) 앞에서 분리.
         */
        private static List<String> splitByItemHeaders(String s) {
            return splitByItemHeadersAndSubBullets(s);
        }

        private static List<String> splitByItemHeadersAndSubBullets(String s) {
            List<String> out = new ArrayList<String>();
            if (s == null || s.length() == 0) {
                return out;
            }
            StringBuilder cur = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                boolean headerStart = false;
                if (ch == '○' || ch == '□' || ch == '▹' || ch == '▸') {
                    headerStart = true;
                } else if (ch == '-' && (i == 0 || Character.isWhitespace(s.charAt(i - 1)))) {
                    int j = i + 1;
                    while (j < s.length() && Character.isWhitespace(s.charAt(j))) {
                        j++;
                    }
                    // 숫자(-100)는 제외, 한글/영문 항목만
                    if (j < s.length()) {
                        char n = s.charAt(j);
                        if (!Character.isDigit(n) && n != '.' && n != ',') {
                            headerStart = true;
                        }
                    }
                }
                if (headerStart && cur.length() > 0) {
                    String prev = cur.toString().trim();
                    if (prev.length() > 0) {
                        out.add(prev);
                    }
                    cur.setLength(0);
                }
                cur.append(ch);
            }
            String last = cur.toString().trim();
            if (last.length() > 0) {
                out.add(last);
            }
            return out;
        }

        private static boolean isItemHeaderLine(String t) {
            if (t == null || t.length() == 0) {
                return false;
            }
            char c = t.charAt(0);
            if (c == '○' || c == '□') {
                return true;
            }
            if (c == '-') {
                if (t.length() == 1) {
                    return true;
                }
                char n = t.charAt(1);
                return Character.isWhitespace(n)
                        || (!Character.isDigit(n) && n != '.' && n != ',');
            }
            return false;
        }

        private boolean overlapsCalcReason(String tableFlat) {
            String src = joinLines(merge(safe(reasonLines), safe(calcLines)));
            if (src.length() == 0) {
                return true; // 텍스트 없으면 표만으로
            }
            Matcher nm = Pattern.compile("\\d{1,3}(?:,\\d{3})+|\\d{4,}").matcher(src);
            int hits = 0;
            while (nm.find()) {
                if (tableFlat.contains(nm.group())) {
                    hits++;
                }
            }
            if (hits >= 1) {
                return true;
            }
            for (String key : new String[] { "산출근거", "플랫폼", "유지보수", "천원" }) {
                if (src.contains(key) && tableFlat.contains(key)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean containsMostNumbers(String a, String b) {
            Matcher nm = Pattern.compile("\\d{1,3}(?:,\\d{3})+|\\d{4,}").matcher(b);
            int total = 0;
            int hit = 0;
            while (nm.find()) {
                total++;
                if (a.contains(nm.group())) {
                    hit++;
                }
            }
            return total > 0 && hit * 2 >= total;
        }

        private static String flattenTableToLine(List<List<TableCell>> table) {
            StringBuilder all = new StringBuilder();
            if (table == null) {
                return "";
            }
            for (List<TableCell> row : table) {
                if (row == null) {
                    continue;
                }
                for (TableCell c : row) {
                    if (c == null) {
                        continue;
                    }
                    String t = compactText(c.text);
                    if (t.length() == 0 || t.startsWith("※") || t.startsWith("주)")) {
                        continue;
                    }
                    if (all.length() > 0) {
                        all.append(' ');
                    }
                    all.append(t);
                }
            }
            return all.toString().trim();
        }

        private static String joinCompact(List<String> lines) {
            // 머릿글 단위로 줄을 나누되, 같은 항목 안에서는 공백으로 이음
            List<String> segs = new ArrayList<String>();
            StringBuilder cur = new StringBuilder();
            for (String line : safe(lines)) {
                String t = compactText(line);
                if (t.length() == 0) {
                    continue;
                }
                if (isItemHeaderLine(t) && cur.length() > 0) {
                    segs.add(cur.toString());
                    cur.setLength(0);
                }
                if (cur.length() > 0) {
                    cur.append(' ');
                }
                cur.append(t);
            }
            if (cur.length() > 0) {
                segs.add(cur.toString());
            }
            // 이후 splitByItemHeaders 에서 한 번 더 정리할 수 있도록 공백 연결
            StringBuilder sb = new StringBuilder();
            for (String seg : segs) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(seg);
            }
            return sb.toString();
        }

        private static void addParas(List<ContentBlock> out, List<String> lines) {
            for (String line : safe(lines)) {
                String t = line.trim();
                if (t.length() == 0) {
                    continue;
                }
                if (t.startsWith("□ ") || t.startsWith("【")) {
                    continue;
                }
                ContentBlock pb = new ContentBlock();
                pb.type = "para";
                pb.text = t;
                out.add(pb);
            }
        }

        /**
         * 투자계획 표를 XML에서 못 찾을 때: 표준 헤더/행라벨로 표 복원.
         * HWP 텍스트는 빈 칸이 생략되므로, 값 개수에 맞게 열 위치를 보정한다.
         */
        private static List<List<TableCell>> rebuildPlanCellsFromLines(List<String> lines) {
            List<String> toks = preprocessPlanTokens(filterPlanNoteLines(lines));
            if (toks.size() < 8) {
                return null;
            }
            // 헤더 연도 라벨 수집
            String y1 = "";
            String y2 = "";
            String y3 = "";
            String y4 = "";
            for (int i = 0; i < toks.size(); i++) {
                String t = toks.get(i).replace(" ", "");
                if ((t.contains("년이전") || t.contains("년이전")) && y1.length() == 0) {
                    y1 = toks.get(i);
                } else if ((t.contains("년까지") || t.contains("포함")) && y2.length() == 0) {
                    y2 = toks.get(i);
                    if (i + 1 < toks.size() && toks.get(i + 1).contains("포함")) {
                        y2 = y2 + toks.get(i + 1);
                    }
                } else if ((t.contains("예산안") || t.contains("추경")) && y3.length() == 0) {
                    y3 = toks.get(i);
                    if (i > 0 && toks.get(i - 1).matches(".*[’']?\\d{2}년.*")) {
                        y3 = toks.get(i - 1) + " " + y3;
                    }
                } else if (t.contains("년이후") && y4.length() == 0) {
                    y4 = toks.get(i);
                }
            }
            if (y1.length() == 0) {
                y1 = "이전";
            }
            if (y2.length() == 0) {
                y2 = "현재까지";
            }
            if (y3.length() == 0) {
                y3 = "예산안";
            }
            if (y4.length() == 0) {
                y4 = "이후";
            }

            List<List<TableCell>> rows = new ArrayList<List<TableCell>>();
            List<TableCell> h1 = new ArrayList<TableCell>();
            h1.add(new TableCell("연차별 구분", 2, 2));
            h1.add(new TableCell("총사업비(계)", 1, 2));
            h1.add(new TableCell("기 투 자", 3, 1));
            h1.add(new TableCell("금 후 투 자", 3, 1));
            rows.add(h1);
            List<TableCell> h2 = new ArrayList<TableCell>();
            h2.add(new TableCell("소계", 1, 1));
            h2.add(new TableCell(y1, 1, 1));
            h2.add(new TableCell(y2, 1, 1));
            h2.add(new TableCell("소계", 1, 1));
            h2.add(new TableCell(y3, 1, 1));
            h2.add(new TableCell(y4, 1, 1));
            rows.add(h2);

            // 사업량 행: 빈 칸 생략·셀 내 줄바꿈 보정
            int qtyIdx = indexOfLabel(toks, "사업량", "사 업 량");
            if (qtyIdx >= 0) {
                List<String> raw = takeValuesAfter(toks, qtyIdx + 1, 20, new String[] {
                        "사업비", "사 업 비", "계", "공사비", "공 사 비", "재원조달", "재원", "시 비", "시비"
                });
                List<String> cells = groupPlanQuantityCells(raw);
                List<String> vals = alignPlanRowValues(cells, 7);
                rows.add(buildDataRow("사 업 량", 2, vals, 7));
            }

            // 사업비 블록 — 실제 존재하는 비목만 출력하고 rowspan을 그에 맞춤
            // (고정 rowspan=6인데 비목이 1개면 표가 좌측으로 밀림)
            String[] costLabels = {
                    "계", "공 사 비", "보 상 비", "설 계 비", "감 리 비", "기타부대비",
                    "인건비", "연구장비재료비", "연구활동비", "연구수당", "간접비"
            };
            String[] costStop = new String[] {
                    "공사비", "공 사 비", "보상비", "보 상 비", "설계비", "설 계 비",
                    "감리비", "감 리 비", "기타부대비", "인건비", "연구장비재료비", "연구활동비",
                    "연구수당", "간접비", "재원조달", "재원", "시 비", "시비", "국 비", "국비"
            };
            List<String> presentCosts = new ArrayList<String>();
            List<Integer> presentIdx = new ArrayList<Integer>();
            int bizbiIdx = indexOfLabel(toks, "사업비", "사 업 비");
            for (String lab : costLabels) {
                int idx;
                if ("계".equals(lab)) {
                    idx = indexOfLabelAfter(toks, bizbiIdx, "계");
                } else {
                    idx = indexOfLabel(toks, lab.replace(" ", ""), lab);
                }
                if (idx < 0) {
                    continue;
                }
                // 재원조달 이후 라벨은 사업비 비목이 아님
                int fundPos = indexOfLabel(toks, "재원조달", "재 원 조 달");
                if (fundPos >= 0 && idx > fundPos) {
                    continue;
                }
                presentCosts.add(lab);
                presentIdx.add(Integer.valueOf(idx));
            }
            int costSpan = Math.max(1, presentCosts.size());
            for (int ci = 0; ci < presentCosts.size(); ci++) {
                String lab = presentCosts.get(ci);
                int idx = presentIdx.get(ci).intValue();
                List<String> raw = takeValuesAfter(toks, idx + 1, 12, costStop);
                List<String> vals = alignPlanRowValues(filterAmountLikeValues(raw), 7);
                if (ci == 0) {
                    List<TableCell> row = new ArrayList<TableCell>();
                    row.add(new TableCell("사 업 비", 1, costSpan));
                    row.add(new TableCell(lab, 1, 1));
                    for (int i = 0; i < 7; i++) {
                        row.add(new TableCell(i < vals.size() ? vals.get(i) : "", 1, 1));
                    }
                    rows.add(row);
                } else {
                    rows.add(buildDataRow(lab, 1, vals, 7));
                }
            }

            // 재원조달 (기타부대비 이후부터 검색해 오매칭 방지)
            String[] fundLabels = { "시 비", "국 비", "교 부 세", "부 담 금", "기 타" };
            int fundFrom = indexOfLabel(toks, "재원조달", "재 원 조 달");
            if (fundFrom < 0) {
                fundFrom = indexOfLabel(toks, "기타부대비");
            }
            List<String> presentFunds = new ArrayList<String>();
            List<Integer> presentFundIdx = new ArrayList<Integer>();
            for (String lab : fundLabels) {
                int idx = indexOfLabelAfter(toks, fundFrom, lab.replace(" ", ""));
                if (idx < 0) {
                    idx = indexOfLabelAfter(toks, fundFrom, lab);
                }
                if (idx < 0) {
                    continue;
                }
                presentFunds.add(lab);
                presentFundIdx.add(Integer.valueOf(idx));
            }
            int fundSpan = Math.max(1, presentFunds.size());
            for (int fi = 0; fi < presentFunds.size(); fi++) {
                String lab = presentFunds.get(fi);
                int idx = presentFundIdx.get(fi).intValue();
                List<String> raw = takeValuesAfter(toks, idx + 1, 12, fundLabels);
                List<String> vals = alignPlanRowValues(filterAmountLikeValues(raw), 7);
                if (fi == 0) {
                    List<TableCell> row = new ArrayList<TableCell>();
                    row.add(new TableCell("재 원 조 달", 1, fundSpan));
                    row.add(new TableCell(lab, 1, 1));
                    for (int i = 0; i < 7; i++) {
                        row.add(new TableCell(i < vals.size() ? vals.get(i) : "", 1, 1));
                    }
                    rows.add(row);
                } else {
                    rows.add(buildDataRow(lab, 1, vals, 7));
                }
            }
            return rows.size() >= 3 ? rows : null;
        }

        /** HWP에서 세로로 쪼개진 라벨(사/업/비, 재/원/조/달)을 하나로 합침 */
        private static List<String> preprocessPlanTokens(List<String> toks) {
            List<String> out = new ArrayList<String>();
            if (toks == null) {
                return out;
            }
            String[][] merges = new String[][] {
                    { "사", "업", "비" },
                    { "재", "원", "조", "달" },
                    { "공", "사", "비" },
                    { "보", "상", "비" },
                    { "설", "계", "비" },
                    { "감", "리", "비" },
                    { "국", "비" },
                    { "시", "비" },
                    { "교", "부", "세" },
                    { "부", "담", "금" },
                    { "기", "타" }
            };
            int i = 0;
            while (i < toks.size()) {
                boolean merged = false;
                for (String[] m : merges) {
                    if (i + m.length <= toks.size()) {
                        boolean ok = true;
                        for (int k = 0; k < m.length; k++) {
                            if (!m[k].equals(toks.get(i + k).replace(" ", ""))) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            StringBuilder sb = new StringBuilder();
                            for (int k = 0; k < m.length; k++) {
                                if (k > 0) {
                                    sb.append(' ');
                                }
                                sb.append(m[k]);
                            }
                            out.add(sb.toString());
                            i += m.length;
                            merged = true;
                            break;
                        }
                    }
                }
                if (!merged) {
                    // 연구장비․ + 재료비 → 연구장비재료비
                    if (i + 1 < toks.size()) {
                        String a = toks.get(i);
                        String b = toks.get(i + 1).replace(" ", "");
                        String an = a.replace(" ", "");
                        if (b.equals("재료비") && (an.startsWith("연구장비") || an.endsWith("․")
                                || an.endsWith("·") || an.endsWith("･"))) {
                            out.add("연구장비재료비");
                            i += 2;
                            continue;
                        }
                    }
                    out.add(toks.get(i));
                    i++;
                }
            }
            return out;
        }

        /** 사업량: 한 칸에 여러 줄(OAC… / 항온항습기…)이 온 경우 묶음 */
        private static List<String> groupPlanQuantityCells(List<String> raw) {
            List<String> src = new ArrayList<String>();
            for (String t : safe(raw)) {
                String n = t.replace(" ", "");
                if (n.equals("사업비") || n.equals("계") || n.equals("재원조달")) {
                    break;
                }
                // 단독 한글 1글자는 라벨 잔여로 보고 중단
                if (n.length() == 1 && n.matches("[가-힣]")) {
                    break;
                }
                src.add(t);
            }
            List<String> cells = new ArrayList<String>();
            int i = 0;
            while (i < src.size()) {
                String a = src.get(i);
                if (!isPlanAmountToken(a) && i + 1 < src.size() && !isPlanAmountToken(src.get(i + 1))) {
                    cells.add(a + "\n" + src.get(i + 1));
                    i += 2;
                } else {
                    cells.add(a);
                    i++;
                }
            }
            return cells;
        }

        private static List<String> filterAmountLikeValues(List<String> raw) {
            List<String> out = new ArrayList<String>();
            for (String t : safe(raw)) {
                if (isPlanAmountToken(t) || "-".equals(t) || "—".equals(t) || "－".equals(t)) {
                    out.add(t);
                } else {
                    // 숫자 아닌 라벨/잔여 글자가 나오면 중단
                    String n = t.replace(" ", "");
                    if (n.length() <= 2 && n.matches(".*[가-힣].*")) {
                        break;
                    }
                    if (n.contains("재원") || n.contains("사업비") || n.contains("공사비")) {
                        break;
                    }
                }
            }
            return out;
        }

        private static boolean isPlanAmountToken(String t) {
            if (t == null) {
                return false;
            }
            String s = t.trim().replace(",", "");
            if (s.length() == 0) {
                return false;
            }
            if (s.matches("^-?\\d+(\\.\\d+)?$")) {
                return true;
            }
            if (s.startsWith("(") && s.endsWith(")") && s.matches(".*\\d.*")) {
                return true;
            }
            return false;
        }

        /**
         * HWP 빈 칸 생략으로 값이 부족할 때 열 위치 보정.
         * 열: 0총사업비 1기투소계 2’24이전 3’25까지 4금후소계 5당해예산안 6이후
         */
        private static List<String> alignPlanRowValues(List<String> vals, int cols) {
            List<String> out = new ArrayList<String>();
            for (int i = 0; i < cols; i++) {
                out.add("");
            }
            if (vals == null || vals.isEmpty()) {
                return out;
            }
            if (vals.size() >= cols) {
                for (int i = 0; i < cols; i++) {
                    out.set(i, vals.get(i));
                }
                return out;
            }
            if (vals.size() == 1) {
                out.set(0, vals.get(0));
            } else if (vals.size() == 2) {
                out.set(0, vals.get(0));
                out.set(5, vals.get(1));
            } else if (vals.size() == 3) {
                // 신규 등: 총사업비 · 금후소계 · 당해년 예산안
                out.set(0, vals.get(0));
                out.set(4, vals.get(1));
                out.set(5, vals.get(2));
            } else if (vals.size() == 4) {
                out.set(0, vals.get(0));
                out.set(1, vals.get(1));
                out.set(4, vals.get(2));
                out.set(5, vals.get(3));
            } else if (vals.size() == 5) {
                out.set(0, vals.get(0));
                out.set(1, vals.get(1));
                out.set(3, vals.get(2));
                out.set(4, vals.get(3));
                out.set(5, vals.get(4));
            } else {
                for (int i = 0; i < vals.size(); i++) {
                    out.set(i, vals.get(i));
                }
            }
            return out;
        }

        private static List<TableCell> buildDataRow(String label, int labelSpan, List<String> vals, int n) {
            List<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(label, labelSpan, 1));
            for (int i = 0; i < n; i++) {
                row.add(new TableCell(i < vals.size() ? vals.get(i) : "", 1, 1));
            }
            return row;
        }

        private static int indexOfLabel(List<String> toks, String... labels) {
            // 1순위: 공백 제거 후 완전일치 (기타 ⊂ 기타부대비 오매칭 방지)
            for (int i = 0; i < toks.size(); i++) {
                String t = toks.get(i).replace(" ", "");
                for (String lab : labels) {
                    if (lab == null) {
                        continue;
                    }
                    if (t.equals(lab.replace(" ", ""))) {
                        return i;
                    }
                }
            }
            // 2순위: 포함 매칭 — 더 긴 토큰에 짧은 라벨이 들어간 경우는 제외
            for (int i = 0; i < toks.size(); i++) {
                String t = toks.get(i).replace(" ", "");
                for (String lab : labels) {
                    if (lab == null) {
                        continue;
                    }
                    String l = lab.replace(" ", "");
                    if (l.length() < 2 || !t.contains(l) || t.length() > l.length()) {
                        continue;
                    }
                    if ("계".equals(l) && t.length() > 2) {
                        continue;
                    }
                    return i;
                }
            }
            return -1;
        }

        private static int indexOfLabelAfter(List<String> toks, int after, String label) {
            if (after < 0) {
                after = -1;
            }
            String l = label.replace(" ", "");
            for (int i = after + 1; i < toks.size(); i++) {
                if (toks.get(i).replace(" ", "").equals(l)) {
                    return i;
                }
            }
            return -1;
        }

        private static List<String> takeValuesAfter(List<String> toks, int from, int max, String[] stopLabels) {
            List<String> out = new ArrayList<String>();
            Set<String> stops = new HashSet<String>();
            if (stopLabels != null) {
                for (String s : stopLabels) {
                    if (s != null) {
                        stops.add(s.replace(" ", ""));
                    }
                }
            }
            stops.add("주)");
            stops.add("※");
            stops.add("사업비");
            stops.add("재원조달");
            for (int i = from; i < toks.size() && out.size() < max; i++) {
                String raw = toks.get(i);
                String t = raw.replace(" ", "");
                if (t.length() == 0) {
                    continue;
                }
                if (stops.contains(t) || t.startsWith("주)") || t.startsWith("※")) {
                    break;
                }
                // 쪼개진 라벨 시작(사/재/공…)이면 중단
                if (t.length() == 1 && t.matches("[가-힣]")
                        && (t.equals("사") || t.equals("재") || t.equals("공") || t.equals("보")
                                || t.equals("설") || t.equals("감") || t.equals("국") || t.equals("시"))) {
                    break;
                }
                out.add(raw);
            }
            return out;
        }

        private static List<String> filterSectionBody(List<String> lines) {
            List<String> raw = new ArrayList<String>();
            for (String line : safe(lines)) {
                String t = line.trim();
                if (t.length() == 0) {
                    continue;
                }
                if (t.startsWith("□ ") || t.startsWith("【")) {
                    continue;
                }
                raw.add(t);
            }
            // 지정 외 표: 병합 표는 제거, 단순 표는 칸을 공백으로 이어 한 줄로
            return normalizeEmbeddedNonDesignatedTables(raw);
        }

        /**
         * 본문으로 풀린 지정 외 표 정리.
         * - 병합 표(프로그램/지원대상 등): 표시하지 않음
         * - 단순 표(연도·건수 격자 등): 칸을 다음칸(공백)으로 이어 한 줄 표시
         */
        private static List<String> normalizeEmbeddedNonDesignatedTables(List<String> lines) {
            List<String> out = new ArrayList<String>();
            if (lines == null || lines.isEmpty()) {
                return out;
            }
            int i = 0;
            while (i < lines.size()) {
                if (isEmbeddedTableDumpStart(lines, i)) {
                    int end = skipEmbeddedTableDump(lines, i);
                    if (!looksLikeMergedTableDump(lines, i, end)) {
                        String flat = flattenEmbeddedTableDump(lines, i, end);
                        if (flat.length() > 0) {
                            out.add(flat);
                        }
                    }
                    i = end;
                    continue;
                }
                out.add(lines.get(i));
                i++;
            }
            return out;
        }

        /** @deprecated use {@link #normalizeEmbeddedNonDesignatedTables(List)} */
        private static List<String> stripEmbeddedNonDesignatedTables(List<String> lines) {
            return normalizeEmbeddedNonDesignatedTables(lines);
        }

        private static String flattenEmbeddedTableDump(List<String> lines, int start, int end) {
            StringBuilder sb = new StringBuilder();
            if (lines == null) {
                return "";
            }
            for (int i = start; i < end && i < lines.size(); i++) {
                String t = compactText(lines.get(i));
                if (t.length() == 0) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(t);
            }
            return sb.toString().trim();
        }

        /** 줄로 풀린 표가 병합형(지정 외에서 숨길 대상)인지 */
        private static boolean looksLikeMergedTableDump(List<String> lines, int start, int end) {
            if (lines == null || start >= end) {
                return false;
            }
            int singleHangul = 0;
            for (int i = start; i < end && i < lines.size(); i++) {
                String t = compactText(lines.get(i));
                String n = t.replace(" ", "");
                if (n.length() == 0) {
                    continue;
                }
                if ("프로그램".equals(n) || "지원대상".equals(n) || "산출내역".equals(n)
                        || n.startsWith("지원금") || n.startsWith("민간부담")
                        || n.startsWith("시비(") || n.startsWith("국비(")) {
                    return true;
                }
                if (n.length() == 1 && n.matches("[가-힣]")) {
                    singleHangul++;
                }
            }
            // 세로 분할 라벨이 많은 병합표 잔여
            return singleHangul >= 4;
        }

        private static boolean isEmbeddedTableDumpStart(List<String> lines, int i) {
            if (i < 0 || i >= lines.size()) {
                return false;
            }
            String t = compactText(lines.get(i));
            if (isTableColumnHeaderCue(t)) {
                return true;
            }
            // 짧은 칸 나열이 이어지고, 그 안에 표 헤더/데이터 단서가 있으면 표로 간주
            if (i + 2 >= lines.size()) {
                return false;
            }
            if (!(isCellLikeDumpLine(t)
                    && isCellLikeDumpLine(compactText(lines.get(i + 1)))
                    && isCellLikeDumpLine(compactText(lines.get(i + 2))))) {
                return false;
            }
            int cellLike = 0;
            int dataCells = 0;
            boolean headerHit = false;
            int end = Math.min(lines.size(), i + 24);
            for (int j = i; j < end; j++) {
                String x = compactText(lines.get(j));
                if (!isCellLikeDumpLine(x)) {
                    break;
                }
                cellLike++;
                if (isTableColumnHeaderCue(x)) {
                    headerHit = true;
                }
                if (isTableDataCellDump(x)) {
                    dataCells++;
                }
            }
            return headerHit || (cellLike >= 6 && dataCells >= 2);
        }

        private static int skipEmbeddedTableDump(List<String> lines, int start) {
            int i = start;
            while (i < lines.size() && isCellLikeDumpLine(compactText(lines.get(i)))) {
                i++;
            }
            return i;
        }

        private static boolean isTableColumnHeaderCue(String t) {
            if (t == null || t.length() == 0 || t.length() > 42) {
                return false;
            }
            String n = t.replace(" ", "");
            if ("프로그램".equals(n) || "지원대상".equals(n) || "건수".equals(n)
                    || "연번".equals(n) || "비고".equals(n) || "산출내역".equals(n)
                    || "구분".equals(n) || "연도".equals(n) || "항목".equals(n)
                    || "사업발굴".equals(n) || "사업기획".equals(n) || "사업유치완료".equals(n)) {
                return true;
            }
            if (n.startsWith("지원금") || n.startsWith("시비") || n.startsWith("민간부담")
                    || n.startsWith("국비") || n.contains("산출내역")
                    || n.startsWith("R&D기획") || n.startsWith("R＆D기획")) {
                return true;
            }
            return false;
        }

        /** 표 칸으로 흘러들어온 짧은 줄(본문 불릿/문장 제외) */
        private static boolean isCellLikeDumpLine(String t) {
            t = compactText(t);
            if (t.length() == 0) {
                return true;
            }
            if (t.startsWith("○") || t.startsWith("□") || t.startsWith("【")
                    || t.startsWith("※") || t.startsWith("주)") || t.startsWith("주）")) {
                return false;
            }
            // 본문 불릿은 표가 아님
            if (t.startsWith("-") || t.startsWith("·") || t.startsWith("▹") || t.startsWith("▸")
                    || t.startsWith("－") || t.startsWith("–")) {
                return false;
            }
            // 긴 문장은 본문
            if (t.length() > 45) {
                return false;
            }
            return true;
        }

        private static boolean isNumericCellDump(String t) {
            if (t == null || t.length() == 0) {
                return false;
            }
            String n = t.replace(" ", "");
            return n.matches("^[0-9]+([.,][0-9]+)?$")
                    || n.matches("^\\([0-9.,×xX]+\\)$")
                    || n.matches("^[0-9]+([.,][0-9]+)?[×xX][0-9]+$");
        }

        /** 단순 표 데이터 칸: 숫자·N건·연도 등 */
        private static boolean isTableDataCellDump(String t) {
            if (isNumericCellDump(t)) {
                return true;
            }
            if (t == null || t.length() == 0) {
                return false;
            }
            String n = t.replace(" ", "");
            if (n.matches(".*[0-9]+건.*")) {
                return true;
            }
            if (n.matches("^\\d{4}년.*") || n.matches("^[’']?\\d{2}년.*")
                    || n.matches("^\\?\\d{2}[.년].*")) {
                return true;
            }
            return false;
        }

        /** 사전절차: 라벨/값 교차 → 4열(2쌍) 표 */
        private static List<List<String>> buildProcedureTable(List<String> lines) {
            List<String> cells = new ArrayList<String>();
            for (String line : safe(lines)) {
                String t = line.trim();
                if (t.length() == 0 || t.startsWith("□") || t.startsWith("【") || t.startsWith("(단위")) {
                    continue;
                }
                // 다음 서식/첨부 구간 — 사전절차 표 종료 (병합표 잔여 유입 방지)
                if (t.startsWith("<") || t.startsWith("서식") || t.contains("위치도")
                        || t.contains("현장사진") || t.contains("조감도") || t.contains("평면도")) {
                    break;
                }
                cells.add(t);
            }
            // 라벨·상태 짝이 아닌 꼬리 잔여 제거
            while (cells.size() >= 2) {
                String last = cells.get(cells.size() - 1);
                String prev = cells.get(cells.size() - 2);
                if (isProcedureStatusValue(last) || isProcedureStatusValue(prev)) {
                    break;
                }
                cells.remove(cells.size() - 1);
            }
            if (cells.size() < 2) {
                return null;
            }
            List<List<String>> rows = new ArrayList<List<String>>();
            // HWP와 같이 한 행에 라벨·값 2쌍(4열)
            for (int i = 0; i + 3 < cells.size(); i += 4) {
                rows.add(Arrays.asList(cells.get(i), cells.get(i + 1), cells.get(i + 2), cells.get(i + 3)));
            }
            int rem = cells.size() % 4;
            int start = cells.size() - rem;
            if (rem == 2) {
                rows.add(Arrays.asList(cells.get(start), cells.get(start + 1), "", ""));
            } else if (rem == 1) {
                // 단독 잔여 라벨은 표에 넣지 않음
            } else if (rem == 3) {
                // 값 없는 세 번째 칸은 버림 (라벨-값 쌍만 유지)
                if (isProcedureStatusValue(cells.get(start + 1))) {
                    rows.add(Arrays.asList(cells.get(start), cells.get(start + 1), "", ""));
                }
            }
            if (rows.isEmpty()) {
                for (int i = 0; i + 1 < cells.size(); i += 2) {
                    rows.add(Arrays.asList(cells.get(i), cells.get(i + 1)));
                }
            }
            return rows;
        }

        private static boolean isProcedureStatusValue(String t) {
            if (t == null) {
                return false;
            }
            String n = t.replace(" ", "");
            return "비대상".equals(n) || "대상".equals(n) || "이행중".equals(n) || "완료".equals(n)
                    || "해당없음".equals(n) || "해당무".equals(n) || n.startsWith("이행")
                    || n.startsWith("완료") || n.startsWith("대상");
        }

        /** 연도별 예산: 구분/연도 헤더 + 예산액/집행액 (금액만, 다음 사업 제목 유입 금지) */
        private static List<List<String>> buildYearlyBudgetTable(List<String> lines) {
            List<String> toks = new ArrayList<String>();
            for (String line : safe(lines)) {
                String t = line.trim();
                if (t.length() == 0 || t.startsWith("(단위") || t.startsWith("【") || t.startsWith("□")) {
                    continue;
                }
                if (isBizDescSectionBoundary(t)) {
                    break;
                }
                toks.add(t);
            }
            int idx = -1;
            for (int i = 0; i < toks.size(); i++) {
                if ("구분".equals(toks.get(i)) || toks.get(i).startsWith("구 분")) {
                    idx = i;
                    break;
                }
            }
            if (idx < 0) {
                return rebuildLooseTable(lines);
            }
            int budgetIdx = -1;
            int execIdx = -1;
            for (int i = idx + 1; i < toks.size(); i++) {
                if ("예산액".equals(toks.get(i)) && budgetIdx < 0) {
                    budgetIdx = i;
                }
                if ("집행액".equals(toks.get(i)) && execIdx < 0) {
                    execIdx = i;
                }
            }
            if (budgetIdx < 0) {
                return rebuildLooseTable(lines);
            }
            List<String> header = new ArrayList<String>();
            header.add("구분");
            for (int i = idx + 1; i < budgetIdx; i++) {
                String h = toks.get(i);
                if ("예산액".equals(h) || "집행액".equals(h) || isBizDescSectionBoundary(h)) {
                    break;
                }
                header.add(h);
            }
            int colCount = header.size();
            if (colCount < 2) {
                return null;
            }
            List<List<String>> rows = new ArrayList<List<String>>();
            rows.add(header);
            int endBudget = execIdx > budgetIdx ? execIdx : toks.size();
            rows.add(takeYearlyAmountRow("예산액", toks, budgetIdx + 1, endBudget, colCount - 1));
            if (execIdx >= 0) {
                rows.add(takeYearlyAmountRow("집행액", toks, execIdx + 1, toks.size(), colCount - 1));
            }
            return rows;
        }

        /** 금액·공란표기만 수집. '주요 ○○사업 설명서' 등이 칸에 섞이지 않게 중단 */
        private static List<String> takeYearlyAmountRow(String label, List<String> toks, int from, int to, int need) {
            List<String> row = new ArrayList<String>();
            row.add(label);
            for (int i = from; i < to && row.size() < need + 1; i++) {
                String t = toks.get(i);
                if (isBizDescSectionBoundary(t) || "예산액".equals(t) || "집행액".equals(t)
                        || "구분".equals(t) || (t != null && t.startsWith("구 분"))) {
                    break;
                }
                if (!isYearlyAmountToken(t)) {
                    break;
                }
                row.add(t);
            }
            while (row.size() < need + 1) {
                row.add("");
            }
            return row;
        }

        static boolean isBizDescSectionBoundary(String t) {
            if (t == null || t.length() == 0) {
                return false;
            }
            String n = t.replace(" ", "");
            if (n.contains("사업설명서") && (n.startsWith("주요") || n.contains("경상사업") || n.contains("투자사업"))) {
                return true;
            }
            if ("부서명".equals(n) || "세부사업".equals(n) || "개별사업".equals(n)
                    || "사업명세서".equals(n) || "담당자".equals(n)) {
                return true;
            }
            return false;
        }

        /** 연도별 표 칸 값: 숫자·괄호금액·대시 등 */
        private static boolean isYearlyAmountToken(String t) {
            if (t == null) {
                return false;
            }
            String s = t.trim();
            if (s.length() == 0 || isBizDescSectionBoundary(s)) {
                return false;
            }
            if ("-".equals(s) || "—".equals(s) || "－".equals(s) || "–".equals(s)
                    || "없음".equals(s) || "해당없음".equals(s) || "―".equals(s)) {
                return true;
            }
            if (s.matches("^-?\\d{1,3}(,\\d{3})+(\\.\\d+)?$")) {
                return true;
            }
            if (s.matches("^-?\\d+(\\.\\d+)?$")) {
                return true;
            }
            // (110,000) / (미확정) 등 보조표기
            if (s.startsWith("(") && s.endsWith(")") && s.length() >= 3) {
                return true;
            }
            return false;
        }

        private static List<String> takeRow(String label, List<String> toks, int from, int to, int need) {
            List<String> row = new ArrayList<String>();
            row.add(label);
            for (int i = from; i < to && row.size() < need + 1; i++) {
                row.add(toks.get(i));
            }
            while (row.size() < need + 1) {
                row.add("");
            }
            return row;
        }

        /** 단순 격자 추정(투자계획 폴백) */
        private static List<List<String>> rebuildLooseTable(List<String> lines) {
            List<String> toks = new ArrayList<String>();
            for (String line : safe(lines)) {
                String t = line.trim();
                if (t.length() == 0 || t.startsWith("(단위") || t.startsWith("주)")) {
                    continue;
                }
                toks.add(t);
            }
            if (toks.size() < 6) {
                return null;
            }
            // 열 수 추정: '총사업비' 전후 헤더 구간
            int cols = 8;
            List<List<String>> rows = new ArrayList<List<String>>();
            for (int i = 0; i < toks.size(); i += cols) {
                List<String> row = new ArrayList<String>();
                for (int c = 0; c < cols && i + c < toks.size(); c++) {
                    row.add(toks.get(i + c));
                }
                rows.add(row);
            }
            return rows.size() >= 2 ? rows : null;
        }

        private static String classifyTable(List<List<String>> t) {
            if (t == null || t.isEmpty()) {
                return "";
            }
            return classifyFlat(flat(t));
        }

        private static String classifyCellTable(List<List<TableCell>> t) {
            if (t == null || t.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (List<TableCell> r : t) {
                if (r == null) {
                    continue;
                }
                for (TableCell c : r) {
                    if (c != null && c.text != null) {
                        sb.append(c.text).append(' ');
                    }
                }
            }
            return classifyFlat(sb.toString());
        }

        /** attach 단계에서 사용 */
        public static String classifyCellTablePublic(List<List<TableCell>> t) {
            return classifyCellTable(t);
        }

        public int scorePlanTablePublic(List<List<TableCell>> table) {
            return scorePlanTable(table);
        }

        private static String classifyFlat(String all) {
            if (all.contains("부서명") && all.contains("담당자") && (all.contains("세부사업") || all.contains("개별사업"))) {
                return "header";
            }
            if (all.contains("출자출연") || all.contains("투자심사") || all.contains("지방보조금심의")
                    || all.contains("공유재산관리계획")) {
                return "procedure";
            }
            // 연도별: 예산액+집행액과 함께 구분/연도 표식이 있을 때만 (다른 금액표 오분류 방지)
            boolean yearlyAmt = all.contains("예산액") && all.contains("집행액");
            boolean yearlyHint = all.contains("연도별 예산") || all.contains("구분") || all.contains("구 분")
                    || all.contains("’") || all.contains("'") || all.matches("(?s).*20\\d{2}.*");
            if (yearlyAmt && yearlyHint) {
                return "yearly";
            }
            if (all.contains("총사업비") || all.contains("기 투 자") || all.contains("기투자")
                    || all.contains("금 후 투 자") || all.contains("연차별") || all.contains("□ 투자계획")) {
                return "plan";
            }
            return "other";
        }

        private static String flat(List<List<String>> t) {
            StringBuilder sb = new StringBuilder();
            for (List<String> r : t) {
                if (r == null) {
                    continue;
                }
                for (String c : r) {
                    if (c != null) {
                        sb.append(c).append(' ');
                    }
                }
            }
            return sb.toString();
        }

        private static List<String> safe(List<String> lines) {
            return lines == null ? Collections.<String>emptyList() : lines;
        }

        private static String n(String s) {
            return s == null ? "" : s;
        }

        /** 표 요약용 행 목록 (문서 순서) — 호환용 */
        public List<Map<String, String>> summaryRows() {
            List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
            addRow(rows, "부서", dept);
            addRow(rows, "담당자", manager);
            addRow(rows, "□ 사업개요", joinLines(overviewLines));
            addRow(rows, "□ 사업내용", joinLines(contentLines));
            addRow(rows, "□ 추진실적/추진경과", joinLines(progressLines));
            addRow(rows, "편성내용·산출근거", joinLines(merge(reasonLines, calcLines)));
            return rows;
        }

        /** 구분(demand_cont) 가져오기: □ 사업개요 + □ 사업내용 */
        public String buildDemandCont() {
            List<String> parts = new ArrayList<String>();
            if (!overviewLines.isEmpty()) {
                parts.add("□ 사업개요");
                parts.addAll(filterBody(overviewLines));
            }
            if (!contentLines.isEmpty()) {
                parts.add("□ 사업내용");
                parts.addAll(filterBody(contentLines));
            }
            return join(parts, "\n");
        }

        /** 검토내용 추진사항: □ 추진실적 / □ 추진경과 및 향후계획 */
        public String buildProgressCont() {
            List<String> parts = new ArrayList<String>();
            parts.add("○ 추진사항");
            List<String> body = filterBody(progressLines);
            if (body.isEmpty()) {
                parts.add("- (해당 내용 없음)");
            } else {
                parts.addAll(body);
            }
            return join(parts, "\n");
        }

        /** 검토내용 요구내용: 편성내용·산출근거(투자) / 사업내용·산출근거(경상) */
        public String buildRequestCont() {
            List<String> parts = new ArrayList<String>();
            parts.add("○ 요구내용");
            List<String> src = filterBody(merge(reasonLines, calcLines));
            if (src.isEmpty()) {
                parts.add("- (해당 내용 없음)");
            } else {
                parts.addAll(src);
            }
            return join(parts, "\n");
        }

        /** 검토내용 전체 초안(추진+요구+검토의견 자리) */
        public String buildExamCont() {
            return buildProgressCont() + "\n" + buildRequestCont() + "\n◈ 검토의견\n- (심사자 작성)";
        }

        private static List<String> filterBody(List<String> lines) {
            List<String> cleaned = stripEmbeddedNonDesignatedTables(lines);
            List<String> out = new ArrayList<String>();
            if (cleaned == null) {
                return out;
            }
            for (String l : cleaned) {
                if (l == null) {
                    continue;
                }
                String t = l.trim();
                if (t.length() == 0 || t.startsWith("□")) {
                    continue;
                }
                if (t.startsWith("○")) {
                    t = "- " + t.substring(1).trim();
                } else if (!t.startsWith("-") && !t.startsWith("·") && !t.startsWith("※")) {
                    t = "- " + t;
                }
                out.add(t);
            }
            return out;
        }

        private static void addRow(List<Map<String, String>> rows, String section, String content) {
            if (content == null || content.trim().length() == 0) {
                return;
            }
            Map<String, String> row = new LinkedHashMap<String, String>();
            row.put("section", section);
            row.put("content", content.trim());
            rows.add(row);
        }

        private static List<String> merge(List<String> a, List<String> b) {
            List<String> out = new ArrayList<String>();
            if (a != null) {
                out.addAll(a);
            }
            if (b != null) {
                out.addAll(b);
            }
            return out;
        }

        private static String joinLines(List<String> lines) {
            if (lines == null || lines.isEmpty()) {
                return "";
            }
            return join(lines, "\n");
        }

        private static String join(List<String> list, String sep) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(sep);
                }
                sb.append(list.get(i));
            }
            return sb.toString();
        }
    }

    public List<BizBlock> parse(InputStream hwpxStream) throws Exception {
        List<String> sectionXmls = new ArrayList<String>();
        List<String> texts = extractTextsAndXml(hwpxStream, sectionXmls);
        List<BizBlock> list = dedupe(parseBusinesses(texts));
        List<List<List<TableCell>>> allCellTables = extractAllCellTables(sectionXmls);
        attachCellTablesToBusinesses(list, allCellTables);
        // blocks는 업로드 저장 시 생략 — 화면 조회 시 재생성
        return list;
    }

    /** HWPX ZIP 또는 HWP OLE에서 추출한 텍스트 목록으로 사업 블록 파싱 */
    public List<BizBlock> parseFromTexts(List<String> texts) throws Exception {
        // blocks는 업로드 저장 시 생략 — 화면 조회 시 재생성
        return dedupe(parseBusinesses(texts));
    }

    private List<String> extractTextsAndXml(InputStream hwpxStream, List<String> sectionXmls) throws Exception {
        List<String> texts = new ArrayList<String>();
        ZipInputStream zis = new ZipInputStream(hwpxStream);
        try {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name != null && name.startsWith("Contents/section") && name.endsWith(".xml")) {
                    String xml = readEntryUtf8(zis);
                    sectionXmls.add(xml);
                    Matcher m = TEXT_NODE.matcher(xml);
                    while (m.find()) {
                        String t = stripTags(m.group(1));
                        t = unescapeXml(t).replace('\u00a0', ' ').replaceAll("\\s+", " ").trim();
                        if (t.length() > 0) {
                            texts.add(t);
                        }
                    }
                }
                zis.closeEntry();
            }
        } finally {
            zis.close();
        }
        return texts;
    }

    private List<List<List<TableCell>>> extractAllCellTables(List<String> sectionXmls) {
        List<List<List<TableCell>>> tables = new ArrayList<List<List<TableCell>>>();
        if (sectionXmls == null) {
            return tables;
        }
        for (String xml : sectionXmls) {
            Matcher tm = TBL_NODE.matcher(xml);
            while (tm.find()) {
                List<List<TableCell>> table = new ArrayList<List<TableCell>>();
                Matcher trm = TR_NODE.matcher(tm.group(1));
                while (trm.find()) {
                    List<TableCell> row = new ArrayList<TableCell>();
                    Matcher tcm = TC_NODE.matcher(trm.group(1));
                    while (tcm.find()) {
                        String tcXml = tcm.group(1);
                        String cell = "";
                        Matcher cm = TEXT_NODE.matcher(tcXml);
                        StringBuilder sb = new StringBuilder();
                        while (cm.find()) {
                            String t = stripTags(cm.group(1));
                            t = unescapeXml(t).replace('\u00a0', ' ').replaceAll("\\s+", " ").trim();
                            if (t.length() > 0) {
                                if (sb.length() > 0) {
                                    sb.append(' ');
                                }
                                sb.append(t);
                            }
                        }
                        cell = compactWs(sb.toString());
                        int colSpan = 1;
                        int rowSpan = 1;
                        Matcher sm = CELL_SPAN.matcher(tcXml);
                        if (sm.find()) {
                            String attrs = sm.group(1);
                            colSpan = readAttrInt(attrs, "colSpan", 1);
                            rowSpan = readAttrInt(attrs, "rowSpan", 1);
                        }
                        // cellAddr 기반 span 보강 (cellSpan 누락 대비)
                        if (colSpan == 1 && rowSpan == 1) {
                            Matcher cam = Pattern.compile(
                                    "<(?:hp:)?cellSpan\\s+colSpan=\"(\\d+)\"\\s+rowSpan=\"(\\d+)\"",
                                    Pattern.CASE_INSENSITIVE).matcher(tcXml);
                            if (cam.find()) {
                                colSpan = Integer.parseInt(cam.group(1));
                                rowSpan = Integer.parseInt(cam.group(2));
                            }
                        }
                        row.add(new TableCell(cell, colSpan, rowSpan));
                    }
                    if (!row.isEmpty()) {
                        table.add(row);
                    }
                }
                if (table.size() >= 1) {
                    tables.add(table);
                }
            }
        }
        return tables;
    }

    private int readAttrInt(String attrs, String name, int def) {
        if (attrs == null) {
            return def;
        }
        Matcher m = Pattern.compile(name + "\\s*=\\s*\"(\\d+)\"", Pattern.CASE_INSENSITIVE).matcher(attrs);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception e) {
                return def;
            }
        }
        return def;
    }

    private List<List<String>> cellsToStringTable(List<List<TableCell>> cells) {
        List<List<String>> out = new ArrayList<List<String>>();
        if (cells == null) {
            return out;
        }
        for (List<TableCell> row : cells) {
            List<String> r = new ArrayList<String>();
            if (row != null) {
                for (TableCell c : row) {
                    r.add(c == null ? "" : c.text);
                }
            }
            out.add(r);
        }
        return out;
    }

    private static String compactWs(String s) {
        if (s == null) {
            return "";
        }
        return s.replace('\u00a0', ' ').replaceAll("[\\r\\n\\t]+", " ").replaceAll(" +", " ").trim();
    }

    /** 문서 순서 표를 사업 블록에 분배 — 투자계획 표는 내용 유사도로 매칭 */
    private void attachCellTablesToBusinesses(List<BizBlock> list, List<List<List<TableCell>>> allTables) {
        if (list == null || list.isEmpty() || allTables == null || allTables.isEmpty()) {
            return;
        }
        List<List<List<TableCell>>> planTables = new ArrayList<List<List<TableCell>>>();
        List<List<List<TableCell>>> procTables = new ArrayList<List<List<TableCell>>>();
        List<List<List<TableCell>>> yearlyTables = new ArrayList<List<List<TableCell>>>();
        List<List<List<TableCell>>> otherTables = new ArrayList<List<List<TableCell>>>();
        for (List<List<TableCell>> t : allTables) {
            List<List<String>> flat = cellsToStringTable(t);
            if (flat.size() <= 5 && maxCols(flat) <= 2 && looksLikeHeaderTable(flat)) {
                continue;
            }
            String k = BizBlock.classifyCellTablePublic(t);
            if ("plan".equals(k)) {
                planTables.add(t);
            } else if ("procedure".equals(k)) {
                procTables.add(t);
            } else if ("yearly".equals(k)) {
                yearlyTables.add(t);
            } else if (!"header".equals(k) && flat.size() >= 1) {
                // 현장사진 등 스킵
                String all = flatCellTexts(t);
                if (all.contains("□ 현장사진") || all.contains("원본 그림의 크기")) {
                    continue;
                }
                // 지정 외 병합 표는 사업에 붙이지 않음(화면 미표시)
                if (BizBlock.hasMergedCells(t)) {
                    continue;
                }
                otherTables.add(t);
            }
        }

        boolean[] planUsed = new boolean[planTables.size()];
        for (BizBlock b : list) {
            // 모든 사업에 전체 투자계획 표 후보 공유 (화면 재검색용)
            b.planTableCandidates = planTables;
            if (b.planLines == null || b.planLines.isEmpty()) {
                continue;
            }
            int bestIdx = -1;
            int bestScore = -1;
            for (int i = 0; i < planTables.size(); i++) {
                if (planUsed[i]) {
                    continue;
                }
                int sc = b.scorePlanTablePublic(planTables.get(i));
                if (sc > bestScore) {
                    bestScore = sc;
                    bestIdx = i;
                }
            }
            if (bestIdx >= 0) {
                planUsed[bestIdx] = true;
                addCellTable(b, planTables.get(bestIdx));
            }
        }
        // 매칭 안 된 투자계획 표: 순서대로 planLines 있는 사업에 보충
        int pi = 0;
        for (BizBlock b : list) {
            if (b.planLines == null || b.planLines.isEmpty()) {
                continue;
            }
            if (hasKind(b, "plan")) {
                continue;
            }
            while (pi < planTables.size() && planUsed[pi]) {
                pi++;
            }
            if (pi < planTables.size()) {
                planUsed[pi] = true;
                addCellTable(b, planTables.get(pi));
                pi++;
            }
        }

        // 사전절차: 라인 유무와 관계없이 표가 있으면 사업에 분배
        assignByPresence(list, procTables, "procedure");
        forceAssignRemaining(list, procTables, "procedure");

        // 연도별: 내용(연도·금액) 유사도로 매칭 — 단순 순서 분배는 다른 사업 표를 붙이는 오류 유발
        boolean[] yearlyUsed = new boolean[yearlyTables.size()];
        for (BizBlock b : list) {
            b.yearlyTableCandidates = yearlyTables;
            if (b.planLines != null && !b.planLines.isEmpty()) {
                continue; // 투자사업에는 연도별 표 없음
            }
            if (b.yearlyBudgetLines == null || b.yearlyBudgetLines.isEmpty()) {
                continue;
            }
            int bestIdx = -1;
            int bestScore = -1;
            for (int i = 0; i < yearlyTables.size(); i++) {
                if (yearlyUsed[i]) {
                    continue;
                }
                int sc = b.scoreYearlyTablePublic(yearlyTables.get(i));
                if (sc > bestScore) {
                    bestScore = sc;
                    bestIdx = i;
                }
            }
            if (bestIdx >= 0 && bestScore >= 8) {
                yearlyUsed[bestIdx] = true;
                addCellTable(b, yearlyTables.get(bestIdx));
            }
        }
        // 점수 미달분은 라인 재구성에 맡김(순서 강제 분배 금지 — 다른 사업 표 부착 방지)

        // 지정 외 표: 사업 수에 맞게 순서 분배(평문 표시용)
        if (!otherTables.isEmpty()) {
            int per = Math.max(1, (otherTables.size() + list.size() - 1) / list.size());
            int idx = 0;
            for (int i = 0; i < list.size(); i++) {
                BizBlock b = list.get(i);
                int end = (i == list.size() - 1) ? otherTables.size() : Math.min(otherTables.size(), idx + per);
                for (int j = idx; j < end; j++) {
                    addCellTable(b, otherTables.get(j));
                }
                idx = end;
            }
        }
    }

    private void forceAssignRemaining(List<BizBlock> list, List<List<List<TableCell>>> tables, String kind) {
        if (tables == null || tables.isEmpty() || list == null) {
            return;
        }
        // 이미 사용된 표 제외를 위해 hasKind 기준으로 사업에만 추가
        int ti = 0;
        for (BizBlock b : list) {
            while (ti < tables.size()) {
                // 이 표가 이미 어떤 사업에 들어갔는지 간단히: 동일 참조가 extracted에 있으면 skip
                List<List<TableCell>> t = tables.get(ti);
                boolean used = false;
                for (BizBlock x : list) {
                    if (x.extractedCellTables != null && x.extractedCellTables.contains(t)) {
                        used = true;
                        break;
                    }
                }
                if (!used) {
                    break;
                }
                ti++;
            }
            if (ti >= tables.size()) {
                break;
            }
            if (hasKind(b, kind)) {
                continue;
            }
            // 사전절차: 라인 있거나 투자/경상 모두 가능. 연도별: 투자(planLines)면 스킵
            if ("yearly".equals(kind) && b.planLines != null && !b.planLines.isEmpty()) {
                continue;
            }
            addCellTable(b, tables.get(ti++));
        }
    }

    private void assignByPresence(List<BizBlock> list, List<List<List<TableCell>>> tables, String kind) {
        if (tables == null || tables.isEmpty()) {
            return;
        }
        int ti = 0;
        for (BizBlock b : list) {
            boolean need = false;
            if ("procedure".equals(kind)) {
                need = b.procedureLines != null && !b.procedureLines.isEmpty();
            } else if ("yearly".equals(kind)) {
                need = b.yearlyBudgetLines != null && !b.yearlyBudgetLines.isEmpty();
            }
            if (!need || hasKind(b, kind)) {
                continue;
            }
            if (ti < tables.size()) {
                addCellTable(b, tables.get(ti++));
            }
        }
        // 남은 표는 순서대로 보충
        for (BizBlock b : list) {
            if (ti >= tables.size()) {
                break;
            }
            if (hasKind(b, kind)) {
                continue;
            }
            boolean softNeed = true;
            if ("yearly".equals(kind) && (b.planLines != null && !b.planLines.isEmpty())) {
                // 투자사업에는 연도별 표가 보통 없음
                softNeed = false;
            }
            if (softNeed) {
                addCellTable(b, tables.get(ti++));
            }
        }
    }

    private boolean hasKind(BizBlock b, String kind) {
        if (b.extractedCellTables == null) {
            return false;
        }
        for (List<List<TableCell>> t : b.extractedCellTables) {
            if (kind.equals(BizBlock.classifyCellTablePublic(t))) {
                return true;
            }
        }
        return false;
    }

    private void addCellTable(BizBlock b, List<List<TableCell>> ct) {
        b.extractedCellTables.add(ct);
        b.extractedTables.add(cellsToStringTable(ct));
    }

    private String flatCellTexts(List<List<TableCell>> t) {
        StringBuilder sb = new StringBuilder();
        for (List<TableCell> row : t) {
            if (row == null) {
                continue;
            }
            for (TableCell c : row) {
                if (c != null && c.text != null) {
                    sb.append(c.text).append(' ');
                }
            }
        }
        return sb.toString();
    }

    private List<List<List<String>>> extractAllTables(List<String> sectionXmls) {
        List<List<List<String>>> tables = new ArrayList<List<List<String>>>();
        for (List<List<TableCell>> ct : extractAllCellTables(sectionXmls)) {
            tables.add(cellsToStringTable(ct));
        }
        return tables;
    }

    /** @deprecated use attachCellTablesToBusinesses */
    private void attachTablesToBusinesses(List<BizBlock> list, List<List<List<String>>> allTables) {
        if (list == null || list.isEmpty() || allTables == null || allTables.isEmpty()) {
            return;
        }
        List<List<List<String>>> bodyTables = new ArrayList<List<List<String>>>();
        for (List<List<String>> t : allTables) {
            if (t.size() >= 2 && !(t.size() <= 5 && maxCols(t) <= 2 && looksLikeHeaderTable(t))) {
                bodyTables.add(t);
            }
        }
        if (bodyTables.isEmpty()) {
            return;
        }
        int per = Math.max(1, bodyTables.size() / list.size());
        int idx = 0;
        for (int i = 0; i < list.size(); i++) {
            BizBlock b = list.get(i);
            int end = (i == list.size() - 1) ? bodyTables.size() : Math.min(bodyTables.size(), idx + per);
            for (int j = idx; j < end; j++) {
                b.extractedTables.add(bodyTables.get(j));
            }
            idx = end;
        }
    }

    private int maxCols(List<List<String>> t) {
        int m = 0;
        for (List<String> r : t) {
            if (r != null && r.size() > m) {
                m = r.size();
            }
        }
        return m;
    }

    private boolean looksLikeHeaderTable(List<List<String>> t) {
        for (List<String> r : t) {
            if (r == null || r.isEmpty()) {
                continue;
            }
            String c0 = r.get(0);
            if ("부서명".equals(c0) || "담당자".equals(c0) || "세부사업".equals(c0) || "개별사업".equals(c0)) {
                return true;
            }
        }
        return false;
    }

    private String readEntryUtf8(InputStream in) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) >= 0) {
            bos.write(buf, 0, n);
        }
        return new String(bos.toByteArray(), Charset.forName("UTF-8"));
    }

    private String stripTags(String s) {
        return s.replaceAll("<[^>]+>", "");
    }

    private String unescapeXml(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
                .replace("&quot;", "\"").replace("&apos;", "'");
    }

    private List<BizBlock> parseBusinesses(List<String> texts) {
        List<BizBlock> list = new ArrayList<BizBlock>();
        int i = 0;
        while (i < texts.size()) {
            if (!"부서명".equals(texts.get(i))) {
                i++;
                continue;
            }
            BizBlock biz = new BizBlock();
            while (i < texts.size() && !texts.get(i).startsWith("□ ")) {
                String key = texts.get(i);
                if ("부서명".equals(key) || "담당자".equals(key) || "세부사업".equals(key) || "개별사업".equals(key)) {
                    List<String> vals = new ArrayList<String>();
                    int j = i + 1;
                    while (j < texts.size()) {
                        String v = texts.get(j);
                        if (HEADER_KEYS.contains(v) || v.startsWith("□ ")) {
                            break;
                        }
                        if ("<".equals(v) || ">".equals(v)) {
                            j++;
                            continue;
                        }
                        vals.add(v);
                        j++;
                        if (j < texts.size() && ("부서명".equals(texts.get(j)) || "담당자".equals(texts.get(j))
                                || "세부사업".equals(texts.get(j)) || "개별사업".equals(texts.get(j)))) {
                            break;
                        }
                        if (join(vals).length() > 100) {
                            break;
                        }
                    }
                    String s = join(vals).replaceAll("\\s+", " ").trim();
                    if ("부서명".equals(key)) {
                        biz.dept = s;
                    } else if ("담당자".equals(key)) {
                        biz.manager = s;
                    } else if ("세부사업".equals(key)) {
                        biz.detailBiz = s;
                    } else if ("개별사업".equals(key)) {
                        biz.indivBiz = s;
                    }
                    i = j;
                    continue;
                }
                i++;
            }
            int bodyStart = i;
            int bodyEnd = texts.size();
            for (int k = bodyStart + 1; k < texts.size(); k++) {
                if ("부서명".equals(texts.get(k))) {
                    bodyEnd = k;
                    break;
                }
            }
            String section = "overview";
            for (int k = bodyStart; k < bodyEnd; k++) {
                String line = texts.get(k);
                if (line.startsWith("□ 사업개요")) {
                    section = "overview";
                    continue;
                }
                if (line.startsWith("□ 사업내용")) {
                    section = "content";
                    continue;
                }
                if (line.startsWith("□ 투자계획") || line.startsWith("□ 예산")) {
                    section = "plan";
                    continue;
                }
                if (line.startsWith("□ 사전절차") || "사전절차".equals(line)) {
                    section = "procedure";
                    continue;
                }
                if (line.contains("연도별 예산") || line.startsWith("【연도별")) {
                    section = "yearly";
                    continue;
                }
                if (BizBlock.isBizDescSectionBoundary(line)) {
                    // 다음 사업/문서 구분자 — 연도별 수집 종료
                    if ("yearly".equals(section) || "procedure".equals(section)) {
                        section = "overview";
                    }
                    continue;
                }
                if (line.contains("편성사유") || line.contains("편성내용") || line.startsWith("□ 편성")
                        || (line.startsWith("□ ") && line.contains("추경예산"))
                        || BizBlock.isBudgetOrReasonHeading(line)) {
                    section = "reason";
                    continue;
                }
                if (line.contains("산출근거") && (line.startsWith("-") || line.startsWith("○") || line.startsWith("□")
                        || line.indexOf("산출근거") == 0)) {
                    section = "calc";
                    // keep label line in calc
                }
                // 추진실적/추진계획: 머릿글(□/ㅁ)만 섹션 전환 — 본문 중 "추진실적 보고" 오탐 방지
                if (BizBlock.isProgressSectionHeading(line)) {
                    section = "progress";
                    biz.progressLines.add(line);
                    continue;
                }
                if ("overview".equals(section)) {
                    biz.overviewLines.add(line);
                } else if ("content".equals(section)) {
                    biz.contentLines.add(line);
                } else if ("plan".equals(section)) {
                    biz.planLines.add(line);
                } else if ("procedure".equals(section)) {
                    biz.procedureLines.add(line);
                } else if ("yearly".equals(section)) {
                    biz.yearlyBudgetLines.add(line);
                } else if ("reason".equals(section)) {
                    biz.reasonLines.add(line);
                } else if ("calc".equals(section)) {
                    biz.calcLines.add(line);
                } else if ("progress".equals(section)) {
                    biz.progressLines.add(line);
                }
            }
            Matcher mok = Pattern.compile("\\((\\d{3})[-–]?(\\d{2})").matcher(biz.indivBiz);
            if (mok.find()) {
                biz.mokCd = mok.group(1) + mok.group(2);
            } else {
                Matcher mok2 = Pattern.compile("\\[(\\d{3})[-–]?(\\d{2})\\]").matcher(biz.indivBiz);
                if (mok2.find()) {
                    biz.mokCd = mok2.group(1) + mok2.group(2);
                }
            }
            if (biz.detailBiz.length() > 0 || biz.indivBiz.length() > 0) {
                // 화면용 blocks는 업로드 시 만들지 않음(대용량 HWP에서 수분 소요).
                // 조회(getSummary) 때 buildBlocksFromLines()로 생성한다.
                list.add(biz);
            }
            i = bodyEnd;
        }
        return list;
    }

    private List<BizBlock> dedupe(List<BizBlock> list) {
        Set<String> seen = new HashSet<String>();
        List<BizBlock> out = new ArrayList<BizBlock>();
        for (BizBlock b : list) {
            String key = b.dept + "|" + b.detailBiz + "|" + b.indivBiz;
            if (seen.contains(key)) {
                continue;
            }
            seen.add(key);
            b.seq = out.size() + 1;
            out.add(b);
        }
        return out;
    }

    private String join(List<String> vals) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(vals.get(i));
        }
        return sb.toString();
    }
}

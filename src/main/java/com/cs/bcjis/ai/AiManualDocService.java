package com.cs.bcjis.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.web.BcjisUserVO;

/**
 * AI 예산도우미 매뉴얼(PDF) 업로드·검색.
 * 저장: {fileStorePath}/ai-manual/
 * 권한: Globals.AiManualUploadAdminOnly=true 이면 POW_GR_CD=BC001(관리)만 업로드/삭제.
 */
@Component("aiManualDocService")
public class AiManualDocService {

    private static final Logger logger = Logger.getLogger(AiManualDocService.class);

    /** 매뉴얼 원문·정규화 캐시 (검색 반복 시 디스크 I/O·정규화 비용 제거) */
    private final ConcurrentHashMap<String, CachedManual> manualCache =
            new ConcurrentHashMap<String, CachedManual>();

    @Autowired
    @Qualifier("config")
    private Properties config;

    public boolean canManageManual() {
        // 로컬 테스트: AdminOnly=false 이면 로그인 사용자 모두 허용
        if (!isAdminOnly()) {
            return true;
        }
        try {
            BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            if (user == null) {
                return false;
            }
            String pow = user.getPowGrCd();
            // BC001=시스템/예산편성 관리권한 (기존 화면 기준)
            return "BC001".equals(pow);
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject listManuals() {
        JSONObject out = new JSONObject();
        JSONArray files = new JSONArray();
        File dir = getManualDir();
        File meta = new File(dir, "index.json");
        if (meta.exists()) {
            try {
                JSONObject idx = JSONObject.fromObject(readTextFile(meta));
                if (idx.containsKey("files") && idx.get("files") instanceof JSONArray) {
                    files = idx.getJSONArray("files");
                }
            } catch (Exception e) {
                logger.warn("매뉴얼 index 읽기 실패: " + e.getMessage());
            }
        }
        out.put("files", files);
        out.put("canManage", Boolean.valueOf(canManageManual()));
        out.put("adminOnly", Boolean.valueOf(isAdminOnly()));
        return out;
    }

    public JSONObject uploadManual(HttpServletRequest request) throws Exception {
        if (!canManageManual()) {
            throw new IllegalArgumentException("예산운용지침 업로드 권한이 없습니다. (관리권한자만 가능)");
        }
        List<MultipartFile> uploadFiles = collectUploadFiles(request);
        if (uploadFiles.isEmpty()) {
            boolean mp = false;
            try {
                mp = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
            } catch (Exception ignore) {
                String ct = request.getContentType();
                mp = ct != null && ct.toLowerCase(Locale.ROOT).indexOf("multipart/form-data") >= 0;
            }
            throw new IllegalArgumentException(mp
                    ? "업로드할 PDF 파일이 없습니다. (파일이 선택되지 않았거나 전송에 실패했습니다)"
                    : "파일 전송 형식 오류입니다. 팝업을 새로 연 뒤 다시 올려 주세요.");
        }

        File dir = getManualDir();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("저장 폴더를 만들 수 없습니다: " + dir.getAbsolutePath());
        }

        JSONObject idx = listManuals();
        JSONArray files = idx.getJSONArray("files");
        JSONArray uploaded = new JSONArray();
        String userId = "";
        try {
            BcjisUserVO user = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            userId = user != null ? user.getUserId() : "";
        } catch (Exception e) {
            userId = "";
        }

        for (int i = 0; i < uploadFiles.size(); i++) {
            MultipartFile file = uploadFiles.get(i);
            String orgName = file.getOriginalFilename();
            if (orgName == null) {
                orgName = "manual.pdf";
            }
            // 경로 포함 파일명 방지
            int slash = Math.max(orgName.lastIndexOf('/'), orgName.lastIndexOf('\\'));
            if (slash >= 0 && slash + 1 < orgName.length()) {
                orgName = orgName.substring(slash + 1);
            }
            String lower = orgName.toLowerCase(Locale.ROOT);
            if (!lower.endsWith(".pdf")) {
                throw new IllegalArgumentException("PDF 파일만 업로드할 수 있습니다: " + orgName);
            }

            String id = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String storeName = id + ".pdf";
            File pdfFile = new File(dir, storeName);
            long t0 = System.currentTimeMillis();
            saveMultipartToFile(file, pdfFile);
            String text;
            try {
                text = extractPdfTextFast(pdfFile);
            } catch (Exception ex) {
                logger.error("PDF 텍스트 추출 실패 name=" + orgName, ex);
                // 깨진 파일 남기지 않음
                try { pdfFile.delete(); } catch (Exception ignore) { /* */ }
                throw new IllegalArgumentException("PDF 텍스트 추출에 실패했습니다: " + orgName
                        + " (" + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()) + ")");
            }
            int pageCnt = countPagesMarker(text);
            if (pageCnt <= 0) {
                logger.warn("PDF 페이지 마커 없음 name=" + orgName + " — 검색 시 p.1만 표시될 수 있음");
            }
            writeTextFile(new File(dir, id + ".txt"), text);
            putManualCache(id, text, new File(dir, id + ".txt").lastModified());
            logger.info("예산운용지침 업로드 완료 name=" + orgName + " size=" + pdfFile.length()
                    + " chars=" + (text == null ? 0 : text.length())
                    + " pages=" + pageCnt
                    + " ms=" + (System.currentTimeMillis() - t0));

            JSONObject entry = new JSONObject();
            entry.put("id", id);
            entry.put("name", orgName);
            entry.put("storeName", storeName);
            entry.put("size", Long.valueOf(pdfFile.length()));
            entry.put("chars", Integer.valueOf(text == null ? 0 : text.length()));
            entry.put("pages", Integer.valueOf(pageCnt));
            entry.put("uploadedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            entry.put("uploadedBy", userId);
            files.add(0, entry);
            uploaded.add(entry);
        }
        saveIndex(files);

        JSONObject out = new JSONObject();
        out.put("files", uploaded);
        out.put("file", uploaded.size() > 0 ? uploaded.get(0) : new JSONObject());
        out.put("count", Integer.valueOf(uploaded.size()));
        out.put("message", uploaded.size() + "건의 예산운용지침이 업로드되었습니다.");
        return out;
    }

    private List<MultipartFile> collectUploadFiles(HttpServletRequest request) {
        List<MultipartFile> uploadFiles = new ArrayList<MultipartFile>();
        if (!(request instanceof MultipartHttpServletRequest)) {
            return uploadFiles;
        }
        MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) request;
        Iterator<String> names = mreq.getFileNames();
        while (names.hasNext()) {
            String fn = names.next();
            List<MultipartFile> list = mreq.getFiles(fn);
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    MultipartFile f = list.get(i);
                    if (f != null && f.getSize() > 0) {
                        uploadFiles.add(f);
                    }
                }
            } else {
                MultipartFile f = mreq.getFile(fn);
                if (f != null && f.getSize() > 0) {
                    uploadFiles.add(f);
                }
            }
        }
        return uploadFiles;
    }

    /** Windows 등에서 MultipartFile.transferTo 절대경로 rename 실패 대비 — 스트림 복사 */
    private void saveMultipartToFile(MultipartFile file, File dest) throws Exception {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = file.getInputStream();
            out = new FileOutputStream(dest);
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
            out.flush();
        } finally {
            if (out != null) {
                try { out.close(); } catch (Exception ignore) { /* */ }
            }
            if (in != null) {
                try { in.close(); } catch (Exception ignore) { /* */ }
            }
        }
        if (!dest.exists() || dest.length() <= 0) {
            throw new IllegalStateException("파일 저장에 실패했습니다: " + dest.getAbsolutePath());
        }
    }

    public JSONObject deleteManual(String id) throws Exception {
        if (!canManageManual()) {
            throw new IllegalArgumentException("매뉴얼 삭제 권한이 없습니다. (예산편성시스템 관리권한자만 가능)");
        }
        if (id == null || id.trim().length() == 0) {
            throw new IllegalArgumentException("삭제할 파일 id가 없습니다.");
        }
        id = id.trim();
        File dir = getManualDir();
        JSONObject idx = listManuals();
        JSONArray files = idx.getJSONArray("files");
        JSONArray next = new JSONArray();
        boolean found = false;
        String storeName = id + ".pdf";
        for (int i = 0; i < files.size(); i++) {
            JSONObject f = files.getJSONObject(i);
            if (id.equals(f.optString("id"))) {
                found = true;
                storeName = f.optString("storeName", storeName);
                continue;
            }
            next.add(f);
        }
        if (!found) {
            throw new IllegalArgumentException("해당 매뉴얼을 찾을 수 없습니다.");
        }
        safeDelete(new File(dir, storeName));
        safeDelete(new File(dir, id + ".txt"));
        manualCache.remove(id);
        saveIndex(next);
        JSONObject out = new JSONObject();
        out.put("message", "매뉴얼이 삭제되었습니다.");
        return out;
    }

    public JSONObject search(String keyword) throws Exception {
        JSONObject out = new JSONObject();
        String q = keyword == null ? "" : keyword.trim();
        if (q.length() == 0) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색어를 입력해 주세요.");
            return out;
        }
        JSONArray files = listManuals().getJSONArray("files");
        if (files == null || files.isEmpty()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "업로드된 매뉴얼이 없습니다. 일반자료검색 > 매뉴얼에서 PDF를 올려 주세요.");
            return out;
        }

        AiKeywordMatcher.SearchExpression expression = AiKeywordMatcher.parseExpression(q);
        if (expression.isEmpty()) {
            out.put("ok", Boolean.FALSE);
            out.put("message", "검색할 키워드를 입력해 주세요.");
            return out;
        }
        int maxPages = getIntProp("Globals.AiManualMaxPages", 2);
        int excerptChars = getIntProp("Globals.AiManualExcerptChars", 1400);
        int summaryLimit = getIntProp("Globals.AiManualSummaryChars", 5500);

        File dir = getManualDir();
        List<FileHits> found = new ArrayList<FileHits>();
        boolean anyExact = false;
        long tSearch0 = System.currentTimeMillis();
        for (int i = 0; i < files.size(); i++) {
            JSONObject f = files.getJSONObject(i);
            String id = f.optString("id");
            CachedManual cached = loadCachedManual(dir, id);
            if (cached == null || cached.text.length() == 0) {
                continue;
            }
            List<PageHit> hits = rankPagesFast(cached, expression);
            if (hits.isEmpty()) {
                continue;
            }
            boolean exact = hits.get(0).exact;
            if (exact) {
                anyExact = true;
            }
            found.add(new FileHits(f, cached.text, hits, exact));
        }
        if (logger.isInfoEnabled()) {
            logger.info("예산운용지침 검색 스캔 ms=" + (System.currentTimeMillis() - tSearch0)
                    + " files=" + files.size() + " hitFiles=" + found.size()
                    + " exact=" + anyExact + " qChars=" + q.length());
        }

        // 질문 문구가 그대로 실린 파일이 있으면 정확일치 파일들을 우선하되,
        // 해당되는 모든 문서를 결과에 포함한다 (특정 책자 1개로 좁히지 않음).
        if (anyExact) {
            List<FileHits> onlyExact = new ArrayList<FileHits>();
            List<FileHits> others = new ArrayList<FileHits>();
            for (int i = 0; i < found.size(); i++) {
                FileHits fh = found.get(i);
                if (fh.exact) {
                    onlyExact.add(fh);
                } else {
                    others.add(fh);
                }
            }
            found = onlyExact;
            // 정확일치가 없을 때만 유사 매칭을 쓰므로 others는 버림
            if (found.isEmpty()) {
                found = others;
            }
        }
        // 파일 정렬: 정확일치·히트수 우선 (책자만 단독 선택하지 않음)
        sortFileHits(found);

        List<JSONObject> items = new ArrayList<JSONObject>();
        StringBuilder contextForSummary = new StringBuilder();
        StringBuilder hitPageLabel = new StringBuilder();
        // 파일별 공정 배분 — 한 문서가 컨텍스트를 독점하지 않게
        int fileCount = Math.max(1, found.size());
        int perFileBudget = Math.max(1100, summaryLimit / fileCount);
        int pageLimit = maxPages;
        for (int i = 0; i < found.size() && items.size() < 10; i++) {
            FileHits fh = found.get(i);
            StringBuilder body = new StringBuilder();
            StringBuilder pages = new StringBuilder();
            int used = 0;
            for (int h = 0; h < fh.hits.size() && h < pageLimit; h++) {
                if (used >= perFileBudget) {
                    break;
                }
                PageHit hit = fh.hits.get(h);
                int remain = perFileBudget - used;
                int thisLimit = Math.min(excerptChars, remain);
                String snip = excerptStructured(fh.text, hit, thisLimit, expression);
                if (snip.length() == 0) {
                    continue;
                }
                if (pages.length() > 0) {
                    pages.append(", ");
                }
                pages.append(hit.pageNo);
                body.append("[p.").append(hit.pageNo).append("]\n").append(snip).append("\n\n");
                used += snip.length();
            }
            if (body.length() == 0) {
                continue;
            }
            String name = fh.file.optString("name");
            JSONObject item = new JSONObject();
            item.put("kind", "예산운용지침");
            item.put("title", name);
            item.put("sub", "관련 페이지: " + pages.toString());
            item.put("pages", pages.toString());
            item.put("body", body.toString().trim());
            item.put("url", "");
            item.put("manualId", fh.file.optString("id"));
            item.put("date", fh.file.optString("uploadedAt"));
            items.add(item);

            if (contextForSummary.length() < summaryLimit) {
                contextForSummary.append("### 파일: ").append(name).append("\n");
                contextForSummary.append(body).append("\n");
            }
            if (hitPageLabel.length() > 0) {
                hitPageLabel.append(" / ");
            }
            hitPageLabel.append(name).append(" p.").append(pages);
        }

        String ctx = contextForSummary.toString();
        if (ctx.length() > summaryLimit) {
            ctx = ctx.substring(0, summaryLimit);
        }

        out.put("ok", Boolean.TRUE);
        out.put("keyword", q);
        out.put("items", items);
        out.put("count", Integer.valueOf(items.size()));
        out.put("summaryContext", ctx);
        out.put("exactHit", Boolean.valueOf(anyExact));
        out.put("hitPageLabel", hitPageLabel.toString());
        if (items.isEmpty()) {
            out.put("message", "매뉴얼에서 \"" + q + "\" 관련 내용을 찾지 못했습니다.");
        }
        return out;
    }

    /** 파일별 적합 페이지 묶음 */
    private static final class FileHits {
        final JSONObject file;
        final String text;
        final List<PageHit> hits;
        final boolean exact;

        FileHits(JSONObject file, String text, List<PageHit> hits, boolean exact) {
            this.file = file;
            this.text = text;
            this.hits = hits;
            this.exact = exact;
        }
    }

    /** 적합 페이지 1건 — 원문 내 위치·페이지 범위·점수 */
    private static final class PageHit {
        final int pageNo;
        final int pos;
        final int pageStart;
        final int pageEnd;
        final double score;
        final boolean exact;

        PageHit(int pageNo, int pos, int pageStart, int pageEnd, double score, boolean exact) {
            this.pageNo = pageNo;
            this.pos = pos;
            this.pageStart = pageStart;
            this.pageEnd = pageEnd;
            this.score = score;
            this.exact = exact;
        }
    }

    /** 페이지 단위 빠른 추출 (위치정렬 OFF). 페이지 마커 포함. */
    private String extractPdfTextFast(File pdfFile) throws Exception {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(pdfFile);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            stripper.setAddMoreFormatting(false);
            int pages = doc.getNumberOfPages();
            StringBuilder sb = new StringBuilder(Math.min((int) pdfFile.length(), 2000000));
            for (int p = 1; p <= pages; p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                sb.append("\n-----PAGE ").append(p).append("-----\n");
                String pageText = stripper.getText(doc);
                if (pageText != null) {
                    sb.append(pageText);
                }
            }
            return sb.toString();
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private int countPagesMarker(String text) {
        if (text == null) {
            return 0;
        }
        int n = 0;
        int idx = 0;
        while ((idx = text.indexOf("-----PAGE ", idx)) >= 0) {
            n++;
            idx += 10;
        }
        return n;
    }

    /**
     * 빠른 페이지 랭킹.
     * 1) 문서 전체 정규화본에서 정확일치 위치를 찾아 해당 페이지만 수집
     * 2) 정확일치가 없을 때만 유사도 검색(페이지 상한)
     */
    private List<PageHit> rankPagesFast(CachedManual doc, AiKeywordMatcher.SearchExpression expression) {
        List<PageHit> exactHits = findExactPageHits(doc, expression);
        if (!exactHits.isEmpty()) {
            sortHits(exactHits);
            return exactHits;
        }
        return rankPagesSimilar(doc, expression);
    }

    /**
     * AND(&)는 문서 안에 모든 항이 있으면 통과하고, 항이 나온 페이지를 합친다.
     * (같은 페이지 교집합만 요구하면 항이 페이지를 나눠 실린 경우 누락됨)
     * OR(,)는 각 묶음의 페이지를 합친다.
     */
    private List<PageHit> findExactPageHits(CachedManual doc, AiKeywordMatcher.SearchExpression expression) {
        List<PageHit> exactHits = new ArrayList<PageHit>();
        if (doc == null || doc.norm == null || doc.norm.norm.length() == 0) {
            return exactHits;
        }
        List<List<String>> groups = expression.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            List<PageHit> union = new ArrayList<PageHit>();
            boolean ok = true;
            for (int t = 0; t < group.size(); t++) {
                String needle = AiKeywordMatcher.normalize(group.get(t));
                if (needle.length() == 0) {
                    ok = false;
                    break;
                }
                List<PageHit> termPages = findPagesForNeedle(doc, needle);
                if (termPages.isEmpty()) {
                    ok = false;
                    break;
                }
                for (int i = 0; i < termPages.size(); i++) {
                    PageHit h = termPages.get(i);
                    if (!containsPageHit(union, h.pageNo)) {
                        // AND 항이 많을수록 점수 가산 (정렬 시 유리)
                        double score = 100d + (group.size() > 1 ? t : 0);
                        union.add(new PageHit(h.pageNo, h.pos, h.pageStart, h.pageEnd, score, true));
                    }
                }
            }
            if (ok) {
                for (int i = 0; i < union.size(); i++) {
                    PageHit h = union.get(i);
                    if (!containsPageHit(exactHits, h.pageNo)) {
                        exactHits.add(h);
                    }
                }
            }
        }
        return exactHits;
    }

    private List<PageHit> findPagesForNeedle(CachedManual doc, String needle) {
        List<PageHit> out = new ArrayList<PageHit>();
        int from = 0;
        int guard = 0;
        while (from < doc.norm.norm.length() && guard < 100) {
            int at = doc.norm.norm.indexOf(needle, from);
            if (at < 0) {
                break;
            }
            guard++;
            int originalPos = doc.norm.map[at];
            int[] page = locatePage(doc.pageStarts, originalPos, doc.text.length());
            if (page != null && !containsPageHit(out, page[1])) {
                out.add(new PageHit(page[1], originalPos, page[0], page[2], 100d, true));
            }
            from = at + Math.max(1, needle.length());
        }
        return out;
    }

    private List<PageHit> intersectPageHits(List<PageHit> a, List<PageHit> b) {
        // 호환용 (문서단위 AND로 전환 후 미사용 가능)
        List<PageHit> out = new ArrayList<PageHit>();
        if (a == null || b == null) {
            return out;
        }
        for (int i = 0; i < a.size(); i++) {
            PageHit ha = a.get(i);
            for (int j = 0; j < b.size(); j++) {
                if (ha.pageNo == b.get(j).pageNo) {
                    out.add(ha);
                    break;
                }
            }
        }
        return out;
    }

    private boolean containsPageHit(List<PageHit> hits, int pageNo) {
        for (int i = 0; i < hits.size(); i++) {
            if (hits.get(i).pageNo == pageNo) {
                return true;
            }
        }
        return false;
    }

    private int[] locatePage(List<int[]> pageStarts, int pos, int textLen) {
        if (pageStarts == null || pageStarts.isEmpty()) {
            return new int[] { 0, 1, textLen };
        }
        for (int p = 0; p < pageStarts.size(); p++) {
            int start = pageStarts.get(p)[0];
            int end = (p + 1 < pageStarts.size()) ? pageStarts.get(p + 1)[0] : textLen;
            if (pos >= start && pos < end) {
                return new int[] { start, pageStarts.get(p)[1], end };
            }
        }
        int[] last = pageStarts.get(pageStarts.size() - 1);
        return new int[] { last[0], last[1], textLen };
    }

    private List<PageHit> rankPagesSimilar(CachedManual doc, AiKeywordMatcher.SearchExpression expression) {
        List<PageHit> similarHits = new ArrayList<PageHit>();
        if (doc == null || doc.text == null || doc.text.length() == 0) {
            return similarHits;
        }
        List<int[]> pageStarts = doc.pageStarts;
        int maxScan = getIntProp("Globals.AiManualFuzzyMaxPages", 60);
        int scanned = 0;
        for (int p = 0; p < pageStarts.size(); p++) {
            if (scanned >= maxScan) {
                break;
            }
            int start = pageStarts.get(p)[0];
            int end = (p + 1 < pageStarts.size()) ? pageStarts.get(p + 1)[0] : doc.text.length();
            int pageNo = pageStarts.get(p)[1];
            String pageBody = doc.text.substring(start, end);
            if (pageBody.length() < 40) {
                continue;
            }
            scanned++;
            NormText norm = buildNormText(pageBody);
            if (norm.norm.length() == 0) {
                continue;
            }
            MatchPoint point = matchExpression(expression, pageBody, norm, start);
            if (point.exact) {
                similarHits.add(new PageHit(pageNo, point.pos, start, end, 100d, true));
            } else if (point.score >= AiKeywordMatcher.DEFAULT_THRESHOLD) {
                similarHits.add(new PageHit(pageNo, point.pos, start, end,
                        point.score * 100d, false));
            }
        }
        sortHits(similarHits);
        return similarHits;
    }

    private static final class MatchPoint {
        final double score;
        final int pos;
        final boolean exact;

        MatchPoint(double score, int pos, boolean exact) {
            this.score = score;
            this.pos = pos;
            this.exact = exact;
        }
    }

    private MatchPoint matchExpression(AiKeywordMatcher.SearchExpression expression,
            String pageBody, NormText norm, int absoluteStart) {
        double bestScore = 0;
        int bestPos = absoluteStart;
        boolean bestExact = false;
        List<List<String>> groups = expression.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            double groupScore = 1.0;
            int groupPos = -1;
            boolean groupExact = true;
            for (int t = 0; t < group.size(); t++) {
                String term = group.get(t);
                String needle = AiKeywordMatcher.normalize(term);
                int at = norm.norm.indexOf(needle);
                double score;
                if (at >= 0) {
                    score = 1.0;
                    int originalPos = absoluteStart + norm.map[at];
                    if (groupPos < 0 || originalPos < groupPos) {
                        groupPos = originalPos;
                    }
                } else {
                    groupExact = false;
                    score = AiKeywordMatcher.bestSimilarity(term, pageBody);
                }
                if (score < groupScore) {
                    groupScore = score;
                }
            }
            if (groupPos < 0) {
                groupPos = absoluteStart;
            }
            if ((groupExact && !bestExact)
                    || (groupExact == bestExact && groupScore > bestScore)) {
                bestScore = groupScore;
                bestPos = groupPos;
                bestExact = groupExact;
            }
        }
        return new MatchPoint(bestScore, bestPos, bestExact);
    }

    private CachedManual loadCachedManual(File dir, String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        File txt = new File(dir, id + ".txt");
        if (!txt.exists()) {
            manualCache.remove(id);
            return null;
        }
        long mtime = txt.lastModified();
        CachedManual cached = manualCache.get(id);
        if (cached != null && cached.mtime == mtime && cached.text != null) {
            return cached;
        }
        try {
            String text = readTextFile(txt);
            return putManualCache(id, text, mtime);
        } catch (Exception e) {
            logger.warn("매뉴얼 텍스트 로드 실패 id=" + id + ": " + e.getMessage());
            return null;
        }
    }

    private CachedManual putManualCache(String id, String text, long mtime) {
        if (id == null) {
            return null;
        }
        String t = text == null ? "" : text;
        CachedManual c = new CachedManual(id, t, mtime, buildNormText(t), buildPageStarts(t));
        manualCache.put(id, c);
        return c;
    }

    private static final class CachedManual {
        final String id;
        final String text;
        final long mtime;
        final NormText norm;
        final List<int[]> pageStarts;

        CachedManual(String id, String text, long mtime, NormText norm, List<int[]> pageStarts) {
            this.id = id;
            this.text = text;
            this.mtime = mtime;
            this.norm = norm;
            this.pageStarts = pageStarts;
        }
    }


    private void sortHits(List<PageHit> hits) {
        java.util.Collections.sort(hits, new java.util.Comparator<PageHit>() {
            public int compare(PageHit a, PageHit b) {
                if (a.score != b.score) {
                    return a.score > b.score ? -1 : 1;
                }
                return a.pageNo - b.pageNo;
            }
        });
    }

    /** 정규화 문자열 + (정규화 위치 → 원문 위치) 매핑 */
    private static final class NormText {
        final String norm;
        final int[] map;

        NormText(String norm, int[] map) {
            this.norm = norm;
            this.map = map;
        }
    }

    /**
     * AiKeywordMatcher.normalize 와 같은 규칙으로 정규화하면서 원문 위치를 기억한다.
     * 정규화 후 찾은 위치를 원문 위치로 되돌려 발췌 구간을 정확히 잡기 위함.
     */
    private NormText buildNormText(String s) {
        int len = s.length();
        StringBuilder sb = new StringBuilder(len);
        int[] map = new int[len];
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (isIgnorableChar(c)) {
                continue;
            }
            map[sb.length()] = i;
            sb.append(Character.toLowerCase(c));
        }
        return new NormText(sb.toString(), map);
    }

    /** normalize 정규식 [\s·ㆍ․\u00A0~\-–—_/／] 과 동일한 무시 문자 판정 */
    private boolean isIgnorableChar(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        }
        switch (c) {
            case '\u00B7': // ·
            case '\u318D': // ㆍ
            case '\u2024': // ․
            case '\u00A0':
            case '~':
            case '-':
            case '\u2013': // –
            case '\u2014': // —
            case '_':
            case '/':
            case '\uFF0F': // ／
                return true;
            default:
                return false;
        }
    }

    private List<int[]> buildPageStarts(String text) {
        List<int[]> pageStarts = new ArrayList<int[]>();
        int idx = 0;
        while ((idx = text.indexOf("-----PAGE ", idx)) >= 0) {
            int numStart = idx + "-----PAGE ".length();
            int numEnd = text.indexOf("-----", numStart);
            int pageNo = 1;
            try {
                if (numEnd > numStart) {
                    pageNo = Integer.parseInt(text.substring(numStart, numEnd).trim());
                }
            } catch (Exception e) {
                pageNo = pageStarts.size() + 1;
            }
            pageStarts.add(new int[] { idx, pageNo });
            idx = numStart;
        }
        if (pageStarts.isEmpty()) {
            pageStarts.add(new int[] { 0, 1 });
        }
        return pageStarts;
    }

    /**
     * 히트 지점 주변을 발췌한다.
     *  - 발췌 구간을 해당 페이지 안으로 제한
     *  - 줄바꿈 유지 (표·항목 구조 보존)
     *  - 긴 정확일치 문구(예: 세출예산 절차별 이행사항)는 해당 줄~페이지 끝까지
     *  - 짧은 키워드(예: 사무관리비)는 주변 발췌만 사용해 여러 문서 요약을 균형 있게
     */
    private String excerptStructured(String text, PageHit hit, int chars,
            AiKeywordMatcher.SearchExpression expression) {
        if (text == null || text.length() == 0) {
            return "";
        }
        int limit = chars > 200 ? chars : 200;
        int pageStart = Math.max(0, hit.pageStart);
        int pageEnd = Math.min(text.length(), hit.pageEnd);
        if (pageEnd <= pageStart) {
            return "";
        }
        int pageLen = pageEnd - pageStart;
        boolean longExact = hit.exact && isLongExactPhrase(expression);

        int start;
        int end;
        if (longExact) {
            int lineStart = text.lastIndexOf('\n', Math.max(hit.pos, pageStart));
            if (lineStart >= pageStart && lineStart < pageEnd) {
                start = lineStart + 1;
            } else {
                start = Math.max(pageStart, hit.pos);
            }
            end = pageEnd;
            if (end - start > Math.max(limit, 8000)) {
                end = start + Math.max(limit, 8000);
                int nlEnd = text.lastIndexOf('\n', end);
                if (nlEnd > start + 200) {
                    end = nlEnd;
                }
            }
        } else if (pageLen <= limit) {
            start = pageStart;
            end = pageEnd;
        } else {
            int before = Math.min(limit / 4, 400);
            start = Math.max(pageStart, hit.pos - before);
            end = Math.min(pageEnd, start + limit);
            if (end - start < limit) {
                start = Math.max(pageStart, end - limit);
            }
            int nl = text.indexOf('\n', start);
            if (nl >= 0 && nl < start + 120 && nl < end) {
                start = nl + 1;
            }
            int nlEnd = text.lastIndexOf('\n', end);
            if (nlEnd > start + limit / 2) {
                end = nlEnd;
            }
        }

        String snip = text.substring(start, end).replaceAll("-----PAGE \\d+-----", " ");
        snip = snip.replaceAll("[ \\t\\x0B\\f\\r]+", " ");
        snip = snip.replaceAll("(?m)^ +", "");
        snip = snip.replaceAll("(?m) +$", "");
        snip = snip.replaceAll("\n{3,}", "\n\n");
        snip = snip.trim();
        if (!longExact && start > pageStart) {
            snip = "..." + snip;
        }
        if (end < pageEnd) {
            snip = snip + "...";
        }
        return snip;
    }

    /** 표·절차 제목처럼 긴 정확 문구인지 (공백제거 길이 기준) */
    private boolean isLongExactPhrase(AiKeywordMatcher.SearchExpression expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        List<List<String>> groups = expression.getOrGroups();
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            for (int t = 0; t < group.size(); t++) {
                String n = AiKeywordMatcher.normalize(group.get(t));
                if (n.length() >= 10) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 정확일치·최고점수·히트수 순으로 파일 정렬 (모든 매칭 문서 유지) */
    private void sortFileHits(List<FileHits> files) {
        if (files == null || files.size() <= 1) {
            return;
        }
        java.util.Collections.sort(files, new java.util.Comparator<FileHits>() {
            public int compare(FileHits a, FileHits b) {
                if (a.exact != b.exact) {
                    return a.exact ? -1 : 1;
                }
                double sa = bestHitScore(a);
                double sb = bestHitScore(b);
                if (sa != sb) {
                    return sa > sb ? -1 : 1;
                }
                int ha = a.hits == null ? 0 : a.hits.size();
                int hb = b.hits == null ? 0 : b.hits.size();
                if (ha != hb) {
                    return hb - ha;
                }
                return 0;
            }
        });
    }

    private double bestHitScore(FileHits fh) {
        if (fh == null || fh.hits == null || fh.hits.isEmpty()) {
            return 0d;
        }
        double best = 0d;
        for (int i = 0; i < fh.hits.size(); i++) {
            if (fh.hits.get(i).score > best) {
                best = fh.hits.get(i).score;
            }
        }
        return best;
    }

    private String extractPdfText(File pdfFile) throws Exception {
        return extractPdfTextFast(pdfFile);
    }

    private void saveIndex(JSONArray files) throws Exception {
        JSONObject idx = new JSONObject();
        idx.put("files", files);
        writeTextFile(new File(getManualDir(), "index.json"), idx.toString());
    }

    private File getManualDir() {
        String base = getProp("Globals.AiManualStorePath", "");
        if (base.length() == 0) {
            String store = getProp("Globals.fileStorePath", "C:/bcjis/upload/");
            if (!store.endsWith("/") && !store.endsWith("\\")) {
                store = store + "/";
            }
            base = store + "ai-manual/";
        }
        return new File(base);
    }

    private boolean isAdminOnly() {
        String v = getProp("Globals.AiManualUploadAdminOnly", "false");
        return "true".equalsIgnoreCase(v) || "Y".equalsIgnoreCase(v) || "1".equals(v);
    }

    private void safeDelete(File f) {
        try {
            if (f != null && f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private String readTextFile(File f) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        br.close();
        return sb.toString();
    }

    private void writeTextFile(File f, String text) throws Exception {
        Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        w.write(text == null ? "" : text);
        w.close();
    }

    private String getProp(String key, String def) {
        if (config == null) {
            return def;
        }
        String v = config.getProperty(key);
        if (v == null || v.trim().length() == 0) {
            return def;
        }
        return v.trim();
    }

    private int getIntProp(String key, int def) {
        try {
            return Integer.parseInt(getProp(key, String.valueOf(def)));
        } catch (Exception e) {
            return def;
        }
    }
}

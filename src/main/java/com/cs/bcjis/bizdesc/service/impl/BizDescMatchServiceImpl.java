package com.cs.bcjis.bizdesc.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cs.bcjis.bizdesc.BizDescMatcher;
import com.cs.bcjis.bizdesc.BizDescMatcher.NameSuggest;
import com.cs.bcjis.bizdesc.HwpOleTextExtractor;
import com.cs.bcjis.bizdesc.HwpxBizDescParser;
import com.cs.bcjis.bizdesc.HwpxBizDescParser.BizBlock;
import com.cs.bcjis.bizdesc.service.BizDescMatchService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.util.BcjisWebUtil;

@Service("bizDescMatchService")
public class BizDescMatchServiceImpl implements BizDescMatchService {

    private static final Logger logger = Logger.getLogger(BizDescMatchServiceImpl.class);

    /** 조회조건 '실국=전체' 업로드 시 모든 실국에서 공유하는 파일 스코프 */
    static final String OFFICE_CD_ALL = "ALL";

    /** 업로드 JSON 파싱 결과 메모리 캐시 (클릭 응답 가속) */
    private static final ConcurrentHashMap<String, CachedBizList> BIZ_LIST_CACHE =
            new ConcurrentHashMap<String, CachedBizList>();

    private static final class CachedBizList {
        final long jsonLastModified;
        final List<BizBlock> list;

        CachedBizList(long jsonLastModified, List<BizBlock> list) {
            this.jsonLastModified = jsonLastModified;
            this.list = list;
        }
    }

    @Resource(name = "bizDescMatchDAO")
    private BizDescMatchDAO bizDescMatchDAO;

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Override
    public JSONObject selectFileList(Map<String, Object> param) throws Exception {
        normalizeOfficeScope(param);
        @SuppressWarnings("rawtypes")
        List list = bizDescMatchDAO.selectFileList(param);
        JSONObject result = new JSONObject();
        result.put("dataList", JSONArray.fromObject(list == null ? new ArrayList() : list));
        return result;
    }

    @Override
    public JSONObject uploadFile(HttpServletRequest request, Map<String, Object> param) throws Exception {
        if (!(request instanceof MultipartHttpServletRequest)) {
            throw new IllegalArgumentException(
                    "파일 업로드 요청이 올바르지 않습니다.\n"
                            + "브라우저를 새로고침(Ctrl+F5)한 뒤 다시 시도해 주세요.");
        }
        MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest) request;
        MultipartFile mFile = null;
        Iterator<?> it = mptRequest.getFileNames();
        if (it.hasNext()) {
            mFile = mptRequest.getFile(String.valueOf(it.next()));
        }
        if (mFile == null || mFile.isEmpty()) {
            throw new IllegalArgumentException("업로드된 HWPX 파일이 없습니다.");
        }
        String orgName = mFile.getOriginalFilename();
        if (orgName == null) {
            orgName = "";
        }

        // 확장자와 무관하게 실제 파일 시그니처로 형식 판별
        // (시 보안솔루션 해제 후 확장자만 .hwpx이고 내부는 구형 HWP(OLE)인 경우가 많음)
        byte[] head = readHead(mFile, 16);
        String kind = detectFileKind(head);
        if ("DRM".equals(kind)) {
            throw new IllegalArgumentException(buildBadFileMessage(kind, orgName));
        }
        if (!"HWPX".equals(kind) && !"HWP".equals(kind)) {
            throw new IllegalArgumentException(buildBadFileMessage(kind, orgName));
        }
        if (orgName.length() == 0) {
            orgName = "HWPX".equals(kind) ? "bizdesc.hwpx" : "bizdesc.hwp";
        }

        File dir = getBizDescDir();
        String fileId = "BIZF_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        String streNm = fileId + ("HWPX".equals(kind) ? ".hwpx" : ".hwp");
        File saved = new File(dir, streNm);
        mFile.transferTo(saved);

        List<BizBlock> businesses;
        try {
            long t0 = System.currentTimeMillis();
            logger.info("bizdesc parse start kind=" + kind + " file=" + orgName
                    + " bytes=" + saved.length());
            if ("HWPX".equals(kind)) {
                InputStream in = new FileInputStream(saved);
                try {
                    businesses = new HwpxBizDescParser().parse(in);
                } finally {
                    in.close();
                }
            } else {
                // 구형 HWP(OLE) — 보안 해제 후 흔히 이 형식으로 남음
                List<String> texts = new HwpOleTextExtractor().extract(saved);
                logger.info("bizdesc extract done texts=" + (texts == null ? 0 : texts.size())
                        + " ms=" + (System.currentTimeMillis() - t0));
                businesses = new HwpxBizDescParser().parseFromTexts(texts);
            }
            logger.info("bizdesc parse done biz=" + (businesses == null ? 0 : businesses.size())
                    + " ms=" + (System.currentTimeMillis() - t0));
        } catch (IllegalArgumentException e) {
            if (saved.exists()) {
                saved.delete();
            }
            throw e;
        } catch (Exception e) {
            if (saved.exists()) {
                saved.delete();
            }
            logger.error("bizdesc parse failed kind=" + kind + " file=" + orgName, e);
            throw new IllegalArgumentException(
                    "사업설명서 파싱에 실패하였습니다.\n"
                            + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
        if (businesses == null || businesses.isEmpty()) {
            if (saved.exists()) {
                saved.delete();
            }
            throw new IllegalArgumentException(
                    "사업설명서에서 사업 블록을 찾지 못했습니다.\n"
                            + "문서에 ‘부서명/세부사업/개별사업’ 표가 포함된 사업설명서인지 확인해 주세요.");
        }

        String jsonNm = fileId + ".json";
        File jsonFile = new File(dir, jsonNm);
        writeJson(jsonFile, BizDescMatcher.toJsonArray(businesses));

        normalizeOfficeScope(param);
        if (BcjisCommUtil.isNullString(param.get("officeCd"))) {
            if (saved.exists()) {
                saved.delete();
            }
            throw new IllegalArgumentException("실국(또는 전체) 조회조건이 필요합니다.");
        }
        if (BcjisCommUtil.isNullString(param.get("bgtDgr"))) {
            if (saved.exists()) {
                saved.delete();
            }
            throw new IllegalArgumentException("예산차수를 선택해 주세요.");
        }

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("bizdescFileId", fileId);
        row.put("fisYear", param.get("fisYear"));
        row.put("bgtDgr", param.get("bgtDgr"));
        row.put("reportCd", param.get("reportCd") == null ? "" : param.get("reportCd"));
        row.put("officeCd", param.get("officeCd"));
        row.put("officeNm", param.get("officeNm") == null ? "" : param.get("officeNm"));
        row.put("atchFileId", "");
        row.put("orgFileNm", orgName);
        row.put("streFileNm", streNm);
        row.put("fileStreCours", dir.getAbsolutePath());
        row.put("parseJsonPath", jsonFile.getAbsolutePath());
        row.put("bizCount", Integer.valueOf(businesses.size()));
        row.put("regiId", param.get("userId"));
        bizDescMatchDAO.insertFile(row);
        putBizListCache(fileId, jsonFile.lastModified(), businesses);

        JSONObject result = new JSONObject();
        result.put("bizdescFileId", fileId);
        result.put("orgFileNm", orgName);
        result.put("bizCount", businesses.size());
        if (logger.isInfoEnabled()) {
            logger.info("bizdesc upload fileId=" + fileId + " biz=" + businesses.size());
        }
        return result;
    }

    @Override
    public JSONObject deleteFile(Map<String, Object> param) throws Exception {
        normalizeOfficeScope(param);
        @SuppressWarnings("rawtypes")
        Map file = bizDescMatchDAO.selectFile(param);
        if (file == null) {
            throw new IllegalArgumentException("삭제할 파일이 없습니다.");
        }
        normalizeOfficeScope(param);
        // 실국 스코프: 전체(ALL) 공유 파일은 실국=전체 조회에서만 삭제
        String reqOffice = param.get("officeCd") == null ? "" : String.valueOf(param.get("officeCd")).trim();
        String fileOffice = str(file, "officeCd", "office_cd");
        if (OFFICE_CD_ALL.equals(fileOffice) && !OFFICE_CD_ALL.equals(reqOffice)) {
            throw new IllegalArgumentException(
                    "전체 공유 사업설명서는 조회조건 '실국=전체'에서만 삭제할 수 있습니다.");
        }
        if (reqOffice.length() > 0 && fileOffice.length() > 0
                && !OFFICE_CD_ALL.equals(fileOffice) && !reqOffice.equals(fileOffice)) {
            throw new IllegalArgumentException("선택한 실국의 파일만 삭제할 수 있습니다.");
        }
        bizDescMatchDAO.deleteMatchByFile(param);
        bizDescMatchDAO.deleteFile(param);
        invalidateBizListCache(str(file, "bizdescFileId", "bizdesc_file_id"));

        deletePhysical(str(file, "fileStreCours", "file_stre_cours"), str(file, "streFileNm", "stre_file_nm"));
        deletePhysicalPath(str(file, "parseJsonPath", "parse_json_path"));

        JSONObject result = new JSONObject();
        result.put("deleted", true);
        return result;
    }

    @Override
    public JSONObject suggestByBizNm(Map<String, Object> param) throws Exception {
        String reportBizNm = String.valueOf(param.get("reportBizNm"));
        if (BcjisCommUtil.isNullString(reportBizNm) || "null".equals(reportBizNm)) {
            throw new IllegalArgumentException("조서 사업명이 필요합니다.");
        }
        normalizeOfficeScope(param);
        if (BcjisCommUtil.isNullString(param.get("officeCd"))) {
            throw new IllegalArgumentException("실국(또는 전체) 조회조건이 필요합니다.");
        }

        // 이미 매칭된 경우: 후보 전체 스캔 생략 → 즉시 요약 조회
        @SuppressWarnings("rawtypes")
        Map match = bizDescMatchDAO.selectMatch(param);
        if (match != null) {
            JSONObject result = new JSONObject();
            result.put("suggestList", new JSONArray());
            result.put("matched", true);
            result.put("match", JSONObject.fromObject(match));
            return result;
        }

        // 회계년도+예산차수+실국 파일(경상·투자 report_cd 무시)을 후보로 검색
        @SuppressWarnings("rawtypes")
        List files = bizDescMatchDAO.selectFileList(param);
        BizDescMatcher matcher = new BizDescMatcher();
        JSONArray suggestList = new JSONArray();
        if (files != null) {
            for (Object o : files) {
                @SuppressWarnings("rawtypes")
                Map f = (Map) o;
                String fileId = str(f, "bizdescFileId", "bizdesc_file_id");
                List<BizBlock> businesses = loadBusinessesCached(f);
                List<NameSuggest> suggests = matcher.suggestByReportName(
                        reportBizNm, businesses, fileId, BizDescMatcher.NAME_MATCH_THRESHOLD);
                for (NameSuggest s : suggests) {
                    JSONObject item = new JSONObject();
                    item.put("bizdescFileId", s.bizdescFileId);
                    item.put("bizSeq", s.bizSeq);
                    item.put("bizNm", s.bizNm);
                    item.put("detailBiz", s.detailBiz);
                    item.put("indivBiz", s.indivBiz);
                    item.put("deptNm", s.deptNm);
                    item.put("score", s.score);
                    item.put("scorePct", Math.round(s.score * 100));
                    item.put("orgFileNm", str(f, "orgFileNm", "org_file_nm"));
                    suggestList.add(item);
                }
            }
        }

        // score desc already per-file; re-sort all
        List<JSONObject> sorted = new ArrayList<JSONObject>();
        for (int i = 0; i < suggestList.size(); i++) {
            sorted.add(suggestList.getJSONObject(i));
        }
        java.util.Collections.sort(sorted, new java.util.Comparator<JSONObject>() {
            public int compare(JSONObject a, JSONObject b) {
                return Double.compare(b.optDouble("score", 0), a.optDouble("score", 0));
            }
        });
        JSONArray out = new JSONArray();
        out.addAll(sorted);

        // 유사도 100%면 자동 매칭 저장 후 요약으로 진입
        if (!sorted.isEmpty()) {
            JSONObject top = sorted.get(0);
            int pct = top.optInt("scorePct", (int) Math.round(top.optDouble("score", 0) * 100));
            if (pct >= 100) {
                Map<String, Object> saveParam = new HashMap<String, Object>();
                saveParam.putAll(param);
                saveParam.put("bizdescFileId", top.opt("bizdescFileId"));
                saveParam.put("bizSeq", top.opt("bizSeq"));
                saveParam.put("bizNm", top.optString("bizNm", top.optString("indivBiz", "")));
                saveParam.put("deptNm", top.optString("deptNm", ""));
                saveParam.put("matchScore", top.opt("score"));
                if (BcjisCommUtil.isNullString(saveParam.get("regiId"))) {
                    saveParam.put("regiId", param.get("userId"));
                }
                saveMatch(saveParam);
                JSONObject result = new JSONObject();
                result.put("suggestList", new JSONArray());
                result.put("matched", true);
                result.put("autoMatched", true);
                @SuppressWarnings("rawtypes")
                Map saved = bizDescMatchDAO.selectMatch(param);
                if (saved != null) {
                    result.put("match", JSONObject.fromObject(saved));
                }
                return result;
            }
        }

        JSONObject result = new JSONObject();
        result.put("suggestList", out);
        result.put("matched", false);
        return result;
    }

    @Override
    public JSONObject saveMatch(Map<String, Object> param) throws Exception {
        @SuppressWarnings("rawtypes")
        Map exist = bizDescMatchDAO.selectMatch(param);
        if (exist == null) {
            bizDescMatchDAO.insertMatch(param);
        } else {
            bizDescMatchDAO.updateMatch(param);
        }
        JSONObject result = new JSONObject();
        result.put("saved", true);
        return result;
    }

    @Override
    public JSONObject clearMatch(Map<String, Object> param) throws Exception {
        bizDescMatchDAO.deleteMatch(param);
        JSONObject result = new JSONObject();
        result.put("cleared", true);
        return result;
    }

    @Override
    public JSONObject getSummary(Map<String, Object> param) throws Exception {
        @SuppressWarnings("rawtypes")
        Map match = bizDescMatchDAO.selectMatch(param);
        if (match == null) {
            throw new IllegalArgumentException("매칭된 사업설명서가 없습니다.");
        }
        Map<String, Object> q = new HashMap<String, Object>();
        q.put("bizdescFileId", str(match, "bizdescFileId", "bizdesc_file_id"));
        @SuppressWarnings("rawtypes")
        Map file = bizDescMatchDAO.selectFile(q);
        if (file == null) {
            throw new IllegalArgumentException("연결된 사업설명서 파일이 없습니다.");
        }
        // 요약: 업로드 시 저장된 JSON 사용(원본 전체 재파싱은 클릭 지연 원인)
        List<BizBlock> businesses = loadBusinessesCached(file);
        int bizSeq = toInt(match.get("bizSeq") != null ? match.get("bizSeq") : match.get("biz_seq"));
        BizBlock biz = null;
        for (BizBlock b : businesses) {
            if (b.seq == bizSeq) {
                biz = b;
                break;
            }
        }
        if (biz == null && !businesses.isEmpty()) {
            // seq 불일치 시 사업명으로 보정
            String matchedNm = str(match, "bizNm", "biz_nm");
            for (BizBlock b : businesses) {
                if (matchedNm != null && matchedNm.length() > 0
                        && (matchedNm.equals(b.bizNm()) || matchedNm.equals(b.indivBiz) || matchedNm.equals(b.detailBiz))) {
                    biz = b;
                    break;
                }
            }
        }
        if (biz == null) {
            throw new IllegalArgumentException("사업설명서에서 해당 사업을 찾지 못했습니다.");
        }

        // 화면 포맷은 항상 최신 규칙으로 재구성 (기존 JSON blocks 무시)
        biz.blocks = biz.buildBlocksFromLines();
        JSONObject result = new JSONObject();
        result.put("match", JSONObject.fromObject(match));
        result.put("orgFileNm", str(file, "orgFileNm", "org_file_nm"));
        result.put("bizNm", biz.bizNm());
        result.put("deptNm", biz.dept);
        result.put("summaryRows", JSONArray.fromObject(biz.summaryRows()));
        result.put("blocks", BizDescMatcher.toJson(biz).getJSONArray("blocks"));
        result.put("demandContDraft", biz.buildDemandCont());
        result.put("progressContDraft", biz.buildProgressCont());
        result.put("requestContDraft", biz.buildRequestCont());
        result.put("examContDraft", biz.buildExamCont());
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public JSONObject buildExportJson(Map<String, Object> param) throws Exception {
        if (BcjisCommUtil.isNullString(param.get("fisYear"))
                || BcjisCommUtil.isNullString(param.get("bgtDgr"))) {
            throw new IllegalArgumentException("회계년도·예산차수가 필요합니다.");
        }
        normalizeOfficeScope(param);
        if (BcjisCommUtil.isNullString(param.get("officeCd"))) {
            throw new IllegalArgumentException("실국(또는 전체) 조회조건이 필요합니다.");
        }

        List fileList = bizDescMatchDAO.selectFileList(param);
        if (fileList == null || fileList.isEmpty()) {
            throw new IllegalArgumentException(
                    "내보낼 사업설명서가 없습니다.\n먼저 HWPX/HWP 파일을 업로드해 주세요.");
        }

        List<String> selectedIds = parseBizdescFileIds(param.get("bizdescFileIds"));
        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException("보낼 파일을 목록에서 선택해 주세요.");
        }
        List filtered = new ArrayList();
        for (int i = 0; i < fileList.size(); i++) {
            Map file = (Map) fileList.get(i);
            String id = str(file, "bizdescFileId", "bizdesc_file_id");
            if (selectedIds.contains(id)) {
                filtered.add(file);
            }
        }
        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("선택한 사업설명서 파일이 없습니다.");
        }
        fileList = filtered;

        JSONArray filesArr = new JSONArray();
        int totalBiz = 0;
        for (int i = 0; i < fileList.size(); i++) {
            Map file = (Map) fileList.get(i);
            List<BizBlock> businesses = loadBusinessesCached(file);
            if (businesses == null) {
                businesses = new ArrayList<BizBlock>();
            }
            totalBiz += businesses.size();

            JSONObject one = new JSONObject();
            one.put("bizdescFileId", str(file, "bizdescFileId", "bizdesc_file_id"));
            one.put("orgFileNm", str(file, "orgFileNm", "org_file_nm"));
            one.put("reportCd", str(file, "reportCd", "report_cd"));
            one.put("bizCount", Integer.valueOf(businesses.size()));
            one.put("businesses", BizDescMatcher.toJsonArray(businesses));
            filesArr.add(one);
        }

        JSONObject root = new JSONObject();
        root.put("exportType", "bizdesc");
        root.put("fisYear", String.valueOf(param.get("fisYear")));
        root.put("bgtDgr", String.valueOf(param.get("bgtDgr")));
        root.put("officeCd", String.valueOf(param.get("officeCd")));
        root.put("officeNm", param.get("officeNm") == null ? "" : String.valueOf(param.get("officeNm")));
        root.put("reportCd", param.get("reportCd") == null ? "" : String.valueOf(param.get("reportCd")));
        root.put("exportedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        root.put("fileCount", Integer.valueOf(filesArr.size()));
        root.put("bizCount", Integer.valueOf(totalBiz));
        root.put("files", filesArr);
        return root;
    }

    @Override
    public String buildExportFileName(Map<String, Object> param) throws Exception {
        String year = param.get("fisYear") == null ? "" : String.valueOf(param.get("fisYear")).trim();
        String dgr = param.get("bgtDgr") == null ? "" : String.valueOf(param.get("bgtDgr")).trim();
        String officeNm = param.get("officeNm") == null ? "" : String.valueOf(param.get("officeNm")).trim();
        String officeCd = param.get("officeCd") == null ? "" : String.valueOf(param.get("officeCd")).trim();
        String label = officeNm.length() > 0 ? officeNm : officeCd;
        label = label.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "");
        if (label.length() == 0) {
            label = "office";
        }
        return "사업설명서_" + year + "_" + dgr + "차_" + label + ".json";
    }

    /**
     * 조회조건 실국 '전체' → OFFICE_CD_ALL 로 통일.
     * 특정 실국 조회 시에는 해당 코드 + ALL 공유 파일을 SQL에서 함께 조회한다.
     */
    private void normalizeOfficeScope(Map<String, Object> param) {
        if (param == null) {
            return;
        }
        String officeCd = param.get("officeCd") == null ? "" : String.valueOf(param.get("officeCd")).trim();
        String officeNm = param.get("officeNm") == null ? "" : String.valueOf(param.get("officeNm")).trim();
        if ("null".equalsIgnoreCase(officeCd) || "undefined".equalsIgnoreCase(officeCd)) {
            officeCd = "";
        }
        if (BcjisCommUtil.isNullString(officeCd) || OFFICE_CD_ALL.equalsIgnoreCase(officeCd)
                || "전체".equals(officeNm)) {
            param.put("officeCd", OFFICE_CD_ALL);
            if (BcjisCommUtil.isNullString(officeNm) || "전체".equals(officeNm)) {
                param.put("officeNm", "전체");
            }
        }
    }

    private File getBizDescDir() {
        String storeRoot = config.getProperty("Globals.fileStorePath");
        if (BcjisCommUtil.isNullString(storeRoot)) {
            storeRoot = "C:/bcjis/upload/";
        }
        File dir = new File(BcjisWebUtil.filePathBlackList(storeRoot), "bizdesc");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private byte[] readHead(MultipartFile mFile, int len) throws Exception {
        byte[] head = new byte[len];
        InputStream in = mFile.getInputStream();
        try {
            int off = 0;
            while (off < len) {
                int n = in.read(head, off, len - off);
                if (n < 0) {
                    break;
                }
                off += n;
            }
            if (off < len) {
                byte[] trimmed = new byte[off];
                System.arraycopy(head, 0, trimmed, 0, off);
                return trimmed;
            }
            return head;
        } finally {
            in.close();
        }
    }

    /** HWPX / HWP(OLE) / DRM / UNKNOWN */
    private String detectFileKind(byte[] head) {
        if (head == null || head.length < 2) {
            return "UNKNOWN";
        }
        // ZIP = HWPX
        if (head[0] == 'P' && head[1] == 'K') {
            return "HWPX";
        }
        // OLE Compound Document = 구형 HWP
        if (head.length >= 4
                && (head[0] & 0xFF) == 0xD0 && (head[1] & 0xFF) == 0xCF
                && (head[2] & 0xFF) == 0x11 && (head[3] & 0xFF) == 0xE0) {
            return "HWP";
        }
        // DRMONE 암호화 문서
        String ascii = new String(head, Charset.forName("ISO-8859-1"));
        if (ascii.indexOf("DRMONE") >= 0 || ascii.toUpperCase().indexOf("DRM") == 0) {
            return "DRM";
        }
        return "UNKNOWN";
    }

    private String buildBadFileMessage(String kind, String orgName) {
        String nameHint = (orgName != null && orgName.length() > 0) ? ("\n선택 파일: " + orgName) : "";
        if ("DRM".equals(kind)) {
            return "암호화(보안)된 문서입니다.\n"
                    + "시 보안솔루션으로 암호/보안을 해제한 뒤 다시 업로드해 주세요.\n"
                    + "(보안 해제 후 확장자가 .hwpx여도 내부가 구형 HWP이면 그대로 업로드 가능합니다.)"
                    + nameHint;
        }
        return "사업설명서 파일 형식을 인식하지 못했습니다. (감지: " + kind + ")\n"
                + "보안 해제된 .hwpx / .hwp 파일을 올려 주세요.\n"
                + "한컴에서 ‘한글 문서(*.hwpx)’로 저장한 파일도 가능합니다."
                + nameHint;
    }

    @SuppressWarnings("rawtypes")
    private List<BizBlock> loadBusinesses(Map file) throws Exception {
        String jsonPath = str(file, "parseJsonPath", "parse_json_path");
        if (!BcjisCommUtil.isNullString(jsonPath)) {
            File jf = new File(BcjisWebUtil.filePathBlackList(jsonPath));
            if (jf.exists()) {
                String json = readFileUtf8(jf);
                return BizDescMatcher.fromJsonArray(JSONArray.fromObject(json));
            }
        }
        return parseBusinessesFromSource(file);
    }

    /** JSON 로드 + 메모리 캐시 (동일 파일 반복 클릭 가속) */
    @SuppressWarnings("rawtypes")
    private List<BizBlock> loadBusinessesCached(Map file) throws Exception {
        String fileId = str(file, "bizdescFileId", "bizdesc_file_id");
        String jsonPath = str(file, "parseJsonPath", "parse_json_path");
        long modi = 0L;
        if (!BcjisCommUtil.isNullString(jsonPath)) {
            File jf = new File(BcjisWebUtil.filePathBlackList(jsonPath));
            if (jf.exists()) {
                modi = jf.lastModified();
            }
        }
        if (fileId.length() > 0) {
            CachedBizList cached = BIZ_LIST_CACHE.get(fileId);
            if (cached != null && cached.jsonLastModified == modi && cached.list != null) {
                return cached.list;
            }
        }
        List<BizBlock> list = loadBusinesses(file);
        if (fileId.length() > 0 && list != null) {
            putBizListCache(fileId, modi, list);
        }
        return list;
    }

    private void putBizListCache(String fileId, long jsonLastModified, List<BizBlock> list) {
        if (fileId == null || fileId.length() == 0 || list == null) {
            return;
        }
        BIZ_LIST_CACHE.put(fileId, new CachedBizList(jsonLastModified, list));
        // 캐시 폭주 방지
        if (BIZ_LIST_CACHE.size() > 40) {
            Iterator<String> it = BIZ_LIST_CACHE.keySet().iterator();
            int remove = BIZ_LIST_CACHE.size() - 30;
            while (it.hasNext() && remove > 0) {
                it.next();
                it.remove();
                remove--;
            }
        }
    }

    private void invalidateBizListCache(String fileId) {
        if (fileId != null && fileId.length() > 0) {
            BIZ_LIST_CACHE.remove(fileId);
        }
    }

    /** 요약/표시용: 원본 파일 재파싱 → 실패 시 JSON */
    @SuppressWarnings("rawtypes")
    private List<BizBlock> loadBusinessesPreferSource(Map file) throws Exception {
        try {
            List<BizBlock> fromSrc = parseBusinessesFromSource(file);
            if (fromSrc != null && !fromSrc.isEmpty()) {
                return fromSrc;
            }
        } catch (Exception e) {
            logger.warn("bizdesc re-parse failed, fallback to json: " + e.getMessage());
        }
        return loadBusinesses(file);
    }

    @SuppressWarnings("rawtypes")
    private List<BizBlock> parseBusinessesFromSource(Map file) throws Exception {
        String cours = str(file, "fileStreCours", "file_stre_cours");
        String stre = str(file, "streFileNm", "stre_file_nm");
        if (BcjisCommUtil.isNullString(cours) || BcjisCommUtil.isNullString(stre)) {
            throw new IllegalArgumentException("사업설명서 원본 경로가 없습니다.");
        }
        File saved = new File(cours, stre);
        if (!saved.exists()) {
            throw new IllegalArgumentException("사업설명서 원본 파일이 없습니다.");
        }
        String lower = stre.toLowerCase();
        if (lower.endsWith(".hwp") || isOleFile(saved)) {
            List<String> texts = new HwpOleTextExtractor().extract(saved);
            return new HwpxBizDescParser().parseFromTexts(texts);
        }
        InputStream in = new FileInputStream(saved);
        try {
            return new HwpxBizDescParser().parse(in);
        } finally {
            in.close();
        }
    }

    private boolean isOleFile(File f) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] head = new byte[4];
            int n = in.read(head);
            return n >= 4
                    && (head[0] & 0xFF) == 0xD0 && (head[1] & 0xFF) == 0xCF
                    && (head[2] & 0xFF) == 0x11 && (head[3] & 0xFF) == 0xE0;
        } catch (Exception e) {
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    private void writeJson(File file, JSONArray arr) throws Exception {
        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
        try {
            w.write(arr.toString());
        } finally {
            w.close();
        }
    }

    private String readFileUtf8(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buf = new byte[(int) file.length()];
            int off = 0;
            while (off < buf.length) {
                int n = in.read(buf, off, buf.length - off);
                if (n < 0) {
                    break;
                }
                off += n;
            }
            return new String(buf, 0, off, Charset.forName("UTF-8"));
        } finally {
            in.close();
        }
    }

    private void deletePhysical(String cours, String stre) {
        if (BcjisCommUtil.isNullString(cours) || BcjisCommUtil.isNullString(stre)) {
            return;
        }
        try {
            File f = new File(BcjisWebUtil.filePathBlackList(cours), BcjisWebUtil.filePathBlackList(stre));
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            logger.warn("deletePhysical: " + e.getMessage());
        }
    }

    private void deletePhysicalPath(String path) {
        if (BcjisCommUtil.isNullString(path)) {
            return;
        }
        try {
            File f = new File(BcjisWebUtil.filePathBlackList(path));
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            logger.warn("deletePhysicalPath: " + e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private List<String> parseBizdescFileIds(Object raw) {
        List<String> ids = new ArrayList<String>();
        if (raw == null) {
            return ids;
        }
        if (raw instanceof JSONArray) {
            JSONArray arr = (JSONArray) raw;
            for (int i = 0; i < arr.size(); i++) {
                String id = String.valueOf(arr.get(i)).trim();
                if (id.length() > 0 && !"null".equals(id)) {
                    ids.add(id);
                }
            }
        } else {
            String id = String.valueOf(raw).trim();
            if (id.length() > 0 && !"null".equals(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private String str(Map row, String camel, String snake) {
        Object v = row.get(camel);
        if (v == null) {
            v = row.get(snake);
        }
        if (v == null) {
            v = row.get(snake.toUpperCase());
        }
        return v == null ? "" : String.valueOf(v).trim();
    }

    private int toInt(Object v) {
        if (v == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return 0;
        }
    }
}

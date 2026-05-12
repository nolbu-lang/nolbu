package com.cs.bcjis.comm.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cs.bcjis.comm.AjaxJsonView;
import com.cs.bcjis.comm.BcjisMessageSource;
import com.cs.bcjis.comm.BcjisUserDetailsHelper;
import com.cs.bcjis.comm.service.BcjisCommService;
import com.cs.bcjis.comm.service.BcjisFileMngService;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.util.BcjisFileMngUtil;
import com.cs.bcjis.comm.util.BcjisWebUtil;
import com.cs.bcjis.report.util.ReportSaveUtil;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;

@Controller
public class CommController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(CommController.class);

    @Resource(name = "bcjisCommService")
    private BcjisCommService bcjisCommService;

    @Resource(name = "bcjisFileMngService")
    private BcjisFileMngService bcjisFileMngService;

    @Resource(name = "bcjisMessageSource")
    BcjisMessageSource bcjisMessageSource;



    @RequestMapping(value = "/comm/ajaxCommSessionExpired.do")
    public ModelAndView ajaxCommSessionExpired(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommSessionExpired(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
        jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.session.expire"));
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommSessionExpired(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    
    @RequestMapping(value = "/comm/ajaxCommLeftMenuTreeList.do")
    public ModelAndView ajaxCommLeftMenuList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommLeftMenuList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            jsonParam.put("userId", bcjisUserVO.getUserId());
            JSONArray resultList = JSONArray.fromObject(bcjisCommService.selectLeftMenuList(jsonParam));

            jsonObject.put("dataList", resultList);
            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);

        } catch (Exception e) {
            logger.error("ajaxCommLeftMenuList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_ERR);
            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }

        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommLeftMenuList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/comm/ajaxCommComboList.do")
    public ModelAndView ajaxCommComboList(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommComboList(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);
            
            BcjisUserVO bcjisUserVO = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();
            
            List paramlist = jsonParam.getJSONArray("comboParam");

            JSONObject tempParam = null;
            for (int i = 0; i < paramlist.size(); i++) {
                tempParam = (JSONObject) paramlist.get(i);
                if("OfficeCd".equals(tempParam.get("subQueryId"))){
                    tempParam.put("userId", bcjisUserVO.getUserId());
                    if(BcjisCommUtil.isNullString(tempParam.get("userDeptYn")) == true){
                        tempParam.put("userDeptYn", bcjisCommService.getDeptUserYn(bcjisUserVO, ReportSaveUtil.getStringValue(tempParam.get("reportCd"))));
                    }
                }
                
                jsonObject.put(tempParam.get("id"), JSONArray.fromObject(bcjisCommService.selectCommComboList(tempParam)));
            }

            ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        } catch (Exception e) {
            logger.error("ajaxCommComboList(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
            ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxCommComboList(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;

    }

    @RequestMapping(value = "/comm/excelFileDownload.do")
    public void excelFileDownload(Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("excelFileDownload(Map, HttpServletRequest, HttpServletResponse) - start");
        }

        String fileName = BcjisCommUtil.getStringParameterEncode(request, "fileName");
        String realFileName = BcjisCommUtil.getStringParameterEncode(request, "realFileName");
        String fileDeleteYn = BcjisCommUtil.getStringParameter(request, "fileDeleteYn");

        if (BcjisCommUtil.isNullString(realFileName) == true) {
            if (logger.isDebugEnabled()) {
                logger.debug("excelFileDownload(Map, HttpServletRequest, HttpServletResponse) - end");
            }
            return;
        }

        if (BcjisCommUtil.isNullString(fileName) == true) {
            fileName = "예산편성심사정보시스템.xls";
        }

        if (BcjisCommUtil.isNullString(fileDeleteYn) == true) {
            fileDeleteYn = "Y";
        }

        File uFile = new File(realFileName);

        fileWrite(request, response, uFile, fileName, fileDeleteYn);

        if (logger.isDebugEnabled()) {
            logger.debug("excelFileDownload(Map, HttpServletRequest, HttpServletResponse) - end");
        }
    }

    @RequestMapping(value = "/comm/attchFileDown.do")
    public void cvplFileDownload(Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("cvplFileDownload(Map, HttpServletRequest, HttpServletResponse) - start");
        }

        String atchFileId = BcjisCommUtil.getStringParameter(request, "atchFileId");
        String fileSn = BcjisCommUtil.getStringParameter(request, "fileSn");

        BcjisFileVO bcjisFileVO = new BcjisFileVO();
        bcjisFileVO.setAtchFileId(atchFileId);
        bcjisFileVO.setFileSn(fileSn);
        BcjisFileVO fvo = bcjisFileMngService.selectFileInf(bcjisFileVO);

        File uFile = new File(fvo.getFileStreCours(), fvo.getStreFileNm());
        fileWrite(request, response, uFile, fvo.getOrignlFileNm(), "N");

        if (logger.isDebugEnabled()) {
            logger.debug("cvplFileDownload(Map, HttpServletRequest, HttpServletResponse) - end");
        }
    }

    @SuppressWarnings("rawtypes")
    public JSONArray getChildArray(String upTreeId, Map treeIdMap, Map treeMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("getChildArray(String, Map, Map) - start");
        }

        JSONArray rtnArray = new JSONArray();

        List list = (List) treeIdMap.get(upTreeId);
        if (list == null || list.size() < 1) {
            if (logger.isDebugEnabled()) {
                logger.debug("getChildArray(String, Map, Map) - end");
            }
            return rtnArray;
        }

        JSONObject treeObject = null;
        JSONObject attrObj = null;

        JSONArray childArray = null;
        String treeId = null;
        Map dataMap = null;
        for (int i = 0; i < list.size(); i++) {
            treeId = String.valueOf(list.get(i));
            dataMap = (Map) treeMap.get(treeId);

            if (dataMap != null) {
                treeObject = new JSONObject();
                treeObject.put("data", dataMap.get("menuNm"));

                attrObj = new JSONObject();
                attrObj.put("id", treeId);
                attrObj.put("url", dataMap.get("url"));
                attrObj.put("upMenuCd", dataMap.get("upMenuCd"));

                treeObject.put("attr", attrObj);
                childArray = getChildArray(treeId, treeIdMap, treeMap);
                if (childArray != null && childArray.size() > 0) {
                    treeObject.put("state", "close");
                    treeObject.put("children", childArray);
                } else {
                    treeObject.put("state", "leaf");
                }

                rtnArray.add(treeObject);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getChildArray(String, Map, Map) - end");
        }
        return rtnArray;
    }

    private void setDisposition(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("setDisposition(String, HttpServletRequest, HttpServletResponse) - start");
        }

        String browser = BcjisWebUtil.getBrowser(request);

        String dispositionPrefix = "attachment; filename=";
        String encodedFilename = null;

        if (browser.equals("MSIE")) {
            encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.equals("Firefox")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Opera")) {
            encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.equals("Chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < filename.length(); i++) {
                char c = filename.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else {
            throw new IOException("Not supported browser");
        }

        response.setHeader("Content-Disposition", dispositionPrefix + encodedFilename);

        if ("Opera".equals(browser)) {
            response.setContentType("application/octet-stream;charset=UTF-8");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("setDisposition(String, HttpServletRequest, HttpServletResponse) - end");
        }
    }

    public void fileWrite(HttpServletRequest request, HttpServletResponse response, File uFile, String fileNm, String fileDeleteYn) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("fileWrite(HttpServletRequest, HttpServletResponse, File, String, String) - start");
        }

        int fSize = (int) uFile.length();

        if (fSize > 0) {
            String mimetype = "application/x-msdownload";

            response.setContentType(mimetype);

            setDisposition(fileNm, request, response);
            response.setContentLength(fSize);

            BufferedInputStream in = null;
            BufferedOutputStream out = null;

            try {
                in = new BufferedInputStream(new FileInputStream(uFile));
                out = new BufferedOutputStream(response.getOutputStream());

                FileCopyUtils.copy(in, out);
                out.flush();
            } catch (Exception ex) {
                logger.error("fileWrite(HttpServletRequest, HttpServletResponse, File, String, String)", ex);

            } finally {
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                } catch (Exception ignore) {
                    logger.error("fileWrite(HttpServletRequest, HttpServletResponse, File, String, String)", ignore);
                    
                }
            }

        } else {
            response.setContentType("application/x-msdownload");

            PrintWriter printwriter = response.getWriter();
            printwriter.println("<html>");
            printwriter.println("<br><br><br><h2>Could not get file name:<br>" + fileNm + "</h2>");
            printwriter.println("<br><br><br><center><h3><a href='javascript: history.go(-1)'>Back</a></h3></center>");
            printwriter.println("<br><br><br>&copy; webAccess");
            printwriter.println("</html>");
            printwriter.flush();
            printwriter.close();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("fileWrite(HttpServletRequest, HttpServletResponse, File, String, String) - end");
        }
    }
    
    @RequestMapping(value = "/comm/ajaxSelectIsCloseYn.do")
    public ModelAndView ajaxSelectIsCloseYn(ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("ajaxSelectIsCloseYn(ModelMap, HttpServletRequest) - start");
        }

        ModelAndView ajaxModel = new ModelAndView(new AjaxJsonView());
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonParam = BcjisCommUtil.getJsonObjectFromRequest(request);

            JSONObject data = JSONObject.fromObject(bcjisCommService.selectIsCloseYn(jsonParam));

            jsonObject.put("data", data);

            jsonObject.put(BcjisCommUtil.BCJIS_RETURN_CODE, BcjisCommUtil.BCJIS_RETURN_CODE_SUCC);
        } catch (Exception e) {
            logger.error("ajaxSelectIsCloseYn(ModelMap, HttpServletRequest)", e);

            jsonObject.put(BcjisCommUtil.BCJIS_MESSAGE, bcjisMessageSource.getMessage("fail.common.select"));
        }
        
        ajaxModel.addObject(BcjisCommUtil.JSON_OBJCT_NM, jsonObject);

        if (logger.isDebugEnabled()) {
            logger.debug("ajaxSelectIsCloseYn(ModelMap, HttpServletRequest) - end");
        }
        return ajaxModel;
    }

    /**
     * 첨부파일 다운로드
     * 
     * @param Map
     *            <String, String> commandMap, ModelMap model,
     *            HttpServletRequest request
     * @return ModelAndView Json object
     * @throws Exception
     */
    @RequestMapping(value = "/comm/fileDown.do")
    public void fileDown(Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("fileDown(Map, HttpServletRequest, HttpServletResponse) - start");
        }

        String atchFileId = BcjisCommUtil.getStringParameter(request, "atchFileId");
        String fileSn = BcjisCommUtil.getStringParameter(request, "fileSn");

        BcjisFileVO bcjisFileVO = new BcjisFileVO();
        bcjisFileVO.setAtchFileId(atchFileId);
        if ("0".equals(fileSn) == false) {
            bcjisFileVO.setFileSn(fileSn);
        }

        List<BcjisFileVO> csFiles = bcjisFileMngService.selectFileInfs(bcjisFileVO);

        File uFile = null;
        String orignlFileNm = "";
        BcjisFileVO csFileTemp = null;
        if (csFiles.size() == 1) {
            csFileTemp = csFiles.get(0);

            orignlFileNm = csFileTemp.getOrignlFileNm();
            uFile = new File(csFileTemp.getFileStreCours(), csFileTemp.getStreFileNm());
        } else {

            String fileName = BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS") + UUID.randomUUID();

            FileInputStream fis = null;
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            ZipEntry zipEntry = null;

            csFileTemp = null;
            int cnt = 0;
            byte buffer[] = new byte[4096];
            try {
                for (int i = 0; i < csFiles.size(); i++) {
                    csFileTemp = csFiles.get(i);
                    if (i == 0) {
                        fileName = BcjisFileMngUtil.createNewFile(csFileTemp.getFileStreCours() + File.separator + BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS") + UUID.randomUUID());

                        uFile = new File(fileName);
                        fos = new FileOutputStream(uFile);
                        zos = new ZipOutputStream(fos);
                        zos.setEncoding("UTF-8");

                        orignlFileNm = csFileTemp.getOrignlFileNm() + "외" + (csFiles.size() - 1) + ".zip";
                    }

                    zipEntry = new ZipEntry(csFileTemp.getOrignlFileNm());
                    zos.putNextEntry(zipEntry);
                    fis = new FileInputStream(csFileTemp.getFileStreCours() + File.separator + csFileTemp.getStreFileNm());

                    cnt = 0;
                    while ((cnt = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, cnt);
                    }
                    fis.close();
                }

                if (zos != null) {
                    zos.closeEntry();
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }

                if (zos != null) {
                    zos.close();
                }

                if (fos != null) {
                    fos.close();
                }
            }

        }

        fileWrite(request, response, uFile, orignlFileNm, "N");

        if (logger.isDebugEnabled()) {
            logger.debug("fileDown(Map, HttpServletRequest, HttpServletResponse) - end");
        }
    }
}

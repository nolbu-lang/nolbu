package com.cs.bcjis.comm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cs.bcjis.comm.web.BcjisFileVO;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Component("bcjisFileMngUtil")
public class BcjisFileMngUtil {

    public static final int BUFF_SIZE = 2048;

    @Resource(name = "csFileIdGnrService")
    private EgovIdGnrService csFileIdGnrService;

    @Autowired
    @Qualifier("config")
    private Properties config;

    public List<BcjisFileVO> uploadFiles(HttpServletRequest request, String KeyStr, int fileKeyParam, String atchFileId, String storePath) throws Exception {
        int fileKey = fileKeyParam;

        String storePathString = "";
        String atchFileIdString = "";

        if (BcjisCommUtil.isNullString(storePath) == true) {
            storePathString = config.getProperty("Globals.fileStorePath");
        } else {
            storePathString = config.getProperty(storePath);
        }

        if (BcjisCommUtil.isNullString(atchFileId) == true) {
            atchFileIdString = csFileIdGnrService.getNextStringId();
        } else {
            atchFileIdString = atchFileId;
        }

        File saveFolder = new File(BcjisWebUtil.filePathBlackList(storePathString));

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        MultipartHttpServletRequest mptRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileIter = mptRequest.getFileNames();

        MultipartFile mFile = null;
        String filePath = "";
        String orginFileName = "";
        String fileExt = "";
        String newName = "";
        List<BcjisFileVO> result = new ArrayList<BcjisFileVO>();
        BcjisFileVO fvo;
        while (fileIter.hasNext()) {
            mFile = mptRequest.getFile(String.valueOf(fileIter.next()));
            orginFileName = BcjisCommUtil.getAttachFileName(mFile.getOriginalFilename());

            if (BcjisCommUtil.isNullString(orginFileName)) {
                continue;
            }

            fileExt = orginFileName.substring(orginFileName.lastIndexOf(".") + 1);
            newName = KeyStr + BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS") + fileKey;

            if (BcjisCommUtil.isNullString(orginFileName) == false) {
                filePath = storePathString + File.separator + newName;
                mFile.transferTo(new File(BcjisWebUtil.filePathBlackList(filePath)));
            }

            fvo = new BcjisFileVO();
            fvo.setFileExtsn(fileExt);
            fvo.setFileStreCours(storePathString);
            fvo.setFileMg(Long.toString(mFile.getSize()));
            fvo.setOrignlFileNm(orginFileName);
            fvo.setStreFileNm(newName);
            fvo.setAtchFileId(atchFileIdString);
            fvo.setFileSn(String.valueOf(fileKey));

            writeFile(mFile, newName, storePathString);
            result.add(fvo);

            fileKey++;
        }

        return result;
    }

    protected static void writeFile(MultipartFile file, String newName, String stordFilePath) throws Exception {
        InputStream stream = null;
        OutputStream bos = null;

        try {
            stream = file.getInputStream();
            File cFile = new File(BcjisWebUtil.filePathBlackList(stordFilePath));

            if (!cFile.isDirectory())
                cFile.mkdir();

            bos = new FileOutputStream(BcjisWebUtil.filePathBlackList(stordFilePath + File.separator + newName));

            int bytesRead = 0;
            byte[] buffer = new byte[BUFF_SIZE];

            while ((bytesRead = stream.read(buffer, 0, BUFF_SIZE)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            Logger.getLogger(BcjisFileMngUtil.class).debug("IGNORED: " + e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception ignore) {
                    Logger.getLogger(BcjisFileMngUtil.class).debug("IGNORED: " + ignore.getMessage());
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignore) {
                    Logger.getLogger(BcjisFileMngUtil.class).debug("IGNORED: " + ignore.getMessage());
                }
            }
        }
    }

    /**
     * 파일 저장
     * 
     * @param MultipartFile
     * @param String 파일의 절대경로 +파일명
     * @return String
     * @throws
     */
    public static String createNewFile(String filePath) {

        // 인자값 유효하지 않은 경우 블랭크 리턴
        if (filePath == null || filePath.equals("")) {
            return "";
        }

        File file = new File(BcjisWebUtil.filePathBlackList(filePath));
        String result = "";
        try {
            if (file.exists()) {
                result = filePath;
            } else {
                // 존재하지 않으면 생성함
                new File(file.getParent()).mkdirs();
                if (file.createNewFile()) {
                    result = file.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}

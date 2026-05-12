package com.cs.bcjis.report.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.util.BcjisWebUtil;
import com.cs.bcjis.report.service.impl.ReportCommDAO;

@Component("report0C0SaveFile")
public class Report0C0SaveFile {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Resource(name = "reportCommDAO")
    private ReportCommDAO reportCommDAO;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void buildHwpDocument(Map model, String KeyStr, String storePath) throws Exception {

        String realFileName = "";
        FileOutputStream fos = null;
        try {
            String storePathString = ReportSaveUtil.getStorePathString(config, storePath, KeyStr);
            File saveFolder = new File(BcjisWebUtil.filePathBlackList(storePathString));

            if (!saveFolder.exists() || saveFolder.isFile()) {
                saveFolder.mkdirs();
            }

            realFileName = new StringBuilder().append(storePathString).append(File.separator).append(BcjisCommUtil.getCurrentDate("yyyyMMddhhmmssSSS")).append(UUID.randomUUID()).append(".hml").toString();

            fos = new FileOutputStream(realFileName);
            String head = ReportSaveHwpUtil.readSrcFile("report0C0Head.hml").replaceAll(">\\s+<", "><");
            head = head.replace("#date#", BcjisCommUtil.getCurrentDate("yyyy년 MM월 dd일 E요일 a h:mm:ss"));
            fos.write(head.getBytes("UTF-8"));

            List dataList = (List) model.get("dataList");
            JSONObject data = null;
            String bgtDgr = "";
            String reportFg = "";
            String detlCont = "";
            String bizNm = ReportSaveHwpUtil.readSrcFile("report0C0BizNm.hml").replaceAll(">\\s+<", "><");
            String bizSmryTitle = ReportSaveHwpUtil.readSrcFile("report0C0BizSmryTitle.hml").replaceAll(">\\s+<", "><");
            String bizSmryCont = ReportSaveHwpUtil.readSrcFile("report0C0BizSmryCont.hml").replaceAll(">\\s+<", "><");
            String promCorpTitle = ReportSaveHwpUtil.readSrcFile("report0C0PromCorpTitle.hml").replaceAll(">\\s+<", "><");
            String promCorpCont = ReportSaveHwpUtil.readSrcFile("report0C0PromCorpCont.hml").replaceAll(">\\s+<", "><");
            String demandTitle = ReportSaveHwpUtil.readSrcFile("report0C0DemandTitle.hml").replaceAll(">\\s+<", "><");
            String demandCont = ReportSaveHwpUtil.readSrcFile("report0C0DemandCont.hml").replaceAll(">\\s+<", "><");
            String explainCont = ReportSaveHwpUtil.readSrcFile("report0C0ExplainCont.hml").replaceAll(">\\s+<", "><");
            String examTitle = ReportSaveHwpUtil.readSrcFile("report0C0ExamTitle.hml").replaceAll(">\\s+<", "><");
            String examCont = ReportSaveHwpUtil.readSrcFile("report0C0ExamCont.hml").replaceAll(">\\s+<", "><");
            String detl010 = ReportSaveHwpUtil.readSrcFile("report0C0010.hml").replaceAll(">\\s+<", "><");
            String detl010Add = ReportSaveHwpUtil.readSrcFile("report0C0010Add.hml").replaceAll(">\\s+<", "><");
            String detl020 = ReportSaveHwpUtil.readSrcFile("report0C0020.hml").replaceAll(">\\s+<", "><");
            String detl020Add = ReportSaveHwpUtil.readSrcFile("report0C0020Add.hml").replaceAll(">\\s+<", "><");
            
            String pageBreakStr = "";
            while (!dataList.isEmpty()) {
                data = (JSONObject) dataList.remove(0);
                bgtDgr = ReportSaveUtil.getStringValue(data.get("bgtDgr"));
                reportFg = ReportSaveUtil.getStringValue(data.get("reportFg"));
                
                fos.write(bizNm.replace("#PageBreakStr#", pageBreakStr).replace("#bizNm#", ReportSaveUtil.getXmlStringValue(data.get("bizNm"))).getBytes("UTF-8"));

                fos.write(bizSmryTitle.replace("#bizSmryTitle#", ReportSaveUtil.getXmlStringValue(data.get("bizSmryTitle"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getContString(bizSmryCont, "#bizSmryCont#", ReportSaveUtil.getXmlStringValue(data.get("bizSmryCont"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getNewLineString().getBytes("UTF-8"));

                fos.write(promCorpTitle.replace("#promCorpTitle#", ReportSaveUtil.getXmlStringValue(data.get("promCorpTitle"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getContString(promCorpCont, "#promCorpCont#", ReportSaveUtil.getXmlStringValue(data.get("promCorpCont"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getNewLineString().getBytes("UTF-8"));

                fos.write(demandTitle.replace("#demandTitle#", ReportSaveUtil.getXmlStringValue(data.get("demandTitle"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getContString(demandCont, "#demandCont#", ReportSaveUtil.getXmlStringValue(data.get("demandCont"))).getBytes("UTF-8"));

                if ("1".equals(bgtDgr) == true) {
                    if ("010".equals(reportFg) == true) {
                        detlCont = ReportSaveHwpUtil.getReport0C0010(detl010, data);
                    } else {
                        detlCont = ReportSaveHwpUtil.getReport0C0020(detl020, data);
                    }
                } else {
                    if ("010".equals(reportFg) == true) {
                        detlCont = ReportSaveHwpUtil.getReport0C0010Add(detl010Add, data);
                    } else {
                        detlCont = ReportSaveHwpUtil.getReport0C0020Add(detl020Add, data);
                    }
                }

                fos.write(detlCont.getBytes("UTF-8"));

                fos.write(ReportSaveHwpUtil.getContString(explainCont, "#explainCont#", ReportSaveUtil.getXmlStringValue(data.get("explainCont"))).getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getNewLineString().getBytes("UTF-8"));

                fos.write(examTitle.replace("#examTitle#", "검토의견").getBytes("UTF-8"));
                fos.write(ReportSaveHwpUtil.getContString(examCont, "#examCont#", ReportSaveUtil.getXmlStringValue(data.get("examCont"))).getBytes("UTF-8"));
                // fos.write(fileCont.getBytes("UTF-8"));
                
                pageBreakStr = "PageBreak=\"true\"";
            }

            String tail = ReportSaveHwpUtil.readSrcFile("report0C0Tail.hml").replaceAll(">\\s+<", "><");
            fos.write(tail.getBytes("UTF-8"));

        } catch (Exception e) {

            throw e;
        } finally {
            if (fos != null)
                fos.close();
        }

        String fileNm = String.valueOf(model.get("fileNm"));
        if (BcjisCommUtil.isNullString(fileNm) == true) {
            fileNm = config.getProperty("Globals.SystemName");
        }

        model.put("fileName", fileNm + ".hwp");
        model.put("realFileName", realFileName);
    }
}

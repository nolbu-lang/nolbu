package com.cs.bcjis.batch.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.indigo.util.EncryptionUtils;

@Component("batchApi")
public class BatchApi {

	private String apiKey = "6a45633c4f3446ad50f0b9bbacef8fb6424b78288ac988cc9a9874d1d9b7626b";
	private String enKey = "4PTPHNQC8F";
	private String userDeptCode = "6260000";
	private String userName = "김주현";
	private String transfGramNo = "";
	//private String trnmlnstCd = "MOI";
	private String trnmlnstCd = "BUS";
	private String rcptnlnstCd = "BUS";
	private String trnmtlnstSysCd = "EHJ";
	
	private String rcptnlnstSysCd = "BIS"; //예산정보지원시스템(예산사업심의시스템, 예산편성지원시스템)
	private String rcptnlnstSysCdCron = "MCO"; //계약정보공개시스템(Cron)
	private String rcptnlnstSysCdCronB = "RAE"; //세입세출정보공개시스템(CronB)
	
	private String baseUrl = "http://10.60.76.53:26002/";
	
	private static String logPath = "/WAS/bcjis_new/ehojoLog/";
	public static String m_FileName = "log";
	public static String m_QryFileName = "qrylog";
	private static FileWriter objfile = null;
	
	int totalDataCnt = 0;
	int pageSize = 2000;
	int bcjisCnt = 1;
	int cronCnt = 1;
	int cronBCnt = 1;
	String fyr = "";
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getData() throws IOException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    	fyr = sdf.format(new Date());
    	
    	String startDt = fyr + "0112";
    	String endDt = "";
    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    	endDt = sdf2.format(new Date());
    	
    	/**
    	 * 1. VIEW테이블로 조회하기 위해 기초 데이터 가져오기
    	 * 2. 가져온 데이터 넣기
    	 * 3. 가져온 데이터로 뷰테이블 조회
    	 * 4. 뷰테이블 데이터를 옮기기 
    	 */
        //startApi(1, "TCM1420C","LR_BM_LAF_SA_00002","http://10.60.76.53:26002/BM/openAPI/LR_BM_LAF_SA_00002", "TBI_STANDFRSC");
    	//startApi2(22, "TFC2020M", "LR_FE_LAF_SA_00012", "http://10.60.76.53:26002/FE/openAPI/TFC2020M", "TCM_INSPECTIONACCTS");
    	
    	//1. VIEW테이블로 조회하기 위해 기초 데이터 가져오기
    	
    	startApi(1,  startDt, endDt, "TBM1100M","LR_BM_LAF_SA_00009","http://10.60.76.53:26002/BM/openAPI/TBM1100M","TBM_BGTDGR");
    	startApi(2,  startDt, endDt, "TBM1120L","LR_BM_LAF_SA_00013","http://10.60.76.53:26002/BM/openAPI/TBM1120L","TBM_BGTDGRGOVOFFICE");
    	startApi(3,  startDt, endDt, "TBM1130L","LR_BM_LAF_SA_00014","http://10.60.76.53:26002/BM/openAPI/TBM1130L","TBM_BGTDGROFFICE");
    	startApi(4,  startDt, endDt, "TBM1140L","LR_BM_LAF_SA_00012","http://10.60.76.53:26002/BM/openAPI/TBM1140L","TBM_BGTDGRDEPT");
    	startApi(5,  startDt, endDt, "TBM1150L","LR_BM_LAF_SA_00010","http://10.60.76.53:26002/BM/openAPI/TBM1150L","TBM_BGTDGRCHR");
    	startApi(6,  startDt, endDt, "TBM2140M","LR_BM_LAF_SA_00043","http://10.60.76.53:26002/BM/openAPI/TBM2140M","TBM_TECOMPO");
    	startApi(7,  startDt, endDt, "TBM2160L","LR_BM_LAF_SA_00044","http://10.60.76.53:26002/BM/openAPI/TBM2160L","TBM_TECOMPOFRSC");
    	startApi(8,  startDt, endDt, "TCM1220C","LR_CM_LAF_SA_00048","http://10.60.76.53:26002/CM/openAPI/TCM1220C","TBI_YEARSECTION");
    	startApi(9,  startDt, endDt, "TCM1230C","LR_CM_LAF_SA_00042","http://10.60.76.53:26002/CM/openAPI/TCM1230C","TBI_YEARFIELD");
    	startApi(10,  startDt, endDt, "TCM1270C","LR_CM_LAF_SA_00050","http://10.60.76.53:26002/CM/openAPI/TCM1270C","TBI_YEARTEMNGMOK");
    	startApi(11,  startDt, endDt, "TCM1340C","LR_CM_LAF_SA_00043","http://10.60.76.53:26002/CM/openAPI/TCM1340C","TBI_YEARFISFG");
    	startApi(12,  startDt, endDt, "TCM1370C","LR_CM_LAF_SA_00059","http://10.60.76.53:26002/CM/openAPI/TCM1370C","TBI_FRSC");
    	startApi(13,  startDt, endDt, "TCM1420C","LR_CM_LAF_SA_00060","http://10.60.76.53:26002/CM/openAPI/TCM1420C","TBI_STANDFRSC");
    	startApi(14,  startDt, endDt, "TCM1450C","LR_CM_LAF_SA_00016","http://10.60.76.53:26002/CM/openAPI/TCM1450C","TBI_FISFGMASTER");
    	startApi(15,  startDt, endDt, "TCM1480C","LR_CM_LAF_SA_00015","http://10.60.76.53:26002/CM/openAPI/TCM1480C","TBI_FISFG");
    	startApi(16,  startDt, endDt, "TCM3030C","LR_CM_LAF_SA_00005","http://10.60.76.53:26002/CM/openAPI/TCM3030C","TBI_COMMCDDETL");
    	startApi(17,  startDt, endDt, "TPM1200M","LR_PM_LAF_SA_00011","http://10.60.76.53:26002/PM/openAPI/TPM1200M","TFP_YEARBIZ");
    	startApi(18,  startDt, endDt, "TPM1201L","LR_PM_LAF_SA_00002","http://10.60.76.53:26002/PM/openAPI/TPM1201L","TFP_BIZ_H");
    	startApi(19,  startDt, endDt, "TPM1203L","LR_PM_LAF_SA_00008","http://10.60.76.53:26002/PM/openAPI/TPM1203L","TFP_POLIBIZ_H");
    	startApi(20,  startDt, endDt, "TPM1204L","LR_PM_LAF_SA_00010","http://10.60.76.53:26002/PM/openAPI/TPM1204L","TFP_UNITBIZ_H");
    	startApi(21,  startDt, endDt, "TPM1205L","LR_PM_LAF_SA_00006","http://10.60.76.53:26002/PM/openAPI/TPM1205L","TFP_DETLBIZ_H");
    	startApi(22,  startDt, endDt, "TFC2030L","LR_FE_LAF_SA_00011","http://10.60.76.53:26002/FE/openAPI/TFC2030L","TCM_INSPECTION");
    	startApi(23,  startDt, endDt, "TFE2501M","LR_FE_LAF_SA_00052","http://10.60.76.53:26002/FE/openAPI/TFE2501M","TEM_EXPDRESOLUTION");
    	startApi(24,  startDt, endDt, "TFE2601L","LR_FE_LAF_SA_00077","http://10.60.76.53:26002/FE/openAPI/TFE2601L","TEM_PAYCMD");
    	startApi(25,  startDt, endDt, "TCM8170M","LR_CM_LAF_SA_00034","http://10.60.76.53:26002/CM/openAPI/TCM8170M","TBI_SYSENVSETTING");
    	startApi(26,  startDt, endDt, "TFC1420L","LR_FE_LAF_SA_00066","http://10.60.76.53:26002/FE/openAPI/TFC1420L","TCM_COCTRT");
    	startApi(27,  startDt, endDt, "TFC1250M","LR_FE_LAF_SA_00067","http://10.60.76.53:26002/FE/openAPI/TFC1250M","TCM_CTRTBOOKS");
    	startApi(28,  startDt, endDt, "TFC1710L","LR_FE_LAF_SA_00016","http://10.60.76.53:26002/FE/openAPI/TFC1710L","TCM_NOTICE");
    	startApi(29,  startDt, endDt, "TCM2060C","LR_CM_LAF_SA_00011","http://10.60.76.53:26002/CM/openAPI/TCM2060C","TBI_DEPT");
    	startApi(30,  startDt, endDt, "TCM2040C","LR_CM_LAF_SA_00019","http://10.60.76.53:26002/CM/openAPI/TCM2040C","TBI_GOVOFFICE");
    	startApi(31,  startDt, endDt, "TCM4040M","LR_CM_LAF_SA_00038","http://10.60.76.53:26002/CM/openAPI/TCM4040M","TBI_USER");
    	startApi(32,  startDt, endDt, "TFC1230D","LR_FE_LAF_SA_00074","http://10.60.76.53:26002/FE/openAPI/TFC1230D","TCM_CTRTRELDEPT");
    	startApi(33,  startDt, endDt, "TFC1190L","LR_FE_LAF_SA_00004","http://10.60.76.53:26002/FE/openAPI/TFC1190L","TCM_CTRTSUPERV");
    	startApi(34,  startDt, endDt, "TFC1910L","LR_FE_LAF_SA_00009","http://10.60.76.53:26002/FE/openAPI/TFC1910L","TCM_FLDAGNT");
    	startApi(35,  startDt, endDt, "TFC1820M","LR_FE_LAF_SA_00079","http://10.60.76.53:26002/FE/openAPI/TFC1820M","TCM_SUBCTRT");
    	startApi(36,  startDt, endDt, "TFC1490L","LR_FE_LAF_SA_00021","http://10.60.76.53:26002/FE/openAPI/TFC1490L","TCM_THISYCTRTITEM");
    	startApi(37,  startDt, endDt, "TCM2130C","LR_CM_LAF_SA_00025","http://10.60.76.53:26002/CM/openAPI/TCM2130C","TBI_LOWDEPT");
    	startApi(38,  startDt, endDt, "TCM2090C","LR_CM_LAF_SA_00028","http://10.60.76.53:26002/CM/openAPI/TCM2090C","TBI_OFFICE");
    	startApi(39,  startDt, endDt, "TCM3200C","LR_CM_LAF_SA_00029","http://10.60.76.53:26002/CM/openAPI/TCM3200C","TBI_POSCL");
    	startApi(40,  startDt, endDt, "TFC2020M","LR_FE_LAF_SA_00012","http://10.60.76.53:26002/FE/openAPI/TFC2020M","TCM_INSPECTIONACCTS");
    	startApi(41,  startDt, endDt, "TSA1000M","LR_SA_LAF_SA_00017","http://10.60.76.53:26002/SA/openAPI/TSA1000M","TBC_CLOSEDGR");
    	startApi(42,  startDt, endDt, "TSA1200L","LR_SA_LAF_SA_00014","http://10.60.76.53:26002/SA/openAPI/TSA1200L","TBC_TEBGTCLOS");
    	startApi(43,  startDt, endDt, "TCM1070C","LR_CM_LAF_SA_00014","http://10.60.76.53:26002/CM/openAPI/TCM1070C","TBI_FIELD");
    	startApi(44,  startDt, endDt, "TCM1050C","LR_CM_LAF_SA_00033","http://10.60.76.53:26002/CM/openAPI/TCM1050C","TBI_SECTION");
    	startApi(45,  startDt, endDt, "TBM3140L","LR_BM_LAF_SA_00004","http://10.60.76.53:26002/BM/openAPI/TBM3140L","TBM_ALLO");
    	startApi(46,  startDt, endDt, "TBM2560L","LR_BM_LAF_SA_00024","http://10.60.76.53:26002/BM/openAPI/TBM2560L","TBM_FRSCCURRAMT");
    	startApi(47,  startDt, endDt, "TBM2550M","LR_BM_LAF_SA_00045","http://10.60.76.53:26002/BM/openAPI/TBM2550M","TBM_TECURRAMT");
    	startApi(48,  startDt, endDt, "TPM1202L","LR_PM_LAF_SA_00004","http://10.60.76.53:26002/PM/openAPI/TPM1202L","TFP_BIZDETL_H");
    	startApi(49,  startDt, endDt, "TCM1430C","LR_CM_LAF_SA_00020","http://10.60.76.53:26002/CM/openAPI/TCM1430C","TBI_HANG");
    	startApi(50,  startDt, endDt, "TCM1360C","LR_CM_LAF_SA_00022","http://10.60.76.53:26002/CM/openAPI/TCM1360C","TBI_JANG");
    	startApi(51,  startDt, endDt, "TCM1040C","LR_CM_LAF_SA_00023","http://10.60.76.53:26002/CM/openAPI/TCM1040C","TBI_KWAN");
    	startApi(52,  startDt, endDt, "TCM1100C","LR_CM_LAF_SA_00032","http://10.60.76.53:26002/CM/openAPI/TCM1100C","TBI_REVMOK");
    	startApi(53,  startDt, endDt, "TCM1320C","LR_CM_LAF_SA_00044","http://10.60.76.53:26002/CM/openAPI/TCM1320C","TBI_YEARHANG");
    	startApi(54,  startDt, endDt, "TCM1300C","LR_CM_LAF_SA_00045","http://10.60.76.53:26002/CM/openAPI/TCM1300C","TBI_YEARJANG");
    	startApi(55,  startDt, endDt, "TCM1200C","LR_CM_LAF_SA_00046","http://10.60.76.53:26002/CM/openAPI/TCM1200C","TBI_YEARKWAN");
    	startApi(56,  startDt, endDt, "TCM1240C","LR_CM_LAF_SA_00047","http://10.60.76.53:26002/CM/openAPI/TCM1240C","TBI_YEARREVMOK");
    	startApi(57,  startDt, endDt, "TFMA190M","LR_FM_LAF_SA_00007","http://10.60.76.53:26002/FM/openAPI/TFMA190M","TEF_TRNS_REVINFO");
    	startApi(58,  startDt, endDt, "TFMA200A","LR_FM_LAF_SA_00020","http://10.60.76.53:26002/FM/openAPI/TFMA200A","TFM_REVINFO");
    	startApi(59,  startDt, endDt, "TCM6060C","LR_CM_LAF_SA_00009","http://10.60.76.53:26002/CM/openAPI/TCM6060C","TBI_CUST");
    	startApi(60,  startDt, endDt, "TCM2070H","LR_CM_LAF_SA_00012","http://10.60.76.53:26002/CM/openAPI/TCM2070H","TBI_DEPT_H");
    	startApi(61,  startDt, endDt, "TFE2506C","LR_FE_LAF_SA_00049","http://10.60.76.53:26002/FE/openAPI/TFE2506C","TEM_EXPDRESOLBGTACCT");
    	startApi(62,  startDt, endDt, "TFE2552L","LR_FE_LAF_SA_00050","http://10.60.76.53:26002/FE/openAPI/TFE2552L","TEM_EXPDRESOLCRDITOR");

    					
    	/*startApi(1, 	startDt, endDt,	"TBM1100M","LR_BM_LAF_SA_00009","http://10.60.76.53:26002/BM/openAPI/TBM1100M","TBM_BGTDGR");//TBM_BGTDGR
    	startApi(2, 	startDt, endDt,	"TBM1120L","LR_BM_LAF_SA_00013","http://10.60.76.53:26002/BM/openAPI/TBM1120L","TBM_BGTDGRGOVOFFICE");//TBM_BGTDGRGOVOFFICE
    	startApi(3, 	startDt, endDt,	"TBM1130L","LR_BM_LAF_SA_00014","http://10.60.76.53:26002/BM/openAPI/TBM1130L","TBM_BGTDGROFFICE");//TBM_BGTDGROFFICE
    	startApi(4, 	startDt, endDt,	"TBM1140L","LR_BM_LAF_SA_00012","http://10.60.76.53:26002/BM/openAPI/TBM1140L","TBM_BGTDGRDEPT");//TBM_BGTDGRDEPT
    	startApi(5, 	startDt, endDt,	"TBM1150L","LR_BM_LAF_SA_00010","http://10.60.76.53:26002/BM/openAPI/TBM1150L","TBM_BGTDGRCHR");//TBM_BGTDGRCHR
    	startApi(6, 	startDt, endDt,	"TBM2140M","LR_BM_LAF_SA_00043","http://10.60.76.53:26002/BM/openAPI/TBM2140M","TBM_TECOMPO");//TBM_TECOMPO
    	startApi(7, 	startDt, endDt,	"TBM2160L","LR_BM_LAF_SA_00044","http://10.60.76.53:26002/BM/openAPI/TBM2160L","TBM_TECOMPOFRSC");//TBM_TECOMPOFRSC
    	startApi(8, 	startDt, endDt,	"TCM1220C","LR_CM_LAF_SA_00048","http://10.60.76.53:26002/CM/openAPI/TCM1220C","TBI_YEARSECTION");//TBI_YEARSECTION
    	startApi(9, 	startDt, endDt,	"TCM1230C","LR_CM_LAF_SA_00042","http://10.60.76.53:26002/CM/openAPI/TCM1230C","TBI_YEARFIELD");//TBI_YEARFIELD
    	startApi(10, 	startDt, endDt,	"TCM1270C","LR_CM_LAF_SA_00050","http://10.60.76.53:26002/CM/openAPI/TCM1270C","TBI_YEARTEMNGMOK");//TBI_YEARTEMNGMOK
    	startApi(11, 	startDt, endDt,	"TCM1340C","LR_CM_LAF_SA_00043","http://10.60.76.53:26002/CM/openAPI/TCM1340C","TBI_YEARFISFG");//TBI_YEARFISFG
    	startApi(12, 	startDt, endDt,	"TCM1370C","LR_CM_LAF_SA_00059","http://10.60.76.53:26002/BM/openAPI/TCM1370C","TBI_FRSC");//TBI_FRSC
    	startApi(13, 	startDt, endDt,	"TCM1420C","LR_CM_LAF_SA_00060","http://10.60.76.53:26002/BM/openAPI/TCM1420C","TBI_STANDFRSC");//TBI_STANDFRSC
    	startApi(14, 	startDt, endDt,	"TCM1450C","LR_CM_LAF_SA_00016","http://10.60.76.53:26002/CM/openAPI/TCM1450C","TBI_FISFGMASTER");//TBI_FISFGMASTER
    	startApi(15, 	startDt, endDt,	"TCM1480C","LR_CM_LAF_SA_00015","http://10.60.76.53:26002/CM/openAPI/TCM1480C","TBI_FISFG");//TBI_FISFG
    	startApi(16, 	startDt, endDt,	"TCM3030C","LR_CM_LAF_SA_00005","http://10.60.76.53:26002/CM/openAPI/TCM3030C","TBI_COMMCDDETL");//TBI_COMMCDDETL
    	startApi(17, 	startDt, endDt,	"TPM1200M","LR_PM_LAF_SA_00011","http://10.60.76.53:26002/PM/openAPI/TPM1200M","TFP_YEARBIZ");//TFP_YEARBIZ
    	startApi(18, 	startDt, endDt,	"TPM1201L","LR_PM_LAF_SA_00002","http://10.60.76.53:26002/PM/openAPI/TPM1201L","TFP_BIZ_H");//TFP_BIZ_H
    	startApi(19, 	startDt, endDt,	"TPM1203L","LR_PM_LAF_SA_00008","http://10.60.76.53:26002/PM/openAPI/TPM1203L","TFP_POLIBIZ_H");//TFP_POLIBIZ_H
    	startApi(20, 	startDt, endDt,	"TPM1204L","LR_PM_LAF_SA_00010","http://10.60.76.53:26002/PM/openAPI/TPM1204L","TFP_UNITBIZ_H");//TFP_UNITBIZ_H
    	startApi(21, 	startDt, endDt,	"TPM1205L","LR_PM_LAF_SA_00006","http://10.60.76.53:26002/PM/openAPI/TPM1205L","TFP_DETLBIZ_H");//TFP_DETLBIZ_H
                        
    	startApi2(1, 	startDt, endDt,	"TFC2030L", "LR_FE_LAF_SA_00011", "http://10.60.76.53:26002/FE/openAPI/TFC2030L", "TCM_INSPECTION");
    	startApi2(2, 	startDt, endDt,	"TFE2501M", "LR_FE_LAF_SA_00052", "http://10.60.76.53:26002/FE/openAPI/TFE2501M", "TEM_EXPDRESOLUTION");
    	startApi2(3, 	startDt, endDt,	"TFE2601L", "LR_FE_LAF_SA_00077", "http://10.60.76.53:26002/FE/openAPI/TFE2601L", "TEM_PAYCMD");
    	startApi2(4, 	startDt, endDt,	"TCM8170M", "LR_CM_LAF_SA_00034", "http://10.60.76.53:26002/CM/openAPI/TCM8170M", "TBI_SYSENVSETTING");
    	startApi2(5, 	startDt, endDt,	"TFC1420L", "LR_FE_LAF_SA_00066", "http://10.60.76.53:26002/FE/openAPI/TFC1420L", "TCM_COCTRT");
    	startApi2(6, 	startDt, endDt,	"TCM3030C", "LR_CM_LAF_SA_00005", "http://10.60.76.53:26002/CM/openAPI/TCM3030C", "TCM_COMMCDDETL");
    	startApi2(7, 	startDt, endDt,	"TFC1250M", "LR_FE_LAF_SA_00067", "http://10.60.76.53:26002/FE/openAPI/TFC1250M", "TCM_CTRTBOOKS");
    	startApi2(8, 	startDt, endDt,	"TFC1710L", "LR_FE_LAF_SA_00016", "http://10.60.76.53:26002/FE/openAPI/TFC1710L", "TCM_NOTICE");
    	startApi2(9, 	startDt, endDt,	"TCM2060C", "LR_CM_LAF_SA_00011", "http://10.60.76.53:26002/CM/openAPI/TCM2060C", "TBI_DEPT");
    	startApi2(10, 	startDt, endDt,	"TCM2040C", "LR_CM_LAF_SA_00019", "http://10.60.76.53:26002/CM/openAPI/TCM2040C", "TBI_GOVOFFICE");
    	startApi2(11, 	startDt, endDt,	"TCM4040M", "LR_CM_LAF_SA_00038", "http://10.60.76.53:26002/CM/openAPI/TCM4040M", "TBI_USER");
    	startApi2(13, 	startDt, endDt,	"TFC1230D", "LR_FE_LAF_SA_00074", "http://10.60.76.53:26002/FE/openAPI/TFC1230D", "TCM_CTRTRELDEPT");
    	startApi2(14, 	startDt, endDt,	"TFC1190L", "LR_FE_LAF_SA_00004", "http://10.60.76.53:26002/FE/openAPI/TFC1190L", "TCM_CTRTSUPERV");
    	startApi2(15, 	startDt, endDt,	"TFC1910L", "LR_FE_LAF_SA_00009", "http://10.60.76.53:26002/FE/openAPI/TFC1910L", "TCM_FLDAGNT");
    	startApi2(16, 	startDt, endDt,	"TFC1820M", "LR_FE_LAF_SA_00079", "http://10.60.76.53:26002/FE/openAPI/TFC1820M", "TCM_SUBCTRT");
    	startApi2(17, 	startDt, endDt,	"TFC1490L", "LR_FE_LAF_SA_00021", "http://10.60.76.53:26002/FE/openAPI/TFC1490L", "TCM_THISYCTRTITEM");
    	startApi2(18, 	startDt, endDt,	"TCM2130C", "LR_CM_LAF_SA_00025", "http://10.60.76.53:26002/CM/openAPI/TCM2130C", "TBI_LOWDEPT");
    	startApi2(19, 	startDt, endDt,	"TCM2090C", "LR_CM_LAF_SA_00028", "http://10.60.76.53:26002/CM/openAPI/TCM2090C", "TBI_OFFICE");
    	startApi2(21, 	startDt, endDt,	"TCM3200C", "LR_CM_LAF_SA_00029", "http://10.60.76.53:26002/CM/openAPI/TCM3200C", "TBI_POSCL");
    	startApi2(22, 	startDt, endDt,	"TFC2020M", "LR_FE_LAF_SA_00012", "http://10.60.76.53:26002/FE/openAPI/TFC2020M", "TCM_INSPECTIONACCTS");
    	startApi2(23, 	startDt, endDt,	"TSA1000M", "LR_SA_LAF_SA_00017", "http://10.60.76.53:26002/SA/openAPI/TSA1000M", "TBC_CLOSEDGR");
    	startApi2(24, 	startDt, endDt,	"TCM1340C", "LR_CM_LAF_SA_00043", "http://10.60.76.53:26002/CM/openAPI/TCM1340C", "TBI_YEARFISFG");
    	startApi2(25, 	startDt, endDt,	"TCM1270C", "LR_CM_LAF_SA_00050", "http://10.60.76.53:26002/CM/openAPI/TCM1270C", "TBI_YEARTEMNGMOK");
    	startApi2(26, 	startDt, endDt,	"TPM1201L", "LR_PM_LAF_SA_00002", "http://10.60.76.53:26002/PM/openAPI/TPM1201L", "TFP_BIZ_H");
    	                
    	startApi3(2, 	startDt, endDt,	"TSA1200L", "LR_SA_LAF_SA_00014", "http://10.60.76.53:26002/SA/openAPI/TSA1200L", "TBC_TEBGTCLOS");
    	startApi3(5, 	startDt, endDt,	"TCM1070C", "LR_CM_LAF_SA_00014", "http://10.60.76.53:26002/CM/openAPI/TCM1070C", "TBI_FIELD");
    	startApi3(6, 	startDt, endDt,	"TCM1370C", "LR_CM_LAF_SA_00059", "http://10.60.76.53:26002/BM/openAPI/TCM1370C", "TBI_FRSC");
    	startApi3(8, 	startDt, endDt,	"TCM1050C", "LR_CM_LAF_SA_00033", "http://10.60.76.53:26002/CM/openAPI/TCM1050C", "TBI_SECTION");
    	startApi3(10, 	startDt, endDt,	"TBM3140L", "LR_BM_LAF_SA_00004", "http://10.60.76.53:26002/BM/openAPI/TBM3140L", "TBM_ALLO");
    	startApi3(11, 	startDt, endDt,	"TBM2560L", "LR_BM_LAF_SA_00024", "http://10.60.76.53:26002/BM/openAPI/TBM2560L", "TBM_FRSCCURRAMT");
    	startApi3(12, 	startDt, endDt,	"TBM2550M", "LR_BM_LAF_SA_00045", "http://10.60.76.53:26002/BM/openAPI/TBM2550M", "TBM_TECURRAMT");
    	startApi3(15, 	startDt, endDt,	"TPM1202L", "LR_PM_LAF_SA_00004", "http://10.60.76.53:26002/PM/openAPI/TPM1202L", "TFP_BIZDETL_H");
    	startApi3(16, 	startDt, endDt,	"TPM1205L", "LR_PM_LAF_SA_00006", "http://10.60.76.53:26002/PM/openAPI/TPM1205L", "TFP_DETLBIZ_H");
    	startApi3(17, 	startDt, endDt,	"TPM1203L", "LR_PM_LAF_SA_00008", "http://10.60.76.53:26002/PM/openAPI/TPM1203L", "TFP_POLIBIZ_H");
    	startApi3(18, 	startDt, endDt,	"TPM1204L", "LR_PM_LAF_SA_00010", "http://10.60.76.53:26002/PM/openAPI/TPM1204L", "TFP_UNITBIZ_H");
    	startApi3(19, 	startDt, endDt,	"TPM1200M", "LR_PM_LAF_SA_00011", "http://10.60.76.53:26002/PM/openAPI/TPM1200M", "TFP_YEARBIZ");
    	startApi3(20, 	startDt, endDt,	"TCM1480C", "LR_CM_LAF_SA_00015", "http://10.60.76.53:26002/CM/openAPI/TCM1480C", "TBI_FISFG");
    	startApi3(21, 	startDt, endDt,	"TCM1430C", "LR_CM_LAF_SA_00020", "http://10.60.76.53:26002/CM/openAPI/TCM1430C", "TBI_HANG");
    	startApi3(22, 	startDt, endDt,	"TCM1360C", "LR_CM_LAF_SA_00022", "http://10.60.76.53:26002/CM/openAPI/TCM1360C", "TBI_JANG");
    	startApi3(23, 	startDt, endDt,	"TCM1040C", "LR_CM_LAF_SA_00023", "http://10.60.76.53:26002/CM/openAPI/TCM1040C", "TBI_KWAN");
    	startApi3(24, 	startDt, endDt,	"TCM1100C", "LR_CM_LAF_SA_00032", "http://10.60.76.53:26002/CM/openAPI/TCM1100C", "TBI_REVMOK");
    	startApi3(25, 	startDt, endDt,	"TCM1320C", "LR_CM_LAF_SA_00044", "http://10.60.76.53:26002/CM/openAPI/TCM1320C", "TBI_YEARHANG");
    	startApi3(26, 	startDt, endDt,	"TCM1300C", "LR_CM_LAF_SA_00045", "http://10.60.76.53:26002/CM/openAPI/TCM1300C", "TBI_YEARJANG");
    	startApi3(27, 	startDt, endDt,	"TCM1200C", "LR_CM_LAF_SA_00046", "http://10.60.76.53:26002/CM/openAPI/TCM1200C", "TBI_YEARKWAN");
    	startApi3(28, 	startDt, endDt,	"TCM1240C", "LR_CM_LAF_SA_00047", "http://10.60.76.53:26002/CM/openAPI/TCM1240C", "TBI_YEARREVMOK");
    	startApi3(29, 	startDt, endDt,	"TFMA190M", "LR_FM_LAF_SA_00007", "http://10.60.76.53:26002/FM/openAPI/TFMA190M", "TEF_TRNS_REVINFO");
    	startApi3(30, 	startDt, endDt,	"TFMA200A", "LR_FM_LAF_SA_00020", "http://10.60.76.53:26002/FM/openAPI/TFMA200A", "TFM_REVINFO");
    	startApi3(31, 	startDt, endDt,	"TCM1230C", "LR_CM_LAF_SA_00042", "http://10.60.76.53:26002/CM/openAPI/TCM1230C", "TBI_YEARFIELD");
    	startApi3(32, 	startDt, endDt,	"TCM1220C", "LR_CM_LAF_SA_00048", "http://10.60.76.53:26002/CM/openAPI/TCM1220C", "TBI_YEARSECTION");
    	startApi3(34, 	startDt, endDt,	"TCM6060C", "LR_CM_LAF_SA_00009", "http://10.60.76.53:26002/CM/openAPI/TCM6060C", "TBI_CUST");
    	startApi3(35,	startDt, endDt,	"TCM2070H", "LR_CM_LAF_SA_00012", "http://10.60.76.53:26002/CM/openAPI/TCM2070H", "TBI_DEPT_H");
    	startApi3(40, 	startDt, endDt,	"TFE2506C", "LR_FE_LAF_SA_00049", "http://10.60.76.53:26002/FE/openAPI/TFE2506C", "TEM_EXPDRESOLBGTACCT");
    	startApi3(41, 	startDt, endDt,	"TFE2552L", "LR_FE_LAF_SA_00050", "http://10.60.76.53:26002/FE/openAPI/TFE2552L", "TEM_EXPDRESOLCRDITOR");
*/
    	sendLog("###########  최종 데이터 갯수 " + totalDataCnt + " ###################");

    }
    
    public void startApi(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, String tableNm) {
    	sendLog("#############################################################################");
    	sendLog("################## " + bcjisCnt + "번  api " + num + ". " + tableNm + " 시작 ##################");
    	
    	startApi(bcjisCnt, startDt, endDt, apiCd, apiId, apiUrl, tableNm, 1, "");
    	
    	sendLog("################## " + bcjisCnt + "번  api " + num + ". " + tableNm + " 종료 ###################");
    	sendLog("#############################################################################");
    	bcjisCnt++;
    }
    
    public void startApi2(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, String tableNm) {
    	sendLog("#############################################################################");
    	sendLog("###########  cron  ####### " + cronCnt + "번  api " + num + ". " + tableNm + " 시작 ##################");
    	
    	startApi(cronCnt, startDt, endDt, apiCd, apiId, apiUrl, tableNm, 1, "cron");
    	
    	sendLog("###########  cron ####### " + cronCnt + "번  api " + num + ". " + tableNm + "  종료 ###################");
    	sendLog("#############################################################################");
    	cronCnt++;
    }
    
    public void startApi3(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, String tableNm) {
    	sendLog("#############################################################################");
    	sendLog("###########  cronB  ####### " + cronBCnt + "번  api " + num + ". " + tableNm + " 시작 ##################");
    	
    	startApi(cronBCnt, startDt, endDt, apiCd, apiId, apiUrl, tableNm, 1, "cron");
    	
    	sendLog("###########  cronB ####### " + cronBCnt + "번  api " + num + ". " + tableNm + "  종료 ###################");
    	sendLog("#############################################################################");
    	cronBCnt++;
    }
    
    public void startApi(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, String tableNm, int page, String type) {
    	
    	sendLog("#### " + num + "번 api " + page + " page 시작 ##  " + type + "  ##");
    	
    	String areaId = apiId.substring(3, 5);
    	String url = baseUrl + areaId + "/openAPI/" + apiCd;
    	
    	JSONObject res = new JSONObject();
    	
    	try{
	    	res = getApiData(num, startDt, endDt, apiCd, apiId, url, page, tableNm, type);
	    	
	    	//데이터를 정상 받아오면 실행
	    	if(res != null){
	    		//sendLog("res 결과 : " + res.toString());
	    		//System.out.println("res 결과 : " + res.toString());
	    		JSONObject body = new JSONObject();
	    		JSONArray dataArr = new JSONArray();
	    		
	    		if(res.has("body")){
	    			body = res.getJSONObject("body");
	    			
	    			String totalCntStr = "";
	    			int totalCnt = 0;
	    			if(body.has("totalCnt")){
	    				totalCntStr = body.get("totalCnt").toString();
	    				if(!"".equals(totalCntStr)){
			    			totalCnt = Integer.parseInt(totalCntStr);
			    		}
	    			}
	    			
	    			String curPageStr = "";
	    			int curPage = 0;
	    			if(body.has("curPage")){
	    				curPageStr = body.get("curPage").toString();
	    				if(!"".equals(curPageStr)){
	    					curPage = Integer.parseInt(curPageStr);
	    				}
	    			}
	    			
	    			String pageRowStr = "";
	    			int pageRow = 0;
	    			if(body.has("pageRow")){
	    				pageRowStr = body.get("pageRow").toString();
	    				if(!"".equals(pageRowStr)){
	    					pageRow = Integer.parseInt(pageRowStr);
	    				}
	    			}
	    			dataArr = JSONArray.fromObject(body.get("data"));
	    			
		    		sendLog("curPage : " + curPage + "   has : " + body.has("curPage"));
		    		sendLog("pageRow : " + pageRow + "   has : " + body.has("pageRow"));
		    		sendLog("totalCnt : " + totalCnt + "   has : " + body.has("totalCnt"));
		    		
		    		//System.out.println("curPage : " + curPage);
		    		//System.out.println("pageRow : " + pageRow);
		    		//System.out.println("totalCnt : " + totalCnt);
		    		//System.out.println("dataArr : " + dataArr.size());
		    		
		    		if("cron".equals(type)){
	    				sendEtcLog("------ " + apiCd + " 결과   " + page + "pgae  " + tableNm, 1, "cronQrylog");
	    			}else{
	    				sendQryLog("------ " + apiCd + " 결과   " + page + "pgae  " + tableNm, 1);
	    			}
		    		
		    		List<HashMap<String, String>> list = convertJsonArrToListQry(dataArr, tableNm);
		    		//sendEtcLog(apiCd + " 결과   " + page + "pgae  " + tableNm, 0, "upsertQry");
		    		for(HashMap<String, String> map : list){
		    			String upsertQry = map.get("upsertQry");
		    			if("cron".equals(type)){
		    				sendEtcLog(upsertQry + ";", 1, "cronQrylog");
		    			}else{
		    				sendQryLog(upsertQry + ";", 1);
		    			}
		    		}
		    		
		    		//데이터 입력 시작0
		    		
		    		sendLog("#### " + num + "번 api " + page + "page 종료 ####");
		    		totalDataCnt += pageRow;
		    		
		    		//페이지 수만큼 실행
		    		int nowCnt = pageSize * page; //현재 칼럼수 * 페이지
		    		
		    		if(totalCnt > 0 && nowCnt < totalCnt){
		    			page++;
		    			startApi(num, startDt, endDt, apiCd, apiId, apiUrl, tableNm, page, type);
		    		}
	    		}
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public JSONObject getApiDataTest(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, int pageNum, String tableNm, String type){
    	
    	JSONObject res = new JSONObject();
    	String testResult = "{\"data\":[{\"rn\":\"1\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"240\",\"acntDvNm\":\"주차장운영특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"8\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"2\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"245\",\"acntDvNm\":\"장기미집행대지보상특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"9\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"3\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"250\",\"acntDvNm\":\"기반시설부담금특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"10\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"4\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"410\",\"acntDvNm\":\"사회복지기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"11\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"5\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"411\",\"acntDvNm\":\"투자진흥기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"18\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"6\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"415\",\"acntDvNm\":\"대청호장학기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"12\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"7\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"420\",\"acntDvNm\":\"청소년자립지원기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"13\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"8\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"425\",\"acntDvNm\":\"체육진흥기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"14\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"9\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"430\",\"acntDvNm\":\"재난관리기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"15\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"10\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"435\",\"acntDvNm\":\"식품진흥기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"16\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"11\",\"lafCd\":\"4420000\",\"fyr\":\"2011\",\"acntDvCd\":\"440\",\"acntDvNm\":\"농촌전문인력육성기금\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"17\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"12\",\"lafCd\":\"4420000\",\"fyr\":\"1994\",\"acntDvCd\":\"230\",\"acntDvNm\":\"주민소득지원및생활안정자금특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"1\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"13\",\"lafCd\":\"4420000\",\"fyr\":\"2004\",\"acntDvCd\":\"230\",\"acntDvNm\":\"주민소득지원및생활안정자금특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"1\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"14\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"100\",\"acntDvNm\":\"일반회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"1\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"15\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"210\",\"acntDvNm\":\"상수도사업특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"2\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"16\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"215\",\"acntDvNm\":\"하수도사업특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"3\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"17\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"220\",\"acntDvNm\":\"수질개선특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"4\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"18\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"225\",\"acntDvNm\":\"의료보호특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"5\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"19\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"230\",\"acntDvNm\":\"주민소득지원및생활안정자금특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"6\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"},{\"rn\":\"20\",\"lafCd\":\"4420000\",\"fyr\":\"2012\",\"acntDvCd\":\"235\",\"acntDvNm\":\"농공단지조성관리특별회계\",\"trsmYn\":\"N\",\"trsmDt\":null,\"lupOrd\":\"7\",\"useYn\":\"Y\",\"frstRgstrUsrId\":\"A000000\",\"frstRgstrDt\":\"20220722080101\",\"lastMdfcnUsrId\":\"A000000\",\"lastMdfcnDt\":\"20220722080101\",\"itgEpAplcnDvCd\":null,\"edeItgEpUseYn\":\"N\"}],\"curPage\":" + pageNum + ",\"pageRow\":3000,\"totalCnt\":109341}";
    	try {
    		JSONObject jsonRes = new JSONObject();
			JSONObject resBody = JSONObject.fromObject(testResult);
			jsonRes.put("body", resBody);
	    	
	    	System.out.println("resBody : " + resBody.toString());
	    	System.out.println("jsonRes : " + jsonRes.toString());
	    	res = jsonRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	
    	return res;
    }
    public JSONObject getApiData(int num, String startDt, String endDt, String apiCd, String apiId, String apiUrl, int pageNum, String tableNm, String type){
    	
    	JSONObject res = new JSONObject();
    	
    	try{
	        sendLog("apiCd = " + apiCd);
	        
	    	//HTTP url
	        String url = apiUrl;
	    	sendLog("url : " + url);
	    	
			
	    	String ifId = apiId;
	    	String tranId = EncryptionUtils.makeTxId(ifId);
	    	
	    	sendLog("ifId : " + ifId);
	    	sendLog("tranId : " + tranId);
	    	//HTTP Client
	    	HttpClient httpClient = HttpClientBuilder.create().build();
	    	//HTTP Method
	    	HttpPost httpPost = new HttpPost(url);
	    	
	    	//HTTP Header
	    	//API 인증키(외부기관에서 요청 시 필요)
	    	httpPost.setHeader("API_KEY", apiKey);
	    	httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
	    	
	    	//JSON 요청 페이지
		    JSONObject jsonReq = new JSONObject();
		    
		    //Header
		    JSONObject header = new JSONObject();
		    
		    //body
		    //JSONArray body = new JSONArray();
		    
		    //Header 설정
		    header.put("ifId", ifId);//인터페이스 아이디
		    header.put("tranId", tranId); //트랜잭션 아이디
		    header.put("transfGramNo", transfGramNo); //거래일련번호
		    header.put("trnmlnstCd", trnmlnstCd); //송신 기관 코드
		    header.put("rcptnlnstCd", rcptnlnstCd); //수신 기관 코드
		    header.put("trnmtlnstSysCd", trnmtlnstSysCd); //송신 기관 시스템 코드
		    if("cron".equals(type)){
		    	header.put("rcptnlnstSysCd", rcptnlnstSysCdCron); //수신 기관 시스템 코드_크론
		    }else{
		    	header.put("rcptnlnstSysCd", rcptnlnstSysCd); //수신 기관 시스템 코드
		    }
		    header.put("userDeptCode", userDeptCode); //이용자 부서코드
		    header.put("userName", userName); //이용자명
		    
		    //body 설정
		    JSONObject bodyObj = new JSONObject();
		    //bodyObj.put("sqlCondition", "1=1");
		    //bodyObj.put("curPage", pageNum);
		    
		    String pageNumStr = Integer.toString(pageNum);
		    String pageSizeStr = Integer.toString(pageSize);
		    if(apiCd.indexOf("TBM") > -1){ //BM(예산)
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("curPage", pageNumStr);
		    	//bodyObj.put("pageRow", pageSizeStr);
		    	bodyObj.put("pageSize", pageSizeStr);
		    	//bodyObj.put("pageNum", pageSizeStr);
		    	bodyObj.put("lastMdfcnBgngYmd", startDt);
		    	bodyObj.put("lastMdfcnEndYmd", endDt);
		    }else if(apiCd.indexOf("TCM") > -1){ //CM(공통)
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("curPage", pageNumStr);
		    	//bodyObj.put("pageRow", pageSizeStr);
		    	bodyObj.put("pageSize", pageSizeStr);
		    	//bodyObj.put("pageNum", pageSizeStr);
		    	bodyObj.put("bgngYmd", startDt);
		    	bodyObj.put("endYmd", endDt);
		    }else if(apiCd.indexOf("TPM") > -1){ //PM(사업)
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("sqlCondition", "1=1");
		    	bodyObj.put("curPage", pageNumStr);
		    	//bodyObj.put("pageNum", pageNumStr);
		    	//bodyObj.put("pageRow", pageSizeStr);
		    	bodyObj.put("pageSize", pageSizeStr);
		    	bodyObj.put("lastMdfcnBgngYmd", startDt);
		    	bodyObj.put("lastMdfcnEndYmd", endDt);
		    }else if(apiCd.indexOf("TSA") > -1){ //결산
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("pageNum", pageNumStr);
		    	//bodyObj.put("pageRow", pageSizeStr);
		    	bodyObj.put("lastMdfcnBgngYmd", startDt);
		    	bodyObj.put("lastMdfcnEndYmd", endDt);
		    }else if(apiCd.indexOf("TFC") > -1){ //계약
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("curPage", pageNumStr);
		    	bodyObj.put("pageSize", pageSizeStr);
		    	bodyObj.put("lastMdfcnBgngYmd", startDt);
		    	bodyObj.put("lastMdfcnEndYmd", endDt);
		    }else{
		    	bodyObj.put("fyr", fyr);
		    	bodyObj.put("curPage", pageNumStr);
		    	bodyObj.put("pageRow", pageSizeStr);
		    	bodyObj.put("pageSize", pageSizeStr);
		    	bodyObj.put("pageNum", pageSizeStr);
		    }
		    
		    //bodyObj = new JSONObject(); //빈값
		    
		    
	    	//bodyObj.put("pageSize", pageSize);
	    	
		    /*if("TCM1370C".equals(apiCd)){
		    	//bodyObj.put("fry", "2023");
		    	bodyObj.put("curPage", 1);
		    	bodyObj.put("pageSize", 20);
		    }*/
		    
		    
		    //body.add(bodyObj); 
		    
		    sendLog("header : " + header.toString());
		    sendLog("body : " + bodyObj.toString());
		    System.out.println("jsonReq.toString() : " + bodyObj.toString());
		    
		    //JSON 요청 메시지 설정
		    jsonReq.put("header", header);
		    
		    //body 암호화 (외부기관에서 요청 시 필요)
		    //jsonReq.put("body", EncryptionUtils.encryptStringAria(body.toString(), enKey));
		    jsonReq.put("body", EncryptionUtils.encryptStringAria(bodyObj.toString(), enKey));
		    
		    System.out.println("jsonReq.toString() : " + jsonReq.toString());
		    sendLog("전체 전송 : " + jsonReq.toString());
		    
		    // Request -> Response
		    httpPost.setEntity(new StringEntity(jsonReq.toString(), "UTF-8"));
		    HttpResponse httpResponse = httpClient.execute(httpPost);
		    HttpEntity httpEntity = httpResponse.getEntity();
		    
		    System.out.println("getStatusCode : " + httpResponse.getStatusLine().getStatusCode());
		    sendLog("getStatusCode : " + httpResponse.getStatusLine().getStatusCode());
		    //정상응답
		    if(httpResponse.getStatusLine().getStatusCode() == 200){
		    	// 응답 메시지
		    	String strRes = EntityUtils.toString(httpEntity, "UTF-8");
		    	//System.out.println("응답메세지 : " + strRes);
		    	sendEtcLog("apiCd : " + apiCd, 0, "rtnMsg");
		    	sendEtcLog("tableNm : " + tableNm, 0, "rtnMsg");
		    	sendEtcLog("응답메세지 : " + strRes, 0, "rtnMsg");
		    	//sendLog("응답메세지 : " + strRes);
		    	// JSON Parsing
		    	JSONObject jsonRes = JSONObject.fromObject(strRes);
		    	//System.out.println("응답메세지 jsonParse : " + jsonRes);
		    	//sendLog("응답메세지 jsonParse : " + jsonRes);
		    	// 요청결과
		    	JSONObject resResult = jsonRes.getJSONObject("result");
		    	System.out.println("요청결과 : " + resResult.toString());
		    	sendLog("요청결과 : " + resResult.toString());
		    	// 결과코드(00000:정상)
		    	String rsltCd = resResult.getString("rsltCd");
		    	System.out.println("응답코드 : " + rsltCd);
		    	sendLog("응답코드 : " + rsltCd);
		    	
		    	if("0000".equals(rsltCd)){
		    		//System.out.println("정상응답  body : " + jsonRes.getString("body"));
		    		//sendLog("정상응답  body : " + jsonRes.getString("body"));
		    		//System.out.println("정상응답  body 복호화 : " + EncryptionUtils.decryptStringAria(jsonRes.getString("body"), enKey));
		    		//sendLog("정상응답  body 복호화 : " + EncryptionUtils.decryptStringAria(jsonRes.getString("body"), enKey));
		    		// body 복호화 (외부기관에서 요청 시 필요)
		    		JSONObject resBody = JSONObject.fromObject(EncryptionUtils.decryptStringAria(jsonRes.getString("body"), enKey));
		    		//sendLog("정상응답 body : " + resBody.toString());
		    		//System.out.println("resBody : " + resBody.toString());
			    	//JSONArray dataArr = JSONArray.fromObject(resBody.get("data"));
			    	jsonRes.put("body", resBody);
			    	
			    	res = jsonRes;
			    	//sendLog("정상응답 : " + res.toString());
			    	//sendEtcLog(apiCd + " 결과   " + tableNm, 0, "result");
			    	//sendEtcLog(res.toString(), 1, "result");
		    	}else{
		    		sendEtcLog("###########################################", 0, "_"+rsltCd);
		    		sendEtcLog("apiCd : " + apiCd, 1, "_"+rsltCd);
		    		sendEtcLog("url : " + url, 1, "_"+rsltCd);
		    		sendEtcLog("ifId : " + ifId, 1, "_"+rsltCd);
		    		sendEtcLog("tranId : " + tranId, 1, "_"+rsltCd);
		    		sendEtcLog("header : " + header.toString(4), 1, "_"+rsltCd);
		    		sendEtcLog("body : " + bodyObj.toString(4), 1, "_"+rsltCd);
		    		sendEtcLog("rsltCd : " + rsltCd, 1, "_"+rsltCd);
		    		sendEtcLog("응답메세지 : " + strRes, 1, "_"+rsltCd);
		    		sendEtcLog("요청결과 : " + resResult.toString(4), 1, "_"+rsltCd);
		    		sendLog("응답 실패 header : " + header.toString());
		    		sendLog("응답 실패 body : " + bodyObj.toString());
		    	}
		    	
		    }else{
		    	//이상응답
		    	System.out.println(httpResponse.getStatusLine().getStatusCode());
		    	sendLog("### 이상응답 : " + httpResponse.getStatusLine().getStatusCode());
		    	sendLog("### 이상응답 상세 : " + httpResponse.getStatusLine()); 
		    	int statusCd = httpResponse.getStatusLine().getStatusCode();
		    	sendEtcLog("###########################################", 0, "_"+statusCd);
	    		sendEtcLog("apiCd : " + apiCd, 1, "_"+statusCd);
	    		sendEtcLog("url : " + url, 1, "_"+statusCd);
	    		sendEtcLog("ifId : " + ifId, 1, "_"+statusCd);
	    		sendEtcLog("tranId : " + tranId, 1, "_"+statusCd);
	    		sendEtcLog("header : " + header.toString(4), 1, "_"+statusCd);
	    		sendEtcLog("body : " + bodyObj.toString(4), 1, "_"+statusCd);
	    		sendEtcLog("httpResponse.getStatusLine().getStatusCode() : " + statusCd, 1, "_"+statusCd);
		    }
	    
    	}catch(Exception e){    
    		sendLog("### 에러 ###\r\n" + e.getMessage());
    		
    		e.printStackTrace();
    	}finally{
	    	return res;
    	}
    }
    public static JSONArray getLogFileList(String storePath){
    	JSONArray arr = new JSONArray();
    	
    	System.out.println("logPath : " + logPath);
    	File dir = new File(logPath);
    	File files[] = dir.listFiles();
    	
    	for(int i=0 ; i<files.length; i++){
    		File file = files[i];
    		JSONObject obj = new JSONObject();
    		obj.put("rowNum", i + 1);
    		obj.put("logFile", file.getName());
    		obj.put("logFilePath", file.getPath());
    		arr.add(obj);
    	}
    	
    	return arr;
    }
    
    //JSONArr 를 List로 변환
    public static List<HashMap<String, String>> convertJsonArrToList(JSONArray arr){
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i=0 ; i<arr.size(); i++) {
        	JSONObject obj = arr.getJSONObject(i);
            list.add(convertJSonObjToHashMap(obj));
        }
    	
    	return list;
    }
    
    //JSONObject 를 HashMap 으로 변환
    public static HashMap<String, String> convertJSonObjToHashMap(JSONObject obj){
    	HashMap<String, String> map = new HashMap<String, String>();
    	for(Iterator<Object> it=obj.keys();it.hasNext();){
    	    Object key=it.next();
    	    if(obj.getString(key.toString())!=null){
    	    	map.put(key.toString(), obj.getString(key.toString()));
    	    }
    	}
    	
    	return map;
    }
    
  //JSONArr 를 List로 변환
    public static List<HashMap<String, String>> convertJsonArrToListQry(JSONArray arr, String table){
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i=0 ; i<arr.size(); i++) {
        	JSONObject obj = arr.getJSONObject(i);
            list.add(convertJSonObjToHashMapQry(obj, table));
        }
    	
    	return list;
    }
    
    //JSONObject 를 HashMap 으로 변환
    public static HashMap<String, String> convertJSonObjToHashMapQry(JSONObject obj, String table){
    	
    	String qry = "";
    	HashMap<String, String> map = new HashMap<String, String>();
    	String selQry = "";
    	String upQry = "";
    	String inQryCal = "";
    	String inQryVal = "";
    	for(Iterator<Object> it=obj.keys();it.hasNext();){
    	    Object key=it.next();
    	    if(obj.getString(key.toString())!=null){
    	    	String keyStr = key.toString();
    	    	String valStr = obj.getString(key.toString()).replaceAll("'", "''");
    	    	
    	    	if(!"rn".equals(keyStr)){ //rn이 붙어나와 제외
    	    		if(!"null".equals(valStr)){ //timestamp 같은 경우 null로 들어감
    	    			if("".equals(selQry)){
    	    				if(valStr.getBytes().length > 250){
    	    					selQry += " '" + new String(valStr.getBytes(), 0, 250) + "' AS " + keyStr + " ";
    	    				}else{
    	    					selQry += " '" + valStr + "' AS " + keyStr + " ";
    	    				}
            	    	}else{
            	    		selQry += " , '" + valStr + "' AS " + keyStr + " ";
            	    	}
            	    	
            	    	if("".equals(upQry)){
            	    		upQry += " " + keyStr + "='" + valStr + "' ";
            	    	}else{
            	    		upQry += " , " + keyStr + "='" + valStr + "' ";
            	    	}
            	    	
            	    	if("".equals(inQryCal)){
            	    		inQryCal += " " + keyStr + " ";
            	    	}else{
            	    		inQryCal += " , " + keyStr + " ";
            	    	}
            	    	
            	    	if("".equals(inQryVal)){
            	    		inQryVal += " '" + valStr + "' ";
            	    	}else{
            	    		inQryVal += " , '" + valStr + "' ";
            	    	}
    	    		}
    	    	}
    	    }
    	}
    	 
    	String wh = "";
    	if(!"".equals(selQry)){
    		wh = getTableKeyWhere(table);
    		qry += "MERGE INTO " + table + " T1 ";
        	qry += " USING ( SELECT ";
        	qry += selQry;
        	qry += "  FROM DB_ROOT ) T2 ";
        	qry += " ON (";
        	qry += wh;
        	qry += ")";
        	qry += " WHEN MATCHED THEN UPDATE SET ";
        	qry += upQry;
        	qry += " WHEN NOT MATCHED THEN INSERT (";
        	qry += inQryCal;
        	qry += ") VALUES (";
        	qry += inQryVal;
        	qry += ")";
    	}
    	if("".equals(wh)){
    		map.put("upsertQry", " -----  " + table + "  where절 없음");
    	}else{
    		map.put("upsertQry", qry);
    	}
    	return map;
    }
    
    public static String getTableKeyWhere(String table){
    	String qry = "";
    	
    	if("TBM_BGTDGR".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr";
    	}else if("TBM_BGTDGRGOVOFFICE".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.gofCd = T2.gofCd";
    	}else if("TBM_BGTDGROFFICE".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.slngkCd = T2.slngkCd";
    	}else if("TBM_BGTDGRDEPT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.deptCd = T2.deptCd";
    	}else if("TBM_BGTDGRCHR".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.cplLvlNo = T2.cplLvlNo";
    	}else if("TBM_TECOMPO".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.aebcSnum = T2.aebcSnum";
    	}else if("TBM_TECOMPOFRSC".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.aebcSnum = T2.aebcSnum AND T1.frscDvCd = T2.frscDvCd";
    	}else if("TBI_YEARSECTION".equals(table)){
    		qry = "T1.fyr = T2.fyr AND T1.sectCd = T2.sectCd";
    	}else if("TBI_YEARFIELD".equals(table)){
    		qry = "T1.fyr = T2.fyr AND T1.fldCd = T2.fldCd";
    	}else if("TBI_YEARTEMNGMOK".equals(table)){
    		qry = "T1.fyr = T2.fyr AND T1.aneStmkCd = T2.aneStmkCd";
    	}else if("TBI_YEARFISFG".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.acntDvCd = T2.acntDvCd";
    	}else if("TBI_FRSC".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.frscDvCd = T2.frscDvCd";
    	}else if("TBI_STANDFRSC".equals(table)){
    		qry = "T1.stdFrscCd = T2.stdFrscCd";
    	}else if("TBI_FISFGMASTER".equals(table)){
    		qry = "T1.acntDvMstrCd = T2.acntDvMstrCd";
    	}else if("TBI_FISFG".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.acntDvCd = T2.acntDvCd";
    	}else if("TBI_COMMCDDETL".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.comClsCd = T2.comClsCd AND T1.comDtsCd = T2.comDtsCd";
    	}else if("TFP_YEARBIZ".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bizCd = T2.bizCd";
    	}else if("TFP_BIZ_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bizCd = T2.bizCd AND T1.bizCnttSnum = T2.bizCnttSnum";
    	}else if("TFP_POLIBIZ_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bizCd = T2.bizCd AND T1.bizCnttSnum = T2.bizCnttSnum";
    	}else if("TFP_UNITBIZ_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bizCd = T2.bizCd AND T1.bizCnttSnum = T2.bizCnttSnum";
    	}else if("TFP_DETLBIZ_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bizCd = T2.bizCd AND T1.bizCnttSnum = T2.bizCnttSnum";
  //cron  		
    	}else if("TCM_INSPECTION".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.ctrtLdgrMngNo = T2.ctrtLdgrMngNo AND T1.exntSnum = T2.exntSnum";
    	}else if("TEM_PAYCMD".equals(table)){
    		qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.acntDvCd = T2.acntDvCd AND T1.expsDvCd = T2.expsDvCd AND T1.pmodNo = T2.pmodNo";
    	}else if("TBI_SYSENVSETTING".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.sysEnvStngAtcCd = T2.sysEnvStngAtcCd";
    	}else if("TCM_COCTRT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.ctrtLdgrMngNo = T2.ctrtLdgrMngNo";
    		qry += " AND T1.ctrtEnpTyCd = T2.ctrtEnpTyCd";
    		qry += " AND T1.ctrtEnpSnum = T2.ctrtEnpSnum";
    	}else if("TCM_CTRTBOOKS".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.ctrtLdgrMngNo = T2.ctrtLdgrMngNo";
    	}else if("TCM_NOTICE".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.ctrtLdgrMngNo = T2.ctrtLdgrMngNo";
    	}else if("TBI_DEPT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.deptCd = T2.deptCd";
    	}else if("TBI_GOVOFFICE".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.gofCd = T2.gofCd";
    	}else if("TBC_CLOSEDGR".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.stlDvCd = T2.stlDvCd";
    		qry += " AND T1.bdgStlDgr = T2.bdgStlDgr";
    	}else if("TBC_TEBGTCLOS".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.stlDvCd = T2.stlDvCd";
    		qry += " AND T1.bdgStlDgr = T2.bdgStlDgr";
    		qry += " AND T1.acntDvCd = T2.acntDvCd";
    		qry += " AND T1.deptCd = T2.deptCd";
    		qry += " AND T1.dbizCd = T2.dbizCd";
    		qry += " AND T1.dbizHistSnum = T2.dbizHistSnum";
    		qry += " AND T1.aneStmkCd = T2.aneStmkCd";
    	}else if("TBI_FIELD".equals(table)){
    		qry = "T1.fldCd = T2.fldCd";
    	}else if("TBI_SECTION".equals(table)){
    		qry = "T1.sectCd = T2.sectCd";
    	}else if("TBM_ALLO".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.bdgBfwDvCd = T2.bdgBfwDvCd";
    		qry += " AND T1.dbizCd = T2.dbizCd";
    		qry += " AND T1.bdacSnum = T2.bdacSnum";
    	}else if("TBM_FRSCCURRAMT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.camtOcrnSnum = T2.camtOcrnSnum";
    		qry += " AND T1.frscDvCd = T2.frscDvCd";
    	}else if("TBM_TECURRAMT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.camtOcrnSnum = T2.camtOcrnSnum";
    	}else if("TFP_BIZDETL_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += " AND T1.fyr = T2.fyr";
    		qry += " AND T1.bizCd = T2.bizCd";
    		qry += " AND T1.bizCnttSnum = T2.bizCnttSnum";
    	}else if("TBI_HANG".equals(table)){
    		qry = "T1.hangCd = T2.hangCd";
    	}else if("TBI_JANG".equals(table)){
    		qry = "T1.jangCd = T2.jangCd";
    	}else if("TBI_KWAN".equals(table)){
    		qry = "T1.gyanCd = T2.gyanCd";
    	}else if("TBI_REVMOK".equals(table)){
    		qry = "T1.armkCd = T2.armkCd";
    	}else if("TBI_YEARHANG".equals(table)){
    		qry = "T1.fyr = T2.fyr";
    		qry += addQueryCondi("hangCd");
    	}else if("TBI_YEARJANG".equals(table)){
    		qry = "T1.fyr = T2.fyr";
    		qry += addQueryCondi("jangCd");
    	}else if("TBI_YEARKWAN".equals(table)){
    		qry = "T1.fyr = T2.fyr";
    		qry += addQueryCondi("gyanCd");
    	}else if("TBI_YEARREVMOK".equals(table)){
    		qry = "T1.fyr = T2.fyr";
    		qry += addQueryCondi("armkCd");
    	}else if("TEF_TRNS_REVINFO".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    		qry += addQueryCondi("txrvYmd");
    		qry += addQueryCondi("dataCrtSnum");
    		qry += addQueryCondi("armkCd");
    	}else if("TFM_REVINFO".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    		qry += addQueryCondi("txrvYmd");
    		qry += addQueryCondi("armkCd");
    	}else if("TBI_CUST".equals(table)){
    		qry = "T1.cltCd = T2.cltCd";
    	}else if("TBI_DEPT_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("deptCd");
    		qry += addQueryCondi("histSnum");
    	}else if("TEM_EXPDRESOLBGTACCT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    		qry += addQueryCondi("eprsSnum");
    		qry += addQueryCondi("deptCd");
    		qry += addQueryCondi("aneStmkCd");
    	}else if("TBI_USER".equals(table)){
    		qry = "T1.usrId = T2.usrId";
    	}else if("TCM_CTRTRELDEPT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("supvDeptCd");
    	}else if("TCM_CTRTSUPERV".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("supvDeptCd");
    		qry += addQueryCondi("spvsrUsrId");
    	}else if("TCM_FLDAGNT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("siteTagnSnum");
    	}else if("TCM_SUBCTRT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("sbcNo");
    	}else if("TCM_THISYCTRTITEM".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("ctrtDgr");
    	}else if("TBI_LOWDEPT".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("deptCd");
    		qry += addQueryCondi("lwrDeptCd");
    	}else if("TBI_OFFICE".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("slngkCd");
    	}else if("TBI_POSCL".equals(table)){
    		qry = "T1.posCd = T2.posCd";
    	}else if("TCM_INSPECTIONACCTS".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("ctrtLdgrMngNo");
    		qry += addQueryCondi("exntSnum");
    		qry += addQueryCondi("ecaSnum");
    		qry += addQueryCondi("epcsSnum");
    		qry += addQueryCondi("dmndSnum");
    	}else if("TEM_EXPDRESOLUTION".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    		qry += addQueryCondi("deptCd");
    		qry += addQueryCondi("eprsSnum");
    	}else if("TBI_YEARFISFG".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    	}else if("TBI_YEARTEMNGMOK".equals(table)){
    		qry = "T1.fyr = T2.fyr";
    		qry += addQueryCondi("aneStmkCd");
    	}else if("TFP_BIZ_H".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("bizCd");
    		qry += addQueryCondi("bizCnttSnum");
    	}else if("TCM_COMMCDDETL".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("comClsCd");
    		qry += addQueryCondi("comDtsCd");
    	}else if("TEM_EXPDRESOLCRDITOR".equals(table)){
    		qry = "T1.lafCd = T2.lafCd";
    		qry += addQueryCondi("fyr");
    		qry += addQueryCondi("acntDvCd");
    		qry += addQueryCondi("deptCd");
    		qry += addQueryCondi("eprsSnum");
    		qry += addQueryCondi("eprsCdrSnum");
    		
   		//}else if("".equals(table)){
    		//qry = "T1.lafCd = T2.lafCd";
    		//qry += addQueryCondi("fyr");
   		//}else if("".equals(table)){
    		//qry = "T1.lafCd = T2.lafCd AND T1.fyr = T2.fyr AND T1.bdgDgr = T2.bdgDgr AND T1.gofCd = T2.gofCd";
    	}else{
    		//qry = "T1.lafCd = T2.lafCd";
    	}
    	
    	return qry;
    }
    
    public static String addQueryCondi(String col){
    	return " AND T1." + col + " = T2." + col;
    }
    
    public static JSONArray makeTestData(){
    	
    	JSONArray arr = new JSONArray();
    	
    	JSONObject obj = new JSONObject();
    	obj.put("stdFrscCd", "11");    	
    	obj.put("stdFrscNm", "2132");    	
    	obj.put("lupOrd", 3);    	
    	obj.put("useYn", "41");    	
    	obj.put("frstRgstrUsrId", "5");    	
    	obj.put("frstRgstrDt", "2022-12-01 10:22:53");    	
    	obj.put("lastMdfcnUsrId", "6");    	
    	obj.put("lastMdfcnDt", "2022-12-02 10:22:53");    	
    	arr.add(obj);
    	
    	obj = new JSONObject();
    	obj.put("stdFrscCd", "7");    	
    	obj.put("stdFrscNm", "8");    	
    	obj.put("lupOrd", 9);    	
    	obj.put("useYn", "10");    	
    	obj.put("frstRgstrUsrId", "11");    	
    	obj.put("frstRgstrDt", "2022-12-03 10:22:53");    	
    	obj.put("lastMdfcnUsrId", "12");    	
    	obj.put("lastMdfcnDt", "2022-12-04 10:22:53");    	
    	arr.add(obj);
    	
    	
    	return arr;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sendLog(String log){

    	int i                 = 0;
        String stPath         = "";
        String stFileName     = "";
       
        String m_PathName = logPath;  
        
        File saveFolder = new File(m_PathName);

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        stPath     = m_PathName;
        
        stFileName = m_FileName;
        
        SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat ("HH:mm:ss");
       
        String stDate = formatter1.format(new Date());
        String stTime = formatter2.format(new Date());
        StringBuffer bufLogPath  = new StringBuffer();      
                     bufLogPath.append(stPath);
                     bufLogPath.append(stDate);
                     bufLogPath.append("_");
                     bufLogPath.append(stFileName);
                     bufLogPath.append(".log") ;
                     
        StringBuffer bufLogMsg = new StringBuffer();
            bufLogMsg.append("[");
            bufLogMsg.append(stTime);
            //bufLogMsg.append("]\r\n");            
            bufLogMsg.append("]");            
            bufLogMsg.append(log);
                    
        try{
        	objfile = new FileWriter(bufLogPath.toString(), true);
            objfile.write(bufLogMsg.toString());
            objfile.write("\r\n");
        } catch(IOException e){
           
        } finally {
            try{
             objfile.close();
            }catch(Exception e1){}
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sendQryLog(String log, int num){

    	int i                 = 0;
        String stPath         = "";
        String stFileName     = "";
       
        String m_PathName = logPath;  
        
        File saveFolder = new File(m_PathName);

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        stPath     = m_PathName;
        
        stFileName = m_QryFileName;
        SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat ("HH:mm:ss");
       
        String stDate = formatter1.format(new Date());
        String stTime = formatter2.format(new Date());
        StringBuffer bufLogPath  = new StringBuffer();      
                     bufLogPath.append(stPath);
                     bufLogPath.append(stDate);
                     bufLogPath.append("_");
                     bufLogPath.append(stFileName);
                     bufLogPath.append(".log") ;
                     
                     
        StringBuffer bufLogMsg = new StringBuffer();
        if(num == 0){
        	bufLogMsg.append("[");
            bufLogMsg.append(stTime);
            bufLogMsg.append("]");
        }
            bufLogMsg.append(log);
                    
        try{
        	objfile = new FileWriter(bufLogPath.toString(), true);
            objfile.write(bufLogMsg.toString());
            objfile.write("\r\n");
        } catch(IOException e){
           
        } finally {
            try{
             objfile.close();
            }catch(Exception e1){}
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sendEtcLog(String log, int num, String fileNm){

    	int i                 = 0;
        String stPath         = "";
        String stFileName     = "";
       
        String m_PathName = logPath;  
        
        File saveFolder = new File(m_PathName);

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        stPath     = m_PathName;
        
        stFileName = fileNm;
        SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat ("HH:mm:ss");
       
        String stDate = formatter1.format(new Date());
        String stTime = formatter2.format(new Date());
        StringBuffer bufLogPath  = new StringBuffer();      
                     bufLogPath.append(stPath);
                     bufLogPath.append(stDate);
                     bufLogPath.append("_");
                     bufLogPath.append(stFileName);
                     bufLogPath.append(".log") ;
                     
                     
        StringBuffer bufLogMsg = new StringBuffer();
        if(num == 0){
        	bufLogMsg.append("[");
            bufLogMsg.append(stTime);
            bufLogMsg.append("]");
        }
            bufLogMsg.append(log);
                    
        try{
        	objfile = new FileWriter(bufLogPath.toString(), true);
            objfile.write(bufLogMsg.toString());
            objfile.write("\r\n");
        } catch(IOException e){
           
        } finally {
            try{
             objfile.close();
            }catch(Exception e1){}
        }
    }
    
}
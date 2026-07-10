package com.cs.bcjis.dialog.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cs.bcjis.comm.util.BcjisNumberUtil;
import com.cs.bcjis.comm.util.BcjisStringUtil;
import com.cs.bcjis.comm.util.ExcelReadOption;
import com.cs.bcjis.comm.web.BcjisUserVO;
import com.cs.bcjis.dialog.service.DialogPledgeInfoService;

import egovframework.rte.fdl.cmmn.exception.FdlException;
import egovframework.rte.fdl.idgnr.EgovIdGnrService;
import egovframework.rte.fdl.string.EgovStringUtil;
import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service("dialogPledgeInfoService")
public class DialogPledgeInfoServiceImpl implements DialogPledgeInfoService {
    @Resource(name = "dialogPledgeInfoDAO")
    private DialogPledgeInfoDAO dialogPledgeInfoDAO;
    
    @Resource(name = "dialogPledgeBizDAO")
    private DialogPledgeBizDAO dialogPledgeBizDAO;

    @Autowired
    @Qualifier("config")
    private Properties config;
    
    @Resource(name = "csPledgeBizIdGnrService")
    private EgovIdGnrService csPledgeBizIdGnrService;
    
    @SuppressWarnings("rawtypes")
    public void insertPledgeInfo(Map map) throws Exception {
        dialogPledgeInfoDAO.insertPledgeInfo(map);
    }

    @SuppressWarnings("rawtypes")
    public void updatePledgeInfo(Map map) throws Exception {
        dialogPledgeInfoDAO.updatePledgeInfo(map);
    }
    
    @SuppressWarnings("rawtypes")
    public EgovMap excelUploadPledgeInfo(MultipartFile multi, BcjisUserVO userVO) throws Exception {
    	EgovMap resultMap = new EgovMap();

    	/*Iterator<String> fileIter = mptRequest.getFileNames();
    	MultipartFile multi = mptRequest.getFile(String.valueOf(fileIter.next()));*/
    	String filePath = saveFile(multi);
    	boolean flag = true;
        String message = "";
    	if(!"".equals(filePath)){
    		
    		ExcelReadOption excelReadOption = new ExcelReadOption();
    		excelReadOption.setFilePath(filePath);
    		excelReadOption.setOutputColumns("A","B","C","D","E","F");
            excelReadOption.setStartRow(2);

    		Workbook wb = getWorkbook(filePath);
    		/**
             * 엑셀 파일에서 첫번째 시트를 가지고 온다.
             */
            Sheet sheet = wb.getSheetAt(0);
            
            System.out.println("Sheet 이름: "+ wb.getSheetName(0)); 
            System.out.println("데이터가 있는 Sheet의 수 :" + wb.getNumberOfSheets());
            /**
             * sheet에서 유효한(데이터가 있는) 행의 개수를 가져온다.
             */
            int numOfRows = sheet.getPhysicalNumberOfRows();
            int numOfCells = 0;
            
            Row row = null;
            Cell cell = null;
            
            String cellName = "";
            /**
             * 각 row마다의 값을 저장할 맵 객체
             * 저장되는 형식은 다음과 같다.
             * put("A", "이름");
             * put("B", "게임명");
             */
            EgovMap map = null;
            /*
             * 각 Row를 리스트에 담는다.
             * 하나의 Row를 하나의 Map으로 표현되며
             * List에는 모든 Row가 포함될 것이다.
             */
            List<EgovMap> result = new ArrayList<EgovMap>(); 
            /**
             * 각 Row만큼 반복을 한다.
             */
            for(int rowIndex = excelReadOption.getStartRow() - 1; rowIndex < numOfRows; rowIndex++) {
                /*
                 * 워크북에서 가져온 시트에서 rowIndex에 해당하는 Row를 가져온다.
                 * 하나의 Row는 여러개의 Cell을 가진다.
                 */
                row = sheet.getRow(rowIndex);
                
                if(row != null) {
                    /*
                     * 가져온 Row의 Cell의 개수를 구한다.
                     */
                    //numOfCells = row.getPhysicalNumberOfCells();
                	//6개로 고정
                    numOfCells = 6;
                    /*
                     * 데이터를 담을 맵 객체 초기화
                     */
                    map = new EgovMap();
                    /*
                     * cell의 수 만큼 반복한다.
                     */
                    for(int cellIndex = 0; cellIndex < numOfCells; cellIndex++) {
                        /*
                         * Row에서 CellIndex에 해당하는 Cell을 가져온다.
                         */
                        cell = row.getCell(cellIndex);
                        /*
                         * 현재 Cell의 이름을 가져온다
                         * 이름의 예 : A,B,C,D,......
                         */
                        cellName = getName(cell, cellIndex);
                        /*
                         * 추출 대상 컬럼인지 확인한다
                         * 추출 대상 컬럼이 아니라면, 
                         * for로 다시 올라간다
                         */
                        if( !excelReadOption.getOutputColumns().contains(cellName) ) {
                            continue;
                        }
                        /*
                         * map객체의 Cell의 이름을 키(Key)로 데이터를 담는다.
                         */
                        //System.out.println("numOfCells : " + numOfCells + "   cellName : " + cellName + "  rowIndex : " + rowIndex + "  cellIndex : " + cellIndex + "  cellValue :  " + getValue(cell));
                        map.put(cellName, getValue(cell));
                    }
                    /*
                     * 만들어진 Map객체를 List로 넣는다.
                     */
                    result.add(map);
                    
                }
                
            }
            
            //System.out.println("============== before ==============");
            //상위 키 가져오기 (상위키가 없으면 상위 rownumber 넣기)
            int listSize = result.size();
            boolean forFlag = true;
            for(int i=0 ; i<listSize ; i++){
        		EgovMap emap = result.get(i);
            	System.out.println("i : " + i + "  emap : " + emap);
            	String pledgeBizId = BcjisStringUtil.nullConvert(emap.get("b"));
            	String upPledgeBizId = BcjisStringUtil.nullConvert(emap.get("c"));
            	String levelStr = BcjisStringUtil.nullConvert(emap.get("d"));
            	if("".equals(levelStr)){
            		forFlag = false;
            		//System.out.println("중단 listsize : " + listSize + " i : " + i);
            		listSize = i;
            	}else{
            		int level = BcjisNumberUtil.nullConvertToInt(emap.get("d"));
                	String pledgeBizFg = BcjisStringUtil.nullConvert(emap.get("e"));
                	String pledgeBizNm = BcjisStringUtil.nullConvert(emap.get("f"));
                	
                	//아이디가 빈칸인 경우에만 실행
                	if("".equals(pledgeBizId)){
                		//상위 데이터 가져오기
                		result = setPlegeData(result, i);
                	}
            	}
            	
            }
            
            //System.out.println("============== after ==============");
            //제대로 가져왔는지 검증
            for(int i=0 ; i<listSize ; i++){
            	EgovMap emap = result.get(i);
            	String pledgeBizId = BcjisStringUtil.nullConvert(emap.get("b"));
            	String upPledgeBizId = BcjisStringUtil.nullConvert(emap.get("c"));
            	int level = BcjisNumberUtil.nullConvertToInt(emap.get("d"));
            	String pledgeBizFg = BcjisStringUtil.nullConvert(emap.get("e"));
            	String pledgeBizNm = BcjisStringUtil.nullConvert(emap.get("f"));
            	
            	//입력할 데이터
            	if("".equals(pledgeBizId)){
            		//상위 키가 없는경우
            		if("".equals(upPledgeBizId)){
            			int uppRow = BcjisNumberUtil.nullConvertToInt(emap.get("uppRow"));
            			if(uppRow == 0){
            				flag = false;
            				message += i + "번째 열의 상위 코드를 가져올수 없습니다.";
            			}
            			
            		}
            	}
            	
            	System.out.println("i : " + i + "  emap : " + emap);
            }
            
            System.out.println("flag : " + flag + "  message : " + message);
            //정상데이터 일경우 입력
            if(flag){
            	String userId = userVO.getUserId();
            	int cnt = insertPlegeExcelData(result, userId, listSize);
            	message = cnt + "건 데이터를 업로드 하였습니다.";
            }
            
    	}else{
    		flag = false;
            message = "첨부파일 업로드에 실패하였습니다.";
    	}
    	
    	if(flag){
    		resultMap.put("flag", "true");
    	}else{
    		resultMap.put("flag", "false");
    	}
    	
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    //데이터 insert
    private int insertPlegeExcelData(List<EgovMap> list, String userId, int listSize) throws FdlException{
    	
    	int cnt = 0;
    	for(int i=1 ; i<listSize ; i++){
    		EgovMap emap = list.get(i);
    		String pledgeInfoId = BcjisStringUtil.nullConvert(emap.get("a"));
    		String pledgeBizId = BcjisStringUtil.nullConvert(emap.get("b"));
        	String upPledgeBizId = BcjisStringUtil.nullConvert(emap.get("c"));
        	int level = BcjisNumberUtil.nullConvertToInt(emap.get("d"));
        	String pledgeBizFg = BcjisStringUtil.nullConvert(emap.get("e"));
        	String pledgeBizNm = BcjisStringUtil.nullConvert(emap.get("f"));
        	System.out.println("insert i : " + i + "  emap : " + emap);
        	//아이디가 빈경우에만 insert
        	if("".equals(pledgeBizId)){
        		pledgeBizId = csPledgeBizIdGnrService.getNextStringId();
        		emap.put("b", pledgeBizId);
        		//상위코드가 없는 경우 지정해놓은 상위항목에서 코드를 가져온다.
        		if("".equals(upPledgeBizId)){
        			int uppRow = BcjisNumberUtil.nullConvertToInt(emap.get("uppRow"));
        			EgovMap emapTmp = list.get(uppRow);
        			String pledgeBizIdTmp = BcjisStringUtil.nullConvert(emapTmp.get("b"));
        			upPledgeBizId = pledgeBizIdTmp;
        			emap.put("c", upPledgeBizId);
        		}
        		
        		if(!"".equals(pledgeBizId)
        				&& !"".equals(pledgeInfoId)
        				&& !"".equals(upPledgeBizId)
        				&& level != 0
        				&& !"".equals(pledgeBizFg)
        				&& !"".equals(pledgeBizNm)
        				){
        			JSONObject param = new JSONObject();
            		param.put("pledgeBizId", pledgeBizId);
            		param.put("upPledgeBizId", upPledgeBizId);
            		param.put("pledgeBizFg", pledgeBizFg);
            		param.put("pledgeBizNm", pledgeBizNm);
            		param.put("pledgeBizLevel", level);
            		param.put("pledgeInfoId", pledgeInfoId);
            		param.put("userId", userId);
            		
            		System.out.println("insert data : " + param.toString());
            		
            		dialogPledgeBizDAO.insertPledgeBiz(param);
            		cnt++;
        		}
        		
        	}
    	}
    	
    	return cnt;
    }
    
    //데이터 세팅(상위코드가 있으면 넣고 없으면 상위항목의 번호를 추가)
    private List<EgovMap> setPlegeData(List<EgovMap> list, int rowNum){
    	//System.out.println("setPlegeData rowNum : " + rowNum );
    	EgovMap emap = list.get(rowNum);
    	String pledgeBizId = BcjisStringUtil.nullConvert(emap.get("b"));
    	String upPledgeBizId = BcjisStringUtil.nullConvert(emap.get("c"));
    	int level = BcjisNumberUtil.nullConvertToInt(emap.get("d"));
    	String pledgeBizFg = BcjisStringUtil.nullConvert(emap.get("e"));
    	String pledgeBizNm = BcjisStringUtil.nullConvert(emap.get("f"));
    	
    	//상위 데이터 가져오기
    	int uppRow = getUpperRowData(list, rowNum, level);
    	//System.out.println("getUpperRowData rowNum : " + uppRow + "  level : " + level);
    	if(uppRow != 0){
    		EgovMap uppMap = list.get(uppRow);
			String pledgeBizIdUpp = BcjisStringUtil.nullConvert(uppMap.get("b"));
	    	String upPledgeBizIdUpp = BcjisStringUtil.nullConvert(uppMap.get("c"));
	    	int levelUpp = BcjisNumberUtil.nullConvertToInt(uppMap.get("d"));
	    	//아이디가 빈칸인 경우
	    	if("".equals(pledgeBizIdUpp)){
	    		emap.put("uppRow", uppRow);
	    		setPlegeData(list, uppRow);
	    	}else{
	    		//System.out.println("upPledgeBizId : " + pledgeBizIdUpp);
	    		emap.put("c", pledgeBizIdUpp);
	    	}
    	}
    	
    	return list;
    }
    
    //상위 항목 번호를 가져온다
    private int getUpperRowData(List<EgovMap> list, int rowNum, int lv){
    	
    	for(int i=rowNum ; i>0 ; i--){
    		EgovMap emap = list.get(i);
        	String pledgeBizId = BcjisStringUtil.nullConvert(emap.get("b"));
        	String upPledgeBizId = BcjisStringUtil.nullConvert(emap.get("c"));
        	int level = BcjisNumberUtil.nullConvertToInt(emap.get("d"));
        	String pledgeBizFg = BcjisStringUtil.nullConvert(emap.get("e"));
        	String pledgeBizNm = BcjisStringUtil.nullConvert(emap.get("f"));
        	//System.out.println("  rowNum : " + rowNum + "  i : " + i + "   lv : " + lv + "   level : " + level + "   lv : " + lv);
        	if(i != 0 && level == (lv-1)){
        		return i;
        	}
    	}
    	
    	return 0;
    }
    
    
    private String saveFile(MultipartFile multi){
    	
    	String filePath = "";
    	try {
   		 
            //String uploadpath = request.getServletContext().getRealPath(path);
            String uploadpath = config.getProperty("Globals.fileStorePath");;
            String originFilename = multi.getOriginalFilename();
            String extName = originFilename.substring(originFilename.lastIndexOf("."),originFilename.length());
            long size = multi.getSize();
            String saveFileName = genSaveFileName(extName);
            
            System.out.println("uploadpath : " + uploadpath);
            
            System.out.println("originFilename : " + originFilename);
            System.out.println("extensionName : " + extName);
            System.out.println("size : " + size);
            System.out.println("saveFileName : " + saveFileName);
            
            if(!multi.isEmpty())
            {
                File file = new File(uploadpath, multi.getOriginalFilename());
                multi.transferTo(file);
                
                filePath = file.getAbsolutePath();
                //model.addAttribute("filename", multi.getOriginalFilename());
                //model.addAttribute("uploadPath", file.getAbsolutePath());
                
                //return "filelist";
            }
        }catch(Exception e)
        {
            System.out.println(e);
        }
    	
    	return filePath;
    }
    
    //현재 시간을 기준으로 파일 이름 생성
    private String genSaveFileName(String extName) {
        String fileName = "";
        
        Calendar calendar = Calendar.getInstance();
        fileName += calendar.get(Calendar.YEAR);
        fileName += calendar.get(Calendar.MONTH);
        fileName += calendar.get(Calendar.DATE);
        fileName += calendar.get(Calendar.HOUR);
        fileName += calendar.get(Calendar.MINUTE);
        fileName += calendar.get(Calendar.SECOND);
        fileName += calendar.get(Calendar.MILLISECOND);
        fileName += extName;
        
        return fileName;
    }

    
    public static Workbook getWorkbook(String filePath) {
        
        /*
         * FileInputStream은 파일의 경로에 있는 파일을
         * 읽어서 Byte로 가져온다.
         * 
         * 파일이 존재하지 않는다면은
         * RuntimeException이 발생된다.
         */
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        Workbook wb = null;
        
        /*
         * 파일의 확장자를 체크해서 .XLS 라면 HSSFWorkbook에
         * .XLSX라면 XSSFWorkbook에 각각 초기화 한다.
         */
        if(filePath.toUpperCase().endsWith(".XLS")) {
            try {
                wb = new HSSFWorkbook(fis);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        else if(filePath.toUpperCase().endsWith(".XLSX")) {
            try {
                wb = new XSSFWorkbook(fis);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        return wb;
        
    }
    
    /**
     * Cell에 해당하는 Column Name을 가젼온다(A,B,C..)
     * 만약 Cell이 Null이라면 int cellIndex의 값으로
     * Column Name을 가져온다.
     * @param cell
     * @param cellIndex
     * @return
     */
    public static String getName(Cell cell, int cellIndex) {
        int cellNum = 0;
        if(cell != null) {
            cellNum = cell.getColumnIndex();
        }
        else {
            cellNum = cellIndex;
        }
        
        return CellReference.convertNumToColString(cellNum);
    }
    
    public static String getValue(Cell cell) {
        String value = "";
        
        if(cell == null) {
            value = "";
        }
        else {
            if( cell.getCellType() == Cell.CELL_TYPE_FORMULA ) {
                value = cell.getCellFormula();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
                value = (int)BcjisNumberUtil.nullConvertToDouble(cell.getNumericCellValue()) + "";
                //value = cell.getNumericCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_STRING ) {
                value = cell.getStringCellValue();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ) {
                value = cell.getBooleanCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_ERROR ) {
                value = cell.getErrorCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BLANK ) {
                value = "";
            }
            else {
                value = cell.getStringCellValue();
            }
        }
        
        return value;
    }
    
}

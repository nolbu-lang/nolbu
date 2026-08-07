/*
 * 
 */
package com.cs.bcjis.comm.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cs.bcjis.comm.service.impl.BcjisCommDAO;
import com.cs.bcjis.comm.util.BcjisCommUtil;
import com.cs.bcjis.comm.web.BcjisUserVO;

import egovframework.rte.fdl.idgnr.EgovIdGnrService;

@Service("accessLogFilter")
public class AccessLogFilter implements Filter {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AccessLogFilter.class);
    
    private EgovIdGnrService traceLogIdStrategy;
    
    private BcjisCommDAO bcjisCommDAO;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - start");
        }

        HttpServletRequest hRequest = (HttpServletRequest) request;
        String userWhere = hRequest.getRequestURI().substring(hRequest.getContextPath().length());

        if(BcjisCommUtil.isNullString(userWhere) == true){
            chain.doFilter(request, response);

            if (logger.isDebugEnabled()) {
                logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
            }
            return;
        }

        if("/".equals(userWhere) == true 
                || userWhere.startsWith("/comm") == true
                || userWhere.startsWith("/login/ajaxLogin.do") == true
                || userWhere.startsWith("/login/logoutAction.do") == true){
            chain.doFilter(request, response);

            if (logger.isDebugEnabled()) {
                logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
            }
            return;
        }
        
        int actionIndex = userWhere.lastIndexOf("/");
        String userAction = "";
        if(actionIndex > 0){
            userAction = userWhere.substring(userWhere.lastIndexOf("/"));
        }
                
        BcjisUserVO bcjisUserVO = (BcjisUserVO)hRequest.getSession().getAttribute("bcjisUserVO");
        if(bcjisUserVO == null){
            if(userAction.indexOf("/ajax") == 0){
                hRequest.getRequestDispatcher("/comm/ajaxCommSessionExpired.do").forward(request, response);
            }else{
                hRequest.getRequestDispatcher("/").forward(request, response);
            }
            
            return;
        }
        
        
        try{
            JSONObject jsonParam = null;
            // Commons FileUpload 판별로 multipart 본문 소비을 확실히 건너뛴다.
            // (getContentType()만으로는 래퍼/charset 케이스에서 누락될 수 있음)
            boolean isMultipart = false;
            try {
                isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload
                        .isMultipartContent(hRequest);
            } catch (Exception ignore) {
                String contentType = hRequest.getContentType();
                isMultipart = contentType != null
                        && contentType.toLowerCase().indexOf("multipart/form-data") >= 0;
            }
            try{
                if (isMultipart) {
                    // multipart는 본문을 소비하면 파일 업로드가 실패하므로 JSON 파싱을 건너뛴다.
                    jsonParam = new JSONObject();
                    jsonParam.put("_multipart", "Y");
                    jsonParam.put("url", userWhere);
                } else {
                    StringBuffer sBuf = new StringBuffer();

                    BufferedReader reader = request.getReader();

                    char[] cBuf = new char[1024 * 8];
                    while (true) {
                        int length = reader.read(cBuf, 0, cBuf.length);
                        if (length < 0) {
                            break;
                        }

                        sBuf.append(String.valueOf(cBuf, 0, length));
                    }

                    jsonParam = JSONObject.fromObject(sBuf.toString().replaceAll("\n", "<BR>"));

                    BcjisCommUtil.setJsonObjectReturnString(jsonParam);
                }
            }catch(JSONException je){
                jsonParam = new JSONObject();
            }

            // 본문 스트림은 위에서 이미 소비되어 컨트롤러가 재차 읽을 수 없으므로,
            // 파싱 결과를 접속로그 적재(부가기능)보다 먼저 request 속성에 보관한다.
            // (multipart는 본문을 읽지 않았으므로 컨트롤러에서 MultipartResolver가 정상 동작한다.)
            hRequest.setAttribute(BcjisCommUtil.JSON_PARAM_NM, jsonParam);

            // 접속로그 적재는 부가기능이므로 실패(채번/DB 오류 등)하더라도 요청 처리는 계속 진행한다.
            // 심사조서 저장처럼 본문이 큰 요청은 전체 JSON 직렬화 대신 요약만 남겨 저장 지연을 줄인다.
            try{
                Map<String, String> map = new HashMap<String, String>();

                map.put("logId",  traceLogIdStrategy.getNextStringId());
                map.put("url", userWhere);
                map.put("userId", bcjisUserVO.getUserId());
                map.put("sessionId", hRequest.getSession().getId());
                map.put("reqParam", buildTraceReqParam(userWhere, jsonParam, isMultipart));
                bcjisCommDAO.insertTracelog(map);
            }catch(Exception logEx){
                logger.error("doFilter - 접속로그(tracelog) 적재 실패: 요청은 계속 진행함", logEx);
            }

        }catch(Exception e){
            logger.error("doFilter(ServletRequest, ServletResponse, FilterChain)", e);
            if(hRequest.getAttribute(BcjisCommUtil.JSON_PARAM_NM) == null){
                hRequest.setAttribute(BcjisCommUtil.JSON_PARAM_NM, new JSONObject());
            }
        }
        
        chain.doFilter(request, response);

        if (logger.isDebugEnabled()) {
            logger.debug("doFilter(ServletRequest, ServletResponse, FilterChain) - end");
        }
    }
    
    public void setCsTraceLogIdGnrService(EgovIdGnrService traceLogIdStrategy){
        this.traceLogIdStrategy = traceLogIdStrategy;
    }
    
    public void setBcjisCommDAO(BcjisCommDAO bcjisCommDAO){
        this.bcjisCommDAO = bcjisCommDAO;
    }

    /**
     * 접속로그 req_param 구성.
     * 저장 API는 검토내용 등 대용량 필드를 포함하므로 전체 toString()을 피하고 요약만 남긴다.
     */
    private String buildTraceReqParam(String userWhere, JSONObject jsonParam, boolean isMultipart) {
        if (jsonParam == null) {
            return "";
        }
        try {
            if (isMultipart) {
                return "{\"multipart\":\"Y\",\"url\":\"" + userWhere + "\"}";
            }
            boolean heavySave = userWhere != null
                    && (userWhere.indexOf("SaveReport") >= 0
                            || userWhere.indexOf("CopyReport") >= 0
                            || userWhere.indexOf("ajaxBudgetCopyNew") >= 0);
            if (heavySave) {
                StringBuilder sb = new StringBuilder(256);
                sb.append("{\"url\":\"").append(userWhere).append("\"");
                if (jsonParam.containsKey("saveDatas")) {
                    Object sd = jsonParam.get("saveDatas");
                    int cnt = (sd instanceof net.sf.json.JSONArray)
                            ? ((net.sf.json.JSONArray) sd).size() : 0;
                    sb.append(",\"saveDatasCnt\":").append(cnt);
                }
                if (jsonParam.containsKey("mappings")) {
                    Object md = jsonParam.get("mappings");
                    int cnt = (md instanceof net.sf.json.JSONArray)
                            ? ((net.sf.json.JSONArray) md).size() : 0;
                    sb.append(",\"mappingsCnt\":").append(cnt);
                }
                if (jsonParam.containsKey("reportCd")) {
                    sb.append(",\"reportCd\":\"").append(String.valueOf(jsonParam.get("reportCd"))).append("\"");
                }
                if (jsonParam.containsKey("fisYear")) {
                    sb.append(",\"fisYear\":\"").append(String.valueOf(jsonParam.get("fisYear"))).append("\"");
                }
                sb.append("}");
                return sb.toString();
            }
            byte[] reqParamByte = jsonParam.toString().getBytes();
            return new String(reqParamByte, 0, reqParamByte.length > 3500 ? 3500 : reqParamByte.length);
        } catch (Exception e) {
            return "{\"url\":\"" + userWhere + "\"}";
        }
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

}

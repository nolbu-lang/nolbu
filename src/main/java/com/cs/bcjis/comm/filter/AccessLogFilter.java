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
            try{
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
            }catch(JSONException je){
                jsonParam = new JSONObject();
            }

            Map<String, String> map = new HashMap<String, String>();

            map.put("logId",  traceLogIdStrategy.getNextStringId());
            map.put("url", userWhere);
            map.put("userId", bcjisUserVO.getUserId());
            map.put("sessionId", hRequest.getSession().getId());
            byte[] reqParamByte = jsonParam.toString().getBytes();
            map.put("reqParam", new String(reqParamByte, 0, reqParamByte.length > 3500 ? 3500 : reqParamByte.length));
            bcjisCommDAO.insertTracelog(map);

            hRequest.setAttribute(BcjisCommUtil.JSON_PARAM_NM, jsonParam);
            
        }catch(Exception e){
            logger.error("doFilter(ServletRequest, ServletResponse, FilterChain)", e);
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

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

}

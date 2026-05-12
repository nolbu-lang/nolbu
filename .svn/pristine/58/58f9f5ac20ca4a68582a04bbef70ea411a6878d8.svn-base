package com.cs.bcjis.comm.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

import egovframework.rte.fdl.cryptography.EgovCryptoService;

public class BcjisCommUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BcjisCommUtil.class);

    public final static String JSON_PARAM_NM = "bcjisJsonParameter";
    public final static String JSON_OBJCT_NM = "jsonObject";
    public final static String BCJIS_MESSAGE_FLAG = "bcjisMessageFlag";
    public final static String BCJIS_MESSAGE = "bcjisMessage";
    public final static String BCJIS_RETURN_CODE = "bcjisRtnCode";
    public final static String BCJIS_RETURN_CODE_SUCC = "SUCC";
    public final static String BCJIS_RETURN_CODE_ERR = "ERR";

    public static boolean isNullString(Object o) {
        if (o == null) {
            return true;
        }

        String s = String.valueOf(o);
        if ("null".equals(s) || s.trim().length() < 1) {
            return true;
        }

        return false;
    }

    public static String getStringParameter(HttpServletRequest request, String paramName) {
        String s = request.getParameter(paramName);

        return isNullString(s) == true ? "" : s;
    }

    public static String getStringParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String s = request.getParameter(paramName);

        return isNullString(s) == true ? defaultValue : s;
    }

    public static String getStringParameterEncode(HttpServletRequest request, String paramName) throws UnsupportedEncodingException {
        String s = request.getParameter(paramName);

        return isNullString(s) == true ? "" : URLDecoder.decode(s, "UTF-8");
    }

    public static String getStringParameterEncode(HttpServletRequest request, String paramName, String defaultValue) throws UnsupportedEncodingException {
        String s = request.getParameter(paramName);

        return isNullString(s) == true ? defaultValue : URLDecoder.decode(s, "UTF-8");
    }

    public static String getAttachFileName(String s) throws UnsupportedEncodingException {
        return isNullString(s) == true ? "" : s;

        // return isNullString(s) == true ? "" : new
        // String(s.getBytes("8859_1"), "UTF-8");
    }

    public static JSONObject getJsonObjectFromRequest(HttpServletRequest request) throws Exception {
        if(request.getAttribute(JSON_PARAM_NM) != null){
            return (JSONObject) request.getAttribute(JSON_PARAM_NM);
        }

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

        JSONObject jsonParam = JSONObject.fromObject(sBuf.toString().replaceAll("\n", "<BR>"));

        BcjisCommUtil.setJsonObjectReturnString(jsonParam);

        return jsonParam;
    }

    public static void setJsonObjectReturnString(JSONObject jObject) throws Exception{
        JSONArray names = jObject.names();
        String name = "";
        Object o;
        for (int i = 0; i < names.size(); i++) {
            name = String.valueOf(names.get(i));
            o = jObject.get(name);
            if (o instanceof String) {
                jObject.put(name, URLDecoder.decode(((String) o).replaceAll("<BR>", "\n"), "UTF-8"));
            } else if (o instanceof JSONObject) {
                setJsonObjectReturnString((JSONObject) o);
            } else if (o instanceof JSONArray) {
                setJosnArrayReturnString((JSONArray) o);
            }
        }
    }

    public static void setJosnArrayReturnString(JSONArray jsonArray) throws Exception{
        Object o;
        for (int i = 0; i < jsonArray.size(); i++) {
            o = jsonArray.get(i);
            if (o instanceof String) {
                o = URLDecoder.decode(((String) o).replaceAll("<BR>", "\n"), "UTF-8");
            } else if (o instanceof JSONObject) {
                setJsonObjectReturnString((JSONObject) o);
            } else if (o instanceof JSONArray) {
                setJosnArrayReturnString((JSONArray) o);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static JSONObject getJsonObjectFromSubmitRequest(HttpServletRequest request) throws Exception {
        JSONObject jsonParam = new JSONObject();

        Enumeration e = request.getParameterNames();
        String key = "";
        while (e.hasMoreElements()) {
            key = String.valueOf(e.nextElement());
            jsonParam.put(key, BcjisCommUtil.getStringParameter(request, key));
        }

        return jsonParam;
    }

    public static String getCurrentDate(String pattern) {

        String rtnStr = "";

        try {
            SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
            Timestamp ts = new Timestamp(System.currentTimeMillis());

            rtnStr = sdfCurrent.format(ts.getTime());
        } catch (Exception e) {

        }

        return rtnStr;
    }

    public static String getDateFormat(String s) {
        if (isNullString(s) == true) {
            return "";
        }

        s = s.replaceAll("-", "");
        if (s.length() < 4) {
            return s;
        }

        if (s.length() < 6) {
            return s.substring(0, 4) + "-" + s.substring(4);
        }

        return s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8);
    }

    public static String addYearMonthDay(String sDate, int year, int month, int day) {

        String dateStr = validChkDate(sDate);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            cal.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }

        if (year != 0)
            cal.add(Calendar.YEAR, year);
        if (month != 0)
            cal.add(Calendar.MONTH, month);
        if (day != 0)
            cal.add(Calendar.DATE, day);
        return sdf.format(cal.getTime());
    }

    public static String addYearMonthDay(String sDate, int year, int month, int day, String format) {
        if (isNullString(format) == true) {
            return convertDate(addYearMonthDay(sDate, year, month, day), "yyyyMMdd", format);
        }

        return convertDate(addYearMonthDay(sDate, year, month, day), "yyyyMMdd", format);
    }

    public static String validChkDate(String dateStr) {
        String _dateStr = dateStr;

        if (dateStr == null || !(dateStr.trim().length() == 8 || dateStr.trim().length() == 10)) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
        if (dateStr.length() == 10) {
            _dateStr = BcjisStringUtil.removeMinusChar(dateStr);
        }
        return _dateStr;
    }

    public static String convertDate(String strSource, String fromDateFormat, String toDateFormat) {
        SimpleDateFormat simpledateformat = null;
        Date date = null;
        String _fromDateFormat = fromDateFormat;
        String _toDateFormat = toDateFormat;

        if (BcjisStringUtil.isNullToString(strSource).trim().equals("")) {
            return "";
        }
        if (BcjisStringUtil.isNullToString(fromDateFormat).trim().equals(""))
            _fromDateFormat = "yyyyMMddHHmmss";
        if (BcjisStringUtil.isNullToString(toDateFormat).trim().equals(""))
            _toDateFormat = "yyyy-MM-dd HH:mm:ss";

        try {
            simpledateformat = new SimpleDateFormat(_fromDateFormat, Locale.getDefault());
            date = simpledateformat.parse(strSource);

            simpledateformat = new SimpleDateFormat(_toDateFormat, Locale.getDefault());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return simpledateformat.format(date);

    }

    public static String getEncodingPass(String userPass) throws Exception {
        StringBuffer sb = new StringBuffer();
        String apiKey = userPass;
        byte[] digest = MessageDigest.getInstance("MD5").digest(apiKey.getBytes());

        sb.setLength(0);
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
            sb.append(Integer.toString(digest[i] & 0xf0, 16));
        }

        return sb.toString();
    }

    public static String getEgovEncrypt(EgovCryptoService egovCryptoService, String s) throws Exception {
        String password = "csitsecurityc0de10*";
        return new String(Base64.encodeBase64(egovCryptoService.encrypt(s.getBytes("UTF-8"), password)));
    }
    
    public static short getShortValue(Object o){
        if(o == null){
            return 0;
        }
        
        return getShortValue(String.valueOf(o));
    }
    
    public static short getShortValue(String s){
        if(isNullString(s) == true){
            return 0;
        }
        
        try{
            return Short.parseShort(s);
        }catch(NumberFormatException nfe){
            return 0;
        }
    }
    
    public static byte getByteValue(Object o){
        if(o == null){
            return 0;
        }
        
        return getByteValue(String.valueOf(o));
    }
    
    public static byte getByteValue(String s){
        if(isNullString(s) == true){
            return 0;
        }
        
        try{
            return (byte)Integer.parseInt(s);
        }catch(Exception e){

            logger.error("fileWrite(HttpServletRequest, HttpServletResponse, File, String, String)", e);
            return 0;
        }
    }
    
    public static int getIntValue(Object o){
        if(o == null){
            return 0;
        }
        
        return getIntValue(String.valueOf(o));
    }
    
    public static int getIntValue(String s){
        if(isNullString(s) == true){
            return 0;
        }
        
        try{
            return Integer.parseInt(s);
        }catch(NumberFormatException nfe){
            return 0;
        }
    }
    
    public static double getDoubleValue(Object o){
        if(o == null){
            return 0d;
        }
        
        return getDoubleValue(String.valueOf(o));
    }
    
    public static double getDoubleValue(String s){
        if(isNullString(s) == true){
            return 0d;
        }
        
        try{
            return Double.parseDouble(s);
        }catch(NumberFormatException nfe){
            return 0d;
        }
    }
    
}

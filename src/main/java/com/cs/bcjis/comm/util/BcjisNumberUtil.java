package com.cs.bcjis.comm.util;

import java.text.NumberFormat;
import java.util.regex.Pattern;

public class BcjisNumberUtil {
	
	public static int nullConvertToInt(Object obj){
		if(obj == null)
			return 0;		
		String str = String.valueOf(obj); 
		return Pattern.matches("^[+-]?[0-9]*$", str) ? Integer.parseInt(str) : 0;	
	}
	
	public static String nullConvertIntToString(Object object){
		if(object == null)
			return "0";		
		String str = String.valueOf(object); 
		return str;
	}
	
	public static double nullConvertToDouble(Object obj){
		if(obj == null)
			return 0;		
		String str = String.valueOf(obj); 
		return Pattern.matches("^[0-9]*(.{0,1})[0-9]*$", str) ? Double.parseDouble(str) : 0;
	}
	
	public static double nullConvertToDouble2(Object obj){
		if(obj == null)
			return 0;		
		String str = String.valueOf(obj); 
		return Pattern.matches("^[+-]?[0-9]*(.{0,1})[0-9]*$", str) ? Double.parseDouble(str) : 0;
	}
	
	public static long nullConvertToLong(Object obj){
		if(obj == null)
			return 0;		
		String str = String.valueOf(obj);
		return Pattern.matches("^[+-]?[0-9]*$", str) ? Long.parseLong(str) : 0;
	}
	
	public static String nullConvertLongToString(Object object){
		if(object == null)
			return "0";		
		String str = String.valueOf(object); 
		return str;
	}
	
	public static long nullConvertToLongPoint(Object obj){
		if(obj == null)
			return 0;		
		String str = String.valueOf(obj);
//		System.out.println(str + " , " +(Pattern.matches("^[+-]?[0-9]*[.]\\d{0,2}$", str)));
		return Pattern.matches("^[+-]?[0-9]*[.]\\d{0,2}$", str) ? Long.parseLong(str) : 0;
	}
	
	/**
	 * 금액에 콤마를 더해서 반환한다.
	 * @param amt	금액
	 * @param dec	소수점
	 * @return  123,123,123.123
	 */
	public static String convertComma(long amt, int dec){
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(dec);
		return nf.format(amt);
	}
	
	public static String convertCommaDouble(double amt, int dec){
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(dec);
		return nf.format(amt);
	}
	
	/**
	 * 숫자에 콤마를 제외하여 반환한다.
	 * @param amt	금액
	 * @param dec	소수점
	 * @return  123,123,123.123
	 */
	public static String convertCommaRevert(String amt){
		String result = amt.replaceAll(",", "");
		
		if(result == null || result.equals("")){
			result = "0";
		}
		
		return result;
	}
}

package com.isec.sc.intgr.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.isec.sc.intgr.web.OrderController;

@Component
public class CommonUtil {
	
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	/**
	 * SC의 날짜형식을 화면에 표시하기 위한 날짜로 변환
	 * SC DateFormat: yyyy-MM-dd'T'HH:mm:ss ex) 2014-11-04T05:00:00+09:00
	 * 
	 * 변환 포맷: yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateString
	 * @return
	 */
	public static String getDateTimeByTimeZone(String dateString){
		
		String pattern = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		
		String transDate = "";
		try{
		
			Date date = sdf.parse(dateString);
			logger.debug("[date]"+date);
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    transDate = sdfDate.format(date);
		    logger.debug("[transDate]"+transDate);
		    
		    return transDate;
		
		}catch(Exception e){
			
			e.printStackTrace();
			return dateString;
		}
		
	}
	
	/**
	 * 시분초까지의 날짜 스트링을 SC DateFormat으로 변환
	 * 2014-11-04T05:00:00
	 * 
	 * 
	 * @param dateString YYYYMMDDHHMISS 
	 * @param delim 날짜구분값
	 * @return
	 */
	public static String getDateTimeToScDate(String dateString, String delim){
		
		if(dateString == null || dateString.length() != 14) 
			return dateString;
		
		if(delim == null) delim = "-";
		
		String transDate = "";
		String date = dateString.substring(0,4) + delim + dateString.substring(4,6) + delim + dateString.substring(6,8);
		String time = dateString.substring(8,10) + ":" + dateString.substring(10,12) + ":" + dateString.substring(12,14);
		
		transDate = date+"T"+time;
		logger.debug("[transDate]"+transDate);
		
		return transDate;
	}
	
	/**
	 * 날짜 스트링을 SC DateFormat으로 변환
	 * 2014-11-04T00:00:00
	 * 
	 * 
	 * @param dateString YYYYMMDD
	 * @param delim 날짜구분값
	 * @return
	 */
	public static String getDateToScDate(String dateString, String delim){
		
		if(dateString == null || dateString.length() != 8) 
			return dateString;
		
		if(delim == null) delim = "-";
		
		String transDate = "";
		String date = dateString.substring(0,4) + delim + dateString.substring(4,6) + delim + dateString.substring(6,8);
		String time = "00:00:00";
		
		transDate = date+"T"+time;
		logger.debug("[transDate]"+transDate);
		
		return transDate;
	}
	
	
	/**
	 * 현재의 날짜를 주어진 포맷으로 변경
	 * 
	 * @return
	 */
	public static String cuurentDateFromFormat(String format){
		
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( format, Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		
		return mTime;
  	}
	
	/**
	 * 일수(Days)로 날짜 계산 후 반환
	 * 
	 * @param dateForm YYYYMMDD
	 * @param days 일수
	 * @return YYYYMMDD
	 */
	public static String calcDate(String dateForm, int days)  
    {  
        String date = dateForm;  
        int year = Integer.parseInt(date.substring(0, 4));  
        int month = Integer.parseInt(date.substring(4, 6)) - 1;  
        int dayOfMonth = Integer.parseInt(date.substring(6, 8));  
  
        GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);  
        SimpleDateFormat timeform = new SimpleDateFormat("yyyyMMdd");  
        cal.add(Calendar.DAY_OF_MONTH, days);  
        Date d = cal.getTime();  
          
        return timeform.format(d);  
    }
	
	/**
	 * 월수(month)로 날짜 계산 후 반환
	 * 
	 * @param dateForm YYYYMM
	 * @param mon 월수
	 * @return
	 */
	public static String calcYearMonth(String dateForm, int mon)  
    {  
        String date = dateForm;  
        int year = Integer.parseInt(date.substring(0, 4));  
        int month = Integer.parseInt(date.substring(4, 6)) - 1;  
        int dayOfMonth = Integer.parseInt(date.substring(6, 8));  
  
        GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);  
        SimpleDateFormat timeform = new SimpleDateFormat("yyyyMMdd");  
        cal.add(Calendar.MONTH, mon);  
        Date d = cal.getTime();  
          
        return timeform.format(d);  
    }
	
	
	private static String getCurrentDate() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	private static String getCurrentDateTime() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	
	
	 /**
	  * XML 특수 문자 처리
	  * XML에서 특정 문자열을 출력시 파싱 오류가 나는 사전 정의어를 파싱해준다.
	  * @throws UtilException
	  */
	 public static String replaceXmlStr(String str) throws Exception {
		 
	  if(str == null || "".equals(str)) return str;
		 
	  str = str.replaceAll("\"", "&quot;");
	  str = str.replaceAll("&", "&amp;");
	  str = str.replaceAll("\'", "&apos;");
	  str = str.replaceAll("<", "&lt;");
	  str = str.replaceAll(">", "&gt;");
	  str = str.trim();
	  return str;
	 }
}

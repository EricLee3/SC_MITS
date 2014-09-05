package com.isec.sc.intgr.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

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
}

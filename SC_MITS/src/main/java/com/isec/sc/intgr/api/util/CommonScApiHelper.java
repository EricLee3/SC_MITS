package com.isec.sc.intgr.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class CommonScApiHelper {

	/**
	 * 현재의 날짜를 주어진 포맷으로 변경
	 * 
	 * @return
	 */
	public String cuurentDateFromFormat(String format){
		
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( format, Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		
		return mTime;
  	}
	
	
}

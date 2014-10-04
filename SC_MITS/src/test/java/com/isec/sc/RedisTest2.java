/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import scala.annotation.meta.setter;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.scheduler.OrderProcessTask;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class RedisTest2 {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	
	@Autowired	private OrderProcessTask orderTask;
	
	
	
	@Resource(name="reportStringRedisTemplate")
    private ValueOperations<String, String> valueOps;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	
	@Resource(name="reportStringRedisTemplate")
	private ZSetOperations<String, String> zSetOps;
	

	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${channel.ma.jns.order}")
	private String ch_ma_jns_order;
	
	
	@Ignore
	public void printReportKeyDate(){
		
		String key = "count:DA:OUTRO:orders:"+getCurrentDate();
		String key1 = "amount:DA:OUTRO:orders:"+getCurrentDate();
		String key2 = "discount:DA:OUTRO:orders:"+getCurrentDate();
		String key3 = "shipping:DA:OUTRO:orders:"+getCurrentDate();
		String key4 = "tax:DA:OUTRO:orders:"+getCurrentDate();
		
//		reportStringRedisTemplate.delete(key);
//		reportStringRedisTemplate.delete(key1);
//		reportStringRedisTemplate.delete(key2);
//		reportStringRedisTemplate.delete(key3);
//		reportStringRedisTemplate.delete(key4);
		
		
		System.out.println("[count]"+valueOps.get(key));
		System.out.println("[amount]"+valueOps.get(key1));
		System.out.println("[discount]"+valueOps.get(key2));
		System.out.println("[shipping]"+valueOps.get(key3));
		System.out.println("[tax]"+valueOps.get(key4));
	}
	
	
	public static int dateSum(String dateForm, int days)  
    {  
        String date = dateForm;  
        int year = Integer.parseInt(date.substring(0, 4));  
        int month = Integer.parseInt(date.substring(4, 6)) - 1;  
        int dayOfMonth = Integer.parseInt(date.substring(6, 8));  
  
        GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);  
        SimpleDateFormat timeform = new SimpleDateFormat("yyyyMMdd");  
        cal.add(Calendar.DAY_OF_MONTH, days);  
        Date d = cal.getTime();  
          
        return Integer.parseInt(timeform.format(d));  
    }
	
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
	
	@Ignore
	public void testQuartzJobRun(){
		
		// 스케쥴러 수동 실행
		orderTask.processOrderRelease("SLV:ASPB:order:release", "SLV:ASPB:order:update:S2M", "SLV:ASPB:order:error");
		
	}
	
	@Ignore
	public void SortedSetTest() throws Exception{
		
		// End Month Random Create
		int start = 50;
		int end = 100;

		double range = end - start + 5;
		Random randomGenerator = new Random();
		
		
		for(int i=0; i<=30; i++){
			
			int randomInt5to10 = (int)(randomGenerator.nextDouble() * range + start);
			
			
			System.out.println(randomInt5to10);
			//zSetOps.add("count:ISEC:ASPB", "10", dateSum(Integer.parseInt("20140701"), i));
			
//			valueOps.set("count:ISEC:ASPB:orders:"+dateSum("20140601", i), String.valueOf(randomInt5to10));
//			valueOps.set("amount:ISEC:ASPB:orders:"+dateSum("20140601", i), String.valueOf(randomInt5to10*1000));
//			
//			valueOps.set("count:ISEC:ASPB:orders:"+dateSum("20140701", i), String.valueOf(randomInt5to10));
//			valueOps.set("amount:ISEC:ASPB:orders:"+dateSum("20140701", i), String.valueOf(randomInt5to10*1000));
		}
		
		
		
		
		for(int i=0; i<=20; i++){
			
			int randomInt5to10 = (int)(randomGenerator.nextDouble() * range + start);
			int randomInt5to10_ = (int)(randomGenerator.nextDouble() * range + start);
			
			//System.out.println(dateSum(Integer.parseInt("20140701"), i));
			//zSetOps.add("count:ISEC:ASPB", "10", dateSum(Integer.parseInt("20140701"), i));
			
			valueOps.set("count:ISEC:ASPB:orders:"+dateSum("20140801", i), String.valueOf(randomInt5to10));
			valueOps.set("amount:ISEC:ASPB:orders:"+dateSum("20140801", i), String.valueOf(randomInt5to10_*1000));
		}
		
		
		String orderCountKey_pre = "count:*:*:orders:";
		String orderAmountKey_pre = "amount:*:*:orders:";
		
		// Today
		/*Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+getCurrentDate());
		List<String> cnt_list = valueOps.multiGet(cnt_key_names);
		int totCnt = 0;
		for(String orderCount: cnt_list){
			System.out.println("[orderCount]"+orderCount);
			totCnt += Integer.parseInt(orderCount);
		}
		System.out.println("[totCnt]"+totCnt);*/
		
		// Yesterday
/*		Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+dateSum(getCurrentDate(), -1));
		Iterator<String> itr = cnt_key_names.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
		
		List<String> cnt_list = valueOps.multiGet(cnt_key_names);
		int totCnt = 0;
		for(String orderCount: cnt_list){
			System.out.println("[orderCount]"+orderCount);
			totCnt += Integer.parseInt(orderCount);
		}
		System.out.println("[totCnt]"+totCnt);*/
		
		
		int tot_cnt = 0;
		double tot_amt = 0.00;
		
		// Last 7 Day
		// for( int i = -7; i<=-1; i++){
		
		// This month
		int currDay = Integer.parseInt(getCurrentDate().substring(6,8));
		String currYearMonth = getCurrentDate().substring(0,6);
		// for( int i = 1; i<=currDay; i++){
			
		// Last Month
		String lastYearMonth = calcYearMonth(getCurrentDate(), -1).substring(0,6);
		
		for( int i = 1; i<=31; i++){
			
			String dd = i<10?"0"+i:String.valueOf(i);
			
			Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+lastYearMonth+dd);
			Set<String> amt_key_names= reportStringRedisTemplate.keys(orderAmountKey_pre+lastYearMonth+dd);
			
			Iterator<String> itr = cnt_key_names.iterator();
			while(itr.hasNext()){
				System.out.println(itr.next());
			}
			
			// Order Count
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			for(String orderCount: cnt_list){
//				System.out.println("[orderCount]"+orderCount);
				tot_cnt += Integer.parseInt(orderCount);
			}
			
			// Order Amount
			List<String> amt_list = valueOps.multiGet(amt_key_names);
			for(String orderAmount: amt_list){
//				System.out.println("[orderAmount]"+orderAmount);
				tot_amt += Integer.parseInt(orderAmount);
			}
						
		}
		System.out.println("[tot_cnt]"+tot_cnt);
		System.out.println("[tot_amt]"+tot_amt);
		
		
		
		// Last 3 Month
		
		
		
		
//		Set<String> dataSet = zSetOps.rangeByScore("count:ISEC:ASPB", Integer.parseInt("20140701"), Integer.parseInt("20140703"));
//		
//		Iterator<String> itr = dataSet.iterator();
//		while(itr.hasNext()){
//			System.out.println(itr.next());
//		}
		
		
	}
	
	@Ignore
	public void insertPlotChartDataMonth(){
		
		String key = "count:ISEC:ASPB:orders:";
		
		/**
		데이타유형: {"yyyymm":"201408", "count":"150", "amount":"1500.00"}
		**/
/*		
		['01/2013', 4],
        ['02/2013', 8],
        ['03/2013', 10],
        ['04/2013', 12],
        ['05/2013', 2125],
        ['06/2013', 324],
        ['07/2013', 1223],
        ['08/2013', 1365],
        ['09/2013', 250],
        ['10/2013', 999],
        ['11/2013', 390]
		*/
		
		valueOps.set(key+"201301", "4");
		valueOps.set(key+"201302", "8");
		valueOps.set(key+"201303", "10");
		valueOps.set(key+"201304", "12");
		valueOps.set(key+"201305", "2125");
		valueOps.set(key+"201306", "324");
		valueOps.set(key+"201307", "1223");
		valueOps.set(key+"201308", "1365");
		valueOps.set(key+"201309", "250");
		valueOps.set(key+"201310", "999");
		valueOps.set(key+"201311", "390");
		valueOps.set(key+"201312", "210");
		
		
		String outro_key = "count:DA:OUTRO:orders:";
		
		
		valueOps.set(outro_key+"201301", "2");
		valueOps.set(outro_key+"201302", "4");
		valueOps.set(outro_key+"201303", "5");
		valueOps.set(outro_key+"201304", "6");
		valueOps.set(outro_key+"201305", "1000");
		valueOps.set(outro_key+"201306", "524");
		valueOps.set(outro_key+"201307", "1000");
		valueOps.set(outro_key+"201308", "700");
		valueOps.set(outro_key+"201309", "150");
		valueOps.set(outro_key+"201310", "555");
		valueOps.set(outro_key+"201311", "290");
		valueOps.set(outro_key+"201312", "200");
		
		//System.out.println("amount:"+valueOps.increment(key, 150.00));
		
	}
	
	
	@Ignore
	public void insertPlotChartDataMonth2(){
		
		// TODO: 채널정보 property로 뺼것
		String entCode[] = {"SLV", "DA", "ISEC"};
		String sellerCode[] = {"ASPB", "OUTRO", "JNS"};
		String sellerCodeName[] = {"Aspen Bay", "Outro", "J&S US"};
		
		
		
		// End Month Random Create
		int start = 50;
		int end = 100;

		double range = end - start + 5;
		Random randomGenerator = new Random();
		
		
		int start1 = 100000;
		int end1 = 500000;

		double range1 = end1 - start1 + 50000;
				
		
		
		String currYear = "2014";
		// 채널별 통계데이타
		for( int i=0; i<sellerCode.length; i++){
			
			for( int j=1; j<=8; j++){
				
				String mm = "0"+j;
				
				for( int jj=1; jj<=30; jj++){
					
					String dd = ""+jj;
					if(dd.length() == 1) dd = "0"+dd;
					
					String cntKey = "count:"+entCode[i]+":"+sellerCode[i]+":orders:"+currYear+mm+dd;
					String amtKey = "amount:"+entCode[i]+":"+sellerCode[i]+":orders:"+currYear+mm+dd;
					
					int r1 = (int)(randomGenerator.nextDouble() * range + start);
					int r2 = (int)(randomGenerator.nextDouble() * range1 + start1);
					
					valueOps.set(cntKey, r1+"");
					valueOps.set(amtKey, r2+"");
					
				}
				
			}
		}
		
	}
	
	
	@Ignore
	public void TestSetList() {
		
		String errorJSON = "{ \"docType\":\"0001\", \"entCode\":\"DA\", \"sellerCode\":\"OUTRO\", \"orderId\":\"0001\", "
				+ " \"orderXML\":\"<xml>\","
				+ " \"errorMsg\":\"Error Message\","
				+ " \"errorDetail\":\"Error Detail Message\","
				+ " \"errorDate\":\"2013-11-23 14:30\" "
				+ "}";
		
		String key = "DA:OUTRO:order:error";
		List<String> orderList = listOps.range(key, 0, -1);
		
		for( int i=0; i<orderList.size(); i++){
			listOps.rightPop(key);
		}
		
		for( int i=0; i<100; i++){
			listOps.leftPush(key, errorJSON);
		}
	}
	
	
	@Ignore
	public void TestHashSet() {
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3.00");

		hashOps.putAll("HKey", map);
		
		List<Object> hashList = hashOps.multiGet("Hkey*", map.keySet());
		System.out.println("[hashList.size]"+ hashList.size());
		
		
	}
	
	
	@Ignore
    public void TestGetOrderListMagento() {
		
		
		// OUTRO
		List<String> orderList = listOps.range("DA:OUTRO:order:update:S2M", 0, -1);
		System.out.println("##### [orderlist - Outro]"+orderList.size());
		for(int i=0; i<orderList.size(); i++){
			System.out.println("["+i+"]"+orderList.get(i));
		}
		
		// JNS
		List<String> orderWList = listOps.range("ISEC:JNS:order:update:S2M", 0, -1);
		System.out.println("##### [orderlist - JNS]"+orderWList.size());
		for(int i=0; i<orderWList.size(); i++){
			System.out.println("["+i+"]"+orderWList.get(i));
		}
		
		
		
		List<String> orderErrList = listOps.range("DA:OUTRO:order:error", 0, -1);
		System.out.println("##### [orderErrorlist]"+orderErrList.size());
		
		
		//listOps.rightPop("ISEC:JNS:order:error"); 
		
		for(int i=0; i<orderErrList.size(); i++){
			System.out.println("["+i+"]"+orderErrList.get(i));
		}
		
    }
	
	
	private static String getCurrentDate() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	private static String getCurrentDateTime() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
}

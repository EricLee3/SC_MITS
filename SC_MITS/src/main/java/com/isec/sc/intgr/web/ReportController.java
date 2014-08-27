package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;



@Controller
@PropertySource("classpath:mits.properties")
@RequestMapping("/reports")
public class ReportController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	@Resource(name="reportStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="reportStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	
	
	/**
	 * 오더 Report ( Chart 데이타 조회)
	 *  - 월별 오더금액, 오더건수
	 *  - Total 오더금액, 오더건수
	 *  - TODO: Shipping, Discount, Tax금액 집계처리
	 *  
	 * @param paramMap 시작년월/종료년월
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOrderReportByCh.sc")
	public ModelAndView getOrderReportByCh( @RequestParam Map<String, String> paramMap ) throws Exception{ 
		
		// TODO: 채널정보 property로 뺼것
//		String entCode[] = {"DA", "ISEC"};
//		String sellerCode[] = {"OUTRO", "ASPB"};
		
		String entCode[] = {"ISEC"};
		String sellerCode[] = {"ASPB"};
		
		
		// 검색일자 Parameter
		int startYear = 2013;
		int startMonth = 6;
		
		
		// End Month Random Create
		int start = 5;
		int end = 10;

		double range = end - start + 1;
		Random randomGenerator = new Random();
		int randomInt5to10 = (int)(randomGenerator.nextDouble() * range + start);
		
		
		int endMonth =  12;
		
		List<HashMap<String, Object>> chDataList = new ArrayList<HashMap<String, Object>>();
		
		// 채널별
		for( int i=0; i<sellerCode.length; i++){
			
			List<String[]> cnt_list = new ArrayList<String[]>(); // Order Count List
			List<String[]> amt_list = new ArrayList<String[]>(); // Order Amount List
			
			for(int j=startMonth; j<=endMonth; j++){
				
				String mm = j<10?"0"+j:String.valueOf(j);
				String cntKey = "count:"+entCode[i]+":"+sellerCode[i]+":orders:"+startYear+mm;
				String amtKey = "amount:"+entCode[i]+":"+sellerCode[i]+":orders:"+startYear+mm;
				
				cnt_list.add( new String[]{startYear+"/"+mm, valueOps.get(cntKey)} );
				amt_list.add( new String[]{startYear+"/"+mm, valueOps.get(amtKey)} );
			}
			HashMap<String, List<String[]>> dataMap = new HashMap<String, List<String[]>>();
			dataMap.put("count", cnt_list);
			dataMap.put("amount", amt_list);
			
			HashMap<String, Object> chMap = new HashMap<String, Object>();
			chMap.put("chName", sellerCode[i]);
			chMap.put("chData", dataMap);
			
			chDataList.add(chMap);
		}
		
		
		// Total
		String orderCountKey_pre = "count:*:*:orders:";
		String orderAmountKey_pre = "amount:*:*:orders:";
		
		List<String[]> totlist = new ArrayList<String[]>();
		List<String[]> amtList = new ArrayList<String[]>();
		
		int tot_orderCnt = 0;
		double tot_orderAmount = 0.00;
		for(int i=startMonth; i<=endMonth; i++){
			
			String mm = i<10?"0"+i:String.valueOf(i);
			
			// Total Order Count
			Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+startYear+mm);
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			int orderCnt = 0;
			for(String orderCount: cnt_list){
				orderCnt += Integer.parseInt(orderCount);
			}
			
			tot_orderCnt += orderCnt;
			totlist.add( new String[]{startYear+"/"+mm, String.valueOf(orderCnt) } );
			
			// Total Order Amount
			Set<String> amt_key_names= reportStringRedisTemplate.keys(orderAmountKey_pre+startYear+mm);
			List<String> amt_list = valueOps.multiGet(amt_key_names);
			double orderAmount = 0.00;
			for(String orderAmt: amt_list){
				orderAmount += Double.parseDouble(orderAmt);
			}
			
			tot_orderAmount += orderAmount;
			amtList.add( new String[]{startYear+"/"+mm, String.valueOf(orderAmount) } );
			
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data", chDataList);
		mav.addObject("tot_count", tot_orderCnt);
		mav.addObject("tot_amount", tot_orderAmount);
		mav.addObject("tot_cnt_data", totlist);
		mav.addObject("tot_amt_data", amtList);
		return mav;   
	}
	
	
	
	@RequestMapping(value = "/getOrderOverAll.sc")
	public ModelAndView getOrderOverAll( @RequestParam String startDate,  @RequestParam String endDate, @RequestParam int term) throws Exception{ 
		
		
//		String orderCountKey_pre = "count:*:*:orders:";
//		String orderAmountKey_pre = "amount:*:*:orders:";
		
		String orderCountKey_pre = "count:ISEC:ASPB:orders:";
		String orderAmountKey_pre = "amount:ISEC:ASPB:orders:";
		
		String orderChargeKey_pre = "shipping:*:*:orders:";
		
		int tot_order_count = 0;
		double tot_order_amount = 0.00;
		double tot_charge_amount = 0.00;
		double tot_order_avg_amount = 0.00;
		
		
		for( int i = -term; i<=0; i++){
			
			Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+calcDate(endDate, i));
			Set<String> amt_key_names= reportStringRedisTemplate.keys(orderAmountKey_pre+calcDate(endDate, i));
			Set<String> charge_key_names= reportStringRedisTemplate.keys(orderChargeKey_pre+calcDate(endDate, i));
			
			Iterator<String> itr = cnt_key_names.iterator();
			while(itr.hasNext()){
				logger.debug("[key]"+itr.next());
			}
			
			
			// Order Count
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			for(String orderCount: cnt_list){
				tot_order_count += Integer.parseInt(orderCount);
			}
			
			// Order Amount
			List<String> amt_list = valueOps.multiGet(amt_key_names);
			for(String orderAmount: amt_list){
				tot_order_amount += Double.parseDouble(orderAmount);
			}
			
			// Order Charge(Shipping)
			List<String> charge_list = valueOps.multiGet(charge_key_names);
			for(String chargeAmount: charge_list){
				tot_charge_amount += Double.parseDouble(chargeAmount);
			}
		}
		
		if(tot_order_count == 0) 
			tot_order_avg_amount = 0;
		else
			tot_order_avg_amount =  tot_order_amount/tot_order_count;
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("tot_order_count", tot_order_count);
		mav.addObject("tot_order_amount", tot_order_amount);
		mav.addObject("tot_charge_amount", tot_charge_amount);
		mav.addObject("tot_order_avg_amount", tot_order_avg_amount);
		return mav;   
	}
	
	
	
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
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
}

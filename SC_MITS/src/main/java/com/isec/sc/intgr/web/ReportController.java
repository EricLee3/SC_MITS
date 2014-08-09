package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
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
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	@RequestMapping(value = "/getOrderReportByCh.sc")
	public ModelAndView getOrderReportByCh( @RequestParam Map<String, String> paramMap ) throws Exception{ 
		
		
		// TODO: property로 뺼것
		String entCode[] = {"DA","ISEC"};
		String sellerCode[] = {"OUTRO","ASPB"};;
		
		
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
		
		HashMap<String, Object> chMap = new HashMap<String, Object>();
		
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
			
			chMap.put(sellerCode[i], dataMap);
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
			Set<String> cnt_key_names= maStringRedisTemplate.keys(orderCountKey_pre+startYear+mm);
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			int orderCnt = 0;
			for(String orderCount: cnt_list){
				orderCnt += Integer.parseInt(orderCount);
			}
			
			tot_orderCnt += orderCnt;
			totlist.add( new String[]{startYear+"/"+mm, String.valueOf(orderCnt) } );
			
			// Total Order Amount
			Set<String> amt_key_names= maStringRedisTemplate.keys(orderAmountKey_pre+startYear+mm);
			List<String> amt_list = valueOps.multiGet(amt_key_names);
			double orderAmount = 0.00;
			for(String orderAmt: amt_list){
				orderAmount += Double.parseDouble(orderAmt);
			}
			
			tot_orderAmount += orderAmount;
			amtList.add( new String[]{startYear+"/"+mm, String.valueOf(orderAmount) } );
			
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data", chMap);
		mav.addObject("tot_count", tot_orderCnt);
		mav.addObject("tot_amount", tot_orderAmount);
		mav.addObject("tot_cnt_data", totlist);
		mav.addObject("tot_amt_data", amtList);
		return mav;   
	}
	

}

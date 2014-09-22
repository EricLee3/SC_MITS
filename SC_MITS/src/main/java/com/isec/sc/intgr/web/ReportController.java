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
import com.isec.sc.intgr.api.util.CommonUtil;
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
	public ModelAndView getOrderReportByCh( @RequestParam String startMonth, @RequestParam int term) throws Exception{ 
		
		// TODO: 채널정보 property로 뺼것
		String entCode[] = {"SLV", "DA", "ISEC"};
		String sellerCode[] = {"ASPB", "OUTRO", "JNS"};
		String sellerCodeName[] = {"Aspen Bay", "Outro", "J&S US"};
		
//		String entCode[] = {"ISEC"};
//		String sellerCode[] = {"ASPB"};
		
		
		// TODO: 기간설정
//		int startYear = 2014;
//		int startMonth = 6;
//		int endMonth =  9;
		
		List<HashMap<String, Object>> chDataList = new ArrayList<HashMap<String, Object>>();
		
		// 채널별 통계데이타
		for( int i=0; i<sellerCode.length; i++){
			
			List<String[]> cnt_list = new ArrayList<String[]>(); // Order Count List
			List<String[]> amt_list = new ArrayList<String[]>(); // Order Amount List
			
			for(int j=0; j<term; j++){
				
				int currYear = Integer.parseInt(startMonth.substring(0,4));
				int currMonth= Integer.parseInt(startMonth.substring(4,6));
				
				currMonth = currMonth+j;
				
				if(currMonth == 13){
					currYear = currYear + 1;
					currMonth = 1;
				}
				String mm = currMonth<10?"0"+currMonth:""+currMonth;
				logger.debug("[currDate]"+currYear+mm);
				
				String cntKey = "count:"+entCode[i]+":"+sellerCode[i]+":orders:"+currYear+mm;
				String amtKey = "amount:"+entCode[i]+":"+sellerCode[i]+":orders:"+currYear+mm;
				
				Set<String> cnt_key_names= reportStringRedisTemplate.keys(cntKey+"*");
				List<String> orderCntList = valueOps.multiGet(cnt_key_names);
				int orderCnt = 0;
				for(String orderCount: orderCntList){
					orderCnt += Integer.parseInt(orderCount);
				}
				
				Set<String> amt_key_names= reportStringRedisTemplate.keys(amtKey+"*");
				List<String> orderAmtList = valueOps.multiGet(amt_key_names);
				int orderAmt = 0;
				for(String orderAmount: orderAmtList){
					orderAmt += Integer.parseInt(orderAmount);
				}
				
//				cnt_list.add( new String[]{startYear+"/"+mm, valueOps.get(cntKey)} );
				cnt_list.add( new String[]{currYear+"/"+mm, orderCnt+""} );
//				cnt_list.add( new String[]{startYear+"/"+mm, valueOps.get(cntKey)} );
				amt_list.add( new String[]{currYear+"/"+mm, orderAmt+""} );
				
			}
			HashMap<String, List<String[]>> dataMap = new HashMap<String, List<String[]>>();
			dataMap.put("count", cnt_list);
			dataMap.put("amount", amt_list);
			
			HashMap<String, Object> chMap = new HashMap<String, Object>();
			chMap.put("chName", sellerCodeName[i]);
			chMap.put("chData", dataMap);
			
			chDataList.add(chMap);
		}
		
		
		// Total
		String orderCountKey_pre = "count:*:*:orders:";
		String orderAmountKey_pre = "amount:*:*:orders:";
		String shipChargeKey_pre = "shipping:*:*:orders:";
		String cancelAmountKey_pre = "cancel_amt:*:*:orders:";
		
		List<String[]> totlist = new ArrayList<String[]>();
		List<String[]> amtList = new ArrayList<String[]>();
		
		int tot_orderCnt = 0;
		double tot_orderAmount = 0.00;
		double tot_shipCharge = 0.00;
		double tot_cancelAmount = 0.00;
		
		
		for(int i=0; i<term; i++){
			
			int currYear = Integer.parseInt(startMonth.substring(0,4));
			int currMonth= Integer.parseInt(startMonth.substring(4,6));
			
			currMonth = currMonth+i;
			
			if(currMonth == 13){
				currYear = currYear + 1;
				currMonth = 1;
			}
			String mm = currMonth<10?"0"+currMonth:""+currMonth;
			logger.debug("[currDate]"+currYear+mm);
			
			// Total Order Count
			Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+currYear+mm+"*");
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			int orderCnt = 0;
			for(String orderCount: cnt_list){
				orderCnt += Integer.parseInt(orderCount);
			}
			
			tot_orderCnt += orderCnt;
			totlist.add( new String[]{currYear+"/"+mm, String.valueOf(orderCnt) } );
			
			// Total Order Amount
			Set<String> amt_key_names= reportStringRedisTemplate.keys(orderAmountKey_pre+currYear+mm+"*");
			List<String> amt_list = valueOps.multiGet(amt_key_names);
			double orderAmount = 0.00;
			for(String orderAmt: amt_list){
				orderAmount += Double.parseDouble(orderAmt);
			}
			
			tot_orderAmount += orderAmount;
			amtList.add( new String[]{currYear+"/"+mm, String.valueOf(orderAmount) } );
			
			
			// Total Order Shipping Charge
			Set<String> shp_key_names= reportStringRedisTemplate.keys(shipChargeKey_pre+currYear+mm+"*");
			double shipCharge = 0.00;
			for(String charge: valueOps.multiGet(shp_key_names)){
				shipCharge += Double.parseDouble(charge);
			}
			tot_shipCharge += shipCharge;
			
			// Total Order Cancel Amount
			Set<String> cancelAmt_key_names= reportStringRedisTemplate.keys(cancelAmountKey_pre+currYear+mm+"*");
			double cancelAmount = 0.00;
			for(String charge: valueOps.multiGet(cancelAmt_key_names)){
				cancelAmount += Double.parseDouble(charge);
			}
			tot_cancelAmount += cancelAmount;
			
			
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("data", chDataList);
		
		// 항목별 총합
		mav.addObject("tot_count", tot_orderCnt);
		mav.addObject("tot_amount", tot_orderAmount);
		mav.addObject("tot_shipping_charge", tot_shipCharge);
		mav.addObject("tot_cancel_amount", tot_cancelAmount);
		
		// 항목별 월별 총합계 - 차트용
		mav.addObject("tot_cnt_data", totlist);
		mav.addObject("tot_amt_data", amtList);
		return mav;   
	}
	
	
	/**
	 * 총주문금액, 총오더건수, 총비용, 환불금액 집계 데이타 조회
	 * - 대시보드 화면 최상단 영역
	 *  
	 * @param startDate 검색시작
	 * @param endDate 검색종료일
	 * @param term 검색일
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOrderOverAll.sc")
	public ModelAndView getOrderOverAll( @RequestParam String startDate,  @RequestParam String endDate, @RequestParam int term) throws Exception{ 
		
		
		String orderCountKey_pre = "count:*:*:orders:";
		String orderAmountKey_pre = "amount:*:*:orders:";
		
		// TODO: 오픈전 키값 변경 - SLV:ASPB
//		String orderCountKey_pre = "count:ISEC:ASPB:orders:";
//		String orderAmountKey_pre = "amount:ISEC:ASPB:orders:";
		
//		String orderCountKey_pre = "count:SLV:ASPB:orders:";
//		String orderAmountKey_pre = "amount:SLV:ASPB:orders:";
		
//		String orderCountKey_pre = "count:Aurora:Auro_Store_1:orders:";
//		String orderAmountKey_pre = "amount:Aurora:Auro_Store_1:orders:";
		
		// TODO: 오픈전 키값 변경 - SLV:ASPB
		String orderChargeKey_pre = "shipping:*:*:orders:";
		String orderTaxKey_pre = "tax:*:*:orders:";
		String orderDiscountKey_pre = "discount:*:*:orders:";
		String orderCancelCntKey_pre = "cancel_cnt:*:*:orders:";
		String orderCancelAmtKey_pre = "cancel_amt:*:*:orders:";
		
		int tot_order_count = 0;
		double tot_order_amount = 0.00;
		double tot_charge_amount = 0.00;
		double tot_tax_amount = 0.00;
		double tot_discount_amount = 0.00;
		
		int tot_cancel_count = 0;
		double tot_cancel_amount = 0.00;
		
		//double tot_order_avg_amount = 0.00;
		
		
		for( int i = -term; i<=0; i++){
			
			Set<String> cnt_key_names= reportStringRedisTemplate.keys(orderCountKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> amt_key_names= reportStringRedisTemplate.keys(orderAmountKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> charge_key_names= reportStringRedisTemplate.keys(orderChargeKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> tax_key_names= reportStringRedisTemplate.keys(orderTaxKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> discount_key_names= reportStringRedisTemplate.keys(orderDiscountKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> cancelCnt_key_names= reportStringRedisTemplate.keys(orderCancelCntKey_pre+CommonUtil.calcDate(endDate, i));
			Set<String> cancelAmt_key_names= reportStringRedisTemplate.keys(orderCancelAmtKey_pre+CommonUtil.calcDate(endDate, i));
			
			Iterator<String> itr = cnt_key_names.iterator();
			while(itr.hasNext()){
				logger.debug("[key]"+itr.next());
			}
			
			
			// Order Count
			List<String> cnt_list = valueOps.multiGet(cnt_key_names);
			for(String orderCount: cnt_list){
//				logger.debug("[]"+orderCount);
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
			
			// Order Tax
			List<String> tax_list = valueOps.multiGet(tax_key_names);
			for(String taxAmount: tax_list){
				tot_tax_amount += Double.parseDouble(taxAmount);
			}
			
			// Order Discount
			List<String> discount_list = valueOps.multiGet(discount_key_names);
			for(String discountAmount: discount_list){
				tot_discount_amount += Double.parseDouble(discountAmount);
			}
			
			// Cancel Count
			List<String> cancelCnt_list = valueOps.multiGet(cancelCnt_key_names);
			for(String cancelCount: cancelCnt_list){
				tot_cancel_count += Double.parseDouble(cancelCount);
			}
			
			// Cancel Amount
			List<String> cancelAmt_list = valueOps.multiGet(cancelAmt_key_names);
			for(String cancelAmount: cancelAmt_list){
				tot_cancel_amount += Double.parseDouble(cancelAmount);
			}
		}
		
//		if(tot_order_count == 0) 
//			tot_order_avg_amount = 0;
//		else
//			tot_order_avg_amount =  tot_order_amount/tot_order_count;
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("tot_order_count", tot_order_count);
		mav.addObject("tot_order_amount", tot_order_amount);
		
		mav.addObject("tot_charge_amount", tot_charge_amount);
		mav.addObject("tot_tax_amount", tot_tax_amount);
		mav.addObject("tot_discount_amount", tot_discount_amount);
		
		mav.addObject("tot_cancel_count", tot_cancel_count);
		mav.addObject("tot_cancel_amount", tot_cancel_amount);
		
//		mav.addObject("tot_order_avg_amount", tot_order_avg_amount);
		return mav;   
	}
	
	
	
	
}

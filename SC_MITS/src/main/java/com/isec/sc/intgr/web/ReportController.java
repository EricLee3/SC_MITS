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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.isec.sc.intgr.api.xml.beans.Organization;
import com.isec.sc.intgr.api.xml.beans.OrganizationList;



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
	public ModelAndView getOrderReportByCh( @RequestParam String startMonth, @RequestParam int term, HttpServletRequest req) throws Exception{ 
		
		// 유저 소속 엔터프라이즈
		String userEntCode = (String)req.getSession().getAttribute("S_USER_ENT_CODE");
		if("DEFAULT".equals(userEntCode)){
			userEntCode = "*";
		}
		
		// 유저 권한 엔터프라이즈 조직리스트
		// List<Organization> entOrgList = (List<Organization>)req.getSession().getAttribute("S_ENT_ORG_LIST");
		// 유저 권한 셀러(채널) 조직리스트
		List<Organization> sellerOrgList = (List<Organization>)req.getSession().getAttribute("S_SELLER_ORG_LIST");
		
		
		
		
		int tot_orderCnt = 0;
		double tot_orderAmount = 0.00;
		double tot_shipCharge = 0.00;
		double tot_cancelAmount = 0.00;
		
		// Channel
		List<HashMap<String, Object>> chDataList = new ArrayList<HashMap<String, Object>>();
		
		// Total
		List<String[]> totlist = new ArrayList<String[]>();
		List<String[]> amtList = new ArrayList<String[]>();
		
		
		// 채널별 통계데이타
		for( int i=0; i<sellerOrgList.size(); i++){
			
			List<String[]> cnt_list = new ArrayList<String[]>(); // Order Count List
			List<String[]> amt_list = new ArrayList<String[]>(); // Order Amount List
			List<String[]> shp_list = new ArrayList<String[]>(); // Order Shipping Amount List
			List<String[]> csl_list = new ArrayList<String[]>(); // Order Cancel Amount List
			
			
			int orderCnt_month_sum =0, orderAmt_month_sum = 0;
			double shipCharge_month_sum=0.00, cancelAmt_month_sum=0.00;
			
			for(int j=0; j<term; j++){
				
				int currYear = Integer.parseInt(startMonth.substring(0,4));
				int currMonth= Integer.parseInt(startMonth.substring(4,6));
				
				// 다음년도 1월 처리
				currMonth = currMonth+j;
				if(currMonth == 13){
					currYear = currYear + 1;
					currMonth = 1;
				}
				String mm = currMonth<10?"0"+currMonth:""+currMonth;
				
				
				
				int orderCnt = 0, orderAmt = 0;
				double shipCharge = 0.00, cancelAmt = 0.00;
				
				// 월별집계를 위해서 일자를 와일드카드로 처리
				String ch_key_suffix = ":*:"+sellerOrgList.get(i).getOrganizationCode()+":orders:"+currYear+mm+"*";
				
				// 월별 주문건수 집계
				Set<String> cnt_key_names= reportStringRedisTemplate.keys("count"+ch_key_suffix);
				for(String orderCount: valueOps.multiGet(cnt_key_names)){
					orderCnt += Integer.parseInt(orderCount);
				}
				
				// 월별 결제금액 집계
				Set<String> amt_key_names= reportStringRedisTemplate.keys("amount"+ch_key_suffix);
				for(String orderAmount: valueOps.multiGet(amt_key_names)){
					orderAmt += Integer.parseInt(orderAmount);
				}
				
				// 월별 배송비용 집계
				Set<String> shp_key_names= reportStringRedisTemplate.keys("shipping"+ch_key_suffix);
				for(String charge: valueOps.multiGet(shp_key_names)){
					shipCharge += Double.parseDouble(charge);
				}
				
				// 월별 취소금액 집계
				Set<String> cancel_key_names= reportStringRedisTemplate.keys("cancel_amt"+ch_key_suffix);
				for(String cancel: valueOps.multiGet(cancel_key_names)){
					cancelAmt += Double.parseDouble(cancel);
				}
				
				cnt_list.add( new String[]{currYear+"/"+mm, orderCnt+""} );
				amt_list.add( new String[]{currYear+"/"+mm, orderAmt+""} );
				shp_list.add( new String[]{currYear+"/"+mm, shipCharge+""} );
				csl_list.add( new String[]{currYear+"/"+mm, cancelAmt+""} );
				
				
				// 월별 집계 누적
				orderCnt_month_sum += orderCnt;
				orderAmt_month_sum += orderAmt;
				shipCharge_month_sum += shipCharge;
				cancelAmt_month_sum += cancelAmt;
			}
			
			// 월별 채널별 집계 - 차트
			HashMap<String, List<String[]>> dataMap = new HashMap<String, List<String[]>>();
			dataMap.put("count", cnt_list);
			dataMap.put("amount", amt_list);
			dataMap.put("shipping", shp_list);
			dataMap.put("cancel", csl_list);
			
			HashMap<String, Object> chMap = new HashMap<String, Object>();
			chMap.put("chName", sellerOrgList.get(i).getOrganizationName());
			chMap.put("chData", dataMap);
			
			chDataList.add(chMap);
			
			
			// 월별 누적 -> 채널전체 집계 - 4가지 지표
			tot_orderCnt += orderCnt_month_sum;
			tot_orderAmount += orderAmt_month_sum;
			tot_shipCharge += shipCharge_month_sum;
			tot_cancelAmount += cancelAmt_month_sum;
		}
		
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		// 채널별 월별 주문건수, 결제금액, 배송비용, 취소금액 집계 - 차트용
		mav.addObject("data", chDataList);
		
		// 전체 집계 - 차트하단 4가지 지표
		mav.addObject("tot_count", tot_orderCnt);
		mav.addObject("tot_amount", tot_orderAmount);
		mav.addObject("tot_shipping_charge", tot_shipCharge);
		mav.addObject("tot_cancel_amount", tot_cancelAmount);
		
		// 항목별 월별 총합계 - 차트용 (사용안함)
		mav.addObject("tot_cnt_data", totlist);
		mav.addObject("tot_amt_data", amtList);
		return mav;   
	}
	
	
	/**
	 * 총주문금액, 총오더건수, 총비용, 환불금액 집계 데이타 조회
	 * - 대시보드 화면 최상단 영역
	 * - 일자가 기준, 최초 로딩시 현재일의 집계 표시
	 *  
	 * @param startDate 검색시작일
	 * @param endDate 검색종료일
	 * @param term 검색일구간
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOrderOverAll.sc")
	public ModelAndView getOrderOverAll( @RequestParam String startDate,  @RequestParam String endDate, 
			@RequestParam int term, HttpServletRequest req) throws Exception{ 
		
		
		// 유저 소속 엔터프라이즈
		String userEntCode = (String)req.getSession().getAttribute("S_USER_ENT_CODE");
		if("DEFAULT".equals(userEntCode)){
			userEntCode = "*";
		}
		
		// 유저 권한 엔터프라이즈 조직리스트
		// List<Organization> entOrgList = (List<Organization>)req.getSession().getAttribute("S_ENT_ORG_LIST");
		// 유저 권한 셀러(채널) 조직리스트
		List<Organization> sellerOrgList = (List<Organization>)req.getSession().getAttribute("S_SELLER_ORG_LIST");
		
		
		int tot_order_count = 0;
		int tot_cancel_count = 0;
		
		
		double tot_order_amount = 0.00;
		double tot_charge_amount = 0.00;
		double tot_tax_amount = 0.00;
		double tot_discount_amount = 0.00;
		double tot_cancel_amount = 0.00;
		
		
		for( int i=0; i<sellerOrgList.size(); i++){
		
			String ch_key_suffix = ":*:"+sellerOrgList.get(i).getOrganizationCode()+":orders:";
		
			for( int j = -term; j<=0; j++){
				
				Set<String> cnt_key_names= reportStringRedisTemplate.keys("count"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> amt_key_names= reportStringRedisTemplate.keys("amount"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> charge_key_names= reportStringRedisTemplate.keys("shipping"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> tax_key_names= reportStringRedisTemplate.keys("tax"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> discount_key_names= reportStringRedisTemplate.keys("discount"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> cancelCnt_key_names= reportStringRedisTemplate.keys("cancel_cnt"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				Set<String> cancelAmt_key_names= reportStringRedisTemplate.keys("cancel_amt"+ch_key_suffix+CommonUtil.calcDate(endDate, j));
				
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
			} // End For term
			logger.debug("[i]"+i);
			
		} // End Seller List
		logger.debug("[all]");
		
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

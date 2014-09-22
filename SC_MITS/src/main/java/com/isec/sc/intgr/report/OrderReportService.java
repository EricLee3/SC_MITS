package com.isec.sc.intgr.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.isec.sc.intgr.scheduler.OrderProcessTask;

@Component
public class OrderReportService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(OrderReportService.class);
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	@Resource(name="reportStringRedisTemplate")
    private ValueOperations<String, String> reportValueOps;
	
	
	
	public OrderReportService(){
		
	}
	
	/**
	 * Create Order 건에 대한 일별 통계데이타 저장
	 *  - 결제금액, 주문건수, 배송비/과세/할인금액 일별 집계 
	 * 
	 * @param entCode
	 * @param sellerCode
	 * @param priceInfo
	 */
	public void saveOrderReportData(String entCode, String sellerCode, HashMap<String, Double> priceInfo){
		
		// logger.debug("[]"+reportStringRedisTemplate.hasKey("count:ISEC:ASPB:orders:201301"));
		
		
		/**
		 *  실시간 통계데이타 저장 (일별 채널별 Key에 값 Increase )
		 *   1. 오더 카운트
		 *     key - count:EntCode:SellerCode:현재일(YYYYMMDD)
		 *     
		 *   2. 오더 금액(결제금액)
		 *     key - amount.EntCode:SellerCode:현재일(YYYYMMDD)
		 *     
		 *   3. Shipping 금액, Tax금액
		 *     key - amaount.ship.EntCode:SellerCode:현재일(YYYYMMDD)
		 *     key - amaount.tax.EntCode:SellerCode:현재일(YYYYMMDD)
		 *     
		 *   4. 판매자 정보 (판매자별 구매금액) - 고객정보 연동 후 가능
		 *   
		 */
		
		// 현재날짜, Report Key 생성
		String nowDate = getCurrentDate();
		
		String sum_count_key = "count:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		String sum_amount_key = "amount:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		String sum_discount_key = "discount:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		String sum_shipping_key = "shipping:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		String sum_tax_key = "tax:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		
		
		logger.debug("[sum_count_key]"+ sum_count_key);
		logger.debug("[sum_amount_key]"+ sum_amount_key);
		logger.debug("[sum_discount_key]"+ sum_discount_key);
		logger.debug("[sum_shipping_key]"+ sum_shipping_key);
		logger.debug("[sum_tax_key]"+ sum_tax_key);
		
		logger.debug("[amount]"+ priceInfo.get("amount"));
		logger.debug("[discount]"+ priceInfo.get("discount"));
		logger.debug("[charge]"+ priceInfo.get("charge"));
		logger.debug("[tax]"+ priceInfo.get("tax"));
		
		
		// Count / Amount 누적
		reportValueOps.increment(sum_count_key, 1);
		reportValueOps.increment(sum_amount_key, priceInfo.get("amount"));
		reportValueOps.increment(sum_discount_key, priceInfo.get("discount"));
		reportValueOps.increment(sum_shipping_key, priceInfo.get("charge"));
		reportValueOps.increment(sum_tax_key, priceInfo.get("tax"));
		
	}
	
	/**
	 * 
	 * @param entCode
	 * @param sellerCode
	 * @param cancelAmt
	 */
	public void saveCancelOrderAmount(String entCode, String sellerCode, double cancelAmt){
		
		// 현재날짜, Report Key 생성
		String nowDate = getCurrentDate();
		
		String sum_cancel_cnt_key = "cancel_cnt:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		String sum_cancel_amt_key = "cancel_amt:" + entCode + ":" + sellerCode + ":orders:" + nowDate;
		reportValueOps.increment(sum_cancel_cnt_key, 1);
		reportValueOps.increment(sum_cancel_amt_key, cancelAmt);
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

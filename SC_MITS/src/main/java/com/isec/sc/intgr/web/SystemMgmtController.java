package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.mongodb.util.Hash;



@Controller
@PropertySource("classpath:mits.properties")
@RequestMapping("/system")
public class SystemMgmtController {

	private static final Logger logger = LoggerFactory.getLogger(SystemMgmtController.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	
	@Value("${sc.api.createOrder.template}")
	private String CREATE_ORDER_TEMPLATE;
	
	/*
	# Ma-SC
	key.ma.KOLOR.ASPB.order=KOLOR:ASPB:order
	key.ma.KOLOR.ASPB.orderUpdate.M2S=KOLOR:ASPB:order:update:M2S
	key.ma.KOLOR.ASPB.orderUpdate.S2M=KOLOR:ASPB:order:update:S2M
	key.ma.KOLOR.ASPB.order.error=KOLOR:ASPB:order:error
	key.ma.KOLOR.ASPB.order.update.error=KOLOR:ASPB:order:update:error
	# SC-Cube
	key.ca.KOLOR.ASPB.orderUpdate.C2S=${ca.KOLOR}:ASPB:order:update:C2S
	key.ca.KOLOR.ASPB.orderUpdate.S2C=${ca.KOLOR}:ASPB:order:update:S2C
	# Order Release 
	key.ca.KOLOR.ASPB.order.release=KOLOR:ASPB:order:release

	##### Product Interface Key
	# MA-SC
	key.ma.KOLOR.ASPB.product.S2M=KOLOR:ASPB:product:S2M
	key.ma.KOLOR.ASPB.product.error=KOLOR:ASPB:product:error
	# SC-Cube
	key.ca.KOLOR.product.C2S=${ca.KOLOR}:product:C2S
	key.ca.KOLOR.product.S2C=${ca.KOLOR}:product:S2C
	key.ca.KOLOR.product.error=${ca.KOLOR}:product:error

	##### Inventory Interface Key
	# MA-SC
	key.ma.KOLOR.ASPB.inventory.S2M=KOLOR:ASPB:inventory:S2M
	key.ma.KOLOR.ASPB.inventory.error=KOLOR:ASPB:inventory:error
	# SC-Cube
	key.ca.KOLOR.inventory.C2S=${ca.KOLOR}:inventory:C2S
	key.ca.KOLOR.inventory.S2C=${ca.KOLOR}:inventory:S2C
	key.ca.KOLOR.inventory.error=${ca.KOLOR}:inventory:error
	*/
	
	@Value("${key.ma.KOLOR.ASPB.order}")
	private String KEY_KOLOR_ASPB_ORDER;
	
	@Value("${key.ca.KOLOR.ASPB.order.release}")
	private String KEY_KOLOR_ASPB_ORDER_RELEASE;
	
	
	// Order Update
	@Value("${key.ma.KOLOR.ASPB.orderUpdate.M2S}")
	private String KEY_MA_KOLOR_ASPB_ORDER_UPDATE_M2S;
	@Value("${key.ma.KOLOR.ASPB.orderUpdate.S2M}")
	private String KEY_MA_KOLOR_ASPB_ORDER_UPDATE_S2M;
	
	@Value("${key.ca.KOLOR.ASPB.orderUpdate.C2S}")
	private String KEY_CA_KOLOR_ASPB_ORDER_UPDATE_C2S;
	@Value("${key.ca.KOLOR.ASPB.orderUpdate.S2C}")
	private String KEY_CA_KOLOR_ASPB_ORDER_UPDATE_S2C;
	
	
	
	
	// Product Key
	@Value("${key.ma.KOLOR.ASPB.product.S2M}")
	private String KEY_MA_KOLOR_ASPB_PRODUCT_S2M;
	@Value("${key.ca.KOLOR.product.C2S}")
	private String KEY_CA_KOLOR_ASPB_PRODUCT_C2S;
	@Value("${key.ca.KOLOR.product.S2C}")
	private String KEY_CA_KOLOR_ASPB_PRODUCT_S2C;
	
	
	
	// Inventory Key
	@Value("${key.ma.KOLOR.ASPB.inventory.S2M}")
	private String KEY_MA_KOLOR_ASPB_INVENTORY_S2M;
	@Value("${key.ca.KOLOR.inventory.C2S}")
	private String KEY_CA_KOLOR_ASPB_INVENTORY_C2S;
	@Value("${key.ca.KOLOR.inventory.S2C}")
	private String KEY_CA_KOLOR_ASPB_INVENTORY_S2C;
	
	@Value("${key.ma.KOLOR.ASPB.order.error}")
	private String KEY_MA_KOLOR_ASPB_ORDER_ERROR;
	
	@Value("${key.ma.KOLOR.ASPB.product.error}")
	private String KEY_MA_KOLOR_ASPB_PRODUCT_ERROR;
	@Value("${key.ca.KOLOR.product.error}")
	private String KEY_CA_KOLOR_PRODUCT_ERROR;
	
	@Value("${key.ma.KOLOR.ASPB.inventory.error}")
	private String KEY_MA_KOLOR_ASPB_INVENTORY_ERROR;
	@Value("${key.ca.KOLOR.inventory.error}")
	private String KEY_CA_KOLOR_INVENTORY_ERROR;
	
	
	private static String KEY_CUBE_3202_90  = ":order:3202:90";			// 출고의뢰결과 - 품절취소
	private static String KEY_CUBE_3202_09  = ":order:3202:09";			// 출고의뢰결과 - 실패
	private static String KEY_CUBE_9000_REQ = ":order:cancel";			// 주문취소요청
	private static String KEY_CUBE_9000_RES = ":order:cancel:result";	// 주문취소요청결과
	
	private static String ENT_KOLOR = "KOLOR";
	private static String SELLER_KOLOR_ASPB = "ASPB";
	private static String KOLOR_ASPB_KEY = ENT_KOLOR+":"+SELLER_KOLOR_ASPB;
	
	@RequestMapping(value = "/getRedisDataListByCh.do")
	public ModelAndView getRedisDataListByCh( @RequestParam String entCode, @RequestParam String sellCode) throws Exception{
		
		
		HashMap<String, Object> keyInfo = new HashMap<String, Object>();
		
		// 주문생성 데이타 1100
		HashMap<String, Object> info_1100 = new HashMap<String, Object>();
		info_1100.put("name", KEY_KOLOR_ASPB_ORDER);
		info_1100.put("desc", "주문생성");
		info_1100.put("size", listOps.size(KEY_KOLOR_ASPB_ORDER));
		
		keyInfo.put("create", info_1100);
		
		
		// 주문확정 대상 데이타 
		HashMap<String, Object> info_release = new HashMap<String, Object>();
		info_release.put("name", KEY_KOLOR_ASPB_ORDER_RELEASE);
		info_release.put("desc", "주문확정대상");
		info_release.put("size", listOps.size(KEY_KOLOR_ASPB_ORDER_RELEASE));
		
		keyInfo.put("release", info_release);
				
		
		// 주문상태 변경 데이타 MA-SC
		HashMap<String, Object> info_update_m2s = new HashMap<String, Object>();
		info_update_m2s.put("name", KEY_MA_KOLOR_ASPB_ORDER_UPDATE_M2S);
		info_update_m2s.put("desc", "주문상태변경 MA to SC");
		info_update_m2s.put("size", listOps.size(KEY_MA_KOLOR_ASPB_ORDER_UPDATE_M2S));
		
		// 주문상태 변경 데이타 SC-MA
		HashMap<String, Object> info_update_s2m = new HashMap<String, Object>();
		info_update_s2m.put("name", KEY_MA_KOLOR_ASPB_ORDER_UPDATE_S2M);
		info_update_s2m.put("desc", "주문상태변경 SC to MA");
		info_update_s2m.put("size", listOps.size(KEY_MA_KOLOR_ASPB_ORDER_UPDATE_S2M));
		
		
		// 주문상태 변경 데이타 SC-CA
		HashMap<String, Object> info_update_s2c = new HashMap<String, Object>();
		info_update_s2c.put("name", KEY_CA_KOLOR_ASPB_ORDER_UPDATE_S2C);
		info_update_s2c.put("desc", "주문상태변경 SC to Cube");
		info_update_s2c.put("size", listOps.size(KEY_CA_KOLOR_ASPB_ORDER_UPDATE_S2C));
		
		// 주문상태 변경 데이타 CA-SC
		HashMap<String, Object> info_update_c2s = new HashMap<String, Object>();
		info_update_c2s.put("name", KEY_CA_KOLOR_ASPB_ORDER_UPDATE_C2S);
		info_update_c2s.put("desc", "주문상태변경 Cube to SC");
		info_update_c2s.put("size", listOps.size(KEY_CA_KOLOR_ASPB_ORDER_UPDATE_C2S));
		
		
		keyInfo.put("info_update_m2s", info_update_m2s);
		keyInfo.put("info_update_s2m", info_update_s2m);
		keyInfo.put("info_update_s2c", info_update_s2c);
		keyInfo.put("info_update_c2s", info_update_c2s);
				
				
		// 상품연동
		HashMap<String, Object> info_product_s2m = new HashMap<String, Object>();
		info_product_s2m.put("name", KEY_MA_KOLOR_ASPB_PRODUCT_S2M);
		info_product_s2m.put("desc", "상품연동 SC to MA");
		info_product_s2m.put("size", listOps.size(KEY_MA_KOLOR_ASPB_PRODUCT_S2M));
		// 상품연동 Cube-SC
		HashMap<String, Object> info_product_c2s = new HashMap<String, Object>();
		info_product_c2s.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_C2S);
		info_product_c2s.put("desc", "상품연동 Cube to SC");
		info_product_c2s.put("size", listOps.size(KEY_CA_KOLOR_ASPB_PRODUCT_C2S));
		// 상품연동 SC-Cube
		HashMap<String, Object> info_product_s2c = new HashMap<String, Object>();
		info_product_s2c.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_S2C);
		info_product_s2c.put("desc", "상품연동 SC to Cube");
		info_product_s2c.put("size", listOps.size(KEY_CA_KOLOR_ASPB_PRODUCT_S2C));
		
		keyInfo.put("info_product_s2m", info_product_s2m);
		keyInfo.put("info_product_c2s", info_product_c2s);
		keyInfo.put("info_product_s2c", info_product_s2c);
		
		
		// 재고연동
		HashMap<String, Object> info_inventory_s2m = new HashMap<String, Object>();
		info_inventory_s2m.put("name", KEY_MA_KOLOR_ASPB_INVENTORY_S2M);
		info_inventory_s2m.put("desc", "재고연동 SC to MA");
		info_inventory_s2m.put("size", listOps.size(KEY_MA_KOLOR_ASPB_INVENTORY_S2M));
		// 재고연동 Cube-SC
		HashMap<String, Object> info_inventory_c2s = new HashMap<String, Object>();
		info_inventory_c2s.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_C2S);
		info_inventory_c2s.put("desc", "재고연동 Cube to SC");
		info_inventory_c2s.put("size", listOps.size(KEY_CA_KOLOR_ASPB_PRODUCT_C2S));
		// 재고연동 SC-Cube
		HashMap<String, Object> info_inventory_s2c = new HashMap<String, Object>();
		info_inventory_s2c.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_S2C);
		info_inventory_s2c.put("desc", "재고연동 SC to Cube");
		info_inventory_s2c.put("size", listOps.size(KEY_CA_KOLOR_ASPB_INVENTORY_S2C));
		
		keyInfo.put("info_inventory_s2m", info_inventory_s2m);
		keyInfo.put("info_inventory_c2s", info_inventory_c2s);
		keyInfo.put("info_inventory_s2c", info_inventory_s2c);
		
		/*
			품절취소:   조직코드:판매채널코드:order:3202:90
			출고의뢰실패: 조직코드:판매채널코드:order:3202:09
	
			주문취소요청: 조직코드:판매채널코드:order:cancel
			주문취소요청결과:조직코드:판매채널코드:order:cancel:result
		*/
		HashMap<String, Object> info_cube_3202_90 = new HashMap<String, Object>();
		info_cube_3202_90.put("name", KOLOR_ASPB_KEY+KEY_CUBE_3202_90);
		info_cube_3202_90.put("desc", "Cube출고의뢰결과 - 품절취소");
		info_cube_3202_90.put("size", listOps.size(KOLOR_ASPB_KEY+KEY_CUBE_3202_90));
		
		keyInfo.put("info_cube_3202_90", info_cube_3202_90);
		
		HashMap<String, Object> info_cube_3202_09 = new HashMap<String, Object>();
		info_cube_3202_09.put("name", KOLOR_ASPB_KEY+KEY_CUBE_3202_09);
		info_cube_3202_09.put("desc", "Cube출고의뢰결과 - 실패");
		info_cube_3202_09.put("size", listOps.size(KOLOR_ASPB_KEY+KEY_CUBE_3202_09));
		
		keyInfo.put("info_cube_3202_09", info_cube_3202_09);
		
		HashMap<String, Object> info_cube_9000_req = new HashMap<String, Object>();
		info_cube_9000_req.put("name", KOLOR_ASPB_KEY+KEY_CUBE_9000_REQ);
		info_cube_9000_req.put("desc", "Cube주문취소요청");
		info_cube_9000_req.put("size", listOps.size(KOLOR_ASPB_KEY+KEY_CUBE_9000_REQ));
		
		keyInfo.put("info_cube_9000_req", info_cube_9000_req);
		
		HashMap<String, Object> info_cube_9000_res = new HashMap<String, Object>();
		info_cube_9000_res.put("name", KOLOR_ASPB_KEY+KEY_CUBE_9000_RES);
		info_cube_9000_res.put("desc", "Cube주문취소요청 결과");
		info_cube_9000_res.put("size", listOps.size(KOLOR_ASPB_KEY+KEY_CUBE_9000_RES));
		
		keyInfo.put("info_cube_9000_res", info_cube_9000_res);
		
		
		/**************************** Error Key ****************************/
		// 에러키
		HashMap<String, Object> keyInfo_error = new HashMap<String, Object>();
		
		/*
		 * 
			주문생성에러: 조직코드:판매채널코드:order:error
			주문상태별에러: 조직코드:판매채널코드:order:update:error:상태코드
			상품연동에러: 사업부코드:product:error
			재고연동에러: 사업부코드:inventory:error

		 */
		// 주문생성연동에러
		HashMap<String, Object> info_create_err = new HashMap<String, Object>();
		info_create_err.put("name", KEY_MA_KOLOR_ASPB_ORDER_ERROR);
		info_create_err.put("desc", "주문생성에러");
		info_create_err.put("size", listOps.size(KEY_MA_KOLOR_ASPB_ORDER_ERROR));
		keyInfo_error.put("info_create_err", info_create_err);
		
		Set<String> kolor_order_error_keys = maStringRedisTemplate.keys(KOLOR_ASPB_KEY+"order:update:error:*");
		List<HashMap<String,String>> err_key_list = new ArrayList<HashMap<String,String>>();
		for( String keyName : kolor_order_error_keys ){
			
			HashMap<String, String> errKeyMap = new HashMap<String, String>();
			errKeyMap.put("name", keyName);
			errKeyMap.put("size", listOps.size(keyName)+"");
			err_key_list.add(errKeyMap);
		}
		keyInfo_error.put("err_key_list", err_key_list);
		
		// 상품연동에러
		HashMap<String, Object> info_product_err_ma = new HashMap<String, Object>();
		info_product_err_ma.put("name", KEY_MA_KOLOR_ASPB_PRODUCT_ERROR);
		info_product_err_ma.put("desc", "상품연동에러 - MA");
		info_product_err_ma.put("size", listOps.size(KEY_MA_KOLOR_ASPB_PRODUCT_ERROR));
		
		keyInfo_error.put("info_product_err_ma", info_product_err_ma);
		
		HashMap<String, Object> info_product_err_ca = new HashMap<String, Object>();
		info_product_err_ca.put("name", KEY_CA_KOLOR_PRODUCT_ERROR);
		info_product_err_ca.put("desc", "상품연동에러 - Cube");
		info_product_err_ca.put("size", listOps.size(KEY_CA_KOLOR_PRODUCT_ERROR));
		
		keyInfo_error.put("info_product_err_ca", info_product_err_ca);
		
		// 재고연동에러
		HashMap<String, Object> info_inventory_err_ma = new HashMap<String, Object>();
		info_inventory_err_ma.put("name", KEY_MA_KOLOR_ASPB_INVENTORY_ERROR);
		info_inventory_err_ma.put("desc", "재고연동에러 - MA");
		info_inventory_err_ma.put("size", listOps.size(KEY_MA_KOLOR_ASPB_INVENTORY_ERROR));
		
		keyInfo_error.put("info_inventory_err_ma", info_inventory_err_ma);
		
		HashMap<String, Object> info_inventory_err_ca = new HashMap<String, Object>();
		info_inventory_err_ca.put("name", KEY_CA_KOLOR_INVENTORY_ERROR);
		info_inventory_err_ca.put("desc", "재고연동에러 - Cube");
		info_inventory_err_ca.put("size", listOps.size(KEY_CA_KOLOR_INVENTORY_ERROR));
		
		keyInfo_error.put("info_inventory_err_ca", info_inventory_err_ca);
		
		
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("KOLOR", keyInfo);
		mav.addObject("KOLOR_ERR", keyInfo_error);
		
		mav.setViewName("admin/system/redis_list_all");
		return mav;   
		
	
	}
	/**
	 * 오더목록조회 (판매오더, 반품오더)
	 *  - 오더의 상태가 Create(1100) ~ Shipped(3700)까지만 조회
	 * 
	 * @param paramMap
	 * @param doc_type
	 * @param orderNos
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getRedisKeyData.sc")
	public ModelAndView getRedisDataListByKey( @RequestParam String key) throws Exception{ 
		
		logger.debug("[key]"+key);
			
			
		List<String> keyDataList= listOps.range(key, 0, -1);
		System.out.println("["+key+" - size]"+keyDataList.size());
			
		List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
		int seq = 1;
		for(String keyData: keyDataList){
			Map<String, String> keyDataMap = new HashMap<String, String>();
			keyDataMap.put("seq", seq+"");
			keyDataMap.put("keyData", keyData);
			
			dataList.add(keyDataMap);
			seq++;
		}
		
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data", dataList);
		return mav;
		
	}
	
	
	@RequestMapping(value = "/deleteKey.sc")
	public ModelAndView deleteRedisKetDataByIndex( @RequestParam String key, @RequestParam int index, @RequestParam String t_key_data) throws Exception{ 
		
		logger.debug("[key]"+key);
		logger.debug("[index]"+index);
		logger.debug("[t_key_data]"+t_key_data);
		
		long cnt = listOps.remove(key, index, t_key_data);
		logger.debug("[cnt]"+cnt);
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", cnt);
		return mav;
		
	}
		
}

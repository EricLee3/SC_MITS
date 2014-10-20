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
	key.ca.KOLOR.ASPB.order.error=${ca.KOLOR}:ASPB:order:error

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
	
	
	
	
	
	@RequestMapping(value = "/getRedisDataListByCh.do")
	public ModelAndView getRedisDataListByCh( @RequestParam String entCode, @RequestParam String sellCode) throws Exception{
		
		
		HashMap<String, Object> keyInfo = new HashMap<String, Object>();
		
		// 주문생성 데이타 1100
		HashMap<String, Object> info_1100 = new HashMap<String, Object>();
		List<String> list_1100 = listOps.range(KEY_KOLOR_ASPB_ORDER, 0, -1);
		info_1100.put("name", KEY_KOLOR_ASPB_ORDER);
		info_1100.put("desc", "주문생성");
		info_1100.put("list", list_1100);
		
		keyInfo.put("create", info_1100);
		
		
		// 주문확정 대상 데이타 
		HashMap<String, Object> info_release = new HashMap<String, Object>();
		List<String> list_release = listOps.range(KEY_KOLOR_ASPB_ORDER_RELEASE, 0, -1);
		info_release.put("name", KEY_KOLOR_ASPB_ORDER_RELEASE);
		info_release.put("desc", "주문확정대상");
		info_release.put("list", list_release);
		
		keyInfo.put("release", info_release);
				
		
		// 주문상태 변경 데이타 MA-SC
		HashMap<String, Object> info_update_m2s = new HashMap<String, Object>();
		List<String> list_update_m2s = listOps.range(KEY_MA_KOLOR_ASPB_ORDER_UPDATE_M2S, 0, -1);
		info_update_m2s.put("name", KEY_MA_KOLOR_ASPB_ORDER_UPDATE_M2S);
		info_update_m2s.put("desc", "주문상태변경 MA to SC");
		info_update_m2s.put("list", list_update_m2s);
		
		// 주문상태 변경 데이타 SC-MA
		HashMap<String, Object> info_update_s2m = new HashMap<String, Object>();
		List<String> list_update_s2m = listOps.range(KEY_MA_KOLOR_ASPB_ORDER_UPDATE_S2M, 0, -1);
		info_update_s2m.put("name", KEY_MA_KOLOR_ASPB_ORDER_UPDATE_S2M);
		info_update_s2m.put("desc", "주문상태변경 SC to MA");
		info_update_s2m.put("list", list_update_s2m);
		
		
		// 주문상태 변경 데이타 SC-CA
		HashMap<String, Object> info_update_s2c = new HashMap<String, Object>();
		List<String> list_update_s2c = listOps.range(KEY_CA_KOLOR_ASPB_ORDER_UPDATE_S2C, 0, -1);
		info_update_s2c.put("name", KEY_CA_KOLOR_ASPB_ORDER_UPDATE_S2C);
		info_update_s2c.put("desc", "주문상태변경 SC to Cube");
		info_update_s2c.put("list", list_update_s2c);
		
		// 주문상태 변경 데이타 CA-SC
		HashMap<String, Object> info_update_c2s = new HashMap<String, Object>();
		List<String> list_update_c2s = listOps.range(KEY_CA_KOLOR_ASPB_ORDER_UPDATE_C2S, 0, -1);
		info_update_c2s.put("name", KEY_CA_KOLOR_ASPB_ORDER_UPDATE_C2S);
		info_update_c2s.put("desc", "주문상태변경 Cube to SC");
		info_update_c2s.put("list", list_update_c2s);
		
		
		keyInfo.put("info_update_m2s", info_update_m2s);
		keyInfo.put("info_update_s2m", info_update_s2m);
		keyInfo.put("info_update_s2c", info_update_s2c);
		keyInfo.put("info_update_c2s", info_update_c2s);
				
				
		// 상품연동
		HashMap<String, Object> info_product_s2m = new HashMap<String, Object>();
		List<String> list_product_s2m = listOps.range(KEY_MA_KOLOR_ASPB_PRODUCT_S2M, 0, -1);
		info_product_s2m.put("name", KEY_MA_KOLOR_ASPB_PRODUCT_S2M);
		info_product_s2m.put("desc", "상품연동 SC to MA");
		info_product_s2m.put("list", list_product_s2m);
		// 상품연동 Cube-SC
		HashMap<String, Object> info_product_c2s = new HashMap<String, Object>();
		List<String> list_product_c2s = listOps.range(KEY_CA_KOLOR_ASPB_PRODUCT_C2S, 0, -1);
		info_product_c2s.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_C2S);
		info_product_c2s.put("desc", "상품연동 Cube to SC");
		info_product_c2s.put("list", list_product_c2s);
		// 상품연동 SC-Cube
		HashMap<String, Object> info_product_s2c = new HashMap<String, Object>();
		List<String> list_product_s2c = listOps.range(KEY_CA_KOLOR_ASPB_PRODUCT_S2C, 0, -1);
		info_product_s2c.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_S2C);
		info_product_s2c.put("desc", "상품연동 SC to Cube");
		info_product_s2c.put("list", list_product_s2c);
		
		keyInfo.put("info_product_s2m", info_product_s2m);
		keyInfo.put("info_product_c2s", info_product_c2s);
		keyInfo.put("info_product_s2c", info_product_s2c);
		
		
		// 재고연동
		HashMap<String, Object> info_inventory_s2m = new HashMap<String, Object>();
		List<String> list_inventory_s2m = listOps.range(KEY_MA_KOLOR_ASPB_INVENTORY_S2M, 0, -1);
		info_inventory_s2m.put("name", KEY_MA_KOLOR_ASPB_INVENTORY_S2M);
		info_inventory_s2m.put("desc", "재고연동 SC to MA");
		info_inventory_s2m.put("list", list_inventory_s2m);
		// 재고연동 Cube-SC
		HashMap<String, Object> info_inventory_c2s = new HashMap<String, Object>();
		List<String> list_inventory_c2s = listOps.range(KEY_CA_KOLOR_ASPB_INVENTORY_C2S, 0, -1);
		info_inventory_c2s.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_C2S);
		info_inventory_c2s.put("desc", "재고연동 Cube to SC");
		info_inventory_c2s.put("list", list_inventory_c2s);
		// 재고연동 SC-Cube
		HashMap<String, Object> info_inventory_s2c = new HashMap<String, Object>();
		List<String> list_inventory_s2c = listOps.range(KEY_CA_KOLOR_ASPB_INVENTORY_S2C, 0, -1);
		info_inventory_s2c.put("name", KEY_CA_KOLOR_ASPB_PRODUCT_S2C);
		info_inventory_s2c.put("desc", "재고연동 SC to Cube");
		info_inventory_s2c.put("list", list_inventory_s2c);
		
		keyInfo.put("info_inventory_s2m", info_inventory_s2m);
		keyInfo.put("info_inventory_c2s", info_inventory_c2s);
		keyInfo.put("info_inventory_s2c", info_inventory_s2c);
		
		
		
		/**************************** Error Key ****************************/
		
		// 에러키
		HashMap<String, Object> keyInfo_error = new HashMap<String, Object>();
		
		
		
		
		
		
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("KOLOR", keyInfo);
		
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
	@RequestMapping(value = "/getRedisDataList.do")
	public ModelAndView getRedisDataList( @RequestParam Map<String, String> paramMap) throws Exception{ 
		
		// KOLOR, DA, ISEC etc
//		HashMap<String, HashMap<String, List<HashMap<String,String>>>> allKeyDataMap = new HashMap<String, HashMap<String,List<HashMap<String,String>>>>();
		
		// Order MA, Order CA, Product MA, Product CA, Inventory MA, Invetory CA
		HashMap<String, List<HashMap<String,String>>> dataMap_KOLOR = new HashMap<String, List<HashMap<String,String>>>();
		
		Set<String> ma_all_keys_SLV= maStringRedisTemplate.keys("KOLOR:*:*");
		Set<String> ca_all_keys_SLV= maStringRedisTemplate.keys("80:*:*");
		
		
		
		// KOLOR - MA
		// TODO: 상품-재고 키 추출로직 보완
		List<HashMap<String,String>> slv_order_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_product_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_inventory_list = new ArrayList<HashMap<String,String>>();
		for( String keyName : ma_all_keys_SLV ){
			
			String keyCh = keyName.split("\\:")[1];
			String keyType = keyName.split("\\:")[2];
			
			
			List<String> keyDataList= listOps.range(keyName, 0, -1);
			System.out.println("["+keyName+" - size]"+keyDataList.size());
			
			HashMap<String, String> keyInfo = new HashMap<String, String>();
			keyInfo.put("name", keyName);
			keyInfo.put("size", ""+keyDataList.size());
			
			if("order".equals(keyType)){
				slv_order_list.add(keyInfo);
			}
			
			
			if("product".equals(keyCh)){
				slv_product_list.add(keyInfo);
			}
			
			if("inventory".equals(keyCh)){
				slv_inventory_list.add(keyInfo);
			}
		}
		dataMap_KOLOR.put("order_ma", slv_order_list);
		dataMap_KOLOR.put("product_ma", slv_product_list);
		dataMap_KOLOR.put("inventory_ma", slv_inventory_list);
		
		
		List<HashMap<String,String>> slv_ca_order_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_ca_product_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_ca_inventory_list = new ArrayList<HashMap<String,String>>();
		for( String keyName : ca_all_keys_SLV ){
			
			String keyCh = keyName.split("\\:")[1];
			String keyType = keyName.split("\\:")[2];
			
			List<String> keyDataList= listOps.range(keyName, 0, -1);
			System.out.println("["+keyName+" - size]"+keyDataList.size());
			
			HashMap<String, String> keyInfo = new HashMap<String, String>();
			keyInfo.put("name", keyName);
			keyInfo.put("size", ""+keyDataList.size());
			
			if("order".equals(keyType)){
				slv_ca_order_list.add(keyInfo);
			}
			
			
			if("product".equals(keyCh)){
				slv_ca_product_list.add(keyInfo);
			}
			
			
			if("inventory".equals(keyCh)){
				slv_ca_inventory_list.add(keyInfo);
			}
		}
		dataMap_KOLOR.put("order_ca", slv_ca_order_list);
		dataMap_KOLOR.put("product_ca", slv_ca_product_list);
		dataMap_KOLOR.put("inventory_ca", slv_ca_inventory_list);
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("KOLOR", dataMap_KOLOR);
		mav.setViewName("admin/system/redis_list_all");
		return mav;   
		
	}
}

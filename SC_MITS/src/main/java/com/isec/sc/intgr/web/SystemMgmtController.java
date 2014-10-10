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
		HashMap<String, List<HashMap<String,String>>> dataMap_DA = new HashMap<String, List<HashMap<String,String>>>();
		HashMap<String, List<HashMap<String,String>>> dataMap_ISEC = new HashMap<String, List<HashMap<String,String>>>();
		
		Set<String> ma_all_keys_SLV= maStringRedisTemplate.keys("KOLOR:*:*");
		Set<String> ca_all_keys_SLV= maStringRedisTemplate.keys("80:*:*");
		
		
		
		
		Set<String> ma_all_keys_DA= maStringRedisTemplate.keys("DA:*:*");
		Set<String> ma_all_keys_ISEC= maStringRedisTemplate.keys("ISEC:*:*");
		
		
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

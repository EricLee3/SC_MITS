/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Ignore; 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class OMCTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	@Autowired	private Environment env;
	
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	@Value("${ui.status.text.kr.1000}")
	private String ui1000;
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	@Value("${redis.port}")
	private String redis_port;
	
	@Value("${sc.order.type.sales}")
	private String SC_ORDER_TYPE_SALES;
	
	
	@Test
	public void testEnvGetProperties() throws Exception{
		
		System.out.println("[SC_ORDER_TYPE_SALES]"+SC_ORDER_TYPE_SALES);
	}
	
	@Test
	public void testDelListData() throws Exception{
		
		
		String a = "VELVET MOSS & ' \" <>JASMINE";
		
		System.out.println(CommonUtil.replaceXmlStr(a));
		
//		String cancelReqKey = "KOLOR:ASPB:order:cancel";
//		
//		List<String> cancelReqRedisList = listOps.range(cancelReqKey, 0, -1);
//		for( int i=0; i<cancelReqRedisList.size(); i++){
//			
//			String jsonData = cancelReqRedisList.get(i);
//			
//			System.out.println("[jsonData]"+ jsonData);
//			HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
//			
//			String cancelOrderNo = cancelReqMap.get("orderNo");
//			
//			if("Y100000380".equals(cancelOrderNo)){
//				System.out.println("[cancelOrderNo]"+cancelOrderNo);
//				listOps.remove(cancelReqKey, i, jsonData);
//				break;
//			}
//		}
		
	}
	
	
	@Test
	public void testListToSet(){
		
		
		List<HashMap<String,Object>> alphaList = new ArrayList<HashMap<String,Object>>();
		
		for(int i=0; i<10; i++){
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("A", "1");
			map.put("B", "2");
			
			alphaList.add(map);
		}
		
		
        System.out.println("List values .....");
        for (HashMap<String,Object> alpha : alphaList)
        {
            System.out.println(alpha.get("A"));
            System.out.println(alpha.get("B"));
        }
        
        Set<HashMap<String,Object>> alphaSet = new HashSet<HashMap<String,Object>>(alphaList);
        System.out.println("\nSet values .....");
        for (HashMap<String,Object> alpha : alphaSet)
        {
        	 	System.out.println(alpha.get("A"));
             System.out.println(alpha.get("B"));
        }
		
	}
	
	
	@Test
	public void testRedisChar(){
		
		try{
			
			
			ObjectMapper mapper = new ObjectMapper();
//			String outputMsgs = mapper.writeValueAsString(allKeyDataMap);
			
			String a = "한글";
			
			
			
			valOps.set("test", a);
			System.out.println(valOps.get("test"));
			
		}catch(Exception e){
			
			
		}
		
	}
	
	@Test
	public void testGetRelease(){
	
		try{
		
			String output = sterlingApiDelegate.getShipNodeByReleaseKey("100000395", "20141008133900227117");
			
			System.out.println(output);
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testEnv(){
		
		System.out.println(""+env.getProperty("ca.80"));
		System.out.println(""+redis_ma_index);
		System.out.println(""+env.getProperty("ui.status.text.kr.1000"));
		System.out.println(""+ui1000);
		
	}
	
	
	@Ignore
	public void getRedisKeydata(){
		
		// KOLOR, DA, ISEC etc
		HashMap<String, HashMap<String, List<HashMap<String,String>>>> allKeyDataMap = new HashMap<String, HashMap<String,List<HashMap<String,String>>>>();
		
		// Order MA, Order CA, Product MA, Product CA, Inventory MA, Invetory CA
		HashMap<String, List<HashMap<String,String>>> dataMap_KOLOR = new HashMap<String, List<HashMap<String,String>>>();
		HashMap<String, List<HashMap<String,String>>> dataMap_DA = new HashMap<String, List<HashMap<String,String>>>();
		HashMap<String, List<HashMap<String,String>>> dataMap_ISEC = new HashMap<String, List<HashMap<String,String>>>();
		
		Set<String> ma_all_keys_SLV= maStringRedisTemplate.keys("SLV:*:*:*");
		Set<String> ca_all_keys_SLV= maStringRedisTemplate.keys("80:*:*:*");
		
		
		
		
		Set<String> ma_all_keys_DA= maStringRedisTemplate.keys("DA:*:*:*");
		Set<String> ma_all_keys_ISEC= maStringRedisTemplate.keys("ISEC:*:*:*");
		
		
		// KOLOR - MA
		List<HashMap<String,String>> slv_order_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_product_list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> slv_inventory_list = new ArrayList<HashMap<String,String>>();
		for( String keyName : ma_all_keys_SLV ){
			
			String keyCh = keyName.split("\\:")[1];
			String keyType = keyName.split("\\:")[2];
			
			
			List<String> keyDataList= listOps.range(keyName, 0, -1);
			System.out.println("["+keyName+" - size]"+keyDataList.size());
			
			HashMap<String, String> keyInfo = new HashMap<String, String>();
			keyInfo.put(keyName, ""+keyDataList.size());
			
			if("order".equals(keyType)){
				slv_order_list.add(keyInfo);
				continue;
			}
			else if("product".equals(keyType)){
				slv_product_list.add(keyInfo);
				continue;
			}
			else if("inventory".equals(keyType)){
				slv_inventory_list.add(keyInfo);
				continue;
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
			keyInfo.put(keyName, ""+keyDataList.size());
			
			if("order".equals(keyType)){
				slv_ca_order_list.add(keyInfo);
				continue;
			}
			else if("product".equals(keyType)){
				slv_ca_product_list.add(keyInfo);
				continue;
			}
			else if("inventory".equals(keyType)){
				slv_ca_inventory_list.add(keyInfo);
				continue;
			}
		}
		dataMap_KOLOR.put("order_ca", slv_ca_order_list);
		dataMap_KOLOR.put("product_ca", slv_ca_product_list);
		dataMap_KOLOR.put("inventory_ca", slv_ca_inventory_list);
		
		
		allKeyDataMap.put("KOLOR", dataMap_KOLOR);
		
		try{
			ObjectMapper mapper = new ObjectMapper();
			String outputMsgs = mapper.writeValueAsString(allKeyDataMap);
			
			System.out.println(outputMsgs);
		}catch(Exception e){
			
		}
	}
	
	
	@Test
	public void orderCreateTest(){
		
		
		String createOrderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<Order DocumentType=\"0001\" EnterpriseCode=\"KOLOR\" SellerOrganizationCode=\"ASPB\" PaymentStatus=\"AUTHORIZED\" VendorID=\"11st\">"
			+"<OrderLines>"
				+"<OrderLine OrderedQty=\"2\">"
					+"<Item ItemID=\"AYB5CL50103L\" UnitOfMeasure=\"EACH\" ItemShortDesc=\"Capri Blue Blue Signature Jar BOHO-LUXE\" ItemDesc=\"http://www.aspenbay.co.kr/capri-blue/capri-blue-blue-signature-jar-boho-luxe.html\"/>"
					+"<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"49000.00\"/>"
					+"<LineCharges>"
						+"<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10.00\" />"
						+"<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5.00\" />"
					+"</LineCharges>"
					+"<LineTaxes>"
						+"<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" TaxableFlag=\"Y\" Tax=\"5.00\" />"
					+"</LineTaxes>"
				+"</OrderLine>"
				+"<OrderLine OrderedQty=\"3\">"
					+"<Item ItemID=\"AYE5CL60303F\" UnitOfMeasure=\"EACH\" ItemShortDesc=\"Capri Blue Blue Signature Jar\" ItemDesc=\"http://www.aspenbay.co.kr/capri-blue/capri-blue-blue-signature-jar-boho-luxe.html\"/>"
					+"<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"39000.00\"/>"
					+"<LineCharges>"
						+"<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10.00\" />"
						+"<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5.00\" />"
					+"</LineCharges>"
					+"<LineTaxes>"
						+"<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" TaxableFlag=\"Y\" Tax=\"5.00\" />"
					+"</LineTaxes>"
				+"</OrderLine>"
			+"</OrderLines>"
			+"<PersonInfoShipTo FirstName=\"홍\" LastName=\"길동\" EMailID=\"yg.jang@isecommerce.co.kr\" DayPhone=\"02-1234-5678\" MobilePhone=\"010-1234-5678\" AddressLine1=\"강남구 삼성동 78번지\" AddressLine2=\"삼안빌딩 7층 SC-Team\" ZipCode=\"123-456\" City=\"서울\" Country=\"KR\" />"
			+"<PersonInfoBillTo  FirstName=\"이\" LastName=\"순신\" EMailID=\"yg.jang@isecommerce.co.kr\" DayPhone=\"02-1111-2222\" MobilePhone=\"010-1111-2222\" AddressLine1=\"강남구 삼성동 78번지\" AddressLine2=\"삼안빌딩 7층 SC-Team\" ZipCode=\"123-456\" City=\"서울\" Country=\"KR\" />"
			+"<PaymentMethods>"
//				+"<PaymentMethod PaymentType=\"CREDIT_CARD\" CreditCardNo=\"0000000000000000\">"+"</PaymentMethod>"
				+"<PaymentMethod PaymentType=\"RTAccTransfer\" CustomerAccountNo=\"0000000000000000\">"+"</PaymentMethod>"
			+"</PaymentMethods>"
			+"<Instructions>"
	        +    "<Instruction InstructionText=\"msg\" InstructionType=\"DLV_MSG\" />"
	        +"</Instructions>"
		+"</Order>";
		
		
		String shipmentXML = "{\"orderId\":\"174004\",\"status\":\"3701\","
							+ "\"shipment\":[{\"carrierCode\":\"UPS\", \"carrierTitle\":\"UPS\",\"trackingNo\":\"11111\","
								+ "\"items\":[{\"itemId\":\"Brighton_Sunset Orange_sku\",\"qty\":\"1.00\"}]"
							+ "}]"
							+ "}";
		
		
		String keyName = "KOLOR:ASPB:order";
		
		try{
			
//			FileInputStream fis = new FileInputStream("/Users/ykjang/git/SC_MITS/SC_MITS/src/test/java/com/isec/sc/CreateOrder_input_AspenBay.xml");
//			
//			BufferedReader br = new BufferedReader( new InputStreamReader(fis, "UTF-8" ));
//			StringBuilder sb = new StringBuilder();
//		    String line;
//			while(( line = br.readLine()) != null ) {
//			   sb.append( line );
//			   sb.append( '\n' );
//			}
			
//			System.out.println(sb.toString());
			
//			String content = new Scanner(new File("/Users/ykjang/git/SC_MITS/SC_MITS/src/test/java/com/isec/sc/CreateOrder_input_AspenBay.xml")).useDelimiter("\\Z").next();
//			System.out.println(content);
			
			listOps.leftPush(keyName, createOrderXML );
			
//			for(int i=0; i<10; i++){
//				listOps.leftPush("ISEC:JNS:order", wcsXML);
//			}
			
//			
//			listOps.leftPush("ISEC:JNS:order:update:S2M", shipm/entXML);
			
//			Map<String, String> sendMsgMap = new HashMap<String, String>();
//			sendMsgMap.put("db", redis_ma_index);
//			sendMsgMap.put("type", "order");
//			sendMsgMap.put("key", "ISEC:JNS:order");
			
			// Java Object(Map) to JSON
//			ObjectMapper mapper = new ObjectMapper();
//			String sendMsg = mapper.writeValueAsString(sendMsgMap);
//			maStringRedisTemplate.convertAndSend(ch_ma_jns_order, sendMsg);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	
	@Test
	public void testConfirmShipment(){
		
		
		String msg = "{		\"orderHeaderKey\":\"20141018000000271014\",		\"sell_code\":\"ASPB\",		\"status\":\"3700\",		\"org_code\":\"80\",		\"list\":[		{		\"custNm\":\"이천재　\",		\"orderLineKey\":\"20141018000000271015\",		\"ship_node\":\"WH601\",		\"custHp\":\"010-9890-7118\",		\"orderId\":\"100001351\",		\"receiptZipcode\":\"157-880\",		\"statuscd\":\"01\",		\"sell_code\":\"ASPB\",		\"orderReleaseKey\":\"20141020091946272349\",		\"itemNm\":\"ASPENBAY_TEST7_2차\",		\"org_code\":\"80\",		\"receiptAddr1\":\"서울특별시강서구강서로12길14-21(인터시티빌라)\",		\"qty\":\"1\",		\"receiptTel\":\"010-9890-7118\",		\"receiptNm\":\"이천재　\",		\"receiptAddr2\":\"C-401\",		\"statusMsg\":\"\",		\"itemId\":\"AYF5AC607016\",		\"receiptHp\":\"010-9890-7118\",		\"shipmentNo\":\"00001\",		\"orderLineNo\":\"1\",		\"deliveryMsg\":\"\",		\"salePrice\":\"23000\",		\"orderDt\":\"20141018\",		\"custTel\":\"010-9890-7118\",		\"expnm\":\"11111\",		\"expNo\":\"00\",		\"outDt\":\"20141016\",		\"outTime\":\"165021\"		}		],		\"tranDt\":\"20141022195842\",		\"orderDt\":\"20141018\",		\"vendor_id\":\"\",		\"orderId\":\"100001351\"		}";
		
		listOps.leftPush("80:ASPB:order:update:C2S", msg);
		
		
		
	}
	
	
	@Ignore
	public void deleteKeyData() {
		
	    String key = "SLV:ASPB:order";
	    String key1 = "SLV:ASPB:order:update:S2M";
	    String key2 = "SLV:ASPB:order:update:M2S";
	    String key3 = "SLV:ASPB:order:update:S2C";
	    String key4 = "SLV:ASPB:order:update:C2S";
	    
	    String errorKey = "SLV:ASPB:order:error";
	    
	    
	    maStringRedisTemplate.delete(key);
	    maStringRedisTemplate.delete(key1);
	    maStringRedisTemplate.delete(key2);
	    maStringRedisTemplate.delete(key3);
	    maStringRedisTemplate.delete(key4);
	    maStringRedisTemplate.delete(errorKey);
	}
	
	@Ignore
	public void getListData() {
		
	    String key = "SLV:ASPB:order";
	    String key1 = "SLV:ASPB:order:update:S2M";
	    String key2 = "SLV:ASPB:order:update:M2S";
	    String key3 = "80:product:S2C";
	    String key4 = "80:ON9999:order:update:C2S";
	    
	    String errorKey = "SLV:ASPB:order:error";
	    
	    
	    List<String> orderList = listOps.range(key, 0, -1);
	    System.out.println( "[SLV:ASPB:order]" +orderList.size());
	    
	    List<String> orderList1 = listOps.range(key1, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:S2M]" +orderList1.size());
	    
	    List<String> orderList2 = listOps.range(key2, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:M2S]" +orderList2.size());
	    
	    List<String> orderList3 = listOps.range(key3, 0, -1);
	    System.out.println( "["+key3+"]" +orderList3.size());
	    
	    List<String> orderList4 = listOps.range(key4, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:C2S]" +orderList4.size());
	    
	    
	    List<String> errorList = listOps.range(errorKey, 0, -1);
	    System.out.println( "[SLV:ASPB:order:error]" +errorList.size());
	    
	    for(int i=0; i<orderList1.size(); i++){
	    		System.out.println( "[S2M Data]" +orderList1.get(i));
	    }
	    
	    for(int i=0; i<orderList3.size(); i++){
	    		System.out.println( "[S2C Data]" +orderList3.get(i));
	    }
	   
	}
	
	@Ignore
	public void getListDataOUTRO() {
		
	    String key = "DA:OUTRO:order";
	    String key1 = "DA:OUTRO:order:update:S2M";
	    String key2 = "DA:OUTRO:order:update:M2S";
	    String key3 = "DA:OUTRO:order:update:S2C";
	    String key4 = "DA:OUTRO:order:update:C2S";
	    
	    String errorKey = "DA:OUTRO:order:error";
	    
	    
	    List<String> orderList = listOps.range(key, 0, -1);
	    System.out.println( "[DA:OUTRO:order]" +orderList.size());
	    
	    List<String> orderList1 = listOps.range(key1, 0, -1);
	    System.out.println( "[DA:OUTRO:order:update:S2M]" +orderList1.size());
	    
	    List<String> orderList2 = listOps.range(key2, 0, -1);
	    System.out.println( "[DA:OUTRO:order:update:M2S]" +orderList2.size());
	    
	    List<String> orderList3 = listOps.range(key3, 0, -1);
	    System.out.println( "[DA:OUTRO:order:update:S2C]" +orderList3.size());
	    
	    List<String> orderList4 = listOps.range(key4, 0, -1);
	    System.out.println( "[DA:OUTRO:order:update:C2S]" +orderList4.size());
	    
	    
	    List<String> errorList = listOps.range(errorKey, 0, -1);
	    System.out.println( "[DA:OUTRO:order:error]" +errorList.size());
	    
	    for(int i=0; i<orderList1.size(); i++){
	    		System.out.println( "[S2M Data]" +orderList1.get(i));
	    }
	    
	    for(int i=0; i<orderList3.size(); i++){
	    		System.out.println( "[S2C Data]" +orderList3.get(i));
	    }
	   
	}
	
	
	@Test
	public void getListDataJNS() {
		
	    String key = "ISEC:JNS:order";
	    
	    
	    String key1 = "ISEC:JNS:order:update:S2M";
	    String key2 = "ISEC:JNS:order:update:M2S";
	    String key3 = "ISEC:JNS:order:update:S2C";
	    String key4 = "ISEC:JNS:order:update:C2S";
	    
	    String key5 = "ISEC:JNS:order:release";
	    
	    String errorKey = "ISEC:JNS:order:error";
	    
	    
	    List<String> orderList = listOps.range(key, 0, -1);
	    System.out.println( "[ISEC:JNS:order]" +orderList.size());
	    
	    List<String> orderList1 = listOps.range(key1, 0, -1);
	    System.out.println( "[ISEC:JNS:order:update:S2M]" +orderList1.size());
	    
	    List<String> orderList2 = listOps.range(key2, 0, -1);
	    System.out.println( "[ISEC:JNS:order:update:M2S]" +orderList2.size());
	    
	    List<String> orderList3 = listOps.range(key3, 0, -1);
	    System.out.println( "[ISEC:JNS:order:update:S2C]" +orderList3.size());
	    
	    List<String> orderList4 = listOps.range(key4, 0, -1);
	    System.out.println( "[ISEC:JNS:order:update:C2S]" +orderList4.size());
	    
	    
	    List<String> orderList5 = listOps.range(key5, 0, -1);
	    System.out.println( "[ISEC:JNS:order:release]" +orderList5.size());
	    
	    List<String> errorList = listOps.range(errorKey, 0, -1);
	    System.out.println( "[ISEC:JNS:order:error]" +errorList.size());
	    
	    for(int i=0; i<orderList1.size(); i++){
	    		System.out.println( "[S2M Data]" +orderList1.get(i));
	    }
	    
	    for(int i=0; i<orderList3.size(); i++){
	    		System.out.println( "[S2C Data]" +orderList3.get(i));
	    }
	   
	}
	
	@Ignore
	public void etcTeset() {
	    System.out.println( CommonUtil.cuurentDateFromFormat("yyyyMMddHHssmm"));
	   
	}
	
	@Ignore
	public void deleteKeyDataVM() {
	    
	    String key = "80:product:S2C";
	    String key1 = "SLV:product:S2M";
	    String key2 = "80:product:error";
	    String key3 = "SLV:product:error";
	    
	    String key4 = "80:inventory:S2C";
	    String key5 = "SLV:inventory:S2M";
	    String key6 = "80:inventory:error";
	    String key7 = "SLV:inventory:error";
	    
	    maStringRedisTemplate.delete(key);
	    maStringRedisTemplate.delete(key1);
	    maStringRedisTemplate.delete(key2);
	    maStringRedisTemplate.delete(key3);
	    maStringRedisTemplate.delete(key4);
	    maStringRedisTemplate.delete(key5);
	    maStringRedisTemplate.delete(key6);
	    maStringRedisTemplate.delete(key7);
	}
	
	
	@Test
	public void insertCancelResTest() throws Exception{
		
		/**
		 * cancelResMap.put("orderNo", orderId);
		cancelResMap.put("enterPrise", entCode);
		cancelResMap.put("sellerOrg", sellCode);
		cancelResMap.put("status_code", cubeStatus);
		cancelResMap.put("status_text", cubeStatusMsg);
		cancelResMap.put("status_class", "danger");
		 */
		
		/*
		 *   01 - 성공
			 02 - 기처리
			 09 - 실패 또는 처리대상건 없음
			 90 - 출고확정건
		 */
		HashMap<String, String> cancelResMap = new HashMap<String, String>();
		cancelResMap.put("orderNo", "100001418");
		cancelResMap.put("enterPrise", "KOLOR");
		cancelResMap.put("sellerOrg", "ASPB");
		cancelResMap.put("status_code", "90");
		cancelResMap.put("status_text", "출고확정건");
		cancelResMap.put("status_class", "danger");
		
		
		ObjectMapper mapper = new ObjectMapper();
		String resJson = mapper.writeValueAsString(cancelResMap);
		
		String cancelResKey = "KOLOR:ASPB:order:cancel:result";
		
		listOps.leftPush(cancelResKey, resJson);
		
		
	}
}

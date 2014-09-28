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
public class MagentoTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Autowired	private StringRedisTemplate reportStringRedisTemplate;
	
	
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private HashOperations<String, String, Object> hashOps;
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Test
	public void orderCreateTest(){
		
		
		String createOrderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<Order DocumentType=\"0001\" EnterpriseCode=\"SLV\" SellerOrganizationCode=\"ASPB\" PaymentStatus=\"AUTHORIZED\">"
			+"<OrderLines>"
				+"<OrderLine OrderedQty=\"2\">"
					+"<Item ItemID=\"ASPB_ITEM_0001\" UnitOfMeasure=\"EACH\" ItemShortDesc=\"Capri Blue Blue Signature Jar BOHO-LUXE\" ItemDesc=\"http://www.aspenbay.co.kr/capri-blue/capri-blue-blue-signature-jar-boho-luxe.html\"/>"
					+"<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"49000.00\"/>"
					+"<LineCharges>"
						+"<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10.00\" />"
						+"<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5.00\" />"
					+"</LineCharges>"
					+"<LineTaxes>"
						+"<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" TaxableFlag=\"Y\" Tax=\"0.00\" />"
					+"</LineTaxes>"
				+"</OrderLine>"
				+"<OrderLine OrderedQty=\"3\">"
					+"<Item ItemID=\"ASPB_ITEM_0002\" UnitOfMeasure=\"EACH\" ItemShortDesc=\"Capri Blue Diffuser BLUE JEAN\" ItemDesc=\"http://www.aspenbay.co.kr/capri-blue/capri-blue-diffuser-blue-jean.html\"/>"
					+"<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"59000.00\"/>"
					+"<LineCharges>"
						+"<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10.00\" />"
						+"<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5.00\" />"
					+"</LineCharges>"
					+"<LineTaxes>"
						+"<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" TaxableFlag=\"Y\" Tax=\"0.00\" />"
					+"</LineTaxes>"
				+"</OrderLine>"
					+"<OrderLine OrderedQty=\"5\">"
					+"<Item ItemID=\"ASPB_ITEM_0003\" UnitOfMeasure=\"EACH\" ItemShortDesc=\"Capri Blue Diffuser BLUE JEAN\" ItemDesc=\"http://www.aspenbay.co.kr/capri-blue/capri-blue-diffuser-blue-jean.html\"/>"
					+"<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"59000.00\"/>"
					+"<LineCharges>"
						+"<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10.00\" />"
						+"<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5.00\" />"
					+"</LineCharges>"
					+"<LineTaxes>"
						+"<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" TaxableFlag=\"Y\" Tax=\"0.00\" />"
					+"</LineTaxes>"
				 +"</OrderLine>"
			+"</OrderLines>"
			+"<PersonInfoShipTo FirstName=\"홍\" LastName=\"길동\" EMailID=\"yg.jang@isecommerce.co.kr\" DayPhone=\"02-1234-5678\" MobilePhone=\"010-1234-5678\" AddressLine1=\"강남구 삼성동 78번지\" AddressLine2=\"삼안빌딩 7층 SC-Team\" ZipCode=\"123-456\" City=\"서울\" Country=\"KR\" />"
			+"<PersonInfoBillTo  FirstName=\"이\" LastName=\"순신\" EMailID=\"yg.jang@isecommerce.co.kr\" DayPhone=\"02-1111-2222\" MobilePhone=\"010-1111-2222\" AddressLine1=\"강남구 삼성동 78번지\" AddressLine2=\"삼안빌딩 7층 SC-Team\" ZipCode=\"123-456\" City=\"서울\" Country=\"KR\" />"
			+"<PaymentMethods>"
				+"<PaymentMethod PaymentType=\"CREDIT_CARD\" CreditCardNo=\"0000000000000000\">"+"</PaymentMethod>"
			+"</PaymentMethods>"
		+"</Order>";
		
		
		String shipmentXML = "{\"orderId\":\"174004\",\"status\":\"3701\","
							+ "\"shipment\":[{\"carrierCode\":\"UPS\", \"carrierTitle\":\"UPS\",\"trackingNo\":\"11111\","
								+ "\"items\":[{\"itemId\":\"Brighton_Sunset Orange_sku\",\"qty\":\"1.00\"}]"
							+ "}]"
							+ "}";
		
		
		String keyName = "SLV:ASPB:order";
		
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
	
	@Test
	public void getListData() {
		
	    String key = "SLV:ASPB:order";
	    String key1 = "SLV:ASPB:order:update:S2M";
	    String key2 = "SLV:ASPB:order:update:M2S";
	    String key3 = "SLV:ASPB:order:update:S2C";
	    String key4 = "SLV:ASPB:order:update:C2S";
	    
	    String errorKey = "SLV:ASPB:order:error";
	    
	    
	    List<String> orderList = listOps.range(key, 0, -1);
	    System.out.println( "[SLV:ASPB:order]" +orderList.size());
	    
	    List<String> orderList1 = listOps.range(key1, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:S2M]" +orderList1.size());
	    
	    List<String> orderList2 = listOps.range(key2, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:M2S]" +orderList2.size());
	    
	    List<String> orderList3 = listOps.range(key3, 0, -1);
	    System.out.println( "[SLV:ASPB:order:update:S2C]" +orderList3.size());
	    
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
	
	@Test
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
	
	@Test
	public void etcTeset() {
	    System.out.println( CommonUtil.cuurentDateFromFormat("yyyyMMddHHssmm"));
	   
	}
	
	@Test
	public void deleteKeyDataVM() {
	    
	    String errorKey = "*:*:order:error";
	    
	    maStringRedisTemplate.delete(errorKey);
	}
}

/**
 * 
 */
package com.isec.sc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/application-config.xml","classpath:quartz-config.xml"})
public class RedisTest {

	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Resource(name="maStringRedisTemplate")
    private ValueOperations<String, String> valueOps;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	
	@Value("${redis.ma.dbindex}")
	private String redis_ma_index;
	
	
	@Value("${channel.ma.jns.order}")
	private String ch_ma_jns_order;
	

	
	@Test
    public void TestGetOrderListMagento() {
		
		List<String> orderList = listOps.range("DA:OUTRO:order", 0, -1);
		System.out.println("[orderlist - Outro]"+orderList.size());
		
		for(int i=0; i<orderList.size(); i++){
			System.out.println("["+i+"]"+orderList.get(i));
		}
		
		List<String> orderWList = listOps.range("ISEC:JNS:order:update:S2M", 0, -1);
		System.out.println("[orderlist - JNS]"+orderWList.size());
		
		for(int i=0; i<orderWList.size(); i++){
			System.out.println("["+i+"]"+orderWList.get(i));
		}
		
    }
	
	
	
	
	
	@Ignore
	public void TestXMLParsing(){
		
		try{
		
			FileInputStream fis = new FileInputStream("/Users/ykjang/Dev/sterling/getOrderDetails_sample.xml");
	        InputSource is = new InputSource(fis);
			
	        
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			
			XPath xp = XPathFactory.newInstance().newXPath();
			
			Node orderStatusNode = (Node)xp.evaluate("/Order/OrderStatuses/OrderStatus[1]", doc.getDocumentElement(), XPathConstants.NODE);
			
			
			
			
			
			System.out.println("OrderHeaderKey: "+orderStatusNode.getAttributes().getNamedItem("OrderHeaderKey").getTextContent());
			System.out.println("Status: "+orderStatusNode.getAttributes().getNamedItem("Status").getTextContent());
			System.out.println("StatusDescription: "+orderStatusNode.getAttributes().getNamedItem("StatusDescription").getTextContent());
			
			/*
			
			Element el = doc.getDocumentElement();
			
			Map<String,Object> sendMsgMap = new HashMap<String,Object>();
			
			// 2. 부분취소처리
			int cancelCount = 0;
			List<HashMap<String,String>> cancelList = new ArrayList<HashMap<String,String>>();
			for(int i=0; i<cancelCount; i++){
				HashMap<String, String> cancelItem = new HashMap<String, String>();
				cancelItem.put("itemId", "");
				cancelItem.put("qty", "");
				
				cancelList.add(cancelItem);
			}
			
			sendMsgMap.put("orderHeaderKey", el.getAttribute("OrderHeaderKey"));
			sendMsgMap.put("enterpriseCode", el.getAttribute("EnterpriseCode"));
			sendMsgMap.put("orderId", el.getAttribute("OrderNo"));
			sendMsgMap.put("status", el.getAttribute("MaxOrderStatus"));
			sendMsgMap.put("cancel", cancelList);
			
			
			// 3. Make sendData (JSON) 
			ObjectMapper mapper = new ObjectMapper();
			String orderUpdateJSON = mapper.writeValueAsString(sendMsgMap);
			System.out.println("[orderUpdateJSON]"+orderUpdateJSON);
        */
        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	@Ignore
	public void TestRedisOrderStatusPub(){
		
		try{

			
			// for(int i=0; i<100; i++){
				listOps.leftPush("DA:OUTRO:order:update:M2S", "{\"orderHeaderKey\":\"2014061716272448539\",\"confirmed\":[{\"qty\":\"1.00\",\"itemId\":\"2500535570001\"},{\"qty\":\"1.00\",\"itemId\":\"2500536180001\"}],\"docType\":\"0001\",\"status\":\"3201\",\"releaseKeys\":[\"2014062418504874241\"],\"orderId\":\"100000056\",\"entCode\":\"Matrix\"}");
				
				Map<String, String> sendMsgMap = new HashMap<String, String>();
				sendMsgMap.put("db", redis_ma_index);
				sendMsgMap.put("type", "orderUpdate");
				sendMsgMap.put("key", "DA:OUTRO:order:update:M2S");
				
				// Java Object(Map) to JSON
				ObjectMapper mapper = new ObjectMapper();
				String sendMsg = mapper.writeValueAsString(sendMsgMap);
				maStringRedisTemplate.convertAndSend("ma:orderUpdate", sendMsg);
			
			// }
//			Thread tr = new Thread();
//			tr.sleep(3000); 
			
			
//			wcslistOps.leftPush(redis_W_key_ordUpdate_M2S, "{\"orderHeaderKey\":\"2014061716272448539\",\"confirmed\":[{\"qty\":\"1.00\",\"itemId\":\"2500535570001\"},{\"qty\":\"1.00\",\"itemId\":\"2500536180001\"}],\"docType\":\"0001\",\"status\":\"3201\",\"releaseKeys\":[\"2014062418504874241\"],\"orderId\":\"100000056\",\"entCode\":\"Matrix\"}");
//		
//			Map<String, String> sendMsgMap2 = new HashMap<String, String>();
//			sendMsgMap2.put("db", redis_wcs_index);
//			sendMsgMap2.put("type", "orderUpdate");
//			sendMsgMap2.put("key", redis_W_key_ordUpdate_M2S);
//			
//			// Java Object(Map) to JSON
//			ObjectMapper mapper2 = new ObjectMapper();
//			String sendMsg2 = mapper2.writeValueAsString(sendMsgMap2);
//			maStringRedisTemplate.convertAndSend(ch_ma_jns_orderUpdate, sendMsg2);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
		
		@Test
		public void TestOrderCreatePubToWCS(){
			
			
			String createOrderXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<Order DocumentType=\"0001\" EnterpriseCode=\"ISEC\" SellerOrganizationCode=\"MI\" PaymentStatus=\"AUTHORIZED\" ShipNode=\"ISEC_WH1\""
					+ " ScacAndServiceKey=\"200409201112576488\">"
					
					+ "<OrderLines>"
						+ "<OrderLine OrderedQty=\"3\" ScacAndServiceKey=\"19991214183438453\">"
							+ "<Item ItemID=\"38855\" UnitOfMeasure=\"EACH\" ProductClass=\"GOOD\"/>"
							+ "<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"398\"/>"

							+ "<LineCharges>"
								+ "<LineCharge ChargeCategory=\"Shipping\" ChargeName=\"Shipping\" ChargePerLine=\"10\" ChargePerUnit=\"10\" />"
								+ "<LineCharge ChargeCategory=\"Discount\" ChargeName=\"Discount\" ChargePerLine=\"5\" ChargePerUnit=\"5\" />"
                       		+ "</LineCharges>"

							+ "<LineTaxes>"
								+ "<LineTax ChargeCategory=\"Price\" TaxName=\"Tax\" Tax=\"10\" TaxableFlag=\"Y\"/>"
			                + "</LineTaxes>"
						
						+ "</OrderLine>"
					+ "</OrderLines>"
					
					+ "<PersonInfoShipTo  FirstName=\"Dong-Ju\" LastName=\"Jang\" DayPhone=\"82212345678\" EMailID=\"dj.jang@isecommerce.co.kr\" "
					+ "MobilePhone=\"01042831028\" AddressLine1=\"seoul\" AddressLine2=\"seoul\" City=\"seoul\" Country=\"KR\" ZipCode=\"123456\" />"
					
					+ "<PersonInfoBillTo FirstName=\"Dong-Ju\" LastName=\"Jang\" DayPhone=\"82212345678\" EMailID=\"dj.jang@isecommerce.co.kr\" "
					+ "MobilePhone=\"01042831028\" AddressLine1=\"seoul\" AddressLine2=\"seoul\" City=\"seoul\" Country=\"KR\" ZipCode=\"123456\" />"
					
					+ "<PaymentMethods>"
						+ "<PaymentMethod PaymentType=\"CREDIT_CARD\">"
							+ "<PaymentDetails ChargeType=\"AUTHORIZATION\" ProcessedAmount=\"1224.00\" />"
						+ "</PaymentMethod>"
					+ "</PaymentMethods>"
					
					+ "</Order>";
			
			
			
			String shipmentXML = "{\"orderId\":\"174004\",\"status\":\"3701\","
								+ "\"shipment\":[{\"carrierCode\":\"UPS\", \"carrierTitle\":\"UPS\",\"trackingNo\":\"11111\","
									+ "\"items\":[{\"itemId\":\"Brighton_Sunset Orange_sku\",\"qty\":\"1.00\"}]"
								+ "}]"
								+ "}";
			
			
			
			
			
			try{

				
				System.out.println("[createOrderXML]"+createOrderXML);
				
//				for(int i=0; i<10; i++){
//					listOps.leftPush("ISEC:JNS:order", wcsXML);
//				}
				
//				listOps.leftPush("ISEC:JNS:order", createOrderXML);
//				listOps.leftPush("ISEC:JNS:order:update:S2M", shipm/entXML);
				
//				Map<String, String> sendMsgMap = new HashMap<String, String>();
//				sendMsgMap.put("db", redis_ma_index);
//				sendMsgMap.put("type", "order");
//				sendMsgMap.put("key", "ISEC:JNS:order");
				
				// Java Object(Map) to JSON
//				ObjectMapper mapper = new ObjectMapper();
//				String sendMsg = mapper.writeValueAsString(sendMsgMap);
//				maStringRedisTemplate.convertAndSend(ch_ma_jns_order, sendMsg);
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}

}

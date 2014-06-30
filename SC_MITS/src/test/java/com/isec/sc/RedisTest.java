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
@ContextConfiguration(locations={"classpath:spring/application-config.xml"})
public class RedisTest {

	
	@Autowired	private StringRedisTemplate mgtStringRedisTemplate;
	@Autowired	private StringRedisTemplate wcsStringRedisTemplate;
	
	
	@Resource(name="mgtStringRedisTemplate")
    private ValueOperations<String, String> valueOps;
	
	@Resource(name="mgtStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="wcsStringRedisTemplate")
	private ListOperations<String, String> wcslistOps;
	
	
	
	@Value("${redis.magento.dbindex}")
	private String redis_db_index;
	@Value("${redis.wcs.dbindex}")
	private String redis_wcs_index;
	
	@Value("${redis.channel.magento.product}")
	private String redis_M_ch;
	
	
	@Value("${redis.channel.wcs.order}")
	private String redis_W_ch_order;
	
	
	@Value("${redis.channel.magento.orderUpdate}")
	private String redis_M_ch_orderUpdate;
	@Value("${redis.channel.wcs.orderUpdate}")
	private String redis_W_ch_orderUpdate;
	
	@Value("${redis.magento.key.product}")
	private String redis_M_key_product;
	
	@Value("${redis.magento.key.order}")
	private String redis_M_key_order;
	
	@Value("${redis.wcs.key.order}")
	private String redis_W_key_order;
	
	@Value("${redis.magento.key.inventory}")
	private String redis_M_key_inv;
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_ordUpdate_S2M;
	
	@Value("${redis.magento.key.orderUpdate.M2S}")
	private String redis_M_key_ordUpdate_M2S;
	@Value("${redis.wcs.key.orderUpdate.M2S}")
	private String redis_W_key_ordUpdate_M2S;
	
	@Value("${magento.outro.ent.code}")
	private String magento_outro_ent_code;
	
	@Value("${redis.magento.key.order.err}")
	private String redis_M_key_order_err;
	
	@Test
    public void TestGetOrderListMagento() {
		
		List<String> orderList = listOps.range(redis_M_key_order, 0, -1);
		System.out.println("[orderlist]"+orderList.size());
		
		for(int i=0; i<orderList.size(); i++){
			System.out.println("["+i+"]"+orderList.get(i));
		}
		
		List<String> orderWList = wcslistOps.range(redis_W_key_order, 0, -1);
		System.out.println("[orderWlist]"+orderWList.size());
		
		for(int i=0; i<orderWList.size(); i++){
			System.out.println("["+i+"]"+orderWList.get(i));
		}
		
    }
	
	@Ignore
    public void TestGetOrderUpdateList() {
		
		List<String> orderUpdateList = listOps.range(redis_M_key_ordUpdate_S2M, 0, -1);
		System.out.println("[orderUpdate S2M list]"+orderUpdateList.size());
		
		for(int i=0; i<orderUpdateList.size(); i++){
			System.out.println("["+i+"]"+orderUpdateList.get(i));
		}
		
		
		List<String> orderUpdateList2 = listOps.range(redis_M_key_ordUpdate_M2S, 0, -1);
		System.out.println("[orderUpdate M2S list]"+orderUpdateList2.size());
		
		for(int i=0; i<orderUpdateList2.size(); i++){
			System.out.println("["+i+"]"+orderUpdateList2.get(i));
		}
    }
	
	
	@Ignore
    public void TestGetOrderErrList() {
		
		List<String> errOrderList = listOps.range(redis_M_key_order_err, 0, -1);
		System.out.println("[errOrderList]"+errOrderList.size());
		
		for(int i=0; i<errOrderList.size(); i++){
			System.out.println("["+i+"]"+errOrderList.get(i));
		}
		
    }
	
	@Ignore
    public void TestGetOrderUpdateM2SList() {
		
//		List<String> errProductList = listOps.range(redis_M_key_ordUpdate_M2S, 0, -1);
//		System.out.println("[orderUpdateM2S_Count]"+errProductList.size());
//		for(int i=0; i<errProductList.size(); i++){
//			System.out.println("["+i+"]"+errProductList.get(i));
//		}
		
		
		try{
			ObjectMapper mapper = new ObjectMapper();
			
			
			long dataCnt =  listOps.size(redis_M_key_ordUpdate_M2S);
			System.out.println("[list.size-read]"+dataCnt);
			
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String keyData = listOps.rightPop(redis_M_key_ordUpdate_M2S);
				HashMap<String, String> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,String>>(){});
				
				String status = dataMap.get("status");
				String orderId = dataMap.get("orderId");
				
				System.out.println("[orderId]"+orderId);
				System.out.println("[status]"+status);
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
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
	public void TestGetRedisData() {
		
//		listOps.rightPush("1", "a");
//		listOps.rightPush("1", "b");
//		listOps.rightPush("1", "c");
		mgtStringRedisTemplate.getConnectionFactory().getConnection().select(Integer.parseInt(redis_db_index));
		
		
		List<String> orderList = listOps.range("com:scteam:magento:order", 0, -1);
		System.out.println("[orderList.size]"+orderList.size());
		
		
		List<String> productList = listOps.range("com:scteam:magento:product", 0, -1);
		System.out.println("[productList.size]"+productList.size());
		
		
//		while(listOps.size("1")>0){
//			System.out.println(""+listOps.leftPop("1"));
//		}
		
	}
	
	
	@Ignore
	public void TestRedisItemPub(){
		
		try{

//			for(int i=0; i < 10; i++){
//				stringRedisTemplate.convertAndSend("com:scteam:magento:channel", "hello -" + i);
//				Thread.sleep(300);
//			}
			// String sendMsg = "{"db":"10","key":"com:scteam:magento:product"}";
			Map<String, String> sendMsgMap = new HashMap<String, String>();
			sendMsgMap.put("db", redis_db_index);
			sendMsgMap.put("type", "product");
			sendMsgMap.put("key", redis_M_key_product);
			
			String sendMsg = "";
			
			
			// Java Object(Map) to JSON
			ObjectMapper mapper = new ObjectMapper();
			sendMsg = mapper.writeValueAsString(sendMsgMap);
			
			System.out.println("[sendMsg]"+sendMsg);
			System.out.println("[redis_M_ch]"+redis_M_ch);
			
			mgtStringRedisTemplate.convertAndSend(redis_M_ch, sendMsg);
			
			
			// Set Database Index
			mgtStringRedisTemplate.getConnectionFactory().getConnection().select(Integer.parseInt(redis_db_index));
						
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00001\",\"qty\":\"100\"}");
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00002\",\"qty\":\"200\"}");
			listOps.leftPush(redis_M_key_product, "{\"item_id\":\"00003\",\"qty\":\"300\"}");
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Ignore
	public void TestRedisOrderStatusPub(){
		
		try{

			
			// for(int i=0; i<100; i++){
				listOps.leftPush(redis_W_key_ordUpdate_M2S, "{\"orderHeaderKey\":\"2014061716272448539\",\"confirmed\":[{\"qty\":\"1.00\",\"itemId\":\"2500535570001\"},{\"qty\":\"1.00\",\"itemId\":\"2500536180001\"}],\"docType\":\"0001\",\"status\":\"3201\",\"releaseKeys\":[\"2014062418504874241\"],\"orderId\":\"100000056\",\"entCode\":\"Matrix\"}");
				
				Map<String, String> sendMsgMap = new HashMap<String, String>();
				sendMsgMap.put("db", redis_db_index);
				sendMsgMap.put("type", "orderUpdate");
				sendMsgMap.put("key", redis_W_key_ordUpdate_M2S);
				
				// Java Object(Map) to JSON
				ObjectMapper mapper = new ObjectMapper();
				String sendMsg = mapper.writeValueAsString(sendMsgMap);
				mgtStringRedisTemplate.convertAndSend(redis_M_ch_orderUpdate, sendMsg);
			
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
//			mgtStringRedisTemplate.convertAndSend(redis_W_ch_orderUpdate, sendMsg2);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
		
		@Test
		public void TestOrderCreatePubToWCS(){
			
			
			String wcsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<Order DocumentType=\"0001\" EnterpriseCode=\"ISEC\" SellerOrganizationCode=\"JNS\" PaymentStatus=\"AUTHORIZED\" ShipNode=\"ISEC_WH1\">"
					
					+ "<OrderLines>"
						+ "<OrderLine OrderedQty=\"1\">"
							+ "<Item ItemID=\"38855\" UnitOfMeasure=\"EACH\" ProductClass=\"GOOD\"/>"
							+ "<LinePriceInfo IsLinePriceForInformationOnly=\"N\" IsPriceLocked=\"Y\" UnitPrice=\"398\"/>"
						+ "</OrderLine>"
					+ "</OrderLines>"
					
					+ "<PersonInfoShipTo  FirstName=\"Dong-Ju\" LastName=\"Jang\" DayPhone=\"82212345678\" EMailID=\"dj.jang@isecommerce.co.kr\" "
					+ "MobilePhone=\"01042831028\" AddressLine1=\"seoul\" AddressLine2=\"seoul\" City=\"seoul\" Country=\"KR\" ZipCode=\"123456\" />"
					
					+ "<PersonInfoBillTo FirstName=\"Dong-Ju\" LastName=\"Jang\" DayPhone=\"82212345678\" EMailID=\"dj.jang@isecommerce.co.kr\" "
					+ "MobilePhone=\"01042831028\" AddressLine1=\"seoul\" AddressLine2=\"seoul\" City=\"seoul\" Country=\"KR\" ZipCode=\"123456\" />"
					
					/*+ "<PaymentMethods>"
						+ "<PaymentMethod PaymentType=\"CREDIT_CARD\"/>"
					+ "</PaymentMethods>"*/
					
					+ "</Order>";
			
			try{

				
				// for(int i=0; i<100; i++){
				wcslistOps.leftPush(redis_W_key_order, wcsXML);
				
				Map<String, String> sendMsgMap = new HashMap<String, String>();
				sendMsgMap.put("db", redis_wcs_index);
				sendMsgMap.put("type", "order");
				sendMsgMap.put("key", redis_W_key_order);
				
				// Java Object(Map) to JSON
				ObjectMapper mapper = new ObjectMapper();
				String sendMsg = mapper.writeValueAsString(sendMsgMap);
				mgtStringRedisTemplate.convertAndSend(redis_W_ch_order, sendMsg);
				
				// }
//				Thread tr = new Thread();
//				tr.sleep(3000);
				
//				wcslistOps.leftPush(redis_W_key_ordUpdate_M2S, "{\"orderHeaderKey\":\"2014061716272448539\",\"confirmed\":[{\"qty\":\"1.00\",\"itemId\":\"2500535570001\"},{\"qty\":\"1.00\",\"itemId\":\"2500536180001\"}],\"docType\":\"0001\",\"status\":\"3201\",\"releaseKeys\":[\"2014062418504874241\"],\"orderId\":\"100000056\",\"entCode\":\"Matrix\"}");
//			
//				Map<String, String> sendMsgMap2 = new HashMap<String, String>();
//				sendMsgMap2.put("db", redis_wcs_index);
//				sendMsgMap2.put("type", "orderUpdate");
//				sendMsgMap2.put("key", redis_W_key_ordUpdate_M2S);
//				
//				// Java Object(Map) to JSON
//				ObjectMapper mapper2 = new ObjectMapper();
//				String sendMsg2 = mapper2.writeValueAsString(sendMsgMap2);
//				mgtStringRedisTemplate.convertAndSend(redis_W_ch_orderUpdate, sendMsg2);
				
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}

}

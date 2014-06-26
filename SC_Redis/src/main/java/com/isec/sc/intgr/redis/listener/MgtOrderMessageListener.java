package com.isec.sc.intgr.redis.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.http.ScInventoryController;


/**
 * 
 * @author ykjang
 *
 */
public class MgtOrderMessageListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(MgtOrderMessageListener.class);
	
	
	@Autowired	private StringRedisTemplate mgtStringRedisTemplate;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Resource(name="mgtStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Value("${redis.magento.key.order.err}")
	private String redis_M_key_order_err;
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_orderUpdate_S2M;
	
	@Override
	public void onMessage(Message message, byte[] chaanel) {
		
		// TODO Auto-generated method stub
		System.out.println("Magento CreateOrder Message: " + message.toString() + " from Channel [" + new String(chaanel) +"]");
		
		
		Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
	 
		String dbIndex = "";
		String key = "";
		String type="";
		
		try {
	 
			// 1. Send Message 추출
			
			// convert JSON string to Map
			// {"db":"10","key":"com:scteam:magento:product"}
			sendMsgMap = mapper.readValue(message.toString(), new TypeReference<HashMap<String,String>>(){});
	 
			dbIndex = sendMsgMap.get("db");
			type = sendMsgMap.get("type");
			key = sendMsgMap.get("key");
			
			System.out.println("[db]"+dbIndex);
			System.out.println("[type]"+type);
			System.out.println("[key]"+key); 

			
			// Set Database Index
//			stringRedisTemplate.getConnectionFactory().getConnection().select(10);
//			ListOperations<String, String> listOps = stringRedisTemplate.opsForList();
			
			// 2. Get Data Count by Key
//			List<String> list = listOps.range(key, 0, -1);
//			System.out.println("[list.size-read]"+list.size());
			
			long dataCnt =  listOps.size(key);
			System.out.println("[list.size-read]"+dataCnt);
			
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String xmlData = listOps.rightPop(key);
				
				// SC API 호출
				HashMap<String, Object> result = sterlingApiDelegate.createOrder(xmlData);
				String status = (String)result.get("status");
				
				
				if("1100".equals(status)){
					
					String orderSuccJSON = mapper.writeValueAsString(result);
					logger.debug("[orderSuccJSON]"+orderSuccJSON);
					listOps.leftPush(redis_M_key_orderUpdate_S2M, orderSuccJSON);
				
				// 에러발생시 별도의 에러키값으로 저장
				}else if("0000".equals(status)){
					
					sendMsgMap.put("data", xmlData);
					sendMsgMap.put("occure_date", cuurentDate());
					
					
					// Java Object(Map) to JSON	
					String orderErrJSON = mapper.writeValueAsString(sendMsgMap);
					logger.debug("Create Order Error occured");
					logger.debug("[orderErrJSON]"+orderErrJSON);
					
					
					listOps.leftPush(redis_M_key_order_err, orderErrJSON);
				}
			}
			
			

			
		} catch (Exception e) {
			
			// 예외처리
			e.printStackTrace();
		}
		
	}
	
	
	private String cuurentDate(){
		
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		
		return mTime;
	}
	
}

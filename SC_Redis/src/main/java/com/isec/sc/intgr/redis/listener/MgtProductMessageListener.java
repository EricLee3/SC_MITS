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


/**
 * 
 * @author ykjang
 *
 */
public class MgtProductMessageListener implements MessageListener {
	
	
	@Autowired	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Resource(name="stringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Value("${redis.magento.key.product.err}")
	private String redis_M_key_product_err;
	
	@Override
	public void onMessage(Message message, byte[] chaanel) {
		
		// TODO Auto-generated method stub
		System.out.println("Message Received at Listener: " + message.toString() + " from Channel [" + new String(chaanel) +"]");
		
		
		Map<String,String> map = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
	 
		String dbIndex = "";
		String key = "";
		String type="";
		
		try {
	 
			// 1. Send Message 추출
			
			// convert JSON string to Map
			// {"db":"10","key":"com:scteam:magento:product"}
			map = mapper.readValue(message.toString(), new TypeReference<HashMap<String,String>>(){});
	 
			dbIndex = map.get("db");
			type = map.get("type");
			key = map.get("key");
			
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
				String result = sterlingApiDelegate.manageItem(xmlData);
				
				// 에러발생시 다른 key로 해당xml저장
				if("0".equals(result)){
					
					
					System.out.println("##### Error Occured!!!");
					
					Map<String, String> sendMsgMap = new HashMap<String, String>();
					sendMsgMap.put("type", "product");
					sendMsgMap.put("key", "key");
					sendMsgMap.put("data", xmlData);
					sendMsgMap.put("date", cuurentDate());
					
					
					// Java Object(Map) to JSON	
					String sendMsg = "";
					ObjectMapper resultMapper = new ObjectMapper();
					sendMsg = resultMapper.writeValueAsString(sendMsgMap);
					
					listOps.leftPush(redis_M_key_product_err, sendMsg);
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

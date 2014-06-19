package com.isec.sc.intgr.redis.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
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
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.http.ScInventoryController;


/**
 * 
 * @author ykjang
 *
 */
public class MgtOrderStsUpdateMessageListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(MgtOrderStsUpdateMessageListener.class);
	
	
	@Autowired	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Resource(name="stringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_orderUpdate_S2M;
	
	@Value("${redis.magento.key.orderUpdate.M2S}")
	private String redis_M_key_orderUpdate_M2S;
	
	
	@Value("${redis.magento.key.order.err}")
	private String redis_M_key_order_err;
	
	
	@Value("${magento.outro.ent.code}")
	private String magento_outro_ent_code;
	
	
	private static final String releaseOrder_template = "/com/isec/sc/intgr/api/xml/releaseOrder_input.xml";
	
	@Override
	public void onMessage(Message message, byte[] chaanel) {
		
		
		// TODO Auto-generated method stub
		logger.debug("Magento OrderUpdate Listener: " + message.toString() + " from Channel [" + new String(chaanel) +"]");
		
		
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
			logger.debug("[list.size-read]"+dataCnt);
			
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String keyData = listOps.rightPop(key);
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				String status = (String)dataMap.get("status");
				
				
				// 1501: Magento의 OrderStatus가 Processing인 단계 (정상정으로 인보이스가 생성된 상태)
				// SC의 Order status를 released로 변경한다.
				if("1501".equals(status)){
				
				
					String template = FileContentReader.readContent(getClass().getResourceAsStream(releaseOrder_template));
	
					MessageFormat msg = new MessageFormat(template);
					String xmlData = msg.format(new String[] {magento_outro_ent_code, (String)dataMap.get("orderId")} );
					logger.debug("[xmlData]"+xmlData);
					
					// SC API 호출
					String result = sterlingApiDelegate.releaseOrder(xmlData);
					logger.debug("[result]"+result);
					
					// 에러발생시 다른 key로 해당xml저장
					if("0".equals(result)){
						
						
						logger.debug("##### Error Occured!!!");
						
						Map<String, String> errMsgMap = new HashMap<String, String>();
						errMsgMap.put("type", "orderUpdate");
						errMsgMap.put("key", redis_M_key_order_err);
						errMsgMap.put("status", status);
						errMsgMap.put("data", xmlData);
						errMsgMap.put("date", cuurentDate());
						
						
						// Java Object(Map) to JSON	
						ObjectMapper resultMapper = new ObjectMapper();
						String errMsg = resultMapper.writeValueAsString(errMsgMap);
						
						listOps.leftPush(redis_M_key_order_err, errMsg);
					}
				
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

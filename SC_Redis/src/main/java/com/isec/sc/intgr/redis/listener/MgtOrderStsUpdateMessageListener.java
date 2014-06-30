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
	
	
	@Autowired	private StringRedisTemplate mgtStringRedisTemplate;
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Resource(name="mgtStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="mgtStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	
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
			
			logger.debug("[db]"+dbIndex);
			logger.debug("[type]"+type);
			logger.debug("[key]"+key); 

			
			// Set Database Index
//			stringRedisTemplate.getConnectionFactory().getConnection().select(10);
//			ListOperations<String, String> listOps = stringRedisTemplate.opsForList();
			
			// 2. Get Data Count by Key
//			List<String> list = listOps.range(key, 0, -1);
//			System.out.println("[list.size-read]"+list.size());
			
			logger.debug("[list.size-read]"+listOps.size(key));
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<listOps.size(key); i++){
				
				String keyData = listOps.rightPop(key);
				logger.debug("[Redis OrderStatus Update Data]"+keyData);
				
				
				// JSON --> HashMap 변환
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				String status = (String)dataMap.get("status");
				logger.debug("[status]"+status);
				
				// 3201: Magento의 OrderStatus가 Order Captured단계 (정상정으로 인보이스가 생성된 상태)
				// Create Shipment 실행
				if("3201".equals(status)){
				
					// createShipment API 호출
					
					String docType = (String)dataMap.get("docType");
					String entCode = (String)dataMap.get("entCode");
					String orderId = (String)dataMap.get("orderId");
					logger.debug("[docType]"+docType);
					logger.debug("[entCode]"+entCode);
					logger.debug("[orderId]"+orderId);
					
					ArrayList<String> releaseKeys = (ArrayList<String>)dataMap.get("releaseKeys");
					
					for(int j=0; j<releaseKeys.size(); j++){
						
						logger.debug("[releaseKeys]"+releaseKeys.get(i));
						
						// Sterling API Call
						HashMap<String, String> resultMap = sterlingApiDelegate.createShipment(releaseKeys.get(i), docType, entCode, orderId);
						
						String apiStatus = resultMap.get("status");
						logger.debug("[apiStatus]"+apiStatus);
						
						// 에러발생시 별도의 에러키값으로 저장
						if("0000".equals(apiStatus)){
							
							/*
							 * TODO: Release 번호에 따라 Shipment가 별도로 생성되는 경우는 거의 없음
							 *       OrderLine별 ShipNode가 다르거나 DeliveryDate가 다른 경우에만 별도로 Shipment가 생성됨
							 *       이런 경우를 제외하면 CreateShipment는 모든 ReleaseKey에 대해 한번만 수행됨.
							 *       따라서 루프처리시 발생하는 에러(사실 에러가 아님)와 실제 에러에 대한 구분이 필요. 
							 */
							
							
						}else{
							
							// confirm shipment 처리 후 Ma로 shipment 정보를 전송하기 위해
							// Ma의 Key값이 되는 오더번호를 별도로 저장해 놓는다.
							String shipmentNo = resultMap.get("shipmentNo");
							logger.debug("[shipmentNo]"+shipmentNo);
							
							valueOps.set(shipmentNo, orderId);
						}
						
					} // End loop ReleaseKey
					
				} // End if OrderStatus
				
			} // End loop Redis Data
			
			
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

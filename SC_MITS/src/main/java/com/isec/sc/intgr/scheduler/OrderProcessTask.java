/*
 *  
 *  * Revision History
 *  * Author              Date                  Description
 *  * ------------------   --------------       ------------------
 *  *  beyondj2ee          2014.01.02              
 *  
 */

package com.isec.sc.intgr.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;



public class OrderProcessTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);


	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;

	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	
    public void createOrder(String redisKey, String redisPushKey, String redisErrKey){
        
    	logger.debug("##### createOrder["+redisKey+"] Task Excuted!!!");
    	logger.debug("##### redisPushKey ["+redisPushKey+"]");
    	logger.debug("##### redisErrKey["+redisErrKey+"]");
    	
    	Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
    	
		
		
    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String xmlData = listOps.rightPop(redisKey);
				
				// SC API 호출
				HashMap<String, Object> result = sterlingApiDelegate.createOrder(xmlData);
				String status = (String)result.get("status");
				
				
				// Create 성공
				if("1100".equals(status)){
					
					String orderSuccJSON = mapper.writeValueAsString(result);
					logger.debug("[Create Order Successful]");
					logger.debug("[orderSucc JSON]"+orderSuccJSON);
					
					// 결과데이타 저장
					listOps.leftPush(redisPushKey, orderSuccJSON);
				
				// Create 실패
				}else if("0000".equals(status)){
					
					sendMsgMap.put("data", xmlData);
					sendMsgMap.put("occure_date", cuurentDate());
					
					
					// Java Object(Map) to JSON	
					String orderErrJSON = mapper.writeValueAsString(sendMsgMap);
					logger.debug("Create Order Error occured");
					logger.debug("[orderErr JSON]"+orderErrJSON);
					
					// 실패데이타 저장
					listOps.leftPush(redisErrKey, orderErrJSON);
				}
			}
			
		}catch(Exception e){
			logger.debug("##### Create Order Task Exeption Occured");
			e.printStackTrace();
		}
		
    }
    
    
	public void updateOrderStatus(String redisKey, String redisPushKey, String redisErrKey){
        
	  	logger.debug("##### createOrder["+redisKey+"] Task Excuted!!!");
	  	logger.debug("##### redisPushKey ["+redisPushKey+"]");
	  	logger.debug("##### redisErrKey["+redisErrKey+"]");
	  	
	  	Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
	  	
			
	  	long dataCnt =  listOps.size(redisKey);
	  	logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			
			logger.debug("[list.size-read]"+listOps.size(redisKey));
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<listOps.size(redisKey); i++){
				
				String keyData = listOps.rightPop(redisKey);
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
			
			
		}catch(Exception e){
			logger.debug("##### Update Order Status Task Exeption Occured");
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

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
	
	
	
	/**
	 * 오더생성 From Redis
	 * 
	 * @param redisKey	MA에 전송한 최초 오더생성 정보 ( SC의 CreateOrder API의 Input XML)
	 * @param redisPushKey MITS에서 오더생성 후 MA로 오더생성 성공여부를 전송하는 Key -> Ma는 이 정보를 확인해서 Placed로 변경한다.
	 * @param redisErrKey MITS에서 오더생성시 에러가 발생할 경우 에러정보를 저장할 Redis Key
	 */
    public void createOrder(String redisKey, String redisPushKey, String redisErrKey){
        
    	
    	logger.debug("##### [createOrder] Job Task Started!!!");
    	logger.debug("     ----- Read Key ["+redisKey+"]");
    	logger.debug("     ----- Push Key ["+redisPushKey+"]");
    	logger.debug("     ----- Error Key ["+redisErrKey+"]");
    	
    	Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
    	
		
    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String orderInputXml = listOps.rightPop(redisKey);
				
				// SC API 호출
				HashMap<String, Object> result = sterlingApiDelegate.createOrder(orderInputXml);
				String status = (String)result.get("status");
				
				
				// Create 성공
				// TODO: ScOrderStatusHandler에서 처리하도록 수정
				if("1100".equals(status)){
					
					// TODO: sellerCode 저장추가
					String orderSuccJSON = mapper.writeValueAsString(result);
					logger.debug("[Create Order Successful]");
					logger.debug("[orderSucc JSON]"+orderSuccJSON);
					
					// 결과데이타 저장
					listOps.leftPush(redisPushKey, orderSuccJSON);
					
				
				// Create 실패
				}else if("0000".equals(status)){
					
					sendMsgMap.put("data", orderInputXml);
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
    
    
    /**
     * 
     * @param redisKey
     * @param redisPushKey
     * @param redisErrKey
     */
	public void updateOrderStatus(String redisKey, String redisPushKey, String redisErrKey){
        
		logger.debug("##### [updateOrderStatus] Job Task Started!!!");
    	logger.debug("     ----- Read Key ["+redisKey+"]");
    	logger.debug("     ----- Push Key ["+redisPushKey+"]");
    	logger.debug("     ----- Error Key ["+redisErrKey+"]");
	  	
	  	Map<String,String> sendMsgMap = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
	  	
			
	  	long dataCnt =  listOps.size(redisKey);
	  	logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<listOps.size(redisKey); i++){
				
				String keyData = listOps.rightPop(redisKey);
				logger.debug("[Redis OrderStatus Update Data]"+keyData);
				
				// JSON --> HashMap 변환
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				String status = (String)dataMap.get("status");
				logger.debug("[status]"+status);
				
				/*
				 * 3201: Magento - OrderStatus가 Order Captured단계 (정상정으로 인보이스가 생성된 상태)
				 *       WCS     - OrderStatus가 Inventory Fulfilled단계
				 *       
				 * Create Shipment 실행
				 */
				if("3201".equals(status)){
				
					// createShipment API 호출
					
					String docType = (String)dataMap.get("docType");
					String entCode = (String)dataMap.get("entCode");
					String orderId = (String)dataMap.get("orderId");
					logger.debug("[docType]"+docType);
					logger.debug("[entCode]"+entCode);
					logger.debug("[orderId]"+orderId);
					
					// Order Release Key 조회
					ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(docType, entCode, orderId);
					
					for(int j=0; j<releaseKeys.size(); j++){
						
						logger.debug("[releaseKeys]"+releaseKeys.get(j));
						
						/**
						 * TODO: 현재는 MITS가 Shipment처리를 담당하나 향후에는 CUBE가 출고지시를 할수 있도록 Release된 주문정보를
						 * Cube로 전송하고 SC(MITS)는 CUBE의 출고확정정보를 받아 Shipped로 주문의 상태를 변경하는 방식으로 변경필요 
						 */
						HashMap<String, String> resultMap = sterlingApiDelegate.createShipment(releaseKeys.get(j), docType, entCode, orderId);
						
						String apiStatus = resultMap.get("status");
						logger.debug("[apiStatus]"+apiStatus);
						
						/*
						 * TODO: Release 번호에 따라 Shipment가 별도로 생성되는 경우는 거의 없음
						 *       OrderLine별 ShipNode가 다르거나 DeliveryDate가 다른 경우에만 별도로 Shipment가 생성됨
						 *       이런 경우를 제외하면 CreateShipment는 모든 ReleaseKey에 대해 한번만 수행됨.
						 *       따라서 루프처리시 발생하는 에러(사실 에러가 아님)와 실제 에러에 대한 구분이 필요. 
						 */
						if("0000".equals(apiStatus)){
							// TODO: 에러처리
						}else{
							
							/**
							 * confirm shipment 처리 후 Ma로 shipment 정보를 전송하기 위해
							 * Ma의 Key값이 되는 오더번호를 별도로 저장해 놓는다. 
							 */
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

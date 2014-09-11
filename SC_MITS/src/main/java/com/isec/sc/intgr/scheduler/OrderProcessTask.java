/*
 *  
 *  * Revision History
 *  * Author              Date                  Description
 *  * ------------------   --------------       ------------------
 *  *  beyondj2ee          2014.01.02              
 *  
 */

package com.isec.sc.intgr.scheduler;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.w3c.dom.Document;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;



public class OrderProcessTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);


	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;

	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	
	@Value("${sc.api.scheduleOrder.template}")
	private String SCHEDULE_ORDER_TEMPLATE;
	
	
	/**
	 * Create Order From MA(Magento, WCS)
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
	    	
	    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				
				// Get Input XML from Redis
				String orderInputXml = listOps.rightPop(redisKey);
				logger.debug("[Create Order input XML]"+ orderInputXml);
				
				// SC API 호출
				String outputXML = sterlingApiDelegate.createOrder(orderInputXml);
				
				// 에러처리
				Document doc = sterlingApiDelegate.processCreateOrderError(orderInputXml, outputXML, redisErrKey);
				
				// 결과처리 (정상일 경우)
				if( doc != null){
					
					logger.debug("[Create Order Successful]");
					
					
					String orderHeaderKey = doc.getDocumentElement().getAttribute("OrderHeaderKey");
					String entCode = doc.getDocumentElement().getAttribute("EnterpriseCode");
					String orderId = doc.getDocumentElement().getAttribute("OrderNo");
					String docType =  doc.getDocumentElement().getAttribute("DocumentType");

					HashMap<String, String> resultMap = new HashMap<String, String>();
					resultMap.put("status", "1100");
					resultMap.put("orderHeaderKey", orderHeaderKey);
					resultMap.put("entCode", entCode);
					resultMap.put("orderId", orderId);
					resultMap.put("docType", docType);
					
					String sellerCode = redisKey.split(":")[1];	// SLV:ASPB:order
					resultMap.put("sellerCode", sellerCode);
					
					ObjectMapper mapper = new ObjectMapper();
					String orderSuccJSON = mapper.writeValueAsString(resultMap);
					logger.debug("[Update OrderStatus - Redis Data]"+orderSuccJSON);
					
					// Put OrderStaus to Redis
					listOps.leftPush(redisPushKey, orderSuccJSON);
					
					
					
					
					// TODO: Schedule & Release 처리, CreateOrder API 후처리 프로세스에서 처리할지 여부 결정
					logger.debug("[Order Schedule & Release Started]");
					String scheduleNrelease = "Y";	// Schedule과 Release를 동시에 처리함.
					String scheduleOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(SCHEDULE_ORDER_TEMPLATE));
					
					MessageFormat msg = new MessageFormat(scheduleOrderXML);
					String inputXML = msg.format(new String[] {docType, entCode, orderId, scheduleNrelease} );
					logger.debug("##### [inputXML]"+inputXML); 
					
					try
					{
					
						// API Call
						String outputMsg = sterlingApiDelegate.comApiCall("scheduleOrder", inputXML);
						doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
						
						// TODD: Error 메세지 정규화 작업필요
						if("Errors".equals(doc.getFirstChild().getNodeName())){
							// TODO: Release API호출 자동화 예외처리
						}
					
					}
					catch(Exception e)
					{
						// TODO: Release API호출 자동화 예외처리
						e.printStackTrace();
						
					}
					logger.debug("[Order Schedule & Release End]");
					
				}
			}
			
		}catch(Exception e){
			logger.debug("##### Create Order Task Exeption Occured");
			e.printStackTrace();
			
		}
    }
    
	
	/**
	 * 
	 * 주문확정정보 응답 수신프로세스 From Redis (From Cube)
	 * 
	 * 주문확정정보 Cube전송 후
	 * Cube의 처리결과를 받아 정상일 경우 Create Shipment API(출고생성)를 호출하는 프로세스
	 * 부분취소가 일어난 경우 Release된 오더라인만 출고생성됨.
	 * 
	 * 
	 * TODO: Cube 응답정보에서 출고번호 전송되는지 여부 확인 - 가능할 경우 Redis항목추가, 출고번호로 Create Shipment 호출가능여부 확인 
	 * TODO: MA 전송처리는 Handler클래스(SC 트랜잭션 후처리 프로세스)에서 처리 고려
	 * 
	 * Redis Data Format
	 *  {
	 *   "orderHeaderKey":"20140905192100126754",
	 *   "docType":"0001",
	 *   "status":"3201",
	 *   "sellerCode":"ASPB",
	 *   "trDate":"20140905193934",
	 *   "orderId":"Y100000350",
	 *   "entCode":"SLV",
	 *   "confirmed":[{"orderReleaseKey":"20140905192954126894","primeLineNo":"1","qty":"2.00","itemId":"ASPB_ITEM_0001"}]
	 *  }
	 * 
	 * 	- Redis Read Key: SLV:ASPB:order:update:C2S
	 *  - 주기: 5분 마다
	 * 
	 * 
	 *  - 정상응답인 경우 
	 *   - Cube로 부터 전달받은 출고번호로 SC의 CreateShipment 호출 -> Include In Shipment로 변경 (3350) [출고준비중]
	 *   - Ma로 Release 정보 전송(Create Shipment 후처리 프로세스에서 처리)  
	 *   
	 *  - 에러인 경우 (주문확정정보가 정상적으로 처리되지 못한 경우)
	 *   - 에러메세지 확인후 조치 -> 기존데이타 Redisdㅔ서 삭제 -> 주문확정정보 재전송 
	 *    
	 *  
	 * @param redisKey
	 * @param redisPushKey
	 * @param redisErrKey
	 */
	public void processReleaseReturn(String redisKey, String redisPushKey, String redisErrKey){
	
		
		logger.debug("##### [processReleaseReturn] Job Task Started!!!");
	    	logger.debug("     ----- Read Key ["+redisKey+"]");
	    	logger.debug("     ----- Push Key ["+redisPushKey+"]");
	    	logger.debug("     ----- Error Key ["+redisErrKey+"]");
	    	
	    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try{
			// 3. Call Sterling API by Type & Key
			for(int i=0; i<dataCnt; i++){
				
				String keyData = listOps.rightPop(redisKey);
				logger.debug("[Redis Key - Cube->SC]"+keyData);
				
				// JSON --> HashMap 변환
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				String status = (String)dataMap.get("status");
				logger.debug("[status]"+status);
				
				
				/*
				 * Cube로 부터 주문확정정보 전송에 대한 정상응답인 경우 - 정상 출고예정정보 등록
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
					
					
					// "confirmed":[{"orderReleaseKey":"20140905192954126894","primeLineNo":"1","qty":"2.00","itemId":"ASPB_ITEM_0001"}]
					
					ArrayList<HashMap<String, String>> releaseNoList = (ArrayList<HashMap<String, String>>)dataMap.get("confirmed");
					for(int j=0; j<releaseNoList.size(); j++){
						
						logger.debug("[releaseNos]"+releaseNoList.get(j).get("primeLineNo"));
						HashMap<String, String> resultMap = sterlingApiDelegate.createShipment("", releaseNoList.get(j).get("primeLineNo"), docType, entCode, orderId);
						
					}
				
				}
				
		
			} // End for
		}catch(Exception e){
			logger.debug("Create Shipment Error occured!!!");
			e.printStackTrace();
		}
		
		
		
		logger.debug("##### [processReleaseReturn] Job Task End!!!");
	}
	
	/**
	 * 출고확정정보 응답 수신 (From Cube)
	 * 
	 *  - Cube에서 출고확정된 주문정보를 전달받아 SC의 ConfirmShiment를 호출 
	 *  - SC의 주문상태 Shipped로 변경 --> MA로 출고정보 전송 --> 배송중
	 *  
	 *  - TODO: MA로의 출고정보 전송처리는 Handler클래스에서 처리 고려
	 *  
	 *  - Redis Read Key: SLV:ASPB:order:update:C2S
	 *  - Redis Push Key: SLV:ASPB:order:update:S2M
	 *  
	 *  - 주기: 5분 마다
	 *  
	 * @param redisKey
	 * @param redisPushKey
	 * @param redisErrKey
	 */
	public void processConfirmShipment(String redisKey, String redisPushKey, String redisErrKey){
	
	}

	
	/**
	 * 주문취소 처리결과 수신 (From Cube)
	 *  - 
	 *  
	 * @param redisKey
	 * @param redisPushKey
	 * @param redisErrKey
	 */
	public void processCancelReturn(String redisKey, String redisPushKey, String redisErrKey){
	
	}
	
	/**
	 * 품절취소 수신 (From Cube)
	 *  - 
	 *  
	 * @param redisKey
	 * @param redisPushKey
	 * @param redisErrKey
	 */
	public void processSolOutCancel(String redisKey, String redisPushKey, String redisErrKey){
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
     * Create Shipment처리 ( OUTRO Test 전용)
     *  - MA에서 Order Captured 된 상태의 정보를 받아 Create Shipment를 실행하는 메서드
     *  - 실 운영에서는 사용하지 않.
     * 
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
				 * 3201: Outro - OrderStatus가 Order Captured단계 (정상정으로 인보이스가 생성된 상태)
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
					ArrayList<String> releaseNos = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(docType, entCode, orderId);
					
					for(int j=0; j<releaseNos.size(); j++){
						
						logger.debug("[releaseNos]"+releaseNos.get(j));
						
						/**
						 * TODO: 현재는 MITS가 Shipment처리를 담당하나 향후에는 CUBE가 출고지시를 할수 있도록 Release된 주문정보를
						 * Cube로 전송하고 SC(MITS)는 CUBE의 출고확정정보를 받아 Shipped로 주문의 상태를 변경하는 방식으로 변경필요 
						 */
						HashMap<String, String> resultMap = sterlingApiDelegate.createShipment("", releaseNos.get(j), docType, entCode, orderId);
						
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
	
}

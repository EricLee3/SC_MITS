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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;



public class OrderProcessTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);

	
	private static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";
	private static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;

	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	
	@Value("${sc.api.scheduleOrder.template}")
	private String SCHEDULE_ORDER_TEMPLATE;
	
	@Value("${sc.api.getShipmentListForOrder.template}")
	private String GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE;
	
	@Value("${sc.api.confirmShipment.template}")
	private String CONFIRM_SHIPMENT_TEMPLATE;
	
	
	/**
	 * Create Order From MA(Magento, WCS)
	 * 
	 * MA로 부터 전송된 주문정보로 SC의 주문생성 API(createOrder)를 수행하고,
	 * 정상일 경우 SC의 schedule&releaseOrder를 바로 실행시킨다.
	 * 
	 * Order Release이후의 처리는
	 * ScOrderStatusHandler의 updateOrderStatus가 처리한다.
	 * 
	 * 
	 * @param redisKey	MA에 전송한 최초 오더생성 정보 ( SC의 CreateOrder API의 Input XML)
	 * @param redisPushKey MITS에서 오더생성 후 MA로 오더생성 성공여부를 전송하는 Key -> Ma는 이 정보를 확인해서 Placed로 변경한다.
	 * @param redisErrKey MITS에서 오더생성시 에러가 발생할 경우 에러정보를 저장할 Redis Key
	 */
    public void createOrder(String redisKey, String redisPushKey, String redisErrKey){
        
    	
	    	logger.debug("##### [createOrder] Job Task Started!!!");
	    	
	    	long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
		
		try{
			for(int i=0; i<dataCnt; i++){
				
				// Get Input XML from Redis
				String orderInputXml = listOps.rightPop(redisKey);
				logger.debug("[Create Order input XML]"+ orderInputXml);
				
				// SC API 호출
				String outputXML = sterlingApiDelegate.createOrder(orderInputXml);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
				
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					// TODO: createOrder API호출 자동화 예외처리
				}
				
			}
			
		}catch(Exception e){
			logger.debug("##### Create Order Task Exeption Occured");
			e.printStackTrace();
			
		}
		
		logger.debug("##### [createOrder] Job Task End!!!");
    }
    
    
    
    /**
     * 주문확정처리(Release) 프로세스 From SC
     * 
     * 주문생성건에 대해 releaseOrder API를 호출한다.
     * 
     * 
     * @param redisKey 
     * 				entCode:sellerCode:order:release
     * @param redisPushKey
     * @param redisErrKey
     */
    public void processOrderRelease(String redisKey, String redisPushKey, String redisErrKey){
    	
		logger.debug("##### [processOrderRelease] Job Task Start!!!");
    	
		long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
    		
		
		for(int i=0; i<dataCnt; i++){
			
			String keyData = listOps.rightPop(redisKey);
			logger.debug("[keyData]"+ keyData);
			
			try
			{
				// JSON --> HashMap 변환
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				String docType = (String)dataMap.get("docType");
				String entCode = (String)dataMap.get("entCode");
				String orderId = (String)dataMap.get("orderId");
				logger.debug("[docType]" + docType);
				logger.debug("[entCode]" + entCode);
				logger.debug("[orderId]" + orderId);
				
		    		
				// Set Input XML
				String scheduleNrelease = "Y";	// Schedule과 Release를 동시에 처리함.
				String scheduleOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(SCHEDULE_ORDER_TEMPLATE));
				
				MessageFormat msg = new MessageFormat(scheduleOrderXML);
				String inputXML = msg.format(new String[] {docType, entCode, orderId, scheduleNrelease} );
				logger.debug("##### [inputXML]"+inputXML); 
		
			
				// API Call
				String outputMsg = sterlingApiDelegate.comApiCall("scheduleOrder", inputXML);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
				
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					// TODO: Release API호출 자동화 예외처리
				}
			
			}
			catch(Exception e)
			{
				// TODO: Release API호출 자동화 예외처리
				e.printStackTrace();
				
			}
		
		} // End for
		
		logger.debug("##### [processOrderRelease] Job Task End!!!");
    	
    	
    }
    
    
    
    /**
     * 출고처리 프로세스 From Cube
     * 
     * Cube의 결과값을 받아 오더상태에 따라 
     * SC의 createShipment, confirmShipment API를 호출하는 Task Method 
     * 스케줄러를 통해 주기적으로 실행된다.
     * 
     * 
     * 
     * @param redisKey Cube에서 전송하는 연동데이타가 저장되는 키 
     * @param redisPushKey	SC에서 후처리후 Ma로 연동데이타를 전송하는 키
     * @param redisErrKey 에러키
     */
    		
    public void processShipmentFromCube(String redisKey, String redisPushKey, String redisErrKey){
    	
    		long dataCnt =  listOps.size(redisKey);
		logger.debug("["+redisKey+"] data length: "+dataCnt);
    	
    	
    		try{
    			
    			for(int i=0; i<dataCnt; i++){
    				
    				String keyData = listOps.rightPop(redisKey);
    				logger.debug("[keyData]"+keyData);
    				
    				// JSON --> HashMap 변환
    				ObjectMapper mapper = new ObjectMapper();
    				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
    				
    				// Order Status 추출
    				String status = (String)dataMap.get("status");
    				logger.debug("[status]"+status);
    				
    				// Create Shipment
    				if("3202".equals(status)){
    					processReleaseReturn(dataMap, redisKey, redisPushKey, redisErrKey);
    					
    				// Confirm Shipment
    				}else if("3700".equals(status)){
    					processConfirmShipment(dataMap, redisKey, redisPushKey, redisErrKey);
    				}
    			} // End for
    	
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	
    }
	
	/**
	 * 
	 * 주문확정정보 응답 수신프로세스 From Redis (From Cube) 3202
	 * 
	 * Cube의 처리결과를 받아 Create Shipment API(출고생성)를 호출하는 프로세스
	 * 부분취소가 일어난 경우 Release된 오더라인만 출고생성됨.
	 * 
	 * createShipment 이후의 처리는
	 * ScOrderShipmentHandler의 shipmentProcess가 처리한다.
	 * 
	 * 
	 * TODO: Cube 응답정보에서 출고번호 전송되는지 여부 확인 - 가능할 경우 Redis항목추가, 출고번호로 Create Shipment 호출가능여부 확인 
	 * TODO: MA 전송처리는 Handler클래스(SC 트랜잭션 후처리 프로세스)에서 처리 고려
	 * 
	 * Redis Data Format
	 *  {
		    "org_code": "사업부코드",
		    "sell_code": "판매채널코드",
		    "orderId": "오더번호",
		    "orderHeaderKey": "오더번호키",
		    "status": "3202",
		    "tranDt": "전송일자",
		    "list": [
		        {
		            "ship_node": "창고코드",
					"orderLineNo": "오더라인순번",
					"orderLineKey": "오더라인키",
		            "orderReleaseKey": "오더라인확정키",
		            “shipmentNo”:”전표번호”,
		            “statuscd”:”결과코드”
		}
		    ]
		}
	 * 
	 * 	- Redis Read Key: SLV:ASPB:order:update:C2S
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
	 *        	Aspenbay: SLV:ASPB:order:update:C2S
	 * @param redisPushKey
	 * 			Aspenbay: SLV:ASPB:order:update:S2M
	 * @param redisErrKey
	 * 			Aspenbay: SLV:ASPB:order:error
	 */
	private void processReleaseReturn(HashMap<String, Object> dataMap, String redisKey, String redisPushKey, String redisErrKey) throws Exception{
	
		logger.debug("##### [processReleaseReturn] Job Task Started!!!");
				
		// createShipment API 호출
		String docType = "0001";	// Sales Order
		String entCode = env.getProperty("ca."+(String)dataMap.get("org_code")); // TODO: Cube의 사업부코드 - SC조직코드로 매핑
		String sellerCode = (String)dataMap.get("sell_code");
		String orderId = (String)dataMap.get("orderId");
		logger.debug("[docType]"+docType);
		logger.debug("[entCode]"+entCode);
		logger.debug("[sellerCode]"+sellerCode);
		logger.debug("[orderId]"+orderId);
		
		/*
		 * TODO: 오더라인 리스트의 첫번째 결과로 해당오더의 처리결과를 판단한다. 
		 *       Cube에서 오더라인 1건만 출고의뢰실패하더라도 오더전체를 품절취소 또는 실패로 처리해서 전송함
		 */
		String resultCode = (String)((ArrayList<HashMap<String,Object>>)dataMap.get("list")).get(0).get("statuscd");
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(dataMap);
		
		// 품절취소 
		if("90".equals(resultCode))
		{	
			logger.debug("##### [processReleaseReturn] Cube shortage occured!!!");
			
			// 1. 상품정보 추출
			
			// 2. adjustInventory 호출 - 재고 0으로 변경 - TODO: 재고변경 이벤트핸들러를 통해 MA로 재고변경정보 전송 
			
			
			// 3. scheduleOrder 호출 - 자동으로 SC에서 재고부족으로 상태변경. 운영자 확인후 취소처리 필요
			HashMap<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("entCode", entCode);
			resultMap.put("orderId", orderId);
			resultMap.put("docType", docType);
			
			String retryOrderReleaseData = mapper.writeValueAsString(resultMap);
			logger.debug("[retryOrderReleaseData]"+retryOrderReleaseData);
			
			// 재 Release처리를 위해 Release처리키에 데이타 전송
			String releaseKey = entCode+":"+sellerCode+":order:release";
			listOps.leftPush(releaseKey, retryOrderReleaseData);
			
			// 스케줄러를 기다리지 않고 바로 수동으로 Order Release Task 실행
			processOrderRelease(releaseKey, "", redisErrKey);
			
		}
		// 실패
		else if("99".equals(resultCode))
		{
			logger.debug("##### [processReleaseReturn] Cube shortage occured!!!");
			
			// 에러키에 저장
			// TODO: 출고의뢰 결과수신 예외처리 필요
			listOps.leftPush(redisErrKey, outputMsg);
			
		}
		// 성공일 경우
		else if("01".equals(resultCode)){
			
			// Order Release Key 조회
			// TODO: 하나의 주문에 릴리즈가 2건이상일 경우 고려필요함. 현재는 한건의 주문은 한건의 릴리즈로 생성됨을 전제로 연동
			ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(docType, entCode, orderId);
			
			// 릴리즈키와 Cube에서 전송된 출고의뢰 전표번호로 출고생성 API호출 - createShipment 
			for(int j=0; j<releaseKeys.size(); j++){
				
				// TODO: Cube연동시 ShipmentNo에 전표번호 적용필요
				int result = sterlingApiDelegate.createShipment("", releaseKeys.get(j));
				if(result == 0){
					
					// TODO: createShipment API호출 예외처리
					// JSON 변환
					listOps.leftPush(redisErrKey, outputMsg);
					
				}
			}
		}
		
		
		logger.debug("##### [processReleaseReturn] Job Task End!!!");
	}
	
	
	/**
	 * 출고확정정보 응답 수신 (From Cube) 3700
	 * 
	 *  - Cube에서 출고확정된 주문정보를 전달받아 SC의 ConfirmShiment를 호출 
	 *  - SC의 주문상태 Shipped(3700)로 변경됨 --> MA로 출고정보 전송 --> 배송중
	 *  
	 *  - TODO: MA로의 출고정보 전송처리는 Handler클래스에서 처리 고려
	 *  
	 *  
	 * @param redisKey
	 *        	Aspenbay: SLV:ASPB:order:update:C2S
	 * @param redisPushKey
	 * 			Aspenbay: SLV:ASPB:order:update:S2M
	 * @param redisErrKey
	 * 			Aspenbay: SLV:ASPB:order:error
	 */
	private void processConfirmShipment(HashMap<String, Object> dataMap, String redisKey, String redisPushKey, String redisErrKey) throws Exception{
	
		logger.debug("##### [processConfirmShipment] Job Task Started!!!");
				
				
		// confirmShipment API 호출
		
		String docType = (String)dataMap.get("docType");
		String entCode = (String)dataMap.get("entCode");
		String orderId = (String)dataMap.get("orderId");
		String sellerCode = (String)dataMap.get("sellerCode");
		logger.debug("[docType]"+docType);
		logger.debug("[entCode]"+entCode);
		logger.debug("[orderId]"+orderId);
		logger.debug("[sellerCode]"+sellerCode);
		
		
		ArrayList<HashMap<String,String>> shipmentInfoList = (ArrayList<HashMap<String,String>>)dataMap.get("shipment");
		
		
		// TODO: Cube로 부터 받아야 하는 정보: 택배사코드, 송장번호, 택배사코드는 조직에 코드추가 및 매핑필요.
		String scac = "19991214183438453"; // USPS Default Code
		String trackingNo = "1111111111";	// Test Code
		
		
//					String xml_template = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE));
//					
//					MessageFormat msg = new MessageFormat(xml_template);
//					String inputXML = msg.format(new String[] {docType, entCode, orderId} );
//					logger.debug("##### [inputXML]"+inputXML); 
//					
//					
//					// Call getShipmentListForOrder - ShipNode 
//					String outputMsg = sterlingApiDelegate.comApiCall(API_GET_SHIPMENT_LIST_FOR_ORDER, inputXML);
//					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
//					
//					XPath xp = XPathFactory.newInstance().newXPath();
//					NodeList shipmentInfoList = (NodeList)xp.evaluate("/ShipmentList/Shipment", doc.getDocumentElement(), XPathConstants.NODESET);
		
		
		// Shipment 별 Confirm Shipment 수행
//					for(int j=0; j<shipmentInfoList.getLength(); j++){
		for(int j=0; j<shipmentInfoList.size(); j++){
			
//						String sellerCode = (String)xp.evaluate("@SellerOrganizationCode", shipmentInfoList.item(i), XPathConstants.STRING);
//						String shipmentNo = (String)xp.evaluate("@ShipmentNo", shipmentInfoList.get(i), XPathConstants.STRING);
//						String shipNode = (String)xp.evaluate("@ShipNode", shipmentInfoList.get(i), XPathConstants.STRING);
			
			/**
				<Shipment 
				    DocumentType="{0}" EnterpriseCode="{1}"
				    SellerOrganizationCode="{2}" 
				    ShipmentNo="{3}" ShipNode="{4}" TrackingNo="{5}">
				    <ScacAndService OrganizationCode="{6}" ScacAndServiceKey="{7}"/>
				</Shipment>
			 */
			String confirmShipment_template = FileContentReader.readContent(getClass().getResourceAsStream(CONFIRM_SHIPMENT_TEMPLATE));
			
			MessageFormat msg = new MessageFormat(confirmShipment_template);
			String inputXML = msg.format(new String[] {docType, entCode, sellerCode, 
							shipmentInfoList.get(j).get("shipmentNo"), 
							shipmentInfoList.get(j).get("shipNode"), 
							shipmentInfoList.get(j).get("trackingNo"),
							shipmentInfoList.get(j).get("entCode"),
							shipmentInfoList.get(j).get("carrierCode")
						} );
			logger.debug("##### [confirmShipment inputXML]"+inputXML); 
			
			
			// Call Confirm Shipment
			String outputXML = sterlingApiDelegate.comApiCall(API_CONFIRM_SHIPMENT, inputXML);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				// TODO: confirmShipment API호출 예외처리
			}
		}
				
		
		logger.debug("##### [processConfirmShipment] Job Task End!!!");
		
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
     * Create Shipment처리 ( OUTRO Test 전용) - 현재 사용안함, processReleaseReturn이 대체
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
					ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(docType, entCode, orderId);
					
					for(int j=0; j<releaseKeys.size(); j++){
						
						logger.debug("[releaseKeys]"+releaseKeys.get(j));
						
						/**
						 * TODO: 현재는 MITS가 Shipment처리를 담당하나 향후에는 CUBE가 출고지시를 할수 있도록 Release된 주문정보를
						 * Cube로 전송하고 SC(MITS)는 CUBE의 출고확정정보를 받아 Shipped로 주문의 상태를 변경하는 방식으로 변경필요 
						 */
						int result = sterlingApiDelegate.createShipment("", releaseKeys.get(j));
						
						if(result == 0){
							/**
							 * confirm shipment 처리 후 Ma로 shipment 정보를 전송하기 위해
							 * Ma의 Key값이 되는 오더번호를 별도로 저장해 놓는다. 
							 */
//							String shipmentNo = resultMap.get("shipmentNo");
//							logger.debug("[shipmentNo]"+shipmentNo);
//							
//							valueOps.set(shipmentNo, orderId);
						}else{
							// TODO: 에러처리
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

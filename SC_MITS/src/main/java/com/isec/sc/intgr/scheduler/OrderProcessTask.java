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
import java.util.List;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scala.Array;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;



public class OrderProcessTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);

	
	private static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";
	private static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	private static final String API_CHANGE_SHIPMENT = "changeShipment";
	private static final String API_CANCEL_ORDER = "cancelOrder";
	
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
	
	@Value("${sc.api.adjustInventory.item.template}")
	private String ADJUST_INVENTORY_ITEM_TEMPLATE;
	
	@Value("${sc.api.cancelOrder.template}")
	private String CANCEL_ORDER_TEMPLATE;
	
	@Value("${sc.api.changeShipment.template}")
	private String CHANGE_SHIPMENT_TEMPLATE;
	
	
	
	
	
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
    		
    public void processOrderUpdate(String redisKey, String redisPushKey, String redisErrKey){
    	
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
    				
    				
    				// Cancel Shipment
    				}else if("9002".equals(status)){
    					processCancelReturn(dataMap, redisKey, redisPushKey, redisErrKey);
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
		            "shipmentNo":"전표번호",
		            "bar_code":"상품코드",
		            "uom":"측정단위",
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
		 *       Cube에서 오더라인 1건만 출고의뢰 실패하더라도 오더전체를 품절취소 또는 실패로 처리해서 전송함
		 */
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(dataMap);
		
		int failCount = 0;
		
		ArrayList<HashMap<String,Object>> resultList = (ArrayList<HashMap<String,Object>>)dataMap.get("list");
		for( int i=0; i<resultList.size(); i++){
			
			String resultCode =  (String)resultList.get(i).get("statuscd");
			
			// 품절취소 - 오더라인별로 처리
			if("90".equals(resultCode))
			{
				String ship_node =  (String)resultList.get(i).get("ship_node");
				String bar_code =  (String)resultList.get(i).get("itemId");
				String uom =  (String)resultList.get(i).get("uom");
				
				logger.debug("##### [processReleaseReturn] 품절취소발생 Cube shortage occured!!!");
				
				
				// 1. adjustInventory 호출 - 재고 0으로 변경 - TODO: 재고변경 이벤트핸들러를 통해 MA로 재고변경정보 전송 
				
				// 현 재고수량 조회
				Double currScQty = sterlingApiDelegate.getCalcQtyBeforeAdjustInv(entCode, bar_code, ship_node, uom, "S");
				// 재고를 0으로 처리하기 위해서 현 재고를 (-)처리 
				Double adjustQty =  -currScQty;
				logger.debug("#####[Sc Qty]"+currScQty);
				
				// SC 재고차감
				// adjustInventory Input XML Generation
				/*
				 *  <Item OrganizationCode="{1}" ItemID="{0}" Quantity="{2}" 
						ShipNode="{3}" UnitOfMeasure="{4}" SupplyType="ONHAND" 
						AdjustmentType="ADJUSTMENT">
					</Item>
				 */
				// 재고조정 API Input
			  	String adjustIvnItemTempate = FileContentReader.readContent(getClass().getResourceAsStream(ADJUST_INVENTORY_ITEM_TEMPLATE));
				MessageFormat msg = new MessageFormat(adjustIvnItemTempate);
				String adjustInvXML = msg.format(new String[] {
															entCode, bar_code, String.valueOf(adjustQty), ship_node, uom, 
														} 
				);
			
				String invXML = "";
				invXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<Items>"
						+  adjustInvXML
						+ "</Items>";
				logger.debug("##### [adjustInventory input XNL]"+invXML); 
			
				// adjustInventory API 호출
				String adjInv_output = sterlingApiDelegate.comApiCall("adjustInventory", invXML);
				//Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(adjInv_output.getBytes("UTF-8")));
				// TODO: adjustInventory 후처리
				
				
				// TODO: MA와 해당상품의 재고 연동
				
				
			}
			// 실패 - 오더라인별로 처리, 09와 90이 같이 들어올수 있음
			else if("09".equals(resultCode))
			{
				failCount++;
				
			}
			// 성공일 경우 - 전체성공 처리, 루프 한번만 수행
			else if("01".equals(resultCode)){
				
				
				String shipmentNo =  (String)resultList.get(i).get("shipmentNo");
				String orderReleaseKey =  (String)resultList.get(i).get("orderReleaseKey");
				
				logger.debug("##### [shipmentNo]" + shipmentNo);
				logger.debug("##### [orderReleaseKey]" + orderReleaseKey);
				
				
				// Order Release Key 조회
				// TODO: 하나의 주문에 릴리즈가 2건이상일 경우 고려필요함. 현재는 한건의 주문은 한건의 릴리즈로 생성됨을 전제로 연동
				ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(orderId, "");
				
				
				for(int j=0; j<releaseKeys.size(); j++){
					
					// TODO: Cube연동시 ShipmentNo에 전표번호 적용필요
					int result = sterlingApiDelegate.createShipment(shipmentNo, releaseKeys.get(j));
					if(result == 0){
						// TODO: createShipment API호출 예외처리
						// JSON 변환
						listOps.leftPush(redisErrKey, outputMsg);
					}
				}
				
				break;
			}// End for orderLine
			
			
			// 전체실패일 경우 에러키에 저장
			if(failCount == resultList.size()){
				logger.debug("##### [processReleaseReturn] 출고의뢰 실패 Cube shortage occured!!!");
				
				// 에러키에 저장
				// TODO: 출고의뢰 결과수신 예외처리 필요
				listOps.leftPush(redisErrKey+":3202", outputMsg);
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
		
		
		/**
		 * {
			    "org_code": "80",
			    "sell_code": "ASPB",
			    "orderId": "Y100001100",
			    "orderHeaderKey": "20141010191200239236",
			    "tranDt": "20141016",
			    "status": "3700",
			    "list": [
			        {
			            "ship_node": "WH001",
			            "orderLineNo": "1",
			            "orderLineKey": "20141010191200239237",
			            "orderReleaseKey": "20141010191300239277",
			            "shipmentNo": "341015007",
			            "expnm": "11111",  -- 송장번호
			            "expNo": "00",     -- 택배사코드
			            "outDt": "20141016",
			            "outTime": "165021"
			        },
			        {
			            "ship_node": "WH001",
			            "orderLineNo": "2",
			            "orderLineKey": "20141010191200239238",
			            "orderReleaseKey": "20141010191300239277",
			            "shipmentNo": "341015007",
			            "expnm": "11111",
			            "expNo": "00",
			            "outDt": "20141016",
			            "outTime": "165021"
			        }
			    ]
			}

		 */
				
		// confirmShipment API 호출
		
		String docType = "0001";
		String entCode = env.getProperty("ca."+(String)dataMap.get("org_code"));
		String sellerCode = (String)dataMap.get("sell_code");
		String orderId = (String)dataMap.get("orderId");
		
		logger.debug("[docType]"+docType);
		logger.debug("[entCode]"+entCode);
		logger.debug("[orderId]"+orderId);
		logger.debug("[sellerCode]"+sellerCode);
		
		
		// TODO: Cube에서 넘어온 출고확정정보는 오더라인갯수와 상관없이 1건. 따라서 list의 첫번째 값으로 confirmShipment수행 
		ArrayList<HashMap<String,String>> shipmentInfoList = (ArrayList<HashMap<String,String>>)dataMap.get("list");
		HashMap<String,String> shipmentInfo = shipmentInfoList.get(0);
		
		// TODO: 택배사코드는 조직에 코드추가 및 매핑필요.
		String shipmentNo = shipmentInfo.get("shipmentNo");
		String shipNode = shipmentInfo.get("ship_node");
		String trackingNo = shipmentInfo.get("expnm");		    // 송장번호
		String scacCode = shipmentInfo.get("expNo");				// 택배사코드
		
		logger.debug("[trackingNo]"+trackingNo);
		logger.debug("[scacCode]"+scacCode);
		
		// TODO: 택배사코드 테스트코드로 작성
		String scacOrgCode = entCode;
		//String scacOrgCode = "CJL";
		//scacCode = "CJL_STD";
		scacCode = "19991214183438453"; // USPS Default Code
		
		String confirmShipment_template = FileContentReader.readContent(getClass().getResourceAsStream(CONFIRM_SHIPMENT_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(confirmShipment_template);
		String inputXML = msg.format(new String[] {docType, entCode, sellerCode, 
								shipmentNo, 
								shipNode, 
								trackingNo,
								scacOrgCode,
								scacCode
						  } );
		logger.debug("##### [confirmShipment inputXML]"+inputXML); 
		
		
		// Call Confirm Shipment
		String outputXML = sterlingApiDelegate.comApiCall(API_CONFIRM_SHIPMENT, inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		if("Errors".equals(doc.getFirstChild().getNodeName())){
			
			ObjectMapper mapper = new ObjectMapper();
			String errJson = mapper.writeValueAsString(dataMap);
			listOps.leftPush(redisErrKey+":3700", errJson);
			
			
			throw new Exception("ConfirmShipment Failed!!!!!");
			
		}
				
		
		logger.debug("##### [processConfirmShipment] Job Task End!!!");
		
	}

	
	
	
	/**
	 * 주문취소 처리결과 수신 (From Cube)
	 * 
	 * 
	 *  1. Cube의 주문취소결과 상태확인
	 *  
	 *    - 실패 또는 처리대상건 없음, 출고확정건 일 경우 
	 *      -> 주문취소요청 결과 키에 저장
	 *         조직코드:판매채널코드:order:cancel:result
	 *         
	 *         후처리 중단
	 *         
	 *    - 기처리건일 경우
	 *      -> 주문상태 확인 후 취소상태가 아니면 주문취소 재수행
	 *      
	 *    - 성공일 경우 
	 *       - SC의 주문상태 확인
	 *          - 3350(출고준비)일 경우 
	 *            -> changeShipment 수행 -> 주문취소 수행
	 *          - 3200(출고의뢰)일 경우 
	 *            -> 주문취소 수행
	 *  
	 * @param redisKey
	 * @param redisPushKey
	 * @param redisErrKey
	 */
	public void processCancelReturn(HashMap<String, Object> dataMap, String redisKey, String redisPushKey, String redisErrKey) throws Exception{
	
		
		logger.debug("##### [processCancelReturn] Job Task Started!!!");
		/*
		 * {
			    "org_code": "80",
			    "sell_code": "ASPB", 
			    "orderId": "100000417",
			    "orderHeaderKey": "20141014180100248322", 
			    "orderDt": "20141014",
			    "status": "9000",   -- 취소요청상태
			    "tranDt": "20141015145140",
			    "list": [
			        {
			            "org_code": "80",
			            "sell_code": "ASPB", 
			            "orderId": "100000417",
			            "orderDt": "20141014",
			            "orderLineNo": "1",
			            "orderLineKey": "20141014180100248323",
			            "orderReleaseKey": "20141014180200248344",
			            "itemId": "AYB5CL50103L",
			            "itemNm": "Capri Blue Blue Signature Jar BOHO-LUXE",
			            "qty": "1",
			             "salePrice": "1000",
			          “statuscd”:”결과코드”,      --- 주문취소결과 전송시 필요항목
			           “statusMsg:”결과메세지”  -- 주문취소결과 전송시 필요항목
			        }
			    ]
			 }

		 */
		String docType = "0001";
		String entCode = env.getProperty("ca."+(String)dataMap.get("org_code")); // TODO: Cube의 사업부코드 - SC조직코드로 매핑
		String sellCode = (String)dataMap.get("sell_code");
		String orderId = (String)dataMap.get("orderId");
		logger.debug("[docType]"+docType);
		logger.debug("[entCode]"+entCode);
		logger.debug("[orderId]"+orderId);
		logger.debug("[sellerCode]"+sellCode);
		
		
		
		
		/*
		 *   01 - 성공
			 02 - 기처리
			 09 - 실패 또는 처리대상건 없음
			 90 - 출고확정건
		 */
		// Cube의 주문취소결과 상태확인
		List<HashMap<String,Object>> cancelList = (ArrayList<HashMap<String,Object>>)dataMap.get("list");
		String cubeStatus = (String)cancelList.get(0).get("statuscd");
		String cubeStatusMsg = (String)cancelList.get(0).get("statusMsg");
		
		logger.debug("[cubeStatus]"+cubeStatus);
		
		// 실패 또는 처리대상건 없음, 출고확정건, 기처리건
		// TODO: 09인 경우 자체주문취소 처리필요
		if("09".equals(cubeStatus) || "90".equals(cubeStatus) || "02".equals(cubeStatus)){
			
			// 주문취소 Result키에 기록
			logger.debug("주문취소요청 실패[CanceOrder Request Result is failed]");
			
			HashMap<String, String> cancelReqMap = new HashMap<String, String>();
			cancelReqMap.put("orderNo", orderId);
			cancelReqMap.put("enterPrise", entCode);
			cancelReqMap.put("sellerOrg", sellCode);
			
			cancelReqMap.put("status_code", cubeStatus);
			cancelReqMap.put("status_text", cubeStatusMsg);
			cancelReqMap.put("status_class", "danger");
			
			ObjectMapper mapper = new ObjectMapper();
			String resJson = mapper.writeValueAsString(cancelReqMap);
			
			
			String cancelResKey = entCode+":"+sellCode+":order:cancel:result";
			logger.debug("[9000 CanceReq Result key]"+cancelResKey);
			logger.debug("[9000 CanceReq Result Data]"+resJson);
			
			listOps.leftPush(cancelResKey, resJson);
			
			return;
		}
		
		
		// 성공일 경우
		else if("01".equals(cubeStatus) ){
		
			// 1. 주문상태 확인 --> 주문취소 수행
			
			Document doc = sterlingApiDelegate.getOrderDetails(docType, entCode, orderId);
			String scStatus = doc.getDocumentElement().getAttribute("MaxOrderStatus");
			
			logger.debug("[maxStatus]"+scStatus);
			
				
			// 3350일 경우 changeShipment 수행 --> cancelOrder 수행
			if("3350".equals(scStatus)){
				
				// changeShipment 수행 - 출고생성 취소
				
				
				// ShipNode, ShipmentNo 조회
				String xml_template = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE));
				MessageFormat msg = new MessageFormat(xml_template);
				String inputXML = msg.format(new String[] {docType, entCode, orderId} );
				
				String outputMsg = sterlingApiDelegate.comApiCall(API_GET_SHIPMENT_LIST_FOR_ORDER, inputXML);
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
				
				XPath xp = XPathFactory.newInstance().newXPath();
				Node shipmentInfoNode = (Node)xp.evaluate("/ShipmentList/Shipment[1]", doc.getDocumentElement(), XPathConstants.NODE);
				
				
				String shipNode = (String)xp.evaluate("@ShipNode", shipmentInfoNode, XPathConstants.STRING);
				String shipmentNo = (String)xp.evaluate("@ShipmentNo", shipmentInfoNode, XPathConstants.STRING);
				logger.debug("[shipNode]"+shipNode);
				logger.debug("[shipmentNo]"+shipmentNo);
				
				
				// changeShipment API Call
				xml_template = FileContentReader.readContent(getClass().getResourceAsStream(CHANGE_SHIPMENT_TEMPLATE));
				msg = new MessageFormat(xml_template);
				inputXML = msg.format(new String[] {entCode, sellCode, shipNode, shipmentNo} );
				
				outputMsg = sterlingApiDelegate.comApiCall(API_CHANGE_SHIPMENT, inputXML);
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
				
				// TODO: changeShipment API 에러처리
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					
					throw new Exception("Cacel Shipment Failed");
				}
				
			}
			
			// cancelOrder API Call
			String cancelReason = "CANCEL_INFO";
			String cacnelNote = "Cube 주문취소 처리완료";
			String templateFile = FileContentReader.readContent(getClass().getResourceAsStream(CANCEL_ORDER_TEMPLATE));
			MessageFormat msg = new MessageFormat(templateFile);
			String inputXML = msg.format(new String[] {docType, entCode, orderId, cancelReason, cacnelNote} );
			
			
			logger.debug("[Cancel Order inputXML]"+inputXML);
			String outputMsg = sterlingApiDelegate.comApiCall(API_CANCEL_ORDER, inputXML);
			logger.debug("[Cancel Order outputXML]"+inputXML);
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			// TODO: cancelOrder API 에러처리
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				// TODO: 에러처리
				throw new Exception("Cacel Order Failed");
			}
		
		} // End if 결과상태 확인
		
		logger.debug("##### [processCancelReturn] Job Task Started!!!");
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
					ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(orderId,"");
					
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

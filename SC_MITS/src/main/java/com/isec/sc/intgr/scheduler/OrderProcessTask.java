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
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.api.util.RedisCommonService;



public class OrderProcessTask {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);

	
	private static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";
	private static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	private static final String API_CHANGE_SHIPMENT = "changeShipment";
	private static final String API_CANCEL_ORDER = "cancelOrder";
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired  private RedisCommonService redisService;
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
	
	@Value("${sc.SCAC.CJL.STD}")
	private String SCAC_CJL_STD;
	
	@Value("${sc.order.type.sales}")
	private String SC_ORDER_TYPE_SALES;
	
	
	/**
	 * Create Order From MA(Magento, WCS)
	 * 
	 * MA로 부터 전송된 주문정보로 SC의 주문생성 API(createOrder)를 수행하고,
	 * 정상일 경우 SC의 schedule&releaseOrder를 바로 실행시킨다.
	 * 
	 * 
	 * @param redisKey	MA에 전송한 최초 오더생성 정보 ( SC의 CreateOrder API의 Input XML)
	 * @param redisPushKey MITS에서 오더생성 후 MA로 오더생성 성공여부를 전송하는 Key -> Ma는 이 정보를 확인해서 Placed로 변경한다.
	 * @param redisErrKey MITS에서 오더생성시 에러가 발생할 경우 에러정보를 저장할 Redis Key
	 */
    public void createOrder(String redisKey, String redisPushKey, String redisErrKey){
        
    	
	    	logger.debug("##### ["+redisKey+"][createOrder] Started!!!");
	    	
	    	long dataCnt =  listOps.size(redisKey);
		logger.debug("##### ["+redisKey+"][createOrder] data length: "+dataCnt);
		
		String orderInputXml = "";
		
		try{
			for(int i=0; i<dataCnt; i++){
				
				// Get Input XML from Redis
				orderInputXml = listOps.rightPop(redisKey);
				if(orderInputXml == null || "".equals(orderInputXml)){
					continue;
				}
				
				logger.debug("[Create Order input XML]"+ orderInputXml);
				
				// SC API 호출
				String outputXML = sterlingApiDelegate.createOrder(orderInputXml);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
				
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					
					logger.debug("[Error Message]"+outputXML);
					// TODO: 오더생성 실패시 시스템관리자 메일발송
					listOps.leftPush(redisErrKey, orderInputXml);
				}
				
			}
			
		}catch(Exception e){
			logger.debug("##### Create Order Task Exeption Occured");
			e.printStackTrace();
			
			// TODO: 오더생성 실패시 시스템관리자 메일발송
			listOps.leftPush(redisErrKey, orderInputXml);
		}
		
		logger.debug("##### ["+redisKey+"][createOrder] End!!!");
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
    	
		logger.debug("##### ["+redisKey+"][releaseOrder] Start!!!");
    	
		long dataCnt =  listOps.size(redisKey);
		logger.debug("##### ["+redisKey+"][releaseOrder] data length: "+dataCnt);
    		
		// Set Input XML
		String scheduleNrelease = "Y";	// Schedule과 Release를 동시에 처리함.
		String scheduleOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(SCHEDULE_ORDER_TEMPLATE));
		MessageFormat msg = new MessageFormat(scheduleOrderXML);
		
		String apiName = "scheduleOrder";
		
		for(int i=0; i<dataCnt; i++){
			
			String keyData = listOps.rightPop(redisKey);
			logger.debug("[keyData]"+ keyData);
			
			String docType = "";
			String entCode = "";
			String orderId = "";
			
			try
			{
				// JSON --> HashMap 변환
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
				
				docType = (String)dataMap.get("docType");
				entCode = (String)dataMap.get("entCode");
				orderId = (String)dataMap.get("orderId");
				
				logger.debug("[docType]" + docType);
				logger.debug("[entCode]" + entCode);
				logger.debug("[orderId]" + orderId);
				
				String inputXML = msg.format(new String[] {docType, entCode, orderId, scheduleNrelease} );
				logger.debug("##### [scheduleOrder inputXML]"+inputXML); 
			
				// API Call
				String outputMsg = sterlingApiDelegate.comApiCall(apiName, inputXML);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
				
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					
					logger.debug("[Error Message]"+outputMsg);
					redisService.saveErrDataByOrderId(redisErrKey, orderId, keyData);
				}
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				redisService.saveErrDataByOrderId(redisErrKey, orderId, keyData);
				
			}
		
		} // End for
		
		logger.debug("##### ["+redisKey+"][releaseOrder] End!!!");
    	
    	
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
    		
    		logger.debug("##### ["+redisKey+"][processOrderUpdate] Start!!!");
    	
    		long dataCnt =  listOps.size(redisKey);
		logger.debug("##### ["+redisKey+"][processOrderUpdate] data length: "+dataCnt);
    	
    	
    		try{
    			
    			for(int i=0; i<dataCnt; i++){
    				
    				String keyData = listOps.rightPop(redisKey);
    				logger.debug("[keyData]"+keyData);
    				
    				
    				if(keyData == null) return;
    				
    				
    				// JSON --> HashMap 변환
    				ObjectMapper mapper = new ObjectMapper();
    				HashMap<String, Object> dataMap = mapper.readValue(keyData, new TypeReference<HashMap<String,Object>>(){});
    				
    				// Order Status 추출
    				String status = (String)dataMap.get("status");
    				logger.debug("[status]"+status);
    				
    				
    				// 상태별 에러키 재지정
    				redisErrKey = redisErrKey+":"+status;
    				logger.debug("[redisErrKey]"+redisErrKey);
    				
    				
    				// Create Shipment
    				if("3202".equals(status)){
    					processCreateShipment(dataMap, redisKey, redisPushKey, redisErrKey);
    					
    				
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
    		
    		logger.debug("##### ["+redisKey+"][processOrderUpdate] End!!!");
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
	 *        	Aspenbay: KOLOR:ASPB:order:update:C2S
	 * @param redisPushKey
	 * 			Aspenbay: KOLOR:ASPB:order:update:S2M
	 * @param redisErrKey
	 * 			Aspenbay: KOLOR:ASPB:order:update:error:3202
	 */
	public void processCreateShipment(HashMap<String, Object> dataMap, String redisKey, String redisPushKey, String redisErrKey) throws Exception{
	
		logger.debug("##### ["+redisKey+"][createShipment] Started!!!");
				
		// createShipment API 호출
		String docType = SC_ORDER_TYPE_SALES;	// Sales Order
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
				
				logger.debug("##### [processCreateShipment] 품절취소발생 Cube shortage occured!!!");
				
				
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
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(adjInv_output.getBytes("UTF-8")));
				
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					// 에러데이타 저장
					redisService.saveErrDataByOrderId(redisErrKey, orderId, outputMsg);
					// TODO: 에러발생 처리 - 시스템담당자 메일발송
				}else{
					
					// 품절취소 키에 오더정보 저장. TODO: KEY 유효기간 정의 필요
					String cubeShortedKey = entCode+":"+sellerCode+":order:3202:90";
					String data = "{\"orderId\":\""+orderId+"\",\"itemId\":\""+bar_code+"\"}";
					listOps.leftPush(cubeShortedKey, data);
					
					/*
					 * 0으로 변경된 재고정보 MA 전송키에 저장
					 * {
						"list": [
							        {
							            "org_code":"사업부코드"
							            "bar_code": "상품코드", ,
							            "qty": "수량",
							        }
							    ]
						}
					 */
					HashMap<String, String> sendMaInv = new HashMap<String, String>();
					sendMaInv.put("org_code", entCode);
					sendMaInv.put("bar_code", bar_code);
					sendMaInv.put("qty", 0+"");
					
					List<HashMap<String,String>> sendMaList = new ArrayList<HashMap<String,String>>();
					sendMaList.add(sendMaInv);
					
					HashMap<String,List<HashMap<String,String>>> sendMaMap = new HashMap<String, List<HashMap<String,String>>>();
					sendMaMap.put("list", sendMaList);
					
					
					ObjectMapper resultMapper = new ObjectMapper();
					logger.debug("[MA Send MSG]"+resultMapper.writeValueAsString(sendMaMap));
					
					String maKey = entCode+":"+sellerCode+":"+"inventory:S2M";
					listOps.leftPush(maKey, resultMapper.writeValueAsString(sendMaMap));
				}
				
			}
			// 실패 - 오더라인별로 처리, 09와 90이 같이 들어올수 있음
			else if("09".equals(resultCode))
			{
				failCount++;
				
			}
			// 성공일 경우 - 전체성공 처리, 루프 한번만 수행, createShipment호출
			else if("01".equals(resultCode)){
				
				
				/*
				 * 주문확정 재처리시  확보된 재고의 ShipNode가 오더라인별로 다를 경우 릴리즈키가 다르게 생성됨.(Shipment도 다르게 생성된다는 의미)
				 */
				String cubeShipmentNo =  (String)resultList.get(i).get("shipmentNo");				
				String orderReleaseKey =  (String)resultList.get(i).get("orderReleaseKey");
				
				logger.debug("##### [cubeShipmentNo]" + cubeShipmentNo);
				logger.debug("##### [orderReleaseKey]" + orderReleaseKey);
				
				
				// Order Release Key 조회. 창고가 다르게 release된 경우 오더라인건만큼 조회됨
				ArrayList<String> releaseKeys = (ArrayList<String>)sterlingApiDelegate.getOrderReleaseList(orderId, "");
				
				for(int j=0; j<releaseKeys.size(); j++){
					
					int result = sterlingApiDelegate.createShipment("", releaseKeys.get(j), cubeShipmentNo);
					if(result == 0){
						
						// 에러데이타 저장
						redisService.saveErrDataByOrderId(redisErrKey, orderId, outputMsg);
						
						// TODO: 에러발생 처리 - 시스템담당자 메일발송
					}
				}
				
				break;
			}// End for orderLine
			
			
			// 전체실패일 경우 에러키에 저장
			if(failCount == resultList.size()){
				logger.debug("##### [processCreateShipment] 출고의뢰 실패 Cube shortage occured!!!");
				
				
				// 큐브실패 키에 오더정보 저장. TODO: 유효기간 정의 필요
				String cubeFailedKey = entCode+":"+sellerCode+":order:3202:09";
				listOps.leftPush(cubeFailedKey, orderId);
				
				
				
				// 에러키에 저장
				redisService.saveErrDataByOrderId(redisErrKey, orderId, outputMsg);
			}
		}
		
		
		logger.debug("##### ["+redisKey+"][createShipment] End!!!");
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
	public void processConfirmShipment(HashMap<String, Object> dataMap, String redisKey, String redisPushKey, String redisErrKey) throws Exception{
	
		logger.debug("##### ["+redisKey+"][cofirmShipment] Started!!!");
		
		
		// confirmShipment API 호출
		
		String docType = SC_ORDER_TYPE_SALES;
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
		
//		String cubeShipmentNo = shipmentInfo.get("shipmentNo");	   // 출고생성시점에 이미 반영됨, 사용안
		String shipNode   = shipmentInfo.get("ship_node");
		String trackingNo = shipmentInfo.get("expnm");		        // 송장번호
		String scacCode   = shipmentInfo.get("expNo");				// 택배사코드
		String outDt      = shipmentInfo.get("outDt");				// 출고일자 YYYYMMDD
		String outTime    = shipmentInfo.get("outTime");				// 출고시간 HH:MI:SS
		
		logger.debug("[outDt]"+outDt);
		logger.debug("[outTime]"+outTime);
		logger.debug("[trackingNo]"+trackingNo);
		logger.debug("[scacCode]"+scacCode);
		
		String aShipDate = CommonUtil.getDateTimeToScDate(outDt+outTime, "-");
		logger.debug("[sShipDate]"+aShipDate);
		 
		// scacCode = "20141016212607191960"; // TODO: CJL_STD PROD
		// scacCode = "20140917094212141984"; // CJL_STD DEV
		
		
		
		// shipment 정보조회
		String shipmentInfo_template = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE));
		MessageFormat msg = new MessageFormat(shipmentInfo_template);
		String inputXML = msg.format(new String[] {docType, entCode, orderId} );
		logger.debug("##### [getShipmentListForOrder inputXML]"+inputXML); 
		
		String outputXML = sterlingApiDelegate.comApiCall("getShipmentListForOrder", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		if("Errors".equals(doc.getFirstChild().getNodeName())){
			
			logger.debug("Error Message:"+ outputXML);
			
			ObjectMapper mapper = new ObjectMapper();
			String errJson = mapper.writeValueAsString(dataMap);
			listOps.leftPush(redisErrKey, errJson);
			
			return;
//			throw new Exception("getShipmentListForOrder Failed!!!!!");
			
		}
		
		
		// shipmentNo 만큼 confirmShipment 수행
		XPath xp = XPathFactory.newInstance().newXPath();
//		NodeList shipmentList = (NodeList)xp.evaluate("/ShipmentList/Shipment", doc.getDocumentElement(), XPathConstants.NODESET);
		NodeList shipmentList = (NodeList)xp.evaluate("/ShipmentList/Shipment[starts-with(@Status,'1100') or starts-with(@Status,'1200') or starts-with(@Status,'1300')]", doc.getDocumentElement(), XPathConstants.NODESET);
		
		String confirmShipment_template = FileContentReader.readContent(getClass().getResourceAsStream(CONFIRM_SHIPMENT_TEMPLATE));
		inputXML = "";
		
		for(int i=0; i<shipmentList.getLength(); i++){
			
			
			String shipmentNo = (String)xp.evaluate("@ShipmentNo", shipmentList.item(i), XPathConstants.STRING);
			
			
			msg = new MessageFormat(confirmShipment_template);
			inputXML = msg.format(new String[] {docType, entCode, sellerCode, 
									shipmentNo, 
									shipNode, 	// 창고번호
									trackingNo,	// 송장번호: trailerNo
									aShipDate,  // 출고일시
									SCAC_CJL_STD    // TODO: 택배사코드 고정값사 CJL - Standard Logistic
							  } );
			logger.debug("##### [confirmShipment inputXML]"+inputXML); 
		
		
			// Call Confirm Shipment
			outputXML = sterlingApiDelegate.comApiCall(API_CONFIRM_SHIPMENT, inputXML);
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				
				logger.debug("Error Message:"+ outputXML);
				
				ObjectMapper mapper = new ObjectMapper();
				String errJson = mapper.writeValueAsString(dataMap);
				listOps.leftPush(redisErrKey, errJson);
				
				continue;
//				throw new Exception("ConfirmShipment Failed!!!!!");
				
			}
		}		
		
		logger.debug("##### ["+redisKey+"][cofirmShipment] End!!!");
		
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
	
		
		logger.debug("##### ["+redisKey+"][cancelOrder By Cube-Result] Started!!!");
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
		String docType = SC_ORDER_TYPE_SALES;
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
		
		
		// 성공/실패에 관계없이 주문취소결과 Result키에 기록
		HashMap<String, String> cancelResMap = new HashMap<String, String>();
		cancelResMap.put("orderNo", orderId);
		cancelResMap.put("enterPrise", entCode);
		cancelResMap.put("sellerOrg", sellCode);
		
		String statusTxt = "";
		String statusCls = "";
		if("01".equals(cubeStatus))
		{
			statusTxt = "성공";
			statusCls = "success";
		}
		else if("02".equals(cubeStatus)) // 큐브에서 이미 주문취소된 건이므로 OMC에서 주문취소 가능해야 함
		{
			statusTxt = "기처리건";
			statusCls = "success";
		}
		else if("09".equals(cubeStatus))
		{
			statusTxt = "실패";
			statusCls = "danger";
		}
		else if("90".equals(cubeStatus))
		{
			statusTxt = "출고확정건";
			statusCls = "warning";
		}
		
		cancelResMap.put("status_code", cubeStatus);
		cancelResMap.put("status_text", statusTxt);
		cancelResMap.put("status_class", statusCls);
		cancelResMap.put("cube_msg", cubeStatusMsg);
		
		ObjectMapper mapper = new ObjectMapper();
		String resJson = mapper.writeValueAsString(cancelResMap);
		
		String cancelResKey = entCode+":"+sellCode+":order:cancel:result";
		logger.debug("[9000 CanceReq Result key]"+cancelResKey);
		logger.debug("[9000 CanceReq Result Data]"+resJson);
		
		listOps.leftPush(cancelResKey, resJson);
		
		
		// 실패 또는 처리대상건 없음, 출고확정건, 기처리건 - 주문취소 수행하지 않고 리턴 (운영자판단)
		// TODO: 09인 경우 자체주문취소 처리필요
		if("09".equals(cubeStatus) || "90".equals(cubeStatus) || "02".equals(cubeStatus)){
			
			logger.debug("주문취소요청 실패[CanceOrder Request Result is failed]");
			return;
		}
		
		
		// 성공일 경우
		else if("01".equals(cubeStatus) ){
		
			// 1. 주문상태 확인 --> 주문취소 수행
			
			Document doc = sterlingApiDelegate.getOrderDetails(docType, entCode, orderId);
			String scStatus = doc.getDocumentElement().getAttribute("MaxOrderStatus");
			
			logger.debug("[maxStatus]"+scStatus);
			
				
			// 3350일 경우 cancelOrder전  changeShipment 수행 
			if("3350".equals(scStatus)){
				
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
				
				
				// changeShipment API Call - Cancel Shipment
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
		
		
		
		// 취소요청 RedisKey에 있는 데이타 삭제 - 취소요청 히스토리 관리를 위해 삭제하지 않음 
		// 조직코드:채널코드:order:cancel 
//		String cancelReqKey = entCode+":"+sellCode+":order:cancel";
//		
//		List<String> cancelReqRedisList = listOps.range(cancelReqKey, 0, -1);
//		for( int i=0; i<cancelReqRedisList.size(); i++){
//			
//			String jsonData = cancelReqRedisList.get(i);
//			HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
//			String cancelOrderNo = cancelReqMap.get("orderNo");
//			if(orderId.equals(cancelOrderNo)){
//				logger.debug("[cancelOrderNo]"+cancelOrderNo);
//				listOps.remove(cancelReqKey, i, jsonData);
//				break;
//			}
//		}
		
		logger.debug("##### ["+redisKey+"][[cancelOrder By Cube-Result]] End!!!");
	}
	
	
}

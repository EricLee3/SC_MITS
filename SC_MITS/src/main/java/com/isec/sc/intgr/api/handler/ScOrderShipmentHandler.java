package com.isec.sc.intgr.api.handler;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;

@Controller
@RequestMapping(value="/sc")
public class ScOrderShipmentHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderShipmentHandler.class);
	
	private static final String API_GET_SHIPMENT_DETAILS = "getShipmentDetails";
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	

	@Value("${sc.api.getShipmentDetails.template}")
	private String GET_SHIPMENT_DETAILS_TEMPLATE;
	
	
	/**
	 * Create Shipment 후처리 프로세스
	 * MA로 출고생성정보 전달. -> 출고준비중
	 * 
	 * @param returnXML
	 * @param res
	 * @throws Exception
	 */
	@RequestMapping(value = "/createShipmentPostProcess.do")
	public void createShipmentPostProcess(@RequestParam(required=false) String returnXML,								  
								  HttpServletResponse res) throws Exception{
		

		logger.debug("##### createShipmentPostProcess Started !!!");
		logger.info("[returnXML]"+returnXML);
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		String shipmentKey = el.getAttribute("ShipmentKey");
		String shipmentNo = el.getAttribute("ShipmentNo");
		String entCode = el.getAttribute("EnterpriseCode");
		String sellerCode = el.getAttribute("SellerOrganizationCode");
		String status = el.getAttribute("Status");
		String docType = el.getAttribute("DocumentType");
		
		String pushKeyToMa = entCode+":"+sellerCode+":order:update:S2M";
		String pushKeyToCa = entCode+":"+sellerCode+":order:update:S2C";
		
		logger.debug("[shipmentKey]"+shipmentKey);
		logger.debug("[shipmentNo]"+shipmentNo);
		logger.debug("[entCode]"+entCode);
		logger.debug("[sellerCode]"+sellerCode);
		logger.debug("[status]"+status);
		
		
		// 2. Shimpment OutPut 데이타 생성
		Map<String,Object> sendMsgMap = new HashMap<String,Object>();
		String outputMsg = "";
		
		/*
		 * {"orderId":"오더번호","status":"3350","orderHeaderKey":"오더헤더키","docType":"0001",”entCode":"SLV",
			”sellerCode”:”ASPB”, "OrderReleaseKey":"주문확정키", "ReleaseNo:"주문확정호번호","shipmentKey":"출고키", "shipmentNo":"출고번호"
			"confirmed":[ 
			    {"itemId":"상품코드","qty":"주문수량"},
			    {"itemId":"상품코드","qty":"주문수량"}
			] }
		 */
		sendMsgMap.put("status","3350"); // Include In Shipment
		sendMsgMap.put("docType",docType);
		sendMsgMap.put("entCode",entCode);
		sendMsgMap.put("sellerCode",sellerCode);
		sendMsgMap.put("shipmentKey",shipmentKey);
		sendMsgMap.put("shipmentNo",shipmentNo);
		
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		// ShipmentLine List 
		NodeList shipmentLineNode = (NodeList)xp.evaluate("/Shipment/ShipmentLines/ShipmentLine", el, XPathConstants.NODESET);
		
		// 오더번호, 릴리즈번호
		String orderHeaderKey = (String)xp.evaluate("@OrderHeaderKey", shipmentLineNode.item(0), XPathConstants.STRING);
		String orderNo = (String)xp.evaluate("@OrderNo", shipmentLineNode.item(0), XPathConstants.STRING);
		String orderReleaseKey = (String)xp.evaluate("@OrderReleaseKey", shipmentLineNode.item(0), XPathConstants.STRING);
		String releaseNo = (String)xp.evaluate("@ReleaseNo", shipmentLineNode.item(0), XPathConstants.STRING);
		
		sendMsgMap.put("orderHeaderKey",orderHeaderKey);
		sendMsgMap.put("orderId",orderNo);
		sendMsgMap.put("orderReleaseKey",orderReleaseKey);
		sendMsgMap.put("releaseNo",releaseNo);
		
		// Item정보
		List<HashMap<String,String>> itemList = new ArrayList<HashMap<String,String>>();
		for(int i=0; i<shipmentLineNode.getLength(); i++){
			
			HashMap<String, String> itemMap = new HashMap<String, String>();
			String itemId = (String)xp.evaluate("@ItemID", shipmentLineNode.item(i), XPathConstants.STRING);
			String qty = (String)xp.evaluate("@Quantity", shipmentLineNode.item(i), XPathConstants.STRING);
			
			itemMap.put("itemId", itemId);
			itemMap.put("qty", qty);
			
			itemList.add(itemMap);
		}
		sendMsgMap.put("confirmed",itemList);
		
		
		// 3. JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[pushKeyToMa]"+pushKeyToMa);
		logger.debug("[pushData]"+outputMsg);
		
		// 4. RedisDB에 메세지 저장
		listOps.leftPush(pushKeyToMa, outputMsg);
		
		logger.debug("##### createShipmentPostProcess End !!!");
		
		
		
		/*
		 * Cube로 부터 출고확정정보 
		 * 
		 * {
		 *  "orderId":"","status":"3700","orderHeaderKey":"","docType":"0001",”entCode":"SLV",
			”sellerCode”:”ASPB”, 
			"totalCharge":"총비용","expDeliveryDate":"배송예정일","currency":"통화",
			“shipment”:[ 
			 {
				"shipmentNo":"출고번호", "shipNode":"ISEC_WH1",
				"carrierCode":"배송업체코드", "carrierTitle":" 배송업체명", "trackingNo":"송장번호", 
				"items":[
						 {"itemId":" 상품코드",  "qty":" 수량 "} ,
						 {"itemId":" 상품코드 ",  "qty":" 수량 "}
						]
			 },
			 {
				"shipmentNo":"출고번호", "shipNode":"ISEC_WH1",
				"carrierCode":" 배송업체코드 ", "carrierTitle":" 배송업체명 ", 
				"trackingNo":"송장번호", 
				"items":[
						{"itemId":" 상품코드",  "qty":" 수량 "} ,
						 {"itemId":" 상품코드 ",  "qty":" 수량 "}
						]
			 }
			} 
		 */		
		// RedisKey for CUBE -> SC 
		// TODO: Test 용, 큐브연동후 삭제 할 것
		Map<String,Object> sendMsgMap_C2S = new HashMap<String,Object>();
		
		try{
		
			List<HashMap<String,Object>> shipmentList = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> lineMap = new HashMap<String, Object>();
			lineMap.put("shipmentNo", shipmentNo);
			lineMap.put("shipNode", "ISEC_WH1");
			lineMap.put("carrierCode", "19991214183438453");
			lineMap.put("trackingNo", "1111111111");;
			lineMap.put("items", sendMsgMap.get("confirmed"));
			
			shipmentList.add(lineMap);
			
			
			sendMsgMap_C2S = sendMsgMap;
			sendMsgMap_C2S.put("status", "3700"); // 3700
			sendMsgMap_C2S.put("shipment",shipmentList);
			sendMsgMap_C2S.remove("confirmed"); 
			
			
			String outputMsgTest = mapper.writeValueAsString(sendMsgMap_C2S);
			
			// TODO: Cube 사업부코드/매장코드 매핑필요
			String mapEntCode = entCode;
			String mapSellerCode = sellerCode;
			if("ASPB".equals(sellerCode)){
				mapEntCode = "80";
				mapSellerCode ="ON9999";
			}
			String pushKey_CubetoSC = mapEntCode+":"+mapSellerCode+":order:update:C2S";
			
			logger.debug("[pushKey_CubetoSC]"+pushKey_CubetoSC);
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey_CubetoSC, outputMsgTest);
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		// 5. 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			
		
	}
	
	
	/**
	 * Confirm Shipment 후처리 프로세스
	 * 
	 * confirmShipment 후처리: MA로 출고확정정보 전달.
	 * 
	 * 
			{"orderId":"","status":"3700","orderHeaderKey":"","docType":"0001",”entCode":"SLV",
				”sellerCode”:”ASPB”,
				"totalCharge":"총비용","expDeliveryDate":"배송예정일","currency":"통화",
				“shippment”:[ 
				{
				 "carrierCode":"배송업체코드", "carrierTitle":" 배송업체명", "trackingNo":"송장번호", 
				"items":[
				{"itemId":" 상품코드",  "qty":" 수량 "} ,
				 {"itemId":" 상품코드 ",  "qty":" 수량 "}
				]
				 },
				{
				 "carrierCode":" 배송업체코드 ", "carrierTitle":" 배송업체명 ", 
				"trackingNo":"송장번호", 
				"items":[
				{"itemId":" 상품코드",  "qty":" 수량 "} ,
				 {"itemId":" 상품코드 ",  "qty":" 수량 "}
				]
				 }
			}
			
			{"totalCharge":"0.00","expDeliveryDate":"2014-09-11T20:48:00+09:00","docType":"0001","status":"3700","shipment":[],"sellerCode":"ASPB","orderId":null,"currency":"KRW","entCode":"SLV"}

	 * 
	 * @param returnXML
	 * @param res
	 * @throws Exception
	 */
	@RequestMapping(value = "/confirmShipmentPostProcess.do")
	public void confirmShipmentPostProcess(@RequestParam(required=false) String returnXML,								  
								  HttpServletResponse res) throws Exception{
		
		
		logger.debug("##### confirmShipmentPostProcess started !!!");
		logger.info("[returnXML]"+returnXML);
		
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		String shipmentKey = el.getAttribute("ShipmentKey");
		String shipmentNo = el.getAttribute("ShipmentNo");
		String shipNode = el.getAttribute("ShipNode");
		
		String entCode = el.getAttribute("EnterpriseCode");
		String sellerCode = el.getAttribute("SellerOrganizationCode");
		String status = el.getAttribute("Status");
		String docType = el.getAttribute("DocumentType");
		
		String pushKeyToMa = entCode+":"+sellerCode+":order:update:S2M";
		
		// TODO: Cube 사업부코드/매장코드 매핑필요
		String entCode_cube = "80";
		String sellerCode_cube = "ON9999";
		String pushKeyToCa = entCode_cube+":"+sellerCode_cube+":order:update:S2C";
		
		logger.debug("[docType]" + docType);
		logger.debug("[shipmentKey]"+shipmentKey);
		logger.debug("[shipmentNo]"+shipmentNo);
		logger.debug("[entCode]"+entCode);
		logger.debug("[sellerCode]"+sellerCode);
		logger.debug("[status]"+status);
		
		
		// 2. Shimpment OutPut 데이타 생성
		Map<String,Object> sendMsgMap = new HashMap<String,Object>();
		String outputMsg = "";
		
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		// Shipment 기본정보 추출
		String currency = (String)xp.evaluate("@Currency", el, XPathConstants.STRING);
		logger.debug("[currency]" + currency);
		String expectedDeliveryDate = (String)xp.evaluate("@ExpectedDeliveryDate", el, XPathConstants.STRING);
		logger.debug("[expectedDeliveryDate]" + expectedDeliveryDate);
		String totalActualCharge = (String)xp.evaluate("@TotalActualCharge", el, XPathConstants.STRING);
		logger.debug("[totalActualCharge]" + totalActualCharge);
		
		String carrierTitle = (String)xp.evaluate("@SCAC", el, XPathConstants.STRING);
		logger.debug("[carrierTitle]" + carrierTitle);
		
		String trackingNo = (String)xp.evaluate("@TrackingNo", el, XPathConstants.STRING);
		logger.debug("[trackingNo]" + trackingNo);
		
		
		// Item List 추출
		NodeList shipmentItemNodeList = (NodeList)xp.evaluate("/Shipment/ShipmentLines/ShipmentLine", el, XPathConstants.NODESET);
		logger.debug("[shipmentItemNodeList]"+shipmentItemNodeList.getLength());
		
		List<HashMap<String,String>> itemList = new ArrayList<HashMap<String,String>>();
		for(int i=0; i<shipmentItemNodeList.getLength(); i++){
		
			HashMap<String,String> itemMap = new HashMap<String,String>();
			
			String itemId = (String)xp.evaluate("@ItemID", shipmentItemNodeList.item(i), XPathConstants.STRING);
			String qty = (String)xp.evaluate("@Quantity", shipmentItemNodeList.item(i), XPathConstants.STRING);
			
			itemMap.put("itemId", itemId);
			itemMap.put("qty", qty);
			
			itemList.add(itemMap);
		}
		
		// Shipment 저장
		// TODO: carrierCode 매핑필요
		HashMap<String,Object> shipmentMap = new HashMap<String,Object>();
		shipmentMap.put("shipmentKey",shipmentKey);
		shipmentMap.put("shipmentNo",shipmentNo);
		shipmentMap.put("carrierCode", carrierTitle);
		shipmentMap.put("carrierTitle", carrierTitle);
		shipmentMap.put("trackingNo", trackingNo);
		shipmentMap.put("items",itemList);
		
		// ShipmentList 저장
		List<HashMap<String,Object>> shipmentList = new ArrayList<HashMap<String,Object>>();
		shipmentList.add(shipmentMap);
		
		// Master Data 저장
		sendMsgMap.put("status","3700"); // Shipped
		
		// Call getShipmentDetails - 오더번호를 찾기 위해 호출함.
		String confirmShipment_template = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPMENT_DETAILS_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(confirmShipment_template);
		String inputXML = msg.format(new String[] {sellerCode, shipNode, shipmentNo} );
		logger.debug("##### [getShipmentDetails inputXML]"+inputXML); 
		
		String outputXML = sterlingApiDelegate.comApiCall(API_GET_SHIPMENT_DETAILS, inputXML);
		Document shipDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		String orderId = (String)xp.evaluate("/Shipment/ShipmentLines/ShipmentLine[1]/@OrderNo", shipDoc.getDocumentElement(), XPathConstants.STRING);
		logger.debug("[orderId]" + orderId);
		
		
		sendMsgMap.put("orderId",orderId); // order Id
		sendMsgMap.put("docType",docType);
		sendMsgMap.put("entCode",entCode);
		sendMsgMap.put("sellerCode",sellerCode);
		sendMsgMap.put("expDeliveryDate", expectedDeliveryDate);	// 배송예정일 - WCS에서 필요
		sendMsgMap.put("totalCharge", totalActualCharge);		// 총배송비용 - WCS에서 필요
		sendMsgMap.put("currency", currency);					// 통화 - WCS에서 필요
		
		sendMsgMap.put("shipment", shipmentList);	// 출고정보
		
		
		// Pack Container별 Item정보 추출(N건)
//		NodeList containerNodeList = (NodeList)xp.evaluate("/Shipment/Containers/Container", el, XPathConstants.NODESET);
//		logger.debug("[containerNodeList]"+containerNodeList.getLength());
//		
//		List<HashMap<String,Object>> containerList = new ArrayList<HashMap<String,Object>>();
//		String[] releaseKeys = new String[containerNodeList.getLength()];
//		
//
//		// TODO: Confirm Shipment 처리 변경필요
//		// Container List
//		for(int i=0; i<containerNodeList.getLength(); i++){
//			
//			HashMap<String,Object> containerMap = new HashMap<String,Object>();
//			
//			String carrierCode = (String)xp.evaluate("@SCAC", containerNodeList.item(i), XPathConstants.STRING);
//			String trackingNo = (String)xp.evaluate("@TrackingNo", containerNodeList.item(i), XPathConstants.STRING);
//			
//			logger.debug("---------------------------------------"+i);
//			logger.debug("[carrierCode]"+carrierCode);
//			logger.debug("[trackingNo]"+trackingNo);
//			
//			// TODO: 배송사코드 MA와 매핑필요
//			containerMap.put("carrierCode", entCode.equals("DA")?"custom":carrierCode);
//			containerMap.put("carrierTitle", carrierCode);
//			containerMap.put("trackingNo", trackingNo);
//			
//			// Item List 
//			NodeList itemNodeList = (NodeList)xp.evaluate("ContainerDetails/ContainerDetail", containerNodeList.item(i), XPathConstants.NODESET);
//			List<HashMap<String,String>> itemList = new ArrayList<HashMap<String,String>>();
//			for(int j=0; j<itemNodeList.getLength(); j++){
//				
//				
//				HashMap<String, String> itemMap = new HashMap<String, String>();
//				String itemId = (String)xp.evaluate("@ItemID", itemNodeList.item(j), XPathConstants.STRING);
//				String qty = (String)xp.evaluate("@Quantity", itemNodeList.item(j), XPathConstants.STRING);
//				
//				itemMap.put("itemId", itemId);
//				itemMap.put("qty", qty);
//				
//				itemList.add(itemMap);
//			}
//			
//			containerMap.put("items", itemList);
//			containerList.add(containerMap);
//		}
		
		
//		sendMsgMap.put("orderId", orderId);	// 오더번호
//		sendMsgMap.put("shipment", containerList);	// 출고정보
//		sendMsgMap.put("expDeliveryDate", expectedDeliveryDate);	// 배송예정일
//		sendMsgMap.put("totalCharge", totalActualCharge);	// 총배송비용
//		sendMsgMap.put("currency", currency);	// 통화
		
		
		// 3. JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[pushKeyToMa]"+pushKeyToMa);
		logger.debug("[pushData]"+outputMsg);
		
		// 4. RedisDB에 메세지 저장
		listOps.leftPush(pushKeyToMa, outputMsg);
		
		
		logger.debug("##### confirmShipmentPostProcess end !!!");
		
		// 5. 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		
	}
	
	
}

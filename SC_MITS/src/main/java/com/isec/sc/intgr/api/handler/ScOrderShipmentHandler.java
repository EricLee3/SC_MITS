package com.isec.sc.intgr.api.handler;

import java.io.ByteArrayInputStream;
import java.io.Reader;
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

@Controller
@RequestMapping(value="/sc")
public class ScOrderShipmentHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderShipmentHandler.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	

	
	@RequestMapping(value = "/shipment.do")
	public void shipmentProcess(@RequestParam(required=false) String returnXML,
								  @RequestParam(required=false) String status,	
								  HttpServletResponse res) throws Exception{
		
		
		
		logger.info("[returnXML]"+returnXML);
		logger.info("[status]"+status);
		
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		String shipmentKey = el.getAttribute("ShipmentKey");
		String shipmentNo = el.getAttribute("ShipmentNo");
		
		String entCode = el.getAttribute("EnterpriseCode");
		String sellerCode = el.getAttribute("SellerOrganizationCode");
		
		String pushKey = entCode+":"+sellerCode+":order:update:S2M";
		logger.debug("[pushKey]"+pushKey);
		
		
		logger.debug("[shipmentKey]"+shipmentKey);
		logger.debug("[shipmentNo]"+shipmentNo);
		logger.debug("[entCode]"+entCode);
		logger.debug("[sellerCode]"+sellerCode);
		
		
		
		// 2. Shimpment OutPut 데이타 생성
		Map<String,Object> sendMsgMap = new HashMap<String,Object>();
		String outputMsg = "";
		
		
		// Shipment Created
		if("1100".equals(status)){
			
			// TODO: 
			
		
		// Shipment Confirmed
		}else if("1400".equals(status)){
			
			// Shipment 기본정보 추출
			String currency = (String)xp.evaluate("@Currency", el, XPathConstants.STRING);
			logger.debug("[currency]" + currency);
			
			String expectedDeliveryDate = (String)xp.evaluate("@ExpectedDeliveryDate", el, XPathConstants.STRING);
			logger.debug("[expectedDeliveryDate]" + expectedDeliveryDate);
			
			String totalActualCharge = (String)xp.evaluate("@TotalActualCharge", el, XPathConstants.STRING);
			logger.debug("[totalActualCharge]" + totalActualCharge);
			
			
			
			// Pack Container별 Item정보 추출(N건)
			NodeList containerNodeList = (NodeList)xp.evaluate("/Shipment/Containers/Container", el, XPathConstants.NODESET);
			logger.debug("[containerNodeList]"+containerNodeList.getLength());
			
			List<HashMap<String,Object>> containerList = new ArrayList<HashMap<String,Object>>();
			String[] releaseKeys = new String[containerNodeList.getLength()];
			
			// Container List
			for(int i=0; i<containerNodeList.getLength(); i++){
				
				
				HashMap<String,Object> containerMap = new HashMap<String,Object>();
				
				
				String carrierCode = (String)xp.evaluate("@SCAC", containerNodeList.item(i), XPathConstants.STRING);
				String trackingNo = (String)xp.evaluate("@TrackingNo", containerNodeList.item(i), XPathConstants.STRING);
				
				logger.debug("---------------------------------------"+i);
				logger.debug("[carrierCode]"+carrierCode);
				logger.debug("[trackingNo]"+trackingNo);
				
				
				// TODO: 배송사코드 MA와 매핑필요
				containerMap.put("carrierCode", entCode.equals("DA")?"custom":carrierCode);
				containerMap.put("carrierTitle", carrierCode);
				containerMap.put("trackingNo", trackingNo);
				
				
				// Item List 
				NodeList itemNodeList = (NodeList)xp.evaluate("ContainerDetails/ContainerDetail", containerNodeList.item(i), XPathConstants.NODESET);
				List<HashMap<String,String>> itemList = new ArrayList<HashMap<String,String>>();
				for(int j=0; j<itemNodeList.getLength(); j++){
					
					
					HashMap<String, String> itemMap = new HashMap<String, String>();
					String itemId = (String)xp.evaluate("@ItemID", itemNodeList.item(j), XPathConstants.STRING);
					String qty = (String)xp.evaluate("@Quantity", itemNodeList.item(j), XPathConstants.STRING);
					
					itemMap.put("itemId", itemId);
					itemMap.put("qty", qty);
					
					logger.debug("---------------------------------------"+i);
					logger.debug("[itemId]"+itemId);
					logger.debug("[qty]"+qty);
					
					itemList.add(itemMap);
				}
				
				containerMap.put("items", itemList);
				
				
				containerList.add(containerMap);
			}
			
			
			String orderId = valueOps.get(shipmentNo);
			logger.debug("[orderId]"+orderId);
			
			sendMsgMap.put("orderId", orderId);	// 오더번호
			sendMsgMap.put("shipment", containerList);	// 출고정보
			sendMsgMap.put("status", "3700"); // 오더상태 (Shipped)
			
			sendMsgMap.put("expDeliveryDate", expectedDeliveryDate);	// 배송예정일
			sendMsgMap.put("totalCharge", totalActualCharge);	// 총배송비용
			sendMsgMap.put("currency", currency);	// 통화
			
			
		}else{
			
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		
		
		
		// 3. JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputMsg]"+outputMsg);
		
		// 4. RedisDB에 메세지 저장
		listOps.leftPush(pushKey, outputMsg);
		
		
		// 5. 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		
		
	}
	
	
}

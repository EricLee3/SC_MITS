package com.isec.sc.intgr.http;

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
public class ScOrderShipmentController {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderShipmentController.class);
	
	
	@Autowired	private StringRedisTemplate mgtStringRedisTemplate;
	@Autowired	private StringRedisTemplate wcsStringRedisTemplate;
	
	
	
	
	@Resource(name="mgtStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="mgtStringRedisTemplate")
	private ValueOperations<String, String> valueOps;
	
	@Resource(name="wcsStringRedisTemplate")
	private ListOperations<String, String> wcsListOps;
	
	@Resource(name="wcsStringRedisTemplate")
	private ValueOperations<String, String> wcsValueOps;
	
	
	
	@Value("${redis.magento.key.orderUpdate.S2M}")
	private String redis_M_key_orderUpdate_S2M;
	
	@Value("${redis.wcs.key.orderUpdate.S2M}")
	private String redis_W_key_orderUpdate_S2M;
	
	
	
	@RequestMapping(value = "/shipment")
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
		
		
		logger.debug("[shipmentKey]"+shipmentKey);
		logger.debug("[shipmentNo]"+shipmentNo);
		
		
		// 2. Shimpment OutPut 데이타 생성
		Map<String,Object> sendMsgMap = new HashMap<String,Object>();
		String outputMsg = "";
		
		
		// Shipment Created
		if("created".equals(status)){
			
			// TODO: 
			
		
		// Shipment Confirmed
		}else if("confirmed".equals(status)){
			
			
			// Released가 된 Item정보 추출
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
				
				
				containerMap.put("carrierCode", "custom");
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
			
		
		}else{
			
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		
		
		
		// 3. JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputMsg]"+outputMsg);
		logger.debug("[redis_M_key_orderUpdate_S2M]"+redis_M_key_orderUpdate_S2M);
		
		// 4. RedisDB에 메세지 저장
		listOps.leftPush("com:scteam:magento:orderUpdate:S2M", outputMsg);
		
		
		// 5. 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		
		
	}
	
	
}

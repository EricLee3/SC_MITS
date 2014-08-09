package com.isec.sc.intgr.api.handler;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ScOrderStatusHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderStatusHandler.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;	
	
	
	
	@RequestMapping(value = "/orderUpdate.do")
	public void updateOrderStatus(@RequestParam(required=false) String returnXML,
								  @RequestParam(required=false) String status,	
								  HttpServletResponse res) throws Exception{
		
		
		
		logger.info("[returnXML]"+returnXML);
		logger.info("[status]"+status);
		
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element outputXML = doc.getDocumentElement();
		
		String orderKey = outputXML.getAttribute("OrderHeaderKey");
		
		String orderNo = outputXML.getAttribute("OrderNo");
		String entCode = outputXML.getAttribute("EnterpriseCode");
		String docType = outputXML.getAttribute("DocumentType");
		
		String orderStatus = outputXML.getAttribute("Status");
		String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 코드값
		String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 코드값

		
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");	// 판매조직코드
		String pushKey = entCode+":"+sellerCode+":order:update:S2M";
		
		logger.debug("#####[pushKey]"+pushKey);
		logger.debug("#####[orderKey]"+orderKey);
		logger.debug("#####[orderNo]"+orderNo);
		logger.debug("#####[entCode]"+entCode);
		logger.debug("#####[orderStatus]"+orderStatus);
		logger.debug("#####[maxOrderStatus]"+maxOrderStatus);
		logger.debug("#####[minOrderStatus]"+minOrderStatus);
		
		
		// 2. OutPut 기초데이타 생성
		HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
		sendMsgMap.put("orderHeaderKey", orderKey);
		sendMsgMap.put("docType", docType);
		
		sendMsgMap.put("orderId", orderNo);
		sendMsgMap.put("entCode", entCode);
		sendMsgMap.put("sellerCode", sellerCode);
		sendMsgMap.put("status", maxOrderStatus);
		
		
		
		if("Scheduled".equals(orderStatus) || "Partially Scheduled".equals(orderStatus))
		{
			
			updateScheduleStauts(outputXML, sendMsgMap, pushKey);
		
		}
		else if("Released".equals(orderStatus) || "Partially Released".equals(orderStatus) )
		{
			
			updateReleaseStauts(outputXML, sendMsgMap, pushKey);
		
		}
		else if("Cancelled".equals(orderStatus))
		{
			
			updateCancelStauts(outputXML, sendMsgMap, pushKey);
		}
		
		
		
		// 5. 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		
		
	}
	
	
	
	/**
	 *  Scheduled or Partially Scheduled
	 *  - 이 단계에서는 부분스케쥴이 되더라도 Magento로는 해당 오더건에 대해 전체 스케쥴상태로 보낸다.
	 *   ( Magento의 상태가 Order Placed상태 즉, 인보이스 생성전에는 Magento에서 부분취소가 불가하기 때문)
	 *  - Release단계에서 출고가 가능한 상품정보를 보내면 Magento는 해당건에 대해서만 인보이스를 발행하고 나머지 상품은 Cancel처리한다. 

	 * @param outputXML
	 * @param sendMsgMap
	 */
	private void updateScheduleStauts(Element outputXML, HashMap<String, Object> sendMsgMap, String pushKey) throws Exception{
	
		// Output XML Parsing
		// TODO: 현재 WCS채널인 경우에만 스케츌단계 연동제외처리. 향후 모든 채널에 적용
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");
		if("JNS".equals(sellerCode)){
			return;
		}
		
		// Send Messgage Setting
		sendMsgMap.put("canceled", "false");
		
		
		// JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputKey]"+pushKey);
		logger.debug("[outputMsg]"+outputMsg);
		
		
		// RedisDB에 메세지 저장
		listOps.leftPush(pushKey, outputMsg);
		
	}
	
	
	/**
	 * Released or Partially Released
	 * 출고가 가능한 상품정보를 추출해서 Magento로 전송
	 * 
	 * @param outputXML
	 * @param sendMsgMap
	 */
	private void updateReleaseStauts(Element outputXML, HashMap<String, Object> sendMsgMap, String pushKey) throws Exception{
		
		// Output XML Parsing
		XPath xp = XPathFactory.newInstance().newXPath();
		
		/*
		 * TODO: Backordered 된 건 처리 
		 * - WCS(MI:JNS)의 경우는 부분취소가 현재 불가능함
		 * - 따라서 Released단계에서  BackOrdered 또는 Canceled 된 건이 하나라도 존재할 경우
		 *   Released결과를 보내주지 않고 Skip처리함. Sterling에서 전체취소처리후 취소정보를 전달해야 함.  
		 */
		NodeList canceledList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@Status='BackOrdered' or @Status='Cancelled']", outputXML, XPathConstants.NODESET);
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");
		if("JNS".equals(sellerCode)){
			if(canceledList.getLength() > 0){
				logger.debug("This Order cannot be fulFilled!!");
				return;
			}
		}
		
		
		// 정상 Released가 된 Item정보 추출
		NodeList releaseOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@Status='Released']", outputXML, XPathConstants.NODESET);
		logger.debug("[releaseOrderLineList]"+releaseOrderLineList.getLength());
		
		
		List<HashMap<String,String>> confirmList = new ArrayList<HashMap<String,String>>();	// 정상 Released된 상품리스트
		Set<String> releaseKeys = new HashSet<String>();	// 생성된 Release Key 정보
		
		for(int i=0; i<releaseOrderLineList.getLength(); i++){
			
			String orderedQty = (String)xp.evaluate("@OrderedQty", releaseOrderLineList.item(i), XPathConstants.STRING);
			String itemID = (String)xp.evaluate("Item/@ItemID", releaseOrderLineList.item(i), XPathConstants.STRING);
			String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus/@OrderReleaseKey", releaseOrderLineList.item(i), XPathConstants.STRING);
			
			logger.debug("---------------------------------------"+i);
			logger.debug("[itemID]"+itemID);
			logger.debug("[orderedQty]"+orderedQty);
			logger.debug("[orderReleaseKey]"+orderReleaseKey);
			
			HashMap<String, String> confirmItemMap = new HashMap<String, String>();
			confirmItemMap.put("itemId", itemID);
			confirmItemMap.put("qty", orderedQty);
			confirmList.add(confirmItemMap);
			
			
			// 중복된 ReleaseKey는 담지 않는다.
			releaseKeys.add(orderReleaseKey);
		}
		
		// Send Message Setting
		sendMsgMap.put("confirmed", confirmList);	// 출고가능한 상품정보
		sendMsgMap.put("releaseKeys", releaseKeys);	// Release Key 
		
		
		// JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputKey]"+pushKey);
		logger.debug("[outputMsg]"+outputMsg);
		
		// RedisDB에 메세지 저장
		listOps.leftPush(pushKey, outputMsg);
				
	}
	
	
	/**
	 * 전체 취소정보 저장
	 *  - 전체취소는 반드시 Release단계 전에 전송되어야 한다.
	 *  
	 * 
	 * @param outputXML
	 * @param sendMsgMap
	 * @param pushKey
	 * @throws Exception
	 */
	private void updateCancelStauts(Element outputXML, HashMap<String, Object> sendMsgMap, String pushKey) throws Exception{
		
		
		// Output XML Parsing
		
		
		// Send Message Setting
		sendMsgMap.put("canceled", "true");
		
		
		// JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputKey]"+pushKey);
		logger.debug("[outputMsg]"+outputMsg);
		
		
		// RedisDB에 메세지 저장
		listOps.leftPush(pushKey, outputMsg);
	}
}

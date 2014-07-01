package com.isec.sc.intgr.http;

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
public class ScOrderStatusController {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderStatusController.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private StringRedisTemplate wcsStringRedisTemplate;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;	
	
	
	@RequestMapping(value = "/orderUpdate")
	public void updateOrderStatus(@RequestParam(required=false) String returnXML,
								  @RequestParam(required=false) String status,	
								  HttpServletResponse res) throws Exception{
		
		
		
		logger.info("[returnXML]"+returnXML);
		logger.info("[status]"+status);
		
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		
		
		String orderKey = el.getAttribute("OrderHeaderKey");
		
		String orderNo = el.getAttribute("OrderNo");
		String entCode = el.getAttribute("EnterpriseCode");
		String docType = el.getAttribute("DocumentType");
		
		String orderStatus = el.getAttribute("Status");
		String maxOrderStatus = el.getAttribute("MaxOrderStatus"); // 코드값
		String minOrderStatus = el.getAttribute("MinOrderStatus");	// 코드값
		
		String sellOrgCode = el.getAttribute("SellerOrganizationCode");	// 판매조직코드
		
		String pushKey = entCode+":"+sellOrgCode+":order:update:S2M";
		logger.debug("[pushKey]"+pushKey);
		
		logger.debug("[orderKey]"+orderKey);
		logger.debug("[orderNo]"+orderNo);
		logger.debug("[entCode]"+entCode);
		logger.debug("[orderStatus]"+orderStatus);
		logger.debug("[maxOrderStatus]"+maxOrderStatus);
		logger.debug("[minOrderStatus]"+minOrderStatus);
		
		
		
		// 2. 오더상태별 OutPut 데이타 생성
		Map<String,Object> sendMsgMap = new HashMap<String,Object>();
		sendMsgMap.put("orderId", orderNo);
		
		// SC 필수항목 
		sendMsgMap.put("orderHeaderKey", orderKey);
		sendMsgMap.put("entCode", entCode);
		sendMsgMap.put("docType", docType);
		
		
		String outputMsg = "";
		
		
		// 오더상태에 따른 Magento Push Data 셋업
		
		
		/*
		 *  Scheduled or Partially Scheduled
		 *  - 이 단계에서는 부분스케쥴이 되더라도 Magento로는 해당 오더건에 대해 전체 스케쥴상태로 보낸다.
		 *   ( Magento의 상태가 Order Placed상태 즉, 인보이스 생성전에는 Magento에서 부분취소가 불가하기 때문)
		 *  - Release단계에서 출고가 가능한 상품정보를 보내면 Magento는 해당건에 대해서만 인보이스를 발행하고 나머지 상품은 Cancel처리한다. 
		 */
		
		if("Scheduled".equals(orderStatus) || "Partially Scheduled".equals(orderStatus)){
			
			sendMsgMap.put("status", maxOrderStatus);
			sendMsgMap.put("canceled", "false");
			
			
		/*
		 * Released or Partially Released
		 * 
		 * 출고가 가능한 상품정보를 추출해서 Magento로 전송
		 */
		}else if("Released".equals(orderStatus) || "Partially Released".equals(orderStatus) ){
			
			// TODO: Backordered 된 건 처리
			NodeList backOrderedList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine", el, XPathConstants.NODESET);
			
			
			// Released가 된 Item정보 추출
			NodeList releaseOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@Status='Released']", el, XPathConstants.NODESET);
			logger.debug("[releaseOrderLineList]"+releaseOrderLineList.getLength());
			
			List<HashMap<String,String>> confirmList = new ArrayList<HashMap<String,String>>();
			
			Set<String> releaseKeys = new HashSet<String>();
			
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
			
			
			sendMsgMap.put("confirmed", confirmList);	// 출고가능한 상품정보
			sendMsgMap.put("releaseKeys", releaseKeys);	// Release Key 
			sendMsgMap.put("status", maxOrderStatus);	
			
		/*
		 *  Canceled (전체 취소일 경우만 처리)
		 *  - 오더라인 개별 취소처리는 제외
		 */
		}else if("Cancelled".equals(orderStatus)){
			
			sendMsgMap.put("canceled", "true");
			sendMsgMap.put("status", maxOrderStatus);
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

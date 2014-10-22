package com.isec.sc.intgr.api.delegate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;





@Component
public class SterlingApiDelegate {
	
	private static final Logger logger = LoggerFactory.getLogger(SterlingApiDelegate.class);
	
	
	@Autowired private SterlingHTTPConnector sterlingHTTPConnector;
	 
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Value("${sc.api.item.manage}")
	private String sc_item_manage;
	
	@Value("${sc.api.order.create}")
	private String sc_order_create;
	
	@Value("${sc.api.order.release}")
	private String sc_order_release;
	
	@Value("${sc.api.shipment.createShipment}")
	private String sc_shipment_create;
	
	@Value("${sc.api.order.details}")
	private String sc_order_details;
	
	@Value("${sc.api.order.releaseList}")
	private String sc_get_orderReleaseList;
	
	
	
	@Value("${sc.api.releaseOrder.template}")
	private String releaseOrder_template;
	
	@Value("${sc.api.createShipment.template}")
	private String createShipment_template;
	
	
	@Value("${sc.api.getOrderReleaseList.template}")
	private String getOrderReleaseList_template;
	
	
	@Value("${sc.api.getPage.template}")
	private String getPage_template;
	
	
	@Value("${sc.api.getShipNodeInventory.template}")
	private String GET_SHIPNODE_TEMPLATE;
	
	
	public SterlingApiDelegate() {
		
	}
	
	/**
	 * Calling Sterling API  
	 *  - API Name, Input XML 인자로 Sterling의  API를 호출하고 결과 XML을 
	 *    반환하는 메서드
	 * 
	 * @param apiName SC API Name
	 * @param inputXML API Input XML
	 * @return Output XML
	 * @throws Exception
	 */
	public String comApiCall(String apiName, String inputXML) throws Exception{
		
		//logger.debug("#####["+apiName+"][input]"+inputXML);
		
		sterlingHTTPConnector.setApi(apiName);
		sterlingHTTPConnector.setData(inputXML);
		
		String outputXML = sterlingHTTPConnector.run();
		//logger.debug("#####["+apiName+"][output]"+outputXML);
		
		return outputXML;
	}
	
	
	/**
	 *  Sterling List Type의 API에 대한 Paging 처리
	 *  
	 * @param pageNumber 현재 페이지번호
	 * @param pageSize 페이지에 표시할 ROW 개수
	 * @param callApi API Name
	 * @param inputXML API Input XML
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getPageApi(int pageNumber, int pageSize, String callApi, String inputXML) {
		
		String template = FileContentReader.readContent(getClass().getResourceAsStream(getPage_template));
		
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {String.valueOf(pageNumber), String.valueOf(pageSize), callApi, inputXML} );
		logger.debug("[getPage intputXML]"+xmlData);
		
		
		Document doc = null;
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		
		try{
			
			sterlingHTTPConnector.setApi("getPage");
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
//			logger.debug("[getPage outputXML]"+outputXML);
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			Element el = doc.getDocumentElement();
			
			XPath xp = XPathFactory.newInstance().newXPath();
			
			returnMap.put("isFirstPage", (String)xp.evaluate("@IsFirstPage", el, XPathConstants.STRING));
			returnMap.put("isLastPage", (String)xp.evaluate("@IsLastPage", el, XPathConstants.STRING));
			returnMap.put("isValidPage", (String)xp.evaluate("@IsValidPage", el, XPathConstants.STRING));
			returnMap.put("pageNumber", (String)xp.evaluate("@PageNumber", el, XPathConstants.STRING));
			returnMap.put("pageSetToken", (String)xp.evaluate("@PageSetToken", el, XPathConstants.STRING));
			
			returnMap.put("output", (Node)xp.evaluate("/Page/Output", el, XPathConstants.NODE));
			
			
		}catch(Exception e){
			e.printStackTrace(); 
		}
		
		return returnMap;
	}
	
	
	public String createOrder(String xmlData) throws Exception{
		
		logger.debug("[createOrder intputXML]"+xmlData);
		
		String outputXML = "";
		
		try {
			
			sterlingHTTPConnector.setApi(sc_order_create);
			sterlingHTTPConnector.setData(xmlData);
			
			outputXML = sterlingHTTPConnector.run();
			logger.debug("[CreateOrder outputXML]"+outputXML);
			
			
		} catch (SAXException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		}
		
		return outputXML;
		
	}
	
	/**
	 * 상품등록 
	 * 
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> manageItem(String xmlData) throws Exception{
		
		
		Document doc = null;
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		XPath xp = XPathFactory.newInstance().newXPath();

		try {
			
			sterlingHTTPConnector.setApi(sc_item_manage);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[manageItem outputXML]"+outputXML);
			
			// output string Parsing
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			
			// Login Failed가 발생하면 재처리 시도
			if("errors".equals(doc.getFirstChild().getNodeName())){
				String errType = (String)xp.evaluate("error/@type", doc.getDocumentElement(), XPathConstants.STRING);
				if("Login".equals(errType)){
					logger.debug("Login Failed!!!");
					resultMap.put("succ", "90");
					return resultMap;
				}
			}
			
			
			// Error 처리
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				
				
				
//				String errCode = (String)xp.evaluate("Error/@ErrorCode", doc.getDocumentElement(), XPathConstants.STRING);
				String errDesc = (String)xp.evaluate("Error/@ErrorDescription", doc.getDocumentElement(), XPathConstants.STRING);
				String orgCode = (String)xp.evaluate("Error/Attribute[Name='OrganizationCode']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
				String uom = (String)xp.evaluate("Error/Attribute[Name='UnitOfMeasure']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
				String itemId = (String)xp.evaluate("Error/Attribute[Name='ItemID']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
				
				resultMap.put("succ", "99");
				resultMap.put("input_xml", xmlData);
				resultMap.put("err_desc", errDesc);
				resultMap.put("org_code", orgCode);
				resultMap.put("uom", uom);
				resultMap.put("item_id", itemId);
				resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
				
				return resultMap;
				
			}else{
				resultMap.put("succ", "01");
			}
			
		} catch (SAXException e) {
			resultMap.put("succ", "99");
			resultMap.put("input_xml", xmlData);
			resultMap.put("err_desc", "SAXException - XML 형식 에러발생");
			resultMap.put("uom", "");
			resultMap.put("item_id", "");
			resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
			
			e.printStackTrace();
		} catch (IOException e) {
			resultMap.put("succ", "99");
			resultMap.put("input_xml", xmlData);
			resultMap.put("err_desc", "IOException - 네트워크 에러발생");
			resultMap.put("uom", "");
			resultMap.put("item_id", "");
			resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
			
			
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			resultMap.put("succ", "99");
			resultMap.put("input_xml", xmlData);
			resultMap.put("err_desc", "ParserConfigurationException");
			resultMap.put("uom", "");
			resultMap.put("item_id", "");
			resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
			
			e.printStackTrace();
		}
		
		return resultMap;
		
	}
	
	/**
	 * 주문확정처리
	 * 
	 * @param xmlData
	 * @return
	 * @throws Exception
	 */
	public String releaseOrder(String xmlData) throws Exception{
		
		logger.debug("[releaseOrder intputXML]"+xmlData);
		
		String result = "1";
		Document doc = null;

		try {
			
			sterlingHTTPConnector.setApi(sc_order_release);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[releaseOrder outputXML]"+outputXML);
			
			// output string Parsing
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			// Error 처리
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			if("Erros".equals(doc.getFirstChild().getNodeName())){
				result = "0";
			}
			
		} catch (SAXException e) {
			result = "0";
			e.printStackTrace();
		} catch (IOException e) {
			result = "0";
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			result = "0";
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	/**
	 * 출고생성
	 * @param shipmentNo ""경우 자동생성
	 * @param releaseKey 주문릴리즈 키
	 * @param cubeShipmentNo 큐브 전표번호
	 * @return
	 * @throws Exception
	 */
	public int createShipment(String shipmentNo, String releaseKey, String cubeShipmentNo) throws Exception{
		
		
		// Generate SC API Input XML	
		String template = FileContentReader.readContent(getClass().getResourceAsStream(createShipment_template));
		
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {shipmentNo, releaseKey, cubeShipmentNo} );
		
		logger.debug("[createShipment intputXML]"+xmlData);
		
		Document doc = null;
		int result = 0;
		
		try {
			
			sterlingHTTPConnector.setApi(sc_shipment_create);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[createShipment outputXML]"+outputXML);
			
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			// Error 처리
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			if("Erros".equals(doc.getFirstChild().getNodeName())){
			}else{
				result = 1;
			}
			
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	
	/**
	 * 오더의 ReleaseNo 정보 조회
	 * 
	 * @param orderId
	 * @param releaseKey
	 * @return releaseKeyList
	 * @throws Exception
	 */
	public ArrayList<String> getOrderReleaseList(String orderId, String orderReleaseKey) throws Exception{
		
		
		// Generate SC API Input XML	
		String template = FileContentReader.readContent(getClass().getResourceAsStream(getOrderReleaseList_template));
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {orderId, orderReleaseKey} );
		logger.debug("[getOrderReleaseList intputXML]"+xmlData);
		
		
		// OutPut Variable
		Document doc = null;
		ArrayList<String> releaseKeyList = new ArrayList<String>();
		
		try {
			
			// SC API Call
			sterlingHTTPConnector.setApi(sc_get_orderReleaseList);
			sterlingHTTPConnector.setData(xmlData);
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[getOrderReleaseList outputXML]"+outputXML);
			
			// OutPut XML Parsing
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			
			
			// Error 발생
			if("Errors".equals(doc.getFirstChild().getNodeName())){

				// 상세예외처리 필요
				new HashMap<String, String>().put("status", "0000");
				
			}else{
				
				Element ele = doc.getDocumentElement();
				
				XPath xp = XPathFactory.newInstance().newXPath();
				NodeList orderReleaseNodeList = (NodeList)xp.evaluate("/OrderReleaseList/OrderRelease", ele, XPathConstants.NODESET);
				
				for(int i=0; i<orderReleaseNodeList.getLength(); i++){
					String releaseKey = (String)xp.evaluate("@OrderReleaseKey", orderReleaseNodeList.item(i), XPathConstants.STRING);
//					String releaseNo = (String)xp.evaluate("@ReleaseNo", orderReleaseNodeList.item(i), XPathConstants.STRING);
					logger.debug("releaseKey:::"+releaseKey);
					
					releaseKeyList.add(releaseKey);
//					releaseKeyList.add(releaseNo);
				}
			}
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return releaseKeyList;
		
	}
	
	/**
	 * 릴리즈키로 선적노드 조회
	 * 
	 * @param orderId
	 * @param orderReleaseKey
	 * @return ShipNode
	 * @throws Exception
	 */
	public String getShipNodeByReleaseKey(String orderId, String orderReleaseKey) throws Exception{
		
		
		// Generate SC API Input XML	
		String template = FileContentReader.readContent(getClass().getResourceAsStream(getOrderReleaseList_template));
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {orderId, orderReleaseKey} );
		logger.debug("[getOrderReleaseList intputXML]"+xmlData);
		
		
		String shipNode = "";
		// SC API Call
		sterlingHTTPConnector.setApi(sc_get_orderReleaseList);
		sterlingHTTPConnector.setData(xmlData);
		String outputXML = sterlingHTTPConnector.run();
		logger.debug("[getOrderReleaseList outputXML]"+outputXML);
		
		// OutPut XML Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		logger.debug("result:::"+doc.getFirstChild().getNodeName());
		
		
		// Error 발생
		if("Errors".equals(doc.getFirstChild().getNodeName())){

			// TODO: 상세예외처리 필요
			throw new Exception();
			
		}else{
			
			Element ele = doc.getDocumentElement();
			
			XPath xp = XPathFactory.newInstance().newXPath();
			Node releaseNode = (Node)xp.evaluate("/OrderReleaseList/OrderRelease", ele, XPathConstants.NODE);
			if(releaseNode == null) return "";
			
			shipNode = (String)xp.evaluate("@ShipNode", releaseNode, XPathConstants.STRING);
			if(shipNode == null) shipNode = "";
		}
			
		return shipNode;
		
		
	}
	
	/**
	 * CreateOrder API 호출시 에러처리
	 * 
	 * @param inputXML 
	 * @param outputXML
	 * @param redisErrKey 에러메세지 저장 키
	 * @return
	 * @throws Exception
	 */
	public Document processCreateOrderError(String inputXML, String outputXML, String redisErrKey) throws Exception{
		
		Document inputDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(inputXML.getBytes("UTF-8")));
		Document outputDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
		boolean isError = "Errors".equals(outputDoc.getFirstChild().getNodeName());
		logger.debug("First Node Name:::"+outputDoc.getFirstChild().getNodeName());
		
		/*
		 * <Errors>
			    <!-- One or more Error Elements -->
			    <Error ErrorCode="" ErrorDescription="" ErrorRelatedMoreInfo="">
			        <!-- One or More Error attribute elements -->
			        <Attribute Name="" Value=""/>
			        <Error ErrorCode="" ErrorDescription="" ErrorRelatedMoreInfo="">
			            <!-- One or More Error attribute elements -->
			            <Attribute Name="" Value=""/>
			            <!-- The stack trace as a text node -->
			            <Stack/>
			        </Error>
			        <!-- The stack trace as a text node -->
			        <Stack/>
			    </Error>
			</Errors>
		 */
		
		
		// Error 발생
		if(isError){
			
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			
			XPath xp = XPathFactory.newInstance().newXPath();
			String orderNo = (String)xp.evaluate("@OrderNo", inputDoc.getDocumentElement(), XPathConstants.STRING);
			String errCode = (String)xp.evaluate("Error/@ErrorCode", outputDoc.getDocumentElement(), XPathConstants.STRING);
			String errDesc = (String)xp.evaluate("Error/@ErrorDescription", outputDoc.getDocumentElement(), XPathConstants.STRING);
			
			logger.debug("[Api Call Error occured]"+outputXML);
			
			// TODO: 상세예외처리 필요
			resultMap.put("orderNo", orderNo);
			resultMap.put("input_xml", inputXML);
			resultMap.put("err_code", errCode);
			resultMap.put("err_desc", errDesc);
			resultMap.put("err_xml", inputXML);
			resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
			
			// Java Object(Map) to JSON	
			String orderErrJSON = mapper.writeValueAsString(resultMap);
			logger.debug("[orderErr JSON]"+orderErrJSON);
			
			// 에러데이타 Redis 저장
			listOps.leftPush(redisErrKey, orderErrJSON);
			
			return null;
			
		}else{
			return outputDoc;
		}
		
	}

	/**
	 * SC의 adjustInventory 호출전 해당상품의 재고수량을 제공하는 메세드
	 * 
	 * CA와의 재고연동시 현 재고수량(공급수량) 필요
	 * MA와의 재고연동시 가용재고 필요
	 * --> qty_type인자로 구분 A:가용재고, S:현재고(공급수량)
	 * --> 현재 가용재고는 allocated 상태의 건만 지시수량으로 계산
	 *  getShipNodeInventory API outputXML
	 *  
	 *   <ShipNodeInventory>
		    <Item ConsiderAllNodes="" ConsiderAllSegments=""
		        ConsiderInventoryNodeControl="" Description=""
		        DistributionRuleId="" ItemID="" OrganizationCode=""
		        ProductClass="" Segment="" SegmentType="" ShipDate=""
		        TrackedEverywhereFlag="" UnitOfMeasure="">
		        <PrimaryInformation Description="" ShortDescription=""/>
		        <LanguageDescriptionList>
		            <LanguageDescription Description="" ExtendedDescription=""
		                LocaleCode="" ShortDescription=""/>
		        </LanguageDescriptionList>
		        <ShipNodes>
		            <ShipNode Description="" ExternalNode=""
		                IdentifiedByParentAs="" OwnerKey="" ShipNode=""
		                TotalDemand="" TotalSupply="" Tracked="">
		                <Supplies TotalSupply="">
		                    <InventorySupplyType OnhandSupply="" Quantity="" SupplyType=""/>
		                </Supplies>
		                <Demands TotalDemand="">
		                    <InventoryDemandType AllocatedDemand=""
		                        DemandType="" PromisedDemand="" Quantity=""/>
		                </Demands>
		            </ShipNode>
		        </ShipNodes>
		    </Item>
		</ShipNodeInventory>
	 * 
	 * @param ent_code  조직코드
	 * @param bar_code  상품코드
	 * @param ship_node 창고코드 - 창고코드가 없는경우 모든 창고의 수량반환 - MA전송시 적용
	 * @param uom       상품측정단위
	 * @param qty_type  재고구분 ( A - 가용재고, S - 공급수량 )
	 * @return
	 */
	public double getCalcQtyBeforeAdjustInv(String ent_code, String bar_code, String ship_node, String uom, String qty_type) throws Exception{
		
		if(uom == null || "".equals(uom) || "null".equals(uom)){
			uom = "EACH";
		}
		
		String getShipNodeTempate = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPNODE_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(getShipNodeTempate);
		String getInvXML = msg.format(new String[] {
													ent_code, bar_code, ship_node, uom 
												} 
		);
		
		String getInv_output = comApiCall("getShipNodeInventory", getInvXML);
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(getInv_output.getBytes("UTF-8")));
		XPath xp = XPathFactory.newInstance().newXPath();
		
		Double currQty = 0.00;
		
		// 창고의 수만큼 Supply Qty 합산
		NodeList shipNodeInvList = (NodeList)xp.evaluate("/ShipNodeInventory/Item/ShipNodes/ShipNode", doc.getDocumentElement(), XPathConstants.NODESET);
		for(int ii=0; ii<shipNodeInvList.getLength(); ii++){
			
			Double totSupply = (Double)xp.evaluate("@TotalSupply", shipNodeInvList.item(ii), XPathConstants.NUMBER);	// 공급수량 (ONHAND)
			Double totDemand = (Double)xp.evaluate("@TotalDemand", shipNodeInvList.item(ii), XPathConstants.NUMBER);	// 수요수량 
			logger.debug("["+bar_code+"]["+ship_node+"][totSupply]"+totSupply);
			
			// 가용재고 = 공급수량 - Allocated수량 (MA 전송시)
			if("A".equals(qty_type))
			{
				// 수요목록
				NodeList demandsList = (NodeList)xp.evaluate("Demands/InventoryDemandType", shipNodeInvList.item(ii), XPathConstants.NODESET);
				Double demandQtySum = 0.00;
				for(int jj=0; jj<demandsList.getLength(); jj++){
					
					String demandType = (String)xp.evaluate("@DemandType", demandsList.item(jj), XPathConstants.STRING);	// 수요유형(Allocated.. 등)
					Double demandQty = (Double)xp.evaluate("@Quantity", demandsList.item(jj), XPathConstants.NUMBER);	// 수요수량
					logger.debug("["+bar_code+"]["+ship_node+"][demandType]"+demandType);
					
					
					// TODO: 수요유형이 Allocated인 경우만 수요수량 합산
					if("ALLOCATED".equals(demandType)){
						logger.debug("["+bar_code+"]["+ship_node+"][demandQty]"+demandQty);
						demandQtySum = demandQtySum + demandQty;
					}
				}
				currQty = currQty + (totSupply-demandQtySum);
			}
			// 공급수량
			else if("S".equals(qty_type))
			{
				currQty = currQty + totSupply;
			}
			
		}
		
		logger.debug("["+bar_code+"][가용재고]"+currQty);
		
		return currQty;
	}
	
	/**
	 * 오더상세 조회
	 * 
	 * @param docType
	 * @param entCode
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public Document getOrderDetails(String docType, String entCode, String orderNo) throws Exception{
		
		// Input XML
		String getOrderDetail_input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
		+ "<Order DocumentType=\"{0}\" "
		+ " EnterpriseCode=\"{1}\" OrderNo=\"{2}\" >"
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderDetail_input);
		String inputXML = msg.format(new String[] {docType, entCode, orderNo} );
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		sterlingHTTPConnector.setApi(sc_order_details);
		sterlingHTTPConnector.setData(inputXML);
		
		String outputXML = sterlingHTTPConnector.run();
		logger.debug("[getOrderDetails outputXML]"+outputXML);
		
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		return doc;
	}
	
}

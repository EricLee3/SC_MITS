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
import com.isec.sc.intgr.redis.listener.OrderCreateMsgListener;





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
		
		
		sterlingHTTPConnector.setApi(apiName);
		sterlingHTTPConnector.setData(inputXML);
		
		String outputXML = sterlingHTTPConnector.run();
		logger.debug("[comApiCall outputXML]"+outputXML);
		
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
			logger.debug("[getPage outputXML]"+outputXML);
			
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
	
	public String manageItem(String xmlData) throws Exception{
		
		
		logger.debug("[manageItem intputXML]"+xmlData);
		
		String result = "1";
		Document doc = null;

		try {
			
			sterlingHTTPConnector.setApi(sc_item_manage);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[manageItem outputXML]"+outputXML);
			
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
	
	
	public HashMap<String, String> createShipment(String shipmentNo, String releaseNo, String docType, String entCode, String orderId) throws Exception{
		
		
		// Generate SC API Input XML	
		String template = FileContentReader.readContent(getClass().getResourceAsStream(createShipment_template));
		
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {shipmentNo, releaseNo, docType, entCode, orderId} );
		logger.debug("[createShipment intputXML]"+xmlData);
		
		
		Document doc = null;
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			
			sterlingHTTPConnector.setApi(sc_shipment_create);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[createShipment outputXML]"+outputXML);
			
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			
			
			// Error 발생
			if("Errors".equals(doc.getFirstChild().getNodeName())){

				// 상세예외처리 필요
				resultMap.put("status", "0000");
				
			}else{
				
				Element ele = doc.getDocumentElement();
				
				resultMap.put("status", "3350");
				resultMap.put("shipmentNo", ele.getAttribute("ShipmentNo"));
			}
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return resultMap;
		
	}
	
	
	/**
	 * 오더의 ReleaseNo 정보 조회
	 * 
	 * @param docType
	 * @param entCode
	 * @param orderId
	 * @return releaseKeyList
	 * @throws Exception
	 */
	public ArrayList<String> getOrderReleaseList(String docType, String entCode, String orderId) throws Exception{
		
		
		// Generate SC API Input XML	
		String template = FileContentReader.readContent(getClass().getResourceAsStream(getOrderReleaseList_template));
		MessageFormat msg = new MessageFormat(template);
		String xmlData = msg.format(new String[] {docType, entCode, orderId} );
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
//					String releaseKey = (String)xp.evaluate("@OrderReleaseKey", orderReleaseNodeList.item(i), XPathConstants.STRING);
					String releaseNo = (String)xp.evaluate("@ReleaseNo", orderReleaseNodeList.item(i), XPathConstants.STRING);
					logger.debug("ReleaseNo:::"+releaseNo);
					
//					releaseKeyList.add(releaseKey);
					releaseKeyList.add(releaseNo);
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

}

package com.isec.sc.intgr.api.delegate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.isec.sc.intgr.redis.listener.MgtOrderMessageListener;





@Component
public class SterlingApiDelegate {
	
	private static final Logger logger = LoggerFactory.getLogger(SterlingApiDelegate.class);
	
	
	@Autowired SterlingHTTPConnector sterlingHTTPConnector;
	
	
	@Value("${sc.api.item.manage}")
	private String sc_item_manage;
	
	@Value("${sc.api.order.create}")
	private String sc_order_create;
	
	@Value("${sc.api.order.release}")
	private String sc_order_release;
	
	public SterlingApiDelegate() {
		
	}
	
	public String manageItem(String xmlData) throws Exception{
		
		
		logger.debug("[intputXML]"+xmlData);
		
		String result = "1";
		Document doc = null;

		try {
			
			sterlingHTTPConnector.setApi(sc_item_manage);
			sterlingHTTPConnector.setData(xmlData);
			
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[outputXML]"+outputXML);
			
			
			// output string Parsing
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			
			// Error 처리
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			if("Erros".equals(doc.getFirstChild().getNodeName())){
				result = "0";
			}
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			result = "0";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "0";
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			result = "0";
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	
	public String releaseOrder(String xmlData) throws Exception{
		
		logger.debug("[intputXML]"+xmlData);
		
		String result = "1";
		Document doc = null;

		try {
			
			sterlingHTTPConnector.setApi(sc_order_release);
			sterlingHTTPConnector.setData(xmlData);
			
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[outputXML]"+outputXML);
			
			
			// output string Parsing
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			
			
			// Error 처리
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			if("Erros".equals(doc.getFirstChild().getNodeName())){
				result = "0";
			}
			
		} catch (SAXException e) {
			result = "0";
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			result = "0";
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			result = "0";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public HashMap<String, Object> createOrder(String xmlData) throws Exception{
		
		logger.debug("[createOrder intputXML]"+xmlData);
		
		
		Document doc = null;
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			
			sterlingHTTPConnector.setApi(sc_order_create);
			sterlingHTTPConnector.setData(xmlData);
			
			String outputXML = sterlingHTTPConnector.run();
			logger.debug("[CreateOrder outputXML]"+outputXML);
			
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
			logger.debug("result:::"+doc.getFirstChild().getNodeName());
			
			
			// Error 발생
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				
				// 상세예외처리 필요
				resultMap.put("status", "0000");
				
			}else{
				
				Element ele = doc.getDocumentElement();
				
				resultMap.put("status", "1100");
				resultMap.put("orderHeaderKey", ele.getAttribute("OrderHeaderKey"));
				resultMap.put("enterpriseCode", ele.getAttribute("EnterpriseCode"));
				resultMap.put("orderId", ele.getAttribute("OrderNo"));
				resultMap.put("documentType", ele.getAttribute("DocumentType"));
				
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
		
		return resultMap;
		
	}
	
}

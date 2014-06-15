package com.isec.sc.api.omp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.isec.sc.api.core.SterlingHTTPConnector;
import com.isec.sc.api.core.util.FileContentReader;

public class GetOrderStatus {
	
	private static final String apiName = "getOrderDetails"; // API Name
	private static final String templateFile = "getOrderDetails.template.xml"; // API Input Template
	private static final String statusTextProperties = "orderStatusText.properties";	// Order Status -> 한글변환
	
	private String enterpriseCd;
	private String orderNo;
	
	public String run() {
		
		// Input XML load
		String template = FileContentReader.readContent(getClass().getResourceAsStream(templateFile));
		
		// Input Data Mapping
		MessageFormat msg = new MessageFormat(template);
		String input = msg.format(new String[] { enterpriseCd, orderNo });
				
		
		// API 호출
		SterlingHTTPConnector sterling = new SterlingHTTPConnector();
		sterling.setApi(apiName);
		sterling.setData(input);
		String output = sterling.run();
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(output.getBytes("UTF-8")));
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
		
		Element root = doc.getDocumentElement();
		String status = root.getAttribute("Status");

		Properties statusText = new Properties();
		try {
			statusText.load(new InputStreamReader(getClass().getResourceAsStream(statusTextProperties), "UTF-8"));
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String translatedStatus = null;

		translatedStatus = statusText.getProperty(status);

		return (translatedStatus != null ? translatedStatus+"("+status+")" : status);
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public String getEnterpriseCd() {
		return enterpriseCd;
	}
	
	public void setEnterpriseCd(String enterpriseCd) {
		this.enterpriseCd = enterpriseCd;
	}
}

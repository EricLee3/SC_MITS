package com.isec.sc.api.inv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.isec.sc.api.core.SterlingHTTPConnector;
import com.isec.sc.api.core.util.FileContentReader;

public class GetInventorySupply {
	
	private static final String apiName = "getInventorySupply"; // API Name
	private static final String templateFile = "getInventorySupply.template.xml"; // API Input Template
	
	private String item; // 아아템ID
	private String organizationCode; // 재고조직코드(큐브 사업부코드)
	private String shipNode;	// 출하노드(창고코드)
	
	
	public int run() {
		String template = FileContentReader.readContent(getClass().getResourceAsStream(templateFile));
		
		MessageFormat msg = new MessageFormat(template);
		String input = msg.format(new String[] { item, organizationCode, shipNode });
		
		
		// API 호출
		SterlingHTTPConnector sterling = new SterlingHTTPConnector();
		sterling.setApi(apiName);
		sterling.setData(input);
		String output = sterling.run();
		
		
		// Output XML Parsing
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(output.getBytes()));
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
		Element supplyNode = (Element) root.getElementsByTagName("InventorySupply").item(0);
		if (supplyNode == null)	return 0;
		
		String itemCount = supplyNode.getAttribute("Quantity");		
				
		return (int)(Double.parseDouble(itemCount));
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getShipNode() {
		return shipNode;
	}

	public void setShipNode(String shipNode) {
		this.shipNode = shipNode;
	}
	
	
}

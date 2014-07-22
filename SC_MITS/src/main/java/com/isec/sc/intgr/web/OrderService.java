package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;



@Controller
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	
	@RequestMapping(value = "/getOrderList")
	public ModelAndView getOrderList( @RequestParam Map paramMap, @RequestParam(required=false, value="orderNo[]") String[] orderNos ) throws Exception{ 
		
		
		/*
		 * 	getOrderReleaseList Sample Input XML
			String getOrderReleaseList_input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OrderRelease DocumentType=\"\" EnterpriseCode=\"\" SalesOrderNoQryType=\"\" "
				+ "SalesOrderNo=\"\" QueryTypeDesc=\"\" ReleaseNo=\"\" BuyerOrganizationCodeQryType=\"\" BuyerOrganizationCode=\"\" "
				+ "SellerOrganizationCodeQryType=\"\" SellerOrganizationCode=\"\" ShipNodeQryType=\"\" "
				+ "ShipNode=\"\" ReceivingNodeQryType=\"\" ReceivingNode=\"\" DeliveryMethod=\"\" "
				+ "ReqShipDateQryType=\"BETWEEN\" ReqDeliveryDateQryType=\"BETWEEN\" OrderDateQryType=\"BETWEEN\" ></OrderRelease>";
		
		 */
		
		/**
		 * -------Custom Action 일 경우 - 컬럼값 변경
		 * draw=34&
		 * columns[0][data]=0&
		 * columns[0][name]=&
		 * columns[0][searchable]=true&
		 * columns[0][orderable]=false&
		 * columns[0][search][value]=&
		 * columns[0][search][regex]=false&
		 * order[0][column]=1&
		 * order[0][dir]=asc&
		 * start=0&
		 * length=20&
		 * search[value]=&
		 * search[regex]=false&
		 * customActionType=group_action&
		 * customActionName=Close&
		 * id[]=1&id[]=2&id[]=3&id[]=4&id[]=5
		 * 
		 * 
		 * 
		 * 
		 * -------일반 Action 일 경우 - 검색
		 * start=0&
		 * length=20& 
		 * action=filter&
		 * order_id=1&
		 * order_date_from=&
		 * order_date_to=&
		 * ent_code=2&
		 * seller_code=3&
		 * bill_to_id=&
		 * total_amount=&
		 * order_status=pending
		 */
		
		logger.debug("start: "+paramMap.get("start")); 
		logger.debug("length: "+paramMap.get("length")); 
		logger.debug("draw: "+paramMap.get("draw")); 
		logger.debug("customActionType: "+paramMap.get("customActionType")); 
		logger.debug("customActionMessage: "+paramMap.get("customActionMessage")); 
		
		if( orderNos != null && orderNos.length > 0)
			logger.debug("[orderNo.length]"+orderNos.length); 
		
		
		String entCode = (String)paramMap.get("ent_code");
		String sellerCode = (String)paramMap.get("seller_code");
		String orderStatus = (String)paramMap.get("order_status");
		String orderId = (String)paramMap.get("order_id");
		
		
		String getOrderList_input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
		+ "<Order DocumentType=\"0001\" "
		   + " EnterpriseCode=\"{0}\" SellerOrganizationCode=\"{1}\" Status=\"{2}\" OrderNo=\"{3}\" OrderNoQryType=\"LIKE\"> "
		   + "<OrderBy> "
		      + "  <Attribute Desc=\"Y\" Name=\"OrderNo\"/> "
		    + "</OrderBy> "
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {entCode, sellerCode, orderStatus, orderId} );
		logger.debug("[inputXML]"+inputXML); 
		
		String outputXML = sterlingApiDelegate.comApiCall("getOrderList", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		String tCnt = (String)xp.evaluate("@TotalOrderList", el, XPathConstants.STRING);
		logger.debug("[tCnt]" + tCnt);
		
		
		
		
		int iTotalRecords = Integer.parseInt(tCnt);
		int iDisplayLength = Integer.parseInt( (String)paramMap.get("length") );
		iDisplayLength = iDisplayLength < 0 ? iTotalRecords:iDisplayLength;
		
		int iDisplayStart = Integer.parseInt( (String)paramMap.get("start"));
		int iEnd = iDisplayStart + iDisplayLength;
		iEnd = iEnd > iTotalRecords ? iTotalRecords:iEnd;
		
		
		HashMap<String, Object> records = new HashMap<String,Object>();
		
		
		NodeList orderNodeList = (NodeList)xp.evaluate("/OrderList/Order", el, XPathConstants.NODESET);
		
		ArrayList<Object> data = new ArrayList<Object>();
		for(int i=iDisplayStart; i<iEnd; i++){
			
			
			String orderNo = (String)xp.evaluate("@OrderNo", orderNodeList.item(i), XPathConstants.STRING);
			String orderDate = (String)xp.evaluate("@OrderDate", orderNodeList.item(i), XPathConstants.STRING);
			String enterPrise = (String)xp.evaluate("@EnterpriseCode", orderNodeList.item(i), XPathConstants.STRING);
			String sellerOrg = (String)xp.evaluate("@SellerOrganizationCode", orderNodeList.item(i), XPathConstants.STRING);
			String billToID = (String)xp.evaluate("@BillToID", orderNodeList.item(i), XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("@OriginalTotalAmount", orderNodeList.item(i), XPathConstants.STRING);
			String status = (String)xp.evaluate("@Status", orderNodeList.item(i), XPathConstants.STRING);
			
			
			
			
			data.add(new String[] { "<input type=\"checkbox\" name=\"orderNo[]\" value=\""+orderNo+"\">", 
					orderNo,
					orderDate,
					enterPrise,
					sellerOrg,
					billToID,
					totalAmount,
				      "<span class=\"label label-sm label-success\">"+status+"</span>",
				      "<a href=\"javascript:;\" class=\"btn btn-xs default\"><i class=\"fa fa-search\"></i> View</a>",
					});
		}
		
	  

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data",data);
		mav.addObject("draw", paramMap.get("draw"));
		mav.addObject("recordsTotal", iTotalRecords);
		mav.addObject("recordsFiltered", iTotalRecords);
		
		String custActionType = (String)paramMap.get("customActionType"); 
		if ("group_action".equals(custActionType)) {
			mav.addObject("customActionStatus","OK"); // pass custom message(useful for getting status of group actions)
			mav.addObject("customActionMessage","Group action successfully has been completed. Well done!"); // pass custom message(useful for getting status of group actions)
		}
		
		return mav;   
		
	}
	
}

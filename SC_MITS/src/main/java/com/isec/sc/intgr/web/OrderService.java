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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;



@Controller
@PropertySource("classpath:mits.properties")
@RequestMapping("/orders")
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	
	@Value("${sc.api.createOrder.template}")
	private String CREATE_ORDER_TEMPLATE;
	
	@Value("${sc.api.createOrderLine.template}")
	private String CREATE_ORDERLINE_TEMPLATE;
	
	@RequestMapping(value = "/dashboard.sc")
	public ModelAndView getDashBoardData(@RequestParam Map<String, String> paramMap) throws Exception{
		
		
		
		String outputXML = sterlingApiDelegate.comApiCall("getOrderList", "");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("orderBaseInfo", "");
		mav.setViewName("admin/orders/order_detail");
		return mav;
	}
	
	@RequestMapping(value = "/orderList.sc")
	public ModelAndView getOrderList( @RequestParam Map<String, String> paramMap,
							@RequestParam(defaultValue="0001" ) String doc_type,
							@RequestParam(required=false, value="orderNo[]") String[] orderNos ) throws Exception{ 
		
		
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
		 * doc_type
		 */
		
		
		
		logger.debug("doc_Type: "+doc_type); 
		logger.debug("action: "+paramMap.get("action")); 
		
		// 페이지 최초접근시 또는 Reset일 경우 API호출하지 않고 바로 리턴처리
		if( paramMap.get("action") == null){
			
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("data","");
			mav.addObject("recordsTotal", 0);
			mav.addObject("recordsFiltered", 0);
			
			return mav;
		}
		
		
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
		+ "<Order DocumentType=\""+ doc_type +"\" "
		   + " EnterpriseCode=\"{0}\" SellerOrganizationCode=\"{1}\" Status=\"{2}\" OrderNo=\"{3}\" "
		   + "OrderNoQryType=\"LIKE\" "
		   // Order Date 검색
		   + "FromOrderDate=\""+paramMap.get("order_date_from")+"\" ToOrderDate=\""+paramMap.get("order_date_to")+"\" OrderDateQryType=\"BETWEEN\" > "
		   + "<OrderBy> "
		      + "  <Attribute Desc=\"Y\" Name=\"OrderNo\"/> "
		    + "</OrderBy> "
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {entCode, sellerCode, orderStatus, orderId} );
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		String outputXML = sterlingApiDelegate.comApiCall("getOrderList", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		String tCnt = (String)xp.evaluate("@TotalOrderList", el, XPathConstants.STRING);
		logger.debug("[tCnt]" + tCnt);
		
/*		
		int iTotalRecords = Integer.parseInt(tCnt);
		int iDisplayLength = Integer.parseInt( (String)paramMap.get("length") );
		iDisplayLength = iDisplayLength < 0 ? iTotalRecords:iDisplayLength;
		
		int iDisplayStart = Integer.parseInt( (String)paramMap.get("start"));
		int iEnd = iDisplayStart + iDisplayLength;
		iEnd = iEnd > iTotalRecords ? iTotalRecords:iEnd;
*/		
		
//		for(int i=iDisplayStart; i<iEnd; i++){
		
		NodeList orderNodeList = (NodeList)xp.evaluate("/OrderList/Order", el, XPathConstants.NODESET);
		ArrayList<Object> data = new ArrayList<Object>();
		
		for(int i=0; i<orderNodeList.getLength(); i++){
			
			
			String orderNo = (String)xp.evaluate("@OrderNo", orderNodeList.item(i), XPathConstants.STRING);
			String orderDate = (String)xp.evaluate("@OrderDate", orderNodeList.item(i), XPathConstants.STRING);
			String enterPrise = (String)xp.evaluate("@EnterpriseCode", orderNodeList.item(i), XPathConstants.STRING);
			String sellerOrg = (String)xp.evaluate("@SellerOrganizationCode", orderNodeList.item(i), XPathConstants.STRING);
			String billToID = (String)xp.evaluate("@BillToID", orderNodeList.item(i), XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("@OriginalTotalAmount", orderNodeList.item(i), XPathConstants.STRING);
			String status = (String)xp.evaluate("@Status", orderNodeList.item(i), XPathConstants.STRING);
			if("".equals(status)) status = "Draft";
			
			String status_class = env.getProperty("ui.status."+status);
			if( status_class == null) status_class = "default";
			
			String linkParam = "docType="+doc_type+"&entCode="+enterPrise+"&orderNo="+orderNo;
			
			data.add(new String[] { "<input type=\"checkbox\" name=\"orderNo[]\" value=\""+orderNo+"\">", 
					orderNo,
					orderDate,
					enterPrise,
					sellerOrg,
					billToID,
					totalAmount,
				      "<span class=\"label label-sm label-"+status_class+"\">"+status+"</span>",
				      "<a href=\"/orders/orderDetail.do?"+linkParam+"\"  class=\"btn default btn-xs blue-stripe ajaxify\"><i class=\"fa fa-search\"></i> View</a>",
					});
		}
		
	  

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data",data);
//		mav.addObject("draw", paramMap.get("draw"));
//		mav.addObject("recordsTotal", iTotalRecords);
//		mav.addObject("recordsFiltered", iTotalRecords);
		mav.addObject("recordsTotal", tCnt);
		mav.addObject("recordsFiltered", tCnt);
		
		String custActionType = (String)paramMap.get("customActionType"); 
		if ("group_action".equals(custActionType)) {
			mav.addObject("customActionStatus","OK"); // pass custom message(useful for getting status of group actions)
			mav.addObject("customActionMessage","Group action successfully has been completed. Well done!"); // pass custom message(useful for getting status of group actions)
		}
		
		return mav;   
		
	}
	
	/**
	 * 오더상세 조회
	 * 
	 * @param orderNo 오더번호
	 * @param entCode 조직코드
	 * @param docType 주문유형 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderDetail.do")
	public ModelAndView getOrderDetail( @RequestParam String orderNo, @RequestParam String entCode, @RequestParam String docType ) throws Exception{ 
		
		// Input XML
		String getOrderDetail_input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
		+ "<Order DocumentType=\"{0}\" "
		+ " EnterpriseCode=\"{1}\" OrderNo=\"{2}\" >"
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderDetail_input);
		String inputXML = msg.format(new String[] {docType, entCode, orderNo} );
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		String outputXML = sterlingApiDelegate.comApiCall("getOrderDetails", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		Element el = doc.getDocumentElement();
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		// XML Parsing
		HashMap<String, Object> baseInfoMap = new HashMap<String, Object>();
		
		String ordreDate = (String)xp.evaluate("@OrderDate", el, XPathConstants.STRING);
		
		
		baseInfoMap.put("orderDate", ordreDate);
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("orderBaseInfo", baseInfoMap);
		mav.setViewName("admin/orders/order_detail");
		return mav;
	}
	
	@RequestMapping(value = "/rerturnCreate.sc")
	public ModelAndView returnCreate(@RequestParam Map<String, String> formData,
					@RequestParam(required=false, value="itemId[]") String[] itemIds,
					@RequestParam(required=false, value="itemDesc[]") String[] itemDesc,
					@RequestParam(required=false, value="itemQty[]") String[] itemQty
			){
		
		
		logger.debug("[doc_type]"+formData.get("doc_type"));
		logger.debug("[ent_code]"+formData.get("ent_code"));
		logger.debug("[seller_code]"+formData.get("seller_code"));
		
		String docType = formData.get("doc_type");
		String entCode = formData.get("ent_code");
		String sellerCode = formData.get("seller_code");
		
		
		String orderNo = formData.get("order_no");
		
		String fName = formData.get("fname");
		String lName = formData.get("lname");
		String phone = formData.get("phone");
		String mPhone = formData.get("mphone");
		String addr1 = formData.get("addr1");
		String addr2 = formData.get("addr2");
		String zipCode = formData.get("zipcode");
		String city = formData.get("city");
		String email = formData.get("email");
		String passwd = formData.get("passwd");
		
		String shipNode = "ISEC_WH1";
		String billToId = "SCUser1";
		
		
		// Generate SC API Input XML	
		String orderXML = FileContentReader.readContent(getClass().getResourceAsStream(CREATE_ORDER_TEMPLATE));
		String orderLineXML = FileContentReader.readContent(getClass().getResourceAsStream(CREATE_ORDERLINE_TEMPLATE));
		String orderLineText = "";
		
		for(int i=0; i<itemIds.length; i++)
		{
			MessageFormat msg = new MessageFormat(orderLineXML);
			
			orderLineText += msg.format(new String[] {itemQty[i], itemIds[i], itemDesc[i]} );
			logger.debug("[createShipment intputXML]"+orderLineText);
		}
		
		
		
	    MessageFormat msg = new MessageFormat(orderXML);
		String inputXML = msg.format(new String[] {
												    docType, entCode, sellerCode, orderNo, billToId,
												    shipNode,orderLineText,
													fName,lName,phone, mPhone, email, addr1, addr2, city, zipCode
												  } );
		logger.debug("[inputXML]"+inputXML); 
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputXML =  "";
		String succ = "Y";
		
		try{
		
			// API Call
			outputXML = sterlingApiDelegate.comApiCall("createOrder", inputXML);
		
		}catch(Exception e){
			succ = "N";
		}
		
		
		mav.addObject("success", succ);
		mav.addObject("outputXML", outputXML);
		return mav;
	}
	
}

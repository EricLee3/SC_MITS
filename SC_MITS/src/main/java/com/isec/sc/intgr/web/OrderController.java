package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	
	@Value("${sc.api.createOrder.template}")
	private String CREATE_ORDER_TEMPLATE;
	
	@Value("${sc.api.createOrderLine.template}")
	private String CREATE_ORDERLINE_TEMPLATE;
	
	
	@Value("${sc.api.scheduleOrder.template}")
	private String SCHEDULE_ORDER_TEMPLATE;
	
	@Value("${sc.api.releaseOrder.template}")
	private String RELEASE_ORDER_TEMPLATE;
	
	/**
	 * 오더목록조회 (판매오더, 반품오더)
	 *  - 오더의 상태가 Create(1100) ~ Shipped(3700)까지만 조회
	 * 
	 * @param paramMap
	 * @param doc_type
	 * @param orderNos
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderList.sc")
	public ModelAndView getOrderList( @RequestParam Map<String, String> paramMap,
							@RequestParam(defaultValue="0001" ) String doc_type,
							@RequestParam(required=false, value="orderNo[]") String[] orderNos ) throws Exception{ 
		
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
		 */
		logger.debug("doc_Type: "+doc_type); 
		logger.debug("action: "+paramMap.get("action")); 
		logger.debug("start: "+paramMap.get("start")); 
		logger.debug("length: "+paramMap.get("length")); 
		logger.debug("draw: "+paramMap.get("draw")); 
		logger.debug("customActionType: "+paramMap.get("customActionType")); 
		logger.debug("customActionMessage: "+paramMap.get("customActionMessage")); 
		
		if( orderNos != null && orderNos.length > 0)
			logger.debug("[orderNo.length]"+orderNos.length); 
		
		
		// 페이지 최초접근시 또는 Reset일 경우 API호출하지 않고 바로 리턴처리
		/*
		if( paramMap.get("action") == null){
			
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("data","");
			mav.addObject("recordsTotal", 0);
			mav.addObject("recordsFiltered", 0);
			
			return mav;
		}
		*/
		
		// Sorting tag Generation
		logger.debug("order[0][column]: "+paramMap.get("order[0][column]")); 
		logger.debug("order[0][dir]: "+paramMap.get("order[0][dir]"));
		
		String sortTag = "";
		String sortColumn = "";
		String sortDirTag = "N";
			
	    int sortColumnIdx = Integer.parseInt(paramMap.get("order[0][column]"));
		String sortDir = paramMap.get("order[0][dir]");
		if("desc".equals(sortDir)) sortDirTag = "Y";
		
		switch (sortColumnIdx) {
			case 2:
				sortColumn = "OrderNo";
				break;
			case 3:
				sortColumn = "OrderDate";
				break;
			case 4:
				sortColumn = "EnterpriseCode";
				break;
			case 5:
				sortColumn = "SellerOrganizationCode";
				break;
			case 11:
				sortColumn = "Status";
				break;
			default:
				sortColumn = "OrderDate"; sortDir = "Y";
				break;
		}
		sortTag = "<OrderBy><Attribute Desc=\""+sortDirTag+"\" Name=\""+sortColumn+"\"/></OrderBy> ";
		
		
		// Search Filter Parameter
		String orderId = (String)paramMap.get("order_id")==null?"":(String)paramMap.get("order_id");
		String entCode = (String)paramMap.get("ent_code")==null?"":(String)paramMap.get("ent_code");
		String sellerCode = (String)paramMap.get("seller_code")==null?"":(String)paramMap.get("seller_code");
		String fromDate = paramMap.get("order_date_from")==null?"":paramMap.get("order_date_from");
		String toDate = paramMap.get("order_date_to")==null?"":paramMap.get("order_date_to");
		String email = paramMap.get("email")==null?"":paramMap.get("email");
		
		String orderStatus = (String)paramMap.get("order_status")==null?"":(String)paramMap.get("order_status");
		if("A".equals(orderStatus)) orderStatus = ""; // All 일 경우
		
		String orderFromStatus = "1100";  // Created
		String orderToStatus = "9000";    // Cancelled 
		
		// 오더상태구간별 검색일 경우
		String orderStatusQryType_Text = "";
		if("".equals(orderStatus)){
			orderStatusQryType_Text = " FromStatus=\""+orderFromStatus+"\" ToStatus=\""+orderToStatus+"\" StatusQryType=\"BETWEEN\" ";
		}
		
		
		// Input XML Creation
		String getOrderList_input = ""
		     + "<Order DocumentType=\""+ doc_type +"\" "
		     + " EnterpriseCode=\"{0}\" SellerOrganizationCode=\"{1}\" "
		     
		     // 오더상태 검색
		     + " Status=\"{2}\" {3} "
		     // 오더번호 검색
		     + " OrderNo=\"{4}\" OrderNoQryType=\"LIKE\" "
		     // Email 검색
		     + " CustomerEMailID=\"{7}\"  CustomerEMailIDQryType=\"LIKE\" "
		     // 오더생성일 검색
		     + " FromOrderDate=\"{5}\" ToOrderDate=\"{6}\" OrderDateQryType=\"BETWEEN\" > "
		     
		     + sortTag
		
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {
				                                entCode,
				                                sellerCode, 
												"A".equals(orderStatus)?"":orderStatus, 
												orderStatusQryType_Text,
												orderId,
												fromDate,
												toDate,
												email
		} );
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		int pageRowNumber = Integer.parseInt(paramMap.get("start"));
		int pageSize = Integer.parseInt(paramMap.get("length")); 
		
		int pageNumber = pageRowNumber==0?1: (pageRowNumber/pageSize)+1; 
		logger.debug("[pageNumber]" + pageNumber);
		
		HashMap<String, Object> resultMap = sterlingApiDelegate.getPageApi(pageNumber, pageSize, "getOrderList", inputXML);
		
		/**
		 * returnMap.put("isFirstPage", (String)xp.evaluate("@IsFirstPage", el, XPathConstants.STRING));
			returnMap.put("isLastPage", (String)xp.evaluate("@IsLastPage", el, XPathConstants.STRING));
			returnMap.put("isValidPage", (String)xp.evaluate("@IsValidPage", el, XPathConstants.STRING));
			returnMap.put("pageNumber", (String)xp.evaluate("@PageNumber", el, XPathConstants.STRING));
			returnMap.put("pageSetToken", (String)xp.evaluate("@PageSetToken", el, XPathConstants.STRING));
			
			returnMap.put("dataList", (NodeList)xp.evaluate("/Page/Output", el, XPathConstants.NODESET));
		 */
		
		Node orderListNode = (Node)resultMap.get("output");
		
		
		XPath xp = XPathFactory.newInstance().newXPath();
		String totCnt = (String)xp.evaluate("OrderList/@TotalOrderList", orderListNode, XPathConstants.STRING);
		logger.debug("[totCnt]" + totCnt);
		
	
		NodeList orderNodeList = (NodeList)xp.evaluate("OrderList/Order", orderListNode, XPathConstants.NODESET);
		ArrayList<Object> data = new ArrayList<Object>();
		
		for(int i=0; i<orderNodeList.getLength(); i++){
			
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			
			String orderNo = (String)xp.evaluate("@OrderNo", orderNodeList.item(i), XPathConstants.STRING);
			String orderDate = (String)xp.evaluate("@OrderDate", orderNodeList.item(i), XPathConstants.STRING);
			String enterPrise = (String)xp.evaluate("@EnterpriseCode", orderNodeList.item(i), XPathConstants.STRING);
			String sellerOrg = (String)xp.evaluate("@SellerOrganizationCode", orderNodeList.item(i), XPathConstants.STRING);
			
			String custFname = (String)xp.evaluate("@CustomerFirstName", orderNodeList.item(i), XPathConstants.STRING);
			String custLname = (String)xp.evaluate("@CustomerLastName", orderNodeList.item(i), XPathConstants.STRING);
			String custName = custFname + " " + custLname;
			
			String phone = (String)xp.evaluate("@CustomerPhoneNo", orderNodeList.item(i), XPathConstants.STRING);
			String emailId = (String)xp.evaluate("@CustomerEMailID", orderNodeList.item(i), XPathConstants.STRING);
			String currency = (String)xp.evaluate("PriceInfo/@Currency", orderNodeList.item(i), XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", orderNodeList.item(i), XPathConstants.STRING);
			
			String paymentType = (String)xp.evaluate("PaymentMethods/PaymentMethod/@PaymentType", orderNodeList.item(i), XPathConstants.STRING);
			
			String status = (String)xp.evaluate("@Status", orderNodeList.item(i), XPathConstants.STRING);
			if("".equals(status)) status = "Draft";
			String status_class = env.getProperty("ui.status."+status+".cssname");
			if( status_class == null) status_class = "default";
			
			
			dataMap.put("orderNo", orderNo);
			dataMap.put("orderDate", orderDate);
			dataMap.put("enterPrise", enterPrise);
			dataMap.put("sellerOrg", sellerOrg);
			dataMap.put("billName", custName);
			dataMap.put("phone", phone);
			dataMap.put("emailId", emailId);
			dataMap.put("paymentType", paymentType);
			dataMap.put("currency", currency);
			dataMap.put("totalAmount", totalAmount);
			dataMap.put("status", status);
			dataMap.put("status_class", status_class);
			
			//-------- 3. Order Line Info
			NodeList orderLineNodeList = (NodeList)xp.evaluate("OrderLines/OrderLine", orderNodeList.item(i), XPathConstants.NODESET);
			logger.debug("orderLineNodeList.getLength()"+orderLineNodeList.getLength());
			List<HashMap<String,Object>> orderLineList = new ArrayList<HashMap<String,Object>>();
			
			for( int lineIdx=0; lineIdx<orderLineNodeList.getLength(); lineIdx++){
				
				Node lineNode = orderLineNodeList.item(lineIdx);
				
				HashMap<String,Object> orderLineMap = new HashMap<String,Object>();
				
				
				// Line Basic Info
				String lineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String PrimeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				String lineStatus = (String)xp.evaluate("@Status", lineNode, XPathConstants.STRING);
				
				orderLineMap.put("lineKey", lineKey);
				orderLineMap.put("PrimeLineNo", PrimeLineNo);
				orderLineMap.put("status", lineStatus);
				
				// Line Price, Charge, Tax Info
				Double qty = (Double)xp.evaluate("@OrderedQty", lineNode, XPathConstants.NUMBER);
				Double lineTatal = (Double)xp.evaluate("LineOverallTotals/@LineTotal", lineNode, XPathConstants.NUMBER);
				Double UnitPrice = (Double)xp.evaluate("LineOverallTotals/@UnitPrice", lineNode, XPathConstants.NUMBER);
				Double lineShipCharge = (Double)xp.evaluate("LineOverallTotals/@Charges", lineNode, XPathConstants.NUMBER);
				Double lineDisountCharge = (Double)xp.evaluate("LineOverallTotals/@Discount", lineNode, XPathConstants.NUMBER);
				Double lineTax= (Double)xp.evaluate("LineOverallTotals/@Tax", lineNode, XPathConstants.NUMBER);
				
				orderLineMap.put("qty", qty);
				orderLineMap.put("lineTotal", lineTatal);
				orderLineMap.put("UnitPrice", UnitPrice);
				orderLineMap.put("lineShipCharge", lineShipCharge);
				orderLineMap.put("lineDisount", -lineDisountCharge);
				orderLineMap.put("lineTax", lineTax);
				
				// Item Info
				String itemId = (String)xp.evaluate("Item/@ItemID", lineNode, XPathConstants.STRING);
				String itemDesc = (String)xp.evaluate("Item/@ItemDesc", lineNode, XPathConstants.STRING); // Item Detail URL
				String itemdShortDesc = (String)xp.evaluate("Item/@ItemShortDesc", lineNode, XPathConstants.STRING);
				
				orderLineMap.put("itemId", itemId);
				orderLineMap.put("itemDesc", itemDesc);
				orderLineMap.put("itemShortDesc", itemdShortDesc);
				
				orderLineList.add(orderLineMap);
			}
			
			dataMap.put("lineList", orderLineList);
			
			data.add(dataMap);

		}
	  

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data",data);
		mav.addObject("draw", paramMap.get("draw"));
		mav.addObject("recordsTotal", totCnt);
		mav.addObject("recordsFiltered", totCnt);
		
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
		
		//-------- 1. base info
		HashMap<String, Object> baseInfoMap = new HashMap<String, Object>();
		
		String ordreDate = (String)xp.evaluate("@OrderDate", el, XPathConstants.STRING);
		String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", el, XPathConstants.STRING);
		String currency = (String)xp.evaluate("PriceInfo/@Currency", el, XPathConstants.STRING);
		String paymentType = (String)xp.evaluate("PaymentMethods/PaymentMethod/@PaymentType", el, XPathConstants.STRING);
		String orderStatus = (String)xp.evaluate("@Status", el, XPathConstants.STRING);
		String orderStatus_class = env.getProperty("ui.status."+orderStatus+".cssname");
		if( orderStatus_class == null) orderStatus_class = "default";
		
		String sellerCode = (String)xp.evaluate("@SellerOrganizationCode", el, XPathConstants.STRING);
		
		
		baseInfoMap.put("orderNo", orderNo);
		baseInfoMap.put("orderDate", ordreDate);
		baseInfoMap.put("currency", currency);
		baseInfoMap.put("totalAmount", totalAmount);
		baseInfoMap.put("paymentType", paymentType);
		baseInfoMap.put("orderStatus", orderStatus);
		baseInfoMap.put("orderStatus_class", orderStatus_class );
		baseInfoMap.put("sellerCode", sellerCode );
		baseInfoMap.put("entCode", entCode );
		
		
		// 주문 전체 가격정보
		Double totLineSub = 0.00;
		Double totCharge = 0.00;
		Double totDiscount = 0.00;
		Double totTax = 0.00;
		
		/*
		 * XML
		 * <OverallTotals GrandCharges="20.00" GrandDiscount="20.00"
        	GrandTax="20.00" GrandTotal="2408.00" HdrCharges="0.00"
        	HdrDiscount="0.00" HdrTax="0.00" HdrTotal="0.00" LineSubTotal="2388.00"/>
		 */
		totLineSub = (Double)xp.evaluate("/Order/OverallTotals/@LineSubTotal", el, XPathConstants.NUMBER);
		totCharge = (Double)xp.evaluate("/Order/OverallTotals/@GrandCharges", el, XPathConstants.NUMBER);
		totDiscount = (Double)xp.evaluate("/Order/OverallTotals/@GrandDiscount", el, XPathConstants.NUMBER);
		totTax = (Double)xp.evaluate("/Order/OverallTotals/@GrandTax", el, XPathConstants.NUMBER);
		
		baseInfoMap.put("totalLine", totLineSub);
		baseInfoMap.put("totalCharge", totCharge);
		baseInfoMap.put("totalDiscount", -totDiscount);
		baseInfoMap.put("totalTax", totTax);
		
		
		
		//-------- 2. customer info
		// CustomerEMailID="" CustomerFirstName="" CustomerLastName="" CustomerPhoneNo
		HashMap<String, Object> custInfoMap = new HashMap<String, Object>();
		
		String custFName = (String)xp.evaluate("@CustomerFirstName", el, XPathConstants.STRING);
		String custLName = (String)xp.evaluate("@CustomerLastName", el, XPathConstants.STRING);
		String custEmail = (String)xp.evaluate("@CustomerEMailID", el, XPathConstants.STRING);
		String custPhone = (String)xp.evaluate("@CustomerPhoneNo", el, XPathConstants.STRING);
		
		custInfoMap.put("custName", custFName +" "+ custLName);
		custInfoMap.put("custEmail", custEmail);
		custInfoMap.put("custPhone", custPhone);
		
		
		//-------- 3. Bill info / Ship info
		/*
			<PersonInfoBillTo AddressID="" AddressLine1="" AddressLine2="" AddressLine3="" AddressLine4="" AddressLine5="" AddressLine6="" 
			AlternateEmailID="" Beeper="" City="" Company="" Country="" DayFaxNo="" DayPhone="" Department="" 
			EMailID="" EveningFaxNo="" EveningPhone="" FirstName="" IsAddressVerified="" IsCommercialAddress="" 
			JobTitle="" LastName="" Latitude="" Longitude="" MiddleName="" MobilePhone="" OtherPhone="" PersonID="" 
			PersonInfoKey="" State="" Suffix="" TaxGeoCode="" Title="" ZipCode=""/>
		*/
		HashMap<String, Object> billInfoMap = new HashMap<String, Object>();
		
		// Name, Addr1, Addr2, City, State, ZipCode, Phone, MobilePhone, Fax
		
		String billFName = (String)xp.evaluate("PersonInfoBillTo/@FirstName", el, XPathConstants.STRING);
		String billLName = (String)xp.evaluate("PersonInfoBillTo/@LastName", el, XPathConstants.STRING);
		String billAddr1 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine1", el, XPathConstants.STRING);
		String billAddr2 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine2", el, XPathConstants.STRING);
		String billCity = (String)xp.evaluate("PersonInfoBillTo/@City", el, XPathConstants.STRING);
		String billState = (String)xp.evaluate("PersonInfoBillTo/@State", el, XPathConstants.STRING);
		String billZipcode = (String)xp.evaluate("PersonInfoBillTo/@ZipCode", el, XPathConstants.STRING);
		
		String billPhone = (String)xp.evaluate("PersonInfoBillTo/@DayPhone", el, XPathConstants.STRING);
		String billFaxNo = (String)xp.evaluate("PersonInfoBillTo/@DayFaxNo", el, XPathConstants.STRING);
		String billMPhone = (String)xp.evaluate("PersonInfoBillTo/@MobilePhone", el, XPathConstants.STRING);
		
		billInfoMap.put("billName", billFName +" "+ billLName);
		billInfoMap.put("billAddr1", billAddr1);
		billInfoMap.put("billAddr2", billAddr2);
		billInfoMap.put("billCity", billCity);
		billInfoMap.put("billState", billState);
		billInfoMap.put("billZipcode", billZipcode);
		billInfoMap.put("billPhone", billPhone);
		billInfoMap.put("billMPhone", billMPhone);
		billInfoMap.put("billFaxNo", billFaxNo);
		
		
		//-------- 3. Order Line Info
		NodeList orderLineNodeList = (NodeList)xp.evaluate("OrderLines/OrderLine", el, XPathConstants.NODESET);
		
		List<HashMap<String,Object>> orderLineList = new ArrayList<HashMap<String,Object>>();
		for( int i=0; i<orderLineNodeList.getLength(); i++){
			
			HashMap<String,Object> orderLineMap = new HashMap<String,Object>();
			
			
			// Line Basic Info
			String lineKey = (String)xp.evaluate("@OrderLineKey", orderLineNodeList.item(i), XPathConstants.STRING);
			String PrimeLineNo = (String)xp.evaluate("@PrimeLineNo", orderLineNodeList.item(i), XPathConstants.STRING);
			String shipNode = (String)xp.evaluate("@ShipNode", orderLineNodeList.item(i), XPathConstants.STRING);
			String status = (String)xp.evaluate("@Status", orderLineNodeList.item(i), XPathConstants.STRING);
			String status_class = env.getProperty("ui.status."+orderStatus+".cssname");
			if( status_class == null) status_class = "default";
			
			orderLineMap.put("lineKey", lineKey);
			orderLineMap.put("PrimeLineNo", PrimeLineNo);
			orderLineMap.put("shipNode", shipNode);
			orderLineMap.put("status", status);
			orderLineMap.put("status_class", status_class);
			
			
			// Line Price, Charge, Tax Info
			Double qty = (Double)xp.evaluate("@OrderedQty", orderLineNodeList.item(i), XPathConstants.NUMBER);
			Double lineTatal = (Double)xp.evaluate("LineOverallTotals/@LineTotal", orderLineNodeList.item(i), XPathConstants.NUMBER);
			Double UnitPrice = (Double)xp.evaluate("LineOverallTotals/@UnitPrice", orderLineNodeList.item(i), XPathConstants.NUMBER);
			Double lineShipCharge = (Double)xp.evaluate("LineOverallTotals/@Charges", orderLineNodeList.item(i), XPathConstants.NUMBER);
			Double lineDisountCharge = (Double)xp.evaluate("LineOverallTotals/@Discount", orderLineNodeList.item(i), XPathConstants.NUMBER);
			Double lineTax= (Double)xp.evaluate("LineOverallTotals/@Tax", orderLineNodeList.item(i), XPathConstants.NUMBER);
			
			/*
			String qty = (String)xp.evaluate("@OrderedQty", orderLineNodeList.item(i), XPathConstants.STRING);
			String lineTatal = (String)xp.evaluate("LinePriceInfo/@LineTotal", orderLineNodeList.item(i), XPathConstants.STRING);
			String UnitPrice = (String)xp.evaluate("LinePriceInfo/@UnitPrice", orderLineNodeList.item(i), XPathConstants.STRING);
			String lineShipCharge = (String)xp.evaluate("LineCharges/LineCharge[@ChargeCategory='Shipping' and @ChargeName='Shipping']/@ChargePerLine", orderLineNodeList.item(i), XPathConstants.STRING);
			String lineDisountCharge = (String)xp.evaluate("LineCharges/LineCharge[@ChargeCategory='Discount' and @ChargeName='Discount']/@ChargePerLine", orderLineNodeList.item(i), XPathConstants.STRING);
			String lineTax= (String)xp.evaluate("LineTaxes/LineTax[@ChargeCategory='Price']/@Tax", orderLineNodeList.item(i), XPathConstants.STRING);
			*/
			
			orderLineMap.put("qty", qty);
			orderLineMap.put("lineTatal", lineTatal);
			orderLineMap.put("UnitPrice", UnitPrice);
			orderLineMap.put("lineShipCharge", lineShipCharge);
			orderLineMap.put("lineDisount", -lineDisountCharge);
			orderLineMap.put("lineTax", lineTax);
			
			
			
			// Item Info
			String itemId = (String)xp.evaluate("Item/@ItemID", orderLineNodeList.item(i), XPathConstants.STRING);
			String itemDesc = (String)xp.evaluate("Item/@ItemDesc", orderLineNodeList.item(i), XPathConstants.STRING);
			String itemdShortDesc = (String)xp.evaluate("Item/@ItemShortDesc", orderLineNodeList.item(i), XPathConstants.STRING);
			String uom = (String)xp.evaluate("Item/@UnitOfMeasure", orderLineNodeList.item(i), XPathConstants.STRING);
			String pclass = (String)xp.evaluate("Item/@ProductClass", orderLineNodeList.item(i), XPathConstants.STRING);
			 
			
			
			orderLineMap.put("itemId", itemId);
			orderLineMap.put("itemDesc", itemDesc);
			orderLineMap.put("itemdShortDesc", itemdShortDesc);
			orderLineMap.put("uom", uom);
			orderLineMap.put("pclass", pclass);
			
			orderLineList.add(orderLineMap);
		}
		
		// TODO: OrderLine Grand Total 계산
		
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("docType", docType);
		mav.addObject("entCode", entCode);
		mav.addObject("orderNo", orderNo);
		
		mav.addObject("baseInfo", baseInfoMap);
		mav.addObject("custInfo", custInfoMap);
		mav.addObject("billInfo", billInfoMap);
		mav.addObject("shipInfo", billInfoMap); // 일단 Bill정보를 Ship정보 그대로 사용
		mav.addObject("lineInfoList", orderLineList);
		
		mav.setViewName("admin/orders/order_detail");
		return mav;
	}
	
	/**
	 * 반품오더 생성
	 * 
	 * @param formData
	 * @param itemIds
	 * @param itemDesc
	 * @param itemQty
	 * @return
	 */
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
	
	/**
	 * Schedule Order
	 * 
	 * 
	 * @param doc_type 오더유형
	 * @param ent_code 관리조직코드
	 * @param order_no 오더번호
	 * @return
	 */
	@RequestMapping(value = "/scheduleOrder.sc")
	public ModelAndView scheduleOrder(@RequestParam String doc_type, @RequestParam String ent_code, @RequestParam String order_no)
	{
		
		logger.debug("##### Schedule Order API Called !!!");
		
		logger.debug("##### [doc_type]"+ doc_type);
		logger.debug("##### [ent_code]"+ ent_code);
		logger.debug("##### [order_no]"+ order_no);
		
		
		String scheduleNrelease = "N";	// Schedule과 Release를 동시에 처리함.
		
		String scheduleOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(SCHEDULE_ORDER_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(scheduleOrderXML);
		String inputXML = msg.format(new String[] {doc_type, ent_code, order_no, scheduleNrelease} );
		logger.debug("##### [inputXML]"+inputXML); 
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		try
		{
		
			// API Call
			outputMsg = sterlingApiDelegate.comApiCall("scheduleOrder", inputXML);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			// TODD: Error 메세지 정규화 작업필요
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				succ = "N";
				mav.addObject("errorMsg", outputMsg);
			}else{
				mav.addObject("outputMsg", "Schedule Transaction was processed Successfully.");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			succ = "N";
			mav.addObject("errorMsg", "처리 중 예기치 못한 에러가 발생했습니다.\n 다시 시도하시거나 관리자에게 문의하시기 바랍니다.");
			
		}
		mav.addObject("success", succ);
		return mav;
	}
	
	
	
	
	
	
	
	@RequestMapping(value = "/releaseOrder.sc")
	public ModelAndView releaseOrder(@RequestParam String doc_type, @RequestParam String ent_code, @RequestParam String order_no)
	{
		
		logger.debug("##### Schedule Order API Called !!!");
		
		logger.debug("##### [doc_type]"+ doc_type);
		logger.debug("##### [ent_code]"+ ent_code);
		logger.debug("##### [order_no]"+ order_no);
		
		
		String releaseOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(RELEASE_ORDER_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(releaseOrderXML);
		String inputXML = msg.format(new String[] {doc_type, ent_code, order_no, releaseOrderXML} );
		logger.debug("##### [inputXML]"+inputXML); 
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		try
		{
		
			// API Call
			outputMsg = sterlingApiDelegate.comApiCall("releaseOrder", inputXML);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			// TODD: Error 메세지 정규화 작업필요
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				succ = "N";
				mav.addObject("errorMsg", outputMsg);
			}else{
				mav.addObject("outputMsg", "Release Transaction was processed Successfully.");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			succ = "N";
			mav.addObject("errorMsg", "처리 중 예기치 못한 에러가 발생했습니다.\n 다시 시도하시거나 관리자에게 문의하시기 바랍니다.");
			
		}
		mav.addObject("success", succ);
		return mav;
	}
	
	
	@RequestMapping(value = "/errorList.sc")
	public ModelAndView getOrderErrorList(@RequestParam Map<String, String> paramMap) throws Exception{
		
		
		String entCode = "DA";
		String sellerCode = "OUTRO";
		
		String errListKey = entCode + ":" + sellerCode + ":order:error";
		
		
		/*
		 * String errorJSON = "{ 'docType':'0001', 'entCode':'DA' 'sellerCode':'OUTRO' orderId:'0001', "
				+ " orderXML:'<xml>',"
				+ " errorMsg:'Error Message',"
				+ " errorDetail:'Error Detail Message',"
				+ " errorDate:'2013-11-23 14:30' "
				+ "}";
		 */
		 
		List<String> errorList = listOps.range(errListKey, 0, 7);
		
		List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String, Object>>();
		
		for( String jsonData:  errorList){
			
			HashMap<String,Object> result = new ObjectMapper().readValue(jsonData, HashMap.class);
			dataList.add(result);
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data",dataList);
		
		
		return mav;
	}
}

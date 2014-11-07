package com.isec.sc.intgr.web;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.api.util.RedisCommonService;



@Controller
@RequestMapping("/orders")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	
	@Autowired private StringRedisTemplate maStringRedisTemplate;
	
	@Autowired private SterlingApiDelegate sterlingApiDelegate;
	@Autowired private RedisCommonService redisService;
	
	@Autowired private Environment env;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	@Resource(name="maStringRedisTemplate")
	private ValueOperations<String, String> valOps;
	
	
	
	
	
	
	@Value("${sc.api.createOrder.template}")
	private String CREATE_ORDER_TEMPLATE;
	
	@Value("${sc.api.createOrderLine.template}")
	private String CREATE_ORDERLINE_TEMPLATE;
	
	@Value("${sc.api.scheduleOrder.template}")
	private String SCHEDULE_ORDER_TEMPLATE;
	
	@Value("${sc.api.releaseOrder.template}")
	private String RELEASE_ORDER_TEMPLATE;
	
	@Value("${sc.api.cancelOrder.template}")
	private String CANCEL_ORDER_TEMPLATE;
	
	@Value("${sc.api.cancelOrderLine.template}")
	private String CANCEL_ORDER_LINE_TEMPLATE;
	
	@Value("${sc.api.changeOrder.addNote.template}")
	private String CHANGE_ORDER_ADD_NOTE_TEMPLATE;
	
	@Value("${sc.api.getShipmentListForOrder.template}")
	private String GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE;
	
	
	@Value("${sc.order.type.sales}")
	private String SC_ORDER_TYPE_SALES;
	
	
	@RequestMapping(value = "/order_list.do")
	public ModelAndView orderList(@RequestParam(defaultValue="false" ) String action, @RequestParam(defaultValue="A" ) String status) throws Exception{ 
		
		logger.debug("[action]"+action);
		logger.debug("[status]"+status);
		
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("action", action); 
		mav.addObject("status", status);
		
		mav.setViewName("/admin/orders/order_list");
		return mav;
		
	}
	
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
							@RequestParam(required=false, value="orderNo[]") String[] orderNos, HttpServletRequest req ) throws Exception{ 
		
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
		if( paramMap.get("action") == null){
			
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("data","");
			mav.addObject("recordsTotal", 0);
			mav.addObject("recordsFiltered", 0);
			
			return mav;
		}
		
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
			case 1:
				sortColumn = "OrderNo";
				break;
			case 2:
				sortColumn = "OrderDate";
				break;
			case 3:
				sortColumn = "EnterpriseCode";
				break;
			case 4:
				sortColumn = "SellerOrganizationCode";
				break;
			default:
				sortColumn = "OrderDate"; sortDir = "Y";
				break;
		}
		sortTag = "<OrderBy><Attribute Desc=\""+sortDirTag+"\" Name=\""+sortColumn+"\"/></OrderBy> ";
		
		
		// Search Filter Parameter
		String orderId = (String)paramMap.get("order_id")==null?"":(String)paramMap.get("order_id");
		
		
		String entCode = (String)paramMap.get("ent_code")==null?"":(String)paramMap.get("ent_code");
		
		// TODO: 관리조직 세션정보로 얻어옴. 빈값이 넘어올 경우 admin이 아닐 경우 사용자의 세선값으로 적용
		String sesEntCode = (String)req.getSession().getAttribute("S_ORG_CODE");
		if("".equals(entCode)){
			if(!"*".equals(sesEntCode)){
				entCode = sesEntCode;
			}
		}
		
		
		// TODO: 판매조직 세션정보로 얻어옴. 빈값이 넘어올 경우 admin이 아닐 경우 사용자의 세선값으로 적용
		String sellerCode = (String)paramMap.get("seller_code")==null?"":(String)paramMap.get("seller_code");
		String sesSellCode = (String)req.getSession().getAttribute("S_SELL_CODE");
		if("".equals(sellerCode)){
			if(!"*".equals(sesSellCode)){
				sellerCode = sesSellCode;
			}
		}
		
		
		String fromDate = paramMap.get("order_date_from")==null?"":paramMap.get("order_date_from");
		if(!"".equals(fromDate)) fromDate = fromDate+"T00:00:00";
		String toDate = paramMap.get("order_date_to")==null?"":paramMap.get("order_date_to");
		if(!"".equals(toDate)) toDate = toDate+"T23:59:59";
		
		String cust_fname = paramMap.get("cust_fname")==null?"":paramMap.get("cust_fname");
		String cust_lname = paramMap.get("cust_lname")==null?"":paramMap.get("cust_lname");
		String cust_phone = paramMap.get("cust_phone")==null?"":paramMap.get("cust_phone");
		String cust_email = paramMap.get("cust_email")==null?"":paramMap.get("cust_email");
		String vendor_id = paramMap.get("vendor_id")==null?"":paramMap.get("vendor_id");
		
		
		// 오더상태 전체처리
		String orderStatus = (String)paramMap.get("order_status")==null?"":(String)paramMap.get("order_status");
		if("A".equals(orderStatus)) orderStatus = ""; // All 일 경우
		
		// 오더상태구간별 검색일 경우
//		String orderStatusQryType_Text = "";
//		if("".equals(orderStatus) || "A".equals(orderStatus)){
//			orderStatusQryType_Text = " FromStatus=\""+orderFromStatus+"\" ToStatus=\""+orderToStatus+"\" StatusQryType=\"BETWEEN\" ";
//		}else{
//			orderStatusQryType_Text = " Status=\""+ orderStatus +"\" StatusQryType=\"EQ\" "; 
//		}
		
		// Input XML Creation
		String getOrderList_input = ""
		     + "<Order DocumentType=\""+ doc_type +"\" "
		     + " EnterpriseCode=\"{0}\" SellerOrganizationCode=\"{1}\" DraftOrderFlag=\"N\" "
		     // 오더상태
		     + " Status=\"{2}\" "
		     // 오더번호 검색
		     + " OrderNo=\"{3}\" OrderNoQryType=\"LIKE\" "
		     // 오더생성일 검색
		     + " FromOrderDate=\"{4}\" ToOrderDate=\"{5}\" OrderDateQryType=\"BETWEEN\" "
		     // Email 검색
		     + " CustomerEMailID=\"{6}\"  CustomerEMailIDQryType=\"LIKE\" "
		     // 고객명/전화번호 검색
		     + " CustomerFirstName=\"{7}\"  CustomerLastName=\"{8}\" CustomerFirstNameQryType=\"LIKE\" CustomerLastNameQryType=\"LIKE\" "
		     + " CustomerPhoneNo=\"{9}\"  CustomerPhoneNoQryType=\"LIKE\" "
		     // 2차DOS 검색
		     + " VendorID=\"{10}\" VendorIDQryType=\"LIKE\" >"
		     + sortTag
		+ "</Order> ";
		
	    MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {
				                                entCode,
				                                sellerCode, 
				                                orderStatus,
												orderId,
												fromDate,
												toDate,
												cust_email,
												cust_fname,
												cust_lname,
												cust_phone,
												vendor_id
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
//			String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", orderNodeList.item(i), XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("OverallTotals/@GrandTotal", orderNodeList.item(i), XPathConstants.STRING);	// Discount 포함
			
			String paymentType = (String)xp.evaluate("PaymentMethods/PaymentMethod/@PaymentType", orderNodeList.item(i), XPathConstants.STRING);
			
			
			String minStatus = (String)xp.evaluate("@MinOrderStatus", orderNodeList.item(i), XPathConstants.STRING);
			String maxStatus = (String)xp.evaluate("@MaxOrderStatus", orderNodeList.item(i), XPathConstants.STRING);
			String defaultText = (String)xp.evaluate("@Status", orderNodeList.item(i), XPathConstants.STRING);
			
			String vendorId = (String)xp.evaluate("@VendorID", orderNodeList.item(i), XPathConstants.STRING); // 2차DOS명
			
			
			
			String[] status = genOrderStatusText(minStatus, maxStatus, defaultText);	
			
			
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
			dataMap.put("minStatus", minStatus);
			dataMap.put("maxStatus", maxStatus);
			dataMap.put("status_text", status[0]);
			dataMap.put("status_class", status[1]);
			dataMap.put("vendor_id", vendorId);
			
			// 주문취소요청 여부 조회
			String checkReqKey = enterPrise+":"+sellerOrg+":order:cancel";
			String cancelReq = "N";
			List<String> cancelReqRedisList = listOps.range(checkReqKey, 0, -1);
			for( String jsonData: cancelReqRedisList){
				
				HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				if(orderNo.equals( cancelReqMap.get("orderNo") )){
					cancelReq = "Y";
					break;
				}
			}
			dataMap.put("cancelReq", cancelReq);
			
			// 주문취소요청 결과 조회
			String checkResKey = enterPrise+":"+sellerOrg+":order:cancel:result";
			String cancelRes = "N";
			List<String> cancelResRedisList = listOps.range(checkResKey, 0, -1);
			for( String jsonData: cancelResRedisList){
				
				HashMap<String,String> cancelResMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				if(orderNo.equals( cancelResMap.get("orderNo") )){
					cancelRes = "Y";
					dataMap.put("cancelRes_code", cancelResMap.get("status_code"));
					dataMap.put("cancelRes_text", cancelResMap.get("status_text"));
					dataMap.put("cancelRes", cancelRes);
					
					break;
				}
			}
			
			
			
			//-------- 3. Order Line Info
			NodeList orderLineNodeList = (NodeList)xp.evaluate("OrderLines/OrderLine", orderNodeList.item(i), XPathConstants.NODESET);
			List<HashMap<String,Object>> orderLineList = new ArrayList<HashMap<String,Object>>();
			
			for( int lineIdx=0; lineIdx<orderLineNodeList.getLength(); lineIdx++){
				
				Node lineNode = orderLineNodeList.item(lineIdx);
				
				HashMap<String,Object> orderLineMap = new HashMap<String,Object>();
				
				
				// Line Basic Info
				String lineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String PrimeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				
				
				String lineStatusText = (String)xp.evaluate("@Status", lineNode, XPathConstants.STRING);
				String lineStatusCode = "";
				
				if("Created".equals(lineStatusText)){
					lineStatusCode = "1100";
				}else if("Backordered".equals(lineStatusText)){
					lineStatusCode = "1300";
				}else if("Scheduled".equals(lineStatusText)){
					lineStatusCode = "1500";
				}else if("Released".equals(lineStatusText)){
					lineStatusCode = "3200";
				}else if("Included In Shipment".equals(lineStatusText)){
					lineStatusCode = "3350";
				}else if("Shipped".equals(lineStatusText)){
					lineStatusCode = "3700";
				}else if("Cancelled".equals(lineStatusText)){
					lineStatusCode = "9000";
				}
				
				String[] lineStatus = genOrderStatusText(lineStatusCode, lineStatusCode, lineStatusText);
				
				orderLineMap.put("lineKey", lineKey);
				orderLineMap.put("PrimeLineNo", PrimeLineNo);
				orderLineMap.put("status_text", lineStatus[0]);
				orderLineMap.put("status_class", lineStatus[1]);
				
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
	 * @param entCode 조직코드
	 * @param docType 오더유형
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderDetail.do")
	public ModelAndView getOrderDetail( @RequestParam String orderNo, @RequestParam String entCode, @RequestParam String docType ) throws Exception{ 
		
		
		// API Call
		Document doc = sterlingApiDelegate.getOrderDetails(docType, entCode, orderNo);
		Element el = doc.getDocumentElement();
		
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		//-------- 1. base info
		HashMap<String, Object> baseInfoMap = new HashMap<String, Object>();
		
		String orderDate = (String)xp.evaluate("@OrderDate", el, XPathConstants.STRING);
		orderDate = CommonUtil.getDateTimeByTimeZone(orderDate);
		
//		String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", el, XPathConstants.STRING);
		String totalAmount = (String)xp.evaluate("OverallTotals/@GrandTotal", el, XPathConstants.STRING);	// Discount 포함
		
		String currency = (String)xp.evaluate("PriceInfo/@Currency", el, XPathConstants.STRING);
		String paymentType = (String)xp.evaluate("PaymentMethods/PaymentMethod/@PaymentType", el, XPathConstants.STRING);
		String paymentTypeName = env.getProperty("sc.payment.type."+paymentType);
		
		String sellerCode = (String)xp.evaluate("@SellerOrganizationCode", el, XPathConstants.STRING);
		
		
		String minStatus = (String)xp.evaluate("@MinOrderStatus", el, XPathConstants.STRING);
		String maxStatus = (String)xp.evaluate("@MaxOrderStatus", el, XPathConstants.STRING);
		String defaultText = (String)xp.evaluate("@Status", el, XPathConstants.STRING);
		
		String vendorId = (String)xp.evaluate("@VendorID", el, XPathConstants.STRING); // 2차DOS명
		
		String[] status = genOrderStatusText(minStatus, maxStatus, defaultText);
		
		baseInfoMap.put("orderNo", orderNo);
		baseInfoMap.put("orderDate", orderDate);
		baseInfoMap.put("currency", currency);
		baseInfoMap.put("totalAmount", totalAmount);
		baseInfoMap.put("paymentType", paymentType);
		baseInfoMap.put("paymentTypeName", paymentTypeName);
		baseInfoMap.put("minStatus", minStatus);
		baseInfoMap.put("maxStatus", maxStatus);
		baseInfoMap.put("orderStatus", status[0]);
		baseInfoMap.put("orderStatus_class", status[1]);
		baseInfoMap.put("sellerCode", sellerCode );
		baseInfoMap.put("entCode", entCode );
		baseInfoMap.put("vendor_id", vendorId);
		
		/*
		 * +"<Instructions>"
	        +    "<Instruction InstructionText=\"msg\" InstructionType=\"DLV_MSG\" />"
	        +"</Instructions>"
		 */
		String deliveryMsg = (String)xp.evaluate("Instructions/Instruction[@InstructionType='DLV_MSG']/@InstructionText", el, XPathConstants.STRING);
		baseInfoMap.put("deliveryMsg", deliveryMsg);
		
		
		
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
		HashMap<String, Object> shipInfoMap = new HashMap<String, Object>();
		
		// Name, Addr1, Addr2, City, State, ZipCode, Phone, MobilePhone, Fax
		
		String billFName = (String)xp.evaluate("PersonInfoBillTo/@FirstName", el, XPathConstants.STRING);
		String billLName = (String)xp.evaluate("PersonInfoBillTo/@LastName", el, XPathConstants.STRING);
		String billAddr1 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine1", el, XPathConstants.STRING)
							+ (String)xp.evaluate("PersonInfoBillTo/@AddressLine2", el, XPathConstants.STRING)
							+ (String)xp.evaluate("PersonInfoBillTo/@AddressLine3", el, XPathConstants.STRING);
		String billAddr2 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine4", el, XPathConstants.STRING)
							+ (String)xp.evaluate("PersonInfoBillTo/@AddressLine5", el, XPathConstants.STRING)
							+ (String)xp.evaluate("PersonInfoBillTo/@AddressLine6", el, XPathConstants.STRING);
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
		
		
		String shipFName = (String)xp.evaluate("PersonInfoShipTo/@FirstName", el, XPathConstants.STRING);
		String shipLName = (String)xp.evaluate("PersonInfoShipTo/@LastName", el, XPathConstants.STRING);
		String shipAddr1 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine1", el, XPathConstants.STRING)
						+ (String)xp.evaluate("PersonInfoShipTo/@AddressLine2", el, XPathConstants.STRING)
						+ (String)xp.evaluate("PersonInfoShipTo/@AddressLine3", el, XPathConstants.STRING);
		String shipAddr2 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine4", el, XPathConstants.STRING)
						+ (String)xp.evaluate("PersonInfoShipTo/@AddressLine5", el, XPathConstants.STRING)
						+ (String)xp.evaluate("PersonInfoShipTo/@AddressLine6", el, XPathConstants.STRING);
		String shipCity = (String)xp.evaluate("PersonInfoShipTo/@City", el, XPathConstants.STRING);
		String shipState = (String)xp.evaluate("PersonInfoShipTo/@State", el, XPathConstants.STRING);
		String shipZipcode = (String)xp.evaluate("PersonInfoShipTo/@ZipCode", el, XPathConstants.STRING);
		
		String shipPhone = (String)xp.evaluate("PersonInfoShipTo/@DayPhone", el, XPathConstants.STRING);
		String shipFaxNo = (String)xp.evaluate("PersonInfoShipTo/@DayFaxNo", el, XPathConstants.STRING);
		String shipMPhone = (String)xp.evaluate("PersonInfoShipTo/@MobilePhone", el, XPathConstants.STRING);
		
		shipInfoMap.put("shipName", shipFName +" "+ shipLName);
		shipInfoMap.put("shipAddr1", shipAddr1);
		shipInfoMap.put("shipAddr2", shipAddr2);
		shipInfoMap.put("shipCity", shipCity);
		shipInfoMap.put("shipState", shipState);
		shipInfoMap.put("shipZipcode", shipZipcode);
		shipInfoMap.put("shipPhone", shipPhone);
		shipInfoMap.put("shipMPhone", shipMPhone);
		shipInfoMap.put("shipFaxNo", shipFaxNo);
		
		
		
		
		
		//-------- 3. Order Line Info
		NodeList orderLineNodeList = (NodeList)xp.evaluate("OrderLines/OrderLine", el, XPathConstants.NODESET);
		
		List<HashMap<String,Object>> orderLineList = new ArrayList<HashMap<String,Object>>();
		for( int i=0; i<orderLineNodeList.getLength(); i++){
			
			HashMap<String,Object> orderLineMap = new HashMap<String,Object>();
			
			// Line Basic Info
			String lineKey = (String)xp.evaluate("@OrderLineKey", orderLineNodeList.item(i), XPathConstants.STRING);
			String PrimeLineNo = (String)xp.evaluate("@PrimeLineNo", orderLineNodeList.item(i), XPathConstants.STRING);
//			String shipNode = (String)xp.evaluate("@ShipNode", orderLineNodeList.item(i), XPathConstants.STRING);
			
			logger.debug("[PrimeLineNo]"+PrimeLineNo);
			logger.debug("[lineKey]"+lineKey);
			
			String minLineStatus = (String)xp.evaluate("@MinLineStatus", orderLineNodeList.item(i), XPathConstants.STRING);
			String maxLineStatus = (String)xp.evaluate("@MaxLineStatus", orderLineNodeList.item(i), XPathConstants.STRING);
			String defaultLineText = (String)xp.evaluate("@Status", el, XPathConstants.STRING);
			
			String[] lineStatus = genOrderStatusText(minLineStatus, maxLineStatus, defaultLineText);
			
			orderLineMap.put("lineKey", lineKey);
			orderLineMap.put("PrimeLineNo", PrimeLineNo);
			orderLineMap.put("status", lineStatus[0]);
			orderLineMap.put("status_class", lineStatus[1]);
			orderLineMap.put("max_status", maxLineStatus);
			
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
			
			// ShipNode 조회 - 주문상태가 주문취소가 아니고 3200이후 건만 조회
			String shipNodeString = "";
			if( Integer.parseInt(maxLineStatus) >= 3200){
				String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus[1]/@OrderReleaseKey", orderLineNodeList.item(i), XPathConstants.STRING);
				String shipNode = sterlingApiDelegate.getShipNodeByReleaseKey(orderNo, orderReleaseKey);
				
				if(shipNode != null && !"".equals(shipNode)){
					shipNodeString = shipNode+ " ("+env.getProperty(shipNode)+")";
				}
			}
			orderLineMap.put("shipNode", shipNodeString);
			
			// 상품의 현재고/가용재고 조회(모든창고)
			Double supplyQty =  sterlingApiDelegate.getCalcQtyBeforeAdjustInv(entCode, itemId, "", uom, "S");
			logger.debug("["+itemId+"][현재고]"+supplyQty);
			Double availQty =  sterlingApiDelegate.getCalcQtyBeforeAdjustInv(entCode, itemId, "", uom, "A");
			logger.debug("["+itemId+"][가용재고]"+availQty);
			
			orderLineMap.put("supplyQty", supplyQty);
			orderLineMap.put("availQty", availQty);
			
			orderLineList.add(orderLineMap);
		}
		
		//-------- 3. Note Info
		NodeList noteNodeList = (NodeList)xp.evaluate("Notes/Note", el, XPathConstants.NODESET);
		
		List<HashMap<String,Object>> noteList = new ArrayList<HashMap<String,Object>>();
		for( int i=0; i<noteNodeList.getLength(); i++){
			
			HashMap<String,Object> noteMap = new HashMap<String,Object>();
			
			// Date, User, Reason, Contact Type, Contact Reference, Notes
			String noteDate = (String)xp.evaluate("@ContactTime", noteNodeList.item(i), XPathConstants.STRING);
			noteDate = CommonUtil.getDateTimeByTimeZone(noteDate);
			
			String noteUser = (String)xp.evaluate("@ContactUser", noteNodeList.item(i), XPathConstants.STRING);
			String noteUserName = (String)xp.evaluate("User/@Username", noteNodeList.item(i), XPathConstants.STRING);
			String noteReason = (String)xp.evaluate("@ReasonCode", noteNodeList.item(i), XPathConstants.STRING);
			String noteContactType = (String)xp.evaluate("@ContactType", noteNodeList.item(i), XPathConstants.STRING);
			String noteContactRef = (String)xp.evaluate("@ContactReference", noteNodeList.item(i), XPathConstants.STRING);
			String noteText = (String)xp.evaluate("@NoteText", noteNodeList.item(i), XPathConstants.STRING);
			
			noteMap.put("noteDate", noteDate);
			noteMap.put("noteUser", noteUser);
			noteMap.put("noteUserName", noteUserName);
			noteMap.put("noteReason", noteReason);
			noteMap.put("noteContactType", noteContactType);
			noteMap.put("noteContactRef", noteContactRef);
			noteMap.put("noteText", noteText);
			
			noteList.add(noteMap);
		}
		
		ModelAndView mav = new ModelAndView("");
		mav.addObject("docType", docType);
		mav.addObject("entCode", entCode);
		mav.addObject("orderNo", orderNo);
		
		mav.addObject("baseInfo", baseInfoMap);
		mav.addObject("custInfo", custInfoMap);
		mav.addObject("billInfo", billInfoMap);
		mav.addObject("shipInfo", shipInfoMap); 
		mav.addObject("lineInfoList", orderLineList);
		
		mav.addObject("noteList", noteList);
		
		
		//-------- 4. Shipment Info
		List<Map<String, Object>> shipList = getShipmentListForOrder(docType, entCode, orderNo);
		mav.addObject("shipList", shipList);
		
		
		// 출고의뢰상태일 경우에만 품절취소 및 출고의뢰 실패여부 체크
		String shortedYN = "N";
		String shortedItemId = "";	// 품절취소된 상품코드
		String failedYN = "N";
		
		if("3200".equals(minStatus) && "3200".equals(maxStatus)){
			
			String cubeShortedKey = entCode+":"+sellerCode+":order:3202:90";
			String cubeFailedKey = entCode+":"+sellerCode+":order:3202:09";
			
			List<String> shortedList = listOps.range(cubeShortedKey, 0, -1);
			for( int i=0; i<shortedList.size(); i++){
				
				String jsonData = shortedList.get(i);
				HashMap<String,String> map = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				
				String shortOrderId = map.get("orderId");
				
				if(orderNo.equals(shortOrderId)){
					
					shortedYN = "Y";
					shortedItemId = map.get("itemId");
					break;
				}
			}
			
			List<String> failedList = listOps.range(cubeFailedKey, 0, -1);
			if("N".equals(shortedYN)){
				for( int i=0; i<failedList.size(); i++){
					
					String failedOrderId = failedList.get(i);
					if(orderNo.equals(failedOrderId)){
						
						failedYN = "Y";
						break;
					}
				}
			}
			
		}
		mav.addObject("shortedYN", shortedYN);
		mav.addObject("shortedItemId", shortedItemId);
		mav.addObject("failedYN", failedYN);
				
				
				
		
		// 취소요청정보 조회
		String reqKey = entCode + ":" + sellerCode + ":order:cancel";
		mav.addObject("cancelReqInfo", getCacelReqInfo(reqKey, entCode, sellerCode, orderNo));
		
		// 취소요청 결과정보 조회
		String resKey = entCode + ":" + sellerCode + ":order:cancel:result";
		mav.addObject("cancelResultInfo", getCacelReqInfo(resKey, entCode, sellerCode, orderNo));
		
		
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
	 * 출고의뢰를 위한 출고확정처리 Schedule Order - Release까지 같이 처리함.
	 * 
	 *  1. 중복출고의뢰 방지를 위한 출고의뢰 여부 및 출고의뢰결과 여부 체크
	 *    - rediskey 데이타 유무 확인: 조직코드:판매채널코드:order:update:S2C, C2S의 3200/3202 확인
	 *    - 데이타 존재할 경우 리턴
	 *    
	 *  2. schedule & release 처리
	 *    - scheduleOrder API 호출
	 *    - api호출 성공시 ScOrderStatusHandler의 processReleaseAfter()가 후처리 수행
	 *    - api호출 실패시 에러키에 저장: 조직코드:판매채널코드:order:update:error:3200
	 *  
	 *  
	 * @param doc_type 오더유형
	 * @param ent_code 관리조직코드
	 * @param order_no 오더번호
	 * @return
	 */
	@RequestMapping(value = "/scheduleOrder.sc")
	public ModelAndView scheduleOrder(@RequestParam String doc_type, @RequestParam String ent_code, @RequestParam String sell_code,
			@RequestParam String order_no,
			@RequestParam String min_status, @RequestParam String max_status)
	{
		
		logger.debug("##### Schedule Order API Called !!!");
		
		logger.debug("##### [doc_type]"+ doc_type);
		logger.debug("##### [ent_code]"+ ent_code);
		logger.debug("##### [sell_code]"+ sell_code);
		logger.debug("##### [order_no]"+ order_no);
		logger.debug("##### [min_status]"+ min_status);
		logger.debug("##### [max_status]"+ max_status);
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		// Redis 에러키 - 주문확정
		String errKey = ent_code+":"+sell_code+":order:update:error:3200";
		
		try
		{
			// 중복출고의뢰 방지를 위한 Cube 출고의뢰여부 확인
			String orgCode = env.getProperty("ca."+ent_code); // 사업부코드 - 조직코드 변환
			if(orgCode == null) orgCode = "";
			
			String sendKey = orgCode+":"+sell_code+":order:update:S2C";
			
			List<String> sendReleaseList= listOps.range(sendKey, 0, -1);
			logger.debug("[release key]"+sendKey);
			logger.debug("[release length]"+sendReleaseList.size());
			
			for(int i=0; i<sendReleaseList.size(); i++){
				String sendData = sendReleaseList.get(i);
				logger.debug("[ Data]"+sendData);
				
				// JSON --> HashMap 변환
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> dataMap = mapper.readValue(sendData, new TypeReference<HashMap<String,Object>>(){});
				
				String orderId = (String)dataMap.get("orderId");
				String status = (String)dataMap.get("status");
				
				// 출고의뢰 데이타 존재
				if("3200".equals(status) && orderId.equals(order_no)){
					succ = "N";
					mav.addObject("errorMsg", "이미 Cube로 출고의뢰가 된 주문입니다.\n페이지를 새로고침 하셔서 주문상태를 다시 확인하시기 바랍니다.");
					break;
				}
			} // End for Check S2C
			
			if("N".equals(succ)) return mav;
			
			
			// 중복출고의뢰 방지를 위한 Cube 출고의뢰결과 데이타유무 확인
			String readKey = orgCode+":"+sell_code+":order:update:C2S";
			
			List<String> releaseConfirmList= listOps.range(readKey, 0, -1);
			for(int i=0; i<releaseConfirmList.size(); i++){
				String readData = releaseConfirmList.get(i);
				
				// JSON --> HashMap 변환
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> dataMap = mapper.readValue(readData, new TypeReference<HashMap<String,Object>>(){});
				
				String orderId = (String)dataMap.get("orderId");
				String status = (String)dataMap.get("status");
				
				// 출고의뢰결과 데이타 존재
				if("3202".equals(status) && orderId.equals(order_no)){
					succ = "N";
					mav.addObject("errorMsg", "이미 Cube로 출고의뢰가 완료된 주문입니다.\n페이지를 새로고침 하셔서 주문상태를 다시 확인하시기 바랍니다.");
					break;
				}
				
			} // End for Check C2S
			if("N".equals(succ)) return mav;
			
			
			
			
			
			// 주문확정 처리
			String scheduleNrelease = "Y";	// Schedule과 Release를 동시에 처리함.
			String scheduleOrderXML = FileContentReader.readContent(getClass().getResourceAsStream(SCHEDULE_ORDER_TEMPLATE));
			
			MessageFormat msg = new MessageFormat(scheduleOrderXML);
			String inputXML = msg.format(new String[] {doc_type, ent_code, order_no, scheduleNrelease} );
			logger.debug("##### [inputXML]"+inputXML); 
		
			// API Call
			outputMsg = sterlingApiDelegate.comApiCall("scheduleOrder", inputXML);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				succ = "N";
				mav.addObject("errorMsg", "주문확정이 실패하였습니다.잠시 후 다시 시도하시거나 계속 실패가 발생할 경우\n시스템 담당자에 문의하시기 바랍니다.");
				
				// 에러메세지 저장
				redisService.saveErrDataByOrderId(errKey, order_no, outputMsg);
				
				
			}else{
				mav.addObject("outputMsg", "주문확정이 정상 처리되었습니다.\n주문상세화면을 새로고침하여 주문상태가 [출고의뢰]인지 확인하시기 바랍니다.");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			succ = "N";
			mav.addObject("errorMsg", "처리 중 예기치 못한 에러가 발생했습니다.\n 다시 시도하시거나 관리자에게 문의하시기 바랍니다.");
			
			
			// 에러메세지 저장
			redisService.saveErrDataByOrderId(errKey, order_no, e.getMessage());
			
		}
		mav.addObject("success", succ);
		return mav;
	}
	
	
	/**
	 * 주문 확정 ( Release ) - 사용안함. Schedule Order에서 Release까지 같이 처리
	 * 
	 * @param doc_type
	 * @param ent_code
	 * @param order_no
	 * @return
	 */
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
	
	
	@RequestMapping(value = "/cancelOrderReq.sc")
	public ModelAndView cancelOrderReq(@RequestParam Map<String, String> paramMap) throws Exception
	{
		String doc_type = (String)paramMap.get("doc_type");
		String ent_code = (String)paramMap.get("ent_code");
		String sell_code = (String)paramMap.get("sell_code");
		String order_no = (String)paramMap.get("order_no");
		String cancel_reason = (String)paramMap.get("cancel_reason");
		String cancel_note = (String)paramMap.get("cancel_note");
		String cancel_type = (String)paramMap.get("cancel_type");
		String line_keys = (String)paramMap.get("line_keys");
		
		logger.debug("##### Cancel Order API Called !!!");
		
		logger.debug("##### [doc_type]"+ doc_type);
		logger.debug("##### [ent_code]"+ ent_code);
		logger.debug("##### [sell_code]"+ sell_code);
		logger.debug("##### [order_no]"+ order_no);
		logger.debug("##### [cancel_reason]"+ cancel_reason);
		logger.debug("##### [cancel_note]"+ cancel_note);
		logger.debug("##### [cancel_type]"+ cancel_type);
		logger.debug("##### [line_keys]"+ line_keys);
		
		
		Document doc = null;
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		
		// 주문취소 가능여부 재 확인 - 현 주문상태 조회, 3700이면 취소불가
		
		/*
		 * 출고의뢰여부 상태조회
		 *  - 주문상태가 부분주문확정 3200이전 상태인 경우 -> 의뢰전 -> 주문취소처리 -> MA전송
		 *  - 주문상태 3700이 아닌경우 -> Cube주문취소요청 -> 응답확인 -> 정상일 경우 주문취소 -> MA 전송   
		 *                                                                 실패일 경우 - 출고확정상태인 경우 주문취소 불가 통보
		 */
		doc = sterlingApiDelegate.getOrderDetails(doc_type, ent_code, order_no);
		Element orderEle = doc.getDocumentElement();
			
		XPath xp = XPathFactory.newInstance().newXPath();
		
		
		String minStatus = (String)xp.evaluate("@MinOrderStatus", orderEle, XPathConstants.STRING);
		String maxStatus = (String)xp.evaluate("@MaxOrderStatus", orderEle, XPathConstants.STRING);
		String defaultText = (String)xp.evaluate("@Status", orderEle, XPathConstants.STRING);
			
			
		// 출고확정(3700)일 경우 - 취소불가
		if("3700".equals(maxStatus)){
			logger.debug("##### 출고확정, 주문취소불가");
			outputMsg = "해당주문건은 이미 [출고확정]된 주문건으로 주문취소 요청을 처리할 수 없습니다.\n고객응대후 주문취소를 요구할 경우 반풒프로세스를 수행하시기 바랍니다.";
			succ = "N";
			
			mav.addObject("success", succ);
			mav.addObject("errorMsg", outputMsg);
			return mav;
		}
			
		// 해당주문의 품절취소여부 조회 - TODO: 이미 오더상세에서 체크한 항목이라 Skip
		/*
		String cubeShortedKey = ent_code+":"+sell_code+":order:3202:90";
		
		String shortedYN = "N";
		List<String> shortedList = listOps.range(cubeShortedKey, 0, -1);
		for( int i=0; i<shortedList.size(); i++){
			
			String jsonData = shortedList.get(i);
			HashMap<String,String> map = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
			
			String shortOrderId = map.get("orderId");
			
			if(order_no.equals(shortOrderId)){
				
				shortedYN = "Y";
				break;
			}
		}*/
			
			
		// 출고의뢰(3200), 출고준비(3350)일 경우 - Cube주문취소 요청
		if("3200".equals(maxStatus) || "3350".equals(maxStatus)){
				
				
			// 기 주문취소요청 처리여부 확인
			boolean isRequest = false;
			
			String checkReqKey = ent_code+":"+sell_code+":order:cancel";
			
			List<String> cancelReqRedisList = listOps.range(checkReqKey, 0, -1);
			for(String jsonData: cancelReqRedisList){
				
				logger.debug("[jsonData]"+ jsonData);
				HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				
				String cancelOrderNo = cancelReqMap.get("orderNo");
				if(order_no.equals(cancelOrderNo)){
					
					isRequest = true;
					break;
				}
			}
				
			if(isRequest){
				
				outputMsg = "해당주문건은 이미 주문취소요청 상태입니다. 주문취소상태를 확인하시기 바랍니다.";
				succ = "N";
				
				mav.addObject("success", succ);
				mav.addObject("errorMsg", outputMsg);
				return mav;
			}

			
			
			
			
			
				
			// 주문기본정보
			String docType = orderEle.getAttribute("DocumentType"); // 오더유형
			String orderNo = orderEle.getAttribute("OrderNo");	// 오더번호
			String orderKey = orderEle.getAttribute("OrderHeaderKey");
			String orderDate = orderEle.getAttribute("OrderDate");
			String orderDateCube = orderDate.substring(0,4)+orderDate.substring(5,7)+orderDate.substring(8,10);
		
			// 3200 or 3350 된 OrderLine정보만 추출
			NodeList releaseOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@MaxLineStatus='3200' or @MaxLineStatus='3350']", orderEle, XPathConstants.NODESET);
			logger.debug("[OrderLine Count]"+releaseOrderLineList.getLength());
						
			List<HashMap<String,Object>> cancelList = new ArrayList<HashMap<String,Object>>();
			
			for(int i=0; i<releaseOrderLineList.getLength(); i++){
				Node lineNode = releaseOrderLineList.item(i);
				HashMap<String, Object> orderLineMap = new HashMap<String, Object>();
				
				String orderLineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String primeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus/@OrderReleaseKey", lineNode, XPathConstants.STRING);
				
				// 상품정보, 가격정보
				String itemID = (String)xp.evaluate("Item/@ItemID", lineNode, XPathConstants.STRING);
				String itemNm = (String)xp.evaluate("Item/@ItemShortDesc", lineNode, XPathConstants.STRING);
				Double pricingQty = (Double)xp.evaluate("LineOverallTotals/@PricingQty", lineNode, XPathConstants.NUMBER);	// 주문수량
				Double lineTotal = (Double)xp.evaluate("LineOverallTotals/@LineTotal", lineNode, XPathConstants.NUMBER);	// 오더라인 최종판매금액(배송비,과세,할인 적용금액)
				Double salePrice = (Double)xp.evaluate("LineOverallTotals/@UnitPrice", lineNode, XPathConstants.NUMBER);	// 개별판매단가(배송비,과세,할인 미적용금액)
				int cubePrice = (int)(lineTotal/pricingQty);	// 큐브전송 개별판매단가(배송비,과세,할인 적용금액을 수량으로 나눔)
				
				
				//---------------------  오더라인 단위정보 저장 for JSON ---------------------//
				// 오더라인 공통정보 (동일한 값)
				orderLineMap.put("org_code", env.getProperty("ca."+ent_code)); // TODO: 관리조직코드 -> 사업부코드로 변환
				orderLineMap.put("sell_code", sell_code);	// TODO: 셀러코드는 SC의 코드사용
				orderLineMap.put("orderId", orderNo);
				orderLineMap.put("orderDt", orderDateCube);
				
				// 오더라인순/오더라인키/오더릴리즈키
				orderLineMap.put("orderLineNo", primeLineNo);
				orderLineMap.put("orderLineKey", orderLineKey);
				orderLineMap.put("orderReleaseKey", orderReleaseKey);
				orderLineMap.put("ship_node", "");	// 주문취소요청시는 필요없는 정보
				
				// 상품/가격 정보
				orderLineMap.put("itemId", itemID);
				orderLineMap.put("itemNm", itemNm);
				orderLineMap.put("qty", pricingQty.intValue()+"");
				orderLineMap.put("salePrice", cubePrice+"");
				
				cancelList.add(orderLineMap);
			} // End for ReleaseList
			
			
			// Redis Send Data Set - 주문취소요청상태
			String custFname = (String)xp.evaluate("@CustomerFirstName", orderEle, XPathConstants.STRING);
			String custLname = (String)xp.evaluate("@CustomerLastName", orderEle, XPathConstants.STRING);
			String custName = custFname+custLname;
			String currency = (String)xp.evaluate("PriceInfo/@Currency", orderEle, XPathConstants.STRING);
//						String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", orderEle, XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("OverallTotals/@GrandTotal", orderEle, XPathConstants.STRING);	// Discount 포함
			
			HashMap<String, String> cancelReqMap = new HashMap<String, String>();
			cancelReqMap.put("orderNo", orderNo);
			cancelReqMap.put("orderDate", orderDate);
			cancelReqMap.put("enterPrise", ent_code);
			cancelReqMap.put("sellerOrg", sell_code);
			cancelReqMap.put("custName", custName);
			cancelReqMap.put("currency", currency);
			cancelReqMap.put("totalAmount", totalAmount);
			
			String[] status = genOrderStatusText(minStatus, maxStatus, defaultText);
			cancelReqMap.put("status_code", "00"); // 주문취소 요청상태
			cancelReqMap.put("status_text", "주문취소 요청중");
			cancelReqMap.put("status_class", "danger");
			
			ObjectMapper mapper = new ObjectMapper();
			String reqJson = mapper.writeValueAsString(cancelReqMap);
			
			
			String cancelReqKey = ent_code+":"+sell_code+":order:cancel";
			logger.debug("[9000 S2C - CanceReq key]"+cancelReqKey);
			logger.debug("[9000 S2C - CanceReq Data]"+reqJson);
			
			listOps.leftPush(cancelReqKey, reqJson);
			
			
			// Redis Send Data Set - Cube 주문취소요청
			HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
			String orgCode = env.getProperty("ca."+ent_code); // 사업부코드 - 조직코드 변환
			sendMsgMap.put("org_code", orgCode);
			sendMsgMap.put("sell_code", sell_code);
			sendMsgMap.put("orderId", orderNo);
			sendMsgMap.put("orderDt", orderDate);
			sendMsgMap.put("orderHeaderKey", orderKey);
			sendMsgMap.put("status", "9000");	// 주문취소 요청
			String tranDt = CommonUtil.cuurentDateFromFormat("yyyyMMddHHssmm");
			sendMsgMap.put("tranDt", tranDt);
			sendMsgMap.put("list", cancelList);
			
			String jsonMsg = mapper.writeValueAsString(sendMsgMap);
			String pushKey = orgCode+":"+sell_code+":order:update:S2C";
			
			logger.debug("[9000 S2C - trans Data]"+jsonMsg);
			logger.debug("[9000 S2C - trans Key]"+pushKey);
			
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey, jsonMsg);
			
			
			logger.debug("##### OrderCancel Request was successful.");
			outputMsg = "해당주문건에 대한 주문취소요청이 정상적으로 처리되었습니다.\nCube의 주문취소 처리결과는 최대 1시간 ~ 1시간30분정도 소요될 수 있습니다. ";
			outputMsg += "\n\n주문취소상태를 해당시간이후 반드시 확인하시기 바랍니다.";
			succ = "Y";
			
			mav.addObject("success", succ);
			mav.addObject("outputMsg", outputMsg);
		}
		
		
		return mav;
	}
	
	
	/**
	 * 주문 취소 및 주문취소 요청처리 
	 * TODO - 주문취소 요청처리 로직 분리 필요
	 * 
	 * @param doc_type
	 * @param ent_code
	 * @param order_no
	 * @return
	 */
	@RequestMapping(value = "/cancelOrder.sc")
	public ModelAndView cancelOrder(@RequestParam Map<String, String> paramMap) throws Exception
	{
		
		String doc_type = (String)paramMap.get("doc_type");
		String ent_code = (String)paramMap.get("ent_code");
		String sell_code = (String)paramMap.get("sell_code");
		String order_no = (String)paramMap.get("order_no");
		String cancel_reason = (String)paramMap.get("cancel_reason");
		String cancel_note = (String)paramMap.get("cancel_note");
		String cancel_type = (String)paramMap.get("cancel_type");
		String line_keys = (String)paramMap.get("line_keys");
		
		
		logger.debug("##### Cancel Order API Called !!!");
		
		logger.debug("##### [doc_type]"+ doc_type);
		logger.debug("##### [ent_code]"+ ent_code);
		logger.debug("##### [sell_code]"+ sell_code);
		logger.debug("##### [order_no]"+ order_no);
		logger.debug("##### [cancel_reason]"+ cancel_reason);
		logger.debug("##### [cancel_note]"+ cancel_note);
		logger.debug("##### [cancel_type]"+ cancel_type);
		logger.debug("##### [line_keys]"+ line_keys);
		
		
		Document doc = null;
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		
		// Redis 에러키 - 주문취소
		String errKey = ent_code+":"+sell_code+":order:update:error:9000";
		
		
		String templateFile = "";
		MessageFormat msg = null;
		String inputXML = "";
		String apiName = "cancelOrder";
		
		// SC주문취소 - 전체
		if("order".equals(cancel_type)){
			
			
			templateFile = FileContentReader.readContent(getClass().getResourceAsStream(CANCEL_ORDER_TEMPLATE));
			msg = new MessageFormat(templateFile);
			inputXML = msg.format(new String[] {doc_type, ent_code, order_no, cancel_reason, cancel_note} );
		
		
		// SC주문취소 - 부분
		}else if("line".equals(cancel_type)){
			templateFile = FileContentReader.readContent(getClass().getResourceAsStream(CANCEL_ORDER_LINE_TEMPLATE));
			msg = new MessageFormat(templateFile);
			
			
			String[] lineKey = line_keys.split("\\|");
			logger.debug("[lineKey]"+lineKey.length);
			
			String lineTemplate = "";
			for( int i=0; i<lineKey.length; i++){
				lineTemplate = lineTemplate + "<OrderLine Action=\"CANCEL\" OrderedQty=\"0\"  OrderLineKey=\""+lineKey[i]+"\">"
				        						+ "</OrderLine>";
			}
			
			inputXML = msg.format(new String[] {doc_type, ent_code, order_no, lineTemplate, cancel_reason, cancel_note} );
		}
		
		
		logger.debug("##### [inputXML_CancelOrder]"+inputXML); 
		
		
		try
		{
			// API Call
			outputMsg = sterlingApiDelegate.comApiCall(apiName, inputXML);
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				succ = "N";
				mav.addObject("errorMsg", outputMsg);
				
				// 에러데이타 저장
				redisService.saveErrDataByOrderId(errKey, order_no, outputMsg);
				
			}else{
				mav.addObject("outputMsg", "주문취소가 정상적으로 처리되었습니다.\nFront 실제 환불처리는 동기화 시점에 따라 다소 지연될 수 있습니다.");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			succ = "N";
			mav.addObject("errorMsg", "처리 중 예기치 못한 에러가 발생했습니다.\n 다시 시도하시거나 시스템 관리자에게 문의하시기 바랍니다.");
			
			// 에러데이타 저장
			redisService.saveErrDataByOrderId(errKey, order_no, outputMsg);
			
		}
		mav.addObject("success", succ);
		return mav;
	}
	
	/**
	 * Change Order - Add Notes
	 * 
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/addNotes.sc")
	public ModelAndView addNotes(@RequestParam Map<String, String> paramMap)
	{
		
		
		logger.debug("[doc_type]" + paramMap.get("doc_type"));
		logger.debug("[ent_code]" + paramMap.get("ent_code"));
		logger.debug("[seller_code]" + paramMap.get("seller_code"));
		logger.debug("[order_no]" + paramMap.get("order_no"));
		
		/*
		 * <Order Action="MODIFY" DocumentType="{0}"
			    EnterpriseCode="{1}" OrderNo="{2}" SellerOrganizationCode="{3}">
			    <Notes>
			        <Note  ContactTime="{4}" ContactUser="{5}" ReasonCode="{6}"
			            ContactType="{7}" ContactReference="{8}"  NoteText="{9}" VisibleToAll="Y"/>
			    </Notes>
			</Order>
		 * 
		 */
		
		String contact_date_day = paramMap.get("contact_date_day");
		String contact_date_time = paramMap.get("contact_date_time");
		String contact_date = contact_date_day+"T"+contact_date_time+":00";
		
		String contact_seller = paramMap.get("seller_code");
		String contact_user = paramMap.get("contact_user");
		String contact_reason = paramMap.get("contact_reason");
		String contact_type = paramMap.get("contact_type");
		String contact_ref = paramMap.get("contact_ref");
		String contact_note = paramMap.get("contact_note");
		
		logger.debug("[contcat_date]" + contact_date_day);
		logger.debug("[contact_user]" + paramMap.get("contact_user"));
		logger.debug("[contact_reason]" + paramMap.get("contact_reason"));
		logger.debug("[contact_type]" + paramMap.get("contact_type"));
		logger.debug("[contact_ref]" + paramMap.get("contact_ref"));
		logger.debug("[contact_note]" + paramMap.get("contact_note"));
		
		// Redis 에러키 - 주문변경
		String errKey = paramMap.get("ent_code")+":"+paramMap.get("seller_code")+":order:udate:error:0000";
		
		
		String addNoteXML = FileContentReader.readContent(getClass().getResourceAsStream(CHANGE_ORDER_ADD_NOTE_TEMPLATE));
		
		MessageFormat msg = new MessageFormat(addNoteXML);
		String inputXML = msg.format(new String[] {paramMap.get("doc_type"), paramMap.get("ent_code"), paramMap.get("order_no"),
								contact_seller,
								contact_date,
								contact_user,
								contact_reason,
								contact_type,
								contact_ref,
								contact_note				
							} );
		logger.debug("##### [inputXML_CancelOrder]"+inputXML); 
		
		ModelAndView mav = new ModelAndView("jsonView");
		String outputMsg =  "";
		String succ = "Y";
		
		try
		{
			// API Call
			outputMsg = sterlingApiDelegate.comApiCall("changeOrder", inputXML);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputMsg.getBytes("UTF-8")));
			
			if("Errors".equals(doc.getFirstChild().getNodeName())){
				succ = "N";
				mav.addObject("errorMsg", outputMsg);
				
				// 에러데이타 저장
				redisService.saveErrDataByOrderId(errKey, paramMap.get("order_no"), outputMsg);
				
			}else{
				mav.addObject("outputMsg", "요청하신 작업이 정상적으로 처리되었습니다.");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			succ = "N";
			mav.addObject("errorMsg", "처리 중 예기치 못한 에러가 발생했습니다.\n 다시 시도하시거나 관리자에게 문의하시기 바랍니다.");
			
			// 에러데이타 저장
			redisService.saveErrDataByOrderId(errKey, paramMap.get("order_no"), e.getMessage());
			
		}
		mav.addObject("success", succ);
		return mav;
	}
	
	
	/**
	 * Orders Overview 조회 - Dashboard > Orders Overviews
	 * - 오더상태별 최근 오더목록 조회
	 *    - 신규오더
	 *    - 주문확정 대기
	 *    - 출고완료
	 *    - 주문취소
	 *    - 에러
	 *    
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderOverviewList.sc")
	public ModelAndView getOrderOverviewList(@RequestParam Map<String, String> paramMap, HttpServletRequest req) throws Exception{
		
		String ch = (String)paramMap.get("ch");
		logger.debug("[ch]"+ch);
		
		String docType = (String)paramMap.get("doc_type");
		if( docType == null || "".equals(docType)){
			docType = SC_ORDER_TYPE_SALES;
		};
		
		// 관리조직 세션정보로 얻어옴
		String entCode = (String)req.getSession().getAttribute("S_ORG_CODE");
		String sellerCode = ch;
		
		
		
		//에러리스트 조회
		String errListKey = entCode + ":" + sellerCode + ":order:update:error";
		
		Set<String> cnt_key_names= maStringRedisTemplate.keys(errListKey);
		Iterator<String> itr = cnt_key_names.iterator();
		
		List<HashMap<String,Object>> errList = new ArrayList<HashMap<String, Object>>();
		while(itr.hasNext()){
			
			String errKey = itr.next();
			logger.debug("[key]"+errKey);
			
			
			// TODO: 건수 변경
			List<String> errorDataList = listOps.range(errKey, 0, 6);
			for( String jsonData:  errorDataList){
				
				HashMap<String,Object> result = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,Object>>(){});
				errList.add(result);
			}
		}
		/*
		 * String errorJSON = "{ 'docType':'0001', 'entCode':'DA' 'sellerCode':'OUTRO' orderId:'0001', "
				+ " orderXML:'<xml>',"
				+ " errorMsg:'Error Message',"
				+ " errorDetail:'Error Detail Message',"
				+ " errorDate:'2013-11-23 14:30' "
				+ "}";
		 */
		
		
		// 주문취소 요청건 조회
		String cancelReqAllKey = entCode + ":" + sellerCode + ":order:cancel";
		
		Set<String> cancelReq_key_names= maStringRedisTemplate.keys(cancelReqAllKey);
		Iterator<String> canItr = cancelReq_key_names.iterator();
		
		List<HashMap<String,String>> cancelReqList = new ArrayList<HashMap<String, String>>();
		
		while(canItr.hasNext()){
			
			String cancelReqKey = canItr.next();
			logger.debug("[cancelReqKey]"+cancelReqKey);
			
			List<String> cancelReqRedisList = listOps.range(cancelReqKey, 0, 6);
			for( String jsonData:  cancelReqRedisList){
				
				logger.debug("[jsonData]"+ jsonData);
				HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				
				// 취소요청 결과정보 조회
				String orderNo = cancelReqMap.get("orderNo");
				HashMap<String,Object> resMap =  getCacelReqInfo(cancelReqKey+":result", entCode, sellerCode, orderNo);
				
				logger.debug("[res_status_code]"+resMap.get("status_code"));
				if(resMap.get("status_code") != null)
				{
					cancelReqMap.put("res_status_code",(String)resMap.get("status_code"));
					cancelReqMap.put("res_status_text",(String)resMap.get("status_text"));
					
				}
				cancelReqList.add(cancelReqMap);
			}
		
		}
		
		
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("newList",getOrderOverviewList(docType, entCode, sellerCode, "1100") );	// New Order List - Top10
		mav.addObject("releaseList",getOrderOverviewList(docType, entCode, sellerCode, "1300") );	// 미 출고의뢰건
		mav.addObject("releaseConfirmList",getOrderOverviewList(docType, entCode, sellerCode, "3200") );	// 출고의뢰건
		mav.addObject("createShipmentList",getOrderOverviewList(docType, entCode, sellerCode, "3350") );	// 출고준비건
		mav.addObject("shippedList",getOrderOverviewList(docType, entCode, sellerCode, "3700") ); // 출고완료 리스트
		mav.addObject("cancelReqList",cancelReqList ); // TODO: 주문취소요청 리스트
		mav.addObject("cancelList",getOrderOverviewList(docType, entCode, sellerCode, "9000") ); // 주문취소 리스트
		//mav.addObject("cancelReqList",cancelReqList);
		
		return mav;
	}
	
	
	/**
	 * 오더상태별 오더목록 조회 - Dashboard > Orders Overview
	 * 	 신규주문건 - All
	 *	 주문확정대기 건 - BackOrdered,  3200 - Partially Released, Released
	 *	 출고완료목록 - 3700 Shipped
	 *	 주문취소건  - 9000 Cancelled
	 * 
	 * 
	 * @param docType
	 * @param entCode
	 * @param sellerCode
	 * @param orderStatus
	 * @return
	 * @throws Exception
	 */
	private ArrayList<HashMap<String, String>> getOrderOverviewList(String docType, String entCode, String sellerCode, String orderStatus) throws Exception{ 

		
		entCode = "*".equals(entCode)?"":entCode;
		sellerCode = "*".equals(sellerCode)?"":sellerCode;
		
		logger.debug("[status]"+ orderStatus);
		
		
		String rowCount = "7";
		
		// Input XML Creation
		String getOrderList_input = ""
		+ "<Order DocumentType=\""+ docType +"\" "
		+ " EnterpriseCode=\"{0}\" SellerOrganizationCode=\"{1}\" DraftOrderFlag=\"N\" Status=\"{2}\" "
		+ " MaximumRecords  = \"{3}\"  >"
		// Descending OrderDate
		+ "<OrderBy><Attribute Desc=\"Y\" Name=\"OrderDate\"/></OrderBy> "
		+ "</Order> ";
		
		MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {
		                                entCode,
		                                sellerCode, 
		                                orderStatus,
		                                rowCount
								} );
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		
		String outputXML = sterlingApiDelegate.comApiCall("getOrderList", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		XPath xp = XPathFactory.newInstance().newXPath();
		NodeList orderNodeList = (NodeList)xp.evaluate("OrderList/Order", doc, XPathConstants.NODESET);
		
		ArrayList<HashMap<String, String>> orderList = new ArrayList<HashMap<String, String>>();
		
		for(int i=0; i<orderNodeList.getLength(); i++){
		
			HashMap<String, String> dataMap = new HashMap<String, String>();
			
			String orderNo = (String)xp.evaluate("@OrderNo", orderNodeList.item(i), XPathConstants.STRING);
			String orderDate = (String)xp.evaluate("@OrderDate", orderNodeList.item(i), XPathConstants.STRING);
			String enterPrise = (String)xp.evaluate("@EnterpriseCode", orderNodeList.item(i), XPathConstants.STRING);
			String sellerOrg = (String)xp.evaluate("@SellerOrganizationCode", orderNodeList.item(i), XPathConstants.STRING);
			
			String custFname = (String)xp.evaluate("@CustomerFirstName", orderNodeList.item(i), XPathConstants.STRING);
			String custLname = (String)xp.evaluate("@CustomerLastName", orderNodeList.item(i), XPathConstants.STRING);
			String custName = custFname + " " + custLname;
			
			String currency = (String)xp.evaluate("PriceInfo/@Currency", orderNodeList.item(i), XPathConstants.STRING);
//			String totalAmount = (String)xp.evaluate("PriceInfo/@TotalAmount", orderNodeList.item(i), XPathConstants.STRING);
			String totalAmount = (String)xp.evaluate("OverallTotals/@GrandTotal", orderNodeList.item(i), XPathConstants.STRING);	// Discount 포함
			
			
			String minStatus = (String)xp.evaluate("@MinOrderStatus", orderNodeList.item(i), XPathConstants.STRING);
			String maxStatus = (String)xp.evaluate("@MaxOrderStatus", orderNodeList.item(i), XPathConstants.STRING);
			String defaultText = (String)xp.evaluate("@Status", orderNodeList.item(i), XPathConstants.STRING);
			
			String[] status = genOrderStatusText(minStatus, maxStatus, defaultText);
			
		
			dataMap.put("orderNo", orderNo);
			dataMap.put("orderDate", orderDate);
			dataMap.put("enterPrise", enterPrise);
			dataMap.put("sellerOrg", sellerOrg);
			dataMap.put("custName", custName);
			dataMap.put("currency", currency);
			dataMap.put("totalAmount", totalAmount);
			dataMap.put("status_text", status[0]);
			dataMap.put("status_class", status[1]);
			
			orderList.add(dataMap);
		}
		
		
		return orderList;   
	}
	
	/**
	 * 주문상태코드에 따른 주문상태명 반환
	 * 
	 * @param minStatus
	 * @param maxStatus
	 * @param defaultText
	 * @return
	 */
	private String[] genOrderStatusText(String minStatus, String maxStatus, String defaultText){
		
		String statusText[] = {"",""};
		
		String mappingText = env.getProperty("ui.status.text.kr."+maxStatus);
		String mappingTextCss = env.getProperty("ui.status.cssname."+maxStatus);
				
		// 오더상태 매핑항목에 없는 경우 SC상태명 그대로 사용
		if( mappingText  == null ) {
			statusText[0]  = defaultText; 
			statusText[1] = "default";
			
			return statusText;
		}
		
		if(minStatus.equals(maxStatus)){
			statusText[0] = mappingText;
			statusText[1]  = mappingTextCss;
			
		// 부분처리인 경우 SC상태명 그대로 사용
		}else{
			
			if("1300".equals(minStatus) && "3200".equals(maxStatus)){
				statusText[0]  = "부분 재고부족";
			}else{
				statusText[0]  = "부분 "+mappingText;
			}
			statusText[1] = "warning";
		}
		
		return statusText;
	}
	
	
	/**
	 * 주문취소요청 및 결과정보 조회
	 * 
	 * 
	 * @param key 조직코드:판매채널코드:order:cancel, 조직코드:판매채널코드:order:cancel:result
	 * @param entCode
	 * @param sellerCode
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, Object> getCacelReqInfo(String key, String entCode, String sellerCode, String orderNo) throws Exception{
		
		
		HashMap<String, Object> cacenReqInfo = new HashMap<String, Object>();
		
		
		List<String> cancelReqRedisList = listOps.range(key, 0, -1);
		for( int i=0; i<cancelReqRedisList.size(); i++){
			
			String jsonData = cancelReqRedisList.get(i);
			
//			logger.debug("[jsonData]"+ jsonData);
			HashMap<String,String> map = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
			
			String cancelOrderNo = map.get("orderNo");
			if(orderNo.equals(cancelOrderNo)){
				
				
				logger.debug("[code]"+map.get("status_code"));
				logger.debug("[text]"+map.get("status_text"));
				
				cacenReqInfo.put("status_code", map.get("status_code"));
				cacenReqInfo.put("status_text", map.get("status_text"));
				
				break;
			}
		}
		
		
		return cacenReqInfo;
	}
	
	
	/**
	 * 오더의 출하정보 조회
	 * 
	 * @param docType
	 * @param entCode
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getShipmentListForOrder( String docType, String entCode, String orderNo  ) throws Exception{
		
		
		// Template Load
		String inputTemplate = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPMENT_LIST_FOR_ORDER_TEMPLATE));
		
		
		// Input XML Create
	    MessageFormat msg = new MessageFormat(inputTemplate);
		String inputXML = msg.format(new String[] {
													docType, entCode, orderNo
												  } );
		logger.debug("[getShipmentListForOrder input]"+inputXML); 
		
		// Call API
		String outputXML = sterlingApiDelegate.comApiCall("getShipmentListForOrder", inputXML);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(outputXML.getBytes("UTF-8")));
		
		
		logger.debug("[getShipmentListForOrder output]"+outputXML); 
		
		// OutPut 
		XPath xp = XPathFactory.newInstance().newXPath();
		NodeList shipNodeList = (NodeList)xp.evaluate("ShipmentList/Shipment[@Status='1400']", doc, XPathConstants.NODESET);
		
		List<Map<String, Object>> shipList = new ArrayList<Map<String,Object>>();
		
		
		for(int i=0; i<shipNodeList.getLength(); i++) {
			
				
			Map<String, Object> dataMap = new HashMap<String, Object>();
			
			String sellerCode = (String)xp.evaluate("@SellerOrganizationCode", shipNodeList.item(i), XPathConstants.STRING);
			String shipmentNo = (String)xp.evaluate("@ShipmentNo", shipNodeList.item(i), XPathConstants.STRING);
			String shipmentNoCube = (String)xp.evaluate("@PickticketNo", shipNodeList.item(i), XPathConstants.STRING);
			
			String shipNode = (String)xp.evaluate("@ShipNode", shipNodeList.item(i), XPathConstants.STRING);
			String shipNodeString = "";
			if(shipNode != null && !"".equals(shipNode)){
				shipNodeString = shipNode+ " ("+env.getProperty(shipNode)+")";
			}
			
			String status = (String)xp.evaluate("@Status", shipNodeList.item(i), XPathConstants.STRING);
			String scac = (String)xp.evaluate("@SCAC", shipNodeList.item(i), XPathConstants.STRING);
			String scacService = (String)xp.evaluate("@ScacAndService", shipNodeList.item(i), XPathConstants.STRING);
			String trackingNo = (String)xp.evaluate("@TrailerNo", shipNodeList.item(i), XPathConstants.STRING);
			
			String aShipDate = (String)xp.evaluate("@ActualShipmentDate", shipNodeList.item(i), XPathConstants.STRING);
			aShipDate = CommonUtil.getDateTimeByTimeZone(aShipDate);
			String eShipDate = (String)xp.evaluate("@ExpectedShipmentDate", shipNodeList.item(i), XPathConstants.STRING);
			eShipDate = CommonUtil.getDateTimeByTimeZone(eShipDate);
			
			dataMap.put("sellerCode", sellerCode);
			dataMap.put("shipmentNo", shipmentNo);
			dataMap.put("shipmentNoCube", shipmentNoCube);
			dataMap.put("shipNode", shipNode);
			dataMap.put("shipNodeString", shipNodeString);
			dataMap.put("status", status);
			dataMap.put("scac", scac);
			dataMap.put("scacService", scacService);
			dataMap.put("trackingNo", trackingNo);
			dataMap.put("aShipDate", aShipDate);
			dataMap.put("eShipDate", eShipDate);
			
			shipList.add(dataMap);
		}
			
		
		return shipList;
		
	}

}

package com.isec.sc.intgr.api.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.text.MessageFormat;
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
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.report.OrderReportService;

@Controller
@RequestMapping(value="/sc")
public class ScOrderStatusHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderStatusHandler.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	// Report Data 생성을 위한 Service Bean
	@Autowired	private OrderReportService orderReportService;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;	
	
	@Value("${sc.api.getOrderReleaseDetails.template}")
	private String GET_ORDER_RELEASE_DETAILS_TEMPLATE;
	
	@Value("${sc.api.order.getOrderReleaseDetails}")
	private String GET_ORDER_RELEASE_DETAILS_API;
	
	/**
	 * 오더 생성 후처리 메서드
	 *  1. 오더생성 결과 MA 전송 
	 *  2. 오더자동릴리즈(주문확정)을 위해 order:release 키에 오더정보 전송
	 *  1. Report Data 생성 - 오더건수, 금액 집계 처리
	 * 
	 * 
	 * @param returnXML
	 * @param status
	 * @param res
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderCreateAfter.do")
	public void createOrderAfter(@RequestParam String returnXML,
			  HttpServletResponse res) throws Exception{
		
		
		if( returnXML == null || "".equals(returnXML)){
			
			// TODO: createOrder 후처리 예외처리필요
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		logger.debug("[Order Create Hanlder Started]");
		logger.debug("[returnXML]"+returnXML);
		
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element outputXML = doc.getDocumentElement();
		
		String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
		String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
		//  Created Status Check 
		if( !"1100".equals(minOrderStatus) || !"1100".equals(maxOrderStatus)){
			
			// TODO: createOrder 후처리 예외처리
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		String entCode = outputXML.getAttribute("EnterpriseCode");
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");	// 판매조직코드
		String orderHeaderKey = outputXML.getAttribute("OrderHeaderKey");
		String orderId = outputXML.getAttribute("OrderNo");
		String docType =  outputXML.getAttribute("DocumentType");
		
		logger.info("[entCode]"+entCode);
		logger.info("[sellerCode]"+sellerCode);
		logger.info("[orderHeaderKey]"+orderHeaderKey);
		logger.info("[orderId]"+orderId);
		logger.info("[docType]"+docType);

		
		//-------------------- Store Redis Data For Ma & Release
		HashMap<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("status", "1100");
		resultMap.put("orderHeaderKey", orderHeaderKey);
		resultMap.put("entCode", entCode);
		resultMap.put("orderId", orderId);
		resultMap.put("docType", docType);
		resultMap.put("sellerCode", sellerCode);
		
		ObjectMapper mapper = new ObjectMapper();
		String orderSuccJSON = mapper.writeValueAsString(resultMap);
		logger.debug("[Update OrderStatus - Redis Data]"+orderSuccJSON);
		
		
		// Put OrderStaus to Redis For MA
		String redisPushKey = entCode+":"+sellerCode+":order:update:S2M";
		listOps.leftPush(redisPushKey, orderSuccJSON);
		
		
		// Put OrderStaus to Redis For SC - OMC에 자동릴리즈를 하기 위해 별도의 릴리즈대상 키에 생성된 오더정보 저장
		String redisPushKeyForRelease = entCode+":"+sellerCode+":order:release";
		listOps.leftPush(redisPushKeyForRelease, orderSuccJSON);
		
		
		
		//-------------------- Order Data Summary
		logger.debug("[Order Summary Process Started]");
		Double totAmount = 0.00;
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
		XPath xp = XPathFactory.newInstance().newXPath();
		totAmount = (Double)xp.evaluate("/Order/OverallTotals/@GrandTotal", outputXML, XPathConstants.NUMBER);
		
		totLineSub = (Double)xp.evaluate("/Order/OverallTotals/@LineSubTotal", outputXML, XPathConstants.NUMBER);
		totCharge = (Double)xp.evaluate("/Order/OverallTotals/@GrandCharges", outputXML, XPathConstants.NUMBER);
		totDiscount = (Double)xp.evaluate("/Order/OverallTotals/@GrandDiscount", outputXML, XPathConstants.NUMBER);
		totTax = (Double)xp.evaluate("/Order/OverallTotals/@GrandTax", outputXML, XPathConstants.NUMBER);
		
		/*
		NodeList orderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine", outputXML, XPathConstants.NODESET);
		
		for( int i=0; i< orderLineList.getLength(); i++){
			
			totAmount +=  (Double)xp.evaluate("LineOverallTotals/@LineTotal", orderLineList.item(i), XPathConstants.NUMBER);
			totCharge +=  (Double)xp.evaluate("LineOverallTotals/@Charges", orderLineList.item(i), XPathConstants.NUMBER);
			totDiscount +=  (Double)xp.evaluate("LineOverallTotals/@Discount", orderLineList.item(i), XPathConstants.NUMBER);
			totTax +=  (Double)xp.evaluate("LineOverallTotals/@Tax", orderLineList.item(i), XPathConstants.NUMBER);
			
		}
		*/
		HashMap<String, Double> priceMap = new HashMap<String, Double>();
		priceMap.put("amount", totAmount);
		priceMap.put("lineTotal", totLineSub);
		priceMap.put("charge", totCharge);
		priceMap.put("discount", -totDiscount);
		priceMap.put("tax", totTax);
		
		// Order Report Service 호출
		orderReportService.saveOrderReportData(entCode, sellerCode, priceMap);
		
		
		logger.debug("[Order Create Hanlder End]");
		
		// 호출한 Sterling 서비스에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
	}
	
	
	/**
	 * 오더상태 업데이트 후처리
	 * 
	 * SC에서 오더의 상태가 변경된 경우 해당 트랜잭션의 EventHandler를 통해 전송된 Output XML(Order Detail)를 
	 * Parsing하여 MA 또는 CA(Cube)로 필요한 정보를 전송-Redis에 기록 - 하는 메서드   
	 * 
	 *  - 처리대상 오더상태
	 *   - Released(3200) - Partially Released 포함
	 *   - Back Ordered(1300)
	 *   - Cancelled (9000)
	 *   
	 *  Shipment처리는 ScOrderShipmentHandler가 담당 
	 *   
	 * @param returnXML 변경된 주문의 상세정보
	 * @param status 주문상태
	 * @param res SC에 반환하는 응답객채
	 * @throws Exception
	 */
	@RequestMapping(value = "/orderUpdate.do")
	public void updateOrderStatus(@RequestParam(required=false) String returnXML,
								  HttpServletResponse res) throws Exception{
		
		
		if( returnXML == null || "".equals(returnXML)){
			
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		
//		logger.debug("[return XML]"+new String(returnXML.getBytes("8859_1"), "UTF-8"));
		logger.debug("[return XML]"+returnXML);
		
		
		try{
		
			// 1. Receive Message(retrunXML) Parsing
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
			Element outputXML = doc.getDocumentElement();
			
			// 공통정보 추출
			String entCode = outputXML.getAttribute("EnterpriseCode"); // 조직코드
			String sellerCode = outputXML.getAttribute("SellerOrganizationCode");	// 판매조직코드
			String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
			String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
			
			logger.info("[minOrderStatus]"+minOrderStatus);
			logger.info("[maxOrderStatus]"+maxOrderStatus);
			
			// 조직코드, 판매조직으로 Redis 저장키 생성
			
			
			// Scheduled 
			if("1500".equals(maxOrderStatus))
			{
				// TODO: Scheduled Event Handler 처리. 현재 사용안함
			}
			
			
			// Released or Partially Released
			else if("3200".equals(maxOrderStatus))
			{
				// 전체 Released일 경우만 후처리
				if(minOrderStatus.equals(maxOrderStatus)){
					
					
					/*
					 *  TODO: 현재 최초 릴리즈시 오더릴리즈상세정보 또는 오더릴리즈목록정보 조회 API가 데이타를 못가져옴.
					 *        릴리즈를 한번 더 수행시 HasStateChanged = 'N'으로 바뀌고 정상조회됨.
					 *        HasStateChanged 값을 체크해서 'Y'일 경우 scheduleOrder API를 다시한번 호출처리함
					 */
					String hasStateChanged = outputXML.getAttribute("HasStateChanged"); // 오더유형
					if(hasStateChanged != null && "Y".equals(hasStateChanged)){
						
						logger.debug("[Order Release Again!!!]");
						
						String orderId = outputXML.getAttribute("OrderNo");
						String docType =  outputXML.getAttribute("DocumentType");
						logger.info("[orderId]"+orderId);
						logger.info("[docType]"+docType);
	
						//-------------------- Store Redis Data For Ma & Release
						HashMap<String, String> resultMap = new HashMap<String, String>();
						resultMap.put("entCode", entCode);
						resultMap.put("orderId", orderId);
						resultMap.put("docType", docType);
						
						ObjectMapper mapper = new ObjectMapper();
						String orderSuccJSON = mapper.writeValueAsString(resultMap);
						logger.debug("[Update OrderStatus - Redis Data]"+orderSuccJSON);
						
						
						// Put OrderStaus to Redis For SC - OMC에 자동릴리즈를 하기 위해 별도의 릴리즈대상 키에 생성된 오더정보 저장
						String redisPushKeyForRelease = entCode+":"+sellerCode+":order:release";
						listOps.leftPush(redisPushKeyForRelease, orderSuccJSON);
						
						return;
					}
					logger.debug("[Order Release Hanlder Started]");
					processReleaseAfter(outputXML, entCode, sellerCode);
					
				}else{
					
					logger.debug("[Partially Released  Started]");
					// TODO: Partially Released일 경우 운영자 Alert처리 
				}
			}
			
			
			// Back Ordered
			else if("1300".equals(maxOrderStatus))
			{
				logger.debug("[Order BackOrder Hanlder Started]");
				
				/*
				 * TODO: Backordered 된 건 처리 - 사용안함
				 * - WCS(MI:JNS)의 경우는 부분취소가 현재 불가능함
				 * - 따라서 Released단계에서  BackOrdered 또는 Canceled 된 건이 하나라도 존재할 경우
				 *   Released결과를 보내주지 않고 Skip처리함. Sterling에서 전체취소처리후 취소정보를 전달해야 함.  
				 */
				// Output XML Parsing
				XPath xp = XPathFactory.newInstance().newXPath();
				
				NodeList canceledList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@Status='BackOrdered' or @Status='Cancelled']", outputXML, XPathConstants.NODESET);
				if("JNS".equals(sellerCode)){
					if(canceledList.getLength() > 0){
						logger.debug("This Order cannot be fulFilled!!");
						return;
					}
				}
			}
			
			
			// Cancelled (전체취소)
			else if("9000".equals(maxOrderStatus))
			{
				
				logger.debug("[Order Cancel Hanlder Started]");
				processCancelAfter(outputXML, entCode, sellerCode);
			}
		
		}catch(Exception e){
			
			// TODO: 예외처리
			e.printStackTrace();
		}
		finally
		{
			try{
				// 호출한 Sterling Transaction에 Response 전달
				logger.debug("[Order Update Event Hanlder End !!!]");
				res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			}catch(Exception e){
				
			}
		}
		
	}
	
	
	
	/**
	 *  Scheduled or Partially Scheduled - 현재 사용안함(Outro Test 전용)
	 *  
	 *  - 이 단계에서는 부분스케쥴이 되더라도 Magento로는 해당 오더건에 대해 전체 스케쥴상태로 보낸다.
	 *   ( Magento의 상태가 Order Placed상태 즉, 인보이스 생성전에는 Magento에서 부분취소가 불가하기 때문)
	 *  - Release단계에서 출고가 가능한 상품정보를 보내면 Magento는 해당건에 대해서만 인보이스를 발행하고 나머지 상품은 Cancel처리한다. 

	 * @param outputXML
	 * @param sendMsgMap
	 */
	private void updateScheduleStauts(Element outputXML, String entCode, String sellerCode) throws Exception{
	
		
		// 공통정보
		String orderKey = outputXML.getAttribute("OrderHeaderKey"); // 오더헤더키
		String orderNo = outputXML.getAttribute("OrderNo");	// 오더번호
		String docType = outputXML.getAttribute("DocumentType"); // 오더유형
		String orderStatus = outputXML.getAttribute("Status"); // 오더상태 Text
		String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
		String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
		
		
		HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
		sendMsgMap.put("orderHeaderKey", orderKey);
		sendMsgMap.put("docType", docType);
		sendMsgMap.put("orderId", orderNo);
		sendMsgMap.put("entCode", entCode);
		sendMsgMap.put("sellerCode", sellerCode);
		sendMsgMap.put("status", maxOrderStatus);
		
		// Output XML Parsing
		// TODO: 현재 WCS채널인 경우에만 스케츌단계 연동제외처리. 향후 모든 채널에 적용
		if("JNS".equals(sellerCode)){
			return;
		}
		
		// Send Messgage Setting
		sendMsgMap.put("canceled", "false");
		
		
		// JSON 변환
		ObjectMapper mapper = new ObjectMapper();
		String outputMsg = mapper.writeValueAsString(sendMsgMap);
		logger.debug("[outputMsg]"+outputMsg);
		
		
		// RedisKey for SC -> MA
		String pushKey = entCode+":"+sellerCode+":order:update:S2M";
		logger.debug("[outputKey]"+pushKey);
		
		// Push Data
		listOps.leftPush(pushKey, outputMsg);
	}
	
	
	/**
	 * Schedule & Release 후처리 프로세스(주문확정전송, SC->Cube) 
	 * 
	 * 
	 * 정상적으로 Released 상태로 변경된 주문정보를 Cube로 전송 - Cube가 수신하는 Redis Key - 하는 메서드
	 * TODO: Ma전송은 큐브에서 정상처리 응답수신후 처리하는 방향 고민 ( OrderProcessTask 에서 처리 )
	 * 
	 * @param outputXML
	 * @param sendMsgMap
	 * @param pushKey Cube가 사용하는 Redis Key
	 * @throws Exception
	 */
	private void processReleaseAfter(Element outputXML, String entCode, String sellerCode) throws Exception{
		
			
			// 공통정보 추출
			/*
			   // 오더라인 Status정보
               <OrderStatuses>
                <OrderStatus OrderHeaderKey="20140902154728116830"
                    OrderLineKey="20140902154728116831"
                    OrderLineScheduleKey="2014090416042811123200"
                    OrderReleaseKey="20140904162811123199"
                    OrderReleaseStatusKey="2014090416352811123201"
                    PipelineKey="SALES-9.3" Status="3200"
                    StatusDate="2014-09-04T16:28:11+09:00"
                    StatusDescription="Released" StatusQty="2.00" TotalQuantity="2.00"/>
               </OrderStatuses>
               // 수취인 주소, 주문자 주소
			   <PersonInfoShipTo AddressLine1="abcd" AddressLine2=""
			        AddressLine3="" AddressLine4="" AddressLine5="" AddressLine6=""
			        AlternateEmailID="" Beeper="" City="California" Company=""
			        Country="US" DayFaxNo="" DayPhone="1234567" Department=""
			        EMailID="yg.jang@isecommerce.co.kr" EveningFaxNo=""
			        EveningPhone="" FirstName="sc" JobTitle="" LastName="team"
			        MiddleName="" MobilePhone="1234567" OtherPhone="" PersonID=""
			        PersonInfoKey="2014061020442933964" State="" Suffix="" Title="" ZipCode="12345"/>
	           
			 */
			
			HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
			
			/*
			 * 1. 큐브전송
			 * 
			 * SC 필수항목
			 * - 오더번호, 오더라인키, 오더유형, 관리조직, 셀러, 상태(3200)
			 * 
			 * 큐브전송항목
			 * - 창고코드
			 * - 출고의뢰일자, 주문번호, 주문순번, 주문생성일, 상품코드, 상품명, 수량, 판매가(개별단가), 고객명(수취인), 우편번호, 수취인주소, 고객/수취인HP, 고객/수취인TEL
			 * - 배송메세지
			 */
			
			// Output XML Parsing
			XPath xp = XPathFactory.newInstance().newXPath();
						
			// 주문기본정보
			String docType = outputXML.getAttribute("DocumentType"); // 오더유형
			String orderNo = outputXML.getAttribute("OrderNo");	// 오더번호
			String orderKey = outputXML.getAttribute("OrderHeaderKey"); // 오더헤더키
			String orderDate = outputXML.getAttribute("OrderDate"); // 오더생성일
			orderDate = orderDate.substring(0,4)+orderDate.substring(5,7)+orderDate.substring(8,10);
			
			String vendorId = outputXML.getAttribute("VendorID"); 
			logger.debug("[vendorId]"+vendorId);
			logger.debug("[vendorId]"+vendorId);
			logger.debug("[vendorId]"+vendorId);
			
			
//			String orderStatus = outputXML.getAttribute("Status"); // 오더상태 Text
//			String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
//			String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
			
			// 수취인 주소정보 PersonInforShipTo - 수취인명, 수취인전화번호, 수취인휴대전화번호, 수취인기본주소, 수취인상세주소, 수취인우편번
			String receiptNm = (String)xp.evaluate("PersonInfoShipTo/@FirstName", outputXML, XPathConstants.STRING)
		    						+(String)xp.evaluate("PersonInfoShipTo/@LastName", outputXML, XPathConstants.STRING);
			receiptNm = receiptNm.trim();
			String receiptTel = (String)xp.evaluate("PersonInfoShipTo/@DayPhone", outputXML, XPathConstants.STRING);
			String receiptHp = (String)xp.evaluate("PersonInfoShipTo/@MobilePhone", outputXML, XPathConstants.STRING);
			String receiptAddr1 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine1", outputXML, XPathConstants.STRING);
			String receiptAddr2 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine2", outputXML, XPathConstants.STRING);
			String receiptZipcode = (String)xp.evaluate("PersonInfoShipTo/@ZipCode", outputXML, XPathConstants.STRING);
			
			
//			logger.debug("[receiptNm]"+receiptNm);
//			logger.debug("[receiptAddr1]"+receiptAddr1);
//			logger.debug("[receiptAddr2]"+receiptAddr2);
			
			// 주문자 주소 PersonInfoBillTo - 주문자명, 주문자전화번호, 주문자휴대전화번
			String custNm = (String)xp.evaluate("PersonInfoBillTo/@FirstName", outputXML, XPathConstants.STRING)
							+(String)xp.evaluate("PersonInfoBillTo/@LastName", outputXML, XPathConstants.STRING);
			custNm = custNm.trim();
			String custTel = (String)xp.evaluate("PersonInfoBillTo/@DayPhone", outputXML, XPathConstants.STRING);
			String custHp = (String)xp.evaluate("PersonInfoBillTo/@MobilePhone", outputXML, XPathConstants.STRING);
//			String custAddr1 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine1", outputXML, XPathConstants.STRING);
//			String custAddr2 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine2", outputXML, XPathConstants.STRING);
//			String custZipcode = (String)xp.evaluate("PersonInfoBillTo/@ZipCode", outputXML, XPathConstants.STRING);
			
			
			/*
			 *  배송메세지
			 *   <Instructions NumberOfInstructions="1">
			        <Instruction InstructionText="aaa" InstructionType="DLV_MSG" InstructionURL=""/>
			    </Instructions>
			 * 
			 */
			String deliveryMsg = (String)xp.evaluate("Instructions/Instruction[@InstructionType='DLV_MSG']/@InstructionText", outputXML, XPathConstants.STRING);
			logger.debug("[deliveryMsg]"+deliveryMsg);
			logger.debug("[deliveryMsg]"+deliveryMsg);
			logger.debug("[deliveryMsg]"+deliveryMsg);
			
		
			// 정상 Released가 된 OrderLine정보만 추출
			NodeList releaseOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@MinLineStatus='3200' and @MaxLineStatus='3200']", outputXML, XPathConstants.NODESET);
			logger.debug("[release Count]"+releaseOrderLineList.getLength());
			
			// Release 대상건이 없는 경우 후처리 중단. 
			if(releaseOrderLineList.getLength() == 0){
				return;
			}
			
			
			
			List<HashMap<String,Object>> confirmList = new ArrayList<HashMap<String,Object>>();
			
			for(int i=0; i<releaseOrderLineList.getLength(); i++){
				Node lineNode = releaseOrderLineList.item(i);
				HashMap<String, Object> orderLineMap = new HashMap<String, Object>();
				
				String orderLineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String primeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus/@OrderReleaseKey", lineNode, XPathConstants.STRING);
				
				// ShipNode 조회 - getOrderReleaseDetails
//				String shipNode = "WH001";	//호법창고
				String shipNode = "";	//호법창고
//				String orderReleaseDetailXML = FileContentReader.readContent(getClass().getResourceAsStream(GET_ORDER_RELEASE_DETAILS_TEMPLATE));
//				Document doc = null;
//				    MessageFormat msg = new MessageFormat(orderReleaseDetailXML);
//					String inputXML = msg.format(new String[] {orderReleaseKey} );
//					logger.debug("[releaseDetails inputXML]"+inputXML); 
//					String releaseDetails = sterlingApiDelegate.comApiCall(GET_ORDER_RELEASE_DETAILS_API, inputXML);
//					doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(releaseDetails.getBytes("UTF-8")));
//					logger.debug("[releaseDetails outputXML]"+releaseDetails);
//					shipNode = doc.getDocumentElement().getAttribute("ShipNode");
					
					logger.debug("[orderNo]"+orderNo); 
					logger.debug("[orderReleaseKey]"+orderReleaseKey); 
					
					shipNode  = sterlingApiDelegate.getShipNodeByReleaseKey(orderNo, orderReleaseKey);
					
					logger.debug("[shipNode]"+shipNode); 
					logger.debug("[shipNode]"+shipNode); 
					logger.debug("[shipNode]"+shipNode); 
				// ShipNode 조회 End
				
				
				// 상품정보, 가격정보
				String itemID = (String)xp.evaluate("Item/@ItemID", lineNode, XPathConstants.STRING);
				String itemNm = (String)xp.evaluate("Item/@ItemShortDesc", lineNode, XPathConstants.STRING);
				Double pricingQty = (Double)xp.evaluate("LineOverallTotals/@PricingQty", lineNode, XPathConstants.NUMBER);	// 주문수량
				Double lineTotal = (Double)xp.evaluate("LineOverallTotals/@LineTotal", lineNode, XPathConstants.NUMBER);	// 오더라인 최종판매금액(배송비,과세,할인 적용금액)
				Double salePrice = (Double)xp.evaluate("LineOverallTotals/@UnitPrice", lineNode, XPathConstants.NUMBER);	// 개별판매단가(배송비,과세,할인 미적용금액)
				int cubePrice = (int)(lineTotal/pricingQty);	// 큐브전송 개별판매단가(배송비,과세,할인 적용금액을 수량으로 나눔)
				
				
				//---------------------  오더라인 단위정보 저장 for JSON ---------------------//
				// 오더라인 공통정보 (동일한 값)
				orderLineMap.put("org_code", env.getProperty("ca."+entCode)); // TODO: 관리조직코드 -> 사업부코드로 변환
				orderLineMap.put("sell_code", sellerCode);	// TODO: 셀러코드는 SC의 코드사용
				orderLineMap.put("ship_node", shipNode);
				orderLineMap.put("orderId", orderNo);
				orderLineMap.put("orderDt", orderDate);
				
				// 오더라인순/오더라인키/오더릴리즈키
				orderLineMap.put("orderLineNo", primeLineNo);
				orderLineMap.put("orderLineKey", orderLineKey);
				orderLineMap.put("orderReleaseKey", orderReleaseKey);
				
				// 수취인/주문자 정보 
				orderLineMap.put("receiptNm", receiptNm);
				orderLineMap.put("receiptTel", receiptTel);
				orderLineMap.put("receiptHp", receiptHp);
				orderLineMap.put("receiptAddr1", receiptAddr1);
				orderLineMap.put("receiptAddr2", receiptAddr2);
				orderLineMap.put("receiptZipcode", receiptZipcode);
				orderLineMap.put("custNm", custNm);
				orderLineMap.put("custTel", custTel);
				orderLineMap.put("custHp", custHp);
//				orderLineMap.put("custAddr1", custAddr1);
//				orderLineMap.put("custAddr2", custAddr2);
//				orderLineMap.put("custZipcode", custZipcode);
				
				
				// 상품/가격 정보
				orderLineMap.put("itemId", itemID);
				orderLineMap.put("itemNm", itemNm);
				orderLineMap.put("qty", pricingQty.intValue()+"");
				orderLineMap.put("salePrice", cubePrice+"");
				
				// TODO: 배송메세지 전송가능여부 확인(MA)
				orderLineMap.put("deliveryMsg", deliveryMsg);	
				
				confirmList.add(orderLineMap);
			} // End for ReleaseList
			
			
			// Order 레벨 정보 저장
			String orgCode = env.getProperty("ca."+entCode); // 사업부코드 - 조직코드 변환
			sendMsgMap.put("org_code", orgCode);
			sendMsgMap.put("sell_code", sellerCode);
			sendMsgMap.put("vendor_id", vendorId);	// 2차DOS채널
			sendMsgMap.put("orderId", orderNo);
			sendMsgMap.put("orderDt", orderDate);
			sendMsgMap.put("orderHeaderKey", orderKey);
			sendMsgMap.put("status", "3200");
			String tranDt = CommonUtil.cuurentDateFromFormat("yyyyMMddHHssmm");
			sendMsgMap.put("tranDt", tranDt);
			// OrderLine 확정정보 저장
			sendMsgMap.put("list", confirmList);
			
			// JSON 변환
			ObjectMapper mapper = new ObjectMapper();
			String outputMsg = mapper.writeValueAsString(sendMsgMap);

			// RedisKey for SC -> CUBE
			String pushKey = orgCode+":"+sellerCode+":order:update:S2C";
			
			logger.debug("[3200 S2C - trans Data]"+outputMsg);
			logger.debug("[3200 S2C - trans Key]"+pushKey);
			
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey, outputMsg);
			
			
			// RedisKey for CUBE -> SC 
			// TODO: Test 용, 큐브연동후 삭제 할 것
			/*sendMsgMap.put("status", "3202"); // 3202
			List<HashMap<String,Object>> returnList = confirmList;
			for(int i=0; i<returnList.size(); i++){
				returnList.get(i).put("shipmentNo", "0000"+(i+1));
				returnList.get(i).put("statuscd", "01");
				returnList.get(i).put("statusMsg", "");
			}
			sendMsgMap.remove("list");
			sendMsgMap.put("list", returnList);
			
			String outputMsgTest = mapper.writeValueAsString(sendMsgMap);
			
			String mapEntCode = env.getProperty("ca."+entCode);
			String mapSellerCode = sellerCode;
			
			String pushKey_CubetoSC = mapEntCode+":"+mapSellerCode+":order:update:C2S";
			
			
			logger.debug("[3202 C2S - trans data]"+outputMsgTest);
			logger.debug("[3202 C2S - trans Key]"+pushKey_CubetoSC);
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey_CubetoSC, outputMsgTest);*/
			
			
			logger.debug("####[Order Release Event Hanlder End]");
		
				
	}
	
	
	/**
	 * 주문취소 후처리 프로세스 (SC->Ma) 9000
	 *  - SC의 주문취소처리 후 SC에서 Ma로 주문취소정보를 전달
	 *  - 전체취소, 부분취소 모두 해당
	 * 
	 * @param outputXML
	 * @param sendMsgMap
	 * @param pushKey
	 * @throws Exception
	 */
	private void processCancelAfter(Element outputXML, String entCode, String sellerCode) throws Exception{
		
		logger.debug("[Order Cancel Hanlder Started]");
		
		try{
		
			// 공통정보 추출
			String orderKey = outputXML.getAttribute("OrderHeaderKey");
			String orderNo = outputXML.getAttribute("OrderNo");	// 오더번호
			String docType = outputXML.getAttribute("DocumentType"); // 오더유형
			String orderStatus = outputXML.getAttribute("Status"); // 오더상태 Text
			String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
			String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
			
			
			HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
			
			
			// Cancelled된 OrderLine정보만 추출
			// Output XML Parsing
			XPath xp = XPathFactory.newInstance().newXPath();
					
			NodeList cancellOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@MinLineStatus='9000' and @MaxLineStatus='9000']", outputXML, XPathConstants.NODESET);
			logger.debug("[cancelled Count]"+cancellOrderLineList.getLength());
			
			List<HashMap<String,String>> cancelList = new ArrayList<HashMap<String,String>>();
			
			for(int i=0; i<cancellOrderLineList.getLength(); i++){
				Node lineNode = cancellOrderLineList.item(i);
				HashMap<String, String> orderLineMap = new HashMap<String, String>();
				
				String orderLineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String primeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				
				// Release 전 취소일 경우 값 없음
				String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus/@OrderReleaseKey", lineNode, XPathConstants.STRING);
				
				String itemID = (String)xp.evaluate("Item/@ItemID", lineNode, XPathConstants.STRING);
//				String orderedQty = (String)xp.evaluate("@OrderedQty", lineNode, XPathConstants.STRING);
//				String orderedQty = (String)xp.evaluate("@ChangeInOrderedQty", lineNode, XPathConstants.STRING);
				String orderedQty = (String)xp.evaluate("@OriginalOrderedQty", lineNode, XPathConstants.STRING);
				String itemNm = (String)xp.evaluate("@ItemShortDesc", lineNode, XPathConstants.STRING);
				String salePrice = (String)xp.evaluate("LineOverallTotals/@UnitPrice", lineNode, XPathConstants.STRING);
				String lineTotal = (String)xp.evaluate("LineOverallTotals/@LineTotal", lineNode, XPathConstants.STRING);
				
				// 오더라인번호/순번/릴리즈키/전송일시
				orderLineMap.put("orderLineKey", orderLineKey);
				orderLineMap.put("primeLineNo", primeLineNo);
				orderLineMap.put("orderReleaseKey", orderReleaseKey);
//				orderLineMap.put("orderDt", CommonUtil.cuurentDateFromFormat("yyyyMMddHHmmss"));
				// 상품/가격 정보
				orderLineMap.put("itemId", itemID);
				orderLineMap.put("qty", orderedQty);
				orderLineMap.put("itemNm", itemNm);
				orderLineMap.put("salePrice", salePrice);
				orderLineMap.put("lineTotal", lineTotal);
				
				cancelList.add(orderLineMap);
			} // End for
			
	
			// Send Message Setting
			sendMsgMap.put("orderHeaderKey", orderKey);
			sendMsgMap.put("docType", docType);
			sendMsgMap.put("orderId", orderNo);
			sendMsgMap.put("entCode", entCode);
			sendMsgMap.put("sellerCode", sellerCode);
			sendMsgMap.put("status", "9000");
			String trDate = CommonUtil.cuurentDateFromFormat("yyyyMMddHHmmss");
			sendMsgMap.put("trDate", trDate);
			sendMsgMap.put("cancelled", cancelList);
			
			// JSON 변환
			/*
			 * {"orderHeaderKey":"20140917162100147011",
			 * "docType":"0001","status":"9000",
			 * "cancelled":[
			 * 		{"orderLineKey":"20140917162100147012","lineTotal":"0.00","orderReleaseKey":"","primeLineNo":"1","itemNm":"","salePrice":"49000.00",
			 * 			"qty":"0.00","itemId":"ASPB_ITEM_0001"},
			 * 		{"orderLineKey":"20140917162100147013","lineTotal":"0.00","orderReleaseKey":"","primeLineNo":"2","itemNm":"","salePrice":"59000.00",
			 * 			"qty":"0.00","itemId":"ASPB_ITEM_0002"},
			 * 		{"orderLineKey":"20140917162100147014","lineTotal":"0.00","orderReleaseKey":"","primeLineNo":"3","itemNm":"","salePrice":"59000.00",
			 * 			"qty":"0.00","itemId":"ASPB_ITEM_0003"}
			 * ],"sellerCode":"ASPB","trDate":"20140917163656","orderId":"Y100000406","entCode":"SLV"}
			 * 
			 */
			ObjectMapper mapper = new ObjectMapper();
			String outputMsg = mapper.writeValueAsString(sendMsgMap);
			
			// RedisKey for SC -> MA
			String pushKey = entCode+":"+sellerCode+":order:update:S2M";
			
			logger.debug("[cancel Data]"+outputMsg);
			logger.debug("[pushKey]"+pushKey);
			
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey, outputMsg);
			
			
			
			// 취소요청 RedisKey에 있는 데이타 삭제
			// 조직코드:채널코드:order:cancel 
			String cancelReqKey = entCode+":"+sellerCode+":order:cancel";
			
			List<String> cancelReqRedisList = listOps.range(cancelReqKey, 0, -1);
			for( int i=0; i<cancelReqRedisList.size(); i++){
				
				String jsonData = cancelReqRedisList.get(i);
				
//				logger.debug("[jsonData]"+ jsonData);
				HashMap<String,String> cancelReqMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				
				String cancelOrderNo = cancelReqMap.get("orderNo");
				
				if(orderNo.equals(cancelOrderNo)){
					logger.debug("[cancelOrderNo]"+cancelOrderNo);
					listOps.remove(cancelReqKey, i, jsonData);
					break;
				}
			}
			
			// 취소요청결과 RedisKey에 있는 데이타 삭제
			// 조직코드:채널코드:order:cancel:result
			String cancelResKey = entCode+":"+sellerCode+":order:cancel:result";
			
			List<String> cancelResRedisList = listOps.range(cancelResKey, 0, -1);
			for( int i=0; i<cancelResRedisList.size(); i++){
				
				String jsonData = cancelResRedisList.get(i);
				
//				logger.debug("[jsonData]"+ jsonData);
				HashMap<String,String> cancelResMap = new ObjectMapper().readValue(jsonData, new TypeReference<HashMap<String,String>>(){});
				
				String cancelOrderNo = cancelResMap.get("orderNo");
				
				if(orderNo.equals(cancelOrderNo)){
					logger.debug("[cancelOrderNo]"+cancelOrderNo);
					listOps.remove(cancelResKey, i, jsonData);
					break;
				}
			}
			
			
			// 집계데이타 저장
			// TODO: 취소금액 항목 확인필요
			double totCancelAmount = 0.00;
			totCancelAmount = (Double)xp.evaluate("/Order/PriceInfo/@ChangeInTotalAmount", outputXML, XPathConstants.NUMBER);
			logger.debug("[totCancelAmount]"+totCancelAmount);
			
			// Order Report Service 호출
			orderReportService.saveCancelOrderAmount(entCode, sellerCode, totCancelAmount);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
		
		logger.debug("##### [Order Cancel Hanlder End]");
	}
}

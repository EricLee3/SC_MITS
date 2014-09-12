package com.isec.sc.intgr.api.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.report.OrderReportService;

@Controller
@RequestMapping(value="/sc")
public class ScOrderStatusHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(ScOrderStatusHandler.class);
	
	
	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	
	// Report Data 생성을 위한 Service Bean
	@Autowired	private OrderReportService orderReportService;
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;	
	
	
	/**
	 * 오더 최소생성 후 처리
	 *  1. 오더건수, 금액 집계
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
			
			// TODO: 예외처리필요
			
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
			
			res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
			return;
		}
		
		String entCode = outputXML.getAttribute("EnterpriseCode");
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");	// 판매조직코드
		logger.info("[entCode]"+entCode);
		logger.info("[sellerCode]"+sellerCode);
		
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
		
		
		logger.debug("[returnXML]"+returnXML);
		
		// 1. Receive Message(retrunXML) Parsing
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(returnXML.getBytes("UTF-8")));
		Element outputXML = doc.getDocumentElement();
		
		// 공통정보 추출
		String entCode = outputXML.getAttribute("EnterpriseCode"); // 조직코드
		String sellerCode = outputXML.getAttribute("SellerOrganizationCode");	// 판매조직코드
		String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
		String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
		
		logger.info("[maxOrderStatus]"+maxOrderStatus);
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
			// Released일 경우만 후처리
			if(minOrderStatus.equals(maxOrderStatus)){
				
				logger.debug("[Order Release Hanlder Started]");
				
				processReleaseAfter(outputXML, entCode, sellerCode);
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
			processCancelAfter(outputXML, entCode, sellerCode);
		}
		
		
		// 호출한 Sterling Transaction에 Response 전달
		res.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TransferSuccess/>");
		
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
		
		try{
			
			// 공통정보 추출
			String orderKey = outputXML.getAttribute("OrderHeaderKey");
			String orderNo = outputXML.getAttribute("OrderNo");	// 오더번호
			String docType = outputXML.getAttribute("DocumentType"); // 오더유형
			String orderStatus = outputXML.getAttribute("Status"); // 오더상태 Text
			String maxOrderStatus = outputXML.getAttribute("MaxOrderStatus"); // 최소 오더상태 코드값
			String minOrderStatus = outputXML.getAttribute("MinOrderStatus");	// 최대 오더상태 코드값
			
			
			HashMap<String,Object> sendMsgMap = new HashMap<String,Object>();
			
			/*
			 * 1. 큐브전송
			 * 
			 * SC 필수항목
			 * - 오더번호, 오더유형, 관리조직, 셀러, 상태(3200)
			 * 
			 * 큐브전송항목
			 * - 출고의뢰일자, 주문번호, 상품코드, 상품명, 수량, 판매가, 고객명(수취인), 우편번호, 수취인주소, 고객/수취인HP, 고객/수취인TEL
			 * 
			 * 
			 *     [오더전체가격정보]
			 		<PriceInfo Currency="" EnterpriseCurrency="" ReportingConversionDate="" ReportingConversionRate="" TotalAmount=""/>
	    			   <OverallTotals GrandCharges="" GrandDiscount="" GrandTax=""
	        				GrandTotal="" HdrCharges="" HdrDiscount="" HdrTax="" HdrTotal="" LineSubTotal=""/>
	               [오더라인 Status정보]
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
	            		
			   {
				    "orderId":"오더번호","orderHeaderKey":"오더헤더키","docType":"0001",”entCode":"SLV",”sellerCode”:”ASPB”,"status":"3200",
				    "tranDt":"전송일자",
					"list":[ 
					    {
					       "orderId":"오더번호","orderHeaderKey":"오더헤더키",
					       "orderLineKey":"오더라인키","primeLineNo":"오더라인순번","OrderReleaseKey":"오더라인확정키","orderDt":"전송일자",
					       "receiptNm":"수취인명","receiptTel":"수취인전화","receiptHp":"수취인휴대폰",
					       "receiptAddr1:"수취인주소1",receiptAddr2:"수취인주소2","receiptZipcode":"수취인우편번호",
					       "custNm":"주문자명","custTel":"주문자전화","custHp":"주문자휴대폰",
					       "custAddr1:"주문자주소1",custAddr2:"주문자주소2","custZipcode":"주문자우편번호",
					       "deliveryMsg":"배송메세지",
					       "itemId":"상품코드(SKU)",itemNm:"상품명","qty":"주문수량","salePrice":"판매가격","lineTotal";"라인판매가격"
					    },
					]
				}
			 * 
			 */
			
			
			// 수취인 주소, 주문자 주소
			/*
			 * <PersonInfoShipTo AddressLine1="abcd" AddressLine2=""
			        AddressLine3="" AddressLine4="" AddressLine5="" AddressLine6=""
			        AlternateEmailID="" Beeper="" City="California" Company=""
			        Country="US" DayFaxNo="" DayPhone="1234567" Department=""
			        EMailID="yg.jang@isecommerce.co.kr" EveningFaxNo=""
			        EveningPhone="" FirstName="sc" JobTitle="" LastName="team"
			        MiddleName="" MobilePhone="1234567" OtherPhone="" PersonID=""
			        PersonInfoKey="2014061020442933964" State="" Suffix="" Title="" ZipCode="12345"/>
			 */
			
			// Output XML Parsing
			XPath xp = XPathFactory.newInstance().newXPath();
			
			// 수취인 주소
			String receiptNm = (String)xp.evaluate("PersonInfoShipTo/@FirstName", outputXML, XPathConstants.STRING)
		    						+" "+(String)xp.evaluate("PersonInfoShipTo/@LastName", outputXML, XPathConstants.STRING);
			String receiptTel = (String)xp.evaluate("PersonInfoShipTo/@DayPhone", outputXML, XPathConstants.STRING);
			String receiptHp = (String)xp.evaluate("PersonInfoShipTo/@MobilePhone", outputXML, XPathConstants.STRING);
			String receiptAddr1 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine1", outputXML, XPathConstants.STRING);
			String receiptAddr2 = (String)xp.evaluate("PersonInfoShipTo/@AddressLine2", outputXML, XPathConstants.STRING);
			String receiptZipcode = (String)xp.evaluate("PersonInfoShipTo/@ZipCode", outputXML, XPathConstants.STRING);
			
			// 주문자 주소
			String custNm = (String)xp.evaluate("PersonInfoBillTo/@FirstName", outputXML, XPathConstants.STRING)
		    						+" "+(String)xp.evaluate("PersonInfoBillTo/@LastName", outputXML, XPathConstants.STRING);
			String custTel = (String)xp.evaluate("PersonInfoBillTo/@DayPhone", outputXML, XPathConstants.STRING);
			String custHp = (String)xp.evaluate("PersonInfoBillTo/@MobilePhone", outputXML, XPathConstants.STRING);
			String custAddr1 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine1", outputXML, XPathConstants.STRING);
			String custAddr2 = (String)xp.evaluate("PersonInfoBillTo/@AddressLine2", outputXML, XPathConstants.STRING);
			String custZipcode = (String)xp.evaluate("PersonInfoBillTo/@ZipCode", outputXML, XPathConstants.STRING);
		
		
		
		
			// 정상 Released가 된 OrderLine정보만 추출
			NodeList releaseOrderLineList = (NodeList)xp.evaluate("/Order/OrderLines/OrderLine[@MinLineStatus='3200' and @MaxLineStatus='3200']", outputXML, XPathConstants.NODESET);
			logger.debug("[release Count]"+releaseOrderLineList.getLength());
			
			List<HashMap<String,String>> confirmList = new ArrayList<HashMap<String,String>>();
			
			for(int i=0; i<releaseOrderLineList.getLength(); i++){
				Node lineNode = releaseOrderLineList.item(i);
				HashMap<String, String> orderLineMap = new HashMap<String, String>();
				
				String orderLineKey = (String)xp.evaluate("@OrderLineKey", lineNode, XPathConstants.STRING);
				String primeLineNo = (String)xp.evaluate("@PrimeLineNo", lineNode, XPathConstants.STRING);
				String orderReleaseKey = (String)xp.evaluate("OrderStatuses/OrderStatus/@OrderReleaseKey", lineNode, XPathConstants.STRING);
				
				String itemID = (String)xp.evaluate("Item/@ItemID", lineNode, XPathConstants.STRING);
				String orderedQty = (String)xp.evaluate("@OrderedQty", lineNode, XPathConstants.STRING);
				String itemNm = (String)xp.evaluate("@ItemShortDesc", lineNode, XPathConstants.STRING);
				String salePrice = (String)xp.evaluate("LineOverallTotals/@UnitPrice", lineNode, XPathConstants.STRING);
				String lineTotal = (String)xp.evaluate("LineOverallTotals/@LineTotal", lineNode, XPathConstants.STRING);
				
				// 오더라인번호/순번/릴리즈키/전송일시
				orderLineMap.put("orderLineKey", orderLineKey);
				orderLineMap.put("primeLineNo", primeLineNo);
				orderLineMap.put("orderReleaseKey", orderReleaseKey);
				orderLineMap.put("orderDt", CommonUtil.cuurentDateFromFormat("yyyyMMddHHmmss"));
				// 상품/가격 정보
				orderLineMap.put("itemId", itemID);
				orderLineMap.put("qty", orderedQty);
				orderLineMap.put("itemNm", itemNm);
				orderLineMap.put("salePrice", salePrice);
				orderLineMap.put("lineTotal", lineTotal);
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
				orderLineMap.put("custAddr1", custAddr1);
				orderLineMap.put("custAddr2", custAddr2);
				orderLineMap.put("custZipcode", custZipcode);
				// TODO: 배송메세지 전송가능여부 확인(MA)
				orderLineMap.put("deliveryMsg", "");	
				
				confirmList.add(orderLineMap);
			} // End for
			
			
			// Redis Message Setting
			sendMsgMap.put("orderHeaderKey", orderKey);
			sendMsgMap.put("docType", docType);
			sendMsgMap.put("orderId", orderNo);
			sendMsgMap.put("entCode", entCode);
			sendMsgMap.put("sellerCode", sellerCode);
			sendMsgMap.put("status", "3200"); // 3200
			String trDate = CommonUtil.cuurentDateFromFormat("yyyyMMddHHssmm");
			sendMsgMap.put("trDate", trDate);
			sendMsgMap.put("confirmed", confirmList);
			
			// JSON 변환
			ObjectMapper mapper = new ObjectMapper();
			String outputMsg = mapper.writeValueAsString(sendMsgMap);

			// RedisKey for SC -> CUBE
			String pushKey = entCode+":"+sellerCode+":order:update:S2C";
			
			logger.debug("[pushMessage ]"+outputMsg);
			logger.debug("[pushKey]"+pushKey);
			
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey, outputMsg);
			
			
			
			// RedisKey for CUBE -> SC 
			// TODO: Test 용, 큐브연동후 삭제 할 것
			sendMsgMap.put("status", "3201"); // 3201
			String outputMsgTest = mapper.writeValueAsString(sendMsgMap);
			
			
			String pushKey_CubetoSC = entCode+":"+sellerCode+":order:update:C2S";
			logger.debug("[pushKey_CubetoSC]"+pushKey_CubetoSC);
			// RedisDB에 메세지 저장
			listOps.leftPush(pushKey_CubetoSC, outputMsgTest);
			
			
			
			
			
			logger.debug("[Order Release Hanlder Started]");
		
		}catch(Exception e){
			
			// TODO:릴리즈정보 큐브전송시 예외처리필요
			e.printStackTrace();
			throw new Exception();
		}
				
	}
	
	
	/**
	 * 주문취소 후처리 프로세스 (SC->Ma)
	 *  - SC의 주문취소처리 후 SC에서 Ma로 주문취소정보를 전달하는 주문취소 후처리 프로세스 
	 *  - 전체주문취소인 경우만 해당
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
				String orderedQty = (String)xp.evaluate("@OrderedQty", lineNode, XPathConstants.STRING);
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
			sendMsgMap.put("cancelList", cancelList);
			
			
			// JSON 변환
			/*
			 * {"orderHeaderKey":"20140912152100132437",
			 *  "docType":"0001","status":"9000",
			 *  "cancelList":[
			 *      {"orderLineKey":"20140912152100132438","lineTotal":"0.00","orderReleaseKey":"","primeLineNo":"1","itemNm":"","salePrice":"49000.00","qty":"0.00","itemId":"ASPB_ITEM_0001"},
			 *      {"orderLineKey":"20140912152100132439","lineTotal":"0.00","orderReleaseKey":"","primeLineNo":"2","itemNm":"","salePrice":"59000.00","qty":"0.00","itemId":"ASPB_ITEM_0002"}
			 *      ],
			 *  "sellerCode":"ASPB",
			 *  "trDate":"20140912174848",
			 *  "orderId":"Y100000368",
			 *  "entCode":"SLV"}

			 * 
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
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
		
		logger.debug("##### [Order Cancel Hanlder End]");
	}
}

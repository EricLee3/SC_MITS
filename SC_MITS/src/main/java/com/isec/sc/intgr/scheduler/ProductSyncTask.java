/*
 *  
 *  * Revision History
 *  * Author              Date                  Description
 *  * ------------------   --------------       ------------------
 *  *  beyondj2ee          2014.01.02              
 *  
 */

package com.isec.sc.intgr.scheduler;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;


@PropertySource("classpath:redis.properties")
public class ProductSyncTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductSyncTask.class);


	@Autowired	private StringRedisTemplate maStringRedisTemplate;
	@Autowired	private SterlingApiDelegate sterlingApiDelegate;
	@Autowired	private Environment env;
	
	
	@Resource(name="maStringRedisTemplate")
	private ListOperations<String, String> listOps;
	
	
	
	@Value("${sc.api.manageItem.template}")
	private String MANAGEITEM_TEMPLATE;
	
	@Value("${sc.api.manageItem.item.template}")
	private String MANAGEITEM_ITEM_TEMPLATE;
	
	@Value("${sc.api.adjustInventory.item.template}")
	private String ADJUST_INVENTORY_ITEM_TEMPLATE;
	
	@Value("${sc.api.getInventorySupply.template}")
	private String GET_INVENTORY_SUPPLY_TEMPLATE;
	
	
	/**
	 * 상품정보 연동 Task - From Cube	
	 * 
	 * Cube가 저장한 상품정보를 조회(POP), inputXML을 생성하 manageItem API를 호출하여 상품을 등록, 또는 변경한다.
	 *  manageItem 실행 실패시, 실패건의 스타일코드와 조직(사업부)코드를 큐브결과전송키(S2C)에 저장하고 (1단계적용)
	 *                        별도로 에러키에 에러메세지와 함께 저장한다.(1단계이후)
	 *                 성공시, 성공데이타는 바로 MA전송키(S2M)에 저장한다.
	 *  
		i.  Read Key: 사업부코드:매장코드:product:C2S
		ii. 상품의 단위는 SKU(Cube의 barcode)기준이다.
		iii, 에러처리의 단위는 스타일코드 기준이다.
	 * 
	 * 
	 * @param redisKeyC2S 상품정보 저장 Key      - 사업부코드:product:C2S
     * @param redisKeyS2C 상품정보 처리결과 Key   - 사업부코드:product:S2C
     * @param redisKeyS2M 상품정보 저장 Key      - 사업부코드:매장코드:product:S2M
     * @param redisErrKey 상품정보 처리에러 Key   - 사업부코드:product:error
	 */
    public void syncProductFromCube(String redisKeyC2S, String redisKeyS2C,  String redisKeyS2M, String redisErrKey){
        
    	try {
    		 
    		logger.debug("#####" + "["+redisKeyC2S+"][syncProductFromCube] Job Task Started!");

    		
    	  	long dataCnt =  listOps.size(redisKeyC2S);
    	  	logger.debug("["+redisKeyC2S+"] data length: "+dataCnt);
			
    	  	ObjectMapper mapper = new ObjectMapper();
    	  	
    	  	String manageItemItemTempate = FileContentReader.readContent(getClass().getResourceAsStream(MANAGEITEM_ITEM_TEMPLATE));
    	  	
			// TODO: 건수제한 처리 고려
			for(int i=0; i<dataCnt; i++){
				// JSON 추출
				/*
				 * {
					    "list": [
					        {
					            "org_code": "사업부코드",
					            "prodinc": "스타일코드",
					            "brand_id": "브랜드ID",
					            "brand_name": "브랜드명",
					            "sale_price": "최초판매가",
					            "optioninfo": [
					                {
					                    "item_color": "컬러",
					                    "item_size": "사이즈",
					                    "bar_code": "상품바코드"
					                },
					                {
					                    "item_color": "컬러",
					                    "item_size": "사이즈",
					                    "bar_code": "상품바코드"
					                }
					            ]
					        }
					    ]
					}

				 */
				String jsonString = listOps.rightPop(redisKeyC2S);
				
				HashMap<String, Object> itemListMap = mapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>(){});
				ArrayList<HashMap<String,Object>> itemList = (ArrayList<HashMap<String,Object>>)itemListMap.get("list");
				
				logger.debug("##### [itemList size]" + itemList.size());
				
				
				ArrayList<HashMap<String,Object>> returnList = new ArrayList<HashMap<String,Object>>();
				ArrayList<HashMap<String,Object>> succList = new ArrayList<HashMap<String,Object>>();
				ArrayList<HashMap<String,Object>> failList = new ArrayList<HashMap<String,Object>>();
				
				for(int k=0; k<itemList.size(); k++){
					
					HashMap<String, Object> itemMap = itemList.get(k);
					
					// TODO: 사업부코드 -> 조직코드 매핑
					String ent_code = env.getProperty("ca."+(String)itemMap.get("org_code"));
					logger.debug("##### [ent_code]" + ent_code);
					ent_code ="SLV";
					
					String prodinc = (String)itemMap.get("prodinc");
					String pname = (String)itemMap.get("pname");
					String sale_price = (String)itemMap.get("sale_price");
					String brand_id = (String)itemMap.get("brand_id");
					String brand_name = (String)itemMap.get("brand_name");
					
					
					// barcode 추출
					String itemString = "";
					ArrayList<HashMap<String,Object>> barCodeList = (ArrayList<HashMap<String,Object>>)itemMap.get("optioninfo");
					for(int kk=0; kk<barCodeList.size(); kk++){
						
						String item_color = (String)barCodeList.get(kk).get("item_color");
						String item_size = (String)barCodeList.get(kk).get("item_size");
						String bar_code = (String)barCodeList.get(kk).get("bar_code");
						
						MessageFormat msg = new MessageFormat(manageItemItemTempate);
						String itemXML = msg.format(new String[] {
																	bar_code, ent_code, "EACH", 
																	CommonUtil.cuurentDateFromFormat("yyyy-MM-dd"),"9999-12-31",
																	brand_id, brand_name, sale_price, pname,
																	item_color, item_size, "3000"
																} 
						);
						itemString = itemString +"\n"+ itemXML;
					} // End for barcode 추출
					
					String itemXML = "";
					itemXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<ItemList>"
							+  itemString
							+ "</ItemList>";
					logger.debug("##### [managetItem input XNL]"+itemXML); 
					
					
					// manageItem API 호출
					HashMap<String, Object> resultMap = sterlingApiDelegate.manageItem(itemXML);
					String succ = (String)resultMap.get("succ");
					
					
					// 결과값처리
					HashMap<String, Object> returnMap = new HashMap<String, Object>();
					returnMap.put("prodinc", prodinc);
					
					// 성공
					if("01".equals(succ)){
						logger.debug("#####[manageItem Success]");
						returnMap.put("statuscd", succ);
						
					// 실패	
					}else if("99".equals(succ)){
						logger.debug("#####[manageItem Api Failed]");
						returnMap.put("statuscd", succ);
					}
					returnList.add(returnMap);
					
				} // End for Item List
				
				
				HashMap<String, Object> returnListMap = new HashMap<String, Object>();
				returnListMap.put("list", returnList);
				
				// Cube에 결과값 전송
				ObjectMapper resultMapper = new ObjectMapper();
				listOps.leftPush(redisKeyS2C, resultMapper.writeValueAsString(returnListMap));
				logger.debug("#####["+redisKeyS2C+"]"+resultMapper.writeValueAsString(returnListMap));
				
				
				// MA에 결과값 전송 - 받은데이타 그대로 전송
				// TODO: 성공건에 대해서만 전송처리
				listOps.leftPush(redisKeyS2M, resultMapper.writeValueAsString(itemList));
				logger.debug("#####["+redisKeyS2M+"]"+resultMapper.writeValueAsString(itemList));
				
				
				// Error키에 실패데이타 저장 - SC 관리용
				// TODO: 실패건에 대해서만 barcode단위로 저장처리
				listOps.leftPush(redisErrKey, resultMapper.writeValueAsString(returnListMap));
				logger.debug("#####["+redisErrKey+"]"+resultMapper.writeValueAsString(returnListMap));
				
				
				
			} // End for Redis Data

			
		} catch (Exception e) {
			
			// TODO: 예외처리
			e.printStackTrace();
		}
		
    }
    
    /**
     * 재고정보 연동 Task - From Cube
     * 
     *  1. SC의 스케쥴러는 주기적으로 Cube가 저장한 재고정보를 조회(POP), 해당상품의 재고정보를 변경한다.
			SC의 재고관리 방식은 증감분에 대한 처리방식이므로  Cube에서 전송한 재고정보(현재판매가능수량)에 대한 별도의 추가처리가 필요하다
			
			i. 먼저 해당 상품의 가용재고를 조회한다. - getSupplyInventory
			ii. Cube의 재고수량에 현 가용재고를 차감한다. 
			iii. 차감된 수량으로 재고변경 API를 호출한다. - adjustInventory
			iv. Read Key: 사업부코드:매장코드: inventory:C2S
		
		2. 재고변경이 정상적으로 처리되면, 정상처리건에 대해 Cube에서 전송된 재고정보를 MA의 '재고정보 전송' 연동 RedisKey에 저장한다. 
		   실패처리된 재고정보에 대해서는 Cube와 별도의 인터페이스 처리를 하지 않는다.
			
			i. MA Write Key: 사업부코드:매장코드: inventory:S2M
     * 
     * @param redisKeyC2S 재고정보 저장 Key      - 사업부코드:inventory:C2S
     * @param redisKeyS2C 재고정보 처리결과 Key   - 사업부코드:inventory:S2C
     * @param redisKeyS2M 재고정보 저장 Key      - 사업부코드:매장코드: inventory:S2M
     * @param redisErrKey 재고정보 처리에러 Key   - 사업부코드:inventory:error
     */
    public void syncInventoryFromCube(String redisKeyC2S, String redisKeyS2C,  String redisKeyS2M, String redisErrKey){
    	
    	logger.debug("#####" + "["+redisKeyC2S+"][syncProductFromCube] Job Task Started!");

		try{
		  	long dataCnt =  listOps.size(redisKeyC2S);
		  	logger.debug("["+redisKeyC2S+"] data length: "+dataCnt);
			
		  	ObjectMapper mapper = new ObjectMapper();
		  	
		  	
		  	// 현 보유수량 조회 API Input
		  	String getInvSupplyTempate = FileContentReader.readContent(getClass().getResourceAsStream(GET_INVENTORY_SUPPLY_TEMPLATE));
		  	// 재고조정 API Input
		  	String adjustIvnItemTempate = FileContentReader.readContent(getClass().getResourceAsStream(ADJUST_INVENTORY_ITEM_TEMPLATE));
		  	
			// TODO: 건수제한 처리 고려
			for(int i=0; i<dataCnt; i++){
	    	
				String jsonString = listOps.rightPop(redisKeyC2S);
				
				HashMap<String, Object> invListMap = mapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>(){});
				ArrayList<HashMap<String,Object>> itemInvList = (ArrayList<HashMap<String,Object>>)invListMap.get("list");
				
				logger.debug("##### [invList size]" + invListMap.size());
				
				MessageFormat msg = null;
				Document doc = null;
				
				String invItemString = "";
				for(int k=0; k<itemInvList.size(); k++){
					
					HashMap<String, Object> itemMap = itemInvList.get(k);
					
					// TODO: 사업부코드 -> 조직코드 매핑
					String ent_code = env.getProperty("ca."+(String)itemMap.get("org_code"));
					logger.debug("##### [ent_code]" + ent_code);
					ent_code ="SLV";
					
					String ship_node = (String)itemMap.get("ship_node");	// 창고코드
					String bar_code = (String)itemMap.get("bar_code");  	// 상품코드
					String qty = (String)itemMap.get("qty");				// 재고수량
					String uom = (String)itemMap.get("uom");			// 측정단위 
					
					// 해당창고의 현 재고수량(ONHAND) 조회- 가용재고아님
					msg = new MessageFormat(getInvSupplyTempate);
					String getInvXML = msg.format(new String[] {
																ent_code, bar_code, ship_node, "EACH", 
															} 
					);
					String getInv_output = sterlingApiDelegate.comApiCall("getInventorySupply", getInvXML);
					
					doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(getInv_output.getBytes("UTF-8")));
					XPath xp = XPathFactory.newInstance().newXPath();
					String currQty =  (String)xp.evaluate("/Item/Supplies/InventorySupply/@Quantity", doc.getDocumentElement(), XPathConstants.STRING);
					
					logger.debug("#####[Currunt Quantity]"+currQty);
					if(currQty == null || "".equals(currQty)){
						currQty = "0.00";
					}
					
					// Cube의 재고수량 - 현 재고수량 차감 
					Double adjustQty = Double.parseDouble(qty) - Double.parseDouble(currQty);
					logger.debug("#####[Adjust Quantity]"+adjustQty);
					
					
					
					// adjustInventory Input XML Generation
					/*
					 *  <Item OrganizationCode="{1}" ItemID="{0}" Quantity="{2}" 
							ShipNode="{3}" UnitOfMeasure="{4}" SupplyType="ONHAND" 
							AdjustmentType="ADJUSTMENT">
						</Item>
					 */
					msg = new MessageFormat(adjustIvnItemTempate);
					String adjustInvXML = msg.format(new String[] {
																ent_code, bar_code, String.valueOf(adjustQty), ship_node, "EACH", 
															} 
					);
					
					invItemString = invItemString +"\n"+ adjustInvXML;
				}// End for InvItem
				
				String invXML = "";
				invXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<Items>"
						+  invItemString
						+ "</Items>";
				logger.debug("##### [adjustInventory input XNL]"+invXML); 
				
				
				// adjustInventory API 호출
				String adjInv_output = sterlingApiDelegate.comApiCall("adjustInventory", invXML);
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(adjInv_output.getBytes("UTF-8")));
				
				
				HashMap<String, Object> returnMap = new HashMap<String, Object>();
				ObjectMapper resultMapper = new ObjectMapper();
				
				// Error 처리
				logger.debug("result:::"+doc.getFirstChild().getNodeName());
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					
					logger.debug("#####[adjustInventory Api Failed]");
					
					// CUBE 결과전송키에 저장 (실패)
					// 수신받은 데이타 그대로 전송( SC는 실패시  전체 RollBack 되기때문)
					returnMap.put("result_code", "99");
					returnMap.put("list", itemInvList);
					
					listOps.leftPush(redisKeyS2C, resultMapper.writeValueAsString(returnMap));
					logger.debug("#####["+redisKeyS2C+"]"+resultMapper.writeValueAsString(returnMap));
					
					
					// Error키에 별도저장 - SC 관리용
					XPath xp = XPathFactory.newInstance().newXPath();
					
//					String errCode = (String)xp.evaluate("Error/@ErrorCode", doc.getDocumentElement(), XPathConstants.STRING);
					String errDesc = (String)xp.evaluate("Error/@ErrorDescription", doc.getDocumentElement(), XPathConstants.STRING);
					String orgCode = (String)xp.evaluate("Error/Attribute[Name='OrganizationCode']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
					String uom = (String)xp.evaluate("Error/Attribute[Name='UnitOfMeasure']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
					String itemId = (String)xp.evaluate("Error/Attribute[Name='ItemID']/@Value", doc.getDocumentElement(), XPathConstants.STRING);
					
					HashMap<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("succ", "99");
					resultMap.put("input_xml", adjInv_output);
					resultMap.put("err_desc", errDesc);
					resultMap.put("org_code", orgCode);
					resultMap.put("uom", uom);
					resultMap.put("item_id", itemId);
					resultMap.put("err_date", CommonUtil.cuurentDateFromFormat("yyyy-MM-dd HH:mm:ss"));
					
					listOps.leftPush(redisErrKey, mapper.writeValueAsString(resultMap));
					logger.debug("#####["+redisErrKey+"]"+resultMapper.writeValueAsString(resultMap));
					
				
				// 성공 
				}else{
					
					logger.debug("#####[adjustInventory Api Success]");
					
					// CUBE 결과전송키에 저장 (성공)
					returnMap.put("result_code", "01");
					returnMap.put("list", new ArrayList());
					
					listOps.leftPush(redisKeyS2C, resultMapper.writeValueAsString(returnMap));
					
					logger.debug("#####["+redisKeyS2C+"]"+resultMapper.writeValueAsString(returnMap));
					
					
					// MA 전송키에 저장 - 수신받은 데이타 그대로 전송
					listOps.leftPush(redisKeyS2M, resultMapper.writeValueAsString(itemInvList));
					logger.debug("#####["+redisKeyS2M+"]"+resultMapper.writeValueAsString(itemInvList));
				}
			}
			
		}catch(Exception e){
			// TODO: 예외처리
			e.printStackTrace();
		}
    	
    	
    }
	
    
    

}

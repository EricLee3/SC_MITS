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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.w3c.dom.NodeList;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.CommonUtil;
import com.isec.sc.intgr.api.util.FileContentReader;


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
	
	@Value("${sc.api.getShipNodeInventory.template}")
	private String GET_SHIPNODE_TEMPLATE;
	
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
    		 
    		logger.debug("##### ["+redisKeyC2S+"][syncProductFromCube] Started!");

    		
    	  	long dataCnt =  listOps.size(redisKeyC2S);
    	  	logger.debug("["+redisKeyC2S+"][syncProductFromCube] data length: "+dataCnt);
			
    	  	ObjectMapper mapper = new ObjectMapper();
    	  	
    	  	String manageItemItemTempate = FileContentReader.readContent(getClass().getResourceAsStream(MANAGEITEM_ITEM_TEMPLATE));
    	  	
			// TODO: 건수제한 처리 고려
			for(int i=0; i<dataCnt; i++){
				
				String jsonString = listOps.rightPop(redisKeyC2S);
				logger.debug("##### [Redis Read Data - Product From Cube]" + jsonString);
				
				
				
				HashMap<String, Object> itemListMap = mapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>(){});
				ArrayList<HashMap<String,Object>> itemList = (ArrayList<HashMap<String,Object>>)itemListMap.get("list");
				
				logger.debug("##### [styleCode size]" + itemList.size());
				
				
				ArrayList<HashMap<String,Object>> returnList = new ArrayList<HashMap<String,Object>>();
				ArrayList<HashMap<String,Object>> succList = new ArrayList<HashMap<String,Object>>();
				ArrayList<HashMap<String,Object>> failList = new ArrayList<HashMap<String,Object>>();
				
				for(int k=0; k<itemList.size(); k++){
					
					HashMap<String, Object> itemMap = itemList.get(k);
					
					String org_code = (String)itemMap.get("org_code");
					String ent_code = env.getProperty("ca."+org_code);
					logger.debug("##### [org_code]" + (String)itemMap.get("org_code"));
					logger.debug("##### [ent_code]" + ent_code);
					
					String prodinc = (String)itemMap.get("prodinc");
					
					String pname = (String)itemMap.get("pname");
					pname = CommonUtil.replaceXmlStr(pname);
					
					String sale_price = (String)itemMap.get("sale_price");
					
					String brand_id = (String)itemMap.get("brand_id");
					brand_id = CommonUtil.replaceXmlStr(brand_id);
					
					String brand_name = (String)itemMap.get("brand_name");
					brand_name = CommonUtil.replaceXmlStr(brand_name);
					
					String tran_date = (String)itemMap.get("tran_date");
					String tran_seq = (String)itemMap.get("tran_seq");
					String status = "3000";	// 판매가능상태
					
					// barcode 추출
					String itemString = "";
					ArrayList<HashMap<String,Object>> barCodeList = (ArrayList<HashMap<String,Object>>)itemMap.get("optioninfo");
					logger.debug("##### [barCodeList size]" + barCodeList.size());
					
					for(int kk=0; kk<barCodeList.size(); kk++){
						
						String item_color = (String)barCodeList.get(kk).get("item_color");
						item_color = CommonUtil.replaceXmlStr(item_color);
						
						String item_size = (String)barCodeList.get(kk).get("item_size");
						item_size = CommonUtil.replaceXmlStr(item_size);
						
						
						String bar_code = (String)barCodeList.get(kk).get("bar_code");
						
						MessageFormat msg = new MessageFormat(manageItemItemTempate);
						String itemXML = msg.format(new String[] {
																	bar_code, ent_code, "EACH", 
																	"9999-12-31",CommonUtil.cuurentDateFromFormat("yyyy-MM-dd"),
																	brand_id, brand_name, sale_price, pname,
																	item_color, item_size, status
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
					
					
					// Login 실패일 경우 Key에 다시 기록 - 재처리 시도
					if("90".equals(succ)){
						
						logger.debug("########## Login Failed!!!");
						listOps.leftPush(redisKeyC2S, jsonString);
						listOps.leftPush(redisKeyC2S+":input", itemXML);
						continue;
					}
					
					
					
					// 결과값처리
					HashMap<String, Object> returnMap = new HashMap<String, Object>();
					returnMap.put("org_code", org_code);
					returnMap.put("prodinc", prodinc);
					returnMap.put("tran_date", tran_date);
					returnMap.put("tran_seq", tran_seq);
					
					// 성공
					if("01".equals(succ)){
						logger.debug("#####[manageItem Success]");
						returnMap.put("statuscd", succ);
						
						succList.add(itemMap);
						
					// 실패	
					}else if("99".equals(succ)){
						logger.debug("#####[manageItem Api Failed]");
						returnMap.put("statuscd", succ);
						
						failList.add(resultMap);
					}
					returnList.add(returnMap);
					
				} // End for Item List
				
				
				// Cube에 결과값 전송
				HashMap<String, Object> returnListMap = new HashMap<String, Object>();
				returnListMap.put("list", returnList);
				ObjectMapper resultMapper = new ObjectMapper();
				 listOps.leftPush(redisKeyS2C, resultMapper.writeValueAsString(returnListMap));
				logger.debug("#####["+redisKeyS2C+"]"+resultMapper.writeValueAsString(returnListMap));
				
				
				
				// MA에 결과값 전송 - 성공데이타 전송
				HashMap<String, Object> succMap = new HashMap<String, Object>();
				succMap.put("list", succList);
				
				listOps.leftPush(redisKeyS2M, resultMapper.writeValueAsString(succMap));
				logger.debug("#####["+redisKeyS2M+"]"+resultMapper.writeValueAsString(succMap));
				
				
				// Error키에 실패데이타 저장 - SC 관리용
				if(failList.size() > 0){
					listOps.leftPush(redisErrKey, resultMapper.writeValueAsString(failList));
					logger.debug("#####["+redisErrKey+"]"+resultMapper.writeValueAsString(failList));
				}
				
				
				
			} // End for Redis Data

			
		} catch (Exception e) {
			
			// TODO: 예외처리
			e.printStackTrace();
		}
		
    		logger.debug("##### ["+redisKeyC2S+"][syncProductFromCube] End!");
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
    	
    		logger.debug("##### ["+redisKeyC2S+"][syncProductFromCube] Started!");

		try{
		  	long dataCnt =  listOps.size(redisKeyC2S);
		  	logger.debug("##### ["+redisKeyC2S+"][syncProductFromCube] data length: "+dataCnt);
			
		  	ObjectMapper mapper = new ObjectMapper();
		  	
		  	// 재고조정 API Input
		  	String adjustIvnItemTempate = FileContentReader.readContent(getClass().getResourceAsStream(ADJUST_INVENTORY_ITEM_TEMPLATE));
		  	
			// TODO: 건수제한 처리 고려
			for(int i=0; i<dataCnt; i++){
	    	
				String jsonString = listOps.rightPop(redisKeyC2S);
				logger.debug("##### [Redis Read Data - Product From Cube]" + jsonString);
				
				HashMap<String, Object> invListMap = mapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>(){});
				ArrayList<HashMap<String,Object>> itemInvList = (ArrayList<HashMap<String,Object>>)invListMap.get("list");
				
				// Ma전송을 위한 리스트객체
				List<HashMap<String,Object>> maSendList = new ArrayList<HashMap<String,Object>>();
				
				logger.debug("##### [invList size]" + invListMap.size());
				
				MessageFormat msg = null;
				Document doc = null;
				
				String invItemString = "";
				for (HashMap<String,Object> itemMap : itemInvList){
					
					String org_code = (String)itemMap.get("org_code");
					String ent_code = env.getProperty("ca."+org_code);
//					logger.debug("##### [org_code]" + (String)itemMap.get("org_code"));
//					logger.debug("##### [ent_code]" + ent_code);
					
					String ship_node = (String)itemMap.get("ship_node");	// 창고코드
					String bar_code = (String)itemMap.get("bar_code");  	// 상품코드
					String qty = (String)itemMap.get("qty");				// 재고수량
					String uom = (String)itemMap.get("uom");			    // 측정단위 
					if(uom == null || "".equals(uom)) uom = "EACH";
					
					// Ma전송을 위한 재고정보 저장 - org_code,bar_code,uom
					HashMap<String, Object> maSendMap = new HashMap<String, Object>();
					maSendMap.put("ent_code", ent_code);
					maSendMap.put("bar_code", bar_code);
					maSendMap.put("uom", uom);
					maSendList.add(maSendMap);
					
					
					// 현 재고수량 조회 - 가용재고
					Double currScQty = sterlingApiDelegate.getCalcQtyBeforeAdjustInv(ent_code, bar_code, ship_node, uom, "A");
					// Cube의 재고수량 - 현 재고수량 차감 
					Double adjustQty = Double.parseDouble(qty) - currScQty;
//					logger.debug("#####[SC 현재고]"+currScQty);
//					logger.debug("#####[Cube 현재고]"+qty);
					logger.debug("#####[증감분] "+bar_code+" "+adjustQty);
					
					// SC 재고차감
					// adjustInventory Input XML Generation
					/*
					 *  <Item OrganizationCode="{1}" ItemID="{0}" Quantity="{2}" 
							ShipNode="{3}" UnitOfMeasure="{4}" SupplyType="ONHAND" 
							AdjustmentType="ADJUSTMENT">
						</Item>
					 */
					msg = new MessageFormat(adjustIvnItemTempate);
					String adjustInvXML = msg.format(new String[] {
																ent_code, bar_code, String.valueOf(adjustQty), ship_node, uom, 
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
				
				
				//---------------------------------- 결과처리
				HashMap<String, Object> returnMap = new HashMap<String, Object>();
				ArrayList<HashMap<String,Object>> returnItemInvList = new ArrayList<HashMap<String,Object>>();
				
				ObjectMapper resultMapper = new ObjectMapper();
				
				// Error 처리
				logger.debug("result:::"+doc.getFirstChild().getNodeName());
				if("Errors".equals(doc.getFirstChild().getNodeName())){
					
					logger.debug("#####[adjustInventory Api Failed]");
					
					// CUBE 결과전송키에 저장 (실패)
					// TODO: 수신받은 데이타 전부전송( SC는 실패시  전체 RollBack 되기때문)
					for (HashMap<String,Object> itemMap : itemInvList){
						HashMap<String, Object> returnItemMap = new HashMap<String, Object>();
						
						returnItemMap.put("statuscd", "99");
						returnItemMap.put("org_code", itemMap.get("org_code"));
						returnItemMap.put("bar_code", itemMap.get("bar_code"));
						returnItemMap.put("ship_node", itemMap.get("ship_node"));
						returnItemMap.put("tran_date", itemMap.get("tran_date"));
						returnItemMap.put("tran_seq", itemMap.get("tran_seq"));
						
						
						returnItemInvList.add(returnItemMap);
					}
					returnMap.put("list", returnItemInvList);
					
					
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
					resultMap.put("input_xml", invXML);
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
					
					//========== 1. CUBE 결과전송키에 저장 (성공)
					for (HashMap<String,Object> itemMap : itemInvList){
						
						HashMap<String, Object> returnItemMap = new HashMap<String, Object>();
						
						returnItemMap.put("statuscd",  "01");
						returnItemMap.put("org_code",  itemMap.get("org_code"));
						returnItemMap.put("bar_code",  itemMap.get("bar_code"));
						returnItemMap.put("ship_node", itemMap.get("ship_node"));
						returnItemMap.put("tran_date", itemMap.get("tran_date"));
						returnItemMap.put("tran_seq",  itemMap.get("tran_seq"));
						returnItemInvList.add(returnItemMap);
					}
					returnMap.put("list", returnItemInvList);
					
					listOps.leftPush(redisKeyS2C, resultMapper.writeValueAsString(returnMap));
					logger.debug("#####Inventory Sync Success ["+redisKeyS2C+"]"+resultMapper.writeValueAsString(returnMap));
					
					
					//========== 2. MA에 해당상품의 모든창고의 가용재고 수량 전달
					returnItemInvList = new ArrayList<HashMap<String,Object>>();	// 전송리스트 초기화
					
					// 중복 bar_code 제거
					Set<HashMap<String,Object>> maDataSet = new HashSet<HashMap<String,Object>>(maSendList);
			        
					for (HashMap<String,Object> itemMap : maDataSet){
						
						String ent_code = (String)itemMap.get("ent_code"); // 조직코드
						String bar_code = (String)itemMap.get("bar_code");  // 상품코드
						String uom = (String)itemMap.get("uom");			// 측정단위 
						
						// 모든창고의 가용재고 수량 조회
						Double currScQty = sterlingApiDelegate.getCalcQtyBeforeAdjustInv(ent_code, bar_code, "", uom, "A");
						logger.info("[qty] "+bar_code+"     "+currScQty);
						
						
						// MA 전송결과값 생성
						HashMap<String, Object> returnItemMap = new HashMap<String, Object>();
						returnItemMap = new HashMap<String, Object>();
						returnItemMap.put("org_code", ent_code);	// MA는 조직코드
						returnItemMap.put("bar_code", bar_code);
						returnItemMap.put("qty", currScQty);
						
						returnItemInvList.add(returnItemMap);
						
					}// End For ItemList
					
					returnMap = new HashMap<String, Object>();
					returnMap.put("list", returnItemInvList);
					
					// MA 전송키에 저장
					listOps.leftPush(redisKeyS2M, resultMapper.writeValueAsString(returnMap));
					logger.debug("#####["+redisKeyS2M+"]"+resultMapper.writeValueAsString(returnMap));
				}
			} // End For DataCnt
			
		}catch(Exception e){
			// TODO: 재고정보 동기화 예외처리
			e.printStackTrace();
		}
    	
		logger.debug("##### ["+redisKeyC2S+"][syncProductFromCube] End!");
    	
    }
	
    
    

}

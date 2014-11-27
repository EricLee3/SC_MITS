package com.isec.sc.intgr.web;

import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.isec.sc.intgr.api.delegate.SterlingApiDelegate;
import com.isec.sc.intgr.api.util.FileContentReader;
import com.isec.sc.intgr.api.xml.beans.InventoryItem;
import com.isec.sc.intgr.api.xml.beans.InventoryList;



@Controller
@RequestMapping("/inventory")
public class InventoryController {

	private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
	
	@Autowired private SterlingApiDelegate sterlingApiDelegate;
	
	@Autowired private Environment env;
	
	@Value("${sc.api.getInventoryItemList.template}")
	private String GET_INVENTORY_ITEM_LIST_TEMPLATE;
	
	@Value("${sc.api.inventory.getInventoryItemList}")
	private String sc_api_getInventoryItemList;
	
	@Value("${sc.api.getShipNodeInventory.template}")
	private String GET_SHIPNODE_TEMPLATE;
	
	
	
	@RequestMapping(value = "/invenItemList.do")
	public ModelAndView goInventoryItemList(@RequestParam Map<String, String> paramMap) throws Exception{ 
		
		ModelAndView mav = new ModelAndView("");
		mav.setViewName("/admin/inventory/inventory_item_list");
		return mav;
		
	}
	
	
	@RequestMapping(value = "/invenItemList.sc")
	public ModelAndView getInventoryItemList( @RequestParam Map<String, String> paramMap, HttpServletRequest req ) throws Exception{ 
		
		logger.debug("action: "+paramMap.get("action")); 
		logger.debug("draw: "+paramMap.get("draw")); 
		logger.debug("customActionType: "+paramMap.get("customActionType")); 
		logger.debug("customActionMessage: "+paramMap.get("customActionMessage")); 
		
		
		if( (String)paramMap.get("ent_code") == null  || "".equals((String)paramMap.get("ent_code"))){
			
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("data","");
			return mav;
		}
		
		// Search Filter Parameter
		String itemId = (String)paramMap.get("item_id")==null?"":(String)paramMap.get("item_id");
		String entCode = (String)paramMap.get("ent_code")==null?"":(String)paramMap.get("ent_code"); 
		if("*".equals(entCode)){
			entCode = "";
		}
		
		String productClass = paramMap.get("product_class")==null?"":paramMap.get("product_class");
		String uom = paramMap.get("uom")==null?"":paramMap.get("uom");
		String shipNode = paramMap.get("ship_node")==null?"":paramMap.get("ship_node");
		
		
		// Input XML Creation
		String getOrderList_input = FileContentReader.readContent(getClass().getResourceAsStream(GET_INVENTORY_ITEM_LIST_TEMPLATE));
	    MessageFormat msg = new MessageFormat(getOrderList_input);
		String inputXML = msg.format(new String[] {
				                                entCode,
				                                itemId,
				                                productClass,
				                                uom,
				                                shipNode
										} );
		
		logger.debug("[inputXML]"+inputXML); 
		
		
		// API Call
		String outPutXML = sterlingApiDelegate.comApiCall(sc_api_getInventoryItemList, inputXML);
		
		// XML to JSON
		Serializer persister = new Persister();
		InventoryList invenList =  persister.read(InventoryList.class, outPutXML);
		
		
//		
//		for(InventoryItem invenItem : invenList.getInventoryItem()){
//			String getShipNodeTempate = FileContentReader.readContent(getClass().getResourceAsStream(GET_SHIPNODE_TEMPLATE));
//			
//			msg = new MessageFormat(getShipNodeTempate);
//			String getInvXML = msg.format(new String[] {
//					entCode, invenItem.getItemID(), "", uom 
//					} 
//			);
//			String getInv_output = sterlingApiDelegate.comApiCall("getShipNodeInventory", getInvXML);
//		
//		}
		
		String totCnt = invenList.getTotalInventoryItemList();
		logger.debug("[inputXML]"+totCnt); 
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("data", invenList.getInventoryItem());
		mav.addObject("draw", paramMap.get("draw"));
		
		
		String custActionType = (String)paramMap.get("customActionType"); 
		if ("group_action".equals(custActionType)) {
			mav.addObject("customActionStatus","OK"); // pass custom message(useful for getting status of group actions)
			mav.addObject("customActionMessage","Group action successfully has been completed. Well done!"); // pass custom message(useful for getting status of group actions)
		}
		
		return mav;   
		
	}
	
	
	@RequestMapping(value = "/inventoryDetail.do")
	public ModelAndView getInventoryDetail( @RequestParam String orderNo, @RequestParam String entCode, @RequestParam String docType ) throws Exception{ 
		
		return null;
	}
	
	
}

package com.isec.sc.api.inv;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.core.SterlingHTTPConnector;
import com.isec.sc.api.core.util.FileContentReader;

/**
 *  * 
 * Item 의 Inventory(재고)정보를 조정하는 API(adjustInventory)를 HTTP URL방식으로 
 * Interface 할 수 있도록 처리한 클래스
 *  * 
 *
 */
public class AdjustInventory {
	
	private static final String apiName = "adjustInventory";	// API Name
	private static final String templateFile = "adjustInventory.template.xml";	// API Input template
	
	// API Input template
	private static final String itemTemplate = "<Item ItemID=\"{0}\" OrganizationCode=\"{1}\" UnitOfMeasure=\"{2}\" "
			+ " Quantity=\"{3}\" "
			+ " ShipNode=\"{4}\" "
			+ " SupplyType=\"{5}\" "
			+ " AdjustmentType=\"{6}\"> "
			+ "</Item>"; 
	  
	
	// API Input Attribute
	private String itemID;
	private String organizationCode;
	private String unitOfMeasure;
	private String productClass;
	private Integer quantity;
	private String shipNode;
	private String supplyType;
	private String adjustmentType;
	
	// 변경대상 Item List
	private ArrayList<AdjustInventory> itemList = new ArrayList<AdjustInventory>();
	
	
	public String invoke() {
		
		// Input XML 생성
		MessageFormat itemMsg = new MessageFormat(itemTemplate);
		String allItemText = "";
		
		// Item 건수만큼 반복
		for(int i=0; i<itemList.size(); i++){
			
			String[] item_param = {
									itemList.get(i).itemID, itemList.get(i).organizationCode, itemList.get(i).unitOfMeasure,
									Integer.toString(itemList.get(i).quantity), itemList.get(i).shipNode,
									itemList.get(i).supplyType, itemList.get(i).adjustmentType
								  };
			
			String itemInput = itemMsg.format(item_param);
			allItemText += itemInput;
		}		
		
		// Template Load
		String template = FileContentReader.readContent(getClass().getResourceAsStream(templateFile));		
		
		
		// Input XML String 완성
		MessageFormat msg = new MessageFormat(template);
		String input = msg.format(new String[]{allItemText});
		
		
		//  API 호출
		SterlingHTTPConnector sterling = new SterlingHTTPConnector();
		sterling.setApi(apiName);
		sterling.setData(input);
		return sterling.run();
		
	}
	
	
	
	// getter/setter

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getProductClass() {
		return productClass;
	}

	public void setProductClass(String productClass) {
		this.productClass = productClass;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public String getShipNode() {
		return shipNode;
	}

	public void setShipNode(String shipNode) {
		this.shipNode = shipNode;
	}

	public String getSupplyType() {
		return supplyType;
	}


	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public ArrayList<AdjustInventory> getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList<AdjustInventory> itemList) {
		this.itemList = itemList;
	}
}

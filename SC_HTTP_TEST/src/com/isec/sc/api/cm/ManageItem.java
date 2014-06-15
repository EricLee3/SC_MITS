package com.isec.sc.api.cm;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.isec.sc.api.core.SterlingHTTPConnector;
import com.isec.sc.api.core.util.FileContentReader;

public class ManageItem {
	
	private static final String apiName = "manageItem"; // API Name
	private static final String templateFile = "manageItem.template.xml"; // API Input Template
	
	// API Input Template
	private static final String itemTemplate = "<Item Action=\"{0}\" GlobalItemID=\"{1}\" ItemID=\"{2}\" OrganizationCode=\"{3}\" UnitOfMeasure=\"{4}\">"
			+ "<PrimaryInformation "
			+ " EffectiveEndDate=\"{5}\" "
			+ " EffectiveStartDate=\"{6}\" "
			+ " ManufacturerItem=\"{7}\" "
			+ " ManufacturerName=\"{8}\" "
			+ " UnitCost=\"{9}\" "
			+ " ShortDescription=\"{10}\" "
			+ " ColorCode=\"{11}\" "
			+ " SizeCode=\"{12}\" "
			+ " Status=\"{13}\" "
			+ "/>"
			+ "</Item>";
	
	// API Input Attribute
	private String action;
	private String globalItemID;
	private String itemID;
	private String organizationCode;
	private String unitOfMeasure;
	private String effectiveEndDate;
	private String effectiveStartDate;
	private String manufacturerItem;
	private String manufacturerName;
	private double unitCost;
	private String shortDescription;
	private String colorCode;
	private String sizeCode;
	private String status;
	
	private ArrayList<ManageItem> itemList = new ArrayList<ManageItem>();
	
	
	public String run() {
		
		// Item List XML 생성
		MessageFormat itemMsg = new MessageFormat(itemTemplate);
		String allItemText = "";
		for(int i=0; i<itemList.size(); i++){
			
			String[] item_param = {
									itemList.get(i).action, itemList.get(i).globalItemID, itemList.get(i).itemID,
									itemList.get(i).organizationCode, itemList.get(i).unitOfMeasure, itemList.get(i).effectiveStartDate,
									itemList.get(i).effectiveEndDate, itemList.get(i).manufacturerItem, itemList.get(i).manufacturerName,
									Double.toString(itemList.get(i).unitCost), itemList.get(i).shortDescription, itemList.get(i).colorCode,
									itemList.get(i).sizeCode, itemList.get(i).status
								  };
			
			String itemInput = itemMsg.format(item_param);
			allItemText += itemInput;
		}		
		
		String template = FileContentReader.readContent(getClass().getResourceAsStream(templateFile));		
		
		MessageFormat msg = new MessageFormat(template);
		String input = msg.format(new String[]{allItemText});
		
		// API 호출
		SterlingHTTPConnector sterling = new SterlingHTTPConnector();
		sterling.setApi(apiName);
		sterling.setData(input);
		return sterling.run();
		
	}
	
	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getGlobalItemID() {
		return globalItemID;
	}


	public void setGlobalItemID(String globalItemID) {
		this.globalItemID = globalItemID;
	}


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


	public String getEffectiveEndDate() {
		return effectiveEndDate;
	}


	public void setEffectiveEndDate(String effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}


	public String getEffectiveStartDate() {
		return effectiveStartDate;
	}


	public void setEffectiveStartDate(String effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}


	public String getManufacturerItem() {
		return manufacturerItem;
	}


	public void setManufacturerItem(String manufacturerItem) {
		this.manufacturerItem = manufacturerItem;
	}


	public String getManufacturerName() {
		return manufacturerName;
	}


	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}


	public double getUnitCost() {
		return unitCost;
	}


	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}


	public String getShortDescription() {
		return shortDescription;
	}


	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	public String getColorCode() {
		return colorCode;
	}


	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}


	public String getSizeCode() {
		return sizeCode;
	}


	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList<ManageItem> getItemList() {
		return itemList;
	}


	public void setItemList(ArrayList<ManageItem> itemList) {
		this.itemList = itemList;
	}
}

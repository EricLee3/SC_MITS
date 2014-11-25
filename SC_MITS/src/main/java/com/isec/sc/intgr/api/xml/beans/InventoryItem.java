package com.isec.sc.intgr.api.xml.beans;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="InventoryItem", strict=false)
public class InventoryItem {

	@Attribute
	private String InventoryOrganizationCode;
	
	@Attribute
	private String ItemID;
	
	@Attribute
	private String ProductClass;
	
	@Attribute
	private String UnitOfMeasure;

	@Element(required=false)
	private Item Item = new Item();

	
	private double supplyQty = 0.00d;
	private double availQty = 0.00d;
	
	

	public double getSupplyQty() {
		return supplyQty;
	}


	public void setSupplyQty(double supplyQty) {
		this.supplyQty = supplyQty;
	}


	public double getAvailQty() {
		return availQty;
	}


	public void setAvailQty(double availQty) {
		this.availQty = availQty;
	}

	
	

	public String getInventoryOrganizationCode() {
		return InventoryOrganizationCode;
	}


	public String getItemID() {
		return ItemID;
	}


	public String getProductClass() {
		return ProductClass;
	}


	public String getUnitOfMeasure() {
		return UnitOfMeasure;
	}


	public Item getItem() {
		return Item;
	}


	public void setInventoryOrganizationCode(String inventoryOrganizationCode) {
		InventoryOrganizationCode = inventoryOrganizationCode;
	}


	public void setItemID(String itemID) {
		ItemID = itemID;
	}


	public void setProductClass(String productClass) {
		ProductClass = productClass;
	}


	public void setUnitOfMeasure(String unitOfMeasure) {
		UnitOfMeasure = unitOfMeasure;
	}


	public void setItem(Item item) {
		Item = item;
	}
	
	
	
}

package com.isec.sc.intgr.api.xml.beans;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="InventoryList",strict=false)
public class InventoryList {
	
	@ElementList(inline=true, entry="InventoryItem", required=false)
	private List<InventoryItem> InventoryItem = new ArrayList<InventoryItem>();
	
	@Attribute
	private String TotalInventoryItemList;
	
	@Attribute
	private String LastRecordSet;
	
	

	public List<InventoryItem> getInventoryItem() {
		return InventoryItem;
	}

	public void setInventoryItem(List<InventoryItem> inventoryItem) {
		InventoryItem = inventoryItem;
	}

	public String getTotalInventoryItemList() {
		return TotalInventoryItemList;
	}

	public void setTotalInventoryItemList(String totalInventoryItemList) {
		TotalInventoryItemList = totalInventoryItemList;
	}

	
	
	
}

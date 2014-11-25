package com.isec.sc.intgr.api.xml.beans;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="Item", strict=false)
public class Item {

	@Attribute
	private String ItemID;
	
	@Attribute
	private String ItemKey;
	
	@Attribute
	private String OrganizationCode;
	
	@Attribute
	private String UnitOfMeasure;
	
	@Path("PrimaryInformation")
	@Attribute(required=false)
	private String Description;
	
	@Path("PrimaryInformation")
	@Attribute(required=false)
	private String ShortDescription;

	
	
	public String getItemID() {
		return ItemID;
	}

	public String getItemKey() {
		return ItemKey;
	}

	public String getOrganizationCode() {
		return OrganizationCode;
	}

	public String getUnitOfMeasure() {
		return UnitOfMeasure;
	}

	public String getDescription() {
		return Description;
	}

	public String getShortDescription() {
		return ShortDescription;
	}

	
	
	
}



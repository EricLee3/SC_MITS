package com.isec.sc.intgr.api.xml.beans;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Organization", strict=false)
public class Organization {

	@Attribute
	private String InventoryOrganizationCode;
	
	@Attribute
	private String OrganizationCode;
	
	@Attribute
	private String OrganizationName;
	
	@Attribute
	private String ParentOrganizationCode;

	@Attribute
	private String LocaleCode;
	
	@Attribute
	private boolean IsHubOrganization;
	
	@Attribute
	private boolean IsNode;

	
	public String getInventoryOrganizationCode() {
		return InventoryOrganizationCode;
	}

	public String getOrganizationCode() {
		return OrganizationCode;
	}

	public String getOrganizationName() {
		return OrganizationName;
	}

	public String getParentOrganizationCode() {
		return ParentOrganizationCode;
	}

	public String getLocaleCode() {
		return LocaleCode;
	}

	public boolean isIsHubOrganization() {
		return IsHubOrganization;
	}

	public boolean isIsNode() {
		return IsNode;
	}
	
	
	
}

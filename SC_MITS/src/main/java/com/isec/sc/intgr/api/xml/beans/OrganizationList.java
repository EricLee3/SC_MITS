package com.isec.sc.intgr.api.xml.beans;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="OrganizationList",strict=false)
public class OrganizationList {
	
	@ElementList(inline=true, entry="Organization", required=false)
	private List<Organization> organization = new ArrayList<Organization>();

	
	public List<Organization> getOrganization() {
		return organization;
	}
	
	
	
}

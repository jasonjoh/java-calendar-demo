package com.outlook.dev.calendardemo.dto;

// The Graph object that represents an event location
// http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/location
public class Location {
	private PhysicalAddress address;
	private String displayName;
	
	public PhysicalAddress getAddress() {
		return address;
	}
	
	public void setAddress(PhysicalAddress address) {
		this.address = address;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Location(PhysicalAddress address, String displayName) {
		super();
		this.address = address;
		this.displayName = displayName;
	}
}

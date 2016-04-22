package com.outlook.dev.calendardemo.dto;

//The Graph object that represents an email address
//http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/emailaddress
public class EmailAddress {
	private String address;
	private String name;
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

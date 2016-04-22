package com.outlook.dev.calendardemo.dto;

// The Graph object that represents a physical address
//http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/physicalAddress
public class PhysicalAddress {
	private String city;
	private String countryOrRegion;
	private String postalCode;
	private String state;
	private String street;
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCountryOrRegion() {
		return countryOrRegion;
	}
	
	public void setCountryOrRegion(String countryOrRegion) {
		this.countryOrRegion = countryOrRegion;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
}

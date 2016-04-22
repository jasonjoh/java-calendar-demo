package com.outlook.dev.calendardemo.dto;

// The Graph object that represents a calendar folder
// http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/calendar
public class Calendar extends Base {
	// Name (e.g. "Calendar")
	private String name;
	// Color (e.g. "lightBlue" or "auto")
	private String color;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public Calendar(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	public Calendar() {
		this.name = null;
		this.color = null;
	}
}

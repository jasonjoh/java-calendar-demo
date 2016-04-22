package com.outlook.dev.calendardemo.dto;

// The Graph object that represents a date/time with timezone
// http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/datetimetimezone
public class DateTimeTimeZone {
	private String dateTime;
	private String timeZone;
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public DateTimeTimeZone(String dateTime, String timeZone) {
		this.dateTime = dateTime;
		this.timeZone = timeZone;
	}
	
	public DateTimeTimeZone() {
		this.dateTime = null;
		this.timeZone = null;
	}
}

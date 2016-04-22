package com.outlook.dev.calendardemo.dto;

import java.util.Date;

//The Graph object that represents an attendee's response status
//http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/responsestatus
public class ResponseStatus {
	private String response;
	private Date time;
	
	public String getResponse() {
		return response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
}

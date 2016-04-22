package com.outlook.dev.calendardemo.dto;

//The Graph object that represents a calendar event attendee
//http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/attendee
public class Attendee {
	private ResponseStatus status;
	private String type;
	private EmailAddress emailAddress;
	
	public ResponseStatus getStatus() {
		return status;
	}
	
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
}

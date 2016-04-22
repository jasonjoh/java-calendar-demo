package com.outlook.dev.calendardemo.dto;

// The Graph object that represents an item body
// http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/itembody
public class ItemBody {
	private String content;
	private String contentType;

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}

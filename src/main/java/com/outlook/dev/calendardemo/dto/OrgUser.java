package com.outlook.dev.calendardemo.dto;

// The Graph object that represents a user in the org
// NOTE: There are more available fields to the user, but these are all the
// app uses
public class OrgUser extends Base {
	// Display name (e.g. "Allie Bellew")
	private String displayName;
	// Email address (e.g. "allieb@contoso.com")
	private String mail;
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
}

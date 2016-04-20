package com.outlook.dev.calendardemo.dto;

import javax.json.JsonObject;

public class User {
	public String getDisplayName() {
		return displayName;
	}
	public String getEmail() {
		return email;
	}
	public String getTenantId() {
		return tenantId;
	}
	public boolean isConsentedForOrg() {
		return consentedForOrg;
	}
	private String displayName;
	private String email;
	private String tenantId;
	private boolean consentedForOrg;
	
	public User(JsonObject token, boolean consentedForOrg) {
		super();
		this.displayName = token.getString("name", null);
		String email = token.getString("upn", null);
		if (null == email) {
			email = token.getString("preferred_username", null);
		}
		this.email = email;
		this.tenantId = token.getString("tid", null);
		this.consentedForOrg = consentedForOrg;
	}
	
	
}

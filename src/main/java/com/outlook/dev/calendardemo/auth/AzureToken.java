package com.outlook.dev.calendardemo.auth;

import com.google.gson.annotations.SerializedName;

public class AzureToken {
	@SerializedName("token_type")
	private String tokenType;
	private String scope;
	@SerializedName("expires_in")
	private int expiresIn;
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("refresh_token")
	private String refreshToken;
	@SerializedName("id_token")
	private String idToken;
	
	public String getTokenType() {
		return tokenType;
	}
	
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public int getExpiresIn() {
		return expiresIn;
	}
	
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getIdToken() {
		return idToken;
	}
	
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
}

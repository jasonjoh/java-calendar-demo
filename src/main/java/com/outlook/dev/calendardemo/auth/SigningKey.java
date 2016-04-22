package com.outlook.dev.calendardemo.auth;

import com.google.gson.annotations.SerializedName;

public class SigningKey {
	@SerializedName("kid")
	private String keyId;
	@SerializedName("n")
	private String modulus;
	@SerializedName("e")
	private String exponent;
	
	public String getKeyId() {
		return keyId;
	}
	
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	
	public String getModulus() {
		return modulus;
	}
	
	public void setModulus(String modulus) {
		this.modulus = modulus;
	}
	
	public String getExponent() {
		return exponent;
	}
	
	public void setExponent(String exponent) {
		this.exponent = exponent;
	}
}

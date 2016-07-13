// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo.dto;

import java.util.Date;

import javax.json.JsonObject;

import com.outlook.dev.calendardemo.auth.AzureToken;

// An object representing a logged on user in the app
public class User {
	// The user's object ID from ID token
	private String id;
	// The user's display name from ID token
	private String displayName;
	// The user's email address from ID token
	private String email;
	// The organizational tenant ID from ID token
	private String tenantId;
	// Set to true if logged in through client credentials flow
	private boolean consentedForOrg;
	// The access token retrieved from the token endpoint
	private AzureToken tokenObj;
	// The date/time when the token expires
	private Date tokenExpires;
	
	public String getId() {
		return id;
	}
	
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
	public AzureToken getTokenObj() {
		return tokenObj;
	}
	public void setTokenObj(AzureToken tokenObj) {
		this.tokenObj = tokenObj;
		
		this.tokenExpires = new Date();
		// Adjust by 5 minutes to give some overlap in expiration
		this.tokenExpires.setSeconds(this.tokenExpires.getSeconds() + tokenObj.getExpiresIn() - 300);
	}
	
	public boolean isTokenExpired() {
		if (null == this.tokenExpires) {
			return true;
		}
		
		return this.tokenExpires.before(new Date());
	}
	
	public String getAccessToken() {
		if (null == this.tokenObj) {
			return null;
		}
		return String.format("Bearer %s", this.tokenObj.getAccessToken());
	}
	
	public String getRefreshToken() {
		if (null == this.tokenObj) {
			return null;
		}
		return this.tokenObj.getRefreshToken();
	}
	
	public User(JsonObject token, boolean consentedForOrg) {
		super();
		this.id = token.getString("oid");
		this.displayName = token.getString("name", null);
		// v1 tokens have the "upn" field
		String email = token.getString("upn", null);
		if (null == email) {
			// v2 tokens have the "preferred_username" field
			email = token.getString("preferred_username", null);
		}
		this.email = email;
		this.tenantId = token.getString("tid", null);
		this.consentedForOrg = consentedForOrg;
		this.tokenObj = null;
		this.tokenExpires = null;
	}
}

// MIT License:

// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// ""Software""), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:

// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
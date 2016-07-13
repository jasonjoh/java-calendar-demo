// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo.auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TokenService {
	@GET("/common/discovery/keys")
	Call<SigningKeys> getSigningKeys();
	
	@FormUrlEncoded
	@POST("/{tenantid}/oauth2/v2.0/token")
	Call<AzureToken> getUserAccessToken(
		@Path("tenantid") String tenantId,
		@Field("client_id") String clientId,
		@Field("client_secret") String clientSecret,
		@Field("grant_type") String grantType,
		@Field("code") String code,
		@Field("redirect_uri") String redirectUrl
	);
	
	@FormUrlEncoded
	@POST("/{tenantid}/oauth2/token")
	Call<AzureToken> getOrgAccessToken(
		@Path("tenantid") String tenantId,
		@Field("client_id") String clientId,
		@Field("client_assertion_type") String assertionType,
		@Field("client_assertion") String assertion,
		@Field("grant_type") String grantType,
		@Field("resource") String resource,
		@Field("redirect_uri") String redirectUrl
	);
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
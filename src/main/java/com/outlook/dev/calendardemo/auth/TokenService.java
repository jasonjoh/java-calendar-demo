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

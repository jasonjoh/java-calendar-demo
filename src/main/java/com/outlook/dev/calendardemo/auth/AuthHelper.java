// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo.auth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.outlook.dev.calendardemo.dto.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthHelper {
	// These values are used when doing the single user signup
	private static final String userSignUpClientId = "YOUR V2 CLIENT ID HERE";
	private static final String userSignUpClientSecret = "YOUR V2 CLIENT SECRET HERE";
	private static final String userAuthority = "login.microsoftonline.com";
	private static final String userAuthorizeUrl = "/common/oauth2/v2.0/authorize";
	
	// These values are used when doing the organizational signup
	private static final String orgSignUpClientId = "YOUR V1 CLIENT ID HERE";
	private static final String certThumbPrint = "YOUR CERT THUMBPRINT HERE";
	
	private static final String adminAuthority = "login.microsoftonline.com";
	private static final String adminAuthorizeUrl = "/common/oauth2/authorize";
	private static final String adminTokenUrl = "https://" + adminAuthority + "/%s/oauth2/token";
	
	private static final String assertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
	private static final String clientCredType = "client_credentials";
	
	// Scopes are used in the v2 app model, which is used to do single user signup
	// This specifies the permissions that the app requires
	private static final StringBuilder scopes = new StringBuilder()
			.append("openid")
			.append(" ").append("offline_access")
			.append(" ").append("profile")
			.append(" ").append("https://graph.microsoft.com/calendars.readwrite");
	
	public static String getUserSignUpUrl(String redirectUrl, UUID state, UUID nonce) {
		List<NameValuePair> query = new ArrayList<NameValuePair>();
		query.add(new BasicNameValuePair("client_id", userSignUpClientId));
		query.add(new BasicNameValuePair("redirect_uri", redirectUrl));
		query.add(new BasicNameValuePair("response_type", "code id_token"));
		query.add(new BasicNameValuePair("scope", scopes.toString()));
		query.add(new BasicNameValuePair("state", state.toString()));
		query.add(new BasicNameValuePair("nonce", nonce.toString()));
		query.add(new BasicNameValuePair("response_mode", "form_post"));
		
		URIBuilder builder = new URIBuilder()
				.setScheme("https")
				.setHost(userAuthority)
				.setPath(userAuthorizeUrl)
				.setParameters(query);
		
		String authUrl = "";
		try {
			authUrl = builder.build().toString();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return authUrl;
	}
	
	public static String getAdminSignUpUrl(String redirectUrl, UUID state, UUID nonce) {
		
		List<NameValuePair> query = new ArrayList<NameValuePair>();
		query.add(new BasicNameValuePair("client_id", orgSignUpClientId));
		query.add(new BasicNameValuePair("redirect_uri", redirectUrl));
		query.add(new BasicNameValuePair("response_type", "id_token"));
		query.add(new BasicNameValuePair("scope", "openid"));
		query.add(new BasicNameValuePair("state", state.toString()));
		query.add(new BasicNameValuePair("nonce", nonce.toString()));
		query.add(new BasicNameValuePair("prompt", "admin_consent"));
		query.add(new BasicNameValuePair("response_mode", "form_post"));
		query.add(new BasicNameValuePair("resource", "https://graph.microsoft.com"));
		
		URIBuilder builder = new URIBuilder()
				.setScheme("https")
				.setHost(adminAuthority)
				.setPath(adminAuthorizeUrl)
				.setParameters(query);
		
		String authUrl = "";
		try {
			authUrl = builder.build().toString();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return authUrl;
	}
	
	public static JsonObject validateUserToken(String encodedToken, UUID nonce) {
		// Validate the ID token, matching audience against the user signup client ID
		return validateIdToken(encodedToken, nonce, userSignUpClientId);
	}
	
	public static JsonObject validateAdminToken(String encodedToken, UUID nonce){
		// Validate the ID token, matching audience against the org signup client ID
		return validateIdToken(encodedToken, nonce, orgSignUpClientId);
	}
	
	private static JsonObject validateIdToken(String encodedToken, UUID nonce, String clientId){
		JsonObject tokenObj = null;
		
		// ID tokens are signed JWT, so start by breaking it into its three parts
		String[] tokenParts = encodedToken.split("\\.");
		
		String header = tokenParts[0];
		String idToken = tokenParts[1];
		String signature = tokenParts[2];
		
		try {
			
			// Check header
			String decodedHeader = new String(Base64.getUrlDecoder().decode(header));
			JsonReader headerReader = Json.createReader(new StringReader(decodedHeader));
			JsonObject headerObj = headerReader.readObject();
			headerReader.close();
			// From the header, check the signing algorithm (alg)
			// and the key ID (kid)
			String alg = headerObj.getString("alg");
			String kid = headerObj.getString("kid");
			
			// Check signature
			if (!verifyTokenSignature(header + "." + idToken, signature, alg, kid)){
				// Invalid signature
				return null;
			}
			
			// Decode token
			String decodedToken = new String(Base64.getUrlDecoder().decode(idToken));
			JsonReader tokenReader = Json.createReader(new StringReader(decodedToken));
			tokenObj = tokenReader.readObject();
			tokenReader.close();
			
			// Check nonce
			UUID tokenNonce = UUID.fromString(tokenObj.getString("nonce"));
			if (!tokenNonce.equals(nonce)){
				// Invalid nonce!
				return null;
			}
			
			// Check audience
			String audience = tokenObj.getString("aud");
			if (!audience.equals(clientId)){
				// Invalid audience
				return null;
			}
		}
		catch (Throwable ex){
			ex.printStackTrace();
		}
		
		return tokenObj;
	}
	
	private static boolean verifyTokenSignature(String content, String signature, String alg, String kid) throws IOException{
		
		TokenService tokenService = getTokenService();
		SigningKeys keys = tokenService.getSigningKeys().execute().body();
		
		String mod = null;
		String exp = null;
		
		for (int i = 0; i < keys.getKeys().length; i++) {
			if (keys.getKeys()[i].getKeyId().equals(kid)) {
				mod = keys.getKeys()[i].getModulus();
				exp = keys.getKeys()[i].getExponent();
				break;
			}
		}
		
		// Did we find our key?
		if (mod != null && !mod.isEmpty() && exp != null && !exp.isEmpty()){
			// Values are base64 url-encoded, convert to BigInteger
			BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(mod));
			BigInteger exponent = new BigInteger(Base64.getUrlDecoder().decode(exp));
			
			// Decode the signature
			byte[] decodedSignature = Base64.getUrlDecoder().decode(signature);
			
			try {
				// Create a public key based on the modulus and exponent retrieved from the JWK
				PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
				
				// Create a signature instance for verification, using the SHA-256 algorithm
				Signature sig = Signature.getInstance("SHA256withRSA");
				sig.initVerify(key);
				
				// Pass in the content that was signed (header + token)
				sig.update(content.getBytes());
				
				// Verify the signature
				return sig.verify(decodedSignature);
			} 
			catch (InvalidKeySpecException ikse) {
				// TODO Auto-generated catch block
				ikse.printStackTrace();
			} 
			catch (NoSuchAlgorithmException nsae) {
				// TODO Auto-generated catch block
				nsae.printStackTrace();
			} catch (InvalidKeyException ike) {
				// TODO Auto-generated catch block
				ike.printStackTrace();
			} catch (SignatureException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			}
		}

		// If we got here something didn't work, so fail validation
		return false;
	}
	
	public static AzureToken getTokenSilently(User user, String redirectUrl, ServletContext ctx){
		if (user.isConsentedForOrg()) {
			return getNewOrganizationToken(user, redirectUrl, ctx);
		}
		else {
			return getNewUserToken(user, redirectUrl, ctx);
		}
	}
	
	private static AzureToken getNewOrganizationToken(User user, String redirectUrl, ServletContext ctx) {
		
		// Get the private key store
		// This keystore has the private key that corresponds to the public key uploaded to
		// our app registration.
		InputStream keystore = ctx.getResourceAsStream("/WEB-INF/calendardemo.jks");

		try {
			return getOrganizationAccessToken(redirectUrl, user.getTenantId(), keystore);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static AzureToken getTokenFromAuthCode(User user, String redirectUrl, String authCode) {
		try {
			return getUserAccessToken(redirectUrl, user.getTenantId(), "authorization_code", authCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static AzureToken getNewUserToken(User user, String redirectUrl, ServletContext ctx) {
		if (user.getRefreshToken() == null) {
			return null;
		}
		
		try {
			return getUserAccessToken(redirectUrl, user.getTenantId(), "refresh_token", user.getRefreshToken());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static AzureToken getUserAccessToken(String redirectUrl, String tenantId, String requestType, String requestParameter) throws ClientProtocolException, IOException {
		TokenService tokenService = getTokenService();
		return tokenService.getUserAccessToken(tenantId, userSignUpClientId, userSignUpClientSecret, 
				requestType, requestParameter, redirectUrl).execute().body();
	}
	
	public static AzureToken getOrganizationAccessToken(String redirectUrl, String tenantId, InputStream keystoreStream) throws ClientProtocolException, IOException{
		// Get the client assertion from the keystore
		String assertion = getClientAssertion(String.format(adminTokenUrl, tenantId), keystoreStream);
		
		TokenService tokenService = getTokenService();
		return tokenService.getOrgAccessToken(tenantId, orgSignUpClientId, assertionType, assertion, 
				clientCredType, "https://graph.microsoft.com", redirectUrl).execute().body();
	}
	
	private static String getClientAssertion(String tokenUrl, InputStream keystoreStream){
		String assertion = null;
		
		/** 
		 * Create a UNIX epoch time value for now - 5 minutes
		 * Why -5 minutes? To allow for time skew between the local machine
		 * and the server.
		 */
		long nbf = System.currentTimeMillis() / 1000L + 300L;
		/**
		 * Add 15 minutes to nbf to get now + 10 minutes
		 */
		long exp = nbf + 900L;
		
		String assertionHeader = "{ \"alg\": \"RS256\", \"x5t\": \"" + certThumbPrint +  "\" }";
		String assertionPayload = "{ \"sub\": \"" + orgSignUpClientId + "\", ";
		assertionPayload += "\"iss\": \"" + orgSignUpClientId + "\", ";
		assertionPayload += "\"jti\": \"" + UUID.randomUUID().toString() + "\", ";
		assertionPayload += "\"exp\": \"" + exp + "\", ";
		assertionPayload += "\"nbf\": \"" + nbf + "\", ";
		assertionPayload += "\"aud\": \"" + tokenUrl + "\" }";
		
		// Generate base64-encoded assertion
		String unsignedAssertion = Base64.getUrlEncoder().encodeToString(assertionHeader.getBytes()) + "."
				+ Base64.getUrlEncoder().encodeToString(assertionPayload.getBytes());
		
		// Load the keystore which contains our private key
		try {
			KeyStore appKeyStore = KeyStore.getInstance("JKS");
			// TODO: key store password in code is a bad idea
			char[] password = "poiqwe1!".toCharArray();
			appKeyStore.load(keystoreStream, password);
			PrivateKey privKey = (PrivateKey)appKeyStore.getKey("calendardemo", password);
			
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(privKey);
			// Sign the assertion
			sign.update(unsignedAssertion.getBytes());
			byte[] signature = sign.sign();
			String assertionSig = Base64.getUrlEncoder().encodeToString(signature);
			
			assertion = unsignedAssertion + "." + assertionSig;
			
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return assertion;
	}
	
	// Helper function to initialize the calendar service
	private static TokenService getTokenService() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://login.microsoftonline.com/")
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		
		return retrofit.create(TokenService.class);
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
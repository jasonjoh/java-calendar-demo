package com.outlook.dev.calendardemo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
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
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class AuthHelper {
	private static final String clientId = "3082ece1-b32e-4884-a620-5fb68e5688c7";
	private static final String certThumbPrint = "LcsZc6fLX3Z5uJ0+TFswojfPRIE=";
	private static final String authority = "login.microsoftonline.com";
	private static final String authorizeUrl = "/common/oauth2/authorize";
	private static final String tokenUrl = "https://" + authority + "/%s/oauth2/token";
	
	public static String getSignUpUrl(String redirectUrl, UUID state, UUID nonce){
		
		List<NameValuePair> query = new ArrayList<NameValuePair>();
		query.add(new BasicNameValuePair("client_id", clientId));
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
				.setHost(authority)
				.setPath(authorizeUrl)
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
	
	public static JsonObject validateIdToken(String encodedToken, UUID nonce){
		JsonObject tokenObj = null;
		
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
		finally {
			return tokenObj;
		}
	}
	
	private static boolean verifyTokenSignature(String content, String signature, String alg, String kid) throws IOException{
		
		// Get MS OpenID token info
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet getKeyInfo = new HttpGet("https://login.microsoftonline.com/common/discovery/keys");
		CloseableHttpResponse keyInfoResponse = httpClient.execute(getKeyInfo);
		String keyInfo = null;
		try {
			HttpEntity keyInfoEntity = keyInfoResponse.getEntity();
			BufferedReader keyInfoReader = new BufferedReader(new InputStreamReader(keyInfoEntity.getContent()));
			keyInfo = keyInfoReader.readLine();
			keyInfoReader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			keyInfoResponse.close();
		}
		
		if (keyInfo != null) {
			JsonReader keyReader = Json.createReader(new StringReader(keyInfo));
			JsonObject keyObj = keyReader.readObject();
			keyReader.close();
			JsonArray keyArray = keyObj.getJsonArray("keys");
			
			String mod = null;
			String exp = null;
			
			// Check keys array for key with matching key ID
			for (int i = 0; i < keyArray.size(); i++) {
				JsonObject key = keyArray.getJsonObject(i);
				String keyId = key.getString("kid");
				if (keyId.equals(kid)) {
					// We have a match, get the
					// public key modulus (in the "n" claim)
					// and the exponent (in the "e" claim)
					mod = key.getString("n");
					exp = key.getString("e");
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
				finally {
					
				}
			}
		}
		
		// If we got here something didn't work, so fail validation
		return false;
	}
	
	public static JsonObject getAccessToken(String redirectUrl, String tenantId, InputStream keystoreStream) throws ClientProtocolException, IOException{
		JsonObject accessTokenObj = null;
		
		String assertion = getClientAssertion(String.format(tokenUrl, tenantId), keystoreStream);
		
		List<NameValuePair> tokenReqParams = new ArrayList<NameValuePair>();
		tokenReqParams.add(new BasicNameValuePair("resource", "https://graph.microsoft.com"));
		tokenReqParams.add(new BasicNameValuePair("client_id", clientId));
		tokenReqParams.add(new BasicNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
		tokenReqParams.add(new BasicNameValuePair("client_assertion", assertion));
		tokenReqParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
		tokenReqParams.add(new BasicNameValuePair("redirect_uri", redirectUrl));
		
		UrlEncodedFormEntity tokenReqForm = new UrlEncodedFormEntity(tokenReqParams, Consts.UTF_8);
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost getTokenReq = new HttpPost(String.format(tokenUrl, tenantId));
		getTokenReq.setEntity(tokenReqForm);
		CloseableHttpResponse tokenResponse = httpClient.execute(getTokenReq);
		try {
			BufferedReader tokenResponseReader = new BufferedReader(new InputStreamReader(tokenResponse.getEntity().getContent()));
			String accessToken = tokenResponseReader.readLine();
			tokenResponseReader.close();
			
			JsonReader tokenReader = Json.createReader(new StringReader(accessToken));
			accessTokenObj = tokenReader.readObject();
			tokenReader.close();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			tokenResponse.close();
		}
		
		return accessTokenObj;
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
		String assertionPayload = "{ \"sub\": \"" + clientId + "\", ";
		assertionPayload += "\"iss\": \"" + clientId + "\", ";
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
			char[] password = "poiqwe1!".toCharArray();
			appKeyStore.load(keystoreStream, password);
			PrivateKey privKey = (PrivateKey)appKeyStore.getKey("calendardemo", password);
			
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(privKey);
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
}

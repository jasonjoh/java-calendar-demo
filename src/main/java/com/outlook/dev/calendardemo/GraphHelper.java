package com.outlook.dev.calendardemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

public class GraphHelper {
	public static String endPoint = "https://graph.microsoft.com/v1.0/";
	
	public static JsonObject makeApiCall(ApiCallOptions options) {
		JsonObject response = null;
		
		if (!options.isValid()) {
			return null;
		}
		
		try {
			URIBuilder builder = null;
			if (null != options.absoluteUrl) {
				builder = new URIBuilder(options.absoluteUrl);
			}
			else {
				builder = new URIBuilder(endPoint);
				builder.setPath(builder.getPath() + options.relativeUrl);
			}
			
			if (null != options.queryOptions) {
				builder.setParameters(options.queryOptions);
			}
			
			CloseableHttpClient httpClient = HttpClients.custom()
					.setUserAgent("java-calendar-demo/1.0")
					.build();
			HttpUriRequest request = null;
			
			switch(options.method) {
			case GET:
				request = new HttpGet(builder.build());
				break;
			case POST:
				StringEntity postBody = new StringEntity(options.payload.toString(), ContentType.APPLICATION_JSON);
				HttpPost postRequest = new HttpPost(builder.build());
				postRequest.setEntity(postBody);
				request = postRequest;
				break;
			case PATCH:
				StringEntity patchBody = new StringEntity(options.payload.toString(), ContentType.APPLICATION_JSON);
				HttpPatch patchRequest = new HttpPatch(builder.build());
				patchRequest.setEntity(patchBody);
				request = patchRequest;
				break;
			case DELETE:
				request = new HttpDelete(builder.build());
				break;
			}
			
			BasicHeader[] requestHeaders = new BasicHeader[3];
			requestHeaders[0] = new BasicHeader("Authorization", "Bearer " + options.accessToken);
			requestHeaders[1] = new BasicHeader("client-request-id", UUID.randomUUID().toString());
			requestHeaders[2] = new BasicHeader("return-client-request-id", "true");
			request.setHeaders(requestHeaders);
			
			if (null != options.userEmail) {
				request.addHeader("X-AnchorMailbox", options.userEmail);
			}
			
			if (null != options.timezone) {
				request.addHeader("Prefer", "outlook.timezone=\"" + options.timezone + "\"");
			}
			
			if (null != options.customHeaders){
				request.setHeaders(options.customHeaders);
			}
			
			CloseableHttpResponse apiResponse = httpClient.execute(request);
			if (apiResponse.getStatusLine().getStatusCode() != 204) {
				HttpEntity responseEntity = apiResponse.getEntity();
				JsonReader responseReader = Json.createReader(responseEntity.getContent());
				response = responseReader.readObject();
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	public static JsonObject getOrgUsers(String accessToken, String pageUrl) {
		
		ApiCallOptions callOptions = new ApiCallOptions();
		if (null != pageUrl) {
			callOptions.absoluteUrl = pageUrl;
		}
		else {
			callOptions.relativeUrl = "users";
			callOptions.queryOptions = new ArrayList<NameValuePair>(1);
			callOptions.queryOptions.add(new BasicNameValuePair("$top", "10"));
		}
		callOptions.accessToken = accessToken;
		callOptions.method = HttpMethod.GET;
		
		return GraphHelper.makeApiCall(callOptions);
	}
	
	public static JsonObject getUserCalendar(String accessToken, String user, Date viewStart, Date viewEnd) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		ApiCallOptions callOptions = new ApiCallOptions();
		callOptions.relativeUrl = String.format("users/%s/calendarview", user);
		callOptions.queryOptions = new ArrayList<NameValuePair>(2);
		callOptions.queryOptions.add(new BasicNameValuePair("startDateTime", df.format(viewStart)));
		callOptions.queryOptions.add(new BasicNameValuePair("endDateTime", df.format(viewEnd)));
		callOptions.accessToken = accessToken;
		callOptions.userEmail = user;
		callOptions.method = HttpMethod.GET;
		
		return GraphHelper.makeApiCall(callOptions);
	}
	
	public static JsonObject createEvent(String accessToken, String user, JsonObject event) {
		
		ApiCallOptions callOptions = new ApiCallOptions();
		callOptions.relativeUrl = String.format("users/%s/events", user);
		callOptions.accessToken = accessToken;
		callOptions.userEmail = user;
		callOptions.method = HttpMethod.POST;
		callOptions.payload = event;
		
		return GraphHelper.makeApiCall(callOptions);
	}
	
	public static JsonObject updateEvent(String accessToken, String user, String itemId, JsonObject update) {
		
		ApiCallOptions callOptions = new ApiCallOptions();
		callOptions.relativeUrl = String.format("users/%s/events/%s", user, itemId);
		callOptions.accessToken = accessToken;
		callOptions.userEmail = user;
		callOptions.method = HttpMethod.PATCH;
		callOptions.payload = update;
		
		return GraphHelper.makeApiCall(callOptions);
	}
	
	public static void deleteEvent(String accessToken, String user, String itemId) {
		ApiCallOptions callOptions = new ApiCallOptions();
		callOptions.relativeUrl = String.format("users/%s/events/%s", user, itemId);
		callOptions.accessToken = accessToken;
		callOptions.userEmail = user;
		callOptions.method = HttpMethod.DELETE;
		
		GraphHelper.makeApiCall(callOptions);
	}
}

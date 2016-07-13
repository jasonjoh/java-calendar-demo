// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo.rawrest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

public class GraphHelper {
	public static String endPoint = "https://graph.microsoft.com/v1.0/";
	
	/**
	 * This method is used to do the actual REST calls to the server and read the response
	 * 
	 * @param options - The ApiCallOptions object that specifies the call parameters
	 * @return A JsonObject representation of the response body (if applicable)
	 */
	public static JsonObject makeApiCall(ApiCallOptions options) {
		JsonObject response = null;
		
		if (!options.isValid()) {
			return null;
		}
		
		try {
			URIBuilder builder = null;
			if (null != options.absoluteUrl) {
				// The absoluteUrl option is used to bypass URL building
				// When parsing a response from the server when paging is involved,
				// the server provides a "nextLink", which is a full URL to get the next
				// page. In this case, we want to just use that value as-is
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
			// The access token goes in the Authorization header
			requestHeaders[0] = new BasicHeader("Authorization", "Bearer " + options.accessToken);
			// Best practice to provide a client-request id
			// This is logged in our telemetry and helps us to find your requests if you contact
			// support for assistance
			requestHeaders[1] = new BasicHeader("client-request-id", UUID.randomUUID().toString());
			// Setting this to true causes the value you provide in client-request-id to be
			// included in the response, making it easier to correlate requests/responses.
			requestHeaders[2] = new BasicHeader("return-client-request-id", "true");
			request.setHeaders(requestHeaders);
			
			if (null != options.userEmail) {
				// Setting this helps the Outlook service route calls to the
				// correct mailbox server more efficiently
				request.addHeader("X-AnchorMailbox", options.userEmail);
			}
			
			if (null != options.timezone) {
				// Optional timezone. If set on calendar calls, the server treats
				// all date/times in the specified time zone.
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
			callOptions.queryOptions.add(new BasicNameValuePair("$top", "50"));
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
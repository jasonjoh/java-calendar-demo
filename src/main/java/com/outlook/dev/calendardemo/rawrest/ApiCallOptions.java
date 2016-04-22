package com.outlook.dev.calendardemo.rawrest;

import java.util.List;

import javax.json.JsonObject;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

public class ApiCallOptions {
	public String absoluteUrl;
	public String relativeUrl;
	public HttpMethod method;
	public String userEmail;
	public String timezone;
	public String accessToken;
	public List<NameValuePair> queryOptions;
	public Header[] customHeaders;
	public JsonObject payload;
	
	public boolean isValid() {
		if ((null == relativeUrl || relativeUrl.isEmpty()) &&
			(null == absoluteUrl || absoluteUrl.isEmpty())) {
			return false;
		}
		
		if (null == accessToken || accessToken.isEmpty()) {
			return false;
		}
		
		return true;
	}
}

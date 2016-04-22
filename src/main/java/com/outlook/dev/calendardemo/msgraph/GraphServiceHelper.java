package com.outlook.dev.calendardemo.msgraph;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphServiceHelper {
	// Helper function to initialize the calendar service
	public static GraphCalendarService getCalendarService() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://graph.microsoft.com/")
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		
		return retrofit.create(GraphCalendarService.class);
	}
	
	// Helper function to initialize the user service
	public static GraphUserService getUserService() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://graph.microsoft.com/")
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		
		return retrofit.create(GraphUserService.class);
	}
}

package com.outlook.dev.calendardemo.msgraph;

import com.outlook.dev.calendardemo.dto.Calendar;
import com.outlook.dev.calendardemo.dto.Event;
import com.outlook.dev.calendardemo.dto.GraphArray;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GraphCalendarService {

	// Calendar operations
	@GET("/{version}/me/calendars")
	Call<GraphArray<Calendar>> getCalendars(
		@Path("version") String version,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/users/{user}/calendars")
	Call<GraphArray<Calendar>> getCalendars(
		@Path("version") String version,
		@Path("user") String user,
		@Header("Authorization") String bearer
	);
	
	@POST("/{version}/me/calendars")
	Call<Calendar> createCalendar(
		@Path("version") String version,
		@Body Calendar calendar,
		@Header("Authorization") String bearer
	);
	
	@POST("/{version}/users/{user}/calendars")
	Call<Calendar> createCalendar(
		@Path("version") String version,
		@Path("user") String user,
		@Body Calendar calendar,
		@Header("Authorization") String bearer
	);
	
	@PATCH("/{version}/me/calendars/{id}")
	Call<Calendar> updateCalendar(
		@Path("version") String version,
		@Path("id") String id,
		@Body Calendar calendar,
		@Header("Authorization") String bearer
	);
	
	@PATCH("/{version}/users/{user}/calendars/{id}")
	Call<Calendar> updateCalendar(
		@Path("version") String version,
		@Path("user") String user,
		@Path("id") String id,
		@Body Calendar calendar,
		@Header("Authorization") String bearer
	);
	
	@DELETE("/{version}/me/calendars/{id}")
	Call<ResponseBody> deleteCalendar(
		@Path("version") String version,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
	
	@DELETE("/{version}/users/{user}/calendars/{id}")
	Call<ResponseBody> deleteCalendar(
		@Path("version") String version,
		@Path("user") String user,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
	
	// Event operations
	@GET("/{version}/me/calendars/{calendarid}/events")
	Call<GraphArray<Event>> getEvents(
		@Path("version") String version,
		@Path("calendarid") String calendarId,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/users/{user}/calendars/{calendarid}/events")
	Call<GraphArray<Event>> getEvents(
		@Path("version") String version,
		@Path("user") String user,
		@Path("calendarid") String calendarId,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/me/calendars/{calendarid}/calendarview")
	Call<GraphArray<Event>> getCalendarView(
		@Path("version") String version,
		@Path("calendarid") String calendarId,
		@Query("startDateTime") String startView,
		@Query("endDateTime") String endView,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/users/{user}/calendars/{calendarid}/calendarview")
	Call<GraphArray<Event>> getCalendarView(
		@Path("version") String version,
		@Path("user") String user,
		@Path("calendarid") String calendarId,
		@Query("startDateTime") String startView,
		@Query("endDateTime") String endView,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/me/events/{id}")
	Call<Event> getEvent(
		@Path("version") String version,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
	
	@GET("/{version}/users/{user}/events/{id}")
	Call<Event> getEvent(
		@Path("version") String version,
		@Path("user") String user,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
	
	@PATCH("/{version}/me/events/{id}")
	Call<Event> updateEvent(
		@Path("version") String version,
		@Path("id") String id,
		@Body Event event,
		@Header("Authorization") String bearer
	);
	
	@PATCH("/{version}/users/{user}/events/{id}")
	Call<Event> updateEvent(
		@Path("version") String version,
		@Path("user") String user,
		@Path("id") String id,
		@Body Event event,
		@Header("Authorization") String bearer
	);
	
	@DELETE("/{version}/me/events/{id}")
	Call<ResponseBody> deleteEvent(
		@Path("version") String version,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
	
	@DELETE("/{version}/users/{user}/events/{id}")
	Call<ResponseBody> deleteEvent(
		@Path("version") String version,
		@Path("user") String user,
		@Path("id") String id,
		@Header("Authorization") String bearer
	);
}

package com.outlook.dev.calendardemo.dto;

// The Graph object that represents a calendar event
// http://graph.microsoft.io/en-us/docs/api-reference/v1.0/resources/event
// NOTE: There are more fields available, these are the only ones the app uses
public class Event extends Base {
	private Attendee[] attendees;
	private ItemBody body;
	private String[] categories;
	private DateTimeTimeZone end;
	private String iCalUId;
	private Location location;
	private DateTimeTimeZone start;
	private String subject;
	
	public Attendee[] getAttendees() {
		return attendees;
	}
	
	public void setAttendees(Attendee[] attendees) {
		this.attendees = attendees;
	}
	
	public ItemBody getBody() {
		return body;
	}
	
	public void setBody(ItemBody body) {
		this.body = body;
	}
	
	public String[] getCategories() {
		return categories;
	}
	
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	
	public DateTimeTimeZone getEnd() {
		return end;
	}
	
	public void setEnd(DateTimeTimeZone end) {
		this.end = end;
	}
	
	public String getiCalUId() {
		return iCalUId;
	}
	
	public void setiCalUId(String iCalUid) {
		this.iCalUId = iCalUid;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public DateTimeTimeZone getStart() {
		return start;
	}
	
	public void setStart(DateTimeTimeZone start) {
		this.start = start;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public Event() {
		this.attendees = null;
		this.body = null;
		this.categories = null;
		this.end = null;
		this.iCalUId = null;
		this.location = null;
		this.start = null;
		this.subject = null;
	}
}

// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
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
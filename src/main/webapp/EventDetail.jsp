<%-- Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.outlook.dev.calendardemo.dto.User, com.outlook.dev.calendardemo.dto.Event"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<t:layout title="Event: ${event.getSubject()}">
	<ol class="breadcrumb">
		<li><a href="/java-calendar-demo/Calendars?selected-user=${selectedUser}">All Calendars</a></li>
		<li><a href="/java-calendar-demo/Events?selectedUser=${selectedUser}&calId=${calId}">Current Calendar</a></li>
		<li class="active">Event Details</li>
	</ol>
	<c:if test="${not empty isUpdate}">
		<div class="panel panel-success">
			<div class="panel-heading">Event updated</div>
		</div>
	</c:if>
	<div class="panel panel-default">
		<div class="panel-heading">Event Details</div>
		<div class="panel-body">
			<form method="post">
				<input type="hidden" name="selectedUser" value="${selectedUser}">
				<input type="hidden" name="eventId" value="${event.getId()}">
				<div class="form-group">
					<label for="subject">Subject</label>
					<input type="text" name="subject" class="form-control" value="${event.getSubject()}">
				</div>
				<div class="form-group">
					<label for="start">Start</label>
					<input type="text" name="start" class="form-control" value="${event.getStart().getDateTime()}">
					<label for="startTz">Timezone</label>
					<input type="text" name="startTz" class="form-control" value="${event.getStart().getTimeZone()}">
				</div>
				<div class="form-group">
					<label for="end">End</label>
					<input type="text" name="end" class="form-control" value="${event.getEnd().getDateTime()}">
					<label for="endTz">Timezone</label>
					<input type="text" name="endTz" class="form-control" value="${event.getEnd().getTimeZone()}">
				</div>
				<div class="form-group">
					<label for="location">Location</label>
					<input type="text" name="location" class="form-control" value="${event.getLocation().getDisplayName()}">
				</div>
				<button type="submit" class="btn btn-default">Update Event</button>
			</form>
		</div>
	</div>
</t:layout>

<%-- MIT License: -->

<%-- Permission is hereby granted, free of charge, to any person obtaining -->
<%-- a copy of this software and associated documentation files (the -->
<%-- ""Software""), to deal in the Software without restriction, including -->
<%-- without limitation the rights to use, copy, modify, merge, publish, -->
<%-- distribute, sublicense, and/or sell copies of the Software, and to -->
<%-- permit persons to whom the Software is furnished to do so, subject to -->
<%-- the following conditions: -->

<%-- The above copyright notice and this permission notice shall be -->
<%-- included in all copies or substantial portions of the Software. -->

<%-- THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, -->
<%-- EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF -->
<%-- MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND -->
<%-- NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE -->
<%-- LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION -->
<%-- OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION -->
<%-- WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. -->
<%-- Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.outlook.dev.calendardemo.dto.User, java.util.List, com.outlook.dev.calendardemo.dto.Event"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<t:layout title="Events">
	<ol class="breadcrumb">
		<li><a href="/java-calendar-demo/Calendars?selected-user=${selectedUser}">All Calendars</a></li>
		<li class="active">Current Calendar</li>
	</ol>
	<div class="panel panel-default">
		<div class="panel-heading">Date Range</div>
		<div class="panel-body">
			<form class="form-inline">
				<input type="hidden" name="selectedUser" value="${selectedUser}">
				<input type="hidden" name="calId" value="${calId}">
				<div class="form-group">
					<label for="start">Start</label>
					<input type="date" name="start" class="form-control" value="${start}">
				</div>
				<div class="form-group">
					<label for="end">End</label>
					<input type="date" name="end" class="form-control" value="${end}">
				</div>
				<button type="submit" class="btn btn-default">Update View</button>
			</form>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">Events</div>
		<table class="table">
			<tr>
				<th>Subject</th>
				<th>Start</th>
				<th>End</th>
				<th>Location</th>
				<th></th>
			</tr>
			<c:if test="${not empty events}">
				<c:forEach var="event" items="${events}">
					<tr>
						<td><a href="/java-calendar-demo/EventDetail?selectedUser=${selectedUser}&eventId=${event.getId()}">${event.getSubject()}</a></td>
						<td>${event.getStart().getDateTime()} (${event.getStart().getTimeZone()})</td>
						<td>${event.getEnd().getDateTime()} (${event.getEnd().getTimeZone()})</td>
						<td>${event.getLocation().getDisplayName()}</td>
						<td>
							<form class="form-inline" method="post">
								<input type="hidden" name="event-op" value="delete">
								<input type="hidden" name="selected-user" value="${selectedUser}">
								<input type="hidden" name="calId" value="${calId}">
								<input type="hidden" name="start" value="${start}">
								<input type="hidden" name="end" value="${end}">
								<input type="hidden" name="event-id" value="${event.getId()}">
								<button type="submit" class="btn btn-default">Delete</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
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
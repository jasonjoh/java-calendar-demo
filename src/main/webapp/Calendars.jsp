<%-- Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.outlook.dev.calendardemo.dto.User, java.util.List, com.outlook.dev.calendardemo.dto.Calendar"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<t:layout title="Calendars">
	<c:if test="${user.isConsentedForOrg()}">
		<div class="panel panel-default">
			<div class="panel-heading">Current User</div>
			<div class="panel-body">
				<form class="form-inline">
					<div class="form-group">
						<select class="form-control" name="selected-user">
							<c:forEach var="orgUser" items="${users}">
								<c:choose>
									<c:when test="${orgUser.getId().equals(selectedUser)}">
										<option value="${orgUser.getId()}" selected>${orgUser.getDisplayName()}</option>
									</c:when>
									<c:otherwise>
										<option value="${orgUser.getId()}">${orgUser.getDisplayName()}</option>
									</c:otherwise>
								</c:choose>
				           	</c:forEach>
						</select>
					</div>
					<button type="submit" class="btn btn-default">Change User</button>
				</form>
			</div>
		</div>
	</c:if>
	<div class="panel panel-default">
		<div class="panel-heading">Calendars</div>
		<table class="table">
			<tr>
				<th>Calendar Name</th>
				<th>Color</th>
				<th></th>
				<th></th>
			</tr>
			<c:if test="${not empty calendars}">
				<c:forEach var="calendar" items="${calendars}">
					<tr>
						<td><a href="/java-calendar-demo/Events?selectedUser=${selectedUser}&calId=${calendar.getId()}">${calendar.getName()}</a></td>
						<td>${calendar.getColor()}</td>
						<td>
							<form class="form-inline" method="post">
								<input type="hidden" name="calendar-op" value="rename">
								<input type="hidden" name="selected-user" value="${selectedUser}">
								<input type="hidden" name="calendar-id" value="${calendar.getId()}">
								<div class="form-group">
									<input type="text" class="form-control" name="new-name" placeholder="New Name">
								</div>
								<button type="submit" class="btn btn-default">Rename</button>
							</form>
						</td>
						<td>
							<form class="form-inline" method="post">
								<input type="hidden" name="calendar-op" value="delete">
								<input type="hidden" name="selected-user" value="${selectedUser}">
								<input type="hidden" name="calendar-id" value="${calendar.getId()}">
								<button type="submit" class="btn btn-default">Delete</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">Add Calendar</div>
		<div class="panel-body">Use the form below to create a new calendar</div>
		<div class="container" style="padding-bottom: 15px;">
			<form class="form-inline" method="post">
				<input type="hidden" name="calendar-op" value="create">
				<input type="hidden" name="selected-user" value="${selectedUser}">
				<div class="form-group">
					<label for="new-cal-name">Calendar Name</label>
					<input type="text" class="form-control" id="new-cal-name" name="new-cal-name">
				</div>
				<div class="form-group">
					<label for="new-cal-color">Calendar Color</label>
					<select class="form-control" id="new-cal-color" name="new-cal-color">
						<option value="auto">Auto</option>
						<option value="lightBlue">Light Blue</option>
						<option value="lightGreen">Light Green</option>
						<option value="lightOrange">Light Orange</option>
						<option value="lightGray">Light Gray</option>
						<option value="lightYellow">Light Yellow</option>
						<option value="lightTeal">Light Teal</option>
						<option value="lightPink">Light Pink</option>
						<option value="lightBrown">Light Brown</option>
						<option value="lightRed">Light Red</option>
					</select>
				</div>
				<button type="submit" class="btn btn-default">Create Calendar</button>
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
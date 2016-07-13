// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.outlook.dev.calendardemo.auth.AuthHelper;
import com.outlook.dev.calendardemo.dto.DateTimeTimeZone;
import com.outlook.dev.calendardemo.dto.Event;
import com.outlook.dev.calendardemo.dto.Location;
import com.outlook.dev.calendardemo.dto.User;
import com.outlook.dev.calendardemo.msgraph.GraphCalendarService;
import com.outlook.dev.calendardemo.msgraph.GraphServiceHelper;

/**
 * Servlet implementation class EventDetail
 */
@WebServlet("/EventDetail")
public class EventDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = ensureUser(request);
		
		if (null == user) {
			// Not signed in
			response.sendRedirect("index.jsp");
			return;
		}
		
		String selectedUser = request.getParameter("selectedUser");
		String eventId = request.getParameter("eventId");
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		
		Event event = calService.getEvent("v1.0", selectedUser, eventId, user.getAccessToken()).execute().body();
		request.setAttribute("event", event);
		request.setAttribute("selectedUser", selectedUser);
		request.getRequestDispatcher("EventDetail.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = ensureUser(request);
		
		if (null == user) {
			// Not signed in
			response.sendRedirect("index.jsp");
			return;
		}
		
		String selectedUser = request.getParameter("selectedUser");
		String eventId = request.getParameter("eventId");
		
		String newSubject = request.getParameter("subject");
		String newStart = request.getParameter("start");
		String newStartTz = request.getParameter("startTz");
		String newEnd = request.getParameter("end");
		String newEndTz = request.getParameter("endTz");
		String newLocation = request.getParameter("location");
		
		Event updateEvent = new Event();
		updateEvent.setSubject(newSubject);
		updateEvent.setStart(new DateTimeTimeZone(newStart, newStartTz));
		updateEvent.setEnd(new DateTimeTimeZone(newEnd, newEndTz));
		updateEvent.setLocation(new Location(null, newLocation));
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		Event result = calService.updateEvent("v1.0", selectedUser, eventId, updateEvent, user.getAccessToken()).execute().body();
		
		request.setAttribute("isUpdate", true);
		doGet(request, response);
	}

	// Helper method to make sure there is a user in the session
	// and check if the access token is expired. Refresh the token
	// if it is expired.
	private User ensureUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		if (null == user) {
			return null;
		}
		
		if (user.isTokenExpired()) {
			String authServlet = user.isConsentedForOrg() ? "AuthorizeOrganization" : "AuthorizeUser";
			StringBuffer requestUrl = request.getRequestURL();
			String redirectUrl = requestUrl.replace(requestUrl.lastIndexOf("/") + 1, requestUrl.length(), authServlet).toString();
			
			user.setTokenObj(AuthHelper.getTokenSilently(user, redirectUrl, this.getServletContext()));
			session.setAttribute("user", user);
		}
		
		return user;
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
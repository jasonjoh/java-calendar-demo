// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.outlook.dev.calendardemo.auth.AuthHelper;
import com.outlook.dev.calendardemo.dto.Event;
import com.outlook.dev.calendardemo.dto.GraphArray;
import com.outlook.dev.calendardemo.dto.User;
import com.outlook.dev.calendardemo.msgraph.GraphCalendarService;
import com.outlook.dev.calendardemo.msgraph.GraphServiceHelper;

/**
 * Servlet implementation class Events
 */
public class Events extends HttpServlet {
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
		String calendarId = request.getParameter("calId");
		String startView = request.getParameter("start");
		String endView = request.getParameter("end");
		
		if (null == startView || null == endView) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// Set default values
			// Start today at midnight
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			startView = format.format(cal.getTime());
			
			cal.add(Calendar.DAY_OF_MONTH, 7);
			
			endView = format.format(cal.getTime());
		}
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		
		//GraphArray<Event> events = calService.getEvents("v1.0", selectedUser, calendarId, user.getAccessToken()).execute().body();
		GraphArray<Event> events = calService.getCalendarView("v1.0", selectedUser, calendarId, startView, endView, user.getAccessToken()).execute().body();
		
		request.setAttribute("selectedUser", selectedUser);
		request.setAttribute("calId", calendarId);
		request.setAttribute("start", startView);
		request.setAttribute("end", endView);
		request.setAttribute("events", null == events ? null : events.getValue());
		request.getRequestDispatcher("Events.jsp").forward(request, response);
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
		
		String selectedUser = request.getParameter("selected-user");
		String calendarId = request.getParameter("calId");
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		
		// Figure out which operation we are doing
		String eventOp = request.getParameter("event-op");
		
		if (eventOp.equals("delete")) {
			String deleteEventId = request.getParameter("event-id");
			calService.deleteEvent("v1.0", selectedUser, deleteEventId, user.getAccessToken()).execute();
		}
		
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
// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.outlook.dev.calendardemo.auth.AuthHelper;
import com.outlook.dev.calendardemo.dto.Calendar;
import com.outlook.dev.calendardemo.dto.GraphArray;
import com.outlook.dev.calendardemo.dto.OrgUser;
import com.outlook.dev.calendardemo.dto.User;
import com.outlook.dev.calendardemo.msgraph.GraphCalendarService;
import com.outlook.dev.calendardemo.msgraph.GraphServiceHelper;
import com.outlook.dev.calendardemo.msgraph.GraphUserService;

/**
 * Servlet implementation class Calendars
 */
public class Calendars extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	// List a user's calendars
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = ensureUser(request);
		
		if (null == user) {
			// Not signed in
			response.sendRedirect("index.jsp");
			return;
		}
		
		// Selected user is set to the logged in user if the app is using single-user flow
		// If the app is using the organization flow, the user can change the selected user on the page
		String selectedUser = request.getParameter("selected-user");
		if (null != selectedUser) {
			request.setAttribute("selectedUser", selectedUser);
		}
		else {
			selectedUser = user.getId();
			request.setAttribute("selectedUser", selectedUser);
		}
		
		if (user.isConsentedForOrg()) {
			// If the is the org flow, we need to populate a drop down with a list of users
			// This executes every time the page loads, which isn't terribly efficient
			// It is probably a better idea to cache the user list in a database somewhere
			// Get list of users from Graph
			GraphUserService userService = GraphServiceHelper.getUserService();
			GraphArray<OrgUser> users = userService.getUsers("v1.0", user.getAccessToken()).execute().body();
			request.setAttribute("users", users.getValue());
		}		
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		GraphArray<Calendar> userCalendars = null;
		// Get list of user's calendars
		userCalendars = calService.getCalendars("v1.0", selectedUser, user.getAccessToken()).execute().body();
		request.setAttribute("calendars", null == userCalendars ? null : userCalendars.getValue());
		request.getRequestDispatcher("Calendars.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	// Make some change to the user's calendar list (create, update, delete)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = ensureUser(request);
		if (null == user) {
			// Not signed in
			response.sendRedirect("index.jsp");
			return;
		}
		
		String selectedUser = request.getParameter("selected-user");
		
		GraphCalendarService calService = GraphServiceHelper.getCalendarService();
		
		// Figure out which operation we are doing
		String calendarOp = request.getParameter("calendar-op");
		if (calendarOp.equals("create")) {
			String name = request.getParameter("new-cal-name");
			String color = request.getParameter("new-cal-color");
			
			Calendar newCalendar = new Calendar(name, color);
			Calendar result = calService.createCalendar("v1.0", selectedUser, newCalendar, user.getAccessToken()).execute().body();
		}
		else if (calendarOp.equals("rename")) {
			String updateId = request.getParameter("calendar-id");
			String newName = request.getParameter("new-name");
			Calendar update = new Calendar();
			update.setName(newName);
			calService.updateCalendar("v1.0", selectedUser, updateId, update, user.getAccessToken()).execute();
		}
		else if (calendarOp.equals("delete")) {
			String deleteId = request.getParameter("calendar-id");
			calService.deleteCalendar("v1.0", selectedUser, deleteId, user.getAccessToken()).execute();
		}
		
		// Now that the change is made, reload the list of calendars
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
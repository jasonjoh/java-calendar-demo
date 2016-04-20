package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.outlook.dev.calendardemo.dto.Calendar;
import com.outlook.dev.calendardemo.dto.User;

/**
 * Servlet implementation class Calendars
 */
public class Calendars extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		if (null == user) {
			// Not signed in
			response.sendRedirect("index.jsp");
		}
		
		if (user.isConsentedForOrg()) {
			// Get list of users from Graph
		}
		
		// Get list of user's calendars
		List<Calendar> userCalendars = new ArrayList<Calendar>();
		Calendar foo = new Calendar();
		foo.setName("Foo");
		foo.setColor("auto");
		
		Calendar bar = new Calendar();
		bar.setName("Bar");
		bar.setColor("lightBlue");
		userCalendars.add(foo);
		userCalendars.add(bar);
		
		request.setAttribute("calendars", userCalendars);
		
		request.getRequestDispatcher("Calendars.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

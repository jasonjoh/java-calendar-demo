package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;

/**
 * Servlet implementation class CalendarOperations
 */
public class CalendarOperations extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalendarOperations() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		StringBuffer requestUrl = request.getRequestURL();
		String redirectUrl = requestUrl.replace(requestUrl.lastIndexOf("/") + 1, requestUrl.length(), "SignUp").toString();
		
		String accessToken = getOrRefreshAccessToken(session, redirectUrl);
		
		PrintWriter pw = response.getWriter();
		
		pw.append("<html><body>");
		pw.append(BuildPageForm(accessToken, null));
		pw.append("</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		StringBuffer requestUrl = request.getRequestURL();
		String redirectUrl = requestUrl.replace(requestUrl.lastIndexOf("/") + 1, requestUrl.length(), "SignUp").toString();
		
		String accessToken = getOrRefreshAccessToken(session, redirectUrl);
		
		String user = request.getParameter("username");
		String action = request.getParameter("action");
		
		PrintWriter pw = response.getWriter();
		pw.append("<html><body>");
		pw.append(BuildPageForm(accessToken, user));
		
		if (action.equals("updateevent")){
			String itemId = request.getParameter("itemid");
			UpdateEventSubject(accessToken, user, itemId);
			pw.append(BuildDefaultCalendarView(accessToken, user));
		}
		else if (action.equals("deleteevent")) {
			String itemId = request.getParameter("itemid");
			DeleteEvent(accessToken, user, itemId);
			pw.append(BuildDefaultCalendarView(accessToken, user));
		}
		else if (action.equals("viewcal")) {
			pw.append(BuildDefaultCalendarView(accessToken, user));
		}
		else if (action.equals("addevent")) {
			pw.append(BuildNewEventForm(user));
		}
		else if (action.equals("createevent")) {
			String subject = request.getParameter("subject");
			String start = request.getParameter("start");
			String end = request.getParameter("end");
			String location = request.getParameter("location");
			JsonObject result = CreateEvent(accessToken, user, subject, start, end, location);
			pw.append("<code>");
			pw.append(result.toString());
			pw.append("</code>");
		}
		
		pw.append("</body></html>");
	}
	
	private String getOrRefreshAccessToken(HttpSession session, String redirectUrl) {
		
		JsonObject accessToken = (JsonObject)session.getAttribute("cal_demo_token");
		Date expiry = null;
		
		if (accessToken != null){
			expiry = new Date(Long.parseLong(accessToken.getString("expires_on"))* 1000);
		}
		
		if (accessToken == null || expiry.before(new Date())) {
			String tenantId = session.getAttribute("cal_demo_tenantid").toString();
			// Request a new token
			// Get the private key store
			InputStream keystore = this.getServletContext().getResourceAsStream("/WEB-INF/calendardemo.jks");

			try {
				accessToken = AuthHelper.getAccessToken(redirectUrl, tenantId, keystore);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.setAttribute("cal_demo_token", accessToken);
		}
		
		return accessToken.getString("access_token");
	}
	
	private String BuildPageForm(String accessToken, String user) {
		
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<form method=\"post\" action=\"CalendarOperations\">");
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"username\">Select user:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append(BuildUserList(accessToken, user));
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"action\">Select action:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=\"radio\" name=\"action\" value=\"viewcal\" checked > View Calendar<br>");
		formBuilder.append("<input type=\"radio\" name=\"action\" value=\"addevent\"> Add Event<br>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=\"submit\" value=\"Go\"/>");
		formBuilder.append("</div>");
		formBuilder.append("</form>");
		
		return formBuilder.toString();
	}
	
	private String BuildUserList(String accessToken, String selectedUser) {
		
		StringBuilder htmlOptions = new StringBuilder();
		htmlOptions.append("<select name=\"username\">");
		
		String match = null == selectedUser ? "" : selectedUser;
		
		JsonObject apiResponse = null;
		
		do {
			String nextLink = null;
			if (null != apiResponse) {
				nextLink = apiResponse.getString("@odata.nextLink");
			}
			apiResponse = GraphHelper.getOrgUsers(accessToken, nextLink);
			
			JsonArray userArray = apiResponse.getJsonArray("value");
			for (int i = 0; i < userArray.size(); i++) {
				JsonObject user = userArray.getJsonObject(i);
				String upn = user.getString("userPrincipalName");
				String display = user.getString("displayName");
				
				String format = upn.equals(match) ? "<option value=%s selected>%s</option>" : "<option value=%s>%s</option>";
				
				htmlOptions.append(String.format(format, upn, display));			}
			
		} while (apiResponse.containsKey("@odata.nextLink"));
		
		htmlOptions.append("</select>");
		
		return htmlOptions.toString();
	}

	private String BuildDefaultCalendarView(String accessToken, String user) {
		StringBuilder view = new StringBuilder();
		
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Date viewStart = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		Date viewEnd = cal.getTime();
		
		view.append(String.format("<h2>%s's calendar: %s - %s", user, viewStart.toString(), viewEnd.toString()));
		
		view.append("<table style=\"border-style: solid; border-width: 1px;\">");
		view.append("<tr><th>Subject</th><th>Start</th><th>End</th><th>Location</th><th>Actions</th></tr>");
		
		JsonObject eventsResponse = GraphHelper.getUserCalendar(accessToken, user, viewStart, viewEnd);
		JsonArray events = eventsResponse.getJsonArray("value");
		for (int i = 0; i < events.size(); i++) {
			JsonObject event = events.getJsonObject(i);
			
			String itemId = event.getString("id");
			String subject = event.getString("subject");
			JsonObject start = event.getJsonObject("start");
			JsonObject end = event.getJsonObject("end");
			JsonObject location = event.getJsonObject("location");
			
			view.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s%s</td></tr>", 
					subject, start.getString("dateTime"), end.getString("dateTime"), location.getString("displayName"),
					BuildUpdateEventForm(user, itemId), BuildDeleteEventForm(user, itemId)));
		}
		
		return view.toString();
	}
	
	private JsonObject CreateEvent(String accessToken, String user, String subject, String start, String end, String location) {
		JsonObjectBuilder newEventBuilder = Json.createObjectBuilder();
		JsonObjectBuilder startBuilder = Json.createObjectBuilder();
		JsonObjectBuilder endBuilder = Json.createObjectBuilder();
		JsonObjectBuilder locationBuilder = Json.createObjectBuilder();
		
		startBuilder.add("dateTime", start);
		startBuilder.add("timeZone", "UTC");
		
		endBuilder.add("dateTime", end);
		endBuilder.add("timeZone", "UTC");
		
		locationBuilder.add("displayName", location);
		
		newEventBuilder.add("subject", subject);
		newEventBuilder.add("start", startBuilder);
		newEventBuilder.add("end", endBuilder);
		newEventBuilder.add("location", locationBuilder);
		
		return GraphHelper.createEvent(accessToken, user, newEventBuilder.build());
	}
	
	private void UpdateEventSubject(String accessToken, String user, String itemId) {
		JsonObjectBuilder updateBuilder = Json.createObjectBuilder();
		updateBuilder.add("subject", "UPDATED SUBJECT");
		
		JsonObject updateResult = GraphHelper.updateEvent(accessToken, user, itemId, updateBuilder.build());
	}
	
	private void DeleteEvent(String accessToken, String user, String itemId) {
		GraphHelper.deleteEvent(accessToken, user, itemId);
	}
	
	private String BuildNewEventForm(String user) {
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<form method=\"post\" action=\"CalendarOperations\">");
		formBuilder.append("<input type=\"hidden\" name=\"action\" value=\"createevent\">");
		formBuilder.append(String.format("<input type=\"hidden\" name=\"username\" value=\"%s\">", user));
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"subject\">Subject:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=text name=\"subject\">");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"start\">Start:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=text name=\"start\" value=\"2016-04-12T21:00:00\">");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"end\">End:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=text name=\"end\" value=\"2016-04-12T21:30:00\">");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<label for=\"location\">Location:</label>");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=text name=\"location\">");
		formBuilder.append("</div>");
		formBuilder.append("<div>");
		formBuilder.append("<input type=\"submit\" value=\"Add\">");
		formBuilder.append("</div>");
		formBuilder.append("</form>");
		
		return formBuilder.toString();
	}
	
	private String BuildUpdateEventForm(String user, String itemId) {
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<form method=\"post\" action=\"CalendarOperations\">");
		formBuilder.append("<input type=\"hidden\" name=\"action\" value=\"updateevent\">");
		formBuilder.append(String.format("<input type=\"hidden\" name=\"itemid\" value=\"%s\">", itemId));
		formBuilder.append(String.format("<input type=\"hidden\" name=\"username\" value=\"%s\">", user));
		formBuilder.append("<input type=\"submit\" value=\"Update\">");
		formBuilder.append("</form>");
		
		return formBuilder.toString();
	}
	
	private String BuildDeleteEventForm(String user, String itemId) {
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<form method=\"post\" action=\"CalendarOperations\">");
		formBuilder.append("<input type=\"hidden\" name=\"action\" value=\"deleteevent\">");
		formBuilder.append(String.format("<input type=\"hidden\" name=\"itemid\" value=\"%s\">", itemId));
		formBuilder.append(String.format("<input type=\"hidden\" name=\"username\" value=\"%s\">", user));
		formBuilder.append("<input type=\"submit\" value=\"Delete\">");
		formBuilder.append("</form>");
		
		return formBuilder.toString();
	}
}

package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		String tenantId = session.getAttribute("cal_demo_tenantid").toString();
		JsonObject accessToken = (JsonObject)session.getAttribute("cal_demo_token");
		Date expiry = null;
		
		if (accessToken != null){
			expiry = new Date(Long.parseLong(accessToken.getString("expires_on"))* 1000);
		}
		
		if (accessToken == null || expiry.before(new Date())) {
			// Request a new token
			StringBuffer requestUrl = request.getRequestURL();
			String redirectUrl = requestUrl.replace(requestUrl.lastIndexOf("/") + 1, requestUrl.length(), "SignUp").toString();
			
			// Get the private key store
			InputStream keystore = this.getServletContext().getResourceAsStream("/WEB-INF/calendardemo.jks");

			accessToken = AuthHelper.getAccessToken(redirectUrl, tenantId, keystore);
			session.setAttribute("cal_demo_token", accessToken);
		}

		response.getWriter().append("Access Token: ").append(accessToken.getString("access_token"));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

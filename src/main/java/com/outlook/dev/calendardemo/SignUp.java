package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SignUp
 */
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		//response.getWriter().append("Served at: ").append(request.getContextPath());

		try {
			StringBuffer redirect = request.getRequestURL();
			
			// Generate a UUID for state and nonce
			// State value should be returned when Azure redirects back, so we
			// can validate that the response contains our expected state
			UUID state = UUID.randomUUID();
			// Nonce value will be embedded in the ID token that comes back, which
			// helps to validate that our token is valid
			UUID nonce = UUID.randomUUID();
			HttpSession session = request.getSession();
			session.setAttribute("auth_state", state);
			session.setAttribute("auth_nonce", nonce);
			
			out.println("<html><body>");
			out.println("<h2>Sign Up</h2>");
			out.println("<p>To begin, we need an administrator to sign in and grant access to the app.<p>");
			out.println("<p><strong>Auth state (expected in response):</strong> " + state + "</p>");
			out.println("<p><strong>Nonce (expected in id token):</strong> " + nonce + "</p>");
			out.println("<p><a href=\"" + AuthHelper.getSignUpUrl(redirect.toString(), state, nonce) + "\">Click here</a> to sign in.");
			out.println("</body></html>");
		}
		finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Retrieve the saved state and nonce for comparison
		HttpSession session = request.getSession();
		String expectedState = session.getAttribute("auth_state").toString();
		String expectedNonce = session.getAttribute("auth_nonce").toString();
		session.removeAttribute("auth_state");
		session.removeAttribute("auth_nonce");
		
		PrintWriter out = response.getWriter();
		
		try {
			out.println("<html><body>");
			out.println("<h2>Authorization results</h2>");
			
			// Get the state parameter from the request
			String state = request.getParameter("state");
			if (!state.equals(expectedState)) {
				out.println("<p><strong>Unexpected state</strong> - The value of the <code>state</code> parameter in the request is not the expected value.</p>");
				out.println("<p>Expected state: " + expectedState + "</p>");
				out.println("<p>State from request: " + state + "</p>");
			}
			else{
				// Check if there is an error
				String error = request.getParameter("error");
				if (error != null && !error.isEmpty()){
					// Get the error description
					String description = request.getParameter("error_description");
					out.println("<p><strong>ERROR: " + error + "</strong> - " + description + "</p>");
				}
				else{
					// Get the ID token from the request
					String encodedToken = request.getParameter("id_token");
					// Validate the token. If valid it returns the token as a JsonObject
					JsonObject token = AuthHelper.validateIdToken(encodedToken, UUID.fromString(expectedNonce));
					
					if (token != null){
						// Token is valid, we can proceed
						out.println("<p>The authorization request returned a valid ID token, indicating that the admin has granted consent to our app.</p>");
						String userName = token.getString("name");
						String userEmail = token.getString("upn");
						session.setAttribute("cal_demo_user", userName);
						session.setAttribute("cal_demo_email", userEmail);
						out.println("<p>The consenting administrator was " + userName + " &lt;" + userEmail + "&gt;</p>");
						String tenantId = token.getString("tid"); 
						session.setAttribute("cal_demo_tenantid", tenantId);
						out.println("<p>From the token, we've found the organization's tenant ID: <code>" + tenantId + "</code>, which we can use to build the token request URL.");
						out.println("<p>To continue, go to the <a href=\"CalendarOperations\">calendar operations page</a>.</p>");
					}
					else{
						// Token invalid
						out.println("<p><strong>Invalid token</strong></p>");
						out.println("<code>" + encodedToken + "</code>");
					}
				}
			}
			
			out.println("</body></html>");
		}
		finally {
			out.close();
		}
	}

}

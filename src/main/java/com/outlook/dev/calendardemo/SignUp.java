// Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
package com.outlook.dev.calendardemo;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.outlook.dev.calendardemo.auth.AuthHelper;

/**
 * Servlet implementation class SignUp
 */
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			StringBuffer redirect = request.getRequestURL();
			String userRedirect = redirect.replace(redirect.lastIndexOf("/") + 1, redirect.length(), "AuthorizeUser").toString();
			String adminRedirect = redirect.replace(redirect.lastIndexOf("/") + 1, redirect.length(), "AuthorizeOrganization").toString();
			
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
			
			// Build an org signup and user signup URL and pass both to the page
			String adminSignUpUrl = AuthHelper.getAdminSignUpUrl(adminRedirect, state, nonce);
			String userSignUpUrl = AuthHelper.getUserSignUpUrl(userRedirect, state, nonce);
			
			request.setAttribute("adminUrl", adminSignUpUrl);
			request.setAttribute("userUrl", userSignUpUrl);
			
			RequestDispatcher dispatch = request.getRequestDispatcher("SignUp.jsp");
			dispatch.forward(request, response);
			return;
		}
		finally {
		}
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
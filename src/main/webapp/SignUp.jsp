<%-- Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Sign Up">
	<div class="container body-content">
		<div class="jumbotron">
			<h1>Sign up</h1>
			<p>The first step is to sign up, which will handle the process of connecting your Outlook calendar to this application. Choose the option below which is best suited for your situation.</p>
		</div>
		<div class="row">
			<div class="col-lg-6">
				<h2>Connect my calendar</h2>
				<p class="text-info">Compatible with Outlook.com and Office 365</p>
				<p>Choose this option to login as a single user and connect their calendar to the app.</p>
				<p><a class="btn btn-primary" href="${userUrl}">Sign me up</a></p>
			</div>
			<div class="col-lg-6">
				<h2>Connect all calendars in my organization</h2>
				<p class="text-info">Compatible with Office 365 only, requires an administrator to login</p>
				<p>Choose this option to login as an administrator and connect all calendars in the Office 365 organization to the app.</p>
				<p><a class="btn btn-primary" href="${adminUrl}">Sign my organization up</a></p>
			</div>
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
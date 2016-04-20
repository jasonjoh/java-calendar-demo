<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Home Page">
	<div class="container body-content">
		<div class="jumbotron">
			<h1>Welcome to the Java Calendar Demo!</h1>
			<p>This demo app uses the <a target="_blank" href="https://msdn.microsoft.com/en-us/library/azure/dn645542.aspx">Azure OAuth Authorization Code Grant flow</a> for individual user sign in and the <a target="_blank" href="https://msdn.microsoft.com/en-us/library/azure/dn645543.aspx">Azure OAuth Client Credential flow</a> for organizational administrator sign in. It uses the <a target="_blank" href="http://graph.microsoft.io">Microsoft Graph</a> to access calendars.</p>
			<p><a class="btn btn-primary btn-lg" href="/java-calendar-demo/SignUp">Get Started</a></p>
		</div>
	</div>
</t:layout>

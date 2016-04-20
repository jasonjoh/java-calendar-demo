<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Error">
	<div class="container body-content">
		<div class="jumbotron">
			<h1>Error</h1>
			<p class="text-danger">${error_message}</p>
			<p><a class="btn btn-primary btn-lg" href="/java-calendar-demo">Start over</a></p>
		</div>
	</div>
</t:layout>

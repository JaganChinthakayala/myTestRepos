<%@page import="com.orchestranetworks.ps.workflow.WorkflowLauncher"%>

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	(new WorkflowLauncher()).execute(request, "ChangeRequest", 
		null, null);
%>
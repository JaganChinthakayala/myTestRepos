<%@page import="com.orchestranetworks.ps.admin.cleanworkflows.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	(new CleanWorkflowsService("clean-comm-workflows.properties"))
			.service(request, response);
%>
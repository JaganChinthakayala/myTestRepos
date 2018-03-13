<%@page import="com.orchestranetworks.ps.project.admin.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	(new ClearWorkflowPermissionsCacheService())
			.service(request, response);
%>
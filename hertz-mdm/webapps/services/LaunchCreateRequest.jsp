<%@page import="com.orchestranetworks.ps.workflow.WorkflowLauncher"%>
<%@page import="com.hertz.mdm.location.path.LocationPaths"%>
<%@page import="com.orchestranetworks.service.ServiceContext" %>

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	(new WorkflowLauncher()).execute(request, "CreateRequest", 
		ServiceContext.getServiceContext(request).getCurrentTable().getPathInSchema().format(), null);
%>
<%@page import="com.orchestranetworks.ps.workflow.WorkflowLauncher"%>
<%@page import="com.hertz.mdm.common.path.CommonReferencePaths"%>
<%@page import="com.orchestranetworks.service.ServiceContext" %>
<%@page import="com.hertz.mdm.location.workflow.launcher.CloseLocationWorkflowLauncher" %>
<%@page import="com.hertz.mdm.location.enums.LocationProjectTypes" %>

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
(new CloseLocationWorkflowLauncher()).execute(request, LocationProjectTypes.CLOSE_LOCATION);
%>
%>
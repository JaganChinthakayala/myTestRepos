<%@page import="com.orchestranetworks.ps.workflow.WorkflowLauncher"%>
<%@page import="com.hertz.mdm.common.path.CommonReferencePaths"%>
<%@page import="com.orchestranetworks.service.ServiceContext" %>
<%@page import="com.hertz.mdm.location.workflow.launcher.OpenLocationWorkflowLauncher" %>
<%@page import="com.hertz.mdm.location.enums.LocationProjectTypes" %>

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
(new OpenLocationWorkflowLauncher()).execute(request, LocationProjectTypes.OPEN_LOCATION);
%>
%>
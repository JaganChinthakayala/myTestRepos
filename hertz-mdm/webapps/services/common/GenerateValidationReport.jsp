<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.onwbp.base.text.Severity" %>
<%@page import="com.orchestranetworks.ps.validation.service.GenerateValidationReport"%>
<%@page import="com.orchestranetworks.service.ServiceContext"%>

<%
	String dataSetName = ServiceContext.getServiceContext(request).getCurrentAdaptation().getAdaptationName().getStringName();
	GenerateValidationReport report = new GenerateValidationReport(request, response,
		System.getProperty("ebx.home"), "validation/" + dataSetName, true, true,
		GenerateValidationReport.DEFAULT_FILE_DOWNLOADER_SERVLET, Severity.WARNING, true);
	report.execute();
%>
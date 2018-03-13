<%@page import="com.orchestranetworks.ps.service.GenerateDataModelDiagram"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	(new GenerateDataModelDiagram()).execute(request, out);
%>
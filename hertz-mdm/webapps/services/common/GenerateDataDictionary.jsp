<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.orchestranetworks.ps.service.GenerateDataDictionary"%>

<%
	new GenerateDataDictionary(request, response).execute();
%>
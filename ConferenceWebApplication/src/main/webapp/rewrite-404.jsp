<%-- 
    Document   : rewrite-404
    Created on : Nov 28, 2019, 7:14:50 PM
    Author     : Ondro Mihalyi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% response.setStatus(HttpServletResponse.SC_OK); %>
<%@ include file="index.html" %>
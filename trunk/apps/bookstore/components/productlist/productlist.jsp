<%--
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
--%><%@page session="false"%><%
%><%@page import="java.util.ArrayList,
                java.util.Iterator,
                java.util.List,
                com.day.crx.sample.bookstore.Util,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceUtil,
                org.apache.sling.api.resource.ValueMap" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");

    final ValueMap attributes = resource.adaptTo(ValueMap.class);
    final String name = attributes.get("jcr:title", ResourceUtil.getName(resource));
    int start = 0;
    if ( request.getParameter("start") != null ) {
        start = Integer.valueOf(request.getParameter("start"));
    }
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><%= name %></title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1><%= name %></h1>
  <div id="products">
  <%
     int index = 0;
     int count = 0;
     int next = -1;
     final Iterator<Resource> fi = ResourceUtil.listChildren(resource);
     while ( fi.hasNext()) {
         final Resource current = fi.next();
         if ( index >= start ) {
	         %>
	         <sling:include resource="<%= current %>" replaceSelectors="detail"/>
	         <%
	         count++;
	         if ( count == 15 ) {
	             if ( fi.hasNext() ) {
	                 next = start + 15;
	             }
	             break;
	         }
         }
         index++;
     }
   %>
  </div>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/ranking"/>
  <div id="footer">
  <%
  if ( start > 0 ) {
      %>&nbsp;<a href="<%= Util.getLink(slingRequest, resource.getPath() + ".html") %>?start=<%= start - 15 %>">Prev Page</a><%
  }
  if ( next > -1 ) {
      if ( start > 0 ) {
          %>|<%
      }
      %>&nbsp;<a href="<%= Util.getLink(slingRequest, resource.getPath() + ".html") %>?start=<%= next %>">Next Page</a><%         
  }
  %>
  </div>
  </div>
</body>
</html>
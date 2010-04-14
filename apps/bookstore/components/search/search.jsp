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
                org.apache.sling.api.resource.ResourceResolver,
                org.apache.sling.api.resource.ResourceUtil,
                org.apache.sling.api.resource.ValueMap" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");

    final String query = request.getParameter("query");
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Search Results</title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1>Search for '<%= query %>'</h1>
  <div id="products">
  <%  
  final ResourceResolver resolver = resource.getResourceResolver();
  final Iterator<Resource> i = resolver.findResources(Util.createQuery(query), "xpath");
  if ( !i.hasNext() ) {
      %>No results found.<%
  } else {
      while ( i.hasNext() ) {
          final Resource current = i.next();
          %>
          <sling:include resource="<%= current %>" replaceSelectors="detail"/>
          <%
      }
  }
  %>
  </div>
  </div>
</body>
</html>
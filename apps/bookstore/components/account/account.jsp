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
%><%@page import="java.util.Iterator, 
    com.day.crx.sample.bookstore.Util,
    com.day.crx.sample.bookstore.Account,
    com.day.crx.sample.bookstore.Order" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
    final Account account = Account.create(slingRequest.getResourceResolver());
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Account</title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1>Account</h1>
  <h2>Orders</h2>
  <div class="newcomment">
  <table><tbody>
    <tr><th>Position</th><th>Order</th><th>Date</th></tr>
    <%
      final Iterator<Order> i = account.getOrders();
      int pos = 1;
      while ( i.hasNext() ) {
          final Order current = i.next();
      %><tr>
          <td><%=pos %></td>
          <td><a href="<%= Util.getLink(slingRequest, current.getPath() + ".html") %>"><%= current.getId() %></a></td>
          <td><%= Util.formatDate(current.getCreated()) %></td>
      </tr><%
          pos++;
      }
      %>
  </tbody></table>
  </div>
  </div>
</body>
</html>
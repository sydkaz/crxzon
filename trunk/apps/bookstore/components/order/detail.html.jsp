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
    java.math.BigDecimal,
    com.day.crx.sample.bookstore.Util,
    com.day.crx.sample.bookstore.Item,
    com.day.crx.sample.bookstore.Order" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    // we first have to check if the current user is the user for the order!
    if ( !Util.isAllowedToAccessOrder(resource) ) {
        response.sendError(404);
        return;
    }
    // get the order
    final Order order = Order.create(resource);
%>
  <h1>Order <%= order.getId() %></h1>
  <p>Order Date: <%= Util.formatDate(order.getCreated()) %></p>
  <p><a href="<%= Util.getLink(slingRequest, resource.getPath() + ".zip") %>">Download</a></p>
<div class="order newcomment">
  <table><tbody>
    <tr><th>Position</th><th>Product</th><th>Price</th><th>Amount</th><th>Total</th></tr>
    <%
      final Iterator<Item> i = order.getItems();
      int pos = 1;
      while ( i.hasNext() ) {
          final Item current = i.next();
          final String id = current.getProduct().getId();
          %><tr>
          <td><%=pos %></td>
          <td><%= current.getProduct().getAuthor() %> : <%= current.getProduct().getTitle() %></td>
          <td>$<%= current.getProduct().getPrice() %></td>
          <td><%= current.getAmount() %></td>
          <td>$<%= current.getTotalPrice() %></td>
          </tr><%
          pos++;
      }
    %>
    <tr>
      <td><b>Total</b></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>$<%= order.getTotal() %></td>
    </tr>
  </tbody></table>
</div>
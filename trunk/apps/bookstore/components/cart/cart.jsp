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
    com.day.crx.sample.bookstore.Cart,
    com.day.crx.sample.bookstore.Item,
    com.day.crx.sample.bookstore.Util" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Cart</title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1>Cart</h1>
  <%
  final Cart cart = Cart.fromRequest(slingRequest);
  if ( cart.isEmpty() ) {
      %><h2>Your cart is empty.</h2><%  
  } else {
      %><form name="cart" class="newcomment">
      <table><tbody>
        <tr><th>Position</th><th>Product</th><th>Price</th><th>Amount</th><th>Total</th><th>&nbsp;</th></tr>
      <%
      final Iterator<Item> i = cart.getItems();
      int pos = 1;
      while ( i.hasNext() ) {
          final Item current = i.next();
          final String id = current.getProduct().getId();
          %><tr>
          <td><%=pos %></td>
          <td><%= current.getProduct().getAuthor() %> : <%= current.getProduct().getTitle() %></td>
          <td>$<%= current.getProduct().getPrice() %></td>
          <td><input type="text" name="amount_<%= id %>" value="<%= current.getAmount() %>" maxlength="2" size="3" onchange="javascript:updateItem('<%= id %>')"/></td>
          <td>$<%= current.getTotalPrice() %></td>
          <td><input type="button" value="x" onclick="javascript:deleteItem('<%= id %>')"/></td>
          </tr><%
          pos++;
      }
      %>
      <tr>
        <td colspan="2"><b>Total</b></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>$<%=  cart.getTotal() %></td>
        <td><input type="button" value="Checkout" onclick="javascript:checkout()"/></td>
      </tr>
      </tbody></table>
      </form>
      <%
  }
  %>
</div>
</body>
</html>
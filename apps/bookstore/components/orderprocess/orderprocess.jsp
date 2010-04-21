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
    com.day.crx.sample.bookstore.Cart,
    com.day.crx.sample.bookstore.Item,
    com.day.crx.sample.bookstore.Order,
    com.day.crx.sample.bookstore.Util" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    // check if the user is logged in - if not redirect!
    if ( Util.isAnonymous(request) ) {
        final String checkoutUrl = Util.getLink(slingRequest, resource.getPath() + ".html?sling:authRequestLogin=BASIC");
        response.sendRedirect(checkoutUrl);
        return;
    }
    // if cart is empty redirect
    final Cart cart = Cart.fromRequest(slingRequest);
    if ( cart.isEmpty() ) {
        response.sendRedirect(Util.getLink(slingRequest, "/products.html") );
        return;
    }
    // write cart to repository
    final Order order = Util.persist(cart, slingRequest.getResourceResolver());

    // and clear the cookie
    Util.clearCookie(response);
    
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");

%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Order <%= order.getId()  %></title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1>Thank you for your order!</h1>
  <sling:include path="<%= order.getPath() %>" replaceSelectors="detail"/>
  </div>
</body>
</html>
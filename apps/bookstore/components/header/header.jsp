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
%><%@page import="com.day.crx.sample.bookstore.Cart,
                  com.day.crx.sample.bookstore.Util"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
%><div class="header"><%
    if ( Util.isAnonymous(request) ) {
%>
Welcome! <a href="<%= Util.getLink(slingRequest, "/products.html?sling:authRequestLogin=BASIC") %>">Login</a>
<%
    } else { %>
Welcome, <%= request.getRemoteUser() %>!
<%
    }
    final Cart cart = Cart.fromRequest(slingRequest);
    final String cartInfo;
    if ( cart.isEmpty() ) {
        cartInfo = "empty";
    } else if ( cart.getItemCount() == 1 ) {
        cartInfo = "1 item";
    } else {
        cartInfo = cart.getItemCount() + " items";
    }
%>
&nbsp;<a href="<%= Util.getLink(slingRequest, "/apps/bookstore/resources/cart.html") %>">Cart</a> (<%= cartInfo %>)
<%
    if ( !Util.isAnonymous(request) ) { %>
&nbsp;<a href="<%= Util.getLink(slingRequest, "/apps/bookstore/resources/account.html") %>">Account</a>
    <% } %>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/navigation"/>
</div>
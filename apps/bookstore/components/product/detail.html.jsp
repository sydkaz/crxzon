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
    org.apache.sling.api.resource.Resource,
    org.apache.sling.api.resource.ResourceUtil,
    com.day.crx.sample.bookstore.Product,
    com.day.crx.sample.bookstore.Util" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
%><div class="product"><%
    
    final Product product = new Product(resource);
    final String productLink = Util.getLink(slingRequest, resource.getPath() + ".html");

    // title
%><h1><a href="<%= productLink %>"><%= product.getTitle() %></a></h1>
  <div class="author"><%= product.getAuthor() %></div><%
  
    // image
    final String imagePath = product.getPreviewPath();
%><div class="image"><a href="<%= productLink %>"><img src="<%= Util.getLink(slingRequest, imagePath) %>" alt="<%= product.getTitle() %>"/></a></div><%

    // price
%><div class="price">
    <div class="amount">$<%= product.getPrice() %></div>
    <div class="addtocart"><a href="#" onclick="javascript:addItem('<%= product.getId() %>', false)"><img src="<%= Util.getLink(slingRequest, "/apps/bookstore/resources/addtocart.gif") %>" alt="Add to cart"/></a></div>
  </div>
</div>
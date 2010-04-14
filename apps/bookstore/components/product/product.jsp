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
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html");

  final Product product = new Product(resource);
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><%= product.getTitle() %></title>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/pagehead"/>
  </head>
  <body>
  <sling:include resource="<%= resource %>" resourceType="bookstore/components/header"/>
  <div id="content">
  <h1>Detail</h1>
  <div class="product"><%
    final String productLink = Util.getLink(slingRequest, resource.getPath() + ".html");

    // title
%><h1><a href="<%= productLink %>"><%= product.getTitle() %></a></h1>
  <div class="author"><%= product.getAuthor() %></div><%
  
    // image
    final String imagePath = product.getPreviewPath();
%><div class="image"><a href="<%= productLink %>"><img src="<%= Util.getLink(slingRequest, imagePath) %>" alt="<%= product.getTitle() %>"/></a></div><%

    // description
    if (product.getDescription() != null) {
        %><div class="description"><%= product.getDescription() %></div><%
    }
    // price
%><div class="price">$<%= product.getPrice() %></div><%
%><div class="released"><%= product.getReleased() %></div><%
%><div class="language"><%= product.getLanguage() %></div><%
    
    // ranking and sold items
    if ( product.getRanking() > 0 ) { 
      %><div class="ranking">Ranking: <%= product.getRanking() %></div>
        <div class="soldItems">Sold: <%= product.getSoldItems() %></div>
      <% 
    } %>
  <div class="basketnav">
    <a href="#" onclick="javascript:addItem('<%= product.getId() %>', true)"><img src="<%= Util.getLink(slingRequest, "/apps/bookstore/resources/addtocart.gif") %>" alt="Add to cart"/></a>
  </div>
  </div>
  <div id="comments"><h1>Comments</h1><%
    final Resource commentsResource = resource.getResourceResolver().getResource(resource.getPath() + "/comments");
    if ( commentsResource != null ) {
        final Iterator<Resource> fi = ResourceUtil.listChildren(commentsResource);
        while ( fi.hasNext()) {
            final Resource current = fi.next();
            %>
            <sling:include resource="<%= current %>" replaceSelectors="detail"/>
            <%
        }
        
    }
    %>
    <sling:include resource="<%= resource %>" resourceType="bookstore/components/comment" replaceSelectors="form"/>
  </div>
</div>
</body>
</html>
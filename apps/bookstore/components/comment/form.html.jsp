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
%><%@page import="org.apache.sling.api.resource.Resource,
    org.apache.sling.api.resource.ResourceUtil,
    org.apache.sling.api.resource.ValueMap,
    com.day.crx.sample.bookstore.Util" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    final ValueMap props = ResourceUtil.getValueMap(resource);
    
    // NOTE: This is not a secure form, posts could be faked, user id's could
    //       be change, resource type overwritten etc.
    //       This is just a sample to demonstrate the easy POST servlet from
    //       Apache Sling.
%><div class="newcomment">
<h1>Add a comment</h1>
<div>
<form method="POST" action="<%= Util.getLink(slingRequest, resource.getPath() + "/comments/*") %>">
<input name="userId" type="hidden" value="<%= request.getRemoteUser() %>"/>
<input name="sling:resourceType" type="hidden" value="bookstore/components/comment"/>
<input name="jcr:created" type="hidden"/>
<input name=":redirect" type="hidden" value="<%= Util.getLink(slingRequest, resource.getPath() + ".html") %>"/>
<ul>
<li><label class="formdescription">Title:</label><div><input name="title" type="text" class="textarea" maxlenght="255"/></div></li>
<li><label class="formdescription">Text:</label>
<div><textarea name="text" class="textarea"></textarea></div></li>
<p><input type="submit" value="Comment"/></p>
</ul>
</form>
</div></div>
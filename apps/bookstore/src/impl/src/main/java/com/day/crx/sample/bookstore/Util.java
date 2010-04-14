/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.day.crx.sample.bookstore;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.ItemBasedPrincipal;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/** Some helper functions. */
public abstract class Util {

    /** Encode the string in UTF-8 */
    public static String encode(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // this should never happen as UTF-8 is always supported
            return value;
        }
    }

    /** Decode the string using UTF-8 */
    public static String decode(final String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // this should never happen as UTF-8 is always supported
            return value;
        }
    }

    /** Check if the current user is not logged in yet. */
    public static boolean isAnonymous(HttpServletRequest request) {
        if ( request.getRemoteUser() == null || "anonymous".equals(request.getRemoteUser())) {
            return true;
        }
        return false;
    }

    /** Persist the shopping cart and create an order. */
    public static Order persist(final Cart cart, final ResourceResolver resolver) {
        final Order order = Order.createNewOrder(cart, resolver.adaptTo(Session.class));
        order.persist(resolver);
        return order;
    }

    /** Clear the cookie for the cart. */
    public static void clearCookie(final HttpServletResponse response) {
        final Cookie c = new Cookie(Cart.COOKIE_NAME, "");
        c.setPath("/");
        c.setMaxAge(-1);
        response.addCookie(c);
    }

    /** Format the date. */
    public static String formatDate(final Date date) {
        if ( date == null ) {
            return "";
        }
        final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        return df.format(date);
    }

    /** Create a link.
     * Prepend the context path and map the link through the resource resolver.
     */
    public static String getLink(final SlingHttpServletRequest request, final String path) {
        return request.getContextPath() + request.getResourceResolver().map(path);
    }

    /**
     * Create the query string for the search
     * If the search string is empty we return all products!
     */
    public static String createQuery(final String text) {
        if ( text == null || text.length() == 0 ) {
            return "/jcr:root/products//*[@price]";
        }
        final StringBuilder buffer = new StringBuilder("/jcr:root/products//*");
        buffer.append("[@price and (jcr:contains(@jcr:title, '");
        buffer.append(text);
        buffer.append("') or jcr:contains(@jcr:description, '");
        buffer.append(text);
        buffer.append("') or jcr:contains(@author, '");
        buffer.append(text);
        buffer.append("') or jcr:contains(@contents, '");
        buffer.append(text);
        buffer.append("'))]");

        return buffer.toString();

    }

    /** Escape xml text */
    public static String escapeXml(String input) {
        if(input == null) {
            return null;
        }

        final StringBuilder b = new StringBuilder(input.length());
        for(int i = 0;i  < input.length(); i++) {
            final char c = input.charAt(i);
            if(c == '&') {
                b.append("&amp;");
            } else if(c == '<') {
                b.append("&lt;");
            } else if(c == '>') {
                b.append("&gt;");
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    /**
     * Helper method to get the home directory of the current user.
     */
    public static String getUserHome(final Session session) {
        String homePath = null;
        if ( session instanceof JackrabbitSession) {
            final JackrabbitSession jSession = (JackrabbitSession)session;
            try {
                final UserManager um = jSession.getUserManager();
                final Authorizable authorizable = um.getAuthorizable(session.getUserID());
                if ( authorizable != null ) {
                    final Principal p = authorizable.getPrincipal();
                    if ( p instanceof ItemBasedPrincipal ) {
                        homePath = ((ItemBasedPrincipal)p).getPath();
                    }
                }
            } catch (RepositoryException re) {
                // we ignore this here
            }
        }
        // fallback
        if ( homePath == null ) {
            homePath = "/userHomes/" + session.getUserID();
        }
        return homePath;
    }

    /** Is the current user allowed to see this order? */
    public static boolean isAllowedToAccessOrder(final Resource order) {
        final Session session = order.getResourceResolver().adaptTo(Session.class);
        final String homePath = getUserHome(session);
        return order.getPath().startsWith(homePath + "/orders/");
    }
}

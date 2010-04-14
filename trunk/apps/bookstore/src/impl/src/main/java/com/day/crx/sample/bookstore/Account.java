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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

/**
 * A container object for the user account.
 * Currently the account is only holding the orders of a user.
 * We could add other information like a user profile etc.
 */
public class Account {

    /** Instantiate the account for the given user
     */
    public static Account create(final ResourceResolver resolver) {
        final Account c = new Account();

        // in order to get the user home we have to access the jcr session
        final Session session = resolver.adaptTo(Session.class);

        final String ordersPath = Util.getUserHome(session) + "/orders";
        final Resource homeResource = resolver.getResource(ordersPath);
        if ( homeResource != null ) {
            final Iterator<Resource> i = ResourceUtil.listChildren(homeResource);
            while ( i.hasNext() ) {
                final Resource orderResource = i.next();

                final Order order = Order.create(orderResource);
                c.add(order);
            }
        }
        return c;
    }

    /** The list of orders */
    private List<Order> orders = new ArrayList<Order>();

    /**
     * Return an iterator for all orders.
     */
    public Iterator<Order> getOrders() {
        return orders.iterator();
    }

    /**
     * Add another order.
     */
    private void add(final Order order) {
        this.orders.add(order);
    }
}

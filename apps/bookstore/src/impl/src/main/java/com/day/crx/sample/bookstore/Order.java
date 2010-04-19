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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.JcrResourceUtil;

/**
 * This is an order.
 */
public class Order {

    public static String RESOURCE_TYPE = "bookstore/components/order";

    /** The items. */
    private final List<Item> items = new ArrayList<Item>();

    /** The id. */
    private final String id;

    /** Created. */
    private Date created;

    /** The price. */
    private BigDecimal total;

    /** The resource path. */
    private String path;

    /**
     * Create a new order from a shopping cart for the given user.
     * @param cart The shopping cart.
     * @return A new order.
     */
    public static Order createNewOrder(final Cart cart, final Session session) {
        final Order order = new Order(UUID.randomUUID().toString());
        order.created = new Date();
        final Iterator<Item> i = cart.getItems();
        while ( i.hasNext() ) {
            order.addItem(i.next());
        }
        order.total = cart.getTotal();
        final String userHome = Util.getUserHome(session);
        order.path = userHome + "/orders/" + order.getId();

        return order;
    }

    /**
     * Create an order object from a resource.
     * @param resource The resource holding the order.
     * @return The order object.
     */
    public static Order create(final Resource resource) {
        final Order order = new Order(ResourceUtil.getName(resource));
        order.created = ResourceUtil.getValueMap(resource).get("created", Date.class);
        order.total = new BigDecimal(ResourceUtil.getValueMap(resource).get("price", "0.0"));
        final Iterator<Resource> i = ResourceUtil.listChildren(resource);
        while ( i.hasNext() ) {
            final Resource itemResource = i.next();
            final ValueMap attributes = ResourceUtil.getValueMap(itemResource);
            final String productId = attributes.get("productId", String.class);
            final int amount = attributes.get("amount", 0);
            final BigDecimal price = new BigDecimal(attributes.get("price", "0.0"));

            final Resource productResource = resource.getResourceResolver().getResource(Util.decode(productId));
            final Item newItem = new Item(new Product(productResource, price), amount);
            order.addItem(newItem);
        }
        order.path = resource.getPath();
        return order;
    }

    /** Create a new order. */
    private Order(final String id) {
        this.id = id;
    }

    /** Return the id. */
    public String getId() {
        return this.id;
    }

    /** Return the created date. */
    public Date getCreated() {
        return created;
    }

    /** Return the total price for the order. */
    public BigDecimal getTotal() {
        return this.total;
    }

    /** Return the resource path. */
    public String getPath() {
        return this.path;
    }

    /** Return all items. */
    public Iterator<Item> getItems() {
        return this.items.iterator();
    }

    /** Add an item. */
    private void addItem(final Item item) {
        this.items.add(item);
    }

    /** Persist the order. */
    public void persist(final ResourceResolver resolver) {
        final Session session = resolver.adaptTo(Session.class);
        try {
            final String userHome = Util.getUserHome(session);
            final String ordersPath = userHome + "/orders";

            // create the complete path
            final Node ordersNode = JcrResourceUtil.createPath(ordersPath,
                     "nt:unstructured", "nt:unstructured", session, true);
            final Node orderNode = ordersNode.addNode(this.getId(), "nt:unstructured");
            orderNode.setProperty("sling:resourceType", RESOURCE_TYPE);
            final Calendar c = Calendar.getInstance();
            c.setTime(created);
            orderNode.setProperty("created", c);
            orderNode.setProperty("price", this.total.toString());
            final Iterator<Item> i = this.getItems();
            int pos = 1;
            while ( i.hasNext() ) {
                final Item current = i.next();
                final Node positionNode = orderNode.addNode("Position " + pos);
                positionNode.setProperty("productId", current.getProduct().getId());
                positionNode.setProperty("amount", current.getAmount());
                positionNode.setProperty("price", current.getProduct().getPrice().toString());
                pos++;
            }
            session.save();
        } catch (RepositoryException re) {
            // we ignore this for now (TODO)
        }
    }

    /** Stream the contents of the order to the output stream. */
    public void stream(final OutputStream os)
    throws IOException {
        final ZipOutputStream zos = new ZipOutputStream(os);
        final Iterator<Item> i = this.getItems();
        int pos = 1;
        while ( i.hasNext() ) {
            final Item current = i.next();
            final ZipEntry entry = new ZipEntry("Item" + pos + ".txt");
            zos.putNextEntry(entry);
            final String contents = current.getProduct().getContents();
            final byte[] bytes = contents.getBytes("UTF-8");
            zos.write(bytes);
            zos.closeEntry();
            pos++;
        }
        zos.finish();
        zos.flush();
    }
}

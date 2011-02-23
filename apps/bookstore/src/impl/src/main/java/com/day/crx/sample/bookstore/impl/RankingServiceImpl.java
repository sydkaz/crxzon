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
package com.day.crx.sample.bookstore.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.crx.sample.bookstore.Item;
import com.day.crx.sample.bookstore.Order;
import com.day.crx.sample.bookstore.Product;
import com.day.crx.sample.bookstore.RankingService;

/**
 * Default implementation of the ranking service.
 * The ranking is updated through observation (based on OSGi events).
 * The service can be used by clients to get the highest ranked products.
 *
 * @scr.component immediate=true metatype=false
 * @scr.service interface="RankingService"
 * @scr.service interface="EventHandler"
 * @scr.property nameRef="org.osgi.service.event.EventConstants.EVENT_TOPIC" valueRef="SlingConstants.TOPIC_RESOURCE_ADDED"
 */
public class RankingServiceImpl
    implements RankingService, EventHandler, Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PROPERTY_PREV_RANKING = "lowerRankingRef";
    private static final String PROPERTY_NEXT_RANKING = "higherRankingRef";

    private static final int SHOW_HIGHEST_RANKING = 3;

    /** Flag for stopping the background service. */
    private volatile boolean running = false;

    /** A local queue for handling new orders. */
    protected final BlockingQueue<String> orders = new LinkedBlockingQueue<String>();

    /** @scr.reference */
    private SlingRepository repository;

    /** @scr.reference */
    private ResourceResolverFactory resourceResolverFactory;

    /** Cache for the highest ranking paths. */
    private volatile String[] highestRankingPaths;

    /** Activate this component. */
    protected void activate(final ComponentContext context) {
        logger.info("Activating ranking service.");
        // start background thread
        this.running = true;
        final Thread t = new Thread(this);
        t.start();
    }

    /** Deactivate this component. */
    protected void deactivate(final ComponentContext context) {
        logger.info("Deactivating ranking service.");
        this.running = false;
        // wakeup thread
        try {
            orders.put("");
        } catch (InterruptedException e) {
            // ignore this
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        logger.debug("Starting background thread.");
        while ( this.running ) {
            String path = null;
            try {
                path = this.orders.take();
            } catch (InterruptedException e) {
                // ignore this
            }
            if ( this.running && path != null ) {
                // as we are running as a background process which takes one
                // path at a time, we never run in parallel!

                // get a session and a resource resolver
                logger.debug("Processing order at path {}", path);
                Session adminSession = null;
                try {
                    adminSession = this.repository.loginAdministrative(null);

                    final ResourceResolver resolver = this.resourceResolverFactory.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) adminSession));

                    // get order resource
                    final Resource orderResource = resolver.getResource(path);
                    if ( orderResource != null ) {
                        final Order order = Order.create(orderResource);

                        this.update(order, adminSession);
                    }
                } catch (LoginException le) {
                    // TODO - we ignore this for now
                    logger.error("Unable to login", le);
                } catch (RepositoryException re) {
                    // TODO - we ignore this for now
                    logger.error("Unable to update ranking!", re);
                } finally {
                    if ( adminSession != null ) {
                        adminSession.logout();
                    }
                }
            }
        }
        logger.debug("Stopping background thread.");
    }

    /**
     * Update the ranking for the given order.
     *
     * Updating the ranking is basically done used a double-linked list.
     * Each product in the repository contains a property with the path
     * to the product with the next higher ranking and a property to
     * the product with the next lower ranking. Through these properties
     * it is very easy to update the ranking of a product without
     * expensive searchs.
     */
    private void update(final Order order, final Session session) throws RepositoryException {
        final Iterator<Item> i = order.getItems();
        while ( i.hasNext() ) {
            final Item current = i.next();

            // get the node
            final String path = current.getProduct().getResourcePath();
            final Node productNode = (Node)session.getItem(path);

            // update sold items
            final int soldItems = current.getProduct().getSoldItems() + current.getAmount();
            productNode.setProperty(Product.PROPERTY_SOLD_ITEMS, soldItems);
            // get current ranking
            final int ranking = current.getProduct().getRanking();
            Node previousNode = null;
            Node nextNode = null;
            if ( ranking == 0 ) {
                // get node with lowest ranking
                final Node lowestRankingNode = getLowestRankingNode(session);
                if ( lowestRankingNode.hasProperty(Product.PROPERTY_RANKING) ) {
                    nextNode = lowestRankingNode;
                    nextNode.setProperty(PROPERTY_PREV_RANKING, productNode.getPath());
                    productNode.setProperty(PROPERTY_NEXT_RANKING, nextNode.getPath());
                    productNode.setProperty(Product.PROPERTY_RANKING, nextNode.getProperty(Product.PROPERTY_RANKING).getLong() + 1);
                } else {
                    productNode.setProperty(Product.PROPERTY_RANKING, 1);
                }
            } else {
                if ( productNode.hasProperty(PROPERTY_PREV_RANKING) ) {
                    previousNode = (Node) session.getItem(productNode.getProperty(PROPERTY_PREV_RANKING).getString());
                }
                if ( productNode.hasProperty(PROPERTY_NEXT_RANKING) ) {
                    nextNode = (Node) session.getItem(productNode.getProperty(PROPERTY_NEXT_RANKING).getString());
                }
            }
            while ( nextNode != null ) {
                int nextSoldItems = (int) nextNode.getProperty(Product.PROPERTY_SOLD_ITEMS).getLong();
                if ( nextSoldItems < soldItems ) {
                    nextNode.setProperty(Product.PROPERTY_RANKING, nextNode.getProperty(Product.PROPERTY_RANKING).getLong() + 1);
                    if ( previousNode != null ) {
                        previousNode.setProperty(PROPERTY_NEXT_RANKING, nextNode.getPath());
                        nextNode.setProperty(PROPERTY_PREV_RANKING, previousNode.getPath());
                    } else {
                        nextNode.setProperty(PROPERTY_PREV_RANKING, (String) null);
                    }
                    previousNode = nextNode;
                    if ( previousNode.hasProperty(PROPERTY_NEXT_RANKING) ) {
                        nextNode = (Node) session.getItem(previousNode.getProperty(PROPERTY_NEXT_RANKING).getString());
                    } else {
                        nextNode = null;
                    }
                    previousNode.setProperty(PROPERTY_NEXT_RANKING, productNode.getPath());
                    productNode.setProperty(PROPERTY_PREV_RANKING, previousNode.getPath());
                    if ( nextNode != null ) {
                        productNode.setProperty(PROPERTY_NEXT_RANKING, nextNode.getPath());
                        nextNode.setProperty(PROPERTY_PREV_RANKING, productNode.getPath());
                    } else {
                        productNode.setProperty(PROPERTY_NEXT_RANKING, (String) null);
                    }
                    productNode.setProperty(Product.PROPERTY_RANKING, productNode.getProperty(Product.PROPERTY_RANKING).getLong() - 1);
                } else {
                    // finished
                    nextNode = null;
                }
            }

            // and now save.
            session.save();

            // check if we have to update the cache
            if ( current.getProduct().getRanking() <= SHOW_HIGHEST_RANKING ) {
                synchronized ( this ) {
                    this.highestRankingPaths = null;
                }
            }
        }
    }

    /** Return the lowest ranked product. */
    private Node getLowestRankingNode(Session session)
    throws RepositoryException {
        final StringBuilder buffer = new StringBuilder("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/products]) AND ");
        buffer.append("s.");
        buffer.append(Product.PROPERTY_PRICE);
        buffer.append(" IS NOT NULL ORDER BY s.");
        buffer.append(Product.PROPERTY_RANKING);
        buffer.append(" DESC");

        final QueryManager manager = session.getWorkspace().getQueryManager();
        final Query q = manager.createQuery(buffer.toString(), Query.JCR_SQL2);
        final QueryResult result = q.execute();
        final NodeIterator i = result.getNodes();
        return (i.hasNext() ? i.nextNode() : null);
    }

    /**
     * @see com.day.crx.sample.bookstore.RankingService#getHighestRankedProductPaths()
     */
    private String[] getHighestRankedProductPaths(int amount) {
        final List<String> paths = new ArrayList<String>();
        final StringBuilder buffer = new StringBuilder("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/products]) AND ");
        buffer.append("s.");
        buffer.append(Product.PROPERTY_RANKING);
        buffer.append(" IS NOT NULL ORDER BY s.");
        buffer.append(Product.PROPERTY_RANKING);
        buffer.append(" ASC");

        // get an admin session
        Session session = null;
        try {
            session = this.repository.loginAdministrative(null);
            final QueryManager manager = session.getWorkspace().getQueryManager();
            final Query q = manager.createQuery(buffer.toString(), Query.JCR_SQL2);
            final QueryResult result = q.execute();
            final NodeIterator i = result.getNodes();
            while ( i.hasNext() && paths.size() < amount ) {
                final Node current = i.nextNode();
                paths.add(current.getPath());
            }
        } catch (RepositoryException re) {
            // TODO - we ignore this for now
            logger.error("Unable to get ranking!", re);
        } finally {
            if ( session != null ) {
                session.logout();
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

    /**
     * @see com.day.crx.sample.bookstore.RankingService#getHighestRankedProductPaths()
     */
    public String[] getHighestRankedProductPaths() {
        // we cache the result in order to avoid a the same search over and
        // over again
        synchronized ( this ) {
            if ( highestRankingPaths == null ) {
                highestRankingPaths = getHighestRankedProductPaths(SHOW_HIGHEST_RANKING);
            }
            return highestRankingPaths;
        }
    }

    /**
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    public void handleEvent(Event event) {
        // we are only interested in order add events
        final String resourceType = (String) event.getProperty(SlingConstants.PROPERTY_RESOURCE_TYPE);
        logger.debug("Getting event with resource type {}", resourceType);
        if ( Order.RESOURCE_TYPE.equals(resourceType) ) {
            // if this is an order we add it to the queue
            // event handlers should never do the real action, they should return
            // as quickly as possible!
            final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
            logger.debug("Putting order path {}", path);
            try {
                this.orders.put(path);
            } catch (InterruptedException e) {
                // we ignore this
            }
        }
    }
}

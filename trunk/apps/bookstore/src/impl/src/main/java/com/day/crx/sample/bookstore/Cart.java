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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;


/**
 * The cart is the classic shopping cart. During shopping it is stored
 * in a cookie ({@link #COOKIE_NAME}).
 * A cart consists of {@link Item}s.
 */
public class Cart {

    /** The name of the cookie holding the shopping cart contents. */
    public static final String COOKIE_NAME = "cart";

    /** Instantiate the cart from the current request.
     * This method checks for the cart cookie and creates items based on the value of
     * the cookie.
     * @param request The current request
     * @return A cart object.
     */
    public static Cart fromRequest(final SlingHttpServletRequest request) {
        final Cart c = new Cart();

        // check for cookie
        Cookie cookie = null;
        if ( request.getCookies() != null ) {
            for(final Cookie current : request.getCookies()) {
                if ( COOKIE_NAME.equals(current.getName()) ) {
                    cookie = current;
                    break;
                }
            }
        }
        if ( cookie != null ) {
            try {
                final JSONObject jo = new JSONObject(cookie.getValue());
                final JSONObject array = (JSONObject) jo.get("products");
                if ( array != null ) {
                    final Iterator<String> i = array.keys();
                    while ( i.hasNext() ) {
                        final String id = i.next();
                        // create product
                        final Resource resource = request.getResourceResolver().getResource(Util.decode(id));
                        if ( resource != null ) {
                            final int amount = array.getInt(id);
                            final Item newItem = new Item(new Product(resource, null), amount);
                            c.add(newItem);
                        }
                    }
                }
            } catch (JSONException je) {
                // if an exception occurs, we consider the cart to be empty
            }
        }
        return c;
    }

    /** The list of items */
    private List<Item> items = new ArrayList<Item>();

    /**
     * Return an iterator for all items.
     */
    public Iterator<Item> getItems() {
        return items.iterator();
    }

    /** Return the number of items in the cart. */
    public int getItemCount() {
        return this.items.size();
    }

    /**
     * Is the cart empty?
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Add another item to the cart.
     */
    private void add(final Item item) {
        this.items.add(item);
    }

    /**
     * Get the total price of all items in the cart.
     */
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0.0);
        for(final Item item : this.items) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }
}

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

/**
 * An item defines a selected product in the shopping cart.
 * The item has a reference to the selected product and an amount.
 */
public class Item {

    /** The product. */
    private final Product product;

    /** The amount for this product. */
    private final int amount;

    /** Create a new item. */
    public Item(final Product product, final int amount) {
        this.product = product;
        this.amount = amount;
    }

    /** Return the amount. */
    public int getAmount() {
        return amount;
    }

    /** Return the product. */
    public Product getProduct() {
        return this.product;
    }

    /** Return the total price which is product.getPrice() * amount */
    public BigDecimal getTotalPrice() {
        return this.product.getPrice().multiply(new BigDecimal(this.amount));
    }
}

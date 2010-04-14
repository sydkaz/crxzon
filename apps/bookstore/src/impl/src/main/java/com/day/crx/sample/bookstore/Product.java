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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

/**
 * The product currently describes a book with an author
 * and a title.
 */
public class Product {

    public static final String PROPERTY_PRICE = "price";
    public static final String PROPERTY_TITLE = "jcr:title";
    public static final String PROPERTY_AUTHOR = "author";
    public static final String PROPERTY_DESCRIPTION = "jcr:description";
    public static final String PROPERTY_RANKING = "ranking";
    public static final String PROPERTY_SOLD_ITEMS = "soldItems";
    public static final String PROPERTY_LANGUAGE = "language";
    public static final String PROPERTY_RELEASED = "released";
    public static final String PROPERTY_CONTENTS = "contents";

    /** Various properties of the product. */
    private final ValueMap properties;

    /** The corresponding product resource. */
    private final Resource resource;

    /** Price if ordered. */
    private final BigDecimal price;

    /** Construct a new product. */
    public Product(final Resource resource) {
        this(resource, null);
    }

    /** Construct a new product. */
    public Product(final Resource resource, final BigDecimal price) {
        this.properties = ResourceUtil.getValueMap(resource);
        this.resource = resource;
        this.price = price;
    }

    /** Get the price for the product. */
    public BigDecimal getPrice() {
        if ( this.price != null ) {
            return this.price;
        }
        return new BigDecimal(this.properties.get(PROPERTY_PRICE, "0.0"));
    }

    /** Return the product id. */
    public String getId() {
        return Util.encode(resource.getPath());
    }

    /** Return the resource path for the product. */
    public String getResourcePath() {
        return resource.getPath();
    }

    /** Return the author. */
    public String getAuthor() {
        return this.properties.get(PROPERTY_AUTHOR, "<unknown>");
    }

    /** Return the title. */
    public String getTitle() {
        return this.properties.get(PROPERTY_TITLE, getId());
    }

    /** Return the language. */
    public String getLanguage() {
        return this.properties.get(PROPERTY_LANGUAGE, String.class);
    }

    /** Return the released info. */
    public String getReleased() {
        return this.properties.get(PROPERTY_RELEASED, String.class);
    }

    /** Return the description. */
    public String getDescription() {
        return this.properties.get(PROPERTY_DESCRIPTION, null);
    }

    /** Return the preview path. */
    public String getPreviewPath() {
        return this.resource.getPath() + "/preview.jpg";
    }

    /** Get contents. */
    public String getContents() {
        return this.properties.get(PROPERTY_CONTENTS, "");
    }

    /** Get the ranking. */
    public int getRanking() {
        return this.properties.get(PROPERTY_RANKING, 0);
    }

    /** Get number of sold items. */
    public int getSoldItems() {
        return this.properties.get(PROPERTY_SOLD_ITEMS, 0);
    }
}

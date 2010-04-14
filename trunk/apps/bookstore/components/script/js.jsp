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
--%><%@page session="false"%>
// set the cookie value
function setCookie(name, value) {
    document.cookie = name + '="' + value + '"; path=/';
 }

// get the value of a cookie
function getCookie(name) {
    var i = document.cookie.indexOf(name + '=');
    if (i == -1) return '';
         
    var value =  document.cookie.substr(i + name.length + 1, document.cookie.length);
    var end = value.indexOf(';');
    if (end != -1)
        value = value.substr(0, end);

    var space = value.indexOf('+');
    while (space != -1) { 
        value = value.substr(0, space) + ' ' + 
              value.substr(space + 1, value.length);                     
        space = value.indexOf('+');
    }
    if ( value.charAt(0) == '"' ) {
    	value = value.substring(1, value.length - 1);
    }
    return value;
}

// remove the cookie
function removeCookie(name) {                  
    var expires = new Date();
    expires.setYear(expires.getYear() - 1);

    document.cookie = name + '=null' + '; expires=' + expires + '; path=/';          
}

// return the cart object
function getCart() {
    var cart=new Object();
    cart.products=new Object();    
    var a=getCookie("cart");
    if (!a) return cart;
    try {
        cart=eval("(" + a + ")");
    } catch (e) {
    }
    return cart;
}

// empty the cart
function emptyCart() {
    clearCookie("cart");
}

// serialize the cart into json
function serializeCart(cart) {
    var first=true; 
    var serializedcart="{products:{";
    for (var a in cart.products) {
        if (!first) serializedcart+=",";
        serializedcart+="'"+a+"':"+cart.products[a];
        first=false;
    }
    serializedcart+="}}";
    return serializedcart;
}

// store the cart in the cookie
function putCart(cart) {
    var serializedcart=serializeCart(cart);
    setCookie("cart", serializedcart);
}

// add an item to the cart
function addToCart(item, amount) {
    var cart=getCart();
    if (cart.products[item]) cart.products[item] = parseInt(cart.products[item]) + amount;
    else cart.products[item]=amount;
    putCart(cart);
}

// remove an item to the cart
function removeFromCart(item) {
    var cart=getCart();
    if (cart.products[item]) delete(cart.products[item]);
    putCart(cart);
}

// change the amount of an item in the cart
function updateItemInCart(item, amount) {
    var cart=getCart();
    cart.products[item]=amount;
    putCart(cart);	
}

// Delete an item through the ui
function deleteItem(item) {
	removeFromCart(item);
    document.location = document.location.pathname;
}

// update an item through the ui
function updateItem(item) {
	var amount = parseInt(document.forms["cart"].elements["amount_" + item].value);
	if ( isNaN(amount) || amount < 1 ) {
		amount = 1;
		document.forms["cart"].elements["amount_" + item].value = amount;
	}
	updateItemInCart(item, amount);
	document.location = document.location.pathname;
}

// add an item to the cart and go to main page
function addItem(item, parentRedirect) {
    addToCart(item, 1);
    var path = window.location.pathname;
    if ( parentRedirect ) {
        var pos = path.lastIndexOf('/');
        document.location = path.substring(0, pos) + ".html";
    } else {
        document.location = path + window.location.search;
    }
}

// go to checkout
function checkout() {
    document.location = "checkout.html";
}

function order() {
    document.location = "order.html";
}
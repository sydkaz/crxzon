This is a sample project for CRX: an online bookstore.

[Day CRX](http://www.day.com/crx) is a commercially packaged version of the [Apache](http://www.apache.org/) [Jackrabbit](http://jackrabbit.apache.org) and [Sling](http://sling.apache.org) open source projects. Have a look at the web site for more information.

This sample project is intended to be used in conjunction with Day CRX IDEs:
  * [CRXDE](http://dev.day.com/docs/en/crx/current/developing/development_tools/developing_with_crxde.htm), a standalone IDE based on Eclipse platform. You can download it for free [here](http://www.day.com/downloadcrxde).
  * [CRXDE Lite](http://dev.day.com/content/docs/en/crx/current/developing/development_tools/developing_with_crxde_lite.html), a web-based IDE, already included in your copy of CRX. Just follow the **Develop** link on the welcome screen.
You can use either of the two CRX IDEs to start looking at the application, they have the same basic functionalities. You might even want to try both out to see which one you like better...

## Getting Started ##

Getting started is very easy - you can follow the "First steps with CRX" link on the CRX welcome screen, or access it [online](http://dev.day.com/docs/en/crx/current/getting_started/first_steps_with_crx.html). The First Steps page features a [screencast](http://www.day.com/day/en/products/crx.html), and a set of instructions on how to start working with this Bookstore sample application.

In essence, the steps to get the project checked out (or exported) to your local CRX instance are the following:
  * Use Checkout or Export action in the version control menu of your CRX IDE
  * Checkout (or export) the following two parts of the project from your CRX IDE. Be careful with the Path field, enter exactly as outlined below. Use anonymous/anonymous as credentials in the version control dialog.
    * apps - the application code; checkout
      * URL: `http://crxzon.googlecode.com/svn/trunk/apps/bookstore`
      * to Path: `/apps/bookstore`
    * products - the available books; checkout
      * URL: `http://crxzon.googlecode.com/svn/trunk/products`
      * to Path: `/products`

After the checkout you'll have all bookstore related code at `/apps/bookstore` and the contents for the products at `/products` in your CRX repository. Build the project using CRX IDE Build Bundle command on the bundle descriptor:
  * `/apps/bookstore/src/impl/com.day.crx.sample.bookshop.bnd`
and then invoke http://localhost:7402/products.html to get the start page.

The sample project comes with a set of books to buy. The books and their content are taken from [Gutenberg](http://www.gutenberg.org|Project). Please have a look at their website for more information about the project and their usage terms. The cover images are taken from [Wikimedia Commons](http://commons.wikimedia.org/wiki/Main_Page). Please check their website for more information about the project and the usage terms.

Enjoy!
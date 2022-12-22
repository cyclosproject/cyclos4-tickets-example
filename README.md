Cyclos example for a ticket payment plugin
==========================================

This project is an example for integrating a [Cyclos](https://www.cyclos.org) payment tickets plugin to some external webshop platform.

This is a [Spring Boot](https://spring.io/projects/spring-boot) application, which makes it very easy for running. It doesn't connect to a database, but stores files directly in disk.

The project is a simple web application which has 2 pages:

* Cyclos connection: Configures the connection to Cyclos via OpenID Connect. Also allows starting over. The connection is comprised of first registering a dynamic OpenID Connect client (a feature supported in Cyclos 4.16+) and then authorizing the merchant through that client;
* Sales: Shows a list of simulated sales. Allows creating sales, as well as paying them using Cyclos tickets.

# Requirements

* Cyclos 4.16+
* Java 17

# Configuring Cyclos

First if you don't yet have your Cyclos installation, download and install Cyclos 4.16 from https://license.cyclos.org. It is free up to 300 users.

In Cyclos you will need to configure payment tickets. Generally only business should be able to receive ticket payments, whereas all users should be able to pay them.

For this, create a product in 'System > User configuration > Products / (permissions)' for merchants that grants 'Receive payments (tickets)' with some payment type. Then, in another product for all users, grant 'Make payments (tickets)' with the same payment type.

Finally, you need to enable the dynamic registration of OpenID Connect / OAuth 2 clients. This is done in 'System > System configuration > Configurations' in the default configuration. Find the 'Dynamic OpenID Connect / OAuth 2 clients' section, define it by clicking in the pencil icon, then enable it. Other fields will show up.

The following 'Permissions (scopes)' are required:

* OpenID Connect
* Basic profile information
* Offline access (refresh tokens)
* Create and manage tickets

You will probably need to check the 'Allow non HTTPS connections' for testing this application, otherwise it would need to be deployed with HTTPS. However, in production systems should always force HTTPS for dynamic clients.

Then, in the 'Allowed ticket types', select the same payment type as granted in the products.

**Important**: It is important to have a single possible payment type because the example application will NOT pass the payment type as parameter when creating a ticket. If there would be multiple options, it would fail. In real world, multiple payment types means multiple currencies, which is not covered in this example.

Finally, set the access token expiration to something like 4 to 24 hours. The less the time, the more often the plugin will need to request additional authorization tokens to Cyclos.

# Getting the application

Visit the application's GitHub page at https://github.com/cyclosproject/cyclos4-tickets-example. You can either clone it with git or download the zip file of the master branch.

Take note on the local folder where you've extracted the application.

# Configuring the application

There are some properties that must be set in Spring Boot's `application.properties`. The file is located at `src/main/resources`, but individual properties can be overridden by environment variables:

* `cyclos.serverUri`: The root URI where the Cyclos server is deployed;
* `cyclos.appUri`: The root URI where this sample application is deployed;
* `cyclos.dataDir`: The name of a local directory which will be used to store data. You can use %t to be replaced by the system temporary directory, such as `%t/cyclos-tickets`.

# Running the application

In a terminal, go to the directory where the project is located and type in:

```bash
./gradlew bootRun
```

You can stop the server by typing in `Ctrl + C`.
# The MarkLogic Java Client API

The API makes it easy to write, read, delete, and find documents
in a [MarkLogic](http://developer.marklogic.com/) database.

The Java API supports the following core features of the MarkLogic database:

*  Write and read binary, JSON, text, and XML documents.
*  Query data structure trees, marked-up text, and all the hybrids in between those extremes.
*  Project values and tuples from hierarchical documents and aggregate over them.
*  Patch documents with partial updates.
*  Match documents against alerting rules expressed as queries. 
*  Use Optimistic Locking to detect contention without creating locks on the server.
*  Execute ACID modifications so the change either succeeds or throws an exception.
*  Execute multi-statement transactions so changes to multiple documents succeed or fail together.

### QuickStart

For people working with MarkLogic 8 EA

    $ mvn test-compile
    $ sh src/test/resources/boot-test.sh
    $ mvn test

### Learning More

The following resources introduce and document the Java API:

* [The Java API in Five Minutes](http://developer.marklogic.com/try/java/index)
* [Java Application Developer's Guide](http://docs.marklogic.com/guide/java)
* [JavaDoc](http://docs.marklogic.com/javadoc/client/index.html)

### Installing

To use the Java API, either add a Maven dependency or download the jar and its dependencies:

http://developer.marklogic.com/products/java

Of course, you'll also need to install the database -- which you can do for free with
the developer license:

https://developer.marklogic.com/free-developer

### Building

You can build the API in the same way as any Maven project on git:

1. Clone the java-client-api repository on your machine.
2. Execute a Maven build in the directory containing the pom.xml file.  

You might want to skip the tests until you have configured a test database and REST server:

    $ mvn package -Dmaven.test.skip=true

## Support
The MarkLogic Java Client API is maintained by MarkLogic Engineering and distributed under the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. Everyone is encouraged to file bug reports, feature requests, and pull requests through GitHub. This input is critical and will be carefully considered, but we can’t promise a specific resolution or timeframe for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client API to licensed customers under the terms outlined in the [Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). For more information or to sign up for support, visit [help.marklogic.com](http://help.marklogic.com).

![GitHub release](https://img.shields.io/github/release/marklogic/java-client-api.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/marklogic/java-client-api.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Known Vulnerabilities](https://snyk.io/test/github/marklogic/java-client-api/badge.svg)](https://snyk.io/test/github/marklogic/java-client-api)

# The MarkLogic Java Client

The MarkLogic Java Client makes it easy to write, read, delete, and find documents
in a [MarkLogic](http://developer.marklogic.com/) database. The client requires connecting to a 
[MarkLogic REST API app server](https://docs.marklogic.com/guide/rest-dev) and is ideal for applications wishing to 
build upon the MarkLogic REST API. 

The client supports the following core features of the MarkLogic database:

*  Write and read binary, JSON, text, and XML documents.
*  Query data structure trees, marked-up text, and all the hybrids in between those extremes.
*  Project values, tuples, and triples from hierarchical documents and aggregate over them.
*  Patch documents with partial updates.
*  Use Optimistic Locking to detect contention without creating locks on the server.
*  Execute ACID modifications so the change either succeeds or throws an exception.
*  Execute multi-statement transactions so changes to multiple documents succeed or fail together.
*  Call Data Services by means of a Java interface on the client for data functionality 
implemented by an endpoint on the server.

The client can be used in applications running on Java 8, 11, and 17. If you are using Java 11 or higher and intend
to use [JAXB](https://docs.oracle.com/javase/tutorial/jaxb/intro/), please see the section below for ensuring that the
necessary dependencies are available in your application's classpath.

## QuickStart

To use the client in your [Maven](https://maven.apache.org/) project, include the following in your `pom.xml` file:

    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>marklogic-client-api</artifactId>
        <version>6.4.1</version>
    </dependency>

To use the client in your [Gradle](https://gradle.org/) project, include the following in your `build.gradle` file:

    dependencies {
        implementation "com.marklogic:marklogic-client-api:6.4.1"
    }

Next, read [The Java API in Five Minutes](http://developer.marklogic.com/try/java/index) to get started.

Full documentation is available at:

* [Java Application Developer's Guide](http://docs.marklogic.com/guide/java)
* [JavaDoc](http://docs.marklogic.com/javadoc/client/index.html)

## Support

The MarkLogic Java Client is maintained by [MarkLogic](https://www.marklogic.com/) Engineering and is made available under 
the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. 
Everyone is encouraged to file bug reports, feature requests, and pull requests through [GitHub](https://github.com/marklogic/java-client-api/issues). 
This input is critical and will be carefully considered. However, we can’t promise a specific resolution or timeframe 
for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client to 
licensed customers under the terms outlined in the [MarkLogic Technical Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). Customers with an 
active maintenance contract can sign up for MarkLogic Technical Support on our [support portal](https://help.marklogic.com/).

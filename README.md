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

### What's New in Java Client API 3.0.1

* Pojo Façade - persist POJOs as JSON objects and use a simple API to query them with all the power
  of MarkLogic's search engine
* Eval (and Invoke) - directly access MarkLogic's powerful server-side XQuery or JavaScript
* Bulk Read & Write - send and retrieve documents and metadata in batches for significant performance
  improvements
* JSON - JacksonHandle, JacksonDatabindHandle, and JacksonParserHandle make wrangling JSON a pleasure
* JavaScript Extensions - develop production-ready server-side extensions using ready-to-go scaffolding

For more details, please read this [deeper dive](http://developer.marklogic.com/features/java-client-api-2)

### What's New in Java Client API 3.0.2

* Many bug fixes

### What's New in Java Client API 3.0.3

* Search Extract - add support to SearchHandle (actually MatchDocumentSummary) for content extracted by
  the [extract-document-data option](http://docs.marklogic.com/search:search#opt-extract-document-data)
* Bi-Temporal enhancements - support bulk write of bitemporal documents; expose bitemporal system time
* Bulk delete

### What's New in Java Client API 3.0.4

* Semantics API - GraphManager for CRUD of semantic graphs; SPARQLQueryManager for executing SPARQL
  queries (select, describe, construct, and ask) and using SPARQL Update
* Enable [MarkLogic Jena API](https://github.com/marklogic/marklogic-jena) (separate project)
* Enable [MarkLogic Sesame API](https://github.com/marklogic/marklogic-sesame) (separate project)

### QuickStart

To use the API in your maven project, include the following in your pom.xml:

    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>java-client-api</artifactId>
        <version>3.0.4</version>
    </dependency>

For gradle projects, include the following:

    dependencies {
        compile group: 'com.marklogic', name: 'java-client-api', version: '3.0.4'
    }

Read [The Java API in Five Minutes](http://developer.marklogic.com/try/java/index)

### Learning More

The following resources document the Java API:

* [Java Application Developer's Guide](http://docs.marklogic.com/guide/java)
* [JavaDoc](http://docs.marklogic.com/javadoc/client/index.html)

### Installing

To use the Java API, either add Maven or Gradle dependency as explained above or download the jar and its dependencies:

http://developer.marklogic.com/products/java

Of course, you'll also need to install the database -- which you can do for free with
the developer license:

https://developer.marklogic.com/free-developer

To obtain verified downloads signed with MarkLogic's PGP key, use maven tools or directly download
the .jar and .asc files from
[maven central](http://repo1.maven.org/maven2/com/marklogic/java-client-api/3.0.1/).  MarkLogic's
pgp key ID is 48D4B86E and it is available from pgp.mit.edu by installing gnupg and running the command:

    $ gpg --keyserver pgp.mit.edu --recv-key 48D4B86E

Files can be verified with the command:

    $ gpg java-client-api-3.0.1.jar.asc


### Building and Contributing

You can build the API in the same way as any Maven project on git:

1. Clone the java-client-api repository on your machine.
2. Execute a Maven build in the directory containing the pom.xml file.  

You might want to skip the tests until you have configured a test database and REST server:

    $ mvn package -Dmaven.test.skip=true

See [CONTRIBUTING.md](CONTRIBUTING.md) for more on contributing to this github project.

### Running JUnit Tests

    $ mvn test-compile
    $ sh src/test/resources/boot-test.sh
    $ mvn test

## Support
The MarkLogic Java Client API is maintained by MarkLogic Engineering and distributed under the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. Everyone is encouraged to file bug reports, feature requests, and pull requests through GitHub. This input is critical and will be carefully considered, but we can’t promise a specific resolution or timeframe for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client API to licensed customers under the terms outlined in the [Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). For more information or to sign up for support, visit [help.marklogic.com](http://help.marklogic.com).

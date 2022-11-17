# The MarkLogic Java Client API

The API makes it easy to write, read, delete, and find documents
in a [MarkLogic](http://developer.marklogic.com/) database.

For example:

    // write a text, binary, XML, or JSON document from any source with ACID guarantees
    documentManager.write(uri, new FileHandle()
      .with(new File("file1234.json"))
      .withFormat(JSON));

    // read and directly parse to your preferred type, even your own POJOs!
    JsonNode jsonDocContents = documentManager.readAs(uri, JsonNode.class);

    // get matches super-fast using full-featured search
    JsonNode results = queryManager.search(
      new StructuredQueryBuilder().term("quick", "brown", "fox"),
      new JacksonHandle()).get();

The Java API supports the following core features of the MarkLogic database:

*  Write and read binary, JSON, text, and XML documents.
*  Query data structure trees, marked-up text, and all the hybrids in between those extremes.
*  Project values, tuples, and triples from hierarchical documents and aggregate over them.
*  Patch documents with partial updates.
*  Use Optimistic Locking to detect contention without creating locks on the server.
*  Execute ACID modifications so the change either succeeds or throws an exception.
*  Execute multi-statement transactions so changes to multiple documents succeed or fail together.
*  Call Data Services by means of a Java interface on the client for data functionality 
implemented by an endpoint on the server.

## QuickStart

To use the API in your maven project, include the following in your pom.xml:

    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>marklogic-client-api</artifactId>
        <version>6.0.0</version>
    </dependency>

For gradle projects, use gradle 4.x+ and include the following:

    dependencies {
        compile "com.marklogic:marklogic-client-api:6.0.0"
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
[maven central](https://repo1.maven.org/maven2/com/marklogic/marklogic-client-api/5.3.0/).  MarkLogic's
pgp key ID is 48D4B86E and it is available from pgp.mit.edu by installing gnupg and running the command:

    $ gpg --keyserver pgp.mit.edu --recv-key 48D4B86E

Files can be verified with the command:

    $ gpg marklogic-client-api-5.3.0.jar.asc


## Support
The MarkLogic Java Client API is maintained by [MarkLogic](https://www.marklogic.com/) Engineering and is made available under the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. Everyone is encouraged to file bug reports, feature requests, and pull requests through [GitHub](https://github.com/marklogic/java-client-api/issues). This input is critical and will be carefully considered. However, we can’t promise a specific resolution or timeframe for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client API to licensed customers under the terms outlined in the [MarkLogic Technical Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). Customers with an active maintenance contract can sign up for MarkLogic Technical Support on our [support portal](https://help.marklogic.com/).

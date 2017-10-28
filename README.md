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
*  Match documents against alerting rules expressed as queries.
*  Use Optimistic Locking to detect contention without creating locks on the server.
*  Execute ACID modifications so the change either succeeds or throws an exception.
*  Execute multi-statement transactions so changes to multiple documents succeed or fail together.

### What's New in Java Client API 4

* Optic API - blends relational with NoSQL by providing joins and aggregates over documents
  * is powered by the new row index and query optimizer
  * uses row, triple, and/or lexicon lenses
  * matches the functionality of the Optic API for XQuery and Javascript, but idiomatic for Java
    developers
* Data Movement SDK - move large amounts of data into, out of, or within a MarkLogic cluster
  * WriteBatcher distributes writes across many threads and across the entire MarkLogic cluster
  * QueryBatcher enables bulk processing or export of matches to a query by distributing the query
    across many threads and batch processing to listeners
  * Comes with ApplyTransformListener, DeleteListener, ExportListener, ExportToWriterListener, and
    UrisToWriterListener
  * With custom listeners you can easily and efficiently apply your business logic to batches of query
    matches
* Kerberos and Client Certificate Authentication
* Geospatial double precision and queries on region indexes
* Temporal document enhancements
  * protect and wipe
  * more control over version uris
* Support for document metadata values

See also [CHANGELOG.md](CHANGELOG.md)

### QuickStart

To use the API in your maven project, include the following in your pom.xml:

    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>marklogic-client-api</artifactId>
        <version>4.0.3</version>
    </dependency>

And add this repository to your pom.xml repositories section:

    <repository>
        <id>jcenter</id>
        <url>http://jcenter.bintray.com</url>
    </repository>

For gradle projects, include the following:

    dependencies {
        compile group: 'com.marklogic', name: 'marklogic-client-api', version: '4.0.3'
    }

Use gradle 1.7+ and add this to your build.gradle repositories section:

    jcenter()

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
[maven central](http://repo1.maven.org/maven2/com/marklogic/marklogic-client-api/4.0.3/).  MarkLogic's
pgp key ID is 48D4B86E and it is available from pgp.mit.edu by installing gnupg and running the command:

    $ gpg --keyserver pgp.mit.edu --recv-key 48D4B86E

Files can be verified with the command:

    $ gpg marklogic-client-api-4.0.3.jar.asc


### Building and Contributing

You can build the API in the same way as any Maven project on git:

1. Clone the java-client-api repository on your machine.
2. Choose the appropriate branch (usually develop)
3. Execute a Maven build in the directory containing the pom.xml file.

You might want to skip the tests until you have configured a test database and REST server:

    $ mvn package -Dmaven.test.skip=true

See [CONTRIBUTING.md](CONTRIBUTING.md) for more on contributing to this github project.

### Running JUnit Tests

    $ mvn test-compile
    $ mvn exec:java -DexecutionId=test-server-init
    $ mvn test

## Support
The MarkLogic Java Client API is maintained by MarkLogic Engineering and distributed under the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. Everyone is encouraged to file bug reports, feature requests, and pull requests through GitHub. This input is critical and will be carefully considered, but we can’t promise a specific resolution or timeframe for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client API to licensed customers under the terms outlined in the [Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). For more information or to sign up for support, visit [help.marklogic.com](http://help.marklogic.com).

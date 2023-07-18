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

The Java API can be used in applications running on Java 8, 11, and 17. If you are using Java 11 or higher and intend
to use [JAXB](https://docs.oracle.com/javase/tutorial/jaxb/intro/), please see the section below for ensuring that the
necessary dependencies are available in your application's classpath.

## QuickStart

To use the API in your [Maven](https://maven.apache.org/) project, include the following in your pom.xml file:

    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>marklogic-client-api</artifactId>
        <version>6.2.2</version>
    </dependency>

To use the API in your [Gradle](https://gradle.org/) project, include the following in your build.gradle file:

    dependencies {
        implementation "com.marklogic:marklogic-client-api:6.2.2"
    }

Next, read [The Java API in Five Minutes](http://developer.marklogic.com/try/java/index) to get started.

### Including JAXB support 

If you are using Java 11 or higher (including Java 17) and you wish to use [JAXB](https://docs.oracle.com/javase/tutorial/jaxb/intro/)
with the Java Client, you'll need to include JAXB API and implementation dependencies as those are no 
longer included in Java 11 and higher.

For Maven, include the following in your pom.xml file:

    <dependency>
        <groupId>javax.xml.bind</groupId>
        <artifactId>jaxb-api</artifactId>
        <version>2.3.1</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jaxb</groupId>
        <artifactId>jaxb-runtime</artifactId>
        <version>2.3.2</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jaxb</groupId>
        <artifactId>jaxb-core</artifactId>
        <version>2.3.0.1</version>
    </dependency>

For Gradle, include the following in your build.gradle file (this can be included in the same `dependencies` block 
as the one that includes the marklogic-client-api dependency):

    dependencies {
        implementation "javax.xml.bind:jaxb-api:2.3.1"
        implementation "org.glassfish.jaxb:jaxb-runtime:2.3.2"
        implementation "org.glassfish.jaxb:jaxb-core:2.3.0.1"
    }

You are free to use any implementation of JAXB that you wish, but you need to ensure that you're using a JAXB 
implementation that corresponds to the `javax.xml.bind` interfaces. JAXB 3.0 and 4.0 interfaces are packaged under 
`jakarta.xml.bind`, and the Java API does not yet depend on those interfaces. 

Thus, you are free to include an implementation of JAXB 3.0 or 4.0 in your project for your own use; it will not 
affect the Java API. A caveat though is if you are trying to use different major versions of the same JAXB 
implementation library - such as `org.glassfish.jaxb:jaxb-runtime` - then you will run into an expected dependency 
conflict between the two versions of the library. This can be worked around by using a different implementation of 
JAXB 3.0 or JAXB 4.0 - for example:

    dependencies {
        // JAXB 2 dependencies required by Java Client
        implementation "javax.xml.bind:jaxb-api:2.3.1"
        implementation "org.glassfish.jaxb:jaxb-runtime:2.3.2"
        implementation "org.glassfish.jaxb:jaxb-core:2.3.0.1"
        
        // JAXB 4 dependencies required by other application code
        implementation "jakarta.xml.bind:jakarta.xml.bind-api:4.0.0"
        implementation "com.sun.xml.bind:jaxb-impl:4.0.1"
    }

The Java Client will soon be updated to use the newer `jakarta.xml.bind` interfaces. Until then, the above approach
or one similar to it will allow for both the old and new JAXB interfaces and implementations to exist together in the
same classpath.

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
[maven central](https://repo1.maven.org/maven2/com/marklogic/marklogic-client-api/).  MarkLogic's
pgp key ID is 48D4B86E and it is available from pgp.mit.edu by installing gnupg and running the command:

    $ gpg --keyserver pgp.mit.edu --recv-key 48D4B86E

Files can be verified with the command:

    $ gpg marklogic-client-api-5.3.0.jar.asc


## Support
The MarkLogic Java Client API is maintained by [MarkLogic](https://www.marklogic.com/) Engineering and is made available under the [Apache 2.0 license](https://github.com/marklogic/java-client-api/blob/master/LICENSE). It is designed for use in production applications with MarkLogic Server. Everyone is encouraged to file bug reports, feature requests, and pull requests through [GitHub](https://github.com/marklogic/java-client-api/issues). This input is critical and will be carefully considered. However, we can’t promise a specific resolution or timeframe for any request. In addition, MarkLogic provides technical support for [release tags](https://github.com/marklogic/java-client-api/releases) of the Java Client API to licensed customers under the terms outlined in the [MarkLogic Technical Support Handbook](http://www.marklogic.com/files/Mark_Logic_Support_Handbook.pdf). Customers with an active maintenance contract can sign up for MarkLogic Technical Support on our [support portal](https://help.marklogic.com/).

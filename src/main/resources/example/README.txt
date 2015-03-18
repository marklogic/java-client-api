MarkLogic Java API Examples
October 2014

== Cookbook =============

The com/marklogic/client/example/cookbook directory provides recipes
for common tasks using the MarkLogic Java API.

Before using the examples, perform the following actions:

1.  Install MarkLogic 8 for your chosen platform.

2.  Edit Example.properties to specify the connection parameters for the
REST API instance.

    You must identify users for the rest-writer and rest-admin roles.  To
    add a user for each of those roles, you can use the Admin Interface at:

        http://YOUR_HOST:8001/user-summary.xqy?section=security

    If you make the user name the same as the role name and set the
    password to "x" you will not need to modify those values in the
    properties file.

The examples illustrate the following common tasks:

*  ClientCreator              create a database client
*  DocumentWrite              write document content to the database
*  DocumentWriteServerURI     write document content to the database with an
                              identifier assigned by the server
*  DocumentRead               read document content from the database
*  DocumentMetadataWrite      write document content and metadata
*  DocumentMetadataRead       read document content and metadata
*  DocumentDelete             delete a document from the database
*  DocumentFormats            work with binary, JSON, text, and XML documents
*  DocumentOutputStream       supply content while writing to the database
*  JAXBDocument               write and read a POJO using JAXB
*  QueryOptions               configure search with saved options
*  StringSearch               search with string criteria and saved options
*  StructuredSearch           search with a criteria structure and saved
                              options
*  RawCombinedSearch          search with a criteria structure and options
                              in the request
*  MultiStatementTransaction  complete a transaction with several requests
*  ServerResponseTransform    transform a search response on the server 
*  DocumentReadTransform      transform content on the server while reading
*  DocumentWriteTransform     transform content on the server while writing
*  ResourcExtension           extend the Java API to execute other capabilities
                              of the MarkLogic server on the client
*  OptimisticLocking          write or delete a document only if it has not
                              changed
*  RawClientAlert             define and find rules that match documents
*  Suggest                    get suggestions for search criteria words
*  SSLClientCreator           create a database client using SSL

Each example is a Java application that you can compile and run using the jar
file for the Java API and its dependencies.

Note:  To run GraphSPARQLExample, you must enable a triple index on your database
(Documents database by default).

Note:  To run SSLClientCreator, you must modify the REST server by specifying
an SSL certificate template.  The other examples do not use SSL.


== Extension Handles ====

You can extend the Java API to support new kinds of content representations.

The examples add support for the JDOM, XOM, Jackson, and other libraries. 

To compile the JDOM example, first download the library at version 2.0.2 or
higher from:

    http://www.jdom.org/

To compile the XOM example, first download the library at version 1.2.5 or
higher from:

    http://www.xom.nu/

To compile the Apache HTTPClient example, you use the Apache HTTPClient
bundled with the MarkLogic Java API in the lib subdirectory.

The Java classes named *Example are applications that you can compile and run
using the jar files for the library as well as the Java API and its
dependencies.

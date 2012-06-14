MarkLogic Java API Examples
2012-4-2

== Cookbook =============

The com/marklogic/client/example/cookbook directory provides recipes
for common tasks using the MarkLogic Java API.

Before using the examples, please perform the following actions:

1.  Install 5.1 MarkLogic Server for your chosen platform.

    We recommend that you uninstall any pevious MarkLogic version before installing
    MarkLogic 5.1 and use a fresh data directory for your forests.

2.  Set up a new REST API instance for your database.  You can use the AppServices web UI at

        http://YOUR_HOST:8000/appservices/

    to create a new RESTful Server for a database.  Alternatively, you can use the bash shell
    script provided with the REST API examples.

3.  Edit Example.properties to specify the connection parameters for the REST API instance.

    You must identify users for the rest-writer and rest-admin roles.  To add a user for
    each of those roles, you can use the Admin UI at

        http://YOUR_HOST:8001/user-summary.xqy?section=security

    If you make the user name the same as the role name and set the password to "x" you won't
    have to modify those values in the properties file.  Change the example.port property to
    the port number of the REST Server you created in step 2 of these instructions.

The examples illustrate the following common tasks:

*  ClientCreator              create a database client
*  DocumentWrite              write document content to the database
*  DocumentRead               read document content from the database
*  DocumentMetadataWrite      write document content and metadata
*  DocumentMetadataRead       read document content and metadata
*  DocumentDelete             delete a document from the database
*  DocumentFormats            work with binary, JSON, text, and XML documents
*  DocumentOutputStream       supply content while writing to the database
*  JAXBDocument               write and read a POJO using JAXB 
*  KeyValueSearch             search for documents based on values
*  QueryOptions               configure search with saved options
*  StringSearch               search with string criteria and saved options
*  StructuredSearch           search with a criteria structure and saved options
*  MultiStatementTransaction  complete a transaction with several requests 
*  DocumentReadTransform      transform content on the server while reading
*  DocumentWriteTransform     transform content on the server while writing
*  OptimisticLocking          write or delete a document only if it hasn't changed
*  SSLClientCreator           create a database client using SSL

Each example is a Java application that you can compile and run using the jar
file for the Java API and its dependencies.

Note:  To run SSLClientCreator, you must modify the REST server by specifying
an SSL certificate template.  The other examples do not use SSL.


== Extension Handles ====

You can extend the Java API to support new kinds of content representations.

The examples add support for the JDOM, XOM, Jackson, and Apache HTTPClient
libraries.  

To compile the JDOM example, first download the library at version 1.1.3 or
higher from

    http://www.jdom.org/

To compile the XOM example, first download the library at version 1.2.5 or
higher from

    http://www.xom.nu/

To compile the Jackson example (which uses the tree model), first download
the core and databinding libraries at 2.0.1 or higher as linked from:

    http://wiki.fasterxml.com/JacksonHome

To compile the Apache HTTPClient example, you use the Apache HTTPClient
bundled with the MarkLogic Java API in the lib subdirectory.

The Java classes named *Example are applications that you can compile and run
using the jar files for the library as well as the Java API and its dependencies.

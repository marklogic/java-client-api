MarkLogic Java API Examples
2012-4-2

== Cookbook =============

The com/marklogic/client/example/cookbook directory provides recipes
for common tasks using the MarkLogic Java API.

Before using the examples, please perform the following actions:

1.  Set up a new REST API instance for your database.  You can use the AppServices web UI
    or the bash shell script provided with the REST API examples.

2.  Edit Example.properties to specify the connection parameters for the REST API instance.
    You must identify users for the rest-writer and rest-admin roles.

The examples illustrate the following common tasks:

*  ClientConnect:              connect to the database
*  DocumentWrite:              write document content to the database
*  DocumentRead:               read document content from the database
*  DocumentMetadataWrite       write document content and metadata
*  DocumentMetadataRead        read document content and metadata
*  DocumentDelete              delete a document from the database
*  DocumentFormats             work with binary, JSON, text, and XML documents
*  JAXBDocument                write and read a POJO using JAXB 
*  KeyValueSearch              search for documents based on values
*  QueryOptions                configure search with saved options
*  StringOptionsSearch         search with string criteria and saved options
*  MultiStatementTransaction:  complete a transaction with several requests 

Each example is a Java application that you can compile and run using the jar
file for the Java API and its dependencies.

== Extension Handles ====

You can extend the Java API to support new kinds of content representations.

The examples add support for the JDOM and XOM libraries.  

To compile the JDOM example, first download the library at version 1.1.3 or
higher from

    http://www.jdom.org/

To compile the XOM example, first download the library at version 1.2.5 or
higher from

    http://www.xom.nu/

The *Example Java classes are applications that you can compile and run using
the jar files for the library as well as the Java API and its dependencies.

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * StringSearch illustrates searching for documents and iterating over results
 * with string criteria referencing a constraint defined by options.
 */
public class StringSearch {
  static final private String OPTIONS_NAME = "products";

  static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

  public static void main(String[] args)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    System.out.println("example: "+StringSearch.class.getName());
    configure(props);
    search(props);
    tearDownExample(props);
  }

  public static void configure(ExampleProperties props)
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // construct the query options
    String options =
      "<search:options "+
        "xmlns:search='http://marklogic.com/appservices/search'>"+
        "<search:constraint name='industry'>"+
        "<search:value>"+
        "<search:element name='industry' ns=''/>"+
        "</search:value>"+
        "</search:constraint>"+
        "</search:options>";

    // create a handle to send the query options
    StringHandle writeHandle = new StringHandle(options);

    // write the query options to the database
    optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

    // release the client
    client.release();
  }

  public static void search(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newClient(props);

    setUpExample(client);

    // create a manager for searching
    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    StringQueryDefinition querydef = queryMgr.newStringDefinition(OPTIONS_NAME);
    querydef.setCriteria("neighborhood industry:\"Real Estate\"");

    // create a handle for the search results
    SearchHandle resultsHandle = new SearchHandle();

    // run the search
    queryMgr.search(querydef, resultsHandle);

    System.out.println("Matched "+resultsHandle.getTotalResults()+
      " documents with '"+querydef.getCriteria()+"'\n");

    // iterate over the result documents
    MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
    System.out.println("Listing "+docSummaries.length+" documents:\n");
    for (MatchDocumentSummary docSummary: docSummaries) {
      String uri = docSummary.getUri();
      int score = docSummary.getScore();

      // iterate over the match locations within a result document
      MatchLocation[] locations = docSummary.getMatchLocations();
      System.out.println("Matched "+locations.length+" locations in "+uri+" with "+score+" score:");
      for (MatchLocation location: locations) {

        // iterate over the snippets at a match location
        for (MatchSnippet snippet : location.getSnippets()) {
          boolean isHighlighted = snippet.isHighlighted();

          if (isHighlighted)
            System.out.print("[");
          System.out.print(snippet.getText());
          if (isHighlighted)
            System.out.print("]");
        }
        System.out.println();
      }
    }

    // release the client
    client.release();
  }

  // set up by writing the document content and options used in the example query
  public static void setUpExample(DatabaseClient client)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    try ( InputStreamHandle contentHandle = new InputStreamHandle() ) {

      for (String filename: filenames) {
        InputStream docStream = Util.openStream("data"+File.separator+filename);
        if (docStream == null)
          throw new IOException("Could not read document example");

        contentHandle.set(docStream);

        docMgr.write("/example/"+filename, contentHandle);
      }
    }
  }

  // clean up by deleting the documents and query options used in the example query
  public static void tearDownExample(ExampleProperties props)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    for (String filename: filenames) {
      docMgr.delete("/example/"+filename);
    }

    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    optionsMgr.deleteOptions(OPTIONS_NAME);

    client.release();
  }
}

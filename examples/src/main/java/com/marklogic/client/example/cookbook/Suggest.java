/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Suggest illustrates getting suggestions for words to find in an element.
 *
 * NOTE:  To get suggestions, you must configure the database with an element
 * word lexicon on the description element before running this example.  You
 * can configure an element word lexicon using the Admin UI on port 8000.
 */
public class Suggest {
  static final private String OPTIONS_NAME = "description";

  static final private String[] filenames = {"curbappeal.xml", "flipper.xml", "justintime.xml"};

  public static void main(String[] args)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException {
    System.out.println("example: "+Suggest.class.getName());
    configure(props);
    suggest(props);
    tearDownExample(props);
  }

  public static void configure(ExampleProperties props)
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException {

	  DatabaseClient client = Util.newAdminClient(props);

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // construct the query options
    String options =
      "<search:options "+
        "xmlns:search='http://marklogic.com/appservices/search'>"+
        "<search:default-suggestion-source>"+
        "<search:word>"+
        "<search:element ns='' name='description'/>"+
        "</search:word>"+
        "</search:default-suggestion-source>"+
        "</search:options>";

    // create a handle to send the query options
    StringHandle writeHandle = new StringHandle(options);

    // write the query options to the database
    optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);

    // release the client
    client.release();
  }

  public static void suggest(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
	  DatabaseClient client = Util.newClient(props);

    setUpExample(client);

    // create a manager for getting suggestions
    QueryManager queryMgr = client.newQueryManager();

    // specify the partial criteria for the suggestions and the options
    //     that define the source of the suggestion words
    String partialCriteria = "tr";
    SuggestDefinition def = queryMgr.newSuggestDefinition(
      partialCriteria, OPTIONS_NAME);

    // get the suggestions
    String[] suggestions = queryMgr.suggest(def);

    System.out.println("'"+partialCriteria+"' criteria matched "+
      suggestions.length+" suggestions:");

    // iterate over the suggestions
    for (String suggestion: suggestions) {
      System.out.println("    "+suggestion);
    }

    // release the client
    client.release();
  }

  // set up by writing the document content and options used in the example query
  public static void setUpExample(DatabaseClient client)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    InputStreamHandle contentHandle = new InputStreamHandle();

    for (String filename: filenames) {
      try ( InputStream docStream = Util.openStream("data"+File.separator+filename) ) {
        if (docStream == null) throw new IOException("Could not read document example");

        contentHandle.set(docStream);

        docMgr.write("/example/"+filename, contentHandle);
      }
    }
  }

  // clean up by deleting the documents and query options used in the example query
  public static void tearDownExample(ExampleProperties props)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
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

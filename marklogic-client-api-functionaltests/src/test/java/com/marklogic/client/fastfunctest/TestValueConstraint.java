/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestValueConstraint extends AbstractFunctionalTest {

  static final private String[] filenames = { "value-constraint-doc.xml", "value-constraint-doc2.xml" };

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testElementValueConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    String docId = null;

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    for (String filename : filenames)
    {
      // acquire the content
      BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/data/" + filename));

      // create an identifier for the document
      docId = "/value-constraint/" + filename;

      handle.set(docStream);

      // write the document content
      docMgr.write(docId, handle);

      System.out.println("Write " + docId + " to database");
    }

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create the query options
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
    builder.append("  <constraint name=\"my-element-value\">\n");
    builder.append("    <value>\n");
    builder.append("      <element ns=\"\" name=\"title\"/>\n");
    builder.append("    </value>\n");
    builder.append("  </constraint>\n");
    builder.append("  <constraint name=\"my-attribute-value\">\n");
    builder.append("    <value>\n");
    builder.append("      <attribute ns=\"\" name=\"year\"/>\n");
    builder.append("      <element ns=\"\" name=\"book\"/>\n");
    builder.append("    </value>\n");
    builder.append("  </constraint>\n");
    builder.append("</options>\n");

    // initialize a handle with the query options
    StringHandle writeHandle = new StringHandle(builder.toString());

    // write the query options to the database
    optionsMgr.writeOptions("valueConstraintOpt", writeHandle);

    // create a manager for searching
    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    StringQueryDefinition querydef = queryMgr.newStringDefinition("valueConstraintOpt");
    querydef.setCriteria("my-element-value:\"Indiana Jones\"");

    // create a handle for the search results
    SearchHandle resultsHandle = new SearchHandle();

    // run the search
    queryMgr.search(querydef, resultsHandle);

    // iterate over the result documents
    MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
    String searchMatch = "";
    for (MatchDocumentSummary docSummary : docSummaries) {
      String uri = docSummary.getUri();

      // iterate over the match locations within a result document
      MatchLocation[] locations = docSummary.getMatchLocations();
      searchMatch = "Matched " + locations.length + " locations in " + uri;
    }

    String expectedSearchMatch = "Matched 1 locations in /value-constraint/value-constraint-doc2.xml";
    assertEquals( expectedSearchMatch, searchMatch);

    // release client
    client.release();
  }

  @Test
  public void testAttributeValueConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create doc id
    String docId = null;

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    for (String filename : filenames)
    {
      // acquire the content
      BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/data/" + filename));

      // create an identifier for the document
      docId = "/value-constraint/" + filename;

      handle.set(docStream);

      // write the document content
      docMgr.write(docId, handle);

      System.out.println("Write " + docId + " to database");
    }
    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create the query options
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
    builder.append("  <constraint name=\"my-element-value\">\n");
    builder.append("    <value>\n");
    builder.append("      <element ns=\"\" name=\"title\"/>\n");
    builder.append("    </value>\n");
    builder.append("  </constraint>\n");
    builder.append("  <constraint name=\"my-attribute-value\">\n");
    builder.append("    <value>\n");
    builder.append("      <attribute ns=\"\" name=\"year\"/>\n");
    builder.append("      <element ns=\"\" name=\"book\"/>\n");
    builder.append("    </value>\n");
    builder.append("  </constraint>\n");
    builder.append("</options>\n");

    // initialize a handle with the query options
    StringHandle writeHandle = new StringHandle(builder.toString());

    // write the query options to the database
    optionsMgr.writeOptions("valueConstraintOpt", writeHandle);

    // create a manager for searching
    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    StringQueryDefinition querydef = queryMgr.newStringDefinition("valueConstraintOpt");
    querydef.setCriteria("my-attribute-value:2005");

    // create a handle for the search results
    SearchHandle resultsHandle = new SearchHandle();

    // run the search
    queryMgr.search(querydef, resultsHandle);

    // iterate over the result documents
    MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
    String searchMatch = "";
    for (MatchDocumentSummary docSummary : docSummaries) {
      String uri = docSummary.getUri();

      // iterate over the match locations within a result document
      MatchLocation[] locations = docSummary.getMatchLocations();
      searchMatch = "Matched " + locations.length + " locations in " + uri;
    }

    String expectedSearchMatch = "Matched 1 locations in /value-constraint/value-constraint-doc.xml";
    assertEquals( expectedSearchMatch, searchMatch);

    // release client
    client.release();
  }
}

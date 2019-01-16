/*
 * Copyright 2014-2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.MatchSnippet;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestSearchOnProperties extends BasicJavaClientREST {

  private static String dbName = "SearchPropsDB";
  private static String[] fNames = { "SearchPropsDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @Test
  public void testSearchOnProperties() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create the query options
    StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
    builder.append("<fragment-scope>properties</fragment-scope>\n");
    builder.append("</options>\n");

    // initialize a handle with the query options
    StringHandle writeHandle = new StringHandle(builder.toString());

    // write the query options to the database
    optionsMgr.writeOptions("propSearchOpt", writeHandle);

    // acquire the content
    File file = new File("xml-with-props.xml");
    file.delete();
    boolean success = file.createNewFile();
    if (success)
      System.out.println("New file created on " + file.getAbsolutePath());
    else
      System.out.println("Cannot create file");

    BufferedWriter out = new BufferedWriter(new FileWriter(file));
    String content =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<shipment>\n" +
            "<location>\n" +
            "<port>Lisbon</port>\n" +
            "<country>Portugal</country>\n" +
            "</location>\n" +
            "<location>\n" +
            "<port>Madrid</port>\n" +
            "<country>Spain</country>\n" +
            "</location>\n" +
            "</shipment>";
    out.write(content);
    out.close();

    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/searchOnProps/" + file.getPath();

    // create a handle on the content
    FileHandle contentHandle = new FileHandle(file);
    contentHandle.set(file);

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("notes", "embarcadero");
    metadataHandle.getProperties().put("number", 1234);
    metadataHandle.getProperties().put("longnumber", 1234.455);
    metadataHandle.getProperties().put("date", Calendar.getInstance());

    // write the document content
    docMgr.write(docId, metadataHandle, contentHandle);

    System.out.println("Write " + docId + " to database");

    DOMHandle readHandle = new DOMHandle();

    // read the metadata
    docMgr.read(docId, metadataHandle, readHandle);

    // create a manager for searching
    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    StringQueryDefinition queryDef = queryMgr.newStringDefinition("propSearchOpt");
    queryDef.setCriteria("embarcadero");

    // create a handle for the search results
    SearchHandle resultsHandle = new SearchHandle();

    // run the search
    queryMgr.search(queryDef, resultsHandle);

    long totalResults = resultsHandle.getTotalResults();
    assertEquals("Total results difference", 1, totalResults);

    // iterate over the result documents
    MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();

    int docSummaryLength = docSummaries.length;
    assertEquals("Document summary list difference", 1, docSummaryLength);

    for (MatchDocumentSummary docSummary : docSummaries) {
      String uri = docSummary.getUri();

      // iterate over the match locations within a result document
      MatchLocation[] locations = docSummary.getMatchLocations();
      assertEquals("Document location difference", "/searchOnProps/" + file.getPath(), uri);

      for (MatchLocation location : locations) {

        // iterate over the snippets at a match location
        for (MatchSnippet snippet : location.getSnippets()) {
          String highlightedText = "";
          boolean isHighlighted = snippet.isHighlighted();
          if (isHighlighted)
            highlightedText = highlightedText + "[";
          highlightedText = highlightedText + snippet.getText();
          highlightedText = highlightedText + "]";

          assertEquals("Document highlight snippet difference", "[embarcadero]", highlightedText);
        }
      }
    }

    // release the client
    client.release();
  }

  @Test
  public void testSearchOnPropertiesFragment() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSearchOnPropertiesFragment");

    String filename1 = "property1.xml";
    String filename2 = "property2.xml";
    String filename3 = "property3.xml";
    String queryOptionName = "propertiesSearchWordOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();

    // set metadata properties
    metadataHandle1.getProperties().put("city", "Tokyo");
    metadataHandle2.getProperties().put("city", "Shanghai");
    metadataHandle3.getProperties().put("city", "Tokyo");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/properties-search/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/properties-search/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/properties-search/", metadataHandle3, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("city-property:Shanghai");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/properties-search/property2.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSearchOnPropertiesBucket() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSearchOnPropertiesBucket");

    String filename1 = "property1.xml";
    String filename2 = "property2.xml";
    String filename3 = "property3.xml";
    String queryOptionName = "propertiesSearchWordOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();

    // set metadata properties
    metadataHandle1.getProperties().put("popularity", 5);
    metadataHandle2.getProperties().put("popularity", 9);
    metadataHandle3.getProperties().put("popularity", 1);

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/properties-search/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/properties-search/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/properties-search/", metadataHandle3, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:medium");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/properties-search/property1.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
    assertXpathEvaluatesTo("/properties-search/property2.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSearchOnPropertiesBucketAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException, TransformerException
  {
    System.out.println("Running testSearchOnPropertiesBucketAndWord");

    String filename1 = "property1.xml";
    String filename2 = "property2.xml";
    String filename3 = "property3.xml";
    String queryOptionName = "propertiesSearchWordOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();

    // set metadata properties
    metadataHandle1.getProperties().put("popularity", 5);
    metadataHandle2.getProperties().put("popularity", 9);
    metadataHandle3.getProperties().put("popularity", 1);
    metadataHandle1.getProperties().put("city", "Shanghai is a good one");
    metadataHandle2.getProperties().put("city", "Tokyo is hot in the summer");
    metadataHandle3.getProperties().put("city", "The food in Seoul is similar in Shanghai");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/properties-search/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/properties-search/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/properties-search/", metadataHandle3, "XML");

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:medium AND city-property:Shanghai");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/properties-search/property1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

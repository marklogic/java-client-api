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
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;
import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.ExtractedItem;
import com.marklogic.client.query.ExtractedResult;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;

public class TestSearchOptions extends BasicJavaClientREST {

  private static String dbName = "TestSearchOptionsDB";
  private static String[] fNames = { "TestSearchOptionsDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testReturnResultsFalse() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testReturnResultsFalse");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "searchReturnResultsFalseOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-results-false/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("intitle:1945");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
    assertXpathNotExists("//*[local-name()='result']", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSetViewMetadata() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetViewMetadata");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "setViewOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // put metadata
    metadataHandle.getCollections().addAll("my-collection");
    metadataHandle.getCollections().addAll("another-collection");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-meta/", metadataHandle, "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setView(QueryView.METADATA);

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='response']//@*[local-name()='start'])", resultDoc);
    assertXpathExists("//*[local-name()='metrics']", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSetViewResults() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetViewResults");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "setViewOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-results/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setView(QueryView.RESULTS);

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
    assertXpathExists("//*[local-name()='result']", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSetViewFacets() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetViewFacets");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "setViewOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-facets/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setView(QueryView.FACETS);

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
    assertXpathExists("//*[local-name()='facet']", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSetViewDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetViewDefault");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "setViewOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-all/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setView(QueryView.DEFAULT);

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
    assertXpathExists("//*[local-name()='result']", resultDoc);
    assertXpathExists("//*[local-name()='facet']", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSetViewAll() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testSetViewAll");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "setViewOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/return-setview-all/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();
    queryMgr.setView(QueryView.ALL);

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//@*[local-name()='total'])", resultDoc);
    assertXpathExists("//*[local-name()='result']", resultDoc);
    assertXpathExists("//*[local-name()='facet']", resultDoc);
    assertXpathExists("//*[local-name()='metrics']", resultDoc);

    // release client
    client.release();
  }
  
  
  //Tests for simple restricted xpath
  @Test
  public void testRestrictedXPaths() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testRestrictedXPaths");
    final String DIRECTORY = "/RXath/";
    final int BATCH_SIZE = 10;
    StringBuilder content1 = new StringBuilder();
    content1.append("{\"World\":[{\"CountyId\": \"0001\",");
    content1.append("\"Govt\": \"Presidential\",");
    content1.append("\"name\": \"USA\",");
    content1.append("\"Pop\": 328,");
    content1.append("\"Regions\":{\"Contiental\":[");
    content1.append("{ \"RegionId\": \"1001\", \"Direction\": \"NE\" },");
    content1.append("{ \"RegionId\": \"1002\", \"Direction\": \"SE\" },");
    content1.append("{ \"RegionId\": \"1003\", \"Direction\": \"NW\" },");
    content1.append("{ \"RegionId\": \"1004\", \"Direction\": \"SW\" }");
    content1.append("]}}]}");
    
    StringBuilder content2 = new StringBuilder();
    content2.append("{\"World\":[{\"CountyId\": \"0002\",");
    content2.append("\"Govt\": \"Monarchy\",");
    content2.append("\"name\": \"Canada\",");
    content2.append("\"Pop\": 215,");
    content2.append("\"Regions\":{\"Contiental\":[");
    content2.append("{ \"RegionId\": \"2001\", \"Direction\": \"NE\" },");
    content2.append("{ \"RegionId\": \"2002\", \"Direction\": \"SE\" },");
    content2.append("{ \"RegionId\": \"2003\", \"Direction\": \"NW\" },");
    content2.append("{ \"RegionId\": \"2004\", \"Direction\": \"SW\" }");
    content2.append("]}}]}");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // Write docs
    String docStr = content1.toString();
    DocumentWriteSet writeset1 = docMgr.newWriteSet();
    for (int i = 0; i < 11; i++) {
        writeset1.add(DIRECTORY + "World-01-" + i + ".json", new StringHandle(docStr));

        if (count % BATCH_SIZE == 0) {
            docMgr.write(writeset1);
            writeset1 = docMgr.newWriteSet();
        }
        count++;
    }
    if (count % BATCH_SIZE > 0) {
        docMgr.write(writeset1);
    }
    
    // Write docs
    DocumentWriteSet writeset2 = docMgr.newWriteSet();
    docStr = content2.toString();
    for (int i = 0; i < 1; i++) {
        writeset2.add(DIRECTORY + "World-02-" + i + ".json", new StringHandle(docStr));

        if (count % BATCH_SIZE == 0) {
            docMgr.write(writeset2);
            writeset2 = docMgr.newWriteSet();
        }
        count++;
    }
    if (count % BATCH_SIZE > 0) {
        docMgr.write(writeset2);
    }
    
    QueryManager queryMgr = client.newQueryManager();
    
    String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
    String tail = "</search:search>";
    String qtext1 = "<search:qtext>Presidential</search:qtext>";
    
    String options1 ="<search:options>" +
                    "<search:extract-document-data selected=\"include\">" +
                    "</search:extract-document-data>" +
                    "</search:options>";

    String combinedSearch = head + qtext1 + options1 + tail;
    RawCombinedQueryDefinition rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(rawCombinedQueryDefinition, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    System.out.println(resultNode.toString());
    assertEquals("Total search elements for incorrect", "11", resultNode.get("total").asText());

    String options2 ="<search:options>" +
            "<search:extract-document-data selected=\"include\">" +
            "<search:extract-path>//Govt</search:extract-path>" +
            "<search:extract-path>//pop</search:extract-path>" +
            "</search:extract-document-data>" +
            "</search:options>";

    // Search for an element value
    String qtext2 = "<search:qtext>Presidential</search:qtext>";

    combinedSearch = head + qtext2 + options2 + tail;
    
     rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    SearchHandle resSearchHandle = new SearchHandle();
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    
    MatchDocumentSummary[] summaries = resSearchHandle.getMatchResults();

    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.JSON == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from element search " + extractItem);
                assertTrue("Extracted items incorrect", extractItem.contains("{\"Govt\":\"Presidential\"}")
                                                         || extractItem.contains("{\"Pop\":328}"));
            }
        }
    }
    // Extract path with array element 
    String qtext3 = "<search:qtext>2002</search:qtext>";
    String options3 ="<search:options>" +
            "<search:extract-document-data selected=\"include\">" +
            "<search:extract-path>/World/Regions/Contiental/RegioId</search:extract-path>" +
            "<search:extract-path>/World/Regions/Contiental/Direction</search:extract-path>" +
            "</search:extract-document-data>" +
            "</search:options>";

    combinedSearch = head + qtext3 + options3 + tail;
    
     rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = new SearchHandle();
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    String RegionPatternStr = "\\{\"RegioId\":\"[0-9]+\"\\}";
    String DirectionPatternStr = "\\{\"Direction\":\"([N|S])([E|W])\"\\}";

    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.JSON == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from array element search " + extractItem);
                if (extractItem.startsWith("{\"R")) {
                assertTrue("Extracted items incorrect", extractItem.matches(RegionPatternStr));
                }
                else {
                    assertTrue("Extracted items incorrect", extractItem.matches(DirectionPatternStr));
                }
            }
        }
    }

    // object-node - Named Node
    String qtext4 = "<search:qtext>1001</search:qtext>";
    String options4 ="<search:options>" +
                    "<search:extract-document-data selected=\"include\">" +
                    "<search:extract-path>/World//object-node('Regions')//Direction</search:extract-path>" +
                    "</search:extract-document-data>" +
                    "</search:options>";

    combinedSearch = head + qtext4 + options4 + tail;
    rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.JSON == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from named node element search " + extractItem);
                    assertTrue("Extracted Named node items incorrect", extractItem.matches(DirectionPatternStr));
            }
        }
    }
    
    // object-node - Unnamed Node
    String qtext5 = "<search:qtext>1001</search:qtext>";
    String options5 ="<search:options>" +
                    "<search:extract-document-data selected=\"include\">" +
                    "<search:extract-path>/World//object-node()//Direction</search:extract-path>" +
                    "</search:extract-document-data>" +
                    "</search:options>";

    combinedSearch = head + qtext5 + options5 + tail;
    rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.JSON == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from Unnamed node element search " + extractItem);
                    assertTrue("Extracted Unnamed node items incorrect", extractItem.matches(DirectionPatternStr));
            }
        }
    }
    
    // object-node - Number Node
    String qtext6 = "<search:qtext>1001</search:qtext>";
    String options6 ="<search:options>" +
                    "<search:extract-document-data selected=\"include\">" +
                    "<search:extract-path>/World//number-node()</search:extract-path>" +
                    "</search:extract-document-data>" +
                    "</search:options>";

    combinedSearch = head + qtext6 + options6 + tail;
    rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.JSON == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from Number node element search " + extractItem);
                    assertTrue("Extracted Number node items incorrect", extractItem.matches("\\{\"Pop\":328\\}"));
            }
        }
    }
    // release client
    client.release();
  }
  
  @Test
  public void testXmlFilesRestrictedXPaths() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testXmlFilesRestrictedXPaths");
    final String DIRECTORY = "/RXathOnXMLFiles/";
    final int BATCH_SIZE = 10;
    StringBuilder content1 = new StringBuilder();

    content1.append("<root>");
    content1.append("<title>Vannevar Bush</title>");
    content1.append("<popularity>5</popularity>");
    content1.append("<id>0011</id>");
    content1.append("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>");
    content1.append("<price xmlns=\"http://cloudbank.com\" amt=\"0.1\"/>");
    content1.append("<p>Vannevar Bush wrote an article for The Atlantic Monthly</p>");
    content1.append("</root>");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // Write docs
    String docStr = content1.toString();
    DocumentWriteSet writeset1 = docMgr.newWriteSet();
    for (int i = 0; i < 11; i++) {
        writeset1.add(DIRECTORY + "constr-01-" + i + ".xml", new StringHandle(docStr));

        if (count % BATCH_SIZE == 0) {
            docMgr.write(writeset1);
            writeset1 = docMgr.newWriteSet();
        }
        count++;
    }
    if (count % BATCH_SIZE > 0) {
        docMgr.write(writeset1);
    }

    QueryManager queryMgr = client.newQueryManager();
    
    String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
    String tail = "</search:search>";
    String qtext1 = "<search:qtext>0011</search:qtext>";
    
    String options1 ="<search:options>" +
                    "<search:extract-document-data selected=\"include\">" +
                    "</search:extract-document-data>" +
                    "</search:options>";

    String combinedSearch = head + qtext1 + options1 + tail;
    RawCombinedQueryDefinition rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    JacksonHandle resultsHandle = new JacksonHandle();
    queryMgr.search(rawCombinedQueryDefinition, resultsHandle);

    // get the result
    JsonNode resultNode = resultsHandle.get();
    System.out.println(resultNode.toString());
    assertEquals("Total search elements for incorrect", "11", resultNode.get("total").asText());

    String options2 ="<search:options>" +
            "<search:extract-document-data selected=\"include\">" +
           "<search:extract-path>/root/*[local-name()='price' and namespace-uri()='http://cloudbank.com' and fn:contains(@amt,'0.1')]</search:extract-path>" +
            "</search:extract-document-data>" +
            "</search:options>";

    // Search for an element value
    String qtext2 = "<search:qtext>0011</search:qtext>";

    combinedSearch = head + qtext2 + options2 + tail;
    
     rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    SearchHandle resSearchHandle = new SearchHandle();
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    
    MatchDocumentSummary[] summaries = resSearchHandle.getMatchResults();

    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( summary.getFormat() == Format.XML) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from price element search " + extractItem);
                assertTrue("Extracted price items incorrect", extractItem.contains("<price xmlns=\"http://cloudbank.com\" amt=\"0.1\"/>"));
            }
        }
    }
    // Extract path with array element - include-with-ancestors
    String qtext3 = "<search:qtext>0011</search:qtext>";
    String options3 ="<search:options>" +
            "<search:extract-document-data selected=\"include-with-ancestors\">" +
           "<search:extract-path>/root/*[local-name()='date' and namespace-uri()='http://purl.org/dc/elements/1.1/' and .='2005-01-01']</search:extract-path>" +
            "</search:extract-document-data>" +
            "</search:options>";

    combinedSearch = head + qtext3 + options3 + tail;
    
     rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = new SearchHandle();
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    
    for (MatchDocumentSummary summary : summaries) {
        ExtractedResult extracted = summary.getExtracted();
        if ( Format.XML == summary.getFormat() ) {
            for (ExtractedItem item : extracted) {
                String extractItem = item.getAs(String.class);
                System.out.println("Extracted item from date and ancestor element search " + extractItem);
                assertTrue("Extracted date and ancestor items incorrect", 
                            extractItem.contains("<root><date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date></root>"));
            }
        }
    }
    
 // Extract path with array element - include-with-ancestors - Negative
    String qtext4 = "<search:qtext>0011</search:qtext>";
    String options4 ="<search:options>" +
            "<search:extract-document-data selected=\"include-with-ancestors\">" +
           "<search:extract-path>/root/*[local-name()='date' and namespace-uri()='http://purl.org/dc/elements/1.1/' and .='2005-01-03']</search:extract-path>" +
            "</search:extract-document-data>" +
            "</search:options>";

    combinedSearch = head + qtext4 + options4 + tail;
    
     rawCombinedQueryDefinition =
            queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

    // create handle
    resSearchHandle = new SearchHandle();
    resSearchHandle = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = resSearchHandle.getMatchResults();
    
    MatchDocumentSummary summary = summaries[0]; 
    ExtractedResult extracted = summary.getExtracted();
    assertTrue("Extracted date and ancestor items incorrect", extracted.isEmpty());

    // release client
    client.release();
  }
  
  public void loadXMLDocuments() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
      
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

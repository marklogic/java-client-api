/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;


public class TestSearchOptions extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
      deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testReturnResultsFalse() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testReturnResultsFalse");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "searchReturnResultsFalseOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
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
    assertEquals( "11", resultNode.get("total").asText());

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
                assertTrue( extractItem.contains("{\"Govt\":\"Presidential\"}") || extractItem.contains("{\"Pop\":328}"));
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
                assertTrue( extractItem.matches(RegionPatternStr));
                }
                else {
                    assertTrue( extractItem.matches(DirectionPatternStr));
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
                    assertTrue( extractItem.matches(DirectionPatternStr));
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
                    assertTrue( extractItem.matches(DirectionPatternStr));
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
                    assertTrue( extractItem.matches("\\{\"Pop\":328\\}"));
            }
        }
    }
    // release client
    client.release();
  }

  // Note : Test asserts changed due to the fact that different serialization and parsers produce diff NS
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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
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
    assertEquals( "11", resultNode.get("total").asText());

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
                assertTrue( extractItem.contains("http://cloudbank.com"));
                assertTrue( extractItem.contains("amt=\"0.1\""));
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
                assertTrue(
                            extractItem.contains("http://purl.org/dc/elements/1.1"));
                assertTrue(
                        extractItem.contains("2005-01-01"));

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
    assertTrue( extracted.isEmpty());

    // release client
    client.release();
  }
}

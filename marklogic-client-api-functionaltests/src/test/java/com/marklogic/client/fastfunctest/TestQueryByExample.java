/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;



public class TestQueryByExample extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testQueryByExampleXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXML");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");

    String qbeQuery = convertFileToString(file);
    StringHandle qbeHandle = new StringHandle(qbeQuery);
    qbeHandle.setFormat(Format.XML);

    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);

    Document resultDoc = queryMgr.search(qbyex, new DOMHandle()).get();

    System.out.println("XML Result" + convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleXMLnew() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXML");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");

    String qbeQuery = convertFileToString(file);
    StringHandle qbeHandle = new StringHandle(qbeQuery);
    qbeHandle.setFormat(Format.XML);

    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);
    Document resultDoc = queryMgr.search(qbyex, new DOMHandle()).get();

    System.out.println("XML Result" + convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testQueryByExampleJSON");

    String[] filenames = { "constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.json");

    String qbeQuery = convertFileToString(file);
    StringHandle qbeHandle = new StringHandle(qbeQuery);
    qbeHandle.setFormat(Format.JSON);

    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);

    String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();

    System.out.println("testQueryByExampleJSON Result : " + resultDoc);

    assertTrue(
        resultDoc
            .contains("<search:result index=\"1\" uri=\"/qbe/constraint1.json\" path=\"fn:doc(&quot;/qbe/constraint1.json&quot;)\""));

    // release client
    client.release();
  }

  @Test
  public void testBug22179() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testBug22179");

    String[] filenames = { "constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
    }

    ServerConfigurationManager confMgr = client.newServerConfigManager();
    confMgr.setQueryValidation(true);

    String combinedCriteria = "{\n" +
        "  \"search\" : {\n" +
        "    \"options\" : {\n" +
        "      \"constraint\" : [ {\n" +
        "        \"name\" : \"para\",\n" +
        "        \"word\" : {\n" +
        "          \"term-option\" : [ \"case-insensitive\" ],\n" +
        "          \"field\" : {\n" +
        "            \"name\" : \"para\"\n" +
        "          }\n" +
        "        }\n" +
        "      }, {\n" +
        "        \"name\" : \"id\",\n" +
        "        \"value\" : {\n" +
        "          \"element\" : {\n" +
        "            \"ns\" : \"\",\n" +
        "            \"name\" : \"id\"\n" +
        "          }\n" +
        "        }\n" +
        "      } ],\n" +
        "      \"return-metrics\" : false,\n" +
        "      \"debug\" : true,\n" +
        "      \"return-qtext\" : false,\n" +
        "      \"transform-results\" : {\n" +
        "        \"apply\" : \"snippet\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"query\" : {\n" +
        "      \"queries\" : [ {\n" +
        "        \"or-query\" : {\n" +
        "          \"queries\" : [ {\n" +
        "            \"and-query\" : {\n" +
        "              \"queries\" : [ {\n" +
        "                \"word-constraint-query\" : {\n" +
        "                  \"constraint-name\" : \"para\",\n" +
        "                  \"text\" : [ \"Bush\" ]\n" +
        "                }\n" +
        "              }, {\n" +
        "                \"not-query\" : {\n" +
        "                  \"word-constraint-query\" : {\n" +
        "                    \"constraint-name\" : \"para\",\n" +
        "                    \"text\" : [ \"memex\" ]\n" +
        "                  }\n" +
        "                }\n" +
        "              } ]\n" +
        "            }\n" +
        "          }, {\n" +
        "            \"and-query\" : {\n" +
        "              \"queries\" : [ {\n" +
        "                \"value-constraint-query\" : {\n" +
        "                  \"constraint-name\" : \"id\",\n" +
        "                  \"text\" : [ \"0026\" ]\n" +
        "                }\n" +
        "              }, {\n" +
        "                \"term-query\" : {\n" +
        "                  \"text\" : [ \"memex\" ]\n" +
        "                }\n" +
        "              } ]\n" +
        "            }\n" +
        "          } ]\n" +
        "        }\n" +
        "      } ]\n" +
        "    }\n" +
        "  }\n" +
        "}";

    QueryManager queryMgr = client.newQueryManager();

    StringHandle combinedHandle = new StringHandle(combinedCriteria).withFormat(Format.JSON);
    RawCombinedQueryDefinition querydef = queryMgr.newRawCombinedQueryDefinition(combinedHandle);
    String output = queryMgr.search(querydef, new StringHandle()).get();
    System.out.println(output);
    assertTrue(output.contains("(cts:search(fn:collection(), cts:or-query((cts:and-query((cts:field-word-query"));
    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleXMLPayload() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXMLPayload");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");
    FileHandle fileHandle = new FileHandle(file);
    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
    SearchHandle results = queryMgr.search(rw, new SearchHandle());

    for (MatchDocumentSummary result : results.getMatchResults())
    {
      System.out.println(result.getUri() + ": Uri");
      assertEquals( result.getUri(), "/qbe/constraint1.xml");
    }

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleJSONPayload() throws KeyManagementException, NoSuchAlgorithmException, IOException, Exception
  {
    System.out.println("Running testQueryByExampleJSONPayload");

    String[] filenames = { "constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.json");
    FileHandle fileHandle = new FileHandle(file);

    QueryManager queryMgr = client.newQueryManager();
    RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.JSON));
    String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();
    System.out.println(resultDoc);
    assertTrue( resultDoc.contains("/qbe/constraint1.json"));

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleXMLPermission() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXMLPermission");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
    }

    // get the combined query
    try {
      File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe2.xml");

      FileHandle fileHandle = new FileHandle(file);
      QueryManager queryMgr = client.newQueryManager();

      RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
      SearchHandle results = queryMgr.search(rw, new SearchHandle());

      for (MatchDocumentSummary result : results.getMatchResults())
      {
        System.out.println(result.getUri() + ": Uri");
        assertEquals( result.getUri(), "/qbe/constraint1.xml");
      }
    } catch (Exception e) {
      System.out.println("Negative Test Passed of executing nonreadable file");
    }
    // release client
    client.release();

  }

  @Test
  public void testQueryByExampleWrongXML() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXMLPayload");

    String filename = "WrongFormat.xml";

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());
    try {
      // write docs
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");

      // get the combined query
      File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");
      FileHandle fileHandle = new FileHandle(file);
      QueryManager queryMgr = client.newQueryManager();

      RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
      SearchHandle results = queryMgr.search(rw, new SearchHandle());

      for (MatchDocumentSummary result : results.getMatchResults())
      {
        System.out.println(result.getUri() + ": Uri");
        // assertEquals(result.getUri() ,
        // "/qbe/constraint1.xml");
      }
    } catch (FailedRequestException e) {
      System.out.println("Negative test passed as XML with invalid structure gave FailedRequestException ");
    }

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleWrongJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testQueryByExampleJSON");

    String filename = "WrongFormat.json";

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());
    try {
      // write docs
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");

      // get the combined query
      File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.json");

      String qbeQuery = convertFileToString(file);
      StringHandle qbeHandle = new StringHandle(qbeQuery);
      qbeHandle.setFormat(Format.JSON);

      QueryManager queryMgr = client.newQueryManager();

      RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);

      String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();

      System.out.println(resultDoc);

      assertTrue( resultDoc.contains("\"total\":1"));
      assertTrue(
          resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));
    } catch (FailedRequestException e) {
      System.out.println("Negative test passed as JSON with invalid structure gave FailedRequestException ");
    }

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleXMLWrongQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, TransformerException, XpathException
  {
    System.out.println("Running testQueryByExampleXMLWrongQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.xml");
    FileHandle fileHandle = new FileHandle(file);
    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition rw = queryMgr.newRawQueryByExampleDefinition(fileHandle.withFormat(Format.XML));
    SearchHandle results = queryMgr.search(rw, new SearchHandle());

    for (MatchDocumentSummary result : results.getMatchResults())
    {
      System.out.println(result.getUri() + ": Uri");
      assertEquals( result.getUri(), "/qbe/constraint1.xml");
    }
    try {
      File wrongFile = new File("src/test/java/com/marklogic/client/functionaltest/qbe/WrongQbe.xml");
      FileHandle wrongFileHandle = new FileHandle(wrongFile);
      QueryManager newQueryMgr = client.newQueryManager();

      RawQueryByExampleDefinition newRw = newQueryMgr.newRawQueryByExampleDefinition(wrongFileHandle.withFormat(Format.XML));
      SearchHandle newResults = queryMgr.search(newRw, new SearchHandle());

      for (MatchDocumentSummary result : newResults.getMatchResults())
      {
        System.out.println(result.getUri() + ": Uri");
        assertEquals( result.getUri(), "/qbe/constraint1.xml");
      }
    } catch (FailedRequestException e) {
      System.out.println("Negative test passed as Query with improper Xml format gave FailedRequestException ");
    }

    // release client
    client.release();
  }

  @Test
  public void testQueryByExampleJSONWrongQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testQueryByExampleJSONWrongQuery");

    String[] filenames = { "constraint1.json", "constraint2.json", "constraint3.json", "constraint4.json", "constraint5.json" };

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/qbe/", "JSON");
    }

    // get the Correct query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/qbe/qbe1.json");

    String qbeQuery = convertFileToString(file);
    StringHandle qbeHandle = new StringHandle(qbeQuery);
    qbeHandle.setFormat(Format.JSON);

    QueryManager queryMgr = client.newQueryManager();

    RawQueryByExampleDefinition qbyex = queryMgr.newRawQueryByExampleDefinition(qbeHandle);

    String resultDoc = queryMgr.search(qbyex, new StringHandle()).get();

    System.out.println("Result of Correct Query" + resultDoc);

    // assertTrue(
    // resultDoc.contains("\"total\":1"));
    // assertTrue(
    // resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));

    // get the query with Wrong Format

    File wrongFile = new File("src/test/java/com/marklogic/client/functionaltest/qbe/WrongQbe.json");

    String wrongQbeQuery = convertFileToString(wrongFile);
    StringHandle newQbeHandle = new StringHandle(wrongQbeQuery);
    newQbeHandle.setFormat(Format.JSON);

    QueryManager newQueryMgr = client.newQueryManager();

    RawQueryByExampleDefinition newQbyex = newQueryMgr.newRawQueryByExampleDefinition(newQbeHandle);
    try {
      String newResultDoc = newQueryMgr.search(newQbyex, new StringHandle()).get();

      System.out.println("Result of Wrong Query" + newResultDoc);

      assertTrue( resultDoc.contains("\"total\":1"));
      assertTrue(
          resultDoc.contains("\"metadata\":[{\"title\":\"Vannevar Bush\"},{\"id\":11},{\"p\":\"Vannevar Bush wrote an article for The Atlantic Monthly\"},{\"popularity\":5}]"));
    } catch (FailedRequestException e) {
      System.out.println("Negative test passed as Query with improper JSON format gave FailedRequestException ");
    }

    // release client
    client.release();
  }

}

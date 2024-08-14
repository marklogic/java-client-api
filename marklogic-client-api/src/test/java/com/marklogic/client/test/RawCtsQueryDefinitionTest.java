/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

public class RawCtsQueryDefinitionTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    queryMgr = Common.client.newQueryManager();
  }

  @AfterAll
  public static void afterClass() {
    Common.client.newDocumentManager().delete("testRawCtsQueryFromFileHandle.xml");
  }

  @BeforeAll
  public static void setupTestOptions()
    throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException,
           ResourceNotResendableException
  {
    Common.connectRestAdmin();

    QueryOptionsManager queryOptionsManager = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();
    File options = new File("src/test/resources/alerting-options.xml");
    queryOptionsManager.writeOptions("alerts", new FileHandle(options));

    Common.restAdminClient.newServerConfigManager().setServerRequestLogging(true);

    Common.connect();
    JSONDocumentManager jsonDocMgr = Common.client.newJSONDocumentManager();
    jsonDocMgr.write("/basic1.json", new FileHandle(new File("src/test/resources/basic1.json")));
    // write three files for alert tests.
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write("/alert/first.xml", new FileHandle(new File("src/test/resources/alertFirst.xml")));
    docMgr.write("/alert/second.xml", new FileHandle(new File("src/test/resources/alertSecond.xml")));
    docMgr.write("/alert/third.xml", new FileHandle(new File("src/test/resources/alertThird.xml")));
  }

  private static QueryManager queryMgr;

  String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
  String tail = "</search:search>";

  String qtext1 = "<search:qtext>false</search:qtext>";
  String qtext2 = "<search:qtext>favorited:true</search:qtext>";
  String qtext3 = "<search:qtext>leaf3</search:qtext>";
  String qtext4 = "<search:qtext>leaf3 OR jsonValue1</search:qtext>";

  StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);

  String returnQueryOption =
    "<search:options><search:return-query>true</search:return-query></search:options>";
  String optionsString = "<search:options >"
    + "<search:constraint name=\"favorited\">"
    + "  <search:value>"
    + "    <search:element name=\"favorited\" ns=\"\"/>"
    + "  </search:value>"
    + "</search:constraint>"
    + "<search:return-query>true</search:return-query>"
    + "</search:options>";

  private void checkCts(CtsQueryWriteHandle handle, String optionsName, long numMatches) {
    RawCtsQueryDefinition rawCtsQueryDefinition;

    if (optionsName == null) {
      rawCtsQueryDefinition = queryMgr.newRawCtsQueryDefinition(handle);
    } else {
      rawCtsQueryDefinition = queryMgr.newRawCtsQueryDefinition(handle, optionsName);
    }

    SearchHandle results;
    results = queryMgr.search(rawCtsQueryDefinition,
      new SearchHandle());

    assertNotNull(results);

    assertEquals(results.getTotalResults(), numMatches);
    assertFalse(results.getMetrics().getTotalTime() == -1);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertTrue(summaries.length > 0);
    for (MatchDocumentSummary summary : summaries) {
      assertTrue(summary.getMimeType().matches("(application|text)/xml"));
      assertEquals(Format.XML, summary.getFormat());
      MatchLocation[] locations = summary.getMatchLocations();
      for (MatchLocation location : locations) {
        assertNotNull(location.getAllSnippetText());
      }
    }
  }

  private void check(StructureWriteHandle handle, String optionsName) {
    QueryManager queryMgr = Common.client.newQueryManager();
    queryMgr.setPageLength(0);
    RawCombinedQueryDefinition query = queryMgr.newRawCombinedQueryDefinition(handle);
    SearchHandle response = queryMgr.search(query, new SearchHandle());
    String wrappedCtsQuery = response.getQuery(new StringHandle()).get();
    // remove the <search:query> wrapper element
    String ctsQuery = wrappedCtsQuery.replaceAll("</?search:query[^>]*>", "");
    long numMatches = response.getTotalResults();
    checkCts(new StringHandle(ctsQuery), optionsName, numMatches);
  }

  private void check(StructureWriteHandle handle) {
    check(handle, null);
  }

  @Test
  public void testConvertedSearches() throws IOException {
    // Structured Query, No Options
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
    StructuredQueryDefinition t = qb.term("leaf3");
    check(new StringHandle(head + t.serialize() + returnQueryOption + tail));

    // String query no options
    String str = head + qtext1 + returnQueryOption + tail;
    check(new StringHandle(str).withMimetype("application/xml"));

    // String query plus options
    str = head + qtext2 + optionsString + tail;
    check(new StringHandle(str));

    // Structured query plus options
    str = head + t.serialize() + optionsString + tail;
    check(new StringHandle(str));

    // Structured query plus options
    str = head + t.serialize() + optionsString + tail;
    check(new StringHandle(str), "alerts");

    // All three
    str = head + qtext3 + t.serialize() + optionsString + tail;
    check(new StringHandle(str));
  }

  @Test
  public void testRawCtsQueryFromFileHandle() throws Exception {
    Common.client.newDocumentManager().write("testRawCtsQueryFromFileHandle.xml",
      new FileHandle(new File("src/test/resources/constraint5.xml")).withFormat(Format.XML));

    String ctsQueryFilePath = "ctsQuery1.xml";
    String ctsQueryAsString = Common.testFileToString(ctsQueryFilePath);
    File ctsQueryFile = new File(Common.getResourceUri(ctsQueryFilePath));
    FileHandle ctsHandle = new FileHandle(ctsQueryFile);

    QueryDefinition[] queries = new QueryDefinition[] {
      queryMgr.newRawCombinedQueryDefinition(ctsHandle),
      queryMgr.newRawCtsQueryDefinition(ctsHandle),
      queryMgr.newRawCombinedQueryDefinitionAs(Format.XML, ctsQueryFile),
      queryMgr.newRawCtsQueryDefinitionAs(Format.XML, ctsQueryFile),
      queryMgr.newRawCtsQueryDefinition(new InputStreamHandle(new FileInputStream(ctsQueryFile))),
      queryMgr.newRawCtsQueryDefinition(new StringHandle(ctsQueryAsString)),
      queryMgr.newRawCtsQueryDefinition(new DOMHandle(Common.testStringToDocument(ctsQueryAsString)))
    };
    for ( QueryDefinition query : queries ) {
      // create result handle
      DOMHandle resultsHandle = new DOMHandle();
      queryMgr.search(query, resultsHandle);

      // get the result
      Document resultDoc = resultsHandle.get();

      assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
      assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id' or local-name()='highlight'])", resultDoc);
    }
  }

  @Test
  public void testRawCombinedCtsQueryFromFileHandle() throws Exception {
    Common.client.newDocumentManager().write("testRawCtsQueryFromFileHandle.xml",
      new FileHandle(new File("src/test/resources/constraint5.xml")).withFormat(Format.XML));

    FileHandle ctsHandle = new FileHandle(new File("src/test/resources/combinedCtsQuery.xml"));

    QueryDefinition[] queries = new QueryDefinition[] {
      queryMgr.newRawCombinedQueryDefinition(ctsHandle),
      queryMgr.newRawCtsQueryDefinition(ctsHandle),
    };
    for ( QueryDefinition query : queries ) {
      // create result handle
      DOMHandle resultsHandle = new DOMHandle();
      queryMgr.search(query, resultsHandle);

      // get the result
      Document resultDoc = resultsHandle.get();

      assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
      assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id' or local-name()='highlight'])", resultDoc);
    }
  }
}

/*
 * Copyright 2013-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.marklogic.client.impl.HandleAccessor;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.marker.CtsQueryWriteHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.ExtractedItem;
import com.marklogic.client.query.ExtractedResult;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawCtsQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class RawCtsQueryDefinitionTest {
  @BeforeClass
  public static void beforeClass() {
    Common.connect();
    queryMgr = Common.client.newQueryManager();
  }

  @AfterClass
  public static void afterClass() {
    Common.client.newDocumentManager().delete("testRawCtsQueryFromFileHandle.xml");
  }

  @BeforeClass
  public static void setupTestOptions()
    throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException,
           ResourceNotResendableException
  {
    Common.connectAdmin();

    QueryOptionsManager queryOptionsManager = Common.adminClient.newServerConfigManager().newQueryOptionsManager();
    File options = new File("src/test/resources/alerting-options.xml");
    queryOptionsManager.writeOptions("alerts", new FileHandle(options));

    Common.adminClient.newServerConfigManager().setServerRequestLogging(true);

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
      assertTrue("Mime type of document",
        summary.getMimeType().matches("(application|text)/xml"));
      assertEquals("Format of document", Format.XML, summary.getFormat());
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

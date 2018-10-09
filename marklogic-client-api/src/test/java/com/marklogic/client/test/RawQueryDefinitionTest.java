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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.ExtractedItem;
import com.marklogic.client.query.ExtractedResult;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.Tuple;
import com.marklogic.client.query.ValuesDefinition;

public class RawQueryDefinitionTest {
  @BeforeClass
  public static void beforeClass() {
    Common.connect();
    queryMgr = Common.client.newQueryManager();
  }

  @AfterClass
  public static void afterClass() {
    Common.client.newDocumentManager().delete("test_issue581_RawStructuredQueryFromFileHandle.xml");
  }

  @BeforeClass
  public static void setupTestOptions()
    throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
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

  String optionsString = "<search:options >"
    + "<search:constraint name=\"favorited\">" + "<search:value>"
    + "<search:element name=\"favorited\" ns=\"\"/>"
    + "</search:value>" + "</search:constraint>"
    + "<search:search-option>relevance-trace</search:search-option>"
    + "</search:options>";

  String lexiconOptions = "<options xmlns=\"http://marklogic.com/appservices/search\">"
    + "<values name=\"grandchild\">"
    + "<range type=\"xs:string\">"
    + "<element ns=\"\" name=\"grandchild\"/>"
    + "</range>"
    + "<values-option>limit=2</values-option>"
    + "</values>"
    + "<tuples name=\"co\">"
    + "<range type=\"xs:double\">"
    + "<element ns=\"\" name=\"double\"/>"
    + "</range>"
    + "<range type=\"xs:int\">"
    + "<element ns=\"\" name=\"int\"/>"
    + "</range>"
    + "</tuples>"
    + "<tuples name=\"n-way\">"
    + "<range type=\"xs:double\">"
    + "<element ns=\"\" name=\"double\"/>"
    + "</range>"
    + "<range type=\"xs:int\">"
    + "<element ns=\"\" name=\"int\"/>"
    + "</range>"
    + "<range type=\"xs:string\">"
    + "<element ns=\"\" name=\"string\"/>"
    + "</range>"
    + "<values-option>ascending</values-option>"
    + "</tuples>"
    + "<return-metrics>true</return-metrics>"
    + "<return-values>true</return-values>" + "</options>";

  private void check(StructureWriteHandle handle, String optionsName) {
    RawCombinedQueryDefinition rawCombinedQueryDefinition;

    if (optionsName == null) {
      rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(handle);
    } else {
      rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(handle, optionsName);
    }
    // StringHandle stringResults = null;
    // stringResults = queryMgr.search(rawCombinedQueryDefinition,
    // new StringHandle());
    // System.out.println(stringResults.get());

    SearchHandle results;
    results = queryMgr.search(rawCombinedQueryDefinition,
      new SearchHandle());

    checkResults(results);
  }

  private void checkResults(SearchHandle results) {
    assertNotNull(results);

    assertFalse(results.getMetrics().getTotalTime() == -1);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertTrue(summaries.length > 0);
    for (MatchDocumentSummary summary : summaries) {
      assertTrue("Mime type of document",
        summary.getMimeType().matches("(application|text)/xml"));
      assertEquals("Format of document", Format.XML, summary.getFormat());
      Document relevanceTrace = summary.getRelevanceInfo();
      if (relevanceTrace != null) {
        assertEquals(relevanceTrace.getDocumentElement().getLocalName(),"relevance-info");
      }
      MatchLocation[] locations = summary.getMatchLocations();
      for (MatchLocation location : locations) {
        assertNotNull(location.getAllSnippetText());
      }
    }
  }

  private void check(StructureWriteHandle handle) {
    check(handle, null);
  }

  @Test
  public void testCombinedSearches() throws IOException {
    // Structured Query, No Options
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(null);
    StructuredQueryDefinition t = qb.term("leaf3");
    check(new StringHandle(head + t.serialize() + tail));

    // String query no options
    String str = head + qtext1 + tail;
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
  public void testFailedSearch() throws IOException {
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder();
    StructuredQueryDefinition t = qb.term("criteriaThatShouldNotMatchAnyDocument");

    RawCombinedQueryDefinition queryDef = queryMgr.newRawCombinedQueryDefinition(
      new StringHandle(head + t.serialize() + tail)
        .withMimetype("application/xml")
    );

    SearchHandle results = queryMgr.search(queryDef, new SearchHandle());
    assertNotNull(results);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertTrue(summaries == null || summaries.length == 0);

    StringHandle criteria = new StringHandle().withFormat(Format.XML);
    criteria.set("<q:query xmlns:q='" + RawQueryByExampleDefinition.QBE_NS
      + "'>"
      + "<q:word>criteriaThatShouldNotMatchAnyDocument</q:word>"
      + "</q:query>");

    RawQueryByExampleDefinition qbe = queryMgr.newRawQueryByExampleDefinition(criteria);

    results = queryMgr.search(qbe, new SearchHandle());
    assertNotNull(results);

    summaries = results.getMatchResults();
    assertTrue(summaries == null || summaries.length == 0);
  }

  @Test
  public void testByExampleSearch() throws IOException, SAXException, XpathException {
    StringHandle criteria = new StringHandle().withFormat(Format.XML);
    criteria.set("<q:query xmlns:q='" + RawQueryByExampleDefinition.QBE_NS
      + "'>" + "<favorited>true</favorited>" + "</q:query>");

    RawQueryByExampleDefinition qbe = queryMgr
      .newRawQueryByExampleDefinition(criteria);

    SearchHandle results = queryMgr.search(qbe, new SearchHandle());

    checkResults(results);

    String output = queryMgr.validate(qbe, new StringHandle()).get();
    assertNotNull("Empty XML validation", output);
    assertXMLEqual("Failed to validate QBE", output,
      "<q:valid-query xmlns:q=\"http://marklogic.com/appservices/querybyexample\"/>");

    output = queryMgr.convert(qbe, new StringHandle()).get();
    assertNotNull("Empty XML conversion", output);
    assertXpathEvaluatesTo(
      "favorited",
      "string(/*[local-name()='search']/*[local-name()='query']/*[local-name()='value-query']/*[local-name()='element']/@name)",
      output);
    assertXpathEvaluatesTo(
      "true",
      "string(/*[local-name()='search']/*[local-name()='query']/*[local-name()='value-query']/*[local-name()='text'])",
      output);

    criteria.withFormat(Format.JSON).set(
      "{"+
        "\"$format\":\"xml\","+
        "\"$query\":{\"favorited\":\"true\"}"+
        "}"
    );
    output = queryMgr.search(qbe, new StringHandle().withFormat(Format.JSON)).get();
    assertNotNull("Empty JSON output", output);
    assertTrue("Output without a match",
      output.contains("\"results\":[{\"index\":1,"));
  }

  @Test
  public void testValues() {
    String str = head + lexiconOptions + tail;
    RawCombinedQueryDefinition rawCombinedQueryDefinition;

    rawCombinedQueryDefinition = queryMgr
      .newRawCombinedQueryDefinition(new StringHandle(str).withMimetype("application/xml"));

    StringHandle stringResults = null;
    ValuesDefinition vdef = queryMgr.newValuesDefinition("grandchild");

    vdef.setQueryDefinition(rawCombinedQueryDefinition);

    stringResults = queryMgr.tuples(vdef, new StringHandle());
    System.out.println(stringResults.get());

    ValuesHandle valuesResults = queryMgr.values(vdef,
      new ValuesHandle());

    assertFalse(valuesResults.getMetrics().getTotalTime() == -1);

    CountedDistinctValue[] values = valuesResults.getValues();
    assertNotNull(values);
  }

  @Test
  public void testTuples() {
    String str = head + lexiconOptions + tail;
    RawCombinedQueryDefinition rawCombinedQueryDefinition;

    rawCombinedQueryDefinition = queryMgr
      .newRawCombinedQueryDefinition(new StringHandle(str).withMimetype("application/xml"));

    StringHandle stringResults = null;
    ValuesDefinition vdef = queryMgr.newValuesDefinition("n-way");

    vdef.setQueryDefinition(rawCombinedQueryDefinition);

    stringResults = queryMgr.tuples(vdef, new StringHandle());
    System.out.println(stringResults.get());

    TuplesHandle tuplesResults = queryMgr.tuples(vdef,
      new TuplesHandle());
    Tuple[] tuples = tuplesResults.getTuples();
    assertNotNull(tuples);
  }

  @Test
  public void testExtractDocumentData() throws Exception {
    String options =
      "<search:options>" +
        "<search:extract-document-data>" +
        "<search:extract-path>/root/child</search:extract-path>" +
        "<search:extract-path>/a/*</search:extract-path>" +
        "</search:extract-document-data>" +
        "</search:options>";
    // test XML response with extracted XML and JSON matches
    String combinedSearch = head + qtext4 + options + tail;
    RawCombinedQueryDefinition rawCombinedQueryDefinition =
      queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
    SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertEquals(2, summaries.length);
    for (MatchDocumentSummary summary : summaries) {
      ExtractedResult extracted = summary.getExtracted();
      if ( Format.XML == summary.getFormat() ) {
        // we don't test for kind because it isn't sent in this case
        assertEquals(3, extracted.size());
        Document item1 = extracted.next().getAs(Document.class);
        assertEquals("1", item1.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
        Document item2 = extracted.next().getAs(Document.class);
        assertEquals("2", item2.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
        Document item3 = extracted.next().getAs(Document.class);
        assertEquals("3", item3.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
        continue;
      } else if ( Format.JSON == summary.getFormat() ) {
        // we don't test for kind because it isn't sent in this case
        assertEquals(3, extracted.size());
        for ( ExtractedItem item : extracted ) {
          String stringJsonItem = item.getAs(String.class);
          JsonNode nodeJsonItem = item.getAs(JsonNode.class);
          if ( nodeJsonItem.has("b1") ) {
            assertEquals("{\"b1\":{\"c\":\"jsonValue1\"}}", stringJsonItem);
            continue;
          } else if ( nodeJsonItem.has("b2") ) {
            assertTrue(stringJsonItem.matches("\\{\"b2\":\"b2 val[12]\"}"));
            continue;
          }
          fail("unexpected extracted item:" + stringJsonItem);
        }
        continue;
      }
      fail("unexpected search result:" + summary.getUri());
    }

    // test JSON response with extracted XML and JSON matches
    JsonNode jsonResults = queryMgr.search(rawCombinedQueryDefinition, new JacksonHandle()).get();
    JsonNode jsonSummaries = jsonResults.get("results");
    assertNotNull(jsonSummaries);
    assertEquals(2, jsonSummaries.size());
    for (int i=0; i < jsonSummaries.size(); i++ ) {
      JsonNode summary = jsonSummaries.get(i);
      String format = summary.get("format").textValue();
      String docPath = summary.get("path").textValue();
      assertNotNull(docPath);
      JsonNode extracted = summary.get("extracted");
      if ( "xml".equals(format) ) {
        if ( docPath.contains("/sample/first.xml") ) {
          JsonNode extractedItems = extracted.path("content");
          assertEquals(3, extractedItems.size());
          assertEquals(3, extractedItems.size());
          Document item1 = parseXml(extractedItems.get(0).textValue());
          assertEquals("1", item1.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
          Document item2 = parseXml(extractedItems.get(1).textValue());
          assertEquals("2", item2.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
          Document item3 = parseXml(extractedItems.get(2).textValue());
          assertEquals("3", item3.getFirstChild().getAttributes().getNamedItem("id").getNodeValue());
          continue;
        }
      } else if ( "json".equals(format) ) {
        if ( docPath.contains("/basic1.json") ) {
          JsonNode items = extracted.get("content");
          assertNotNull(items);
          assertEquals(3, items.size());
          assertTrue(items.get(0).has("b1"));
          assertTrue(items.get(1).has("b2"));
          assertTrue(items.get(2).has("b2"));
          continue;
        }
      }
      fail("unexpected search result:" + summary);
    }

    // test XML response with full document XML and JSON matches
    options =
      "<search:options>" +
        "<search:extract-document-data selected=\"all\"/>" +
        "</search:options>";
    combinedSearch = head + qtext4 + options + tail;
    rawCombinedQueryDefinition =
      queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
    results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertEquals(2, summaries.length);
    for (MatchDocumentSummary summary : summaries) {
      ExtractedResult extracted = summary.getExtracted();
      if ( Format.XML == summary.getFormat() ) {
        assertEquals("element", extracted.getKind());
        assertEquals(1, extracted.size());
        Document root = extracted.next().getAs(Document.class);
        assertEquals("root", root.getFirstChild().getNodeName());
        NodeList children = root.getFirstChild().getChildNodes();
        assertEquals(3, children.getLength());
        Node item1 = children.item(0);
        assertEquals("1", item1.getAttributes().getNamedItem("id").getNodeValue());
        Node item2 = children.item(1);
        assertEquals("2", item2.getAttributes().getNamedItem("id").getNodeValue());
        Node item3 = children.item(2);
        assertEquals("3", item3.getAttributes().getNamedItem("id").getNodeValue());
        continue;
      } else if ( Format.JSON == summary.getFormat() ) {
        assertEquals("object", extracted.getKind());
        String jsonDocument = extracted.next().getAs(String.class);
        assertEquals("{\"a\":{\"b1\":{\"c\":\"jsonValue1\"}, \"b2\":[\"b2 val1\", \"b2 val2\"]}}",
          jsonDocument);
        continue;
      }
      fail("unexpected search result:" + summary.getUri());
    }

    // test JSON response with full document XML matches
    jsonResults = queryMgr.search(rawCombinedQueryDefinition, new JacksonHandle()).get();
    jsonSummaries = jsonResults.get("results");
    assertNotNull(jsonSummaries);
    assertEquals(2, jsonSummaries.size());
    for (int i=0; i < jsonSummaries.size(); i++ ) {
      JsonNode summary = jsonSummaries.get(i);
      String format = summary.get("format").textValue();
      String docPath = summary.get("path").textValue();
      assertNotNull(docPath);
      JsonNode extracted = summary.get("extracted");
      if ( "xml".equals(format) ) {
        if ( docPath.contains("/sample/first.xml") ) {
          assertEquals("fn:doc(\"/sample/first.xml\")", docPath);
          JsonNode extractedItems = extracted.path("content");
          assertEquals(1, extractedItems.size());
          Document root = parseXml(extractedItems.get(0).textValue());
          assertEquals("root", root.getFirstChild().getNodeName());
          NodeList children = root.getFirstChild().getChildNodes();
          assertEquals(3, children.getLength());
          Node item1 = children.item(0);
          assertEquals("1", item1.getAttributes().getNamedItem("id").getNodeValue());
          Node item2 = children.item(1);
          assertEquals("2", item2.getAttributes().getNamedItem("id").getNodeValue());
          Node item3 = children.item(2);
          assertEquals("3", item3.getAttributes().getNamedItem("id").getNodeValue());
          continue;
        }
      } else if ( "json".equals(format) ) {
        if ( docPath.contains("/basic1.json") ) {
          assertEquals("fn:doc(\"/basic1.json\")", docPath);
          assertEquals("object", extracted.get("kind").textValue());
          JsonNode items = extracted.get("content");
          assertNotNull(items);
          assertEquals(1, items.size());
          assertTrue(items.path(0).has("a"));
          assertTrue(items.path(0).path("a").has("b1"));
          assertTrue(items.path(0).path("a").path("b1").has("c"));
          continue;
        }
      }
      fail("unexpected search result:" + summary);
    }

    // test XML response with XML and JSON document matches with path that does not match
    options =
      "<search:options>" +
        "<search:extract-document-data>" +
        "<search:extract-path>/somethingThatShouldNeverMatch</search:extract-path>" +
        "</search:extract-document-data>" +
        "</search:options>";
    combinedSearch = head + qtext4 + options + tail;
    rawCombinedQueryDefinition =
      queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
    results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
    summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertEquals(2, summaries.length);
    for (MatchDocumentSummary summary : summaries) {
      ExtractedResult extracted = summary.getExtracted();
      assertTrue(extracted.isEmpty());
    }

    // test JSON response with XML and JSON document matches with path that does not match
    jsonResults = queryMgr.search(rawCombinedQueryDefinition, new JacksonHandle()).get();
    jsonSummaries = jsonResults.get("results");
    assertNotNull(jsonSummaries);
    assertEquals(2, jsonSummaries.size());
    for (int i=0; i < jsonSummaries.size(); i++ ) {
      JsonNode summary = jsonSummaries.get(i);
      JsonNode extractedNone = summary.get("extracted-none");
      assertNotNull(extractedNone);
      assertEquals(0, extractedNone.size());
    }
  }

  @Test
  public void test_issue581_RawStructuredQueryFromFileHandle() throws Exception {
    Common.client.newDocumentManager().write("test_issue581_RawStructuredQueryFromFileHandle.xml",
      new FileHandle(new File("src/test/resources/constraint5.xml")).withFormat(Format.XML));

    // get the combined query
    File file = new File("src/test/resources/combinedQueryOption.xml");

    // create a handle for the search criteria
    FileHandle rawHandle = new FileHandle(file);

    QueryManager queryMgr = Common.client.newQueryManager();

    // create a search definition based on the handle
    RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle);

    // create result handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
  }

  private static Document parseXml(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new InputSource(new StringReader(xml)));
    return document;
  }
}

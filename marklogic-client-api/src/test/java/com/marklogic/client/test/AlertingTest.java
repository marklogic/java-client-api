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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import com.marklogic.client.DatabaseClient;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.alerting.RuleDefinition;
import com.marklogic.client.alerting.RuleDefinition.RuleMetadata;
import com.marklogic.client.alerting.RuleDefinitionList;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class AlertingTest {

  private static RuleManager ruleManager;
  private static QueryOptionsManager queryOptionsManager;
  private static QueryManager queryManager;
  private static TransformExtensionsManager transformManager;
  private static DatabaseClient adminClient = Common.connectAdmin();
  private static final String RULE_NAME_WRITE_RULE_AS_TEST = "writeRuleAsTest";

  @AfterClass
  public static void teardown()
    throws ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    XMLDocumentManager docMgr = adminClient.newXMLDocumentManager();
    docMgr.delete("/alert/first.xml");
    docMgr.delete("/alert/second.xml");
    docMgr.delete("/alert/third.xml");
    teardownMatchRules();

    transformManager = adminClient.newServerConfigManager().newTransformExtensionsManager();
    transformManager.deleteTransform("ruleTransform");
  }

  @BeforeClass
  public static void setup()
    throws FileNotFoundException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    XMLUnit.setIgnoreWhitespace(true);


    queryOptionsManager = adminClient.newServerConfigManager()
      .newQueryOptionsManager();
    File options = new File("src/test/resources/alerting-options.xml");
    queryOptionsManager.writeOptions("alerts", new FileHandle(options));

    queryManager = adminClient.newQueryManager();

    transformManager = adminClient.newServerConfigManager().newTransformExtensionsManager();

    File ruleTransform = new File("src/test/resources/rule-transform.xqy");
    transformManager.writeXQueryTransform("ruleTransform", new FileHandle(ruleTransform));

    adminClient.newServerConfigManager().setServerRequestLogging(true);
    Common.connect();

    // write three files for alert tests.
    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
    docMgr.write("/alert/first.xml", new FileHandle(new File(
      "src/test/resources/alertFirst.xml")));
    docMgr.write("/alert/second.xml", new FileHandle(new File(
      "src/test/resources/alertSecond.xml")));
    docMgr.write("/alert/third.xml", new FileHandle(new File(
      "src/test/resources/alertThird.xml")));

    ruleManager = Common.client.newRuleManager();
    setupMatchRules();
  }

  @Test
  public void testRuleDefinitions()
    throws ParserConfigurationException, SAXException, IOException, ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    RuleDefinition definition = new RuleDefinition("javatestrule",
      "Rule for testing java");

    String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
    String tail = "</search:search>";

    String qtext1 = "<search:qtext>favorited:true</search:qtext>";

    StructuredQueryBuilder qb = queryManager.newStructuredQueryBuilder();

    String structuredString = qb.valueConstraint("name", "one").serialize();

    String ruleOptions = "<search:options >"
      + "<search:constraint name=\"favorited\">" + "<search:value>"
      + "<search:element name=\"favorited\" ns=\"\"/>"
      + "</search:value>" + "</search:constraint>"
      + "</search:options>";

    StringHandle textQuery = new StringHandle(head + qtext1 + tail);
    definition.importQueryDefinition(textQuery);

    StringHandle qdefCheck = definition
      .exportQueryDefinition(new StringHandle());
    assertEquals(head + qtext1 + tail, qdefCheck.get());

    RuleMetadata metadata = definition.getMetadata();
    metadata.put(new QName("dataelem1"), "Here's a value in metadata");
    metadata.put(new QName("dataelem2"), 10.2);

    // one. no options, string query.
    ruleManager.writeRule(definition);

    // fetch the rule
    RuleDefinition roundTripped = ruleManager.readRule("javatestrule",
      new RuleDefinition());
    assertEquals("javatestrule", roundTripped.getName());
    assertEquals("Rule for testing java", roundTripped.getDescription());

    // test exporting XML
    BytesHandle exportedDef = roundTripped
      .exportQueryDefinition(new BytesHandle());

    assertXMLEqual(
      "Search element round-tripped",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\"><search:qtext>favorited:true</search:qtext></search:search>",
      new String(exportedDef.get()));

    FileHandle fileExport = roundTripped
      .exportQueryDefinition(new FileHandle(new File(
        "target/fileout.xml")));

    StringBuilder sb;
    try (BufferedReader reader = new BufferedReader(new FileReader(fileExport.get()))) {
      String line = null;
      sb = new StringBuilder();
      String ls = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append(ls);
      }
    }

    String fileContents = sb.toString();

    assertXMLEqual(
      "Search element round-tripped",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\"><search:qtext>favorited:true</search:qtext></search:search>",
      fileContents);

    RuleMetadata metadataReturned = roundTripped.getMetadata();
    assertEquals(metadata.get(new QName("dataelem1")),
      metadataReturned.get(new QName("dataelem1")));
    assertEquals(metadata.get(new QName("dataelem2")),
      metadataReturned.get(new QName("dataelem2")));

    // two. with options string query.
    StringHandle rawDefWithOptions = new StringHandle(head + qtext1
      + ruleOptions + tail);
    definition.importQueryDefinition(rawDefWithOptions);
    ruleManager.writeRule(definition);
    roundTripped = ruleManager.readRule("javatestrule",
      new RuleDefinition());
    assertEquals("javatestrule", roundTripped.getName());
    assertEquals("Rule for testing java", roundTripped.getDescription());

    assertXMLEqual(
      "Search element round-tripped - string query and options",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\"><search:qtext>favorited:true</search:qtext></search:search>",
      new String(exportedDef.get()));

    // three. structured query with options.

    StringHandle structuredWithOptions = new StringHandle(head
      + structuredString + ruleOptions + tail);
    definition.importQueryDefinition(structuredWithOptions);
    ruleManager.writeRule(definition);
    roundTripped = ruleManager.readRule("javatestrule",
      new RuleDefinition());
    assertEquals("javatestrule", roundTripped.getName());
    assertEquals("Rule for testing java", roundTripped.getDescription());

    assertXMLEqual(
      "Search element round-tripped - structured query and options",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\"><search:qtext>favorited:true</search:qtext></search:search>",
      new String(exportedDef.get()));
    ruleManager.delete("javatestrule");
  }

  @Test
  public void testXMLRuleDefinitions()
    throws SAXException, IOException, ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    File ruleFile = new File("src/test/resources/rule1.xml");
    FileHandle ruleHandle = new FileHandle(ruleFile);
    ruleManager.writeRule("javatestrule", ruleHandle);

    assertTrue(ruleManager.exists("javatestrule"));
    RuleDefinition def = ruleManager.readRule("javatestrule",
      new RuleDefinition());
    assertEquals("javatestrule", def.getName());
    assertXMLEqual(
      "Search element round-tripped - structured query and options",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">"
        + "<search:qtext>favorited:true</search:qtext>"
        + "<search:options xmlns:search=\"http://marklogic.com/appservices/search\">"
        + "<search:constraint name=\"favorited\">"
        + "<search:value>"
        + "<search:element ns=\"\" name=\"favorited\" />"
        + "</search:value>" + "</search:constraint>"
        + "</search:options>" + "</search:search>", new String(
        def.exportQueryDefinition(new BytesHandle()).get()));
    ruleManager.delete("javatestrule");

  }

  @Test
  public void testXMLRuleDefinitionsWithStructuredQuery()
    throws SAXException, IOException, ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    RuleDefinition defWithImport = new RuleDefinition();
    defWithImport.setName("javatestrule2");
    File ruleFile = new File("src/test/resources/structured-query.xml");
    FileHandle queryHandle = new FileHandle(ruleFile);
    defWithImport.importQueryDefinition(queryHandle);
    ruleManager.writeRule(defWithImport);

    assertTrue(ruleManager.exists("javatestrule2"));
    RuleDefinition def = ruleManager.readRule("javatestrule2",
      new RuleDefinition());
    assertEquals("javatestrule2", def.getName());
    assertXMLEqual(
      "Search element round-tripped - structured query and options",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">"
        + "<search:query>"
        + "<search:term-query><search:text>foo</search:text></search:term-query>"
        + "</search:query>"
        + "</search:search>", new String(
        def.exportQueryDefinition(new BytesHandle()).get()));
    ruleManager.delete("javatestrule");

  }

  @Test
  public void testJSONRuleDefinitions()
    throws SAXException, IOException, ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    File ruleFile = new File("src/test/resources/rule1.json");
    FileHandle ruleHandle = new FileHandle(ruleFile);
    ruleHandle.setFormat(Format.JSON);
    ruleManager.writeRule("javatestrule", ruleHandle);

    assertTrue(ruleManager.exists("javatestrule"));
    RuleDefinition def = ruleManager.readRule("javatestrule",
      new RuleDefinition());
    assertEquals("javatestrule", def.getName());
    assertXMLEqual(
      "Search element round-tripped - structured query and options",
      "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">"
        + "<search:qtext>favorited:true</search:qtext>"
        + "<search:options xmlns:search=\"http://marklogic.com/appservices/search\">"
        + "<search:constraint name=\"favorited\">"
        + "<search:value>"
        + "<search:element ns=\"\" name=\"favorited\" />"
        + "</search:value>" + "</search:constraint>"
        + "</search:options>" + "</search:search>", new String(
        def.exportQueryDefinition(new BytesHandle()).get()));

    BytesHandle bHandle = ruleManager.readRule("javatestrule",
      new BytesHandle().withFormat(Format.JSON));
    assertEquals(
      "{\"rule\":{\"name\":\"javatestrule\", \"description\":\"rule to demonstrate REST alerting\", \"search\":{\"qtext\":[\"favorited:true\"], \"options\":{\"constraint\":[{\"name\":\"favorited\", \"value\":{\"element\":{\"ns\":\"\", \"name\":\"favorited\"}}}]}}, \"rule-metadata\":null}}",
      new String(bHandle.get()));

    ruleManager.delete("javatestrule");

  }

  private static void setupMatchRules()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    RuleDefinition definition = new RuleDefinition("favorites",
      "Rule for testing favorited:true");
    String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
    String qtext1 = "<search:qtext>favorited:true</search:qtext>";
    String qtext2 = "<search:qtext>favorited:false</search:qtext>";
    String tail = "</search:search>";
    String ruleOptions = "<search:options >"
      + "<search:constraint name=\"favorited\">" + "<search:value>"
      + "<search:element name=\"favorited\" ns=\"\"/>"
      + "</search:value>" + "</search:constraint>"
      + "</search:options>";
    StringHandle textQuery = new StringHandle(head + qtext1 + ruleOptions
      + tail);
    definition.importQueryDefinition(textQuery);
    ruleManager.writeRule(definition);

    textQuery = new StringHandle(head + qtext2 + ruleOptions + tail);
    definition.importQueryDefinition(textQuery);
    definition.setName("notfavorited");
    definition.setDescription("Rule for testing favorited:false");
    ruleManager.writeRule(definition);

  }

  private static void teardownMatchRules()
    throws ForbiddenUserException, FailedRequestException
  {
    ruleManager.delete("notfavorited");
    ruleManager.delete("favorites");
    ruleManager.delete(RULE_NAME_WRITE_RULE_AS_TEST);
  }

  @Test
  public void testMatchGetDocumentUri() {

    String[] docs = new String[] { "/alert/second.xml" };
    String[] candidates = new String[] { "notfavorited" };

    RuleDefinitionList answer = ruleManager.match(docs,
      new RuleDefinitionList());

    assertEquals(
      "One answer for first match scenario, favorite against all rules",
      1, answer.size());

    RuleDefinition ruleMatch = answer.iterator().next();
    assertEquals("favorites", ruleMatch.getName());

    answer = ruleManager.match(docs, candidates, answer, null);
    assertEquals(
      "Zero answers for second match scenario, favorites against false rule ",
      0, answer.size());

    docs = new String[] { "/alert/first.xml", "/alert/third.xml" };

    answer = ruleManager.match(docs, answer);
    assertEquals("One answer for first match scenario", 1, answer.size());

    RuleDefinition match = answer.iterator().next();
    assertEquals("notfavorited", match.getName());

  }

  @Test
  public void testMatchGetQuery() {

    StringQueryDefinition qtext = queryManager
      .newStringDefinition("alerts");
    qtext.setCriteria("favorited:true");

    RuleDefinitionList answer = ruleManager.match(qtext,
      new RuleDefinitionList());
    assertEquals("One answer", 1, answer.size());

    qtext.setCriteria("favorited:false");
    answer = ruleManager.match(qtext, 1L, 10L, new String[] { "favorites",
      "notfavorited" }, answer);
    assertEquals("One answer", answer.size(), 1);
    assertEquals("Right answer", "notfavorited", answer.iterator().next()
      .getName());
    answer = ruleManager.match(qtext, 1L, 0L, new String[] { "favorites",
      "notfavorited" }, answer);
    assertEquals("Zero answers (pageLength 0)", answer.size(), 0);
    answer = ruleManager.match(qtext, 3L, QueryManager.DEFAULT_PAGE_LENGTH, new String[] { "favorites",
      "notfavorited" }, answer);
    assertEquals("Zero answers (default pageLength, but start beyond result size)", answer.size(), 0);
  }

  @Test
  public void testMatchPostQuery() throws SAXException, IOException {
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition structuredQuery;
    structuredQuery = qb.rangeConstraint("favorited", Operator.EQ, "true");

    DOMHandle answer = ruleManager.match(structuredQuery, new DOMHandle());

    Document doc = answer.get();
    NodeList nl = doc.getElementsByTagNameNS(
      "http://marklogic.com/rest-api", "name");
    assertEquals(2, nl.getLength());

    answer = ruleManager.match(structuredQuery, 1, QueryManager.DEFAULT_PAGE_LENGTH,
      new String[] { "favorites" }, new DOMHandle());

    doc = answer.get();
    nl = doc.getElementsByTagNameNS("http://marklogic.com/rest-api", "name");
    assertEquals(1, nl.getLength());

  }

  @Test
  public void testPostDocumentWithoutFormat() throws SAXException, IOException {
    StringHandle stringHandle = new StringHandle("<search xmlns=\"http://marklogic.com/appservices/search\"><qtext>true</qtext></search>");

    DOMHandle answer = ruleManager.match(stringHandle, new DOMHandle());

    Document doc = answer.get();
    NodeList nl = doc.getElementsByTagNameNS(
      "http://marklogic.com/rest-api", "name");
    assertEquals(2, nl.getLength());

  }


  @Test
  public void testRuleMatchTransform() {
    StructuredQueryBuilder qb = new StructuredQueryBuilder();
    StructuredQueryDefinition structuredQuery;
    structuredQuery = qb.rangeConstraint("favorited", Operator.EQ, "true");

    ServerTransform transform = new ServerTransform("ruleTransform");

    DOMHandle answer = ruleManager.match(structuredQuery, 0L, QueryManager.DEFAULT_PAGE_LENGTH, new String[] {}, new DOMHandle(), transform);

    Document doc = answer.get();
    NodeList nl = doc.getElementsByTagNameNS(
      "", "transformed-name");
    assertEquals(2, nl.getLength());
  }

  @Test
  public void testMatchPostDocument() {
    String docToMatch1 = "<favorited>true</favorited>";
    String docToMatch2 = "<favorited>false</favorited>";

    RuleDefinitionList ans1 = ruleManager.match(new StringHandle(
      docToMatch1).withFormat(Format.XML), new RuleDefinitionList());
    assertEquals("doc match 1 count", 1, ans1.size());
    assertEquals("doc match 1", "favorites", ans1.iterator().next()
      .getName());

    RuleDefinitionList ans2 = ruleManager.match(new StringHandle(
      docToMatch2).withFormat(Format.XML), new RuleDefinitionList());
    assertEquals("doc match 2 count", 1, ans2.size());
    assertEquals("doc match 2", "notfavorited", ans2.iterator().next()
      .getName());

  }

  @Test
  public void testWriteRuleAs() {
    RuleDefinition definition = new RuleDefinition("original" + RULE_NAME_WRITE_RULE_AS_TEST,
                "test to verify that writeRuleAs does not result in stackoverflow");
    String query =
            "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">" +
            "<search:qtext>favorited:true</search:qtext>" +
            "</search:search>";
    StringHandle textQuery = new StringHandle(query).withFormat(Format.XML);
    definition.importQueryDefinition(textQuery);

    ruleManager.writeRuleAs(RULE_NAME_WRITE_RULE_AS_TEST, definition);
    assertTrue("Rule was written with the name provided", ruleManager.exists(RULE_NAME_WRITE_RULE_AS_TEST));
  }
}

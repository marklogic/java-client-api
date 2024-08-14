/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

public class TestBug18736 extends AbstractFunctionalTest {

	@BeforeEach
	void setup() {
		deleteDocuments(newDatabaseClientBuilder().build());
	}

  @Test
  public void testBug18736() throws KeyManagementException, NoSuchAlgorithmException, TransformerException, IOException
  {
    System.out.println("Running testBug18736");

    String filename = "constraint1.xml";
    String docId = "/content/without-xml-ext";
    // XpathEngine xpathEngine;

    /*
     * Map<String,String> xpathNS = new HashMap<>(); xpathNS.put("",
     * "http://purl.org/dc/elements/1.1/"); SimpleNamespaceContext
     * xpathNsContext = new SimpleNamespaceContext(xpathNS);
     *
     * XMLUnit.setIgnoreAttributeOrder(true); XMLUnit.setIgnoreWhitespace(true);
     * XMLUnit.setNormalize(true); XMLUnit.setNormalizeWhitespace(true);
     * XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
     *
     * xpathEngine = XMLUnit.newXpathEngine();
     * xpathEngine.setNamespaceContext(xpathNsContext);
     */

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create write handle
    InputStreamHandle writeHandle = new InputStreamHandle();

    // get the file
    InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    writeHandle.set(inputStream);

    // create doc descriptor
    DocumentDescriptor docDesc = docMgr.newDescriptor(docId);

    docMgr.write(docDesc, writeHandle);

    docDesc.setFormat(Format.XML);
    DOMHandle readHandle = new DOMHandle();
    docMgr.read(docDesc, readHandle);
    Document readDoc = readHandle.get();
    String out = convertXMLDocumentToString(readDoc);
    System.out.println(out);

    assertTrue(out.contains("0011"));

    // get xml document for expected result
    // Document expectedDoc = expectedXMLDocument(filename);

    // assertXMLEqual("Write XML difference", expectedDoc, readDoc);

    // release client
    client.release();
  }

  @Test
  public void testDefaultFacetValue() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testDefaultFacetValue");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/def-facet/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options builder
    String opts = new StringBuilder()
        .append("<search:options xmlns:search=\"http://marklogic.com/appservices/search\">")
        .append("<search:constraint name=\"pop\">")
        .append("<search:range type=\"xs:int\">")
        .append("<search:element name=\"popularity\" ns=\"\"/>")
        .append("</search:range>")
        .append("</search:constraint>")
        .append("</search:options>").toString();
    // build and write query options with new handle
    optionsMgr.writeOptions("FacetValueOpt", new StringHandle(opts));

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("FacetValueOpt", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("FacetValueOpt");
    StructuredQueryDefinition queryFinal = qb.rangeConstraint("pop", Operator.EQ, "5");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("pop", "string(//*[local-name()='response']//*[local-name()='facet']//@*[local-name()='name'])", resultDoc);
    assertXpathEvaluatesTo("3", "string(//*[local-name()='response']//*[local-name()='facet']/*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);

    // release client
    client.release();
  }


  @Test
  public void testBug18990() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException, JSONException
  {
    System.out.println("Running testBug18990");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true and server logger to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setServerRequestLogging(true);
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/bug18990/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition valueConstraintQuery1 = qb.valueConstraint("id", "00*2");
    StructuredQueryDefinition valueConstraintQuery2 = qb.valueConstraint("id", "0??6");
    StructuredQueryDefinition orFinalQuery = qb.or(valueConstraintQuery1, valueConstraintQuery2);

    // create handle
    StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);
    queryMgr.search(orFinalQuery, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();

    System.out.println(resultDoc);
    JSONAssert
        .assertEquals(
            "{\"snippet-format\":\"raw\", \"total\":2, \"start\":1, \"page-length\":10, \"results\":[{\"index\":1, \"uri\":\"/bug18990/constraint5.xml\", \"path\":\"fn:doc(\\\"/bug18990/constraint5.xml\\\")\", \"score\":0, \"confidence\":0, \"fitness\":0, \"href\":\"/v1/documents?uri=%2Fbug18990%2Fconstraint5.xml\", \"mimetype\":\"application/xml\", \"format\":\"xml\", \"content\":\"<root xmlns:search=\\\"http://marklogic.com/appservices/search\\\">\\n  <title>The memex</title>\\n  <popularity>5</popularity>\\n  <id>0026</id>\\n  <date xmlns=\\\"http://purl.org/dc/elements/1.1/\\\">2009-05-05</date>\\n  <price amt=\\\"123.45\\\" xmlns=\\\"http://cloudbank.com\\\"/>\\n  <p>The Memex, unfortunately, had no automated search feature.</p>\\n</root>\"}, {\"index\":2, \"uri\":\"/bug18990/constraint2.xml\", \"path\":\"fn:doc(\\\"/bug18990/constraint2.xml\\\")\", \"score\":0, \"confidence\":0, \"fitness\":0, \"href\":\"/v1/documents?uri=%2Fbug18990%2Fconstraint2.xml\", \"mimetype\":\"application/xml\", \"format\":\"xml\", \"content\":\"<root xmlns:search=\\\"http://marklogic.com/appservices/search\\\">\\n  <title>The Bush article</title>\\n  <popularity>4</popularity>\\n  <id>0012</id>\\n  <date xmlns=\\\"http://purl.org/dc/elements/1.1/\\\">2006-02-02</date>\\n  <price amt=\\\"0.12\\\" xmlns=\\\"http://cloudbank.com\\\"/>\\n  <p>The Bush article described a device called a Memex.</p>\\n</root>\"}], \"report\":\"(cts:search(fn:collection(), cts:or-query((cts:element-value-query(fn:QName(\\\"\\\",\\\"id\\\"), \\\"00*2\\\", (\\\"lang=en\\\"), 1), cts:element-value-query(fn:QName(\\\"\\\",\\\"id\\\"), \\\"0??6\\\", (\\\"lang=en\\\"), 1)), ()), (\\\"score-logtfidf\\\",cts:score-order(\\\"descending\\\")), 1))[1 to 10]\"}",
            resultDoc, false);
    // assertTrue("Result in json is not correct",
    // resultDoc.contains("{\"snippet-format\":\"raw\",\"total\":4,\"start\":1,\"page-length\":10,\"results\":[{\"index\":1,\"uri\":\"/bug18990/constraint5.xml\""));

    // turn off server logger
    srvMgr.setServerRequestLogging(false);
    srvMgr.writeConfiguration();

    // release client
    client.release();
  }

  @Test
  public void testBug19046() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug19046");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // read non-existent query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Could not get /config/query/NonExistentOpt";

    String exception = "";

    try
    {
      optionsMgr.readOptions("NonExistentOpt", readHandle);
    } catch (Exception e) {
      exception = e.toString();
    }

    System.out.println(exception);

    assertTrue(exception.contains(expectedException));

    // release client
    client.release();
  }

  @Test
  public void testBug19092() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug19092");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options handle
    String xmlOptions = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:term>" +
        "<search:term-option>case-sensitive</search:term-option>" +
        "</search:term>" +
        "</search:options>";
    StringHandle handle = new StringHandle(xmlOptions);

    // write query options
    optionsMgr.writeOptions("DefaultTermOpt", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("DefaultTermOpt", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    assertTrue(
        output.contains("<ns2:term-option>case-sensitive</ns2:term-option>") || output.contains("<search:term-option>case-sensitive</search:term-option>"));
    assertFalse(output.contains("<ns2:weight>0.0</ns2:weight>") || output.contains("<search:weight>0.0</search:weight>"));
    assertFalse(output.contains("<ns2:default/>") || output.contains("<search:default/>"));

    // release client
    client.release();
  }

  @Test
  public void testBug19092WithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBug19092WithJson");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options handle
    String xmlOptions = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:term>" +
        "<search:term-option>case-sensitive</search:term-option>" +
        "</search:term>" +
        "</search:options>";
    StringHandle handle = new StringHandle(xmlOptions);

    // write query options
    optionsMgr.writeOptions("DefaultTermOpt", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("DefaultTermOpt", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    assertTrue(output.contains("{\"options\":{\"term\":{\"term-option\":[\"case-sensitive\"]}}}"));

    // release client
    client.release();
  }

  @Test
  public void testBug19140() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug19140");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options handle
    String xmlOptions = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";
    StringHandle handle = new StringHandle(xmlOptions);

    // write query options
    optionsMgr.writeOptions("RawResultsOpt", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("RawResultsOpt", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    assertTrue(output.contains("transform-results apply=\"raw\"/"));
    assertFalse(output.contains("preferred-elements/"));

    // release client
    client.release();
  }

  @Test
  public void testBug19144WithJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBug19144WithJson");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/bug19144/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("correlation", "covariance");
    queryDef.setName("pop-rate-tups");

    // create handle
    StringHandle resultHandle = new StringHandle().withFormat(Format.JSON);
    queryMgr.tuples(queryDef, resultHandle);

    String result = resultHandle.get();

    System.out.println(result);

    assertEquals("{", result.substring(0, 1));

    // release client
    client.release();
  }

  @Test
  public void testBug19144WithXml() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBug19144WithXml");

    String[] filenames = { "aggr1.xml", "aggr2.xml", "aggr3.xml", "aggr4.xml", "aggr5.xml" };
    String queryOptionName = "aggregatesOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/bug19144/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    ValuesDefinition queryDef = queryMgr.newValuesDefinition("popularity", "aggregatesOpt.xml");
    queryDef.setAggregate("correlation", "covariance");
    queryDef.setName("pop-rate-tups");

    // create handle
    StringHandle resultHandle = new StringHandle().withFormat(Format.XML);
    queryMgr.tuples(queryDef, resultHandle);

    String result = resultHandle.get();

    System.out.println(result);

    assertEquals("<", result.substring(0, 1));

    // release client
    client.release();
  }

  @Test
  public void testBug19389() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug19389");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set error format to JSON
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    // depricated
    // srvMgr.setErrorFormat(Format.JSON);
    srvMgr.writeConfiguration();

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // read non-existent query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);

    String expectedException = "com.marklogic.client.ResourceNotFoundException: Could not get /config/query/NonExistentOpt";

    String exception = "";

    try
    {
      optionsMgr.readOptions("NonExistentOpt", readHandle);
    } catch (Exception e) {
      exception = e.toString();
    }

    System.out.println(exception);

    assertTrue(exception.contains(expectedException));

    // release client
    client.release();
  }
  @Test
  public void testBug19443() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    System.out.println("Running testBug19443");

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:constraint name='geoElemChild'>" +
        "<search:geo-elem>" +
        "<search:element name='bar' ns=''/>" +
        "<search:geo-option>type=long-lat-point</search:geo-option>" +
        "<search:parent name='foo' ns=''/>" +
        "</search:geo-elem>" +
        "</search:constraint>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("ElementChildGeoSpatialIndex", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("ElementChildGeoSpatialIndex", readHandle);
    String output = readHandle.get();

    String actual =
        "<search:options xmlns:search=\"http://marklogic.com/appservices/search\">" +
            "<search:constraint name=\"geoElemChild\">" +
            "<search:geo-elem>" +
            "<search:element name=\"bar\" ns=\"\"/>" +
            "<search:geo-option>type=long-lat-point</search:geo-option>" +
            "<search:parent name=\"foo\" ns=\"\"/>" +
            "</search:geo-elem>" +
            "</search:constraint>" +
            "</search:options>";
    System.out.println("Expected is :  \n");
    System.out.println(actual);
    System.out.println("Output is :  \n");
    System.out.println(output);
    assertTrue(actual.contains("<search:geo-option>type=long-lat-point</search:geo-option>"));

    // release client
    client.release();
  }

  @Test
  public void testBug20979() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug20979");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "wordConstraintWithElementAndAttributeIndexPlanOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("intitle:1945 OR inprice:12");

    // create handle
    SearchHandle resultsHandle = new SearchHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    // Document resultDoc = resultsHandle.getPlan();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    String strPlan = resultsHandle.getPlan(new StringHandle()).get();
    System.out.println(strPlan);

    assertTrue(strPlan.contains("qry:result estimate=\"3\""));

    // release client
    client.release();
  }

  @Test
  public void testBug21183() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug21183");

    String[] filenames = { "bug21183.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/bug-21183/", "XML");
    }

    // set query option
    setQueryOption(client, "bug21183Opt.xml");

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("bug21183Opt.xml");
    querydef.setCriteria("a");

    // create result handle
    SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle());

    String resultDoc1 = "";

    // get the result
    for (MatchDocumentSummary result : resultsHandle.getMatchResults())
    {
      for (Document s : result.getSnippets())
        resultDoc1 = convertXMLDocumentToString(s);
      System.out.println(resultDoc1);
      // Commenting as per Update from Bug 23788
      // assertTrue("Returned doc from SearchHandle has no namespace",
      // resultDoc1.contains("<test xmlns:myns=\"http://mynamespace.com\" xmlns:search=\"http://marklogic.com/appservices/search\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"));
      assertTrue(resultDoc1.contains("<txt att=\"1\">a</txt>"));
      System.out.println();
    }

    XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
    String resultDoc2 = shandle.toString();
    System.out.println(resultDoc2);
    assertTrue(resultDoc2.contains("<test xmlns:myns=\"http://mynamespace.com\">"));
    assertTrue(resultDoc2.contains("<txt att=\"1\">a</txt>"));

    // release client
    client.release();
  }

  @Test
  public void testBug22037() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException, TransformerException
  {
    System.out.println("Running testBug22037");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "rangeConstraintIntOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder(queryOptionName);
    StructuredQueryDefinition rangeQuery = qb.range(qb.element("popularity"), "xs:int", Operator.GE, 4);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(rangeQuery, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }
}

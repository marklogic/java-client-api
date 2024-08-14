/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;



public class TestQueryOptionBuilder extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUp() throws Exception {
    createUserRolesWithPrevilages("evalSearchRole", "eval-search-string", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("evalSearchUser", "evalSearch", "tde-admin", "tde-view", "evalSearchRole", "rest-admin", "rest-writer",
            "rest-reader", "rest-extension-user", "manage-user");
  }

  @AfterEach
  public void testCleanUp() throws Exception {
    deleteDocuments(connectAsAdmin());
  }

  @AfterAll
  public static void tearDown() throws Exception {
    deleteUserRole("evalSearchRole");
    deleteRESTUser("evalSearchUser");
  }

  // Begin TestQueryOptionBuilder
  @Test
  public void testValueConstraintWildcard() throws KeyManagementException, NoSuchAlgorithmException, XpathException, IOException
  {
    System.out.println("Running testValueConstraintWildcard");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint-query-builder/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:debug>true</search:debug>" +
        "<search:constraint name='id'>" +
        "<search:value>" +
        "<search:element name='id' ns=''/>" +
        "</search:value>" +
        "</search:constraint>" +
        "<search:return-metrics>false</search:return-metrics>" +
        "<search:return-qtext>false</search:return-qtext>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("ValueConstraintWildcard", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("ValueConstraintWildcard", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("ValueConstraintWildcard");
    querydef.setCriteria("id:00*2 OR id:0??6");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-value-query(fn:QName(\"\",\"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\",\"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

    assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testWordConstraintNormalWordQuery() throws KeyManagementException, NoSuchAlgorithmException, XpathException, IOException
  {
    System.out.println("Running testWordConstraintNormalWordQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint-query-builder/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:debug>true</search:debug>" +
        "<search:constraint name='intitle'>" +
        "<search:word>" +
        "<search:element name='title' ns=''/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='inprice'>" +
        "<search:word>" +
        "<search:attribute name='amt' ns=''/>" +
        "<search:element name='price' ns='http://cloudbank.com'/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:return-metrics>false</search:return-metrics>" +
        "<search:return-qtext>false</search:return-qtext>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("WordConstraintNormalWordQuery", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("WordConstraintNormalWordQuery", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("WordConstraintNormalWordQuery");
    querydef.setCriteria("Memex  OR inprice:.12");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
    assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
    assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
    assertXpathEvaluatesTo("123.45", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);

    String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:word-query(\"Memex\", (\"lang=en\"), 1), cts:element-attribute-word-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \".12\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

    assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testAllConstraintsWithStringSearch() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testAllConstraintsWithStringSearch");

    String filename1 = "constraint1.xml";
    String filename2 = "constraint2.xml";
    String filename3 = "constraint3.xml";
    String filename4 = "constraint4.xml";
    String filename5 = "constraint5.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://test.com/set1");
    metadataHandle1.getCollections().addAll("http://test.com/set5");
    metadataHandle2.getCollections().addAll("http://test.com/set1");
    metadataHandle3.getCollections().addAll("http://test.com/set3");
    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
    metadataHandle5.getCollections().addAll("http://test.com/set1");
    metadataHandle5.getCollections().addAll("http://test.com/set5");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:debug>true</search:debug>" +
        "<search:constraint name='id'>" +
        "<search:value>" +
        "<search:element name='id' ns=''/>" +
        "</search:value>" +
        "</search:constraint>" +
        "<search:constraint name='date'>" +
        "<search:range type='xs:date'>" +
        "<search:element name='date' ns='http://purl.org/dc/elements/1.1/'/>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:constraint name='coll'>" +
        "<search:collection prefix='http://test.com/' facet='true'/>" +
        "</search:constraint>" +
        "<search:constraint name='para'>" +
        "<search:word>" +
        "<search:field name='para'/>" +
        "<search:term-option>case-insensitive</search:term-option>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='intitle'>" +
        "<search:word>" +
        "<search:element name='title' ns=''/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='price'>" +
        "<search:range facet='false' type='xs:decimal'>" +
        "<search:attribute name='amt' ns=''/>" +
        "<search:element name='price' ns='http://cloudbank.com'/>" +
        "<search:bucket ge='120' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='14' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='0' lt='2' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:constraint name='pop'>" +
        "<search:range facet='true' type='xs:int'>" +
        "<search:element name='popularity' ns=''/>" +
        "<search:bucket ge='5' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='5' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='1' lt='3' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:return-metrics>false</search:return-metrics>" +
        "<search:return-qtext>false</search:return-qtext>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("AllConstraintsWithStringSearch", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("AllConstraintsWithStringSearch", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("AllConstraintsWithStringSearch");
    querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testAllConstraintsWithStructuredSearch() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testAllConstraintsWithStructuredSearch");

    String filename1 = "constraint1.xml";
    String filename2 = "constraint2.xml";
    String filename3 = "constraint3.xml";
    String filename4 = "constraint4.xml";
    String filename5 = "constraint5.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
    DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

    // set the metadata
    metadataHandle1.getCollections().addAll("http://test.com/set1");
    metadataHandle1.getCollections().addAll("http://test.com/set5");
    metadataHandle2.getCollections().addAll("http://test.com/set1");
    metadataHandle3.getCollections().addAll("http://test.com/set3");
    metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
    metadataHandle5.getCollections().addAll("http://test.com/set1");
    metadataHandle5.getCollections().addAll("http://test.com/set5");

    // write docs
    writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
    writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
    writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
    writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
    writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:debug>true</search:debug>" +
        "<search:constraint name='id'>" +
        "<search:value>" +
        "<search:element name='id' ns=''/>" +
        "</search:value>" +
        "</search:constraint>" +
        "<search:constraint name='date'>" +
        "<search:range type='xs:date'>" +
        "<search:element name='date' ns='http://purl.org/dc/elements/1.1/'/>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:constraint name='coll'>" +
        "<search:collection prefix='http://test.com/' facet='true'/>" +
        "</search:constraint>" +
        "<search:constraint name='para'>" +
        "<search:word>" +
        "<search:field name='para'/>" +
        "<search:term-option>case-insensitive</search:term-option>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='intitle'>" +
        "<search:word>" +
        "<search:element name='title' ns=''/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='price'>" +
        "<search:range facet='false' type='xs:decimal'>" +
        "<search:attribute name='amt' ns=''/>" +
        "<search:element name='price' ns='http://cloudbank.com'/>" +
        "<search:bucket ge='120' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='14' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='0' lt='2' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:constraint name='pop'>" +
        "<search:range facet='true' type='xs:int'>" +
        "<search:element name='popularity' ns=''/>" +
        "<search:bucket ge='5' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='5' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='1' lt='3' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:return-metrics>false</search:return-metrics>" +
        "<search:return-qtext>false</search:return-qtext>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("AllConstraintsWithStructuredSearch", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("AllConstraintsWithStructuredSearch", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("AllConstraintsWithStructuredSearch");
    StructuredQueryDefinition query1 = qb.and(qb.collectionConstraint("coll", "set1"), qb.collectionConstraint("coll", "set5"));
    StructuredQueryDefinition query2 = qb.not(qb.wordConstraint("intitle", "memex"));
    StructuredQueryDefinition query3 = qb.valueConstraint("id", "**11");
    StructuredQueryDefinition query4 = qb.rangeConstraint("date", StructuredQueryBuilder.Operator.EQ, "2005-01-01");
    StructuredQueryDefinition query5 = qb.and(qb.wordConstraint("para", "Bush"), qb.not(qb.wordConstraint("para", "memex")));
    StructuredQueryDefinition query6 = qb.rangeConstraint("price", StructuredQueryBuilder.Operator.EQ, "low");
    StructuredQueryDefinition query7 = qb.or(qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "high"),
        qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "medium"));
    StructuredQueryDefinition queryFinal = qb.and(query1, query2, query3, query4, query5, query6, query7);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testExtractMetadataWithStructuredSearch() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException,
      ParserConfigurationException, SAXException, IOException
  {
    System.out.println("testExtractMetadataWithStructuredSearch");

    String filename = "xml-original.xml";
    String uri = "/extract-metadata/";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    ServerConfigurationManager scMgr = client.newServerConfigManager();
    scMgr.setServerRequestLogging(true);
    scMgr.writeConfiguration();

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // write the doc
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create doc id
    String docId = uri + filename;

    // write metadata
    docMgr.writeMetadata(docId, writeMetadataHandle);

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:fragment-scope>properties</search:fragment-scope>" +
        "<search:constraint name='appname'>" +
        "<search:word>" +
        "<search:element name='AppName' ns=''/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:extract-metadata>" +
        "<search:qname elem-ns='' elem-name='Author'/>" +
        "<search:qname elem-ns='' elem-name='AppName'/>" +
        "</search:extract-metadata>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("ExtractMetadataWithStructuredSearch", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("ExtractMetadataWithStructuredSearch", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("ExtractMetadataWithStructuredSearch");
    StructuredQueryDefinition queryTerm1 = qb.term("MarkLogic");
    StructuredQueryDefinition queryTerm2 = qb.term("Microsoft");
    // StructuredQueryDefinition queryWord = qb.wordConstraint("appname",
    // "Microsoft");
    StructuredQueryDefinition queryFinal = qb.and(queryTerm1, queryTerm2);

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])", resultDoc);
    assertXpathEvaluatesTo("Microsoft Office Word", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])", resultDoc);

    // release client
    client.release();
  }

  // See bug 18361
  /*
   * public void testExtractMetadataWithStructuredSearchAndConstraint() throws
   * KeyManagementException, NoSuchAlgorithmException, XpathException,
   * TransformerException, ParserConfigurationException, SAXException,
   * IOException {
   * System.out.println("testExtractMetadataWithStructuredSearchAndConstraint");
   *
   * String filename = "xml-original.xml"; String uri = "/extract-metadata/";
   *
   * DatabaseClient client = getDatabaseClient("rest-admin", "x",
   * getConnType());
   *
   * ServerConfigurationManager scMgr = client.newServerConfigManager();
   * scMgr.setServerRequestLogging(true); scMgr.writeConfiguration();
   *
   * // get the original metadata Document docMetadata =
   * getXMLMetadata("metadata-original.xml");
   *
   * // create doc manager XMLDocumentManager docMgr =
   * client.newXMLDocumentManager();
   *
   * // write the doc writeDocumentUsingInputStreamHandle(client, filename, uri,
   * "XML");
   *
   * // create handle to write metadata DOMHandle writeMetadataHandle = new
   * DOMHandle(); writeMetadataHandle.set(docMetadata);
   *
   * // create doc id String docId = uri + filename;
   *
   * // write metadata docMgr.writeMetadata(docId, writeMetadataHandle);
   *
   * // create query options manager QueryOptionsManager optionsMgr =
   * client.newServerConfigManager().newQueryOptionsManager();
   *
   * // create query options String opts1 = new QueryOptionsBuilder();
   *
   * // create query options handle StringHandle handle = new StringHandle();
   *
   * // build query options
   * handle.withConfiguration(builder.configure().fragmentScope
   * (FragmentScope.PROPERTIES));
   * handle.withExtractMetadata(builder.extractMetadata(builder.elementValue(new
   * QName("", "Author")), builder.constraintValue("appname")));
   * handle.withConstraints(builder.constraint("appname",
   * builder.word(builder.elementTermIndex(new QName("AppName")))));
   *
   * // write query options
   * optionsMgr.writeOptions("ExtractMetadataWithStructuredSearchAndConstraint",
   * handle);
   *
   * // create query manager QueryManager queryMgr = client.newQueryManager();
   *
   * // create query def StructuredQueryBuilder qb =
   * queryMgr.newStructuredQueryBuilder
   * ("ExtractMetadataWithStructuredSearchAndConstraint");
   * StructuredQueryDefinition queryTerm = qb.term("MarkLogic");
   * StructuredQueryDefinition queryWord = qb.wordConstraint("appname",
   * "Microsoft"); StructuredQueryDefinition queryFinal = qb.and(queryTerm,
   * queryWord);
   *
   * // create handle DOMHandle resultsHandle = new DOMHandle();
   * queryMgr.search(queryFinal, resultsHandle);
   *
   * // get the result Document resultDoc = resultsHandle.get();
   * System.out.println(convertXMLDocumentToString(resultDoc));
   *
   * //assertXpathEvaluatesTo("MarkLogic",
   * "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])"
   * , resultDoc); //assertXpathEvaluatesTo("Microsoft Office Word",
   * "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])"
   * , resultDoc);
   *
   * // release client client.release(); }
   */

  @Test
  public void testExtractMetadataWithStructuredSearchAndRangeConstraint() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException,
      ParserConfigurationException, SAXException, IOException
  {
    System.out.println("testExtractMetadataWithStructuredSearchAndRangeConstraint");

    String filename = "xml-original.xml";
    String uri = "/extract-metadata/";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    ServerConfigurationManager scMgr = client.newServerConfigManager();
    scMgr.setServerRequestLogging(true);
    scMgr.writeConfiguration();

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // write the doc
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create doc id
    String docId = uri + filename;

    // write metadata
    docMgr.writeMetadata(docId, writeMetadataHandle);

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:fragment-scope>properties</search:fragment-scope>" +
        "<search:constraint name='pop'>" +
        "<search:range facet='true' type='xs:int'>" +
        "<search:element name='popularity' ns=''/>" +
        "<search:bucket ge='5' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='5' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='1' lt='3' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:extract-metadata>" +
        "<search:qname elem-ns='' elem-name='Author'/>" +
        "<search:constraint-value ref='pop'/>" +
        "</search:extract-metadata>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("ExtractMetadataWithStructuredSearchAndRangeConstraint", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("ExtractMetadataWithStructuredSearchAndRangeConstraint", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("ExtractMetadataWithStructuredSearchAndRangeConstraint");
    StructuredQueryDefinition queryFinal = qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='Author'])", resultDoc);
    assertXpathEvaluatesTo("5", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='constraint-meta'])", resultDoc);
    // assertXpathEvaluatesTo("Microsoft Office Word",
    // "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])",
    // resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testDocumentLevelMetadata() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, ParserConfigurationException,
      SAXException, IOException
  {
    System.out.println("testDocumentLevelMetadata");

    String filename = "xml-original.xml";
    String uri = "/extract-metadata/";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    ServerConfigurationManager scMgr = client.newServerConfigManager();
    scMgr.setServerRequestLogging(true);
    scMgr.writeConfiguration();

    // get the original metadata
    Document docMetadata = getXMLMetadata("metadata-original.xml");

    // create doc manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // write the doc
    writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");

    // create handle to write metadata
    DOMHandle writeMetadataHandle = new DOMHandle();
    writeMetadataHandle.set(docMetadata);

    // create doc id
    String docId = uri + filename;

    // write metadata
    docMgr.writeMetadata(docId, writeMetadataHandle);

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:fragment-scope>documents</search:fragment-scope>" +
        "<search:constraint name='pop'>" +
        "<search:range facet='true' type='xs:int'>" +
        "<search:element name='popularity' ns=''/>" +
        "<search:bucket ge='5' name='high'>High</search:bucket>" +
        "<search:bucket ge='3' lt='5' name='medium'>Medium</search:bucket>" +
        "<search:bucket ge='1' lt='3' name='low'>Low</search:bucket>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:extract-metadata>" +
        "<search:qname elem-ns='' elem-name='Author'/>" +
        "<search:qname elem-ns='' elem-name='name'/>" +
        "<search:constraint-value ref='pop'/>" +
        "</search:extract-metadata>" +
        "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);
    // write query options
    optionsMgr.writeOptions("DocumentLevelMetadata", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("DocumentLevelMetadata", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("DocumentLevelMetadata");
    StructuredQueryDefinition queryFinal = qb.term("noodle");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("noodle", "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='name'])", resultDoc);
    // assertXpathEvaluatesTo("5",
    // "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='constraint-meta'])",
    // resultDoc);
    // assertXpathEvaluatesTo("Microsoft Office Word",
    // "string(//*[local-name()='result']//*[local-name()='metadata']/*[local-name()='AppName'])",
    // resultDoc);

    // release client
    client.release();
  }
  // End of TestQueryOptionBuilder

  // Begin TestQueryOptionBuilderGrammar
  @Test
  public void testGrammarOperatorQuotation() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testGrammarOperatorQuotation");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/gramar-op-quote/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:grammar>" +
            "<search:implicit>" +
            "<cts:and-query xmlns:cts='http://marklogic.com/cts' strength='20'/>" +
            "</search:implicit>" +
            "<search:joiner apply='infix' consume='0' element='cts:or-query' strength='20' tokenize='word'>OR</search:joiner>" +
            "<search:joiner apply='infix' consume='0' element='cts:and-query' strength='30' tokenize='word'>AND</search:joiner>" +
            "<search:joiner apply='constraint' consume='0' strength='50'>:</search:joiner>" +
            "<search:quotation>\"</search:quotation>" +
            "<search:starter apply='grouping' delimiter=')' strength='30'>(</search:starter>" +
            "<search:starter apply='prefix' element='cts:not-query' strength='40'>-</search:starter>" +
            "</search:grammar>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("GrammarOperatorQuotation", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("GrammarOperatorQuotation", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarOperatorQuotation");
    querydef.setCriteria("1945 OR \"Atlantic Monthly\"");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0113", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testGrammarTwoWordsSpace() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testGrammarTwoWordsSpace");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:grammar>" +
            "<search:implicit>" +
            "<cts:and-query xmlns:cts='http://marklogic.com/cts' strength='20'/>" +
            "</search:implicit>" +
            "<search:joiner apply='infix' consume='0' element='cts:or-query' strength='20' tokenize='word'>OR</search:joiner>" +
            "<search:joiner apply='infix' consume='0' element='cts:and-query' strength='30' tokenize='word'>AND</search:joiner>" +
            "<search:joiner apply='constraint' consume='0' strength='50'>:</search:joiner>" +
            "<search:quotation>\"</search:quotation>" +
            "<search:starter apply='grouping' delimiter=')' strength='30'>(</search:starter>" +
            "<search:starter apply='prefix' element='cts:not-query' strength='40'>-</search:starter>" +
            "</search:grammar>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("GrammarTwoWordsSpace", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("GrammarTwoWordsSpace", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarTwoWordsSpace");
    querydef.setCriteria("\"Atlantic Monthly\" \"Bush\"");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testGrammarPrecedenceAndNegate() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testGrammarPrecedenceAndNegate");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:grammar>" +
            "<search:implicit>" +
            "<cts:and-query xmlns:cts='http://marklogic.com/cts' strength='20'/>" +
            "</search:implicit>" +
            "<search:joiner apply='infix' consume='0' element='cts:or-query' strength='10' tokenize='word'>OR</search:joiner>" +
            "<search:joiner apply='infix' consume='0' element='cts:and-query' strength='20' tokenize='word'>AND</search:joiner>" +
            "<search:joiner apply='constraint' consume='0' strength='50'>:</search:joiner>" +
            "<search:quotation>\"</search:quotation>" +
            "<search:starter apply='grouping' delimiter=')' strength='30'>(</search:starter>" +
            "<search:starter apply='prefix' element='cts:not-query' strength='40'>-</search:starter>" +
            "</search:grammar>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("GrammarPrecedenceAndNegate", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("GrammarPrecedenceAndNegate", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarPrecedenceAndNegate");
    querydef.setCriteria("-bush AND -memex");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0113", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testGrammarConstraint() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testGrammarConstraint");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/gramar-two-words-space/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:grammar>" +
            "<search:implicit>" +
            "<cts:and-query xmlns:cts='http://marklogic.com/cts' strength='20'/>" +
            "</search:implicit>" +
            "<search:joiner apply='infix' consume='0' element='cts:or-query' strength='20' tokenize='word'>OR</search:joiner>" +
            "<search:joiner apply='infix' consume='0' element='cts:and-query' strength='30' tokenize='word'>AND</search:joiner>" +
            "<search:joiner apply='constraint' consume='0' strength='50'>:</search:joiner>" +
            "<search:quotation>\"</search:quotation>" +
            "<search:starter apply='grouping' delimiter=')' strength='30'>(</search:starter>" +
            "<search:starter apply='prefix' element='cts:not-query' strength='20'>-</search:starter>" +
            "</search:grammar>" +
            "<search:constraint name='intitle'>" +
            "<search:word>" +
            "<search:element name='title' ns=''/>" +
            "</search:word>" +
            "</search:constraint>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("GrammarConstraint", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("GrammarConstraint", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("GrammarConstraint");
    querydef.setCriteria("intitle:Vannevar AND served");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0024", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }
  // End of TestQueryOptionBuilderGrammar

  // Begin TestQueryOptionBuilderSearchableExpression
  @Test
  public void testSearchableExpressionChildAxis() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchableExpressionChildAxis");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-child-axis/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>//root/child::p</search:searchable-expression>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionChildAxis", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("SearchableExpressionChildAxis", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionChildAxis");
    querydef.setCriteria("bush");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("The Bush article described a device called a Memex.", "string(//*[local-name()='result'][1]//*[local-name()='p'])", resultDoc);
    assertXpathEvaluatesTo("Vannevar Bush wrote an article for The Atlantic Monthly", "string(//*[local-name()='result'][2]//*[local-name()='p'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSearchableExpressionDescendantAxis() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchableExpressionDescendantAxis");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-desc-axis/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>/root/descendant::title</search:searchable-expression>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionDescendantAxis", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("SearchableExpressionDescendantAxis", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionDescendantAxis");
    querydef.setCriteria("bush OR memex");

    SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    assertEquals(3, results.getTotalResults());
    List<String> uris = new ArrayList<>();
    for (MatchDocumentSummary matchResult : results.getMatchResults()) {
      uris.add(matchResult.getUri());
    }
    assertTrue(uris.contains("/search-expr-desc-axis/constraint1.xml")); // Vannevar Bush
    assertTrue(uris.contains("/search-expr-desc-axis/constraint2.xml")); // The Bush article
    assertTrue(uris.contains("/search-expr-desc-axis/constraint5.xml")); // The memex

    client.release();
  }

  @Test
  public void testSearchableExpressionOrOperator() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchableExpressionOrOperator");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-or-op/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>//(title|id)</search:searchable-expression>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>4</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionOrOperator", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("SearchableExpressionOrOperator", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionOrOperator");
    querydef.setCriteria("bush OR 0011");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][1]//*[local-name()='highlight'])", resultDoc);
    assertXpathEvaluatesTo("0011", "string(//*[local-name()='result'][2]//*[local-name()='highlight'])", resultDoc);
    assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][3]//*[local-name()='highlight'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSearchableExpressionDescendantOrSelf() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchableExpressionDescendantOrSelf");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-desc-or-self/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>/descendant-or-self::root</search:searchable-expression>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>10</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionDescendantOrSelf", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("SearchableExpressionDescendantOrSelf", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionDescendantOrSelf");
    querydef.setCriteria("Bush");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    // release client
    client.release();
  }

  @Test
  public void testSearchableExpressionFunction() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchableExpressionFunction");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-func/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>//p[contains(.,'groundbreaking')]</search:searchable-expression>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>10</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionFunction", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("SearchableExpressionFunction", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionFunction");
    querydef.setCriteria("atlantic");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/search-expr-func/constraint3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  /*
   * User with eval-string privs and a searchable expression. Similar to testSearchableExpressionFunction
   * but in this case user has eval-string privs.
   */

  @Test
  public void testEvalPrivAndSearchableExpression() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testEvalPrivAndSearchableExpression");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("evalSearchUser", "evalSearch", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-func/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>//p[contains(.,'groundbreaking')]</search:searchable-expression>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>10</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionFunction", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("SearchableExpressionFunction", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionFunction");
    querydef.setCriteria("atlantic");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/search-expr-func/constraint3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  /*
   * User with eval-string privs and no searchable expression
   */
  @Test
  public void testevalsearchstringPrivilege() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    DatabaseClient client = null;
    System.out.println("Running testevalsearchstringPrivilege");
    try {

      String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

      client = getDatabaseClient("evalSearchUser", "evalSearch", getConnType());

      // write docs
      for (String filename : filenames) {
        writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-func/", "XML");
      }

      // create query options manager
      QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

      // create query options with no searchable expression
      String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
              "<search:return-metrics>false</search:return-metrics>" +
              "<search:return-qtext>false</search:return-qtext>" +
              "<search:transform-results apply='snippet'>" +
              "<search:per-match-tokens>30</search:per-match-tokens>" +
              "<search:max-matches>10</search:max-matches>" +
              "<search:max-snippet-chars>200</search:max-snippet-chars>" +
              "</search:transform-results>" +
              "</search:options>";

      // create query options handle
      StringHandle handle = new StringHandle(opts1);

      // write query options
      optionsMgr.writeOptions("SearchableExpressionFunction", handle);

      // create query manager
      QueryManager queryMgr = client.newQueryManager();

      // create query def
      StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionFunction");
      querydef.setCriteria("atlantic");

      // create handle
      JacksonHandle jacksonHandle = new JacksonHandle();

      queryMgr.search(querydef, jacksonHandle);

      // get the result
      JsonNode jsonResults = jacksonHandle.get();
      JsonNode results = jsonResults.path("results");
      JsonNode result1 = results.get(0).get("uri");
      JsonNode result2 = results.get(1).get("uri");
      System.out.println("Testing eval-string privilege without searchable-expression");
      assertTrue(  result1.asText().contains("/search-expr-func/constraint1.xml")
              || result1.asText().contains("/search-expr-func/constraint3.xml"));
      assertTrue(  result2.asText().contains("/search-expr-func/constraint1.xml")
              || result2.asText().contains("/search-expr-func/constraint3.xml"));
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      // release client
      client.release();
    }

  }

  /* Negative case. Have a searchable expression not in the path range. User does not have eval-search-string privilege.
   * Response will have XDMP-UNSEARCHABLE
   */
  @Test
  public void testInvalidSearchableExpressionFunction() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testInvalidSearchableExpressionFunction");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-expr-func/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:searchable-expression>junk[contains(.,'grdbreaking')]</search:searchable-expression>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>10</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("SearchableExpressionFunction", handle);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchableExpressionFunction");
    querydef.setCriteria("atlantic");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    StringBuilder strb = new StringBuilder();
    try {
      queryMgr.search(querydef, resultsHandle);

    }
    catch (FailedRequestException fex) {
      strb.append(fex.getServerStatusCode());
      strb.append(" ");
      strb.append(fex.getServerMessage());
      strb.append(" ");
      strb.append(fex.getServerMessageCode());
      System.out.println("Exception from search " + strb.toString());
    }
    assertTrue(strb.toString().contains("400"));
    assertTrue(strb.toString().contains("XDMP-UNSEARCHABLE"));
    // release client
    client.release();
  }
  // End of TestQueryOptionBuilderSearchableExpression

  // Begin TestQueryOptionBuilderSearchOptions
  @Test
  public void testSearchOptions1() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchOptions1");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-ops-1/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:debug>true</search:debug>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:search-option>checked</search:search-option>" +
            "<search:search-option>filtered</search:search-option>" +
            "<search:search-option>score-simple</search:search-option>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    List<String> listOfSearchOptions = new ArrayList<>();
    listOfSearchOptions.add("checked");
    listOfSearchOptions.add("filtered");
    listOfSearchOptions.add("score-simple");

    // write query options
    optionsMgr.writeOptions("SearchOptions1", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("SearchOptions1", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchOptions1");
    querydef.setCriteria("bush");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    String expectedSearchReport = "(cts:search(fn:collection(), cts:word-query(\"bush\", (\"lang=en\"), 1), (\"checked\",\"filtered\",\"score-simple\",cts:score-order(\"descending\")), 1))[1 to 10]";

    assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testSearchOptions2() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchOptions2");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/search-ops-2/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:debug>true</search:debug>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:search-option>unchecked</search:search-option>" +
            "<search:search-option>unfiltered</search:search-option>" +
            "<search:search-option>score-logtfidf</search:search-option>" +
            "<search:transform-results apply='raw'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    List<String> listOfSearchOptions = new ArrayList<>();
    listOfSearchOptions.add("unchecked");
    listOfSearchOptions.add("unfiltered");
    listOfSearchOptions.add("score-logtfidf");

    // write query options
    optionsMgr.writeOptions("SearchOptions2", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("SearchOptions2", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("SearchOptions2");
    querydef.setCriteria("bush");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    String expectedSearchReport = "(cts:search(fn:collection(), cts:word-query(\"bush\", (\"lang=en\"), 1), (\"unchecked\",\"unfiltered\",\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

    assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }
  // End of TestQueryOptionBuilderSearchOptions

  // Begin TestQueryOptionBuilderSortOrder
  // All tests were marked with @Ignore. See Git Issue 347 on 4.0.2 tag
  // End of TestQueryOptionBuilderSortOrder

  // Begin TestQueryOptionBuilderTransformResults
  @Test
  public void testTransformResuleWithSnippetFunction() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, JsonProcessingException,
          IOException
  {
    System.out.println("Running testTransformResuleWithSnippetFunction");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/trans-res-with-snip-func/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='snippet'>" +
            "<search:per-match-tokens>30</search:per-match-tokens>" +
            "<search:max-matches>4</search:max-matches>" +
            "<search:max-snippet-chars>200</search:max-snippet-chars>" +
            "<search:preferred-elements>" +
            "<search:element name='elem' ns='ns'/>" +
            "</search:preferred-elements>" +
            "</search:transform-results>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("TransformResuleWithSnippetFunction", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions("TransformResuleWithSnippetFunction", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("TransformResuleWithSnippetFunction");
    querydef.setCriteria("Atlantic groundbreaking");

    // create handle
    StringHandle resultsHandle = new StringHandle();
    resultsHandle.setFormat(Format.JSON);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();

    JsonNode jn = new ObjectMapper().readTree(resultDoc);
    // System.out.println(resultDoc);
    System.out.println(jn.get("results").findValue("uri").textValue());

    assertTrue( jn.get("results").findValue("uri").textValue().contains("/trans-res-with-snip-func/constraint3.xml"));

    // release client
    client.release();
  }

  @Test
  public void testTransformResuleWithEmptySnippetFunction() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, SAXException,
          IOException
  {
    System.out.println("Running testTransformResuleWithEmptySnippetFunction");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/trans-res-with-emp-snip-func/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options
    String opts1 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
            "<search:return-metrics>false</search:return-metrics>" +
            "<search:return-qtext>false</search:return-qtext>" +
            "<search:transform-results apply='empty-snippet'/>" +
            "</search:options>";

    // create query options handle
    StringHandle handle = new StringHandle(opts1);

    // write query options
    optionsMgr.writeOptions("TransformResuleWithEmptySnippetFunction", handle);

    // read query option
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.XML);
    optionsMgr.readOptions("TransformResuleWithEmptySnippetFunction", readHandle);
    String output = readHandle.get();
    System.out.println(output);

    // create query manager
    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("TransformResuleWithEmptySnippetFunction");
    querydef.setCriteria("Atlantic groundbreaking");

    // create handle
    StringHandle resultsHandle = new StringHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    String resultDoc = resultsHandle.get();
    System.out.println(resultDoc);

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/trans-res-with-emp-snip-func/constraint3.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    // assertXpathEvaluatesTo("groundbreaking",
    // "string(//*[local-name()='result']//*[local-name()='highlight'][2])",
    // resultDoc);

    // release client
    client.release();
  }
  // End of TestQueryOptionBuilderTransformResults

  // Begin TestQueryOptionsHandle
  @Test
  public void testRoundtrippingQueryOptionPOJO() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
          TransformerException
  {
    System.out.println("Running testRoundtrippingQueryOptionPOJO");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "valueConstraintWildCardOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/pojo-query-option/", "XML");
    }

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.set(docStream);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option with QueryOptionsHandle
    StringHandle readHandle = new StringHandle();
    optionsMgr.readOptions(queryOptionName, readHandle);
    String output = readHandle.toString();

    // write back query option with QueryOptionsHandle
    String queryOptionNamePOJO = "valueConstraintWildCardPOJOOpt.xml";
    optionsMgr.writeOptions(queryOptionNamePOJO, readHandle);

    // read POJO query option
    optionsMgr.readOptions(queryOptionNamePOJO, readHandle);
    String outputPOJO = readHandle.toString();

    boolean isQueryOptionsSame = output.equals(outputPOJO);
    assertTrue( isQueryOptionsSame);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionNamePOJO);
    querydef.setCriteria("id:00*2 OR id:0??6");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRoundtrippingQueryOptionPOJOAll() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
          XpathException, TransformerException
  {
    System.out.println("Running testRoundtrippingQueryOptionPOJOAll");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "appservicesConstraintCombinationOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/pojo-query-option-all/", "XML");
    }

    // create handle
    ReaderHandle handle = new ReaderHandle();

    // write the files
    BufferedReader docStream = new BufferedReader(new FileReader("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.set(docStream);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option with QueryOptionsHandle
    StringHandle readHandle = new StringHandle();
    optionsMgr.readOptions(queryOptionName, readHandle);
    String output = readHandle.toString();
    System.out.println(output);
    System.out.println("============================");

    // write back query option with QueryOptionsHandle
    String queryOptionNamePOJO = "appservicesConstraintCombinationPOJOOpt.xml";
    optionsMgr.writeOptions(queryOptionNamePOJO, readHandle);

    // read POJO query option
    optionsMgr.readOptions(queryOptionNamePOJO, readHandle);
    String outputPOJO = readHandle.toString();
    System.out.println(outputPOJO);

    boolean isQueryOptionsSame = output.equals(outputPOJO);
    assertTrue( isQueryOptionsSame);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionNamePOJO);
    // querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();
    // System.out.println(convertXMLDocumentToString(resultDoc));

    assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testRoundtrippingQueryOptionPOJOAllJSON() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
          XpathException, TransformerException
  {
    System.out.println("Running testRoundtrippingQueryOptionPOJOAllJSON");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "appservicesConstraintCombinationOpt.json";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/pojo-query-option-all-json/", "XML");
    }

    // create handle
    FileHandle handle = new FileHandle(new File("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.setFormat(Format.JSON);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option with StringHandle
    StringHandle readHandle = new StringHandle();
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions(queryOptionName, readHandle);
    String output = readHandle.toString();
    System.out.println(output);
    System.out.println("============================");

    // write back query option with StringHandle
    String queryOptionNamePOJO = "appservicesConstraintCombinationPOJOOpt.json";
    readHandle.setFormat(Format.JSON);
    optionsMgr.writeOptions(queryOptionNamePOJO, readHandle);

    // read POJO query option
    readHandle.setFormat(Format.JSON);
    optionsMgr.readOptions(queryOptionNamePOJO, readHandle);
    String outputPOJO = readHandle.toString();
    System.out.println(outputPOJO);

    boolean isQueryOptionsSame = output.equals(outputPOJO);
    assertTrue( isQueryOptionsSame);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionNamePOJO);
    querydef.setCriteria("pop:high");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);

    // release client
    client.release();
  }

  @Test
  public void testJSONConverter() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
          TransformerException
  {
    System.out.println("Running testJSONConverter");

    // String queryOptionName = "jsonConverterOpt.json";
    String queryOptionName = "queryValidationOpt.json";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create a manager for writing query options
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create handle
    FileHandle handle = new FileHandle(new File("src/test/java/com/marklogic/client/functionaltest/queryoptions/" + queryOptionName));
    handle.setFormat(Format.JSON);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, handle);

    System.out.println("Write " + queryOptionName + " to database");

    // read query option with QueryOptionsHandle
    StringHandle readHandle = new StringHandle();
    optionsMgr.readOptions(queryOptionName, readHandle);
    String output = readHandle.toString();
    System.out.println(output);
    System.out.println("============================");

    client.release();
  }
  // End of TestQueryOptionsHandle
}

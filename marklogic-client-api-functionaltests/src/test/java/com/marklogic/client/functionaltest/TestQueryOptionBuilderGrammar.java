/*
 * Copyright 2014-2019 MarkLogic Corporation
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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestQueryOptionBuilderGrammar extends BasicJavaClientREST {

  private static String dbName = "TestQueryOptionBuilderGrammarDB";
  private static String[] fNames = { "TestQueryOptionBuilderGrammarDB-1" };

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @After
  public void testCleanUp() throws Exception {
    clearDB();
    System.out.println("Running clear script");
  }

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

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

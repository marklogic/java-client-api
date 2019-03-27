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

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestAppServicesFieldConstraint extends BasicJavaClientREST {

  // private String serverName = "";
  private static String dbName = "AppServicesFieldConstraintDB";
  private static String[] fNames = { "AppServicesFieldConstraintDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
    addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
    addFieldExcludeRoot(dbName, "para");
    includeElementFieldWithWeight(dbName, "para", "", "p", 5, "", "", "");
  }

  @Test
  public void testWithSnippetAndVariousGrammarAndWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      XpathException
  {
    System.out.println("Running testWithSnippetAndVariousGrammarAndWordQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "fieldConstraintWithSnippetAndVariousGrammarAndWordQueryOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("(para:Bush AND -para:memex) OR id:0026 AND memex");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);
    assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='match'][2]//*[local-name()='highlight'])", resultDoc);
    assertXpathEvaluatesTo("Memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][3]//*[local-name()='highlight'])", resultDoc);
    assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][2]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);

    // String expectedSearchReport =
    // "(cts:search(fn:collection(), cts:and-query((cts:or-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"5\"), (), 1), cts:and-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"3\"), (), 1), cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&lt;\", xs:int(\"5\"), (), 1)), ()))), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&gt;=\", 3.0, (), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&lt;\", 14.0, (), 1), cts:element-word-query(fn:QName(\"\", \"title\"), \"served\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

    // assertXpathEvaluatesTo(expectedSearchReport,
    // "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

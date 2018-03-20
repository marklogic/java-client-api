/*
 * Copyright 2014-2018 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestAppServicesRangeConstraint extends BasicJavaClientREST {

  // private String serverName = "";
  private static String dbName = "AppServicesRangeConstraintDB";
  private static String[] fNames = { "AppServicesRangeConstraintDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    // super.setUp();
    // serverName = getConnectedServerName();
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testWithWordSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
  {
    System.out.println("Running testWithWordSearch");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "rangeConstraintWithWordSearchOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("date:2006-02-02 OR policymaker");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("2008-04-04", "string(//*[local-name()='result'][1]//*[local-name()='date'])", resultDoc);
    assertXpathEvaluatesTo("2006-02-02", "string(//*[local-name()='result'][2]//*[local-name()='date'])", resultDoc);
    assertXpathEvaluatesTo("Vannevar served as a prominent policymaker and public intellectual.", "string(//*[local-name()='result'][1]//*[local-name()='p'])", resultDoc);
    assertXpathEvaluatesTo("The Bush article described a device called a Memex.", "string(//*[local-name()='result'][2]//*[local-name()='p'])", resultDoc);

    String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\",\"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

    assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);

    // release client
    client.release();
  }

  /*
   * public void testNegativeWithoutIndexSettings() throws
   * KeyManagementException, NoSuchAlgorithmException, IOException,
   * ParserConfigurationException, SAXException, XpathException,
   * TransformerException {
   * System.out.println("Running testNegativeWithoutIndexSettings");
   * 
   * String[] filenames = {"constraint1.xml", "constraint2.xml",
   * "constraint3.xml", "constraint4.xml", "constraint5.xml"}; String
   * queryOptionName = "rangeConstraintNegativeWithoutIndexSettingsOpt.xml";
   * 
   * DatabaseClient client = getDatabaseClient("rest-admin", "x",
   * Authentication.DIGEST);
   * 
   * // write docs for(String filename : filenames) {
   * writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/",
   * "XML"); }
   * 
   * setQueryOption(client, queryOptionName);
   * 
   * QueryManager queryMgr = client.newQueryManager();
   * 
   * // create query def StringQueryDefinition querydef =
   * queryMgr.newStringDefinition(queryOptionName);
   * querydef.setCriteria("title:Bush");
   * 
   * // create handle DOMHandle resultsHandle = new DOMHandle();
   * 
   * String exception = "";
   * 
   * // run search try { queryMgr.search(querydef, resultsHandle); } catch
   * (Exception e) { exception = e.toString(); }
   * 
   * String expectedException =
   * "com.marklogic.client.FailedRequestException: Local message: search failed: Internal Server ErrorServer Message: XDMP-ELEMRIDXNOTFOUND"
   * ;
   * 
   * //assertEquals("Wrong exception", expectedException, exception); boolean
   * exceptionIsThrown = exception.contains(expectedException);
   * assertTrue("Exception is not thrown", exceptionIsThrown);
   * 
   * // release client client.release(); }
   */

  @Test
  public void testNegativeTypeMismatch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testNegativeTypeMismatch");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "rangeConstraintNegativeTypeMismatchOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("date:2006-02-02");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();

    String exception = "";

    // run search
    try
    {
      queryMgr.search(querydef, resultsHandle);
    } catch (Exception e) {
      exception = e.toString();
    }

    String expectedException = "com.marklogic.client.FailedRequestException: Local message: search failed: Bad Request. Server Message: XDMP-ELEMRIDXNOTFOUND";

    // assertEquals("Wrong exception", expectedException, exception);
    boolean exceptionIsThrown = exception.contains(expectedException);
    assertTrue("Exception is not thrown", exceptionIsThrown);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
    // super.tearDown();
  }
}

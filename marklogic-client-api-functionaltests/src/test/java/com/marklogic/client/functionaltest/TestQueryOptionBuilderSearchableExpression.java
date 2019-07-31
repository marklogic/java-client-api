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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestQueryOptionBuilderSearchableExpression extends BasicJavaClientREST {

  private static String dbName = "TestQueryOptionBuilderSearchableExpressionDB";
  private static String[] fNames = { "TestQueryOptionBuilderSearchableExpressionDB-1" };

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
    
    createUserRolesWithPrevilages("evalSearchRole", "eval-search-string", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("evalSearchUser", "evalSearch", "tde-admin", "tde-view", "evalSearchRole", "rest-admin", "rest-writer", 
    		                             "rest-reader", "rest-extension-user", "manage-user");
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception
  {
    System.out.println("In AfterClass tear down");
    
    deleteUserRole("evalSearchRole");
    deleteRESTUser("evalSearchUser");
  }

  @After
  public void testCleanUp() throws Exception {
    clearDB();
    System.out.println("Running clear script");
  }

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

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    resultsHandle.setFormat(Format.XML);
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
    assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
    assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][3]//*[local-name()='title'])", resultDoc);

    // release client
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
    assertTrue("Row 1 uri value incorrect",  result1.asText().contains("/search-expr-func/constraint1.xml")
    		|| result1.asText().contains("/search-expr-func/constraint3.xml"));
    assertTrue("Row 1 uri value incorrect",  result2.asText().contains("/search-expr-func/constraint1.xml")
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
    	strb.append(fex.getServerMessage());
    	strb.append(fex.getServerMessageCode());
    	 System.out.println("Exception from search " + strb.toString());
    }
    assertTrue(strb.toString().contains("INTERNAL ERROR"));
    assertTrue(strb.toString().contains("XDMP-UNSEARCHABLE"));
    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

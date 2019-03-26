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
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

public class TestAppServicesRangePathIndexConstraint extends BasicJavaClientREST {

  private static String dbName = "AppServicesPathIndexConstraintDB";
  private static String[] fNames = { "AppServicesPathIndexConstraintDB-1" };

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
  public void testPathIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testPathIndex");

    String[] filenames = { "pathindex1.xml", "pathindex2.xml" };
    String queryOptionName = "pathIndexConstraintOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
    querydef.setCriteria("pindex:Aries");

    // create handle
    DOMHandle resultsHandle = new DOMHandle();
    queryMgr.search(querydef, resultsHandle);

    // get the result
    Document resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
    assertXpathEvaluatesTo("/path-index-constraint/pathindex2.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();

    // ***********************************************
    // *** Running test path index with constraint ***
    // ***********************************************

    System.out.println("Running testPathIndexWithConstraint");

    client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
    }

    // create query options manager
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options handle
    // QueryOptionsHandle handle = new QueryOptionsHandle();
    String xmlOptions = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:constraint name='lastname'>" +
        "<search:word>" +
        "<search:element name='ln' ns=''/>" +
        "</search:word>" +
        "</search:constraint>" +
        "<search:constraint name='pindex'>" +
        "<search:range collation='http://marklogic.com/collation/' type='xs:string'>" +
        "<search:path-index>/Employee/fn</search:path-index>" +
        "</search:range>" +
        "</search:constraint>" +
        "</search:options>";
    StringHandle handle = new StringHandle(xmlOptions);

    // write query options
    optionsMgr.writeOptions("PathIndexWithConstraint", handle);

    // create query manager
    queryMgr = client.newQueryManager();

    // create query def
    querydef = queryMgr.newStringDefinition("PathIndexWithConstraint");
    querydef.setCriteria("pindex:Aries AND lastname:Yuwono");

    StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
    StructuredQueryDefinition queryPathIndex = qb.rangeConstraint("pindex", Operator.EQ, "Aries");
    StructuredQueryDefinition queryWord = qb.wordConstraint("lastname", "Yuwono");
    StructuredQueryDefinition queryFinal = qb.and(queryPathIndex, queryWord);

    // create handle
    resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();

    // ***********************************************
    // *** Running test path index on int ***
    // ***********************************************

    System.out.println("Running testPathIndexOnInt");

    String[] filenames2 = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames2) {
      writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
    }

    // create query options manager
    optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

    // create query options handle
    String xmlOptions2 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +

        "<search:constraint name='amount'>" +
        "<search:range type='xs:decimal'>" +
        "<search:path-index>//@amt</search:path-index>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:constraint name='pop'>" +
        "<search:range type='xs:int'>" +
        "<search:path-index>/root/popularity</search:path-index>" +
        "</search:range>" +
        "</search:constraint>" +
        "<search:transform-results apply='raw'/>" +
        "</search:options>";

    handle = new StringHandle(xmlOptions2);

    // write query options
    optionsMgr.writeOptions("PathIndexWithConstraint", handle);

    // create query manager
    queryMgr = client.newQueryManager();

    // create query builder
    qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
    StructuredQueryDefinition queryPathIndex1 = qb.rangeConstraint("pop", Operator.EQ, "5");
    StructuredQueryDefinition queryPathIndex2 = qb.rangeConstraint("amount", Operator.EQ, "0.1");
    queryFinal = qb.and(queryPathIndex1, queryPathIndex2);

    // create handle
    resultsHandle = new DOMHandle();
    queryMgr.search(queryFinal, resultsHandle);

    resultDoc = resultsHandle.get();

    assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
    assertXpathEvaluatesTo("/path-index-constraint/constraint1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

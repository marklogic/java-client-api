/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestQueryOptionsHandle extends BasicJavaClientREST {
  private static String dbName = "TestQueryOptionsHandleDB";
  private static String[] fNames = { "TestQueryOptionsHandleDB-1" };

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
    assertTrue("Query options is not the same", isQueryOptionsSame);

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
    assertTrue("Query options is not the same", isQueryOptionsSame);

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
    assertTrue("Query options is not the same", isQueryOptionsSame);

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

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

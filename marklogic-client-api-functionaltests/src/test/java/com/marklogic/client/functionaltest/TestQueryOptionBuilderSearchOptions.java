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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestQueryOptionBuilderSearchOptions extends BasicJavaClientREST {

  private static String dbName = "TestQueryOptionBuilderSearchOptionsDB";
  private static String[] fNames = { "TestQueryOptionBuilderSearchOptionsDB-1" };

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @Test
  public void testSearchOptions1() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, IOException
  {
    System.out.println("Running testSearchOptions1");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

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

  @AfterClass
  public static void tearDown() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

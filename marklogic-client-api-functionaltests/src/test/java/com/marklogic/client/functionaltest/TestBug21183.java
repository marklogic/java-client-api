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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class TestBug21183 extends BasicJavaClientREST {

  private static String dbName = "TestBug21183DB";
  private static String[] fNames = { "TestBug21183DB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
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
      assertTrue("Returned doc from SearchHandle has no attribute", resultDoc1.contains("<txt att=\"1\">a</txt>"));
      System.out.println();
    }

    XMLStreamReaderHandle shandle = queryMgr.search(querydef, new XMLStreamReaderHandle());
    String resultDoc2 = shandle.toString();
    System.out.println(resultDoc2);
    assertTrue("Returned doc from XMLStreamReaderHandle has no namespace", resultDoc2.contains("<test xmlns:myns=\"http://mynamespace.com\">"));
    assertTrue("Returned doc from XMLStreamReaderHandle has no attribute", resultDoc2.contains("<txt att=\"1\">a</txt>"));

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

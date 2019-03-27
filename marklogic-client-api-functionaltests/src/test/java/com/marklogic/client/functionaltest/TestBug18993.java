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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;

public class TestBug18993 extends BasicJavaClientREST {

  private static String dbName = "Bug18993DB";
  private static String[] fNames = { "Bug18993DB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    if (!IsSecurityEnabled()) {
      // Run it only in non-SSL mode.
      loadBug18993();
    }
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testBug18993() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    if (!IsSecurityEnabled()) {
      // Run it only in non-SSL mode.
      System.out.println("Running testBug18993");

      DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

      XMLDocumentManager docMgr = client.newXMLDocumentManager();

      StringHandle readHandle = new StringHandle();

      String uris[] = { "/a b" };

      String expectedXML = "<foo>a space b</foo>";

      for (String uri : uris)
      {
        System.out.println("uri = " + uri);
        docMgr.read(uri, readHandle);
        System.out.println();
        String strXML = readHandle.toString();
        System.out.print(readHandle.toString());
        assertTrue("Document is not returned", strXML.contains(expectedXML));
        System.out.println();
      }

      // release client
      client.release();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

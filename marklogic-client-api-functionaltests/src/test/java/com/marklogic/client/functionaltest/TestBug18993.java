/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Disabled
public class TestBug18993 extends BasicJavaClientREST {

  private static String dbName = "Bug18993DB";
  private static String[] fNames = { "Bug18993DB-1" };

  @BeforeAll
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    if (!IsSecurityEnabled()) {
      // Run it only in non-SSL mode.
      loadBug18993();
    }
  }

  @AfterEach
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
        Assertions.assertTrue(strXML.contains(expectedXML));
        System.out.println();
      }

      // release client
      client.release();
    }
  }

  @AfterAll
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}

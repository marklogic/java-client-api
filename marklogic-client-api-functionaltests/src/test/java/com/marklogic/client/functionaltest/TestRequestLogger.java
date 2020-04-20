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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.util.RequestLogger;

public class TestRequestLogger extends BasicJavaClientREST {

  private static String dbName = "TestRequestLoggerDB";
  private static String[] fNames = { "TestRequestLoggerDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
  }

  @Test
  public void testRequestLogger() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("testRequestLogger");

    String filename = "bbq1.xml";
    String uri = "/request-logger/";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create transaction
    Transaction transaction = client.openTransaction();

    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = uri + filename;

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create logger
    RequestLogger logger = client.newLogger(System.out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);

    // start logging
    docMgr.startLogging(logger);

    // write the document content
    docMgr.write(docId, handle, transaction);

    // commit transaction
    transaction.commit();

    // stop logging
    docMgr.stopLogging();

    String expectedContentMax = "9223372036854775807";
    assertEquals("Content log is not equal", expectedContentMax, Long.toString(logger.getContentMax()));

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

/*
 * Copyright (c) 2022 MarkLogic Corporation
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

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.util.RequestLogger;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestRequestLogger extends AbstractFunctionalTest {

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
    assertEquals( expectedContentMax, Long.toString(logger.getContentMax()));

    // release client
    client.release();
  }
}

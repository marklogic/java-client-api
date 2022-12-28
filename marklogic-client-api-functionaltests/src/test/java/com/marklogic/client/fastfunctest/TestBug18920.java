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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBug18920 extends AbstractFunctionalTest {

  private static ServerConfigurationManager configMgr;

  @BeforeAll
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    client = getDatabaseClient("rest-admin", "x", getConnType());
    // create a manager for the server configuration
    configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // require content versions for updates and deletes
    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);
    System.out.println("set optimistic locking to required");

    // write the server configuration to the database
    configMgr.writeConfiguration();
  }

  @Test
  public void testBug18920() {
    System.out.println("Running testBug18920");

    String filename = "xml-original.xml";
    String uri = "/bug18920/";
    String docId = uri + filename;

    // create document manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

    // create a handle on the content
    FileHandle handle = new FileHandle(file);
    handle.set(file);

    // create document descriptor
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    // write doc
    docMgr.write(desc, handle);

    String docUri = desc.getUri();
    System.out.println(docUri);

    String exception = "";
    String statusCode = "";
    String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version required to write document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /bug18920/xml-original.xml";
    int expCode = 0;
    // update document with no content version
    try {
      docMgr.write(docUri, handle);
    } catch (FailedRequestException e) {
      exception = e.toString();
      statusCode = e.getFailedRequest().getMessageCode();
      expCode = e.getFailedRequest().getStatusCode();
    }
    System.out.println("Exception is " + exception);
    System.out.println("Status message --- codenumber are " + statusCode + " --- " + expCode);
    assertTrue( statusCode.contains("RESTAPI-CONTENTNOVERSION"));
    assertTrue( expCode == 428);
    assertTrue( exception.contains(expectedException));
  }

  @AfterAll
  public static void tearDown() throws Exception {
    System.out.println("In tear down");

    // set content version back to none
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_OPTIONAL);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    // release client
    client.release();
  }
}

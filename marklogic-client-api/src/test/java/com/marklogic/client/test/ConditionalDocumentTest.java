/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

public class ConditionalDocumentTest {
  static DatabaseClient adminClient = Common.connectAdmin();
  static ServerConfigurationManager serverConfig;

  @BeforeClass
  public static void beforeClass()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    serverConfig = adminClient.newServerConfigManager();
    serverConfig.readConfiguration();
    serverConfig.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);
    serverConfig.writeConfiguration();

    Common.propertyWait();

    Common.connect();
  }
  @AfterClass
  public static void afterClass()
    throws FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
    serverConfig.setUpdatePolicy(UpdatePolicy.MERGE_METADATA);
    serverConfig.writeConfiguration();

    Common.propertyWait();
  }

  @Test
  public void testConditional()
    throws SAXException, IOException, ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    String docId = "/test/conditional1.xml";
    long badVersion = 11111;

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    DocumentDescriptor desc = docMgr.exists(docId);
    if (desc != null) {
      docMgr.delete(desc);
    }

    desc = docMgr.newDescriptor(docId);
    desc.setFormat(Format.XML);
    desc.setVersion(badVersion);

    StringHandle contentHandle = new StringHandle().with(GenericDocumentTest.content);

    Exception ex = null;
    int statusCode = 0;
    try {
      docMgr.write(desc, contentHandle);
    } catch (FailedRequestException e) {
      FailedRequest failreq = e.getFailedRequest();
      if (failreq != null)
        statusCode = failreq.getStatusCode();
      ex = e;
    }
    assertTrue("Write with bad version succeeded", ex != null);
    assertTrue("Write with bad version had wrong error", statusCode == 412);
    assertTrue("Write with no version had misleading message",
      ex.getMessage().contains("Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /test/conditional1.xml doesn't match if-match: 11111"));


    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, contentHandle);

    String result = docMgr.read(desc, new StringHandle()).get();
    long goodVersion = desc.getVersion();
    assertTrue("Failed to read version", goodVersion != DocumentDescriptor.UNKNOWN_VERSION);
    assertXMLEqual("Failed to read document content",result,GenericDocumentTest.content);

    desc.setVersion(badVersion);
    assertTrue("Read with bad version did not get content",
      docMgr.read(desc, new StringHandle()).get() != null);

    desc.setVersion(goodVersion);
    assertTrue("Read with good version did not skip content",
      docMgr.read(desc, new StringHandle()) == null);

    ex = null;
    statusCode = 0;
    // test with string to expose problem for bug 18920
    String documentUri = desc.getUri();
    try {
      docMgr.write(documentUri, contentHandle);
    } catch (FailedRequestException e) {
      FailedRequest failreq = e.getFailedRequest();
      if (failreq != null)
        statusCode = failreq.getStatusCode();
      ex = e;
    }
    assertTrue("Overwrite without version succeeded", ex != null);
    assertEquals("Write with no version had wrong error", 428, statusCode);
    assertEquals("Write with no version had misleading message",
      "Local message: Content version required to write document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /test/conditional1.xml",
      ex.getMessage());

    ex = null;
    statusCode = 0;
    try {
      desc.setVersion(badVersion);
      docMgr.write(desc, contentHandle);
    } catch (FailedRequestException e) {
      FailedRequest failreq = e.getFailedRequest();
      if (failreq != null)
        statusCode = failreq.getStatusCode();
      ex = e;
    }
    assertTrue("Overwrite with bad version succeeded", ex != null);
    assertEquals("Write with bad version had wrong error", 412, statusCode);
    assertTrue("Write with no version had misleading message",
      ex.getMessage().contains("Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /test/conditional1.xml has current version"));

    desc.setVersion(goodVersion);
    docMgr.write(desc, contentHandle);

    desc = docMgr.exists(docId);
    assertTrue("Exists did not get version", desc.getVersion() != DocumentDescriptor.UNKNOWN_VERSION);
    assertTrue("Overwrite did not change version", goodVersion != desc.getVersion());
    goodVersion = desc.getVersion();

    ex = null;
    statusCode = 0;
    try {
      desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      FailedRequest failreq = e.getFailedRequest();
      if (failreq != null)
        statusCode = failreq.getStatusCode();
      ex = e;
    }
    assertTrue("Delete without version succeeded", ex != null);
    assertEquals("Delete without version had wrong error", 428, statusCode);

    ex = null;
    // TODO: statusCode
    try {
      desc.setVersion(badVersion);
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      ex = e;
    }
    assertTrue("Delete with bad version succeeded", ex != null);

    try {
      docMgr.delete(documentUri); // internal documentdescriptor
    } catch (FailedRequestException e) {
      ex = e;
    }
    assertTrue("Delete with no version succeeded", ex != null);
    assertEquals("Delete with no version had misleading message",
      "Local message: Content version required to delete document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /test/conditional1.xml",
      ex.getMessage());


    desc.setVersion(goodVersion);
    docMgr.delete(desc);
  }
}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.*;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionalDocumentTest {
  static ServerConfigurationManager serverConfig;

  @BeforeAll
  public static void beforeClass()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    serverConfig = Common.connectRestAdmin().newServerConfigManager();
    serverConfig.readConfiguration();
    serverConfig.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);
    serverConfig.writeConfiguration();

    Common.propertyWait();

    Common.connect();
  }
  @AfterAll
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
    assertTrue( ex != null);
    assertTrue( statusCode == 412);
    assertTrue(
      ex.getMessage().contains("Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /test/conditional1.xml doesn't match if-match: 11111"));


    desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
    docMgr.write(desc, contentHandle);

    String result = docMgr.read(desc, new StringHandle()).get();
    long goodVersion = desc.getVersion();
    assertTrue( goodVersion != DocumentDescriptor.UNKNOWN_VERSION);
    assertXMLEqual("Failed to read document content",result,GenericDocumentTest.content);

    desc.setVersion(badVersion);
    assertTrue(
      docMgr.read(desc, new StringHandle()).get() != null);

    desc.setVersion(goodVersion);
    assertTrue(
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
    assertTrue( ex != null);
    assertEquals( 428, statusCode);
    assertEquals(
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
    assertTrue( ex != null);
    assertEquals( 412, statusCode);
    assertTrue(
      ex.getMessage().contains("Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION: (err:FOER0000) Content version mismatch:  uri /test/conditional1.xml has current version"));

    desc.setVersion(goodVersion);
    docMgr.write(desc, contentHandle);

    desc = docMgr.exists(docId);
    assertTrue( desc.getVersion() != DocumentDescriptor.UNKNOWN_VERSION);
    assertTrue( goodVersion != desc.getVersion());
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
    assertTrue( ex != null);
    assertEquals( 428, statusCode);

    ex = null;
    // TODO: statusCode
    try {
      desc.setVersion(badVersion);
      docMgr.delete(desc);
    } catch (FailedRequestException e) {
      ex = e;
    }
    assertTrue( ex != null);

    try {
      docMgr.delete(documentUri); // internal documentdescriptor
    } catch (FailedRequestException e) {
      ex = e;
    }
    assertTrue( ex != null);
    assertEquals(
      "Local message: Content version required to delete document. Server Message: RESTAPI-CONTENTNOVERSION: (err:FOER0000) No content version supplied:  uri /test/conditional1.xml",
      ex.getMessage());


    desc.setVersion(goodVersion);
    docMgr.delete(desc);
  }

  @Test
  public void testConditionalMultiple() {
    String[] docIds = {"/sample/first.xml", "/sample/fourth.xml"};
    List<String> docList = Arrays.asList(docIds);

    XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();

    verifyDescriptors(docList, docMgr.read(docIds));

    verifyDescriptors(
            docList,
            docMgr.search(new StructuredQueryBuilder().document(docIds), 1)
    );
  }
  void verifyDescriptors(List<String> docList, DocumentPage page) {
    for (DocumentRecord record: page) {
      DocumentDescriptor desc = record.getDescriptor();
      assertTrue( docList.contains(desc.getUri()));
      assertEquals( Format.XML, desc.getFormat());
      assertTrue( desc.getMimetype().startsWith("application/xml"));
      assertTrue( desc.getByteLength() >= 0);
      assertTrue( desc.getVersion() >= -1);
    }
  }
}

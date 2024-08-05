/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TransformExtensionsTest {
  final static String XQUERY_NAME = "testxqy";
  final static String XSLT_NAME   = "testxsl";
  final static String XQUERY_FILE = XQUERY_NAME + ".xqy";
  final static String XSLT_FILE   = XSLT_NAME + ".xsl";

  static private String      xqueryTransform;
  static private String      xslTransform;
  static private XpathEngine xpather;

  @BeforeAll
  public static void beforeClass() throws IOException {
    Common.connect();
    Common.connectRestAdmin();

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalize(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

    Map<String,String> namespaces = new HashMap<>();
    namespaces.put("xsl",  "http://www.w3.org/1999/XSL/Transform");
    namespaces.put("rapi", "http://marklogic.com/rest-api");

    SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

    xpather = XMLUnit.newXpathEngine();
    xpather.setNamespaceContext(namespaceContext);

    xqueryTransform = Common.testFileToString(XQUERY_FILE, "UTF-8");
    xslTransform    = Common.testFileToString(XSLT_FILE, "UTF-8");
  }
  @AfterAll
  public static void afterClass() {
    xqueryTransform = null;
    xslTransform    = null;
  }

  static ExtensionMetadata makeXQueryMetadata() {
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Document XQuery Transform");
    metadata.setDescription("This plugin adds an attribute to the root element");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    return metadata;
  }

  static ExtensionMetadata makeXSLTMetadata() {
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Document XSLT Transform");
    metadata.setDescription("This plugin adds an attribute to the root element");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    return metadata;
  }

  @Test
  public void testTransformExtensions()
    throws XpathException, SAXException, IOException, FailedRequestException, ResourceNotFoundException, ForbiddenUserException, ResourceNotResendableException
  {
    TransformExtensionsManager extensionMgr =
      Common.restAdminClient.newServerConfigManager().newTransformExtensionsManager();

    StringHandle handle = new StringHandle();
    handle.setFormat(Format.TEXT);

    writeXQueryTransform(extensionMgr);

    writeXSLTransform(extensionMgr);

    extensionMgr.readXQueryTransform(XQUERY_NAME, handle);
    assertEquals( xqueryTransform, handle.get());

    String result = extensionMgr.readXSLTransform(XSLT_NAME, new StringHandle()).get();
    assertNotNull( result);
    assertTrue( xpather.getMatchingNodes(
      "/xsl:stylesheet",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);

    result = extensionMgr.listTransforms(new StringHandle().withFormat(Format.XML), true).get();
    assertNotNull( result);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:transforms/rapi:transform/rapi:name[string(.) = 'testxqy']",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:transforms/rapi:transform/rapi:name[string(.) = 'testxsl']",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);

    extensionMgr.deleteTransform(XQUERY_NAME);
    boolean transformDeleted = true;

    try {
      handle = new StringHandle();
      extensionMgr.readXQueryTransform(XQUERY_NAME, handle);
      result = handle.get();
      transformDeleted = (result == null);
    } catch(FailedRequestException ex) {
    }
    assertTrue( transformDeleted);

    extensionMgr.deleteTransform(XSLT_NAME);

    try {
      handle = new StringHandle();
      extensionMgr.readXSLTransform(XSLT_NAME, handle);
// TODO: INVESTIGATE
      result = handle.get();
      transformDeleted = (result == null || result.length() == 0);
    } catch(FailedRequestException ex) {
    }

    assertTrue( transformDeleted);
  }
  public void writeXQueryTransform(TransformExtensionsManager extensionMgr)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    extensionMgr.writeXQueryTransform(
      XQUERY_NAME,
      new StringHandle().withFormat(Format.TEXT).with(xqueryTransform),
      makeXQueryMetadata()
    );
  }
  public void writeXSLTransform(TransformExtensionsManager extensionMgr)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
    extensionMgr.writeXSLTransform(
      XSLT_NAME,
      new StringHandle().withFormat(Format.XML).withMimetype("application/xslt+xml").with(xslTransform),
      makeXSLTMetadata()
    );
  }
}

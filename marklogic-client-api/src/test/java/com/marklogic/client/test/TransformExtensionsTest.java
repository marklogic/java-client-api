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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.io.Format;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.io.StringHandle;
import java.util.Map;

public class TransformExtensionsTest {
  final static String XQUERY_NAME = "testxqy";
  final static String XSLT_NAME   = "testxsl";
  final static String XQUERY_FILE = XQUERY_NAME + ".xqy";
  final static String XSLT_FILE   = XSLT_NAME + ".xsl";

  static private String      xqueryTransform;
  static private String      xslTransform;
  static private XpathEngine xpather;

  @BeforeClass
  public static void beforeClass() throws IOException {
    Common.connect();
    Common.connectAdmin();

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
  @AfterClass
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
      Common.adminClient.newServerConfigManager().newTransformExtensionsManager();

    StringHandle handle = new StringHandle();
    handle.setFormat(Format.TEXT);

    writeXQueryTransform(extensionMgr);

    writeXSLTransform(extensionMgr);

    extensionMgr.readXQueryTransform(XQUERY_NAME, handle);
    assertEquals("Failed to retrieve XQuery transform", xqueryTransform, handle.get());

    String result = extensionMgr.readXSLTransform(XSLT_NAME, new StringHandle()).get();
    assertNotNull("Failed to retrieve XSLT transform", result);
    assertTrue("Did not recognize XSLT transform", xpather.getMatchingNodes(
      "/xsl:stylesheet",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);

    result = extensionMgr.listTransforms(new StringHandle().withFormat(Format.XML), true).get();
    assertNotNull("Failed to retrieve transforms list", result);
    assertTrue("List without XQuery transform", xpather.getMatchingNodes(
      "/rapi:transforms/rapi:transform/rapi:name[string(.) = 'testxqy']",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);
    assertTrue("List without XSLT transform", xpather.getMatchingNodes(
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
    assertTrue("Failed to delete XQuery transform", transformDeleted);

    extensionMgr.deleteTransform(XSLT_NAME);

    try {
      handle = new StringHandle();
      extensionMgr.readXSLTransform(XSLT_NAME, handle);
// TODO: INVESTIGATE
      result = handle.get();
      transformDeleted = (result == null || result.length() == 0);
    } catch(FailedRequestException ex) {
    }

    assertTrue("Failed to delete XSLT transform", transformDeleted);
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

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
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

public class ResourceExtensionsTest {
  final static String RESOURCE_NAME = "testresource";
  final static String XQUERY_FILE   = RESOURCE_NAME + ".xqy";

  static private String      resourceServices;
  static private XpathEngine xpather;

  @BeforeAll
  public static void beforeClass() throws IOException {
    Common.connectRestAdmin();
    resourceServices = Common.testFileToString(XQUERY_FILE);

    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalize(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

    Map<String,String> namespaces = new HashMap<>();
    namespaces.put("rapi", "http://marklogic.com/rest-api");

    SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(namespaces);

    xpather = XMLUnit.newXpathEngine();
    xpather.setNamespaceContext(namespaceContext);
  }
  @AfterAll
  public static void afterClass() {
    resourceServices = null;
  }

  static ExtensionMetadata makeMetadata() {
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Test Resource Services");
    metadata.setDescription("This library supports all methods on the test resource");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    return metadata;
  }

  static MethodParameters[] makeParameters() {
    MethodType[] methods = MethodType.values();
    MethodParameters[] params = new MethodParameters[methods.length];
    for (int i=0; i < methods.length; i++) {
      params[i] = new MethodParameters(methods[i]);
      params[i].put("value", "xs:boolean");
    }
    return params;
  }

  @Test
  public void testResourceServiceExtension() throws XpathException, SAXException, IOException {
    ResourceExtensionsManager extensionMgr =
      Common.restAdminClient.newServerConfigManager().newResourceExtensionsManager();

    StringHandle handle = new StringHandle();

    ExtensionMetadata metadata = makeMetadata();
    MethodParameters[] params = makeParameters();

    handle.set(resourceServices);
    extensionMgr.writeServices(RESOURCE_NAME, handle, metadata, params);

    extensionMgr.readServices(RESOURCE_NAME, handle);
    assertEquals( resourceServices, handle.get());

    String result = extensionMgr.listServices(new StringHandle().withFormat(Format.XML), true).get();
    assertNotNull( result);
    assertTrue( xpather.getMatchingNodes(
      "/rapi:resources/rapi:resource/rapi:name[string(.) = 'testresource']",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);

    extensionMgr.deleteServices(RESOURCE_NAME);

    result = extensionMgr.readServices(RESOURCE_NAME, handle).get();
    assertTrue( result == null);
  }
}

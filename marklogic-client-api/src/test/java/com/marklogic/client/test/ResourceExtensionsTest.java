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
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import java.util.Map;

public class ResourceExtensionsTest {
  final static String RESOURCE_NAME = "testresource";
  final static String XQUERY_FILE   = RESOURCE_NAME + ".xqy";

  static private String      resourceServices;
  static private XpathEngine xpather;

  @BeforeClass
  public static void beforeClass() throws IOException {
    Common.connectAdmin();
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
  @AfterClass
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
      Common.adminClient.newServerConfigManager().newResourceExtensionsManager();

    StringHandle handle = new StringHandle();

    ExtensionMetadata metadata = makeMetadata();
    MethodParameters[] params = makeParameters();

    handle.set(resourceServices);
    extensionMgr.writeServices(RESOURCE_NAME, handle, metadata, params);

    extensionMgr.readServices(RESOURCE_NAME, handle);
    assertEquals("Failed to retrieve resource services", resourceServices, handle.get());

    String result = extensionMgr.listServices(new StringHandle().withFormat(Format.XML), true).get();
    assertNotNull("Failed to retrieve resource services list", result);
    assertTrue("List without resource", xpather.getMatchingNodes(
      "/rapi:resources/rapi:resource/rapi:name[string(.) = 'testresource']",
      XMLUnit.buildControlDocument(result)
    ).getLength() == 1);

    extensionMgr.deleteServices(RESOURCE_NAME);

    result = extensionMgr.readServices(RESOURCE_NAME, handle).get();
    assertTrue("Failed to delete resource services", result == null);
  }
}

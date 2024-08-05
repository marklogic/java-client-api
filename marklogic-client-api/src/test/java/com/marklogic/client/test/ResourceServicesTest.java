/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.RequestParameters;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.*;

public class ResourceServicesTest {
  static private String resourceServices;

  @BeforeAll
  public static void beforeClass() throws IOException {
    Common.connectRestAdmin();
    resourceServices = Common.testFileToString(ResourceExtensionsTest.XQUERY_FILE, "UTF-8");
  }
  @AfterAll
  public static void afterClass() {
    resourceServices = null;
  }

  @Test
  public void testResourceServices() throws XpathException {
    ResourceExtensionsManager extensionMgr =
      Common.restAdminClient.newServerConfigManager().newResourceExtensionsManager();

    extensionMgr.writeServices(
      ResourceExtensionsTest.RESOURCE_NAME,
      new StringHandle().withFormat(Format.TEXT).with(resourceServices),
      ResourceExtensionsTest.makeMetadata(),
      ResourceExtensionsTest.makeParameters()
    );

    SimpleResourceManager resourceMgr =
      Common.restAdminClient.init(ResourceExtensionsTest.RESOURCE_NAME, new SimpleResourceManager());

    RequestParameters params = new RequestParameters();
    params.put("value", "true");

    Document result = resourceMgr.getResourceServices().get(params, new DOMHandle()).get();
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/read-doc/param", result);

    ServiceResultIterator resultItr = resourceMgr.getResourceServices().get(params);

    List<Document> resultDocuments = new ArrayList<>();
    DOMHandle readHandle = new DOMHandle();
    while (resultItr.hasNext()) {
      resultDocuments.add(
        resultItr.next().getContent(readHandle).get()
      );
    }

    resultItr.close();

    int size = resultDocuments.size();
    assertTrue( size == 2);
    result = resultDocuments.get(0);
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/read-doc/param", result);
    result = resultDocuments.get(1);
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/read-multi-doc/multi-param", result);

    StringHandle writeHandle =
      new StringHandle().withFormat(Format.XML).with("<input-doc>true</input-doc>");

    result = resourceMgr.getResourceServices().put(params, writeHandle, new DOMHandle()).get();
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/wrote-doc/param", result);
    assertXpathEvaluatesTo("true", "/wrote-doc/input-doc", result);

    StringHandle[] writeHandles = new StringHandle[2];
    writeHandles[0] = new StringHandle().withFormat(Format.XML).with("<input-doc>true</input-doc>");
    writeHandles[1] = new StringHandle().withFormat(Format.XML).with("<multi-input-doc>true</multi-input-doc>");

    result = resourceMgr.getResourceServices().put(params, writeHandles, new DOMHandle()).get();
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/wrote-doc/param", result);
    assertXpathEvaluatesTo("true", "/wrote-doc/input-doc", result);
    assertXpathEvaluatesTo("true", "/wrote-doc/multi-input-doc", result);

    result = resourceMgr.getResourceServices().post(params, writeHandle, new DOMHandle()).get();
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/applied-doc/param", result);
    assertXpathEvaluatesTo("true", "/applied-doc/input-doc", result);

    resultItr = resourceMgr.getResourceServices().post(params, writeHandles);

    resultDocuments = new ArrayList<>();
    readHandle = new DOMHandle();
    while (resultItr.hasNext()) {
      resultDocuments.add(
        resultItr.next().getContent(readHandle).get()
      );
    }

    resultItr.close();

    size = resultDocuments.size();
    assertTrue( size == 2);
    result = resultDocuments.get(0);
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/applied-doc/param", result);
    result = resultDocuments.get(1);
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/applied-multi-doc/multi-param", result);

    result = resourceMgr.getResourceServices().delete(params, new DOMHandle()).get();
    assertNotNull( result);
    assertXpathEvaluatesTo("true", "/deleted-doc/param", result);

    extensionMgr.deleteServices(ResourceExtensionsTest.RESOURCE_NAME);
  }

  @Test
  /** Avoid regression on https://github.com/marklogic/java-client-api/issues/172 */
  public void test_172() {
    ResourceExtensionsManager extensionMgr =
      Common.restAdminClient.newServerConfigManager().newResourceExtensionsManager();
    Common.restAdminClient.release();
    // since we released the existing connection, clear it out
    Common.restAdminClient = null;
    String expectedMessage = "You cannot use this connected object anymore--connection has already been released";
    try { extensionMgr.writeServices(ResourceExtensionsTest.RESOURCE_NAME, null, null);
    } catch (IllegalStateException e) { assertEquals( expectedMessage, e.getMessage()); }
    try { extensionMgr.readServices(ResourceExtensionsTest.RESOURCE_NAME, new StringHandle());
    } catch (IllegalStateException e) { assertEquals( expectedMessage, e.getMessage()); }
    try { extensionMgr.listServices(new DOMHandle());
    } catch (IllegalStateException e) { assertEquals( expectedMessage, e.getMessage()); }
    try { extensionMgr.deleteServices(ResourceExtensionsTest.RESOURCE_NAME);
    } catch (IllegalStateException e) { assertEquals( expectedMessage, e.getMessage()); }
  }

  @Test
  /** Avoid regression on https://github.com/marklogic/java-client-api/issues/761 */
  public void test_issue_761() {
    DatabaseClient client = Common.newClientBuilder().withDatabase("Documents").build();
    try {
      client.newServerConfigManager().newResourceExtensionsManager()
        .listServices(new DOMHandle());
    } finally {
      client.release();
    }
  }

  static class SimpleResourceManager extends ResourceManager {
    public SimpleResourceManager() {
      super();
    }
    // a real ResourceManager would provide a facade over services
    public ResourceServices getResourceServices() {
      return getServices();
    }
  }
}

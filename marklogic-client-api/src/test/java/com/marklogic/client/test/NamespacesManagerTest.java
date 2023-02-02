/*
 * Copyright (c) 2023 MarkLogic Corporation
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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.util.EditableNamespaceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NamespacesManagerTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connectRestAdmin();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testWriteReadPrefix()
    throws ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    NamespacesManager nsMgr =
      Common.restAdminClient.newServerConfigManager().newNamespacesManager();

    nsMgr.updatePrefix("dc", "http://purl.org/dc/terms/");

    String nsUri = nsMgr.readPrefix("dc");
    assertEquals( nsUri, "http://purl.org/dc/terms/");

    nsMgr.updatePrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    nsMgr.updatePrefix("skos", "http://www.w3.org/2004/02/skos/core#");

    EditableNamespaceContext context = (EditableNamespaceContext) nsMgr.readAll();

    int initialSize = context.size();
    assertTrue( initialSize >= 3);
    assertEquals(
      "http://purl.org/dc/terms/",
      context.get("dc"));
    assertEquals(
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
      context.get("rdf"));
    assertEquals(
      "http://www.w3.org/2004/02/skos/core#",
      context.get("skos"));

    nsMgr.updatePrefix("dc", "http://diverted/category/");

    nsUri = nsMgr.readPrefix("dc");
    assertEquals( nsUri, "http://diverted/category/");

    nsMgr.deletePrefix("dc");
    context = (EditableNamespaceContext) nsMgr.readAll();
    // assumes no concurrent deletes
    assertEquals( initialSize - 1, context.size());

    nsMgr.deleteAll();
    context = (EditableNamespaceContext) nsMgr.readAll();
    assertTrue(
      context == null || context.size() == 0);
  }

  @Test
  public void testExceptions()
    throws ForbiddenUserException, FailedRequestException, ResourceNotFoundException
  {
    NamespacesManager nsMgr =
      Common.restAdminClient.newServerConfigManager().newNamespacesManager();

    boolean illegalArgument = false;
    try {
      nsMgr.updatePrefix(javax.xml.XMLConstants.DEFAULT_NS_PREFIX, "http://invalid");
    } catch (IllegalArgumentException e) {
      illegalArgument = true;
    }
    assertTrue( illegalArgument);

    illegalArgument = false;
    try {
      nsMgr.addPrefix(javax.xml.XMLConstants.DEFAULT_NS_PREFIX, "http://invalid");
    } catch (IllegalArgumentException e) {
      illegalArgument = true;
    }
    assertTrue( illegalArgument);
  }
}

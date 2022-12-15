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


// This test needs to be removed/deleted when NamespaceManager Class is taken out of Java Client API package.
package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import org.junit.Test;

import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestNamespaces extends AbstractFunctionalTest {

  @Test
  public void testNamespaces() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testNamespaces");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create namespaces manager
    NamespacesManager nsMgr = client.newServerConfigManager().newNamespacesManager();

    // create logger
    RequestLogger logger = client.newLogger(System.out);
    logger.setContentMax(RequestLogger.ALL_CONTENT);

    // start logging
    nsMgr.startLogging(logger);

    // add prefix
    nsMgr.addPrefix("foo", "http://example.com");

    NamespaceContext nsContext = nsMgr.readAll();

    assertEquals("Prefix is not equal", "foo", nsContext.getPrefix("http://example.com"));
    assertEquals("Namespace URI is not equal", "http://example.com", nsContext.getNamespaceURI("foo"));

    // update prefix
    nsMgr.updatePrefix("foo", "http://exampleupdated.com");
    nsContext = nsMgr.readAll();
    assertEquals("Updated Namespace URI is not equal", "http://exampleupdated.com", nsContext.getNamespaceURI("foo"));

    // stop logging
    nsMgr.stopLogging();

    String expectedLogContentMax = "9223372036854775807";
    assertEquals("Content log is not equal", expectedLogContentMax, Long.toString(logger.getContentMax()));

    // delete prefix
    nsMgr.deletePrefix("foo");
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("foo") == null);

    nsMgr.deleteAll();
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("foo") == null);

    // release client
    client.release();
  }

  @Test
  public void testDefaultNamespaces() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testDefaultNamespaces");

    // connect the client
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // create namespaces manager
    NamespacesManager nsMgr = client.newServerConfigManager().newNamespacesManager();

    // add namespaces
    nsMgr.addPrefix("ns1", "http://foo.com");
    nsMgr.addPrefix("ns2", "http://bar.com");
    nsMgr.addPrefix("ns3", "http://baz.com");

    nsMgr.readAll();

    // set default namespace
    nsMgr.updatePrefix("defaultns", "http://baz.com");
    String defaultNsUri = nsMgr.readPrefix("defaultns");
    assertEquals("Default NS is wrong", "http://baz.com", defaultNsUri);

    // delete namespace
    nsMgr.deletePrefix("baz");
    nsMgr.readAll();

    // get default namespace
    assertEquals("Default NS is wrong", "http://baz.com", nsMgr.readPrefix("defaultns"));

    nsMgr.deleteAll();
    nsMgr.readAll();
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns1") == null);
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns2") == null);
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("ns3") == null);
    assertTrue("Namespace URI is not deleted", nsMgr.readPrefix("defaultns") == null);

    // release client
    client.release();
  }

  @Test
  public void testBug22396() throws KeyManagementException, NoSuchAlgorithmException, IOException {

    System.out.println("Runing testBug22396");

    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    // write docs
    writeDocumentUsingInputStreamHandle(client, "constraint1.xml", "/testBug22396/", "XML");

    String docId = "/testBug22396/constraint1.xml";

    // create document manager
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();

    // get namespaces
    Collection<String> nameSpaceCollection = patchBldr.getNamespaces().getAllPrefixes();
    assertEquals("getNamespace failed ", false, nameSpaceCollection.isEmpty());
    for (String prefix : nameSpaceCollection) {
      System.out.println("Prefixes : " + prefix);
      System.out.println(patchBldr.getNamespaces().getNamespaceURI(prefix));
    }
    // set namespace
    EditableNamespaceContext namespaces = new EditableNamespaceContext();
    namespaces.put("new", "http://www.marklogic.com");
    patchBldr.setNamespaces(namespaces);
    System.out.println("\n Namespace Output : " + patchBldr.getNamespaces().getNamespaceURI("xmlns") + "\n Next xml : " + patchBldr.getNamespaces().getNamespaceURI("xml")
        + "\n Next xs : " + patchBldr.getNamespaces().getNamespaceURI("xs") + "\n Next xsi : " + patchBldr.getNamespaces().getNamespaceURI("xsi") + "\n Next rapi : "
        + patchBldr.getNamespaces().getNamespaceURI("rapi") + "\n Next new : " + patchBldr.getNamespaces().getNamespaceURI("new"));
    String content = docMgr.read(docId, new StringHandle()).get();
    assertTrue("setNamespace didn't worked", patchBldr.getNamespaces().getNamespaceURI("new").contains("www.marklogic.com"));
    System.out.println(content);

    // release client
    client.release();
  }
}

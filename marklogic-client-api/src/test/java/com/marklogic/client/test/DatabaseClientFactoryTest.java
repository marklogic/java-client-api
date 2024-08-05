/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.*;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseClientFactoryTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connectEval();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testConnectStringIntStringStringDigest() {
    assertNotNull( Common.evalClient);
  }

  @Test
  public void testRuntimeDatabaseSelection() throws SAXException, IOException {
//  final String FIRST_DB_NAME  = "java-unittest";
//  final String SECOND_DB_NAME = "java-unittest";
    final String FIRST_DB_NAME  = "Triggers";
    final String SECOND_DB_NAME = "Documents";
    DatabaseClient tmpClient = Common.newEvalClient(FIRST_DB_NAME);
    try {
      assertNotNull( tmpClient);
      String database =
        tmpClient.newServerEval()
          .xquery("xdmp:database-name(xdmp:database())")
          .evalAs(String.class);
      assertEquals( FIRST_DB_NAME, database);
    } finally {
      tmpClient.release();
    }

    tmpClient = Common.newEvalClient(SECOND_DB_NAME);
    try {
      assertNotNull( tmpClient);
      String database =
        tmpClient.newServerEval()
          .xquery("xdmp:database-name(xdmp:database())")
          .evalAs(String.class);
      assertEquals( SECOND_DB_NAME, database);
/*
      QueryManager fixupQueryMgr = tmpClient.newQueryManager();
      DeleteQueryDefinition delQuery = fixupQueryMgr.newDeleteDefinition();
      delQuery.setDirectory("/test/");
      fixupQueryMgr.delete(delQuery);
 */
      XMLDocumentManager runtimeDbDocMgr = tmpClient.newXMLDocumentManager();
      // test that doc creation happens in the Documents db
      String docContents = "<a>hello</a>";
      DocumentUriTemplate template = runtimeDbDocMgr.newDocumentUriTemplate(".xml").withDirectory("/test/");
      StringHandle handle = new StringHandle(docContents).withFormat(Format.XML);
      DocumentDescriptor createDesc = runtimeDbDocMgr.create(template, handle);
      String docUri = createDesc.getUri();
      // a reusable server eval call from the java-unittest db which just gets the doc contents in the Documents db
      ServerEvaluationCall getHello =
        Common.evalClient.newServerEval()
          .javascript("xdmp.eval('fn.doc(\"" + docUri + "\")', " +
            "null, {database:xdmp.database(\"" + SECOND_DB_NAME + "\")})");
      // make sure we can see the doc
      String value = getHello.evalAs(String.class);
      assertXMLEqual("Doc contents incorrect", docContents, value);

      // test that the doc exists via the DocumentManager api
      DocumentDescriptor existsDesc = runtimeDbDocMgr.exists(docUri);
      assertNotNull(existsDesc);

      // test overwriting the contents of the doc
      String docContents2 = "<a>hello2</a>";
      runtimeDbDocMgr.write(docUri, new StringHandle(docContents2).withFormat(Format.XML));

      // use read to make sure the doc got the update
      String value2 = runtimeDbDocMgr.readAs(docUri, String.class);
      assertXMLEqual("Doc contents incorrect", docContents2, value2);

      // test searching
      QueryManager runtimeDbQueryMgr = tmpClient.newQueryManager();
      StringQueryDefinition query = runtimeDbQueryMgr.newStringDefinition();
      query.setCriteria("hello2");
      query.setDirectory("/test/");
      MatchDocumentSummary match = runtimeDbQueryMgr.findOne(query);
      assertNotNull( match);
      assertEquals( docUri, match.getUri());

      // test patching
      String newValue = "new value";
      String docContents3 = "<a>" + newValue + "</a>";
      DocumentPatchBuilder patchBldr = runtimeDbDocMgr.newPatchBuilder();
      patchBldr.replaceValue("/a", newValue);
      DocumentPatchHandle patchHandle = patchBldr.build();
      runtimeDbDocMgr.patch(docUri, patchHandle);

      // use bulk read to make sure the doc got the update
      DocumentPage docs = runtimeDbDocMgr.read(docUri);
      assertNotNull( docs);
      assertTrue( docs.hasNext());
      String value3 = docs.next().getContent(new StringHandle()).get();
      assertXMLEqual("Doc contents incorrect", docContents3, value3);

      // test deleting the doc
      runtimeDbDocMgr.delete(docUri);
      boolean deleted = false;
      try {
        runtimeDbDocMgr.readAs(docUri, String.class);
      } catch (ResourceNotFoundException e) {
        deleted = true;
      }
      assertTrue( deleted);
      String value4 = getHello.evalAs(String.class);
      assertEquals( null, value4);

    } finally {
      tmpClient.release();
    }
  }

  static int testConnectTimeoutMillis = 123456;
  @Test
  public void testConfigurator() {
    ConfiguratorImpl configurator = new ConfiguratorImpl();

    DatabaseClientFactory.addConfigurator(configurator);

	DatabaseClient client = Common.newClientBuilder().build();
    try {
      assertTrue( configurator.isConfigured);
      OkHttpClient okClient = (OkHttpClient) client.getClientImplementation();
      assertEquals(testConnectTimeoutMillis, okClient.connectTimeoutMillis());
    } finally {
      client.release();
	  DatabaseClientFactory.removeConfigurators();
    }
  }

  static class ConfiguratorImpl implements OkHttpClientConfigurator {
    public boolean isConfigured = false;
    @Override
    public void configure(OkHttpClient.Builder clientBldr) {
      if (clientBldr != null) {
        isConfigured = true;
        clientBldr.connectTimeout(testConnectTimeoutMillis, TimeUnit.MILLISECONDS);
      }
    }
  }
}

/*
 * Copyright 2015-2018 MarkLogic Corporation
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
package com.marklogic.client.test.datamovement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.test.Common;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplyTransformTest {
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static GenericDocumentManager docMgr = client.newDocumentManager();
  private static QueryManager queryMgr = client.newQueryManager();
  private static StructuredQueryBuilder sqb = new StructuredQueryBuilder();
  private static String collection = "ApplyTransformTest_" +
    new Random().nextInt(10000);
  private static String docContents = "";
  private static String transformName1 = "WriteBatcherTest_transform.sjs";
  private static String transformName2 = "ApplyTransformTest_result_ignore_transform.sjs";

  @BeforeClass
  public static void beforeClass() {
    installModule();
  }

  @AfterClass
  public static void afterClass() {
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
//    queryMgr.delete(deleteQuery);
  }

  public static void installModule() {
    Common.newAdminClient().newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
      transformName1, new FileHandle(new File("src/test/resources/" + transformName1)));
    Common.newAdminClient().newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
      transformName2, new FileHandle(new File("src/test/resources/" + transformName2)));
  }

  @Test
  public void testResultReplace() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
    // write the document
    client.newDocumentManager().writeAs(collection + "/test1.json", meta, "{ \"testProperty\": \"test1\" }");

    StructuredQueryDefinition query = sqb.value(sqb.jsonProperty("testProperty"), "test1");

    ServerTransform transform = new ServerTransform(transformName1)
      .addParameter("newValue", "test1a");
    ApplyTransformListener listener = new ApplyTransformListener()
      .withTransform(transform)
      .withApplyResult(ApplyResult.REPLACE);
    QueryBatcher batcher = moveMgr.newQueryBatcher(query)
      .onUrisReady(listener);
    JobTicket ticket = moveMgr.startJob( batcher );
    batcher.awaitCompletion();
    moveMgr.stopJob(ticket);

    JsonNode docContents = docMgr.readAs(collection + "/test1.json", JsonNode.class);
    assertEquals( "the transform should have changed testProperty to 'test1a'",
      "test1a", docContents.get("testProperty").textValue() );
  }

  @Test
  public void testResultIgnore() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
    // write the document
    client.newDocumentManager().writeAs(collection + "/test2.json", meta, "{ \"testProperty\": \"test2\" }");

    StructuredQueryDefinition query = sqb.value(sqb.jsonProperty("testProperty"), "test2");
    ServerTransform transform = new ServerTransform(transformName2)
      .addParameter("newValue", "test2a");
    ApplyTransformListener listener = new ApplyTransformListener()
      .withTransform(transform)
      .withApplyResult(ApplyResult.IGNORE);
    QueryBatcher batcher = moveMgr.newQueryBatcher(query)
      .onUrisReady(listener);
    JobTicket ticket = moveMgr.startJob( batcher );
    batcher.awaitCompletion();
    moveMgr.stopJob(ticket);

    JsonNode docContents = docMgr.readAs(collection + "/test2.json", JsonNode.class);
    assertEquals( "the transform should have changed testProperty to 'test2a'",
      "test2a", docContents.get("testProperty").textValue() );
  }

  @Test
  public void testManyDocs() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
    int numDocs = 1000;
    // write the documents
    WriteBatcher batcher1 = moveMgr.newWriteBatcher()
      .withBatchSize(100)
      .onBatchFailure((batch, throwable) -> throwable.printStackTrace());
    JobTicket ticket1 = moveMgr.startJob( batcher1 );
    for ( int i=0; i < numDocs; i++) {
      batcher1.addAs(collection + "/test_doc_" + i + ".json", meta, "{ \"testProperty\": \"test3\" }");
    }
    batcher1.flushAndWait();
    moveMgr.stopJob(ticket1);


    StructuredQueryDefinition query2 = sqb.value(sqb.jsonProperty("testProperty"), "test3");
    query2.setCollections(collection);
    final AtomicInteger count2 = new AtomicInteger(0);
    ServerTransform transform = new ServerTransform(transformName1)
      .addParameter("newValue", "test3a");
    ApplyTransformListener listener = new ApplyTransformListener()
      .withTransform(transform)
      .withApplyResult(ApplyResult.REPLACE)
      .onSuccess(batch -> count2.addAndGet(batch.getItems().length))
      .onBatchFailure((batch, throwable) -> throwable.printStackTrace());
    QueryBatcher batcher = moveMgr.newQueryBatcher(query2)
      .onUrisReady(listener)
      .withConsistentSnapshot();
    JobTicket ticket2 = moveMgr.startJob( batcher );
    batcher.awaitCompletion();
    moveMgr.stopJob(ticket2);
    System.out.println("DEBUG: count2=[" + count2 + "]");
    assertEquals( "exactly " + numDocs + " docs should have been transformed", numDocs, count2.get());

    StructuredQueryDefinition query3 = sqb.value(sqb.jsonProperty("testProperty"), "test3a");
    query3.setCollections(collection);
    final AtomicInteger count3 = new AtomicInteger(0);
    QueryBatcher batcher3 = moveMgr.newQueryBatcher(query3)
      .withBatchSize(100)
      .onUrisReady(batch -> count3.addAndGet(batch.getItems().length))
      .onQueryFailure((throwable) -> throwable.printStackTrace());
    JobTicket ticket3 = moveMgr.startJob( batcher3 );
    batcher3.awaitCompletion();
    moveMgr.stopJob(ticket3);

    System.out.println("DEBUG: count3=[" + count3 + "]");
    assertEquals( "exactly " + numDocs + " docs should match the new value test3a", numDocs, count3.get());
  }

  @Test
  public void testOnSkipped() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
    client.newDocumentManager().writeAs(collection + "/test1.txt", meta, "test");
    client.newDocumentManager().writeAs(collection + "/test2.txt", meta, "test");

    List<String> uris = new ArrayList<>();
    uris.add(collection + "/test1.txt");
    uris.add(collection + "/test2.txt");
    uris.add(collection + "/test3.txt");

    ServerTransform transform = new ServerTransform(transformName1);
    List skippedUris = new ArrayList<>();
    StringBuilder failures = new StringBuilder();
    QueryBatcher batcher = moveMgr.newQueryBatcher(uris.iterator())
      .withBatchSize(1)
      .onUrisReady(
        new ApplyTransformListener()
          .withTransform(transform)
          .onSkipped(batch -> {
            if ( batch.getItems().length == 0 ) failures.append("batch length should never be 0");
            for ( String uri : batch.getItems() ) {
              skippedUris.add(uri);
            }
          })
          .onBatchFailure((batch, throwable) -> throwable.printStackTrace())
      )
      .onQueryFailure(throwable -> throwable.printStackTrace());
    JobTicket ticket = moveMgr.startJob( batcher );
    batcher.awaitCompletion();
    moveMgr.stopJob(ticket);

    if ( failures.length() > 0 ) fail("ERROR: " + failures);

    assertEquals(1, skippedUris.size());
    assertEquals(collection + "/test3.txt", skippedUris.get(0));
  }
}

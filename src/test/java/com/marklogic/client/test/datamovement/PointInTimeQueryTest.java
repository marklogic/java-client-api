/*
 * Copyright 2015-2016 MarkLogic Corporation
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

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;

import com.marklogic.client.datamovement.WriteHostBatcher;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.FailureListener;
import com.marklogic.client.datamovement.QueryHostBatcher;
import com.marklogic.client.datamovement.QueryHostException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PointInTimeQueryTest {
  private static Logger logger = LoggerFactory.getLogger(PointInTimeQueryTest.class);
  private static int numDocs = 50;
  private static DataMovementManager moveMgr = DataMovementManager.newInstance();
  private static String collection = "PointInTimeQueryTest_" +
    new Random().nextInt(10000);

  @BeforeClass
  public static void beforeClass() throws Exception {
    Common.connect();
    //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    moveMgr.withClient(Common.client);
    setup();
  }

  @AfterClass
  public static void afterClass() {
    QueryManager queryMgr = Common.client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
    //queryMgr.delete(deleteQuery);

    Common.release();
  }

  public static void setup() throws Exception {
    StringBuffer failures = new StringBuffer();
    WriteHostBatcher writeBatcher = moveMgr.newWriteHostBatcher()
      .withBatchSize(10)
      .onBatchFailure((client, event, throwable) -> {
        throwable.printStackTrace();
        failures.append("ERORR:[" + throwable.toString() + "]");
      });
    moveMgr.startJob(writeBatcher);
    // a collection so we're only looking at docs related to this test run
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
    for ( int i=1; i <= numDocs; i++ ) {
      writeBatcher.addAs(collection + "/doc_" + i + ".txt", meta, "test contents");
    }
    writeBatcher.flushAsync();
    writeBatcher.awaitCompletion();
    if ( failures.length() > 0 ) fail(failures.toString());
    logger.info("Successfully wrote {} docs to collection {}", numDocs, collection);
  }

  /** This test starts an export job, then sleeps during it while a parallel job
   * deletes all the underlying docs.  Both the export and the delete would
   * normally fail to get all the docs because they can't paginate properly
   * when docs matching the query are deleted.  But here both paginate properly
   * all the way because they set withConsistentSnapshot() which uses the
   * REST timestamp parameter to both get the uris and the documents at the
   * same timestamp as the first query, back when no matches had been deleted.
   */
  @Test
  public void test_A_DeleteSomeMatchesDuringJob() throws Exception {
    QueryDefinition query = new StructuredQueryBuilder().collection(collection);
    AtomicInteger successDocs = new AtomicInteger();
    AtomicInteger badDocs = new AtomicInteger();
    StringBuilder failures = new StringBuilder();
    FailureListener<QueryHostException> failListener = (client, throwable) -> {
        throwable.printStackTrace();
        logger.error("ERORR:[{}]", throwable);
        failures.append("ERORR:[" + throwable.toString() + "]");
    };
    QueryHostBatcher exportBatcher = moveMgr.newQueryHostBatcher(query)
      .withThreadCount(1)
      .withBatchSize(30)
      .withConsistentSnapshot()
      .onUrisReady(
        new ExportListener()
          .withConsistentSnapshot()
          .onDocumentReady(doc -> {
            // check that doc contents match "test contents"
            String contents = doc.getContentAs(String.class);
            if ( ! "test contents".equals(contents) ) {
              badDocs.incrementAndGet();
              logger.debug("Contents should be 'test contents' but are '{}'", contents);
            }
          })
      )
      .onUrisReady((client, batch) -> {
        successDocs.addAndGet(batch.getItems().length);
        logger.info("Retrieved {} docs, sleeping for 1 second", batch.getItems().length);
        // sleep for a second to give us time to delete some of the batches we were going to get
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
      })
      .onQueryFailure(failListener);
    moveMgr.startJob(exportBatcher);

    // while the exportBatcher is still running, let's delete the docs out from under it
    QueryHostBatcher deleteBatcher = moveMgr.newQueryHostBatcher(query)
      .withBatchSize(10)
      .withConsistentSnapshot()
      .onUrisReady(new DeleteListener())
      .onUrisReady((client, batch) -> logger.info("Deleting {} docs", batch.getItems().length) )
      .onQueryFailure(failListener);
    moveMgr.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();

    // now that we're done deleting, wait for the exportBatcher to finish
    exportBatcher.awaitCompletion();

    // if we still got all the docs, that means our delete was ignored during the run
    assertEquals(numDocs, successDocs.get());
    assertEquals(0, badDocs.get());
    if ( failures.length() > 0 ) fail(failures.toString());
  }
}

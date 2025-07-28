/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class PointInTimeQueryTest {
  private static Logger logger = LoggerFactory.getLogger(PointInTimeQueryTest.class);
  private static int numDocs = 50;
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static String collection = "PointInTimeQueryTest_" +
    new Random().nextInt(10000);

  @BeforeAll
  public static void beforeClass() throws Exception {
    setup();
  }

  @AfterAll
  public static void afterClass() {
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
    queryMgr.delete(deleteQuery);
  }

  public static void setup() throws Exception {
    StringBuffer failures = new StringBuffer();
    WriteBatcher writeBatcher = moveMgr.newWriteBatcher()
      .withBatchSize(10)
      .onBatchFailure((event, throwable) -> {
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
    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
    AtomicInteger successDocs = new AtomicInteger();
    AtomicInteger badDocs = new AtomicInteger();
    StringBuilder failures = new StringBuilder();
    QueryFailureListener failListener = throwable -> {
      throwable.printStackTrace();
      logger.error("ERORR:[{}]", throwable);
      failures.append("ERORR:[" + throwable.toString() + "]");
    };
    QueryBatcher exportBatcher = moveMgr.newQueryBatcher(query)
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
      .onUrisReady(batch -> {
        successDocs.addAndGet(batch.getItems().length);
        logger.info("Retrieved {} docs, sleeping for 1 second", batch.getItems().length);
        // sleep for a second to give us time to delete some of the batches we were going to get
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
      })
      .onQueryFailure(failListener);
    moveMgr.startJob(exportBatcher);

    // while the exportBatcher is still running, let's delete the docs out from under it
    QueryBatcher deleteBatcher = moveMgr.newQueryBatcher(query)
      .withBatchSize(10)
      .withConsistentSnapshot()
      .onUrisReady(new DeleteListener())
      .onUrisReady(batch -> logger.info("Deleting {} docs", batch.getItems().length) )
      .onQueryFailure(failListener);
    moveMgr.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();

    // now that we're done deleting, wait for the exportBatcher to finish
    exportBatcher.awaitCompletion();

    assertNotNull(exportBatcher.getServerTimestamp(),
		"withConsistentSnapshot was used, so the server timestamp should be available to the client");

    // if we still got all the docs, that means our delete was ignored during the run
    assertEquals(numDocs, successDocs.get());
    assertEquals(0, badDocs.get());
    if ( failures.length() > 0 ) fail(failures.toString());
  }
}

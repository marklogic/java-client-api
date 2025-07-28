/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteListenerTest {
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static String collection = "DeleteListenerTest";
  private static String docContents = "doc contents";

  @BeforeAll
  public static void beforeClass() {
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testMassDelete() throws Exception {
    // write 100 simple text files to the db
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection);
    WriteBatcher writeBatcher = moveMgr.newWriteBatcher();
    moveMgr.startJob(writeBatcher);
    String[] uris = new String[100];
    for ( int i=0; i < 100; i++ ) {
      uris[i] = "doc" + i + ".txt";
      writeBatcher.addAs(uris[i], meta, docContents);
    }
    writeBatcher.flushAndWait();

    // verify that the files made it to the db
    assertEquals(100, client.newDocumentManager().read(uris).size() );

    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(
        new StructuredQueryBuilder().collection(collection)
        )
      .withBatchSize(10)
      .onUrisReady(new DeleteListener())
      .withConsistentSnapshot();
    JobTicket ticket = moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(ticket);

    // validate that the docs were deleted
    assertEquals(0, client.newDocumentManager().read(uris).size() );
  }
}

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

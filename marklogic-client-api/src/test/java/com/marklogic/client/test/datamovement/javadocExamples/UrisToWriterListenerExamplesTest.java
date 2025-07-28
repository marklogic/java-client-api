/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement.javadocExamples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrisToWriterListenerExamplesTest {
  private static Logger logger = LoggerFactory.getLogger(UrisToWriterListenerExamplesTest.class);
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager dataMovementManager = client.newDataMovementManager();
  private static String collection = "UrisToWriterListenerExamples_" +
    new Random().nextInt(10000);
  private static DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
  private static StructuredQueryDefinition collectionQuery = new StructuredQueryBuilder().collection(collection);

  @BeforeAll
  public static void beforeClass() {
  }

  @AfterAll
  public static void afterClass() {
    DeleteQueryDefinition deleteQuery = client.newQueryManager().newDeleteDefinition();
    deleteQuery.setCollections(collection);
    client.newQueryManager().delete(deleteQuery);
  }

  @Test
  public void testWriteUrisToDisk() throws Exception {
    client.newDocumentManager().writeAs(collection + "/test1.txt", meta, "text");
    client.newDocumentManager().writeAs(collection + "/test2.txt", meta, "text");
    client.newDocumentManager().writeAs(collection + "/test3.txt", meta, "text");
    assertEquals(3, client.newQueryManager().search(collectionQuery, new SearchHandle()).getTotalResults());

    StructuredQueryDefinition query = collectionQuery;

    // begin copy fromclass javadoc in src/main/java/com/marklogic/datamovement/UrisToWriterListener.java
    FileWriter writer = new FileWriter("uriCache.txt");
    QueryBatcher getUris = dataMovementManager.newQueryBatcher(query)
      .withBatchSize(5000)
      .onUrisReady( new UrisToWriterListener(writer) )
      .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket getUrisTicket = dataMovementManager.startJob(getUris);
    getUris.awaitCompletion();
    dataMovementManager.stopJob(getUrisTicket);
    writer.flush();
    writer.close();

    // start interject a test
    try ( BufferedReader reader1 = new BufferedReader(new FileReader("uriCache.txt")) ) {
      assertEquals(3, reader1.lines().count());
    }
    // end interject a test

    // now we have the uris, let's step through them
    try ( BufferedReader reader = new BufferedReader(new FileReader("uriCache.txt")) ) {
      QueryBatcher performDelete =
        dataMovementManager.newQueryBatcher(reader.lines().iterator())
        .onUrisReady(new DeleteListener())
        .onQueryFailure(exception-> exception.printStackTrace());
      JobTicket ticket = dataMovementManager.startJob(performDelete);
      performDelete.awaitCompletion();
      dataMovementManager.stopJob(ticket);
    }
    // end copy from class javadoc in src/main/java/com/marklogic/datamovement/UrisToWriterListener.java

    assertEquals(0, client.newQueryManager().search(collectionQuery, new SearchHandle()).getTotalResults());
  }
}

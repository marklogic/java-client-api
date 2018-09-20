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
package com.marklogic.client.test.datamovement.javadocExamples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.UrisToWriterListener;
import com.marklogic.client.test.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class UrisToWriterListenerExamplesTest {
  private static Logger logger = LoggerFactory.getLogger(UrisToWriterListenerExamplesTest.class);
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager dataMovementManager = client.newDataMovementManager();
  private static String collection = "UrisToWriterListenerExamples_" +
    new Random().nextInt(10000);
  private static DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection);
  private static StructuredQueryDefinition collectionQuery = new StructuredQueryBuilder().collection(collection);

  @BeforeClass
  public static void beforeClass() {
  }

  @AfterClass
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

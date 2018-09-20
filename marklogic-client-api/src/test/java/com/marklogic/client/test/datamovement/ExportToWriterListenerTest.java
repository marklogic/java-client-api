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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import com.marklogic.client.datamovement.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

import com.marklogic.client.test.Common;

public class ExportToWriterListenerTest {
  private Logger logger = LoggerFactory.getLogger(ExportToWriterListenerTest.class);
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static String collection = "ExportToWriterListenerTest_" +
    new Random().nextInt(10000);
  private static String docContents = "doc contents";

  @BeforeClass
  public static void beforeClass() {
  }

  @AfterClass
  public static void afterClass() {
  }

  @Test
  public void testMassExportToWriter() throws Exception {
    File outputFile = Files.createTempFile("ExportToWriterListenerTest", "csv").toFile();
    logger.debug("outputFile=[{}]", outputFile);
    // write 100 simple text files to the db
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection);
    WriteBatcher batcher = moveMgr.newWriteBatcher();
    moveMgr.startJob(batcher);
    String[] uris = new String[100];
    for ( int i=0; i < 100; i++ ) {
      uris[i] = "/" + collection + "/doc" + i + ".txt";
      batcher.addAs(uris[i], meta, docContents);
    }
    batcher.flushAndWait();

    // verify that the files made it to the db
    assertEquals( "There should be 100 documents in the db",
      100, client.newDocumentManager().read(uris).size() );

    // export to a csv with uri, collection, and contents columns
    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
    try (FileWriter writer = new FileWriter(outputFile)) {
      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
        .withRecordSuffix("\n")
        .withMetadataCategory(DocumentManager.Metadata.COLLECTIONS)
        .onGenerateOutput(
          record -> {
            String uri = record.getUri();
            String collection = record.getMetadata(new DocumentMetadataHandle()).getCollections().iterator().next();
            String contents = record.getContentAs(String.class);
            return uri + "," + collection + "," + contents;
          }
        );

      QueryBatcher queryJob =
        moveMgr.newQueryBatcher(query)
          .withThreadCount(5)
          .withBatchSize(10)
          .onUrisReady(exportListener)
          .onQueryFailure( throwable -> throwable.printStackTrace() );
      moveMgr.startJob( queryJob );

      // wait for the export to finish
      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
      if ( finished == false ) {
        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
      }
    }

    // validate that the docs were exported
    try (FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
      int lines = 0;
      while ( reader.readLine() != null ) lines++;
      assertEquals( "There should be 100 lines in the output file", 100, lines );
    }
    if(outputFile.exists()) outputFile.delete();
  }
}

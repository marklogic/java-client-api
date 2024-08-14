/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.marklogic.client.query.StructuredQueryBuilder.Operator;
import static org.junit.jupiter.api.Assertions.fail;

public class LegalHoldsTest {
  private Logger logger = LoggerFactory.getLogger(LegalHoldsTest.class);
  private static String collection = "LegalHoldsTest_" +
    new Random().nextInt(10000);
  private static String directory = "/LegalHoldsTest/";
  private static ObjectMapper mapper = new ObjectMapper();
  private static DatabaseClient evalClient = Common.connectEval();
  private static DatabaseClient restAdminClient = Common.connectRestAdmin();

  @BeforeAll
  public static void beforeClass() throws Exception {
    // install the filterUrisReferencedByHolds module
    installModule();
    // enable point-in-time queries
    //setMergeTimestamp("xdmp:request-timestamp()");
    // upload sample json docs.  Only file5.json matches all criteria.
    uploadData();
  }
  @AfterAll
  public static void afterClass() throws Exception {
    cleanup(restAdminClient);
  }

  @Test
  /** 10.  Duane is charged with implementing his organization's TTL policy,
   * such that after 7 years all transactional data must be deleted. He
   * performs a batch delete every month. Each batch is typically in the
   * millions of documents and has very selective criteria, e.g. "All
   * instruments of type X, originating in jurisdiction Y, having a state of
   * of 'resolved', 'closed', or 'finalized' that have not been marked 'hold'
   * or is not referenced by a document marked 'hold' that has not been
   * updated in the previous 7 years." He first does a dry run and gets a
   * report of the candidate documents. He has some code that sanity checks
   * those numbers against accounting data he gets from an external system.
   * If those numbers add up he runs the delete. Because of the size, he
   * knows his delete can't run in an atomic transaction. However, he's able
   * to specify that his export run at a single, consistent timestamp, thus
   * ignoring subsequent updates. After a successful run, he gets back a
   * report confirming that he deleted what he intended. In the case of an
   * unsuccessful delete he gets a detailed report of which documents were
   * deleted and why his job failed.
   */
  public void scenario10() throws Exception {

    // query for docs matching most of our legal holds criteria
    // but recognize this query can't filter out docs referenced by docs
    // with legal holds
    Calendar date = Calendar.getInstance();
//	 TODO This test failed when 2022 became 2023; increasing -7 to a higher number fixed it. The test could obviously
//	  use some rework to ensure that it doesn't fail every time the year changes, but this comment is being left here
//	  so that if/when this does fail in the future, it'll be easy to fix.
    date.roll(Calendar.YEAR, -10);
    StructuredQueryBuilder sqb = new StructuredQueryBuilder();
    StructuredQueryDefinition query =
      sqb.and(
        sqb.collection(collection),
        sqb.value(sqb.jsonProperty("type"), "X"),
        sqb.value(sqb.jsonProperty("originJurisdiction"), "Y"),
        sqb.value(sqb.jsonProperty("state"), "resolved", "closed", "finalized"),
        sqb.not( sqb.value(sqb.jsonProperty("hold"), "true") ),
        sqb.range(
          sqb.jsonProperty("lastModified"),
          "xs:dateTime", new String[0],
          Operator.LE, DatatypeConverter.printDateTime(date)
        )
      );

    // walk through all batches of docs matching our query
    DataMovementManager moveMgr = evalClient.newDataMovementManager();
    StringBuilder anyFailure = new StringBuilder();
    Hashtable<String,AtomicInteger> urisDeleted = new Hashtable<>();
    QueryBatcher batcher = moveMgr.newQueryBatcher(query)
      .withBatchSize(1)
      .withConsistentSnapshot()
      .onUrisReady(
        batch -> {
          ArrayNode uris = mapper.createArrayNode();
          for ( String uri : batch.getItems() ) {
            uris.add( uri );
          }
          // for each batch, filter out any matches referenced by docs
          // with legal holds
          String urisToDelete =
            batch.getClient().newServerEval()
              .modulePath("/ext" + directory + "filterUrisReferencedByHolds.sjs")
              .addVariable("uris", new JacksonHandle(uris))
              .evalAs(String.class);
          logger.info("DEBUG: [LegalHoldsTest] urisToDelete =[" + urisToDelete  + "]");
          logger.debug("DEBUG: [LegalHoldsTest] batch.getForest().getForestName()=[" + batch.getForest().getForestName() + "]");

          if ( urisToDelete != null && urisToDelete.length() > 0 ) {
            // now that we have a clean list, delete them
            batch.getClient().newDocumentManager().delete(urisToDelete.split(","));
            for ( String uri : urisToDelete.split(",") ) {
              logger.info("DEBUG: [LegalHoldsTest] uri =[" + uri  + "]");
              synchronized (urisDeleted) {
                AtomicInteger deleted = urisDeleted.get(batch.getForest().getForestName());
                if ( deleted == null ) {
                  deleted = new AtomicInteger();
                  urisDeleted.put(batch.getForest().getForestName(), deleted);
                }
                deleted.incrementAndGet();
                logger.debug("DEBUG: [LegalHoldsTest] deleted=[" + deleted + "]");
              }
            }
          }
        }
      )
      .onQueryFailure( throwable -> {
        anyFailure.append("error: " + throwable + "\n");
        throwable.printStackTrace();
      });
    moveMgr.startJob(batcher);

    // give this process up to 1 day to finish
    batcher.awaitCompletion(1, TimeUnit.DAYS);

    if ( anyFailure.length() > 0 ) {
      fail(anyFailure.toString());
    }

    JSONDocumentManager docMgr = evalClient.newJSONDocumentManager();
    // validate that we didn't delete the docs that didn't match
    try {
      docMgr.readAs(directory + "file1.json", String.class);
      docMgr.readAs(directory + "file2.json", String.class);
      docMgr.readAs(directory + "file3.json", String.class);
      docMgr.readAs(directory + "file4.json", String.class);
    } catch (ResourceNotFoundException e) {
      fail("missing a file that should still be there: " + e);
    }
    try {
      docMgr.readAs(directory + "file5.json", String.class);
      fail("found file5.json which should not be there");
    } catch (ResourceNotFoundException e) { }
    try {
      docMgr.readAs(directory + "file6.json", String.class);
      fail("found file6.json which should not be there");
    } catch (ResourceNotFoundException e) { }
    try {
      docMgr.readAs(directory + "file7.json", String.class);
      fail("found file7.json which should not be there");
    } catch (ResourceNotFoundException e) { }
    try {
      docMgr.readAs(directory + "file8.json", String.class);
      fail("found file8.json which should not be there");
    } catch (ResourceNotFoundException e) { }
  }

  private static void installModule() throws Exception {
    // get a modules manager
    ExtensionLibrariesManager libsMgr = restAdminClient.newServerConfigManager().newExtensionLibrariesManager();

    // write server-side javascript module file to the modules database
    libsMgr.write("/ext" + directory + "filterUrisReferencedByHolds.sjs",
      new FileHandle(new File("src/test/resources/legal_holds/filterUrisReferencedByHolds.sjs"))
        .withFormat(Format.TEXT));
  }

  private static void uploadData() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection);
    JSONDocumentManager docMgr = evalClient.newJSONDocumentManager();
    File folder = new File("src/test/resources/legal_holds/data");
    for ( Path path: Files.newDirectoryStream(folder.toPath(), "*.json") ) {
      File file = path.toFile();
      docMgr.write(directory + file.getName(), meta, new FileHandle(file));
    }
  }

  private static void cleanup(DatabaseClient adminClient) throws Exception {
    //setMergeTimestamp("0");
  }
}

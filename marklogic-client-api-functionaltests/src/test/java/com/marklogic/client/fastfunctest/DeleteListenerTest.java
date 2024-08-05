/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.UrisToWriterListener;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.functionaltests.WriteHostBatcherTest;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DeleteListenerTest extends AbstractFunctionalTest {

  private static DataMovementManager dmManager = null;
  private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

  private static DatabaseClient dbClient;
  private static JacksonHandle jacksonHandle;
  private static StringHandle stringHandle;
  private static FileHandle fileHandle;

  private static DocumentMetadataHandle meta;

  private static String stringTriple;
  private static File fileJson;
  private static JsonNode jsonNode;
  private static final String query1 = "fn:count(fn:doc())";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    dbClient = newDatabaseClientBuilder().build();
    dmManager = dbClient.newDataMovementManager();

    // JacksonHandle
    jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
    jacksonHandle = new JacksonHandle();
    jacksonHandle.set(jsonNode);

    meta = new DocumentMetadataHandle().withCollections("DeleteListener");

    // StringHandle
    stringTriple = "<abc>xml</abc>";
    stringHandle = new StringHandle(stringTriple);
    stringHandle.setFormat(Format.XML);

    // FileHandle
    fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "dir.json"));
    fileHandle = new FileHandle(fileJson);
    fileHandle.setFormat(Format.JSON);
  }

  @BeforeEach
  public void setUp() throws Exception {
	  deleteDocuments(client);

    WriteBatcher ihb2 = dmManager.newWriteBatcher();
    ihb2.withBatchSize(27).withThreadCount(10);
    dmManager.startJob(ihb2);
    for (int j = 0; j < 2000; j++) {
      String uri = "/local/json-" + j;
      ihb2.add(uri, meta, jacksonHandle);
    }

    ihb2.flushAndWait();
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void massDeleteSingleThread() throws Exception {
    HashSet<String> urisList = new HashSet<>();

    assertTrue(urisList.isEmpty());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(11, 1)
        //.withThreadCount(1)
        .onUrisReady(batch -> {
          synchronized (this) {
            urisList.addAll(Arrays.asList(batch.getItems()));
          }
        })
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    JobTicket ticket = dmManager.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    dmManager.stopJob(ticket);

    assertEquals(2000, urisList.size());

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(23)
        .withThreadCount(1)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void massDeleteMultipleThreads() throws Exception {

    HashSet<String> urisList = new HashSet<>();

    QueryBatcher queryBatcher = dmManager.newQueryBatcher(
        new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(batch -> {
          synchronized (this) {
            urisList.addAll(Arrays.asList(batch.getItems()));
          }

        })
        .onQueryFailure(throwable -> {
          System.out.println("Exceptions thrown from callback onQueryFailure");
          throwable.printStackTrace();

        });

    JobTicket ticket = dmManager.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    dmManager.stopJob(ticket);

    assertEquals(2000, urisList.size());

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(119)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void massDeleteConsistentSnapShot() {
	  setMergeTimestamp(DB_NAME, "-600000000");

	  try {
		  QueryBatcher queryBatcher = dmManager.newQueryBatcher(
				  new StructuredQueryBuilder().collection("DeleteListener"))
										  .withBatchSize(7)
										  .withConsistentSnapshot()
										  .withThreadCount(5)
										  .onUrisReady(new DeleteListener())
										  .onQueryFailure(throwable -> {
											  System.out.println("Exceptions thrown from callback onQueryFailure");
											  throwable.printStackTrace();

										  });

		  JobTicket ticket = dmManager.startJob(queryBatcher);
		  queryBatcher.awaitCompletion();
		  dmManager.stopJob(ticket);

		  assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	  } finally {
		  setMergeTimestamp(DB_NAME, "0");
	  }

  }

  @Test
  public void deleteNonExistentDoc() throws Exception {

    HashSet<String> urisList = new HashSet<>();
    // add a non existent doc uri
    urisList.add("/abc/nonexistent");

    AtomicInteger successDocs = new AtomicInteger();
    HashSet<String> uris2 = new HashSet<>();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  // ISSUE 94
  @Test
  public void deleteServerFile() throws Exception {
//	  if (true) return;

	  setMergeTimestamp(DB_NAME, "-6000000000");

	  try {
		  // No idea what this is supposed to be doing, as the docs it's deleting don't ever exist.
		  // j initially was 1999 but that was causing the test to take about 20s, so lowered it.
		  class MyRunnable implements Runnable {
			  @Override
			  public void run() {
				  for (int j = 300; j >= 200; j--) {
					  dbClient.newDocumentManager().delete("/local/json-" + j);
				  }
			  }
		  }
		  Thread t1;
		  t1 = new Thread(new MyRunnable());

		  Set<String> urisList = Collections.synchronizedSet(new HashSet<>());

		  QueryBatcher queryBatcher = dmManager.newQueryBatcher(
				  new StructuredQueryBuilder().collection("DeleteListener"))
										  .withBatchSize(11)
										  .withThreadCount(4)
										  .withConsistentSnapshot()
										  .onUrisReady(batch -> {
											  for (String s : batch.getItems()) {
												  urisList.add(s);
											  }
										  })
										  .onQueryFailure(throwable -> {
											  System.out.println("Exceptions thrown from callback onQueryFailure");
											  throwable.printStackTrace();

										  });

		  t1.start();
		  JobTicket ticket = dmManager.startJob(queryBatcher);

		  queryBatcher.awaitCompletion();
		  t1.join();
		  dmManager.stopJob(ticket);

		  System.out.println("URI's size " + urisList.size());
		  AtomicInteger successDocs = new AtomicInteger();
		  Set<String> uris2 = Collections.synchronizedSet(new HashSet<>());
		  StringBuffer failures2 = new StringBuffer();

		  QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
										   .withBatchSize(13)
										   .withThreadCount(5)
										   .onUrisReady(new DeleteListener())
										   .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
										   .onUrisReady(batch -> uris2.addAll(Arrays.asList(batch.getItems())))
										   .onQueryFailure(throwable -> {
											   throwable.printStackTrace();
											   failures2.append("ERROR:[" + throwable + "]\n");
										   });

		  JobTicket delTicket = dmManager.startJob(deleteBatcher);
		  deleteBatcher.awaitCompletion();
		  dmManager.stopJob(delTicket);

		  if (failures2.length() > 0)
			  fail(failures2.toString());

		  assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

		  DocumentPage page = dbClient.newDocumentManager().read("/local/json-1998");
		  JacksonHandle dh = new JacksonHandle();
		  while (page.hasNext()) {
			  DocumentRecord rec = page.next();
			  rec.getContent(dh);
			  System.out.println("Results are: " + dh.get().get("k1").asText());

		  }
	  } finally {
		  setMergeTimestamp(DB_NAME, "0");
	  }
  }

  @Test
  public void deleteEmptyIterator() {

    HashSet<String> urisList = new HashSet<>();

    AtomicInteger successDocs = new AtomicInteger();
    StringBuffer failures2 = new StringBuffer();

    QueryBatcher deleteBatcher = dmManager.newQueryBatcher(urisList.iterator())
        .withBatchSize(11)
        .withThreadCount(11)
        .onUrisReady(new DeleteListener())
        .onUrisReady(batch -> successDocs.addAndGet(batch.getItems().length))
        .onQueryFailure(throwable -> {
          throwable.printStackTrace();
          failures2.append("ERROR:[" + throwable + "]\n");
        });

    JobTicket delTicket = dmManager.startJob(deleteBatcher);
    deleteBatcher.awaitCompletion();
    dmManager.stopJob(delTicket);

    if (failures2.length() > 0)
      fail(failures2.toString());
    assertEquals(0, successDocs.intValue());
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }

  @Test
  public void deleteOnDiskUris() throws Exception {
    String pathname = "uriCache.txt";
    assertEquals(2000, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());

    ArrayList<String> foundUris = getUris();
    ArrayList<String> diskUris = writeUrisToDisk();

    assertEquals(foundUris.size(), diskUris.size());

    File file = new File(pathname);
    assertTrue(file.exists());

    ArrayList<String> deletedUris = deleteDocuments(pathname);

    assertEquals(foundUris.size(), deletedUris.size());
    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    file.delete();
  }

  public ArrayList<String> getUris() {

    Set<String> uris = Collections.synchronizedSet(new HashSet<>());

    QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(5000)
        .onUrisReady(batch -> uris.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket getUrisTicket = dmManager.startJob(getUris);
    getUris.awaitCompletion();
    dmManager.stopJob(getUrisTicket);
    return new ArrayList<String>(uris);
  }

  public ArrayList<String> writeUrisToDisk() throws IOException {
    Set<String> uris = Collections.synchronizedSet(new HashSet<>());

    FileWriter writer = new FileWriter("uriCache.txt");
    QueryBatcher getUris = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("DeleteListener"))
        .withBatchSize(100)
        .onUrisReady(new UrisToWriterListener(writer))
        .onUrisReady(batch -> uris.addAll(Arrays.asList(batch.getItems())))
        .onQueryFailure(exception -> exception.printStackTrace());
    JobTicket getUrisTicket = dmManager.startJob(getUris);
    getUris.awaitCompletion();
    dmManager.stopJob(getUrisTicket);
    writer.flush();
    writer.close();

    return new ArrayList<String>(uris);
  }

  public ArrayList<String> deleteDocuments(String file) throws Exception {
    Set<String> uris = Collections.synchronizedSet(new HashSet<String>());

    BufferedReader reader = new BufferedReader(new FileReader(file));
    Iterator<String> itr = reader.lines().iterator();

    QueryBatcher performDelete = dmManager.newQueryBatcher(itr)
        .withThreadCount(5)
        .withBatchSize(99)
        .withConsistentSnapshot()
        .onUrisReady(new DeleteListener())
        .onUrisReady((batch) -> {
          uris.addAll(Arrays.asList(batch.getItems()));
        }
        ).onQueryFailure(exception -> exception.printStackTrace());

    JobTicket ticket = dmManager.startJob(performDelete);
    performDelete.awaitCompletion();

    dmManager.stopJob(ticket);
    reader.close();
    return new ArrayList<String>(uris);
  }
}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.*;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class WriteBatcherTest {
  private Logger logger = LoggerFactory.getLogger(WriteBatcherTest.class);
  private static DatabaseClient client = Common.connectEval();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static DocumentManager<?,?> docMgr;
  private static String uri1 = "WriteBatcherTest_content_1.txt";
  private static String uri2 = "WriteBatcherTest_content_2.txt";
  private static String uri3 = "WriteBatcherTest_content_3.txt";
  private static String uri4 = "WriteBatcherTest_content_4.txt";
  private static String uri5 = "invalidXML.xml";
  private static String uri6 = "invalidXML_2.xml";
  private static String transform = "WriteBatcherTest_transform.sjs";
  private static String whbTestCollection = "WriteBatcherTest_" +
    new Random().nextInt(10000);


  @BeforeAll
  public static void beforeClass() {
    docMgr = client.newDocumentManager();
    installModule();
  }

  @AfterAll
  public static void afterClass() {
    docMgr.delete(uri1, uri2, uri3);
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(whbTestCollection);
    queryMgr.delete(deleteQuery);
  }

  public static void installModule() {
    Common.newRestAdminClient().newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
      transform, new FileHandle(new File("src/test/resources/" + transform)));
  }

  @Test
  public void testSimple() throws Exception {
    String collection = whbTestCollection + ".testSimple";

    StringBuilder successBatch = new StringBuilder();
    StringBuilder failureBatch = new StringBuilder();
    WriteBatcher ihb1 =  moveMgr.newWriteBatcher()
      .withBatchSize(1)
      .onBatchSuccess(
        batch -> {
          logger.debug("[testSimple] batch: {}, items: {}",
            batch.getJobBatchNumber(), batch.getItems().length);
          for(WriteEvent w: batch.getItems()){
            successBatch.append(w.getTargetUri()+":");
          }
        })
      .onBatchFailure(
        (batch, throwable) -> {
          for(WriteEvent w: batch.getItems()){
            failureBatch.append(w.getTargetUri()+":");
          }
        });

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    ihb1.add("/doc/jackson", meta, new JacksonHandle(new ObjectMapper().readTree("{\"test\":true}")));
      //.add("/doc/reader_wrongxml", new ReaderHandle)
    meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    ihb1.add("/doc/string", meta, new StringHandle("test"));
      /*
      .add("/doc/file", docMeta2, new FileHandle)
      .add("/doc/is", new InputStreamHandle)
      .add("/doc/os_wrongjson", docMeta2, new OutputStreamHandle)
      .add("/doc/bytes", docMeta1, new BytesHandle)
      .add("/doc/dom", new DomHandle);
      */

    ihb1.flushAndWait();
  }
  @Test
  public void testWrites() throws Exception {
    String collection = whbTestCollection + ".testWrites";

    assertEquals(
      null, docMgr.exists(uri1) );

    final StringBuffer successListenerWasRun = new StringBuffer();
    final StringBuffer failListenerWasRun = new StringBuffer();
    final StringBuffer failures = new StringBuffer();
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      .withBatchSize(2)
      .withTransform(
        new ServerTransform(transform)
          .addParameter("newValue", "test1a")
      )
      .onBatchSuccess(
        batch -> {
          logger.debug("[testWrites] batch: {}, items: {}",
            batch.getJobBatchNumber(), batch.getItems().length);
          successListenerWasRun.append("true");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      )
      .onBatchFailure(
        (batch, throwable) -> {
          failListenerWasRun.append("true");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    JsonNode doc1 = new ObjectMapper().readTree("{ \"testProperty\": \"test1\" }");
    JsonNode doc2 = new ObjectMapper().readTree("{ \"testProperty2\": \"test2\" }");
    // the batch with this doc will fail to write because we say withFormat(JSON)
    // but it isn't valid JSON. That will trigger our onBatchFailure listener.
    StringHandle doc3 = new StringHandle("<thisIsNotJson>test3</thisIsNotJson>")
      .withFormat(Format.JSON);
    JsonNode doc4 = new ObjectMapper().readTree("{ \"testProperty4\": \"test4\" }");
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(collection, whbTestCollection);
    batcher.addAs(uri1, meta, doc1);
    meta = new DocumentMetadataHandle().withCollections(collection, whbTestCollection);
    batcher.addAs(uri2, meta, doc2);
    meta = new DocumentMetadataHandle().withCollections(collection, whbTestCollection);
    batcher.add(uri3, meta, doc3);
    meta = new DocumentMetadataHandle().withCollections(collection, whbTestCollection);
    batcher.add(uri4, meta, new JacksonHandle(doc4));
    batcher.flushAndWait();
    assertEquals( "true", successListenerWasRun.toString());
    assertEquals( "true", failListenerWasRun.toString());

    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
    try ( DocumentPage docs = docMgr.search(query, 1) ) {
      // only doc1 and doc2 wrote successfully, doc3 failed
      assertEquals(2, docs.getTotalSize());

      for (DocumentRecord record : docs ) {
        if ( uri1.equals(record.getUri()) ) {
          assertEquals("test1a", record.getContentAs(JsonNode.class).get("testProperty").textValue() );
        }
      }
    }
  }

  @Test
  public void testDefaultMetadata() throws Exception {
    StringHandle firstContent = new StringHandle("test1");
    StringHandle secondContent = new StringHandle("test2");

    GenericDocumentManager docMgr = client.newDocumentManager();
    for (int i: new int[]{1,2}) {
      String collection = whbTestCollection + ".testDefaultMetadata"+i;
      String[] collections = new String[]{collection, whbTestCollection};

      String firstUri  = "/metadataTest/doc"+i+"_1.txt";
      String secondUri = "/metadataTest/doc"+i+"_2.txt";

      DocumentMetadataHandle meta = new DocumentMetadataHandle()
              .withCollections(collections)
              .withMetadataValue("metaKey", "metaValue")
              .withQuality(1);
      meta.getPermissions().add("manage-user", DocumentMetadataHandle.Capability.READ);

      List<String> successBatch = new ArrayList<>();
      List<String> failureBatch = new ArrayList<>();

      WriteBatcher batcher =  moveMgr.newWriteBatcher()
              .withBatchSize(2)
              .withDefaultMetadata(meta)
              .onBatchSuccess(
                      batch -> {
                        logger.debug("[testSimple] batch: {}, items: {}",
                                batch.getJobBatchNumber(), batch.getItems().length);
                        for(WriteEvent w: batch.getItems()){
                          successBatch.add(w.getTargetUri());
                        }
                      })
              .onBatchFailure(
                      (batch, throwable) -> {
                        for(WriteEvent w: batch.getItems()){
                          failureBatch.add(w.getTargetUri());
                        }
                      });

      JobTicket ticket = moveMgr.startJob(batcher);

      switch(i) {
      case 1:
        batcher.add(firstUri, firstContent);
        batcher.add(secondUri, secondContent);
        break;
      case 2:
        batcher.addAll(Stream.of(
            new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE, firstUri, null, firstContent),
            new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE, secondUri, null, secondContent)
        ));
        break;
      }

      batcher.flushAndWait();

      assertEquals(3, successBatch.size());
      assertNull( successBatch.get(0));
      assertEquals(firstUri, successBatch.get(1));
      assertEquals(secondUri, successBatch.get(2));
      assertEquals(0, failureBatch.size());

      DocumentPage docPage = docMgr.readMetadata(firstUri, secondUri);
      assertEquals(2, docPage.getPageSize());
      for (DocumentRecord docRecord: docPage) {
        DocumentMetadataHandle actual = docRecord.getMetadata(new DocumentMetadataHandle());
        assertNotNull( actual);

        DocumentMetadataHandle.DocumentCollections actualCollections = actual.getCollections();
        assertNotNull( actualCollections);
        for (String expectedCollection: collections) {
          assertTrue( actualCollections.contains(expectedCollection));
        }

        DocumentMetadataHandle.DocumentPermissions actualPermissions = actual.getPermissions();
        assertNotNull( actualPermissions);
        assertTrue(
            actualPermissions.get("manage-user").contains(DocumentMetadataHandle.Capability.READ)
        );

        DocumentMetadataHandle.DocumentMetadataValues actualMetaVals = actual.getMetadataValues();
        assertNotNull( actualMetaVals);
        assertEquals( actualMetaVals.get("metaKey"), "metaValue");

        assertEquals( meta.getQuality(), actual.getQuality());
      }
    }
  }

  @Test
  public void testListenerManagement() {
    WriteBatchListener successListener = batch -> {};
    WriteFailureListener failureListener = (batch, throwable) -> {};

    WriteBatcher batcher = moveMgr.newWriteBatcher();
    WriteBatchListener[] successListeners = batcher.getBatchSuccessListeners();
    assertEquals(1, successListeners.length);

    batcher.onBatchSuccess(successListener);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(2, successListeners.length);
    assertEquals(successListener, successListeners[1]);

    WriteFailureListener[] failureListeners = batcher.getBatchFailureListeners();
    assertEquals(3, failureListeners.length);
    assertEquals(HostAvailabilityListener.class, failureListeners[0].getClass());
    assertEquals(NoResponseListener.class, failureListeners[2].getClass());

    batcher.onBatchFailure(failureListener);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(4, failureListeners.length);
    assertEquals(failureListener, failureListeners[3]);

    batcher.setBatchSuccessListeners();
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(0, successListeners.length);

    batcher.setBatchSuccessListeners(successListener);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(1, successListeners.length);
    assertEquals(successListener, successListeners[0]);

    batcher.setBatchSuccessListeners((WriteBatchListener[]) null);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(0, successListeners.length);

    batcher.setBatchFailureListeners();
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(0, failureListeners.length);

    batcher.setBatchFailureListeners(failureListener);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(1, failureListeners.length);
    assertEquals(failureListener, failureListeners[0]);

    batcher.setBatchFailureListeners((WriteFailureListener[]) null);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(0, failureListeners.length);

    try {
      HostAvailabilityListener ha = new HostAvailabilityListener(moveMgr)
        .withMinHosts(5);
      fail("should have thrown IllegalArgumentException since minHosts was " +
        "set to a number greater than the number of hosts in the cluster");
    } catch (IllegalArgumentException e) {
      logger.debug("[testListenerManagement] threw expected exception since " +
        "HostAvailabilityListener was initialized with greater minHosts: " + e);
    }
  }

  /*
  @Test
  public void testWritesWithTransactions() throws Exception {
    String collection = whbTestCollection + ".testWritesWithTransactions";

    final StringBuffer successListenerWasRun = new StringBuffer();
    final StringBuffer failListenerWasRun = new StringBuffer();
    final StringBuffer failures = new StringBuffer();
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      .withBatchSize(2)
      .withTransactionSize(2)
      .withThreadCount(1)
      .withTransform(
        new ServerTransform(transform)
          .addParameter("newValue", "test1a")
      )
      .onBatchSuccess(
        batch -> {
          successListenerWasRun.append("true");
          logger.debug("[testWritesWithTransactions.onBatchSuccess] batch.getJobBatchNumber()=[" + batch.getJobBatchNumber() + "]");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      )
      .onBatchFailure(
        (batch, throwable) -> {
          failListenerWasRun.append("true");
          throwable.printStackTrace();
          logger.debug("[testWritesWithTransactions.onBatchFailure] batch.getJobBatchNumber()=[" + batch.getJobBatchNumber() + "]");
          assertEquals( 2, batch.getItems().length);
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    JsonNode doc = new ObjectMapper().readTree("{ \"testProperty\": \"true\" }");
    batcher.add(whbTestCollection + "doc_1.json", meta, new JacksonHandle(doc));
    batcher.add(whbTestCollection + "doc_2.json", meta, new JacksonHandle(doc));
    // the batch with this doc will fail to write because we say withFormat(JSON)
    // but it isn't valid JSON. That will trigger our onBatchFailure listener.
    StringHandle doc3 = new StringHandle("<thisIsNotJson>test1</thisIsNotJson>")
      .withFormat(Format.JSON);
    batcher.add(whbTestCollection + "doc_3.json", meta, doc3);
    batcher.add(whbTestCollection + "doc_4.json", meta, new JacksonHandle(doc));
    String uri5 = whbTestCollection + "doc_5.json";
    JsonNode doc5 = new ObjectMapper().readTree("{ \"testProperty\": \"test1\" }");
    batcher.addAs(uri5,                             meta, doc5);
    JsonNode doc6 = new ObjectMapper().readTree("{ \"testProperty6\": \"test6\" }");
    batcher.addAs(whbTestCollection + "doc_6.json", meta, doc6);
    StringHandle doc7 = new StringHandle("{ \"testProperty7\": \"test7\" }").withFormat(Format.JSON);
    batcher.add  (whbTestCollection + "doc_7.json", meta, doc7);
    StringHandle doc8 = new StringHandle("{ \"testProperty8\": \"test8\" }").withFormat(Format.JSON);
    batcher.add  (whbTestCollection + "doc_8.json", meta, doc8);
    batcher.flushAndWait();
    moveMgr.stopJob(ticket);

    if ( failures.length() > 0 ) fail(failures.toString());
    assertEquals( "truetrue", successListenerWasRun.toString());
    assertEquals( "truetrue", failListenerWasRun.toString());

    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
    DocumentPage docs = docMgr.search(query, 1);
    // only docs 5, 7, 8, and 8 wrote successfully, docs 1, 2, 3, and 4 failed in the same
    // transaction as doc 1
    assertEquals( 4, docs.getTotalSize());

    for (DocumentRecord record : docs ) {
      if ( uri5.equals(record.getUri()) ) {
        assertEquals( "the transform should have changed testProperty to 'test1a'",
          "test1a", record.getContentAs(JsonNode.class).get("testProperty").textValue() );
      }
    }
  }
  */

  @Test
  public void testZeros() throws Exception {
    try {
      WriteBatcher batcher = moveMgr.newWriteBatcher()
        .withBatchSize(0);
      fail("should have thrown IllegalArgumentException because batchSize must be > 1");
    } catch(IllegalArgumentException e) {}
    try {
      WriteBatcher batcher = moveMgr.newWriteBatcher()
        .withThreadCount(0);
      fail("should have thrown IllegalArgumentException because threadCount must be > 1");
    } catch(IllegalArgumentException e) {}
  }

  @Test
  public void testCloseListeners() {

    AtomicBoolean calledBatchListener = new AtomicBoolean(false);
    AtomicBoolean calledFailureListener = new AtomicBoolean(false);

    class CloseBatchListener implements WriteBatchListener, AutoCloseable {
      @Override
      public void close() throws Exception {
        logger.debug("Called the close method");
        calledBatchListener.set(true);
      }

      @Override
      public void processEvent(WriteBatch batch) {
        logger.debug("Processed the listener");
      }
    }

    class CloseFailureListener implements WriteFailureListener, AutoCloseable {
      @Override
      public void close() throws Exception {
        logger.debug("Called the close method");
        calledFailureListener.set(true);
      }

      @Override
      public void processFailure(WriteBatch batch, Throwable failure) {
        logger.debug("Processed the failure listener");
      }
    }

    WriteBatcher writeBatcher = moveMgr.newWriteBatcher()
        .withBatchSize(1)
        .onBatchSuccess(new CloseBatchListener())
        .onBatchFailure(new CloseFailureListener());

    moveMgr.startJob(writeBatcher);
    moveMgr.stopJob(writeBatcher);
    assertTrue( calledBatchListener.get());
    assertTrue( calledFailureListener.get());
  }

  @Test
  public void testOnes() throws Exception {
    runWriteTest(1, 1, 1, 10, "ones");
  }

  @Test
  public void testExternalThreads() throws Exception {
    runWriteTest(1, 3, 1, 10, "threads");
  }

  @Test
  public void testBatchesThreads() throws Exception {
    runWriteTest(2, 20, 20, 200, "batchesThreads");
  }

  public void runWriteTest( int batchSize, int externalThreadCount, int batcherThreadCount,
                            int totalDocCount, String testName)
  {
    String config = "{ batchSize:           " + batchSize + ",\n" +
                    "  externalThreadCount: " + externalThreadCount + ",\n" +
                    "  batcherThreadCount:  " + batcherThreadCount + ",\n" +
                    "  totalDocCount:       " + totalDocCount + " }";
    System.out.println("Starting test " + testName + " with config=" + config);

    String collection = whbTestCollection + ".testWrites_" + testName;
    String writeBatcherJobId = "WriteBatcherJobId";
    long start = System.currentTimeMillis();

    int docsPerExternalThread = Math.floorDiv(totalDocCount, externalThreadCount);
    final int expectedBatchSize = (batchSize > 0) ? batchSize : 1;
    final AtomicInteger successfulCount = new AtomicInteger(0);
    final AtomicInteger failureCount = new AtomicInteger(0);
    final AtomicInteger failureBatchCount = new AtomicInteger(0);
    final AtomicInteger successfulBatchCount = new AtomicInteger(0);
    final AtomicReference<JobTicket> batchTicket = new AtomicReference<>();
    final AtomicReference<Calendar> batchTimestamp = new AtomicReference<>();
    final StringBuffer failures = new StringBuffer();
    final int expectedBatches = (int) Math.ceil(totalDocCount / expectedBatchSize);
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(batcherThreadCount)
      .onBatchSuccess(
        batch -> {
          try {
            logger.debug("[testWrites_{}] batch: {}, items: {}", testName,
                    batch.getJobBatchNumber(), batch.getItems().length);
            successfulBatchCount.incrementAndGet();
            for ( WriteEvent event : batch.getItems() ) {
              successfulCount.incrementAndGet();
              //logger.debug("success event.getTargetUri()=[{}]", event.getTargetUri());
            }
            if ( expectedBatchSize != batch.getItems().length) {
              // if this isn't the last batch
              if ( batch.getJobBatchNumber() != expectedBatches ) {
                failures.append("ERROR: There should be " + expectedBatchSize +
                        " items in batch " + batch.getJobBatchNumber() + " but there are " + batch.getItems().length);
              }
            }
            batchTicket.set(batch.getJobTicket());
            batchTimestamp.set(batch.getTimestamp());
          } catch(Throwable e) {
            System.out.println("onBatchSuccess failed");
            e.printStackTrace(System.out);
          }
        }
      )
      .onBatchFailure(
        (batch, throwable) -> {
          try {
            failureBatchCount.incrementAndGet();
            failureCount.addAndGet(batch.getItems().length);
            throwable.printStackTrace();
            for ( WriteEvent event : batch.getItems() ) {
              logger.debug("failure event.getTargetUri()=[{}]", event.getTargetUri());
            }
            if ( expectedBatchSize != batch.getItems().length) {
              // if this isn't the last batch
              if ( batch.getJobBatchNumber() != expectedBatches ) {
                failures.append("ERROR: There should be " + expectedBatchSize +
                        " items in batch " + batch.getJobBatchNumber() + " but there are " + batch.getItems().length);
              }
            }
          } catch(Throwable e) {
            System.out.println("onBatchFailure failed");
            e.printStackTrace(System.out);
          }
        }
      )
      .withJobId(writeBatcherJobId);
    long batchMinTime = new Date().getTime();
    assertFalse( batcher.isStarted());
    moveMgr.startJob(batcher);
    assertTrue( batcher.isStarted());
    JobTicket ticket = moveMgr.getActiveJob(writeBatcherJobId);
    assertEquals(batchSize, batcher.getBatchSize());
    assertEquals(writeBatcherJobId, batcher.getJobId());
    assertEquals(batcherThreadCount, batcher.getThreadCount());

    final AtomicBoolean noExternalFailure = new AtomicBoolean(true);

    class WriteOperationRunnable implements Runnable {
      @Override
      public void run() {
        try {
          String threadName = Thread.currentThread().getName();
          DocumentWriteSet writeSet = client.newDocumentManager().newWriteSet();
          for (int j = 1; j <= docsPerExternalThread; j++) {
            String uri = "/" + collection + "/" + threadName + "/" + j + ".txt";
            DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections(whbTestCollection, collection);
            writeSet.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
          }
          for (DocumentWriteOperation doc : writeSet) {
            batcher.add(doc);
          }
        } catch(Throwable e) {
          noExternalFailure.compareAndSet(true, false);
          System.out.println("WriteOperationRunnable failed");
          e.printStackTrace(System.out);
        }
      }
    }

    class MyRunnable implements Runnable {
      @Override
      public void run() {
        try {
          String threadName = Thread.currentThread().getName();
          for (int j=1; j <= docsPerExternalThread; j++) {
            String uri = "/" + collection + "/"+ threadName + "/" + j + ".txt";
            DocumentMetadataHandle meta = new DocumentMetadataHandle()
                    .withCollections(whbTestCollection, collection);
            batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
          }
        } catch(Throwable e) {
          noExternalFailure.compareAndSet(true, false);
          System.out.println("MyRunnable failed");
          e.printStackTrace(System.out);
        }
      }
    }

    Thread[] externalThreads = new Thread[externalThreadCount];
    logger.debug("starting runnables threadCount=[{}]", externalThreadCount);
    for (int i = 0; i < externalThreadCount - 1; i++) {
      externalThreads[i] = new Thread(new MyRunnable(), testName + i);
      externalThreads[i].start();
    }
    externalThreads[externalThreadCount - 1] = new Thread(new WriteOperationRunnable(),
        testName + (externalThreadCount - 1));
    externalThreads[externalThreadCount - 1].start();
    logger.debug("started runnables threadCount=[{}]", externalThreadCount);

    for ( Thread thread : externalThreads ) {
      try { thread.join(); } catch (Exception e) {}
    }
    logger.debug("finished runnables threadCount=[{}]", externalThreadCount);
    batcher.flushAndWait();

    int leftover = (totalDocCount % docsPerExternalThread);
    logger.debug("adding leftover=[{}]", leftover);
    // write any leftovers
    for (int j =0; j < leftover; j++) {
      String uri = "/" + collection + "/"+ Thread.currentThread().getName() + "/" + j + ".txt";
      DocumentMetadataHandle meta = new DocumentMetadataHandle()
        .withCollections(whbTestCollection, collection);
      batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
    }
    logger.debug("finished leftover=[{}]", leftover);
    batcher.flushAndWait();

    JobReport report = moveMgr.getJobReport(ticket);
    //assertEquals( false, report.isJobComplete());

    assertFalse( batcher.isStopped());
    moveMgr.stopJob(ticket);
    //assertTrue( batcher.isStopped());

    if ( failures.length() > 0 ) fail(failures.toString());
    assertTrue( noExternalFailure.get());

    logger.debug("expectedSuccess=[{}] successfulCount.get()=[{}]", totalDocCount, successfulCount.get());
    assertEquals( totalDocCount, successfulCount.get());

    assertEquals( ticket, batchTicket.get());

    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collection);
    DocumentPage docs = docMgr.search(query, 1);
    assertEquals(successfulCount.get(), docs.getTotalSize());

    report = moveMgr.getJobReport(ticket);
    long maxTime = new Date().getTime();
    Date batchDate = batchTimestamp.get().getTime();
    assertTrue( batchDate.getTime() >= batchMinTime && batchDate.getTime() <= maxTime);

    long minTime = new Date().getTime()-200;
    Date reportDate = report.getReportTimestamp().getTime();
    assertTrue( reportDate.getTime() >= minTime && reportDate.getTime() <= maxTime);
    assertEquals( successfulBatchCount.get(),report.getSuccessBatchesCount());
    assertEquals( successfulCount.get(),report.getSuccessEventsCount());
    assertEquals( failureBatchCount.get(), report.getFailureBatchesCount());
    assertEquals( failureCount.get(), report.getFailureEventsCount());
    //assertEquals( true, report.isJobComplete());
    long duration = System.currentTimeMillis() - start;
    System.out.println("Completed test " + testName + " in " + duration + " millis");
  }

  @Test
  public void testWriteOneAndThrowException() {
    String directory = "/WriteBatcherTest/testWriteOneAndThrowException/";
    DocumentWriteSet writeSet = client.newDocumentManager().newWriteSet();
    writeSet.add(directory + uri1, new StringHandle("test"));
    testExceptions(writeSet, 1, 0);
  }

  @Test
  public void testWriteInvalidXMLAndThrowException() {
    String directory = "/WriteBatcherTest/testWriteInvalidXMLAndThrowException/";
    DocumentWriteSet writeSet = client.newDocumentManager().newWriteSet();
    writeSet.add(directory + uri5, new StringHandle("this is not valid XML").withFormat(Format.XML));
    testExceptions(writeSet, 0, 1);
  }

  @Test
  public void testWriteValidAndInvalidDocsAndThrowException() {
    String directory = "/WriteBatcherTest/testWriteValidAndInvalidDocsAndThrowException/";
    DocumentWriteSet writeSet = client.newDocumentManager().newWriteSet();
    writeSet.add(directory + uri1, new StringHandle("test"));
    writeSet.add(directory + uri6, new StringHandle("this is not valid XML").withFormat(Format.XML));
    writeSet.add(directory + uri2, new StringHandle("test"));
    writeSet.add(directory + uri5, new StringHandle("this is not valid XML").withFormat(Format.XML));
    writeSet.add(directory + uri3, new StringHandle("test"));
    testExceptions(writeSet, 3, 2);
  }

  private String errorMessage = "This is an expected exception used for a negative test";

  public void testExceptions(DocumentWriteSet docs, int expectedSuccesses, int expectedFailures) {
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      .onBatchSuccess( batch -> { throw new InternalError(errorMessage); } )
      .onBatchFailure( (batch, throwable) -> { throw new InternalError(errorMessage); } );
    testExceptions(batcher, docs, expectedSuccesses, expectedFailures);

    batcher = moveMgr.newWriteBatcher()
      .onBatchSuccess( batch -> { throw new RuntimeException(errorMessage); } )
      .onBatchFailure( (batch, throwable) -> { throw new RuntimeException(errorMessage); } );
    testExceptions(batcher, docs, expectedSuccesses, expectedFailures);

    cleanupDocs(docs);
  }

  public void testExceptions(WriteBatcher writeBatcher, DocumentWriteSet docs, int expectedSuccesses, int expectedFailures) {
    final AtomicInteger successfulBatchCount = new AtomicInteger(0);
    final AtomicInteger failureBatchCount = new AtomicInteger(0);
    writeBatcher
      .withBatchSize(1)
      .onBatchSuccess( batch -> successfulBatchCount.incrementAndGet() )
      .onBatchFailure( (batch, throwable) -> failureBatchCount.incrementAndGet() );
    moveMgr.startJob(writeBatcher);
    for ( DocumentWriteOperation doc : docs ) {
      writeBatcher.add(doc.getUri(), doc.getContent());
    }
    // while batchSize=1 means all batches are queued, we still need to wait for them to finish
    writeBatcher.flushAndWait();
    moveMgr.stopJob(writeBatcher);
    assertEquals(expectedSuccesses, successfulBatchCount.get());
    assertEquals(expectedFailures,  failureBatchCount.get());
  }

  public void cleanupDocs(DocumentWriteSet docs) {
    client.newDocumentManager().delete(docs.stream().map(doc -> doc.getUri()).toArray(String[]::new));
  }

  @Test
  public void testAddMultiThreadedSuccess_Issue61() throws Exception{
    String collection = whbTestCollection + ".testAddMultiThreadedSuccess_Issue61";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    WriteBatcher batcher =  moveMgr.newWriteBatcher();
    batcher.withBatchSize(100);
    batcher.onBatchSuccess(
      batch -> {
        logger.debug("[testAddMultiThreadedSuccess_Issue61] batch: {}, items: {}",
          batch.getJobBatchNumber(), batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          //System.out.println("Success "+w.getTargetUri());
        }
      });
    batcher.onBatchFailure(
      (batch, throwable) -> {
        throwable.printStackTrace();
        for(WriteEvent w:batch.getItems()){
          System.out.println("Failure "+w.getTargetUri());
        }
      });
    moveMgr.startJob(batcher);

    class MyRunnable implements Runnable {

      @Override
      public void run() {

        for (int j =0 ;j < 100; j++){
          String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
          DocumentMetadataHandle meta = new DocumentMetadataHandle()
            .withCollections(collection, whbTestCollection);
          batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
        }
        logger.debug("[testAddMultiThreadedSuccess_Issue61] added 100 docs");
        batcher.flushAndWait();
      }

    }
    Thread t1,t2,t3;
    t1 = new Thread(new MyRunnable());
    t2 = new Thread(new MyRunnable());
    t3 = new Thread(new MyRunnable());
    t1.start();
    t2.start();
    t3.start();

    t1.join();
    t2.join();
    t3.join();

    int docCount = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
    assertEquals(300, docCount);
  }

  @Test
  public void testAddMultiThreadedSuccess_Issue48() throws Exception{
    String collection = whbTestCollection + ".testAddMultiThreadedSuccess_Issue48";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    WriteBatcher batcher = moveMgr.newWriteBatcher();
    batcher.withBatchSize(120);
    batcher
      .onBatchSuccess( batch -> {
        logger.debug("[testAddMultiThreadedSuccess_Issue48] batch: {}, items: {}",
          batch.getJobBatchNumber(), batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          //System.out.println("Success "+w.getTargetUri());
        }
      })
      .onBatchFailure( (batch, throwable) -> {
        throwable.printStackTrace();
        System.out.println("Failure Batch size "+batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          System.out.println("Failure "+w.getTargetUri());
        }
      });
    moveMgr.startJob(batcher);

    FileHandle fileHandle = new FileHandle(new File("src/test/resources/test.xml"));

    class MyRunnable implements Runnable {

      @Override
      public void run() {

        for (int j =0 ;j < 100; j++){
          String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
          DocumentMetadataHandle meta = new DocumentMetadataHandle()
            .withCollections(collection, whbTestCollection);
          batcher.add(uri, meta, fileHandle);
        }
        logger.debug("[testAddMultiThreadedSuccess_Issue48] added 100 docs");
        batcher.flushAndWait();
      }
    }

    Thread t1,t2,t3;
    t1 = new Thread(new MyRunnable());
    t2 = new Thread(new MyRunnable());
    t3 = new Thread(new MyRunnable());
    t1.start();
    t2.start();
    t3.start();

    t1.join();
    t2.join();
    t3.join();

    int docCount = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
    assertEquals(300, docCount);
  }

  @Test
  public void testUndeclaredFormat_Issue60() {
    String collection = whbTestCollection + ".testUndeclaredFormat_Issue60";
    WriteBatcher batcher =  moveMgr.newWriteBatcher();
    batcher.withBatchSize(1);
    final AtomicInteger successfulCount = new AtomicInteger(0);
    batcher.onBatchSuccess(
      batch -> {
        logger.debug("[testUndeclaredFormat_Issue60] batch: {}, items: {}",
          batch.getJobBatchNumber(), batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          successfulCount.incrementAndGet();
        }
      }
    );
    batcher.onBatchFailure(
      (batch, throwable) -> {
        throwable.printStackTrace();
      }
    );
    JobTicket ticket = moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);

    String docContents = "{a:{b1:{c:\"jsonValue1\"}, b2:[\"b2 val1\", \"b2 val2\"]}}";
    batcher.add("/doc/string", meta, new StringHandle(docContents));
    batcher.flushAndWait();
    moveMgr.stopJob(ticket);

    BytesHandle readContents = client.newDocumentManager().read("/doc/string", new BytesHandle());
    System.out.println("Read doc, and format=[" + readContents.getFormat() + "]");
    assertEquals( docContents, new String(readContents.get()));

    assertEquals( 1, successfulCount.get());
  }

  @Test
  public void testCloseHandles() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(whbTestCollection);


    final AtomicInteger failCount = new AtomicInteger(0);
    WriteBatcher batcher = moveMgr.newWriteBatcher()
      .onBatchFailure(
        (batch, throwable) -> {
          logger.error("Error in testCloseHandles", throwable);
          failCount.incrementAndGet();
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    AtomicBoolean closed = new AtomicBoolean(false);
    FileInputStream fileStream = new FileInputStream("src/test/resources/test.xml") {
      public void close() throws IOException {
        super.close();
        closed.set(true);
      }
    };

    batcher.add("test.xml", meta, new InputStreamHandle(fileStream));

    // when we call flushAndWait, the WriteBatcher should write the batch the close all the handles
    batcher.flushAndWait();
    assertEquals(true, closed.get());

    moveMgr.stopJob(ticket);

    assertEquals(0, failCount.get());
  }

  // from https://github.com/marklogic/data-movement/issues/109
  @Test
  public void testMultipleFlushAnStop_Issue109() throws Throwable {
    String collection = whbTestCollection + "_testMultipleFlushAnStop_Issue109";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    assertTrue(client.newServerEval().xquery(query1).eval().next().getNumber().intValue() ==0);
    WriteBatcher ihbMT =  moveMgr.newWriteBatcher();
    ihbMT.withBatchSize(11);
    ihbMT.onBatchSuccess(
      batch -> {
        logger.debug("[testMultipleFlushAnStop_Issue109] batch: {}, items: {}",
          batch.getJobBatchNumber(), batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          //logger.debug("Success "+w.getTargetUri());
        }
      })
    .onBatchFailure(
        (batch, throwable) -> {
          //throwable.printStackTrace();
          for(WriteEvent w:batch.getItems()){
            //System.out.println("Failure "+w.getTargetUri());
          }
        });
    JobTicket writeTicket = moveMgr.startJob(ihbMT);


    AtomicReference<Throwable> unexpectedError = new AtomicReference<>();
    class MyRunnable implements Runnable {

      @Override
      public void run() {
        try {

          for (int j =0 ;j < 100; j++){
            String uri ="/local/multi-"+ j+"-"+Thread.currentThread().getId();
            logger.debug("[testMultipleFlushAnStop_Issue109] add URI:"+ uri);
            DocumentMetadataHandle meta = new DocumentMetadataHandle()
              .withCollections(collection, whbTestCollection);
            ihbMT.add(uri, meta, new StringHandle("test"));
            if(j ==80){
              logger.debug("[testMultipleFlushAnStop_Issue109] flushAndWait");
              ihbMT.flushAndWait();
              logger.debug("[testMultipleFlushAnStop_Issue109] stopJob");
              moveMgr.stopJob(writeTicket);
            }

          }
        } catch (IllegalStateException e) {
          if ( "This instance has been stopped".equals(e.getMessage()) ) {
            // this is expected behavior if we stop a job before calling flushAndWait
            logger.debug("[testMultipleFlushAnStop_Issue109] flushAndWait threw expected exception since stopJob was called first: " + e);
          } else {
            unexpectedError.set(e);
          }
        } catch (Throwable e) {
          unexpectedError.set(e);
        }
      }
    }
    Thread t1,t2;
    t1 = new Thread(new MyRunnable());
    t2 = new Thread(new MyRunnable());

    t1.start();
    t2.start();

    t1.join();
    t2.join();
    if ( unexpectedError.get() != null ) {
      throw unexpectedError.get();
    }

    System.out.println("Size is "+client.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    // we cannot test the ending size because it's inconsistent based on when the job is stopped
    // but if we made it through with no hang, then this test passes
  }

  // from https://github.com/marklogic/java-client-api/issues/595
  @Test
  public void testStopBeforeFlush_Issue595() throws Exception {
    String collection = whbTestCollection + "_testStopBeforeFlush_Issue595";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    AtomicInteger count = new AtomicInteger(0);
    AtomicBoolean isStopped = new AtomicBoolean(false);
    AtomicReference<Throwable> unexpectedError = new AtomicReference<>();
    WriteBatcher ihbMT =  moveMgr.newWriteBatcher();
    ihbMT.withBatchSize(7).withThreadCount(60);

    ihbMT.onBatchSuccess( batch -> {
      if (count.get() > 6 ) {
        boolean stopped = isStopped.getAndSet(true);
        if ( stopped == false ) {
          moveMgr.stopJob(batch.getBatcher());
          logger.debug("[testStopBeforeFlush_Issue595] Job stopped");
        }
      }
      count.addAndGet(batch.getItems().length);
      logger.debug("[testStopBeforeFlush_Issue595] batch: " + batch.getJobBatchNumber() +
        ", items: " + batch.getItems().length +
        ", writes so far: " + batch.getJobWritesSoFar() +
        ", host: " + batch.getClient().getHost());
    })
    .onBatchFailure( (batch, throwable) -> {
      Throwable cause = throwable;
      while ( cause != null ) {
        if ( cause instanceof InterruptedIOException ) {
          logger.debug("An expected InterruptedIOException occurred because the job was stopped prematurely" +
            ", batch: " + batch.getJobBatchNumber() +
            ", writes so far: " + batch.getJobWritesSoFar() +
            ", host: " + batch.getClient().getHost() +
            ", uris: " +
            Stream.of(batch.getItems()).map(event->event.getTargetUri()).collect(Collectors.toList()));
          return;
        }
        cause = cause.getCause();
      }
      //throwable.printStackTrace();
      logger.debug("[testStopBeforeFlush_Issue595] Failed Batch: batch: " + batch.getJobBatchNumber() +
        ", batch: " + batch.getJobBatchNumber() +
        ", writes so far: " + batch.getJobWritesSoFar() +
        ", host: " + batch.getClient().getHost() +
        ", uris: " +
        Stream.of(batch.getItems()).map(event->event.getTargetUri()).collect(Collectors.toList()));
    });
    moveMgr.startJob(ihbMT);

    class MyRunnable implements Runnable {

      @Override
      public void run() {
        try {
          for (int j =0 ;j < 400; j++){
            String uri ="/local/multi-"+ j+"-"+Thread.currentThread().getId();
            DocumentMetadataHandle meta = new DocumentMetadataHandle()
              .withCollections(collection, whbTestCollection);
            ihbMT.add(uri, meta, new StringHandle("test"));
          }
          logger.debug("[testStopBeforeFlush_Issue595] Finished executing thread");
        } catch (Throwable t) {
          //logger.error("", t);
        }
      }

    }
    Thread t1,t2;
    t1 = new Thread(new MyRunnable());
    t2 = new Thread(new MyRunnable());
    t1.setName("First Thread");
    t2.setName("Second Thread");

    t1.start();
    t2.start();

    t1.join();
    t2.join();

    try {
      ihbMT.flushAndWait();
      int countAfterFlushAndWait = count.get();
      logger.debug("[testStopBeforeFlush_Issue595] Count after flushAndWait: "+countAfterFlushAndWait);
      if ( countAfterFlushAndWait < 800 ) {
        Thread.currentThread().sleep(5000L);
        int countAfterSleep = count.get();
        logger.debug("[testStopBeforeFlush_Issue595] Count 5s after flushAndWait: "+countAfterSleep);
        assertEquals(countAfterFlushAndWait, countAfterSleep);
      }
      assertEquals(countAfterFlushAndWait, client.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    } catch (IllegalStateException e) {
      if ( "This instance has been stopped".equals(e.getMessage()) ) {
        // this is expected behavior if we stop a job before calling flushAndWait
        logger.debug("[testStopBeforeFlush_Issue595] flushAndWait threw expected exception since stopJob was called first: " + e);
      } else {
        throw e;
      }
    }
  }

  // TODO: uncomment this after fixing https://github.com/marklogic/java-client-api/issues/646
  @Disabled
  public void testIssue646() throws Exception {

    WriteBatcher ihb2 = moveMgr.newWriteBatcher()
      .withBatchSize(10);

    ihb2.onBatchFailure( (batch, throwable) -> throwable.printStackTrace() );

    for (int j =0 ;j < 21; j++){
      String uri ="/local/string-"+ j;
      DocumentMetadataHandle meta6 = new DocumentMetadataHandle().withProperty("docMeta-1", "true");
      ihb2.addAs(uri , meta6,	"test");
    }

    ihb2.flushAndWait();
  }

  @Test
  public void testIssue793() {
    WriteBatcher batcher =  moveMgr.newWriteBatcher();

    batcher.addAs("test.txt", "test");

    moveMgr.startJob(batcher);
    moveMgr.stopJob(batcher);
  }

  @Test
  public void testUrisWithDifferentCharacters() {
    GenericDocumentManager docMgr = client.newDocumentManager();
    DocumentWriteSet writeSet = docMgr.newWriteSet();
    StringHandle handle1 = new StringHandle("{hello:\"test1\"}");
    StringHandle handle2 = new StringHandle("{hello:\"test2\"}");

    writeSet.add("/CXXXX_Ü_9999.json", handle1);
    writeSet.add("Ä_9999.json", handle2);
    docMgr.write(writeSet);

    DocumentPage documentsPage = docMgr.read("/CXXXX_Ü_9999.json","Ä_9999.json");
    assertTrue(documentsPage.size() == 2);
    assertEquals(handle1,documentsPage.next().getContent(handle1));
    assertEquals(handle2,documentsPage.next().getContent(handle2));

    client.newDocumentManager().delete("/CXXXX_Ü_9999.json");
    client.newDocumentManager().delete("Ä_9999.json");
  }
}

/*
 * Copyright 2015 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.BatchListener;
import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.datamovement.WriteHostBatcher;

public class WriteHostBatcherTest {
  private Logger logger = LoggerFactory.getLogger(WriteHostBatcherTest.class);
  private static DataMovementManager moveMgr = DataMovementManager.newInstance();
  private static DatabaseClient client;
  private static DocumentManager<?,?> docMgr;
  private static String uri1 = "WriteHostBatcherTest_content_1.txt";
  private static String uri2 = "WriteHostBatcherTest_content_2.txt";
  private static String uri3 = "WriteHostBatcherTest_content_3.txt";
  private static String uri4 = "WriteHostBatcherTest_content_4.txt";
  private static String transform = "WriteHostBatcherTest_transform.sjs";
  private static String whbTestCollection = "WriteHostBatcherTest_" +
    new Random().nextInt(10000);


  @BeforeClass
  public static void beforeClass() {
    client = Common.connectEval();
    //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    moveMgr.withClient(client);
    docMgr = client.newDocumentManager();
    installModule();
  }

  @AfterClass
  public static void afterClass() {
    docMgr.delete(uri1, uri2, uri3);
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(whbTestCollection);
    queryMgr.delete(deleteQuery);

    client.release();
  }

  public static void installModule() {
    Common.newAdminClient().newServerConfigManager().newTransformExtensionsManager().writeJavascriptTransform(
      transform, new FileHandle(new File("src/test/resources/" + transform)));
  }

  @Test
  public void testSimple() throws Exception {
    String collection = whbTestCollection + ".testSimple";

    StringBuilder successBatch = new StringBuilder();
    StringBuilder failureBatch = new StringBuilder();
    WriteHostBatcher ihb1 =  moveMgr.newWriteHostBatcher()
      .withBatchSize(1)
      .onBatchSuccess(
        (client, batch) -> {
          for(WriteEvent w: batch.getItems()){
            successBatch.append(w.getTargetUri()+":");
          }
      })
      .onBatchFailure(
        (client, batch, throwable) -> {
          for(WriteEvent w: batch.getItems()){
            failureBatch.append(w.getTargetUri()+":");
          }
      });

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    ihb1.add("/doc/jackson", meta, new JacksonHandle(new ObjectMapper().readTree("{\"test\":true}")))
      //.add("/doc/reader_wrongxml", new ReaderHandle)
      .add("/doc/string", meta, new StringHandle("test"));
      /*
      .add("/doc/file", docMeta2, new FileHandle)
      .add("/doc/is", new InputStreamHandle)
      .add("/doc/os_wrongjson", docMeta2, new OutputStreamHandle)
      .add("/doc/bytes", docMeta1, new BytesHandle)
      .add("/doc/dom", new DomHandle);
      */

    ihb1.flush();
  }
  @Test
  public void testWrites() throws Exception {
    String collection = whbTestCollection + ".testWrites";

    assertEquals( "Since the doc doesn't exist, docMgr.exists() should return null",
      null, docMgr.exists(uri1) );

    final StringBuffer successListenerWasRun = new StringBuffer();
    final StringBuffer failListenerWasRun = new StringBuffer();
    final StringBuffer failures = new StringBuffer();
    WriteHostBatcher batcher = moveMgr.newWriteHostBatcher()
      .withBatchSize(2)
      .withTransform(
        new ServerTransform(transform)
          .addParameter("newValue", "test1a")
      )
      .onBatchSuccess(
        (client, batch) -> {
          successListenerWasRun.append("true");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      )
      .onBatchFailure(
        (client, batch, throwable) -> {
          failListenerWasRun.append("true");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    JsonNode doc1 = new ObjectMapper().readTree("{ \"testProperty\": \"test1\" }");
    JsonNode doc2 = new ObjectMapper().readTree("{ \"testProperty2\": \"test2\" }");
    // the batch with this doc will fail to write because we say withFormat(JSON)
    // but it isn't valid JSON. That will trigger our onBatchFailure listener.
    StringHandle doc3 = new StringHandle("<thisIsNotJson>test3</thisIsNotJson>")
      .withFormat(Format.JSON);
    JsonNode doc4 = new ObjectMapper().readTree("{ \"testProperty4\": \"test4\" }");
    batcher.addAs(uri1, meta, doc1);
    batcher.addAs(uri2, meta, doc2);
    batcher.add(uri3, meta, doc3);
    batcher.add(uri4, meta, new JacksonHandle(doc4));
    batcher.flush();
    assertEquals("The success listener should have run", "true", successListenerWasRun.toString());
    assertEquals("The failure listener should have run", "true", failListenerWasRun.toString());

    QueryDefinition query = new StructuredQueryBuilder().collection(collection);
    DocumentPage docs = docMgr.search(query, 1);
    // only doc1 and doc2 wrote successfully, doc3 failed
    assertEquals("there should be two docs in the collection", 2, docs.getTotalSize());

    for (DocumentRecord record : docs ) {
      if ( uri1.equals(record.getUri()) ) {
        assertEquals( "the transform should have changed testProperty to 'test1a'",
          "test1a", record.getContentAs(JsonNode.class).get("testProperty").textValue() );
      }
    }
  }

  @Test
  public void testListenerManagement() {
    BatchListener<WriteEvent> successListener = (client, batch) -> {};
    BatchFailureListener<WriteEvent> failureListener = (client, batch, throwable) -> {};

    WriteHostBatcher batcher = moveMgr.newWriteHostBatcher();
    BatchListener<WriteEvent>[] successListeners = batcher.getBatchSuccessListeners();
    assertEquals(0, successListeners.length);

    batcher.onBatchSuccess(successListener);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(1, successListeners.length);
    assertEquals(successListener, successListeners[0]);

    BatchFailureListener<WriteEvent>[] failureListeners = batcher.getBatchFailureListeners();
    assertEquals(1, failureListeners.length);
    assertEquals(HostAvailabilityListener.class, failureListeners[0].getClass());

    batcher.onBatchFailure(failureListener);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(2, failureListeners.length);
    assertEquals(failureListener, failureListeners[1]);

    batcher.setBatchSuccessListeners();
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(0, successListeners.length);

    batcher.setBatchSuccessListeners(successListener);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(1, successListeners.length);
    assertEquals(successListener, successListeners[0]);

    batcher.setBatchSuccessListeners(null);
    successListeners = batcher.getBatchSuccessListeners();
    assertEquals(0, successListeners.length);

    batcher.setBatchFailureListeners();
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(0, failureListeners.length);

    batcher.setBatchFailureListeners(failureListener);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(1, failureListeners.length);
    assertEquals(failureListener, failureListeners[0]);

    batcher.setBatchFailureListeners(null);
    failureListeners = batcher.getBatchFailureListeners();
    assertEquals(0, failureListeners.length);
  }

  /*
  @Test
  public void testWritesWithTransactions() throws Exception {
    String collection = whbTestCollection + ".testWritesWithTransactions";

    final StringBuffer successListenerWasRun = new StringBuffer();
    final StringBuffer failListenerWasRun = new StringBuffer();
    final StringBuffer failures = new StringBuffer();
    WriteHostBatcher batcher = moveMgr.newWriteHostBatcher()
      .withBatchSize(2)
      .withTransactionSize(2)
      .withThreadCount(1)
      .withTransform(
        new ServerTransform(transform)
          .addParameter("newValue", "test1a")
      )
      .onBatchSuccess(
        (client, batch) -> {
          successListenerWasRun.append("true");
          logger.debug("[testWritesWithTransactions.onBatchSuccess] batch.getJobBatchNumber()=[" + batch.getJobBatchNumber() + "]");
          if ( 2 != batch.getItems().length) {
            failures.append("ERROR: There should be 2 items in batch " + batch.getJobBatchNumber() +
              " but there are " + batch.getItems().length);
          }
        }
      )
      .onBatchFailure(
        (client, batch, throwable) -> {
          failListenerWasRun.append("true");
          throwable.printStackTrace();
          logger.debug("[testWritesWithTransactions.onBatchFailure] batch.getJobBatchNumber()=[" + batch.getJobBatchNumber() + "]");
          assertEquals("There should be two items in the batch", 2, batch.getItems().length);
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
    batcher.flush();
    moveMgr.stopJob(ticket);

    if ( failures.length() > 0 ) fail(failures.toString());
    assertEquals("The success listener should have run", "truetrue", successListenerWasRun.toString());
    assertEquals("The failure listener should have run", "truetrue", failListenerWasRun.toString());

    QueryDefinition query = new StructuredQueryBuilder().collection(collection);
    DocumentPage docs = docMgr.search(query, 1);
    // only docs 5, 7, 8, and 8 wrote successfully, docs 1, 2, 3, and 4 failed in the same
    // transaction as doc 1
    assertEquals("there should be four docs in the collection", 4, docs.getTotalSize());

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
      WriteHostBatcher batcher = moveMgr.newWriteHostBatcher()
        .withBatchSize(0);
      fail("should have thrown IllegalArgumentException because batchSize must be > 1");
    } catch(IllegalArgumentException e) {}
    try {
      WriteHostBatcher batcher = moveMgr.newWriteHostBatcher()
        .withThreadCount(0);
      fail("should have thrown IllegalArgumentException because threadCount must be > 1");
    } catch(IllegalArgumentException e) {}
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

  @Test
  public void testEverything() throws Exception {
    runWriteTest(2, 20, 20, 200, "everything");
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
    long start = System.currentTimeMillis();

    int docsPerExternalThread = Math.floorDiv(totalDocCount, externalThreadCount);
    final int expectedBatchSize = (batchSize > 0) ? batchSize : 1;
    final AtomicInteger successfulCount = new AtomicInteger(0);
    final StringBuffer failures = new StringBuffer();
    final int expectedBatches = (int) Math.ceil(totalDocCount / expectedBatchSize);
    WriteHostBatcher batcher = moveMgr.newWriteHostBatcher()
      .withBatchSize(batchSize)
      .withThreadCount(batcherThreadCount)
      .onBatchSuccess(
        (client, batch) -> {
          for ( WriteEvent event : batch.getItems() ) {
            successfulCount.incrementAndGet();
            logger.debug("success event.getTargetUri()=[{}]", event.getTargetUri());
          }
          if ( expectedBatchSize != batch.getItems().length) {
            // if this isn't the last batch
            if ( batch.getJobBatchNumber() != expectedBatches ) {
              failures.append("ERROR: There should be " + expectedBatchSize +
                " items in batch " + batch.getJobBatchNumber() + " but there are " + batch.getItems().length);
            }
          }
        }
      )
      .onBatchFailure(
        (client, batch, throwable) -> {
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
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    assertEquals(batchSize, batcher.getBatchSize());
    assertEquals(batcherThreadCount, batcher.getThreadCount());

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(whbTestCollection, collection);

    class MyRunnable implements Runnable {

      @Override
      public void run() {
        String threadName = Thread.currentThread().getName();
        for (int j=1; j <= docsPerExternalThread; j++) {
          String uri = "/" + collection + "/"+ threadName + "/" + j + ".txt";
          batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
        }
      }
    }

    Thread[] externalThreads = new Thread[externalThreadCount];
    for ( int i=0; i < externalThreads.length; i++ ) {
      externalThreads[i] = new Thread(new MyRunnable(), testName + i);
      externalThreads[i].start();
    }

    for ( Thread thread : externalThreads ) {
      try { thread.join(); } catch (Exception e) {}
    }
    batcher.flush();
    int leftover = (totalDocCount % docsPerExternalThread);
    // write any leftovers
    for (int j =0; j < leftover; j++) {
      String uri = "/" + collection + "/"+ Thread.currentThread().getName() + "/" + j + ".txt";
      batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));
    }
    batcher.flush();
    moveMgr.stopJob(ticket);

    if ( failures.length() > 0 ) fail(failures.toString());

    logger.debug("expectedSuccess=[{}] successfulCount.get()=[{}]", totalDocCount, successfulCount.get());
    assertEquals("The success listener ran wrong number of times", totalDocCount, successfulCount.get());

    QueryDefinition query = new StructuredQueryBuilder().collection(collection);
    DocumentPage docs = docMgr.search(query, 1);
    assertEquals("there should be " + successfulCount + " docs in the collection", successfulCount.get(), docs.getTotalSize());

    long duration = System.currentTimeMillis() - start;
    System.out.println("Completed test " + testName + " in " + duration + " millis");
  }

  @Test
  public void testAddMultiThreadedSuccess_Issue61() throws Exception{
    String collection = whbTestCollection + ".testAddMultiThreadedSuccess_Issue61";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    WriteHostBatcher batcher =  moveMgr.newWriteHostBatcher();
    batcher.withBatchSize(100);
    batcher.onBatchSuccess(
        (client, batch) -> {
        System.out.println("Batch size "+batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
        //System.out.println("Success "+w.getTargetUri());
        }



        }
        )
      .onBatchFailure(
          (client, batch, throwable) -> {
          throwable.printStackTrace();
          for(WriteEvent w:batch.getItems()){
          System.out.println("Failure "+w.getTargetUri());
          }


          });
    moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);

    class MyRunnable implements Runnable {

      @Override
        public void run() {

          for (int j =0 ;j < 100; j++){
            String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
            System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
            batcher.add(uri, meta, new StringHandle("test").withFormat(Format.TEXT));


          }
          batcher.flush();
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
    WriteHostBatcher batcher =  moveMgr.newWriteHostBatcher();
    batcher.withBatchSize(120);
    batcher
      .onBatchSuccess( (client, batch) -> {
        System.out.println("Success Batch size "+batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          System.out.println("Success "+w.getTargetUri());
        }
      })
      .onBatchFailure( (client, batch, throwable) -> {
        throwable.printStackTrace();
        System.out.println("Failure Batch size "+batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
          System.out.println("Failure "+w.getTargetUri());
        }
      });
    moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);

    FileHandle fileHandle = new FileHandle(new File("src/test/resources/test.xml"));

    class MyRunnable implements Runnable {

      @Override
        public void run() {

          for (int j =0 ;j < 100; j++){
            String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
            System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
            batcher.add(uri, meta, fileHandle);


          }
          batcher.flush();
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
    WriteHostBatcher batcher =  moveMgr.newWriteHostBatcher();
    batcher.withBatchSize(1);
    final AtomicInteger successfulCount = new AtomicInteger(0);
    batcher.onBatchSuccess(
        (client, batch) -> {
          for(WriteEvent w:batch.getItems()){
            successfulCount.incrementAndGet();
          }
        }
      )
      .onBatchFailure(
        (client, batch, throwable) -> {
          throwable.printStackTrace();
        }
      );
    JobTicket ticket = moveMgr.startJob(batcher);

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);

    String docContents = "{a:{b1:{c:\"jsonValue1\"}, b2:[\"b2 val1\", \"b2 val2\"]}}";
    batcher.add("/doc/string", meta, new StringHandle(docContents));
    batcher.flush();
    moveMgr.stopJob(ticket);

    BytesHandle readContents = client.newDocumentManager().read("/doc/string", new BytesHandle());
    System.out.println("Read doc, and format=[" + readContents.getFormat() + "]");
    assertEquals("contents from db should match", docContents, new String(readContents.get()));

    assertEquals("one doc should have been written", 1, successfulCount.get());
  }

  @Test
  public void testCloseHandles() throws Exception {
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(whbTestCollection);


    final AtomicInteger failCount = new AtomicInteger(0);
    WriteHostBatcher batcher =  moveMgr.newWriteHostBatcher()
      .onBatchFailure(
        (client, batch, throwable) -> {
          throwable.printStackTrace();
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

    // when we call flush, the WriteHostBatcher should write the batch the close all the handles
    batcher.flush();
    assertEquals(true, closed.get());

    moveMgr.stopJob(ticket);

    assertEquals(0, failCount.get());
  }

  @Test
  public void testMultipleFlushAnStop_Issue109() throws Exception {
    String collection = whbTestCollection + "_testMultipleFlushAnStop_Issue109";
    String query1 = "fn:count(fn:collection('" + collection + "'))";
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, whbTestCollection);
    assertTrue(client.newServerEval().xquery(query1).eval().next().getNumber().intValue() ==0);
    WriteHostBatcher ihbMT =  moveMgr.newWriteHostBatcher();
    ihbMT.withBatchSize(11);
    ihbMT.onBatchSuccess(
        (client, batch) -> {
        System.out.println("Batch size "+batch.getItems().length);
        for(WriteEvent w:batch.getItems()){
        System.out.println("Success "+w.getTargetUri());
        }
        })
    .onBatchFailure(
        (client, batch, throwable) -> {
        throwable.printStackTrace();
        for(WriteEvent w:batch.getItems()){
        System.out.println("Failure "+w.getTargetUri());
        }


        });
    JobTicket writeTicket = moveMgr.startJob(ihbMT);


    class MyRunnable implements Runnable {

      @Override
      public void run() {

        for (int j =0 ;j < 100; j++){
          String uri ="/local/multi-"+ j+"-"+Thread.currentThread().getId();
          System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
          ihbMT.add(uri, meta, new StringHandle("test"));
          if(j ==80){
            ihbMT.flush();
            moveMgr.stopJob(writeTicket);
          }

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

    System.out.println("Size is "+client.newServerEval().xquery(query1).eval().next().getNumber().intValue());
  }
}

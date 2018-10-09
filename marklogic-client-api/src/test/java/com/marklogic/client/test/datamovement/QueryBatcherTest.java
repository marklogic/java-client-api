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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import com.marklogic.client.datamovement.*;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.RawCtsQueryDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import static com.marklogic.client.io.Format.JSON;
import static com.marklogic.client.io.Format.XML;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.UrisToWriterListener;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchException;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.QueryFailureListener;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.GenericDocumentImpl;
import com.marklogic.client.datamovement.impl.QueryBatchImpl;

import com.marklogic.client.test.Common;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBatcherTest {
  private Logger logger = LoggerFactory.getLogger(QueryBatcherTest.class);
  private static DatabaseClient client = Common.connect();
  private static DataMovementManager moveMgr = client.newDataMovementManager();
  private static String uri1 = "/QueryBatcherTest/content_1.json";
  private static String uri2 = "/QueryBatcherTest/content_2.json";
  private static String uri3 = "/QueryBatcherTest/content_3.json";
  private static String uri4 = "/QueryBatcherTest/content_4.json";
  private static String uri5 = "/QueryBatcherTest/content_5.json";
  private static String collection = "QueryBatcherTest";
  private static String qhbTestCollection = "QueryBatcherTest_" +
    new Random().nextInt(10000);

  @BeforeClass
  public static void beforeClass() throws Exception {
    //((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.INFO);
    setup();
  }

  @AfterClass
  public static void afterClass() {
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
    queryMgr.delete(deleteQuery);
  }

  public static void setup() throws Exception {
    WriteBatcher writeBatcher = moveMgr.newWriteBatcher();
    moveMgr.startJob(writeBatcher);
    // a collection so we're only looking at docs related to this test
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, qhbTestCollection);
    // all the docs are one-word text docs
    writeBatcher.addAs(uri1, meta, new StringHandle("{name:\"John Doe\",   department:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri2, meta, new StringHandle("{name:\"Jane Doe\",   department:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri3, meta, new StringHandle("{name:\"John Smith\", department:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri4, meta, new StringHandle("{name:\"John Lennon\",department:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri5, meta, new StringHandle("{name:\"John Man\",   department:\"Engineering\"}").withFormat(JSON));
    writeBatcher.flushAsync();
    writeBatcher.awaitCompletion();
    moveMgr.stopJob(writeBatcher);
    StringHandle options = new StringHandle(
      "<options xmlns='http://marklogic.com/appservices/search'>" +
        "<constraint name='dept'>" +
          "<value>" +
            "<json-property>department</json-property>" +
          "</value>" +
        "</constraint>" +
      "</options>")
      .withFormat(XML);
    QueryOptionsManager queryOptionsMgr =
      Common.connectAdmin().newServerConfigManager().newQueryOptionsManager();
    queryOptionsMgr.writeOptions("employees", options);
  }

  @Test
  public void testStructuredQuery() throws Exception {
    StructuredQueryDefinition query = new StructuredQueryBuilder().collection(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 1, 2);
  }

  @Test
  public void testCollectionQuery() throws Exception {
    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 2, 1);
  }

  @Test
  public void testDirectoryQuery() throws Exception {
    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    query.setDirectory("/QueryBatcherTest");
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 3, 2);
  }

  @Test
  public void testRawCtsQuery() throws Exception {
    String ctsQuery = "<cts:directory-query xmlns:cts=\"http://marklogic.com/cts\"><cts:uri>/QueryBatcherTest/</cts:uri></cts:directory-query>";
    RawCtsQueryDefinition query = client.newQueryManager().newRawCtsQueryDefinition(new StringHandle().with(ctsQuery).withFormat(Format.XML)).withCriteria("Jane");
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 1, 2);
    ctsQuery = "{ctsquery : {\"directoryQuery\":{\"uris\":[\"/QueryBatcherTest/\"]}}}";;
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    query = client.newQueryManager().newRawCtsQueryDefinition(new StringHandle().with(ctsQuery).withFormat(Format.JSON));
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 1, 2);
  }

  @Test
  public void testStringQuery() throws Exception {
    StringQueryDefinition query = client.newQueryManager().newStringDefinition().withCriteria("John AND dept:HR");
    query.setCollections(qhbTestCollection);
    query.setOptionsName("employees");
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 99, 17);
  }

  @Test
  public void testRawValueQuery() throws Exception {
    StringHandle structuredQuery = new StringHandle(
      "{ \"query\": " +
        "{ \"queries\": [" +
          "{ \"value-query\": " +
            "{  \"json-property\": \"department\"," +
            "  \"text\": [\"HR\"]" +
            "}" +
          "}" +
        "]}" +
      "}").withFormat(JSON);
    RawStructuredQueryDefinition query = client.newQueryManager().newRawStructuredQueryDefinition(structuredQuery);
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 17, 99);
  }

  @Test
  public void testIterator() throws Exception {
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    String[] uris = new String[] {uri1, uri2, uri3, uri4, uri5};
    List<String> uriList = Arrays.asList(uris);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 1, 1, false);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 2, 2, false);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 2, 3, false);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 2, 10, false);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 10, 1, false);
    runQueryBatcher(moveMgr.newQueryBatcher(uriList.iterator()), null, matchesByForest, 18, 33, false);
  }

  @Test
  public void testRawCombinedQuery() throws Exception {
    StringHandle structuredQuery = new StringHandle(
      "{ \"search\": " +
        "{ \"query\": " +
          "{ \"queries\": [" +
            "{ \"value-query\": " +
              "{  \"json-property\": \"department\"," +
              "  \"text\": [\"HR\"]" +
              "}" +
            "}" +
          "]}" +
        "}" +
      "}").withFormat(JSON);
    RawCombinedQueryDefinition query = client.newQueryManager().newRawCombinedQueryDefinition(structuredQuery);
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, 30, 20);
  }

  public void runQueryBatcher(QueryBatcher queryBatcher, QueryDefinition query, Map<String,String[]> matchesByForest,
        int batchSize, int threadCount, boolean queryBatcherChecks) throws Exception {
    String queryBatcherJobId = "QueryBatcherJobId";
    String queryBatcherJobName = "QueryBatcherJobName";
    int numExpected = 0;
    for ( String forest : matchesByForest.keySet() ) {
      numExpected += matchesByForest.get(forest).length;
    }

    final AtomicInteger totalResults = new AtomicInteger();
    final AtomicInteger successfulBatchCount = new AtomicInteger();
    final AtomicInteger failureBatchCount = new AtomicInteger();
    final AtomicReference<String> batchDatabaseName = new AtomicReference<>();
    final AtomicReference<JobTicket> batchTicket = new AtomicReference<>();
    final AtomicReference<Calendar> batchTimestamp = new AtomicReference<>();
    final Map<String, Set<String>> results = new ConcurrentHashMap<>();
    final StringBuffer failures = new StringBuffer();
    queryBatcher
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(
        batch -> {
          successfulBatchCount.incrementAndGet();
          totalResults.addAndGet(batch.getItems().length);
          if(queryBatcherChecks) {
            String forestName = batch.getForest().getForestName();
            // atomically gets the set unless it's missing in which case it creates it
            Set<String> matches = results.computeIfAbsent(forestName, k->ConcurrentHashMap.<String>newKeySet());
            for ( String uri : batch.getItems() ) {
              matches.add(uri);
            }
            batchDatabaseName.set(batch.getForest().getDatabaseName());
          }
          batchTicket.set(batch.getJobTicket());
          batchTimestamp.set(batch.getTimestamp());
        }
      )
      .onQueryFailure(
        throwable -> {
          failureBatchCount.incrementAndGet();
          throwable.printStackTrace();
          failures.append("ERROR:[" + throwable + "]\n");
        }
      )
      .withJobId(queryBatcherJobId)
      .withJobName(queryBatcherJobName);

    assertEquals(batchSize, queryBatcher.getBatchSize());
    assertEquals(threadCount, queryBatcher.getThreadCount());
    assertEquals(queryBatcherJobId, queryBatcher.getJobId());
    assertFalse("Job should not be stopped yet", queryBatcher.isStopped());

    long minTime = new Date().getTime();
    assertFalse("Job should not be started yet", queryBatcher.isStarted());
    moveMgr.startJob(queryBatcher);
    long reportStartTime = new Date().getTime();
    JobTicket ticket = moveMgr.getActiveJob(queryBatcherJobId);
    assertTrue("Job should be started now", queryBatcher.isStarted());
    assertEquals(queryBatcherJobName, ticket.getBatcher().getJobName());

    JobReport report = moveMgr.getJobReport(ticket);
    //assertFalse("Job Report has incorrect job completion information", report.isJobComplete());
    boolean finished = queryBatcher.awaitCompletion();


    if ( finished == false ) {
      fail("Job did not finish, it was interrupted");
    }

    assertTrue("Job Report should return null for end timestamp", report.getJobEndTime() == null);
    moveMgr.stopJob(ticket.getBatcher());

    assertTrue("Job should be stopped now", queryBatcher.isStopped());
    assertEquals("Batch JobTicket should match JobTicket from startJob", ticket, batchTicket.get());

    if ( failures.length() > 0 ) {
      fail(failures.toString());
    }

    // make sure we got the right number of results
    assertEquals(numExpected, totalResults.get());

    report = moveMgr.getJobReport(ticket);
    long maxTime = new Date().getTime();
    Date batchDate = batchTimestamp.get().getTime();
    assertTrue("Batch has incorrect timestamp=" + batchDate.getTime() + " should be between " +
      minTime + " and " + maxTime, batchDate.getTime() >= minTime && batchDate.getTime() <= maxTime);
    Date reportDate = report.getReportTimestamp().getTime();
    Date reportStartDate = report.getJobStartTime().getTime();
    Date reportEndDate = report.getJobEndTime().getTime();
    assertTrue("Job Report has incorrect start timestamp", reportStartDate.getTime() >= minTime &&
      reportStartDate.getTime() <= reportStartTime);
    assertTrue("Job Report has incorrect end timestamp", reportEndDate.getTime() >= reportStartDate.getTime() &&
      reportEndDate.getTime() <= maxTime);
    assertTrue("Job Report has incorrect timestamp", reportDate.getTime() >= minTime && reportDate.getTime() <= maxTime);
    assertEquals("Job Report has incorrect successful batch counts", successfulBatchCount.get(),report.getSuccessBatchesCount());
    assertEquals("Job Report has incorrect successful event counts", totalResults.get(),report.getSuccessEventsCount());
    assertEquals("Job Report has incorrect failure batch counts", failureBatchCount.get(), report.getFailureBatchesCount());
    assertEquals("Job Report has incorrect failure events counts", failureBatchCount.get(), report.getFailureEventsCount());
    //assertEquals("Job Report has incorrect job completion information", true, report.isJobComplete());

    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) {

// TODO: verify for the entire database instead of per forest

    } else if(queryBatcherChecks) {
      assertEquals("java-unittest", batchDatabaseName.get());
      // make sure we get the same number of results via search for the same query
      SearchHandle searchResults = client.newQueryManager().search(query, new SearchHandle());
      assertEquals(numExpected, searchResults.getTotalResults());
      // if there are only the three expected forests, make sure we got the expected results per forest
      if ( queryBatcher.getForestConfig().listForests().length == 3 ) {
        for ( String forest : matchesByForest.keySet() ) {
          String[] expected = matchesByForest.get(forest);
          for ( String uri : expected ) {
            if ( results.get(forest) == null || ! results.get(forest).contains(uri) ) {
              for ( String resultsForest : results.keySet() ) {
                logger.error("Results found for forest {}: {}, expected {}", resultsForest, results.get(resultsForest),
                    Arrays.asList(matchesByForest.get(resultsForest)));
              }
              fail("Missing uri=[" + uri + "] from forest=[" + forest + "]");
            }
          }
        }
      }
    }
  }

  public void runQueryBatcher(QueryBatcher queryBatcher, QueryDefinition query, Map<String,String[]> matchesByForest,
      int batchSize, int threadCount) throws Exception {
    runQueryBatcher(queryBatcher, query, matchesByForest, batchSize, threadCount, true);
  }

  @Test
  public void testMatchOneAndThrowException() {
    StructuredQueryDefinition query = new StructuredQueryBuilder().document(uri1);
    List<String> urisIterator = testQueryExceptions(query, 1, 0);
    testIteratorExceptions(urisIterator, 1, 0);
  }

  @Test
  public void testMatchNoneAndThrowException() {
    StructuredQueryDefinition query = new StructuredQueryBuilder().document("nonExistentUri");
    List<String> urisIterator = testQueryExceptions(query, 0, 0);
    testIteratorExceptions(urisIterator, 0, 0);
  }

  @Test
  public void testBadQueryAndThrowException() {
    RawStructuredQueryDefinition query = client.newQueryManager().newRawStructuredQueryDefinition(
      new StringHandle("<this is not a valid structured query>").withFormat(JSON));
    // we'll see one failure per forest
    List<String> urisIterator = testQueryExceptions(query, 0, moveMgr.readForestConfig().listForests().length);
    // without any matching uris, there will be no success or failure batches
    testIteratorExceptions(urisIterator, 0, 0);
  }

  @Test
  public void testBadIteratorAndThrowException() {
    // On second uri let's throw an error in the iterator to trigger onQueryFailure
    List<String> urisIterator = new ArrayList<String>() {
      public Iterator<String> iterator() {
        AtomicInteger steps = new AtomicInteger(0);
        return new Iterator<String>() {
          public boolean hasNext() { return steps.get() <= 2; }
          public String next() {
            if ( steps.incrementAndGet() == 1 ) return "some uri.txt";
            else throw new InternalError(errorMessage);
          }
        };
      }
    };
    testIteratorExceptions(urisIterator, 1, 1);
  }

  private String errorMessage = "This is an expected exception used for a negative test";

  public QueryBatcher newQueryBatcher(QueryDefinition query) {
    if ( query instanceof RawStructuredQueryDefinition ) {
      return moveMgr.newQueryBatcher((RawStructuredQueryDefinition) query);
    } else if ( query instanceof StructuredQueryDefinition ) {
      return moveMgr.newQueryBatcher((StructuredQueryDefinition) query);
    } else {
      throw new IllegalStateException("Unsupported query type: " + query.getClass().getName());
    }
  }

  public List<String> testQueryExceptions(QueryDefinition query, int expectedSuccesses, int expectedFailures) {
    QueryBatcher queryBatcher = newQueryBatcher(query)
      .onUrisReady( batch -> { throw new InternalError(errorMessage); } )
      .onQueryFailure( queryThrowable -> { throw new InternalError(errorMessage); } );
    testExceptions(queryBatcher, expectedSuccesses, expectedFailures);

    // collect the uris this time
    List<String> matchingUris = Collections.synchronizedList(new ArrayList<>());
    queryBatcher = newQueryBatcher(query)
      .onUrisReady( batch -> matchingUris.addAll(Arrays.asList(batch.getItems())) )
      .onUrisReady( batch -> { throw new RuntimeException(errorMessage); } )
      .onQueryFailure( queryThrowable -> { throw new RuntimeException(errorMessage); } );
    testExceptions(queryBatcher, expectedSuccesses, expectedFailures);
    return matchingUris;
  }

  public void testIteratorExceptions(List<String> uris, int expectedSuccesses, int expectedFailures) {
    QueryBatcher uriListBatcher = moveMgr.newQueryBatcher(uris.iterator())
      .onUrisReady( batch -> { throw new InternalError(errorMessage); } )
      .onQueryFailure( queryThrowable -> { throw new InternalError(errorMessage); } );
    testExceptions(uriListBatcher, expectedSuccesses, expectedFailures);

    uriListBatcher = moveMgr.newQueryBatcher(uris.iterator())
      .onUrisReady( batch -> { throw new RuntimeException(errorMessage); } )
      .onQueryFailure( queryThrowable -> { throw new RuntimeException(errorMessage); } );
    testExceptions(uriListBatcher, expectedSuccesses, expectedFailures);
  }

  public void testExceptions(QueryBatcher queryBatcher, int expectedSuccesses, int expectedFailures) {
    final AtomicInteger successfulBatchCount = new AtomicInteger();
    final AtomicInteger failureBatchCount = new AtomicInteger();
    queryBatcher
      .withBatchSize(1)
      .onUrisReady( batch -> successfulBatchCount.incrementAndGet() )
      .onQueryFailure( queryThrowable -> failureBatchCount.incrementAndGet() );
    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
    assertEquals(expectedSuccesses, successfulBatchCount.get());
    assertEquals(expectedFailures, failureBatchCount.get());
  }

  @Test
  public void testApplyTransformListenerException() {
    final AtomicInteger failureBatchCount = new AtomicInteger();
    testListenerException(
      new ApplyTransformListener()
        .withTransform(new ServerTransform("thisTransformDoesntExist"))
        .onBatchFailure( (batch, throwable) -> failureBatchCount.incrementAndGet() )
    );
    // there should be one failure sent to the ApplyTransformListener
    // onBatchFailure listener since the transform is invalid
    assertEquals(1, failureBatchCount.get());
  }

  @Test
  public void testCloseListeners() {

    AtomicBoolean calledBatchListener = new AtomicBoolean(false);
    AtomicBoolean calledFailureListener = new AtomicBoolean(false);

    class CloseBatchListener implements QueryBatchListener, AutoCloseable {
      @Override
      public void close() throws Exception {
        logger.debug("Called the close method");
        calledBatchListener.set(true);
      }

      @Override
      public void processEvent(QueryBatch batch) {
        logger.debug("Processed the listener");
      }
    }

    class CloseFailureListener implements QueryFailureListener, AutoCloseable {
      @Override
      public void close() throws Exception {
        logger.debug("Called the close method");
        calledFailureListener.set(true);
      }

      @Override
      public void processFailure(QueryBatchException failure) {
        logger.debug("Processed the failure listener");
      }
    }

    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    query.setCollections(qhbTestCollection);
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
        .onUrisReady(new CloseBatchListener())
        .onQueryFailure(new CloseFailureListener());

    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
    assertTrue("Close method is not called on QueryBatchListener", calledBatchListener.get());
    assertTrue("Close method is not called on QueryFailureListener", calledFailureListener.get());
  }

  @Test
  public void testJobCompletionListeners() throws InterruptedException {
    AtomicBoolean urisReadyFlag = new AtomicBoolean(false);
    AtomicBoolean jobCompletionFlag = new AtomicBoolean(false);

    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    query.setCollections(qhbTestCollection);
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
        .onUrisReady(batch -> {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.warn("Thread interrupted while sleeping", e);
          }
          urisReadyFlag.set(true);
        })
        .onJobCompletion(batcher -> {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            logger.warn("Thread interrupted while sleeping", e);
          }
          assertTrue("UrisReady listener is not completed yet", urisReadyFlag.get());
          jobCompletionFlag.set(true);
        });
    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
    assertTrue("onJobCompletionListener is not called", jobCompletionFlag.get());

    urisReadyFlag.set(false);
    jobCompletionFlag.set(false);
    QueryBatcher queryBatcher2 = moveMgr.newQueryBatcher(query)
        .onUrisReady(batch -> {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.warn("Thread interrupted while sleeping", e);
          }
          urisReadyFlag.set(true);
        })
        .onJobCompletion(batcher -> {
          assertTrue("UrisReady listener is not completed yet", urisReadyFlag.get());
          jobCompletionFlag.set(true);
        });
    moveMgr.startJob(queryBatcher2);
    Thread.sleep(1100);
    assertTrue("onJobCompletionListener is not called", jobCompletionFlag.get());

    jobCompletionFlag.set(false);
    QueryBatcher queryBatcher3 = moveMgr.newQueryBatcher(query)
      .onJobCompletion(batcher -> jobCompletionFlag.set(true));
    moveMgr.startJob(queryBatcher3);
    queryBatcher3.awaitCompletion();
    moveMgr.stopJob(queryBatcher3);
    assertTrue("onJobCompletionListener is not called", jobCompletionFlag.get());

    jobCompletionFlag.set(false);
    String[] uris = new String[] {"uri1.txt", "uri2.txt", "uri3.json", "uri4.xml","uri5.png"};
    QueryBatcher queryBatcher4 = moveMgr.newQueryBatcher(Arrays.asList(uris).iterator())
        .onUrisReady(batch -> {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.warn("Thread interrupted while sleeping", e);
          }
          urisReadyFlag.set(true);
        })
        .onJobCompletion(batcher -> jobCompletionFlag.set(true));
    moveMgr.startJob(queryBatcher4);
    queryBatcher4.awaitCompletion();
    moveMgr.stopJob(queryBatcher4);
    assertTrue("onJobCompletionListener is not called", jobCompletionFlag.get());
  }

  @Test
  public void testExportListenerException() {
    final AtomicInteger failureBatchCount = new AtomicInteger();
    testListenerException(
      new ExportListener()
        .withTransform(new ServerTransform("thisTransformDoesntExist"))
        .onBatchFailure( (batch, throwable) -> failureBatchCount.incrementAndGet() )
    );
    // there should be one failure sent to the ExportListener
    // onBatchFailure listener since the transform is invalid
    assertEquals(1, failureBatchCount.get());
  }

  @Test
  public void testExportToWriterListenerException() {
    final AtomicInteger failureBatchCount = new AtomicInteger();

    testListenerException(
      new ExportToWriterListener(new StringWriter())
        .withTransform(new ServerTransform("thisTransformDoesntExist"))
        .onBatchFailure( (batch, throwable) -> failureBatchCount.incrementAndGet() )
    );
    // there should be one failure sent to the ExportToWriterListener
    // onBatchFailure listener since the transform is invalid
    assertEquals(1, failureBatchCount.get());
  }

  @Test
  public void testUrisToWriterListenerException() {
    final AtomicInteger failureBatchCount = new AtomicInteger();
    StringWriter badWriter = new StringWriter() {
      public void write(String str) {
        throw new InternalError(errorMessage);
      }
    };
    testListenerException(
      new UrisToWriterListener(badWriter)
        .onBatchFailure( (batch, throwable) -> failureBatchCount.incrementAndGet() )
    );
    // there should be one failure sent to the UrisToWriterListener
    // onBatchFailure listener since the writer is invalid
    assertEquals(1, failureBatchCount.get());
  }

  @Test
  public void testDeleteListenerException() {
    final AtomicInteger failureBatchCount = new AtomicInteger();
    testListenerException( batch -> {
        DeleteListener listener = new DeleteListener()
          .onBatchFailure( (batch2, throwable) -> failureBatchCount.incrementAndGet() );
        QueryBatch mockQueryBatch = new QueryBatchImpl() {
          public DatabaseClient getClient() {
            throw new InternalError(errorMessage);
          }

          public QueryBatcher getBatcher() {
            return moveMgr.newQueryBatcher(new StructuredQueryBuilder().collection("dummy"));
          }
        };
        listener.processEvent(mockQueryBatch);
      }
    );
    // there should be one failure sent to the DeleteListener
    // onBatchFailure listener since getClient in mockQueryBatch throws InternalError
    assertEquals(1, failureBatchCount.get());
  }

  private void testListenerException(QueryBatchListener listener) {
    final AtomicInteger failureBatchCount = new AtomicInteger();
    Iterator<String> iterator = Arrays.asList(new String[] {uri1}).iterator();
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(iterator)
      .onUrisReady( batch -> logger.debug("uri={}", batch.getItems()[0]) )
      .onUrisReady(listener)
      .onQueryFailure( queryThrowable -> failureBatchCount.incrementAndGet() );
    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    moveMgr.stopJob(queryBatcher);
    // there should be no failure sent to the QueryBatcher onQueryFailure listeners
    assertEquals(0, failureBatchCount.get());
  }

  @Test
  public void issue623() {
    String issue623Collection = qhbTestCollection + "_issue623";
    WriteBatcher wb = moveMgr.newWriteBatcher();

    String uniqueDir = issue623Collection + "/";
    ArrayList<String> uris = new ArrayList<>();
    uris.add(uniqueDir + "test_with_ampersand.txt?a=b&c=d");
    uris.add(uniqueDir + "test+with+plus.txt");
    uris.add(uniqueDir + "test/with/forwardslash.txt");
    uris.add(uniqueDir + "test.with.dot.txt");
    uris.add(uniqueDir + "test_with-every@thing!#else$*()[]:',~.txt");
    uris.add(uniqueDir + "test_with_semicolon.txt?a=b;c=d");

    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, issue623Collection);
    moveMgr.startJob(wb);
    for ( String uri : uris ) {
      wb.addAs(uri, meta, uri);
    }
    wb.flushAndWait();
    moveMgr.stopJob(wb);

    QueryDefinition collectionQuery = new StructuredQueryBuilder().collection(issue623Collection);
    QueryManager queryMgr = client.newQueryManager();
    assertEquals(uris.size(), queryMgr.search(collectionQuery, new SearchHandle()).getTotalResults());

    AtomicInteger deletedCount = new AtomicInteger(0);
    StringBuffer errors = new StringBuffer();
    QueryBatcher qb = moveMgr.newQueryBatcher(uris.iterator())
      .withThreadCount(2)
      .withBatchSize(99)
      .withConsistentSnapshot()
      .onUrisReady(new ExportListener()
        .onDocumentReady(doc -> {
          String contents = doc.getContent(new StringHandle()).get();
          if (doc.getUri().equals(contents)) {
            // all good
          } else {
            errors.append("uri=[" + doc.getUri() + "] doesn't match contents=[" + contents + "]");
          }
        })
      )
      .onUrisReady(new DeleteListener())
      .onUrisReady(batch -> deletedCount.addAndGet(batch.getItems().length))
      .onQueryFailure(exception -> exception.printStackTrace());
    moveMgr.startJob(qb);
    qb.awaitCompletion();
    moveMgr.stopJob(qb);

    assertTrue(errors.toString(), "".equals(errors.toString()));
    assertEquals(uris.size(), deletedCount.get());
    assertEquals(0, queryMgr.search(collectionQuery, new SearchHandle()).getTotalResults());
  }

  @Test
  public void testIssue658() throws Exception{
    QueryBatcher batcher =
      moveMgr.newQueryBatcher(new StructuredQueryBuilder().collection(qhbTestCollection))
      .withBatchSize(20)
      .withThreadCount(20);

    AtomicInteger batchCount = new AtomicInteger(0);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicReference<JobTicket> queryTicket = new AtomicReference<>(null);

    Set uris = Collections.synchronizedSet(new HashSet());

    batcher.onUrisReady(batch->{
      uris.addAll(Arrays.asList(batch.getItems()));
      batchCount.incrementAndGet();
      if(moveMgr.getJobReport(queryTicket.get()).getSuccessEventsCount() > 40){
        moveMgr.stopJob(queryTicket.get());
      }
    });
    batcher.onQueryFailure(throwable -> throwable.printStackTrace());

    queryTicket.set( moveMgr.startJob(batcher) );
    batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

    queryTicket.set( moveMgr.startJob(batcher) );
    batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);

    System.out.println("Success event: "+moveMgr.getJobReport(queryTicket.get()).getSuccessEventsCount());
    System.out.println("Success batch: "+moveMgr.getJobReport(queryTicket.get()).getSuccessBatchesCount());
    System.out.println("Failure event: "+moveMgr.getJobReport(queryTicket.get()).getFailureEventsCount());
    System.out.println("Failure batch: "+moveMgr.getJobReport(queryTicket.get()).getFailureBatchesCount());


    assertTrue(successCount.get() < 200);
    assertTrue(batchCount.get() == moveMgr.getJobReport(queryTicket.get()).getSuccessBatchesCount());
  }
}

/*
 * Copyright 2015-2016 MarkLogic Corporation
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import static com.marklogic.client.io.Format.JSON;
import static com.marklogic.client.io.Format.XML;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;

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
    //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    setup();
  }

  @AfterClass
  public static void afterClass() {
    QueryManager queryMgr = client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
    queryMgr.delete(deleteQuery);

    Common.release();
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
    runQueryBatcher(query, matchesByForest, 1, 2);
  }

  @Test
  public void testCollectionQuery() throws Exception {
    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {uri5});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(query, matchesByForest, 2, 1);
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
    runQueryBatcher(query, matchesByForest, 3, 2);
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
    runQueryBatcher(query, matchesByForest, 99, 17);
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
    StructuredQueryDefinition query = client.newQueryManager().newRawStructuredQueryDefinition(structuredQuery);
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri1, uri3, uri4});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri2});
    runQueryBatcher(query, matchesByForest, 17, 99);
  }


  public void runQueryBatcher(StructuredQueryDefinition query, Map<String,String[]> matchesByForest,
      int batchSize, int threadCount)
    throws Exception
  {
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, batchSize, threadCount);
  }

  public void runQueryBatcher(StringQueryDefinition query, Map<String,String[]> matchesByForest,
      int batchSize, int threadCount)
    throws Exception
  {
    runQueryBatcher(moveMgr.newQueryBatcher(query), query, matchesByForest, batchSize, threadCount);
  }

  public void runQueryBatcher(QueryBatcher queryBatcher, QueryDefinition query, Map<String,String[]> matchesByForest,
      int batchSize, int threadCount)
    throws Exception
  {
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
    final Map<String, Set<String>> results = new HashMap<>();
    final StringBuffer failures = new StringBuffer();
    queryBatcher
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(
        batch -> {
          successfulBatchCount.incrementAndGet();
          totalResults.addAndGet(batch.getItems().length);
          String forestName = batch.getForest().getForestName();
          Set<String> matches = results.get(forestName);
          if ( matches == null ) {
            matches = new HashSet<>();
            results.put(forestName, matches);
          }
          for ( String uri : batch.getItems() ) {
            matches.add(uri);
          }
          batchDatabaseName.set(batch.getForest().getDatabaseName());
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
      );

    assertEquals(batchSize, queryBatcher.getBatchSize());
    assertEquals(threadCount, queryBatcher.getThreadCount());
    assertFalse("Job should not be stopped yet", queryBatcher.isStopped());

    JobTicket ticket = moveMgr.startJob(queryBatcher);
    JobReport report = moveMgr.getJobReport(ticket);
    assertFalse("Job Report has incorrect job completion information", report.isJobComplete());
    boolean finished = queryBatcher.awaitCompletion();
    if ( finished == false ) {
      fail("Job did not finish, it was interrupted");
    }

    moveMgr.stopJob(ticket);
    assertTrue("Job should be stopped now", queryBatcher.isStopped());
    assertEquals("Batch JobTicket should match JobTicket from startJob", ticket, batchTicket.get());

    if ( failures.length() > 0 ) {
      fail(failures.toString());
    }

    assertEquals("java-unittest", batchDatabaseName.get());

    // make sure we got the right number of results
    assertEquals(numExpected, totalResults.get());

    report = moveMgr.getJobReport(ticket);
    long maxTime = new Date().getTime()+1000;
    long minTime = new Date().getTime()-1000;
    Date batchDate = batchTimestamp.get().getTime();
    assertTrue("Batch has incorrect timestamp", batchDate.getTime() >= minTime && batchDate.getTime() <= maxTime);
    Date reportDate = report.getReportTimestamp().getTime();
    assertTrue("Job Report has incorrect timestamp", reportDate.getTime() >= minTime && reportDate.getTime() <= maxTime);
    assertEquals("Job Report has incorrect successful batch counts", successfulBatchCount.get(),report.getSuccessBatchesCount());
    assertEquals("Job Report has incorrect successful event counts", totalResults.get(),report.getSuccessEventsCount());
    assertEquals("Job Report has incorrect failure batch counts", failureBatchCount.get(), report.getFailureBatchesCount());
    assertEquals("Job Report has incorrect failure events counts", failureBatchCount.get(), report.getFailureEventsCount());
    assertEquals("Job Report has incorrect job completion information", true, report.isJobComplete());

    // make sure we get the same number of results via search for the same query
    SearchHandle searchResults = client.newQueryManager().search(query, new SearchHandle());
    assertEquals(numExpected, searchResults.getTotalResults());

    // make sure we got the expected results per forest
    for ( String forest : matchesByForest.keySet() ) {
      String[] expected = matchesByForest.get(forest);
      for ( String uri : expected ) {
        if ( results.get(forest) == null || ! results.get(forest).contains(uri) ) {
          for ( String resultsForest : results.keySet() ) {
            logger.error("Results found for forest {}: {}", resultsForest, results.get(resultsForest));
          }
          fail("Missing uri=[" + uri + "] from forest=[" + forest + "]");
        }
      }
    }
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
    StructuredQueryDefinition query = client.newQueryManager().newRawStructuredQueryDefinition(
      new StringHandle("<this is not a valid structured query>").withFormat(JSON));
    // we'll see one failure per forest
    List<String> urisIterator = testQueryExceptions(query, 0, 3);
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
          public boolean hasNext() { return steps.incrementAndGet() <= 2; }
          public String next() {
            if ( steps.get() == 1 ) return "some uri.txt";
            else throw new InternalError(errorMessage);
          }
        };
      }
    };
    testIteratorExceptions(urisIterator, 1, 1);
  }

  private String errorMessage = "This is an expected exception used for a negative test";

  public List<String> testQueryExceptions(StructuredQueryDefinition query, int expectedSuccesses, int expectedFailures) {
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
      .onUrisReady( batch -> { throw new InternalError(errorMessage); } )
      .onQueryFailure( queryThrowable -> { throw new InternalError(errorMessage); } );
    testExceptions(queryBatcher, expectedSuccesses, expectedFailures);

    // collect the uris this time
    List<String> matchingUris = Collections.synchronizedList(new ArrayList<>());
    queryBatcher = moveMgr.newQueryBatcher(query)
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
}

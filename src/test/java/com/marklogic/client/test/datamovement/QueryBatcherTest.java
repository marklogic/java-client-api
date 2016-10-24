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
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import static com.marklogic.client.io.Format.JSON;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBatcherTest {
  private Logger logger = LoggerFactory.getLogger(QueryBatcherTest.class);
  private static DataMovementManager moveMgr = DataMovementManager.newInstance();
  private static String uri1 = "/QueryBatcherTest/content_1.json";
  private static String uri2 = "/QueryBatcherTest/content_2.json";
  private static String uri3 = "/QueryBatcherTest/content_3.json";
  private static String uri4 = "/QueryBatcherTest/content_4.json";
  private static String collection = "QueryBatcherTest";
  private static String qhbTestCollection = "QueryBatcherTest_" +
    new Random().nextInt(10000);

  @BeforeClass
  public static void beforeClass() throws Exception {
    Common.connect();
    //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
    moveMgr.withClient(Common.client);
    setup();
  }

  @AfterClass
  public static void afterClass() {
    QueryManager queryMgr = Common.client.newQueryManager();
    DeleteQueryDefinition deleteQuery = queryMgr.newDeleteDefinition();
    deleteQuery.setCollections(collection);
    queryMgr.delete(deleteQuery);

    Common.release();
  }

  public static void setup() throws Exception {

    assertEquals( "Since the doc doesn't exist, documentManager.exists() should return null",
      null, Common.client.newDocumentManager().exists(uri1) );

    WriteBatcher writeBatcher = moveMgr.newWriteBatcher();
    moveMgr.startJob(writeBatcher);
    // a collection so we're only looking at docs related to this test
    DocumentMetadataHandle meta = new DocumentMetadataHandle()
      .withCollections(collection, qhbTestCollection);
    // all the docs are one-word text docs
    writeBatcher.addAs(uri1, meta, new StringHandle("{name:\"John Doe\",dept:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri2, meta, new StringHandle("{name:\"Jane Doe\",dept:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri3, meta, new StringHandle("{name:\"John Smith\",dept:\"HR\"}").withFormat(JSON));
    writeBatcher.addAs(uri4, meta, new StringHandle("{name:\"John Lennon\",dept:\"HR\"}").withFormat(JSON));
    writeBatcher.flushAsync();
    writeBatcher.awaitCompletion();
  }

  @Test
  public void testStructuredQuery() throws Exception {
    QueryDefinition query = new StructuredQueryBuilder().collection(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri2});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri1, uri3, uri4});
    runQueryBatcher(query, matchesByForest, 1, 2);
  }

  @Test
  public void testCollectionQuery() throws Exception {
    QueryDefinition query = new StructuredQueryBuilder().and();
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri2});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri1, uri3, uri4});
    runQueryBatcher(query, matchesByForest, 2, 1);
  }

  @Test
  public void testDirectoryQuery() throws Exception {
    QueryDefinition query = new StructuredQueryBuilder().and();
    query.setDirectory("/QueryBatcherTest");
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri2});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri1, uri3, uri4});
    runQueryBatcher(query, matchesByForest, 3, 2);
  }

  @Test
  public void testStringQuery() throws Exception {
    QueryDefinition query = Common.client.newQueryManager().newStringDefinition().withCriteria("John");
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri1, uri3, uri4});
    runQueryBatcher(query, matchesByForest, 99, 17);
  }

  @Test
  public void testRawValueQuery() throws Exception {
    StringHandle structuredQuery = new StringHandle(
      "{ \"query\": " +
        "{ \"queries\": [" +
          "{ \"value-query\": " +
            "{  \"json-property\": \"dept\"," +
            "  \"text\": \"HR\"" +
            "}" +
          "}" +
        "]}" +
      "}").withFormat(JSON);
    QueryDefinition query = Common.client.newQueryManager().newRawStructuredQueryDefinition(structuredQuery);
    query.setCollections(qhbTestCollection);
    Map<String, String[]> matchesByForest = new HashMap<>();
    matchesByForest.put("java-unittest-1", new String[] {uri2});
    matchesByForest.put("java-unittest-2", new String[] {});
    matchesByForest.put("java-unittest-3", new String[] {uri1, uri3, uri4});
    runQueryBatcher(query, matchesByForest, 17, 99);
  }


  @Test
  public void testQBE() throws Exception {
    StringHandle qbe = new StringHandle(
      "{ dept: \"HR\" }").withFormat(JSON);
    QueryDefinition query = Common.client.newQueryManager().newRawQueryByExampleDefinition(qbe);
    AtomicReference<Throwable> error = new AtomicReference<>();
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
      .onQueryFailure(
        (client, throwable) -> error.set(throwable.getCause())
      );
    moveMgr.startJob(queryBatcher);
    queryBatcher.awaitCompletion();
    logger.debug("error.get()=[{}]", error.get());
    if ( ! (error.get() instanceof UnsupportedOperationException) ) {
      fail("The QBE should have thrown UnsupportedOperationException");
    }
  }

  public void runQueryBatcher(QueryDefinition query, Map<String,String[]> matchesByForest,
      int batchSize, int threadCount)
    throws Exception
  {
    int numExpected = 0;
    for ( String forest : matchesByForest.keySet() ) {
      numExpected += matchesByForest.get(forest).length;
    }

    final AtomicInteger urisReadyListenerWasRun = new AtomicInteger();
    final AtomicInteger totalResults = new AtomicInteger();
    final StringBuffer databaseName = new StringBuffer();
    final Map<String, Set<String>> results = new HashMap<>();
    final StringBuffer failures = new StringBuffer();
    QueryBatcher queryBatcher = moveMgr.newQueryBatcher(query)
      .withBatchSize(batchSize)
      .withThreadCount(threadCount)
      .onUrisReady(
        (client, batch) -> {
          // append one period for each run.  This should run three times because
          // there are three forests setup for the database java-unittest
          urisReadyListenerWasRun.incrementAndGet();
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
          if ( databaseName.length() == 0 ) databaseName.append(batch.getForest().getDatabaseName());
        }
      )
      .onQueryFailure(
        (client, throwable) -> {
          throwable.printStackTrace();
          failures.append("ERROR:[" + throwable + "]\n");
        }
      );

    assertEquals(batchSize, queryBatcher.getBatchSize());
    assertEquals(threadCount, queryBatcher.getThreadCount());
    moveMgr.startJob(queryBatcher);
    boolean finished = queryBatcher.awaitCompletion();
    if ( finished == false ) {
      fail("Job did not finish, it was interrupted");
    }

    if ( failures.length() > 0 ) {
      fail(failures.toString());
    }

    assertEquals("java-unittest", databaseName.toString());

    // make sure we got the right number of results
    assertEquals(numExpected, totalResults.get());

    // make sure we get the same number of results via search for the same query
    SearchHandle searchResults = Common.client.newQueryManager().search(query, new SearchHandle());
    assertEquals(numExpected, searchResults.getTotalResults());

    // make sure we got the expected results per forest
    for ( String forest : matchesByForest.keySet() ) {
      String[] expected = matchesByForest.get(forest);
      for ( String uri : expected ) {
        if ( ! results.get(forest).contains(uri) ) {
          for ( String resultsForest : results.keySet() ) {
            logger.error("Results found for forest {}: {}", resultsForest, results.get(resultsForest));
          }
          fail("Missing uri=[" + uri + "]");
        }
      }
    }
  }
}

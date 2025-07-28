/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class QueryBatcherImplTest {
  private Logger logger = LoggerFactory.getLogger(QueryBatcherImplTest.class);
  private static DataMovementManager moveMgr = Common.connect().newDataMovementManager();

  @Test
  public void testFinalize() {
    // this test can't actually validate that the message is printed, so a human
    // must check the logging output
    // Expect something like:
    //   18:03:52.308 [Finalizer] WARN  c.m.c.d.impl.QueryBatcherImpl - QueryBatcher instance "unnamed" was never cleanly stopped.  You should call dataMovementManager.stopJob.
    //
    moveMgr.newQueryBatcher(new ArrayList<String>().iterator());
  }

  @Test
  public void testPrematureStopIteratorJob() {
    // this test can't actually validate that the message is printed, so a human
    // must check the logging output
    // Expected something like:
    //   18:14:58.420 [main] WARN  c.m.c.d.impl.QueryBatcherImpl - QueryBatcher instance "unnamed" stopped before all results were processed
    List<String> list = new ArrayList<>();
    list.add("firstUri.txt");
    QueryBatcher batcher = moveMgr.newQueryBatcher(list.iterator());
    moveMgr.stopJob(batcher);
  }

  @Test
  public void testPrematureStopQueryJob() {
    // this test can't actually validate that the message is printed, so a human
    // must check the logging output
    // Expected something like:
    //   18:20:33.607 [main] WARN  c.m.c.d.impl.QueryBatcherImpl - QueryBatcher instance "unnamed" stopped before all results were retrieved
    StructuredQueryDefinition query = new StructuredQueryBuilder().and();
    QueryBatcher batcher = moveMgr.newQueryBatcher(query);
    moveMgr.stopJob(batcher);
  }

  @AfterAll
  public static void afterClass() {
    System.gc();
    System.runFinalization();
  }
}

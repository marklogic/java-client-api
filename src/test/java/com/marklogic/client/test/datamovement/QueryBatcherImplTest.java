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
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @AfterClass
  public static void afterClass() {
    System.gc();
    System.runFinalization();
  }
}

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.FilteredForestConfiguration;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.datamovement.impl.ForestImpl;
import com.marklogic.client.test.Common;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class FilteredForestConfigTest {
  private Logger logger = LoggerFactory.getLogger(FilteredForestConfigTest.class);
  private DatabaseClient client = Common.connect();
  private DataMovementManager moveMgr = client.newDataMovementManager();

  private ForestConfiguration forests = () -> new Forest[] {
    new ForestImpl("host1", "openReplicaHost1", "requestHost1", "alternateHost1", "databaseName1",
      "forestName1", "forestId1", true, false),
    new ForestImpl("host2", "openReplicaHost2", null, null, "databaseName2",
      "forestName2", "forestId2", true, false),
    new ForestImpl("host3", null, null, "alternateHost3", "databaseName3",
      "forestName3", "forestId3", true, false),
    new ForestImpl("host4", null, "requestHost4", null, "databaseName4", 
      "forestName4", "forestId4", true, false)
  };

  @Test
  public void testRename() {
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    FilteredForestConfiguration ffg = new FilteredForestConfiguration(forests)
      .withRenamedHost("host1", "host1a");

    Forest[] filteredForests = ffg.listForests();

    assertEquals("host1a", filteredForests[0].getHost());
    assertEquals("openreplicahost1", filteredForests[0].getOpenReplicaHost());
    assertEquals("alternatehost1", filteredForests[0].getAlternateHost());
    assertEquals("requesthost1", filteredForests[0].getRequestHost());
    assertEquals("forestId1", filteredForests[0].getForestId());

    ffg.withRenamedHost("openReplicaHost1", "openReplicaHost1a");

    filteredForests = ffg.listForests();

    assertEquals("host1a", filteredForests[0].getHost());
    assertEquals("openreplicahost1a", filteredForests[0].getOpenReplicaHost());
    assertEquals("alternatehost1", filteredForests[0].getAlternateHost());
    assertEquals("requesthost1", filteredForests[0].getRequestHost());
    assertEquals("forestId1", filteredForests[0].getForestId());

    ffg.withRenamedHost("alternateHost1", "alternateHost1a");

    filteredForests = ffg.listForests();

    assertEquals("host1a", filteredForests[0].getHost());
    assertEquals("openreplicahost1a", filteredForests[0].getOpenReplicaHost());
    assertEquals("alternatehost1a", filteredForests[0].getAlternateHost());
    assertEquals("requesthost1", filteredForests[0].getRequestHost());
    assertEquals("forestId1", filteredForests[0].getForestId());

    ffg.withRenamedHost("requestHost1", "requestHost1a");

    filteredForests = ffg.listForests();

    assertEquals("host1a", filteredForests[0].getHost());
    assertEquals("openreplicahost1a", filteredForests[0].getOpenReplicaHost());
    assertEquals("alternatehost1a", filteredForests[0].getAlternateHost());
    assertEquals("requesthost1a", filteredForests[0].getRequestHost());
    assertEquals("forestId1", filteredForests[0].getForestId());
  }

  @Test
  public void testBlackList() {
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    FilteredForestConfiguration ffg = new FilteredForestConfiguration(forests)
      .withBlackList("host1")
      .withBlackList("openReplicaHost2")
      .withBlackList("alternateHost3")
      .withBlackList("requestHost4");

    Forest[] filteredForests = ffg.listForests();

    // even though we black-listed "host1", it's only changed if it's the preferredHost
    assertEquals("host1", filteredForests[0].getHost());
    assertEquals("alternatehost1", filteredForests[0].getPreferredHost());
    assertEquals("openreplicahost1", filteredForests[0].getOpenReplicaHost());
    assertEquals("requesthost1", filteredForests[0].getRequestHost());
    assertEquals("alternatehost1", filteredForests[0].getAlternateHost());
    assertEquals("forestId1", filteredForests[0].getForestId());

    // we black-listed "openReplicaHost2", and it's changed because it's the preferredHost
    assertFalse("openreplicahost2".equals(filteredForests[1].getOpenReplicaHost()));
    assertFalse("openreplicahost2".equals(filteredForests[1].getPreferredHost()));
    assertEquals("host2", filteredForests[1].getHost());
    assertEquals(null, filteredForests[1].getRequestHost());
    assertEquals(null, filteredForests[1].getAlternateHost());
    assertEquals("forestId2", filteredForests[1].getForestId());

    // we black-listed "alternateHost3", and it's changed because it's the preferredHost
    assertFalse("alternatehost3".equals(filteredForests[2].getAlternateHost()));
    assertFalse("alternatehost3".equals(filteredForests[2].getPreferredHost()));
    assertEquals("host3", filteredForests[2].getHost());
    assertEquals(null, filteredForests[2].getOpenReplicaHost());
    assertEquals(null, filteredForests[2].getRequestHost());
    assertEquals("forestId3", filteredForests[2].getForestId());

    // we black-listed "requestHost4", and it's changed because it's the preferredHost
    assertFalse("requestHost4".equals(filteredForests[3].getRequestHost()));
    assertFalse("requestHost4".equals(filteredForests[3].getPreferredHost()));
    assertFalse("host4".equals(filteredForests[3].getHost()));
    assertEquals(null, filteredForests[3].getOpenReplicaHost());
    assertEquals(null, filteredForests[3].getAlternateHost());
    assertEquals("forestId4", filteredForests[3].getForestId());
  }

  @Test
  public void testWhiteList() {
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    FilteredForestConfiguration ffg = new FilteredForestConfiguration(forests)
      .withWhiteList("host1")
      .withWhiteList("openReplicaHost2")
      .withWhiteList("alternateHost3")
      .withWhiteList("requestHost4");

    Forest[] filteredForests = ffg.listForests();

    // we white-listed "host1", so it's used, but it's not the preferredHost
    assertEquals("host1", filteredForests[0].getHost());
    // we didn't white-listed "alternateHost1", so it's changed
    assertFalse("alternatehost1".equals(filteredForests[0].getAlternateHost()));
    assertFalse("alternatehost1".equals(filteredForests[0].getPreferredHost()));
    assertFalse("openreplicahost1".equals(filteredForests[0].getOpenReplicaHost()));
    assertFalse("requesthost1".equals(filteredForests[0].getRequestHost()));
    assertEquals("forestId1", filteredForests[0].getForestId());

    // we white-listed "openReplicaHost2", so it's used
    assertEquals("openreplicahost2", filteredForests[1].getOpenReplicaHost());
    assertEquals("openreplicahost2", filteredForests[1].getPreferredHost());
    // and since the preferredHost is white-listed, we left alone the non-preferred hosts
    assertEquals("host2", filteredForests[1].getHost());
    assertEquals(null, filteredForests[1].getAlternateHost());
    assertEquals(null, filteredForests[1].getRequestHost());
    assertEquals("forestId2", filteredForests[1].getForestId());

    // we white-listed "alternateHost3", and it's used
    assertEquals("alternatehost3", filteredForests[2].getAlternateHost());
    assertEquals("alternatehost3", filteredForests[2].getPreferredHost());
    // and since the preferredHost is white-listed, we left alone the non-preferred hosts
    assertEquals("host3", filteredForests[2].getHost());
    assertEquals(null, filteredForests[2].getOpenReplicaHost());
    assertEquals(null, filteredForests[1].getRequestHost());
    assertEquals("forestId3", filteredForests[2].getForestId());

    // we white-listed "requestHost4", and it's used
    assertEquals("requesthost4", filteredForests[3].getRequestHost());
    assertEquals("requesthost4", filteredForests[3].getPreferredHost());
    // and since the preferredHost is white-listed, we left alone the non-preferred hosts
    assertEquals("host4", filteredForests[3].getHost());
    assertEquals(null, filteredForests[3].getOpenReplicaHost());
    assertEquals(null, filteredForests[3].getAlternateHost());
    assertEquals("forestId4", filteredForests[3].getForestId());
  }

  @Test
  public void testWithWriteAndQueryBatcher() throws Exception{
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    ForestConfiguration forestConfig = moveMgr.readForestConfig();

    long hostNum = Stream.of(forestConfig.listForests()).map(forest->forest.getPreferredHost()).distinct().count();
    if ( hostNum <= 1 ) return; // we're not in a cluster, so this test isn't valid

    String host1 = forestConfig.listForests()[0].getPreferredHost();
    FilteredForestConfiguration ffg = new FilteredForestConfiguration(forestConfig)
      .withRenamedHost(host1, Inet4Address.getByName(host1).getHostAddress());
    runWithWriteAndQueryBatcher(ffg);
    ffg = new FilteredForestConfiguration(forestConfig)
      .withWhiteList(host1);
    runWithWriteAndQueryBatcher(ffg);
    ffg = new FilteredForestConfiguration(forestConfig)
      .withBlackList(host1);
    runWithWriteAndQueryBatcher(ffg);
  }

  @Test
  public void testWithInvalidHosts() throws Exception{
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    ForestConfiguration forestConfig = moveMgr.readForestConfig();
    String host1 = forestConfig.listForests()[0].getPreferredHost();

    FilteredForestConfiguration ffg = new FilteredForestConfiguration(forestConfig)
      .withRenamedHost("someInvalidHostName", "anotherInvalidHostName");
    runWithWriteAndQueryBatcher(ffg);
    ffg = new FilteredForestConfiguration(forestConfig)
      .withBlackList("someInvalidHostName");
    runWithWriteAndQueryBatcher(ffg);
    ffg = new FilteredForestConfiguration(forestConfig)
      .withWhiteList("someInvalidHostName")
      .withWhiteList(host1);
    runWithWriteAndQueryBatcher(ffg);
  }

  public void runWithWriteAndQueryBatcher(FilteredForestConfiguration ffg) {
    String collection = "testAgainstRealHosts_" + new Random().nextInt(10000);
    DocumentMetadataHandle meta6 = new DocumentMetadataHandle()
      .withCollections(collection)
      .withQuality(0);

    Set<String> sentUris = Collections.synchronizedSet(new HashSet<String>());
    WriteBatcher writeBatcher =  moveMgr.newWriteBatcher()
      .withBatchSize(10)
      .withForestConfig(ffg)
      .onBatchSuccess( batch -> {
        for ( WriteEvent event : batch.getItems() ) {
          sentUris.add(event.getTargetUri());
        }
      })
      .onBatchFailure( (batch, throwable) -> throwable.printStackTrace() );
    for (int j =0 ;j < 10; j++){
      String uri ="/testAgainstRealHosts/"+ j;
      writeBatcher.addAs(uri, meta6, "test");
      sentUris.add(uri);
    }
    writeBatcher.flushAndWait();
    moveMgr.stopJob(writeBatcher);

    Set<String> retrievedUris = Collections.synchronizedSet(new HashSet<String>());
    StructuredQueryDefinition query =  new StructuredQueryBuilder().collection(collection);
    QueryBatcher getUris =  moveMgr.newQueryBatcher(query)
      .withForestConfig(ffg)
      .withBatchSize(2)
      .withThreadCount(5)
      .onUrisReady(batch -> retrievedUris.addAll(Arrays.asList(batch.getItems())) )
      .onQueryFailure(exception -> exception.printStackTrace() );
    moveMgr.startJob(getUris);
    getUris.awaitCompletion();

    assertEquals(sentUris, retrievedUris);
  }
}

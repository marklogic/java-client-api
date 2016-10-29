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
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.impl.QueryBatcherImpl;
import com.marklogic.client.datamovement.impl.DataMovementServices;
import com.marklogic.client.datamovement.impl.WriteBatcherImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataMovementManagerImpl implements DataMovementManager {
  private static Logger logger = LoggerFactory.getLogger(DataMovementManager.class);
  private DataMovementServices service = new DataMovementServices();
  private ForestConfiguration forestConfig;
  private DatabaseClient primaryClient;
  // clientMap key is the hostname_database
  private Map<String,DatabaseClient> clientMap = new HashMap<>();

  public DataMovementManagerImpl() {
  }

  public DataMovementManagerImpl(DatabaseClient client) {
    this.primaryClient = client;
    clientMap.put(primaryClient.getHost(), primaryClient);
    service.setClient(primaryClient);
  }

  @Deprecated
  public DataMovementManager withClient(DatabaseClient client) {
    this.primaryClient = client;
    clientMap.put(primaryClient.getHost(), primaryClient);
    service.setClient(primaryClient);
    return this;
  }

  @Deprecated
  public DatabaseClient getClient() {
    return primaryClient;
  }

  @Override
  public void release() {
    for ( DatabaseClient client : clientMap.values() ) {
      try {
        // don't release the primaryClient because we didn't create it, it was provided to us
        if ( primaryClient != client ) client.release();
      } catch (Throwable t) {
        logger.error("Failed to release client for host \"" + client.getHost() + "\"", t);
      }
    }
  }

  @Override
  public JobTicket startJob(WriteBatcher batcher) {
    return service.startJob(batcher);
  }

  @Override
  public JobTicket startJob(QueryBatcher batcher) {
    return service.startJob(batcher);
  }

  @Override
  public JobReport getJobReport(JobTicket ticket) {
    return service.getJobReport(ticket);
  }

  @Override
  public void stopJob(JobTicket ticket) {
    service.stopJob(ticket);
  }

  @Override
  public void stopJob(Batcher batcher) {
    service.stopJob(batcher);
  }

  @Override
  public WriteBatcher newWriteBatcher() {
    verifyClientIsSet("newWriteBatcher");
    WriteBatcherImpl batcher = new WriteBatcherImpl(this, getForestConfig());
    batcher.onBatchFailure(new HostAvailabilityListener(this));
    return batcher;
  }

  @Override
  public QueryBatcher newQueryBatcher(StructuredQueryDefinition query) {
    return newQueryBatcherImpl(query);
  }

  @Override
  public QueryBatcher newQueryBatcher(StringQueryDefinition query) {
    return newQueryBatcherImpl(query);
  }

  @Override
  public QueryBatcher newQueryBatcher(RawCombinedQueryDefinition query) {
    return newQueryBatcherImpl(query);
  }

  private QueryBatcher newQueryBatcherImpl(QueryDefinition query) {
    verifyClientIsSet("newQueryBatcher");
    QueryBatcherImpl batcher = new QueryBatcherImpl(query, this, getForestConfig());
    batcher.onQueryFailure(new HostAvailabilityListener(this));
    return batcher;
  }

  @Override
  public QueryBatcher newQueryBatcher(Iterator<String> iterator) {
    verifyClientIsSet("newQueryBatcher");
    QueryBatcherImpl batcher = new QueryBatcherImpl(iterator, this, getForestConfig());
    // add a default listener to handle host failover scenarios
    batcher.onQueryFailure(new HostAvailabilityListener(this));
    return batcher;
  }

  private ForestConfiguration getForestConfig() {
    if ( forestConfig != null ) return forestConfig;
    return readForestConfig();
  }

  @Override
  public ForestConfiguration readForestConfig() {
    verifyClientIsSet("readForestConfig");
    forestConfig = service.readForestConfig();
    return forestConfig;
  }

  private void verifyClientIsSet(String method) {
    if ( primaryClient == null ) throw new IllegalStateException("The method " + method +
      " cannot be called without first calling withClient()");
  }

  public DatabaseClient getForestClient(Forest forest) {
    String hostName = forest.getHost();
    if ( forest.getOpenReplicaHost() != null ) {
      hostName = forest.getOpenReplicaHost();
    } else if ( forest.getAlternateHost() != null ) {
      hostName = forest.getAlternateHost();
    }
    String key = hostName;
    DatabaseClient client = clientMap.get(key);
    if ( client != null ) return client;
    // since this is shared across threads, let's get an exclusive lock on it before updating it
    synchronized(clientMap) {
      // just to avoid creating unnecessary DatabaseClient instances, let's check one more time if
      // another thread just barely inserted an instance that matches
      client = clientMap.get(key);
      if ( client != null ) return client;
      client = DatabaseClientFactory.newClient(
        hostName,
        primaryClient.getPort(),
        forest.getDatabaseName(),
        primaryClient.getSecurityContext()
      );
      clientMap.put(key, client);
    }
    return client;
  }
}

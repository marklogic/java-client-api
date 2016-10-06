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
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.HostBatcher;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryHostBatcher;
import com.marklogic.client.datamovement.WriteHostBatcher;
import com.marklogic.client.datamovement.impl.QueryHostBatcherImpl;
import com.marklogic.client.datamovement.impl.DataMovementServices;
import com.marklogic.client.datamovement.impl.WriteHostBatcherImpl;

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
    service.setClient(primaryClient);
  }

  @Deprecated
  public DataMovementManager withClient(DatabaseClient client) {
    this.primaryClient = client;
    service.setClient(primaryClient);
    return this;
  }

  @Deprecated
  public DatabaseClient getClient() {
    return primaryClient;
  }

  public void release() {
    for ( DatabaseClient client : clientMap.values() ) {
      try {
        client.release();
      } catch (Throwable t) {
        logger.error("Failed to release client for host \"" + client.getHost() + "\"", t);
      }
    }
  }

  public JobTicket startJob(WriteHostBatcher batcher) {
    return service.startJob(batcher);
  }

  public JobTicket startJob(QueryHostBatcher batcher) {
    return service.startJob(batcher);
  }

  public JobReport getJobReport(JobTicket ticket) {
    return service.getJobReport(ticket);
  }

  public void stopJob(JobTicket ticket) {
    service.stopJob(ticket);
  }

  public void stopJob(HostBatcher batcher) {
    service.stopJob(batcher);
  }

  public WriteHostBatcher newWriteHostBatcher() {
    verifyClientIsSet("newWriteHostBatcher");
    WriteHostBatcherImpl batcher = new WriteHostBatcherImpl(primaryClient, this, getForestConfig());
    return batcher;
  }

  public QueryHostBatcher newQueryHostBatcher(QueryDefinition query) {
    verifyClientIsSet("newQueryHostBatcher");
    QueryHostBatcherImpl batcher = new QueryHostBatcherImpl(query, this, getForestConfig());
    if ( primaryClient != null ) batcher.setClient(primaryClient);
    return batcher;
  }

  public QueryHostBatcher newQueryHostBatcher(Iterator<String> iterator) {
    verifyClientIsSet("newQueryHostBatcher");
    QueryHostBatcherImpl batcher = new QueryHostBatcherImpl(iterator, this, getForestConfig());
    if ( primaryClient != null ) batcher.setClient(primaryClient);
    return batcher;
  }

  private ForestConfiguration getForestConfig() {
    if ( forestConfig != null ) return forestConfig;
    return readForestConfig();
  }

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
    String key = forest.getHost() + "_" + forest.getDatabaseName();
    DatabaseClient client = clientMap.get(key);
    if ( client != null ) return client;
    // since this is shared across threads, let's get an exclusive lock on it before updating it
    synchronized(clientMap) {
      // just to avoid creating unnecessary DatabaseClient instances, let's check one more time if
      // another thread just barely inserted an instance that matches
      client = clientMap.get(key);
      if ( client != null ) return client;
      client = DatabaseClientFactory.newClient(
        forest.getHost(),
        primaryClient.getPort(),
        forest.getDatabaseName(),
        primaryClient.getSecurityContext()
      );
      clientMap.put(key, client);
    }
    return client;
  }
}

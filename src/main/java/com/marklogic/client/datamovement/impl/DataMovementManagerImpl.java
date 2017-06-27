/*
 * Copyright 2015-2017 MarkLogic Corporation
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
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.HostAvailabilityListener;
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.impl.QueryJobReportListener;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.impl.WriteJobReportListener;
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
import java.util.concurrent.ConcurrentHashMap;

public class DataMovementManagerImpl implements DataMovementManager {
  private static Logger logger = LoggerFactory.getLogger(DataMovementManager.class);
  private DataMovementServices service = new DataMovementServices();
  private ConcurrentHashMap<String, JobTicket> activeJobs = new ConcurrentHashMap<>();
  private ForestConfiguration forestConfig;
  private DatabaseClient primaryClient;
  // clientMap key is the hostname_database
  private Map<String,DatabaseClient> clientMap = new HashMap<>();

  public DataMovementManagerImpl(DatabaseClient client) {
    this.primaryClient = client;
    clientMap.put(primaryClient.getHost(), primaryClient);
    service.setClient(primaryClient);
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
    if ( batcher == null ) throw new IllegalArgumentException("batcher must not be null");
    return service.startJob(batcher, activeJobs);
  }

  @Override
  public JobTicket startJob(QueryBatcher batcher) {
    if ( batcher == null ) throw new IllegalArgumentException("batcher must not be null");
    return service.startJob(batcher, activeJobs);
  }

  @Override
  public JobReport getJobReport(JobTicket ticket) {
    if ( ticket == null ) throw new IllegalArgumentException("ticket must not be null");
    return service.getJobReport(ticket);
  }

  @Override
  public void stopJob(JobTicket ticket) {
    if ( ticket == null ) throw new IllegalArgumentException("ticket must not be null");
    service.stopJob(ticket, activeJobs);
  }

  @Override
  public void stopJob(Batcher batcher) {
    if ( batcher == null ) throw new IllegalArgumentException("batcher must not be null");
    service.stopJob(batcher, activeJobs);
  }

  @Override
  public WriteBatcher newWriteBatcher() {
    WriteBatcherImpl batcher = new WriteBatcherImpl(this, getForestConfig());
    batcher.onBatchFailure(new HostAvailabilityListener(this));
    WriteJobReportListener writeJobListener = new WriteJobReportListener();
    batcher.onBatchFailure(writeJobListener);
    batcher.onBatchSuccess(writeJobListener);
    return batcher;
  }

  @Override
  public QueryBatcher newQueryBatcher(StructuredQueryDefinition query) {
    return newQueryBatcherImpl(query);
  }

  @Override
  public QueryBatcher newQueryBatcher(RawStructuredQueryDefinition query) {
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
    if ( query == null ) throw new IllegalArgumentException("query must not be null");
    QueryBatcherImpl batcher = new QueryBatcherImpl(query, this, getForestConfig());
    batcher.onQueryFailure(new HostAvailabilityListener(this));
    QueryJobReportListener queryJobListener = new QueryJobReportListener();
    batcher.onQueryFailure(queryJobListener);
    batcher.onUrisReady(queryJobListener);
    return batcher;
  }

  @Override
  public QueryBatcher newQueryBatcher(Iterator<String> iterator) {
    if ( iterator == null ) throw new IllegalArgumentException("iterator must not be null");
    QueryBatcherImpl batcher = new QueryBatcherImpl(iterator, this, getForestConfig());
    // add a default listener to handle host failover scenarios
    batcher.onQueryFailure(new HostAvailabilityListener(this));
    QueryJobReportListener queryJobListener = new QueryJobReportListener();
    batcher.onQueryFailure(queryJobListener);
    batcher.onUrisReady(queryJobListener);
    return batcher;
  }

  private ForestConfiguration getForestConfig() {
    if ( forestConfig != null ) return forestConfig;
    return readForestConfig();
  }

  @Override
  public ForestConfiguration readForestConfig() {
    forestConfig = service.readForestConfig();
    return forestConfig;
  }

  public DatabaseClient getForestClient(Forest forest) {
    if ( forest == null ) throw new IllegalArgumentException("forest must not be null");
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

  @Override
  public JobTicket getActiveJob(String jobId) {
    if (jobId == null)  throw new IllegalArgumentException("Job id must not be null");
    if (activeJobs.containsKey(jobId)) {
      return activeJobs.get(jobId);
    } else {
      return null;
    }
  }
}

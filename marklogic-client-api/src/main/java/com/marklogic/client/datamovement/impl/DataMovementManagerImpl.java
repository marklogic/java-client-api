/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.query.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataMovementManagerImpl implements DataMovementManager {
  private static final Logger logger = LoggerFactory.getLogger(DataMovementManager.class);
  private DataMovementServices service = new DataMovementServices();
  private static final ConcurrentHashMap<String, JobTicket> activeJobs = new ConcurrentHashMap<>();
  private ForestConfiguration forestConfig;
  private DatabaseClient primaryClient;
  // clientMap key is the hostname_database
  private final Map<String,DatabaseClient> clientMap = new HashMap<>();

  public DataMovementManagerImpl(DatabaseClient client) {
    setPrimaryClient(client);

    clientMap.put(primaryClient.getHost(), primaryClient);
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
  public JobTicket startJob(QueryBatcher batcher) {
    if ( batcher == null ) throw new IllegalArgumentException("batcher must not be null");
    return service.startJob(batcher, activeJobs);
  }
  @Override
  public JobTicket startJob(WriteBatcher batcher) {
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
	logger.info("Stopping {} job with ID: {}", ticket.getJobType(), ticket.getJobId());
    service.stopJob(ticket, activeJobs);
  }

  @Override
  public void stopJob(Batcher batcher) {
    if ( batcher == null ) throw new IllegalArgumentException("batcher must not be null");
	logger.info("Stopping batcher; job name: {}; job ID: {}", batcher.getJobName(), batcher.getJobId());
    service.stopJob(batcher, activeJobs);
  }

  @Override
  public WriteBatcher newWriteBatcher() {
    WriteBatcherImpl batcher = new WriteBatcherImpl(this, getForestConfig());
    batcher.onBatchFailure(new HostAvailabilityListener(this));
    WriteJobReportListener writeJobListener = new WriteJobReportListener();
    batcher.onBatchFailure(writeJobListener);
    batcher.onBatchFailure(new NoResponseListener(this));
    batcher.onBatchSuccess(writeJobListener);
    return batcher;
  }

  @Override
  public QueryBatcher newQueryBatcher(CtsQueryDefinition query) {
    return newQueryBatcherImpl(query);
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
  @Override
  public QueryBatcher newQueryBatcher(RawCtsQueryDefinition query) {
    return newQueryBatcherImpl(query);
  }

  private QueryBatcher newQueryBatcherImpl(SearchQueryDefinition query) {
    if ( query == null ) throw new IllegalArgumentException("query must not be null");

    QueryBatcherImpl queryBatcher = null;
    // preprocess the query if the effective version is at least 10.0-5
    if (Long.compareUnsigned(getServerVersion(), Long.parseUnsignedLong("10000500")) >= 0) {
      DataMovementServices.QueryConfig queryConfig = service.initConfig("POST", query);
      queryBatcher = new QueryBatcherImpl(query, this, queryConfig.forestConfig,
              queryConfig.serializedCtsQuery, queryConfig.filtered,
              queryConfig.maxDocToUriBatchRatio, queryConfig.defaultDocBatchSize, queryConfig.maxUriBatchSize);
    } else {
      queryBatcher = new QueryBatcherImpl(query, this, getForestConfig());
    }

    return newQueryBatcher(queryBatcher);
  }

  @Override
  public QueryBatcher newQueryBatcher(Iterator<String> iterator) {
    if ( iterator == null ) throw new IllegalArgumentException("iterator must not be null");
    return newQueryBatcher(new QueryBatcherImpl(iterator, this, getForestConfig()));
  }

  private QueryBatcher newQueryBatcher(QueryBatcherImpl batcher) {
    // add a default listener to handle host failover scenarios
    batcher.onQueryFailure(new HostAvailabilityListener(this));
    QueryJobReportListener queryJobListener = new QueryJobReportListener();
    batcher.onQueryFailure(queryJobListener);
    batcher.onQueryFailure(new NoResponseListener(this));
    batcher.onUrisReady(queryJobListener);
    return batcher;
  }

  ForestConfiguration getForestConfig() {
    if ( forestConfig != null ) return forestConfig;
    return readForestConfig();
  }

  @Override
  public ForestConfiguration readForestConfig() {
    forestConfig = service.readForestConfig();
    return forestConfig;
  }

  public DatabaseClient getForestClient(Forest forest) {
    if (forest == null) throw new IllegalArgumentException("forest must not be null");
    return getHostClient(forest.getPreferredHost());
  }
  public DatabaseClient getHostClient(String hostName) {
    if (getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) {
      return getPrimaryClient();
    }

    DatabaseClient client = clientMap.get(hostName);
    if (client != null) return client;

    // since this is shared across threads, let's get an exclusive lock on it before updating it
    synchronized(clientMap) {
      // just to avoid creating unnecessary DatabaseClient instances, let's check one more time if
      // another thread just barely inserted an instance that matches
      client = clientMap.get(hostName);
      if (client != null) return client;

	  client = new DatabaseClientBuilder()
		  .withHost(hostName)
		  .withPort(primaryClient.getPort())
		  .withDatabase(primaryClient.getDatabase())
		  .withBasePath(primaryClient.getBasePath())
		  .withSecurityContext(primaryClient.getSecurityContext())
		  .build();
      clientMap.put(hostName, client);
    }
    return client;
  }

  @Override
  public JobTicket getActiveJob(String jobId) {
    if (jobId == null)  throw new IllegalArgumentException("Job id must not be null");
    return activeJobs.getOrDefault(jobId, null);
  }

  @Override
  public DatabaseClient.ConnectionType getConnectionType() {
    return primaryClient.getConnectionType();
  }

  @Override
  public <T> RowBatcher<T> newRowBatcher(ContentHandle<T> rowsHandle) {
    return new RowBatcherImpl<>(this, rowsHandle);
  }

  @Override
  public JobTicket startJob(RowBatcher<?> batcher) {
    if (batcher == null)
      throw new IllegalArgumentException("batcher must not be null");
    return service.startJob(batcher, activeJobs);
  }

  public DataMovementServices getDataMovementServices() {
    return service;
  }

  public void setDataMovementServices(DataMovementServices service) {
    this.service = service;
  }

  public void setPrimaryClient(DatabaseClient client) {
    this.primaryClient = client;
    service.setClient(primaryClient);
  }

  public DatabaseClient getPrimaryClient() {
    return primaryClient;
  }

  public long getServerVersion() {
    return ((DatabaseClientImpl) getPrimaryClient()).getServerVersion();
  }
}

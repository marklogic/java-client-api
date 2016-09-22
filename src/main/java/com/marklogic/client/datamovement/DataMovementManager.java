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
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.datamovement.impl.QueryHostBatcherImpl;
import com.marklogic.client.datamovement.impl.DataMovementServices;
import com.marklogic.client.datamovement.impl.WriteHostBatcherImpl;

import java.util.Iterator;

/**
 * DataMovementManager is the starting point for getting new instances of
 * QueryHostBatcher and WriteHostBatcher, configured with a DatabaseClient and
 * ForestConfiguration.  The first thing must always be to call {@link
 * #withClient withClient} which will provide a DatabaseClient with which it
 * can obtain the ForestConfiguration and from which it can create host-specific
 * DatabaseClient instances for each applicable host.  Applicable hosts are
 * those with forests for the database specified in the DatabaseClient.  If not
 * specified, the database is the default database for the port in the
 * DatabaseClient.
 *
 * Sample Usage:
 *
 *     DataMovementManager dataMovementManager = DataMovementManager.newInstance()
 *         .withClient(client);
 */
public class DataMovementManager {
  private DatabaseClient client;
  private DataMovementServices service = new DataMovementServices();
  private ForestConfiguration forestConfig;

  private DataMovementManager() {
    // TODO: implement
  }

  /**
   * Instantiate a new instance.  Generally only one DataMovementManager
   * instance is needed per database.
   *
   * @return a new DataMovementManager instance
   */
  public static DataMovementManager newInstance() {
    return new DataMovementManager();
  }

  /**
   * Set the default DatabaseClient instance that is used to retrieve the
   * ForestConfiguration and as the template for host-specific DatabaseClient
   * instances.  Host-specific DatabaseClient instances will have everything
   * the same as this instance except the hostname.
   * @param client the DatabaseClient instance
   * @return this instance for method chaining
   */
  public DataMovementManager withClient(DatabaseClient client) {
    this.client = client;
    service.setClient(client);
    return this;
  }

  /**
   * get the current DatabaseClient
   *
   * @return the current DatabaseClient
   */
  public DatabaseClient getClient() {
    return client;
  }

  /**
   * Begins job tracking on the WriteHostBatcher.  Calling startJob is not
   * required on a WriteHostBatcher if you don't intend to ever call stopJob or
   * look at the JobReport.
   * @param batcher the WriteHostBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(WriteHostBatcher batcher) {
    return service.startJob(batcher);
  }

  /**
   * Starts the QueryHostBatcher job.
   * @param batcher the QueryHostBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(QueryHostBatcher batcher) {
    return service.startJob(batcher);
  }

  /**
   * Not yet implemented 
   * @param ticket the JobTicket for which the report has to be generated
   * @return the report for the job
   */
  public JobReport getJobReport(JobTicket ticket) {
    return service.getJobReport(ticket);
  }

  /**
   * Immediately cancel all queued tasks, prevent new tasks from being added
   * to the queue, and free all resources.  In-process tasks cannot be
   * cancelled because they talk to the REST server which does not have such a
   * mechanism.
   * @param ticket indicates the job which has to be stopped
   */
  public void stopJob(JobTicket ticket) {
    service.stopJob(ticket);
  }

  /**
   * Create a new WriteHostBatcher instance.
   *
   * @return the new WriteHostBatcher instance
   */
  public WriteHostBatcher newWriteHostBatcher() {
    verifyClientIsSet("newWriteHostBatcher");
    WriteHostBatcherImpl batcher = new WriteHostBatcherImpl(getForestConfig());
    if ( client != null ) batcher.setClient(client);
    return batcher;
  }

  /**
   * Create a new QueryHostBatcher instance configured to retrieve uris that
   * match this query.  The query can be any StringQueryDefinition,
   * StructuredQueryDefinition, or RawCombinedQueryDifinition.  The query
   * cannot be a SPARQLQueryDefinition, RawQueryByExampleDefinition, or a
   * cts:query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryHostBatcher instance
   */
  public QueryHostBatcher newQueryHostBatcher(QueryDefinition query) {
    verifyClientIsSet("newQueryHostBatcher");
    QueryHostBatcherImpl batcher = new QueryHostBatcherImpl(query, getForestConfig());
    if ( client != null ) batcher.setClient(client);
    return batcher;
  }

  /**
   * Create a new QueryHostBatcher instance configured to retrieve uris from
   * this Iterator.  This is helpful when deleting documents when one cannot
   * set the server's [merge timestamp][].  For more discussion, see {@link
   * QueryHostBatcher}.
   *
   * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
   *
   * @param iterator the provider of uris
   *
   * @return the new QueryHostBatcher instance
   */
  public QueryHostBatcher newQueryHostBatcher(Iterator<String> iterator) {
    verifyClientIsSet("newQueryHostBatcher");
    QueryHostBatcherImpl batcher = new QueryHostBatcherImpl(iterator, getForestConfig());
    if ( client != null ) batcher.setClient(client);
    return batcher;
  }

  private ForestConfiguration getForestConfig() {
    if ( forestConfig != null ) return forestConfig;
    return readForestConfig();
  }

  /**
   * Update the ForestConfiguration with the latest from the server.
   *
   * @return the latest ForestConfiguration from the server
   */
  public ForestConfiguration readForestConfig() {
    verifyClientIsSet("readForestConfig");
    forestConfig = service.readForestConfig();
    return forestConfig;
  }

  private void verifyClientIsSet(String method) {
    if ( client == null ) throw new IllegalStateException("The method " + method +
      " cannot be called without first calling withClient()");
  }
}

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
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;

import java.util.Iterator;

/**
 * DataMovementManager is the starting point for getting new instances of
 * QueryHostBatcher and WriteHostBatcher, configured with a DatabaseClient and
 * ForestConfiguration.  On instantiation, it will immediately call
 * readForestConfig to obtain the ForestConfiguration from which it can create
 * host-specific DatabaseClient instances for each applicable host.  Applicable
 * hosts are those with forests for the database specified in the
 * DatabaseClient.  If not specified, the database is the default database for
 * the port in the DatabaseClient.  Call {@link release()
 * dataMovementMangaer.release()} when you're done with your
 * DataMovementManager instance to free resources associated with those
 * host-specific DatabaseClient instances.
 *
 * Sample Usage:
 *
 *     DataMovementManager dataMovementManager = databaseClient.newDataMovementManager();
 *     WriteHostBatcher batcher = dataMovementManager.newWriteHostBatcher();
 *     dataMovementManager.startJob(batcher);
 *     . . .
 *     dataMovementManager.stopJob(batcher);
 *     dataMovementManager.release();
 */
public interface DataMovementManager {
  /**
   * Instantiate a new instance.  Generally only one DataMovementManager
   * instance is needed per database.
   *
   * @return a new DataMovementManager instance
   * @deprecated use databaseClient.newDataMovementManager() instead
   */
  @Deprecated
  public static DataMovementManager newInstance() {
    return new DataMovementManagerImpl();
  }

  /**
   * Set the default DatabaseClient instance that is used to retrieve the
   * ForestConfiguration and as the template for host-specific DatabaseClient
   * instances.  Host-specific DatabaseClient instances will have everything
   * the same as this instance except the hostname.
   * @param client the DatabaseClient instance
   * @return this instance for method chaining
   */
  @Deprecated
  public DataMovementManager withClient(DatabaseClient client);

  /**
   * get the current DatabaseClient
   *
   * @return the current DatabaseClient
   */
  @Deprecated
  public DatabaseClient getClient();

  /** Calls release() on all host-specific DatabaseClient instances (but not on
   * the DatabaseClient instance used to create this DataMovementManager
   * instance).
   */
  public void release();

  /**
   * Begins job tracking on the WriteHostBatcher.  Calling startJob is not
   * required on a WriteHostBatcher if you don't intend to ever call stopJob or
   * look at the JobReport.
   * @param batcher the WriteHostBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(WriteHostBatcher batcher);

  /**
   * Starts the QueryHostBatcher job.
   * @param batcher the QueryHostBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(QueryHostBatcher batcher);

  /**
   * Not yet implemented 
   * @param ticket the JobTicket for which the report has to be generated
   * @return the report for the job
   */
  public JobReport getJobReport(JobTicket ticket);

  /**
   * Immediately cancel all queued tasks, prevent new tasks from being added
   * to the queue, and free all resources.  In-process tasks cannot be
   * cancelled because they talk to the REST server which does not have such a
   * mechanism.
   * @param ticket indicates the job which has to be stopped
   */
  public void stopJob(JobTicket ticket);

  /**
   * Immediately cancel all queued tasks, prevent new tasks from being added
   * to the queue, and free all resources.  In-process tasks cannot be
   * cancelled because they talk to the REST server which does not have such a
   * mechanism.
   * @param batcher the batcher instance to stop
   */
  public void stopJob(HostBatcher batcher);

  /**
   * Create a new WriteHostBatcher instance.
   *
   * @return the new WriteHostBatcher instance
   */
  public WriteHostBatcher newWriteHostBatcher();

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
  public QueryHostBatcher newQueryHostBatcher(QueryDefinition query);

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
  public QueryHostBatcher newQueryHostBatcher(Iterator<String> iterator);

  /**
   * Update the ForestConfiguration with the latest from the server.
   *
   * @return the latest ForestConfiguration from the server
   */
  public ForestConfiguration readForestConfig();
}

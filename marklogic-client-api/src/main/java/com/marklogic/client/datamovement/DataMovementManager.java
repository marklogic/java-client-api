/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.query.*;

import java.util.Iterator;

/**
 * <p>DataMovementManager is the starting point for getting new instances of
 * QueryBatcher, WriteBatcher and RowBatcher, configured with a DatabaseClient and
 * ForestConfiguration.  On instantiation, it will immediately call
 * readForestConfig to obtain the ForestConfiguration from which it can create
 * host-specific DatabaseClient instances for each applicable host.  Applicable
 * hosts are those with forests for the database specified in the
 * DatabaseClient.  If not specified, the database is the default database for
 * the port in the DatabaseClient.  Call {@link #release()
 * dataMovementMangaer.release()} when you're done with your
 * DataMovementManager instance to free resources associated with those
 * host-specific DatabaseClient instances.</p>
 *
 * Sample Usage:
 *
 * <pre>{@code
 *     DataMovementManager dataMovementManager = databaseClient.newDataMovementManager();
 *     WriteBatcher batcher = dataMovementManager.newWriteBatcher();
 *     dataMovementManager.startJob(batcher);
 *     . . .
 *     dataMovementManager.stopJob(batcher);
 *     dataMovementManager.release();
 *}</pre>
 */
public interface DataMovementManager {
  /** Calls release() on all host-specific DatabaseClient instances (but not on
   * the DatabaseClient instance used to create this DataMovementManager
   * instance).
   */
  public void release();

  /**
   * Begins job tracking on the WriteBatcher.  Calling startJob is not
   * required on a WriteBatcher if you don't intend to ever call stopJob or
   * look at the JobReport.
   * @param batcher the WriteBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(WriteBatcher batcher);

  /**
   * Starts the QueryBatcher job.
   * @param batcher the QueryBatcher instance which has to be started
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket startJob(QueryBatcher batcher);

  /**
   * Starts the RowBatcher job.
   * @param batcher the RowBatcher instance to start
   * @return a JobTicket which can be used to track the job
   */
  JobTicket startJob(RowBatcher<?>  batcher);

  /**
   * Get a snapshot report of the state of the job when the call is made.
   * Depends on some pre-installed listeners attached to the job to collect the
   * details.
   *
   * @param ticket the JobTicket for which the report has to be generated
   * @return the report for the job
   */
  public JobReport getJobReport(JobTicket ticket);

  /**
   * Gets the job ticket for an active job that has not been stopped by calling
   * stopJob method. It returns null if there is no such active job.
   *
   * @param jobId the jobId of the active job
   * @return a JobTicket which can be used to track the job
   */
  public JobTicket getActiveJob(String jobId);

  /**
   * Immediately cancel all queued tasks, prevent new tasks from being added
   * to the queue, and begin to free all resources.  In-process tasks cannot be
   * cancelled because they talk to the REST server which does not have such a
   * mechanism.  This does not block until all in-process tasks are finished,
   * for that use QueryBatcher.awaitCompletion() or WriteBatcher.awaitCompletion().
   *
   * @param ticket indicates the job to stop
   */
  public void stopJob(JobTicket ticket);

  /**
   * Immediately cancel all queued tasks, prevent new tasks from being added
   * to the queue, and begin to free all resources.  In-process tasks cannot be
   * cancelled because they talk to the REST server which does not have such a
   * mechanism.  This does not block until all in-process tasks are finished,
   * for that use QueryBatcher.awaitCompletion() or WriteBatcher.awaitCompletion().
   *
   * @param batcher the batcher instance to stop
   */
  public void stopJob(Batcher batcher);

  /**
   * Create a new WriteBatcher instance.
   *
   * @return the new WriteBatcher instance
   */
  public WriteBatcher newWriteBatcher();

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(CtsQueryDefinition query);

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(StructuredQueryDefinition query);

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(RawStructuredQueryDefinition query);

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(StringQueryDefinition query);

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(RawCombinedQueryDefinition query);

  /**
   * Create a new QueryBatcher instance configured to retrieve uris that
   * match this Cts query.
   *
   * @param query the query used to find matching uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(RawCtsQueryDefinition query);

  /**
   * <p>Create a new QueryBatcher instance configured to retrieve uris from this
   * Iterator.  This form enables the uris (actually any String) to come from
   * any source, whereas the other form requires the uris to come from the
   * results of a query.  This form is helpful when deleting documents when one
   * cannot set the server's
   * <a href="https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468">merge timestamp</a>.
   * For more discussion, see {@link QueryBatcher}.</p>
   *
   * <p>The Iterator needn't be thread-safe as it is only iterated from one
   * thread.</p>
   *
   * @param iterator the provider of uris
   *
   * @return the new QueryBatcher instance
   */
  public QueryBatcher newQueryBatcher(Iterator<String> iterator);

  /**
   * Create a new RowBatcher instance to export all of the rows
   * from a view in batches.
   *
   * <p>You pass a sample handle (that is, an adapter for the Java class that
   * should store a batch of retrieved rows). The handle must implement the
   * {@link ContentHandle ContentHandle}
   * and
   * {@link com.marklogic.client.io.marker.StructureReadHandle StructuredReadHandle}
   * interfaces.</p>
   *
   * @param rowsHandle a sample handle for storing a batch of rows
   * @param <T> the Java class that stores a batch of retrieved roles
   * @return the new RowBatcher instance
   */
  <T> RowBatcher<T> newRowBatcher(ContentHandle<T> rowsHandle);

  /**
   * Update the ForestConfiguration with the latest from the server.
   *
   * @return the latest ForestConfiguration from the server
   */
  public ForestConfiguration readForestConfig();

  /**
   * Identify whether the DataMovementManager connects directly to each MarkLogic host
   * with a forest for the database or whether the DataMovementManager uses a gateway
   * such as a load balancer to communicate with the MarkLogic hosts.
   * @return the connection type
   */
  public DatabaseClient.ConnectionType getConnectionType();
}

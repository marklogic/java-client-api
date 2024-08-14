/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryDefinition;

/** The context for a QueryBatch passed to QueryBatchListener or an exception
 * passed to QueryFailureListener, indicating the state at the time this event
 * occurred.
 */
public interface QueryEvent {
  /** A reference to the batcher--useful for modifying the
   * {@link Batcher#withForestConfig ForestConfiguration}
   * or calling {@link DataMovementManager#stopJob(Batcher)} if needed.
   *
   * @return the QueryBatcher that created this event
   */
  QueryBatcher getBatcher();

  /** The DatabaseClient used to retrieve this batch (if it's a job based on a
   * QueryDefinition).  This is useful for performing additional operations on
   * the same host.  If this job is based on an Iterator this is just the
   * DatabaseClient for the next host in the round-robin rotation.
   *
   * @return the DatabaseClient for this batch
   */
  DatabaseClient getClient();

  /** Within the context of the job, the numeric position of this batch.
   *
   * @return the batch number in this job
   */
  long getJobBatchNumber();

  /** Within the context of the job, the number of uris processed including the
   * uris in this event if this is a QueryBatch.
   *
   * @return the number of results in this job up to the point of this event
   */
  long getJobResultsSoFar();

  /** Within the context of this forest within the job, the numeric position of
   * this batch.
   *
   * @return the batch number in this forest
   */
  long getForestBatchNumber();

  /** Within the context of this forest within the job, the number of uris
   * processed including the uris in this event if this is a QueryBatch.
   *
   * @return the number of results in this job in this forest up to the point of this event
   */
  long getForestResultsSoFar();

  /** Within the context of this forest within the job, the last uri
   * processed including the uris in this event if this is a QueryBatch.
   *
   * @return the last uri in this job in this forest up to the point of this event
   */
  String getLastUriForForest();

  /** The forest queried for this event if this job is based on a
   * QueryDefinition.  Returns null if this job is based on an Iterator.
   *
   * @return the Forest with which this event is associated
   */
  Forest getForest();

  /** The ticket for this job.  This can be useful for getting a snapshot
   * {@link DataMovementManager#getJobReport getJobReport} or for calling
   * {@link DataMovementManager#stopJob(JobTicket) stopJob} if needed.
   *
   * @return the JobTicket for this job
   */
  JobTicket getJobTicket();
}

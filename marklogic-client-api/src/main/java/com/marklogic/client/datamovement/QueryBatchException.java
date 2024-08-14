/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.Format;

/**
 * An exception which occurred when attempting to retrieve a batch of matches
 * to a query.
 */
public class QueryBatchException extends Exception implements QueryEvent {
  private QueryEvent queryEvent;

  public QueryBatchException(QueryEvent queryEvent, Throwable cause) {
    super(cause);
    this.queryEvent = queryEvent;
  }

  /**
   * The QueryBatcher assocated with this event--useful for modifying the
   * {@link Batcher#withForestConfig ForestConfiguration}
   * or calling {@link DataMovementManager#stopJob(Batcher)} if needed.
   */
  @Override
  public QueryBatcher getBatcher() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getBatcher();
  }

  /** The DatabaseClient used to retrieve this batch (if it's a job based on a
   * QueryDefinition).  This is useful for performing additional operations on
   * the same host.  If this job is based on an Iterator this is just the
   * DatabaseClient for the next host in the round-robin rotation.
   */
  @Override
  public DatabaseClient getClient() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getClient();
  }

  /** Within the context of the job, the numeric position of this batch. */
  @Override
  public long getJobBatchNumber() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobBatchNumber();
  }

  /** Within the context of the job, the number of uris processed including the
   * uris in this event if this is a QueryBatch.
   */
  @Override
  public long getJobResultsSoFar() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobResultsSoFar();
  }

  /** Within the context of this forest within the job, the numeric position of
   * this batch.
   */
  @Override
  public long getForestBatchNumber() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForestBatchNumber();
  }

  /** Within the context of this forest within the job, the number of uris
   * processed including the uris in this event if this is a QueryBatch.
   */
  @Override
  public long getForestResultsSoFar() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForestResultsSoFar();
  }

  /** Within the context of this forest within the job, the last uri
   * processed including the uris in this event if this is a QueryBatch.
   */
  @Override
  public String getLastUriForForest() {
    return queryEvent.getLastUriForForest();
  }

  /** The forest queried for this event if this job is based on a
   * QueryDefinition.  Returns null if this job is based on an Iterator.
   */
  @Override
  public Forest getForest() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForest();
  }

  /** The ticket for this job.  This can be useful for getting a snapshot
   * {@link DataMovementManager#getJobReport getJobReport} or for calling
   * {@link DataMovementManager#stopJob(JobTicket) stopJob} if needed.
   */
  @Override
  public JobTicket getJobTicket() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobTicket();
  }
}

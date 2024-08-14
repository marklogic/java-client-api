/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;

import java.util.Calendar;

/** The base class (shared methods) for {@link QueryBatcher} and {@link WriteBatcher}. */
public interface Batcher {
  /**
   * Sets the name of the job to help with managing multiple concurrent jobs.
   *
   * @param jobName the name you would like to assign to this job
   * @return this instance (for method chaining)
   */
  Batcher withJobName(String jobName);

  /**
   * @return the job name
   */
  String getJobName();

  /**
   * Sets the unique id of the job to help with managing multiple concurrent jobs and
   * start the job with the specified job id.
   *
   * @param jobId the unique id you would like to assign to this job
   * @return this instance (for method chaining)
   */
  Batcher withJobId(String jobId);

  /**
   * @return the unique job id of the job
   */
  String getJobId();

  /**
   * <p>The size of each batch (usually 50-500). With some experimentation with
   * your custom job, this value can be tuned. Tuning this value is one of the
   * best ways to achieve optimal throughput.</p>
   *
   * <p>This method cannot be called after the job has started.</p>
   *
   * @param batchSize the batch size -- must be 1 or greater
   * @return this instance (for method chaining)
   */
  Batcher withBatchSize(int batchSize);

  /**
   * @return the batch size
   */
  int getBatchSize();

  /**
   * <p>The number of threads to be used internally by this job to perform
   * concurrent tasks on batches (usually &gt; 10).  With some experimentation with your custom
   * job and client environment, this value can be tuned.  Tuning this value is
   * one of the best ways to achieve optimal throughput or to throttle the
   * server resources used by this job.  Setting this to 1 does not guarantee
   * that batches will be processed sequentially because the calling thread
   * will sometimes also process batches.</p>
   *
   * <p>Unless otherwise noted by a subclass, this method cannot be called after the job has started.</p>
   *
   * @param threadCount the number of threads to use in this Batcher
   *
   * @return this instance (for method chaining)
   */
  Batcher withThreadCount(int threadCount);

  /**
   * @return the thread count
   */
  int getThreadCount();

  /**
   * @return the forest configuration in use by this job
   */
  ForestConfiguration getForestConfig();

  /**
   * Updates the ForestConfiguration used by this job to spread the writes or reads.
   * This can be called mid-job in order to accommodate for node failures or other
   * changes without requiring a restart of this job.  Ideally, this ForestConfiguration
   * will come from {@link DataMovementManager#readForestConfig}, perhaps wrapped by
   * something like {@link FilteredForestConfiguration}.
   *
   * @param forestConfig the updated list of forests with thier hosts, etc.
   *
   * @return this instance (for method chaining)
   */
  Batcher withForestConfig(ForestConfiguration forestConfig);

  /**
   * true if the job is started (e.g. {@link DataMovementManager#startJob
   * DataMovementManager.startJob} was called), false otherwise
   *
   * @return true if the job is started (e.g. {@link
   * DataMovementManager#startJob DataMovementManager.startJob} was called), false otherwise
   */
  boolean isStarted();

  /**
   * true if the job is terminated (e.g. {@link DataMovementManager#stopJob
   * DataMovementManager.stopJob} was called), false otherwise
   *
   * @return true if the job is terminated (e.g. {@link
   * DataMovementManager#stopJob DataMovementManager.stopJob} was called), false otherwise
   */
  boolean isStopped();

  /**
   * After the job has been started, returns the JobTicket generated when the
   * job was started.
   *
   * @return the JobTicket generated when this job was started
   *
   * @throws IllegalStateException if this job has not yet been started
   */
  JobTicket getJobTicket();

  /**
   * Gets the time at which the Batcher was started
   *
   * @return the Calendar instance and null if the job hasn't started yet
   */
  Calendar getJobStartTime();

  /**
   * Gets the time at which the Batcher was stopped
   *
   * @return the Calendar instance and null if the job hasn't ended yet
   */
  Calendar getJobEndTime();

  /**
   * Gets the primary DatabaseClient associated with the batcher
   *
   * @return the primary DatabaseClient instance
   */
  DatabaseClient getPrimaryClient();
}

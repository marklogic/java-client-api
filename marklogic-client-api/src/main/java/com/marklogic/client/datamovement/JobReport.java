/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import java.util.Calendar;

/**
 * A Job report is used to report status on a {@link WriteBatcher} or a {@link QueryBatcher}
 * job at any point of time after it is started and provide a
 * snapshot of the job's status at that time.
 * <p>
 * Note that for a query job, the counts of successful and failed events and batches only account for the process of
 * retrieving items from the database. They do not account for the processing of batches of items via user-provided
 * instances of {@link QueryBatchListener}. Such instances are responsible for determining their own definitions of
 * "success" and "failure". See {@link ApplyTransformListener} for an example of how this can be done.
 * </p>
 */
public interface JobReport {
  /**
   * {@link WriteBatcher} : gets the number of documents written to the database<br>
   * {@link QueryBatcher} : gets the number of items retrieved from the database
   * @return the number of events that succeeded
   */
  long getSuccessEventsCount();
  /**
   * {@link WriteBatcher} : gets the number of documents that were sent but failed to write<br>
   * {@link QueryBatcher} : gets the number of batches that the job failed to retrieve (same as getFailureBatchesCount)
   * @return the number of events that failed
   */
  long getFailureEventsCount();
  /**
   * {@link WriteBatcher} : gets the number of batches written to the database<br>
   * {@link QueryBatcher} : gets the number of batches retrieved from the database
   * @return the number of batches that succeeded
   */
  long getSuccessBatchesCount();
  /**
   * {@link WriteBatcher} : gets the number of batches that the job failed to write<br>
   * {@link QueryBatcher} : gets the number of batches that the job failed to retrieve (same as getFailureEventsCount)
   * @return the number of batches that failed
   */
  long getFailureBatchesCount();

  //boolean isJobComplete();

  /**
   * Gets the timestamp at which this instance of JobReport was created
   * @return the timestamp of the JobReport
   */
  Calendar getReportTimestamp();

  /**
   * Gets the timestamp at which {@link WriteBatcher} or {@link QueryBatcher}
   * started the job
   *
   * @return the job start time or null if the job hasn't started yet
   */
  Calendar getJobStartTime();

  /**
   * Gets the timestamp at which {@link WriteBatcher} or {@link QueryBatcher}
   * finished the job
   *
   * @return the job end time or null if the job hasn't ended yet
   */
  Calendar getJobEndTime();
}

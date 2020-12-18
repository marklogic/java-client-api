/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import java.util.Calendar;

/**
 * A Job report is used to report status on a {@link WriteBatcher} or a {@link QueryBatcher}
 * job at any point of time after it is started and provide a
 * snapshot of the job's status at that time.
 */
public interface JobReport {
  /**
   * {@link WriteBatcher} : gets the number of documents written to the database<br>
   * {@link QueryBatcher} : gets the number of uris processed from the database
   * @return the number of events that succeeded
   */
  long getSuccessEventsCount();
  /**
   * {@link WriteBatcher} : gets the number of documents that were sent but failed to write<br>
   * {@link QueryBatcher} : gets the number of batches that failed to process (same as getFailureBatchesCount)
   * @return the number of events that failed
   */
  long getFailureEventsCount();
  /**
   * {@link WriteBatcher} : gets the number of batches written to the database<br>
   * {@link QueryBatcher} : gets the number of batches processed from the database
   * @return the number of batches that succeeded
   */
  long getSuccessBatchesCount();
  /**
   * {@link WriteBatcher} : gets the number of batches that the job failed to write<br>
   * {@link QueryBatcher} : gets the number of batches that failed to process (same as getFailureEventsCount)
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

/*
 * Copyright 2015-2018 MarkLogic Corporation
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

import com.marklogic.client.DatabaseClient;

/** A group of items (generally documents or uris) and context representing a
 * completed action in a datamovement job.
 */
public interface Batch<T> {
  /** The documents read by WriteBatcher or the uris retrieved by QueryBatcher.
   *
   * @return the items in this batch
   */
  T[] getItems();

  /** The client-side timestamp when this batch completed writing or reading.
   *
   * @return the client timestamp when this batch completed
   */
  Calendar getTimestamp();

  /** Within the context of the job, the numeric position of this batch.
   *
   * @return the batch number
   */
  long getJobBatchNumber();

  /** The ticket for this job.  This can be useful for getting a snapshot
   * {@link DataMovementManager#getJobReport getJobReport} or for calling
   * {@link DataMovementManager#stopJob(JobTicket) stopJob} if needed.
   *
   * @return the JobTicket for this job
   */
  JobTicket getJobTicket();

  /** The DatabaseClient used to send or retrieve this batch.  This is useful
   * for performing additional operations on the same host.
   *
   * @return the DatabaseClient for this batch
   */
  DatabaseClient getClient();
}

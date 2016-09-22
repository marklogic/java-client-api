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
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobTicket.JobType;

public class JobTicketImpl implements JobTicket {
  private String jobId;
  private JobType jobType;
  private QueryHostBatcherImpl queryHostBatcher;
  private WriteHostBatcherImpl writeHostBatcher;

  public JobTicketImpl(String jobId, JobType jobType) {
    this.jobId = jobId;
    this.jobType = jobType;
  }

  @Override
  public String getJobId() {
    return jobId;
  }

  @Override
  public JobType getJobType() {
    return jobType;
  }

  public WriteHostBatcherImpl getWriteHostBatcher() {
    return writeHostBatcher;
  }

  public JobTicketImpl withWriteHostBatcher(WriteHostBatcherImpl writeHostBatcher) {
    this.writeHostBatcher = writeHostBatcher;
    return this;
  }

  public QueryHostBatcherImpl getQueryHostBatcher() {
    return queryHostBatcher;
  }

  public JobTicketImpl withQueryHostBatcher(QueryHostBatcherImpl queryHostBatcher) {
    this.queryHostBatcher = queryHostBatcher;
    return this;
  }
}

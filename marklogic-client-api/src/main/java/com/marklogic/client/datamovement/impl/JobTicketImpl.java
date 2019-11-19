/*
 * Copyright 2015-2019 MarkLogic Corporation
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

public class JobTicketImpl implements JobTicket {
  private String jobId;
  private JobType jobType;
  private QueryBatcherImpl queryBatcher;
  private WriteBatcherImpl writeBatcher;

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

  @Override
  public BatcherImpl getBatcher() {
    JobType jobType = getJobType();
    if (jobType == null) {
      throw new InternalError("null job type");
    }
    switch(jobType) {
      case QUERY_BATCHER: return getQueryBatcher();
      case WRITE_BATCHER: return getWriteBatcher();
      default:
        throw new InternalError("unknown job type: "+jobType.name());
    }
  }


  public QueryBatcherImpl getQueryBatcher() {
    return queryBatcher;
  }
  public WriteBatcherImpl getWriteBatcher() {
    return writeBatcher;
  }

  public JobTicketImpl withQueryBatcher(QueryBatcherImpl queryBatcher) {
    this.queryBatcher = queryBatcher;
    return this;
  }
  public JobTicketImpl withWriteBatcher(WriteBatcherImpl writeBatcher) {
    this.writeBatcher = writeBatcher;
    return this;
  }
}

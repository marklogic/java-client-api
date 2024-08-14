/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.JobTicket;

public class JobTicketImpl implements JobTicket {
  private String jobId;
  private JobType jobType;
  private QueryBatcherImpl queryBatcher;
  private WriteBatcherImpl writeBatcher;
  private RowBatcherImpl<?> rowBatcher;

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
      case ROW_BATCHER:   return getRowBatcher();
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
  public RowBatcherImpl<?> getRowBatcher() {
    return rowBatcher;
  }

  public JobTicketImpl withQueryBatcher(QueryBatcherImpl queryBatcher) {
    this.queryBatcher = queryBatcher;
    return this;
  }
  public JobTicketImpl withWriteBatcher(WriteBatcherImpl writeBatcher) {
    this.writeBatcher = writeBatcher;
    return this;
  }
  public JobTicketImpl withRowBatcher(RowBatcherImpl rowBatcher) {
    this.rowBatcher = rowBatcher;
    return this;
  }
}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.util.Calendar;

import com.marklogic.client.datamovement.*;

public class JobReportImpl implements JobReport {

  private long successEventsCount = 0;
  private long failureEventsCount = 0;
  private long successBatchesCount = 0;
  private long failureBatchesCount = 0;
  private boolean isJobComplete;
  private Calendar jobStartTime;
  private Calendar jobEndTime;
  private Calendar reportTimestamp;

  static public JobReportImpl about(JobTicketImpl ticket) {
    BatcherImpl       batcher = ticket.getBatcher();
    JobTicket.JobType jobType = ticket.getJobType();
    if (jobType == null) {
      throw new InternalError("null job type");
    }
    switch(jobType) {
      case QUERY_BATCHER:
        return new JobReportImpl((QueryBatcher) batcher);
      case WRITE_BATCHER:
        return new JobReportImpl((WriteBatcher) batcher);
      default:
        throw new InternalError("unknown job type: "+jobType.name());
    }
  }

  public JobReportImpl(WriteBatcher batcher) {
    WriteJobReportListener writeJobSuccessListener = null;
    WriteJobReportListener writeJobFailureListener = null;
    WriteBatchListener[] batchListeners = batcher.getBatchSuccessListeners();
    for(WriteBatchListener batchListener : batchListeners) {
      if(batchListener instanceof WriteJobReportListener) {
        writeJobSuccessListener = (WriteJobReportListener) batchListener;
        break;
      }
    }
    WriteFailureListener[] failureListeners = batcher.getBatchFailureListeners();
    for(WriteFailureListener failureListener : failureListeners) {
      if(failureListener instanceof WriteJobReportListener) {
        writeJobFailureListener = (WriteJobReportListener) failureListener;
        break;
      }
    }
    if(writeJobSuccessListener == null || writeJobFailureListener == null) {
      throw new IllegalStateException("WriteJobReportListener should be registered "
        + "in both the Success and Failure Listeners");
    }
    if(writeJobSuccessListener != writeJobFailureListener) {
      throw new IllegalStateException("The same WriteJobReportListener should be registered "
        + "in both the Success and Failure Listeners");
    }
    successBatchesCount = writeJobSuccessListener.getSuccessBatchesCount();
    failureBatchesCount = writeJobSuccessListener.getFailureBatchesCount();
    successEventsCount = writeJobSuccessListener.getSuccessEventsCount();
    failureEventsCount = writeJobSuccessListener.getFailureEventsCount();
    isJobComplete = batcher.isStopped();
    reportTimestamp = Calendar.getInstance();
    jobStartTime = batcher.getJobStartTime();
    jobEndTime = batcher.getJobEndTime();
  }

  public JobReportImpl(QueryBatcher batcher) {
    QueryJobReportListener queryJobSuccessListener = null;
    QueryJobReportListener queryJobFailureListener = null;
    for(QueryBatchListener batchListener : batcher.getUrisReadyListeners()) {
      if(batchListener instanceof QueryJobReportListener) {
        queryJobSuccessListener = (QueryJobReportListener) batchListener;
        break;
      }
    }
    for(QueryFailureListener failureListener : batcher.getQueryFailureListeners()) {
      if(failureListener instanceof QueryJobReportListener) {
        queryJobFailureListener = (QueryJobReportListener) failureListener;
        break;
      }
    }
    if(queryJobSuccessListener == null || queryJobFailureListener == null) {
      throw new IllegalStateException("QueryJobReportListener should be registered "
        + "in both the Success and Failure Listeners");
    }
    if(queryJobSuccessListener != queryJobFailureListener) {
      throw new IllegalStateException("The same QueryJobReportListener should be registered "
        + "in both the Success and Failure Listeners");
    }
    successBatchesCount = queryJobSuccessListener.getSuccessBatchesCount();
    failureBatchesCount = queryJobSuccessListener.getFailureBatchesCount();
    failureEventsCount = failureBatchesCount;
    successEventsCount = queryJobSuccessListener.getSuccessEventsCount();
    isJobComplete = batcher.isStopped();
    reportTimestamp = Calendar.getInstance();
    jobStartTime = batcher.getJobStartTime();
    jobEndTime = batcher.getJobEndTime();
  }

  @Override
  public long getSuccessEventsCount() {
    return successEventsCount;
  }

  @Override
  public long getFailureEventsCount() {
    return failureEventsCount;
  }

  @Override
  public long getSuccessBatchesCount() {
    return successBatchesCount;
  }

  @Override
  public long getFailureBatchesCount() {
    return failureBatchesCount;
  }

  public boolean isJobComplete() {
    return isJobComplete;
  }

  @Override
  public Calendar getReportTimestamp() {
    return reportTimestamp;
  }

  @Override
  public Calendar getJobStartTime() {
    return jobStartTime;
  }

  @Override
  public Calendar getJobEndTime() {
    return jobEndTime;
  }
}

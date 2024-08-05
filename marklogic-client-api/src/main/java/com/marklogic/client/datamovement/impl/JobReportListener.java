/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.util.concurrent.atomic.AtomicLong;

public class JobReportListener {

  protected AtomicLong successEventsCount = new AtomicLong(0);
  protected AtomicLong failureEventsCount = new AtomicLong(0);
  protected AtomicLong successBatchesCount = new AtomicLong(0);
  protected AtomicLong failureBatchesCount = new AtomicLong(0);

  public long getSuccessEventsCount() {
    return successEventsCount.get();
  }

  public long getFailureEventsCount() {
    return failureEventsCount.get();
  }

  public long getSuccessBatchesCount() {
    return successBatchesCount.get();
  }

  public long getFailureBatchesCount() {
    return failureBatchesCount.get();
  }
}

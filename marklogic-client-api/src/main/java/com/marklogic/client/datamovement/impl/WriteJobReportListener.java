/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatchListener;
import com.marklogic.client.datamovement.WriteFailureListener;

public class WriteJobReportListener extends JobReportListener implements  WriteBatchListener, WriteFailureListener {

  @Override
  public void processFailure(WriteBatch batch, Throwable failure) {
    // Increment the number of batches that failed
    failureBatchesCount.incrementAndGet();

    // Get the number of written documents that failed
    failureEventsCount.addAndGet(batch.getItems().length);
  }

  @Override
  public void processEvent(WriteBatch batch) {
    // Increment the number of batches that succeeded
    successBatchesCount.incrementAndGet();

    // Get the number of written documents that succeeded
    successEventsCount.addAndGet(batch.getItems().length);
  }
}

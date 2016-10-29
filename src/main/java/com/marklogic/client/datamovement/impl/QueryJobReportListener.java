package com.marklogic.client.datamovement.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.QueryFailureListener;
import com.marklogic.client.datamovement.QueryHostException;

public class QueryJobReportListener extends JobReportListener implements  QueryBatchListener, QueryFailureListener {

  @Override
  public void processFailure(DatabaseClient client, QueryHostException failure) {
    // Increment the number of batches that failed
    failureBatchesCount.incrementAndGet();
  }

  @Override
  public void processEvent(DatabaseClient client, QueryBatch batch) {
    // Increment the number of batches that succeeded
    successBatchesCount.incrementAndGet();

    // Get the number of documents that have been read successfully
    successEventsCount.addAndGet(batch.getItems().length);
  }

}

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
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.QueryFailureListener;
import com.marklogic.client.datamovement.QueryBatchException;

public class QueryJobReportListener extends JobReportListener implements  QueryBatchListener, QueryFailureListener {

  @Override
  public void processFailure(QueryBatchException failure) {
    // Increment the number of batches that failed
    failureBatchesCount.incrementAndGet();
  }

  @Override
  public void processEvent(QueryBatch batch) {
    // Increment the number of batches that succeeded
    successBatchesCount.incrementAndGet();

    // Get the number of documents that have been read successfully
    successEventsCount.addAndGet(batch.getItems().length);
  }

}

/*
 * Copyright 2015-2017 MarkLogic Corporation
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

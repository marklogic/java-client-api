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
package com.marklogic.client.datamovement;

/**
 * Runs processEvent on each batch as it is ready during a WriteBatcher
 * job.
 *
 * @see #processEvent processEvent
 */
public interface WriteBatchListener extends BatchListener<WriteBatch> {
  /**
   * <p>The method called by WriteBatcher to run your
   * custom code on this batch.  You usually implement this as a lambda expression.</p>
   *
   * For example, see the lambda expression passed to onBatchSuccess:
   *
   * <pre>{@code
   *     WriteBatcher wb = dataMovementManager.newWriteBatcher(query)
   *         .withBatchSize(1000)
   *         .withThreadCount(20)
   *         .onBatchSuccess(batch -> {
   *             for ( WriteEvent doc : batch.getItems() ) {
   *                 if ( doc.getTargetUri().contains("/legal/") ) {
   *                     // do something
   *                 }
   *             }
   *         })
   *         .onBatchFailure(throwable -> throwable.printStackTrace());
   *}</pre>
   *
   * @param batch the batch of documents written and some metadata about the current status of the job
   */
  void processEvent(WriteBatch batch);
}


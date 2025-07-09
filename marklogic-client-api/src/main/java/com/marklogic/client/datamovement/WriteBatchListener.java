/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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

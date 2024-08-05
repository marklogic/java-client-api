/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/** A generic interface for listeners which process failures on batches.
 * Currently only WriteFailureListener implements since QueryBatcher has no
 * batch when the query fails.
 */
public interface BatchFailureListener<T extends BatchEvent> {
  /** The method called when a failure occurs.
   *
   * @param batch the batch of items that failed processing
   * @param throwable the exception that caused the failure
   */
  void processFailure(T batch, Throwable throwable);
}

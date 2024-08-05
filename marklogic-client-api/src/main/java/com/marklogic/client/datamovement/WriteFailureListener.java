/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/** The listener interface for handling exceptions occurring withing WriteBatcher.
 */
public interface WriteFailureListener extends BatchFailureListener<WriteBatch> {
  void processFailure(WriteBatch batch, Throwable failure);
}

/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/** The listener interface for handling exceptions occurring withing WriteBatcher.
 */
public interface WriteFailureListener extends BatchFailureListener<WriteBatch> {
  void processFailure(WriteBatch batch, Throwable failure);
}

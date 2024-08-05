/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/** A generic base interface for listeners implemented by QueryFailureListener
 * for processing a Throwable that caused a failure.
 */
public interface FailureListener<T extends Throwable> {
  void processFailure(T failure);
}

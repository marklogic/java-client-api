/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

public class DataMovementInternalError extends InternalError {
  DataMovementInternalError(String message, Throwable cause) {
    super(message, cause);
  }
}

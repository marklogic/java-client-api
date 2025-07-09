/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

public class DataMovementInternalError extends InternalError {
  DataMovementInternalError(String message, Throwable cause) {
    super(message, cause);
  }
}

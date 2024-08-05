/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * The generic base exception used throughout the Data Movement SDK.  It is a
 * runtime exception to reduce the need to handle checked exceptions from which
 * an application cannot recover programmatically.
 */
public class DataMovementException extends RuntimeException {
  public DataMovementException(String message, Throwable cause) {
    super(message, cause);
  }
}

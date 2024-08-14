/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * An IO Exception indicates that there was a problem on input or output (similar
 * to a java.lang.IOException but defined as a runtime rather than checked
 * exception).
 */
@SuppressWarnings("serial")
public class MarkLogicIOException extends RuntimeException {


  public MarkLogicIOException(String message) {
    super(message);
  }

  public MarkLogicIOException(Throwable cause) {
    super(cause);
  }

  public MarkLogicIOException(String message, Throwable cause) {
    super(message, cause);
  }

}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * An InternalException suggests a defect in the API.
 *
 * If you have an active maintenance contract, you can contact MarkLogic Technical Support.
 */
@SuppressWarnings("serial")
public class MarkLogicInternalException extends RuntimeException {

  public MarkLogicInternalException(String message) {
    super(message);
  }

  public MarkLogicInternalException(Throwable cause) {
    super(cause);
  }

  public MarkLogicInternalException(String message, Throwable cause) {
    super(message, cause);
  }


}

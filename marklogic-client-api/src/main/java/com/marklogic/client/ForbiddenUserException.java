/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * Thrown when the credentials used to connect to a MarkLogic REST instance
 * are not sufficient for the task requested.  This exception corresponds
 * to HTTP status code 403.
 */
@SuppressWarnings("serial")
public class ForbiddenUserException extends MarkLogicServerException {
  public ForbiddenUserException(String message) {
    super(message);
  }

  public ForbiddenUserException(String localMessage,
                                FailedRequest failedRequest) {
    super(localMessage, failedRequest);
  }

}

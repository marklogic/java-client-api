/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * A FailedRetryException is used to capture and report when retry
 * of the request timed out or failed in some other way.
 */
@SuppressWarnings("serial")
public class FailedRetryException extends FailedRequestException {

  public FailedRetryException(String message) {
    super(message);
  }

  public FailedRetryException(String localMessage, Throwable cause) {
    super(localMessage, cause);
  }

  public FailedRetryException(String localMessage, FailedRequest failedRequest) {
    super(localMessage, failedRequest);
  }

}

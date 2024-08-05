/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * A FailedRequestException is used to capture and report on problems
 * from the REST API.  This class is a semantically thin one, meaning that it is used as
 * catch-all for various things that can go wrong on the REST server.
 */
@SuppressWarnings("serial")
public class FailedRequestException extends MarkLogicServerException {

  public FailedRequestException(String message) {
    super(message);
  }

  public FailedRequestException(String localMessage, Throwable cause) {
    super(localMessage, cause);
  }

  public FailedRequestException(String localMessage, FailedRequest failedRequest) {
    super(localMessage, failedRequest);
  }

}

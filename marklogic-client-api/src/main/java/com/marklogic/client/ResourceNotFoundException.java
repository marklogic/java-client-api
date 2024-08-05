/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * Exception thrown when the server responds with HTTP status code 404.
 * Access the failed request payload using getFailedRequest()
 *
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends MarkLogicServerException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
  public ResourceNotFoundException(String localMessage,
                                   FailedRequest failedRequest) {
    super(localMessage, failedRequest);
  }

}

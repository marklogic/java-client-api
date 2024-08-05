/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * Exception thrown when the server responds with HTTP status code 503
 * and a Retry-After header of 1 but the request is a PUT or POST
 * and the payload is streaming.
 */
@SuppressWarnings("serial")
public class ResourceNotResendableException extends MarkLogicServerException {

  public ResourceNotResendableException(String message) {
    super(message);
  }

}

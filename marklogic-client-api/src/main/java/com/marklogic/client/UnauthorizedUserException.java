/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * An Unauthorized User Exception indicates the user is
 * not recognized by the server.
 *
 */
@SuppressWarnings("serial")
public class UnauthorizedUserException extends RuntimeException {
  public UnauthorizedUserException() {
    super();
  }
  public UnauthorizedUserException(String message) {
    super(message);
  }
  public UnauthorizedUserException(Throwable cause) {
    super(cause);
  }
  public UnauthorizedUserException(String message, Throwable cause) {
    super(message, cause);
  }

}

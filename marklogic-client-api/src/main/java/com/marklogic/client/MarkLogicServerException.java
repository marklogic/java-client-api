/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * Abstract class that implements functionality for errors returned from a MarkLogic REST API instance.
 * A REST instance wraps error messages into an XML or JSON structure that can be parsed and turned into
 * the FailedRequest Java object.  The MarkLogicServerException contains the FailedRequest object which
 * is incorporated into getMessage() or can be examined via getFailedRequest()
 *
 */
@SuppressWarnings("serial")
public abstract class MarkLogicServerException extends RuntimeException {
  // NOTE:  to verify that exceptions are declared, switch extends
  // temporarily from RuntimeException to Exception
  private FailedRequest failedRequest;

  /**
   * @param localMessage message describing the exception
   * @param failedRequest details about the failed request behind this exception
   */
  public MarkLogicServerException(String localMessage, FailedRequest failedRequest) {
    super(localMessage);
    this.failedRequest = failedRequest;
  }

  public MarkLogicServerException(String localMessage) {
    super(localMessage);
  }

  public MarkLogicServerException(String localMessage, Throwable cause) {
    super(localMessage, cause);
  }

  @Override
  public String getMessage() {
    if (super.getMessage() != null && failedRequest != null) {
      return "Local message: " + super.getMessage() + ". Server Message: " + failedRequest.getMessage();
    }
    else if (failedRequest != null) {
      return failedRequest.getMessage();
    }
    else return super.getMessage();
  }

  /**
   * Gets the HTTP status code (if any) associated with the error on the server.
   * @return  the status code
   */
  public int getServerStatusCode() {
    return (failedRequest == null) ? null : failedRequest.getStatusCode();
  }
  /**
   * Gets the HTTP status message (if any) associated with the error on the server.
   * @return  the status message
   */
  public String getServerStatus() {
    return (failedRequest == null) ? null : failedRequest.getStatus();
  }
  /**
   * Gets the error code (if any) specific to the error on the server.
   * @return  the error code
   */
  public String getServerMessageCode() {
    return (failedRequest == null) ? null : failedRequest.getMessageCode();
  }
  /**
   * Gets the error message (if any) specific to the error on the server.
   * @return  the error message
   */
  public String getServerMessage() {
    return (failedRequest == null) ? null : failedRequest.getMessage();
  }
  /**
   * Gets the stack trace (if any) specific to the error on the server.
   * @return  the server stack trace
   */
  public String getServerStackTrace() {
    return (failedRequest == null) ? null : failedRequest.getStackTrace();
  }

  @Deprecated
  public FailedRequest getFailedRequest() {
    return failedRequest;
  }
}


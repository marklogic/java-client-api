/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

/**
 * Identifies a server state for sharing across multiple calls to server endpoints.
 *
 * Internally, the identifier is sent to the server as a session cookie.
 * The session cookie can be used for load balancing.
 */
public interface SessionState {
  /**
   * Provides the identifier used for the server state (for instance, for use in logging).
   * @return   the session identifier
   */
  public String getSessionId();
}

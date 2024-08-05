/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.PrintStream;

import com.marklogic.client.util.RequestLogger;

abstract class AbstractLoggingManager {
  protected RequestLogger requestLogger;

  AbstractLoggingManager() {
    super();
  }

  public void startLogging(RequestLogger logger) {
    requestLogger = logger;
  }
  public void stopLogging() {
    if (requestLogger == null) return;

    PrintStream out = requestLogger.getPrintStream();
    if (out != null) out.flush();

    requestLogger = null;
  }
  protected boolean isLoggerEnabled() {
    if (requestLogger != null)
      return requestLogger.isEnabled();

    return false;
  }
  protected PrintStream getPrintLogger() {
    if (requestLogger == null)
      return null;

    return requestLogger.getPrintStream();
  }
}

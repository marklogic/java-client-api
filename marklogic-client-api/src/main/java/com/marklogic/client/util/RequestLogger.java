/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.util;

import java.io.PrintStream;

/**
 * Request Logger records the requests sent to the server.  After creating
 * a document or query manager, you can set a logger on the manager.  You
 * can choose to log content sent to the server as well as requests.
 *
 * FileHandle constitutes an exception to the ability to log content.  Only
 * the name of the file is logged.
 */
public interface RequestLogger {
  /**
   * Indicates that no content is copied to the log (the default).
   */
  long NO_CONTENT  = 0;
  /**
   * Indicates that all content is copied to the log.
   */
  long ALL_CONTENT = Long.MAX_VALUE;

  /**
   * Returns how much content is copied to the log.
   * @return	the limit on copying content
   */
  long getContentMax();
  /**
   * Controls how much content is copied to the log (defaulting to NO_CONTENT).
   * @param length	the limit on copying content
   */
  void setContentMax(long length);

  /**
   * Returns whether logging is active or suspended.
   * @return	the enablement of logging
   */
  boolean isEnabled();
  /**
   * Suspend or resume logging.
   * @param enabled	the enablement of logging
   */
  void setEnabled(boolean enabled);

  /**
   * Returns the underlying PrintStream used for logging.
   * @return	the PrintStream for logging
   */
  PrintStream getPrintStream();

  /**
   * Copies content to the log during request processing
   * up to the length limit specified for the logger.
   *
   * Ordinarily, this method is called internally
   * during reading content from the database or writing
   * content to the database.  You may, however, use
   * this method directly if convenient.
   *
   * @param content	the copied content
   * @param <T> the type to return
   * @return	the copied content
   */
  <T> T copyContent(T content);

  /**
   * Send buffered output to the log destination.
   */
  void flush();
  /**
   * Close the log.
   */
  void close();
}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Binding Exception indicates a problem converting between a Java object
 * and XML representation of the data.  The exception may indicate an internal
 * error.
 *
 * If you have an active maintenance contract, you can contact MarkLogic Technical Support.
 */
@SuppressWarnings("serial")
public class MarkLogicBindingException extends RuntimeException {

  static final private Logger logger = LoggerFactory
    .getLogger(MarkLogicBindingException.class);

  public MarkLogicBindingException(String message, Throwable e) {
    super(message, e);
  }

  public MarkLogicBindingException(Exception e) {
    super(e);
    e.printStackTrace();
  }

  public MarkLogicBindingException(String msg) {
    logger.error(msg);
  }

}

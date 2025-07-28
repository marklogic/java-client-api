/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.admin;

/**
 * The Method Type enumerates a kind of operation.
 */
public enum MethodType {
  /**
   * A read operation.
   */
  GET,
  /**
   * A write operation.
   */
  PUT,
  /**
   * An apply operation; a catch-all for operations that
   * don't fit another method
   */
  POST,
  /**
   * A remove operation.
   */
  DELETE;
}

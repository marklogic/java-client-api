/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.pojo;

import com.marklogic.client.Page;
import java.io.Closeable;

/** Enables pagination over objects retrieved from the server and deserialized by
 * PojoRepository read and search methods.
 */
public interface PojoPage<T> extends Page<T>, Closeable {
  /** Frees the underlying resources, including the http connection. */
  @Override
  void close();
}

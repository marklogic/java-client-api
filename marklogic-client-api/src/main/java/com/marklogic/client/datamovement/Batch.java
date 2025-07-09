/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import java.util.Calendar;

import com.marklogic.client.DatabaseClient;

/** A group of items (generally documents or uris) and context representing a
 * completed action in a datamovement job.
 */
public interface Batch<T> extends BatchEvent {
  /** The documents read by WriteBatcher or the uris retrieved by QueryBatcher.
   *
   * @return the items in this batch
   */
  T[] getItems();
}

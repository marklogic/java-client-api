/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.lang.reflect.Array;
import java.util.Calendar;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.JobTicket;

public class BatchImpl<T> extends BatchEventImpl implements Batch<T> {
  private T[] items;
  private Class<T> as;

  public BatchImpl(Class<T> as) {
    super();
    if (as == null) {
      throw new MarkLogicInternalException("batch instantiated without type token");
    }
    this.as = as;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T[] getItems() {
    if (items == null) {
      return (T[]) Array.newInstance(as, 0);
    }
    return items;
  }
  public BatchImpl<T> withItems(T[] items) {
    this.items = items;
    return this;
  }

	@SuppressWarnings("unchecked")
  public BatchImpl<T> withClient(DatabaseClient client) {
    return (BatchImpl<T>) super.withClient(client);
  }

	@SuppressWarnings("unchecked")
  public BatchImpl<T> withTimestamp(Calendar timestamp) {
    return (BatchImpl<T>) super.withTimestamp(timestamp);
  }

	@SuppressWarnings("unchecked")
  public BatchImpl<T> withJobTicket(JobTicket jobTicket) {
    return (BatchImpl<T>) super.withJobTicket(jobTicket);
  }

	@SuppressWarnings("unchecked")
  public BatchImpl<T> withJobBatchNumber(long jobBatchNumber) {
    return (BatchImpl<T>) super.withJobBatchNumber(jobBatchNumber);
  }
}

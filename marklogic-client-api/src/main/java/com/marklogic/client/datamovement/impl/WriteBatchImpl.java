/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.util.Calendar;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;

public class WriteBatchImpl extends BatchImpl<WriteEvent> implements WriteBatch {
  private WriteBatcher batcher;
  private long bytesMoved;
  private long jobWritesSoFar;

  public WriteBatchImpl() {
    super(WriteEvent.class);
  }

  @Override
  public WriteBatcher getBatcher() {
    return batcher;
  }

  public WriteBatchImpl withBatcher(WriteBatcher batcher) {
    this.batcher = batcher;
    return this;
  }

  @Override
  public WriteBatchImpl withItems(WriteEvent[] items) {
    super.withItems(items);
    return this;
  }

  @Override
  public WriteBatchImpl withClient(DatabaseClient client) {
    super.withClient(client);
    return this;
  }

  @Override
  public WriteBatchImpl withTimestamp(Calendar timestamp) {
    super.withTimestamp(timestamp);
    return this;
  }

  @Override
  public WriteBatchImpl withJobBatchNumber(long jobBatchNumber) {
    super.withJobBatchNumber(jobBatchNumber);
    return this;
  }

  @Override
  public WriteBatchImpl withJobTicket(JobTicket jobTicket) {
    super.withJobTicket(jobTicket);
    return this;
  }

  public long getBytesMoved() {
    return bytesMoved;
  }

  public WriteBatchImpl withBytesMoved(long bytesMoved) {
    this.bytesMoved = bytesMoved;
    return this;
  }

  @Override
  public long getJobWritesSoFar() {
    return jobWritesSoFar;
  }

  public WriteBatchImpl withJobWritesSoFar(long jobWritesSoFar) {
    this.jobWritesSoFar = jobWritesSoFar;
    return this;
  }
}

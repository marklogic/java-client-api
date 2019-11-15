/*
 * Copyright 2015-2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

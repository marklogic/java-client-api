/*
 * Copyright 2015-2018 MarkLogic Corporation
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

public class BatchImpl<T> implements Batch<T> {
  private T[] items;
  private DatabaseClient client;
  private long jobBatchNumber;
  private Calendar timestamp;
  private JobTicket jobTicket;

  public BatchImpl() {
    timestamp = Calendar.getInstance();
  }

  @Override
  public T[] getItems() {
    return items;
  }

  public BatchImpl<T> withItems(T[] items) {
    this.items = items;
    return this;
  }

  @Override
  public DatabaseClient getClient() {
    return client;
  }

  public BatchImpl<T> withClient(DatabaseClient client) {
    this.client = client;
    return this;
  }

  @Override
  public Calendar getTimestamp() {
    return timestamp;
  }

  public BatchImpl<T> withTimestamp(Calendar timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  @Override
  public JobTicket getJobTicket() {
    return jobTicket;
  }

  public BatchImpl<T> withJobTicket(JobTicket jobTicket) {
    this.jobTicket = jobTicket;
    return this;
  }

  @Override
  public long getJobBatchNumber() {
    return jobBatchNumber;
  }

  public BatchImpl<T> withJobBatchNumber(long jobBatchNumber) {
    this.jobBatchNumber = jobBatchNumber;
    return this;
  }
}

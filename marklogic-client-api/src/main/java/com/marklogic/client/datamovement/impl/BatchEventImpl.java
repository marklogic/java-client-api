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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.BatchEvent;
import com.marklogic.client.datamovement.JobTicket;

import java.util.Calendar;

public class BatchEventImpl implements BatchEvent {
  private DatabaseClient client;
  private long jobBatchNumber;
  private Calendar timestamp;
  private JobTicket jobTicket;

  public BatchEventImpl() {
    timestamp = Calendar.getInstance();
  }

  @Override
  public DatabaseClient getClient() {
    return client;
  }

  public BatchEventImpl withClient(DatabaseClient client) {
    this.client = client;
    return this;
  }

  @Override
  public Calendar getTimestamp() {
    return timestamp;
  }

  public BatchEventImpl withTimestamp(Calendar timestamp) {
    if (timestamp == null) {
      timestamp = Calendar.getInstance();
    }
    this.timestamp = timestamp;
    return this;
  }

  @Override
  public JobTicket getJobTicket() {
    return jobTicket;
  }

  public BatchEventImpl withJobTicket(JobTicket jobTicket) {
    this.jobTicket = jobTicket;
    return this;
  }

  @Override
  public long getJobBatchNumber() {
    return jobBatchNumber;
  }

  public BatchEventImpl withJobBatchNumber(long jobBatchNumber) {
    this.jobBatchNumber = jobBatchNumber;
    return this;
  }
}

/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

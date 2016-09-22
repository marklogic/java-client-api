/*
 * Copyright 2015 MarkLogic Corporation
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
import com.marklogic.client.datamovement.HostBatcher;

public class HostBatcherImpl implements HostBatcher {
  private String jobName = "unnamed";
  private int batchSize = 100;
  private int threadCount = 1;
  private DatabaseClient client;

  @Override
  public HostBatcher withJobName(String jobName) {
    this.jobName = jobName;
    return this;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public HostBatcher withBatchSize(int batchSize) {
    if ( batchSize <= 0 ) {
      throw new IllegalArgumentException("batchSize must be 1 or greater");
    }
    this.batchSize = batchSize;
    return this;
  }

  @Override
  public int getBatchSize() {
    return batchSize;
  }

  @Override
  public HostBatcher withThreadCount(int threadCount) {
    if ( threadCount <= 0 ) {
      throw new IllegalArgumentException("threadCount must be 1 or greater");
    }
    this.threadCount = threadCount;
    return this;
  }

  @Override
  public int getThreadCount() {
    return threadCount;
  }

  public synchronized void setClient(DatabaseClient client) {
    if ( client == null ) {
      throw new IllegalStateException("client must not be null");
    }
    if ( this.client != null ) {
      throw new IllegalStateException("You can only call setClient once per ImportHostBatcher instance");
    }
    this.client = client;
  }

  public DatabaseClient getClient() {
    return client;
  }

}

/*
 * Copyright 2015-2016 MarkLogic Corporation
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
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.ForestConfiguration;

public abstract class BatcherImpl implements Batcher {
  private String jobName = "unnamed";
  private int batchSize = 100;
  private int threadCount = 1;
  private ForestConfiguration forestConfig;
  private DatabaseClient client;

  @Override
  public Batcher withJobName(String jobName) {
    this.jobName = jobName;
    return this;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public Batcher withBatchSize(int batchSize) {
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
  public Batcher withThreadCount(int threadCount) {
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

  @Override
  public ForestConfiguration getForestConfig() {
    return forestConfig;
  }

  @Override
  public Batcher withForestConfig(ForestConfiguration forestConfig) {
    this.forestConfig = forestConfig;
    return this;
  }

  @Override
  public abstract boolean isStopped();
}

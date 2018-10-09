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
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ForestConfiguration;

public abstract class BatcherImpl implements Batcher {
  private String jobName = "unnamed";
  private String jobId = null;
  private int batchSize = 100;
  private int threadCount = 1;
  private ForestConfiguration forestConfig;
  private DatabaseClient client;
  private DataMovementManagerImpl moveMgr;

  protected BatcherImpl(DataMovementManager moveMgr){
    if (moveMgr == null)
      throw new IllegalArgumentException("moveMgr must not be null");
    if (!(moveMgr instanceof DataMovementManagerImpl))
      throw new IllegalArgumentException("moveMgr must be DataMovementManagerImpl");
    this.moveMgr = (DataMovementManagerImpl) moveMgr;
  }

  @Override
  public Batcher withJobName(String jobName) {
    this.jobName = jobName;
    return this;
  }

  @Override
  public Batcher withJobId(String jobId) {
    this.jobId = jobId;
    return this;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public String getJobId() {
    return jobId;
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
    if ( forestConfig == null ) throw new IllegalArgumentException("forestConfig must not be null");
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY && !(forestConfig instanceof ForestConfigurationImpl))
      throw new IllegalArgumentException("cannot change internal forestConfig when using a gateway");
    this.forestConfig = forestConfig;
    return this;
  }

  @Override
  public abstract boolean isStopped();

  @Override
  public abstract boolean isStarted();

  public DataMovementManagerImpl getMoveMgr() {
    return moveMgr;
  }
}

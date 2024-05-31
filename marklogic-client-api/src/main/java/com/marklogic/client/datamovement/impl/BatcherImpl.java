/*
 * Copyright (c) 2022 MarkLogic Corporation
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
import com.marklogic.client.datamovement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BatcherImpl implements Batcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

  private String jobName = "unnamed";
  private String jobId = null;
  private int batchSize = 100;
  private int threadCount = 1;
  private ForestConfiguration forestConfig;
  private DataMovementManagerImpl moveMgr;
  private JobTicket jobTicket;
  private Calendar jobStartTime;
  private Calendar jobEndTime;

  private final AtomicBoolean stopped = new AtomicBoolean(false);
  private final AtomicBoolean started = new AtomicBoolean(false);

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

  public void setJobId(String jobId) {
    this.jobId = jobId;
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
  public DatabaseClient getPrimaryClient() {
    return getMoveMgr().getPrimaryClient();
  }

  public abstract void start(JobTicket ticket);
  public abstract void stop();

  @Override
  public JobTicket getJobTicket() {
    return jobTicket;
  }
  void setJobTicket(JobTicket ticket) {
    this.jobTicket = ticket;
  }
  @Override
  public Calendar getJobStartTime() {
    return this.jobStartTime;
  }
  void setJobStartTime() {
    if (this.jobStartTime != null) return;
    this.jobStartTime = Calendar.getInstance();
  }
  @Override
  public Calendar getJobEndTime() {
    return this.jobEndTime;
  }
  void setJobEndTime() {
    if (this.jobEndTime != null) return;
    this.jobEndTime = Calendar.getInstance();
  }

  @Override
  public boolean isStarted() {
    return started.get();
  }

  @Override
  public boolean isStopped() {
    return stopped.get();
  }

	final void setStartedToTrue() {
		logger.info("Setting 'started' to true.");
		this.started.set(true);
	}

	final void setStoppedToTrue() {
		logger.info("Setting 'stopped' to true.");
		this.stopped.set(true);
	}

  final boolean isStoppedTrue() {
	  // This method is necessary as calling "isStopped()" results in different behavior in QueryBatcherImpl, where
	  // that method has been overridden to inspect the thread pool status instead. It's not clear why that was done,
	  // so this preserves the existing behavior where the value of `stopped` is check in multiple places (it would seem
	  // that in all of those places, calling "isStopped()" would be preferable).
	  return this.stopped.get() == true;
  }

  protected DataMovementManagerImpl getMoveMgr() {
    return moveMgr;
  }

  protected Forest[] forests(ForestConfiguration config) {
    if (config == null) {
      throw new IllegalArgumentException("forestConfig must not be null");
    }
    return config.listForests();
  }
  protected Set<String> hosts(Forest[] forests) {
    if (forests.length == 0) {
      throw new IllegalStateException("batcher requires at least one forest");
    }
    Set<String> hosts = new HashSet<>();
    for (Forest forest: forests) {
      if (forest.getPreferredHost() == null) {
        throw new IllegalStateException("Hostname must not be null for any forest");
      }
      hosts.add(forest.getPreferredHost());
    }
    for (Forest forest: forests) {
      String hostName = forest.getHost();
      if (forest.getPreferredHostType() == Forest.HostType.REQUEST_HOST &&
              !hostName.toLowerCase().equals(forest.getRequestHost().toLowerCase())) {
        if (hosts.contains(hostName))
          hosts.remove(hostName);
      }
    }
    return hosts;
  }
  protected List<DatabaseClient> clients(Set<String> hosts) {
    if (hosts == null || hosts.size() == 0) {
      throw new IllegalStateException("no hosts for batcher");
    }
    List<DatabaseClient> clients = new ArrayList<>();
    for (String host: hosts) {
      clients.add(moveMgr.getHostClient(host));
    }
    return clients;
  }

}

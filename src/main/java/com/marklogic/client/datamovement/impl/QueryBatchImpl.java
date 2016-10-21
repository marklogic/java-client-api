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

import java.util.Calendar;

import com.marklogic.client.datamovement.Batch;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.query.QueryDefinition;

public class QueryBatchImpl extends BatchImpl<String> implements QueryBatch {
  private QueryDefinition query;
  private long jobResultsSoFar;
  private long forestBatchNumber;
  private long forestResultsSoFar;
  private Forest forest;
  private long serverTimestamp;

  @Override
  public QueryBatchImpl withItems(String[] items) {
    super.withItems(items);
    return this;
  }

  @Override
  public QueryBatchImpl withTimestamp(Calendar timestamp) {
    super.withTimestamp(timestamp);
    return this;
  }

  @Override
  public QueryBatchImpl withJobBatchNumber(long jobBatchNumber) {
    super.withJobBatchNumber(jobBatchNumber);
    return this;
  }

  @Override
  public QueryBatchImpl withJobTicket(JobTicket jobTicket) {
    super.withJobTicket(jobTicket);
    return this;
  }

  @Override
  public QueryDefinition getQuery() {
    return query;
  }

  public QueryBatchImpl withQuery(QueryDefinition query) {
    this.query = query;
    return this;
  }

  @Override
  public Forest getForest() {
    return forest;
  }

  public QueryBatchImpl withForest(Forest forest) {
    this.forest = forest;
    return this;
  }

  @Override
  public long getJobResultsSoFar() {
    return jobResultsSoFar;
  }

  public QueryBatchImpl withJobResultsSoFar(long jobResultsSoFar) {
    this.jobResultsSoFar = jobResultsSoFar;
    return this;
  }

  @Override
  public long getForestBatchNumber() {
    return forestBatchNumber;
  }

  public QueryBatchImpl withForestBatchNumber(long forestBatchNumber) {
    this.forestBatchNumber = forestBatchNumber;
    return this;
  }

  @Override
  public long getForestResultsSoFar() {
    return forestResultsSoFar;
  }

  public QueryBatchImpl withForestResultsSoFar(long forestResultsSoFar) {
    this.forestResultsSoFar = forestResultsSoFar;
    return this;
  }

  @Override
  public long getServerTimestamp() {
    return serverTimestamp;
  }

  public QueryBatchImpl withServerTimestamp(long serverTimestamp) {
    this.serverTimestamp = serverTimestamp;
    return this;
  }
}

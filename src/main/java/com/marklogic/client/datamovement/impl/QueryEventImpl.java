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

import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.QueryEvent;

public class QueryEventImpl implements QueryEvent {
  QueryDefinition query;
  long jobBatchNumber;
  long jobResultsSoFar;
  long forestBatchNumber;
  long forestResultsSoFar;
  Forest forest;
  long serverTimestamp;

  public QueryEventImpl(QueryDefinition query, long jobBatchNumber, long jobResultsSoFar,
    long forestBatchNumber, long forestResultsSoFar, Forest forest, long serverTimestamp)
  {
    this.query = query;
    this.jobBatchNumber = jobBatchNumber;
    this.jobResultsSoFar = jobResultsSoFar;
    this.forestBatchNumber = forestBatchNumber;
    this.forestResultsSoFar = forestResultsSoFar;
    this.forest = forest;
    this.serverTimestamp = serverTimestamp;
  }

  @Override
  public QueryDefinition getQuery() {
    return query;
  }

  @Override
  public long getJobBatchNumber() {
    return jobBatchNumber;
  }

  @Override
  public long getJobResultsSoFar() {
    return jobResultsSoFar;
  }

  @Override
  public long getForestBatchNumber() {
    return forestBatchNumber;
  }

  @Override
  public long getForestResultsSoFar() {
    return forestResultsSoFar;
  }

  @Override
  public Forest getForest() {
    return forest;
  }

  @Override
  public long getServerTimestamp() {
    return serverTimestamp;
  }
}

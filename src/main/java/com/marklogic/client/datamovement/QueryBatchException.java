/*
 * Copyright 2015-2017 MarkLogic Corporation
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
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.Format;

public class QueryBatchException extends Exception implements QueryEvent {
  private QueryEvent queryEvent;

  public QueryBatchException(QueryEvent queryEvent, Throwable cause) {
    super(cause);
    this.queryEvent = queryEvent;
  }

  @Override
  public QueryBatcher getBatcher() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getBatcher();
  }

  @Override
  public DatabaseClient getClient() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getClient();
  }

  @Override
  public long getJobBatchNumber() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobBatchNumber();
  }

  @Override
  public long getJobResultsSoFar() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobResultsSoFar();
  }

  @Override
  public long getForestBatchNumber() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForestBatchNumber();
  }

  @Override
  public long getForestResultsSoFar() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForestResultsSoFar();
  }

  @Override
  public Forest getForest() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getForest();
  }

  @Override
  public long getServerTimestamp() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getServerTimestamp();
  }

  @Override
  public JobTicket getJobTicket() {
    if ( queryEvent == null ) throw new IllegalStateException("QueryEvent is null");
    return queryEvent.getJobTicket();
  }
}

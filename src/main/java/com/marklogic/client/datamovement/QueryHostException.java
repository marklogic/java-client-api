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
package com.marklogic.client.datamovement;

import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryDefinition;

public class QueryHostException extends Exception implements QueryEvent {
  private QueryEvent queryEvent;

  public QueryHostException(QueryEvent queryEvent, Throwable cause) {
    super(cause);
    this.queryEvent = queryEvent;
  }

  @Override
  public QueryDefinition getQuery() {
    if ( queryEvent == null ) return null;
    return queryEvent.getQuery();
  }

  @Override
  public long getJobBatchNumber() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getJobBatchNumber();
  }

  @Override
  public long getJobResultsSoFar() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getJobResultsSoFar();
  }

  @Override
  public long getForestBatchNumber() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getForestBatchNumber();
  }

  @Override
  public long getForestResultsSoFar() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getForestResultsSoFar();
  }

  @Override
  public Forest getForest() {
    if ( queryEvent == null ) return null;
    return queryEvent.getForest();
  }

  @Override
  public long getServerTimestamp() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getServerTimestamp();
  }
}

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

public class QueryHostException extends Exception implements QueryEvent {
  private QueryEvent queryEvent;

  public QueryHostException(QueryEvent queryEvent, Throwable cause) {
    super(cause);
    this.queryEvent = queryEvent;
  }
  @Override
  public long getBytesMoved() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getBytesMoved();
  }

  @Override
  public long getJobRecordNumber() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getJobRecordNumber();
  }

  @Override
  public long getBatchRecordNumber() {
    if ( queryEvent == null ) return -1;
    return queryEvent.getBatchRecordNumber();
  }

  @Override
  public String getSourceUri() {
    if ( queryEvent == null ) return null;
    return queryEvent.getSourceUri();
  }

  @Override
  public Forest getSourceForest() {
    if ( queryEvent == null ) return null;
    return queryEvent.getSourceForest();
  }

  @Override
  public Format getFormat() {
    if ( queryEvent == null ) return null;
    return queryEvent.getFormat();
  }

  @Override
  public String getMimetype() {
    if ( queryEvent == null ) return null;
    return queryEvent.getMimetype();
  }
}

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
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.QueryEvent;

public class QueryEventImpl extends DataMovementEventImpl implements QueryEvent {
  String sourceUri;
  Forest forest;
  Format format;
  String mimeType;

  public QueryEventImpl(String sourceUri, Forest forest, Format format, String mimeType) {
    this.sourceUri = sourceUri ;
    this.forest = forest ;
    this.format = format ;
    this.mimeType = mimeType ;
  }

  @Override
  public String getSourceUri() {
    return sourceUri;
  }

  @Override
  public Forest getSourceForest() {
    return forest;
  }

  @Override
  public Format getFormat() {
    return format;
  }

  @Override
  public String getMimetype() {
    return mimeType;
  }
}

/*
 * Copyright 2015-2019 MarkLogic Corporation
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

import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.datamovement.WriteEvent;

public class WriteEventImpl extends DataMovementEventImpl implements WriteEvent {
  private String targetUri;
  private AbstractWriteHandle content;
  private DocumentMetadataWriteHandle metadata;

  @Override
  public String getTargetUri() {
    return targetUri;
  }

  public WriteEventImpl withTargetUri(String targetUri) {
    this.targetUri = targetUri;
    return this;
  }

  @Override
  public AbstractWriteHandle getContent() {
    return content;
  }

  public WriteEventImpl withContent(AbstractWriteHandle content) {
    this.content = content;
    return this;
  }

  @Override
  public DocumentMetadataWriteHandle getMetadata() {
    return metadata;
  }

  public WriteEventImpl withMetadata(DocumentMetadataWriteHandle metadata) {
    this.metadata = metadata;
    return this;
  }
}

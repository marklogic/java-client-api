/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
